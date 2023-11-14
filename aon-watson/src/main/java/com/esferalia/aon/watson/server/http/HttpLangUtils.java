package com.esferalia.aon.watson.server.http;

/**
 * A set of utility methods to help produce consistent
 * {@link Object#equals equals} and {@link Object#hashCode hashCode} methods.
 *
 *
 * @since 4.0
 */
final class HttpLangUtils {

    static final int HASH_SEED = 17;
    static final int HASH_OFFSET = 37;

    /** Disabled default constructor. */
    private HttpLangUtils() {
    }

    static int hashCode(final int seed, final int hashcode) {
        return seed * HASH_OFFSET + hashcode;
    }

    static int hashCode(final int seed, final boolean b) {
        return hashCode(seed, b ? 1 : 0);
    }

    static int hashCode(final int seed, final Object obj) {
        return hashCode(seed, obj != null ? obj.hashCode() : 0);
    }

    /**
     * Check if two objects are equal.
     *
     * @param obj1 first object to compare, may be {@code null}
     * @param obj2 second object to compare, may be {@code null}
     * @return {@code true} if the objects are equal or both null
     */
    static boolean equals(final Object obj1, final Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

    /**
     * Check if two object arrays are equal.
     * <ul>
     * <li>If both parameters are null, return {@code true}</li>
     * <li>If one parameter is null, return {@code false}</li>
     * <li>If the array lengths are different, return {@code false}</li>
     * <li>Compare array elements using .equals(); return {@code false} if any comparisons fail.</li>
     * <li>Return {@code true}</li>
     * </ul>
     *
     * @param a1 first array to compare, may be {@code null}
     * @param a2 second array to compare, may be {@code null}
     * @return {@code true} if the arrays are equal or both null
     */
    static boolean equals(final Object[] a1, final Object[] a2) {
        if (a1 == null) {
            return a2 == null;
        }
        if (a2 != null && a1.length == a2.length) {
            for (int i = 0; i < a1.length; i++) {
                if (!equals(a1[i], a2[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
