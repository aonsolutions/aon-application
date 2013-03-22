package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class AgreementDraft extends ResizeComposite {

	interface Binder extends UiBinder<Widget, AgreementDraft> {
		
	}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	
	private AgreementDraftObject agreementDraftObject;
	
	public AgreementDraft() {
		initWidget(binder.createAndBindUi(this));
	}
	
	
	public void setAgreementDraftObject(
			AgreementDraftObject agreementDraftObject) {
		this.agreementDraftObject = agreementDraftObject;
	}
	

}
