package com.esferalia.aon.watson.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AonArrayUtils {
	
	private AonArrayUtils() {
		
	}

	/**
	 * An empty immutable {@code String} array.
	 */
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * An empty immutable <code>byte</code> array.
     */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     * 
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static byte[] nullToEmpty(byte[] array) {
        if (array == null || array.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        return array;
    }
	
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
	
	public static Boolean constains(final String[] array, final String str) {
		if (array == null) {
			return false;
		}
		Boolean bool = false;
		Integer i = 0;
		while(i < array.length && !bool) {
			if(str.equals(array[i])) {
				bool = true;
			}
			i++;
		}
		return bool;
	}
	
	public static Boolean constainsIgnoreCase(final String[] array, final String str) {
		if (array == null) {
			return false;
		}
		Boolean bool = false;
		Integer i = 0;
		while(i < array.length && !bool) {
			if(str.equalsIgnoreCase(array[i])) {
				bool = true;
			}
			i++;
		}
		return bool;
	}
	
	
	public static <T> List<List<T>> allNoRepeatCombinations(T[] dataset) {
		
		if(dataset.length == 0) {
			return new ArrayList<>();
		}
		
		if(dataset.length == 1) {
			ArrayList<List<T>> aux = new ArrayList<>();
			aux.add(new ArrayList<T>());
			aux.get(0).add(dataset[0]);
			return aux;
		}
		
		ArrayList<List<T>> total = new ArrayList<>();
		
		// Add first line
		List<T> first = Arrays.asList(dataset);
		total.add(first);
		
		// set current to first
		List<T> current = new ArrayList<>();
		current.addAll(first);
		
		// while combination isn't repeating
		do {
			
			T moving = current.get(0);  
			
			// for each movement
			for (int i = 0; i < dataset.length - 1; i++) {
				
				T next = current.get(i + 1);
				current.set(i, next);
				current.set(i + 1 , moving);
				
				total.add(new ArrayList<T>(current));
			}
			
		} while(!current.toString().equals(first.toString()));
		
		return total;
	}
	
    /**
     * <p>Returns the length of the specified array.
     * This method can deal with <code>Object</code> arrays and with primitive arrays.</p>
     *
     * <p>If the input array is <code>null</code>, <code>0</code> is returned.</p>
     *
     * <pre>
     * ArrayUtils.getLength(null)            = 0
     * ArrayUtils.getLength([])              = 0
     * ArrayUtils.getLength([null])          = 1
     * ArrayUtils.getLength([true, false])   = 2
     * ArrayUtils.getLength([1, 2, 3])       = 3
     * ArrayUtils.getLength(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array  the array to retrieve the length from, may be null
     * @return The length of the array, or <code>0</code> if the array is <code>null</code>
     * @throws IllegalArgumentException if the object arguement is not an array.
     * @since 2.1
     */
    public static int getLength(Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

}
