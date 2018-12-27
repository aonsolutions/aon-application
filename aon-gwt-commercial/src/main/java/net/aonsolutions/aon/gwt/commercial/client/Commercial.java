package net.aonsolutions.aon.gwt.commercial.client;

import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import net.aonsolutions.aon.gwt.commercial.client.commission.CommissionCalculate;

public class Commercial implements EntryPoint{

	private AonData aonData;

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getSubEntryPoint()
	/*-{
		return $wnd.getSubEntryPoint();
	}-*/;
	
	public Commercial(AonData aonData) {
		this.aonData = aonData;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);
		selection(entryPoint, getAonData());
	}

	private void selection(String entryPoint, AonData aonData){
		CommissionCalculate cc = new CommissionCalculate(aonData);
		cc.onModuleLoad();		
	}
	
}
