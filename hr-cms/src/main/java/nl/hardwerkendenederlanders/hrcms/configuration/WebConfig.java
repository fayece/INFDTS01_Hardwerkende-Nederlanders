package nl.hardwerkendenederlanders.hrcms.configuration;

import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final PermissionInterceptor permissionInterceptor;
    private final UserSessionService userSessionService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**").addResourceLocations("file:uploads/media/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/error/**", "/css/**", "/js/**");
    }

    @Bean
    public FilterRegistrationBean<DbRoleFilter> dbRoleFilter() {
        FilterRegistrationBean<DbRoleFilter> registration = new FilterRegistrationBean<>(
                new DbRoleFilter(userSessionService, userRepository, roleRepository));
        registration.addUrlPatterns("/*");
        return registration;
    }
}
