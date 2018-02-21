package com.esferalia.aon.gwt.stat.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.stat.client.panel.StatControlPanel;
import com.esferalia.aon.gwt.stat.client.panel.fee.StatFeeProjectionPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class MainEntryPoint implements EntryPoint {

	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	//
	//    ================================================================== STATS
	//
	private static final String ST_STATS_ENTRY_POINT = "StatControlPanel";
	private static final String CRM_STATS_ENTRY_POINT = "CrmStatControlPanel";
	private static final String FEE_PROJECTION_ENTRY_POINT = "feeProjection";

	AonData aonData;

	
	public MainEntryPoint(AonData aonData) {
		this.aonData = aonData;
	}
	
	public MainEntryPoint() {

	}
	
	public void onModuleLoad(String entryPoint){
		
		if(entryPoint.equalsIgnoreCase(ST_STATS_ENTRY_POINT)) {
			GWT.runAsync(StatControlPanel.class, new RunAsyncCallback() {
	
				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}
	
				@Override
				public void onSuccess() {
					StatControlPanel panel = new StatControlPanel();
					panel.onModuleLoad();
				}
				
			});
		} else if(entryPoint.equalsIgnoreCase(FEE_PROJECTION_ENTRY_POINT)){
			GWT.runAsync(StatFeeProjectionPanel.class, new RunAsyncCallback() {
				
				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}
	
				@Override
				public void onSuccess() {
					StatFeeProjectionPanel panel = new StatFeeProjectionPanel(aonData);
					panel.onModuleLoad();
				}
				
			});
		}
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);
		onModuleLoad(entryPoint);
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getCurrentUser()
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
	public static native String getParameter(String moduleName,
			String parameterName) /*-{
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
