package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoicePanel.IInvoicePanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class InvoiceWithholdingPanel extends SimplePanel implements HasValueChangeHandlers<InvoiceWithholding>, HasSelectionHandlers<Account>, Focusable {
	
	private FlexTable withholdingTable;
	private ListBox withholdingTaxs;
	private DoubleBox withholdingBase;
	private DoubleBox withholdingPercent;
	private DoubleBox withholdingQuota;
	private WithholdingTypeListBox withholdingType;
	private AccountBox withholdingAccount;
	
	private IInvoicePanelCallback callback;
	private int tabindex = InvoicePanel.PAY_PANEL_TAB_OFFSET;	
	
	public InvoiceWithholdingPanel(IInvoicePanelCallback callback) {
		setStyleName(AON.AON_CSS.aonWidthAll());
		setCallback(callback);
	}
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceWithholding> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	
	public IInvoicePanelCallback getCallback() {
		return callback;
	}
	public void setCallback(IInvoicePanelCallback invoiceCallback) {
		this.callback = invoiceCallback;
	}
	private AccountingInvoice getWrapper() {
		return callback.getInvoice();
	}
	
	public void paint() {
		
		withholdingTable = new FlexTable();
//		withholdingTable.setVisible((getWrapper().isWithholding()));
		setWidget(withholdingTable);
		withholdingTable.setStyleName(AON.AON_CSS.aonBorderTop());
		withholdingTable.addStyleName(AON.AON_CSS.aonWidthAll());
		
		int row = 0;
		int col = 0;
		
		InlineLabel lbl0 = new InlineLabel(AON.MSG.irpf());
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonFontMedium());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonWidth50());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundWhite());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		withholdingTable.setWidget(row, col, lbl0);
		col++;
		
		if (getCallback().getModule().getConfiguration().getWithholdingTaxes() != null 
			&& getCallback().getModule().getConfiguration().getWithholdingTaxes().size() > 0) {
			withholdingTaxs = new ListBox();
			withholdingTaxs.setTabIndex(++tabindex);
			withholdingTaxs.addStyleName(AON.AON_CSS.aonMarginLeft5());
			withholdingTaxs.setWidth("100px");
			withholdingTaxs.addItem("--------","-1");
			for (Tax tax : getCallback().getModule().getConfiguration().getWithholdingTaxes()) {
				withholdingTaxs.addItem(tax.getName(),AonNumberUtils.toString( tax.getId()));
			}
			withholdingTaxs.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					for (Tax tax : getCallback().getModule().getConfiguration().getWithholdingTaxes()) {
						if ( AonNumberUtils.equals( AonNumberUtils.toInteger( withholdingTaxs.getSelectedValue()),tax.getId())  ) {
							
							Account taxAccount = getWrapper().isSales()
									?tax.getSalesAccount()
									:tax.getPurchaseAccount();
							if (taxAccount == null) {
								taxAccount = getWrapper().isSales()
									?getCallback().getModule().getConfiguration().getDefaultPaidRetAccount()
									:getCallback().getModule().getConfiguration().getDefaultChargedRetAccount();
							}
							getWrapper().getWithholdingData().setPercentage(tax.getPercentage());
							getWrapper().getWithholdingData().setWithholdingType(tax.getWithholdingType());
							if (taxAccount != null) {
								getWrapper().getWithholdingData().setAccountId(taxAccount.getId());
								getWrapper().getWithholdingData().setAccountCode(taxAccount.getCode());
								getWrapper().getWithholdingData().setAccountDescription(taxAccount.getDescription());
								getCallback().getModule().onBalance(taxAccount);
							} else {
								getWrapper().getWithholdingData().setAccountId(null);
								getWrapper().getWithholdingData().setAccountCode(null);
								getWrapper().getWithholdingData().setAccountDescription(null);
							}
							ValueChangeEvent.fire(InvoiceWithholdingPanel.this, getWrapper().getWithholdingData() );
						}
					}
				}
			});
			withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
			withholdingTable.getCellFormatter().setWidth(row, col, "1%");
			withholdingTable.setWidget(row, col, withholdingTaxs);
			col++;
		}
		
		InlineLabel lbl1 = new InlineLabel(AON.MSG.taxableBaseAbr());
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonBold());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		withholdingTable.setWidget(row, col, lbl1);
		col++;
		
		withholdingBase = new DoubleBox(8,4);
		withholdingBase.setEnabled(false);
		withholdingTable.setWidget(row, col, withholdingBase);
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		col++;
		
		InlineLabel lbl2 = new InlineLabel(AonStringUtils.PERCENT);
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonBold());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		withholdingTable.setWidget(row, col, lbl2);
		col++;
		
		withholdingPercent = new DoubleBox(6,2);
		withholdingPercent.setTabIndex(++tabindex);
		withholdingPercent.setVisibleLength(3);
		withholdingPercent.addStyleName(AON.AON_CSS.aonMarginLeft5());
		withholdingPercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setWithholdingPercent( event.getValue() );
				InvoiceCalculator.calculate(getWrapper());
				setValue(getWrapper().getWithholdingData());
				ValueChangeEvent.fire(InvoiceWithholdingPanel.this, getWrapper().getWithholdingData() );
			}
		});
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		withholdingTable.setWidget(row, col, withholdingPercent);
		col++;

		InlineLabel lbl3 = new InlineLabel(AON.MSG.quota());
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonBold());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		withholdingTable.setWidget(row, col, lbl3);
		col++;
		
		withholdingQuota = new DoubleBox(8,2);
		withholdingQuota.setEnabled(false);
		withholdingQuota.setVisibleLength(5);
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		withholdingTable.setWidget(row, col, withholdingQuota);
		col++;
		
		InlineLabel lbl4 = new InlineLabel(AON.MSG.accountAbr());
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonBold());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		withholdingTable.setWidget(row, col, lbl4);
		col++;
		
		withholdingAccount = new AccountBox(callback.getCurrentDomainName()
				, callback.getCurrentDomainId(), false);
		withholdingAccount.setTabIndex(++tabindex);
		withholdingAccount.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setWithholdingAccount( event.getSelectedItem() );
				SelectionEvent.<Account>fire(InvoiceWithholdingPanel.this, event.getSelectedItem());
			}
		});
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		withholdingTable.setWidget(row, col, withholdingAccount);
		col++;

		InlineLabel lbl5 = new InlineLabel(AON.MSG.type());
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonBold());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().setWidth(row, col, "1%");
		withholdingTable.setWidget(row, col, lbl5);
		col++;
		
		withholdingType = new WithholdingTypeListBox();
		withholdingType.setTabIndex(++tabindex);
		withholdingType.addStyleName(AON.AON_CSS.aonMarginLeft5());
		withholdingType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getWrapper().setWithholdingType( withholdingType.getValue() );
				ValueChangeEvent.fire(InvoiceWithholdingPanel.this, getWrapper().getWithholdingData() );
			}
		});
		withholdingType.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				callback.setFocusOnPayDate();
			}
		});
		
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonWidthAuto());
		withholdingTable.setWidget(row, col, withholdingType);
		col++;
	}
	
	public void setValue( InvoiceWithholding data ) {
		if (this.getWidget() != null) {
			withholdingBase.setValue( data.getBase(),false);
			withholdingPercent.setValue( data.getPercentage(),false);
			withholdingQuota.setValue( data.getQuota(),false);
			withholdingType.setValue(data.getWithholdingType());
			withholdingAccount.setValue(data.getAccountId()
					,data.getAccountCode()
					,data.getAccountDescription(),false);
		}
	}
	@Override
	public int getTabIndex() {
		return withholdingTaxs.getTabIndex();
	}
	@Override
	public void setAccessKey(char key) {
		withholdingTaxs.setAccessKey(key);
	}
	@Override
	public void setFocus(boolean focused) {
		withholdingTaxs.setFocus(focused);
	}
	@Override
	public void setTabIndex(int index) {
		withholdingTaxs.setTabIndex(index);
	}
	
}
