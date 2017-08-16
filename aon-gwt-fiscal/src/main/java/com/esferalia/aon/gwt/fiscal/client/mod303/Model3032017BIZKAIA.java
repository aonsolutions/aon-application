package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIAScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIASpecificOperationsScript;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model3032017BIZKAIA extends Model303Base {
	
	
	public Model3032017BIZKAIA(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(callback,tabPanel);
		paintDeclarationTab(callback,tabPanel);
		paintLiquidationTab(callback,tabPanel);
		paintAdditionalDataTab(callback,tabPanel);
		paintSpecificOperationsTab(callback,tabPanel);
		paintInformationTab(callback,tabPanel);
		
	}
	
	private void paintAdditionalDataTab(Model303Callback callback, TabLayoutPanel tabPanel) {
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
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "60px");
		
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, "140px");

		table.getColumnFormatter().setWidth(7, "40px");
		table.getColumnFormatter().setStyleName(7, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(8, "140px");

		table.getColumnFormatter().setWidth(9, "50px");
		additionalDataScrollPanel.setWidget(table);
		tabPanel.add(additionalDataScrollPanel, TAB_TEMPLATE.render(AON.MSG.additionalData(), AON.AON_CSS.aonIconCompanyData()));
		paintDeclaration(table,Model3032017BIZKAIAAdditionalDataScript.values(),10);
	}

	private void paintSpecificOperationsTab(Model303Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel specificOpDataScrollPanel = new ScrollPanel();
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
		table.getColumnFormatter().setWidth(4, "140px");

		table.getColumnFormatter().setWidth(5, "50px");
		specificOpDataScrollPanel.setWidget(table);
		tabPanel.add(specificOpDataScrollPanel, TAB_TEMPLATE.render(AON.MSG.specificOperations(), AON.AON_CSS.aonIconCompanyData()));
		paintDeclaration(table,Model3032017BIZKAIASpecificOperationsScript.values(),4);
	}

	private void paintLiquidationTab(Model303Callback callback, TabLayoutPanel tabPanel) {
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
		paintDeclaration(table,Model3032017BIZKAIAScript1.values(),8);
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
		
		table.getColumnFormatter().setWidth(3, "50px");
		paintDeclaration(table,Model3032017BIZKAIAScript2.values(),4);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.liquidacion(), AON.AON_CSS.aonIconModel()));
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

	private void paintInformationTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		FlowPanel panel = getInformationPanel(callback);
		tabPanel.add(panel,TAB_TEMPLATE.render(AON.MSG.information(), FiscalModelUtils.getAdministrationIconBW(getMod303().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Impreso","http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/303EurCas.pdf"));
		list.add(new Pair<String, String>("Instrucciones","http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/303EurCasInst.pdf"));
		list.add(new Pair<String, String>("ORDEN FORAL 145/2017, de 17 de enero","http://www.bizkaia.eus/lehendakaritza/Bao_bob/2017/01/20170124a016.pdf#page=14"));
		list.add(new Pair<String, String>("Enlace al programa de ayuda","http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=1975"));
		list.add(new Pair<String, String>("Enlace a las fechas de vencimiento en el a\u00F1o vigente","http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=276&Age_Codigo=18/07/2017&Tem_Codigo=5346"));
		list.add(new Pair<String, String>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA","http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=ca&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=1322"));
		return list;
	}

}
