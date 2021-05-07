package com.esferalia.aon.gwt.fiscal.client.mod347;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model347AEAT extends Model347Base {

	public Model347AEAT(Model347ModuleOptions options, Mod347 mod347, Model347Callback cbk, Integer selectedIndexDeclared, Integer selectedIndexAsset, int tabPanelIndex) {
		super(options, mod347, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintDeclaredTab(options, tabPanel, selectedIndexDeclared);
		paintAssetsTab(tabPanel, selectedIndexAsset);
		paintAdministrationTab(options, cbk,tabPanel);
		addTabPanelSelectionHandler(tabPanel);
		
		tabPanel.selectTab(tabPanelIndex, true);
	}

	private void paintAdministrationTab(Model347ModuleOptions options, Model347Callback cbk,TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel(options, cbk));
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationIconBW(getMod347().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n general."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientoini/GI27.shtml"));
		return list;
	}
	
}
