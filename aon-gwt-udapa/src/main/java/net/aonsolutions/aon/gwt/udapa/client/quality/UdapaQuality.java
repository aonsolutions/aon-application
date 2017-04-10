package net.aonsolutions.aon.gwt.udapa.client.quality;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;
import net.aonsolutions.aon.gwt.udapa.client.IUdapaAsync;

public class UdapaQuality extends AonTemplate2{

	final IUdapaAsync impl = GWT.create(IUdapa.class);
	private API API;
	
	public API getAPI() {
		return API;
	}
	
	public UdapaQuality(AonData aonData) {
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
	/*	Polymer.importHref(Arrays.asList(
			IronIconsElement.SRC
		), o -> {
	*/
			super.onModuleLoad();
			startApplication();
	/*		return null;
		});
	*/
	}
	
	private void startApplication() {
		toolbar();
		westContent();
		content();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar() {
			
			@Override
			protected void reset() {}
			
			@Override
			protected void remove() {}
			
			@Override
			protected void print() {}
			
			@Override
			protected void next() {}
			
			@Override
			protected void email() {}
			
			@Override
			protected void back() {}
			
			@Override
			protected void ant() {}
		};
		toolbar.setAllVisible(false);
		setToolbar(toolbar);
	}
	
	private void westContent() {

	}
	
	private void content() {
		setContent(new QualitySheet(this));
	}
}
