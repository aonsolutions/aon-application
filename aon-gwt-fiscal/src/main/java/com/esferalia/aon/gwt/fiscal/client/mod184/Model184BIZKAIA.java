package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model184BIZKAIA extends Model184Base {

	private static final int ENTITY_TAB = 1;

	public Model184BIZKAIA(Mod184 mod184,Model184Callback cbk,Integer selectedIncomeIndex,Integer selectedPartnerIndex,Integer tabIndex) {
		super(mod184, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintEntityTab(tabPanel);
		paintIncomeTab(tabPanel, selectedIncomeIndex);
		paintPartnersTab(tabPanel, selectedPartnerIndex);
		paintAdministrationTab(cbk, tabPanel);
		
		tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				setSelectedTab(event.getSelectedItem());
			}
		});
		setSelectedTab(tabIndex);

		if (tabIndex == null || tabIndex < 0 || tabIndex >= tabPanel.getWidgetCount()) {
			tabIndex = ENTITY_TAB;
		}
		tabPanel.selectTab(tabIndex, false);
	}

	private void paintAdministrationTab(Model184Callback cbk, TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel(cbk));
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationIconBW(getMod184().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Impreso"
				,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/184EurCas.pdf"));
		list.add(new Pair<String, String>("Hoja Interna 1"
				,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/184EurCasHoja1.pdf"));
		list.add(new Pair<String, String>("Instrucciones"
				,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/184EurCasInst.pdf"));
		list.add(new Pair<String, String>("ORDEN FORAL 83/2014,de 13 de enero"
				,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2014/01/20140123a015.pdf#page=163"));
		list.add(new Pair<String, String>("Enlace al programa de ayuda"
				,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=2886"));
		list.add(new Pair<String, String>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
				,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=182&Age_Codigo=24/11/2017&Tem_Codigo=5346"));
		list.add(new Pair<String, String>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"		
				,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=CA&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=1095&IdPublicoMostrarAnterior=804"));
		return list;
	}
	
}
