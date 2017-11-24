package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model184AEAT extends Model184Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model184AEAT(Mod184 mod184,Model184Callback cbk,Integer selectedIndex) {
		super(mod184, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintEntityTab(tabPanel);
		paintIncomeTab(tabPanel, selectedIndex);
		paintPartnersTab(tabPanel, selectedIndex);
		paintAdministrationTab(tabPanel);
		
		tabPanel.selectTab(PERCEPTORS_TAB, false);
		
	}

	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel());
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationIconBW(getMod184().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GI00.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general y ayuda." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/GI00.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GI00.shtml"));
		return list;
	}
	
}
