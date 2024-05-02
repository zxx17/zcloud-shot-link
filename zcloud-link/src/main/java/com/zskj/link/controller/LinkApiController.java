package com.zskj.link.controller;

import com.zskj.common.enums.link.ShortLinkStateEnum;
import com.zskj.common.util.CommonUtil;
import com.zskj.link.service.ShortLinkService;
import com.zskj.link.vo.ShortLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 * 短链302跳转长链
 * </p>
 */

@Slf4j
@Controller
public class LinkApiController {


    @Autowired
    private ShortLinkService shortLinkService;

    /**
     * 解析 301还是302，这边是返回http code是302
     * <p>
     * 知识点一，为什么要用 301 跳转而不是 302 ？
     * <p>
     * 301 是永久重定向，302 是临时重定向。
     * <p>
     * 短地址一经生成就不会变化，所以用 301 是同时对服务器压力也会有一定减少
     * <p>
     * 但是如果使用了 301，无法统计到短地址被点击的次数。
     * <p>
     * 所以选择302虽然会增加服务器压力，但是有很多数据可以获取进行分析
     *
     * @param shortLinkCode 短链码
     */
    @GetMapping(path = "/{shortLinkCode}")
    public void dispatch(@PathVariable(name = "shortLinkCode") String shortLinkCode,
                         HttpServletRequest request, HttpServletResponse response) {
        if (log.isInfoEnabled()) {
            log.info("短链码：{}", shortLinkCode);
        }
        try {
            //判断短链码是否合规
            if (isLetterDigit(shortLinkCode)) {
                //查找短链
                ShortLinkVO shortLinkVO = shortLinkService.parseShortLinkCode(shortLinkCode);
                //判断是否过期和可用
                if (isVisitable(shortLinkVO)) {
                    response.setHeader("Location", shortLinkVO.getOriginalUrl());
                    //302跳转
                    response.setStatus(HttpStatus.FOUND.value());
                } else {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                }
            }
        } catch (Exception e) {
            log.error("服务端短链解析异常");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }


    /**
     * 判断短链是否可用
     *
     * @param shortLinkVO vo
     * @return bool
     */
    private static boolean isVisitable(ShortLinkVO shortLinkVO) {
        // 有过期时间
        if ((shortLinkVO != null && shortLinkVO.getExpired().getTime() > CommonUtil.getCurrentTimestamp())) {
            return ShortLinkStateEnum.ACTIVE.name().equalsIgnoreCase(shortLinkVO.getState());
            // 没有过期时间
        } else if ((shortLinkVO != null && shortLinkVO.getExpired().getTime() == -1)) {
            return ShortLinkStateEnum.ACTIVE.name().equalsIgnoreCase(shortLinkVO.getState());
        }
        return false;
    }

    /**
     * 仅包括数字和字母
     *
     * @param str 短链码
     * @return bool
     */
    private static boolean isLetterDigit(String str) {
        // ^和$分别表示字符串的开始和结束
        String regex = "^[a-z\\dA-Z]+$";
        return str.matches(regex);
    }


}
