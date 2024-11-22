package su.foxogram.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import su.foxogram.interceptors.AuthenticationInterceptor;
import su.foxogram.services.AuthenticationService;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    final AuthenticationService authenticationService;

    @Autowired
    public WebConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(authenticationService)).excludePathPatterns("/v1/auth/signup", "/v1/auth/login");
    }
}