package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017ARABAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017ARABARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017ARABAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017ARABAScript2;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model3032017ARABA extends Model303Base {
	
	public Model3032017ARABA(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(callback,tabPanel);
		paintDeclarationTab(callback,tabPanel);
		paintGeneralRegimenTab(callback,tabPanel);
		paintResultTab(callback,tabPanel);
		paintAdditionalDataTab(callback,tabPanel);
		paintInformationTab(callback,tabPanel);
		
	}

	private void paintDeclarationTab(Model303Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		// ---
		declarationScrollPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Label notYet = new Label("NO IMPLEMENTADO");
		notYet.setStyleName(AON.AON_CSS.aonColorRed());
		notYet.addStyleName(AON.AON_CSS.aonFontBig());
		// ---
		
		declarationScrollPanel.setWidget(notYet);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}

	private void paintIdentificationTab(Model303Callback callback, TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.AON_CSS.aonIconIdentification()));
	}
	
	private void paintGeneralRegimenTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		ScrollPanel generalRegimeScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "60px");
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, "140px");
		table.getColumnFormatter().setWidth(7, "50px");
		paintDeclaration(table,Model3032017ARABARScript1.values(),8);
		container.add(table);
		
		table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "140px");
		table.getColumnFormatter().setWidth(5, "50px");
		paintDeclaration(table,Model3032017ARABAScript2.values(),6);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.generalRegime(), AON.AON_CSS.aonIconModel()));
	}
	
	private void paintResultTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		ScrollPanel resultScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		table.getColumnFormatter().setWidth(3, "50px");
		resultScrollPanel.setWidget(table);
		tabPanel.add(resultScrollPanel, TAB_TEMPLATE.render(AON.MSG.result(), AON.AON_CSS.aonIconModel()));
		paintDeclaration(table,Model3032017ARABAResultScript.values(),3);
	}

	private void paintAdditionalDataTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		ScrollPanel additionalDataScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		table.getColumnFormatter().setWidth(3, "50px");
		additionalDataScrollPanel.setWidget(table);
		tabPanel.add(additionalDataScrollPanel, TAB_TEMPLATE.render(AON.MSG.additionalData(), AON.AON_CSS.aonIconCompanyData()));
		paintDeclaration(table,Model3032017ARABAAdditionalDataScript.values(),3);
	}
	private void paintInformationTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		FlowPanel panel = getInformationPanel(callback);
		tabPanel.add(panel,TAB_TEMPLATE.render(AON.MSG.information(), FiscalModelUtils.getAdministrationIconBW(getMod303().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Formulario papel."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D303.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224092997440&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 39 de 3 de febrero de 2010 que regula la obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma telem\u00E1tica por Internet." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224092997447&ssbinary=true"));
		list.add(new Pair<String, String>("Resoluci\u00F3n 21 de 12 de enero de 2016."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DResoluci%C3%B3n+21+de+12+de+enero+de+2016+.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224092997448&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 38 de 4 de febrero de 2015."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+38+de+4+de+febrero+de+2015+.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224092997449&ssbinary=true"));
		return list;
	}
}
