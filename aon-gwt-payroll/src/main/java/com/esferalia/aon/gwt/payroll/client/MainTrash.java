package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.shared.CollectionUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.NumberUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

interface Listener {

	void onDeleteAction(Agreement agreement);

	void onRestoreAction(Agreement agreement);

}

public class MainTrash extends MainEntryPoint implements
		AgreementsTree.Listener, Listener {

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
			ImageResource resource = AgreementsTree.getImageResource(
					draftObject.canUndo(), draftObject.hasErrors(),
					draftObject.hasWarnings());
			treeItem.setHTML(AgreementsTree.imageItemSafeHtml(resource,
					draftObject.getDescription()));
		}

	}

	static interface Binder extends UiBinder<Widget, MainTrash> {
	}

	private static final Images IMAGES = GWT.create(Images.class);

	private static final Binder binder = GWT.create(Binder.class);

	class AgreementChangesCallback implements AsyncCallback<SortedSet<Date>> {

		AgreementDraftObject agreementDraftObject;

		public AgreementChangesCallback(
				AgreementDraftObject agreementDraftObject) {
			this.agreementDraftObject = agreementDraftObject;
		}

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
			MainTrash.this.agreementDraft
					.setAgreementDraftObject(agreementDraftObject);
		}

		@Override
		public void onSuccess(SortedSet<Date> result) {
			// TODO Auto-generated method stub
			if (!CollectionUtils.isEmpty(result)) {
				Date lastChange = result.last();
				agreementDraftObject.setStartDate(DateUtils
						.getFirstDayOfMonth(lastChange));
				agreementDraftObject.setEndDate(DateUtils
						.getLastDayOfMonth(lastChange));
			}

			MainTrash.this.agreementDraft
					.setAgreementDraftObject(agreementDraftObject);

		}
	}

	@UiField
	Button deleteAgreementButton;
	@UiField
	Button restoreAgreementButton;
	@UiField
	Button clearAgreementButton;

	@UiField
	AgreementsTree agreementsTree;

	@UiField
	AgreementDraft agreementDraft;

	private Integer domain;
	private Agreement agreement;
	private List<Listener> listeners;
	private Map<Integer, AgreementDraftObject> agreementDrafts;

	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources> create(
				MainEntryPoint.CodeMirrorResources.class).css()
				.ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		this.listeners = new ArrayList<Listener>();
		this.agreementsTree.addListener(this);
		this.agreement = null;
		this.agreementDrafts = new HashMap<Integer, AgreementDraftObject>();

		addListener(this);

		agreementsTree.getEnterpriseService().getDomain(
				new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						MainTrash.this.domain = result;
						getAgreements();
					}
				});
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public Integer getDomain() {
		return this.domain;
	}

	@UiHandler("deleteAgreementButton")
	void onDeleteClickButton(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onDeleteAction(agreement);
	}

	@UiHandler("restoreAgreementButton")
	void onRestoreClickButton(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onRestoreAction(agreement);
	}

	@UiHandler("clearAgreementButton")
	void onClearClickButton(ClickEvent event) {

	}

	@Override
	public void onDeleteAction(Agreement agreement) {
		Window.alert("Voy a borrar a " + agreement.getDescription()
				+ " con id " + agreement.getId());

	}

	private boolean getTreeCount() {
		return agreementsTree.getTree().getItemCount() > 0;
	}

	@Override
	public void onRestoreAction(Agreement agreement) {

		agreementsTree.getEnterpriseService().updateAgreementId(agreement,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

					}

					@Override
					public void onSuccess(Void result) {
						getAgreements();
					}
				});
	}

	// ---------------------------------------------------- Agreements.Listener

	@Override
	public boolean evaluateId(Agreement agreement) {
		return agreement.getId() < 0;
	}

	@Override
	public void getAgreements() {
		agreementsTree.clearTree();
		agreementsTree.getEnterpriseService().getAgreements(0, 100,
				new AsyncCallback<List<Agreement>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(List<Agreement> agreements) {

						int item2Select = -1;
						for (int i = 0; i < agreements.size(); i++) {

							Agreement agreement = agreements.get(i);
							if (evaluateId(agreement))
								addAgreementItem(agreement);

							if (agreement.isRedefined()) {
								if (item2Select == -1)
									item2Select = i;
							}
							if (agreement.hasEmployees()) {
								if (item2Select == -1)
									item2Select = i;
							}

						}
						// Select the first one.
						if (agreementsTree.getTree().getItemCount() > 0)
							agreementsTree.getTree().setSelectedItem(
									agreementsTree.getTree().getItem(
											Math.max(item2Select, 0)), true);
						

						MainTrash.this.enabledButtons(agreementsTree.getTree()
								.getItemCount());

					}
				});
	}

	private void enabledButtons(Integer count) {
		clearAgreementButton.setEnabled(count > 0);
		deleteAgreementButton.setEnabled(count > 0);
		restoreAgreementButton.setEnabled(count > 0);
	}

	private TreeItem addAgreementItem(Agreement agreement) {
		String description = agreement.getDescription();
		if (agreement.isRedefined()) {
			description = "*" + description;
		}

		List<ImageResource> marks = new ArrayList<ImageResource>();
		if (NumberUtils.notEquals(domain, agreement.getDomain()))
			marks.add(IMAGES.parent());

		TreeItem treeItem = new TreeItem(AgreementsTree.imageItemSafeHtml(
				description, AgreementsTree.getImageResource(agreement),
				marks.toArray(new ImageResource[marks.size()])));

		treeItem.setUserObject(agreement);

		agreementsTree.getTree().addItem(treeItem);

		return treeItem;
	}

	@Override
	public void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		Object object = selectedItem.getUserObject();

		if (object instanceof Agreement)
			agreementSelected((Agreement) object);

	}

	private void agreementSelected(Agreement agreement) {
		this.agreement = agreement;

		AgreementDraftObject agreementDraftObject = agreementDrafts
				.get(agreement.getId());
		if (agreementDraftObject == null) {
			com.esferalia.aon.gwt.payroll.shared.AgreementDraft draft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();

			draft.setId(agreement.getId());
			draft.setDomain(agreement.getDomain());
			draft.setDescription(agreement.getDescription());

			draft.setStartDate(DateUtils.getFirstDayOfMonth());
			draft.setEndDate(DateUtils.getLastDayOfMonth());

			agreementDraftObject = new AgreementDraftObject(getDomain(), draft,
					agreementsTree.getEmployeesService());
			agreementDrafts.put(agreement.getId(), agreementDraftObject);

			TreeItem treeItem = agreementsTree.getSelectedItem();

			agreementDraftObject.addListener(new DraftObjectListener(treeItem,
					agreementDraftObject));

			agreementsTree.getEmployeesService().getChanges(agreement,
					new AgreementChangesCallback(agreementDraftObject));

		} // end-if: Not exists, create it then...
		else {
			agreementDraft.setAgreementDraftObject(agreementDraftObject);
		}

	}

	@Override
	public void onAgreementCtrlC(Agreement agreement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAgreementCtrlV(Agreement agreement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAgreementSupr(Agreement agreement) {

	}

	@Override
	public void onAgreementContextMenu(Agreement agreement,
			ContextMenuEvent event) {
		// TODO Auto-generated method stub

	}

}
