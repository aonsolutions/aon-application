package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130.Model130Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;

public class Model130Bizkaia extends Model130Base {
	
	private static final String DOWNLOAD_TEXT = "Descargar fichero para m\u00F3dulo de impresi\u00F3n.";
	
	public Model130Bizkaia(Mod130 mod130, Model130Callback callback) {
		super(mod130, callback);
	}
	
	public FlowPanel getDeclarationPanel(){
		FlowPanel panel = new FlowPanel();
		panel.setWidth("98%");
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod130Hidden);
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBackgroundStyle(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIconStyle(getModel().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.AON_CSS.aonPadding2());
		Button button1 = new Button(DOWNLOAD_TEXT);
		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button1.addStyleName(AON.AON_CSS.aonBorderNone());
		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
		button1.addStyleName(AON.AON_CSS.aonClickable());
		button1.addClickHandler( event -> {
			if (getModel().isFinished() || getModel().isSent()) {
				submitForm(MODEL130_FILE);
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		
		panel.add(tab);
		return panel;
	}
	
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<>();
		list.add(new Pair<>("Impreso (Rellenable)"
			,"http://www.bizkaia.eus/ogasuna/ereduak/info_descarga.asp?Idioma=CA&val1=8AA405AF81D"
			+ "76CC992B77CD9B7C7112FCA1B1651B529D2961D5EFE269326793A&Tam=572&Ext=application/pdf&"
			+ "Tem_Codigo=2093"));
		list.add(new Pair<>("Instrucciones" 
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/130EurBilInst.pdf"));
		list.add(new Pair<>("ORDEN FORAL 636/2014, de 17 de marzo"
			,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2014/03/20140324a057.pdf#page=40"));
		list.add(new Pair<>("Enlace al programa de ayuda"
			,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=1937"));
		list.add(new Pair<>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
			,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=145&Age_Codigo"
			+ "=04/05/2016&Tem_Codigo=5346"));
		list.add(new Pair<>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
			,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea_loturak.asp?Idioma=CA&Tem_Codigo"
			+ "=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdColumna=3556"));
		return list;
	}
	
}
