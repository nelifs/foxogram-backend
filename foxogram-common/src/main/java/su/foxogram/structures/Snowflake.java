package su.foxogram.structures;

import java.time.Instant;

public class Snowflake {
	// Epoch since foxogram launch
	public static final long DEFAULT_CUSTOM_EPOCH = 1698406020000L;

	private static final int NODE_ID_BITS = 10;

	private static final int SEQUENCE_BITS = 12;

	private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

	private static long customEpoch = 0;

	private final long nodeId;

	private volatile long lastTimestamp = -1L;

	private volatile long sequence = 0L;

	// Create Snowflake with a nodeId and custom epoch
	public Snowflake() {
		this.nodeId = 1;
		Snowflake.customEpoch = DEFAULT_CUSTOM_EPOCH;
	}

	public static long[] parse(long id) {
		long maskNodeId = ((1L << NODE_ID_BITS) - 1) << SEQUENCE_BITS;
		long maskSequence = (1L << SEQUENCE_BITS) - 1;

		long timestamp = (id >> (NODE_ID_BITS + SEQUENCE_BITS)) + customEpoch;
		long nodeId = (id & maskNodeId) >> SEQUENCE_BITS;
		long sequence = id & maskSequence;

		return new long[]{timestamp, nodeId, sequence};
	}

	public synchronized long nextId() {
		long currentTimestamp = timestamp();

		if (currentTimestamp < lastTimestamp) {
			throw new IllegalStateException("Invalid System Clock!");
		}

		if (currentTimestamp == lastTimestamp) {
			sequence = (sequence + 1) & maxSequence;
			if (sequence == 0) {
				// Sequence Exhausted, wait till next millisecond.
				currentTimestamp = waitNextMillis(currentTimestamp);
			}
		} else {
			// reset sequence to start with zero for the next millisecond
			sequence = 0;
		}

		lastTimestamp = currentTimestamp;

		return currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS)
				| (nodeId << SEQUENCE_BITS)
				| sequence;
	}

	// Get current timestamp in milliseconds, adjust for the custom epoch.
	private long timestamp() {
		return Instant.now().toEpochMilli() - customEpoch;
	}

	// Block and wait till next millisecond
	private long waitNextMillis(long currentTimestamp) {
		while (currentTimestamp == lastTimestamp) {
			currentTimestamp = timestamp();
		}
		return currentTimestamp;
	}

	public static String create() {
		return String.valueOf(new Snowflake().nextId());
	}
}
