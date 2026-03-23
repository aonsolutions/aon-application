package com.esferalia.aon.gwt.fiscal.client.mod202;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2025Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202.Model202Callback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.mod202.Model2022025AddDataAEATScript;
import com.esferalia.aon.occam.api.model.fiscal.mod202.Model2022025AddInfoAEATScript;
import com.esferalia.aon.occam.api.model.fiscal.mod202.Model2022025LiquidationAEATScript;
import com.esferalia.aon.occam.api.model.type.CNAE2025;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model2022026AEAT extends Model202Base {
	
	public Model2022026AEAT(Model202Callback callback) {
		super(callback);
	}
	
	private class TaxTypeListBox extends ListBox {

		public TaxTypeListBox() {
			this.setWidth("130px");
			this.addItem("---", "");
			this.addItem("00");            // (Fondo pensiones)                                                                                           
			this.addItem("01");            // (SICAV y SII)                                                                                               
			this.addItem("04");            // (ZEC)                                                                                                       
			this.addItem("10");            // (Entidad sin fines lucrativos)                                                                              
			this.addItem("15");            // (Entidad nueva creación)                                                                                    
			this.addItem("24");            // (ERD 25)                                                                                                    
			this.addItem("23");            // (Microempresa 24) (ERD 26)                                                                                                    
			this.addItem("25");            // (Tipo general) (cooperativa + tipo general = Resultados cooperativos 25% y Resultados extracooperativos 25%)
			this.addItem("30");            // (Entidad de crédito, hidrocarburos)                                                                         
			this.addItem("00/23");         // (SOCIMI + microempresa 24) (SOCIMI + ERD 26)
			this.addItem("00/21/22");      // (SOCIMI + microempresa 25)                                                                                  
			this.addItem("00/19/21");      // (SOCIMI + microempresa 26)
			this.addItem("00/24");         // (SOCIMI + ERD 25)
			this.addItem("00/25");         // (SOCIMI + tipo general)                                                                                     
			this.addItem("21/22");         // (microempresa 25),  (cooperativa + microempresa 25 = R. cooperativos 21/22 y R. extracooperativos 21/22)    
			this.addItem("19/21");         // (microempresa 26),  (cooperativa + microempresa 26 = R. cooperativos 19/21 y R. extracooperativos 19/21)
			this.addItem("20/23");         // (Cooperativa f. protegida + microempresa 24 = R. cooperativos 20% y R. extracooperativos 23%) (Cooperativa f. protegida + ERD 26 = R. cooperativos 20% y R. extracooperativos 23%)
			this.addItem("20/24");         // (Cooperativa f. protegida + ERD 25 = R. cooperativos 20% y R. extracooperativos 24%)                        
			this.addItem("20/25");         // (Cooperativa f. protegida + tipo general = R. cooperativos 20% y R. extracooperativos 25%)                  
			this.addItem("18/19/21/22");   // (Cooperativa f. protegida + microempresa 25 = R. cooperativos 18/19 y R. extracooperativos 21/22)
			this.addItem("16/18/19/21");   // (Cooperativa f. protegida + microempresa 26 = R. cooperativos 16/18 y R. extracooperativos 19/21)           
			this.addItem("12/15");         // (Cooperativa f. protegida + entidad nueva creación = R. cooperativos 12 y R. extracooperativos 15)          
			this.addItem("25/30");         // (cooperativa de crédito + tipo general = R. cooperativos 25 y R. extracooperativos 30)                      
			this.addItem("23/30");         // (cooperativa de crédito + microempresa 24 = R. cooperativos 23% y R. extracooperativos 30%) (cooperativa de crédito + ERD 26 = R. cooperativos 23% y R. extracooperativos 30%)
			this.addItem("24/30");         // (cooperativa de crédito + ERD 25 = R. cooperativos 24% y R. extracooperativos 30%)                          
			this.addItem("21/22/30");      // (cooperativa de crédito + microempresa 25 = R. cooperativos 21/22 y R. extracooperativos 30%)               
			this.addItem("19/21/30");      // (cooperativa de crédito + microempresa 26 = R. cooperativos 19/21 y R. extracooperativos 30%)
			this.addItem("15/30");         // (cooperativa de crédito + entidad nueva creación = R. cooperativos 15% y R. extracooperativos 30%)          
			this.addItem("04/23");         // (ZEC + microempresa 24) (ZEC + ERD 26)
			this.addItem("04/21/22");      // (ZEC + microempresa 25)                                                                                     
			this.addItem("04/19/21");      // (ZEC + microempresa 26)
			this.addItem("04/24");         // (ZEC + ERD 25)                                                                                              
			this.addItem("04/25");         // (ZEC + tipo general)                                                                                        
			this.addItem("23/23N");        // (Naviera rég.tonelaje + microempresa 24) (Naviera rég.tonelaje + ERD 26)
			this.addItem("24/24N");        // (Naviera rég.tonelaje + ERD 25)
			this.addItem("25/25N");        // (Naviera rég.tonelaje + tipo general)                                                                       
			this.addItem("21/22/21N/22N"); // (Naviera rég.tonelaje 21N/22N+ microempresa 25 21/22)
			this.addItem("19/21/19N/21N"); // (Naviera rég.tonelaje 19N/21N+ microempresa 26 19/21)                                                       
		}
		
		public void setSelectedValue(String value) {
			setSelectedIndex(0);
			if (AonStringUtils.isNotBlank(value)) {
				for (int i = 0; i < this.getItemCount(); i++) {
					if (AonStringUtils.equalsIgnoreCase(this.getValue(i), value)) {
						setSelectedIndex(i);
						break;
					}				
				}
			}
		}		
	}
	
	@Override
	protected void paintDeclarationInfoTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel additionalDataScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		container.add(table);
		defineTable(table);
		for (IModelScript<Mod202Key> ms : Model2022025AddDataAEATScript.values()) {
			paintRow(table,callback,ms);	
		}
		additionalDataScrollPanel.setWidget(container);
		tabPanel.add(additionalDataScrollPanel, AON.MSG.additionalData());
	}

	@Override
	protected void paintDeclarationAditionalInfoTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel additionalInfoScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		container.add(table);
		defineTable(table);
		for (IModelScript<Mod202Key> ms : Model2022025AddInfoAEATScript.values()) {
			paintRow(table,callback,ms);	
		}
		additionalInfoScrollPanel.setWidget(container);
		tabPanel.add(additionalInfoScrollPanel, AON.MSG.additionalInfo());
	}
	
	@Override
	protected void paintLiquidationTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel liquidationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		container.add(table);
		defineTable(table);
		for (IModelScript<Mod202Key> ms : Model2022025LiquidationAEATScript.values()) {
			if (ms == Model2022025LiquidationAEATScript.B) {
				paintEmptyRow(table);
			}
			if (ms == Model2022025LiquidationAEATScript.B || ms == Model2022025LiquidationAEATScript.B1 || ms == Model2022025LiquidationAEATScript.B2  || ms == Model2022025LiquidationAEATScript.B2C27) {
				paintEmptyRow(table);
				paintEmptyRow(table);
			}
			paintRow(table,callback,ms);
		}
		liquidationScrollPanel.setWidget(container);
		tabPanel.add(liquidationScrollPanel, AON.MSG.liquidacion());
		int x00 = (int) callback.getModel().getAmount(Mod202Key.X00);
		if (x00 < 0 || x00 > 2) {
			callback.getModel().putAmount( Mod202Key.X00, 0 );
		}
		calculationMethodChanged(callback);
	}

	@Override
	protected void paintParticularyRow(FlexTable table, Model202Callback callback, IModelScript<Mod202Key> script) {
		
		if (script == Model2022025AddDataAEATScript.R01_P02) 
			paintDateRow(table, callback, script);
		
		if (script == Model2022025AddDataAEATScript.R01_P03) 
			paintCNAERow(table, callback, script);
		
		if (script == Model2022025AddDataAEATScript.R02_X08) 
			paintX08(table, callback, script); // Tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso
		
		if (script == Model2022025AddDataAEATScript.R02_X09) 
			paintX09(table, callback, script);
		
		if (script == Model2022025LiquidationAEATScript.X00) 
			paintX00(table, callback, script);
		
		if (script == Model2022025AddInfoAEATScript.AIA02) 
			paintR62(table, callback, script);
		
		if (script == Model2022025AddDataAEATScript.R00_X15
		 || script == Model2022025AddDataAEATScript.R00_X16
		 || script == Model2022025AddDataAEATScript.R00_X17
		 || script == Model2022025AddDataAEATScript.R00_X18
		 || script == Model2022025AddDataAEATScript.R02_X19 
		 || script == Model2022025AddDataAEATScript.R02_X01 
		 || script == Model2022025AddDataAEATScript.R02_X02 
		 || script == Model2022025AddDataAEATScript.R02_X04 
		 || script == Model2022025AddDataAEATScript.R02_X12 
		 || script == Model2022025AddDataAEATScript.R02_X06 
	 	 || script == Model2022025AddDataAEATScript.R02_X13 
		 || script == Model2022025AddDataAEATScript.R02_X11 
		 || script == Model2022025AddDataAEATScript.R02_X14 
		 || script == Model2022025AddDataAEATScript.R02_X20 
		 || script == Model2022025AddInfoAEATScript.AIA01) {
			paintCheckRow(table, callback, script);
		}
		
	}
	
	private void paintCNAERow(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		CNAE2025 cn = CNAE2025.valueOfCode(callback.getModel().getCnae());
		table.setWidget(row, 0,new Label(script.getLabel() + " (CNAE2025)"));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		
		final InlineLabel cnaeLabel = new InlineLabel(cn != null ? cn.getDescription() : "");
		cnaeLabel.setStyleName(AON.CSS.aonMarginLeft());

		FlowPanel cnaePanel = new FlowPanel();
		final AonTextBox cnaeBox = new AonTextBox();
		cnaeBox.setValue(cn != null ? cn.getCode() : "");
		cnaeBox.setVisibleLength(4);
		cnaeBox.setMaxLength(4);
		cnaeBox.setReadOnly(true);
		cnaePanel.add(cnaeBox);
		AonTableButton cnaeButton = new AonTableButton(AON.MSG.mainActivityCNAE(), AON.CSS.aonIconSearch());
		cnaeButton.addStyleName(AON.CSS.aonMarginLeft());
		final AonCnae2025Panel cnae2025Panel = new AonCnae2025Panel();
		cnae2025Panel.addSelectionHandler(event -> {
			CNAE2025 selected = event.getSelectedItem();
			callback.getModel().setCnae(selected.getCode());
			cnaeBox.setValue( selected.getCode());
			cnaeLabel.setText( selected.getDescription() );
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		cnaeButton.addClickHandler(event -> cnae2025Panel.onShow());
		cnaePanel.add(cnaeButton);
		cnaePanel.add(cnaeLabel);
		table.setWidget(row, 1, cnaePanel );
		table.getFlexCellFormatter().setColSpan(row, 1, 6);
		
	}
	
	private void paintDateRow(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		
		final AonDateBox dateBox = new AonDateBox();
		dateBox.setValue(callback.getModel().getInitialDate());
		dateBox.addValueChangeHandler( event -> {
			callback.getModel().setInitialDate(dateBox.getValue());
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		table.setWidget(row, 1, dateBox );
		table.getFlexCellFormatter().setColSpan(row, 1, 6);
	}
	
	private void paintCheckRow(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		final Mod202Key key = script.getKeys()[0]; 
		final CheckBox check = new CheckBox();
		check.setText(script.getLabel());
		check.setValue(callback.getModel().getAmount(key) == 1);
		check.addClickHandler(event -> {
			callback.getModel().putAmount(key,check.getValue().booleanValue()?1.0:0.0);
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		table.setWidget(row, 0, check );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().setColSpan(row, 0, 7);
		
	}
	
	// Tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso
	private void paintX08(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		final Mod202Key key = script.getKeys()[0];
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		
		final TaxTypeListBox r18Box = new TaxTypeListBox();
		r18Box.setSelectedValue(callback.getModel().getDescription(key));
		r18Box.addChangeHandler( event -> {
			callback.getModel().putDescription(key,r18Box.getSelectedValue());		
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		table.setWidget(row, 1, r18Box );
		table.getFlexCellFormatter().setColSpan(row, 1, 6);
	}

	private void paintX09(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		Mod202Key key = script.getKeys()[0];
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		
		final ListBox r19Box = new ListBox();
		r19Box.setWidth("260px");
		r19Box.addItem("NO CONSTA","0");
		r19Box.addItem("Igual/sup. 10 mill. \u20AC e inferior a 20 mill. \u20AC","1");
		r19Box.addItem("Igual/sup. 20 mill. \u20AC e inferior a 60 mill. \u20AC","2");
		r19Box.addItem("Igual/sup. 60 mill. \u20AC.","3");
		int value = (int) callback.getModel().getAmount(key);
		if (value < 0 || value > 4) value = 0;
		r19Box.setSelectedIndex(value);
		r19Box.addChangeHandler( event -> {
			callback.getModel().putAmount(key,r19Box.getSelectedIndex());		
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		table.setWidget(row, 1, r19Box );
		table.getFlexCellFormatter().setColSpan(row, 1, 6);
	}
	
	private void paintX00(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		paintEmptyRow(table);
		paintEmptyRow(table);
		int row = table.getRowCount();
		Mod202Key key = script.getKeys()[0];
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		
		final ListBox r21Box = new ListBox();
		r21Box.setWidth("350px");
		r21Box.addItem(AON.MSG.calculation0(), "0");
		r21Box.addItem(AON.MSG.calculation1(), "1");
		r21Box.addItem(AON.MSG.calculation2(), "2");
		int value = (int) callback.getModel().getAmount(key);
		if (value < 0 || value > 2) value = 0;
		r21Box.setSelectedIndex(value);
		r21Box.addChangeHandler( event -> {
			callback.getModel().putAmount(Mod202Key.X00,r21Box.getSelectedIndex());
			calculationMethodChanged(callback);
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		table.setWidget(row, 1, r21Box );
		table.getFlexCellFormatter().setColSpan(row, 1, 6);
		paintEmptyRow(table);
		paintEmptyRow(table);
		paintEmptyRow(table);
	}

	private void paintR62(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		Mod202Key key = script.getKeys()[0];

		paintLabel(table, row, script);
		table.getFlexCellFormatter().setColSpan(row, 0, 6);
		
		final AonTextBox textBox = new AonTextBox();
		textBox.setMaxLength(22);
		textBox.setVisibleLength(15);
		textBox.setValue(callback.getModel().getDescription(key));
		textBox.addValueChangeHandler( event -> {
			callback.getModel().putDescription(key,textBox.getValue());
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		table.setWidget(row, 1, textBox );
	}
	
	protected void calculationMethodChanged(final Model202Callback callback) {
		
		boolean methodA = callback.getModel().isMethodA();
		boolean methodB1 = callback.getModel().isMethodB1();
		boolean methodB2 = callback.getModel().isMethodB2();
		boolean methodB = callback.getModel().isMethodB();
		
		// 	A) Calculo del pago fraccionado: modalidad artículo 40.2 LIS
		getFieldsMap().get(Mod202Key.C01).setEnabled(methodA);
		getFieldsMap().get(Mod202Key.C02).setEnabled(methodA);
		
		// B) Cálculo del pago fraccionado: modalidad artículo 40.3 LIS
		getFieldsMap().get(Mod202Key.C04).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C05).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C06).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C67).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C37).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C07).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C08).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C44).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C14).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C45).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C46).setEnabled(methodB);
		
		// B.1) Caso general (entidades con porcentaje único)
		getFieldsMap().get(Mod202Key.C47).setEnabled(methodB1);
		getFieldsMap().get(Mod202Key.C40).setEnabled(methodB1);
		getFieldsMap().get(Mod202Key.C48).setEnabled(methodB1);
		getFieldsMap().get(Mod202Key.C49).setEnabled(methodB1);

		// B.2) Casos específicos (entidades con más de un porcentaje)
		getFieldsMap().get(Mod202Key.C20).setEnabled(methodB2);
		getFieldsMap().get(Mod202Key.C23).setEnabled(methodB2);
		getFieldsMap().get(Mod202Key.C61).setEnabled(methodB2);
		getFieldsMap().get(Mod202Key.C64).setEnabled(methodB2);
		getFieldsMap().get(Mod202Key.C50).setEnabled(methodB2);		
		getFieldsMap().get(Mod202Key.C42).setEnabled(methodB2);
		getFieldsMap().get(Mod202Key.C51).setEnabled(methodB2);
		getFieldsMap().get(Mod202Key.C52).setEnabled(methodB2);
		
		// B) Cálculo del pago fraccionado: modalidad artículo 40.3 LIS
		getFieldsMap().get(Mod202Key.C27).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C28).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C29).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C30).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C31).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C33).setEnabled(methodB);
	}

}
