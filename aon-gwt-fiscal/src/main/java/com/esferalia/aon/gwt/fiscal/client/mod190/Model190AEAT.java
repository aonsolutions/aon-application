package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190.Model190Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model190AEAT extends Model190Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model190AEAT(Mod190 mod190,Model190Callback cbk,Integer selectedIndex) {
		super(mod190, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintPerceptorsTab(tabPanel, selectedIndex);
		paintAdministrationTab(cbk, tabPanel);
		
		tabPanel.selectTab(PERCEPTORS_TAB, false);
		
	}

	private void paintAdministrationTab(Model190Callback cbk, TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel(cbk));
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationIconBW(getMod190().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GI10.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general y ayuda." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/GI10.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GI10.shtml"));
		return list;
	}

	@Override
	protected void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		if ( getCallback().getMod190().getYear() < 2015) {
			setDetailManager( new Model190AEATDetail2014( getCallback() , selectedIndex ));
		} else if ( getCallback().getMod190().getYear() == 2015) {
			setDetailManager( new Model190AEATDetail2015( getCallback() , selectedIndex ));
		} else if ( getCallback().getMod190().getYear() == 2016) {
			setDetailManager( new Model190AEATDetail2016( getCallback() , selectedIndex ));
		} else {
			setDetailManager( new Model190AEATDetail2017( getCallback() , selectedIndex ));
		}
		tabPanel.add( (Widget) getDetailManager(),  TAB_TEMPLATE.render(AON.MSG.receiverList(), AON.AON_CSS.aonIconInvoice()) );
	}
	
}
