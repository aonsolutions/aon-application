package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class AccountBalancePanel extends SplitLayoutPanel implements HasSelectionHandlers<Integer>{
	  
	private static final String SESSION_LOG_BACKGROUND_COLOR = "lightyellow";
	
	static FiscalServiceAsync fiscalService;
	
	private boolean showEntry;
	private boolean showBalances;
	
	private SessionLog entryPanel;
	private FlowPanel headerPanel;
	private ScrollPanel scrollCenter; 
	private FlowPanel center;
	private HashSet<Integer> accounts;

	public AccountBalancePanel(boolean showEntry, boolean showBalances ) {
		super();
		this.showEntry = showEntry;
		this.showBalances = showBalances;
		
		accounts = new HashSet<Integer>();
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		if (showEntry) {
			entryPanel = new SessionLog();
			entryPanel.getElement().getStyle().setBackgroundColor(SESSION_LOG_BACKGROUND_COLOR);
			if (showBalances) {
				addNorth(entryPanel, 150);
			} else {
				scrollCenter = new ScrollPanel();
				scrollCenter.addStyleName(AON.AON_CSS.aonMarginBottom());
				scrollCenter.setWidget(entryPanel);
				add(scrollCenter);
			}
		}
		if (showBalances) {
			headerPanel = new FlowPanel("pre");
			headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
			headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
			Label header = new Label(" CUENTA CONTABLE                         "
					+ "PERIODO                                    "
					+ "             DEBE"
					+ "            HABER"
					+ "     SALDO DEUDOR"
					+ "   SALDO ACREEDOR");
			header.setStyleName(AON.AON_CSS.aonBold());
			header.addStyleName(AON.AON_CSS.aonBorderTop());
			header.addStyleName(AON.AON_CSS.aonBorderBottom());
			headerPanel.add(header);
			
			center = new FlowPanel();
			center.add(headerPanel);
			scrollCenter = new ScrollPanel();
			scrollCenter.addStyleName(AON.AON_CSS.aonMarginBottom());
			scrollCenter.setWidget(center);
			add(scrollCenter);
		}
	}
	
	public void preview( final IAccountEntryWrapper entry) {
		if (showEntry) {
			entryPanel.clear();
			entryPanel.addPreview(entry);
		}
	}
	public void preview( final IAccountEntryWrapper[] entries) {
		if (showEntry) {
			entryPanel.clear();
			entryPanel.addPreview(entries);
		}
	}
	
	public void add( final AccountEntry entry) {
		if (showBalances) {
			clearBalances();
			for (AccountEntryDetail detail : entry.getDetails()) {
				Account account = new Account()
						.setId(detail.getAccount())
						.setCode(detail.getAccountCode())
						.setDescription(detail.getAccountDescription());
				Date from = DateUtils.getFirstDayOfYear(entry.getEntryDate());
				add(account, from, entry.getEntryDate());
			}
		}
	}
	public void add( final Account account,final Date from,final  Date to ) {
		if (showBalances) {
			if (account != null && !accounts.contains(account.getId())) {
				accounts.add(account.getId());
				final FlowPanel panel = new FlowPanel("pre");
				panel.setStyleName(AON.AON_CSS.aonFixedFont());
				panel.addStyleName(AON.AON_CSS.aonFontMedium());
				panel.addStyleName(AON.AON_CSS.aonMarginBottom());
				panel.addStyleName( (center.getWidgetCount() % 2 == 0)
							?AON.AON_CSS.aonOddBackground()
							:AON.AON_CSS.aonEvenBackground()
						);
				final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
						+AonStringUtils.rightPad(AonStringUtils.defaultString(account.getCode()),10)
						+AonStringUtils.rightPad(AonStringUtils.abbreviate(
								AonStringUtils.defaultString(account.getDescription()), 29), 30)
						);
				acc.setTitle(AON.MSG.accountStatetement());
				acc.setStyleName(AON.AON_CSS.aonBold());
				acc.addStyleName(AON.AON_CSS.aonClickableLabel());
				final Integer id = account.getId();
				acc.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						SelectionEvent.<Integer>fire(AccountBalancePanel.this, id);
					}
				});
				AccountingReportParams params = new AccountingReportParams();
				params.setAccount(account);
				params.setFromDate(from);
				params.setToDate(to);
				
				fiscalService.getAccountBalance(AccountEntryModule.getCurrentDomainName(),
						AccountEntryModule.getCurrentDomain(), params,  
						new AsyncCallback<LinkedList<AccountStatement>>() {
							
							@Override
							public void onSuccess(LinkedList<AccountStatement> result) {
								if (result != null && result.size() > 0) {
									for (AccountStatement as : result) {
										FlowPanel line = new FlowPanel();
										line.add(panel.getWidgetCount() == 0?acc
												:new InlineLabel(AonStringUtils.repeat(AonStringUtils.SPACE, 41)));
										InlineLabel label = new InlineLabel(
												format(as.getConcept()
														,as.getDebit()
														,as.getCredit()
														,as.getDebitBalance()
														,as.getUnpaidBalance())
												 );
										line.add(label);
										panel.add(line);
									}
								} else {
									FlowPanel line = new FlowPanel();
									line.add(panel.getWidgetCount() == 0?acc
											:new InlineLabel(AonStringUtils.repeat(AonStringUtils.SPACE, 40)));
									InlineLabel label = new InlineLabel(format(AON.MSG.noData()));
									line.add(label);
									panel.add(line);
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								InlineLabel fakeAcc = new InlineLabel(AonStringUtils.repeat(AonStringUtils.SPACE, 40));
								FlowPanel line = new FlowPanel();
								line.add(panel.getWidgetCount() == 0?acc:fakeAcc);
								InlineLabel label = new InlineLabel(format(AON.MSG.noData()));
								line.add(label);
								panel.add(line);
							}
							
							private String format(String description) {
								return format(description,0,0,0,0);
							}
							private String format(String description, double debit, double credit, double debitBalance, double unpaidBalance) {
								return AonStringUtils.rightPad(description, 43)
									+AonStringUtils.leftPad(AonMathUtils.isZero(debit)
											?AonStringUtils.SPACE:AON.FMT.format(debit),17)		
									+AonStringUtils.leftPad(AonMathUtils.isZero(credit)
											?AonStringUtils.SPACE:AON.FMT.format(credit),17)
									+AonStringUtils.leftPad(AonMathUtils.isZero(debitBalance)
											?AonStringUtils.SPACE:AON.FMT.format(debitBalance),17)		
									+AonStringUtils.leftPad(AonMathUtils.isZero(unpaidBalance)
											?AonStringUtils.SPACE:AON.FMT.format(unpaidBalance),17)
									+AonStringUtils.repeat(AonStringUtils.SPACE, 10);
							}
						});
				push(panel);
				scrollCenter.scrollToTop();
			}
		}
	}

	private void push( final Widget w ) {
		w.addStyleName(AON.AON_CSS.aonValueChanged());
		new Timer() {
			@Override
			public void run() {
				w.removeStyleName(AON.AON_CSS.aonValueChanged());
			}
		}.schedule(400);
		center.insert(w,1);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	public void clearBalances() {
		if (showEntry) {
			entryPanel.clear();
		}
		if (showBalances) {
			accounts.clear();
			center.clear();
			center.add(headerPanel);
		}
	}
}
