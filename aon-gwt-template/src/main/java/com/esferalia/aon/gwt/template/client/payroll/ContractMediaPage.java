package com.esferalia.aon.gwt.template.client.payroll;

import java.util.Date;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.payroll.Payroll.UnsexedCallback;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLoadingPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

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
		getDockLayoutPanel().setWidgetSize(getToolbar(), 50);
		
		AonToolbar toolbar = new AonToolbar("Informe de personal asalariado");
		
		AonToolbarButton downloadInforme = new AonToolbarButton("Descargar Informe", AON.CSS.aonIconPdf());
		downloadInforme.addClickHandler(e -> downloadInforme());
		
		AonToolbarButton downloadRegistroRetributivo = new AonToolbarButton("Descargar Registro Retributivo", AON.CSS.aonIconExcel());
		downloadRegistroRetributivo.addClickHandler(e -> downloadRegistroRetributivo());
		
		toolbar.add(downloadInforme);
		toolbar.add(downloadRegistroRetributivo);
		
		setToolbar(toolbar);
	}
	
	ListBox ejercicio;
	CheckBox detallado;
	CheckBox resumen;
	FlowPanel fp ;
	
	private void content(){
		VerticalPanel vp = new VerticalPanel();
		vp.getElement().getStyle().setMarginLeft(30, Unit.PX);
		HorizontalPanel hp = new HorizontalPanel();
		Label l1 = new Label("Ejercicio");
		l1.getElement().getStyle().setMarginRight(30, Unit.PX);
		hp.add(l1);
		ejercicio = new ListBox();
		Integer inityear = 2010;
		Date date = new Date();
		Integer year = date.getYear() + 1900;
		
		for(Integer i = 0; i < (year - inityear); i++){
			Integer y = year-i;
			ejercicio.addItem(y.toString(), y.toString());
		}
		ejercicio.setSelectedIndex(1);
		hp.add(ejercicio);
		vp.add(hp);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		Label l2 = new Label("Detallado");
		l2.getElement().getStyle().setMarginRight(30, Unit.PX);
		hp2.add(l2);
		detallado = new CheckBox();
		detallado.setValue(true);
		hp2.add(detallado);
		vp.add(hp2);
		
		HorizontalPanel hp3 = new HorizontalPanel();
		Label l3 = new Label("Resumen");
		l3.getElement().getStyle().setMarginRight(30, Unit.PX);
		hp3.add(l3);
		resumen = new CheckBox();
		hp3.add(resumen);
		vp.add(hp3);
		fp = new FlowPanel();
		vp.add(fp);
			
		setContent(vp);
		
//		<g:Label text="Detallado" styleName="{aonResources.css.aonPaddingRight}"></g:Label>
//		<g:CheckBox ui:field="detailCheckBox"></g:CheckBox>
//		setContent(new ContractMediaContent(API));
	}
	
	public void downloadInforme() {
		if(resumen.getValue() || detallado.getValue()){
			API.getPayroll().printContractMedia(Integer.parseInt(ejercicio.getSelectedItemText()),
				resumen.getValue(), detallado.getValue());
		}
	}
	
	private void downloadRegistroRetributivo() {
		AonLoadingPanel loadingPanel = new AonLoadingPanel("Su informe est\u00E1 siendo generado. Por favor, espere.");
		fp.add(loadingPanel);
		loadingPanel.show();
		
		if(resumen.getValue() || detallado.getValue()){
			UnsexedCallback unsexedCallback = (nss, name) -> {
				Window.alert(nss + " " + name);
			};
			
			API.getPayroll().printRemunerationRecord(Integer.parseInt(ejercicio.getSelectedItemText()), fp, unsexedCallback);
		}
	}
	
}
