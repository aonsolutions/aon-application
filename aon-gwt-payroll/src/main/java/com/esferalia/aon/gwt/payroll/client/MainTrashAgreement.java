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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

interface TrashEditionListener {
	
	void onAgreementDelete4Ever(Agreement agreement);
	
	void onAgreementRestore(Agreement agreement);
	
}

public abstract class MainTrashAgreement extends Composite implements Listener,
	TrashEditionListener, TrashAgreements.Toolbar, AonTrashAgreementsToolbar.Listener {
	
	static interface Binder extends UiBinder<Widget, MainTrashAgreement> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String borderR();
		String cmdBtn();
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
	
	private Integer domain;
	private AgreementDraft agreementDraft;
	
	private List<Listener> listeners;
	private List<Toolbar> toolbars;
	
	protected MainTrashAgreement() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		initWidget(binder.createAndBindUi(this));
		
		agreements.addStyleName(style.borderR());
		
		this.agreementDrafts = new HashMap<>();
		this.editionsListener = new LinkedList<>();
		
		this.domain = null;
		this.agreementDraft = new AgreementDraft();
		
		this.agreements.addToolbar(this);
		this.agreements.addListener(this);
		
		this.toolbars = new LinkedList<>();
		this.listeners = new LinkedList<>();
		
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
	
	// ---------------------------------------------------- TrashAgreements.Listener
	
	@Override
	public void onAgreementSelected(Agreement agreement) {
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
					Wnd.getCurrentUser(),
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
	public void onBackButtonClick(ClickEvent event) {
		onBackButtonClick();
	}

	@Override
	public void onAgreementDelete4Ever(Agreement agreement) {
		AonConfirmDialog confirmDialog = new AonConfirmDialog();
		confirmDialog.confirm(
				"BORRADO", 
				String.valueOf("\u00BF") + "Desea eliminar definitivamente el convenio  " + agreement.getDescription() + "?. Le recordamos que este convenio tiene contratos asociados, si lo elimina definitivamente estos contratos se desvincular\u00E1n de este convenio.",
				new AonConfirmDialogCallback() {

					@Override
					public void onAccept() {
						agreements.agreementsTree.getEnterpriseService().deleteAgreement(agreement, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								MainTrashAgreement.this.agreements.reloadAgreements();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								// Not use here
							}
							
						});
					}

					@Override
					public void onCancel() {
						// Not use here
					}
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
							public void onFailure(Throwable caught) {
								// Not use here
							}
							
						});
					}

					@Override
					public void onCancel() {
						// Not use here
					}
					
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

	public TrashAgreements getAgreements() {
		return this.agreements;
	}

	public void selectFirstItem() {
		agreements.getTrashAgreements(s -> getAgreementsTree().getTree().setSelectedItem(getAgreementsTree().getTree().getItem(0), true));
	}
	
}
