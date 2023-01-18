package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary.IrpfSummaryGroup;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary.IrpfSummaryPercent;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary.IrpfSummaryType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class IRPFReportSummaryPanel extends ScrollPanel implements HasValueChangeHandlers<IRPFParams> {
	
	private FlowPanel container = new FlowPanel();
	private AonDisplayTable mainTab = new AonDisplayTable();

	public IRPFReportSummaryPanel(IRPFParams params, IrpfSummary summary) {
		setStyleName(AON.CSS.aonScrollArea());
		setWidget(container);
		
		if (summary == null || summary.isEmpty()) {
			Label noDataLabel = new Label( AON.MSG.noData() );
			noDataLabel.setStyleName(AON.CSS.aonTextCenter());
			noDataLabel.addStyleName(AON.CSS.aonBold());
			noDataLabel.addStyleName(AON.CSS.aonMarginTop());
			container.add(noDataLabel);
		} else {
			mainTab.addStyleName(AON.CSS.aonBlockCenter());
			mainTab.addStyleName(AON.CSS.aonMarginTop());
			container.add(mainTab);
			
			paintHeader( params );
			paintSummary(params, summary);
		}
	}
	
	private void paintHeader(IRPFParams params) {
		Label outputLabel = new Label( AON.MSG.outputInvoices() );
		Label inputLabel = new Label( AON.MSG.inputInvoices() );
		mainTab.addHeaderRow()
			.addCell(outputLabel	, AON.CSS.aonWidth200())
			.addCell(outputLabel	, AON.CSS.aonWidth200())	
			.addCell(outputLabel	, AON.CSS.aonWidth320())
			.addCell(new Label()	, AON.CSS.aonWidth20())
			.addCell(inputLabel		, AON.CSS.aonWidth440())
			;
		
	}

	private void paintSummary(IRPFParams params, IrpfSummary summary) {
		summary
			.getMap()
			.values()
			.stream()
			.forEach(g -> paintGroup(params, g));
	}
	
	private void paintGroup(IRPFParams params, IrpfSummaryGroup summaryGroup) {
		AonDisplayTableRow row = mainTab.addRow();
		Label groupLabel = new Label( summaryGroup.getGroup().getDescription() );
		groupLabel.setStyleName(AON.CSS.aonBold());
		row.addCell(groupLabel);
		boolean first = true;
		for ( IrpfSummaryType type : summaryGroup.getMap().values()) {
			if (!first) {
				row = mainTab.addRow().addCell(new Label());
			}
			paintType(params, type , row);
			first = false;
		}
	}
	private void paintType(IRPFParams params, IrpfSummaryType summaryType, AonDisplayTableRow row) {
		Label typeLabel = new Label( summaryType.getType().getAbbreviatedDescription() );
		typeLabel.setStyleName(AON.CSS.aonBold());
		row.addCell( typeLabel )
			.addCell( getOutputTable( params, summaryType ) )
			.addCell(new Label())
			.addCell( getInputTable( params, summaryType ) )
			;
	}
	
	private AonDisplayGrid getOutputTable(IRPFParams params, IrpfSummaryType summaryType) {
		AonDisplayGrid outputTab = new AonDisplayGrid();
		outputTab.addStyleName(AON.CSS.aonBlockCenter());
		outputTab.addStyleName(AON.CSS.aonMarginTop());
		
		Label baseLabel = new Label( AON.MSG.taxableBase() );
		Label percentLabel = new Label( "%" );
		Label quotaLabel = new Label( AON.MSG.quota() );
		outputTab.addHeaderRow()
			.addCell(baseLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(percentLabel	, AON.CSS.aonWidth80()	,AON.CSS.aonTextCenter())
			.addCell(quotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
		;
		double sumBase = 0;
		double sumQuota = 0;
		for ( IrpfSummaryPercent percent : summaryType.getMap().values()) {
			Label outputBaseLabel = new Label("");
			Label outputPercentLabel = new Label(AON.FMT.format(percent.getPercent()));
			Label outputQuotaLabel = new Label("");
			if (percent.getOutput() != null) {
				outputBaseLabel.setText( AON.FMT.format(percent.getOutput().getBase()) );
				outputQuotaLabel.setText( AON.FMT.format(percent.getOutput().getQuota()) );
				sumBase = AonMathUtils.round(sumBase + percent.getOutput().getBase());
				sumQuota = AonMathUtils.round(sumQuota + percent.getOutput().getQuota());
			}
			outputTab.addRow()
				.addCell(outputBaseLabel	, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthGray())
				.addCell(outputPercentLabel	, AON.CSS.aonTextCenter(), AON.CSS.aonBackgroundLigthGray())
				.addCell(outputQuotaLabel	, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthGray())
			;
		}
		Label sumBaseLabel = new Label( AON.FMT.format( sumBase ) );
		Label sumQuotaLabel = new Label( AON.FMT.format( sumQuota ) );
		outputTab.addRow()
			.addCell(sumBaseLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthGray(), AON.CSS.aonBold())
			.addCell(new Label(""), AON.CSS.aonTextCenter(), AON.CSS.aonBackgroundLigthGray())
			.addCell(sumQuotaLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthGray(), AON.CSS.aonBold())
	;
		return outputTab;		
	}
	
	private AonDisplayGrid getInputTable(IRPFParams params, IrpfSummaryType summaryType) {
		AonDisplayGrid inputTab = new AonDisplayGrid();
		inputTab.addStyleName(AON.CSS.aonBlockCenter());
		inputTab.addStyleName(AON.CSS.aonMarginTop());
		
		Label baseLabel = new Label( AON.MSG.taxableBase() );
		Label percentLabel = new Label( "%" );
		Label quotaLabel = new Label( AON.MSG.quota() );
		Label dedQuotaLabel = new Label( AON.MSG.dedQuota() );
		inputTab.addHeaderRow()
			.addCell(baseLabel		, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(percentLabel	, AON.CSS.aonWidth80()	,AON.CSS.aonTextCenter())
			.addCell(quotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(dedQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
		;
		
		double sumBase = 0;
		double sumQuota = 0;
		double sumDedQuota = 0;
		
		for ( IrpfSummaryPercent percent : summaryType.getMap().values()) {
			Label inputBaseLabel = new Label("");
			Label inputPercentLabel = new Label(AON.FMT.format(percent.getPercent()));
			Label inputQuotaLabel = new Label("");
			Label inputDedQuotaLabel = new Label("");
			if (percent.getInput() != null) {
				inputBaseLabel.setText( AON.FMT.format(percent.getInput().getBase()) );
				inputQuotaLabel.setText( AON.FMT.format(percent.getInput().getQuota()) );
				inputDedQuotaLabel.setText( AON.FMT.format(percent.getInput().getDeductibleQuota()) );
				sumBase = AonMathUtils.round(sumBase + percent.getInput().getBase());
				sumQuota = AonMathUtils.round(sumQuota + percent.getInput().getQuota());
				sumDedQuota = AonMathUtils.round(sumDedQuota + percent.getInput().getDeductibleQuota());
			}
			inputTab.addRow()
				.addCell(inputBaseLabel		, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight(), AON.CSS.aonMarginLeft())
				.addCell(inputPercentLabel	, AON.CSS.aonWidth80()	,AON.CSS.aonTextCenter())
				.addCell(inputQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
				.addCell(inputDedQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			;
		}
		Label sumBaseLabel = new Label( AON.FMT.format( sumBase ) );
		Label sumQuotaLabel = new Label( AON.FMT.format( sumQuota ) );
		Label sumDedQuotaLabel = new Label( AON.FMT.format( sumDedQuota ) );
		inputTab.addRow()
			.addCell(sumBaseLabel, AON.CSS.aonTextRight(),  AON.CSS.aonBold())
			.addCell(new Label(""), AON.CSS.aonTextCenter())
			.addCell(sumQuotaLabel, AON.CSS.aonTextRight(), AON.CSS.aonBold())
			.addCell(sumDedQuotaLabel, AON.CSS.aonTextRight(), AON.CSS.aonBold())
		;
		return inputTab;
	}

	protected void onClick( IRPFParams params ) {
		ValueChangeEvent.<IRPFParams>fire( IRPFReportSummaryPanel.this, params );
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<IRPFParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
