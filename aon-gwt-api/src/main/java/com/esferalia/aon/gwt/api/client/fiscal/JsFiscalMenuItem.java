package com.esferalia.aon.gwt.api.client.fiscal;

import com.google.gwt.core.client.JavaScriptObject;

public class JsFiscalMenuItem extends JavaScriptObject {
	
	protected JsFiscalMenuItem() {
	}
	
	public final native Integer getId() /*-{return this.id;	}-*/;
	public final native int getDomain() /*-{return this.domain;	}-*/;
	public final native String getDomainName() /*-{return this.domainName;	}-*/;
	public final native String getModel() /*-{return this.model;	}-*/;
	public final native int getYear() /*-{return this.year;	}-*/;
	public final native String getPeriod() /*-{return this.period;	}-*/;
	public final native String getAdministration() /*-{return this.administration;	}-*/;
	public final native String getStatus() /*-{return this.status;	}-*/;
	public final native String getDeclarationTypeKey() /*-{return this.declarationTypeKey;	}-*/;
	public final native String getDeclarationType() /*-{return this.declarationType;	}-*/;
	public final native boolean isComplementary() /*-{return this.complementary;	}-*/;
	public final native boolean isReplacement() /*-{return this.replacement;	}-*/;
	public final native String getDocument() /*-{return this.document;	}-*/;
	public final native String getName() /*-{return this.name;	}-*/;
	public final native String getSurname() /*-{return this.surname;	}-*/;
	public final native double getResult() /*-{return this.result;	}-*/;
	public final native Integer getFinanceId() /*-{return this.financeId;	}-*/;
	public final native String  getBankAccount() /*-{return this.bankAccount;	}-*/;
	public final native String getBankAlias() /*-{return this.bankAlias;	}-*/;
	public final native String getBic() /*-{return this.bic;	}-*/;
	public final native String getDeclarationResultType() /*-{return this.type;	}-*/;
	public final native String getIban() /*-{return this.iban;	}-*/;
	public final native String getNrc() /*-{return this.nrc;	}-*/;
	
}
