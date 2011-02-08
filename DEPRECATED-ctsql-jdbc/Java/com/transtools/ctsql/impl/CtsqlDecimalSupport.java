package com.transtools.ctsql.impl;

import java.math.BigInteger;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
class CtsqlDecimalSupport {

	// exponente
	private short dec_exp = 0;
	// signo 1=positivo, 0=negativo -1 null
	private short dec_pos = -1;
	// numero de digitos significativos
	private short dec_ndgts = 0;
	// digitos
	private byte dec_dgts[] = new byte[DECSIZE];
	// Fin  New Eva-Fer 21-6-2000

	private int scale;
	/**
	 *  Description of the Field
	 */
	public final static int DBLPRECISION = 16;
	/**
	 *  Description of the Field
	 */
	public final static int FLOATPREC = -1;

	private final static int DECSIZE = 16;
	private final static int DECBASE = 100;
	private final static int SIZBUFNUM = 80;


	/**
	 *  Constructor for the CtsqlDecimalSupport object
	 *
	 *@param  aDouble  Description of Parameter
	 */
	public CtsqlDecimalSupport(Double aDouble) {
		if (aDouble != null) {
			doubleToDecimal(aDouble.doubleValue(), DBLPRECISION + 14);
		}else
			dec_pos = -1; // null decimal
	}


	/**
	 *  Constructor for the CtsqlDecimalSupport object
	 *
	 *@param  aDouble  Description of Parameter
	 */
	public CtsqlDecimalSupport(double aDouble) {
		doubleToDecimal(aDouble, DBLPRECISION + 14);
	}


	/**
	 *  Constructor for the CtsqlDecimalSupport object
	 */
	public CtsqlDecimalSupport() {
		this(null);
	}


	/**
	 *  Gets the Null attribute of the CtsqlDecimalSupport object
	 *
	 *@return    The Null value
	 */
	public boolean isNull() {
		return dec_pos == -1;
	}


	/**
	 *  Gets the NumberOfDigits attribute of the CtsqlDecimalSupport object
	 *
	 *@return    The NumberOfDigits value
	 */
	public int getNumberOfDigits() {
		return dec_ndgts;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public double tdectodbl() {
		byte[] buf = new byte[SIZBUFNUM];
		int s = 0;
		int t;
		int lex;
		int ldig;

		if (dec_pos == 0) {
			buf[s++] = (byte) '-';
		}

		lex = dec_exp;
		ldig = dec_ndgts;
		t = 0;

		if (lex <= 0) {
			buf[s++] = (byte) '0';
			buf[s++] = (byte) '.';
		}
		else if (lex < ldig) {
			s = pack_100(t, lex, buf, s);
			buf[s++] = (byte) '.';
			t += lex;
			ldig -= lex;
			lex = 0;
		}
		else {
			lex -= ldig;
		}

		s = pack_100(t, ldig, buf, s);
		if (lex != 0) {
			lex *= 2;
			buf[s++] = (byte) 'E';
			if (lex < 0) {
				lex = (short) (-lex);
				buf[s++] = (byte) '-';
			}
			if (lex >= DECBASE) {
				buf[s++] = (byte) (lex / DECBASE + '0');
				lex = (short) (lex % DECBASE);
			}
			buf[s++] = (byte) ((lex / 10) + '0');
			buf[s++] = (byte) (lex % 10 + '0');
		}
		return (new Double(new String(buf, 0, (int) s))).doubleValue();
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public String toString() {
		byte[] buf = new byte[SIZBUFNUM];
		int t;
		t = pack_100_exp(buf);
		return new String(buf,0,t);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 *@param  len  Description of Parameter
	 */
	public void lddec(byte[] buf, int off, int len) {
		byte[] aux = new byte[18];
		byte cexp;

		if (buf[off] == 0 && buf[off + 1] == 0) {
			dec_ndgts = 0;
			dec_exp = 0;
			dec_pos = -1;
			return;
		}
		System.arraycopy(buf, off + 1, aux, 0, --len);

		cexp = buf[off];

		if ((cexp & 128) == 0) {
			comp_100(aux, 0, len);
			// New Eva-Fer 21-6-2000

			if (decneg20()) {
				cexp = (byte) (127 - buf[off]);
			}
			/*
			 * exp en comp 127
			 */
			// Fin New Eva-Fer 21-6-2000

		}

		// Mod Eva-Fer 21-6-2000
		//		dec_t_load(aux, off, len, (int)((buf[off] & 128) != 0 ? 1 : 0),
		//							(int)((cexp & 127) - 64));

		dec_t_load(aux,
				len,
				(short) ((buf[off] & 128) != 0 ? 1 : 0),
				(short) ((cexp & 127) - 64)
				);
		// Fin Mod Eva-Fer 21-6-2000

	}


	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 *@param  len  Description of Parameter
	 */
	public void stdec(byte[] buf, int off, int len) {
		int s;
		int t;

		s = off;
		buf[s] = (byte) ((dec_exp + 64) & 127);

		if (dec_pos != 0) {
			buf[s] += (byte) 128;
		}
		else if (decneg20()) {
			buf[s] = (byte) (127 - buf[s]);
		}

		System.arraycopy(dec_dgts, 0, buf, ++s, --len);
		if (len > dec_ndgts) {
			s += dec_ndgts;
			while (s < len) {
				buf[s++] = 0;
			}
		}
		else if ((len < dec_ndgts) && (dec_dgts[len] >= 50)) {
			t = s + len;
			while (++buf[--t] >= DECBASE) {
				if (s == t) {
					buf[t] = 1;
					buf[s - 1]++;
					break;
				}
				else {
					buf[t] = 0;
				}
			}
		}

		if (dec_pos == 0) {
			comp_100(buf, s, len);
		}
	}


	/**
	 *  Sets the Scale attribute of the CtsqlDecimalSupport object
	 *
	 *@param  value  The new Scale value
	 */
	private void setScale(int value) {
		scale = value;
	}


	/**
	 *  Gets the Scale attribute of the CtsqlDecimalSupport object
	 *
	 *@return    The Scale value
	 */
	private int getScale() {
		return scale;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s    Description of Parameter
	 *@param  len  Description of Parameter
	 *@param  buf  Description of Parameter
	 *@param  t    Description of Parameter
	 *@return      Description of the Returned Value
	 */
	private int pack_100(int s, int len, byte[] buf, int t) {
		while (--len >= 0) {
			buf[t++] = (byte) (dec_dgts[s] / 10 + (byte) '0');
			buf[t++] = (byte) (dec_dgts[s++] % 10 + (byte) '0');
		}
		return t;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  s    Description of Parameter
	 *@param  len  Description of Parameter
	 *@param  buf  Description of Parameter
	 *@param  t    Description of Parameter
	 *@return      Description of the Returned Value
	 */
	private int pack_100_exp(byte[] buf) {
		int s=0;
		int t=0;
		boolean isNeg = (dec_pos == 0);
		int posComma = dec_exp;

		if (isNeg) {
			buf[t++]=(byte) '-';
		}

		if(posComma < 0){
			buf[t++]=(byte) '0';
			buf[t++]=(byte) '.';
			while(posComma < 0){
				buf[t++]=(byte) '0';
				buf[t++]=(byte) '0';
				posComma++;
			}
			posComma--;
		} else if (posComma == 0) {
			buf[t++] = (byte) '0';
		}

		for (int i = 0; i < dec_ndgts; i++){
			if(posComma == 0){
				buf[t++] = (byte) '.';
			}
			posComma--;
			byte value = (byte) (dec_dgts[s] / 10 + (byte) '0');
			if (t != (isNeg ? 1 : 0) || value != (byte) '0') {
				buf[t++] = value;
			}
			value = (byte) (dec_dgts[s++] % 10 + (byte) '0');
			if (value != (byte) '0' || i != dec_ndgts-1 || posComma >= 0) {
				buf[t++] = value;
			}
		}
		while (posComma > 0) {
			buf[t++]=(byte) '0';
			buf[t++]=(byte) '0';
			posComma--;
		}
		return t;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  buffer  Description of Parameter
	 *@param  off     Description of Parameter
	 *@param  len     Description of Parameter
	 */
	private void comp_100(byte[] buffer, int off, int len) {
		short i = DECBASE;
		int t;

		for (t = (len + off - 1); t >= off; t--) {
			if ((buffer[t] != 0) || (i != DECBASE)) {
				buffer[t] = (byte) (i - buffer[t]);
				i = 99;
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  len  Description of Parameter
	 *@param  pos  Description of Parameter
	 *@param  exp  Description of Parameter
	 */
	private void dec_t_load(byte[] buf, int len, short pos, short exp) {
		int s;
		int b;

		if (pos == -1) {
			dec_pos = -1;
			dec_exp = dec_ndgts = 0;
			return;
		}

		s = len;
		b = 0;

		/*
		 * elimina ceros de la derecha
		 */
		while (len > 0 && buf[--s] == 0) {
			len--;
		}

		/*
		 * elimina ceros de la izquierda (multiplica)
		 */
		while (buf[b] == 0 && len > 0) {
			exp--;
			len--;
			b++;
		}

		if (len > DECSIZE) {
			s = (short) (b + DECSIZE);

			if (buf[s] >= 50) {
				while (++buf[--s] >= DECBASE) {
					buf[s] = 0;
				}
			}

			if (b > s) {
				exp++;
				b--;
			}
			len = DECSIZE;
		}

		if ((dec_ndgts = (short) len) > 0) {
			System.arraycopy(buf, b, dec_dgts, 0, len);
		}
		else {
			exp = 0;
			pos = 1;
		}

		dec_pos = pos;
		dec_exp = exp;
		if (exp < -64) {
			dec_exp = -64;
			return;
		}

		if (exp > 63) {
			dec_exp = 63;
			return;
		}

		return;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  src     Description of Parameter
	 *@param  target  Description of Parameter
	 *@param  i       Description of Parameter
	 *@param  len     Description of Parameter
	 *@return         Description of the Returned Value
	 */
	private int pack_odd(byte[] src, byte[] target, int i, int len) {
		byte j = 0;
		int s = 0;
		int t = 0;

		while ((s < len) || (j != 0)) {
			if ((s < len)) {
				j += src[s++] - '0';
			}
			if ((i & 1) != 0) {
				target[t++] = j;
				j = 0;
			}
			else {
				j *= 10;
			}
			i++;
		}
		return i / 2;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  d    Description of Parameter
	 *@param  len  Description of Parameter
	 */
	private void doubleToDecimal(double d, int len) {
		/*
		 * Esto deberia funcionar pero antes del JDK1.3 daba problemas
		 *
		 * BigDecimal bd = new BigDecimal( String.valueOf( d ); );
		 * str = bd.unscaledValue().toString();
		 * decpt = str.length() - bd.scale();
		 *
		 */

		String str = unscaledValueString(d);
		int decpt = getScale();

		if (str.length() > len) {
			if (str.charAt(len) >= (byte) '5' &&
					str.charAt(len - 1) == (byte) '9') {
				str = new BigInteger(str.substring(0, len)).add(BigInteger.valueOf(1)).toString();
				if (str.length() > len) {
					str = str.substring(0, len);
					decpt++;
				}
			}
			else {
				str = str.substring(0, len);
			}
		}
		else {
			while (str.length() < len) {
				str = str + '0';
			}
		}

		short sign = (short) (d < 0 ? 0 : 1);
		byte[] dest = str.getBytes();

		dec_ndgts = (short) pack_odd(dest, dec_dgts, decpt & 1, dest.length);
		dec_t_load(dec_dgts, dec_ndgts, sign, (short) ((decpt + 1 & ~1) / 2));
	}


	/**
	 *  Description of the Method
	 *
	 *@param  value  Description of Parameter
	 *@return        Description of the Returned Value
	 */
	private String unscaledValueString(double value) {
		String finalString = "";
		int scale = 0;
		int startPosition;
		int endPosition;

		String valueString = new Double(java.lang.Math.abs(value)).toString().toUpperCase();

		if (valueString.startsWith("0")) {
			valueString = valueString.substring(1);
		}

		int point = valueString.indexOf('.');
		if (point > 0) {
			finalString = valueString.substring(0, point);
			scale = point;
		}
		startPosition = point + 1;

		int exponentPosition = valueString.indexOf("E");
		if (exponentPosition > 0) {
			String exponentString = valueString.substring(exponentPosition + 1);
			scale += Integer.valueOf(exponentString).intValue();
			endPosition = exponentPosition;
		}
		else {
			endPosition = valueString.length();
		}

		setScale(scale);
		finalString += valueString.substring(startPosition, endPosition);
		return finalString;
	}


	// New Eva-Fer 21-6-2000
	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	private static boolean decneg20() {
		return true;
	}

	public static void main(String[] args) {
		printResult("0");
		printResult("0.2");
		printResult("4");
		printResult("-2");
		printResult("10");
		printResult("12.34567");
		printResult("123");
		printResult("1234");
		printResult("1234000");
		printResult("0.123");
		printResult("0.000123");
		printResult("12300000");
		printResult("123000000");
		printResult("-0.000000123");
		printResult("-0.0000000123");
		printResult("1.23E-10");
		printResult("-1.23E11");
	}

	private static void printResult(String value) {
		CtsqlDecimalSupport decimal = new CtsqlDecimalSupport(Double.parseDouble(value));
		System.out.println("value = "+value+", toString() ="+decimal.toString());
	}

}
