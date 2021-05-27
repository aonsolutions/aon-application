package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.client.AON;

public class ValidationMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum ValidationMessages {
		// PAGE00
		 EMPTY_YEAR		(new ValidationMessage(0, AON.MSG.requiredField(AON.MSG.fiscalYear())))
		,EMPTY_DOCUMENT	(new ValidationMessage(0, AON.MSG.requiredField(AON.MSG.document())))
		,WRONG_DOCUMENT	(new ValidationMessage(0, "El NIF/DNI no es correcto"))
		,REQ_NAME 		(new ValidationMessage(0, "Para personas f\u00EDsicas, el nombre es obligatorio."))
		,REQ_SURNAME 	(new ValidationMessage(0, "Para personas f\u00EDsicas, el primer apellido es obligatorio."))
		,EMPTY_NAME 	(new ValidationMessage(0, "No se ha indicado el nombre del declarante."))
		// PAGE01
		,EMPTY_ACTI 	(new ValidationMessage(1, "No se ha indicado actividad principal."))
		// PAGE02
		,EMPTY_REPR 	(new ValidationMessage(2, "Indique datos del represante."))
		,EMPTY_REPR_DOC	(new ValidationMessage(2, "Para personas f\u00EDsicas, el NIF/DNI del representante es obligatorio."))
		,WRONG_REPR_DOC	(new ValidationMessage(2, "El NIF/DNI del representante no es correcto."))
		,LG1_WRONG_DOC	(new ValidationMessage(2, "El NIF del primer representante para personas jur\u00EDdicas no es correcto."))
		,LG2_WRONG_DOC	(new ValidationMessage(2, "El NIF del segundo representante para personas jur\u00EDdicas no es correcto."))
		,LG3_WRONG_DOC	(new ValidationMessage(2, "El NIF del tercer representante para personas jur\u00EDdicas no es correcto."))
		;
		
		private ValidationMessage msg;
		private ValidationMessages(ValidationMessage msg) {
			this.msg = msg;
		}
		public ValidationMessage getMsg() {
			return msg;
		}
	}

	int page;
	String message;
	String key;
	String expression;

	public ValidationMessage() {
	}

	public ValidationMessage(int page, String key, String message, String expression) {
		this.message = message;
		this.page = page;
		this.key = key;
		this.expression = expression;
	}

	public ValidationMessage(int page, String message) {
		this(page,null,message,null);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
