package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.AccountEntryListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
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


public class JournalPanelReport extends DockLayoutPanel implements Focusable, HasSelectionHandlers<AccountEntry>{

	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	
	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanel;
	
	private FlexTable tab;
	private AccountPeriodBox period;
	private FlowPanel datePanel; 
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private AccountEntryListBox entryListBox;
	private CheckBox confidential;
	private IntegerBox journal;
	private AccountBox account;
	private DoubleBox debit;
	private DoubleBox credit;
	private TextBox concept;
	private TextBox document;
	private ListBox activity;
	private TextBox comments;
	private ListBox order;
	private Button cleanButton;
	
	
	
	public JournalPanelReport(String domainName,String user, int domainId) {
		this(domainName,user,domainId,Integer.MAX_VALUE,null);
	}
	public JournalPanelReport(String domainName,String user,int domainId, int tabIndex, AonConfiguration config) {
		super(Unit.PX);
		this.currentDomainName = domainName;
		this.currentUser = user;
		this.currentDomainId = domainId;
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		
		northPanel = new SimpleLayoutPanel();
		fillNorthPanel(tabIndex,config);
		addNorth(northPanel, 110);
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		onSearch(config);
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
	private void fillNorthPanel(int tabIndex,final AonConfiguration config) {
		period = new AccountPeriodBox();
		period.fill(config.getPeriods(),true);
		period.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch(config);
			}
		});
		
		fromDate = new DateBoxEx();
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch(config);
			}
		});
		toDate = new DateBoxEx();
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch(config);
			}
		});
		confidential = new CheckBox(AON.MSG.confidential());
		confidential.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onSearch(config);
			}
		});
		account = new AccountBox(this.currentDomainName,this.currentDomainId);
		account.setRequired(false);
		account.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				onSearch(config);
			}
		});
		debit = new DoubleBox();
		debit.setVisibleLength(8);
		debit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				onSearch(config);
			}
		});
		credit = new DoubleBox();
		credit.setVisibleLength(8);
		credit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				onSearch(config);
			}
		});
		entryListBox = new AccountEntryListBox();
		entryListBox.setWidth("120px");
		entryListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch(config);
			}
		});
		
		journal = new IntegerBox();
		journal.setVisibleLength(8);
		journal.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				onSearch(config);
			}
		});
		concept = new TextBox();
		concept.setVisibleLength(10);
		concept.setStyleName(AON.AON_CSS.aonInputText());
		concept.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch(config);
			}
		});
		document = new TextBox();
		document.setVisibleLength(10);
		document.setStyleName(AON.AON_CSS.aonInputText());
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch(config);
			}
		});
		
		comments = new TextBox();
		comments.setVisibleLength(30);
		comments.setStyleName(AON.AON_CSS.aonInputText());
		comments.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch(config);
			}
		});
		
		if (config != null && config.hasActivities()) {
			activity = new ListBox();
			activity.setWidth("150px");
			activity.addItem("-- Todas --", "");
			activity.setSelectedIndex(0);
			int i = 1;
			for (EnterpriseActivity ea : config.getActivities()) {
				activity.addItem(ea.getDescription(), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch(config);
				}
			});
		}
		
		order = new ListBox();
		order.setWidth("200px");
		order.addItem("Ejerc., n\u00BA diario, fecha");
		order.addItem("Fecha creaci\u00F3n, descendente");
		order.addItem("Fecha modificaci\u00F3n, descendente");
		order.setSelectedIndex(0);
		order.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch(config);
			}
		});
		
		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "1%");
		tab.getColumnFormatter().setWidth(5, "1%");
		tab.getColumnFormatter().setWidth(6, "1%");
		tab.getColumnFormatter().setWidth(7, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonPanelGridOdd());
		
		datePanel = new FlowPanel();
		datePanel.setStyleName(AON.AON_CSS.aonNowrap());
		datePanel.add(period);
		period.addStyleName(AON.AON_CSS.aonMarginRight());
		datePanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		datePanel.add(to);
		datePanel.add(toDate);
		tab.setWidget(0, 1, datePanel);
		tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(0, 2, new Label(AON.MSG.type()));
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonPanelGridOdd());
		
		FlowPanel entryTypePanel = new FlowPanel();
		entryTypePanel.add(entryListBox);
		if (datePanel != null && config.getUser() != null && config.getUser().hasConfidentialityRole()) {
			confidential.addStyleName(AON.AON_CSS.aonMarginLeft());
			entryTypePanel.add(confidential);
		}
		tab.setWidget(0, 3, entryTypePanel);
		tab.getCellFormatter().setStyleName(0,3, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(0, 4, new Label(AON.MSG.journal()));
		tab.getCellFormatter().setStyleName(0,4, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(0, 5, journal);
		tab.getCellFormatter().setStyleName(0,5, AON.AON_CSS.aonPanelGridEven());
		
		if (config != null && config.hasActivities()) {
			tab.setWidget(0, 6, new Label(AON.MSG.activity()));
			tab.setWidget(0, 7, activity);
		} else {
			tab.setWidget(0, 6, new Label());
			tab.setWidget(0, 7, new Label());
		}
		tab.getCellFormatter().setStyleName(0,6, AON.AON_CSS.aonPanelGridOdd());
		tab.getCellFormatter().setStyleName(0,7, AON.AON_CSS.aonPanelGridEven());	
		
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
				entryListBox.setValue(null);
				confidential.setValue(false,false);
				journal.setValue(null,false);
				account.setAccount(null, false);;
				debit.setValue(null,false);
				credit.setValue(null,false);
				concept.setValue(null,false);
				document.setValue(null,false);
				if (config != null && config.hasActivities()) {
					activity.setSelectedIndex(0);
				}
				period.setFocus(true);
				onSearch(config);
			}
		});

		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 1, account);
		tab.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		
		tab.setWidget(1, 2, new Label(AON.MSG.debit()));
		tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonPanelGridOdd());
		
		FlowPanel amountsPanel = new FlowPanel();
		amountsPanel.setStyleName(AON.AON_CSS.aonNowrap());
		amountsPanel.add(debit);
		InlineLabel cre= new InlineLabel(AON.MSG.credit());
		cre.setStyleName(AON.AON_CSS.aonBold());
		cre.addStyleName(AON.AON_CSS.aonMarginRight());
		cre.addStyleName(AON.AON_CSS.aonMarginLeft());
		amountsPanel.add(cre);
		amountsPanel.add(credit);
		tab.setWidget(1, 3, amountsPanel);
		tab.getCellFormatter().setStyleName(1,3, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(1, 4, new Label(AON.MSG.concept()));
		tab.getCellFormatter().setStyleName(1,4, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 5, concept);
		tab.getCellFormatter().setStyleName(1,5, AON.AON_CSS.aonPanelGridEven());

		tab.setWidget(1, 6, new Label(AON.MSG.document()));
		tab.getCellFormatter().setStyleName(1,6, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 7, document);
		tab.getCellFormatter().setStyleName(1,7, AON.AON_CSS.aonPanelGridEven());
		
		
		tab.setWidget(2, 0, new Label(AON.MSG.comments()));
		tab.getCellFormatter().setStyleName(2,0, AON.AON_CSS.aonPanelGridOdd());
		
		tab.setWidget(2, 1, comments);
		tab.getCellFormatter().setStyleName(2, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(2, 1, 5);
		
		tab.setWidget(2, 2, order);
		tab.getCellFormatter().setStyleName(2,2, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(2, 2, 2);
		
		cleanButton.setTitle(AON.MSG.clean());
		tab.setWidget(2, 3, cleanButton);
		tab.getCellFormatter().setStyleName(2,3, AON.AON_CSS.aonPanelGridEven());

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		northPanel.setWidget(scrollPanel);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountEntry> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
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
	
	private void onSearch(AonConfiguration config) {
		Integer activityId = null;
		if (config != null && config.hasActivities()) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		AccountEntryParams params = new AccountEntryParams()
			.setDomain(this.currentDomainId)
			.setPeriod(period.getValue())
			.setFrom(fromDate.getValue())
			.setTo(toDate.getValue())
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
			.setConfidential(confidential.getValue())
			.setOrder(order.getSelectedIndex())
		;
		
		JournalPanel journalPanel = new JournalPanel(getCurrentDomainName(), getCurrentUser(), getCurrentDomainId(), params);
		journalPanel.addSelectionHandler(new SelectionHandler<AccountEntry>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountEntry> event) {
				SelectionEvent.fire(JournalPanelReport.this, event.getSelectedItem() );
			}
		});
		centerPanel.setWidget(journalPanel);
	}
	
}
