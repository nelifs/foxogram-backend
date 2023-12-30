package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import su.foxogram.constructors.EmailVerification;
import su.foxogram.constructors.User;

import java.util.List;

public interface EmailVerifyRepository extends CrudRepository<EmailVerification, Long> {

	@AllowFiltering
	EmailVerification findByUserId(long userId);

	@AllowFiltering
	List<EmailVerification> findAllByUserId(long userId);

	@AllowFiltering
	List<EmailVerification> findAllBy();

	@AllowFiltering
	EmailVerification findByDigitCode(String digitCode);

	@AllowFiltering
	List<EmailVerification> findAllByDigitCode(String digitCode);

	@AllowFiltering
	EmailVerification findByLetterCode(String letterCode);

	@AllowFiltering
	List<EmailVerification> findAllByLetterCode(String letterCode);

	@AllowFiltering
	EmailVerification findByType(boolean type);

	@AllowFiltering
	List<EmailVerification> findAllByType(boolean type);

	@Override
	void delete(EmailVerification emailVerification);
}
