package com.zskj.shop.aspect;

import com.zskj.common.constant.RedisKeyConstant;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.exception.BizException;
import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.common.util.CommonUtil;
import com.zskj.shop.annotation.RepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/16
 * <p>
 * 接口防重复提交切面
 * </p>
 */
@Aspect
@Component
@Slf4j
public class RepeatSubmitAspect {

    /**
     * 用于token形式
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 用于param形式
     */
    @Autowired
    private RedissonClient redissonClient;


    /**
     * 定义 @Pointcut注解表达式, 通过特定的规则来筛选连接点, 就是Pointcut，选中那几个你想要的方法
     * 在程序中主要体现为书写切入点表达式（通过通配、正则表达式）过滤出特定的一组 JointPoint连接点
     * <p>
     * 方式一：@annotation：当执行的方法上拥有指定的注解时生效（我们采用这）
     * 方式二：execution：一般用于指定方法的执行
     */
    @Pointcut("@annotation(repeatSubmit)")
    public void pointCutNoRepeatSubmit(RepeatSubmit repeatSubmit) {
    }

    /**
     * 环绕通知, 围绕着方法执行
     *
     * @param joinPoint    切面
     * @param repeatSubmit 注解
     *                     <p>
     *                     方式一：单用 @Around("execution(* net.xdclass.controller.*.*(..))")可以
     *                     方式二：用@Pointcut和@Around联合注解也可以（我们采用这个）
     *                     <p>
     *                     <p>
     *                     两种方式
     *                     方式一：加锁 固定时间内不能重复提交
     *                     <p>
     *                     方式二：先请求获取token，这边再删除token,删除成功则是第一次提交
     */
    @Around("pointCutNoRepeatSubmit(repeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, RepeatSubmit repeatSubmit) throws Throwable {
        log.info("环绕通知-----{接口防重提交}-----执行前");
        // 获取请求
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        //用于记录成功或者失败
        boolean res = false;
        //防重提交类型
        String type = repeatSubmit.limitType().name();
        if (type.equalsIgnoreCase(RepeatSubmit.Type.PARAM.name())) {
            // 参数式放重提交
            long lockTime = repeatSubmit.lockTime();
            String ipAddr = CommonUtil.getIpAddr(request);
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String className = method.getDeclaringClass().getName();
            String key = "order-server:repeat_submit:" + CommonUtil.MD5(String.format("%s-%s-%s-%s", ipAddr, className, method, accountNo));
            RLock lock = redissonClient.getLock(key);
            // 尝试加锁，最多等待0秒，上锁以后5秒自动解锁 [lockTime默认为5s, 可以自定义]
            res = lock.tryLock(0, lockTime, TimeUnit.SECONDS);
        } else {
            // token形式防重提交
            String requestToken = request.getHeader("request-token");
            if (StringUtils.isBlank(requestToken)) {
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
            }
            String key = String.format(RedisKeyConstant.SUBMIT_ORDER_TOKEN_KEY, accountNo, requestToken);
            /*
             * 提交表单的token key
             * 方式一：用lua脚本获取再判断，之前是因为 key组成是 order:submit:accountNo, value是对应的token，所以需要先获取值，再判断
             * 方式二：可以直接key是 order:submit:accountNo:token,然后直接删除成功则完成
             */
            res = Boolean.TRUE.equals(redisTemplate.delete(key));
        }
        if (!res) {
            log.error("请求重复提交");
            return null;
        }
        Object proceed = joinPoint.proceed();
        log.info("环绕通知-----{接口防重提交}-----执行后");
        return proceed;
    }
}
