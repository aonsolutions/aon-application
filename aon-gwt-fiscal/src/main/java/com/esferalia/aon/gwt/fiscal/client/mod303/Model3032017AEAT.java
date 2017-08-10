package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATResultScript;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model3032017AEAT extends Model303Base {
	
	public Model3032017AEAT(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(getCallback(),tabPanel);
		paintDeclarationTab(mod303,getCallback(),tabPanel);
		paintGeneralRegimenTab(getCallback(),tabPanel);
		paintSimplifiedRegimenTab(getCallback(),tabPanel);
		paintResultTab(getCallback(),tabPanel);
		paintAdditionalDataTab(getCallback(),tabPanel);
		paintInformationTab(getCallback(),tabPanel);
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
		paintDeclaration(table,Model3032017AEATGeneralRegimeScript1.values(),8);
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
		paintDeclaration(table,Model3032017AEATGeneralRegimeScript2.values(),6);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.generalRegime(), AON.AON_CSS.aonIconModel()));
	}
	
	private void paintSimplifiedRegimenTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		ScrollPanel simplifiedRegimeScrollPanel = new ScrollPanel();
		// ---
		simplifiedRegimeScrollPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Label notYet = new Label("NO IMPLEMENTADO");
		notYet.setStyleName(AON.AON_CSS.aonColorRed());
		notYet.addStyleName(AON.AON_CSS.aonFontBig());
		// ---
		
		simplifiedRegimeScrollPanel.setWidget(notYet);
		tabPanel.add(simplifiedRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.simplifiedRegime(), AON.AON_CSS.aonIconModel()));
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
		paintDeclaration(table,Model3032017AEATResultScript.values(),3);
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
		paintDeclaration(table,Model3032017AEATAdditionalDataScript.values(),3);
	}

	private void paintInformationTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		FlowPanel panel = getInformationPanel(callback);
		tabPanel.add(panel,TAB_TEMPLATE.render(AON.MSG.information(), FiscalModelUtils.getAdministrationIconBW(getMod303().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/G414.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/G414.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/G414.shtml"));
		return list;
	}
	
	private void paintDeclarationTab(Mod303 mod303,Model303Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = createTable();
		paintCheck(mod303,Mod303Key.CM_002,table);	// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		paintA02(mod303,Mod303Key.CT_A02,table);	// ¿Tributa exclusivamente en régimen simplificado?
		
		paintCheck(mod303,Mod303Key.CT_A03,table);	// ¿Es autoliquidación conjunta?
		
		paintCheck(mod303,Mod303Key.CT_A07,table);	// ¿Ha optado por el régimen especial del criterio de Caja (art. 163 undecies LIVA)?
		paintCheck(mod303,Mod303Key.CT_A08,table);	// ¿Es destinatario de operaciones a las que se aplique el régimen especial del criterio de caja?
		paintCheck(mod303,Mod303Key.CT_A11,table);	// Exonerados de la declaraci\u00F3n-resumen anual del IVA, modelo 390: ¿Existe volumen de operaciones (art. 121 LIVA)?
		
		paintCheck(mod303,Mod303Key.CT_A04,table);	// Ha sido declarado en concurso de acreedores en el presente período de liquidación?
		paintA05( mod303,Mod303Key.CT_A05,table);	// Fecha en que se dictó el auto de declaración de concurso
		paintCheck(mod303,Mod303Key.CT_A06,table);	// Auto de declaración de concurso dictado en el períDodo

		paintEmptyRow(table);
		paintCheck(mod303,Mod303Key.CT_A09,table);	// Opción por la aplicación de la prorrata especial
		paintCheck(mod303,Mod303Key.CT_A10,table);	// Revocación de la opción por la aplicación de la prorrata especial
		container.add(addGroupPanel("", table));
		
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}
	
	private void paintA02(Mod303 mod303, Mod303Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		final ListBox a02 = new ListBox();
		a02.addItem("S\u00F3lo Reg. Simplificado");
		a02.addItem("Reg. General y Reg. Simpl.");
		a02.addItem("S\u00F3lo Reg. General");
		a02.setEnabled(mod303.isNotFinished());
		a02.setSelectedIndex( (int) mod303.ensureDetail(Mod303Key.CT_A02).getAmount() );
		a02.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				mod303.ensureDetail(Mod303Key.CT_A02).setAmount(a02.getSelectedIndex());
				markAsDirty();
			}
		});
		table.setWidget(row, 1, a02);
	}

	private void paintA05(Mod303 mod303, Mod303Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		final DateBoxEx a05 = new DateBoxEx();
		a05.setEnabled(mod303.isNotFinished());
		if (AonStringUtils.isNotEmpty( mod303.ensureDetail(Mod303Key.CT_A05).getDescription() ) ) {
			a05.setValue( a05.parse(mod303.ensureDetail(Mod303Key.CT_A05).getDescription() , false) );
		}
		a05.addValueChangeHandler( new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				mod303.ensureDetail(Mod303Key.CT_A05).setDescription(a05.format());
				markAsDirty();
			}
		});
		table.setWidget(row, 1, a05);
	}

	private void paintCheck(Mod303 mod303,Mod303Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		final CheckBox check = new CheckBox();
		check.setEnabled(mod303.isNotFinished());
		check.setValue(mod303.ensureDetail(key).getAmount() == 1);
		check.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod303.ensureDetail(key).setAmount(check.getValue()?1.0:0.0);
				markAsDirty();
			}
		});
		table.setWidget(row, 1, check);
	}
	
	private FlexTable createTable() {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "300px");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		return table;
	}
	
	private FlowPanel addGroupPanel(String label, Widget w) {
		FlowPanel groupPanel = new FlowPanel();
		groupPanel.setStyleName(AON.AON_CSS.aonGroup());
		
			FlowPanel groupHeaderPanel = new FlowPanel();
			groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
			groupHeaderPanel.add (new InlineLabel(label)); 
			groupPanel.add(groupHeaderPanel);
			
			FlowPanel groupBodyPanel = new FlowPanel();
			groupBodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
			groupBodyPanel.add(w);
			groupPanel.add(groupBodyPanel);
		return groupPanel;
	}
	
}
