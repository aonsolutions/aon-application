package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.AccountEntryListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryPrinter;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class JournalPanel extends DockLayoutPanel implements Focusable, HasSelectionHandlers<AccountEntry>{

	static FiscalServiceAsync fiscalService;
	
	private String domainName;
	private int domainId;
	
	final private int limit = 20;
	
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt( 0 ); 

	private SimpleLayoutPanel northPanel;
	private ScrollPanel centerPanel;
	
	private FlowPanel container;
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
	
	
	
	private int lastScrollPos = 0;
	
	public JournalPanel(String domainName,int domainId) {
		this(domainName,domainId,Integer.MAX_VALUE,null);
	}
	
	public JournalPanel(String domainName,int domainId, int tabIndex, AonConfiguration config) {
		super(Unit.PX);
		this.domainName = domainName;
		this.domainId = domainId;
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		northPanel = new SimpleLayoutPanel();
		fillNorthPanel(tabIndex,config);
		addNorth(northPanel, 110);
		centerPanel = new ScrollPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
				
		centerPanel.addStyleName(AON.AON_CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		add(centerPanel);
		
		centerPanel.addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = centerPanel.getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = centerPanel.getWidget().getOffsetHeight() - centerPanel.getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						search(offset.getValue(),config);
					}
				}
			}
		});
		
	}
//	public void setUser(User user) {
//		this.user = user;
//		if (datePanel != null && user != null && user.hasConfidentialityRole()) {
//			datePanel.add(confidential);
//		}
//	}
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	private void onSearch(AonConfiguration config) {
		enableMoreData();
		search(config);
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
		account = new AccountBox(this.domainName,this.domainId);
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
	
	private void search(AonConfiguration config) {
		container.clear();
		offset.setValue(0);
		search(offset.getValue(),config);
	}
	
	private void search(final int ofs,AonConfiguration config) {
		if (!isMoreData()) return;
		Integer activityId = null;
		if (config != null && config.hasActivities()) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		AccountEntryParams params = new AccountEntryParams()
			.setDomain(AccountEntryModule.getCurrentDomain())
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
			.setHasConfidentialityRole(config.getUser() != null && config.getUser().hasConfidentialityRole())
			.setOrder(order.getSelectedIndex())
		;
		
		fiscalService.getAccountEntries(domainName,domainId, params, ofs, limit
				, new AsyncCallback<LinkedList<AccountEntry>>() {
					
					@Override
					public void onSuccess(LinkedList<AccountEntry> result) {
						if (result != null && !result.isEmpty()) {
							for (final AccountEntry entry : result) {
								final FocusPanel entryPanel = AccountEntryPrinter.print(entry);
								container.add(entryPanel);
								entryPanel.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										 SelectionEvent.<AccountEntry>fire( JournalPanel.this, entry);
									}
								});
		
							}
							offset.setValue(ofs + result.size());
							enableMoreData();
						} else {
							FlowPanel line = new FlowPanel();
							InlineLabel label = new InlineLabel(AON.MSG.noData());
							line.add(label);
							container.add(line);
							disableMoreData();
						}
						enableSearch();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						FlowPanel line = new FlowPanel();
						InlineLabel label = new InlineLabel(AON.MSG.noData());
						line.add(label);
						container.add(line);
						enableSearch();
					}
				});
	}
	
}
