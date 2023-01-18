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
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


public class IRPFReportFilterPanel extends SimpleLayoutPanel implements HasValueChangeHandlers<IRPFParams> {

	private static final String ALL_OPTIONS = "-- Todas --";

	private AonIntegerBox year;
	private PeriodListBox period;
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private ListBox output;
	private AonAccountingRegistryBox registry;
	private WithholdingTypeGroupListBox withholdingTypeGroup;
	private WithholdingTypeListBox withholdingType;
	private ListBox activity;
	private ListBox rectificationType;
	private ListBox orderBy;
	private ListBox groupedBy;
	private AonDoubleBox percent;
	
	public IRPFReportFilterPanel(IrpfReportModuleOptions options) {
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
		year = new AonIntegerBox();
		year.setValue(DateUtils.getYear(),false);
		year.addStyleName(AON.CSS.aonMarginLeft());
		year.setMaxLength(4);
		year.setVisibleLength(5);
		year.addValueChangeHandler(event -> {
			fillDates();
			ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options));
		});
		firstRowPanel.add(year);
		
		Label periodLabel = new InlineLabel(AON.MSG.period());
		periodLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		periodLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(periodLabel);
		period = new PeriodListBox();
		period.setSelectedIndex(0);
		period.addStyleName(AON.CSS.aonMarginLeft());
		period.addChangeHandler(event -> {
			fillDates();
			ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options));
		});
		firstRowPanel.add(period);
		
		Label dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		dateLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(dateLabel);
		fromDate = new AonDateBox();
		fromDate.addStyleName(AON.CSS.aonMarginLeft());
		fromDate.addValueChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		firstRowPanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.CSS.aonItalic());
		to.addStyleName(AON.CSS.aonMarginRight());
		to.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(to);
		toDate = new AonDateBox();
		toDate.addValueChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		firstRowPanel.add(toDate);
		
		fillDates();
		
		Label outputLabel = new InlineLabel(AON.MSG.invoices());
		outputLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		outputLabel.addStyleName(AON.CSS.aonMarginLeft());
		firstRowPanel.add(outputLabel);
		output = new ListBox();
		output.addStyleName(AON.CSS.aonMarginLeft());
		output.addItem(ALL_OPTIONS);
		output.addItem(AON.MSG.inputInvoices());
		output.addItem(AON.MSG.outputInvoices());
		output.setSelectedIndex(0);
		output.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		firstRowPanel.add(output);
		
		// ---------------------------------------------------------------- SECOND ROW
		FlowPanel secondRowPanel = new FlowPanel();
		filterPanel.add(secondRowPanel);

		Label titularLabel = new InlineLabel(AON.MSG.titular());
		titularLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		secondRowPanel.add(titularLabel);
		registry = new AonAccountingRegistryBox( options , true ); 
		registry.addStyleName(AON.CSS.aonMarginLeft());
		registry.setRequired(false);
		registry.setValue((AccountingRegistry) null,false);
		registry.addSelectionHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		secondRowPanel.add(registry);

		// ---------------------------------------------------------------- THIRD ROW
		FlowPanel thirdRowPanel = new FlowPanel();
		filterPanel.add(thirdRowPanel);
		
		Label withholdingTypeLabel = new InlineLabel(AON.MSG.withholdingType());
		withholdingTypeLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		thirdRowPanel.add(withholdingTypeLabel);
		
		withholdingTypeGroup = new WithholdingTypeGroupListBox("-- Todos --");
		withholdingTypeGroup.addStyleName(AON.CSS.aonMarginLeft());
		withholdingTypeGroup.setWidth("100px");
		withholdingTypeGroup.setSelectedIndex(0);
		withholdingTypeGroup.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		thirdRowPanel.add(withholdingTypeGroup);

		withholdingType = new WithholdingTypeListBox("-- Todos --");
		withholdingType.addStyleName(AON.CSS.aonMarginLeft());
		withholdingType.setWidth("100px");
		withholdingType.setSelectedIndex(0);
		withholdingType.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		thirdRowPanel.add(withholdingType);

		Label rectifiedLabel = new InlineLabel(AON.MSG.rectified());
		rectifiedLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		rectifiedLabel.addStyleName(AON.CSS.aonMarginLeft());
		thirdRowPanel.add(rectifiedLabel);
		rectificationType = new ListBox();
		rectificationType.addStyleName(AON.CSS.aonMarginLeft());
		rectificationType.setWidth("100px");
		rectificationType.addItem(ALL_OPTIONS);
		rectificationType.addItem("Ni rectificativa ni rectificada");
		rectificationType.addItem(RectificationType.NORMAL_RECTIFIER.getDescription());
		rectificationType.addItem(RectificationType.SPECIAL_RECTIFIER.getDescription());
		rectificationType.getElement().<SelectElement>cast().getOptions().getItem(3).setDisabled(true);
		rectificationType.addItem(RectificationType.RECTIFIED.getDescription());
		rectificationType.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		rectificationType.setSelectedIndex(0);
		thirdRowPanel.add(rectificationType);

		if (options.getConfiguration() != null && options.getConfiguration().hasActivities()) {
			InlineLabel activityLabel = new InlineLabel(AON.MSG.activity());
			activityLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
			activityLabel.addStyleName(AON.CSS.aonMarginLeft());
			thirdRowPanel.add(activityLabel);
			activity = new ListBox();
			activity.addStyleName(AON.CSS.aonMarginLeft());
			activity.setWidth("120px");
			activity.addItem(ALL_OPTIONS, "");
			activity.setSelectedIndex(0);
			int i = 1;
			for (EnterpriseActivity ea : options.getConfiguration().getActivities()) {
				activity.addItem(ea.getDescription() + (ea.getIae().isEmpty()?"":(" ("+ea.getEpigraph()+")")), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
			activity.addItem("-- Sin actividad --", "-1");
			activity.setSelectedIndex(0);
			activity.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
			thirdRowPanel.add(activity);
		}
		
		Label percentLabel = new InlineLabel(AON.MSG.percent());
		percentLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		percentLabel.addStyleName(AON.CSS.aonMarginLeft());
		thirdRowPanel.add(percentLabel);
		percent = new AonDoubleBox();
		percent.setVisibleLength(5);
		percent.addStyleName(AON.CSS.aonMarginLeft());
		percent.addValueChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		thirdRowPanel.add(percent);
		
		// ---------------------------------------------------------------- FOURTH ROW
		FlowPanel fourthRowPanel = new FlowPanel();
		filterPanel.add(fourthRowPanel);

		Label orderbyLabel = new InlineLabel("Ordenar por...");
		orderbyLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		fourthRowPanel.add(orderbyLabel);
		orderBy = new ListBox();
		orderBy.addStyleName(AON.CSS.aonMarginLeft());
		orderBy.setWidth("200px");
		for (IRPFParamsOrderBy o :IRPFParamsOrderBy.values()) {
			orderBy.addItem(o.getDescription());
		}
		orderBy.setSelectedIndex(0);
		orderBy.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		fourthRowPanel.add(orderBy);
		
		
		Label groupByNifLabel = new InlineLabel("Agrupar por ");
		groupByNifLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		groupByNifLabel.addStyleName(AON.CSS.aonMarginLeft());
		fourthRowPanel.add(groupByNifLabel);
		groupedBy = new ListBox();
		groupedBy.addStyleName(AON.CSS.aonMarginLeft());
		for (IRPFParamsGroupedBy g :IRPFParamsGroupedBy.values()) {
			groupedBy.addItem(g.getDescription());
		}
		groupedBy.setSelectedIndex(0);
		groupedBy.addChangeHandler(event -> ValueChangeEvent.<IRPFParams>fire(IRPFReportFilterPanel.this, getParams(options)));
		fourthRowPanel.add(groupedBy);

		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		setWidget(scrollPanel);
	}

	protected void fillDates() {
		Integer y = year.getValue();
		Period p = period.getValue();
		if ( y == null) {
			fromDate.setValue(null,false);
			toDate.setValue(null,false);
		} else {
			if (p == null) {
				fromDate.setValue(DateUtils.getFirstDayOfYear(y - 1900),false);
				toDate.setValue(DateUtils.getLastDayOfYear(y - 1900),false);
			} else {
				fromDate.setValue(DateUtils.getDate(p.getStartMonth(), y),false);
				toDate.setValue(DateUtils.getLastDayOfMonth(DateUtils.getDate(p.getDueMonth(), y)),false);
			}
		}
	}

	public IRPFParams getParams(IrpfReportModuleOptions options) {
		IRPFParams params = new IRPFParams()
				.setDomain(options.getDomain())
				.setDomainName(options.getDomainName())
				.setUser(options.getUser())
				.setRegistry(registry.getId())
				.setFromDate(fromDate.getValue())
				.setToDate(toDate.getValue())
				.setPercent( AonNumberUtils.nullIfZero(percent.getValue()) )
				;
			if (options.getConfiguration() != null && options.getConfiguration().hasActivities() && activity.getSelectedIndex() > 0) {
				params.setActivity( AonNumberUtils.toInteger( activity.getSelectedValue()));
			}
			params.setWithholdingTypeGroup(withholdingTypeGroup.getValue());
			params.setWithholdingType(withholdingType.getValue());
			params.setOrderBy(IRPFParamsOrderBy.safeValueOf(orderBy.getSelectedIndex()));	
			params.setGroupedBy(IRPFParamsGroupedBy.safeValueOf(groupedBy.getSelectedIndex()));

			if (output.getSelectedIndex() == 0) params.setOutput( null );
			if (output.getSelectedIndex() == 1) params.setOutput(false);
			if (output.getSelectedIndex() == 2) params.setOutput(true);
			
			if (rectificationType.getSelectedIndex() > 0) {
				params.setRectificationType(RectificationType.values()[rectificationType.getSelectedIndex() - 1]);	
			}
			return params;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<IRPFParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void setValue(IRPFParams params) {
		groupedBy.setSelectedIndex(params.getGroupedBy()==null?0:params.getGroupedBy().ordinal());
		output.setSelectedIndex(params.isOutput()?0:1);
		withholdingType.setValue(params.getWithholdingType());
		percent.setValue(params.getPercent());
	}

}
