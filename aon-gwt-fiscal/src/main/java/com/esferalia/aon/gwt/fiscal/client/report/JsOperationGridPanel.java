package com.esferalia.aon.gwt.fiscal.client.report;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.fiscal.OperationParamsNew;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public abstract class JsOperationGridPanel extends FlowPanel implements HasSelectionHandlers<JsOperationBreakdown> {
	
	private OperationParamsNew params;
	private final AonDisplayGrid grid;
	
	private boolean something;
	private double sumBase = 0.0;
	private double sumQuota = 0.0;
	private double sumDeductibleQuota = 0.0;
	private double sumSurchargeQuota = 0.0;
	private double sumTotal = 0.0;
	
	protected JsOperationGridPanel() {
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonFontSmaller());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		add(grid);
	}
	
	abstract void addRow(JsOperationBreakdown jsOperationBreakdown);
	abstract void addFooterRow();

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsOperationBreakdown> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
	public OperationParamsNew getParams() {
		return params;
	}

	public void setParams(OperationParamsNew params) {
		this.params = params;		
	}

	public AonDisplayGrid getGrid() {
		return grid;
	}

	public boolean isSomething() {
		return something;
	}

	public void setSomething(boolean something) {
		this.something = something;
	}

	public double getSumBase() {
		return sumBase;
	}

	public void setSumBase(double sumBase) {
		this.sumBase = sumBase;
	}

	public double getSumQuota() {
		return sumQuota;
	}

	public void setSumQuota(double sumQuota) {
		this.sumQuota = sumQuota;
	}

	public double getSumSurchargeQuota() {
		return sumSurchargeQuota;
	}

	public void setSumSurchargeQuota(double sumSurchargeQuota) {
		this.sumSurchargeQuota = sumSurchargeQuota;
	}

	public double getSumTotal() {
		return sumTotal;
	}

	public void setSumTotal(double sumTotal) {
		this.sumTotal = sumTotal;
	}

	public double getSumDeductibleQuota() {
		return sumDeductibleQuota;
	}

	public void setSumDeductibleQuota(double sumDeductibleQuota) {
		this.sumDeductibleQuota = sumDeductibleQuota;
	}
	
	protected void addCell(AonDisplayGridRow row, String value, String... style) {
		row.addCell(new Label(value == null ? AonStringUtils.EMPTY : value), style);
	}
	
	protected void addCell(AonDisplayGridRow row, Date value) {
		row.addCell(new Label(value == null ? AonStringUtils.EMPTY : AON.DATE_FORMAT.format(value)), AON.CSS.aonTextCenter());	
	}
	
	protected void addCell(AonDisplayGridRow row, double value) {
		row.addCell(new Label(AON.CURRENCY_FORMAT.format(value)), AON.CSS.aonTextRight());
	}
	
	protected void addCell(AonDisplayGridRow row, int value) {
		row.addCell(new Label(AonNumberUtils.toString(value)), AON.CSS.aonTextRight());
	}

}
