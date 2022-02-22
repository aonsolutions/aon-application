package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class JsSalaryIrpfBreakdownGridPanel extends FlowPanel {
	
	private final Label title;
	private final Label subTitle;
	private final AonDisplayGrid grid;
	private double sumBase = 0.0;
	private double sumQuota = 0.0;
	
	public JsSalaryIrpfBreakdownGridPanel() {
		setStyleName(AON.CSS.aonBackgroundLigthGray());
		
		title = new Label();
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonWidthAll());
		title.addStyleName(AON.CSS.aonTextCenter());
		title.addStyleName(AON.CSS.aonTextUppercase());
		add( title );
		subTitle = new Label();	
		subTitle.setStyleName(AON.CSS.aonMarginTop());
		subTitle.addStyleName(AON.CSS.aonBold());
		subTitle.addStyleName(AON.CSS.aonWidthAll());
		subTitle.addStyleName(AON.CSS.aonTextCenter());
		add( subTitle );
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonFontSmaller());
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
			.addCell(new Label("Fecha"),AON.CSS.aonWidth80())
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
		grid.addRow()
			.addCell(new Label(ensure(br.getRegistryDocument(), br::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getRegistryName(), () -> AonStringUtils.abbreviate(br.getRegistryName(),25), AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getIssueDate(), () -> AON.DATE_FORMAT.format(br.getIssueDate()), AonStringUtils.EMPTY)))
			.addCell(new Label())			
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getBase())),AON.CSS.aonTextRight())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getQuota())),AON.CSS.aonTextRight())
		;
		sumBase += br.getBase();
		sumQuota += br.getQuota();
	}

	public void addFooterRow() {
		grid.addFooterRow()
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label(AON.MSG.total()),AON.CSS.aonTextRight(),AON.CSS.aonBold(),AON.CSS.aonTextUppercase())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumBase)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumQuota)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
		;
	}
	public void setReportTitle( String title) {
		this.title.setText(title);
	}

}
