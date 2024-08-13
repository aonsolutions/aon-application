package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class OperatingPanelReport extends DockLayoutPanel implements Focusable, HasAccountEntrySelectionHandlers{

	private static CommonServiceAsync commonService;

	private SimpleLayoutPanel centerPanel;
	private TabLayoutPanel tabPanel;
	
	private HashSet<String> costCentersSet;
	
	private AccountPeriodBox period;
	private FlowPanel periodBoxContainer = new FlowPanel();
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private ListBox confidential;
	private ListBox level;
	private ListBox previousPeriods;
	private ListBox activity;
	private CheckBox showPercents;
	private CheckBox byMonth;

	public OperatingPanelReport(String domainName,String user, int domainId) {
		this(domainName,user,domainId,null,null);
	}
	
	public OperatingPanelReport(String domainName,String user,int domainId, AonConfiguration config, AccountingReportParams params) {
		this( new AccountingReportModuleOptions()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user)
				.setConfiguration(config)
				,params);
 	}
	public OperatingPanelReport(AccountingReportModuleOptions options) {
		this(options,null);
	}
	public OperatingPanelReport(AccountingReportModuleOptions options, AccountingReportParams params) {
		super(Unit.PX);
		if (options.getConfiguration() == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			commonService.getAonConfiguration(options.getDomainName(), options.getDomain(), options.getUser()
				,new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						fill(options.setConfiguration(result), params);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al leer la configuraci\u00F3n");
					}
				});
		} else {
			fill (options,params);
		}
	}
	
	private void fill(AccountingReportModuleOptions options, AccountingReportParams params) {
		if (options.getConfiguration().accounting().getPeriods() == null || options.getConfiguration().accounting().getPeriods().size() == 0 ) {
			Window.alert("No se han encontrado ejercicios contables");
		} else {
			boolean activitiesListBoxEnabled = (options.getConfiguration() != null && options.getConfiguration().hasActivities());
			boolean hasCostCenters = (options.getConfiguration() != null && options.getConfiguration().accounting().hasCostCenters());
			
			addStyleName(AON.CSS.aonScrollArea());
			addStyleName(AON.CSS.aonMarginBottom());

			period = new AccountPeriodBox();
			period.setWidth("100px");
			fromDate = new AonDateBox();
			toDate = new AonDateBox();
			
			boolean periodBoxShown = false;
			period.fill(options.getConfiguration().accounting().getPeriods(),true);
			period.removeItem(0);
			if (params != null) {
				if (params.getPeriod() != null) {
					for (AccountPeriod p : options.getConfiguration().accounting().getPeriods()) {
						if (AonNumberUtils.equals(p.getId(), params.getPeriod())) {
							period.select(params.getPeriod());
							ListBox periodBox = getPeriodBox(options,period.getSelectedValue());
							periodBoxContainer.clear();
							periodBoxContainer.add(periodBox);
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
					ListBox periodBox = getPeriodBox(options,period.getSelectedValue());
					periodBoxContainer.clear();
					periodBoxContainer.add(periodBox);
					onSearch(options);
				}
			});
	
			fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					onSearch(options);
				}
			});
			
			toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					onSearch(options);
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
					onSearch(options);
				}
			});
			
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
					onSearch(options);
				}
			});
	
			confidential = new ListBox();
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
						onSearch(options);
					}
				});
			}
	
			activity = new ListBox();
			if (activitiesListBoxEnabled) {
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
				activity.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						onSearch(options);
					}
				});
			}
			
			showPercents = new CheckBox("Informaci\u00F3n extendida");
			if (params != null ) {
				showPercents.setValue(params.isPercentsEnabled());
			}
			showPercents.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					onSearch(options);
				}
			});
			
			byMonth = new CheckBox("Mensual");
			if (params != null ) {
				byMonth.setValue(params.isByMonth());
			}
			byMonth.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (byMonth.getValue()) {
						previousPeriods.setSelectedIndex(0);
						showPercents.setValue(false);
					}
					showPercents.setEnabled(!byMonth.getValue());
					previousPeriods.setEnabled(!byMonth.getValue());
					onSearch(options);
				}
			});
			
			ListBox costCenters = new ListBox();
			FlowPanel selectedCostCenter = new FlowPanel();
			if (options.getConfiguration() != null && options.getConfiguration().accounting().hasCostCenters()) {
				costCenters.setWidth("200px");
				costCenters.addItem("--- Todos ---");
				costCenters.addItem(AccountingReportParams.EMPTY_COST_CENTER_ACCOUNT);
				costCenters.setSelectedIndex(0);
				for (String costCenter : options.getConfiguration().accounting().getCostCenters()) {
					costCenters.addItem(costCenter);
				}
				costCenters.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						if (costCenters.getSelectedIndex() == 0) {
							if (params.getCostCenters() != null ) {
								params.setCostCenters( new HashSet<String>());
							}
							selectedCostCenter.clear();
						} else {
							if (costCentersSet == null) {
								costCentersSet = new HashSet<String>();
							}
							if (!costCentersSet.contains(costCenters.getSelectedValue())) {
								if (costCentersSet == null) costCentersSet = new HashSet<String>(); 
								costCentersSet.add(costCenters.getSelectedValue());
								InlineLabel l = new InlineLabel( costCenters.getSelectedValue() );
								l.setStyleName(AON.CSS.aonMarginLeft());
								l.addStyleName(AON.CSS.aonPaddingLeft());
								l.addStyleName(AON.CSS.aonIconDelete());
								l.addStyleName(AON.CSS.aonItalic());
								l.addStyleName(AON.CSS.aonFontSmall());
								l.addStyleName(AON.CSS.aonTabIcon());
								l.addClickHandler( new ClickHandler() {
									
									@Override
									public void onClick(ClickEvent event) {
										costCentersSet.remove(l.getText());
										selectedCostCenter.remove(l);
										onSearch(options);			
									}
								});
								selectedCostCenter.add(l);
								costCenters.setSelectedIndex(0);
							}
						}
						onSearch(options);
					}
				});
			}
			
			AonSearchPanelButton cleanButton = new AonSearchPanelButton(AON.MSG.clean(),AON.CSS.aonIconDelete());
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
					costCenters.setSelectedIndex(0);
					costCentersSet = null;
					costCenters.setEnabled(true);
					byMonth.setValue(false);
					previousPeriods.setEnabled(true);
					previousPeriods.setSelectedIndex(0);
					period.setFocus(true);
					onSearch(options);
				}
			});
	
			AonSearchPanelButton refreshButton = new AonSearchPanelButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
			refreshButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onSearch(options);
				}
			});
	
			AonDisplayTable mainTab = new AonDisplayTable(
				 AON.CSS.aonSearchPanel()
				,AON.CSS.aonBlockCenter()
				,AON.CSS.aonWidthAlmostAll()
			);
			
			if (!periodBoxShown) {
				periodBoxContainer.clear();
				periodBoxContainer.add(getPeriodBox(options,period.getSelectedValue()));
			}

			mainTab.addRow().addCell( 
				new AonDisplayTable().addRow()
				.addCell(new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()),AON.CSS.aonSearchPanelLabel())
				.addCell(period)
				.addCellIf(!periodBoxShown, periodBoxContainer)
				.addCell(new InlineLabel(AON.MSG.from()), AON.CSS.aonSearchPanelLabel(), AON.CSS.aonPaddingLeft())
				.addCell(fromDate)
				.addCell(new InlineLabel(AON.MSG.to()), AON.CSS.aonSearchPanelLabel())
				.addCell(toDate)
				.addCell(showPercents, AON.CSS.aonPaddingLeft())
				.addCell(byMonth, AON.CSS.aonPaddingLeft())
				.addCellIf( activitiesListBoxEnabled, new Label(AON.MSG.activity()), AON.CSS.aonSearchPanelLabel(), AON.CSS.aonPaddingLeft())
				.addCellIf( activitiesListBoxEnabled, activity))
				.addCell(new Label(), AON.CSS.aonFlexGrow1())
				;
			
			FlowPanel buttonsPanel = new FlowPanel();
			buttonsPanel.addStyleName(AON.CSS.aonMarginLeft());
			buttonsPanel.add( cleanButton );
			buttonsPanel.add( refreshButton );
			mainTab.addRow().addCell( 
				new AonDisplayTable().addRow()
				.addCell(new Label(AON.MSG.level()),AON.CSS.aonSearchPanelLabel())
				.addCell(level)
				.addCell(new Label("Comparar con ..."),AON.CSS.aonSearchPanelLabel(), AON.CSS.aonPaddingLeft())
				.addCell(previousPeriods)
				.addCellIf(options.getConfiguration().getUser().hasConfidentialityRole(),new Label(AON.MSG.show()), AON.CSS.aonSearchPanelLabel(), AON.CSS.aonPaddingLeft())
				.addCellIf(options.getConfiguration().getUser().hasConfidentialityRole(),confidential)
				.addCell(buttonsPanel), AON.CSS.aonFlexGrow1())
				;
	
			if (hasCostCenters) {
				mainTab.addRow().addCell( 
						new AonDisplayTable().addRow()
						.addCell(new Label(AON.MSG.costCenter()), AON.CSS.aonSearchPanelLabel())
						.addCell(costCenters)
						.addCell(selectedCostCenter, AON.CSS.aonFlexGrow1())
						)				;
			}

			SimpleLayoutPanel northPanel = new SimpleLayoutPanel();
			ScrollPanel scrollPanel = new ScrollPanel();
			scrollPanel.addStyleName(AON.CSS.aonWidthAll());
			scrollPanel.setWidget(mainTab);
			northPanel.setWidget(scrollPanel);
			addNorth(northPanel, hasCostCenters?100:80);
			SimpleLayoutPanel centerPanelContainer = new SimpleLayoutPanel();
			centerPanelContainer.setStyleName(AON.CSS.aonSelector());
			centerPanel = new SimpleLayoutPanel();
			tabPanel = new TabLayoutPanel(30, Unit.PX);
			tabPanel.add(centerPanel,new AonCloseTab("Resultados", false));
			centerPanelContainer.setWidget(tabPanel);
			add(centerPanelContainer);
			
			onSearch(options);
		
		}
	}

	private ListBox getPeriodBox(AccountingReportModuleOptions options,String selectedValue) {
		LinkedList<AccountPeriod> periods = options.getConfiguration().accounting().getPeriods();
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
								onSearch(options);
							}
						} else {
							fromDate.setValue(periodStart,false);
							toDate.setValue(periodEnd,false);
							onSearch(options);
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
	private void onSearch(AccountingReportModuleOptions options) {
		AccountingReportParams params = getWidgetParams(options);
		onSearch(options,params,0);
	}
	private void onSearch(AccountingReportModuleOptions options, AccountingReportParams params, int tab) {
		SimpleLayoutPanel panel = (SimpleLayoutPanel) tabPanel.getWidget(tab);
		panel.clear();
		panel.add(getResultPanel(options,params));
		tabPanel.selectTab(tab);
	}
	
	private OperatingPanel getResultPanel(AccountingReportModuleOptions options, AccountingReportParams params) {
		OperatingPanel operatingPanel = new OperatingPanel(options, params);
		operatingPanel.addSelectionHandler( new SelectionHandler<AccountingReportParams>() {
			
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
				AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
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
						.setAccount( newParams.getAccount().copy() )
				;
				StatementPanel statement = new StatementPanel(options, stmParams, true);
				statement.addSelectionHandler(new AccountEntrySelectionHandler () {
					
					@Override
					public void onSelection(AccountEntrySelectionEvent  event) {
						AccountEntrySelectionEvent.fire(OperatingPanelReport.this, event.getSelectedItem(), event.getCallback() );
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
				TrialBalancePanel trialBalance = new TrialBalancePanel(options, newParams);
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
		return operatingPanel;
	}

	public AccountingReportParams getWidgetParams(AccountingReportModuleOptions options) {
		boolean activitiesListBoxEnabled = (options.getConfiguration() != null && options.getConfiguration().hasActivities());
		Integer activityId = null;
		if (activitiesListBoxEnabled) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		int l = level.getSelectedIndex();
		return new AccountingReportParams()
			.setDomain(options.getDomain())
			.setPeriod(AonNumberUtils.toInteger( period.getSelectedValue()))
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setActivity(activityId)
			.setLevel(l==0?3:l==1?4:9)
			.setPreviousPeriods( previousPeriods.getSelectedIndex() )
			.setPercentsEnabled(showPercents.getValue())
			.setCostCenters(costCentersSet)
			.setSecurityLevel(confidential!=null?SecurityLevel.safeValueOf(confidential.getSelectedIndex()):SecurityLevel.OFFICIAL)
			.setByMonth(byMonth.getValue())
			;
	}
	
}
