package net.aonsolutions.watson.server;

import java.util.Optional;
import java.util.function.Function;

import net.aonsolutions.watson.client.util.AonNumberUtils;

public class AonObjectUtils {

	private AonObjectUtils() {
	}

	public static boolean equals(final Object obj1, final Object obj2) {
		if (obj1 == obj2) {
			return true;
		}
		if (obj1 == null || obj2 == null) {
			return false;
		}
		return obj1.equals(obj2);
	}

	public static boolean notEquals(final Object obj1, final Object obj2) {
		return !equals(obj1, obj2);
	}

	public static boolean equals(final Number n1, final Number n2) {
		return AonNumberUtils.equals(n1, n2);
	}

	public static boolean notEquals(final Number n1, final Number n2) {
		return !equals(n1, n2);
	}

	public static <T> T defaultIfNull(final T object, final T defaultValue) {
		return object != null ? object : defaultValue;
	}

	public static <T, R> R ifOptionalPresent(Optional<T> value, Function<T, R> action) {
		return ifPresent(value.orElse(null), action);
	}

	/**
	 * @Deprecated use ifNotNullDo
	 */
	@Deprecated 
	public static <T, R> R ifPresent(T value, Function<T, R> action) {
		return ifNotNullDo(value, action);
	}

	public static <T, R> R ifNotNullDo(final T object, Function<T, R> value) {
		return object == null ? null : value.apply(object);
	}

	public static void ifTrue(boolean cond, Runnable action) {
		if (cond ) action.run();
	}
}
