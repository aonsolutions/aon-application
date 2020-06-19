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
import com.google.gwt.user.client.rpc.AsyncCallback;
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
		Toolbar toolbar = new Toolbar("Importar Excel");		
		Button downloadTemplate = toolbar.addButton("Descargar Plantillas", AON.AON_CSS.aonIconExcel());
		downloadTemplate.setVisible(true);
		downloadTemplate.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				downloadInvoiceTemplate();
			}
		});
		
		Button infoButton = toolbar.addButton("Ayuda", AON.AON_CSS.aonIconInfo());
		infoButton.setVisible(true);
		infoButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				info();
			}
		});
		
		Boolean showFixButton = getDomain().getName().contains("auditors") || getDomain().getName().contains("ayudat");
		Button fixButton = toolbar.addButton("Regenerar Clientes/Proveedores/Acreedores", "aon-icon-segment");
		fixButton.setVisible(showFixButton);
		fixButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				importFix();
			}
		});
		setToolbar(toolbar);
	}
	
	private void downloadInvoiceTemplate() {
		Window.open( GWT.getModuleBaseURL()+ "/gwt_download_template"
				+ "?type=invoice", "_blank",null);
		Window.open( GWT.getModuleBaseURL()+ "/gwt_download_template"
				+ "?type=registry", "_blank",null);
		Window.open( GWT.getModuleBaseURL()+ "/gwt_download_template"
				+ "?type=diary", "_blank",null);
		Window.open( GWT.getModuleBaseURL()+ "/gwt_download_template"
				+ "?type=pgc", "_blank",null);
		
	}
	
	private void info() {
		Window.open("https://drive.google.com/file/d/1tw-LHm-NnGqMZoEHNWR1kNR1enAp2Unq/view?usp=sharing", "_blank", null);
	}
	
	private void content() {
		setContent(new ImportContent(getAonData()));
	}
	
	private void importFix() {
		item.importFix(getDomain(), getUser(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				Window.alert("El proceso de correcci\u00f3n ha terminado.");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
		
}

