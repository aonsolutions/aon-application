package com.esferalia.aon.occam.test;

import org.junit.Assert;
import org.junit.function.ThrowingRunnable;

/**
 * Fase 1 de la migracion JUnit4 -> JUnit5 (aon-occam).
 *
 * Helper de aserciones con la MISMA notacion que {@code org.junit.jupiter.api.Assertions}
 * (el mensaje como ULTIMO argumento), pero que internamente delega en {@code org.junit.Assert}
 * (JUnit 4, mensaje primero). Permite migrar todas las llamadas a la notacion de JUnit 5
 * SIN cambiar todavia la version de JUnit.
 *
 * Cuando se migre a JUnit 5, el salto en cada clase de test se reduce a sustituir
 *   import static com.esferalia.aon.occam.test.OccamAssertions.*;
 * por
 *   import static org.junit.jupiter.api.Assertions.*;
 * (las firmas coinciden; los lambdas de assertThrows se revinculan a Executable).
 *
 * NO usar tildes ni caracteres no ASCII en este fichero.
 */
public final class OccamAssertions {

    private OccamAssertions() { }

    // ---------- assertEquals ----------
    public static void assertEquals(Object expected, Object actual) {
        Assert.assertEquals(expected, actual);
    }
    public static void assertEquals(Object expected, Object actual, String message) {
        Assert.assertEquals(message, expected, actual);
    }
    public static void assertEquals(long expected, long actual) {
        Assert.assertEquals(expected, actual);
    }
    public static void assertEquals(long expected, long actual, String message) {
        Assert.assertEquals(message, expected, actual);
    }
    public static void assertEquals(double expected, double actual, double delta) {
        Assert.assertEquals(expected, actual, delta);
    }
    public static void assertEquals(double expected, double actual, double delta, String message) {
        Assert.assertEquals(message, expected, actual, delta);
    }

    // ---------- assertNotEquals ----------
    public static void assertNotEquals(Object unexpected, Object actual) {
        Assert.assertNotEquals(unexpected, actual);
    }
    public static void assertNotEquals(Object unexpected, Object actual, String message) {
        Assert.assertNotEquals(message, unexpected, actual);
    }

    // ---------- assertTrue / assertFalse ----------
    public static void assertTrue(boolean condition) {
        Assert.assertTrue(condition);
    }
    public static void assertTrue(boolean condition, String message) {
        Assert.assertTrue(message, condition);
    }
    public static void assertFalse(boolean condition) {
        Assert.assertFalse(condition);
    }
    public static void assertFalse(boolean condition, String message) {
        Assert.assertFalse(message, condition);
    }

    // ---------- assertNull / assertNotNull ----------
    public static void assertNull(Object actual) {
        Assert.assertNull(actual);
    }
    public static void assertNull(Object actual, String message) {
        Assert.assertNull(message, actual);
    }
    public static void assertNotNull(Object actual) {
        Assert.assertNotNull(actual);
    }
    public static void assertNotNull(Object actual, String message) {
        Assert.assertNotNull(message, actual);
    }

    // ---------- assertSame / assertNotSame ----------
    public static void assertSame(Object expected, Object actual) {
        Assert.assertSame(expected, actual);
    }
    public static void assertSame(Object expected, Object actual, String message) {
        Assert.assertSame(message, expected, actual);
    }
    public static void assertNotSame(Object unexpected, Object actual) {
        Assert.assertNotSame(unexpected, actual);
    }
    public static void assertNotSame(Object unexpected, Object actual, String message) {
        Assert.assertNotSame(message, unexpected, actual);
    }

    // ---------- fail ----------
    public static void fail() {
        Assert.fail();
    }
    public static void fail(String message) {
        Assert.fail(message);
    }

    // ---------- assertThrows ----------
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, ThrowingRunnable runnable) {
        return Assert.assertThrows(expectedType, runnable);
    }
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, ThrowingRunnable runnable, String message) {
        return Assert.assertThrows(message, expectedType, runnable);
    }
}
