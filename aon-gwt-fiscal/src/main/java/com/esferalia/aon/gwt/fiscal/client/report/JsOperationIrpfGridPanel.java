package com.esferalia.aon.gwt.fiscal.client.report;

import java.util.Map;
import java.util.TreeMap;
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

public class JsOperationIrpfGridPanel extends FlowPanel implements HasSelectionHandlers<JsOperationBreakdown>{
	
	private final Label title;
	private final Label subTitle;
	private final AonDisplayGrid grid;
	private boolean something;
	private double sumBase = 0.0;
	private double sumQuota = 0.0;
	private double sumTotal = 0.0;
	private Map<String,Object[]> mapConceptSummary = new TreeMap<>();
	
	public JsOperationIrpfGridPanel() {
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
			.addCell(new Label("Act."),AON.CSS.aonWidth40())
			.addCell(new Label("Fecha"),AON.CSS.aonWidth80())
			.addCell(new Label("Concepto"),AON.CSS.aonWidth150())
			.addCell(new Label("N\u00BA Documento"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label("Titular"),AON.CSS.aonWidthAuto())
			.addCell(new Label("Base Imp."),AON.CSS.aonTextRight(),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())		
			.addCell(new Label("Impuestos"),AON.CSS.aonTextRight(),AON.CSS.aonWidth100())
			.addCell(new Label("Total"),AON.CSS.aonTextRight(),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
		;
	}
	
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}
	
	public void addRow(JsOperationBreakdown br) {
		if (!something) {
			something = true;
			paintHeader();			
		}
		AonDisplayGridRow row = grid.addRow();
		row.addClickHandler(event -> SelectionEvent.fire(this, br));
		row .addCell(new Label(ensure(br.getEpigraph(), br::getEpigraph, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getEntryDate(), () -> AON.DATE_FORMAT.format(br.getEntryDate()), AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getConcept(), br::getConcept, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getDocumentNumber(), br::getDocumentNumber, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getRegistryFullName(), br::getRegistryFullName, AonStringUtils.EMPTY)))
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getBase())),AON.CSS.aonTextRight())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getQuota())),AON.CSS.aonTextRight())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getTotal())),AON.CSS.aonTextRight())
		;
		sumBase = sumBase + br.getBase();
		sumQuota = sumQuota + br.getQuota();
		sumTotal = sumTotal + br.getTotal();

		Object[] indexIrpf = mapConceptSummary.get(br.getAccount());								  
		if (indexIrpf != null) {
			Object[] obj = new Object[2];	
			obj[0] = br.getAccountDescription();
			obj[1] = (double) indexIrpf[1] + br.getBase();
			mapConceptSummary.put(br.getAccount(), obj);
		} else {
			Object[] obj = new Object[2];
			obj[0] = br.getAccountDescription();
			obj[1] = br.getBase();
			mapConceptSummary.put(br.getAccount(), obj);
		}
		
	}

	public void addFooterRow() {
		if (!something) {
			Label noDataLabel = new Label(AON.MSG.noData());
			noDataLabel.setStyleName(AON.CSS.aonTextCenter());
			noDataLabel.addStyleName(AON.CSS.aonMarginTop());
			noDataLabel.addStyleName(AON.CSS.aonFontSmall());
			add( noDataLabel );
		} else {
			grid.addFooterRow()
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label(AON.MSG.total()),AON.CSS.aonTextRight(),AON.CSS.aonBold(),AON.CSS.aonTextUppercase())
				.addCell(new Label())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(sumBase)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(sumQuota)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(sumTotal)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
				;
			paintSummary();
		}
	}

	public void setReportTitle( String title) {
		this.title.setText(title);
	}
	private void paintSummary() {
		paintSummary("RESUMEN POR CONCEPTO", mapConceptSummary);
	}
	
	private void paintSummary(String label, Map<String, Object[]> map) {
		Label summaryLabel = new Label(label);
		summaryLabel.setStyleName(AON.CSS.aonTextCenter());
		summaryLabel.addStyleName(AON.CSS.aonMarginTop());
		summaryLabel.addStyleName(AON.CSS.aonFontSmall());
		summaryLabel.addStyleName(AON.CSS.aonBold());
		add( summaryLabel );
		AonDisplayGrid summaryGrid = new AonDisplayGrid();
		summaryGrid.addStyleName(AON.CSS.aonMarginTop());
		summaryGrid.addStyleName(AON.CSS.aonFontSmaller());
		summaryGrid.addStyleName(AON.CSS.aonBlockCenter());
		summaryGrid.addHeaderRow()
			.addCell(new Label("Cuenta"),AON.CSS.aonWidth100())		
			.addCell(new Label("Descripci\u00F3n"),AON.CSS.aonWidthAuto(),AON.CSS.aonNowrap())
			.addCell(new Label("Total"),AON.CSS.aonTextRight(),AON.CSS.aonWidth150())
		;		
		map.entrySet()
			.stream()
			.forEach(entry ->  summaryGrid.addRow()
				.addCell(new Label(entry.getKey()))		
				.addCell(new Label((String) entry.getValue()[0]))
				.addCell(new Label(AON.CURRENCY_FORMAT.format((Double) entry.getValue()[1])),AON.CSS.aonTextRight())
		);
		add( summaryGrid );
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsOperationBreakdown> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
