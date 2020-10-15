package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.widget.AccountingInvoiceBox;
import com.esferalia.aon.occam.api.model.AccountingDUAInfo;
import com.esferalia.aon.occam.api.model.AccountingDUAInvoice;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class InvoiceDUAPanel extends SimplePanel implements HasSelectionHandlers<IInvoicePanelCallback>, Focusable {
	
	private AccountingInvoiceBox duaInvoice;
	private FlexTable tab;
	private TextBox code; 
	private DoubleBox price;
	private DoubleBox adjust;
	private DoubleBox statisticalValue;
	private FlexTable tab47;
	private DoubleBox dutyBase;
	private DoubleBox dutyPercent;
	private DoubleBox dutyTotal;
	private CheckBox authCalc;
	
	public InvoiceDUAPanel(IInvoicePanelCallback callback) {
		code = new TextBox();
		price = new DoubleBox(10);
		adjust = new DoubleBox(8);
		statisticalValue = new DoubleBox(10);
		dutyBase = new DoubleBox(10);
		dutyPercent = new DoubleBox(5);
		dutyTotal = new DoubleBox(10);
		authCalc = new CheckBox( AON.MSG.authomaticCalculation() );

		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonAccountTable());
		tab.getColumnFormatter().setWidth(0, "120px");
		tab.getColumnFormatter().setWidth(1, "120px");
		tab.getColumnFormatter().setWidth(2, "80px");
		tab.getColumnFormatter().setWidth(3, "120px");
		tab.getColumnFormatter().setWidth(4, "120px");
		tab.getColumnFormatter().setWidth(5, "auto");
		tab.setVisible(false);
		
		
		FlexTable duaTab = new FlexTable();
		duaTab.getColumnFormatter().setWidth(0, "40px");
		duaTab.getColumnFormatter().setWidth(1, "130px");
		duaTab.getColumnFormatter().setWidth(2, "auto");
		
		duaTab.setStyleName(AON.AON_CSS.aonAccountTable());
		duaTab.addStyleName(AON.AON_CSS.aonWidthAll());
		duaTab.addStyleName(AON.AON_CSS.aonSimpleBorder());
		duaTab.addStyleName(AON.AON_CSS.aonMarginTop5());
		duaTab.addStyleName(AON.AON_CSS.aonMarginTop5());
		duaTab.getElement().getStyle().setBackgroundColor(EditableInvoicePanel.DUA_BACKGROUND_COLOR);
		
		setWidget(duaTab);
		
		int row = 0;
		int col = 0;
		
		Label duaLabel = new Label("DUA");
		duaLabel.setStyleName(AON.AON_CSS.aonTextVertical());
		duaLabel.addStyleName(AON.AON_CSS.aonBold());
		duaTab.setWidget(row, col, duaLabel);
		duaTab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonSimpleBorder());
		duaTab.getCellFormatter().getElement(row, col).getStyle().setBackgroundColor("LightGreen");
		duaTab.getCellFormatter().getElement(row, col).getStyle().setHeight(40.0, Unit.PX);
		duaTab.getFlexCellFormatter().setRowSpan(0, 0, 2);		
		col++;

		InlineLabel lbl3 = new InlineLabel("Fra. Importaci\u00F3n");
		lbl3.setStyleName(AON.AON_CSS.aonInnerLabel());
		duaTab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		duaTab.setWidget(row, col, lbl3);
		col++;
		
		duaInvoice = new AccountingInvoiceBox(callback.getCurrentDomainName(),callback.getCurrentDomainId(),callback.getCurrentUser(),callback.getConfiguration());
		duaInvoice.addSelectionHandler(new SelectionHandler<AccountingInvoice>() {

			@Override
			public void onSelection(SelectionEvent<AccountingInvoice> event) {
				AccountingInvoice extInvoice = event.getSelectedItem();
				duaInvoiceSelected( callback, extInvoice );
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);				
			}
			
		});
		duaTab.setWidget(row, col, duaInvoice);
		col++;
		
		row++;
		col=0;
		duaTab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		duaTab.getFlexCellFormatter().setColSpan(row, col, 2);
		duaTab.setWidget(row, col, tab);
		

		row=0;
		col=0;

		InlineLabel dateLbl = new InlineLabel(AON.MSG.date());
		dateLbl.setStyleName(AON.AON_CSS.aonInnerLabel());
		dateLbl.addStyleName(AON.AON_CSS.aonMarginRight());
		tab.setWidget(row, col, dateLbl);
		col++;
		

		DateBoxEx  date = new DateBoxEx();
		date.addStyleName(AON.AON_CSS.aonMarginRight());
		date.setValue(callback.getInvoice().getInvoice().getIssueDate());
		date.setEnabled(false);
		tab.setWidget(row, col, date);
		col++;
		
		InlineLabel codeLbl = new InlineLabel("[A] " + AON.MSG.duaNumber());
		codeLbl.setStyleName(AON.AON_CSS.aonInnerLabel());
		codeLbl.addStyleName(AON.AON_CSS.aonMarginRight());
		tab.setWidget(row, col, codeLbl);
		col++;
		
		code.setStyleName(AON.AON_CSS.aonInputText());
		code.setVisibleLength(18);
		code.setMaxLength(18);
		code.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.getInvoice().getDuaInvoice().getInfo().setCode(code.getValue() );
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});
		tab.setWidget(row, col, code);
		tab.getFlexCellFormatter().setColSpan(row, col, 2);
		col++;
		
		authCalc.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AccountingDUAInfo info = callback.getInvoice().getDuaInvoice().getInfo();
				info.setAuthCalcEnabled(authCalc.getValue());
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});

		
		tab.setWidget(row, col, authCalc);
		
		row++;
		col=0;
		
		InlineLabel priceLbl = new InlineLabel("[42] " + AON.MSG.productPrice());
		priceLbl.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		tab.setWidget(row, col, priceLbl);
		col++;
		
		price.addStyleName(AON.AON_CSS.aonMarginRight());
		price.setEnabled(false);
		tab.setWidget(row, col, price);
		col++;
		
		InlineLabel adjustLbl = new InlineLabel("[45] " + AON.MSG.adjust());
		adjustLbl.setStyleName(AON.AON_CSS.aonInnerLabel());
		adjustLbl.addStyleName(AON.AON_CSS.aonMarginRight());
		tab.setWidget(row, col, adjustLbl);
		col++;
		
		adjust.addStyleName(AON.AON_CSS.aonMarginRight());
		adjust.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				callback.getInvoice().getDuaInvoice().getInfo().setAdjust( adjust.getValue() == null? 0 : adjust.getValue() );
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});
		tab.setWidget(row, col, adjust);
		col++;
		
		InlineLabel statisticalValueLbl = new InlineLabel("[46] " + AON.MSG.statisticalValue());
		statisticalValueLbl.setStyleName(AON.AON_CSS.aonInnerLabel());
		statisticalValueLbl.addStyleName(AON.AON_CSS.aonMarginRight());
		statisticalValueLbl.addStyleName(AON.AON_CSS.aonBold());
		tab.setWidget(row, col, statisticalValueLbl);
		col++;
		
		statisticalValue.addStyleName(AON.AON_CSS.aonMarginRight());
		statisticalValue.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double p = callback.getInvoice().getDuaInvoice().getInfo().getPrice();
				double sv = statisticalValue.getValue()==null?0.0:statisticalValue.getValue();
				double a = AonMathUtils.round(sv - p);
				callback.getInvoice().getDuaInvoice().getInfo().setAdjust(a);
				if ( !authCalc.isAttached() ) {
					callback.getInvoice().getDuaInvoice().getInfo().setStatisticalValue(sv);	
				}
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});
		tab.setWidget(row, col, statisticalValue);
		col++;
		
		row++;
		col=0;
		
		InlineLabel dutyLbl = new InlineLabel("[47] Tributos ");
		dutyLbl.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		tab.setWidget(row, col, dutyLbl);
		col++;
		
		tab47 = new FlexTable();
		tab47.setStyleName(AON.AON_CSS.aonAccountTable());
		tab47.getColumnFormatter().setWidth(0, "40");
		tab47.getColumnFormatter().setWidth(1, "120");
		tab47.getColumnFormatter().setWidth(2, "60");
		tab47.getColumnFormatter().setWidth(3, "120");
		tab.setWidget(row, col, tab47 );
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		tab.getFlexCellFormatter().setColSpan(row, col, 5);
		
		row = 0;
		col = 0;
		
		Label label = new Label(AON.MSG.type());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab47.setWidget(row, col, label);
		decorateHeader(tab47,row, col);
		++col;
		label = new Label(AON.MSG.taxableBase());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab47.setWidget(row, col, label);
		decorateHeader(tab47,row, col);
		++col;
		label = new Label(AON.MSG.percent());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab47.setWidget(row, col, label);
		decorateHeader(tab47,row, col);
		++col;
		label = new Label(AON.MSG.total());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab47.setWidget(row, col, label);
		decorateHeader(tab47,row, col);
		++col;
		
		row++;
		col = 0;
		
		label = new Label("[AXX] " + AON.MSG.duties());
		tab47.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonBorderBottom());
		tab47.setWidget(row, col, label);
		++col;

		dutyBase.addValueChangeHandler( new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double b = dutyBase.getValue()== null? 0 : dutyBase.getValue();
				callback.getInvoice().getDuaInvoice().getInfo().setDutyBase(b);
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});
		dutyBase.addStyleName(AON.AON_CSS.aonMarginRight());
		tab47.setWidget(row, col, dutyBase);
		++col;
		
		dutyPercent.addValueChangeHandler( new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double p = dutyPercent.getValue() == null? 0 : dutyPercent.getValue();
				callback.getInvoice().getDuaInvoice().getInfo().setDutyPercent(p);
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});
		dutyPercent.addStyleName(AON.AON_CSS.aonMarginRight());
		tab47.setWidget(row, col, dutyPercent);
		++col;
		
		dutyTotal.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double q = event.getValue() == null? 0 : event.getValue();
				callback.getInvoice().getDuaInvoice().getInfo().setDutyTotal(q);
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});
		dutyTotal.addStyleName(AON.AON_CSS.aonMarginRight());
		tab47.setWidget(row, col, dutyTotal);
		
		row++;

		populate(callback);
	}
	
	public void paintVats(IInvoicePanelCallback callback) {
		int row = 2;
		while (tab47.getRowCount() > row) {
			tab47.removeRow(tab47.getRowCount()-1);
		}
		for( InvoiceVAT vat : callback.getInvoice().getDuaInvoice().getInfo().getDuaVats()) {
			int col=0;
			Label label = new Label("[B00] " + AON.MSG.vat());
			tab47.setWidget(row, col, label);
			++col;
			
			DoubleBox vatBase = new DoubleBox(10);
			DoubleBox vatPercent = new DoubleBox(5);
			DoubleBox vatTotal = new DoubleBox(10);
			vatBase.setValue(vat.getBase());
			vatPercent.setValue(vat.getPercentage());
			vatTotal.setValue(vat.getQuota());

			vatBase.addValueChangeHandler( new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setBase(vatBase.getValue()== null? 0 : vatBase.getValue());
					SelectionEvent.fire(InvoiceDUAPanel.this, callback);
				}
			});
			vatBase.addStyleName(AON.AON_CSS.aonMarginRight());
			tab47.setWidget(row, col, vatBase);
			++col;
			
			vatPercent.addValueChangeHandler( new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setPercentage(vatPercent.getValue() == null? 0 : vatPercent.getValue());
					SelectionEvent.fire(InvoiceDUAPanel.this, callback);
				}
			});
			vatPercent.addStyleName(AON.AON_CSS.aonMarginRight());
			tab47.setWidget(row, col, vatPercent);
			++col;
			
			vatTotal.addValueChangeHandler( new ValueChangeHandler<Double>() {
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					vat.setQuota(vatTotal.getValue() == null? 0 : vatTotal.getValue());
					SelectionEvent.fire(InvoiceDUAPanel.this, callback);
				}
			});
			vatTotal.addStyleName(AON.AON_CSS.aonMarginRight());
			tab47.setWidget(row, col, vatTotal);
			row++;
			if ( AonMathUtils.isNotZero( vat.getSurcharge() ) ) {
				col=0;
				
				label = new Label("[B01] " + AON.MSG.re());
				tab47.setWidget(row, col, label);
				++col;
				
				DoubleBox rePercent = new DoubleBox(5);
				DoubleBox reTotal = new DoubleBox(10);
				rePercent.setValue(vat.getSurcharge());
				reTotal.setValue(vat.getSurchargeQuota());

				++col;
				
				rePercent.addValueChangeHandler( new ValueChangeHandler<Double>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<Double> event) {
						vat.setSurcharge(rePercent.getValue() == null? 0 : rePercent.getValue());
						SelectionEvent.fire(InvoiceDUAPanel.this, callback);
					}
				});
				rePercent.addStyleName(AON.AON_CSS.aonMarginRight());
				tab47.setWidget(row, col, rePercent);
				++col;
				
				reTotal.addValueChangeHandler( new ValueChangeHandler<Double>() {
					@Override
					public void onValueChange(ValueChangeEvent<Double> event) {
						vat.setSurchargeQuota(reTotal.getValue() == null? 0 : reTotal.getValue());
						//				AccountingDUAInfo info = callback.getInvoice().getDuaInvoice().getInfo();
						//				info.setVatTotalEdited( AonMathUtils.isNotZero(InvoiceCalculator.getVatTotalGap(info, q)) );
						//				if (info.isVatTotalEdited()) {
						//					vatTotal.setTitle("Importe aranceles modificada. Deber\u00EDa ser: " + InvoiceCalculator.getVatTotal(info));
						//				} else {
						//					vatTotal.setTitle(null);
						//				}
						SelectionEvent.fire(InvoiceDUAPanel.this, callback);
					}
				});
				reTotal.addStyleName(AON.AON_CSS.aonMarginRight());
				tab47.setWidget(row, col, reTotal);
				
				row++;
			}
				
		}
	}

	protected void duaInvoiceSelected(IInvoicePanelCallback callback, AccountingInvoice extInvoice) {
		if (extInvoice == null) {
			tab.setVisible( false );
			callback.getInvoice().setDuaInvoice(null);
		} else {
			tab.setVisible( true );
			AccountingDUAInvoice duaInvoice = new AccountingDUAInvoice();
			duaInvoice.setAccountingInvoice(extInvoice);
			duaInvoice.setInfo(new AccountingDUAInfo()
					.setPrice(duaInvoice.getAccountingInvoice().getTotalInvoice())
					.setStatisticalValue(duaInvoice.getAccountingInvoice().getTotalInvoice())
					.setVatAccount(callback.getConfiguration().getDefaultDUAVatAccount() )
					.setDutyAccount(callback.getConfiguration().getDefaultDUADutyAccount() )
					.setDutyBase(duaInvoice.getAccountingInvoice().getTotalInvoice())
					.setAuthCalcEnabled(true)
			);
			LinkedList<InvoiceVAT> duaVats = new LinkedList<InvoiceVAT>();
			for (InvoiceVAT ori : duaInvoice.getAccountingInvoice().getVats()) {
				duaVats.add(ori.clone());
			}
			duaInvoice.getInfo().setDuaVats(duaVats);
			callback.getInvoice().setDuaInvoice(duaInvoice);
		}
	}

	protected void populate(IInvoicePanelCallback callback) {
		AccountingInvoice extInvoice = null;
		AccountingDUAInfo info = null;
		if (callback.getInvoice().getDuaInvoice() != null) {
			tab.setVisible( true );
			extInvoice = callback.getInvoice().getDuaInvoice().getAccountingInvoice();
			if (callback.getInvoice().getDuaInvoice().getInfo() != null) {
				info = callback.getInvoice().getDuaInvoice().getInfo();
			}
		} else {
			tab.setVisible( false );
			info = new AccountingDUAInfo();	
		}
		duaInvoice.setValue(extInvoice,false);
		duaInvoice.setEnabled(info.getId() == null);
		code.setValue( info.getCode() );
		authCalc.setValue(info.isAuthCalcEnabled());
		price.setValue( info.getPrice(),false, AonNumberUtils.notEquals(price.getValue(), info.getPrice()));
		adjust.setValue( info.getAdjust(),false, AonNumberUtils.notEquals(adjust.getValue(), info.getAdjust()));
		statisticalValue.setValue( info.getStatisticalValue(),false , AonNumberUtils.notEquals(statisticalValue.getValue(), info.getStatisticalValue()));
		dutyBase.setValue(info.getDutyBase(),false, AonNumberUtils.notEquals(dutyBase.getValue(), info.getDutyBase()));				
		dutyPercent.setValue(info.getDutyPercent(),false, AonNumberUtils.notEquals(dutyPercent.getValue(), info.getDutyPercent()));
		dutyTotal.setValue(info.getDutyTotal(),false, AonNumberUtils.notEquals(dutyTotal.getValue(), info.getDutyTotal()));
		if (callback.getInvoice().getDuaInvoice() != null 
			&& callback.getInvoice().getDuaInvoice().getAccountingInvoice() != null
			&& callback.getInvoice().getDuaInvoice().getAccountingInvoice().getVats() != null) {
			paintVats( callback);
		}
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<IInvoicePanelCallback> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	@Override
	public int getTabIndex() {
		return 0;
	}
	@Override
	public void setAccessKey(char key) {
	}
	@Override
	public void setFocus(boolean focused) {
	}
	@Override
	public void setTabIndex(int index) {
	}

	public void initialize(IInvoicePanelCallback callback, AccountingInvoice extInvoice) {
		duaInvoiceSelected(callback, extInvoice);
	}

	private void decorateHeader(FlexTable tab, int row, int col) {
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderTop());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontMedium());
	}
}
