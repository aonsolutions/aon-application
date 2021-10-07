package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017ARABAAdditionalDataScript;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017ARABARScript1;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017ARABAResultScript;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032019ARABAScript2;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model3032019ARABA extends Model303Base {
	
	public Model3032019ARABA(Mod303 mod303,Model303Callback callback, Model303ModuleOptions options) {
		super(mod303,callback, options);
		
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
		
		paintCheck(Mod303Key.AR_C910,table);	// ¿Ha optado por el régimen especial del criterio de Caja?
		paintCheck(Mod303Key.AR_C911,table);	// ¿Es destinatario de operaciones a las que se aplique el régimen especial del criterio de caja?
		
		paintCheck(Mod303Key.AR_C907 ,table);	// ¿Ha sido declarado en concurso de acreedores en el presente per\u00EDodo de liquidaci\u00F3n?
		paintDate( Mod303Key.AR_C908,table);	// Fecha en que se dictó el auto de declaración de concurso
		paintC909(Mod303Key.AR_C909,table);	// Si se ha dictado auto de declaración de concurso en este periodo, indique el tipo de autoliquidación
		
		// Número de identificación declaracion anterior (necesario si la anterior se presento telematicamente)
		// Debe introducirse Ejercicio+Numero (EEEENNNNNN)
		if (getCallback().getMod303().isReplacement()) {
			int row = table.getRowCount();
			paintLabel(table, row, AON.MSG.previousReceipt() + " Formato EEEENNNNNN (Ejercicio + N\u00FAmero)");
			
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			final AonTextBox receiptBox = new AonTextBox();
			receiptBox.setVisibleLength(15);
			receiptBox.setMaxLength(12);
			receiptBox.setValue( getCallback().getMod303().getReplacedNumber() );
			receiptBox.addValueChangeHandler( event -> {
				getCallback().getMod303().setReplacedNumber(receiptBox.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, receiptBox);
		}		
		
		container.add(addGroupPanel("", table));
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.CSS.aonIconData()));
	}

	private void paintC909(Mod303Key key, FlexTable table) {
		final ListBox c909 = new ListBox();
		c909.addItem("--");
		c909.addItem("Preconsursal");
		c909.addItem("Postconsursal");
		paintListBox(c909, key, table);
	}

	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.CSS.aonIconEmployee()));
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
		paintDeclaration(table,Model3032017ARABARScript1.values(),8);
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
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "140px");
		table.getColumnFormatter().setWidth(5, "50px");
		paintDeclaration(table,Model3032019ARABAScript2.values(),6);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.generalRegime(), AON.CSS.aonIconLetterG()));
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
		tabPanel.add(resultScrollPanel, TAB_TEMPLATE.render(AON.MSG.result(), AON.CSS.aonIconLetterR()));
		paintDeclaration(table,Model3032017ARABAResultScript.values(),3);
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
		tabPanel.add(additionalDataScrollPanel, TAB_TEMPLATE.render(AON.MSG.additionalData(), AON.CSS.aonIconLetterD()));
		paintDeclaration(table,Model3032017ARABAAdditionalDataScript.values(),3);
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
		list.add(new Pair<>("Formulario papel."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D303.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224092997440&ssbinary=true"));
		list.add(new Pair<>("Orden Foral 39 de 3 de febrero de 2010 que regula la obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma telem\u00E1tica por Internet." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224092997447&ssbinary=true"));
		list.add(new Pair<>("Resoluci\u00F3n 21 de 12 de enero de 2016."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DResoluci%C3%B3n+21+de+12+de+enero+de+2016+.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224092997448&ssbinary=true"));
		list.add(new Pair<>("Orden Foral 38 de 4 de febrero de 2015."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+38+de+4+de+febrero+de+2015+.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224092997449&ssbinary=true"));
		return list;
	}
}
