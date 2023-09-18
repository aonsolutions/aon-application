package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeGroupListBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParamsGroupedBy;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParamsOrderBy;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;


public class IRPFReportFilterPanel extends ScrollPanel implements HasValueChangeHandlers<IRPFParams> {

	private static final String ALL_OPTIONS = "-- Todas --";

	private boolean groupedByDisabled = false;
	
	private AonIntegerBox yearBox;
	private PeriodListBox periodBox;
	private AonDateBox fromDateBox;
	private AonDateBox toDateBox;
	private ListBox outputBox;
	private AonAccountingRegistryBox registryBox;
	private WithholdingTypeGroupListBox withholdingTypeGroupBox;
	private WithholdingTypeListBox withholdingTypeBox;
	private ListBox activityBox;
	private ListBox rectificationTypeBox;
	private ListBox orderByBox;
	private ListBox groupedByBox;
	private AonDoubleBox percentBox;

	public IRPFReportFilterPanel(IrpfReportModuleOptions options) {
		this(options, false);
	}
	
	public IRPFReportFilterPanel(IrpfReportModuleOptions options, boolean groupedByDisabled) {
		this.groupedByDisabled = groupedByDisabled;
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonSearchPanel());
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginRight());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		tab.getColumnFormatter().setWidth(0, "auto");
		tab.getColumnFormatter().setWidth(1, "30px;");

		FlowPanel filterPanel = new FlowPanel();
		tab.setWidget(0, 0, filterPanel);
		
		// ---------------------------------------------------------------- FIRST ROW
		FlowPanel firstRowPanel = new FlowPanel();
		firstRowPanel.addStyleName(AON.CSS.aonMarginTop());
		filterPanel.add(firstRowPanel);
		
		Label yearLabel = new InlineLabel(AON.MSG.fiscalYear());
		yearLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		firstRowPanel.add(yearLabel);
		yearBox = new AonIntegerBox();
		yearBox.addStyleName(AON.CSS.aonMarginLeft());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(5);
		yearBox.addValueChangeHandler(event -> {
			fillDates();
			ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options));
		});
		firstRowPanel.add(yearBox);
		
		Label periodLabel = new InlineLabel(AON.MSG.period());
		periodLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		periodLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(periodLabel);
		periodBox = new PeriodListBox();
		periodBox.setSelectedIndex(0);
		periodBox.addStyleName(AON.CSS.aonMarginLeft());
		periodBox.addChangeHandler(event -> {
			fillDates();
			ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options));
		});
		firstRowPanel.add(periodBox);
		
		Label dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		dateLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(dateLabel);
		fromDateBox = new AonDateBox();
		fromDateBox.addStyleName(AON.CSS.aonMarginLeft());
		fromDateBox.addValueChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		firstRowPanel.add(fromDateBox);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.CSS.aonItalic());
		to.addStyleName(AON.CSS.aonMarginRight());
		to.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(to);
		toDateBox = new AonDateBox();
		toDateBox.addValueChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		firstRowPanel.add(toDateBox);
		
		Label outputLabel = new InlineLabel(AON.MSG.invoices());
		outputLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		outputLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(outputLabel);
		outputBox = new ListBox();
		outputBox.addStyleName(AON.CSS.aonMarginLeft());
		outputBox.addItem(ALL_OPTIONS);
		outputBox.addItem(AON.MSG.inputInvoices());
		outputBox.addItem(AON.MSG.outputInvoices());
		outputBox.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		firstRowPanel.add(outputBox);
		
		// ---------------------------------------------------------------- SECOND ROW
		FlowPanel secondRowPanel = new FlowPanel();
		filterPanel.add(secondRowPanel);

		Label titularLabel = new InlineLabel(AON.MSG.titular());
		titularLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		secondRowPanel.add(titularLabel);
		registryBox = new AonAccountingRegistryBox( options , true ); 
		registryBox.addStyleName(AON.CSS.aonMarginLeft());
		registryBox.setRequired(false);
		registryBox.addSelectionHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		secondRowPanel.add(registryBox);

		// ---------------------------------------------------------------- THIRD ROW
		FlowPanel thirdRowPanel = new FlowPanel();
		filterPanel.add(thirdRowPanel);
		
		Label withholdingTypeLabel = new InlineLabel(AON.MSG.withholdingType());
		withholdingTypeLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		thirdRowPanel.add(withholdingTypeLabel);
		
		withholdingTypeGroupBox = new WithholdingTypeGroupListBox("-- Todos --");
		withholdingTypeGroupBox.addStyleName(AON.CSS.aonMarginLeft());
		withholdingTypeGroupBox.setWidth("100px");
		withholdingTypeGroupBox.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		thirdRowPanel.add(withholdingTypeGroupBox);

		withholdingTypeBox = new WithholdingTypeListBox("-- Todos --");
		withholdingTypeBox.addStyleName(AON.CSS.aonMarginLeft());
		withholdingTypeBox.setWidth("100px");
		withholdingTypeBox.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		thirdRowPanel.add(withholdingTypeBox);

		Label rectifiedLabel = new InlineLabel(AON.MSG.rectified());
		rectifiedLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		rectifiedLabel.addStyleName(AON.CSS.aonMarginLeft());
		thirdRowPanel.add(rectifiedLabel);
		rectificationTypeBox = new ListBox();
		rectificationTypeBox.addStyleName(AON.CSS.aonMarginLeft());
		rectificationTypeBox.setWidth("100px");
		rectificationTypeBox.addItem(ALL_OPTIONS);
		rectificationTypeBox.addItem("Ni rectificativa ni rectificada");
		rectificationTypeBox.addItem(RectificationType.NORMAL_RECTIFIER.getDescription());
		rectificationTypeBox.addItem(RectificationType.SPECIAL_RECTIFIER.getDescription());
		rectificationTypeBox.getElement().<SelectElement>cast().getOptions().getItem(3).setDisabled(true);
		rectificationTypeBox.addItem(RectificationType.RECTIFIED.getDescription());
		rectificationTypeBox.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		thirdRowPanel.add(rectificationTypeBox);

		if (options.getConfiguration() != null && options.getConfiguration().hasActivities()) {
			InlineLabel activityLabel = new InlineLabel(AON.MSG.activity());
			activityLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
			activityLabel.addStyleName(AON.CSS.aonMarginLeft());
			thirdRowPanel.add(activityLabel);
			activityBox = new ListBox();
			activityBox.addStyleName(AON.CSS.aonMarginLeft());
			activityBox.setWidth("120px");
			activityBox.addItem(ALL_OPTIONS, "");
			activityBox.setSelectedIndex(0);
			int i = 1;
			for (EnterpriseActivity ea : options.getConfiguration().getActivities()) {
				activityBox.addItem(ea.getDescription() + (ea.getIae().isEmpty()?"":(" ("+ea.getEpigraph()+")")), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activityBox.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
			activityBox.addItem("-- Sin actividad --", "-1");
			activityBox.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
			thirdRowPanel.add(activityBox);
		}
		
		Label percentLabel = new InlineLabel(AON.MSG.percent());
		percentLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		percentLabel.addStyleName(AON.CSS.aonMarginLeft());
		thirdRowPanel.add(percentLabel);
		percentBox = new AonDoubleBox();
		percentBox.setVisibleLength(5);
		percentBox.addStyleName(AON.CSS.aonMarginLeft());
		percentBox.addValueChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		thirdRowPanel.add(percentBox);
		
		// ---------------------------------------------------------------- FOURTH ROW
		FlowPanel fourthRowPanel = new FlowPanel();
		filterPanel.add(fourthRowPanel);

		Label orderbyLabel = new InlineLabel("Ordenar por...");
		orderbyLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		fourthRowPanel.add(orderbyLabel);
		orderByBox = new ListBox();
		orderByBox.addStyleName(AON.CSS.aonMarginLeft());
		orderByBox.setWidth("200px");
		for (IRPFParamsOrderBy o :IRPFParamsOrderBy.values()) {
			orderByBox.addItem(o.getDescription());
		}
		orderByBox.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		fourthRowPanel.add(orderByBox);
		
		groupedByBox = new ListBox();
		if (!isGroupedByDisabled()) {
			Label groupByNifLabel = new InlineLabel("Agrupar por ");
			groupByNifLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
			groupByNifLabel.addStyleName(AON.CSS.aonMarginLeft());
			fourthRowPanel.add(groupByNifLabel);
			groupedByBox.addStyleName(AON.CSS.aonMarginLeft());
			for (IRPFParamsGroupedBy g :IRPFParamsGroupedBy.values()) {
				groupedByBox.addItem(g.getDescription());
			}
			groupedByBox.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
			fourthRowPanel.add(groupedByBox);
		}

		initialize( options );
		addStyleName(AON.CSS.aonWidthAll());
		setWidget(tab);
	}

	protected void fillDates() {
		Integer y = yearBox.getValue();
		Period p = periodBox.getValue();
		if ( y == null) {
			fromDateBox.setValue(null,false);
			toDateBox.setValue(null,false);
		} else {
			if (p == null) {
				fromDateBox.setValue(DateUtils.getFirstDayOfYear(y - 1900),false);
				toDateBox.setValue(DateUtils.getLastDayOfYear(y - 1900),false);
			} else {
				fromDateBox.setValue(DateUtils.getDate(p.getStartMonth(), y),false);
				toDateBox.setValue(DateUtils.getLastDayOfMonth(DateUtils.getDate(p.getDueMonth(), y)),false);
			}
		}
	}

	public boolean isGroupedByDisabled() {
		return groupedByDisabled;
	}

	public IRPFParams getParams(IrpfReportModuleOptions options) {
		IRPFParams params = new IRPFParams()
				.setDomain(options.getDomain())
				.setDomainName(options.getDomainName())
				.setUser(options.getUser())
				.setRegistry(registryBox.getId())
				.setFromDate(fromDateBox.getValue())
				.setToDate(toDateBox.getValue())
				.setPercent( AonNumberUtils.nullIfZero(percentBox.getValue()) )
				;
			if (options.getConfiguration() != null && options.getConfiguration().hasActivities() && activityBox.getSelectedIndex() > 0) {
				params.setActivity( AonNumberUtils.toInteger( activityBox.getSelectedValue()));
			}
			params.setWithholdingTypeGroup(withholdingTypeGroupBox.getValue());
			params.setWithholdingType(withholdingTypeBox.getValue());
			params.setOrderBy(IRPFParamsOrderBy.safeValueOf(orderByBox.getSelectedIndex()));	
			params.setGroupedBy(IRPFParamsGroupedBy.safeValueOf(groupedByBox.getSelectedIndex()));

			if (outputBox.getSelectedIndex() == 0) params.setOutput( null );
			if (outputBox.getSelectedIndex() == 1) params.setOutput(false);
			if (outputBox.getSelectedIndex() == 2) params.setOutput(true);
			
			if (rectificationTypeBox.getSelectedIndex() > 0) {
				params.setRectificationType(RectificationType.values()[rectificationTypeBox.getSelectedIndex() - 1]);	
			}
			return params;
	}
	
	protected void initialize(IrpfReportModuleOptions opt) {
		yearBox.setValue(DateUtils.getYear(),false);
		periodBox.setSelectedIndex(0);
		fillDates();
		outputBox.setSelectedIndex(0);
		registryBox.setValue((AccountingRegistry) null,false);
		withholdingTypeGroupBox.setSelectedIndex(0);
		withholdingTypeBox.setSelectedIndex(0);
		if (opt.getConfiguration() != null && opt.getConfiguration().hasActivities()) {
			activityBox.setSelectedIndex(0);
		}
		rectificationTypeBox.setSelectedIndex(0);
		orderByBox.setSelectedIndex(0);
		groupedByBox.setSelectedIndex(0);
		percentBox.setValue(null, false);
	}

		

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<IRPFParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void setValue(IRPFParams params) {
		groupedByBox.setSelectedIndex(params.getGroupedBy()==null?0:params.getGroupedBy().ordinal());
		percentBox.setValue(params.getPercent(), false);
		withholdingTypeBox.setValue(params.getWithholdingType());
		withholdingTypeGroupBox.setValue(params.getWithholdingTypeGroup());
		
		if (params.getOutput() == null) outputBox.setSelectedIndex(0);
		else if (params.isInput() ) outputBox.setSelectedIndex(1);
		else if (params.isOutput() ) outputBox.setSelectedIndex(2);
	}

}
