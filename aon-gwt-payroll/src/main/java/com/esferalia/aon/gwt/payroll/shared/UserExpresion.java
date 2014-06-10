package com.esferalia.aon.gwt.payroll.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

public class UserExpresion {

	private static final int USER_START_GROUP = 1;
	private static final int USER_LABEL_GROUP = 2;
	private static final int USER_INPUT_GROUP = 3;

	private static final String USER_PATTERN = "(/\\*user(?::label=(\\w*))?\\*/)((?:[^/]|(?:/[^\\*]))*)(/\\*\\*/)";

	public static final UserExpresion BLANK_USER_EXPRESSION = new UserExpresion() {
		public String getInput() {
			return "";
		};

		public String getLabel() {
			return null;
		};

		public String replace(String replacement) {
			return replacement;
		};
	};

	
	public static UserExpresion[] parse(String expression) {

		if (StringUtils.isBlank(expression))
			return new UserExpresion[] { };

		RegExp regExp = RegExp.compile(USER_PATTERN, "g");

		List<UserExpresion> userExpresions = new ArrayList<UserExpresion>();
		for (MatchResult result = regExp.exec(expression); result != null ; result = regExp
				.exec(expression)) {

			UserExpresion userExpresion = new UserExpresion();

			userExpresion.expression = expression;

			userExpresion.label = result.getGroup(USER_LABEL_GROUP);
			userExpresion.input = result.getGroup(USER_INPUT_GROUP);

			userExpresion.inputBeginIndex = result.getIndex();
			String begin = result.getGroup(USER_START_GROUP);
			if (begin != null)
				userExpresion.inputBeginIndex += begin.length();

			userExpresion.inputEndIndex = userExpresion.inputBeginIndex;
			if (userExpresion.input != null)
				userExpresion.inputEndIndex += userExpresion.input.length();

			userExpresions.add(userExpresion);
			
		}

		return userExpresions.toArray(new UserExpresion[userExpresions.size()]);
	}
	
	public static UserExpresion getNoLabelExpression(final String expression)
			throws IllegalArgumentException {
		UserExpresion userExprs[] = UserExpresion.parse(expression);
		List<UserExpresion> noLabelUserExprs = new ArrayList<UserExpresion>();

		for (UserExpresion userExpr : userExprs)
			if (StringUtils.isBlank(userExpr.getLabel()))
				noLabelUserExprs.add(userExpr);

		if (noLabelUserExprs.isEmpty())
			return new UserExpresion() {
				@Override
				public String getInput() {
					return expression;
				}

				@Override
				public String replace(String replacement) {
					return replacement;
				}
			}; // Identity UserExpression
			
		if (noLabelUserExprs.size() > 1)
			throw new IllegalArgumentException();

		return noLabelUserExprs.get(0);
	}
	

	private String label;
	private String input;
	private int inputBeginIndex;
	private int inputEndIndex;

	private String expression;

	public String getLabel() {
		return label;
	}

	public String getInput() {
		return input;
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
