package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.widget.DetailPanel;

public class MainSystem extends MainEntryPoint implements MetaData.Listener{


	static interface Binder extends UiBinder<Widget, MainSystem> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	MetaData metaData;
	
	@UiField
	DetailPanel detailPanel;
	
	private BonusEditor bonusEditor;
	private PaymentEditor paymentEditor;
	private DeductionEditor deductionEditor;
	

	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(
				GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(
				AonResources.class).css().ensureInjected();

		// Create the UI defined in MainSystem.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);
		
		bonusEditor = new BonusEditor();
		paymentEditor = new PaymentEditor();
		deductionEditor = new DeductionEditor();
		metaData.addListener(this);

	}

	// ---------------------------------------------- MetaData.Listener methods
	
	@Override
	public void onBonusConceptSelected(Bonus bonus) {
		bonusEditor.setBonus(bonus);
		detailPanel.setWidget(bonusEditor);
	}
	
	@Override
	public void onPaymentConceptSelected(Payment payment) {
		paymentEditor.setPayment(payment);
		detailPanel.setWidget(paymentEditor);
	}

	@Override
	public void onDeductionConceptSelected(Deduction deduction) {
		deductionEditor.setDeduction(deduction);
		detailPanel.setWidget(deductionEditor);
	}

}
