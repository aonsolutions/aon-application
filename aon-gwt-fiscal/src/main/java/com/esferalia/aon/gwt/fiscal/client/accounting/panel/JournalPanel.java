package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class JournalPanel extends ScrollPanel implements HasAccountEntrySelectionHandlers{

	private static final Logger LOGGER = Logger.getLogger(JournalPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final String ACCOUNT_ENTRY_STREAM_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/AccountEntryFlatStreamServlet");
	
	private static final int LIMIT = 101;
	private Integer oldId = -1;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private FlowPanel container;
	private AccountEntryPanel entryPanel;
	private int lastScrollPos = 0;
	
	public JournalPanel(AccountingReportModuleOptions options, AccountEntryParams params) {
		
		setStyleName(AON.CSS.aonTextCenter());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		
		container = new FlowPanel();
		setWidget(container);
		
		addScrollHandler(event -> {
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
					search(options,offset.getValue(),params);
				}
			}
		});
		onSearch(options, params);
	}

	public boolean isSearchEnabled() {
		return searchEnabled.getValue();
	}
	public void disableSearch() {
		searchEnabled.setValue( false );
	}
	public void enableSearch() {
		searchEnabled.setValue( true );
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
	
	private void onSearch(AccountingReportModuleOptions options, AccountEntryParams params) {
		enableMoreData();
		search(options, params);
	}

	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

	private void search(AccountingReportModuleOptions options, AccountEntryParams params) {
		container.clear();
		offset.setValue(0);
		oldId = -1;
		search(options, offset.getValue(),params);
	}
	
	private void search(AccountingReportModuleOptions options, final int ofs,AccountEntryParams params) {
		if (!isMoreData()) return;
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, ACCOUNT_ENTRY_STREAM_SERVLET);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new JournalReadyStateChangeHandler(ofs, params.hasDetailProperties()) );
		
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + options.getDomainName()  );
		requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + options.getDomain() );
		requestData.append("&"+IRequestParamsNames.USER					+"=" + options.getUser() );
		requestData.append("&"+IRequestParamsNames.ACCOUNT_ENTRY_PARAMS +"=" + JsonParams.convert( params ));
		requestData.append("&"+IRequestParamsNames.OFFSET 				+"=" + ofs );
		requestData.append("&"+IRequestParamsNames.LIMIT				+"=" + LIMIT );
		xhr.send(requestData.toString());
	}
	
	private static class AccountEntryPanel extends FocusPanel {
		
		private final FlowPanel container;
		private final MutableDouble sumD = new MutableDouble(0.0);
		private final MutableDouble sumC = new MutableDouble(0.0);
		
		public AccountEntryPanel() {
			setTabIndex(Integer.MAX_VALUE);
			container = new FlowPanel("pre");
			container.setStyleName(AON.CSS.aonClickableBlock());		
			container.addStyleName(AON.CSS.aonFixedFont());
			container.addStyleName(AON.CSS.aonMarginBottom());
			setWidget(container);
		}
		
		public AccountEntryPanel addHeader(AccountEntry entry) {
			Label header = new Label();
			header.setStyleName(AON.CSS.aonBold());
			header.addStyleName(AON.CSS.aonTextUnderline());
			header.addStyleName(AON.CSS.aonPre());
			header.setText(toString(entry));
			if (AonStringUtils.isNotBlank(entry.getComments())) {
				header.setTitle(entry.getComments());
			}
			container.add(header);
			return this;
		}

		public AccountEntryPanel addDetail(AccountEntryDetail det) {
			Label detail = new Label(toString(det));
			detail.setStyleName(null);
			container.add(detail);
			sumD.setValue( AonMathUtils.sum(sumD.getValue(), det.getDebit())); 	
			sumC.setValue( AonMathUtils.sum(sumC.getValue(), det.getCredit()));
			return this;
		}

		
		public AccountEntryPanel addFooter() {
			Label totals = new Label(toString(new AccountEntryDetail(),sumD.getValue(),sumC.getValue()));
			totals.setStyleName(AON.CSS.aonBold());
			container.add(totals);
			return this;
		}

		private String toString(AccountEntry entry) {
			int lineSize = 172; 
			StringBuilder buf = new StringBuilder();
			buf.append(entry.isConfidential()
				?AonStringUtils.OPEN_BRACKET + AON.MSG.confidential() + AonStringUtils.CLOSE_BRACKET + AonStringUtils.SPACE
				:AonStringUtils.repeat(AonStringUtils.SPACE, 14));
			buf.append(AonStringUtils.SPACE);
			buf.append((entry.getActivity() == null) 
				?AonStringUtils.repeat(AonStringUtils.SPACE, 40)
				:AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(entry.getActivityDescription()), 39), 40));
			buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 8));
			buf.append(AON.MSG.date());
			buf.append(AonStringUtils.COLON);
			buf.append(AonStringUtils.SPACE);
			buf.append(AON.DATE_FORMAT.format(entry.getEntryDate()));
			buf.append(AonStringUtils.SPACE);
			buf.append(AON.MSG.journal());
			buf.append(AonStringUtils.COLON);
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad(entry.getJournal()==null?"????":""+entry.getJournal(),10));
			buf.append(AonStringUtils.SPACE);
			if (AonStringUtils.isNotBlank(entry.getComments())) {
				buf.append(AonStringUtils.OPEN_BRACKET);
				buf.append(AonStringUtils.abbreviate(AonStringUtils.removeTabsAndNewLine(entry.getComments()), 38));
				buf.append(AonStringUtils.CLOSE_BRACKET);
			} else {
				buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 40));
			}
			buf.append(AonStringUtils.leftPad(entry.getEntryType().getDescription(), lineSize - buf.length()));
			return buf.toString();
		}
		
		public static String toString(AccountEntryDetail detail) {
			return toString(detail,detail.getDebit(),detail.getCredit());
		}
		
		public static String toString(AccountEntryDetail detail, double debit, double credit) {
			StringBuilder buf = new StringBuilder();
			buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getAccountCode()),10));
			buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(detail.getAccountDescription()), 39), 40));
			buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(detail.getConcept()), 45), 45));
			buf.append(AonStringUtils.leftPad(AON.FMT.format(debit),17));		
			buf.append(AonStringUtils.leftPad(AON.FMT.format(credit),17));
			buf.append(AonStringUtils.center(AonStringUtils.defaultString(detail.getBalancingAccountCode()),11));
			buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getDocumentNumber()), 33));
			return buf.toString();
		}
	}
	
	private class JournalReadyStateChangeHandler implements ReadyStateChangeHandler {
		final int ofs;
		final boolean detailProperties;
		
		JournalReadyStateChangeHandler(int ofs, boolean detailProperties) {
			this.ofs = ofs;
			this.detailProperties = detailProperties; 
		}
		
		@Override
		public void onReadyStateChange(XMLHttpRequest xhr) {
			int state = xhr.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				String text = xhr.getResponseText();
				try {
					giveResponse( text );
				} catch (IndexOutOfBoundsException e) {
					addMessage( e.getMessage() );
				}
				enableSearch();
			}
		}
		
		private void giveResponse(String text) {
			int count = 0;
			if (!JsonUtils.safeToEval(text)) {
				addMessage( "ERROR de evaluación" );
				return;
			}
			JavaScriptObject unk = JsonUtils.safeEval(text);
			JsArray<JsFlatAccountEntry> array = unk.cast();
			if (array.length() == 0) {
				if (entryPanel != null) {
					entryPanel.addFooter();
				}
				addMessage( AON.MSG.noData() );
			} else {
				int accountEntries = 0;
				for (; count < array.length(); count++ ) {
					JsFlatAccountEntry flatEntry = array.get(count);
					if (!AonNumberUtils.equals( flatEntry.getEntryId(), oldId)) {
						if (entryPanel != null) {
							entryPanel.addFooter();
						}
						oldId = flatEntry.getEntryId();
						final FlowPanel entrycontainer = new FlowPanel();
						container.add(entrycontainer);
						entryPanel = getAccountEntryPanel(entrycontainer, newAccountEntry(flatEntry));
						entrycontainer.add(entryPanel);
						++accountEntries;
					}
					AccountEntryDetail detail = newAccountEntryDetail(flatEntry);
					entryPanel.addDetail( detail );
				}
				if (array.length() < LIMIT && entryPanel != null) {
					entryPanel.addFooter();
					entryPanel = null;
					addMessage( AON.MSG.noMoreData() );
				} else {
					enableMoreData();
				}
				if (detailProperties) {
					offset.setValue(ofs + accountEntries);
				} else {
					offset.setValue(ofs + count);
				}
			}
		}

		private void addMessage(String message) {
			FlowPanel line = new FlowPanel();
			line.add(new InlineLabel(message));
			container.add(line);
			disableMoreData();
		}

		private AccountEntryDetail newAccountEntryDetail(JsFlatAccountEntry flatEntry) {
			AccountEntryDetail out = new AccountEntryDetail();
			out.setDomain(flatEntry.getEntryDomain());
			out.setId(flatEntry.getDetailId());
			out.setAccountEntry(flatEntry.getEntryId());
			out.setAccountId(flatEntry.getAccount());
			out.setAccountCode(flatEntry.getAccountCode());
			out.setAccountDescription(flatEntry.getAccountDescription());
			out.setLine(flatEntry.getLine());
			out.setConcept(flatEntry.getConcept());
			out.setDebit(AonNumberUtils.zeroIfNull(flatEntry.getDebit()));
			out.setCredit(AonNumberUtils.zeroIfNull(flatEntry.getCredit()));
			out.setBalancingAccountId(flatEntry.getBalancingAccount());
			out.setBalancingAccountCode(flatEntry.getBalancingAccountCode());
			out.setBalancingAccountDescription(flatEntry.getBalancingAccountDescription());
			out.setDocumentNumber(flatEntry.getDocumentNumber());
			return out;
		}

		private AccountEntry newAccountEntry(JsFlatAccountEntry ori) {
			AccountEntry out = new AccountEntry();
			out.setDomain(ori.getEntryDomain());
			out.setId(ori.getEntryId());
			out.setPeriod(ori.getEntryPeriod());
			out.setPeriodName(ori.getEntryPeriodName());
			out.setEntryDate(ori.getEntryDate());
			out.setEntryType(AccountEntryType.safeValueOf( ori.getEntryType()));
			out.setActivity(ori.getActivity());
			out.setActivityDescription(ori.getActivityName());
			out.setJournal(ori.getJournal());
			if ("0".equals(""+ori.getSecurityLevel())) {
				out.setSecurityLevel(SecurityLevel.OFFICIAL);
			}
			if ("1".equals(""+ori.getSecurityLevel())) {
				out.setSecurityLevel(SecurityLevel.CONFIDENTIAL);
			}
			out.setComments(ori.getComments());
			return out;
		}

		private AccountEntryPanel getAccountEntryPanel(FlowPanel entrycontainer, AccountEntry entry) {
			final AccountEntryPanel ep = new AccountEntryPanel();
			ep.addHeader(entry);
			ep.addClickHandler(e -> { 
				entrycontainer.addStyleName( AON.CSS.aonBackgroundLigthYellow());
				AccountEntrySelectionEvent.fire( JournalPanel.this, entry, new ModuleCallback() {
					private static final long serialVersionUID = -1716981945272019639L;
	
					@Override
					public void onRemove(IAccountEntryWrapper removed) {
						entrycontainer.removeStyleName( AON.CSS.aonBackgroundLigthYellow());
						entrycontainer.remove(ep);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						entrycontainer.removeStyleName( AON.CSS.aonBackgroundLigthYellow());
					}
					
					@Override
					public void onExit() {
						entrycontainer.removeStyleName( AON.CSS.aonBackgroundLigthYellow());
					}
					
					@Override
					public void onChange(IAccountEntryWrapper changed) {
						entrycontainer.removeStyleName( AON.CSS.aonBackgroundLigthYellow());
						int index = entrycontainer.getWidgetIndex( ep );
						entrycontainer.remove( ep );
						AccountEntryPanel newEp = getAccountEntryPanel(entrycontainer, changed.getAccountEntry() );
						AonCollectionUtils.stream(changed.getAccountEntry().getDetails())
							.forEach( newEp::addDetail );
						newEp.addFooter();
						newEp.addStyleName(AON.CSS.aonValueChanged());
						entrycontainer.insert( newEp , index);
						new Timer() {
							@Override
							public void run() {
								newEp.removeStyleName(AON.CSS.aonValueChanged());
							}
						}.schedule(3000);
					}
				});
			});
			return ep ;
		}
		
	}
}
