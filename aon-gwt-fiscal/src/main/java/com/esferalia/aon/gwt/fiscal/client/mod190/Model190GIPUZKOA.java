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

public class Model190GIPUZKOA extends Model190Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model190GIPUZKOA(Mod190 mod190,Model190Callback cbk,Integer selectedIndex) {
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
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod190().getAdministration())));
	}
	
	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n general."
				,"http://www2.gipuzkoa.net/wps/portal/!ut/p/b1/hY_XasNAEEW_yOysuh7VWySr7Kq9CMfEKlZJHMWy_fVRwBAIxJ55GAbO5XBRiQqMRUYGWRQB5agcd-e23s3tNO76n78UKhwwhqJiBSSs8-AYMa8kUcpuOWYFihWAf0aBP_nY1dY8cTXqqCCy-J5_ADzxZ6h8qCDME8DGd-BRh2ctAnsa3pCLSoa9L0qBq6JOWiYvGRbapVGwryci4ZBcl68bnWmwb4JbMqfr9aGQNK5u9DAO5dGOzTjSy5C_pqhYzeKv2dJ9Exwa-th0BNA8QATleXFTtUlZdMOvPopTzY_Wwc82fdtEgidwJJRlwZii_fG0RINlDLOdk6Noaud3teAuxPJpvLj9EpjAzuVgSE7yiX1y2OrzhbsAyaims684hYxSmQS4klIW6PZl8TrY2JvOQ0PZm7LThs05UL4B5nAkJA!!/dl4/d5/L0lJSklKSUpKZ0EhIS9JTmpBQUF4QUFFU29BQ0ltWWchIS80SmtHUW9RdHV5RWQtVVlRL1o2XzFOMkVBQjFBMDhSSkMwSUVUSkNVSUIwNzMxL1o3XzFOMkVBQjFBMEc1VEEwSVVEUEZOMTFKMDY1LzA!/?contenido=hweogasunaeslbr%2Fogasuna%2Fhweimpuestos-040sta%2Fhweimpuestos-040-020sta%2Fhweinformativosanuales-040-020-180sta%2Fhwemodelos-040-020-180sta%2Fhwem190-040-020-180sta%2Fc12390d4-f367-48d9-b664-6066a6e7db62"));
		return list;
	}
	
	@Override
	protected void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		setDetailManager( new Model190GIPUZKOADetail2016( getCallback() , selectedIndex ));
		tabPanel.add( (Widget) getDetailManager(),  TAB_TEMPLATE.render(AON.MSG.receiverList(), AON.AON_CSS.aonIconInvoice()) );
	}
}
