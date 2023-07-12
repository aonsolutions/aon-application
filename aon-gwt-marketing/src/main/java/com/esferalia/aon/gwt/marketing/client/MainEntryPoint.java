package com.esferalia.aon.gwt.marketing.client;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.marketing.client.marketing.QuestionModule;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainEntryPoint implements EntryPoint {
	
	private static final String ERROR_MSG = "Error al cargar";
	private static AonConfiguration aonConfiguration;
	
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}
	
	private static final String ENTRY_POINT_PARAM = "entryPoint";

	//    ================================================================== MARKETING
	
	private static final String MARK_QUESTION_ENTRY_POINT = "QuestionModule";
	
	//	  ================================================================== ON MODULE LOAD
	
	@Override
	public void onModuleLoad() {
		String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);	
		if(getToken() != null) {
			Occam occam = new Occam()
				.setDomainName(getCurrentDomainName())
				.setDomain(getCurrentDomain())
				.setUser(getCurrentUser());
			ConfigParams params = new ConfigParams().setToken(getToken());
			COMMON_SERVICE.getAonConfiguration(occam, params, new AsyncCallback<AonConfiguration>() {
				
				@Override public void onSuccess(AonConfiguration config) {
					selection(entryPoint,aonConfiguration);
				}
				
				@Override public void onFailure(Throwable arg0) {
					Window.alert(ERROR_MSG);
				}
			});
		} else {
			selection(entryPoint,null);
		}

		
	}
	
	private void selection(String entryPoint, AonConfiguration aonConfiguration) {
		if ( entryPoint.equalsIgnoreCase(MARK_QUESTION_ENTRY_POINT)) {
			GWT.runAsync(QuestionModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					QuestionModule questionModule = new QuestionModule();
					questionModule.onModuleLoad();
				}
				
			});
		} 
	}
	protected Occam getOccam() {
		return new Occam()
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser());
	}
	
	public static native String getToken()
	/*-{
		return $wnd.localStorage.getItem("aon_session_id");
	}-*/;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	public static String getCurrentUser() {
		return getCurrentUserJs();
	}
	
	public static native String getCurrentUserJs()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
	/**
	 * Fetches a parameter passed to the module's nocache script.
	 * 
	 * @param moduleName
	 *            the module's name.
	 * @param parameterName
	 *            the name of the parameter to fetch.
	 * @return the value of the parameter, or <code>null</code> if it was not
	 *         found.
	 */
	public static native String getParameter(String moduleName, String parameterName) /*-{
		var search = "/" + moduleName + ".nocache.js";
		var scripts = $doc.getElementsByTagName("script");
		for ( var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var params = scripts[i].src.match(/\w+=\w+/g);
				for ( var j = 0; j < params.length; ++j) {
					var keyvalue = params[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;


}
