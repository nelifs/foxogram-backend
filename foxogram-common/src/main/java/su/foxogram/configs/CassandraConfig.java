package su.foxogram.configs;

import com.datastax.oss.driver.api.core.CqlSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import su.foxogram.util.Env;

@Configuration
@EnableCassandraRepositories(basePackages = {"su.foxogram.repositories"})
public class CassandraConfig extends AbstractCassandraConfiguration {

	@NotNull
	@Override
	protected String getKeyspaceName() {
		return Env.get("DATABASE_KEYSPACE");
	}

	@NotNull
	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.CREATE_IF_NOT_EXISTS;
	}

	@Bean
	@Primary
	public SessionFactoryFactoryBean sessionFactory(CqlSession cassandraSession, CassandraConverter converter) {
		SessionFactoryFactoryBean sessionFactory = new SessionFactoryFactoryBean();
		sessionFactory.setSession(cassandraSession);
		sessionFactory.setConverter(converter);
		sessionFactory.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);
		return sessionFactory;
	}

	@Bean
	public CassandraOperations cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
		return new CassandraTemplate(sessionFactory, converter);
	}
}