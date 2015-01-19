package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.OptionsToolbar;
import com.esferalia.aon.gwt.common.shared.CollectionUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Agreements.Listener;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;


interface EditionListener {
	
	void onAgreementCopy(Agreement agreement);
	
	void onAgreementPaste(Agreement agreement);
	
	void onAgreementDelete(Agreement agreement);
}

public class MainAgreement extends MainEntryPoint implements Listener,
		OptionsToolbar.Listener, EditionListener {
	

	
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

	static interface Binder extends UiBinder<Widget, MainAgreement> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	class NewAgreementCommand implements ScheduledCommand {

		@Override
		public void execute() {
		}
	}
	
	class CopyAgreementCommand implements ScheduledCommand {

		@Override
		public void execute() {
			for(EditionListener listener : editionsListener)
				listener.onAgreementCopy(agreement);
		}
	}
	
	class PasteAgreementCommand implements ScheduledCommand {

		@Override
		public void execute() {
		}
	}
	
	class DeleteAgreementCommand implements ScheduledCommand  {

		@Override
		public void execute() {
			for(EditionListener listener : editionsListener)
				listener.onAgreementDelete(agreement);
		}
	}
	
	class AgreementContextMenu extends ContextMenu {
		
		MenuItem newItem = null;
		MenuItem copyItem = null;
		MenuItem deleteItem = null;
		MenuItem pasteItem = null;
		
		public AgreementContextMenu() {
			
			newItem = addItem("Nuevo", new NewAgreementCommand(), 
					AON.AON_ICON_RESET, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			copyItem = addItem("Copiar", new CopyAgreementCommand(), 
					AON.AON_ICON_COPY, AON.AON_ICON_CMD_BUTTON);
			pasteItem = addItem("Pegar", new PasteAgreementCommand(), 
					AON.AON_ICON_PASTE, AON.AON_ICON_CMD_BUTTON);
			pasteItem.setVisible(false);
			deleteItem = addItem("Eliminar", new DeleteAgreementCommand(), 
					AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON); 
		}
	}

	

	class AgreementChangesCallback implements AsyncCallback<SortedSet<Date>> {

		AgreementDraftObject agreementDraftObject;

		public AgreementChangesCallback(
				AgreementDraftObject agreementDraftObject) {
			this.agreementDraftObject = agreementDraftObject;
		}

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
			MainAgreement.this.agreementDraft
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

			MainAgreement.this.agreementDraft
					.setAgreementDraftObject(agreementDraftObject);

		}
	}
	
	/*
	 * @UiField MetaData metaData;
	 */
	@UiField
	Agreements agreements;

	@UiField
	AgreementDraft agreementDraft;

	@UiField
	OptionsToolbar toolbar;

	private Map<Integer, AgreementDraftObject> agreementDrafts;
	
	private Agreement agreement;
	private AgreementContextMenu contextMenu;
	
	private List<EditionListener> editionsListener;
	
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
		
		this.editionsListener = new LinkedList<EditionListener>();
		this.agreement = null;
		this.contextMenu = new AgreementContextMenu();
		this.toolbar.addListener(this);
		this.agreements.addListener(this);
		this.agreementDrafts = new HashMap<Integer, AgreementDraftObject>();
		
		addEditionOptions(this);

	}

	// ---------------------------------------------------- Agreements.Listener

	@Override
	public void onAgreementSelected(Agreement agreement) {

		this.agreement = agreement;

		AgreementDraftObject agreementDraftObject = agreementDrafts
				.get(agreement.getId());
		if (agreementDraftObject == null) {
			com.esferalia.aon.gwt.payroll.shared.AgreementDraft draft = new 
					com.esferalia.aon.gwt.payroll.shared.AgreementDraft();

			draft.setId(agreement.getId());
			draft.setDomain(agreement.getDomain());
			draft.setDescription(agreement.getDescription());

			draft.setStartDate(DateUtils.getFirstDayOfMonth());
			draft.setEndDate(DateUtils.getLastDayOfMonth());
			
			agreementDraftObject = new AgreementDraftObject(agreements
					.getDomain(), draft, agreements.getAgreementsTree().getEmployeesService());
			agreementDrafts.put(agreement.getId(), agreementDraftObject);
			
			TreeItem treeItem = agreements.getAgreementsTree().getSelectedItem();

			agreementDraftObject.addListener(new DraftObjectListener(treeItem,
					agreementDraftObject));
			
			agreements.getAgreementsTree().getEmployeesService()
					.getChanges(agreement, new AgreementChangesCallback(
							agreementDraftObject));

		} // end-if: Not exists, create it then...
		else {
			agreementDraft.setAgreementDraftObject(agreementDraftObject);
		}
	}
	
	public void addEditionOptions(EditionListener listener) {
		editionsListener.add(listener);
	}

	public void removeEditionListener(EditionListener listener) {
		editionsListener.remove(listener);
	}

	@Override
	public void onNewButtonClick(ClickEvent event) {
		//Window.alert("onNew(..");
	}

	@Override
	public void onPasteButtonClick(ClickEvent event) {
		for(EditionListener listener : editionsListener)
			listener.onAgreementCopy(agreement);
	}

	@Override
	public void onCopyButtonClick(ClickEvent event) {
		for(EditionListener listener : editionsListener)
			listener.onAgreementPaste(agreement);
	}

	@Override
	public void onDraftButtonClick(ClickEvent event) {
		for(EditionListener listener : editionsListener)
			listener.onAgreementDelete(agreement);
	}

	@Override
	public void onViewButtonClick(ClickEvent event) {
		
	}

	@Override
	public void onAgreementContextMenu(Agreement agreement,
			ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), 
				nativeEvent.getClientY());
		contextMenu.show();
	}

	@Override
	public void onAgreementCtrlC(Agreement agreement) {
		for(EditionListener listener : editionsListener)
			listener.onAgreementCopy(agreement);
	}

	@Override
	public void onAgreementCtrlV(Agreement agreement) {
		for(EditionListener listener : editionsListener) 
			listener.onAgreementPaste(agreement);
	}

	@Override
	public void onAgreementSupr(Agreement agreement) {
		for(EditionListener listener : editionsListener)
			listener.onAgreementDelete(agreement);
	}

	@Override
	public void onAgreementCopy(Agreement agreement) {
		//Window.alert("onCopy(..");
		
	}

	@Override
	public void onAgreementPaste(Agreement agreement) {
		//Window.alert("onPaste(..");
	}

	@Override
	public void onAgreementDelete(Agreement agreement) {
		agreements.getAgreementsTree().getEnterpriseService().updateAgreementId(
				agreement, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				MainAgreement.this.agreements.reloadAgreements();
			}
		});
	}	
	//--------------------------------------------- private methods

	

}
