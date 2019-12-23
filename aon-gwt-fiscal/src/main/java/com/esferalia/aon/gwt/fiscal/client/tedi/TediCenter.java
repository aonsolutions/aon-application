package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.CloseTab;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TediCenter extends MainEntryPoint {

	public static interface TediCenterCallback {
		boolean isSnapshot();
		boolean isAttached( String tabId );
		void onAttachTab( String tabId, Widget child, Widget tab);
		void onDettachTab( String tabId);
		Widget selectTab( String tabId );
		TediInvoiceList getTediInvoiceList(String currentDomainName,int currentDomain, String currentUser,Company company, String color);
	}
	
	public static native boolean isTediSnapshot()
	/*-{
		return $wnd.isTediSnapshot();
	}-*/;

	private static TediServiceAsync SERVICE;
	
	private String currentDomainName;
	private int currentDomain;
	private String currentUser;
	private HashMap<String,Widget> tabs = new HashMap<String,Widget>();
	private AonConfiguration configuration;
	private TabLayoutPanel mainTabLayoutPanel;
	private TediCenterCallback tediCenterCallback;
	
	public TediCenter(String domainName, int domain, String user) {
		currentDomainName = domainName;
		currentDomain = domain;
		currentUser = user;
		
		tediCenterCallback = new TediCenterCallback() {
			
			@Override
			public Widget selectTab(String tabId) {
				Widget w = tabs.get(tabId);
				if (w != null) {
					mainTabLayoutPanel.selectTab(w);
				}
				return w;
			}
			
			@Override
			public void onDettachTab(String tabId) {
				Widget w = tabs.get(tabId);
				if (w != null) {
					mainTabLayoutPanel.remove(w);
				}
				tabs.remove(tabId);
			}
			
			@Override
			public void onAttachTab(String tabId, Widget child, Widget tab) {
				tabs.put(tabId, child);
				mainTabLayoutPanel.add(child,tab);
				mainTabLayoutPanel.selectTab( mainTabLayoutPanel.getWidgetCount() - 1 );
			}
			
			@Override
			public boolean isSnapshot() {
				return isTediSnapshot();
			}
			
			@Override
			public boolean isAttached(String tabId) {
				return tabs.containsKey(tabId);
			}

			@Override
			public TediInvoiceList getTediInvoiceList(String currentDomainName,int currentDomain, String currentUser,Company company, String color) {
				return TediCenter.this.getTediInvoiceList(currentDomainName,currentDomain, currentUser,company, color);
			}
		};
		
	}

	private int getDomain() {
		return currentDomain;
	}

	private String getUser() {
		return currentUser;
	}

	private String getDomainName() {
		return currentDomainName;
	}

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		
		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		SERVICE = new TediServiceAsyncDecorator(serviceRaw);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		
		SimpleLayoutPanel mainPanel = new SimpleLayoutPanel();
		mainPanel.setStyleName(AON.AON_CSS.aonSelector());
		mainTabLayoutPanel = new TabLayoutPanel(30,Unit.PX);
		mainPanel.add(mainTabLayoutPanel);
		root.add(mainPanel);

		SERVICE.getAonConfiguration(getDomainName(), getUser(), getDomain(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				configuration = result;
				
				if(configuration.getChildDomains().size() > 0) {
					mainTabLayoutPanel.add(getTediCompanyList(getDomainName(), getDomain(), getUser()),new CloseTab(AON.MSG.companyList(), false));
				} else {
					Company company = configuration.getCompany(); 
					mainTabLayoutPanel.add(getTediInvoiceList(getDomainName(), getDomain(), getUser(),company,null),new CloseTab(AON.MSG.invoiceList(), false));
				}			
				mainTabLayoutPanel.add(getTediFileUploader(getDomainName(), getDomain(), getUser()),new CloseTab("Carga de facturas", false));
			}

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
			}
		});
	}
	
	private TediCompanyList getTediCompanyList(String currentDomainName,int currentDomain, String currentUser) {
		return new TediCompanyList(currentDomainName, currentDomain, currentUser, tediCenterCallback);
	}

	private TediInvoiceList getTediInvoiceList(String currentDomainName,int currentDomain, String currentUser,Company company, String color) {
		return new TediInvoiceList(currentDomainName, currentDomain, currentUser, color, tediCenterCallback);
	}
	
	private TediUploaderPanel getTediFileUploader(String currentDomainName,int currentDomain, String currentUser) {
		return new TediUploaderPanel(currentDomainName, currentDomain, currentUser, tediCenterCallback);
	}
}