package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryPrinter;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class JournalPanel extends ScrollPanel implements HasAccountEntrySelectionHandlers{

	private static final Logger LOGGER = Logger.getLogger(JournalPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final String ACCOUNT_ENTRY_STREAM_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/AccountEntryFlatStreamServlet");
	
	private String domainName;
	private String user;
	private int domainId;
	private final int limit = 100;
	private Integer oldId = -1;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private AccountEntry entry = null;
	private FlowPanel container;
	private int lastScrollPos = 0;
	
	public JournalPanel(String domainName,String user,int domainId, AccountEntryParams params) {
		
		this.domainName = domainName;
		this.user = user;
		this.domainId = domainId;
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		
		setStyleName(AON.AON_CSS.aonTextCenter());
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		
		container = new FlowPanel();
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
						search(offset.getValue(),params);
					}
				}
			}
		});
		onSearch(params);
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
	
	private void onSearch(AccountEntryParams params) {
		enableMoreData();
		search(params);
	}

	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

	private void search(AccountEntryParams params) {
		container.clear();
		offset.setValue(0);
		oldId = -1;
		search(offset.getValue(),params);
	}
	
	private void search(final int ofs,AccountEntryParams params) {
		if (!isMoreData()) return;
		
		final AonToast toast = new AonToast();
		final InlineLabel label =  new InlineLabel("Un momento, por favor ...");
		toast.show("Cargando ...", label);
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, ACCOUNT_ENTRY_STREAM_SERVLET);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				boolean somethingPrinted = false;
				boolean something = false;
				if (state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						int count = 0;
						if (!JsonUtils.safeToEval(text)) {
							Window.alert("ERROR de evaluación");
						}
						JavaScriptObject unk = JsonUtils.safeEval(text);
						JsArray<JsFlatAccountEntry> array = unk.cast();
						for (int i = 0; i < array.length(); i++ ) {
							JsFlatAccountEntry flatEntry = array.get(i);
							if (!AonNumberUtils.equals( flatEntry.getEntryId(), oldId)) {
								if (entry != null) {
									final FlowPanel entrycontainer = new FlowPanel();
									container.add(entrycontainer);
									paintEntry(entrycontainer, entry);
									somethingPrinted = true;
								}
								oldId = flatEntry.getEntryId();
								entry = newAccountEntry(flatEntry);
							} else {
								if (entry == null) {
									entry = newAccountEntry(flatEntry);
								}
							}
							entry.getDetails().add( newAccountEntryDetail(flatEntry));
							something = true;
							count++;
						}
						if (something) {
							double sumD = 0.0;
							double sumC = 0.0;
							for (AccountEntryDetail aed : entry.getDetails()) {
								sumD = AonMathUtils.sum(sumD, aed.getDebit());	
								sumC = AonMathUtils.sum(sumC, aed.getCredit());
							}
							if (AonNumberUtils.equals(sumD, sumC)) {
								final FlowPanel entrycontainer = new FlowPanel();
								container.add(entrycontainer);
								paintEntry(entrycontainer, entry);
							} 
						}
						offset.setValue(ofs + count);
						enableMoreData();
						
						// Si el primer apunte tiene más de 100 líneas (más líneas que "limit"), se 
						// fuerza una nueva búsqueda para mostrar algo puesto que no sale nada al no estar cuadrado.
						if (something && !somethingPrinted) {
							search(offset.getValue(),params);						
						}
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
				}
			}
			
			
			private AccountEntryDetail newAccountEntryDetail(JsFlatAccountEntry flatEntry) {
				AccountEntryDetail out = new AccountEntryDetail();
				out.setDomain(flatEntry.getEntryDomain());
				out.setId(flatEntry.getDetailId());
				out.setAccountEntry(flatEntry.getEntryId());
				out.setAccount(flatEntry.getAccount());
				out.setAccountCode(flatEntry.getAccountCode());
				out.setAccountDescription(flatEntry.getAccountDescription());
				out.setLine(flatEntry.getLine());
				out.setConcept(flatEntry.getConcept());
				out.setDebit(AonNumberUtils.zeroIfNull(flatEntry.getDebit()));
				out.setCredit(AonNumberUtils.zeroIfNull(flatEntry.getCredit()));
				out.setBalancingAccount(flatEntry.getBalancingAccount());
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

			private FocusPanel paintEntry(final FlowPanel entrycontainer, AccountEntry entry) {
				final FocusPanel entryPanel = AccountEntryPrinter.print(entry);
				entrycontainer.add(entryPanel);
				entryPanel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						AccountEntrySelectionEvent.fire( JournalPanel.this, entry, new ModuleCallback() {
							
							@Override
							public void onRemove(IAccountEntryWrapper removed) {
								entrycontainer.remove(entryPanel);
							}
							
							@Override
							public void onFailure(Throwable caught) {}
							
							@Override
							public void onExit() {}
							
							@Override
							public void onChange(IAccountEntryWrapper changed) {
								entrycontainer.remove(entryPanel);
								AccountEntry entry = changed.getAccountEntry();
								FocusPanel newEntryPanel = paintEntry(entrycontainer, entry);
								newEntryPanel.addStyleName(AON.AON_CSS.aonValueChanged());
								new Timer() {
									@Override
									public void run() {
										newEntryPanel.removeStyleName(AON.AON_CSS.aonValueChanged());
									}
								}.schedule(3000);
							}
						});
					}
				});
				return entryPanel;
			}
			
		});
		StringBuffer requestData = new StringBuffer();
		requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + this.domainName  );
		requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + this.domainId );
		requestData.append("&"+IRequestParamsNames.USER					+"=" + this.user );
		requestData.append("&"+IRequestParamsNames.ACCOUNT_ENTRY_PARAMS +"=" + JsonParams.convert( params ));
		requestData.append("&"+IRequestParamsNames.OFFSET 				+"=" + ofs );
		requestData.append("&"+IRequestParamsNames.LIMIT				+"=" + limit );
		xhr.send(requestData.toString());
	}
}
