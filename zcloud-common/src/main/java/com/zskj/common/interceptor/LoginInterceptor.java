package com.zskj.common.interceptor;

import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.model.LoginUser;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.JWTUtil;
import com.zskj.common.util.JsonData;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/25
 * <p>
 * 登录拦截器
 * </p>
 */
@SuppressWarnings("NullableProblems")
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler)  {
        // 放行OPTION请求
        if (HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }
        // 获取请求头的token
        String accessToken = request.getHeader("token");
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter("token");
        }

        // 获取到了进行校验
        if (StringUtils.isNotBlank(accessToken)) {
            // 解析jwt
            Claims claims = JWTUtil.checkJWT(accessToken);
            if (claims == null) {
                //未登录
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }
            // 获取token中的信息
            long accountNo = Long.parseLong(claims.get("account_no").toString());
            String headImg = (String) claims.get("head_img");
            String username = (String) claims.get("username");
            String mail = (String) claims.get("mail");
            String phone = (String) claims.get("phone");
            String auth = (String) claims.get("auth");

            LoginUser loginUser = LoginUser.builder()
                    .accountNo(accountNo)
                    .auth(auth)
                    .phone(phone)
                    .headImg(headImg)
                    .mail(mail)
                    .username(username)
                    .build();

            // 通过threadLocal 或者 `request.setAttribute("loginUser",loginUser);`
            threadLocal.set(loginUser);
            return true;
        }
        log.error("异常请求: {}", "请求未携带TOKEN 或 请求路径不存在");
        CommonUtil.sendJsonMessage(response, BizCodeEnum.ACCOUNT_UNLOGIN);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)  {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        threadLocal.remove();
    }
}
