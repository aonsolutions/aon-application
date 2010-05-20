package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.FaqDetail;

public class FaqHandler {

	private String question;
	
	private String answer;
	
	public FaqHandler (FaqDetail fd) {
		question = fd.getQuestion();
		answer = fd.getAnswer();
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}

}
