package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.accounting.BalanceType.IBalanceTypeVisitor;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class BalancePanelFilter extends SimpleLayoutPanel implements HasValueChangeHandlers<AccountingReportParams>{
	
	private AccountPeriodBox period;
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private ListBox activity;
	private ListBox confidential;
	private ListBox previousPeriods;
	private ListBox balanceType;
	private CheckBox breakdownEnabled;
	
	public BalancePanelFilter(final AccountingReportModuleOptions options, AccountingReportParams params) {
		setStyleName(AON.CSS.aonSelector());
		FlexTable mainTab = new FlexTable();
		mainTab.setStyleName(AON.CSS.aonSearchPanel());
		mainTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		mainTab.addStyleName(AON.CSS.aonBlockCenter());
		mainTab.getColumnFormatter().setWidth(0, "auto");
		mainTab.getColumnFormatter().setWidth(1, "50px");
		mainTab.setWidget(0, 0, getFilterTab(options,params));
		mainTab.setWidget(0, 1, getMinMaxButtonsPanel());
		setWidget(mainTab);
	}

	private FlowPanel getMinMaxButtonsPanel() {
		FlowPanel min = new FlowPanel();
		min.setStyleName(AON.CSS.aonTextRight());
		min.addStyleName(AON.CSS.aonPaddingRight());
		min.addStyleName(AON.CSS.aonNowrap());
		min.addStyleName(AON.CSS.aonWidthAll());
		
		AonSearchPanelButton maximize = new AonSearchPanelButton(AON.MSG.maximize(),AON.CSS.aonIconMaximize());
		maximize.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				MaximizeEvent.fire(BalancePanelFilter.this);
			}
		});

		min.add(maximize);
		
		AonSearchPanelButton minimize = new AonSearchPanelButton(AON.MSG.minimize(),AON.CSS.aonIconMinimize());
		minimize.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				MinimizeEvent.fire(BalancePanelFilter.this);
			}
		});
		min.add(minimize);
		return min;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<AccountingReportParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	public HandlerRegistration addMinimizeHandler(MinimizeHandler handler) {
		return addHandler(handler, MinimizeEvent.getType());
	}

	public HandlerRegistration addMaximizeHandler(MaximizeHandler handler) {
		return addHandler(handler, MaximizeEvent.getType());
	}

	private ListBox getPeriodBox(AccountingReportModuleOptions options, String selectedValue) {
		LinkedList<AccountPeriod> periods = options.getConfiguration().getPeriods();
		ListBox periodBox = new ListBox();
		periodBox.clear();
		periodBox.addItem(" --- ", "");
		Integer periodId = AonNumberUtils.toInteger(selectedValue);
		if (periodId != null) {
			LinkedList<Pair<Date, Date>> dates = new LinkedList<Pair<Date, Date>> ();
			AccountPeriod period = null;
			for (AccountPeriod iterPeriod : periods) {
				if (AonNumberUtils.equals(periodId, iterPeriod.getId())) {
					period = iterPeriod;
				}
			}
			if (period != null) {
				DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH_ABBR);
				final Date periodStart = period.getInitiationDate();
				final Date periodEnd = period.getDeadline();
				Date optionStart = new Date(periodStart.getTime());
				while ( optionStart.before(periodEnd) ) {
					Date pairLeft = DateUtils.getFirstDayOfMonth(optionStart); 
					Date pairRight = DateUtils.getLastDayOfMonth(optionStart);
					dates.add(new Pair<Date, Date>(pairLeft, pairRight));
					periodBox.addItem( DATE_FORMAT.format(optionStart) );
					optionStart = DateUtils.addMonths2Date(optionStart, 1);
				}
				periodBox.addItem( "----------------" );
				dates.add(null);
				optionStart = new Date(periodStart.getTime());
				while ( optionStart.before(periodEnd) ) {
					Date pairLeft = new Date(optionStart.getTime());
					Period quarter = Period.getQuarterlyPeriod( DateUtils.getMonth(pairLeft));
					Date pairRight = DateUtils.getLastDayOfMonth(DateUtils.getDate(quarter.getDueMonth(), DateUtils.getYear(pairLeft)));
					dates.add(new Pair<Date, Date>(pairLeft, pairRight));
					periodBox.addItem( quarter.getDescription() + " " + DateUtils.getYear(optionStart));
					optionStart = DateUtils.addMonths2Date(optionStart, 3);
				}
				periodBox.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						int i = periodBox.getSelectedIndex() - 1;
						if (i >= 0) {
							Pair<Date, Date> pair = dates.get(i);
							if (pair != null) {
								fromDate.setValue(dates.get(i).getLeft(),false);
								toDate.setValue(dates.get(i).getRight(),false);
								ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
							}
						} else {
							fromDate.setValue(periodStart,false);
							toDate.setValue(periodEnd,false);
							ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
						}
					}
				});
				fromDate.setValue(period.getInitiationDate(),false);
				toDate.setValue(period.getDeadline(),false);
			} else {
				fromDate.setValue(null,false);
				toDate.setValue(null,false);
			}
		} else {
			fromDate.setValue(null,false);
			toDate.setValue(null,false);
		}
		return periodBox;
	}

	private Widget getFilterTab(final AccountingReportModuleOptions options, AccountingReportParams params) {
		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonWidthAll());
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "1%");
		tab.getColumnFormatter().setWidth(5, "1%");
		tab.getColumnFormatter().setWidth(6, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.CSS.aonSearchPanelLabel());
		
		FlexTable dateTab = new FlexTable();
		period = new AccountPeriodBox();
		period.setWidth("100px");
		fromDate = new AonDateBox();
		toDate = new AonDateBox();
		confidential = new ListBox();
		previousPeriods = new ListBox();
		balanceType = new ListBox();
		breakdownEnabled = new CheckBox("Desglose de cuentas a 4 d\u00EDgitos");

		dateTab.getColumnFormatter().setWidth(0, "50px");
		dateTab.getColumnFormatter().setWidth(1, "80px");
		dateTab.getColumnFormatter().setWidth(2, "20px");
		dateTab.getColumnFormatter().setWidth(3, "80px");
		dateTab.getColumnFormatter().setWidth(4, "20px");
		dateTab.getColumnFormatter().setWidth(5, "80px");
	
		// ***********************************************************************  EJERCICIO
		period.fill(options.getConfiguration().getPeriods(),true);
		dateTab.setWidget(0, 0, period);
		period.addStyleName(AON.CSS.aonMarginRight());
		
		// **********************************************************************  PERIOD BOX
		ListBox periodBox = getPeriodBox(options,period.getSelectedValue());
		dateTab.setWidget(0, 1, periodBox);

		// **********************************************************************  FROM DATE
		InlineLabel from = new InlineLabel(AON.MSG.from());
		from.setStyleName(AON.CSS.aonItalic());
		from.addStyleName(AON.CSS.aonMarginRight());
		from.addStyleName(AON.CSS.aonMarginLeft());
		dateTab.setWidget(0, 2, from);
		dateTab.setWidget(0, 3, fromDate);
		
		// **********************************************************************  TO DATE
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.CSS.aonItalic());
		to.addStyleName(AON.CSS.aonMarginRight());
		to.addStyleName(AON.CSS.aonMarginLeft());
		dateTab.setWidget(0, 4, to);
		dateTab.setWidget(0, 5, toDate);

		tab.setWidget(0, 1, dateTab);
		tab.getFlexCellFormatter().setColSpan(0, 1, 3);


		
		previousPeriods = new ListBox();
		previousPeriods.setWidth("200px");
		previousPeriods.addItem( "----");
		previousPeriods.addItem( " ejercicio anterior");
		previousPeriods.addItem( " dos \u00FAltimos ejercicios");
		previousPeriods.addItem( " tres \u00FAltimos ejercicios");
		previousPeriods.setSelectedIndex(0);
		if (params != null ) {
			previousPeriods.setSelectedIndex(params.getPreviousPeriods());
		}
		previousPeriods.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if ( previousPeriods.getSelectedIndex() > 0) {
					breakdownEnabled.setValue(false);
					breakdownEnabled.setEnabled(false);
				} else {
					breakdownEnabled.setEnabled(true);
				}
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
			}
		});
		tab.setWidget(0, 2, new Label("Comparar con ..."));
		tab.getCellFormatter().setStyleName(0,2, AON.CSS.aonSearchPanelLabel());
		
		tab.setWidget(0, 3, previousPeriods);
		
		
		// ************************************************************************  ACTIVITY
		boolean activitiesListBoxEnabled = (options.getConfiguration() != null && options.getConfiguration().hasActivities());
		if (activitiesListBoxEnabled) {
			activity = new ListBox();
			activity.setWidth("150px");
			activity.addItem("-- Todas --", "");
			activity.addItem("-- Sin actividad --", "-1");
			activity.setSelectedIndex(0);
			int i = 2;
			for (EnterpriseActivity ea : options.getConfiguration().getActivities()) {
				activity.addItem(ea.getDescription(), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				if (params != null && params.getActivity() != null) {
					if (AonNumberUtils.equals(params.getActivity(), ea.getId())) {
						activity.setSelectedIndex(i);
					}
				}
				i++;
			}
			tab.setWidget(1, 0, new Label(AON.MSG.activity()));
			tab.setWidget(1, 1, activity);
		} else {
			tab.setWidget(1, 0, new Label());
			tab.setWidget(1, 1, new Label());
		}
		tab.getCellFormatter().setStyleName(1,0, AON.CSS.aonSearchPanelLabel());
		
		// ************************************************************************  CONFIDENTIAL
		if (options.hasConfidentialityRole()) {
			confidential.addItem( "Asientos NO confidenciales" );
			confidential.addItem( "Asientos confidenciales" );
			confidential.addItem(" Todos ");
			if (params != null && params.getSecurityLevel() == SecurityLevel.OFFICIAL) {
				confidential.setSelectedIndex(0);
			} else if (params != null && params.getSecurityLevel() == SecurityLevel.CONFIDENTIAL) {
				confidential.setSelectedIndex(1);
			} else {
				confidential.setSelectedIndex(2);
			}
			confidential.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
				}
			});
			
			tab.setWidget(1, 2, new Label(AON.MSG.show()));
			tab.getCellFormatter().setStyleName(1,2, AON.CSS.aonSearchPanelLabel());
			
			tab.setWidget(1, 3, confidential);
		} else {
			tab.setWidget(1, 2, new Label());
			tab.setWidget(1, 3, new Label());
		}

		
		// ************************************************************************  TIPO BALANCE
		balanceType = new ListBox();
		balanceType.setWidth("200px");
		IBalanceTypeVisitor visitor = new IBalanceTypeVisitor() {
			
			private static final long serialVersionUID = -7265627120635758519L;
			
			@Override 
			public void visitBalanceNormal() {balanceType.addItem( "Balance de situaci\u00F3n (Normal)");}
			@Override public void visitBalanceAbbreviate() {
				balanceType.addItem( "Balance de situaci\u00F3n (Abreviado)");
				if (options.getConfiguration() != null && options.getConfiguration().getCompany() != null && !AonDocumentUtil.isCooperative(options.getConfiguration().getCompany().getDocument())) {
					balanceType.setSelectedIndex( balanceType.getItemCount() - 1);				
				}
			}
			@Override public void visitBalancePymes() 			{balanceType.addItem( "Balance de situaci\u00F3n (PYMES)");}
			@Override public void visitPygNormal() 				{balanceType.addItem( "Cuenta de Explotaci\u00F3n (Normal)"); }
			@Override public void visitPygAbbreviate() 			{balanceType.addItem( "Cuenta de Explotaci\u00F3n (Abreviada)");}
			@Override public void visitPygPymes() 				{balanceType.addItem( "Cuenta de Explotaci\u00F3n (PYMES)");}

			@Override public void visitBalanceCoopAbbreviate() 	{
				balanceType.addItem( "Balance de situaci\u00F3n COOPERATIVAS (Abreviado)");
				balanceType.getElement()
					.getElementsByTagName("option")
					.getItem( balanceType.getItemCount() - 1 )
					.setAttribute("disabled", "disabled");
			}
			@Override
			public void visitBalanceCoopNormal() {
				balanceType.addItem( "Balance de situaci\u00F3n COOPERATIVAS (Normal)");
				if (options.getConfiguration() != null && options.getConfiguration().getCompany() != null && AonDocumentUtil.isCooperative(options.getConfiguration().getCompany().getDocument())) {
					balanceType.setSelectedIndex( balanceType.getItemCount() - 1);
				}
			}
			@Override public void visitPygCoopNormal() 			{balanceType.addItem( "Cuenta de Explotaci\u00F3n COOPERATIVAS (Normal)");}
			@Override public void visitPygCoopAbbreviate() 		{balanceType.addItem( "Cuenta de Explotaci\u00F3n COOPERATIVAS (Abreviado)");}
			@Override public void visitBalanceAsocAbbreviate()  {balanceType.addItem( "Balance ASOC.SIN LUCRO (PYMES)");}
			@Override public void visitPygAsocAbbreviate() 		{balanceType.addItem( "Cuenta de Resultados ASOC.SIN LUCRO (PYMES)");}
			
		};
		
		for (BalanceType bt : BalanceType.values()) {
			bt.visit(visitor);
		}
		if (params != null ) {
			balanceType.setSelectedIndex(params.getBalanceType().ordinal());
		}
		balanceType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
			}
		});
		tab.setWidget(1, 4, new Label("Tipo balance"));
		tab.getCellFormatter().setStyleName(1,4, AON.CSS.aonSearchPanelLabel());
		
		tab.setWidget(1, 5, balanceType);
		
		// ************************************************************************  DESGLOSE
		tab.setWidget(2, 1, breakdownEnabled);
		tab.getFlexCellFormatter().setColSpan(2, 1, 3);
		breakdownEnabled.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
			}
		});
		
		// ************************************************************************  CLEAN
		AonSearchPanelButton cleanButton = new AonSearchPanelButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		cleanButton.setTitle(AON.MSG.clean());
		tab.setWidget(0, 6, cleanButton);
		
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				period.selectDefaultPeriod();
				fromDate.setValue(period.getSelectedInitiationDate(),false);
				toDate.setValue(period.getSelectedDeadline(),false);
				confidential.setSelectedIndex(2);
				if (activitiesListBoxEnabled) {
					activity.setSelectedIndex(0);
				}
				period.setFocus(true);
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
			}
		});
		
		// **********************************************************************  EVENTS
		period.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ListBox periodBox = getPeriodBox(options,period.getSelectedValue());
				dateTab.setWidget(0, 1, periodBox);
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
			}
		});
		
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
			}
		});

		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
			}
		});
		if (activitiesListBoxEnabled) {
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams(options));
				}
			});
		}
		
		return tab;
	}
	
	public AccountingReportParams getWidgetParams(final AccountingReportModuleOptions options) {
		Integer activityId = null;
		if (activity != null) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		return new AccountingReportParams()
			.setDomain(options.getDomain())
			.setPeriod(period.getSelectedIndex()==0?null:AonNumberUtils.toInteger( period.getSelectedValue()))
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setActivity(activityId)
			.setSecurityLevel(confidential!=null?SecurityLevel.safeValueOf(confidential.getSelectedIndex()):SecurityLevel.OFFICIAL)
			.setPreviousPeriods( previousPeriods.getSelectedIndex() )
			.setBalanceType(BalanceType.values()[balanceType.getSelectedIndex()])
			.setBreakdownEnabled(breakdownEnabled.getValue())
			;
	}
	
}
