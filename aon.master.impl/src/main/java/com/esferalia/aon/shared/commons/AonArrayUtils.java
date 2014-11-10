package com.esferalia.aon.shared.commons;

public class AonArrayUtils {

	/**
	 * An empty immutable {@code String} array.
	 */
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * <p>
	 * Checks if an array of Objects is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final Object[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Checks if an array of primitive longs is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final long[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Checks if an array of primitive ints is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final int[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Checks if an array of primitive shorts is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final short[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Checks if an array of primitive chars is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final char[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Checks if an array of primitive bytes is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final byte[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Checks if an array of primitive doubles is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final double[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Checks if an array of primitive floats is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final float[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Checks if an array of primitive booleans is empty or {@code null}.
	 * </p>
	 *
	 * @param array
	 *            the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final boolean[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * There is no special handling for multi-dimensional arrays.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final Object[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final long[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final int[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final short[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final char[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final byte[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final double[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final float[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 */
	public static void reverse(final boolean[] array) {
		if (array == null) {
			return;
		}
		reverse(array, 0, array.length);
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final boolean[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		boolean tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final byte[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		byte tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final char[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		char tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final double[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		double tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final float[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		float tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final int[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		int tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final long[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		long tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final Object[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		Object tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * <p>
	 * Reverses the order of the given array in the given range.
	 * </p>
	 *
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array
	 *            the array to reverse, may be {@code null}
	 * @param startIndexInclusive
	 *            the starting index. Undervalue (&lt;0) is promoted to 0,
	 *            overvalue (&gt;array.length) results in no change.
	 * @param endIndexExclusive
	 *            elements up to endIndex-1 are reversed in the array.
	 *            Undervalue (&lt; start index) results in no change. Overvalue
	 *            (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final short[] array,
			final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}
		int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
		int j = Math.min(array.length, endIndexExclusive) - 1;
		short tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}
}
