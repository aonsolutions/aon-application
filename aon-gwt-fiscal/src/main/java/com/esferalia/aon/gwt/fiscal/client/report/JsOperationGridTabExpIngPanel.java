// FACTURAS EXPEDIDAS / VENTAS E INGRESOS
package com.esferalia.aon.gwt.fiscal.client.report;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridFooterRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.Label;

public class JsOperationGridTabExpIngPanel extends JsOperationGridPanel {
	
	private void paintHeader() {
		
		AonDisplayGridHeaderRow row = getGrid().addHeaderRow()
			.addCell(new Label("Fecha Liq."), AON.CSS.aonWidth80())
			.addCell(new Label("Ep. IAE."), AON.CSS.aonWidth40());
		
		if (getParams().getBookType() != 0) {
			row.addCell(new Label("Concepto Ingreso"), AON.CSS.aonWidth40());
			row.addCell(new Label("Descripci\u00F3n Ingreso"), AON.CSS.aonWidth100());
			row.addCell(new Label("Cuenta Contable"), AON.CSS.aonWidth80());
			row.addCell(new Label("Ingreso Computable"), AON.CSS.aonWidth80());
		}
		
		row.addCell(new Label("Fecha Exp."), AON.CSS.aonWidth80())
			.addCell(new Label("Serie"), AON.CSS.aonWidth60())
			.addCell(new Label("N\u00FAmero Fra."), AON.CSS.aonWidth80())
			.addCell(new Label("NIF Destinatario"), AON.CSS.aonWidth80())
			.addCell(new Label(getParams().getBookType() == 0 ? "Nombre Destinatario" : "Nombre Destinatario / Descripci\u00F3n"), AON.CSS.aonWidthAuto())
			.addCell(new Label("Base Imp."), AON.CSS.aonWidth80())		
			.addCell(new Label("% IVA."), AON.CSS.aonWidth40())
			.addCell(new Label("Cuota IVA."), AON.CSS.aonWidth80())
			.addCell(new Label("% REq."), AON.CSS.aonWidth40())
			.addCell(new Label("Cuota REq."), AON.CSS.aonWidth80())
			.addCell(new Label("Total Fra."), AON.CSS.aonWidth80());
		
		if (getParams().getBookType() != 0) {
			row.addCell(new Label("% Ret."), AON.CSS.aonWidth40())
			   .addCell(new Label("Importe Retenci\u00F3n"), AON.CSS.aonWidth80())
			;
		}
		
		row.addCell(new Label("RECC"), AON.CSS.aonWidth40());
		if (getParams().getBookType() != 1)
			row.addCell(new Label("Cobro RECC"), AON.CSS.aonWidth40());
		row.addCell(new Label("N\u00FAmero Diario"), AON.CSS.aonWidth40());
	}
	
	public void addRow(JsOperationBreakdown br) {
		
		if (!isSomething()) {
			setSomething(true);
			paintHeader();			
		}
		
		AonDisplayGridRow row = getGrid().addRow();
		row.addClickHandler(event -> SelectionEvent.fire(this, br));
		
		addCell(row, br.getTaxDate());
		addCell(row, br.getActivityIAE(), AON.CSS.aonTextCenter());
		
		if (getParams().getBookType() != 0) {
			addCell(row, br.getConceptCode(), AON.CSS.aonTextCenter());
			addCell(row, br.getConceptDescription());
			addCell(row, br.getAccountCode());
			addCell(row, br.getConceptAmount());
		}
		
		addCell(row, br.getEntryDate());
		addCell(row, br.getInvoiceSeries());
		addCell(row, br.getInvoiceNumber());
		addCell(row, br.getDocument());
		addCell(row, br.getName());
		addCell(row, br.getBase());
		addCell(row, br.getPercent());
		addCell(row, br.getQuota());
		addCell(row, br.getSurchargePercent());
		addCell(row, br.getSurchargeQuota());
		addCell(row, br.getTotal());
			
		if (getParams().getBookType() != 0) {
			addCell(row, br.getRetentionPercent());
			addCell(row, br.getRetentionQuota());
		}
		
		addCell(row, br.isInvoiceRECC() ? "S" : "N", AON.CSS.aonTextCenter());
		if (getParams().getBookType() != 1)
			addCell(row, br.getPayAmount());
		addCell(row, br.getEntryJournal());
		
		setSumBase(getSumBase() + br.getBase());
		setSumQuota(getSumQuota() + br.getQuota());
		setSumSurchargeQuota(getSumSurchargeQuota() + br.getSurchargeQuota());
		setSumTotal(getSumTotal() + br.getTotal());
	}

	public void addFooterRow() {
		
		if (!isSomething()) {
			Label noDataLabel = new Label(AON.MSG.noData());
			noDataLabel.setStyleName(AON.CSS.aonTextCenter());
			noDataLabel.addStyleName(AON.CSS.aonMarginTop());
			noDataLabel.addStyleName(AON.CSS.aonFontSmall());
			add( noDataLabel );
		} else {
			AonDisplayGridFooterRow row = getGrid().addFooterRow()
				.addCell(new Label())
				.addCell(new Label());
			
			if (getParams().getBookType() != 0) {
				row.addCell(new Label())
				   .addCell(new Label())
				   .addCell(new Label())
				   .addCell(new Label());
			}
			
			row.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label(AON.MSG.total()),AON.CSS.aonTextRight(),AON.CSS.aonBold(),AON.CSS.aonTextUppercase())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(getSumBase())),AON.CSS.aonTextRight(),AON.CSS.aonBold())
				.addCell(new Label())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(getSumQuota())),AON.CSS.aonTextRight(),AON.CSS.aonBold())
				.addCell(new Label())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(getSumSurchargeQuota())),AON.CSS.aonTextRight(),AON.CSS.aonBold())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(getSumTotal())),AON.CSS.aonTextRight(),AON.CSS.aonBold());
				
			if (getParams().getBookType() != 0) {
				row.addCell(new Label())
				   .addCell(new Label());
			}
			
			row.addCell(new Label()); // RECC
			if (getParams().getBookType() != 1) 
				row.addCell(new Label()); // Cobro RECC
			row.addCell(new Label()); // Número de diario
		}
		
	}

}