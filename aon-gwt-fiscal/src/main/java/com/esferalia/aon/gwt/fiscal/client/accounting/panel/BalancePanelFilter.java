package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class BalancePanelFilter extends SimpleLayoutPanel implements HasValueChangeHandlers<AccountingReportParams>{
	
	private String domainName;
	private String user;
	private Integer domainId;

	private AccountPeriodBox period;
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private ListBox activity;
	private ListBox confidential;
	private ListBox previousPeriods;
	private ListBox balanceType;
	
	public BalancePanelFilter(String domainName,String user, int domainId, AonConfiguration config, AccountingReportParams params) {
		this.domainName = domainName;
		this.user = user;
		this.domainId = domainId;
		
		setStyleName(AON.AON_CSS.aonSelector());
		FlexTable mainTab = new FlexTable();
		mainTab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		mainTab.addStyleName(AON.AON_CSS.aonWidthAll());
		mainTab.getColumnFormatter().setWidth(0, "auto");
		mainTab.getColumnFormatter().setWidth(1, "50px");
		mainTab.setWidget(0, 0, getFilterTab(config,params));
		mainTab.setWidget(0, 1, getMinMaxButtonsPanel());
		mainTab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		mainTab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonVerticalAlignTop());
		setWidget(mainTab);
	}

	
	public String getDomainName() {
		return domainName;
	}
	public String getUser() {
		return user;
	}
	public Integer getDomainId() {
		return domainId;
	}

	private FlowPanel getMinMaxButtonsPanel() {
		FlowPanel min = new FlowPanel();
		min.setStyleName(AON.AON_CSS.aonTextRight());
		min.addStyleName(AON.AON_CSS.aonPaddingRight());
		min.addStyleName(AON.AON_CSS.aonNowrap());
		min.addStyleName(AON.AON_CSS.aonWidthAll());
		
		Button maximize = new Button();
		maximize.setStyleName(AON.AON_CSS.aonIconCommandButton());
		maximize.addStyleName(AON.AON_CSS.aonIconMaximize());
		maximize.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				MaximizeEvent.fire(BalancePanelFilter.this);
			}
		});

		min.add(maximize);
		
		Button minimize = new Button();
		minimize.setStyleName(AON.AON_CSS.aonIconCommandButton());
		minimize.addStyleName(AON.AON_CSS.aonIconMinimize());
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

	private ListBox getPeriodBox(LinkedList<AccountPeriod> periods, String selectedValue) {
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
								ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
							}
						} else {
							fromDate.setValue(periodStart,false);
							toDate.setValue(periodEnd,false);
							ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
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

	private Widget getFilterTab(AonConfiguration config, AccountingReportParams params) {
		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "1%");
		tab.getColumnFormatter().setWidth(5, "1%");
		tab.getColumnFormatter().setWidth(6, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonPanelGridOdd());
		
		FlexTable dateTab = new FlexTable();
		period = new AccountPeriodBox();
		period.setWidth("100px");
		fromDate = new DateBoxEx();
		toDate = new DateBoxEx();
		confidential = new ListBox();
		previousPeriods = new ListBox();
		balanceType = new ListBox();

		dateTab.getColumnFormatter().setWidth(0, "50px");
		dateTab.getColumnFormatter().setWidth(1, "80px");
		dateTab.getColumnFormatter().setWidth(2, "20px");
		dateTab.getColumnFormatter().setWidth(3, "80px");
		dateTab.getColumnFormatter().setWidth(4, "20px");
		dateTab.getColumnFormatter().setWidth(5, "80px");
	
		// ***********************************************************************  EJERCICIO
		period.fill(config.getPeriods(),true);
		dateTab.setWidget(0, 0, period);
		period.addStyleName(AON.AON_CSS.aonMarginRight());
		
		// **********************************************************************  PERIOD BOX
		ListBox periodBox = getPeriodBox(config.getPeriods(),period.getSelectedValue());
		dateTab.setWidget(0, 1, periodBox);

		// **********************************************************************  FROM DATE
		InlineLabel from = new InlineLabel(AON.MSG.from());
		from.setStyleName(AON.AON_CSS.aonItalic());
		from.addStyleName(AON.AON_CSS.aonMarginRight());
		from.addStyleName(AON.AON_CSS.aonMarginLeft());
		dateTab.setWidget(0, 2, from);
		dateTab.setWidget(0, 3, fromDate);
		
		// **********************************************************************  TO DATE
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		dateTab.setWidget(0, 4, to);
		dateTab.setWidget(0, 5, toDate);

		tab.setWidget(0, 1, dateTab);
		tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
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
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
			}
		});
		tab.setWidget(0, 2, new Label("Comparar con ..."));
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonPanelGridOdd());
		
		tab.setWidget(0, 3, previousPeriods);
		tab.getCellFormatter().setStyleName(0,3, AON.AON_CSS.aonPanelGridEven());
		
		
		// ************************************************************************  ACTIVITY
		boolean activitiesListBoxEnabled = (config != null && config.hasActivities());
		if (activitiesListBoxEnabled) {
			activity = new ListBox();
			activity.setWidth("150px");
			activity.addItem("-- Todas --", "");
			activity.addItem("-- Sin actividad --", "-1");
			activity.setSelectedIndex(0);
			int i = 2;
			for (EnterpriseActivity ea : config.getActivities()) {
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
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());
		tab.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		// ************************************************************************  CONFIDENTIAL
		if (config.getUser().hasConfidentialityRole()) {
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
					ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
				}
			});
			
			tab.setWidget(1, 2, new Label(AON.MSG.show()));
			tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonPanelGridOdd());
			
			tab.setWidget(1, 3, confidential);
			tab.getCellFormatter().setStyleName(1,3, AON.AON_CSS.aonPanelGridEven());
		} else {
			tab.setWidget(1, 2, new Label());
			tab.setWidget(1, 3, new Label());
		}

		
		// ************************************************************************  TIPO BALANCE
		balanceType = new ListBox();
		balanceType.setWidth("200px");
		balanceType.addItem( "Normal");
		balanceType.addItem( "Abreviado");
		balanceType.addItem( "PYMES");
		balanceType.setSelectedIndex(0);
		if (params != null ) {
			balanceType.setSelectedIndex(params.getBalanceType().ordinal());
		}
		balanceType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
			}
		});
		tab.setWidget(1, 4, new Label("Tipo balance"));
		tab.getCellFormatter().setStyleName(1,4, AON.AON_CSS.aonPanelGridOdd());
		
		tab.setWidget(1, 5, balanceType);
		tab.getCellFormatter().setStyleName(1,5, AON.AON_CSS.aonPanelGridEven());
		
		// ************************************************************************  CLEAN
		Button cleanButton = new Button("Limpiar");
		cleanButton.setStyleName(AON.AON_CSS.aonIconDelete());
		cleanButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		cleanButton.setTitle(AON.MSG.clean());
		tab.setWidget(0, 6, cleanButton);
		tab.getCellFormatter().setStyleName(0,6, AON.AON_CSS.aonPanelGridEven());
		
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
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
			}
		});
		
		// **********************************************************************  EVENTS
		period.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ListBox periodBox = getPeriodBox(config.getPeriods(),period.getSelectedValue());
				dateTab.setWidget(0, 1, periodBox);
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
			}
		});
		
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
			}
		});

		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
			}
		});
		if (activitiesListBoxEnabled) {
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.<AccountingReportParams>fire(BalancePanelFilter.this, getWidgetParams());
				}
			});
		}
		
		return tab;
	}
	
	public AccountingReportParams getWidgetParams() {
		Integer activityId = null;
		if (activity != null) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		return new AccountingReportParams()
			.setDomain(getDomainId())
			.setPeriod(period.getSelectedIndex()==0?null:AonNumberUtils.toInteger( period.getSelectedValue()))
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setActivity(activityId)
			.setSecurityLevel(SecurityLevel.safeValueOf(confidential.getSelectedIndex()))
			.setPreviousPeriods( previousPeriods.getSelectedIndex() )
			.setBalanceType(BalanceType.values()[balanceType.getSelectedIndex()])
			;
	}
	
}
