/**
 * 
 */
package com.esferalia.aon.in.payroll.pdf.api.toolkit;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toDecimal;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.StringToolkit.trimToEmpty;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;

public class Number2Text {
	private int	  flag;
	public int	  numero;
	public String parcialAmount;
	public String num;
	public String numLetter;
	public String numLetters;
	public String numLetterm;
	public String numLetterdm;
	public String numLettercm;
	public String numLettermm;
	public String numLetterdmm;

	/**
	 * Convert Integer to text
	 * 
	 * @param number - number to convert
	 * @return [String] Converted number.
	 */
	public static String convertInteger(Integer number) {
		if (number == null)
			return null;

		return instance().convertInt(number);
	}

	/**
	 * Convert Double to text
	 * 
	 * @param number - number to convert
	 * @return [String] Converted number.
	 */
	public static String convertDouble(Double number) {
		String	 result	  = "";
		String	 toString = PdfFormats.toDecimal(number).replace(",", ".");
		String[] parts	  = toString.split("\\.");

		int	units	 = Integer.parseInt(parts[0]);
		int	decimals = Integer.parseInt(parts[1]);

		result += convertInteger(units);
		String decStr = convertInteger(decimals);

		if (!decStr.equalsIgnoreCase("cero"))
		{
			result += " con ";
			result += decStr;
		}
		return result;
	}

	public static void main(String[] args) {
		String res = convertDouble(1293123.99);
		System.out.println(res);
	}

	/**
	 * Instance a number2Text
	 * 
	 * @return [Number2Text] The instance.
	 */
	public static Number2Text instance() {
		return new Number2Text();
	}

	public Number2Text() {
		numero = 0;
		flag   = 0;
	}

	public Number2Text(int n) {
		numero = n;
		flag   = 0;
	}

	private String unit(int numero) {

		switch (numero)
		{
		case 9:
			num = "nueve";
			break;
		case 8:
			num = "ocho";
			break;
		case 7:
			num = "siete";
			break;
		case 6:
			num = "seis";
			break;
		case 5:
			num = "cinco";
			break;
		case 4:
			num = "cuatro";
			break;
		case 3:
			num = "tres";
			break;
		case 2:
			num = "dos";
			break;
		case 1:
			if (flag == 0)
				num = "uno";
			else
				num = "un";
			break;
		case 0:
			num = "";
			break;
		}
		return num;
	}

	private String decena(int numero) {

		if (numero >= 90 && numero <= 99)
		{
			numLetter = "noventa ";
			if (numero > 90)
				numLetter = numLetter.concat("y ").concat(unit(numero - 90));
		} else if (numero >= 80 && numero <= 89)
		{
			numLetter = "ochenta ";
			if (numero > 80)
				numLetter = numLetter.concat("y ").concat(unit(numero - 80));
		} else if (numero >= 70 && numero <= 79)
		{
			numLetter = "setenta ";
			if (numero > 70)
				numLetter = numLetter.concat("y ").concat(unit(numero - 70));
		} else if (numero >= 60 && numero <= 69)
		{
			numLetter = "sesenta ";
			if (numero > 60)
				numLetter = numLetter.concat("y ").concat(unit(numero - 60));
		} else if (numero >= 50 && numero <= 59)
		{
			numLetter = "cincuenta ";
			if (numero > 50)
				numLetter = numLetter.concat("y ").concat(unit(numero - 50));
		} else if (numero >= 40 && numero <= 49)
		{
			numLetter = "cuarenta ";
			if (numero > 40)
				numLetter = numLetter.concat("y ").concat(unit(numero - 40));
		} else if (numero >= 30 && numero <= 39)
		{
			numLetter = "treinta ";
			if (numero > 30)
				numLetter = numLetter.concat("y ").concat(unit(numero - 30));
		} else if (numero >= 20 && numero <= 29)
		{
			if (numero == 20)
				numLetter = "veinte ";
			else
				numLetter = "veinti".concat(unit(numero - 20));
		} else if (numero >= 10 && numero <= 19)
		{
			switch (numero)
			{
			case 10:

				numLetter = "diez ";
				break;

			case 11:

				numLetter = "once ";
				break;

			case 12:

				numLetter = "doce ";
				break;

			case 13:

				numLetter = "trece ";
				break;

			case 14:

				numLetter = "catorce ";
				break;

			case 15:

				numLetter = "quince ";
				break;

			case 16:

				numLetter = "dieciseis ";
				break;

			case 17:

				numLetter = "diecisiete ";
				break;

			case 18:

				numLetter = "dieciocho ";
				break;

			case 19:

				numLetter = "diecinueve ";
				break;

			}
		} else
			numLetter = unit(numero);

		return numLetter;
	}

	private String tens(int numero) {
		if (numero >= 100)
		{
			if (numero >= 900 && numero <= 999)
			{
				numLetter = "novecientos ";
				if (numero > 900)
					numLetter = numLetter.concat(decena(numero - 900));
			} else if (numero >= 800 && numero <= 899)
			{
				numLetter = "ochocientos ";
				if (numero > 800)
					numLetter = numLetter.concat(decena(numero - 800));
			} else if (numero >= 700 && numero <= 799)
			{
				numLetter = "setecientos ";
				if (numero > 700)
					numLetter = numLetter.concat(decena(numero - 700));
			} else if (numero >= 600 && numero <= 699)
			{
				numLetter = "seiscientos ";
				if (numero > 600)
					numLetter = numLetter.concat(decena(numero - 600));
			} else if (numero >= 500 && numero <= 599)
			{
				numLetter = "quinientos ";
				if (numero > 500)
					numLetter = numLetter.concat(decena(numero - 500));
			} else if (numero >= 400 && numero <= 499)
			{
				numLetter = "cuatrocientos ";
				if (numero > 400)
					numLetter = numLetter.concat(decena(numero - 400));
			} else if (numero >= 300 && numero <= 399)
			{
				numLetter = "trescientos ";
				if (numero > 300)
					numLetter = numLetter.concat(decena(numero - 300));
			} else if (numero >= 200 && numero <= 299)
			{
				numLetter = "doscientos ";
				if (numero > 200)
					numLetter = numLetter.concat(decena(numero - 200));
			} else if (numero >= 100 && numero <= 199)
			{
				if (numero == 100)
					numLetter = "cien ";
				else
					numLetter = "ciento ".concat(decena(numero - 100));
			}
		} else
			numLetter = decena(numero);

		return numLetter;
	}

	private String miles(int numero) {
		if (numero >= 1000 && numero < 2000)
		{
			numLetterm = ("mil ").concat(tens(numero % 1000));
		}
		if (numero >= 2000 && numero < 10000)
		{
			flag	   = 1;
			numLetterm = unit(numero / 1000).concat(" mil ").concat(tens(numero % 1000));
		}
		if (numero < 1000)
			numLetterm = tens(numero);

		return numLetterm;
	}

	private String tenThousand(int numero) {
		if (numero == 10000)
			numLetterdm = "diez mil";
		if (numero > 10000 && numero < 20000)
		{
			flag		= 1;
			numLetterdm	= decena(numero / 1000).concat("mil ").concat(tens(numero % 1000));
		}
		if (numero >= 20000 && numero < 100000)
		{
			flag		= 1;
			numLetterdm	= decena(numero / 1000).concat(" mil ").concat(miles(numero % 1000));
		}

		if (numero < 10000)
			numLetterdm = miles(numero);

		return numLetterdm;
	}

	private String hundredThousand(int numero) {
		if (numero == 100000)
			numLettercm = "cien mil";
		if (numero >= 100000 && numero < 1000000)
		{
			flag		= 1;
			numLettercm	= tens(numero / 1000).concat(" mil ").concat(tens(numero % 1000));
		}
		if (numero < 100000)
			numLettercm = tenThousand(numero);
		return numLettercm;
	}

	private String million(int numero) {
		if (numero >= 1000000 && numero < 2000000)
		{
			flag		= 1;
			numLettermm	= ("Un millon ").concat(hundredThousand(numero % 1000000));
		}
		if (numero >= 2000000 && numero < 10000000)
		{
			flag		= 1;
			numLettermm	= unit(numero / 1000000).concat(" millones ").concat(hundredThousand(numero % 1000000));
		}
		if (numero < 1000000)
			numLettermm = hundredThousand(numero);

		return numLettermm;
	}

	private String tenMillion(int numero) {
		if (numero == 10000000)
			numLetterdmm = "diez millones";
		if (numero > 10000000 && numero < 20000000)
		{
			flag		 = 1;
			numLetterdmm = decena(numero / 1000000).concat("millones ").concat(hundredThousand(numero % 1000000));
		}
		if (numero >= 20000000 && numero < 100000000)
		{
			flag		 = 1;
			numLetterdmm = decena(numero / 1000000).concat(" milllones ").concat(million(numero % 1000000));
		}

		if (numero < 10000000)
			numLetterdmm = million(numero);

		return numLetterdmm;
	}

	private String convertInt(int numero) {
		if (numero == 0)
		{
			numLetters = "cero";
		} else
		{
			numLetters = tenMillion(numero);
		}
		return trimToEmpty(numLetters);
	}

}