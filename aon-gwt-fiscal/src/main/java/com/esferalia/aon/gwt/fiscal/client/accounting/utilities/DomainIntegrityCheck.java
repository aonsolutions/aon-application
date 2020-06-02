package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog.MessageDialogCallback;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesDomainIntegrityItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

class DomainIntegrityCheck extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	 
	SimpleLayoutPanel content;
	ScrollPanel container;
	private String domainName;
	private String user;
	private Domain domain;
	
	protected DomainIntegrityCheck(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);

		content = new SimpleLayoutPanel();
		container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		content.add(container);
		setContent(content);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Chequeo de integridad de dominios de cuentas en asientos contables.";
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
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		final Button refresh = new Button();
		refresh.setText(AON.MSG.refresh());
		refresh.setTitle(AON.MSG.refresh());
		refresh.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		refresh.addStyleName(AON.AON_CSS.aonIconRedo());
		refresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		buttonContainer.add(refresh);
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
		
		SERVICE.domainIntegrity(domainName, user, domain, new AsyncCallback<AccUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
				popup.hide();
			}

			@Override
			public void onSuccess(AccUtilitiesResult result) {
				popup.hide();
				cleanErrorPanel();
				container.setWidget( paintResults(result) );
			}
		});
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		FlowPanel tabContainer = new FlowPanel();
		if (result.getItems() != null && result.getItems().size() > 0) {
			FlexTable tab = new FlexTable();
			tab.setStyleName(AON.AON_CSS.aonDataTable());
			tab.addStyleName(AON.AON_CSS.aonBlockCenter());
			
			tab.getColumnFormatter().setWidth(0, "50px");
			tab.getColumnFormatter().setWidth(1, "auto");
			tab.getColumnFormatter().setWidth(2, "100px");
			
	
			tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonDataTableHeader());
			
			tab.setWidget(0, 0, new Label("*"));
			tab.setWidget(0, 1, new Label("Mensaje"));
			tab.setWidget(0, 2, new Label(""));
			
			int row = 1;
			for (IAccUtilitiesItem item : result.getItems()) {
				AccUtilitiesDomainIntegrityItem it = (AccUtilitiesDomainIntegrityItem) item;
				
				tab.setWidget(row, 0, new Label(AonNumberUtils.toString(it.getCount())));
				tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonTextRight());
				
				tab.setWidget(row, 1, new Label(it.getMessage()));
				
				FlowPanel fixPanel = new FlowPanel();
				item.getType().visit( new DomainIntegritVisitor(fixPanel,it) );
				tab.setWidget(row, 2, fixPanel);
				
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
	
	private class DomainIntegritVisitor implements IAccUtilitiesItemTypeVisitor {
		private FlowPanel domainPanel;
		private AccUtilitiesDomainIntegrityItem item;
		
		public DomainIntegritVisitor(FlowPanel domainPanel, AccUtilitiesDomainIntegrityItem item) {
			this.domainPanel = domainPanel;
			this.item = item;
		}
		
		@Override public void visitUnbalancedEntry(AccUtilitiesItemType type) {}
		@Override public void visitParentAccountLinker(AccUtilitiesItemType type) {}
		@Override public void visitOther(AccUtilitiesItemType type) {}
		@Override public void visitInfoMessage(AccUtilitiesItemType type) {}
		@Override public void visitErrorMessage(AccUtilitiesItemType type) {}
		@Override public void visitCustomerAccount(AccUtilitiesItemType type) {}
		@Override public void visitSupplierAccount(AccUtilitiesItemType type) {}
		@Override public void visitCreditorAccount(AccUtilitiesItemType type) {}
		@Override public void visitEmptyEntry(AccUtilitiesItemType type) {}
		@Override public void visitNoLowLevelAccount(AccUtilitiesItemType type) {}
		@Override public void visitAccountIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitWrongRecordedInvoices(AccUtilitiesItemType type) {}
		
		@Override public void visitDomainIntegrity(AccUtilitiesItemType type) {
			InlineLabel fixLabel = new InlineLabel();
			fixLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			fixLabel.addStyleName(AON.AON_CSS.aonClickableBlock());
			fixLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
			if (item.getRightAccount() == null) {
				fixLabel.addStyleName(AON.AON_CSS.aonIconBlocked());
				fixLabel.setText("No existe");
				fixLabel.addClickHandler( new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						MessageDialog.show("No existe la cuenta " + item.getWrongAccount().getFullName() + " en el dominio actual");
					}
					
				});
			} else {
				if (!AonStringUtils.equals(item.getRightAccount().getDescription(), item.getWrongAccount().getDescription())) {
					fixLabel.addStyleName(AON.AON_CSS.aonIconBlocked());
					fixLabel.setText("No coincide");
					fixLabel.addClickHandler( new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							MessageDialog.show("La cuenta [" + item.getWrongAccount().getFullName() + "] no coincide con la cuenta [" + item.getRightAccount().getFullName() + "] del dominio actual");
						}
						
					});
				} else {
					fixLabel.setText("Arreglar");
					fixLabel.setTitle("Arreglar");
					fixLabel.addStyleName(AON.AON_CSS.aonIconPointRed());
					fixLabel.addClickHandler( new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (item.getRightAccount() == null) {
								MessageDialog.show("La cuenta [" + item.getWrongAccount().getFullName() +"] no existe en el dominio en curso. Se creará." );
							}
							SERVICE.domainIntegrityFix(domainName, user, item.getDomain(), item.getWrongAccount(), new AsyncCallback<AccUtilitiesResult>() {
								
								@Override
								public void onFailure(Throwable caught) {
									openFootPanelIfNeeded();
									showErrorPanel(caught.getMessage());
								}
								
								@Override
								public void onSuccess(AccUtilitiesResult result) {
									StringBuilder msg = new StringBuilder();
									for (IAccUtilitiesItem item : result.getItems()) {
										if (item.getType() == AccUtilitiesItemType.INFO_MESSAGE) {
											msg.append(item.getMessage() + " ");
										}
									}
									if (AonStringUtils.isNotBlank(msg.toString())) {
										MessageDialog.show( msg.toString(), new MessageDialogCallback() {
											
											@Override
											public void onClose() {
												run();
											}
										});
									} else {
										run();
									}
									
								}
							});
						}
					});
				}
			}

			domainPanel.add(fixLabel);
			
		}
	}
}
