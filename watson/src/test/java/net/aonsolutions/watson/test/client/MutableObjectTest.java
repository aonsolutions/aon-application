package net.aonsolutions.watson.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.client.MutableObject;

class MutableObjectTest {

    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        assertNull(new MutableObject<String>().getValue());

        final Integer i = Integer.valueOf(6);
        assertSame(i, new MutableObject<>(i).getValue());
        assertSame("HI", new MutableObject<>("HI").getValue());
        assertSame(null, new MutableObject<>(null).getValue());
    }

    @Test
    public void testEquals() {
        final MutableObject<String> mutNumA = new MutableObject<>("ALPHA");
        final MutableObject<String> mutNumB = new MutableObject<>("ALPHA");
        final MutableObject<String> mutNumC = new MutableObject<>("BETA");
        final MutableObject<String> mutNumD = new MutableObject<>(null);

        assertEquals(mutNumA, mutNumA);
        assertEquals(mutNumA, mutNumB);
        assertEquals(mutNumB, mutNumA);
        assertEquals(mutNumB, mutNumB);
        assertNotEquals(mutNumA, mutNumC);
        assertNotEquals(mutNumB, mutNumC);
        assertEquals(mutNumC, mutNumC);
        assertNotEquals(mutNumA, mutNumD);
        assertEquals(mutNumD, mutNumD);

        assertNotEquals(null, mutNumA);
        assertNotEquals(mutNumA, new Object());
        assertNotEquals("0", mutNumA);
    }

    @Test
    public void testGetSet() {
        final MutableObject<String> mutNum = new MutableObject<>();
        assertNull(new MutableObject<>().getValue());

        mutNum.setValue("HELLO");
        assertSame("HELLO", mutNum.getValue());

        mutNum.setValue(null);
        assertSame(null, mutNum.getValue());
    }

    @Test
    public void testHashCode() {
        final MutableObject<String> mutNumA = new MutableObject<>("ALPHA");
        final MutableObject<String> mutNumB = new MutableObject<>("ALPHA");
        final MutableObject<String> mutNumC = new MutableObject<>("BETA");
        final MutableObject<String> mutNumD = new MutableObject<>(null);

        assertEquals(mutNumA.hashCode(), mutNumA.hashCode());
        assertEquals(mutNumA.hashCode(), mutNumB.hashCode());
        assertNotEquals(mutNumA.hashCode(), mutNumC.hashCode());
        assertNotEquals(mutNumA.hashCode(), mutNumD.hashCode());
        assertEquals(mutNumA.hashCode(), "ALPHA".hashCode());
        assertEquals(0, mutNumD.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals("HI", new MutableObject<>("HI").toString());
        assertEquals("10.0", new MutableObject<>(Double.valueOf(10)).toString());
        assertEquals("null", new MutableObject<>(null).toString());
    }

}
