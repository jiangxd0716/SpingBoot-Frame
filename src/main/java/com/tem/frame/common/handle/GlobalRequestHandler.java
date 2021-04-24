package com.tem.frame.common.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.tem.frame.common.thread.CurrentUser;
import com.tem.frame.common.utils.JWTUtil;
import com.tem.frame.common.wrapper.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
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

    /**
     * 是否启用接口授权
     */
    @Value("${api.authority}")
    private boolean isAuthority;


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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 处理跨域所产生的 OPETIONS 请求
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return Boolean.TRUE;
        }

        // 从请求头中获取前端通过 header 传递过来的 token
        String jwtToken = request.getHeader(this.jwtKey);

        // 但是只要存在 TOKEN 则必须进行解析并保证 TOKEN 正常
        if (StrUtil.isNotBlank(jwtToken)) {
            JWTUtil.JWT jwt = JWTUtil.INSTANCE.check(jwtToken, this.jwtSign);
            if (jwt.getStatus() == JWTUtil.JWT.NORMAL) {     //TOKEN 解析正常

                // 配置文件开启接口授权
                if (isAuthority) {
                    // 判断类方法是否有权限注解，如果没有则跳过授权
                    Authority annotation = null;
                    if (handler instanceof HandlerMethod) {
                        annotation = ((HandlerMethod) handler).getMethodAnnotation(Authority.class);
                    }

                    // 类方法有权限注解，需要判断用户是否有该权限
                    if (null != annotation && !this.authority(annotation.mark(), jwt)) {
                        //boolean authority = this.authority(request, jwt);
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        return Boolean.FALSE;
                    }
                }

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

    /**
     * 判断用户是否有该接口权限
     *
     * @param mark 接口类方法的权限标识
     * @param jwt  用户权限
     * @return
     */
    private boolean authority(String mark, JWTUtil.JWT jwt) {
        String[] userAuthoritys = mark.split(",");

        // 从jwt中取出用户权限标识，判断用户是否有该接口的访问权限
        for (String userAuthority : userAuthoritys) {
            // 用户有权限且有该接口权限标识，则允许访问
            if (CollUtil.isNotEmpty(jwt.getAuthorityMark()) && jwt.getAuthorityMark().contains(userAuthority)) {
                return Boolean.TRUE;
            }
        }
        // 接口有权限，但是该用户没有权限 返回401未授权
        return Boolean.FALSE;
    }

//    /**
//     * 开启接口授权
//     *
//     * @param request
//     * @param jwt
//     * @return
//     */
//    private boolean authority(HttpServletRequest request, JWTUtil.JWT jwt) {
//        // 获取请求接口path
//        String realPath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
//        // 获取请求接口类型
//        String requestMethod = request.getMethod();
//        // 从权限缓存中获取接口权限标识
//        AuthorityWrapper authorityWrapper = AuthorityRegistry.INSTANCE.get(requestMethod, realPath);
//
//        // 没有注解不允许访问
//        if (authorityWrapper == null) {
//            return Boolean.FALSE;
//        }
//
//        // 接口需要授权
//        if (authorityWrapper.isLogin()) {
//            String[] userAuthoritys = authorityWrapper.getMark().split(",");
//
//            // 从jwt中取出用户权限标识，判断用户是否有该接口的访问权限
//            for (String userAuthority : userAuthoritys) {
//                if (jwt.getAuthorityMark().contains(userAuthority)) {
//                    return Boolean.TRUE;
//                }
//            }
//
//            // 没有权限
//            return Boolean.FALSE;
//
//        }
//
//        // 接口不需要授权，直接返回
//        return Boolean.TRUE;
//    }

}
