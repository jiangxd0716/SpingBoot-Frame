package com.tem.frame.handle;

import cn.hutool.core.util.StrUtil;
import com.tem.frame.thread.CurrentUser;
import com.tem.frame.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局请求拦截器
 */
@Slf4j
@Component
public class GlobalRequestHandler implements HandlerInterceptor {


    /**
     * JWT Token 在请求头中的 KEY
     */
    @Value("${jwt.key}")
    private String jwtKey;

    /**
     * JWT Token 加密签名
     */
    @Value("${jwt.sign}")
    private String jwtSign;

    private final String OPETIONS = "OPTIONS";

    /**
     * 请求前置拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 处理跨域所产生的 OPETIONS 请求
        if (request.getMethod().equals(OPETIONS)) {
            return Boolean.TRUE;
        }

        // 从请求头中获取前端通过 header 传递过来的 token
        String jwtToken = request.getHeader(this.jwtKey);

        // 某些接口是不一定需要登录状态的
        // 但是只要存在 TOKEN 则必须进行解析并保证 TOKEN 正常
        if (!StrUtil.isEmpty(jwtToken)) {
            JWTUtil.JWT jwt = JWTUtil.INSTANCE.check(jwtToken, this.jwtSign);
            if (jwt.getStatus() == 0) {     //TOKEN 解析正常
                CurrentUser.init(Long.parseLong(jwt.getUserId()), jwt.getUsername());
                // 若本次请求合法，记录本次请求的各项参数及请求人
                log.info("REQUEST:[{}:{}][{}:{}]", request.getMethod(), request.getRequestURI(), CurrentUser.getId(), CurrentUser.getUsername());
                return Boolean.TRUE;
            }
        }

        // 若存在非法请求，则记录这次非法请求
        // 不记录没意义
        // log.error("ILLEGAL REQUEST:[{}:{}]", request.getMethod(), request.getRequestURI());

        // 其它情况, 如不存在 token 或 token 解析异常的均提示未授权
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return Boolean.FALSE;

    }

    /**
     * 请求相应拦截
     * 不论接口内部是否发生异常，相应都会进入该方法
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 请求结束，记录相应日志
        log.info("RESPONSE:[{}:{}][{}:{}]", request.getMethod(), request.getRequestURI(), CurrentUser.getId(), CurrentUser.getUsername());
        //当前请求结束需要销毁线程中存储的内容，否则线程池的作用会导致这些缓存的数据无法被虚拟机销毁
        CurrentUser.destroy();
    }

}
