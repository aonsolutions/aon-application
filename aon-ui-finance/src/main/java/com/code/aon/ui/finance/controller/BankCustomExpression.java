/**
 * 
 */
package com.code.aon.ui.finance.controller;

import java.util.Map;

import ar.com.fdvs.dj.domain.CustomExpression;

public class BankCustomExpression implements CustomExpression {
	private static final long serialVersionUID = 7368651157413691588L;
	private Integer bankId;

	public BankCustomExpression(Integer bankId) {
		this.bankId = bankId;
	}

	@Override
	public String getClassName() {
		return Double.class.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object evaluate(Map fields, Map variables, Map parameters) {
		CashFlowReport to = (CashFlowReport) fields.get("to");
		if (to.getMap().containsKey(bankId)) {
			return to.getMap().get(bankId).getBalance();
		}
		return null;
	}
}