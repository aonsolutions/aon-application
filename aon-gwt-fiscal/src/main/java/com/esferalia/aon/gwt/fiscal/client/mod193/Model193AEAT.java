package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193.Model193Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model193AEAT extends Model193Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model193AEAT(Model193ModuleOptions options,Mod193 mod193,Model193Callback cbk,Integer selectedIndex) {
		super(options, mod193, cbk);
		
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

	private void paintAdministrationTab(Model193ModuleOptions options,Model193Callback cbk, TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel(options, cbk));
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationIconBW(getMod193().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GI12.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general y ayuda." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/GI12.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GI12.shtml"));
		return list;
	}

	@Override
	protected void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
//		if ( getCallback().getMod193().getYear() < 2016) {
//			setDetailManager( new Model193AEATDetail2015( getCallback() , selectedIndex ));
//		} else {
			setDetailManager( new Model193AEATDetail2016( getCallback() , selectedIndex ));
//		}
		tabPanel.add( (Widget) getDetailManager(),  TAB_TEMPLATE.render(AON.MSG.receiverList(), AON.AON_CSS.aonIconInvoice()) );
	}
	
}
