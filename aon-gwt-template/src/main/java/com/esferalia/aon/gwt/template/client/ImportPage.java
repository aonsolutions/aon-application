package com.esferalia.aon.gwt.template.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;

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
		Button downloadTemplate = toolbar.addButton("Plantilla Facturas", AON.AON_CSS.aonIconExcel());
		downloadTemplate.setVisible(true);
		downloadTemplate.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				downloadInvoiceTemplate();
			}
		});
		setToolbar(toolbar);
	}
	
	private void downloadInvoiceTemplate() {
		Window.open( GWT.getModuleBaseURL()+ "/gwt_download_invoice_template", "_blank",null);
	}
	
	private void content() {
		setContent(new ImportContent(getAonData()));
	}
	
		
}

