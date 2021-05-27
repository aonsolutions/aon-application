package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.AccountEntryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class JournalPanelReport extends DockLayoutPanel implements Focusable, HasAccountEntrySelectionHandlers{

	private static CommonServiceAsync commonService;

	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanel;
	
	private FlexTable tab;
	private AccountPeriodBox period;
	private FlowPanel datePanel; 
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private AccountEntryListBox entryListBox;
	private ListBox confidential;
	private AonIntegerBox journal;
	private AonAccountBox account;
	private AonDoubleBox debit;
	private AonDoubleBox credit;
	private TextBox concept;
	private TextBox document;
	private ListBox activity;
	private TextBox comments;
	private AonIntegerBox id;
	private ListBox order;
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;

	private boolean activitiesListBoxEnabled;
	
	public JournalPanelReport(String domainName,String user, int domainId) {
		this(domainName,user,domainId,null);
	}
	
	public JournalPanelReport(String domainName,String user,int domainId, AonConfiguration config) {
		this( new AccountingReportModuleOptions()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user)
				.setConfiguration(config));
	}
	
	public JournalPanelReport(AccountingReportModuleOptions options) {
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
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		northPanel = new SimpleLayoutPanel();
		fillNorthPanel(options);
		addNorth(northPanel, 100);
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		onSearch(options);
	}
	
	private void fillNorthPanel(final AccountingReportModuleOptions options) {
		period = new AccountPeriodBox();
		period.fill(options.getConfiguration().getPeriods(),true);
		period.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch(options);
			}
		});
		
		fromDate = new AonDateBox();
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch(options);
			}
		});
		toDate = new AonDateBox();
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch(options);
			}
		});
		confidential = new ListBox();
		confidential.setWidth("100px");
		confidential.addItem( "Asientos NO confidenciales" );
		confidential.addItem( "Asientos confidenciales" );
		confidential.addItem(" Todos ");
		confidential.setSelectedIndex(2);
		confidential.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onSearch(options);
			}
		});
		
		account = new AonAccountBox(options.getDomainName(),options.getDomain(), options.getUser());
		account.setRequired(false);
		account.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				onSearch(options);
			}
		});
		debit = new AonDoubleBox();
		debit.setVisibleLength(8);
		debit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				onSearch(options);
			}
		});
		credit = new AonDoubleBox();
		credit.setVisibleLength(8);
		credit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				onSearch(options);
			}
		});
		entryListBox = new AccountEntryListBox();
		entryListBox.setWidth("120px");
		entryListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch(options);
			}
		});
		
		journal = new AonIntegerBox();
		journal.setVisibleLength(8);
		journal.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				onSearch(options);
			}
		});
		concept = new TextBox();
		concept.setVisibleLength(10);
		concept.setStyleName(AON.CSS.aonInputText());
		concept.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch(options);
			}
		});
		document = new TextBox();
		document.setVisibleLength(10);
		document.setStyleName(AON.CSS.aonInputText());
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch(options);
			}
		});
		
		comments = new TextBox();
		comments.setVisibleLength(30);
		comments.setStyleName(AON.CSS.aonInputText());
		comments.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch(options);
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
				i++;
			}
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch(options);
				}
			});
		}
		
		id = new AonIntegerBox();
		id.setVisibleLength(8);
		id.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				onSearch(options);
			}
		});

		order = new ListBox();
		order.setWidth("200px");
		order.addItem("Ejerc., n\u00BA diario, fecha");
		order.addItem("Fecha creaci\u00F3n, descendente");
		order.addItem("Fecha modificaci\u00F3n, descendente");
		order.setSelectedIndex(0);
		order.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch(options);
			}
		});
		
		tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonSearchPanel());
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginRight());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "1%");
		tab.getColumnFormatter().setWidth(5, "1%");
		tab.getColumnFormatter().setWidth(6, "1%");
		tab.getColumnFormatter().setWidth(7, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.CSS.aonSearchPanelLabel());
		
		datePanel = new FlowPanel();
		datePanel.setStyleName(AON.CSS.aonNowrap());
		datePanel.add(period);
		period.addStyleName(AON.CSS.aonMarginRight());
		datePanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.CSS.aonItalic());
		to.addStyleName(AON.CSS.aonMarginRight());
		to.addStyleName(AON.CSS.aonMarginLeft());
		datePanel.add(to);
		datePanel.add(toDate);
		tab.setWidget(0, 1, datePanel);
		
		tab.setWidget(0, 2, new Label(AON.MSG.type()));
		tab.getCellFormatter().setStyleName(0,2, AON.CSS.aonSearchPanelLabel());
		
		FlowPanel entryTypePanel = new FlowPanel();
		entryTypePanel.add(entryListBox);
		if (datePanel != null && options.hasConfidentialityRole()) {
			confidential.addStyleName(AON.CSS.aonMarginLeft());
			entryTypePanel.add(confidential);
			entryTypePanel.addStyleName(AON.CSS.aonNowrap());
		}
		tab.setWidget(0, 3, entryTypePanel);
		
		tab.setWidget(0, 4, new Label(AON.MSG.journal()));
		tab.getCellFormatter().setStyleName(0,4, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(0, 5, journal);
		
		if (activitiesListBoxEnabled) {
			tab.setWidget(0, 6, new Label(AON.MSG.activity()));
			tab.setWidget(0, 7, activity);
		} else {
			tab.setWidget(0, 6, new Label());
			tab.setWidget(0, 7, new Label());
		}
		tab.getCellFormatter().setStyleName(0,6, AON.CSS.aonSearchPanelLabel());
		
		cleanButton = new AonSearchPanelButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				period.selectDefaultPeriod();
				fromDate.setValue(null,false);
				toDate.setValue(null,false);
				entryListBox.setValue(null);
				confidential.setSelectedIndex(2);
				journal.setValue(null,false);
				account.setAccount(null, false);;
				debit.setValue(null,false);
				credit.setValue(null,false);
				concept.setValue(null,false);
				document.setValue(null,false);
				if (activitiesListBoxEnabled) {
					activity.setSelectedIndex(0);
				}
				period.setFocus(true);
				onSearch(options);
			}
		});

		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSearch(options);
			}
		});

		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1,0, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(1, 1, account);
		
		
		tab.setWidget(1, 2, new Label(AON.MSG.debit()));
		tab.getCellFormatter().setStyleName(1,2, AON.CSS.aonSearchPanelLabel());
		
		FlowPanel amountsPanel = new FlowPanel();
		amountsPanel.setStyleName(AON.CSS.aonNowrap());
		amountsPanel.add(debit);
		InlineLabel cre= new InlineLabel(AON.MSG.credit());
		cre.setStyleName(AON.CSS.aonBold());
		cre.addStyleName(AON.CSS.aonMarginRight());
		cre.addStyleName(AON.CSS.aonMarginLeft());
		amountsPanel.add(cre);
		amountsPanel.add(credit);
		tab.setWidget(1, 3, amountsPanel);
		
		tab.setWidget(1, 4, new Label(AON.MSG.concept()));
		tab.getCellFormatter().setStyleName(1,4, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(1, 5, concept);

		tab.setWidget(1, 6, new Label(AON.MSG.document()));
		tab.getCellFormatter().setStyleName(1,6, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(1, 7, document);
		
		
		tab.setWidget(2, 0, new Label(AON.MSG.comments()));
		tab.getCellFormatter().setStyleName(2,0, AON.CSS.aonSearchPanelLabel());
		
		tab.setWidget(2, 1, comments);

		tab.setWidget(2, 2, new Label("Id Interno"));
		tab.getCellFormatter().setStyleName(2,2, AON.CSS.aonSearchPanelLabel());
		tab.getCellFormatter().addStyleName(2,2, AON.CSS.aonTextRight());
		
		tab.setWidget(2, 3, id);

		tab.setWidget(2, 4, new Label("Orden"));
		tab.getCellFormatter().setStyleName(2,4, AON.CSS.aonSearchPanelLabel());
		
		tab.setWidget(2, 5, order);
		tab.getFlexCellFormatter().setColSpan(2, 5, 2);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(2, 6, buttonsPanel);

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		northPanel.setWidget(scrollPanel);
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
	
	private void onSearch( AccountingReportModuleOptions options ) {
		AccountEntryParams params = getWidgetParams(options);
		JournalPanel journalPanel = new JournalPanel(options, params);
		journalPanel.addSelectionHandler(new AccountEntrySelectionHandler() {
			
			@Override
			public void onSelection(AccountEntrySelectionEvent event) {
				AccountEntrySelectionEvent.fire(JournalPanelReport.this, event.getSelectedItem(), event.getCallback() );
			}

		});
		centerPanel.setWidget(journalPanel);
	}

	public AccountEntryParams getWidgetParams( AccountingReportModuleOptions options ) {
		Integer activityId = null;
		if (activitiesListBoxEnabled) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		return new AccountEntryParams()
			.setAccountEntryId(id.getValue())
			.setDomain(options.getDomain())
			.setPeriod(period.getValue())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setType(entryListBox.getValue())
			.setJournal(journal.getValue())
			.setActivity(activityId)
			.setAccount(account.getId())
			.setBalancingAccount(account.getId())
			.setDebit(debit.getValue())
			.setCredit(credit.getValue())
			.setConcept(concept.getValue())
			.setDocument(document.getValue())
			.setComments(comments.getValue())
			.setSecurityLevel(confidential!=null?SecurityLevel.safeValueOf(confidential.getSelectedIndex()):SecurityLevel.OFFICIAL)
			.setOrder(order.getSelectedIndex());
	}
	
}
