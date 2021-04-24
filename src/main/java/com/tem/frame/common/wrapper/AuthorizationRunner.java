package com.tem.frame.common.wrapper;

import com.tem.frame.dao.RoleDao;
import com.tem.frame.dao.UserDao;
import com.tem.frame.dao.UserRoleDao;
import com.tem.frame.pojo.po.Role;
import com.tem.frame.pojo.po.User;
import com.tem.frame.pojo.po.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Optional;

/**
 * 服务启动时拉取接口权限缓存
 */
@Slf4j
@Component
public class AuthorizationRunner implements CommandLineRunner {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

//    @Autowired
//    private WebApplicationContext webApplicationContext;

//    /**
//     * 是否启用接口授权
//     */
//    @Value("${api.authority}")
//    private boolean isAuthority;


    @Override
    public void run(String... args) {

//        if (isAuthority) {
//            //缓存接口权限
//            this.cacheAuthority();
//        }

        // 初始化超级管理员
        this.initAdmin();

    }

//    /**
//     * 缓存接口权限
//     */
//    public void cacheAuthority() {
//        //未避免与 Swagger2 中的一个类型冲突 , 所以使用 beanName 的方式获取
//        RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) this.webApplicationContext.getBean("requestMappingHandlerMapping");
//        //获取全部方法
//        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
//
//        //用于装载解析出来的全部权限
//        AuthorityRegistry authorityRegistry = AuthorityRegistry.INSTANCE;
//
//        //循环
//        for (RequestMappingInfo info : map.keySet()) {
//
//            //获取提供该接口的方法
//            HandlerMethod method = map.get(info);
//
//            /*
//            获取该方法上标注的 Authority 注解
//            仅当该接口方法上存在 Authority 注解时才会认为该接口会通过网关进行暴露 , 否则程序将不再对该接口进行处理 , 网关也不会允许访问该接口
//             */
//            Optional<Authority> authorityO = Optional.ofNullable(method.getMethodAnnotation(Authority.class));
//            authorityO.ifPresent(authority -> {
//
//                //获取 path 地址
//                String path = (String) info.getPatternsCondition().getPatterns().toArray()[0];
//
//                //获取请求类型
//                String requestMethod = info.getMethodsCondition().getMethods().toArray()[0].toString();
//
//                AuthorityWrapper wrapper = AuthorityWrapper
//                        .builder()
//                        .path(path)
//                        .method(requestMethod)
//                        .mark(authority.mark())
//                        .name(authority.name())
//                        .build();
//
//                authorityRegistry.put(requestMethod, path, wrapper);
//
//            });
//
//        }
//    }

    /**
     * 初始化超级管理员
     */
    public void initAdmin() {
        log.info("系统初始化超级管理员角色");
        Role role = new Role();
        role.setId(0L);
        role.setName("admin");
        role.setMark("admin");

        try {
            this.roleDao.insert(role);
        } catch (Exception ignored) {
        }

        log.info("系统初始化超级管理员用户");
        User user = new User();
        user.setId(0L);
        user.setUsername("admin");
        user.setPassword("admin");

        try {
            this.userDao.insert(user);
        } catch (Exception ignored) {
        }

        UserRole ur = new UserRole();
        ur.setId(0L);
        ur.setUserId(0L);
        ur.setRoleId(0L);

        try {
            this.userRoleDao.insert(ur);
        } catch (Exception ignored) {
        }

    }

}
