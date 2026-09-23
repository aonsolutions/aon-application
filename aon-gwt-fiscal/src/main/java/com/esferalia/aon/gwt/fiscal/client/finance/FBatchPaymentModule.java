package com.esferalia.aon.gwt.fiscal.client.finance;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

public class FBatchPaymentModule  implements EntryPoint {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	// Variables
	
	private DeckLayoutPanel deckLayoutPanel;
	private FBatchPaymentEntryModule fBatchPaymentPayrollEntryModule;
	private FBatchPaymentList fBatchPaymentList;
	
	private FBatchType fbatchType;
	
	// -------------------------------------------------------------------
	// ----------------------  ON MODULE LOAD  ---------------------------
	// -------------------------------------------------------------------
	
	public FBatchPaymentModule(FBatchType fbatchType) {
		this.fbatchType = fbatchType;
	}

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		FinanceModuleOptions options = new FinanceModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final FinanceModuleOptions opt ) {
		AON.ensureInjected();

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		deckLayoutPanel = new DeckLayoutPanel();
		
		fBatchPaymentList = new FBatchPaymentList(opt, this.fbatchType) {
			
			@Override
			protected void onFBatchSelect(FBatch fBatch, boolean isNewFBatch) {
				fBatchPaymentPayrollEntryModule = new FBatchPaymentEntryModule(fbatchType) {
					
					@Override
					public void back(boolean refresh) {
						if(refresh)
							fBatchPaymentList.onSearch();
						
						deckLayoutPanel.showWidget(0);
					}
					
				};
				deckLayoutPanel.remove(fBatchPaymentPayrollEntryModule);
				deckLayoutPanel.add(fBatchPaymentPayrollEntryModule);
				
				deckLayoutPanel.showWidget(fBatchPaymentPayrollEntryModule);
				fBatchPaymentPayrollEntryModule.onModuleLoad(opt, fBatch);
				fBatchPaymentPayrollEntryModule.setHasSaved(isNewFBatch);
			}
			
		};
		deckLayoutPanel.add(fBatchPaymentList);
		
		fBatchPaymentPayrollEntryModule = new FBatchPaymentEntryModule(fbatchType) {
			
			@Override
			public void back(boolean refresh) {
				Window.alert("back - refresh: " + refresh);
				if(refresh) fBatchPaymentList.onSearch();
				deckLayoutPanel.showWidget(0);
			}
			
		};
		deckLayoutPanel.add(fBatchPaymentPayrollEntryModule);
		
		deckLayoutPanel.showWidget(0);
		opt.getParentWidget().add(deckLayoutPanel);
		
		if ( opt.getConfiguration() == null) {
			
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					opt.setConfiguration(result);
					fBatchPaymentList.onSearch();		
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Window.alert(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]");
				}
			});
		
		} else 
			fBatchPaymentList.onSearch();
	}

	
}		
