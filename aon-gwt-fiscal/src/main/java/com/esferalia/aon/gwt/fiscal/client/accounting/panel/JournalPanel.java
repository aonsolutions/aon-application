package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryPrinter;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;


public class JournalPanel extends ScrollPanel implements HasSelectionHandlers<AccountEntry>{

	static FiscalServiceAsync fiscalService;
	
	private String domainName;
	private String user;
	private int domainId;
	private final int limit = 20;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 ); 
	private FlowPanel container;
	private int lastScrollPos = 0;
	
	public JournalPanel(String domainName,String user,int domainId, AccountEntryParams params) {
		this.domainName = domainName;
		this.user = user;
		this.domainId = domainId;
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountEntry> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	private void search(AccountEntryParams params) {
		container.clear();
		offset.setValue(0);
		search(offset.getValue(),params);
	}
	
	private void search(final int ofs,AccountEntryParams params) {
		if (!isMoreData()) return;
		fiscalService.getAccountEntries(domainName,user ,domainId, params, ofs, limit
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
