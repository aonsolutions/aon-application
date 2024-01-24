package com.esferalia.aon.gwt.template.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

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
		getDockLayoutPanel().setWidgetSize(getToolbar(), 50);
		
		AonToolbar toolbar = new AonToolbar("Importar Excel");
		
		AonToolbarButton downloadTemplate = new AonToolbarButton("Descargar Plantillas", AON.CSS.aonIconExcel());
		downloadTemplate.addClickHandler(e -> downloadInvoiceTemplate());
		
		AonToolbarButton infoButton = new AonToolbarButton("Descargar Plantillas", AON.CSS.aonIconInfo());
		infoButton.addClickHandler(e -> info());
		
//		Boolean showFixButton = getDomain().getName().contains("auditors") || getDomain().getName().contains("ayudat");
//		AonToolbarButton fixButton = new AonToolbarButton("Regenerar Clientes/Proveedores/Acreedores", "aon-icon-segment");
//		fixButton.setVisible(showFixButton);
//		fixButton.addClickHandler(e -> importFix());
		
//		Boolean showRegistryEmptyFixButton = getDomain().getName().equals("b06844062-cezaragoza.aonsolutions.net");
//		AonToolbarButton registryEmptyFixButton = new AonToolbarButton("Regenerar Clientes Vacíos", "aon-icon-segment");
//		registryEmptyFixButton.setVisible(showRegistryEmptyFixButton);
//		registryEmptyFixButton.addClickHandler(e -> importRegistryEmptyFix());
		
		toolbar.add(downloadTemplate);
		toolbar.add(infoButton);
//		toolbar.add(fixButton);
//		toolbar.add(registryEmptyFixButton);
		
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
		Window.open( GWT.getModuleBaseURL()+ "/gwt_download_template"
				+ "?type=fee", "_blank",null);		
	}
	
	private void info() {
		Window.open("https://drive.google.com/file/d/1FP06gvmneA18j0TmY8Z4_cX6-psxPgmC/view?usp=sharing", "_blank", null);
	}
	
	private void content() {
		setContent(new ImportContent(getAonData()));
	}
	
// TODO remove
//	private void importFix() {
//		item.importFix(getDomain(), getUser(), new AsyncCallback<Void>() {
//			
//			@Override
//			public void onSuccess(Void result) {
//				Window.alert("El proceso de correcci\u00f3n ha terminado.");
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				
//			}
//		});
//	}
	
//	private void importRegistryEmptyFix() {
//		item.importRegistryEmptyFix(getDomain(), getUser(), new AsyncCallback<Void>() {
//			
//			@Override
//			public void onSuccess(Void result) {
//				Window.alert("El proceso de correcci\u00f3n ha terminado.");
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				
//			}
//		});
//	}
	
		
}

