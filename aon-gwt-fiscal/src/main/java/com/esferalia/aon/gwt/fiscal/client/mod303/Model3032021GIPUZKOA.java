package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017GIPUZKOARScript1;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017GipuzkoaResultScript;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032021GIPUZKOAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
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

class Model3032021GIPUZKOA extends Model303Base {
	
	protected Model3032021GIPUZKOA(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintGeneralRegimenTab(tabPanel);
		paintResultTab(tabPanel);
		paintAdditionalDataTab(tabPanel);
		paintAdministrationTab(tabPanel);
		
		if (getMod303().getId() == null && getMod303().getYear() == 2021 && !getMod303().isDiffCalculationDisabled()) {
			double c51 = getMod303().getAmount(Mod303Key.GP_C051);
			double c52 = getMod303().getAmount(Mod303Key.GP_C052);
			if (AonMathUtils.isNotZero(c51) || AonMathUtils.isNotZero(c52)) {
				AonMessageDialog.show("AVISO",
						"Solapa \"Datos adicionales\". La casilla 32 se ha desdoblado en las casillas 51 y 52. No se puede realizar el c\u00E1lculo por diferencia en dichas casillas y tienen valor. Por favor revise los valores suministrados por la aplicaci\u00F3n."
						);
			}
		}

	}

	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, AON.MSG.identification());
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

		paintTextBox(table, Mod303Key.GP_I000, 9, true);
		paintWithoutActivityCheck(table);	// Sin actividad

		paintCheck(Mod303Key.GP_A001,table);		// Autoliquidación concursal. PRE
		paintCheck(Mod303Key.GP_A002,table);		// Autoliquidación concursal. POST
		
		container.add(addGroupPanel("", table));
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}
	
	private void paintGeneralRegimenTab(TabLayoutPanel tabPanel) {
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
		paintDeclaration(table,Model3032017GIPUZKOARScript1.values(),8);
		container.add(table);
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, AON.MSG.generalRegime());
	}
	
	private void paintResultTab(TabLayoutPanel tabPanel) {
		ScrollPanel resultScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		table.getColumnFormatter().setWidth(3, "50px");
		resultScrollPanel.setWidget(table);
		tabPanel.add(resultScrollPanel, AON.MSG.result());
		paintDeclaration(table,Model3032017GipuzkoaResultScript.values(),3);
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
		table.getColumnFormatter().setWidth(4, "140px");

		table.getColumnFormatter().setWidth(5, "50px");
		
		additionalDataScrollPanel.setWidget(table);
		tabPanel.add(additionalDataScrollPanel, AON.MSG.additionalData());
		paintDeclaration(table,Model3032021GIPUZKOAAdditionalDataScript.values(),3);
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
		
		tabPanel.add(panel,"Foru Aldundia / Diputaci\u00F3n Foral");		
		
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
		list.add(new Pair<>("Informaci\u00F3n general."
				,"https://egoitza.gipuzkoa.eus/es/listado-tramites/-/tramiteak/xehetasuna/740"));
		list.add(new Pair<>("Formulario PDF." 
				,"http://www2.gipuzkoa.eus/wps/wcm/connect/990ddb4f-eb8d-471c-a61e-df793f4d44b9/Impreso_300_Inprimakia_v2_2011_01_19.pdf?MOD=AJPERES&CACHEID=990ddb4f-eb8d-471c-a61e-df793f4d44b9&useDefaultText=0&useDefaultDesc=0"));
		return list;
	}
}
