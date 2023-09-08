package com.esferalia.aon.gwt.fiscal.client.report;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

class OperationReportIrpfPanel extends ScrollPanel{
		
	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/AccountingOperationReportStream");
	
	private FlowPanel rootPanel = new FlowPanel();
	private JsOperationIrpfGridPanel grid = new JsOperationIrpfGridPanel();
	
	public OperationReportIrpfPanel(OperationReportModuleOptions options, OperationParams params) {
		setStyleName(AON.CSS.aonScrollArea());
		rootPanel.add(grid);
		setWidget(rootPanel);
		
		grid.addSelectionHandler( event -> showEntry(options,event.getSelectedItem().getEntryId()));
		
		final AonToast toast = new AonToast();
		final InlineLabel label =  new InlineLabel("Un momento, por favor ...");
		toast.show("Cargando ...", label);
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, REPORT_URL);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			private int loaded = 0;
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						for (JsOperationBreakdown op = read(text); text != null; op = read(text)) {
							grid.addRow(op);
						}
					} catch (IndexOutOfBoundsException e) {
						// Nothing
					}
				}
				if (state == XMLHttpRequest.DONE) {
					toast.hide();
					grid.addFooterRow();
				}
			}

			private JsOperationBreakdown read(String text) {
				for (int begin = loaded; begin < text.length(); begin++) {
					if (text.charAt(begin) == '{') {
						loaded = findEnd(text, begin + 1) + 1;
						String json = text.substring(begin, loaded);
						return JsonUtils.safeEval(json);
					}
				}
				throw new IndexOutOfBoundsException();
			}

			private int findEnd(String text, int start) {
				for (int end = start; end < text.length(); end++) {
					if (text.charAt(end) == '}') return end;
					else if (text.charAt(end) == '{') end = findEnd(text, end + 1);; 
				}
				throw new IndexOutOfBoundsException();
			}
		});
		StringBuilder requestData = new StringBuilder();
		requestData.append("&domainName=" + options.getDomainName() );
		requestData.append("&domainId=" + options.getDomain() );
		requestData.append("&user=" + options.getUser() );
		requestData.append("&irpfParams=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
	}

	private void showEntry(OperationReportModuleOptions options,Integer entryId) {
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad(new AccountEntryModuleOptions()
			.setParentWidget(entryDialog)
			.setDomainName(options.getDomainName())
			.setUser(options.getUser())
			.setDomain(options.getDomain())
			.setAccountEntryId(entryId)
			.setSessionLogTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setExternalCallback( new ModuleCallback() {
				
				private static final long serialVersionUID = 459085332301151216L;

				@Override
				public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					entryDialog.hide();
				}
				
				@Override
				public void onExit() {
					entryDialog.hide();
				}
				
				@Override
				public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}
	
}
