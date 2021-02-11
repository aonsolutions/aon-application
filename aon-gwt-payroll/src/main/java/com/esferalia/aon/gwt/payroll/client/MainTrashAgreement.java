package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DetailPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTrashAgreementsToolbar;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.TrashAgreements.Listener;
import com.esferalia.aon.gwt.payroll.client.TrashAgreements.Toolbar;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

interface TrashEditionListener {
	
	void onAgreementDelete4Ever(Agreement agreement);
	
	void onAgreementRestore(Agreement agreement);
	
}

public abstract class MainTrashAgreement extends Composite implements Listener,
	TrashEditionListener, TrashAgreements.Toolbar, AonTrashAgreementsToolbar.Listener {
	
	static interface Binder extends UiBinder<Widget, MainTrashAgreement> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	class Delete4EverAgreementCommand implements ScheduledCommand {

		@Override
		public void execute() {
			for(TrashEditionListener listener : editionsListener)
				listener.onAgreementDelete4Ever(agreement);
		}
	}
	
	class RestoreAgreementCommand implements ScheduledCommand {

		@Override
		public void execute() {
			for(TrashEditionListener listener : editionsListener)
				listener.onAgreementRestore(agreement);
		}
	}
	
	class AgreementContextMenu extends ContextMenu {
		
		private MenuItem delete4EverItem = null;
		private MenuItem restoreItem = null;
		
		public AgreementContextMenu() {
			
			delete4EverItem = addItem("Eliminar Definitivamente", new Delete4EverAgreementCommand(), 
					AON.CSS.aonIconDeleteForever(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			delete4EverItem.ensureDebugId("delete4EverItem");
			
			restoreItem = addItem(AON.MSG.restoreAction(), new RestoreAgreementCommand(), 
					AON.CSS.aonIconRestore(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			restoreItem.ensureDebugId("restoreItem");
		}
		
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String borderR();
		String cmd_btn();
	}
	
	@UiField
	DockLayoutPanel splitLayoutPanel;
	
	@UiField
	AonTrashAgreementsToolbar toolbar;
	
	@UiField
	TrashAgreements agreements;

	@UiField
	DetailPanel detailPanel;
	
	private Map<Integer, AgreementDraftObject> agreementDrafts;	
	private List<TrashEditionListener> editionsListener;
	
	private AgreementContextMenu contextMenu;
	
	private Integer domain;	
	private Agreement agreement;
	private AgreementDraft agreementDraft;
	
	private List<Listener> listeners;
	private List<Toolbar> toolbars;
	
	public MainTrashAgreement() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		initWidget(binder.createAndBindUi(this));
		
		agreements.addStyleName(style.borderR());
		
		this.agreementDrafts = new HashMap<Integer, AgreementDraftObject>();
		this.editionsListener = new LinkedList<TrashEditionListener>();
		
		this.contextMenu = new AgreementContextMenu();
		
		this.domain = null;
		this.agreement = null;
		this.agreementDraft = new AgreementDraft();
		
		this.agreements.addToolbar(this);
		this.agreements.addListener(this);
		
		this.toolbars = new LinkedList<Toolbar>();
		this.listeners = new LinkedList<Listener>();
		
		addToolbar(this);
		toolbar.addListener(this);
		
		addEditionOptions(this);	
		
		agreements.agreementsTree.getEnterpriseService().getDomain(
				new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						MainTrashAgreement.this.domain = result;
					}
				});
		
	}
	
	// ---------------------------------------------------- TrashAgreements.Toolbar

	@Override
	public void onDelete4EverButtonClick(ClickEvent event) {
		Object object = getAgreementsTree().getTree().getSelectedItem().getUserObject();
		if(object instanceof Agreement) {
			for(Toolbar toolbar : toolbars)
				toolbar.onAgreementDelete4Ever((Agreement) object);
		}
	}

	@Override
	public void onRestoreButtonClick(ClickEvent event) {
		Object object = getAgreementsTree().getTree().getSelectedItem().getUserObject();
		if(object instanceof Agreement) {
			for(Toolbar toolbar : toolbars)
				toolbar.onAgreementRestore((Agreement) object);
		}
	}
	
	// ---------------------------------------------------- TrashAgreements.Listener
	
	@Override
	public void onAgreementSelected(Agreement agreement) {
		
		this.agreement = agreement;
		
		detailPanel.setWidget(agreementDraft);
		
		AgreementDraftObject agreementDraftObject = agreementDrafts
				.get(agreement.getId());
		if (agreementDraftObject == null) {
			com.esferalia.aon.gwt.payroll.shared.AgreementDraft draft = 
					new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();

			draft.setId(agreement.getId());
			draft.setDomain(agreement.getDomain());
			draft.setDescription(agreement.getDescription());
			draft.setSSNumber(agreement.getSSNumber());

			draft.setStartDate(DateUtils.getFirstDayOfMonth());
			draft.setEndDate(DateUtils.getLastDayOfMonth());

			agreementDraftObject = new AgreementDraftObject(
					getDomain(),
					Wnd.getCurrentDomainNameURL(),
					draft,
					agreements.agreementsTree.getEmployeesService()) {
				@Override
				public boolean isMine() {
					return false;
				}
			};
			agreementDrafts.put(agreement.getId(), agreementDraftObject);			
	
		} 
		
		agreementDraft.setAgreementDraftObject(agreementDraftObject);

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
	public void onAgreementSupr(Agreement agreement) {
		for(TrashEditionListener listener : editionsListener)
			listener.onAgreementDelete4Ever(agreement);
	}
	
	@Override
	public void onCollapseMenuButtonClick() {
		splitLayoutPanel.setWidgetSize(agreements, 0);
		splitLayoutPanel.animate(500);
	}

	@Override
	public void onShowMenuButtonClick() {
		splitLayoutPanel.setWidgetSize(agreements, 350);
		splitLayoutPanel.animate(500);
	}
	
	@Override
	public void onCollapseTrashMenuButtonClick(ClickEvent event) {
		splitLayoutPanel.setWidgetSize(agreements, 0);
		splitLayoutPanel.animate(500);
	}

	@Override
	public void onShowTrashMenuButtonClick(ClickEvent event) {
		splitLayoutPanel.setWidgetSize(agreements, 350);
		splitLayoutPanel.animate(500);
	}

	@Override
	public void onKeyUpSearchTrashTextBox(KeyUpEvent event) {
		agreements.filter(toolbar.getSearchTextBox().getValue());
	}

	@Override
	public void onBackButtonClick(ClickEvent event) {
		onBackButtonClick();
	}

	@Override
	public void onAgreementDelete4Ever(Agreement agreement) {
		AonConfirmDialog confirmDialog = new AonConfirmDialog();
		confirmDialog.confirm(
				"BORRADO", 
				String.valueOf("\u00BF") + "Desea eliminar definitivamente el convenio  " + agreement.getDescription() + "?. Le recordamos que este convenio tiene contratos asociados, si lo elimina definitivamente estos contratos se desvincular"+ String.valueOf("\u00E1") +"n de este convenio.",
				new AonConfirmDialogCallback() {

					@Override
					public void onAccept() {
						agreements.agreementsTree.getEnterpriseService().deleteAgreement(agreement, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								MainTrashAgreement.this.agreements.reloadAgreements();
							}
							
							@Override
							public void onFailure(Throwable caught) {}
							
						});
					}

					@Override
					public void onCancel() {}
				}
		);
		
	}

	@Override
	public void onAgreementRestore(Agreement agreement) {
		AonConfirmDialog confirmDialog = new AonConfirmDialog();
		confirmDialog.confirm(
				"RESTAURAR", 
				String.valueOf("\u00BF") + "Desea restaurar el convenio " + agreement.getDescription() + "?",
				new AonConfirmDialogCallback() {

					@Override
					public void onAccept() {
						agreements.agreementsTree.getEnterpriseService().updateAgreementId(agreement, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								MainTrashAgreement.this.agreements.reloadAgreements();
							}
							
							@Override
							public void onFailure(Throwable caught) {}
							
						});
					}

					@Override
					public void onCancel() {}
					
				}
		);
		
	}
	
	// --------------------------------------------------------- Listener & Toolbar
	
	public void addEditionOptions(TrashEditionListener listener) {
		editionsListener.add(listener);
	}

	public void removeEditionListener(TrashEditionListener listener) {
		editionsListener.remove(listener);
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void addToolbar(Toolbar toolbar) {
		toolbars.add(toolbar);
	}
	
	public void removeToolbar(Toolbar toolbar) {
		toolbars.remove(toolbar);
	}

	// --------------------------------------------------------- Auxiliar Methods

	public Integer getDomain() {
		return this.domain;
	}
	
	private TrashAgreementsTree getAgreementsTree() {
		return this.agreements.agreementsTree;
	}

	public void selectFirstItem() {
		agreements.getTrashAgreements();
		getAgreementsTree().getTree().setSelectedItem(
				getAgreementsTree().getTree().getItem(0),
				true);
	}
	
	// --------------------------------------------------------- Abstract Methods
	
	public abstract void onBackButtonClick();
	
}
