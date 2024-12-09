package su.foxogram.interceptors;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.APIConstants;
import su.foxogram.exceptions.RateLimitExceededException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
	private final Map<String, Bucket> clients = new ConcurrentHashMap<>();

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws RateLimitExceededException {
		String clientRemoteAddr = request.getRemoteAddr();

		Bucket bucket = clients.computeIfAbsent(clientRemoteAddr, this::createNewBucket);

		long estimateAbilityToConsumeInMs = bucket.estimateAbilityToConsume(APIConstants.RATE_LIMIT_CONSUME).getNanosToWaitForRefill() / 1000000;

		if (bucket.tryConsume(10)) return true;
		else throw new RateLimitExceededException(estimateAbilityToConsumeInMs);
	}

	private Bucket createNewBucket(String clientRemoteAddr) {
		return Bucket.builder()
				.addLimit(limit -> limit.capacity(APIConstants.RATE_LIMIT_CAPACITY).refillGreedy(APIConstants.RATE_LIMIT_REFILL, Duration.ofMinutes(APIConstants.RATE_LIMIT_DURATION)))
				.build();
	}
}
