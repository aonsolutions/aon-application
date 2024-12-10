package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
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
import com.esferalia.aon.occam.api.model.AccountStatement;
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
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


public class StatementPanelReport extends DockLayoutPanel implements Focusable, HasAccountEntrySelectionHandlers{

	private static CommonServiceAsync commonService;

	private boolean allowChecks;
	
	private SimpleLayoutPanel centerPanel;
	private AccountPeriodBox period;
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private ListBox confidential;
	private AonAccountBox account;
	private ListBox activity;
	private CheckBox reverseOrder;
	SimpleLayoutPanel filterPanel = new SimpleLayoutPanel();
	private Label messageLabel;

	
	private boolean activitiesListBoxEnabled;
	
	
	public StatementPanelReport(final AccountingReportModuleOptions options) {
		this( options, null, false);
	}
	public StatementPanelReport(final AccountingReportModuleOptions options, boolean allowChecks) {
		this( options, null, allowChecks);
	}

	public StatementPanelReport(final AccountingReportModuleOptions options, AccountingReportParams params, boolean allowChecks) {
		super(Unit.PX);
		this.allowChecks = allowChecks;
		
		if (options.getConfiguration() == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			commonService.getAonConfiguration(options.getDomainName(), options.getDomain(),options.getUser()
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
	
	private void fill(final AccountingReportModuleOptions options, AccountingReportParams params) {
		activitiesListBoxEnabled = (options.getConfiguration() != null && options.getConfiguration().hasActivities());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		addNorth(filterPanel, 110);
		
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		
		FlexTable mainTab = new FlexTable();
		mainTab.setStyleName(AON.CSS.aonSearchPanel());
		mainTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		mainTab.addStyleName(AON.CSS.aonBlockCenter());
		
		mainTab.getColumnFormatter().setWidth(0, "auto");
		mainTab.getColumnFormatter().setWidth(1, "50px");
		
		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonWidthAll());		
		mainTab.setWidget(0, 0, tab);

		FlexTable dateTab = new FlexTable();
		period = new AccountPeriodBox();
		fromDate = new AonDateBox();
		toDate = new AonDateBox();
		
		boolean periodBoxShown = false;
		period.fill(options.getConfiguration().accounting().getPeriods(),true);
		if (params != null) {
			if (params.getPeriod() != null) {
				for (AccountPeriod p : options.getConfiguration().accounting().getPeriods()) {
					if (AonNumberUtils.equals(p.getId(), params.getPeriod())) {
						period.select(params.getPeriod());
						ListBox periodBox = getPeriodBox(options,period.getSelectedValue());
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
		
		if (options.hasConfidentialityRole()) {
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
					onSearch(options);
				}
			});
		}
		account = new AonAccountBox(options.getDomainName(), options.getDomain(),options.getUser());
		account.setRequired(false);
		if (params != null && params.getAccount() != null) {
			account.setValue(params.getAccount().getId()
				,params.getAccount().getCode()
				,params.getAccount().getDescription()
				,false);
		}
		account.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				onSearch(options);
			}
		});
		account.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				String accountValue = account.getValue();
				if (AonStringUtils.contains(accountValue, "*")) {
					Label msg = new Label("Si desea mostrar mas de una cuenta, utilice el \"Balance de sumas y saldos\"");
					msg.setStyleName(AON.CSS.aonColorRed());
					tab.setWidget(2, 1, msg);			
				} else {
					tab.setWidget(2, 1, new Label());
				}
			}
		});

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
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch(options);
				}
			});
		}
		
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.CSS.aonSearchPanelLabel());
		
		dateTab.getColumnFormatter().setWidth(0, "50px");
		dateTab.getColumnFormatter().setWidth(1, "80px");
		dateTab.getColumnFormatter().setWidth(2, "20px");
		dateTab.getColumnFormatter().setWidth(3, "80px");
		dateTab.getColumnFormatter().setWidth(4, "20px");
		dateTab.getColumnFormatter().setWidth(5, "80px");
		
		dateTab.setWidget(0, 0, period);
		period.addStyleName(AON.CSS.aonMarginRight());
		
		if (!periodBoxShown) {
			ListBox periodBox = getPeriodBox(options,period.getSelectedValue());
			dateTab.setWidget(0, 1, periodBox);
		}

		InlineLabel from = new InlineLabel(AON.MSG.from());
		from.setStyleName(AON.CSS.aonItalic());
		from.addStyleName(AON.CSS.aonMarginRight());
		from.addStyleName(AON.CSS.aonMarginLeft());
		dateTab.setWidget(0, 2, from);

		dateTab.setWidget(0, 3, fromDate);
		
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.CSS.aonItalic());
		to.addStyleName(AON.CSS.aonMarginRight());
		to.addStyleName(AON.CSS.aonMarginLeft());
		dateTab.setWidget(0, 4, to);
		dateTab.setWidget(0, 5, toDate);
		
		tab.setWidget(0, 1, dateTab);
		
		
		if (activitiesListBoxEnabled) {
			tab.setWidget(0, 2, new Label(AON.MSG.activity()));
			tab.setWidget(0, 3, activity);
		} else {
			tab.setWidget(0, 2, new Label());
			tab.setWidget(0, 3, new Label());
		}
		tab.getCellFormatter().setStyleName(0,2, AON.CSS.aonSearchPanelLabel());
			
		
		reverseOrder = new CheckBox(AON.MSG.newersFirst());
		reverseOrder.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onSearch(options);
			}
		});
		
		tab.setWidget(0, 4, reverseOrder);

		AonSearchPanelButton cleanButton = new AonSearchPanelButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				period.selectDefaultPeriod();
				fromDate.setValue(null,false);
				toDate.setValue(null,false);
				confidential.setSelectedIndex(2);
				account.setAccount(null, false);;
				if (activitiesListBoxEnabled) {
					activity.setSelectedIndex(0);
				}
				reverseOrder.setValue(false);
				period.setFocus(true);
				centerPanel.clear();
			}
		});

		AonSearchPanelButton refreshButton = new AonSearchPanelButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSearch(options);
			}
		});

		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1,0, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(1, 1, account);
		
		
		if (options.getConfiguration().getUser().hasConfidentialityRole()) {
			tab.setWidget(1, 2, new Label(AON.MSG.show()));
			tab.getCellFormatter().setStyleName(1,2, AON.CSS.aonSearchPanelLabel());
			
			tab.setWidget(1, 3, confidential);
			
		} else {
			tab.setWidget(1, 2, new Label());
			tab.setWidget(1, 3, new Label());
		}
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.add(cleanButton);
		buttonContainer.add(refreshButton);
		tab.setWidget(1, 4, buttonContainer);
		

		FlowPanel min = new FlowPanel();
		min.setStyleName(AON.CSS.aonTextRight());
		min.addStyleName(AON.CSS.aonPaddingRight());
		min.addStyleName(AON.CSS.aonNowrap());
		min.addStyleName(AON.CSS.aonWidthAll());
		
//		AonSearchPanelButton maximize = new AonSearchPanelButton(AON.MSG.maximize(),AON.CSS.aonIconMaximize());
//		maximize.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				tab.removeStyleName(AON.CSS.aonDisplayNone());
//				StatementPanelReport.this.setWidgetSize(filterPanel, 90 );
//				StatementPanelReport.this.animate(500);
//			}
//		});
//
//		min.add(maximize);
//		
		AonSearchPanelButton close = new AonSearchPanelButton(AON.MSG.close(),AON.CSS.aonWidgetClose());
		close.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
	            closeFilterPanel();
			}
		});
		min.add(close);

		mainTab.setWidget(0, 1, min);
		mainTab.getCellFormatter().setStyleName(0,1, AON.CSS.aonSearchPanelLabel());

		filterPanel.setWidget(mainTab);
		if (params != null) {
			tab.addStyleName(AON.CSS.aonDisplayNone());
			StatementPanelReport.this.setWidgetSize(filterPanel, 35);
			onSearch(options);
		} else {
			account.setFocus(true);
		}
	}

	private ListBox getPeriodBox(final AccountingReportModuleOptions options, String selectedValue) {
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
	
	private void onSearch(final AccountingReportModuleOptions options) {
		AccountingReportParams params = getWidgetParams( options );
		StatementPanel statementPanel = new StatementPanel(options, params, allowChecks);
		statementPanel.addSelectionHandler(new AccountEntrySelectionHandler () {
			
			@Override
			public void onSelection(AccountEntrySelectionEvent  event) {
				AccountEntrySelectionEvent.fire(StatementPanelReport.this, event.getSelectedItem(), event.getCallback() );
			}
		});
		centerPanel.setWidget(statementPanel);
	}

	public AccountingReportParams getWidgetParams(final AccountingReportModuleOptions options) {
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
			.setAccount(new Account()
					.setId(account.getId())
					.setCode(account.getValue())
					.setDescription(account.getDescription()))
					.setSecurityLevel(confidential != null
					?SecurityLevel.safeValueOf(confidential.getSelectedIndex())
					:SecurityLevel.OFFICIAL)
			.setReverseOrder(reverseOrder.getValue())
			;
	}
	
	public void closeFilterPanel() {
	    filterPanel.addStyleName(AON.CSS.aonDisplayNone());
	    StatementPanelReport.this.setWidgetSize(filterPanel, 0);

	    if (messageLabel == null) {
	        messageLabel = new Label("Introduzca valores en el filtro de b\u00FAsqueda");
	        messageLabel.setStyleName("gwt-InlineLabel aon_bold aon_closeFilter_message");
	        centerPanel.add(messageLabel);
	    }
	}

	public void openFilterPanel() {
	    filterPanel.removeStyleName(AON.CSS.aonDisplayNone());
	    StatementPanelReport.this.setWidgetSize(filterPanel, 110);
	    StatementPanelReport.this.animate(500);

	    if (messageLabel != null) {
	    	centerPanel.remove(messageLabel);
	        messageLabel = null;
	    }
	}
	
	public boolean isFilterPanelOpened() {
		double filterPanelSize = getWidgetSize(filterPanel);
		if (Double.compare(filterPanelSize, 0.0) == 0) {
			return false;
		}
		return true;
	}
	
}
