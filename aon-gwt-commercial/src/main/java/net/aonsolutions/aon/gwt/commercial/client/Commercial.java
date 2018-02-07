package net.aonsolutions.aon.gwt.commercial.client;

import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.commercial.client.commission.CommissionCalculate;

public class Commercial implements EntryPoint{

	final ICommercialAsync impl = GWT.create(ICommercial.class);

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
	
	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);
		impl.getAonData(getCurrentDomainName(), getCurrentDomain(), new AsyncCallback<AonData>() {
			
			@Override public void onSuccess(AonData aonData) {
				selection(entryPoint, aonData);
			}
			
			@Override public void onFailure(Throwable arg0) {}
		});
	}

	private void selection(String entryPoint, AonData aonData){
		CommissionCalculate cc = new CommissionCalculate(aonData);
		cc.onModuleLoad();		
	}
	
}
