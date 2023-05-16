package net.aonsolutions.watson.test.client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.client.util.AonStringUtils;


class AonStringUtilsTests {
	
    private static final String BAR = "bar";
    private static final String FOO = "foo";
    private static final String FOOBAR = "foobar";
    private static final String SENTENCE = "foo bar baz";
    
    static final String WHITESPACE;
    static final String NON_WHITESPACE;
    static final String HARD_SPACE;
    static final String TRIMMABLE;
    static final String NON_TRIMMABLE;

    static {
        final StringBuilder ws = new StringBuilder();
        final StringBuilder nws = new StringBuilder();
        final String hs = String.valueOf(((char) 160));
        final StringBuilder tr = new StringBuilder();
        final StringBuilder ntr = new StringBuilder();
        for (int i = 0; i < Character.MAX_VALUE; i++) {
            if (Character.isWhitespace((char) i)) {
                ws.append(String.valueOf((char) i));
                if (i > 32) {
                    ntr.append(String.valueOf((char) i));
                }
            } else if (i < 40) {
                nws.append(String.valueOf((char) i));
            }
        }
        for (int i = 0; i <= 32; i++) {
            tr.append(String.valueOf((char) i));
        }
        WHITESPACE = ws.toString();
        NON_WHITESPACE = nws.toString();
        HARD_SPACE = hs;
        TRIMMABLE = tr.toString();
        NON_TRIMMABLE = ntr.toString();
    }

    private static class CustomCharSequence implements CharSequence {
        private final CharSequence seq;

        CustomCharSequence(final CharSequence seq) {
            this.seq = seq;
        }

        @Override
        public char charAt(final int index) {
            return seq.charAt(index);
        }

        @Override
        public int length() {
            return seq.length();
        }

        @Override
        public CharSequence subSequence(final int start, final int end) {
            return new CustomCharSequence(seq.subSequence(start, end));
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof CustomCharSequence)) {
                return false;
            }
            final CustomCharSequence other = (CustomCharSequence) obj;
            return seq.equals(other.seq);
        }

        @Override
        public int hashCode() {
            return seq.hashCode();
        }

        @Override
        public String toString() {
            return seq.toString();
        }
    }
    
    @Test
    void testIsEmpty() {
        assertTrue(AonStringUtils.isEmpty(null));
        assertTrue(AonStringUtils.isEmpty(""));
        assertFalse(AonStringUtils.isEmpty(" "));
        assertFalse(AonStringUtils.isEmpty("foo"));
        assertFalse(AonStringUtils.isEmpty("  foo  "));
    }

    @Test
    void testIsNotEmpty() {
        assertFalse(AonStringUtils.isNotEmpty(null));
        assertFalse(AonStringUtils.isNotEmpty(""));
        assertTrue(AonStringUtils.isNotEmpty(" "));
        assertTrue(AonStringUtils.isNotEmpty("foo"));
        assertTrue(AonStringUtils.isNotEmpty("  foo  "));
    }

    @Test
    void testIsAnyEmpty() {
        assertTrue(AonStringUtils.isAnyEmpty((String) null));
        assertFalse(AonStringUtils.isAnyEmpty((String[]) null));
        assertTrue(AonStringUtils.isAnyEmpty(null, "foo"));
        assertTrue(AonStringUtils.isAnyEmpty("", "bar"));
        assertTrue(AonStringUtils.isAnyEmpty("bob", ""));
        assertTrue(AonStringUtils.isAnyEmpty("  bob  ", null));
        assertFalse(AonStringUtils.isAnyEmpty(" ", "bar"));
        assertFalse(AonStringUtils.isAnyEmpty("foo", "bar"));
    }

    @Test
    void testIsBlank() {
        assertTrue(AonStringUtils.isBlank(null));
        assertTrue(AonStringUtils.isBlank(""));
        assertTrue(AonStringUtils.isBlank(WHITESPACE));
        assertFalse(AonStringUtils.isBlank("foo"));
        assertFalse(AonStringUtils.isBlank("  foo  "));
    }

    @Test
    void testIsNotBlank() {
        assertFalse(AonStringUtils.isNotBlank(null));
        assertFalse(AonStringUtils.isNotBlank(""));
        assertFalse(AonStringUtils.isNotBlank(WHITESPACE));
        assertTrue(AonStringUtils.isNotBlank("foo"));
        assertTrue(AonStringUtils.isNotBlank("  foo  "));
    }

    @Test
    void testEquals() {
        final CharSequence fooCs = new StringBuilder(FOO), barCs = new StringBuilder(BAR), foobarCs = new StringBuilder(FOOBAR);
        assertTrue(AonStringUtils.equals(null, null));
        assertTrue(AonStringUtils.equals(new String("a"), new String("a")));
        assertTrue(AonStringUtils.equals(fooCs, fooCs));
        assertTrue(AonStringUtils.equals(fooCs, new StringBuilder(FOO)));
        assertTrue(AonStringUtils.equals(fooCs, new String(new char[] { 'f', 'o', 'o' })));
        assertTrue(AonStringUtils.equals(fooCs, new CustomCharSequence(FOO)));
        assertTrue(AonStringUtils.equals(new CustomCharSequence(FOO), fooCs));
        assertFalse(AonStringUtils.equals(fooCs, new String(new char[] { 'f', 'O', 'O' })));
        assertFalse(AonStringUtils.equals(fooCs, barCs));
        assertFalse(AonStringUtils.equals(fooCs, null));
        assertFalse(AonStringUtils.equals(null, fooCs));
        assertFalse(AonStringUtils.equals(fooCs, foobarCs));
        assertFalse(AonStringUtils.equals(foobarCs, fooCs));
    }


    @Test
    void testEqualsIgnoreCase() {
        assertTrue(AonStringUtils.equalsIgnoreCase(null, null));
        assertTrue(AonStringUtils.equalsIgnoreCase(FOO, FOO));
        assertTrue(AonStringUtils.equalsIgnoreCase(FOO, new String(new char[] { 'f', 'o', 'o' })));
        assertTrue(AonStringUtils.equalsIgnoreCase(FOO, new String(new char[] { 'f', 'O', 'O' })));
        assertFalse(AonStringUtils.equalsIgnoreCase(FOO, BAR));
        assertFalse(AonStringUtils.equalsIgnoreCase(FOO, null));
        assertFalse(AonStringUtils.equalsIgnoreCase(null, FOO));
        assertTrue(AonStringUtils.equalsIgnoreCase("", ""));
        assertFalse(AonStringUtils.equalsIgnoreCase("abcd", "abcd "));
    }

    @Test
    void testAbbreviate_StringInt() {
        assertNull(AonStringUtils.abbreviate(null, 10));
        assertEquals("", AonStringUtils.abbreviate("", 10));
        assertEquals("short", AonStringUtils.abbreviate("short", 10));
        assertEquals("Now is ...", AonStringUtils.abbreviate("Now is the time for all good men to come to the aid of their party.", 10));

        final String raspberry = "raspberry peach";
        assertEquals("raspberry p...", AonStringUtils.abbreviate(raspberry, 14));
        assertEquals("raspberry peach", AonStringUtils.abbreviate("raspberry peach", 15));
        assertEquals("raspberry peach", AonStringUtils.abbreviate("raspberry peach", 16));
        assertEquals("abc...", AonStringUtils.abbreviate("abcdefg", 6));
        assertEquals("abcdefg", AonStringUtils.abbreviate("abcdefg", 7));
        assertEquals("abcdefg", AonStringUtils.abbreviate("abcdefg", 8));
        assertEquals("a...", AonStringUtils.abbreviate("abcdefg", 4));
        assertEquals("", AonStringUtils.abbreviate("", 4));

        assertThrows(
                IllegalArgumentException.class,
                () -> AonStringUtils.abbreviate("abc", 3),
                "StringUtils.abbreviate expecting IllegalArgumentException");
    }


    @Test
    public void testAbbreviate_StringStringIntInt() {
        assertNull(AonStringUtils.abbreviate(null, null, 10, 12));
        assertNull(AonStringUtils.abbreviate(null, "...", 10, 12));
        assertEquals("", AonStringUtils.abbreviate("", null, 0, 10));
        assertEquals("", AonStringUtils.abbreviate("", "...", 2, 10));

        assertThrows(
                IllegalArgumentException.class,
                () -> AonStringUtils.abbreviate("abcdefghij", "::", 0, 2),
                "AonStringUtils.abbreviate expecting IllegalArgumentException");
        assertThrows(
                IllegalArgumentException.class,
                () -> AonStringUtils.abbreviate("abcdefghij", "!!!", 5, 6),
                "AonStringUtils.abbreviate expecting IllegalArgumentException");

        final String raspberry = "raspberry peach";
        assertEquals("raspberry peach", AonStringUtils.abbreviate(raspberry, "--", 12, 15));

        assertNull(AonStringUtils.abbreviate(null, ";", 7, 14));
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefgh;;", ";;", -1, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefghi.", ".", 0, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefgh++", "++", 1, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdefghi*", "*", 2, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdef{{{{", "{{{{", 4, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("abcdef____", "____", 5, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("==fghijk==", "==", 5, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("___ghij___", "___", 6, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 7, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 8, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 9, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("///ijklmno", "///", 10, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("//hijklmno", "//", 10, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("//hijklmno", "//", 11, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("...ijklmno", "...", 12, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 13, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("/ghijklmno", "/", 14, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("999ijklmno", "999", 15, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("_ghijklmno", "_", 16, 10);
        assertAbbreviateWithAbbrevMarkerAndOffset("+ghijklmno", "+", Integer.MAX_VALUE, 10);
    }

    private void assertAbbreviateWithAbbrevMarkerAndOffset(final String expected, final String abbrevMarker, final int offset, final int maxWidth) {
        final String abcdefghijklmno = "abcdefghijklmno";
        final String message = "abbreviate(String,String,int,int) failed";
        final String actual = AonStringUtils.abbreviate(abcdefghijklmno, abbrevMarker, offset, maxWidth);
        if (offset >= 0 && offset < abcdefghijklmno.length()) {
        	int i = actual.indexOf((char) ('a' + offset));
            assertNotEquals(-1, i, message + " -- should contain offset character");
        }
        assertTrue(actual.length() <= maxWidth,
                message + " -- should not be greater than maxWidth");
        assertEquals(expected, actual, message);
    }
    
    @Test
    void testRepeat_CharInt() {
        assertEquals("zzz", AonStringUtils.repeat('z', 3));
        assertEquals("", AonStringUtils.repeat('z', 0));
        assertEquals("", AonStringUtils.repeat('z', -2));
    }
    
    @Test
    void testRepeat_StringInt() {
        assertNull(AonStringUtils.repeat(null, 2));
        assertEquals("", AonStringUtils.repeat("ab", 0));
        assertEquals("", AonStringUtils.repeat("", 3));
        assertEquals("aaa", AonStringUtils.repeat("a", 3));
        assertEquals("", AonStringUtils.repeat("a", -2));
        assertEquals("ababab", AonStringUtils.repeat("ab", 3));
        assertEquals("abcabcabc", AonStringUtils.repeat("abc", 3));
        final String str = AonStringUtils.repeat("a", 10000);  // bigger than pad limit
        assertEquals(10000, str.length());
        assertTrue(AonStringUtils.containsOnly(str, 'a'));
    }
    
    @Test
    void testLeftPad_StringIntChar() {
        assertNull(AonStringUtils.leftPad(null, 5, ' '));
        assertEquals("     ", AonStringUtils.leftPad("", 5, ' '));
        assertEquals("  abc", AonStringUtils.leftPad("abc", 5, ' '));
        assertEquals("xxabc", AonStringUtils.leftPad("abc", 5, 'x'));
        assertEquals("\uffff\uffffabc", AonStringUtils.leftPad("abc", 5, '\uffff'));
        assertEquals("abc", AonStringUtils.leftPad("abc", 2, ' '));
        final String str = AonStringUtils.leftPad("aaa", 10000, 'a');  // bigger than pad length
        assertEquals(10000, str.length());
        assertTrue(AonStringUtils.containsOnly(str, 'a'));
    }

    @Test
    void testLeftPad_StringIntString() {
        assertNull(AonStringUtils.leftPad(null, 5, "-+"));
        assertNull(AonStringUtils.leftPad(null, 5, null));
        assertEquals("     ", AonStringUtils.leftPad("", 5, " "));
        assertEquals("-+-+abc", AonStringUtils.leftPad("abc", 7, "-+"));
        assertEquals("-+~abc", AonStringUtils.leftPad("abc", 6, "-+~"));
        assertEquals("-+abc", AonStringUtils.leftPad("abc", 5, "-+~"));
        assertEquals("abc", AonStringUtils.leftPad("abc", 2, " "));
        assertEquals("abc", AonStringUtils.leftPad("abc", -1, " "));
        assertEquals("  abc", AonStringUtils.leftPad("abc", 5, null));
        assertEquals("  abc", AonStringUtils.leftPad("abc", 5, ""));
    }
    
    @Test
    public void testSubstring_StringIntInt() {
        assertNull(AonStringUtils.substring(null, 0, 0));
        assertNull(AonStringUtils.substring(null, 1, 2));
        assertEquals("", AonStringUtils.substring("", 0, 0));
        assertEquals("", AonStringUtils.substring("", 1, 2));
        assertEquals("", AonStringUtils.substring("", -2, -1));

        assertEquals("", AonStringUtils.substring(SENTENCE, 8, 6));
        assertEquals(FOO, AonStringUtils.substring(SENTENCE, 0, 3));
        assertEquals("o", AonStringUtils.substring(SENTENCE, -9, 3));
        assertEquals(FOO, AonStringUtils.substring(SENTENCE, 0, -8));
        assertEquals("o", AonStringUtils.substring(SENTENCE, -9, -8));
        assertEquals(SENTENCE, AonStringUtils.substring(SENTENCE, 0, 80));
        assertEquals("", AonStringUtils.substring(SENTENCE, 2, 2));
        assertEquals("b", AonStringUtils.substring("abc", -2, -1));
    }
}
