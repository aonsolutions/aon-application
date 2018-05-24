package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceParams;
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
import com.google.gwt.user.client.ui.TextBox;


public class TrialBalancePanelReport extends DockLayoutPanel implements Focusable, HasAccountEntrySelectionHandlers{

	private static CommonServiceAsync commonService;

	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	
	private SimpleLayoutPanel centerPanel;
	
	private AccountPeriodBox period;
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private ListBox activity;
	
	private ListBox level;
	private CheckBox lowLevelAccountVisible;
	private ListBox confidential;
	
	private CheckBox noActivityAccountVisible;
	private TextBox account;

	private Button cleanButton;

	private boolean activitiesListBoxEnabled;
	
	public TrialBalancePanelReport(String domainName,String user, int domainId) {
		this(domainName,user,domainId,Integer.MAX_VALUE,null,null);
	}
	
	public TrialBalancePanelReport(String domainName,String user,int domainId, int tabIndex, AonConfiguration config, AccountTrialBalanceParams params) {
		super(Unit.PX);
		this.currentDomainName = domainName;
		this.currentUser = user;
		this.currentDomainId = domainId;
		
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
	
	private void fill(int tabIndex, AonConfiguration config, AccountTrialBalanceParams params) {
		if (config.getPeriods() == null || config.getPeriods().size() == 0 ) {
			Window.alert("No se han encontrado ejercicios contables");
		} else {
			activitiesListBoxEnabled = (config != null && config.hasActivities());
			addStyleName(AON.AON_CSS.aonScrollArea());
			addStyleName(AON.AON_CSS.aonMarginBottom());
			SimpleLayoutPanel northPanel = new SimpleLayoutPanel();
			addNorth(northPanel, 105);
			centerPanel = new SimpleLayoutPanel();
			add(centerPanel);
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
	
	private void fillNorthPanel(SimpleLayoutPanel northPanel, int tabIndex ,final AonConfiguration config, AccountTrialBalanceParams params) {
		
		FlexTable dateTab = new FlexTable();
		period = new AccountPeriodBox();
		period.setWidth("100px");
		fromDate = new DateBoxEx();
		toDate = new DateBoxEx();
		
		boolean periodBoxShown = false;
		period.fill(config.getPeriods(),true);
//		period.removeItem(0);
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
		level.addItem( "Cuentas a 1 d\u00EDgitos","1");
		level.addItem( "Cuentas a 2 d\u00EDgitos","2");
		level.addItem( "Cuentas a 3 d\u00EDgitos","3");
		level.addItem( "Cuentas a 4 d\u00EDgitos","4");
		level.addItem( "Cuentas a 9 d\u00EDgitos","9");
		level.setSelectedIndex(4);
		if (params != null ) {
			if (params.getLevel() == 4) level.setSelectedIndex(3);
			else if (params.getLevel() == 3) level.setSelectedIndex(2);
			else if (params.getLevel() == 2) level.setSelectedIndex(1);
			else if (params.getLevel() == 1) level.setSelectedIndex(0);
			else level.setSelectedIndex(4);	
		}
		level.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});
		
		lowLevelAccountVisible = new CheckBox("Mostrar acumulados inferiores");
		if (params != null ) {
			lowLevelAccountVisible.setValue(params.isLowLevelAccountVisible());
		}
		lowLevelAccountVisible.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
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
		
		noActivityAccountVisible = new CheckBox("Mostrar cuentas sin movimientos en el periodo");
		if (params != null ) {
			noActivityAccountVisible.setValue(params.isNoActivityAccountVisible());
		}
		noActivityAccountVisible.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onSearch();
			}
		});
		
		account = new TextBox();
		account.setStyleName(AON.AON_CSS.aonInputText());
		account.setVisibleLength(10);
		account.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch();
			}
		});

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
		tab.getColumnFormatter().setWidth(5, "1%");
		tab.getColumnFormatter().setWidth(6, "auto");
		
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
				level.setSelectedIndex(2);
				if (activitiesListBoxEnabled) {
					activity.setSelectedIndex(0);
				}
				period.setFocus(true);
				centerPanel.clear();
				account.setValue(null,false);
				lowLevelAccountVisible.setValue(false, false);
				noActivityAccountVisible.setValue(false, false);
			}
		});

		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());

		FlexTable accountContainer = new FlexTable();
		accountContainer.setWidget(0, 0, account);
		FlowPanel accountHelpPanel = new FlowPanel();
		
		InlineLabel i1 = new InlineLabel("Tesorer\u00EDa");
		i1.setStyleName(AON.AON_CSS.aonInnerLabel());
		i1.addStyleName(AON.AON_CSS.aonClickable());
		i1.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("57",true);}});
		InlineLabel i2 = new InlineLabel("Bancos");
		i2.setStyleName(AON.AON_CSS.aonInnerLabel());
		i2.addStyleName(AON.AON_CSS.aonClickable());
		i2.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("572",true);}});
		InlineLabel i3 = new InlineLabel("Caja");
		i3.setStyleName(AON.AON_CSS.aonInnerLabel());
		i3.addStyleName(AON.AON_CSS.aonClickable());
		i3.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("570",true);}});
		InlineLabel i4 = new InlineLabel("Adm.P\u00FAblicas");
		i4.setStyleName(AON.AON_CSS.aonInnerLabel());
		i4.addStyleName(AON.AON_CSS.aonClickable());
		i4.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("47",true);}});
		InlineLabel i5 = new InlineLabel("Clientes");
		i5.setStyleName(AON.AON_CSS.aonInnerLabel());
		i5.addStyleName(AON.AON_CSS.aonClickable());
		i5.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("430",true);}});
		InlineLabel i6 = new InlineLabel("Proveedores");
		i6.setStyleName(AON.AON_CSS.aonInnerLabel());
		i6.addStyleName(AON.AON_CSS.aonClickable());
		i6.addClickHandler(new ClickHandler() {@Override public void onClick(ClickEvent event) { account.setValue("400",true);}});
		InlineLabel i7 = new InlineLabel("Acreedores");
		i7.setStyleName(AON.AON_CSS.aonInnerLabel());
		i7.addStyleName(AON.AON_CSS.aonClickable());
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
		tab.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		if (config.getUser().hasConfidentialityRole()) {
			tab.setWidget(1, 2, new Label(AON.MSG.show()));
			tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonPanelGridOdd());
			
			tab.setWidget(1, 3, confidential);
			tab.getCellFormatter().setStyleName(1,3, AON.AON_CSS.aonPanelGridEven());
		} else {
			tab.setWidget(1, 3, new Label());
			tab.setWidget(1, 3, new Label());
		}

		
		tab.setWidget(2, 0, new Label(AON.MSG.level()));
		tab.getCellFormatter().setStyleName(2,0, AON.AON_CSS.aonPanelGridOdd());
		
		tab.setWidget(2, 1, level);
		tab.getCellFormatter().setStyleName(2,1, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(2, 2, lowLevelAccountVisible);
		tab.getCellFormatter().setStyleName(2,2, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(2, 2, 2);
		
		tab.setWidget(2, 3, noActivityAccountVisible);
		tab.getCellFormatter().setStyleName(2,3, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().setStyleName(2,3, AON.AON_CSS.aonNowrap());
		tab.getFlexCellFormatter().setColSpan(2, 3, 2);
		

		cleanButton.setTitle(AON.MSG.clean());
		tab.setWidget(1, 4, cleanButton);
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
				TrialBalancePanelReport.this.setWidgetSize(northPanel, 95 );
				TrialBalancePanelReport.this.animate(500);
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
				TrialBalancePanelReport.this.setWidgetSize(northPanel, 25);
				TrialBalancePanelReport.this.animate(500);
			}
		});
		min.add(minimize);

		mainTab.setWidget(0, 1, min);
		mainTab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridOdd());

		northPanel.setWidget(mainTab);
		
		if (params != null) {
			tab.addStyleName(AON.AON_CSS.aonDisplayNone());
			TrialBalancePanelReport.this.setWidgetSize(northPanel, 25);
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
		AccountTrialBalanceParams params = getWidgetParams();
		TrialBalancePanel operatingPanel = new TrialBalancePanel(getCurrentDomainName(), getCurrentUser(), getCurrentDomainId(), params);
//		operatingPanel.addSelectionHandler(new AccountEntrySelectionHandler () {
//			
//			@Override
//			public void onSelection(AccountEntrySelectionEvent  event) {
//				AccountEntrySelectionEvent.fire(OperatingPanelReport.this, event.getSelectedItem(), event.getCallback() );
//			}
//		});
		centerPanel.setWidget(operatingPanel);
	}

	public AccountTrialBalanceParams getWidgetParams() {
		Integer activityId = null;
		if (activitiesListBoxEnabled) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		return new AccountTrialBalanceParams()
			.setDomain(this.currentDomainId)
			.setPeriod(period.getSelectedIndex()==0?null:AonNumberUtils.toInteger( period.getSelectedValue()))
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setActivity(activityId)
			.setLevel(AonNumberUtils.toInteger( level.getSelectedValue()))
			.setLowLevelAccountVisible(lowLevelAccountVisible.getValue() )
			.setNoActivityAccountVisible(noActivityAccountVisible.getValue())
			.setCode(account.getValue())
			.setSecurityLevel(SecurityLevel.safeValueOf(confidential.getSelectedIndex()))
			;
	}
	
}
