package com.esferalia.aon.gwt.fiscal.client.mod202;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202.Model202Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.mod202.Model2022018AEATScript;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model2022018AEAT extends Model202Base {
	
	
	public Model2022018AEAT(Model202Callback callback) {
		super(callback);
	}
	
	@Override
	protected void paintLiquidationTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		super.paintLiquidationTab(callback, tabPanel);
		final FiscalModelDetail detail = callback.getModel().ensureDetail(Mod202Key.X00);
		int x00 = (int) detail.getAmount();
		if (x00 < 0 || x00 > 2) x00 = 0;
		calculationMethodChanged(x00);
	}

	@Override
	protected void paintParticularyRow(FlexTable table, Model202Callback callback, IModelScript<Mod202Key> script) {
		if (script == Model2022018AEATScript.C01) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.C02) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.C03) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.C04) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.C05) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R01) {
			paintDateRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R02) {
			paintCNAERow(table, callback, script);
		} else if (script == Model2022018AEATScript.R04) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R05) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R06) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R07) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R08) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R09) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R10) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R17) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R18) {
			paintR18(table, callback, script);
		} else if (script == Model2022018AEATScript.R19) {
			paintR19(table, callback, script);
		} else if (script == Model2022018AEATScript.R21) {
			paintR21(table, callback, script);
		} else if (script == Model2022018AEATScript.R61) {
			paintCheckRow(table, callback, script);
		} else if (script == Model2022018AEATScript.R62) {
			paintR62(table, callback, script);
		}
	}
	
	private void paintCNAERow(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		CNAE2009 cn = CNAE2009.valueOfCode(callback.getModel().getCnae());
		table.setWidget(row, 0,new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		
		final InlineLabel cnaeLabel = new InlineLabel(cn!=null?cn.getDescription():"");
		cnaeLabel.setStyleName(AON.CSS.aonMarginLeft());

		FlowPanel cnaePanel = new FlowPanel();
		final AonTextBox cnaeBox = new AonTextBox();
		cnaeBox.setValue(cn!=null?cn.getCode():"");
		cnaeBox.setVisibleLength(4);
		cnaeBox.setMaxLength(4);
		cnaeBox.setReadOnly(true);
		cnaePanel.add(cnaeBox);
		AonTableButton cnaeButton = new AonTableButton(AON.MSG.mainActivityCNAE(), AON.CSS.aonIconSearch());
		cnaeButton.addStyleName(AON.CSS.aonMarginLeft());
		final AonCnae2009Panel cnae2009Panel = new AonCnae2009Panel();
		cnae2009Panel.addSelectionHandler(event -> {
			CNAE2009 selected = event.getSelectedItem();
			callback.getModel().setCnae(selected.getCode());
			cnaeBox.setValue( selected.getCode());
			cnaeLabel.setText( selected.getDescription() );
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		cnaeButton.addClickHandler(event -> cnae2009Panel.onShow());
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
	
	private void paintR18(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		final Mod202Key key = script.getKeys()[0];
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		
		final AonTextBox textBox = new AonTextBox();
		textBox.setMaxLength(5);
		textBox.setVisibleLength(6);
		textBox.setValue(callback.getModel().getDescription(key));
		textBox.addValueChangeHandler( event -> {
			callback.getModel().putDescription(key,textBox.getValue());
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		table.setWidget(row, 1, textBox );
		table.getFlexCellFormatter().setColSpan(row, 1, 6);
	}

	private void paintR19(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		int row = table.getRowCount();
		Mod202Key key = script.getKeys()[0];
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		
		final ListBox r19Box = new ListBox();
		r19Box.setWidth("200px");
		r19Box.addItem("NO CONSTA","0");
		r19Box.addItem("- Igual/sup. 10 mill. \u20AC e inferior a 20 mill. \u20AC","1");
		r19Box.addItem("- Igual/sup. 20 mill. \u20AC e inferior a 60 mill. \u20AC","2");
		r19Box.addItem("- Igual/sup. 60 mill. \u20AC.","3");
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
	
	private void paintR21(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
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
			calculationMethodChanged(r21Box.getSelectedIndex());
			calculateAndRefresh( callback );
			markAsDirty(callback);
		});
		table.setWidget(row, 1, r21Box );
		table.getFlexCellFormatter().setColSpan(row, 1, 6);
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
	
	protected void calculationMethodChanged(int method ) {
		
		boolean methodA = (method == 0);
		boolean methodB1 = (method == 1);
		boolean methodB2 = (method == 2);
		boolean methodB = (methodB1 || methodB2);
		
		// 	A) Calculo del pago fraccionado: modalidad artículo 40.2 LIS
		getFieldsMap().get(Mod202Key.C01).setEnabled(methodA);
		getFieldsMap().get(Mod202Key.C02).setEnabled(methodA);
		
		// B) Cálculo del pago fraccionado: modalidad artículo 40.3 LIS
		getFieldsMap().get(Mod202Key.C04).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C05).setEnabled(methodB);
		getFieldsMap().get(Mod202Key.C06).setEnabled(methodB);
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
