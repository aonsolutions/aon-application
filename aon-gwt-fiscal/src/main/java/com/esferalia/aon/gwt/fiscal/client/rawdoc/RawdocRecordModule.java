package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.RawdocService;
import com.esferalia.aon.gwt.fiscal.client.RawdocServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.RawdocServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

public class RawdocRecordModule  extends MainEntryPoint  {
	
	private static RawdocServiceAsync RAWDOC_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		SimplePanel panel = new SimplePanel();
		panel.getElement().getStyle().setDisplay(Display.NONE);
		root.add(panel);

		RawdocServiceAsync rawdocServiceRaw = GWT.create(RawdocService.class);
		RAWDOC_SERVICE = new RawdocServiceAsyncDecorator(rawdocServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(),getCurrentDomain(),getCurrentUser(),new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				recordInvoice(result);		
			}
			
			@Override
			public void onFailure(Throwable caught) {

			}
		});
		

//		root.add(panel);

		
	}
	
	public void recordInvoice(AonConfiguration configuration) {
		
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountingDocument());

		RAWDOC_SERVICE.getAccountingInvoice(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), getInvoice(), new AsyncCallback<AccountingInvoice>() {
			@Override
			public void onSuccess(AccountingInvoice result) {
				AccountEntryModule module = new AccountEntryModule();
				module.onModuleLoad(new AccountEntryModuleOptions()
						.setParentWidget(entryDialog)
						.setDomainName(getCurrentDomainName())
						.setDomain(getCurrentDomain())
						.setUser(getCurrentUser())
						.setConfiguration(configuration)
						.setAccountingInvoice(result)
						.setTediResult(new TediResult()
								.setAon(result)
								.setInv(result.getInvoice()))
						.setBackButtonVisible(false)
						.setSessionLogTabVisible(false)
						.setJournalTabVisible(false)
						.setExtraInfoTabVisible(false)
						.setExternalCallback(new ModuleCallback() {

							private static final long serialVersionUID = -2947804456883665519L;

							@Override
							public void onRemove(IAccountEntryWrapper removed) {
								entryDialog.hide();
								AccountingInvoice ai = (AccountingInvoice) removed;
								reloadInvoice(ai.getInvoice().getId());
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
								AccountingInvoice ai = (AccountingInvoice) changed;
								reloadInvoice(ai.getInvoice().getId());
							}
						})
						);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
		
		entryDialog.center();
		entryDialog.show();
	}
	
	public static native String getInvoice()
	/*-{
		return $wnd.getInvoice();
	}-*/;
	
	public static native void reloadInvoice(Integer invoiceId)
	/*-{
		$wnd.reloadInvoice(invoiceId);
	}-*/;
}
