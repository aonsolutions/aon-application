package com.esferalia.aon.gwt.template.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

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
		Button downloadTemplate = toolbar.addButton("Plantilla Facturas", AON.AON_CSS.aonIconExcel());
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
		setToolbar(toolbar);
	}
	
	private void downloadInvoiceTemplate() {
		Window.open( GWT.getModuleBaseURL()+ "/gwt_download_invoice_template", "_blank",null);
	}
	
	private void info() {
		FlexTable table = new FlexTable();
		table.getElement().getStyle().setMarginTop(50, Unit.PX);
		
		table.setWidget(0, 0, new Label("TIPO OPERACI\u00d3N"));
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(new Label("NAC - NACIONAL"));
		vp1.add(new Label("INT - INTRACOMUNITARIA"));
		vp1.add(new Label("EXT - EXTRACOMUNITARIA"));
		vp1.add(new Label("CCM - CANARIAS, CEUTA Y MELILLA"));
		vp1.add(new Label("ISP - INVERSION SUJETO PASIVO"));
		vp1.getElement().getStyle().setMarginBottom(10, Unit.PX);
		table.setWidget(0, 1, vp1);
		
		
		table.setWidget(1, 0, new Label("TIPO FACTURA"));
		VerticalPanel vp2 = new VerticalPanel();
		vp2.add(new Label("Ventas"));
		vp2.add(new Label("Compras"));
		vp2.add(new Label("Gastos"));
		vp2.add(new Label("Gt.No Ded o Ticket"));
		vp2.getElement().getStyle().setMarginBottom(10, Unit.PX);
		table.setWidget(1, 1, vp2);
		
		table.setWidget(2, 0, new Label("CLAVE RETENCI\u00d3N"));
		VerticalPanel vp3 = new VerticalPanel();
		vp3.add(new Label("PR - Profesional"));
		vp3.add(new Label("AR - Arrendamientos"));
		vp3.add(new Label("CM - Capital Mobiliario"));
		vp3.add(new Label("AG - AGRICULTORES"));
		vp3.add(new Label("TA - TRANSPORTISTAS Y ASIMILADOS"));
		vp3.getElement().getStyle().setMarginBottom(10, Unit.PX);
		table.setWidget(2, 1, vp3);

		table.setWidget(3, 0, new Label("CLAVE Y SUBCLAVE RETENCI\u00d3N"));
		Button aeat = new Button();
		aeat.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		aeat.addStyleName(AON.AON_CSS.aonIconAeat());
		aeat.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("https://www.agenciatributaria.es/AEAT.internet/Inicio/La_Agencia_Tributaria/Campanas/Retenciones/Cuadro_informativo_tipos_de_retencion_aplicables__2020_.shtml", "_blank", null);
			}
		});
		aeat.getElement().getStyle().setMarginBottom(10, Unit.PX);
		table.setWidget(3, 1, aeat);
		
		table.setWidget(4, 0, new Label("PA\u00cdS"));
		table.setWidget(4, 1, new Label("C\u00f3digo de pa\u00eds en formato ISO2"));
		
		ImportContent ic = (ImportContent) getContent().getWidget();
		ic.htmlPanel.add(table);
	}
	
	private void content() {
		setContent(new ImportContent(getAonData()));
	}
	
		
}

