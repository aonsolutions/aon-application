package net.aonsolutions.watson.test.client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.client.util.AonArrayUtils;


class AonArrayUtilsTests {

    @Test
    void getLength1Test() {
        assertEquals(0, AonArrayUtils.getLength(null));

        final Object[] emptyObjectArray = new Object[0];
        final Object[] notEmptyObjectArray = new Object[]{"aValue"};
        assertEquals(0, AonArrayUtils.getLength(null));
        assertEquals(0, AonArrayUtils.getLength(emptyObjectArray));
        assertEquals(1, AonArrayUtils.getLength(notEmptyObjectArray));

        final int[] emptyIntArray = new int[]{};
        final int[] notEmptyIntArray = new int[]{1};
        assertEquals(0, AonArrayUtils.getLength(null));
        assertEquals(0, AonArrayUtils.getLength(emptyIntArray));
        assertEquals(1, AonArrayUtils.getLength(notEmptyIntArray));

        final short[] emptyShortArray = new short[]{};
        final short[] notEmptyShortArray = new short[]{1};
        assertEquals(0, AonArrayUtils.getLength(null));
        assertEquals(0, AonArrayUtils.getLength(emptyShortArray));
        assertEquals(1, AonArrayUtils.getLength(notEmptyShortArray));

        final char[] emptyCharArray = new char[]{};
        final char[] notEmptyCharArray = new char[]{1};
        assertEquals(0, AonArrayUtils.getLength(null));
        assertEquals(0, AonArrayUtils.getLength(emptyCharArray));
        assertEquals(1, AonArrayUtils.getLength(notEmptyCharArray));
    }
    
    @Test
    void getLength2Test() {

        final byte[] emptyByteArray = new byte[]{};
        final byte[] notEmptyByteArray = new byte[]{1};
        assertEquals(0, AonArrayUtils.getLength(null));
        assertEquals(0, AonArrayUtils.getLength(emptyByteArray));
        assertEquals(1, AonArrayUtils.getLength(notEmptyByteArray));

        final double[] emptyDoubleArray = new double[]{};
        final double[] notEmptyDoubleArray = new double[]{1.0};
        assertEquals(0, AonArrayUtils.getLength(null));
        assertEquals(0, AonArrayUtils.getLength(emptyDoubleArray));
        assertEquals(1, AonArrayUtils.getLength(notEmptyDoubleArray));

        final float[] emptyFloatArray = new float[]{};
        final float[] notEmptyFloatArray = new float[]{1.0F};
        assertEquals(0, AonArrayUtils.getLength(null));
        assertEquals(0, AonArrayUtils.getLength(emptyFloatArray));
        assertEquals(1, AonArrayUtils.getLength(notEmptyFloatArray));

        final boolean[] emptyBooleanArray = new boolean[]{};
        final boolean[] notEmptyBooleanArray = new boolean[]{true};
        assertEquals(0, AonArrayUtils.getLength(null));
        assertEquals(0, AonArrayUtils.getLength(emptyBooleanArray));
        assertEquals(1, AonArrayUtils.getLength(notEmptyBooleanArray));

        assertThrows(IllegalArgumentException.class, () -> AonArrayUtils.getLength("notAnArray"));
    }
    
    @Test
    void getEmpty() {
        final Object[] emptyObjectArray = new Object[0];
        final Object[] notEmptyObjectArray = new Object[]{"aValue"};
        assertTrue(AonArrayUtils.isEmpty(null));
        assertTrue(AonArrayUtils.isEmpty(emptyObjectArray));
        assertFalse(AonArrayUtils.isEmpty(notEmptyObjectArray));
    }
	
}
