package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.fiscal.client.widget.AccountingImportInvoiceBox;
import com.esferalia.aon.occam.api.model.AccountingDUAInfo;
import com.esferalia.aon.occam.api.model.AccountingDUAInvoice;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Style.BorderStyle;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class InvoiceDUAPanel extends AonDisplayTable implements HasSelectionHandlers<IInvoicePanelCallback>, Focusable {
	
	private AccountingImportInvoiceBox duaInvoice;
	private AonDisplayTable duaDataTab;
	private AonDisplayTable duaDataTaxTab; 
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

		
		addStyleName(AON.CSS.aonWidthAll());
		addStyleName(AON.CSS.aonMarginTopSep());
		getElement().getStyle().setBackgroundColor(EditableInvoicePanel.DUA_BACKGROUND_COLOR);

		AonDisplayTableRow labelRow = addRow();
		AonDisplayTableCell labelCell = labelRow.addCell();
		labelCell.addStyleName(AON.CSS.aonTextVerticalContainer());
		labelCell.getElement().getStyle().setWidth(40, Unit.PX);
		labelCell.getElement().getStyle().setBackgroundColor("LightGreen");
		labelCell.getElement().getStyle().setBorderColor("Green");
		labelCell.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		labelCell.getElement().getStyle().setBorderWidth(1, Unit.PX);
		labelCell.getElement().getStyle().setProperty("border-radius", 10, Unit.PCT);
		Label duaDescription = new Label("DUA");
		duaDescription.setStyleName(AON.CSS.aonTextVertical());
		duaDescription.addStyleName(AON.CSS.aonBold());
		labelCell.add(duaDescription);

		AonDisplayTable duaTab = new AonDisplayTable();
		duaTab.addStyleName(AON.CSS.aonWidthAll());
		duaTab.addStyleName(AON.CSS.aonMarginTop());
		duaTab.getElement().getStyle().setBackgroundColor(EditableInvoicePanel.DUA_BACKGROUND_COLOR);
		labelRow.addCell( duaTab );
		
		// ************************************************************
		// ***************** FACTURA DE IMPORTACION *******************
		// ************************************************************
		
		FlowPanel invoicePanel = new FlowPanel();
		invoicePanel.setStyleName(AON.CSS.aonNowrap());
		InlineLabel lbl3 = new InlineLabel("Fra. Importaci\u00F3n");
		lbl3.setStyleName(AON.CSS.aonFlexLabel());
		invoicePanel.add(lbl3);
		duaInvoice = new AccountingImportInvoiceBox(callback.getOccam(),callback.getConfiguration());
		duaInvoice.addSelectionHandler(new SelectionHandler<AccountingInvoice>() {
			@Override
			public void onSelection(SelectionEvent<AccountingInvoice> event) {
				AccountingInvoice extInvoice = event.getSelectedItem();
				duaInvoiceSelected( callback, extInvoice );
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);				
			}
			
		});
		invoicePanel.add(duaInvoice);
		duaTab.addRow().addCell(invoicePanel);
		

		// ***********************************************
		// ***************** DATOS DUA *******************
		// ***********************************************
		duaDataTab = new AonDisplayTable();
		duaDataTab.setVisible(false);
		duaTab.addRow().addCell(duaDataTab);
		InlineLabel dateLbl = new InlineLabel(AON.MSG.date());
		dateLbl.setStyleName(AON.CSS.aonFlexLabel());
		
		AonDateBox  date = new AonDateBox();
		date.setValue(callback.getInvoice().getInvoice().getIssueDate());
		date.setEnabled(false);
		
		InlineLabel codeLbl = new InlineLabel("[A] " + AON.MSG.duaNumber());
		codeLbl.setStyleName(AON.CSS.aonFlexLabel());

		code.setStyleName(AON.CSS.aonInputText());
		code.setVisibleLength(18);
		code.setMaxLength(18);
		code.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.getInvoice().getDuaInvoice().getInfo().setCode(code.getValue() );
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});

		authCalc.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AccountingDUAInfo info = callback.getInvoice().getDuaInvoice().getInfo();
				info.setAuthCalcEnabled(authCalc.getValue());
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});

		duaDataTab.addRow()
			.addCell( dateLbl )
			.addCell( date )
			.addCell( codeLbl )
			.addCell( code )
			.addCell( authCalc )
			.addCell( new Label() , AON.CSS.aonFlexGrow1())
			;
		
		InlineLabel priceLbl = new InlineLabel("[42] " + AON.MSG.productPrice());
		priceLbl.setStyleName(AON.CSS.aonFlexLabel());
		
		price.addStyleName(AON.CSS.aonMarginRight());
		price.setEnabled(false);

		InlineLabel adjustLbl = new InlineLabel("[45] " + AON.MSG.adjust());
		adjustLbl.setStyleName(AON.CSS.aonFlexLabel());

		adjust.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				callback.getInvoice().getDuaInvoice().getInfo().setAdjust( adjust.getValue() == null? 0 : adjust.getValue() );
				SelectionEvent.fire(InvoiceDUAPanel.this, callback);
			}
		});

		InlineLabel statisticalValueLbl = new InlineLabel("[46] " + AON.MSG.statisticalValue());
		statisticalValueLbl.setStyleName(AON.CSS.aonFlexLabel());

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

		duaDataTab.addRow()
			.addCell( priceLbl )
			.addCell( price )
			.addCell( adjustLbl )
			.addCell( adjust )
			.addCell( statisticalValueLbl )
			.addCell( statisticalValue , AON.CSS.aonFlexGrow1())
		;
		
		// **********************************************
		// ***************** TRIBUTOS *******************
		// **********************************************
		
		duaDataTaxTab = new AonDisplayTable();
		duaDataTaxTab.setVisible(false);
		duaTab.addRow().addCell(duaDataTaxTab);

		InlineLabel dutyLbl = new InlineLabel("[47] Tributos ");
		dutyLbl.setStyleName(AON.CSS.aonFlexLabel());

		tab47 = new FlexTable();
		tab47.setStyleName(AON.AON_CSS.aonAccountTable());
		tab47.getColumnFormatter().setWidth(0, "120");
		tab47.getColumnFormatter().setWidth(1, "120");
		tab47.getColumnFormatter().setWidth(2, "60");
		tab47.getColumnFormatter().setWidth(3, "120");

		duaDataTaxTab.addRow()
			.addCell(dutyLbl)
			.addCell(tab47, AON.CSS.aonFlexGrow1())
		;
		
		// ** ** **


		int row = 0;
		int col = 0;
		
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
			duaDataTab.setVisible(false);
			duaDataTaxTab.setVisible(false);
			callback.getInvoice().setDuaInvoice(null);
		} else {
			duaDataTab.setVisible(true);
			duaDataTaxTab.setVisible(true);
			AccountingDUAInvoice duaInvoice = new AccountingDUAInvoice();
			duaInvoice.setAccountingInvoice(extInvoice);
			duaInvoice.setInfo(new AccountingDUAInfo()
					.setPrice(duaInvoice.getAccountingInvoice().getTotalInvoice())
					.setStatisticalValue(duaInvoice.getAccountingInvoice().getTotalInvoice())
					.setVatAccount(callback.getConfiguration().accounting().getDefaultDUAVatAccount() )
					.setDutyAccount(callback.getConfiguration().accounting().getDefaultDUADutyAccount() )
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
			duaDataTab.setVisible(true);
			duaDataTaxTab.setVisible(true);
			extInvoice = callback.getInvoice().getDuaInvoice().getAccountingInvoice();
			if (callback.getInvoice().getDuaInvoice().getInfo() != null) {
				info = callback.getInvoice().getDuaInvoice().getInfo();
			}
		} else {
			duaDataTab.setVisible(false);
			duaDataTaxTab.setVisible(false);
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
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonDisplayGridHeaderCell());
		tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonNowrap());
		tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBorderBottom());
	}
}
