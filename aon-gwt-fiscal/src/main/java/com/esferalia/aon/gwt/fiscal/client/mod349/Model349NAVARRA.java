package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349.Model349Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model349NAVARRA extends Model349Base {

	private static final int OPERATORS_TAB = 1;
	
	public Model349NAVARRA(Mod349 mod349,Model349Callback cbk,Integer selectedIndex) {
		super(mod349, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintOperatorsTab(tabPanel,selectedIndex);
		paintAdministrationTab(cbk,tabPanel);
		
		tabPanel.selectTab(OPERATORS_TAB, false);			
		
	}

	private void paintAdministrationTab(Model349Callback cbk,TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel(cbk));
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod349().getAdministration())));
	}
	
	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n general."
				,"http://www.navarra.es/home_es/Servicios/ficha/1789/Declaracion-recapitulativa-de-operaciones-intracomunitarias-(349)"));
		return list;
	}
	
}
