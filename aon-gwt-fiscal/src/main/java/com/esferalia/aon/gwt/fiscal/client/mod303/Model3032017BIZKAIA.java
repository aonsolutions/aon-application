package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017BIZKAIAAdditionalDataScript;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017BIZKAIAScript1;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017BIZKAIAScript2;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017BIZKAIASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model3032017BIZKAIA extends Model303Base {
	
	
	public Model3032017BIZKAIA(Mod303 mod303,Model303Callback callback, Model303ModuleOptions options) {
		super(mod303,callback, options);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintLiquidationTab(tabPanel);
		paintAdditionalDataTab(tabPanel);
		paintSpecificOperationsTab(tabPanel);
		paintAdministrationTab(tabPanel);
		
	}
	
	private void paintAdditionalDataTab(TabLayoutPanel tabPanel) {
		ScrollPanel additionalDataScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "60px");
		
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, "140px");

		table.getColumnFormatter().setWidth(7, "40px");
		table.getColumnFormatter().setStyleName(7, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(8, "140px");

		table.getColumnFormatter().setWidth(9, "50px");
		additionalDataScrollPanel.setWidget(table);
		tabPanel.add(additionalDataScrollPanel, TAB_TEMPLATE.render(AON.MSG.additionalData(), AON.CSS.aonIconLetterD()));
		paintDeclaration(table,Model3032017BIZKAIAAdditionalDataScript.values(),10);
	}

	private void paintSpecificOperationsTab(TabLayoutPanel tabPanel) {
		ScrollPanel specificOpDataScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "140px");

		table.getColumnFormatter().setWidth(5, "50px");
		specificOpDataScrollPanel.setWidget(table);
		tabPanel.add(specificOpDataScrollPanel, TAB_TEMPLATE.render(AON.MSG.specificOperations(), AON.CSS.aonIconLetterE()));
		paintDeclaration(table,Model3032017BIZKAIASpecificOperationsScript.values(),4);
	}

	private void paintLiquidationTab(TabLayoutPanel tabPanel) {
		ScrollPanel generalRegimeScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );

		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "60px");
		
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, "140px");
		
		table.getColumnFormatter().setWidth(7, "50px");
		paintDeclaration(table,Model3032017BIZKAIAScript1.values(),8);
		container.add(table);
		
		table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		
		table.getColumnFormatter().setWidth(3, "50px");
		paintDeclaration(table,Model3032017BIZKAIAScript2.values(),4);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.liquidacion(), AON.CSS.aonIconData()));
	}

	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "300px");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );

		paintWithoutActivityCheck(table);	// Sin actividad
		paintCheck(Mod303Key.CM_002,table);		// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		paintCheck(Mod303Key.BZ_C186 ,table);	// Sujeto pasivo acogido al régimen especial del criterio de caja.
		paintCheck(Mod303Key.BZ_C187,table);		// Destinatario/a de operaciones a las que se aplica el r\u00E9gimen especial del criterio de caja
		
		paintDate( Mod303Key.BZ_C185_1,table);	// Fraccionamiento de per\u00EDodo en concursal. Desde.
		paintDate( Mod303Key.BZ_C185_2,table);	// Fraccionamiento de per\u00EDodo en concursal. Hasta.
		container.add(addGroupPanel("", table));
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.CSS.aonIconData()));
	}

	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.CSS.aonIconEmployee()));
	}

	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod303Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formContainer.add(diskForm);
		panel.add(formContainer);
		
		FlowPanel administrationPanel = getAdministrationPanel(); 
		panel.add(administrationPanel);
		FlowPanel informationPanel = getInformationPanel();
		panel.add(informationPanel);
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationBWIconStyle(getMod303().getAdministration())));
	}

	protected FlowPanel getAdministrationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		panel.addStyleName(AON.CSS.aonWidthAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonPaddingTop());
		panel.addStyleName(AON.CSS.aonPaddingLeft());

		Label title = new Label("Presentaci\u00F3n del modelo");
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonTextUnderline());
		panel.add(title);

		FlowPanel p1 = new FlowPanel();
		p1.addStyleName(AON.CSS.aonMarginTop());
		Anchor a1 = new Anchor("Descargar fichero para su presentaci\u00F3n");
		a1.setStyleName(AON.CSS.aonLabelWithIcon());
		a1.addStyleName(FiscalModelUtils.getAdministrationBWIconStyle(getMod303().getAdministration()));
		a1.addStyleName(AON.CSS.aonPaddingLeft());
		a1.addClickHandler( event -> {
			if (getMod303().isFinished() || getMod303().isSent()) {
				submitForm(DOWNLOAD_FILE_ACTION);
			} else {
				getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
			}
		});
		p1.add(a1);
		panel.add(p1);
		
		return panel;
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<>();
		list.add(new Pair<>("Impreso"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/303EurCas.pdf"));
		list.add(new Pair<>("Instrucciones"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/308EurBilInst.pdf"));
		list.add(new Pair<>("ORDEN FORAL 2311/2012, de 20 de agosto"
			,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2012/08/20120829a166.pdf#page=18"));
		list.add(new Pair<>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
			,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=227&Age_Codigo=21/12/2017&Tem_Codigo=5346"));
		list.add(new Pair<>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
			,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=ca&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=1322"));
		return list;
	}

}
