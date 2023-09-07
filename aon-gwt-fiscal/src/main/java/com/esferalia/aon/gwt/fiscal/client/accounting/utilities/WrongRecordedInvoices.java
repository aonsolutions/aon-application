package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryPrinter;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoiceViewer;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesWrongRecordedInvoicesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class WrongRecordedInvoices extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	
	SimpleLayoutPanel content;
	ScrollPanel container;
	private String domainName;
	private String user;
	private Domain domain;
	
	protected WrongRecordedInvoices(String domainName, String user, Domain domain) {
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
		return AonStringUtils.BULLET + " Chequeo de integridad de facturas contabilizadas";
	}

	public void run() {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(getSplashWidget());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		SERVICE.wrongRecordedInvoices(domainName, user, domain, new AsyncCallback<AccUtilitiesResult>(){

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
			}
		});
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
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
				item.getType().visit( new WrongRecordedInvoicesVisitor(domainPanel,(AccUtilitiesWrongRecordedInvoicesItem) item) );
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

	private void showInvoice(int domain,Invoice invoice) {
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.invoice());
		InvoiceViewer viewer = new InvoiceViewer(invoice);
		viewer.addStyleName(AON.CSS.aonMarginTop());
		entryDialog.add(viewer);
		entryDialog.center();
		entryDialog.show();
	}
	
	protected Widget getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar(getOptionDescription());
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		toolbarPanel.add(refresh);
		return toolbarPanel;
	}

	private class WrongRecordedInvoicesVisitor implements IAccUtilitiesItemTypeVisitor {
		private FlowPanel domainPanel;
		private AccUtilitiesWrongRecordedInvoicesItem item;
		
		public WrongRecordedInvoicesVisitor(FlowPanel domainPanel, AccUtilitiesWrongRecordedInvoicesItem item) {
			this.domainPanel = domainPanel;
			this.item = item;
		}
		
		@Override
		public void visitWrongRecordedInvoices(AccUtilitiesItemType type) {
			FlowPanel itemPanel = new FlowPanel();
			InlineLabel msgLabel = new InlineLabel(item.getMessage());
			msgLabel.addStyleName(AON.CSS.aonBold());
			itemPanel.add(msgLabel);
			
			InlineLabel clickLabel = new InlineLabel("[Ver Factura]");
			clickLabel.setTitle("Click para Ver Factura");
			clickLabel.setStyleName(AON.CSS.aonMarginLeft());;
			clickLabel.addStyleName(AON.CSS.aonButton());
			clickLabel.addStyleName(AON.CSS.aonIconSearch());
			clickLabel.addStyleName(AON.CSS.aonTextButton());
			itemPanel.add(clickLabel);
			clickLabel.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showInvoice(item.getDomain(),item.getInvoice());
				}
			});
			if (item.isOnlyMarked()) {
				InlineLabel fixLabel = new InlineLabel("[Arreglar]");
				fixLabel.setTitle("Click para arreglar el problema");
				fixLabel.setStyleName(AON.CSS.aonMarginLeft());;
				fixLabel.addStyleName(AON.CSS.aonButton());
				fixLabel.addStyleName(AON.CSS.aonIconFix());
				fixLabel.addStyleName(AON.CSS.aonTextButton());
				itemPanel.add(fixLabel);
				fixLabel.addClickHandler( new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						fixLabel.setVisible(false);
						SERVICE.removeWrongCheckedInvoice(domainName, domain.getId(), user, item.getInvoice().getId(), new AsyncCallback<AccUtilitiesResult>(){
							@Override
							public void onFailure(Throwable caught) {
								openFootPanelIfNeeded();
								showErrorPanel(caught.getMessage());
							}
							
							@Override
							public void onSuccess(AccUtilitiesResult result) {
								run();
							}
						});
					}
				});
			}
			if (item.getEntries() != null) {
				for ( AccountEntry entry : item.getEntries() ) {
					FlowPanel entryPanel = new FlowPanel();
					entryPanel.addStyleName(AON.CSS.aonMarginLeft());
					entryPanel.addStyleName(AON.CSS.aonMarginBottom());
					entryPanel.addStyleName(AON.CSS.aonPaddingLeft());
					entryPanel.addStyleName(AON.CSS.aonBorder());
					FlowPanel buttonsPanel = new FlowPanel();
					if (entry.getPeriodStatus().isActive()) {
						Button deleteButton = new Button(AON.MSG.deleteAction());
						deleteButton.addStyleName(AON.CSS.aonButton());
						deleteButton.addStyleName(AON.CSS.aonIconDelete());
						deleteButton.addStyleName(AON.CSS.aonTextButton());
						deleteButton.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								deleteButton.setEnabled(false);
								SERVICE.removeWrongRecordedInvoice(domainName, domain.getId(), user, entry.getId(), new AsyncCallback<AccUtilitiesResult>(){
									@Override
									public void onFailure(Throwable caught) {
										openFootPanelIfNeeded();
										showErrorPanel(caught.getMessage());
									}
									
									@Override
									public void onSuccess(AccUtilitiesResult result) {
										run();
									}
								});
							}
						});
						buttonsPanel.add(deleteButton);
					} else {
						Label warningLabel = new Label("Ejercicio cerrado o inactivo. La correcci\u00F3n debe realizarse de forma manual.");
						warningLabel.setStyleName(AON.CSS.aonBlockMessage());
						warningLabel.addStyleName(AON.CSS.aonBlockWarningMessage());
						warningLabel.addStyleName(AON.CSS.aonMarginTop());
						buttonsPanel.add(warningLabel);
					}
					entryPanel.add(buttonsPanel);
					FocusPanel entryPrintedPanel = AccountEntryPrinter.print(entry);
					entryPanel.add(entryPrintedPanel);
					itemPanel.add(entryPanel);
				}
			}
			domainPanel.add(itemPanel);
		}
		@Override public void visitUnbalancedEntry(AccUtilitiesItemType type) {}
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
		@Override public void visitInvoiceIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitAccountChange(AccUtilitiesItemType type) {}
		@Override public void visitOutOfDateEntry(AccUtilitiesItemType type) {}
	}
}
