package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesOutOfDateEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class OutOfDateEntryFinder extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	
	SimpleLayoutPanel content;
	ScrollPanel container;
	private String domainName;
	private String user;
	private Domain domain;

	private AccUtilitiesResult findResult;
	private AonToolbarButton refresh;
	private AonToolbarButton move;	
	
	protected OutOfDateEntryFinder(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);
		
		content = new SimpleLayoutPanel();
		container = new ScrollPanel();
		container.setStyleName(AON.CSS.aonScrollArea());
		
		content.add(container);
		setContent(content);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Buscador de apuntes fuera de fecha";
	}

	public void run() {		
		
		refresh.setVisible(true);
		move.setVisible(false);
		
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(getSplashWidget());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();		
		 
		SERVICE.outOfDateEntries(domainName, user, domain, new AsyncCallback<AccUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
				popup.hide();
			}

			@Override
			public void onSuccess(AccUtilitiesResult result) {
				popup.hide();
				container.setWidget( paintResults(result) );
				move.setVisible(!result.isEmpty());
				// TODO - ESTE ES EL BOTON PARA MOVER LOS APUNTES A LOS EJERCICIOS DE SU FECHA, SE DEJA DE MOMENTO ASI HASTA QUE EUKE LO REVISE
				move.setVisible(false);
			}
		});
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		findResult = result;
		FlowPanel log = new FlowPanel();
		log.setStyleName(AON.CSS.aonWidthAlmostAll());
		log.addStyleName(AON.CSS.aonBlockCenter());
		log.addStyleName(AON.CSS.aonMarginTop());
		log.addStyleName(AON.CSS.aonMarginBottom());
		log.addStyleName(AON.CSS.aonFixedFont());
		log.addStyleName(AON.CSS.aonFontMedium());
		log.addStyleName(AON.CSS.aonNowrap());
		if (result != null && !result.isEmpty()) {
			String lastDomain = null;
			DisclosurePanel disclosurePanel = null;
			FlowPanel domainPanel = null;
			for (IAccUtilitiesItem item : result.getItems()) {
				if (!AonStringUtils.equals(lastDomain, item.getDomainName())) {
					if (disclosurePanel != null) {
						String header = lastDomain + " (" + domainPanel.getWidgetCount() + ")";
						disclosurePanel.getHeaderTextAccessor().setText(header);
						disclosurePanel.getHeader().addStyleName(AON.CSS.aonFixedFont());
						disclosurePanel.getHeader().addStyleName(AON.CSS.aonFontMedium());
						log.add(disclosurePanel);
					}
					disclosurePanel = new DisclosurePanel(item.getDomainName());
					if (AonStringUtils.isBlank(lastDomain)) disclosurePanel.setOpen(true);
					lastDomain = item.getDomainName();
					domainPanel = new FlowPanel();
					disclosurePanel.add(domainPanel);
					disclosurePanel.addStyleName(AON.CSS.aonMarginTop());
					disclosurePanel.addStyleName(AON.CSS.aonFixedFont());
					disclosurePanel.addStyleName(AON.CSS.aonFontMedium());
					disclosurePanel.addStyleName(AON.CSS.aonNowrap());
				}
				item.getType().visit( new OutOfDateVisitor(domainPanel,(AccUtilitiesOutOfDateEntryItem) item) );
			}
			if (disclosurePanel != null) {
				String header = lastDomain + " (" + domainPanel.getWidgetCount() + ")";
				disclosurePanel.getHeaderTextAccessor().setText(header);
				disclosurePanel.getHeader().addStyleName(AON.CSS.aonFixedFont());
				disclosurePanel.getHeader().addStyleName(AON.CSS.aonFontMedium());
				log.add(disclosurePanel);
			}
			
		} else {
			Label label = new Label(AON.MSG.noData());
			log.add(label);
		}
		return log;
	}

	private void showEntry(int domain,Integer entryId) {
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad( new AccountEntryModuleOptions()
			.setParentWidget( entryDialog)
			.setDomainName( domainName )
			.setUser( user )
			.setDomain( domain)
			.setAccountEntryId( entryId )
			.setSessionLogTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setPreviewTabVisible(true)
			.setTrialBalanceFromPreviewEnabled(false)
			.setExternalCallback( new ModuleCallback() {
			
				@Override public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
				}
				@Override public void onFailure(Throwable caught) {}
				@Override public void onExit() {
					entryDialog.hide();
				}
				@Override public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}
	
	protected Widget getToolbarPanel() {		
		 
		AonToolbar toolbarPanel = new AonToolbar(getOptionDescription());
		
		refresh = new AonToolbarButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});		
		toolbarPanel.add(refresh);
		
		move = new AonToolbarButton("Mover apuntes al ejercicio seg\u00FAn su fecha",AON.CSS.aonIconFix());
		move.setVisible(false);
		move.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (!findResult.isEmpty()) {
					new AonConfirmDialog().confirm("Mover apuntes","Esta opci\u00F3n mueve los apuntes encontrados, al ejercicio seg\u00FAn la fecha de cada apunte. Solo se mover\u00E1n los apuntes si el ejercicio destino existe y su estado es Activo o Apertura. \u00BFDesea continuar?" 
							, new AonConfirmDialogCallback() {
							
							@Override
							public void onAccept() {
								refresh.setVisible(false);
								move.setVisible(false);
								moveEntries();
							}
			
							@Override
							public void onCancel() {
								// Nothing
							}
						});
				}
			}
		});
		toolbarPanel.add(move);
		
		return toolbarPanel;
		
	}

	protected void moveEntries() {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(getSplashWidget());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();		
		 
		SERVICE.moveOutOfDateEntries(domainName, user, domain, findResult, new AsyncCallback<AccUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
				popup.hide();
			}

			@Override
			public void onSuccess(AccUtilitiesResult result) {
				popup.hide();				
				container.setWidget( paintResults(result) );
				AonMessageDialog.show("MOVER APUNTES", "Para aquellos ejercicios a donde se han movido apuntes, es recomendable ejecutar los procesos de Regenerar el n\u00FAmero de diario y Regenerar el n\u00FAmero de IVA soportado, pues pueden encontrarse m\u00E1s de un apunte con el mismo n\u00FAmero de diario o m\u00E1s de una factura con el mismo n\u00FAmero de IVA soportado.");
			}
		});
		
	}

	private class OutOfDateVisitor implements IAccUtilitiesItemTypeVisitor {
		private FlowPanel domainPanel;
		private AccUtilitiesOutOfDateEntryItem item;
		
		public OutOfDateVisitor(FlowPanel domainPanel, AccUtilitiesOutOfDateEntryItem item) {
			this.domainPanel = domainPanel;
			this.item = item;
		}
		
		@Override
		public void visitOutOfDateEntry(AccUtilitiesItemType type) {
			FlowPanel itemPanel = new FlowPanel();
			InlineLabel msgLabel = new InlineLabel(item.getMessage());
			itemPanel.add(msgLabel);
			
			InlineLabel clickLabel = new InlineLabel("Ver/Editar");
			clickLabel.setTitle("Click para Ver/Editar");
			clickLabel.setStyleName(AON.CSS.aonMarginLeft());;
			clickLabel.addStyleName(AON.CSS.aonButton());
			clickLabel.addStyleName(AON.CSS.aonIconSearch());
			clickLabel.addStyleName(AON.CSS.aonTextButton());
			itemPanel.add(clickLabel);
			clickLabel.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showEntry(item.getDomain(),item.getEntryId());
				}
			});
			domainPanel.add(itemPanel);
		}
		
		@Override public void visitParentAccountLinker(AccUtilitiesItemType type) {}
		@Override public void visitOther(AccUtilitiesItemType type) {}
		@Override public void visitInfoMessage(AccUtilitiesItemType type) {}
		@Override public void visitErrorMessage(AccUtilitiesItemType type) {}
		@Override public void visitEmptyEntry(AccUtilitiesItemType type) {}
		@Override public void visitDeleteEntries(AccUtilitiesItemType type) {}		
		@Override public void visitAccountIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitDomainIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitNoLowLevelAccount(AccUtilitiesItemType type) {}
		@Override public void visitCustomerAccount(AccUtilitiesItemType type) {}
		@Override public void visitSupplierAccount(AccUtilitiesItemType type) {}
		@Override public void visitCreditorAccount(AccUtilitiesItemType type) {}
		@Override public void visitWrongRecordedInvoices(AccUtilitiesItemType type) {}
		@Override public void visitInvoiceIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitAccountChange(AccUtilitiesItemType type) {}
		@Override public void visitUnbalancedEntry(AccUtilitiesItemType type) {}
	}
}
