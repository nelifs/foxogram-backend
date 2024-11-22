package su.foxogram.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import su.foxogram.interceptors.AuthenticationInterceptor;
import su.foxogram.services.AuthenticationService;
import su.foxogram.services.JwtService;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Autowired
    public WebConfig(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(authenticationService, jwtService)).excludePathPatterns("/v1/auth/signup", "/v1/auth/login");
    }
}