package com.esferalia.aon.watson.util;

import java.math.BigDecimal;

public class AonNumberUtils {

	public static final Integer INTEGER_ZERO = Integer.valueOf(0);
	public static final Integer INTEGER_ONE = Integer.valueOf(1);
	
	public static boolean isValid(Double number) {
		return number != null && !number.isNaN() && !number.isInfinite();
	}

	public static boolean isNotValid(Double number) {
		return !isValid(number);
	}

	public static boolean equals(Number n1, Number n2) {
		if (n1 == n2)
			return true;
		if (n1 == null)
			return false;
		if (n2 == null)
			return false;
		return Double.compare(n1.doubleValue(), n2.doubleValue()) == 0;
	}
	
	public static boolean notEquals(Number n1, Number n2) {
		return !equals(n1, n2);
	}

	public static <T extends Number> int compare(T n1, T n2) {
		if (n1 == n2)
			return 0;
		if (n1 == null)
			return -1;
		if (n2 == null)
			return 1;
		return Double.compare(n1.doubleValue(), n2.doubleValue());
	}

	public static <T extends Number> int compare(T n1, T n2, int decimals) {
		if (n1 == n2)
			return 0;
		if (n1 == null)
			return -1;
		if (n2 == null)
			return 1;
		return Double.compare(Math.round(n1.doubleValue() * Math.pow(10,decimals)), Math.round(n2.doubleValue() * Math.pow(10,decimals)));
	}

	public static Byte toByte(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Byte.parseByte(value);
		}
		return null;
	}

	public static Byte toByte(Short i) {
		if (i != null) {
			return i.byteValue();
		}
		return null;
	}

	public static byte toByte(Integer i) {
		if (i != null) {
			return i.byteValue();
		}
		return 0;
	}

	public static Byte toNullableByte(Integer i) {
		if (i != null) {
			return i.byteValue();
		}
		return null;
	}

	public static Integer toInteger(Number number) {
		if (number == null) return null; 
		return Integer.valueOf( number.intValue() );
	}

	public static Integer toInteger(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Integer.parseInt(value);
		}
		return null;
	}

	public static Short toShort(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Short.parseShort(value);
		}
		return null;
	}
	public static Short toShort(Number number) {
		if (number == null) return null; 
		return Short.valueOf( number.shortValue());
	}
	public static short toshort(Number number) {
		if (number == null) return 0; 
		return number.shortValue();
	}

	public static Double toDouble(Number number) {
		if (number == null) return null; 
		return Double.valueOf( number.doubleValue());
	}

	public static Double toDouble(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Double.parseDouble(value);
		}
		return null;
	}

	public static double todouble(String value) {
		if (!AonStringUtils.isBlank(value)) {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				// Nothing. returns 0.
			}
		}
		return 0;		
	}

	public static int toint(Number number) {
		if (number == null) return 0; 
		return number.intValue();
	}
	
	public static int toint(String value) {
		if (!AonStringUtils.isBlank(value)) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				// Nothing. returns 0.
			}
		}
		return 0;		
	}

	public static BigDecimal toBigDecimal(String value) {
		if (AonStringUtils.isNotBlank(value)) {
			return new BigDecimal(value);
		}
		return null;
	}

	public static String emptyIfNull(Integer value) {
		if (value == null) return "";
		return value.toString();
	}
	public static String toString(Integer value) {
		if (value == null) return null;
		return value.toString();
	}
	public static String toString(Double value) {
		if (value == null) return null;
		return value.toString();
	}
	public static String toString(Number value) {
		if (value == null) return null;
		return value.toString();
	}

	public static boolean between(Number value, Number limit1, Number limit2) {
		if (Double.compare(value.doubleValue(), limit1.doubleValue()) < 0) return false;
		if (Double.compare(value.doubleValue(), limit2.doubleValue()) > 0) return false;
		return true;
	}

	public static Double nullIfZero(Double value) {
		return (value != null && AonMathUtils.isZero(value)?null:value);
	}

	public static double zeroIfNull(Double value) {
		return (value==null?0:value);
	}

	public static double zeroIfNull(BigDecimal value) {
		return (value==null?0:value.doubleValue());
	}

	public static int zeroIfNull(Integer value) {
		return (value==null?0:value);
	}
	
	public static double todouble(Number number) {
		if (number == null) return 0;
		return number.doubleValue();
	}

	public static double todouble(Object obj) {
		if (obj instanceof Number) {
			return todouble((Number) obj);
		}
		return 0;
	}

    /**
     * <p>Checks whether the String a valid Java number.</p>
     *
     * <p>Valid numbers include hexadecimal marked with the <code>0x</code> or
     * <code>0X</code> qualifier, octal numbers, scientific notation and numbers 
     * marked with a type qualifier (e.g. 123L).</p>
     * 
     * <p>Non-hexadecimal strings beginning with a leading zero are
     * treated as octal values. Thus the string <code>09</code> will return
     * <code>false</code>, since <code>9</code> is not a valid octal value.
     * However, numbers beginning with {@code 0.} are treated as decimal.</p>
     *
     * <p><code>null</code> and empty/blank {@code String} will return
     * <code>false</code>.</p>
     *
     * @param str  the <code>String</code> to check
     * @return <code>true</code> if the string is a correctly formatted number
     * @since 3.3 the code supports hex {@code 0Xhhh} and octal {@code 0ddd} validation
     */
    public static boolean isNumber(final String str) {
        if (AonStringUtils.isEmpty(str)) {
            return false;
        }
        final char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        final int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1 && chars[start] == '0') { // leading 0
            if (
                 (chars[start + 1] == 'x') || 
                 (chars[start + 1] == 'X') 
            ) { // leading 0x/0X
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                        && (chars[i] < 'a' || chars[i] > 'f')
                        && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
           } else if (Character.isDigit(chars[start + 1])) {
               // leading 0, but not hex, must be octal
               int i = start + 1;
               for (; i < chars.length; i++) {
                   if (chars[i] < '0' || chars[i] > '7') {
                       return false;
                   }
               }
               return true;               
           }
        }
        sz--; // don't want to loop to the last char, check it afterwords
              // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent   
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns
                && (chars[i] == 'd'
                    || chars[i] == 'D'
                    || chars[i] == 'f'
                    || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                || chars[i] == 'L') {
                // not allowing L with an exponent or decimal point
                return foundDigit && !hasExp && !hasDecPoint;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }

    /**
     * <p>Checks whether the <code>String</code> contains only
     * digit characters.</p>
     *
     * <p><code>Null</code> and empty String will return
     * <code>false</code>.</p>
     *
     * @param str  the <code>String</code> to check
     * @return <code>true</code> if str contains only unicode numeric
     */
    public static boolean isDigits(String str) {
        if (AonStringUtils.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * <p>Checks whether the <code>String</code> can be parsed as an <code>int</code>.</p>
     *
     * <p><code>null</code> and empty/blank <code>String</code> will return
     * <code>false</code>, as will values out of the <code>int</code> range
     * or with a decimal part.</p>
     *
     * @param value  the <code>String</code> to check
     * @return <code>true</code> if the string is a valid <code>int</code>
     */
    public static boolean isInteger(String value) {
        if (AonStringUtils.isBlank(value)) {
            return false;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * <p>Checks whether the number has no decimal part.</p>
     *
     * <p><code>null</code>, <code>NaN</code> and infinite values will return
     * <code>false</code>.</p>
     *
     * @param number  the <code>Number</code> to check
     * @return <code>true</code> if the number is a whole value
     */
    public static boolean isInteger(Number number) {
        if (number == null) {
            return false;
        }
        double value = number.doubleValue();
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return false;
        }
        return Double.compare(value, Math.floor(value)) == 0;
    }

    public static boolean isPar(Integer n) {
		return n % 2 == 0;
	}

    public static boolean isImpar(Integer n) {
		return !isPar(n);
	}

	public static Integer toInteger(Boolean b) {
		if (b == null) {
			return null;
		}
		return b.booleanValue()?1:0;
	}

}
