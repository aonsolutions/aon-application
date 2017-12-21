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

public class Model347GIPUZKOA extends Model347Base {

	//private static final int DECLARED_TAB = 1;

	public Model347GIPUZKOA(Mod347 mod347, Model347Callback cbk, Integer selectedIndexDeclared, Integer selectedIndexAsset, int tabPanelIndex ) {
		super(mod347, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintDeclaredTab(tabPanel, selectedIndexDeclared);
		paintAssetsTab(tabPanel, selectedIndexAsset);
		paintAdministrationTab(tabPanel);
		addTabPanelSelectionHandler(tabPanel);
		
		//tabPanel.selectTab(DECLARED_TAB, false);
		tabPanel.selectTab(tabPanelIndex, true);
	}

	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel());
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod347().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n general."
				,"https://egoitza.gipuzkoa.eus/es/listado-tramites/-/tramiteak/xehetasuna/813"));
		return list;
	}
	
}
