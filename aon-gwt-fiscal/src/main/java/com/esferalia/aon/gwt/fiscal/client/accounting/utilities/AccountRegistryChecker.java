package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextButton;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountLinkItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class AccountRegistryChecker extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	
	protected AccountRegistryChecker(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);

		SimpleLayoutPanel content = new SimpleLayoutPanel();
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.CSS.aonScrollArea());
		content.add(container);
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonTable());
		tab.addStyleName(AON.CSS.aonBlockCenter());

		int row = 0;
		Label title = new Label("Introduzca los par\u00E1metros de b\u00FAsqueda");
		title.setStyleName(AON.CSS.aonMarginBottom());
		tab.setWidget(row, 0, title);
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonBold());
		tab.getCellFormatter().addStyleName(row, 0, AON.CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		
		tab.setWidget(row, 0, new Label(AON.MSG.filter()));
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		AonTextBox filter = new AonTextBox();
		filter.setMaxLength(128);
		filter.setVisibleLength(45);
		tab.setWidget(row, 1, filter);
		row++;
		
		tab.setWidget(row, 0, new Label(AON.MSG.show()));
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		FlowPanel checksContainer = new FlowPanel();
		CheckBox customer = new CheckBox(AON.MSG.customer());
		checksContainer.add(customer);
		CheckBox supplier = new CheckBox(AON.MSG.supplier());
		checksContainer.add(supplier);
		CheckBox creditor = new CheckBox(AON.MSG.creditor());
		checksContainer.add(creditor);
		tab.setWidget(row, 1, checksContainer);
		row++;
		
		tab.setWidget(row, 0, new Label());
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		CheckBox inactives = new CheckBox("Tambi\u00E9n los inactivos");
		tab.setWidget(row, 1, inactives);
		row++;
		
		tab.setWidget(row, 0, new Label());
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		CheckBox synchronizables = new CheckBox("Solo los que sus descripciones no coinciden");
		tab.setWidget(row, 1, synchronizables);
		row++;

		tab.setWidget(row, 0, new Label());
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		CheckBox noAccount = new CheckBox("Solo los que no tienen cuenta asginada");
		tab.setWidget(row, 1, noAccount);
		row++;

		AonTextButton search = new AonTextButton(AON.MSG.searchAction(),AON.CSS.aonIconSearch());
		search.addStyleName(AON.CSS.aonMarginTop());
		
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.setWidget(row, 0, search);
		
		search.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AccUtilitiesParams params = new AccUtilitiesParams()
						.setQuery(filter.getValue())
						.setShowCustomers(customer.getValue())
						.setShowSuppliers(supplier.getValue())
						.setShowCreditors(creditor.getValue())
						.setShowInactives(inactives.getValue())
						.setShowWihtoutAccount(noAccount.getValue())
						.setShowSynchronizables(synchronizables.getValue())
				;
				SERVICE.getAccountLinks(domainName, user, domain.getId(), params, new AsyncCallback<AccUtilitiesResult>() {

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(caught.getMessage());
					}

					@Override
					public void onSuccess(AccUtilitiesResult result) {
						showResults(result);
					}

				});
				
			}
		});
		container.setWidget(tab);
		setContent(content);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Chequeo de cuentas asignadas a clientes, proveedores y acreedores";
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonGrid());

		tab.getColumnFormatter().setWidth(0, "20px");
		tab.getColumnFormatter().setWidth(1, "100px");
		tab.getColumnFormatter().setWidth(2, "275px");
		tab.getColumnFormatter().setWidth(3, "20px");
		tab.getColumnFormatter().setWidth(4, "20px");
		tab.getColumnFormatter().setWidth(5, "275px");
		tab.getColumnFormatter().setWidth(6, "auto");
		
		tab.setWidget(0, 0, new Label());
		tab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonGridHeader());
		tab.setWidget(0, 1, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(0, 1, AON.CSS.aonGridHeader());
		tab.getFlexCellFormatter().setColSpan(0, 1, 2);
		tab.setWidget(0, 2, new Label("Entidad vinculada"));
		tab.getCellFormatter().setStyleName(0, 2, AON.CSS.aonGridHeader());
		tab.getFlexCellFormatter().setColSpan(0, 2, 3);
		tab.setWidget(0, 3, new Label("Acciones disponibles"));
		tab.getCellFormatter().setStyleName(0, 3, AON.CSS.aonGridHeader());

		if (result != null && !result.isEmpty()) {
			for (IAccUtilitiesItem item : result.getItems()) {
				item.getType().visit( new AccountRegistryCheckerVisitor(tab,(AccUtilitiesAccountLinkItem) item) );
			}
		} else {
			Label noData = new Label(AON.MSG.noData());
			noData.setStyleName(AON.CSS.aonColorRed());
			noData.addStyleName(AON.CSS.aonBold());
			noData.addStyleName(AON.CSS.aonTextCenter());
			tab.setWidget(1, 0, noData);
			tab.getFlexCellFormatter().setColSpan(1, 0, 7);
		}
		return tab;
	}

	private class AccountRegistryCheckerVisitor implements IAccUtilitiesItemTypeVisitor {
		private FlexTable tab;
		private AccUtilitiesAccountLinkItem item;
		
		public AccountRegistryCheckerVisitor(FlexTable tab, AccUtilitiesAccountLinkItem item) {
			this.tab = tab;
			this.item = item;
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
		@Override public void visitWrongRecordedInvoices(AccUtilitiesItemType type) {}
		
		@Override 
		public void visitCustomerAccount(AccUtilitiesItemType type) {
			Label iconLabel = new Label();
			iconLabel.setTitle(AON.MSG.customer());
			iconLabel.setStyleName(AON.CSS.aonIconLabel());
			iconLabel.addStyleName(AON.CSS.aonIconCustomer());
			int row = tab.getRowCount(); 
			tab.setWidget(row, 3, iconLabel);
			visit(row);
		}
		@Override public void visitSupplierAccount(AccUtilitiesItemType type) {
			Label iconLabel = new Label();
			iconLabel.setTitle(AON.MSG.supplier());
			iconLabel.setStyleName(AON.CSS.aonIconLabel());
			iconLabel.addStyleName(AON.CSS.aonIconSupplier());
			int row = tab.getRowCount(); 
			tab.setWidget(row, 3, iconLabel);
			visit(row);
		}
		@Override public void visitCreditorAccount(AccUtilitiesItemType type) {
			Label iconLabel = new Label();
			iconLabel.setTitle(AON.MSG.creditor());
			iconLabel.setStyleName(AON.CSS.aonIconLabel());
			iconLabel.addStyleName(AON.CSS.aonIconCreditor());
			int row = tab.getRowCount(); 
			tab.setWidget(row, 3, iconLabel);
			visit(row);
		}
		
		private void visit(int row) {
			Label inheritanceLabel = new Label();
			if (item.isParentAccount()) {
				inheritanceLabel.setTitle("Cuenta heredada del entorno");
				inheritanceLabel.setStyleName(AON.CSS.aonIconLabel());
				inheritanceLabel.addStyleName(AON.CSS.aonIconLevelTop());
			}
			tab.setWidget(row, 0, inheritanceLabel);

			InlineLabel accountCodeLabel = new InlineLabel(item.getAccountCode());
			tab.setWidget(row, 1, accountCodeLabel);
			
			InlineLabel accountDescriptionLabel = new InlineLabel(item.getAccountDescripion());
			tab.setWidget(row, 2, accountDescriptionLabel);
			
			Label inactiveLabel = new Label();
			if (item.isLinkedInactive()) {
				inactiveLabel.setTitle("Inactivo");
				inactiveLabel.setStyleName(AON.CSS.aonIconLabel());
				inactiveLabel.addStyleName(AON.CSS.aonIconBlock());
			}
			tab.setWidget(row, 4, inactiveLabel);
			
			InlineLabel registryLabel = new InlineLabel(item.getLinkedDescription());
			tab.setWidget(row, 5, registryLabel);
			
			FlowPanel buttonContainer = new FlowPanel();
			if (!item.isParentAccount() 
				&& item.getAccountId() != null 
				&& !AonStringUtils.equals(item.getAccountDescripion(), item.getLinkedDescription())) {
				
				AonTextButton synchronize = new AonTextButton("Sincronizar",AON.CSS.aonIconRefresh());
				buttonContainer.add(synchronize);
				synchronize.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						synchronize.setEnabled(false);
						
						SERVICE.changeAccountDescription(getDomainName(), getUser() , getDomain().getId(), item.getAccountId()
								, item.getLinkedDescription() , new AsyncCallback<String>() {
							
							@Override
							public void onSuccess(String newDescription) {
								synchronize.setVisible(false);
								InlineLabel accountDescriptionLabel = new InlineLabel(newDescription);
								accountDescriptionLabel.addStyleName(AON.CSS.aonValueChanged());
								new Timer() {
									@Override
									public void run() {
										accountDescriptionLabel.removeStyleName(AON.CSS.aonValueChanged());
									}
								}.schedule(2000);
								tab.setWidget(row, 2, accountDescriptionLabel);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								showErrorPanel(caught.getMessage());
							}
						});
					}
				});
			}

			if (item.getAccountId() == null) {
				AonTextButton create = new AonTextButton("Crear",AON.CSS.aonIconAdd());
				create.setTitle("Crear y vincular una cuenta");
				buttonContainer.add(create);
				
				create.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						create.setEnabled(false);
						SERVICE.createAndLinkAccount(getDomainName(), getUser() , getDomain().getId(), item.getRegistryType()
								, item.getLinkedId() , new AsyncCallback<Account>() {
							
							@Override
							public void onSuccess(Account account) {
								create.setVisible(false);
								InlineLabel accountCodeLabel = new InlineLabel(account.getCode());
								InlineLabel accountDescriptionLabel = new InlineLabel(account.getDescription());
								tab.setWidget(row, 1, accountCodeLabel);
								tab.setWidget(row, 2, accountDescriptionLabel);
								accountCodeLabel.addStyleName(AON.CSS.aonValueChanged());
								accountDescriptionLabel.addStyleName(AON.CSS.aonValueChanged());
								new Timer() {
									@Override
									public void run() {
										accountCodeLabel.removeStyleName(AON.CSS.aonValueChanged());
										accountDescriptionLabel.removeStyleName(AON.CSS.aonValueChanged());
									}
								}.schedule(2000);
							}
							@Override
							public void onFailure(Throwable caught) {
								showErrorPanel(caught.getMessage());
							}
						});
					}
				});
				
			}
			tab.setWidget(row, 6, buttonContainer);	
		}
		
	}
}
