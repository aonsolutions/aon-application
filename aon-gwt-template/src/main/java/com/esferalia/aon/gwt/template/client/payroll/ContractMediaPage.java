package com.esferalia.aon.gwt.template.client.payroll;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;

public class ContractMediaPage extends AonTemplate2 {
	
	private AonData aonData;
	private API API;
	
	public AonData getAonData() {
		return aonData;
	}

	public API getAPI() {
		return API;
	}
	
	public ContractMediaPage(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
			aonData.getDomain().getName(), aonData.getDomain().getId(),
			aonData.getUser().getLogin());	
	}
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		startApplication();
	}
	
	private void startApplication() {
		toolbar();
		content();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Informe de personal asalariado");
		setToolbar(toolbar);
	} 
	
	private void content(){
		setContent(new ContractMediaContent(API));
	}
}
