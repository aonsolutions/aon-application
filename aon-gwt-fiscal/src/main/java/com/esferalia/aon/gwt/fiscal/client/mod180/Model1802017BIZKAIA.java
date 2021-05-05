package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180.Model180Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model1802017BIZKAIA extends Model180Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model1802017BIZKAIA(Mod180 mod180, Model180ModuleOptions options,Model180Callback cbk,Integer selectedIndex) {
		super(mod180, options, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintPerceptorsTab(tabPanel, selectedIndex);
		paintAdministrationTab(options, cbk,tabPanel);
		
		tabPanel.selectTab(PERCEPTORS_TAB, false);
		
	}

	private void paintAdministrationTab(Model180ModuleOptions options, Model180Callback cbk,TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel(options, cbk));
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod180().getAdministration())));
	}
	
	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Orden Foral 2310/2014 de 26 de noviembre."
				,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2014/12/20141209a235.pdf#page=23"));
		list.add(new Pair<String, String>("Impreso."
				,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/180EurBil.pdf"));
		list.add(new Pair<String, String>("Hoja Interna." 
				,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/180EurBilHoja.pdf"));
		list.add(new Pair<String, String>("Instrucciones."
				,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/180EurBilInst.pdf"));		
		list.add(new Pair<String, String>("Programa de Ayuda."
				,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=1993"));		
		return list;
	}
	
}
