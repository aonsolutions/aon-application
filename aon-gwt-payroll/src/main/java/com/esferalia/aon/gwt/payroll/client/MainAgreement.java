package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.client.Agreements.Listener;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class MainAgreement extends MainEntryPoint implements Listener {

	interface GWTResources extends ClientBundle {
		@Source("agreement.png")
		ImageResource agreement();
	}

	static interface Binder extends UiBinder<Widget, MainAgreement> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Agreements agreements;

	@UiField
	AgreementDraft agreementDraft;

	private EmployeesServiceAsync employeesServiceAsync;

	private Map<Integer, AgreementDraftObject> agreementDrafts;

	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<MainEntryPoint.GWTResources> create(
				MainEntryPoint.GWTResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.AonResources> create(
				MainEntryPoint.AonResources.class).css().ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		EmployeesServiceAsync employeesServiceRaw = GWT
				.create(EmployeesService.class);
		employeesServiceAsync = new EmployeesServiceAsyncDecorator(
				employeesServiceRaw);

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		agreements.addListener(this);
		agreementDrafts = new HashMap<Integer, AgreementDraftObject>();

	}

	// ---------------------------------------------------- Agreements.Listener

	@Override
	public void onAgreementSelected(Agreement agreement) {
		AgreementDraftObject agreementDraftObject = agreementDrafts
				.get(agreement.getId());
		if (agreementDraftObject == null) {
			com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();
			agreementDraft.setId(agreement.getId());
			agreementDraft.setDescription(agreement.getDescription());
			agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
			agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());
			agreementDraftObject = new AgreementDraftObject(agreementDraft,
					employeesServiceAsync);
			agreementDrafts.put(agreement.getId(), agreementDraftObject);
		} // end-if: Not exists, create it then...

		agreementDraft.setAgreementDraftObject(agreementDraftObject);
	}
}
