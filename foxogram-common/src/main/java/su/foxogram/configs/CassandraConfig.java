package su.foxogram.configs;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = {"su.foxogram.repositories"})
@ConfigurationProperties(prefix = "cassandra")
@Getter
@Setter
public class CassandraConfig {

	private String host;
	private String username;
	private String password;
	private int port;
	private String datacenter;
	private String keyspace;

	@Bean
	public CqlSessionFactoryBean session() {
		CqlSessionFactoryBean session = new CqlSessionFactoryBean();
		session.setContactPoints(host);
		session.setUsername(username);
		session.setPassword(password);
		session.setPort(port);
		session.setKeyspaceName(keyspace);
		session.setLocalDatacenter(datacenter);
		session.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);

		return session;
	}

	@Bean
	public CassandraOperations cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
		return new CassandraTemplate(sessionFactory, converter);
	}
}