package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.occam.api.model.Account;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class LedgerPanelReport extends DockLayoutPanel implements Focusable, HasAccountEntrySelectionHandlers{

	private static CommonServiceAsync commonService;

	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanel;
	
	private AccountPeriodBox period;
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private ListBox activity;
	private TextBox account;
	private ListBox confidential;

	private boolean activitiesListBoxEnabled;
	
	public LedgerPanelReport(final AccountingReportModuleOptions options) {
		super(Unit.PX);
		if (options.getConfiguration() == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			commonService.getAonConfiguration(options.getDomainName(), options.getDomain(), options.getUser()
				,new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						fill(options.setConfiguration(result));
					}
	
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al leer la configuraci\u00F3n");
					}
				});
		} else {
			fill (options);
		}
	}
	
	private void fill(final AccountingReportModuleOptions options) {
		activitiesListBoxEnabled = (options.getConfiguration() != null && options.getConfiguration().hasActivities());
		setStyleName(AON.CSS.aonSelector());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		northPanel = new SimpleLayoutPanel();
		FlexTable mainTab = new FlexTable();
		mainTab.setStyleName(AON.CSS.aonSearchPanel());
		mainTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		mainTab.addStyleName(AON.CSS.aonBlockCenter());
		mainTab.getColumnFormatter().setWidth(0, "auto");
		mainTab.getColumnFormatter().setWidth(1, "50px");
		mainTab.setWidget(0, 0, getFilterTab(options,new AccountingReportParams()));
		mainTab.setWidget(0, 1, getMinMaxButtonsPanel());
		northPanel.setWidget(mainTab);
		addNorth(northPanel, 110);
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		onSearch(options);
	}
	
	private ListBox getPeriodBox(final AccountingReportModuleOptions options, String selectedValue) {
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
		
		account = new TextBox();
		confidential = new ListBox();
		
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
			tab.setWidget(0, 2, new Label(AON.MSG.activity()));
			tab.setWidget(0, 3, activity);
		} else {
			tab.setWidget(0, 2, new Label());
			tab.setWidget(0, 3, new Label());
		}
		tab.getCellFormatter().setStyleName(0,2, AON.CSS.aonSearchPanelLabel());


		// ************************************************************************  ACCOUNT
		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1,0, AON.CSS.aonSearchPanelLabel());
		
		account.setStyleName(AON.CSS.aonInputText());
		account.setVisibleLength(10);
		FlexTable accountContainer = new FlexTable();
		accountContainer.setWidget(0, 0, account);
		FlowPanel accountHelpPanel = new FlowPanel();
		
		InlineLabel i1 = new InlineLabel("Tesorer\u00EDa");
		i1.setStyleName(AON.CSS.aonInnerLabel());
		i1.addStyleName(AON.CSS.aonClickable());
		i1.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("57",true);}});
		InlineLabel i2 = new InlineLabel("Bancos");
		i2.setStyleName(AON.CSS.aonInnerLabel());
		i2.addStyleName(AON.CSS.aonClickable());
		i2.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("572",true);}});
		InlineLabel i3 = new InlineLabel("Caja");
		i3.setStyleName(AON.CSS.aonInnerLabel());
		i3.addStyleName(AON.CSS.aonClickable());
		i3.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("570",true);}});
		InlineLabel i4 = new InlineLabel("Adm.P\u00FAblicas");
		i4.setStyleName(AON.CSS.aonInnerLabel());
		i4.addStyleName(AON.CSS.aonClickable());
		i4.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("47",true);}});
		InlineLabel i5 = new InlineLabel("Clientes");
		i5.setStyleName(AON.CSS.aonInnerLabel());
		i5.addStyleName(AON.CSS.aonClickable());
		i5.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("430",true);}});
		InlineLabel i6 = new InlineLabel("Proveedores");
		i6.setStyleName(AON.CSS.aonInnerLabel());
		i6.addStyleName(AON.CSS.aonClickable());
		i6.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("400",true);}});
		InlineLabel i7 = new InlineLabel("Acreedores");
		i7.setStyleName(AON.CSS.aonInnerLabel());
		i7.addStyleName(AON.CSS.aonClickable());
		i7.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("410",true);}});
		accountHelpPanel.add(i1);
		accountHelpPanel.add(i2);
		accountHelpPanel.add(i3);
		accountHelpPanel.add(i4);
		accountHelpPanel.add(i5);
		accountHelpPanel.add(i6);
		accountHelpPanel.add(i7);
		accountContainer.setWidget(0, 1, accountHelpPanel);
		
		tab.setWidget(1, 1, accountContainer);
		tab.getFlexCellFormatter().setColSpan(1, 1, 3);
		
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
			tab.setWidget(1, 0, new Label(AON.MSG.show()));
			tab.getCellFormatter().setStyleName(1,0, AON.CSS.aonSearchPanelLabel());
			tab.setWidget(1, 3, confidential);
		}
		
		// ************************************************************************  CLEAN
		AonSearchPanelButton cleanButton = new AonSearchPanelButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		cleanButton.setTitle(AON.MSG.clean());
		tab.setWidget(1, 4, cleanButton);
		
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
				account.setValue(null,false);
				onSearch(options);
			}
		});
		

		// **********************************************************************  EVENTS
		period.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ListBox periodBox = getPeriodBox(options,period.getSelectedValue());
				dateTab.setWidget(0, 1, periodBox);
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
		if (activitiesListBoxEnabled) {
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch(options);
				}
			});
		}
		
		account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch(options);
			}
		});
		
		if (options.hasConfidentialityRole()) {
			confidential.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch(options);
				}
			});
		}
		
		return tab;
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
	
	private void onSearch( final AccountingReportModuleOptions options ) {
		AccountingReportParams params = getWidgetParams(options);
		LedgerPanel ledgerPanel = new LedgerPanel(options, params);
		ledgerPanel.addSelectionHandler(new AccountEntrySelectionHandler() {
			
			@Override
			public void onSelection(AccountEntrySelectionEvent event) {
				AccountEntrySelectionEvent.fire(LedgerPanelReport.this, event.getSelectedItem(), event.getCallback() );
			}

		});
		centerPanel.setWidget(ledgerPanel);
	}

	public AccountingReportParams getWidgetParams( final AccountingReportModuleOptions options ) {
		Integer activityId = null;
		if (activitiesListBoxEnabled) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		return new AccountingReportParams()
			.setDomain(options.getDomain())
			.setPeriod(period.getValue())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setActivity(activityId)
			.setAccount(new Account().setCode(account.getValue()))
			.setSecurityLevel(confidential!=null?SecurityLevel.safeValueOf(confidential.getSelectedIndex()):SecurityLevel.OFFICIAL)
			;
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
				MaximizeEvent.fire(LedgerPanelReport.this);
			}
		});

		min.add(maximize);
		
		AonSearchPanelButton minimize = new AonSearchPanelButton(AON.MSG.minimize(),AON.CSS.aonIconMinimize());
		minimize.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				MinimizeEvent.fire(LedgerPanelReport.this);
			}
		});
		min.add(minimize);
		return min;
	}
}
