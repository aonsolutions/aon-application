package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;

public abstract class AccountEntryDetailExpressionScript<T> implements Serializable {
	
	private static final long serialVersionUID = -1280419601669765944L;
	
	protected static final boolean DEBIT = true; 
	protected static final boolean CREDIT= false;
	public static final String MODEL_FULL_NAME_EXPRESSION = "nombreModelo()";
	
	public static class AccountEntryDetailExpression implements Serializable {
		
		private static final long serialVersionUID = -2876540340281580397L;
		
		private String account;
		private String expression;
		private String conceptExpression;
		private String balancingAccount;
		private boolean creditNature;
		
		public AccountEntryDetailExpression(boolean creditNature) {
			this.creditNature = creditNature;
		}
		
		public boolean isCreditNature() {
			return creditNature;
		}

		public String getAccount() {
			return account;
		}
		public AccountEntryDetailExpression setAccount(String account) {
			this.account = account;
			return this;
		}
		
		public String getExpression() {
			return expression;
		}
		public AccountEntryDetailExpression setExpression(String expression) {
			this.expression = expression;
			return this;
		}
		
		public String getConceptExpression() {
			return conceptExpression;
		}
		public AccountEntryDetailExpression setConceptExpression(String conceptExpression) {
			this.conceptExpression = conceptExpression;
			return this;
		}
		
		public String getBalancingAccount() {
			return balancingAccount;
		}
		public AccountEntryDetailExpression setBalancingAccount(String balancingAccount) {
			this.balancingAccount = balancingAccount;
			return this;
		}
		
	}

	public abstract LinkedList<AccountEntryDetailExpression> getDetails();
	public abstract boolean accept(T accepter);
	public abstract IFiscalModelKey getAccruedKey(); 
	public abstract IFiscalModelKey getDeductibleKey(); 

}

