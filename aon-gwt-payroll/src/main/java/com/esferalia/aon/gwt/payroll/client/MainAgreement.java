package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Agreements.Listener;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.CollectionUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class MainAgreement extends MainEntryPoint implements Listener {

	static class DraftObjectListener implements UndoManager.Listener {

		private TreeItem treeItem;
		private AgreementDraftObject draftObject;

		public DraftObjectListener(TreeItem treeItem,
				AgreementDraftObject draftObject) {
			this.treeItem = treeItem;
			this.draftObject = draftObject;
		}

		@Override
		public void onChange(UndoManager undoManager) {
			ImageResource resource = Agreements.getImageResource(
					draftObject.canUndo(), draftObject.hasErrors(),
					draftObject.hasWarnings());
			treeItem.setHTML(Agreements.imageItemSafeHtml(resource,
					draftObject.getDescription()));
		}

	}



	static interface Binder extends UiBinder<Widget, MainAgreement> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	class AgreementChangesCallback implements AsyncCallback<SortedSet<Date>> {
		
		AgreementDraftObject agreementDraftObject;
		
		public AgreementChangesCallback(AgreementDraftObject agreementDraftObject) {
			this.agreementDraftObject = agreementDraftObject;
		}
		
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
			MainAgreement.this.agreementDraft.setAgreementDraftObject(agreementDraftObject);
		}

		@Override
		public void onSuccess(SortedSet<Date> result) {
			// TODO Auto-generated method stub
			if ( !CollectionUtils.isEmpty(result)){
				Date lastChange = result.last();
				agreementDraftObject.setStartDate(DateUtils.getFirstDayOfMonth(lastChange));
				agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(lastChange));
			}
				
			MainAgreement.this.agreementDraft.setAgreementDraftObject(agreementDraftObject);

		}
	}

	/*
	 * @UiField MetaData metaData;
	 */
	@UiField
	Agreements agreements;

	@UiField
	AgreementDraft agreementDraft;

	private EmployeesServiceAsync employeesServiceAsync;

	private Map<Integer, AgreementDraftObject> agreementDrafts;

	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(
				GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(
				AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources> create(
				MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();

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
			com.esferalia.aon.gwt.payroll.shared.AgreementDraft draft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();

			draft.setId(agreement.getId());
			draft.setDescription(agreement.getDescription());

			draft.setStartDate(DateUtils.getFirstDayOfMonth());
			draft.setEndDate(DateUtils.getLastDayOfMonth());

			agreementDraftObject = new AgreementDraftObject(draft,
					employeesServiceAsync);
			agreementDrafts.put(agreement.getId(), agreementDraftObject);

			TreeItem treeItem = agreements.getSelectedItem();

			agreementDraftObject.addListener(new DraftObjectListener(treeItem,
					agreementDraftObject));

			employeesServiceAsync.getChanges(agreement,
					new AgreementChangesCallback(agreementDraftObject));

		} // end-if: Not exists, create it then...
		else {
			agreementDraft.setAgreementDraftObject(agreementDraftObject);
		}
	}

}
