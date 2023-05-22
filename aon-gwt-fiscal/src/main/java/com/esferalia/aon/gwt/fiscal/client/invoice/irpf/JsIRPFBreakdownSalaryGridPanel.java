package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import java.util.HashSet;
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

public class JsIRPFBreakdownSalaryGridPanel extends FlowPanel implements HasSelectionHandlers<JsIRPFBreakdown>{
	
	private final HashSet<String> uniquePerceptors;
	private final Label title;
	private final Label subTitle;
	private final AonDisplayGrid grid;
	private int sumPerceptors = 0;
	private double sumBase = 0.0;
	private double sumQuota = 0.0;
	
	public JsIRPFBreakdownSalaryGridPanel() {
		uniquePerceptors = new HashSet<>();
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
			.addCell(new Label("Documento"),AON.CSS.aonWidth100())
			.addCell(new Label("Nombre Empleado."),AON.CSS.aonWidthAuto())
			.addCell(new Label("Fecha emisi\u00F3n"),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Fecha cargo"),AON.CSS.aonWidth120(),AON.CSS.aonWidth120())
			.addCell(new Label("Perceptor"),AON.CSS.aonTextRight(),AON.CSS.aonWidth40())		
			.addCell(new Label("Percepciones."),AON.CSS.aonTextRight(),AON.CSS.aonWidth150())		
			.addCell(new Label("Ret. / Ingr. a Cuenta"),AON.CSS.aonTextRight(),AON.CSS.aonWidth150())
		;
	}
	
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}
	
	public void addRow(JsIRPFBreakdown br) {
		boolean added = uniquePerceptors.add(br.getRegistryDocument());
		AonDisplayGridRow row = grid.addRow();
		row.addClickHandler(event -> SelectionEvent.fire(this, br));
		row .addCell(new Label(ensure(br.getRegistryDocument(), br::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getRegistryName(), () -> AonStringUtils.abbreviate(br.getRegistryName(),25), AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getIssueDate(), () -> AON.DATE_FORMAT.format(br.getIssueDate()), AonStringUtils.EMPTY)),AON.CSS.aonTextCenter())
			.addCell(new Label(ensure(br.getChargeDate(), () -> AON.DATE_FORMAT.format(br.getChargeDate()), AonStringUtils.EMPTY)),AON.CSS.aonTextCenter())
			.addCell(new Label(added?"1":""),AON.CSS.aonTextCenter())			
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getBase())),AON.CSS.aonTextRight())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getQuota())),AON.CSS.aonTextRight())
		;
		sumPerceptors += added?1:0;
		sumBase += br.getBase();
		sumQuota += br.getQuota();
	}

	public void addFooterRow() {
		grid.addFooterRow()
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label(AON.MSG.total()),AON.CSS.aonTextRight(),AON.CSS.aonBold(),AON.CSS.aonTextUppercase())
			.addCell(new Label(AON.FMT_INT.format(sumPerceptors)),AON.CSS.aonTextCenter(),AON.CSS.aonBold())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumBase)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumQuota)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
		;
	}
	public void setReportTitle( String title) {
		this.title.setText(title);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsIRPFBreakdown> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
