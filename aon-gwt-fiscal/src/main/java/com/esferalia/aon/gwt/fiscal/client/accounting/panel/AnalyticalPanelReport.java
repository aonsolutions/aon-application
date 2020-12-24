package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.CloseTab;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class AnalyticalPanelReport extends DockLayoutPanel implements Focusable, HasAccountEntrySelectionHandlers{

	private static CommonServiceAsync commonService;

	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	
	private SimpleLayoutPanel centerPanel;
	private TabLayoutPanel tabPanel;
	
	private HashSet<String> costCentersSet;
	
	private AccountPeriodBox period;
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private ListBox confidential;
	private ListBox level;
	private ListBox activity;
	private Button cleanButton;
	private Button refreshButton;

	private boolean activitiesListBoxEnabled;
	
	public AnalyticalPanelReport(String domainName,String user, int domainId) {
		this(domainName,user,domainId,Integer.MAX_VALUE,null,null);
	}
	
	public AnalyticalPanelReport(String domainName,String user,int domainId, int tabIndex, AonConfiguration config, AccountingReportParams params) {
		super(Unit.PX);
		this.currentDomainName = domainName;
		this.currentUser = user;
		this.currentDomainId = domainId;
		
		if (config == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			commonService.getAonConfiguration(domainName, domainId, user
				,new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						fill(tabIndex, result, params);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al leer la configuraci\u00F3n");
					}
				});
		} else {
			fill (tabIndex,config,params);
		}
	}
	
	private void fill(int tabIndex, AonConfiguration config, AccountingReportParams params) {
		if (config.getPeriods() == null || config.getPeriods().size() == 0 ) {
			Window.alert("No se han encontrado ejercicios contables");
		} else {
			activitiesListBoxEnabled = (config != null && config.hasActivities());
			addStyleName(AON.AON_CSS.aonScrollArea());
			addStyleName(AON.AON_CSS.aonMarginBottom());
			SimpleLayoutPanel northPanel = new SimpleLayoutPanel();
			addNorth(northPanel, 75);
			SimpleLayoutPanel centerPanelContainer = new SimpleLayoutPanel();
			centerPanelContainer.setStyleName(AON.AON_CSS.aonSelector());
			centerPanel = new SimpleLayoutPanel();
			tabPanel = new TabLayoutPanel(30, Unit.PX);
			tabPanel.add(centerPanel,new CloseTab("Resultados", false));
			centerPanelContainer.setWidget(tabPanel);
			add(centerPanelContainer);

			fillNorthPanel(northPanel,tabIndex,config,params);
		}
	}
	
	public String getCurrentDomainName() {
		return currentDomainName;
	}
	public Integer getCurrentDomainId() {
		return currentDomainId;
	}
	public String getCurrentUser() {
		return currentUser;
	}
	
	private void fillNorthPanel(SimpleLayoutPanel northPanel, int tabIndex ,final AonConfiguration config, AccountingReportParams params) {
		
		FlexTable dateTab = new FlexTable();
		period = new AccountPeriodBox();
		period.setWidth("100px");
		fromDate = new DateBoxEx();
		toDate = new DateBoxEx();
		
		boolean periodBoxShown = false;
		period.fill(config.getPeriods(),true);
		period.removeItem(0);
		if (params != null) {
			if (params.getPeriod() != null) {
				for (AccountPeriod p : config.getPeriods()) {
					if (AonNumberUtils.equals(p.getId(), params.getPeriod())) {
						period.select(params.getPeriod());
						ListBox periodBox = getPeriodBox(config.getPeriods(),period.getSelectedValue());
						dateTab.setWidget(0, 1, periodBox);
						periodBoxShown = true;
					}
				}
			}
			if (params.getFromDate() != null) {
				fromDate.setValue(params.getFromDate(), false);
			}
			if (params.getToDate() != null) {
				toDate.setValue(params.getToDate(), false);
			}
		} 
		period.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox periodBox = getPeriodBox(config.getPeriods(),period.getSelectedValue());
				dateTab.setWidget(0, 1, periodBox);
				onSearch();
			}
		});

		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch();
			}
		});
		
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch();
			}
		});
		
		level = new ListBox();
		level.addItem( "Cuentas a 3 d\u00EDgitos","3");
		level.addItem( "Cuentas a 4 d\u00EDgitos","4");
		level.addItem( "Cuentas a 9 d\u00EDgitos","9");
		level.setSelectedIndex(1);
		if (params != null ) {
			if (params.getLevel() == 9) { 
				level.setSelectedIndex(2);
			} else if (params.getLevel() == 3) {
				level.setSelectedIndex(0);
			} else {
				level.setSelectedIndex(1);		
			}
		}
		level.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});
		
		if (config.getUser().hasConfidentialityRole()) {
			confidential = new ListBox();
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
					onSearch();
				}
			});
		}

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
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch();
				}
			});
		}
		
		FlexTable mainTab = new FlexTable();
		mainTab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		mainTab.addStyleName(AON.AON_CSS.aonWidthAll());
		mainTab.getColumnFormatter().setWidth(0, "auto");
		mainTab.getColumnFormatter().setWidth(1, "50px");
		
		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		
		mainTab.setWidget(0, 0, tab);
		
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "1%");
		tab.getColumnFormatter().setWidth(5, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonPanelGridOdd());
		
		dateTab.getColumnFormatter().setWidth(0, "50px");
		dateTab.getColumnFormatter().setWidth(1, "80px");
		dateTab.getColumnFormatter().setWidth(2, "20px");
		dateTab.getColumnFormatter().setWidth(3, "80px");
		dateTab.getColumnFormatter().setWidth(4, "20px");
		dateTab.getColumnFormatter().setWidth(5, "80px");
		
		dateTab.setWidget(0, 0, period);
		period.addStyleName(AON.AON_CSS.aonMarginRight());
		
		if (!periodBoxShown) {
			ListBox periodBox = getPeriodBox(config.getPeriods(),period.getSelectedValue());
			dateTab.setWidget(0, 1, periodBox);
		}

		InlineLabel from = new InlineLabel(AON.MSG.from());
		from.setStyleName(AON.AON_CSS.aonItalic());
		from.addStyleName(AON.AON_CSS.aonMarginRight());
		from.addStyleName(AON.AON_CSS.aonMarginLeft());
		dateTab.setWidget(0, 2, from);

		dateTab.setWidget(0, 3, fromDate);
		
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		dateTab.setWidget(0, 4, to);
		dateTab.setWidget(0, 5, toDate);
		
		tab.setWidget(0, 1, dateTab);
		tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(0, 1, 3);
		
		if (activitiesListBoxEnabled) {
			tab.setWidget(0, 2, new Label(AON.MSG.activity()));
			tab.setWidget(0, 3, activity);
		} else {
			tab.setWidget(0, 2, new Label());
			tab.setWidget(0, 3, new Label());
		}
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonPanelGridOdd());
		tab.getCellFormatter().setStyleName(0,3, AON.AON_CSS.aonPanelGridEven());
		
		cleanButton = new Button();
		cleanButton.setTitle(AON.MSG.clean());
		cleanButton.setStyleName(AON.AON_CSS.aonIconDelete());
		cleanButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				period.selectDefaultPeriod();
				AccountPeriod ap = period.getSelectedPeriod();
				if (ap != null) {
					fromDate.setValue(ap.getInitiationDate(),false);
					toDate.setValue(ap.getDeadline(),false);
				} else {
					fromDate.setValue(null,false);
					toDate.setValue(null,false);
				}
				confidential.setSelectedIndex(2);
				level.setSelectedIndex(1);
				if (activitiesListBoxEnabled) {
					activity.setSelectedIndex(0);
				}
				centerPanel.clear();
				costCentersSet = null;
				period.setFocus(true);
				onSearch();
			}
		});

		refreshButton = new Button();
		refreshButton.setTitle(AON.MSG.refresh());
		refreshButton.setStyleName(AON.AON_CSS.aonIconRefresh());
		refreshButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		refreshButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		refreshButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSearch();
			}
		});

		tab.setWidget(1, 0, new Label(AON.MSG.level()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 1, level);
		tab.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		if (config.getUser().hasConfidentialityRole()) {
			tab.setWidget(1, 2, new Label(AON.MSG.show()));
			tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonPanelGridOdd());
			
			tab.setWidget(1, 3, confidential);
			tab.getCellFormatter().setStyleName(1,3, AON.AON_CSS.aonPanelGridEven());
		} else {
			tab.setWidget(1, 2, new Label());
			tab.setWidget(1, 3, new Label());
		}
		
		FlowPanel buttons = new FlowPanel();
		buttons.setStyleName(AON.AON_CSS.aonNowrap());
		buttons.add(cleanButton);
		buttons.add(refreshButton);
		tab.setWidget(1, 4, buttons );
		tab.getCellFormatter().setStyleName(1,4, AON.AON_CSS.aonPanelGridEven());

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
				tab.removeStyleName(AON.AON_CSS.aonDisplayNone());
				AnalyticalPanelReport.this.setWidgetSize(northPanel, 95 );
				AnalyticalPanelReport.this.animate(500);
			}
		});

		min.add(maximize);
		
		Button minimize = new Button();
		minimize.setStyleName(AON.AON_CSS.aonIconCommandButton());
		minimize.addStyleName(AON.AON_CSS.aonIconMinimize());
		minimize.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tab.addStyleName(AON.AON_CSS.aonDisplayNone());
				AnalyticalPanelReport.this.setWidgetSize(northPanel, 25);
				AnalyticalPanelReport.this.animate(500);
			}
		});
		min.add(minimize);

		mainTab.setWidget(0, 1, min);
		mainTab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridOdd());

		northPanel.setWidget(mainTab);
		
		if (params != null) {
			tab.addStyleName(AON.AON_CSS.aonDisplayNone());
			AnalyticalPanelReport.this.setWidgetSize(northPanel, 25);
		}
		onSearch();
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
								onSearch();
							}
						} else {
							fromDate.setValue(periodStart,false);
							toDate.setValue(periodEnd,false);
							onSearch();
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

	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

	@Override
	public int getTabIndex() {
		return fromDate.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		fromDate.setAccessKey(key);;
	}

	@Override
	public void setFocus(boolean focused) {
		fromDate.setFocus(true);
		fromDate.hideDatePicker();
		fromDate.getTextBox().selectAll();
	}

	@Override
	public void setTabIndex(int index) {
		fromDate.setTabIndex(index);
	}
	private void onSearch() {
		AccountingReportParams params = getWidgetParams();
		onSearch(params,0);
	}
	private void onSearch(AccountingReportParams params, int tab) {
		SimpleLayoutPanel panel = (SimpleLayoutPanel) tabPanel.getWidget(tab);
		panel.clear();
		panel.add(getResultPanel(params));
		tabPanel.selectTab(tab);
	}
	
	private AnalyticalPanel getResultPanel(AccountingReportParams params) {
		AnalyticalPanel analyticalPanel = new AnalyticalPanel(getCurrentDomainName(), getCurrentUser(), getCurrentDomainId(), params);
		analyticalPanel.addSelectionHandler( new SelectionHandler<AccountingReportParams>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingReportParams> event) {
				AccountingReportParams newParams = event.getSelectedItem();
				addTab(newParams);
			}

			private void addTab(AccountingReportParams newParams) {
				SimpleLayoutPanel breakdownPanel = new SimpleLayoutPanel();
				String tabLabel = "";
				String code = newParams.getAccount().getCode();
				if (AonStringUtils.length( code ) < 9 ) {
					tabLabel = "Bal S/S: (" + code + "*)";
					breakdownPanel.add(getTrialBalance(newParams));
				} else {
					tabLabel = "Extr: " + code;
					breakdownPanel.add(getStatementPanel(newParams));
				}
				CloseTab closeTab = new CloseTab(tabLabel, true);
				closeTab.addCloseHandler(new CloseHandler<Integer>() {
					@Override
					public void onClose(CloseEvent<Integer> event) {
						tabPanel.remove(breakdownPanel); 
					}
				});
				tabPanel.add(breakdownPanel,closeTab);
				tabPanel.selectTab(tabPanel.getWidgetCount() - 1);

			}

			private Widget getStatementPanel(AccountingReportParams newParams) {
				AccountingReportParams stmParams = new AccountingReportParams()
						.setDomain( newParams.getDomain() )
						.setPeriod( newParams.getPeriod() )
						.setFromDate( newParams.getFromDate() )
						.setToDate( newParams.getToDate() )
						.setActivity( newParams.getActivity() )
						.setSecurityLevel( newParams.getSecurityLevel() )
						.setAccount( newParams.getAccount().clone() )
				;
				StatementPanel statement = new StatementPanel(
						new AccountingReportModuleOptions()
							.setDomainName(getCurrentDomainName())
							.setDomain(getCurrentDomainId())
							.setUser(getCurrentUser())
						, stmParams, true);
				statement.addSelectionHandler(new AccountEntrySelectionHandler () {
					
					@Override
					public void onSelection(AccountEntrySelectionEvent  event) {
						AccountEntrySelectionEvent.fire(AnalyticalPanelReport.this, event.getSelectedItem(), event.getCallback() );
					}
				});
				return statement;
			}

			private TrialBalancePanel getTrialBalance(AccountingReportParams newParams) {
				if (newParams.getLevel() == 1) newParams.setLevel(2);
				else if (newParams.getLevel() == 2) newParams.setLevel(3);
				else if (newParams.getLevel() == 3) newParams.setLevel(4);
				else if (newParams.getLevel() == 4) newParams.setLevel(9);
				else newParams.setLevel(9);
				TrialBalancePanel trialBalance = new TrialBalancePanel(getCurrentDomainName(), getCurrentUser(), getCurrentDomainId(), newParams);
				trialBalance.addSelectionHandler(new SelectionHandler<AccountingReportParams>() {

					@Override
					public void onSelection(SelectionEvent<AccountingReportParams> event) {
						AccountingReportParams newParams = event.getSelectedItem();
						addTab(newParams);
					}
				});
				return trialBalance;
			}
		});
		return analyticalPanel;
	}

	public AccountingReportParams getWidgetParams() {
		Integer activityId = null;
		if (activitiesListBoxEnabled) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		int l = level.getSelectedIndex();
		return new AccountingReportParams()
			.setDomainName(this.currentDomainName)
			.setDomain(this.currentDomainId)
			.setUser(this.currentUser)
			.setPeriod(AonNumberUtils.toInteger( period.getSelectedValue()))
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setActivity(activityId)
			.setLevel(l==0?3:l==1?4:9)
			.setCostCenters(costCentersSet)
			.setSecurityLevel(confidential!=null?SecurityLevel.safeValueOf(confidential.getSelectedIndex()):SecurityLevel.OFFICIAL)
			;
		
	}
	
}
