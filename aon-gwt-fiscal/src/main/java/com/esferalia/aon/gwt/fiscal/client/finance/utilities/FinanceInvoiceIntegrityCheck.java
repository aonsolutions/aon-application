package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceInvoiceIntegrityItem;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.finance.utilities.IFinanceUtilitiesItem;
import com.esferalia.aon.occam.api.model.finance.utilities.IFinanceUtilitiesItem.FinanceUtilitiesItemType;
import com.esferalia.aon.occam.api.model.finance.utilities.IFinanceUtilitiesItem.IFinanceUtilitiesItemTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class FinanceInvoiceIntegrityCheck extends OptionBase {

	private static FinanceUtilitiesServiceAsync SERVICE;
	private String domainName;
	private String user;
	private Domain domain;
	
	private SimpleLayoutPanel content;
	private ScrollPanel container;
	
	protected FinanceInvoiceIntegrityCheck(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		FinanceUtilitiesServiceAsync serviceRaw = GWT.create(FinanceUtilitiesService.class);
		SERVICE = new FinanceUtilitiesServiceAsyncDecorator(serviceRaw);
		
		content = new SimpleLayoutPanel();
		content.setStyleName(AON.AON_CSS.aonBorderTop());
		
		container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		content.add(container);
		setContent(container);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Integridad de vencimientos en facturas.";
	}

	protected Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label(getOptionDescription()));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		
		Button run = new Button();
		run.setText("Buscar");
		run.setText(AON.MSG.searchAction());
		run.setTitle(AON.MSG.searchAction());
		run.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		run.addStyleName(AON.AON_CSS.aonIconLoupe());
		run.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		buttonContainer.add(run);
		
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	
	public void run() {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		SERVICE.financeInvoiceIntegrity(domainName, user, domain, new AsyncCallback<FinanceUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
				popup.hide();
			}

			@Override
			public void onSuccess(FinanceUtilitiesResult result) {
				popup.hide();
				cleanErrorPanel();
				container.setWidget( paintResults(result) );
			}
		});
	}

	@Override
	protected Widget paintResults(FinanceUtilitiesResult result) {
		FlowPanel tabContainer = new FlowPanel();
		if (result.getItems() != null && result.getItems().size() > 0) {
			FlexTable tab = new FlexTable();
			tab.setStyleName(AON.AON_CSS.aonDataTable());
			tab.addStyleName(AON.AON_CSS.aonBlockCenter());
			
			tab.getColumnFormatter().setWidth(0, "200px");
			tab.getColumnFormatter().setWidth(1, "80px");
			tab.getColumnFormatter().setWidth(2, "80px");
			tab.getColumnFormatter().setWidth(3, "70px");
			tab.getColumnFormatter().setWidth(4, "80px");
			tab.getColumnFormatter().setWidth(5, "80px");
			tab.getColumnFormatter().setWidth(6, "120px");
			tab.getColumnFormatter().setWidth(7, "auto");
			tab.getColumnFormatter().setWidth(8, "70px");
			
	
			tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().addStyleName(0,3, AON.AON_CSS.aonTextRight());
			tab.getCellFormatter().setStyleName(0,3, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,4, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,5, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,6, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,7, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,8, AON.AON_CSS.aonDataTableHeader());
			
			tab.setWidget(0, 0, new Label("Tipo"));
			tab.setWidget(0, 1, new Label("Estado."));
			tab.setWidget(0, 2, new Label("Fecha Vto."));
			tab.setWidget(0, 3, new Label("Importe"));
			tab.setWidget(0, 4, new Label("Fra."));
			tab.setWidget(0, 5, new Label("Fecha Fra."));
			tab.setWidget(0, 6, new Label("N. Factura"));
			tab.setWidget(0, 7, new Label("Titular"));
			tab.setWidget(0, 8, new Label(""));
			
			int row = 1;
			for ( IFinanceUtilitiesItem it : result.getItems() ) {
				FinanceInvoiceIntegrityItem item = (FinanceInvoiceIntegrityItem) it;
				Finance finance = item.getFinance();
				Invoice invoice = finance.getInvoice();
				tab.setWidget(row, 0, new Label(item.getMessage()));
				Label status = new Label( finance.getFinanceStatus().getDescription() );
				tab.setWidget(row, 1, status );
				finance.getFinanceStatus().visit( new IFinanceStatusVisitor() {
					@Override
					public void visitSettled() {
						status.setStyleName(AON.AON_CSS.aonColoRoyalblue());
					}
					
					@Override
					public void visitReturned() {
						status.setStyleName(AON.AON_CSS.aonColorRed());
						status.addStyleName(AON.AON_CSS.aonBold());
					}
					
					@Override
					public void visitPending() {
						status.setStyleName(AON.AON_CSS.aonColorRed());
					}
					
					@Override
					public void visitPaid() {
						status.setStyleName(AON.AON_CSS.aonColorGreen());
					}
					
					@Override
					public void visitBatched() {
						status.setStyleName(AON.AON_CSS.aonColorGreen());
					}
				});
				tab.setWidget(row, 2, new Label( AON.DATE_FORMAT.format( finance.getDueDate())));
				tab.getCellFormatter().setStyleName(row,3, AON.AON_CSS.aonTextRight());
				tab.setWidget(row, 3, new Label(AON.FMT.format( invoice.getTotal())));
				tab.setWidget(row, 4, new Label(invoice.getType().getDescription()));
				tab.setWidget(row, 5, new Label( AON.DATE_FORMAT.format( invoice.getIssueDate())));
				tab.setWidget(row, 6, new Label(invoice.isSales()?invoice.getDocumentNumber():invoice.getReferenceCode()));
				tab.setWidget(row, 7, new Label(invoice.getRegistryName()));
				FlowPanel fixPanel = new FlowPanel();
				item.getType().visit( new FinanceInvoiceIntegrityItemVisitor(fixPanel,(FinanceInvoiceIntegrityItem) item) );
				tab.setWidget(row, 8, fixPanel);
				row++;
			}
			
			tabContainer.add(tab);
		} else {
			Label noData = new Label( AON.MSG.noData());
			noData.setStyleName(AON.AON_CSS.aonTextCenter());
			noData.addStyleName(AON.AON_CSS.aonBold());
			noData.addStyleName(AON.AON_CSS.aonMarginTop());
			tabContainer.add(noData);	
		}
		
		return tabContainer;
	}
	
	private class FinanceInvoiceIntegrityItemVisitor implements IFinanceUtilitiesItemTypeVisitor {
		private FlowPanel domainPanel;
		private FinanceInvoiceIntegrityItem item;
		
		public FinanceInvoiceIntegrityItemVisitor(FlowPanel domainPanel, FinanceInvoiceIntegrityItem item) {
			this.domainPanel = domainPanel;
			this.item = item;
		}
		
		@Override public void visitErrorMessage(FinanceUtilitiesItemType type) {}
		@Override public void visitInfoMessage(FinanceUtilitiesItemType type) {}
		@Override public void visitOther(FinanceUtilitiesItemType type) {}
		@Override public void visitMissingFinanceInvoice(FinanceUtilitiesItemType type) {}
		@Override
		public void  visitFinanceInvoiceIntegrityCheck(FinanceUtilitiesItemType type) {
			InlineLabel fixLabel = new InlineLabel("Arreglar");
			fixLabel.setTitle("Arreglar");
			fixLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			fixLabel.addStyleName(AON.AON_CSS.aonIconSettings());
			fixLabel.addStyleName(AON.AON_CSS.aonClickableBlock());
			fixLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
			fixLabel.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fixLabel.setVisible(false);
					SERVICE.financeInvoiceIntegrityFix(domainName, user, item.getDomain(), item.getFinance(), new AsyncCallback<Finance>() {

						@Override
						public void onFailure(Throwable caught) {
							openFootPanelIfNeeded();
							showErrorPanel(caught.getMessage());
						}

						@Override
						public void onSuccess(Finance fin) {
							fixLabel.setVisible(true);
							fixLabel.setText("");
							fixLabel.removeStyleName(AON.AON_CSS.aonIconSettings());
							fixLabel.addStyleName(AON.AON_CSS.aonIconCheckYes());
						}
					});
				}
			});
			domainPanel.add(fixLabel);
			
			if (item.getTracking() != null && item.getTracking().getAccountEntry() != null ) {
				InlineLabel entryLabel = new InlineLabel("Apunte");
				entryLabel.setTitle("Apunte");
				entryLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
				entryLabel.addStyleName(AON.AON_CSS.aonIconInvoice());
				entryLabel.addStyleName(AON.AON_CSS.aonClickableBlock());
				entryLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
				entryLabel.addClickHandler( new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						showEntry( item.getDomain(), item.getTracking().getAccountEntry() );
					}
				});
				domainPanel.add(entryLabel);
			}
			
		}
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
			.setExternalCallback( new ModuleCallback() {
			
				@Override public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
					run();
				}
				@Override public void onFailure(Throwable caught) {}
				@Override public void onExit() {
					entryDialog.hide();
				}
				@Override public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
					run();
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}
}
