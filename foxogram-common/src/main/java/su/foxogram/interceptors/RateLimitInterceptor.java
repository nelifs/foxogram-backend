package su.foxogram.interceptors;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.RateLimitConstants;
import su.foxogram.exceptions.RateLimitExceededException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
	private final Map<String, Bucket> clients = new ConcurrentHashMap<>();

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws RateLimitExceededException {
		String clientRemoteAddr = request.getHeader("X-Forwarded-For");
		Bucket bucket = clients.computeIfAbsent(clientRemoteAddr, this::createNewBucket);

		long availableTokens = bucket.getAvailableTokens();
		long estimateAbilityToConsumeInMs = bucket.estimateAbilityToConsume(RateLimitConstants.RATE_LIMIT_CONSUME).getNanosToWaitForRefill() / 1000000;

		if (bucket.tryConsume(RateLimitConstants.RATE_LIMIT_CONSUME)) return true;
		else {
			log.info("Rate-limited client ({}, {}, {}) successfully", clientRemoteAddr, availableTokens, estimateAbilityToConsumeInMs);
			throw new RateLimitExceededException(estimateAbilityToConsumeInMs);
		}
	}

	private Bucket createNewBucket(String clientRemoteAddr) {
		return Bucket.builder()
				.addLimit(limit -> limit.capacity(RateLimitConstants.RATE_LIMIT_CAPACITY).refillGreedy(RateLimitConstants.RATE_LIMIT_REFILL, Duration.ofMinutes(RateLimitConstants.RATE_LIMIT_DURATION)))
				.build();
	}
}
