package com.esferalia.aon.gwt.template.client;

import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.core.client.GWT;

public class ImportPage extends AonTemplate2{
	
	final ITemplateAsync item = GWT.create(ITemplate.class);

	AonData aonData;
	
	public ImportPage(AonData aonData) {
		this.aonData = aonData;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public Domain getDomain(){
		return getAonData().getDomain();
	}
	
	public User getUser(){
		return getAonData().getUser();
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
		Toolbar toolbar = new Toolbar("Importar Excel (Ayuda-T)");		
		setToolbar(toolbar);
	}
	
	private void content() {
		setContent(new ImportContent(getAonData()));
	}
	
		
}

