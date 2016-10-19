package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoicePanel.IInvoicePanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

public class InvoiceVATPanel extends ScrollPanel implements HasValueChangeHandlers<InvoiceVAT>, HasSelectionHandlers<Account> {
	
	private FlowPanel container;
	private FlexTable tab;
	private IInvoicePanelCallback callback;
	private LinkedList<InvoicePanelRow> rows;
	private LinkedList<Account> suggestedAccounts;
	
	private Label reLabel;
	private Label reQuotaLabel;
	private Label investAssetLabel;
	private Label dedPercentLabel;
	private Label dedQuotaLabel;
	private Label adjAccountLabel;
	private Label inputVatLabel;
	private Label outputVatLabel;
	private Label withholdingLabel;
	
	
	private class InvestAssetListBox extends ListBox {
		private InvestAssetListBox() {
			setWidth("90px");
			addItem("------",(String) null);
			if (callback.isInvestAssetsAvailable()) {
				for (InvestAsset asset : callback.getConfiguration().getInvestAssets()) {
					addItem(asset.getDescription(),AonNumberUtils.toString(asset.getId()));
				}
			}
		}
		
		private InvestAsset getValue() {
			if (getSelectedIndex() == 0) return null;
			return callback.getConfiguration().getInvestAssets().get(getSelectedIndex() -1 );
		}

		public void setValue(Integer investAsset) {
			if (investAsset == null) setSelectedIndex(0);
			int i = 1;
			for (InvestAsset asset : callback.getConfiguration().getInvestAssets()) {
				if (AonNumberUtils.equals(asset.getId(),investAsset)) {
					setSelectedIndex(i);		
				}
				i++;
			}
		}
		
	}
	
	public InvoiceVATPanel(IInvoicePanelCallback callback) {
		setStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonWidthAll());
		
		setCallback(callback);
		container = new FlowPanel();
		add(container);
	}
	
	public void setCallback(IInvoicePanelCallback invoiceCallback) {
		this.callback = invoiceCallback;
	}

	public void setSuggestedAccounts(LinkedList<Account> suggestedAccounts) {
		this.suggestedAccounts = suggestedAccounts;
	}

	void paint() {
		container.clear();
		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		container.add(tab);
		
		paintHeader();
		paintRows();
		paintButtons();
	}

	private void paintHeader() {
		int row = 0;
		int col = 0;
		
		Label label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		tab.getCellFormatter().setWidth(row, col, "1%");
		++col;
		label = new Label(AON.MSG.taxableBaseAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		label = new Label("% IVA");
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		label = new Label(AON.MSG.vatQuota());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, label);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		reLabel = new Label("% RE");
		reLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, reLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		reQuotaLabel = new Label(AON.MSG.surchargeQuota());
		reQuotaLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, reQuotaLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		
		investAssetLabel = new Label(AON.MSG.actInvestAsset());
		investAssetLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, investAssetLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		dedPercentLabel = new Label(AON.MSG.dedPercent());
		dedPercentLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, dedPercentLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		dedQuotaLabel = new Label(AON.MSG.dedQuota());
		dedQuotaLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, dedQuotaLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		adjAccountLabel = new Label(AON.MSG.adjAccount());
		adjAccountLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, adjAccountLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		++col;
		inputVatLabel = new Label(AON.MSG.inputVatAccount());
		inputVatLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, inputVatLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		++col;
		outputVatLabel = new Label(AON.MSG.outputVatAccount());
		outputVatLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, outputVatLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		++col;
		withholdingLabel = new Label("IRPF");
		withholdingLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, withholdingLabel);
		tab.getCellFormatter().setWidth(row, col, "1%");
		++col;

		label = new Label();
		tab.setWidget(row, col, label);
		tab.getCellFormatter().setWidth(row, col, "auto");
		++col;
	}
	
	private void paintButtons() {
		FlowPanel panel = new FlowPanel();

		Button addButton = new Button();
		addButton.setAccessKey( 'L' );
		addButton.setStyleName(AON.AON_CSS.aonIconReset());
		addButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = callback.getInvoice().getVats().size() - 1;
				InvoiceVAT last = callback.getInvoice().getVats().get( idx );
				InvoiceVAT vat = new InvoiceVAT()
					.setExpAccountId(last.getExpAccountId())
					.setExpAccountCode(last.getExpAccountCode())
					.setExpAccountDescription(last.getExpAccountDescription())
					.setPercentage(last.getPercentage())
					.setSurcharge(last.getSurcharge())
					.setWithholding(last.isWithholding())
					.setInputAccountId(last.getInputAccountId())
					.setInputAccountCode(last.getInputAccountCode())
					.setInputAccountDescription(last.getInputAccountDescription())
					.setOutputAccountId(last.getOutputAccountId())
					.setOutputAccountCode(last.getOutputAccountCode())
					.setOutputAccountDescription(last.getOutputAccountDescription());
				callback.getInvoice().addVat( vat );
				addRow(vat, true);
			}
		});
		panel.add(addButton);
		
		Button saveButton = new Button();
		saveButton.setTitle( AON.MSG.saveAction() );
		saveButton.setAccessKey( 'L' );
		saveButton.setStyleName(AON.AON_CSS.aonIconSave());
		saveButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		saveButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				callback.save(event);
			}
		});
		panel.add(saveButton);
		
		container.add(panel);
	}
	
	private void paintRows() {
		rows = new LinkedList<InvoicePanelRow>();
		if (callback.getInvoice().getVats() != null && callback.getInvoice().getVats().size() > 0) {
			for (InvoiceVAT vat : callback.getInvoice().getVats()) {
				addRow(vat, false);
			}
		} 
	}

	private void addRow(InvoiceVAT vat, boolean focus) {
		if (suggestedAccounts != null && !suggestedAccounts.isEmpty() && suggestedAccounts.size() > rows.size()) {
			Account a = suggestedAccounts.get(rows.size());
			vat.setExpAccountId(a.getId());
			vat.setExpAccountCode(a.getCode());
			vat.setExpAccountDescription(a.getDescription());
		}
		InvoicePanelRow invoiceRow = new InvoicePanelRow(vat, tab, focus);
		rows.add(invoiceRow);
		if (rows.size() > 1) {
			callback.enableInvoiceTotal( false );
		}
	}

	public void surchargeChanged(boolean surcharge) {
		for (InvoicePanelRow row : rows) {
			row.enableSurcharge(surcharge);
		}
	}
	

	private class InvoicePanelRow {
		private final AccountBox expAccount = new AccountBox(AccountEntryModule.getCurrentDomainName(), AccountEntryModule.getCurrentDomain(), false);
		private final DoubleBox taxableBase = new DoubleBox(8);
		private final DoubleBox vatPercent = new DoubleBox(6);
		private final DoubleBox vatQuota = new DoubleBox(8);
		private final DoubleBox surchargePercent = new DoubleBox(6);
		private final DoubleBox surchargeQuota = new DoubleBox(8);
		private final AccountBox inputVatAccount = new AccountBox(AccountEntryModule.getCurrentDomainName(), AccountEntryModule.getCurrentDomain(), false);
		private final AccountBox outputVatAccount = new AccountBox(AccountEntryModule.getCurrentDomainName(), AccountEntryModule.getCurrentDomain(), false);
		private final CheckBox withholding  = new CheckBox();
		private final InvestAssetListBox investAsset = new InvestAssetListBox(); 
		private final DoubleBox dedPercent = new DoubleBox(6);
		private final DoubleBox dedQuota = new DoubleBox(8);
		private final AccountBox adjAccount = new AccountBox(AccountEntryModule.getCurrentDomainName(), AccountEntryModule.getCurrentDomain(), false);
		
		private InvoicePanelRow(final InvoiceVAT vat, FlexTable tab, boolean focus) {
			int currentRow = tab.getRowCount();
			int col = 0;
			expAccount.setValue(vat.getExpAccountId(),vat.getExpAccountCode(),vat.getExpAccountDescription(),true);
			if (vat.getExpAccountId() != null) {
				SelectionEvent.<Account>fire(InvoiceVATPanel.this, 
						new Account()
							.setId(vat.getExpAccountId())
							.setCode(vat.getExpAccountCode())
							.setDescription(vat.getExpAccountDescription()) );
			}
			expAccount.addSelectionHandler( new SelectionHandler<Account>() {
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					if (a != null) {
						vat.setExpAccountId(a.getId());
						vat.setExpAccountCode(a.getCode());
						vat.setExpAccountDescription(a.getDescription());
					} else {
						vat.setExpAccountId(null);
						vat.setExpAccountCode(null);
						vat.setExpAccountDescription(null);
					}
					SelectionEvent.<Account>fire(InvoiceVATPanel.this, a);
				}
			});
			tab.setWidget(currentRow, col, expAccount);
			++col;

			taxableBase.setStyleName(AON.AON_CSS.aonInputText());
			taxableBase.addStyleName(AON.AON_CSS.aonTextRight());
			taxableBase.setValue(vat.getBase());
			taxableBase.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setBase( event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, taxableBase);
			++col;

			vatPercent.setStyleName(AON.AON_CSS.aonInputText());
			vatPercent.addStyleName(AON.AON_CSS.aonTextRight());
			vatPercent.addStyleName(AON.AON_CSS.aonWidth40());
			vatPercent.setValue(vat.getPercentage());
			vatPercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setPercentage( event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, vatPercent);
			++col;
			
			vatQuota.setStyleName(AON.AON_CSS.aonInputText());
			vatQuota.addStyleName(AON.AON_CSS.aonTextRight());
			vatQuota.setValue(vat.getQuota());
			vatQuota.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setQuota(event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, vatQuota);
			++col;
			
			surchargePercent.setStyleName(AON.AON_CSS.aonInputText());
			surchargePercent.addStyleName(AON.AON_CSS.aonTextRight());
			surchargePercent.setValue(vat.getSurcharge());
			surchargePercent.setVisible(callback.getInvoice().isSurcharge());
			reLabel.setVisible(callback.getInvoice().isSurcharge());
			surchargePercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setSurcharge(event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, surchargePercent);
			++col;
			
			surchargeQuota.setStyleName(AON.AON_CSS.aonInputText());
			surchargeQuota.addStyleName(AON.AON_CSS.aonTextRight());
			surchargeQuota.setValue(vat.getSurchargeQuota());
			surchargeQuota.setVisible(callback.getInvoice().isSurcharge());
			reQuotaLabel.setVisible(callback.getInvoice().isSurcharge());
			surchargeQuota.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setSurchargeQuota(event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, surchargeQuota);
			++col;
			
			
			investAsset.setValue(vat.getInvestAsset());
			investAsset.setVisible(callback.isInvestAssetsAvailable());
			investAssetLabel.setVisible(callback.isInvestAssetsAvailable());
			investAsset.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					if (investAsset.getValue() == null) {
						vat.setInvestAsset( null );
						vat.setDeductiblePercent(100.0);
					} else {
						vat.setInvestAsset( investAsset.getValue().getId() );
						vat.setDeductiblePercent(investAsset.getValue().getPercent());
					}
					dedPercent.setValue(vat.getDeductiblePercent(),false);
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, investAsset);
			++col;
			
			dedPercent.setStyleName(AON.AON_CSS.aonInputText());
			dedPercent.addStyleName(AON.AON_CSS.aonTextRight());
			dedPercent.addStyleName(AON.AON_CSS.aonWidth50());
			dedPercent.setValue(vat.getDeductiblePercent());
			dedPercent.setVisible(callback.isInvestAssetsAvailable());
			dedPercentLabel.setVisible(callback.isInvestAssetsAvailable());
			dedPercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setDeductiblePercent( event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, dedPercent);
			++col;
			
			dedQuota.setStyleName(AON.AON_CSS.aonInputText());
			dedQuota.addStyleName(AON.AON_CSS.aonTextRight());
			dedQuota.setValue(vat.getDeductibleQuota());
			dedQuota.setVisible(callback.isInvestAssetsAvailable());
			dedQuotaLabel.setVisible(callback.isInvestAssetsAvailable());
			dedQuota.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setDeductibleQuota(event.getValue() );
					calculate(vat);
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, dedQuota);
			++col;

			adjAccount.setValue(vat.getAdjAccountId(),vat.getAdjAccountCode()
					,vat.getAdjAccountDescription(),true);
			adjAccount.setVisible(callback.isInvestAssetsAvailable());
			adjAccountLabel.setVisible(callback.isInvestAssetsAvailable());
			adjAccount.addSelectionHandler( new SelectionHandler<Account>() {
				
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					if (a != null) {
						vat.setAdjAccountId(a.getId());
						vat.setAdjAccountCode(a.getCode());
						vat.setAdjAccountDescription(a.getDescription());
					} else {
						vat.setAdjAccountId(null);
						vat.setAdjAccountCode(null);
						vat.setAdjAccountDescription(null);
					}
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, adjAccount);
			++col;

			
			inputVatAccount.setValue(vat.getInputAccountId(),vat.getInputAccountCode()
					,vat.getInputAccountDescription(),true);
			inputVatAccount.setVisible(callback.getInvoice().isInputVatEnabled());
			inputVatLabel.setVisible(callback.getInvoice().isInputVatEnabled());
			inputVatAccount.addSelectionHandler( new SelectionHandler<Account>() {
				
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					vat.setInputAccountId(a.getId());
					vat.setInputAccountCode(a.getCode());
					vat.setInputAccountDescription(a.getDescription());
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, inputVatAccount);
			++col;

			outputVatAccount.setValue(vat.getOutputAccountId(),vat.getOutputAccountCode()
					,vat.getOutputAccountDescription(),true);
			outputVatAccount.setVisible(callback.getInvoice().isOutputVatEnabled());
			outputVatLabel.setVisible(callback.getInvoice().isOutputVatEnabled());
			outputVatAccount.addSelectionHandler( new SelectionHandler<Account>() {
				
				@Override
				public void onSelection(SelectionEvent<Account> event) {
					Account a = event.getSelectedItem();
					if (a!=null) {
						vat.setOutputAccountId(a.getId());
						vat.setOutputAccountCode(a.getCode());
						vat.setOutputAccountDescription(a.getDescription());
					} else {
						vat.setOutputAccountId(null);
						vat.setOutputAccountCode(null);
						vat.setOutputAccountDescription(null);
					}
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, outputVatAccount);
			++col;
			
			withholding.setValue(vat.isWithholding());
			withholding.setVisible(callback.getInvoice().isWithholding());
			withholdingLabel.setVisible(callback.getInvoice().isWithholding());
			withholding.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					vat.setWithholding(withholding.getValue());
					ValueChangeEvent.fire(InvoiceVATPanel.this, vat );
				}
			});
			tab.setWidget(currentRow, col, withholding);
			++col;
			
			tab.setWidget(currentRow, col, new Label());
			tab.getCellFormatter().setWidth(currentRow, col, "auto");
			++col;
			if (focus) {
				taxableBase.setFocus(true);
				taxableBase.selectAll();
			}
		}

		public void enableSurcharge(boolean enabled) {
			surchargePercent.setValue(0.0,true);
			surchargePercent.setVisible(enabled);
			reLabel.setVisible(enabled);
			surchargeQuota.setValue(0.0,true);
			surchargeQuota.setVisible(enabled);
			reQuotaLabel.setVisible(enabled);
			
			investAssetLabel.setVisible(callback.isInvestAssetsAvailable());
			investAsset.setVisible(callback.isInvestAssetsAvailable());
			dedPercentLabel.setVisible(callback.isInvestAssetsAvailable());
			dedPercent.setVisible(callback.isInvestAssetsAvailable());
			dedPercent.setValue(100.0,true);
			dedQuotaLabel.setVisible(callback.isInvestAssetsAvailable());
			dedQuota.setVisible(callback.isInvestAssetsAvailable());
			adjAccountLabel.setVisible(callback.isInvestAssetsAvailable());
			adjAccount.setVisible(callback.isInvestAssetsAvailable());
		}
		
		public void enableInputVat(boolean enabled) {
			inputVatAccount.setVisible(enabled);
			inputVatLabel.setVisible(enabled);
		}
		public void enableOutputVat(boolean enabled) {
			outputVatAccount.setVisible(enabled);
			outputVatLabel.setVisible(enabled);
		}
		public void enableWithholding(boolean enabled) {
			withholding.setVisible(enabled);
			withholdingLabel.setVisible(enabled);
		}

		public void invoiceTotalChanged(InvoiceVAT vat,Double total) {
			double vatPerc = vatPercent.getValue();
			double surchargePerc = 0.0;
			double withHoldingPerc = 0.0;
			if (callback.getInvoice().isWithholding()) {
				withHoldingPerc = callback.getInvoice().getWithholdingData().getPercentage();
			}
			if (callback.getInvoice().isSurcharge()) {
				surchargePerc = surchargePercent.getValue(); 	
			}
			double tb = (total * 100) / (100 + vatPerc + surchargePerc - withHoldingPerc);
			taxableBase.setValue(tb,true);
			calculate(vat);
		}
		
		private void calculate(InvoiceVAT vat) {
			vat.setQuota(AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 ));
			vat.setSurchargeQuota( callback.getInvoice().isSurcharge()
					?AonMathUtils.round(vat.getBase() * vat.getSurcharge() / 100 )
					:0.0);
			vat.setDeductiblePercent(vat.getInvestAsset() != null
					?vat.getDeductiblePercent()
					:100.0);
			vat.setDeductibleQuota(vat.getInvestAsset() != null
					?AonMathUtils.round( vat.getQuota() * vat.getDeductiblePercent() / 100 )
					:vat.getQuota());
			vatQuota.setValue( vat.getQuota() , false);
			surchargeQuota.setValue( vat.getSurchargeQuota() , false);
			dedPercent.setValue( vat.getDeductiblePercent() , false);
			dedQuota.setValue( vat.getDeductibleQuota() , false);
			callback.invoiceTotalChanged();
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceVAT> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void invoiceTotalChanged(Double total) {
		if (rows != null &&  rows.size() == 1) {
			rows.get(0).invoiceTotalChanged(callback.getInvoice().getVats().get(0),total);
		}
	}
	public void withholdingChanged() {
		for (InvoicePanelRow row : rows) {
			row.enableWithholding(callback.getInvoice().isWithholding());
		}
	}
	
	public void transactionChanged() {
		for (InvoicePanelRow row : rows) {
			row.enableInputVat(callback.getInvoice().isInputVatEnabled());
			row.enableOutputVat(callback.getInvoice().isOutputVatEnabled());
		}
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
}
