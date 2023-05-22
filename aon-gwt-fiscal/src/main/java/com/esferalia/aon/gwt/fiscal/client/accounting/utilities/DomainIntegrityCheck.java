package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog.AonMessageDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
		container.setStyleName(AON.CSS.aonScrollArea());
		content.add(container);
		setContent(content);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Chequeo de integridad de dominios de cuentas en asientos contables.";
	}

	@Override
	protected Widget getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar(getOptionDescription());
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		toolbar.add(refresh);
		return toolbar;
	}
	
	
	public void run() {
		final PopupPanel popup = new PopupPanel(false, true);
		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleName(AON.CSS.aonBlockCenter());
		hp.addStyleName(AON.CSS.aonMarginTop() );
		Label iconWaitLabel = new Label();
		iconWaitLabel.setStyleName(AON.CSS.aonLoader());
		iconWaitLabel.addStyleName(AON.CSS.aonMargin());
		hp.add(iconWaitLabel);
		Label textWaitLabel = new Label( AON.MSG.processing());
		textWaitLabel.setStyleName(AON.CSS.aonMargin());
		textWaitLabel.addStyleName(AON.CSS.aonBold());
		hp.add(textWaitLabel);
		popup.add(hp);
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
			tab.setStyleName(AON.CSS.aonGrid());
			tab.addStyleName(AON.CSS.aonBlockCenter());
			
			tab.getColumnFormatter().setWidth(0, "50px");
			tab.getColumnFormatter().setWidth(1, "auto");
			tab.getColumnFormatter().setWidth(2, "100px");
			
	
			tab.getCellFormatter().setStyleName(0,0, AON.CSS.aonGridHeader());
			tab.getCellFormatter().setStyleName(0,1, AON.CSS.aonGridHeader());
			tab.getCellFormatter().setStyleName(0,2, AON.CSS.aonGridHeader());
			
			tab.setWidget(0, 0, new Label("*"));
			tab.setWidget(0, 1, new Label("Mensaje"));
			tab.setWidget(0, 2, new Label(""));
			
			int row = 1;
			for (IAccUtilitiesItem item : result.getItems()) {
				AccUtilitiesDomainIntegrityItem it = (AccUtilitiesDomainIntegrityItem) item;
				
				tab.setWidget(row, 0, new Label(AonNumberUtils.toString(it.getCount())));
				tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTextRight());
				
				tab.setWidget(row, 1, new Label(it.getMessage()));
				
				FlowPanel fixPanel = new FlowPanel();
				item.getType().visit( new DomainIntegritVisitor(fixPanel,it) );
				tab.setWidget(row, 2, fixPanel);
				
				row++;
			}
			
			tabContainer.add(tab);
		} else {
			Label noData = new Label( AON.MSG.noData());
			noData.setStyleName(AON.CSS.aonTextCenter());
			noData.addStyleName(AON.CSS.aonBold());
			noData.addStyleName(AON.CSS.aonMarginTop());
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
		@Override public void visitDeleteEntries(AccUtilitiesItemType type) {}
		@Override public void visitNoLowLevelAccount(AccUtilitiesItemType type) {}
		@Override public void visitAccountIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitWrongRecordedInvoices(AccUtilitiesItemType type) {}
		@Override public void visitInvoiceIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitAccountChange(AccUtilitiesItemType type) {}
		@Override public void visitOutOfDateEntry(AccUtilitiesItemType type) {}
		
		@Override public void visitDomainIntegrity(AccUtilitiesItemType type) {
			if (item.getRightAccount() == null) {
				AonTextButton fixLabel = new AonTextButton("No existe",AON.CSS.aonIconInvalid());
				fixLabel.addClickHandler( new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						AonMessageDialog.show("Error","No existe la cuenta " + item.getWrongAccount().getFullName() + " en el dominio actual");
					}
					
				});
				domainPanel.add(fixLabel);
			} else {
				if (!AonStringUtils.equals(item.getRightAccount().getDescription(), item.getWrongAccount().getDescription())) {
					AonTextButton fixLabel = new AonTextButton("No coincide",AON.CSS.aonIconInvalid());
					fixLabel.addClickHandler( new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							AonMessageDialog.show("Error","La cuenta [" + item.getWrongAccount().getFullName() + "] no coincide con la cuenta [" + item.getRightAccount().getFullName() + "] del dominio actual");
						}
						
					});
					domainPanel.add(fixLabel);
				} else {
					AonTextButton fixLabel = new AonTextButton("Arreglar",AON.CSS.aonIconFix());
					fixLabel.addClickHandler( new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (item.getRightAccount() == null) {
								AonMessageDialog.show("Error","La cuenta [" + item.getWrongAccount().getFullName() +"] no existe en el dominio en curso. Se creará." );
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
										AonMessageDialog._show("Resultado", msg.toString(), new AonMessageDialogCallback() {
											@Override
											public void onAccept() {
												run();
											}
											
											@Override
											public void onClose() {
											}
										});
									} else {
										run();
									}
									
								}
							});
						}
					});
					domainPanel.add(fixLabel);
				}
			}
			
		}

	}
}
