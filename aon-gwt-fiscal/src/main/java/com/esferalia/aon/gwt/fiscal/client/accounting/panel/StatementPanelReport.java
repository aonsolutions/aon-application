package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
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
import com.google.gwt.user.client.ui.Button;
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

	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	private boolean allowChecks;
	
	private SimpleLayoutPanel centerPanel;
	
	private AccountPeriodBox period;
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private ListBox confidential;
	private AccountBox account;
	private ListBox activity;
	private Button cleanButton;
	private CheckBox reverseOrder;

	private boolean activitiesListBoxEnabled;
	
	public StatementPanelReport(String domainName,String user, int domainId) {
		this(domainName,user,domainId,Integer.MAX_VALUE,null,null, false);
	}
	
	public StatementPanelReport(String domainName,String user, int domainId, boolean allowChecks) {
		this(domainName,user,domainId,Integer.MAX_VALUE,null,null, allowChecks);
	}

	public StatementPanelReport(String domainName,String user,int domainId, int tabIndex, AonConfiguration config, AccountingReportParams params, boolean allowChecks) {
		super(Unit.PX);
		this.currentDomainName = domainName;
		this.currentUser = user;
		this.currentDomainId = domainId;
		this.allowChecks = allowChecks;
		
		if (config == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			commonService.getAonConfiguration(domainName, domainId
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
		activitiesListBoxEnabled = (config != null && config.hasActivities());
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		SimpleLayoutPanel northPanel = new SimpleLayoutPanel();
		addNorth(northPanel, 95);
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		fillNorthPanel(northPanel,tabIndex,config,params);
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
		
		FlexTable mainTab = new FlexTable();
		mainTab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		mainTab.addStyleName(AON.AON_CSS.aonWidthAll());
		mainTab.getColumnFormatter().setWidth(0, "auto");
		mainTab.getColumnFormatter().setWidth(1, "50px");
		
		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.AON_CSS.aonWidthAll());		
		mainTab.setWidget(0, 0, tab);

		FlexTable dateTab = new FlexTable();
		period = new AccountPeriodBox();
		fromDate = new DateBoxEx();
		toDate = new DateBoxEx();
		
		boolean periodBoxShown = false;
		period.fill(config.getPeriods(),true);
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
		account = new AccountBox(this.currentDomainName,this.currentDomainId);
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
				onSearch();
			}
		});
		account.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				String accountValue = account.getValue();
				if (AonStringUtils.contains(accountValue, "*")) {
					Label msg = new Label("Si desea mostrar mas de una cuenta, utilice el \"Balance de sumas y saldos\"");
					msg.setStyleName(AON.AON_CSS.aonColorRed());
					tab.setWidget(2, 1, msg);			
				} else {
					tab.clearCell(2, 1);
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
		
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "auto");
		
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
		
		if (activitiesListBoxEnabled) {
			tab.setWidget(0, 2, new Label(AON.MSG.activity()));
			tab.setWidget(0, 3, activity);
		} else {
			tab.setWidget(0, 2, new Label());
			tab.setWidget(0, 3, new Label());
		}
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonPanelGridOdd());
		tab.getCellFormatter().setStyleName(0,3, AON.AON_CSS.aonPanelGridEven());	
		
		reverseOrder = new CheckBox(AON.MSG.newersFirst());
		reverseOrder.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onSearch();
			}
		});
		
		tab.setWidget(0, 4, reverseOrder);

		cleanButton = new Button();
		cleanButton.setStyleName(AON.AON_CSS.aonIconDelete());
		cleanButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
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

		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 1, account);
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

		cleanButton.setTitle(AON.MSG.clean());
		tab.setWidget(1, 4, cleanButton);
		tab.getCellFormatter().setStyleName(2,4, AON.AON_CSS.aonPanelGridEven());

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
				StatementPanelReport.this.setWidgetSize(northPanel, 95 );
				StatementPanelReport.this.animate(500);
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
				StatementPanelReport.this.setWidgetSize(northPanel, 25);
				StatementPanelReport.this.animate(500);
			}
		});
		min.add(minimize);

		mainTab.setWidget(0, 1, min);
		mainTab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridOdd());

		northPanel.setWidget(mainTab);
		if (params != null) {
			tab.addStyleName(AON.AON_CSS.aonDisplayNone());
			StatementPanelReport.this.setWidgetSize(northPanel, 25);
			onSearch();
		} else {
			account.setFocus(true);
		}
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
		StatementPanel statementPanel = new StatementPanel(getCurrentDomainName(), getCurrentUser(), getCurrentDomainId(), params, allowChecks);
		statementPanel.addSelectionHandler(new AccountEntrySelectionHandler () {
			
			@Override
			public void onSelection(AccountEntrySelectionEvent  event) {
				AccountEntrySelectionEvent.fire(StatementPanelReport.this, event.getSelectedItem(), event.getCallback() );
			}
		});
		centerPanel.setWidget(statementPanel);
	}

	public AccountingReportParams getWidgetParams() {
		Integer activityId = null;
		if (activitiesListBoxEnabled) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		return new AccountingReportParams()
			.setDomain(this.currentDomainId)
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
	
}
