package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate.AccountEntryUpdateVisitor;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;


public class SpecialUpdatePanel extends ScrollPanel {
	
	public interface SpecialUpdatePanelCallback {
		public void onSuccess(IAccountEntryWrapper result);
		public void onCancel();
		public void onFailure(Throwable caught);			
	}

	
	static final AccountEntryServiceAsync ACCOUNT_ENTRY_SERVICE;
	static {
		AccountEntryServiceAsync accountEntryServiceRaw = GWT.create(AccountEntryService.class);
		ACCOUNT_ENTRY_SERVICE = new AccountEntryServiceAsyncDecorator(accountEntryServiceRaw);
	}

	public SpecialUpdatePanel(AccountEntryModuleOptions options, IAccountEntryWrapper entryWrapper, LinkedList<AccountEntryUpdate> updates, SpecialUpdatePanelCallback callback) {
		this.setWidth("800px;");
		this.setHeight("600px;");
		this.setStyleName(AON.CSS.aonScrollArea());
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonScrollArea());
		this.setWidget(rootPanel);
		
		AonDisplayTable table = new AonDisplayTable();
		rootPanel.add(table);
		table.addStyleName(AON.CSS.aonWidthAll());
		Visitor visitor = new Visitor(options, table, callback);
		AonCollectionUtils.stream(updates)
			.forEach(update -> update.visit(visitor, entryWrapper))
		;
		
		FlowPanel buttonsPanel =  new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginTop());
    	cancelButton.setText( AON.MSG.close());
    	cancelButton.addClickHandler(event -> callback.onCancel());
    	buttonsPanel.add(cancelButton);
    	rootPanel.add(buttonsPanel);
	}
	
	private class Visitor implements AccountEntryUpdateVisitor<AonDisplayTableRow> {
		
		private final AccountEntryModuleOptions options;
		private final AonDisplayTable table;
		private final SpecialUpdatePanelCallback callback;
		
		Visitor(AccountEntryModuleOptions options, AonDisplayTable table, SpecialUpdatePanelCallback callback) {
			this.options = options;
			this.table = table;
			this.callback = callback;
		}
		
		private AonDisplayTableRow getRow(AccountEntryUpdate update) {
			return table.addRow()
				.addCell(new InlineLabel(update.getDescription()),AON.CSS.aonFlexGrow1(),AON.CSS.aonBold(),AON.CSS.aonNowrap());
		}

		@Override
		public AonDisplayTableRow visitManualType(IAccountEntryWrapper wrapper) {
			AccountEntryUpdate update = AccountEntryUpdate.MANUAL_TYPE;
			return getRow( update )
				.addCell(getToggleButton( update, wrapper, false ))
				.addCell(new InlineLabel());
		}

		@Override
		public AonDisplayTableRow visitOpeningType(IAccountEntryWrapper wrapper) {
			AccountEntryUpdate update = AccountEntryUpdate.OPENING_TYPE;
			return getRow( update )
				.addCell(getToggleButton( update, wrapper, false ))
				.addCell(new InlineLabel());
		}

		@Override
		public AonDisplayTableRow visitSecurityLevel(IAccountEntryWrapper wrapper) {
			AccountEntryUpdate update = AccountEntryUpdate.SECURITY_LEVEL;
			return getRow( update )
				.addCell(getToggleButton( update, wrapper, wrapper.getAccountEntry().isConfidential()))
				.addCell(new InlineLabel());
		}

		@Override
		public AonDisplayTableRow visitInvestment(IAccountEntryWrapper wrapper) {
			if (wrapper instanceof AccountingInvoice) {
				AccountingInvoice ai = (AccountingInvoice) wrapper;
				AccountEntryUpdate update = AccountEntryUpdate.INVESTMENT;
				return getRow( update )
					.addCell(getToggleButton( update, wrapper, ai.getInvoice().isInvestment()))
					.addCell(new InlineLabel());
			}
			return null;
		}


		@Override
		public AonDisplayTableRow visitService(IAccountEntryWrapper wrapper) {
			if (wrapper instanceof AccountingInvoice) {
				AccountingInvoice ai = (AccountingInvoice) wrapper;
				AccountEntryUpdate update = AccountEntryUpdate.SERVICE;
				return getRow( update )
					.addCell(getToggleButton( update, wrapper, ai.getInvoice().isService()))
					.addCell(new InlineLabel());
			}
			return null;
		}

		@Override
		public AonDisplayTableRow visitVatAccrualPayment(IAccountEntryWrapper wrapper) {
			if (wrapper instanceof AccountingInvoice) {
				AccountingInvoice ai = (AccountingInvoice) wrapper;
				AccountEntryUpdate update = AccountEntryUpdate.VAT_ACCRUAL_PAYMENT;
				return getRow( update )
					.addCell(getToggleButton( update, wrapper, ai.getInvoice().isVatAccrualPayment()))
					.addCell(new InlineLabel());
			}
			return null;
		}
		
		@Override
		public AonDisplayTableRow visitTaxDate(IAccountEntryWrapper wrapper) {
			if (wrapper instanceof AccountingInvoice) {
				AccountingInvoice ai = (AccountingInvoice) wrapper;
				AccountEntryUpdate update = AccountEntryUpdate.TAX_DATE;
				AonDateBox taxDate = new AonDateBox();
				taxDate.setValue(ai.getInvoice().getTaxDate());
				taxDate.addValueChangeHandler(event -> ai.getInvoice().setTaxDate(event.getValue()));
				return getRow( update )
					.addCell(taxDate)
					.addCell(getOkButton( update, wrapper))
				;
			}
			return null;
		}

		@Override
		public AonDisplayTableRow visitWithholdingType(IAccountEntryWrapper wrapper) {
			if (wrapper instanceof AccountingInvoice) {
				AccountingInvoice ai = (AccountingInvoice) wrapper; 
				if (ai.getWithholdingData() != null) {
					AccountEntryUpdate update = AccountEntryUpdate.WITHHOLDING_TYPE;
					WithholdingTypeListBox withholdingType = new WithholdingTypeListBox();
					withholdingType.setValue(ai.getWithholdingData().getWithholdingType());
					withholdingType.addChangeHandler(event -> ai.setWithholdingType( withholdingType.getValue() ));
					return getRow( update )	
						.addCell(withholdingType)
						.addCell(getOkButton( update, wrapper))
					;
				}
			}
			return null;
		}

		@Override
		public AonDisplayTableRow visitActivity(IAccountEntryWrapper wrapper) {
			AccountEntryUpdate update = AccountEntryUpdate.ACTIVITY;
			ListBox activity = new ListBox();
			activity.setVisible(true);
			activity.addItem("-- Todas --", "");
			activity.setSelectedIndex(0);
			int i = 1;
			for (EnterpriseActivity ea : options.getConfiguration().getActivities()) {
				activity.addItem(ea.getDescription(), AonNumberUtils.toString( ea.getId()));
				if (AonNumberUtils.equals(wrapper.getAccountEntry().getActivity(), ea.getId())  ) {
					activity.setSelectedIndex(i);
				}
				i++;
			}
			activity.addChangeHandler(event -> {
				Integer act = AonNumberUtils.toInteger(activity.getSelectedValue());
				wrapper.getAccountEntry().setActivity( act );
			});
			return getRow( update )
				.addCell(activity)
				.addCell(getOkButton(update, wrapper));
		}


		@Override
		public AonDisplayTableRow visitOperatingAccount(IAccountEntryWrapper wrapper) {
			if (wrapper instanceof AccountingInvoice) {
				AccountEntryUpdate update = AccountEntryUpdate.OPERATING_ACCOUNT;
				AccountingInvoice ai = (AccountingInvoice) wrapper; 
				AonAccountBox account = new AonAccountBox(options.getOccam());
				final LinkedList<Account> suggestedAccounts = new LinkedList<>();
				if (ai.getVats() != null && ai.getVats().size() > 0) {
					InvoiceVAT vat = ai.getVats().get(0);
					account.setAccount(vat.getExpAccount().orElse(null));
					Account oldAccount = vat.getExpAccount().orElse(null);
					suggestedAccounts.add(oldAccount);	
				}
				account.addSelectionHandler(event -> {
					if (event.getSelectedItem() != null) {
						if (suggestedAccounts .size() > 1) {
							suggestedAccounts.set(1,  event.getSelectedItem());
						} else {
							suggestedAccounts.add(event.getSelectedItem());
						}
						ai.setSuggestedAccounts(suggestedAccounts);
					}
				});
				return getRow( update )
					.addCell(account)
					.addCell( getOkButton(update, wrapper));
			}
			return null;
		}
		
		private AonTableButton getToggleButton(AccountEntryUpdate update, IAccountEntryWrapper wrapper, boolean value ) {
			AonTableButton toggleButton = new AonTableButton( AON.MSG.accept() , value?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff());
			toggleButton.addClickHandler(event -> {
				toggleButton.setEnabled(false);
				AonConfirmDialog acd = new AonConfirmDialog();
				acd.confirm("Confirma \"" + update.getDescription()+"\"?", new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
						toggleButton.setEnabled(true);
					}
					
					@Override
					public void onAccept() {
						acceptChange( wrapper, update);
					}
				});
			});
			return toggleButton;
		}
		
		private void acceptChange(IAccountEntryWrapper wrapper, AccountEntryUpdate update) {
			ACCOUNT_ENTRY_SERVICE.updateSpecial(options.getOccam(), update, wrapper
				, new AsyncCallback<IAccountEntryWrapper>() {
					@Override
					public void onSuccess(IAccountEntryWrapper result) {
						callback.onSuccess(result);
					}
						
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});							
		}
		
		private AonTableButton getOkButton(AccountEntryUpdate update, IAccountEntryWrapper wrapper) {
			AonTableButton okButton = new AonTableButton( AON.MSG.accept() , AON.CSS.aonIconSave());
			okButton.addClickHandler(event -> {
				okButton.setEnabled(false);
				AonConfirmDialog acd = new AonConfirmDialog();
				acd.confirm("Confirma \"" + update.getDescription()+"\"?", new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
						okButton.setEnabled(true);
					}
					
					@Override
					public void onAccept() {
						acceptChange( wrapper , update );
					}
				});
			});
			return okButton;
		}
	}

	  
}
