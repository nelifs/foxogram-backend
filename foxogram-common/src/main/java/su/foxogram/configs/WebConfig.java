package su.foxogram.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import su.foxogram.interceptors.AuthenticationInterceptor;
import su.foxogram.interceptors.ChannelInterceptor;
import su.foxogram.interceptors.MemberInterceptor;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.services.AuthenticationService;
import su.foxogram.services.ChannelsService;
import su.foxogram.services.JwtService;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final ChannelsService channelsService;
    private final MemberRepository memberRepository;

    @Autowired
    public WebConfig(AuthenticationService authenticationService, JwtService jwtService, ChannelsService channelsService, MemberRepository memberRepository) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.channelsService = channelsService;
        this.memberRepository = memberRepository;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(authenticationService, jwtService)).excludePathPatterns("/v1/auth/signup", "/v1/auth/login");
        registry.addInterceptor(new ChannelInterceptor(channelsService)).excludePathPatterns("/v1/auth/**", "/v1/users/**", "/v1/channels/create");
        registry.addInterceptor(new MemberInterceptor(memberRepository, channelsService)).excludePathPatterns("/v1/auth/**", "/v1/users/**", "/v1/channels/create");
    }
}
