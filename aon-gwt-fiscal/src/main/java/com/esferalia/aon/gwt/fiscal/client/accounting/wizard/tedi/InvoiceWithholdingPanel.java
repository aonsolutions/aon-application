package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class InvoiceWithholdingPanel extends SimplePanel implements HasValueChangeHandlers<InvoiceWithholding>, HasSelectionHandlers<Account>, Focusable {
	
	private ListBox withholdingTaxs;
	private AonDoubleBox withholdingBase;
	private AonDoubleBox withholdingPercent;
	private AonDoubleBox withholdingQuota;
	private WithholdingTypeListBox withholdingType;
	private AonAccountBox withholdingAccount;
	
	public InvoiceWithholdingPanel(IInvoicePanelCallback callback) {
		AccountingInvoice ai = callback.getInvoice();
		
		setStyleName(AON.CSS.aonDisplayTable());
		addStyleName(AON.CSS.aonWidthAll());
		addStyleName(AON.CSS.aonMarginTopSep());
		getElement().getStyle().setBackgroundColor(EditableInvoicePanel.INNER_BACKGROUND_COLOR);

		FlowPanel withholdingTableRowDiv = new FlowPanel();
		withholdingTableRowDiv.setStyleName(AON.CSS.aonDisplayTableRow());
		add(withholdingTableRowDiv);
		
		FlowPanel withholdingTableCellDiv0 = new FlowPanel();
		withholdingTableCellDiv0.setStyleName(AON.CSS.aonDisplayTableCell());
		withholdingTableCellDiv0.addStyleName(AON.CSS.aonTextVerticalContainer());
		withholdingTableCellDiv0.getElement().getStyle().setWidth(40, Unit.PX);
		withholdingTableCellDiv0.getElement().getStyle().setBackgroundColor(EditableInvoicePanel.LABEL_BACKGROUND_COLOR);
		withholdingTableCellDiv0.getElement().getStyle().setBorderColor("DarkGray");
		withholdingTableCellDiv0.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		withholdingTableCellDiv0.getElement().getStyle().setBorderWidth(1, Unit.PX);
		withholdingTableCellDiv0.getElement().getStyle().setProperty("border-radius", 10, Unit.PCT);
		withholdingTableRowDiv.add(withholdingTableCellDiv0);
		
		Label withholdingDescription = new Label("IRPF");
		withholdingDescription.setStyleName(AON.CSS.aonTextVertical());
		withholdingDescription.addStyleName(AON.CSS.aonBold());
		withholdingTableCellDiv0.add(withholdingDescription);
		
		FlowPanel withholdingPanel = new FlowPanel();
		withholdingPanel.setStyleName(AON.CSS.aonDisplayTableCell());
		withholdingTableRowDiv.add(withholdingPanel);
		
		FlowPanel dataTable = new FlowPanel();
		dataTable.setStyleName(AON.CSS.aonDisplayGrid());
		dataTable.addStyleName(AON.CSS.aonWidthAlmostAll());
		dataTable.addStyleName(AON.CSS.aonMarginTopSep());
		dataTable.addStyleName(AON.CSS.aonBlockCenter());
		withholdingPanel.add(dataTable);
		
		FlowPanel headerRow = new FlowPanel();
		headerRow.setStyleName(AON.CSS.aonDisplayGridHeaderRow());
		dataTable.add(headerRow);
		
		headerRow.add(getHeaderCell(new Label()));
		headerRow.add(getHeaderCell(new Label(AON.MSG.taxableBaseAbr())));
		headerRow.add(getHeaderCell(new InlineLabel(AonStringUtils.PERCENT)));
		headerRow.add(getHeaderCell(new InlineLabel(AON.MSG.quota())));
		headerRow.add(getHeaderCell(new InlineLabel(AON.MSG.accountAbr())));
		headerRow.add(getHeaderCell(new InlineLabel(AON.MSG.type())));
		Label l = new Label("");
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonDisplayTableCell());
		cell.addStyleName(AON.CSS.aonWidthAll());
		cell.add(l);
		headerRow.add(cell);
		
		FlowPanel dataRow = new FlowPanel();
		dataRow.setStyleName(AON.CSS.aonDisplayGridRow());
		dataTable.add(dataRow);
		
		if (callback.getConfiguration().getWithholdingTaxes() != null 
			&& callback.getConfiguration().getWithholdingTaxes().size() > 0) {
			withholdingTaxs = new ListBox();
			withholdingTaxs.addStyleName(AON.CSS.aonMarginLeftSep());
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
			dataRow.add(getCell(withholdingTaxs));
		} else {
			dataRow.add(getCell(new Label()));
		}

		withholdingBase = new AonDoubleBox(8,4);
		withholdingBase.setEnabled(false);
		dataRow.add(getCell(withholdingBase));
		
		withholdingPercent = new AonDoubleBox(6,2);
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
		dataRow.add(getCell(withholdingPercent));
		
		withholdingQuota = new AonDoubleBox(8,2);
		withholdingQuota.setEnabled(false);
		withholdingQuota.setVisibleLength(5);
		dataRow.add(getCell(withholdingQuota));
		
		withholdingAccount = new AonAccountBox(callback.getCurrentDomainName(), callback.getCurrentDomainId(), callback.getCurrentUser(), false);
		withholdingAccount.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				ai.setWithholdingAccount( event.getSelectedItem() );
				SelectionEvent.<Account>fire(InvoiceWithholdingPanel.this, event.getSelectedItem());
			}
		});
		dataRow.add(getCell(withholdingAccount));
		
		withholdingType = new WithholdingTypeListBox();
		withholdingType.addStyleName(AON.AON_CSS.aonMarginLeft5());
		withholdingType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ai.setWithholdingType( withholdingType.getValue() );
				ValueChangeEvent.fire(InvoiceWithholdingPanel.this, ai.getWithholdingData() );
			}
		});
		dataRow.add(getCell(withholdingType));
		
		Label l0 = new Label("");
		FlowPanel cell0 = new FlowPanel();
		cell0.setStyleName(AON.CSS.aonDisplayTableCell());
		cell0.addStyleName(AON.CSS.aonWidthAll());
		cell0.add(l0);
		dataRow.add(cell0);
		
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

	private FlowPanel getHeaderCell(Widget widget) {
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonDisplayGridHeaderCell());
		cell.addStyleName(AON.CSS.aonNowrap());
		cell.add(widget);
		return cell;
	}
	private Widget getCell(Widget widget) {
		FlowPanel cell = new FlowPanel();
		cell.addStyleName(AON.CSS.aonDisplayGridCell());
		cell.add(widget);
		return cell;
	}
	

}
