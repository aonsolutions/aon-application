package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary.IrpfSummaryGroup;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary.IrpfSummaryPercent;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary.IrpfSummaryType;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class IRPFReportSummaryPanel extends ScrollPanel implements HasValueChangeHandlers<IRPFParams> {
	
	private FlowPanel container = new FlowPanel();
	private AonDisplayTable mainTab;
	private AonDisplayTable subMainTab;
	
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
			paintSummary(params, summary);
		}
	}

	protected void onClick( IRPFParams params ) {
		ValueChangeEvent.<IRPFParams>fire( IRPFReportSummaryPanel.this, params );
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<IRPFParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	private void paintSummary(IRPFParams params, IrpfSummary summary) {
		mainTab = new AonDisplayTable();
		mainTab.addStyleName(AON.CSS.aonBlockCenter());
		mainTab.addStyleName(AON.CSS.aonMarginTop());
		Label outputLabel = new Label( AON.MSG.outputInvoices() );
		Label inputLabel = new Label( AON.MSG.inputInvoices() );
		
		subMainTab = new AonDisplayTable(); 
		subMainTab.addRow()
			.addCell(outputLabel	, AON.CSS.aonWidth320()	,AON.CSS.aonTextCenter(), AON.CSS.aonBold(), AON.CSS.aonBorderBottom())
			.addCell(new Label()	, AON.CSS.aonWidth20())
			.addCell(inputLabel		, AON.CSS.aonWidth440()	,AON.CSS.aonTextCenter(), AON.CSS.aonBold(), AON.CSS.aonBorderBottom())
		;
		mainTab.addRow().addCell(subMainTab); 
		container.add( mainTab );
		
		summary
			.getMap()
			.values()
			.stream()
			.forEach(g -> paintGroup(params, g));
	}

	private void paintGroup(IRPFParams params, IrpfSummaryGroup summaryGroup) {
		FlowPanel groupPanel = new FlowPanel();
		groupPanel.setStyleName(AON.CSS.aonPaddingLeft());
		groupPanel.addStyleName(AON.CSS.aonMarginTop());
		groupPanel.addStyleName(AON.CSS.aonTextCenter());
		Label groupLabel = new Label( summaryGroup.getGroup().getDescription() );
		groupLabel.setStyleName(AON.CSS.aonBold());
		groupPanel.add(groupLabel);
		mainTab.addRow().addCell(groupPanel);
		
		for ( IrpfSummaryType type : summaryGroup.getMap().values()) {
			paintType(params, type );
		}
	}
	
	private void paintType(IRPFParams params, IrpfSummaryType summaryType) {
		FlowPanel typePanel = new FlowPanel();
		typePanel.setStyleName(AON.CSS.aonPaddingLeft());
		Label typeLabel = new Label( summaryType.getType().getDescription() );
		typeLabel.setStyleName(AON.CSS.aonBold());
		typePanel.add(typeLabel);
		mainTab.addRow().addCell(typePanel);

		AonDisplayGrid tab = getTable( params );
		mainTab.addRow().addCell(tab);
		
		for ( IrpfSummaryPercent percent : summaryType.getMap().values()) {
			paintTab(params, tab, percent);
		}
	}

	private AonDisplayGrid getTable(IRPFParams params) {
		AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonMarginTop());
		
		Label outputBaseLabel = new Label( AON.MSG.taxableBase() );
		Label outputPercentLabel = new Label( "%" );
		Label outputQuotaLabel = new Label( AON.MSG.quota() );
		Label inputBaseLabel = new Label( AON.MSG.taxableBase() );
		Label inputPercentLabel = new Label( "%" );
		Label inputQuotaLabel = new Label( AON.MSG.quota() );
		Label inputDedQuotaLabel = new Label( AON.MSG.dedQuota() );
		
		tab.addHeaderRow()
			.addCell(outputBaseLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(outputPercentLabel	, AON.CSS.aonWidth80()	,AON.CSS.aonTextCenter())
			.addCell(outputQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(new Label()		, AON.CSS.aonWidth20())
			.addCell(inputBaseLabel		, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(inputPercentLabel	, AON.CSS.aonWidth80()	,AON.CSS.aonTextCenter())
			.addCell(inputQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(inputDedQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			
		;
		return tab;
	}

	private AonDisplayGrid paintTab(IRPFParams params, AonDisplayGrid tab, IrpfSummaryPercent percent) {
		Label outputBaseLabel = new Label( );
		Label outputPercentLabel = new Label( );
		Label outputQuotaLabel = new Label( );
		if (percent.getOutput() != null) {
			outputBaseLabel.setText( AON.FMT.format(percent.getOutput().getBase()) );
			outputPercentLabel.setText( AON.FMT.format(percent.getOutput().getPercent()) );
			outputQuotaLabel.setText( AON.FMT.format(percent.getOutput().getQuota()) );
		}
		
		Label inputBaseLabel = new Label( );
		Label inputPercentLabel = new Label( );
		Label inputQuotaLabel = new Label( );
		Label inputDedQuotaLabel = new Label( );
		if (percent.getInput() != null) {
			inputBaseLabel.setText( AON.FMT.format(percent.getInput().getBase()) );
			inputPercentLabel.setText( AON.FMT.format(percent.getInput().getPercent()) );
			inputQuotaLabel.setText( AON.FMT.format(percent.getInput().getQuota()) );
			inputDedQuotaLabel.setText( AON.FMT.format(percent.getInput().getDeductibleQuota()) );
		}
		tab.addRow()
			.addCell(outputBaseLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(outputPercentLabel	, AON.CSS.aonWidth80()	,AON.CSS.aonTextCenter())
			.addCell(outputQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(new Label()		, AON.CSS.aonWidth20())			
			.addCell(inputBaseLabel		, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight(), AON.CSS.aonMarginLeft())
			.addCell(inputPercentLabel	, AON.CSS.aonWidth80()	,AON.CSS.aonTextCenter())
			.addCell(inputQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
			.addCell(inputDedQuotaLabel	, AON.CSS.aonWidth120()	,AON.CSS.aonTextRight())
		;
		
		return tab;
	}

}
