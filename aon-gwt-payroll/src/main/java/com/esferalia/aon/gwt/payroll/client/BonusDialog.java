package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class BonusDialog extends CustomDialog {

	interface Callback {
		void onAccept(BonusDialog dialog);
	}

	interface Binder extends UiBinder<Widget, BonusDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	com.esferalia.aon.gwt.payroll.client.Bonus bonus;

	private Callback cb;

	public BonusDialog() {
		setCaption("Deducci\u00f3n");
		setWidget(binder.createAndBindUi(this));
	}

	public void setAvailableDeductions(
			List<com.esferalia.aon.gwt.payroll.shared.Deduction> availableDeductions) {
		bonus.setAvailableDeductions(availableDeductions);
	}

	public String getDeductionExpression() {
		return bonus.getExpression();
	}

	public void setDeductionExpression(String expression) {
		bonus.setExpression(expression);
	}

	public com.esferalia.aon.gwt.payroll.shared.Bonus.Type getType() {
		return bonus.getType();
	}

	public void setType(com.esferalia.aon.gwt.payroll.shared.Bonus.Type type) {
		bonus.setType(type);
	}

	public String getDescription() {
		return bonus.getDescription();
	}

	public void setDescription(String description) {
		bonus.setDescription(description);
	}

	public com.esferalia.aon.gwt.payroll.shared.Item<com.esferalia.aon.gwt.payroll.shared.Bonus.Type> getConcept() {
		return bonus.getConcept();
	}

	public void setConcept(com.esferalia.aon.gwt.payroll.shared.Bonus concept) {
		bonus.setConcept(concept);
	}

	public void setContextProvider(IContextProvider contextProvider) {
		bonus.setContextProvider(contextProvider);
	}

	public void setNumberFormat(NumberFormat numberFormat) {
		bonus.setNumberFormat(numberFormat);
	}

	public void show(Callback cb) {
		this.cb = cb;
		super.show();
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent clickEvent) {
		hide();
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent clickEvent) {
		hide();
		cb.onAccept(this);
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------

}
