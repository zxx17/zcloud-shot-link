package com.zskj.account.service.impl;

import com.zskj.account.config.rbtmq.RabbitMQConfig;
import com.zskj.account.controller.request.AccountLoginRequest;
import com.zskj.account.controller.request.AccountRegisterRequest;
import com.zskj.account.manager.AccountManager;
import com.zskj.account.model.AccountDO;
import com.zskj.account.service.AccountService;
import com.zskj.account.service.NotifyService;
import com.zskj.common.enums.EventMessageType;
import com.zskj.common.enums.account.AuthTypeEnum;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.enums.account.SendCodeEnum;
import com.zskj.common.model.EventMessage;
import com.zskj.common.model.LoginUser;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.IDUtil;
import com.zskj.common.util.JWTUtil;
import com.zskj.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-04-22
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    /**
     * 免费流量包商品id
     */
    private static final Long FREE_TRAFFIC_PRODUCT_ID = 1L;

    /**
     * 用户注册
     *
     * @param request 注册表单
     * @return res
     */
    @Override
    public JsonData register(AccountRegisterRequest request) {
        // 判断验证码
        boolean checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER, request.getPhone(), request.getCode());
        //验证码错误
        if (!checkCode) {
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }
        AccountDO accountDO = new AccountDO();
        BeanUtils.copyProperties(request, accountDO);
        //认证级别
        accountDO.setAuth(AuthTypeEnum.DEFAULT.name());
        //生成唯一的账号
        accountDO.setAccountNo(Long.valueOf(IDUtil.geneSnowFlakeId().toString()));
        accountDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));
        String cryptPwd = Md5Crypt.md5Crypt(request.getPwd().getBytes(), accountDO.getSecret());
        accountDO.setPwd(cryptPwd);
        int rows = accountManager.insert(accountDO);
        log.info("rows:{},注册成功:{}", rows, accountDO.getAccountNo());
        //用户注册成功，发放福利
        userRegisterInitTask(accountDO);
        return JsonData.buildSuccess();
    }


    /**
     * 用户登录
     *
     * @param request 登录表单数据
     * @return res
     */
    @Override
    public JsonData login(AccountLoginRequest request) {
        List<AccountDO> user = accountManager.findByPhone(request.getPhone());
        if (user != null && user.size() == 1) {
            AccountDO accountDO = user.get(0);
            // 前端传来的密码和存储的盐加密，然后和数据库的比较
            String md5Crypt = Md5Crypt.md5Crypt(request.getPwd().getBytes(), accountDO.getSecret());
            if (md5Crypt.equalsIgnoreCase(accountDO.getPwd())) {
                LoginUser loginUser = LoginUser.builder().build();
                BeanUtils.copyProperties(accountDO, loginUser);
                String token = JWTUtil.geneJsonWebToken(loginUser);
                return JsonData.buildSuccess(token);
            } else {
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
            }
        } else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
        }
    }


    /**
     * 用户注册成功，发放福利
     *
     * @param accountDO 用户信息
     */
    private void userRegisterInitTask(AccountDO accountDO) {
        EventMessage eventMessage = EventMessage.builder()
                .messageId(IDUtil.geneSnowFlakeId().toString())
                .accountNo(accountDO.getAccountNo())
                .eventMessageType(EventMessageType.TRAFFIC_FREE_INIT.name())
                .bizId(FREE_TRAFFIC_PRODUCT_ID.toString())
                .build();

        //发送发放流量包消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getTrafficEventExchange(),
                rabbitMQConfig.getTrafficFreeInitRoutingKey(),eventMessage);
    }
}
