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

public class Model1802017GIPUZKOA extends Model180Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model1802017GIPUZKOA(Mod180 mod180, Model180ModuleOptions options,Model180Callback cbk,Integer selectedIndex) {
		super(mod180, options, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintPerceptorsTab(tabPanel, selectedIndex);
		paintAdministrationTab(options, cbk, tabPanel);
		
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
		list.add(new Pair<String, String>("Informaci\u00F3n general."
				,"https://egoitza.gipuzkoa.eus/es/listado-tramites/-/tramiteak/xehetasuna/1652"));
		list.add(new Pair<String, String>("Formulario PDF." 
				,"http://www2.gipuzkoa.net/wps/wcm/connect/7cb7ad09-02aa-4780-870e-e3d11b3a7a4e/180.pdf?MOD=AJPERES&CACHEID=7cb7ad09-02aa-4780-870e-e3d11b3a7a4e"));
		return list;
	}
	
}
