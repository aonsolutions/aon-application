package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;

public class Model123Bizkaia extends Model123Base {

	private static final String DOWNLOAD_TEXT = "Descargar fichero para m\u00F3dulo de impresi\u00F3n.";

	public Model123Bizkaia(IFiscalModelCallback<Mod123> callback, AonData aonData) {
		super(callback, aonData);
	}
	
	public FlowPanel getDeclarationPanel(){
		FlowPanel panel = new FlowPanel();
		panel.setWidth("98%");
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod123Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formContainer.add(diskForm);

		panel.add(formContainer);
		FlowPanel administrationPanel = getAdministrationPanel(); 
		panel.add(administrationPanel);
		FlowPanel informationPanel = getInformationPanel();
		panel.add(informationPanel);
		return panel;
	}
	
	protected FlowPanel getAdministrationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getModel().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.AON_CSS.aonPadding2());
		Button button1 = new Button(DOWNLOAD_TEXT);
		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button1.addStyleName(AON.AON_CSS.aonBorderNone());
		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
		button1.addStyleName(AON.AON_CSS.aonClickable());
		button1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getModel().isFinished() || getModel().isSent()) {
					submitForm(MODEL123_FILE);
				} else {
			// TODO		getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		panel.add(tab);
		return panel;
	}

	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Impreso Rellenable." 
			,"http://www.bizkaia.eus/ogasuna/ereduak/info_descarga.asp?Idioma=CA&val1=0E427541D20570EC345E56908844474CEB6AAFD583BF29E10CEFB85F5E99D0BA&Tam=374&Ext=application/pdf&Tem_Codigo=2093"));
		list.add(new Pair<String, String>("Instrucciones"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/123EurBilInst.pdf"));
		list.add(new Pair<String, String>("Orden Foral 1.330/2000, de 27 de abril"
			,"http://www.bizkaia.eus/Lehendakaritza/Bao_Bob/2000/06/20000601a104.pdf"));
		list.add(new Pair<String, String>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
			,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=249&Age_Codigo=25/02/2016&Tem_Codigo=5346"));
		list.add(new Pair<String, String>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
			,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=CA&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdPublicoMostrarAnterior=804"));
		return list;
	}

}
