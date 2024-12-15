package su.foxogram.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
	private final APIConfig apiConfig;

	public OpenAPIConfig(APIConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info().title("Foxogram").version(apiConfig.getVersion());
		SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");
		Components components = new Components()
				.addSecuritySchemes("Authorization", new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT"));

		return new OpenAPI()
				.info(info)
				.addSecurityItem(securityRequirement)
				.components(components);
	}
}
