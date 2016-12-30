package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

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
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class JournalPanel extends DockLayoutPanel implements Focusable, HasSelectionHandlers<AccountEntry>{

	static FiscalServiceAsync fiscalService;
	
	private String domainName;
	private int domainId;
	private User user;
	
	final private int limit = 20;
	
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt( 0 ); 

	private SimpleLayoutPanel northPanel;
	private ScrollPanel centerPanel;
	
	private FlowPanel container;
	private FlexTable tab;
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
	private Button filter;
	
	
	private int lastScrollPos = 0;
	
	public JournalPanel(String domainName,int domainId) {
		this(domainName,domainId,Integer.MAX_VALUE);
	}
	
	public JournalPanel(String domainName,int domainId, int tabIndex) {
		super(Unit.PX);
		this.domainName = domainName;
		this.domainId = domainId;
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		northPanel = new SimpleLayoutPanel();
		fillNorthPanel(tabIndex);
		addNorth(northPanel, 90);
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
						search(offset.getValue());
					}
				}
			}
		});
		
	}
	public void setUser(User user) {
		this.user = user;
		if (datePanel != null && user != null && user.hasConfidentialityRole()) {
			datePanel.add(confidential);
		}
	}
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
	

	private void fillNorthPanel(int tabIndex) {
		fromDate = new DateBoxEx();
		toDate = new DateBoxEx();
		confidential = new CheckBox(AON.MSG.confidential());
		account = new AccountBox(this.domainName,this.domainId);
		account.setRequired(false);
		debit = new DoubleBox();
		credit = new DoubleBox();
		journal = new IntegerBox();
		concept = new TextBox();
		concept.setStyleName(AON.AON_CSS.aonInputText());
		document = new TextBox();
		document.setStyleName(AON.AON_CSS.aonInputText());
		filter = new Button();
		filter.setText(AON.MSG.searchAction());
		filter.setStyleName(AON.AON_CSS.aonIconCommandButton());
		filter.addStyleName(AON.AON_CSS.aonIconSearch());
		filter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				enableMoreData();
				search();
			}
		});
		
		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		
		tab.getColumnFormatter().setWidth(0, "60px");
		tab.getColumnFormatter().setWidth(1, "360px");
		tab.getColumnFormatter().setWidth(2, "80px");
		tab.getColumnFormatter().setWidth(3, "175px");
		tab.getColumnFormatter().setWidth(4, "110px");
		tab.getColumnFormatter().setWidth(5, "80px");
		tab.getColumnFormatter().setWidth(6, "110px");
		tab.getColumnFormatter().setWidth(7, "auto");
		tab.getColumnFormatter().setWidth(8, "50px");
		
		tab.setWidget(0, 0, new Label(AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonPanelGridOdd());
		
		datePanel = new FlowPanel();
		datePanel.setStyleName(AON.AON_CSS.aonNowrap());
		InlineLabel from = new InlineLabel(AON.MSG.from());
		from.setStyleName(AON.AON_CSS.aonItalic());
		from.addStyleName(AON.AON_CSS.aonMarginRight());
		datePanel.add(from);
		datePanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		datePanel.add(to);
		datePanel.add(toDate);
		tab.setWidget(0, 1, datePanel);
		tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(0, 2, new Label(AON.MSG.accountEntryTypeLabel()));
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonPanelGridOdd());
		
		entryListBox = new AccountEntryListBox();
		tab.setWidget(0, 3, entryListBox);
		tab.getCellFormatter().setStyleName(0,3, AON.AON_CSS.aonPanelGridEven());

		tab.setWidget(0, 4, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(0,4, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(0, 5, account);
		tab.getFlexCellFormatter().setColSpan(0, 5, 3);
		tab.getCellFormatter().setStyleName(0,5, AON.AON_CSS.aonPanelGridEven());
		
		tab.getFlexCellFormatter().setRowSpan(0, 8, 2);
		tab.getCellFormatter().setStyleName(0,8, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0,8, AON.AON_CSS.aonVerticalAlignMiddle());
		tab.getCellFormatter().addStyleName(0,8, AON.AON_CSS.aonTextCenter());
		tab.setWidget(0, 8, filter);
		
		tab.setWidget(1, 0, new Label(AON.MSG.amount()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());

		FlowPanel amountsPanel = new FlowPanel();
		amountsPanel.setStyleName(AON.AON_CSS.aonNowrap());
		InlineLabel deb = new InlineLabel(AON.MSG.debit());
		deb.setStyleName(AON.AON_CSS.aonItalic());
		deb.addStyleName(AON.AON_CSS.aonMarginRight());
		amountsPanel.add(deb);
		amountsPanel.add(debit);
		InlineLabel cre= new InlineLabel(AON.MSG.credit());
		cre.setStyleName(AON.AON_CSS.aonItalic());
		cre.addStyleName(AON.AON_CSS.aonMarginRight());
		cre.addStyleName(AON.AON_CSS.aonMarginLeft());
		amountsPanel.add(cre);
		amountsPanel.add(credit);
		tab.setWidget(1, 1, amountsPanel);
		tab.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(1, 2, new Label(AON.MSG.concept()));
		tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 3, concept);
		tab.getCellFormatter().setStyleName(1,3, AON.AON_CSS.aonPanelGridEven());

		tab.setWidget(1, 4, new Label(AON.MSG.document()));
		tab.getCellFormatter().setStyleName(1,4, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 5, document);
		tab.getCellFormatter().setStyleName(1,5, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(1, 6, new Label(AON.MSG.journal()));
		tab.getCellFormatter().setStyleName(1,6, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 7, journal);
		tab.getCellFormatter().setStyleName(1,7, AON.AON_CSS.aonPanelGridEven());

		FocusPanel focusPanel = new FocusPanel();
		focusPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		focusPanel.setTabIndex(tabIndex);
		focusPanel.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					enableMoreData();
					search();
				}
			}
		});
		focusPanel.setWidget(tab);
		
		northPanel.setWidget(focusPanel);
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
	
	private void search() {
		container.clear();
		offset.setValue(0);
		search(offset.getValue());
	}
	
	private void search(final int ofs) {
		if (!isMoreData()) return; 
		
		AccountEntryParams params = new AccountEntryParams()
			.setDomain(AccountEntryModule.getCurrentDomain())
			.setFrom(fromDate.getValue())
			.setTo(toDate.getValue())
			.setType(entryListBox.getValue())
			.setJournal(journal.getValue())
			.setAccount(account.getId())
			.setDebit(debit.getValue())
			.setCredit(credit.getValue())
			.setConcept(concept.getValue())
			.setDocument(document.getValue())
			.setConfidential(confidential.getValue())
			.setHasConfidentialityRole(user != null && user.hasConfidentialityRole())
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
