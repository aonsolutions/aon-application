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

public class Model190ARABA extends Model190Base {
	private static final int PERCEPTORS_TAB = 1;

	public Model190ARABA(Model190ModuleOptions options,Mod190 mod190,Model190Callback cbk,Integer selectedIndex) {
		super(options,mod190, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintPerceptorsTab(tabPanel, selectedIndex);
		paintAdministrationTab(options,cbk, tabPanel);
		
		tabPanel.selectTab(PERCEPTORS_TAB, false);
		
	}

	private void paintAdministrationTab(Model190ModuleOptions options,Model190Callback cbk, TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel(options,cbk));
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod190().getAdministration())));
	}
	
	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Car\u00E1tula"
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D190+Car%C3%A1tula.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107823&ssbinary=true"));
		list.add(new Pair<String, String>("Relaci\u00F3n de perceptores." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D190+Relaci%C3%B3n+de+perceptores.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107824&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 651 de 17 de diciembre de 2007."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+651+de+17+de+diciembre+de+2007.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107826&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 39 de 3 de febrero de 2010 que regula la obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma telem\u00E1tica por Internet."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093120457&ssbinary=true"));
		list.add(new Pair<String, String>("Correcci\u00F3n errores de la Orden Foral 651 de 17 de diciembre de 2007."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DCorrecci%C3%B3n+errores+de+la+Orden+Foral+651+de+17+de+diciembre+de+2007.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107827&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 735 de 23 de diciembre de 2008."        
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+735+de+23+de+diciembre+de+2008.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107828&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 731 de 21 de diciembre de 2009."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+731+de+21+de+diciembre+de+2009.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107829&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 848 de 30 de diciembre de 2013."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+848+de+30+de+diciembre+de+2013.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107830&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 826 de 19 de diciembre de 2014."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+826+de+19+de+diciembre+de+2014.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107831&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 721 de 10 de diciembre de 2015."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+721+de+10+de+diciembre+de+2015.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107832&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 707 de 7 de diciembre de 2016."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+707+de+7+de+diciembre+de+2016.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224093107833&ssbinary=true"));
		return list;
	}
	
	@Override
	protected void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		if ( getCallback().getMod190().getYear() < 2017) {
			setDetailManager( new Model190ARABADetail2016( getCallback() , selectedIndex ));	
		} else {
			setDetailManager( new Model190ARABADetail2017( getCallback() , selectedIndex ));
		}
		tabPanel.add( (Widget) getDetailManager(),  TAB_TEMPLATE.render(AON.MSG.receiverList(), AON.AON_CSS.aonIconInvoice()) );
	}
}
