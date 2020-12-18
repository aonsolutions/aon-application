package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.CollectionUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Agreements.Listener;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;


interface EditionListener {
	
	void onAgreementCopy(Agreement agreement);
	
	void onAgreementPaste(Agreement agreement);
	
	void onAgreementDelete(Agreement agreement);
}

public class MainAgreement extends MainEntryPoint implements Listener,
		EditionListener, Agreements.Toolbar {
	

	
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
			MainAgreement.this.agreements.addNewItemTree(null);
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
	
	class MoveAgreementCommand implements ScheduledCommand {

		@Override
		public void execute() {
			
			if(Window.confirm("\u00BFDesea subir el convenio seleccionado al dominio padre\u003F")) {
				moveAgreement2Parent(MainAgreement.this.agreement);
			}
		}
		
		private void moveAgreement2Parent(Agreement agreement) {
			
			MainAgreement.this.agreements.getAgreementsTree().getEnterpriseService()
				.moveAgreement2Parent(agreement, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Ooopsss ...." + caught.getMessage()) ;
				}

				@Override
				public void onSuccess(Void result) {
					Window.alert("Convenio movido correctamente");
					MainAgreement.this.agreements.reloadAgreements();
				}
			});
		}
		
	}
	
	class AgreementContextMenu extends ContextMenu {
		
		private Agreement agreementCopy = null;		
		private MenuItem newItem = null;
		private MenuItem copyItem = null;
		private MenuItem moveItem = null;
		private MenuItem deleteItem = null;
		
		public AgreementContextMenu() {
			
			newItem = addItem("Nuevo", new NewAgreementCommand(), 
					AON.AON_ICON_RESET, AON.AON_ICON_CMD_BUTTON);
			newItem.ensureDebugId("newItem");
			addSeparator();
			
			copyItem = addItem("Copiar", new CopyAgreementCommand(), 
					AON.AON_ICON_COPY, AON.AON_ICON_CMD_BUTTON);
			copyItem.ensureDebugId("copyItem");
			
			deleteItem = addItem("Eliminar", new DeleteAgreementCommand(), 
					AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);			
			deleteItem.ensureDebugId("deleteItem");
			
			moveItem = addItem("Mover a..", new MoveAgreementCommand(), 
					AON.AON_ICON_MOVE_UP, AON.AON_ICON_CMD_BUTTON);
			moveItem.ensureDebugId("moveItem");
			moveItem.setTitle("Mover convenio al dominio padre");
			moveItem.setVisible(false);

		}
		
		public void setAgreementCopy(Agreement agreement) {
			this.agreementCopy = agreement;
		}
		
		public Agreement getAgreementCopy() {
			return this.agreementCopy;
		}
		
		public void setVisibleCopyItem(Integer id) {
			this.copyItem.setEnabled(true);
			this.copyItem.setTitle("");

			if ( id < 0) {
				this.copyItem.setEnabled(false);
				this.copyItem.setTitle("No es posible copiar "
						+ "un convenio no guardado.");
			}
		}
		
		public void setVisibleMoveItem(boolean visible) {
			this.moveItem.setVisible(visible);
		}

		public void setVisibleDeleteItem(boolean visible) {
			this.deleteItem.setVisible(visible);
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
			if ( !isSelected() ) return;
			// TODO Auto-generated method stub
			MainAgreement.this.agreementDraft
					.setAgreementDraftObject(agreementDraftObject);
		}

		@Override
		public void onSuccess(SortedSet<Date> result) {
			if ( !isSelected() ) return;
			// TODO Auto-generated method stub
			if (!CollectionUtils.isEmpty(result)) {
				Date lastChange = result.last();
				agreementDraftObject.setStartDate(lastChange);
//				agreementDraftObject.setStartDate(DateUtils
//						.getFirstDayOfMonth(lastChange));
				agreementDraftObject.setEndDate(DateUtils
						.getLastDayOfMonth(lastChange));
			}

			MainAgreement.this.agreementDraft
					.setAgreementDraftObject(agreementDraftObject);

		}
		
		private boolean isSelected() {
			TreeItem treeItem = agreements.getAgreementsTree().getSelectedItem();
			int selectedId = ((Agreement)treeItem.getUserObject()).getId();
			int callbackId =  agreementDraftObject.getAgreementDraft().getId();
			return selectedId == callbackId;
		}
	}
	
	private static final String AGREEMENT = "c-agreement";
	
	/*
	 * @UiField MetaData metaData;
	 */
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	
	@UiField
	Agreements agreements;

	@UiField
	AgreementDraft agreementDraft;
	
	private Integer parentDomain;	
	private Storage storage;
	private Map<Integer, AgreementDraftObject> agreementDrafts;	
	private Agreement agreement;
	private AgreementContextMenu contextMenu;
	
	private List<EditionListener> editionsListener;
	
	private AgreementServiceAsync agreementServiceAsync;
	
	@Override
	public void onModuleLoad() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);
		
		AgreementServiceAsync agreementServiceRaw = GWT
				.create(AgreementService.class);
		agreementServiceAsync = new AgreementServiceAsyncDecorator(
				agreementServiceRaw);

		// LocalStorage getItems
		this.storage = Storage.getLocalStorageIfSupported();

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);
		
		this.editionsListener = new LinkedList<EditionListener>();
		this.agreement = null;
		this.parentDomain = null;
		this.contextMenu = new AgreementContextMenu();
		this.agreements.addToolbar(this);
		this.agreements.addListener(this);
		this.agreementDrafts = new HashMap<Integer, AgreementDraftObject>();
		
		addEditionOptions(this);
		
		if(storage.getItem(AGREEMENT) != null) {
			Integer id = Integer.parseInt(storage.getItem(AGREEMENT).toString());
			Agreement agreement = new Agreement();
			agreement.setId(id);
			contextMenu.setAgreementCopy(agreement);
			contextMenu.setVisible(true);
		}	
		
		agreements.getAgreementsTree().getEnterpriseService().getParentDomain(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Integer parentDomain) {
				MainAgreement.this.parentDomain = parentDomain;				
			}
		});
	}

	// ---------------------------------------------------- Agreements.Listener

	@Override
	public void onAgreementSelected(Agreement agreement) {

		this.agreement = agreement;		
		this.contextMenu.setVisibleCopyItem(agreement.getId());		
		
		this.contextMenu.setVisibleMoveItem( (parentDomain != null) && 
				parentDomain.intValue() != agreement.getDomain().intValue());

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
			
			agreementDraftObject = new AgreementDraftObject(
					agreements.getDomain()
					, Wnd.getCurrentDomainNameURL()
					, draft 
					, agreementServiceAsync);
			agreementDrafts.put(agreement.getId(), agreementDraftObject);
			
			TreeItem treeItem = agreements.getAgreementsTree().getSelectedItem();

			agreementDraftObject.addListener(new DraftObjectListener(treeItem,
					agreementDraftObject));
			
			agreements.getAgreementsTree().getEmployeesService()
					.getChanges( 
							Wnd.getCurrentDomainNameURL(),
							agreement, 
							new AgreementChangesCallback(agreementDraftObject));

		} // end-if: Not exists, create it then...
		else {
			agreementDraft.setAgreementDraftObject(agreementDraftObject);
		}
		
		agreements.toolbar.setVisibleDraftButton(isEditable(agreementDraftObject));
		
	}
	
	public void addEditionOptions(EditionListener listener) {
		editionsListener.add(listener);
	}

	public void removeEditionListener(EditionListener listener) {
		editionsListener.remove(listener);
	}

	@Override
	public void onNewAgreement(Agreement agreement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMoveToTrash(Agreement agreement) {
		for(EditionListener listener : editionsListener)
			listener.onAgreementDelete(agreement);
	}

	@Override
	public void onCopyAgreement(Agreement agreement) {
		for(EditionListener listener : editionsListener)
			listener.onAgreementPaste(agreement);
	}

	@Override
	public void onPasteAgreement(Agreement agreement) {
		for(EditionListener listener : editionsListener)
			listener.onAgreementCopy(agreement);
	}

	@Override
	public void onAgreementContextMenu(Agreement agreement,
			ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), 
				nativeEvent.getClientY());
		contextMenu.setVisibleDeleteItem(isEditable(agreementDraft.agreementDraftObject));
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
		
		contextMenu.setAgreementCopy(agreement);	
		
		storage.setItem(AGREEMENT, new String(agreement.getId().toString()));
	}

	@Override
	public void onAgreementPaste(Agreement agreement) {
		
		agreement = contextMenu.getAgreementCopy();
		
		if(agreement != null) {
			agreements.getAgreementsTree().getEnterpriseService().copyAgreement(agreement, 
					new AsyncCallback<Agreement>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Oopss. Estamos corrigiendolo.");
				}

				@Override
				public void onSuccess(Agreement result) {
					agreements.reloadAgreements();			
				}
			});
		}
	}

	@Override
	public void onAgreementDelete(Agreement agreement) {
		agreements.getAgreementsTree().getEnterpriseService().updateAgreementId(
				agreement, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("No ha sido posible enviar el Convenio a la papelera.");
			}

			@Override
			public void onSuccess(Void result) {
				MainAgreement.this.agreements.reloadAgreements();
			}
		});
	}	
	

	private static boolean isEditable(AgreementDraftObject agreementDraftObject) {
		return agreementDraftObject.isMine() || !agreementDraftObject.isSystem();		
	}
	
	@Override
	public void onCollapseMenuButtonClick() {
		splitLayoutPanel.setWidgetSize(agreements, 20);
		splitLayoutPanel.animate(500);
	}

	@Override
	public void onShowMenuButtonClick() {
		splitLayoutPanel.setWidgetSize(agreements, 350);
		splitLayoutPanel.animate(500);
	}

	
}
