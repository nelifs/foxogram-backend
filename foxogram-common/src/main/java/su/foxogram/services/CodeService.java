package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxogram.configs.APIConfig;
import su.foxogram.exceptions.CodeExpiredException;
import su.foxogram.exceptions.CodeIsInvalidException;
import su.foxogram.models.Code;
import su.foxogram.repositories.CodeRepository;

@Slf4j
@Service
public class CodeService {

	private final CodeRepository codeRepository;

	private final APIConfig apiConfig;

	public CodeService(CodeRepository codeRepository, APIConfig apiConfig) {
		this.codeRepository = codeRepository;
		this.apiConfig = apiConfig;
	}

	public Code validateCode(String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = codeRepository.findByValue(pathCode);

		if (apiConfig.isDevelopment()) return null;

		if (code == null)
			throw new CodeIsInvalidException();

		if (code.expiresAt <= System.currentTimeMillis())
			throw new CodeExpiredException();

		return code;
	}

	public void deleteCode(Code code) {
		codeRepository.delete(code);
		log.info("CODE record deleted ({}, {}) successfully", code.getUserId(), code.getValue());
	}

	public void saveCode(long id, String type, String digitCode, long issuedAt, long expiresAt) {
		Code code = new Code(id, type, digitCode, issuedAt, expiresAt);
		codeRepository.save(code);
	}
}
