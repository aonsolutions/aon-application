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

public class Model349ARABA extends Model349Base {

	private static final int OPERATORS_TAB = 1;
	
	public Model349ARABA(Mod349 mod349,Model349Callback cbk,Integer selectedIndex) {
		super(mod349, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintOperatorsTab(tabPanel, selectedIndex);
		paintAdministrationTab(tabPanel);
		
		tabPanel.selectTab(OPERATORS_TAB, false);	
		
	}

	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel());
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod349().getAdministration())));
	}
	
	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Car\u00E1tula"
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D349+Car%C3%A1tula.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093118451&ssbinary=true"));
		list.add(new Pair<String, String>("Relaci\u00F3n de operaciones intracomunitarias." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DHoja+Relacion+operaciones+intracomunitarias.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093118452&ssbinary=true"));
		list.add(new Pair<String, String>("Hoja de Rectificaci\u00F3n de periodos anteriores." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DHoja+Rectificaci%C3%B3n+de+periodos+anteriores.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093118453&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 39 de 3 de febrero de 2010 que regula la obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma telem\u00E1tica por Internet."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093118454&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 318 de 19 de mayo de 2010."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+318+de+19+de+mayo+de+2010.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093118455&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 592 de 13 de octubre de 2011."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+592+de+13+de+octubre+de+2011.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093118458&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 62 de 29 de enero de 2014."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+62+de+29+de+enero+de+2014.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093118459&ssbinary=true"));
		
		return list;
	}
	
}
