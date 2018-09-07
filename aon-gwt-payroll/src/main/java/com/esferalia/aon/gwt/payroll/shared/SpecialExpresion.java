package com.esferalia.aon.gwt.payroll.shared;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

public class SpecialExpresion {

	private static final int START_GROUP = 1;
	private static final int LABEL_GROUP = 2;
	private static final int INPUT_GROUP = 3;

	private static final String PATTERN = "(/\\*(user|read-only)\\*/)((?:[^/]|(?:/[^\\*]))*)(/\\*\\*/)";

	private static class BlankExpression extends SpecialExpresion {

		public String getInput() {
			return "";
		};

		public String replace(String replacement) {
			return replacement;
		};
	}

	private static class Expression extends BlankExpression {

		private String expression;

		public Expression(String expression) {
			this.expression = expression;
		}

		public String getInput() {
			return expression;
		};

	}


	public static SpecialExpresion parse(String expression) {

		if (StringUtils.isBlank(expression))
			return new BlankExpression();

		if (StringUtils.equals(expression.trim(), "REMOVE()"))
			return new BlankExpression();

		RegExp regExp = RegExp.compile(PATTERN, "g");

		MatchResult result = regExp.exec(expression);
		if (result == null)
			return new Expression(expression);

		SpecialExpresion expresion = new SpecialExpresion();

		expresion.expression = expression;

		expresion.input = result.getGroup(INPUT_GROUP);


		expresion.inputBeginIndex = result.getIndex();
		String begin = result.getGroup(START_GROUP);
		if (begin != null)
			expresion.inputBeginIndex += begin.length();

		expresion.inputEndIndex = expresion.inputBeginIndex;
		if (expresion.input != null)
			expresion.inputEndIndex += expresion.input.length();

		expresion.readOnly = result.getGroup(LABEL_GROUP).equals("read-only");

		return expresion;

	}

	public static boolean isZero(String expression) {
		return RegExp.compile("\\*\\s*0\\.00/\\*\\*/").test(expression);
	}

	public static boolean isReadOnly(String expression) {
		return parse(expression).isReadOnly();
	}

	public static boolean isSpecial(String expression) {
		if (StringUtils.isBlank(expression))
			return false;

		RegExp regExp = RegExp.compile(PATTERN, "g");

		MatchResult result = regExp.exec(expression);
		return result != null;
	}

	private String input;
	private int inputBeginIndex;
	private int inputEndIndex;

	private String expression;

	private boolean readOnly;

	public String getInput() {
		return input;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public String replace(String replacement) {
		StringBuffer buffer = new StringBuffer();

		if (inputBeginIndex > 0)
			buffer.append(expression.substring(0, inputBeginIndex));

		if (replacement != null)
			buffer.append(replacement);

		if (inputEndIndex < expression.length())
			buffer.append(expression.substring(inputEndIndex));

		return buffer.toString();
	}

}
