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

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	private final AuthenticationInterceptor authenticationInterceptor;

	private final ChannelInterceptor channelInterceptor;

	private final MemberInterceptor memberInterceptor;

	@Autowired
	public WebConfig(AuthenticationInterceptor authenticationInterceptor, ChannelInterceptor channelInterceptor, MemberInterceptor memberInterceptor) {
		this.authenticationInterceptor = authenticationInterceptor;
		this.channelInterceptor = channelInterceptor;
		this.memberInterceptor = memberInterceptor;
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
		registry.addInterceptor(authenticationInterceptor).excludePathPatterns("/v1/auth/register", "/v1/auth/login", "/docs");
		registry.addInterceptor(channelInterceptor).excludePathPatterns("/v1/auth/**", "/v1/users/**", "/v1/channels/create", "/docs");
		registry.addInterceptor(memberInterceptor).excludePathPatterns("/v1/auth/**", "/v1/users/**", "/v1/channels/create", "/docs");
	}
}
