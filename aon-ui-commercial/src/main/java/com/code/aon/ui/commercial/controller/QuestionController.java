package com.code.aon.ui.commercial.controller;

import com.code.aon.commercial.Question;
import com.code.aon.commercial.enumeration.QuestionType;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class QuestionController extends BasicController {
	
	public Question getQuestion() {
		return (Question) getTo();
	}
	
	public boolean isBoolean() {
		return getQuestion().getType() == QuestionType.BOOLEAN;
	}

	public boolean isDate() {
		return getQuestion().getType() == QuestionType.DATE;
	}

	public boolean isText() {
		return getQuestion().getType() == QuestionType.TEXT;
	}

	public boolean isNumber() {
		return getQuestion().getType() == QuestionType.NUMBER;
	}
	
}