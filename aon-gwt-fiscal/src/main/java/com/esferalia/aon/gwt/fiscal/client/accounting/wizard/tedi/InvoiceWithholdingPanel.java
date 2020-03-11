package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Label;
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
	
	public InvoiceWithholdingPanel(IInvoicePanelCallback callback) {
		AccountingInvoice ai = callback.getInvoice();
		
		withholdingTable = new FlexTable();
		withholdingTable.getColumnFormatter().setWidth(0, "40px");
		withholdingTable.getColumnFormatter().setWidth(1, "auto");
		
		withholdingTable.setStyleName(AON.AON_CSS.aonAccountTable());
		withholdingTable.addStyleName(AON.AON_CSS.aonSimpleBorder());
		withholdingTable.addStyleName(AON.AON_CSS.aonMarginTop5());
		withholdingTable.getElement().getStyle().setBackgroundColor(EditableInvoicePanel.INNER_BACKGROUND_COLOR);
		
		setWidget(withholdingTable);
		
		int row = 0;
		int col = 0;
		
		Label vtoLabel = new Label("IRPF");
		vtoLabel.setStyleName(AON.AON_CSS.aonTextVertical());
		vtoLabel.addStyleName(AON.AON_CSS.aonBold());
		withholdingTable.setWidget(row, col, vtoLabel);
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonSimpleBorder());
		withholdingTable.getCellFormatter().getElement(row, col).getStyle().setBackgroundColor(EditableInvoicePanel.LABEL_BACKGROUND_COLOR);
		withholdingTable.getCellFormatter().getElement(row, col).getStyle().setHeight(40.0, Unit.PX);
		col++;
		
		if (callback.getConfiguration().getWithholdingTaxes() != null 
			&& callback.getConfiguration().getWithholdingTaxes().size() > 0) {
			withholdingTaxs = new ListBox();
			withholdingTaxs.addStyleName(AON.AON_CSS.aonMarginLeft5());
			withholdingTaxs.setWidth("100px");
			withholdingTaxs.addItem("--------","-1");
			for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
				withholdingTaxs.addItem(tax.getName(),AonNumberUtils.toString( tax.getId()));
			}
			withholdingTaxs.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					for (Tax tax : callback.getConfiguration().getWithholdingTaxes()) {
						if ( AonNumberUtils.equals( AonNumberUtils.toInteger( withholdingTaxs.getSelectedValue()),tax.getId())  ) {
							
							Account taxAccount = ai.isSales()
									?tax.getSalesAccount()
									:tax.getPurchaseAccount();
							if (taxAccount == null) {
								taxAccount = ai.isSales()
									?callback.getConfiguration().getDefaultPaidRetAccount()
									:callback.getConfiguration().getDefaultChargedRetAccount();
							}
							ai.getWithholdingData().setPercentage(tax.getPercentage());
							ai.getWithholdingData().setWithholdingType(tax.getWithholdingType());
							if (taxAccount != null) {
								ai.getWithholdingData().setAccountId(taxAccount.getId());
								ai.getWithholdingData().setAccountCode(taxAccount.getCode());
								ai.getWithholdingData().setAccountDescription(taxAccount.getDescription());
								callback.getModule().onBalance(taxAccount);
							} else {
								ai.getWithholdingData().setAccountId(null);
								ai.getWithholdingData().setAccountCode(null);
								ai.getWithholdingData().setAccountDescription(null);
							}
							ValueChangeEvent.fire(InvoiceWithholdingPanel.this, ai.getWithholdingData() );
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
		withholdingPercent.setVisibleLength(3);
		withholdingPercent.addStyleName(AON.AON_CSS.aonMarginLeft5());
		withholdingPercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				ai.setWithholdingPercent( event.getValue() );
				InvoiceCalculator.calculate(ai);
				setValue(ai.getWithholdingData());
				ValueChangeEvent.fire(InvoiceWithholdingPanel.this, ai.getWithholdingData() );
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
		
		withholdingAccount = new AccountBox(callback.getCurrentDomainName(), callback.getCurrentDomainId(), callback.getCurrentUser(), false);
		withholdingAccount.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				ai.setWithholdingAccount( event.getSelectedItem() );
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
		withholdingType.addStyleName(AON.AON_CSS.aonMarginLeft5());
		withholdingType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ai.setWithholdingType( withholdingType.getValue() );
				ValueChangeEvent.fire(InvoiceWithholdingPanel.this, ai.getWithholdingData() );
			}
		});
		withholdingType.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
//				callback.setFocusOnPayDate();
			}
		});
		
		withholdingTable.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonNowrap());
		withholdingTable.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonWidthAuto());
		withholdingTable.setWidget(row, col, withholdingType);
		col++;
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InvoiceWithholding> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
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
