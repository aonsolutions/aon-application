package net.aonsolutions.watson.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.client.MutableBoolean;

class MutableBooleanTest {

    @Test
	void testCompareTo() {
        final MutableBoolean mutBool = new MutableBoolean(false);

        assertEquals(0, mutBool.compareTo(new MutableBoolean(false)));
        assertEquals(-1, mutBool.compareTo(new MutableBoolean(true)));
        mutBool.setValue(true);
        assertEquals(+1, mutBool.compareTo(new MutableBoolean(false)));
        assertEquals(0, mutBool.compareTo(new MutableBoolean(true)));
    }

    @Test
    void testCompareToNull() {
        final MutableBoolean mutBool = new MutableBoolean(false);
        assertThrows(NullPointerException.class, () -> mutBool.compareTo(null));
    }

    @Test
    void testConstructorNull() {
        assertThrows(NullPointerException.class, () -> new MutableBoolean(null));
    }

    // ----------------------------------------------------------------
    @Test
    void testConstructors() {
        assertFalse(new MutableBoolean().booleanValue());

        assertTrue(new MutableBoolean(true).booleanValue());
        assertFalse(new MutableBoolean(false).booleanValue());

        assertTrue(new MutableBoolean(Boolean.TRUE).booleanValue());
        assertFalse(new MutableBoolean(Boolean.FALSE).booleanValue());

    }

    @Test
    void testEquals() {
        final MutableBoolean mutBoolA = new MutableBoolean(false);
        final MutableBoolean mutBoolB = new MutableBoolean(false);
        final MutableBoolean mutBoolC = new MutableBoolean(true);

        String a = "OTHER OBJECT";
        assertNotEquals( mutBoolA, a);
        
        assertEquals(mutBoolA, mutBoolA);
        assertEquals(mutBoolA, mutBoolB);
        assertEquals(mutBoolB, mutBoolA);
        assertEquals(mutBoolB, mutBoolB);
        assertNotEquals(mutBoolA, mutBoolC);
        assertNotEquals(mutBoolB, mutBoolC);
        assertEquals(mutBoolC, mutBoolC);
        assertNotEquals(null, mutBoolA);
        assertNotEquals(Boolean.FALSE, mutBoolA);
        assertNotEquals("false", mutBoolA);
    }

    @Test
    void testGetSet() {
        assertFalse(new MutableBoolean().booleanValue());
        assertEquals(Boolean.FALSE, new MutableBoolean().getValue());

        final MutableBoolean mutBool = new MutableBoolean(false);
        assertEquals(Boolean.FALSE, mutBool.toBoolean());
        assertFalse(mutBool.booleanValue());
        assertTrue(mutBool.isFalse());
        assertFalse(mutBool.isTrue());

        mutBool.setValue(Boolean.TRUE);
        assertEquals(Boolean.TRUE, mutBool.toBoolean());
        assertTrue(mutBool.booleanValue());
        assertFalse(mutBool.isFalse());
        assertTrue(mutBool.isTrue());

        mutBool.setValue(false);
        assertFalse(mutBool.booleanValue());

        mutBool.setValue(true);
        assertTrue(mutBool.booleanValue());

        mutBool.setFalse();
        assertFalse(mutBool.booleanValue());

        mutBool.setTrue();
        assertTrue(mutBool.booleanValue());

    }

    @Test
    void testHashCode() {
        final MutableBoolean mutBoolA = new MutableBoolean(false);
        final MutableBoolean mutBoolB = new MutableBoolean(false);
        final MutableBoolean mutBoolC = new MutableBoolean(true);

        assertEquals(mutBoolA.hashCode(), mutBoolA.hashCode());
        assertEquals(mutBoolA.hashCode(), mutBoolB.hashCode());
        assertNotEquals(mutBoolA.hashCode(), mutBoolC.hashCode());
        assertEquals(mutBoolA.hashCode(), Boolean.FALSE.hashCode());
        assertEquals(mutBoolC.hashCode(), Boolean.TRUE.hashCode());
    }

    @Test
    void testSetNull() {
        final MutableBoolean mutBool = new MutableBoolean(false);
        assertThrows(NullPointerException.class, () -> mutBool.setValue(null));
    }

    @Test
    void testToString() {
        assertEquals(Boolean.FALSE.toString(), new MutableBoolean(false).toString());
        assertEquals(Boolean.TRUE.toString(), new MutableBoolean(true).toString());
    }

}
