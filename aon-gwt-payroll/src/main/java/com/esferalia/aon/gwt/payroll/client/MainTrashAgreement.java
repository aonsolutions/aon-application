package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTrashAgreementsToolbar;
import com.esferalia.aon.gwt.payroll.client.TrashAgreements.Listener;
import com.esferalia.aon.gwt.payroll.client.TrashAgreements.Toolbar;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
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
		String dialogGlass();
		String dialogZIndex();
	}
	
	@UiField
	DockLayoutPanel splitLayoutPanel;
	
	@UiField
	AonTrashAgreementsToolbar toolbar;
	
	@UiField
	TrashAgreements agreements;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel agreementContainer;
	
	@UiField (provided = true)
	AgreementPreview agreementPreview;
	
	@UiField
	HTMLPanel agreementMessage;
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<TrashEditionListener> editionsListener;
	
	private Integer parentDomain;
	private Integer domain;
	
	private AgreementInfo agreementSelected;
	
	private List<Listener> listeners;
	private List<Toolbar> toolbars;
	
	protected MainTrashAgreement() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		
		agreementPreview = new AgreementPreview() {
			
			@Override
			protected void reloadAgreement() {
				getAgreement(agreementSelected.getId(), agreementInfo -> {
					agreementSelected = agreementInfo;
					agreementPreview.resetSelectedDate();
					agreementPreview.setAgreementPreview(agreementSelected);
				});
			}
			
			@Override
			public void onSaved() {
				// Not save on trash
			}
		};

		initWidget(binder.createAndBindUi(this));
		
		agreements.addStyleName(style.borderR());
		
		this.editionsListener = new LinkedList<>();
		
		this.parentDomain = null;
		this.domain = null;
		
		this.agreements.addToolbar(this);
		this.agreements.addListener(this);
		
		this.toolbars = new LinkedList<>();
		this.listeners = new LinkedList<>();
		
		addToolbar(this);
		toolbar.addListener(this);
		
		addEditionOptions(this);	
		
		showAgreementMessage();
		
		agreements.getAgreementsTree().getEnterpriseService().getParentDomain(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				// Not use here
			}

			@Override
			public void onSuccess(Integer parentDomain) {
				MainTrashAgreement.this.parentDomain = parentDomain;
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
		});
		
		
		
	}
	
	// ---------------------------------------------------- TrashAgreements.Listener
	
	@Override
	public void onAgreementSelected(Agreement agreement) {
		agreementPreview.showLoading("Cargando convenio...");
		agreementPreview.setReadOnly(true);
		
		if(null != agreement.getDomain()) {
			this.agreements.setVisibleDraft4EverButton(0 != agreement.getDomain().intValue() && (parentDomain == null || ((parentDomain != null) && parentDomain.intValue() != agreement.getDomain().intValue())));
			this.agreements.setVisibleRestoreButton(0 != agreement.getDomain().intValue() && (parentDomain == null || ((parentDomain != null) && parentDomain.intValue() != agreement.getDomain().intValue())));
		}
		
		getAgreement(agreement.getId(), agreeementInfo -> {
			agreementSelected = agreeementInfo;
			showAgreementContainer();
			agreementPreview.resetSelectedDate();
			agreementPreview.setAgreementPreview(agreeementInfo);	
		});

	}
	
	private void getAgreement(Integer agreementId, Consumer<AgreementInfo> success) {
		impl.getAgreementInfo(agreementId, false, new AsyncCallback<AgreementInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				agreementPreview.showError("Error carga convenio", caught.getMessage());
			}

			@Override
			public void onSuccess(AgreementInfo agreement) {
				success.accept(agreement);
			}
			
		});
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
		AonDialog confirmDialog = new AonDialog("BORRADO DEFINITIVO", new HTMLPanel(String.valueOf("\u00BF") + "Desea eliminar definitivamente el convenio  " + agreement.getDescription() + "?. Le recordamos que este convenio tiene contratos asociados, si lo elimina definitivamente estos contratos se desvincular\u00E1n de este convenio."));
		confirmDialog.setGlassStyleName(style.dialogGlass());
		confirmDialog.addStyleName(style.dialogZIndex());
		confirmDialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onAccept() {
						agreementPreview.showLoading("Borrando convenio definitivamente ...");
						agreements.agreementsTree.getEnterpriseService().deleteAgreement(agreement, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								MainTrashAgreement.this.agreements.reloadAgreements(finish -> {
									if(MainTrashAgreement.this.agreements.getAgreementsTree().getTree().getItemCount() == 0)
										showAgreementMessage();
									else
										showAgreementContainer();
								});
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
		AonDialog confirmDialog = new AonDialog("Restaurar Convenio", new HTML(String.valueOf("\u00BF") + "Desea restaurar el convenio " + agreement.getDescription() + "?"));
		confirmDialog.setGlassStyleName(style.dialogGlass());
		confirmDialog.addStyleName(style.dialogZIndex());
		confirmDialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onAccept() {
						agreementPreview.showLoading("Restaurando convenio ...");
						agreements.agreementsTree.getEnterpriseService().updateAgreementId(agreement, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								MainTrashAgreement.this.agreements.reloadAgreements(finish -> {
									if(MainTrashAgreement.this.agreements.getAgreementsTree().getTree().getItemCount() == 0)
										showAgreementMessage();
									else
										showAgreementContainer();
								});
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
	
	public void hasTrashAgreements(Consumer<Boolean> hasTrashAgreement) {
		agreements.getTrashAgreements(s -> {
			hasTrashAgreement.accept(getAgreementsTree().getTree().getItemCount() != 0);
		});
	}
	
	// ------------------------------------ Main view
	
	private void showAgreementMessage() {
		agreementContainer.getElement().getStyle().setDisplay(Display.NONE);
		agreementMessage.getElement().getStyle().clearDisplay();
	}
	
	private void showAgreementContainer() {
		agreementMessage.getElement().getStyle().setDisplay(Display.NONE);
		agreementContainer.getElement().getStyle().clearDisplay();
	}
	
}
