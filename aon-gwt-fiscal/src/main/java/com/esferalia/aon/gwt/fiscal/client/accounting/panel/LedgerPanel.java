package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class LedgerPanel extends ScrollPanel implements HasAccountEntrySelectionHandlers{

	private static final Logger LOGGER = Logger.getLogger(LedgerPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final String LEDGER_STREAM_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/AccountStatementStreamServlet");

	private static final int LINE_LENGTH = 185;
	
	private final int limit = 100;
	private Integer oldId = -1;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private FlowPanel container;
	private int lastScrollPos = 0;
	
	public LedgerPanel(final AccountingReportModuleOptions options, AccountingReportParams params) {
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		
		setStyleName(AON.CSS.aonTextCenter());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		
		container = new FlowPanel("pre");
		container.addStyleName(AON.CSS.aonFixedFont());
		container.addStyleName(AON.CSS.aonFontSmaller());

		setWidget(container);
		
		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						search(options,params, offset.getValue(),limit, null);
					}
				}
			}
		});
		onSearch(options, params);
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
	
	private void onSearch(final AccountingReportModuleOptions options, AccountingReportParams params) {
		enableMoreData();
		search(options, params);
	}

	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

	private void search(final AccountingReportModuleOptions options, AccountingReportParams params) {
		offset.setValue(0);
		container.clear();
		
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.rightPad("N\u00BA Diario",10));
		buf.append(AonStringUtils.rightPad("Fecha",11));
		buf.append(AonStringUtils.rightPad("Concepto", 53));
		buf.append(AonStringUtils.leftPad("Debe",17));		
		buf.append(AonStringUtils.leftPad("Haber",17));
		buf.append(AonStringUtils.leftPad("Saldo Deudor",17));
		buf.append(AonStringUtils.leftPad("Saldo Acreedor",17));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad("Contrapartida",26));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad("N\u00BA Documento",15));
		Label headerLabel = new Label();
		headerLabel.setStyleName(AON.CSS.aonBold());
		headerLabel.addStyleName(AON.CSS.aonMarginTop());
		headerLabel.addStyleName(AON.CSS.aonBorderBottom());
		headerLabel.addStyleName(AON.CSS.aonBorderTop());
		headerLabel.addStyleName(AON.CSS.aonPre());
		headerLabel.setText(buf.toString());
		container.add(headerLabel);
		
		
		oldId = -1;
		search(options,params,offset.getValue(),limit, null);
	}
	
	private void search(final AccountingReportModuleOptions options, AccountingReportParams params, final int ofs,int  limit, Integer scrollPosition) {
		if (!isMoreData()) return;
		final AonToast toast = new AonToast();
		final InlineLabel label =  new InlineLabel("Un momento, por favor ...");
		toast.show("Cargando ...", label);

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, LEDGER_STREAM_SERVLET);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				boolean something = false;
				if (state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						int count = 0;
						if (!JsonUtils.safeToEval(text)) {
							Window.alert("ERROR de evaluación");
						}
						JavaScriptObject unk = JsonUtils.safeEval(text);
						JsArray<JsFlatAccountEntryDetail> array = unk.cast();
						
						for (int i = 0; i < array.length(); i++ ) {
							JsFlatAccountEntryDetail flatEntry = array.get(i);
							if (!AonNumberUtils.equals( flatEntry.getAccount(), oldId)) {
								Label accountLabel = new Label();
								accountLabel.setStyleName(AON.CSS.aonBold());
								accountLabel.addStyleName(AON.CSS.aonMarginTop());
								accountLabel.addStyleName(AON.CSS.aonMarginBottom());
								accountLabel.addStyleName(AON.CSS.aonTextUnderline());
								accountLabel.addStyleName(AON.CSS.aonPre());
								accountLabel.setText(AonStringUtils.center( 
										(flatEntry.getAccountCode() + AonStringUtils.SPACE + flatEntry.getAccountDescription())
										, LINE_LENGTH));
								container.add(accountLabel);
								if (AonMathUtils.isNotZero( flatEntry.getInitialDebitBalance()) || AonMathUtils.isNotZero( flatEntry.getInitialUnpaidBalance())) {
									Label initialLabel = new Label();
									initialLabel.setStyleName(AON.CSS.aonPre());
									initialLabel.addStyleName(AON.CSS.aonColorBlue());
									initialLabel.setText(
										AonStringUtils.rightPad( 
											  AonStringUtils.repeat(" ",21)
											+ AonStringUtils.leftPad("Saldo anterior al " + AON.DATE_FORMAT.format(params.getFromDate()), 53)
											+ AonStringUtils.repeat(" ",17)		
											+ AonStringUtils.repeat(" ",17)
											+ AonStringUtils.leftPad(AON.FMT.format(flatEntry.getInitialDebitBalance()),17)		
											+ AonStringUtils.leftPad(AON.FMT.format(flatEntry.getInitialUnpaidBalance()),17)
											, LINE_LENGTH));
									container.add(initialLabel);
								}
								oldId = flatEntry.getAccount();
								
							} 
							
							StringBuffer buf = new StringBuffer();
							buf.append(AonStringUtils.rightPad(("" + flatEntry.getJournal()),10));
							buf.append(AonStringUtils.rightPad(AON.DATE_FORMAT.format(flatEntry.getEntryDate()),11));
							buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(flatEntry.getConcept()), 52), 53));
							buf.append(AonStringUtils.leftPad(AON.FMT.format(flatEntry.getDebit()),17));		
							buf.append(AonStringUtils.leftPad(AON.FMT.format(flatEntry.getCredit()),17));
							buf.append(AonStringUtils.leftPad(AON.FMT.format(flatEntry.getDebitBalance()),17));		
							buf.append(AonStringUtils.leftPad(AON.FMT.format(flatEntry.getUnpaidBalance()),17));
							buf.append(AonStringUtils.SPACE);
							buf.append(AonStringUtils.rightPad(AonStringUtils.defaultIfBlank(flatEntry.getBalancingAccountCode(),""),10));
							buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(flatEntry.getBalancingAccountDescription(),""),15),16));
							buf.append(AonStringUtils.SPACE);
							buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(flatEntry.getDocumentNumber(),""),14),15));
							Label entryLabel = new Label();
							entryLabel.setStyleName(AON.CSS.aonPre());
							entryLabel.addStyleName(AON.CSS.aonClickableBlock());
							entryLabel.setText(buf.toString());
							container.add(entryLabel);
							entryLabel.addClickHandler(new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									AccountEntry ae = new AccountEntry()
											.setId(flatEntry.getEntryId())
											.setDomain( options.getDomain() );
									AccountEntrySelectionEvent.fire(LedgerPanel.this, ae, new ModuleCallback() {
										
										@Override
										public void onRemove(IAccountEntryWrapper removed) {
											int scrollPosition = LedgerPanel.this.getVerticalScrollPosition();
											container.clear();
											oldId = -1;
											search(options,params,0,offset.getValue(), Integer.valueOf( scrollPosition) );
										}
										
										@Override
										public void onFailure(Throwable caught) {
										}
										
										@Override
										public void onExit() {
										}
										
										@Override
										public void onChange(IAccountEntryWrapper changed) {
											int scrollPosition = LedgerPanel.this.getVerticalScrollPosition();
											container.clear();
											oldId = -1;
											enableMoreData();
											search(options,params,0,offset.getValue() > limit ? offset.getValue() : limit, Integer.valueOf( scrollPosition) );
										}
									} );
								}
							});
							params.setLedgerAccount(flatEntry.getAccount());
							params.setLedgerDebitBalance(flatEntry.getDebitBalance());
							params.setLedgerUnpaidBalance(flatEntry.getUnpaidBalance());
							
							something = true;
							count++;
						}
						offset.setValue(ofs + count);
						enableMoreData();
					} catch (IndexOutOfBoundsException e) {
						FlowPanel line = new FlowPanel();
						InlineLabel label = new InlineLabel(e.getMessage());
						line.add(label);
						container.add(line);
						enableSearch();
					}
				}
				if (state == XMLHttpRequest.DONE) {
					if (!something) {
						FlowPanel line = new FlowPanel();
						InlineLabel label = new InlineLabel(AON.MSG.noData());
						line.add(label);
						container.add(line);
						disableMoreData();
					}
					toast.hide();
					enableSearch();
					if (scrollPosition != null) {
						LedgerPanel.this.setVerticalScrollPosition(scrollPosition);
					}
				}
			}
			
		});
		StringBuffer requestData = new StringBuffer();
		requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + options.getDomainName() );
		requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + options.getDomain() );
		requestData.append("&"+IRequestParamsNames.USER					+"=" + options.getUser() );
		requestData.append("&"+IRequestParamsNames.ACCOUNT_ENTRY_PARAMS +"=" + JsonParams.convert( params ));
		requestData.append("&"+IRequestParamsNames.OFFSET 				+"=" + ofs );
		requestData.append("&"+IRequestParamsNames.LIMIT				+"=" + limit );
		requestData.append("&"+IRequestParamsNames.LEDGER_ACCOUNT		+"=" + (params.getLedgerAccount() != null ? params.getLedgerAccount() : "" ));
		requestData.append("&"+IRequestParamsNames.LEDGER_DEBIT_BALANCE +"=" + params.getLedgerDebitBalance() );
		requestData.append("&"+IRequestParamsNames.LEDGER_UNPAID_BALANCE+"=" + params.getLedgerUnpaidBalance() );
		xhr.send(requestData.toString());
	}
	
	
}
