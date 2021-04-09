package com.esferalia.aon.in.payroll.pdf.api.toolkit;

import java.util.Optional;

public class NumberToolkit {

	/**
	 * Random number between 0 and max
	 * 
	 * @param max - the number
	 * @return [double] The random number
	 */
	public static double random(double max) {
		Double random = 1 + Math.random() * (max - 1);
		return random;
	}

	/**
	 * Parse an Integer without exceptions
	 * @param parseable - The String to parse
	 * @return [Optional - Integer] parsed number or empty
	 */
	public static Optional<Integer> safeParseInt(String parseable) {
		Optional<Integer> parse;

		try
		{
			parse = Optional.of(Integer.parseInt(parseable));
		} catch (NumberFormatException e)
		{
			parse = Optional.empty();
		}

		return parse;
	}
	
	/**
	 * Parse a Double without exceptions
	 * @param parseable - The String to parse
	 * @return [Optional - Double] parsed number or empty
	 */
	public static Optional<Double> safeParseDouble(String parseable) {
		Optional<Double> parse;

		try
		{
			parse = Optional.of(Double.parseDouble(parseable));
		} catch (NumberFormatException e)
		{
			parse = Optional.empty();
		}

		return parse;
	}
	
	

}
