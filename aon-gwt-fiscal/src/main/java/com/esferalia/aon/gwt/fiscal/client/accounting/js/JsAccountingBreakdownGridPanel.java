package com.esferalia.aon.gwt.fiscal.client.accounting.js;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class JsAccountingBreakdownGridPanel extends FlowPanel implements HasSelectionHandlers<JsAccountingBreakdown>{
	
	private boolean participationInfo;
	private final Label title;
	private final Label subTitle;
	private final AonDisplayGrid grid;
	private double sumDebit = 0.0;
	private double sumCredit = 0.0;
	
	public JsAccountingBreakdownGridPanel() {
		this(false);
	}
	
	public JsAccountingBreakdownGridPanel( boolean participationInfo ) {
		this.participationInfo =  participationInfo;
		title = new Label();
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonWidthAll());
		title.addStyleName(AON.CSS.aonTextCenter());
		title.addStyleName(AON.CSS.aonTextUppercase());
		title.addStyleName(AON.CSS.aonFontLarger());
		add( title );
		subTitle = new Label();	
		subTitle.setStyleName(AON.CSS.aonMarginTop());
		subTitle.addStyleName(AON.CSS.aonBold());
		subTitle.addStyleName(AON.CSS.aonWidthAll());
		subTitle.addStyleName(AON.CSS.aonTextCenter());
		subTitle.addStyleName(AON.CSS.aonFontLarger());
		add( subTitle );
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		paintHeader();
		add( grid );
	}
	
	@Override
	public void setTitle( String title) {
		super.setTitle(title);
		this.title.setText(title);	
	}
	public void setSubTitle( String subTitle) {
		this.subTitle.setText(subTitle);	
	}
	
	private void paintHeader() {
		grid.addHeaderRow()
			.addCell(new Label("Cuenta."),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			.addCell(new Label("Descrip, cuenta."),AON.CSS.aonWidthAuto(), AON.CSS.aonNowrap())
			.addCell(new Label("Epigr."),AON.CSS.aonWidth80())
			.addCell(new Label("Actividad"),AON.CSS.aonWidth200(), AON.CSS.aonNowrap())
			.addCell(new Label("Debe"),AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
			.addCell(new Label("Haber"),AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
			.addCell(new Label("Saldo deudor"),AON.CSS.aonTextRight(),AON.CSS.aonWidth120(), AON.CSS.aonNowrap())
			.addCell(new Label("Saldo acree."),AON.CSS.aonTextRight(),AON.CSS.aonWidth120(), AON.CSS.aonNowrap())
		;
	}
	
	private String ensure(Object nullable, Supplier<String>  supplier) {
		return ensure(nullable, supplier, "---");
	}
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}
	
	
	public final native boolean hasEntryId(int entry ) /*-{
		return !isNaN(entry) && entry != 0;
	}-*/;
	
	public void addRow(JsAccountingBreakdown br) {
		AonDisplayGridRow row = grid.addRow();
		if ( hasEntryId( br.getEntryId() ) ) {
			row.addClickHandler(event -> SelectionEvent.fire(this, br));
		}
		sumDebit += br.getDebit();
		sumCredit += br.getCredit();
		double sumDebitBalance = (sumDebit >= sumCredit)?sumDebit - sumCredit:0;
		double sumCreditBalance = (sumCredit >= sumDebit)?sumCredit - sumDebit:0;
		row
			.addCell(new Label(ensure(br.getAccountCode(), br::getAccountCode, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getAccountDescription(), br::getAccountDescription, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getEpigraph(), br::getEpigraph, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getActivityDescription(), br::getActivityDescription, AonStringUtils.EMPTY)))
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getDebit())),AON.CSS.aonTextRight())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getCredit())),AON.CSS.aonTextRight())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumDebitBalance)),AON.CSS.aonTextRight())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumCreditBalance)),AON.CSS.aonTextRight())
		;
	}

	public void addFooterRow() {
		double sumDebitBalance = (sumDebit >= sumCredit)?sumDebit - sumCredit:0;
		double sumCreditBalance = (sumCredit >= sumDebit)?sumCredit - sumDebit:0;
		grid.addFooterRow()
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumDebit)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumCredit)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumDebitBalance)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumCreditBalance)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
		;
	}
	public void setReportTitle( String title) {
		this.title.setText(title);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsAccountingBreakdown> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
