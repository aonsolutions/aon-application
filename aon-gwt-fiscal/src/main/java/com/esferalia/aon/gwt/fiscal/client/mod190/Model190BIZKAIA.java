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

public class Model190BIZKAIA extends Model190Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model190BIZKAIA(Mod190 mod190,Model190Callback cbk,Integer selectedIndex) {
		super(mod190, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintPerceptorsTab(tabPanel, selectedIndex);
		paintAdministrationTab(tabPanel);
		
		tabPanel.selectTab(PERCEPTORS_TAB, false);
		
	}

	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel());
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod190().getAdministration())));
	}
	
	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Car\u00E1tula"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D190+Car%C3%A1tula.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093120453&ssbinary=true"));
		list.add(new Pair<String, String>("Impreso"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/190EurBil.pdf"));
		list.add(new Pair<String, String>("Hoja Interna"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/190EurBilHoja.pdf"));
		list.add(new Pair<String, String>("Instrucciones"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/190EurBilInst.pdf"));
		list.add(new Pair<String, String>("Descripci\u00F3n de soportes"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/190CasSoporte.pdf"));
		list.add(new Pair<String, String>("Orden Foral 2994/2007, de 30 de noviembre"
			,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2007/12/20071212b241.pdf#page=8"));
		list.add(new Pair<String, String>("ORDEN FORAL 3502/2008, de 17 de diciembre, por la que se modifica el modelo 190"
			,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2009/01/20090105a002.pdf#page=3"));
		list.add(new Pair<String, String>("ORDEN FORAL 2141/2016, de 5 de diciembre"
			,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2016/12/20161213a235.pdf#page=3"));
		list.add(new Pair<String, String>("Enlace al programa de ayuda"
			,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=2002"));
		list.add(new Pair<String, String>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
			,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=258&Age_Codigo=06/11/2017&Tem_Codigo=5346"));
		list.add(new Pair<String, String>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
			,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=CA&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdPublicoMostrarAnterior=804"));
	    return list;
	}
	
	@Override
	protected void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		if ( getCallback().getMod190().getYear() < 2017) {
			setDetailManager( new Model190BIZKAIADetail2016( getCallback() , selectedIndex ));
		} else {
			setDetailManager( new Model190BIZKAIADetail2017( getCallback() , selectedIndex ));
		}
		tabPanel.add( (Widget) getDetailManager(),  TAB_TEMPLATE.render(AON.MSG.receiverList(), AON.AON_CSS.aonIconInvoice()) );
	}
}
