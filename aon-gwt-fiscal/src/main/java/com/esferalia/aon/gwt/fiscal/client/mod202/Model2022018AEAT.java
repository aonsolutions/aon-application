package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.shared.mod202.Model2022018AEATScript;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class Model2022018AEAT extends Model202Base {
	
	
	public Model2022018AEAT(IFiscalModelCallback<Mod202> callback) {
		super(callback);
	}
	
	@Override
	protected void paintDeclaration(IFiscalModelCallback<Mod202> callback) {
		super.paintDeclaration(callback);
		final FiscalModelDetail detail = callback.getFiscalModel().ensureDetail(Mod202Key.X00);
		int x00 = (int) detail.getAmount();
		if (x00 < 0 || x00 > 2) x00 = 0;
		calculationMethodChanged(callback,x00);
	}

	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GE00.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/GE00.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GE00.shtml"));
		return list;
	}

	@Override
	protected void paintParticularyRow(IFiscalModelCallback<Mod202> callback, IModelScript<Mod202Key> script) {
		if (script == Model2022018AEATScript.C01) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.C02) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.C03) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.C04) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.C05) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R01) {
			paintDateRow(callback, script);
		} else if (script == Model2022018AEATScript.R02) {
			paintCNAERow(callback, script);
		} else if (script == Model2022018AEATScript.R04) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R05) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R06) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R07) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R08) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R09) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R10) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R17) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R18) {
			paintR18(callback, script);
		} else if (script == Model2022018AEATScript.R19) {
			paintR19(callback, script);
		} else if (script == Model2022018AEATScript.R21) {
			paintR21(callback, script);
		} else if (script == Model2022018AEATScript.R61) {
			paintCheckRow(callback, script);
		} else if (script == Model2022018AEATScript.R62) {
			paintR62(callback, script);
		}
	}
	
	private void paintCNAERow(final IFiscalModelCallback<Mod202> callback, IModelScript<Mod202Key> script) {
		int row = getTable().getRowCount();
		CNAE2009 cn = callback.getFiscalModel().getCnae();
		getTable().setWidget(row, 0,new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		
		final InlineLabel cnaeLabel = new InlineLabel(cn!=null?cn.getDescription():"");
		cnaeLabel.setStyleName(AON.AON_CSS.aonMarginLeft());

		FlowPanel cnaePanel = new FlowPanel();
		final TextBox cnaeBox = new TextBox();
		cnaeBox.setValue(cn!=null?cn.getCode():"");
		cnaeBox.setStyleName(AON.AON_CSS.aonInputText());
		cnaeBox.setVisibleLength(4);
		cnaeBox.setMaxLength(4);
		cnaeBox.setReadOnly(true);
		cnaePanel.add(cnaeBox);
		Button cnaeButton = new Button();
		cnaeButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		cnaeButton.addStyleName(AON.AON_CSS.aonIconLoupe());
		cnaeButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		cnaeButton.setTitle(AON.MSG.mainActivityCNAE());
		final Cnae2009Panel cnae2009Panel = new Cnae2009Panel( new Cnae2009Panel.SelectionCallBack() {
			@Override
			public void onSelect(CNAE2009 selected) {
				callback.getFiscalModel().setCnae(selected);
				cnaeBox.setValue( selected.getCode());
				cnaeLabel.setText( selected.getDescription() );
				calculateAndRefresh( callback );
				callback.markAsDirty();
			}
			@Override
			public void onClose() {
			}
		});
		cnaeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cnae2009Panel.onShow();
			}
		});
		cnaePanel.add(cnaeButton);
		cnaePanel.add(cnaeLabel);
		getTable().setWidget(row, 1, cnaePanel );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 6);
		
	}
	
	private void paintDateRow(final IFiscalModelCallback<Mod202> callback, IModelScript<Mod202Key> script) {
		int row = getTable().getRowCount();
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		
		final DateBoxEx dateBox = new DateBoxEx();
		dateBox.setValue(callback.getFiscalModel().getInitialDate());
		dateBox.setEnabled(callback.getFiscalModel().isNotFinished());
		dateBox.addValueChangeHandler( new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				callback.getFiscalModel().setInitialDate(dateBox.getValue());
				calculateAndRefresh( callback );
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 1, dateBox );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 6);
	}
	
	private void paintCheckRow(final IFiscalModelCallback<Mod202> callback, IModelScript<Mod202Key> script) {
		int row = getTable().getRowCount();
		final Mod202Key key = script.getKeys()[0]; 
		final CheckBox check = new CheckBox();
		check.setText(script.getLabel());
		check.setEnabled(callback.getFiscalModel().isNotFinished());
		check.setValue(callback.getFiscalModel().getAmount(key) == 1);
		check.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				callback.getFiscalModel().putAmount(key,check.getValue()?1.0:0.0);
				calculateAndRefresh( callback );
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 0, check );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 7);
		
	}
	
	private void paintR18(final IFiscalModelCallback<Mod202> callback, IModelScript<Mod202Key> script) {
		int row = getTable().getRowCount();
		final Mod202Key key = script.getKeys()[0];
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		
		final TextBox textBox = new TextBox();
		textBox.setStyleName(AON.AON_CSS.aonInputText());
		textBox.setMaxLength(5);
		textBox.setVisibleLength(6);
		textBox.setValue(callback.getFiscalModel().getDescription(key));
		textBox.setEnabled(callback.getFiscalModel().isNotFinished());
		textBox.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.getFiscalModel().putDescription(key,textBox.getValue());
				calculateAndRefresh( callback );
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 1, textBox );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 6);
	}

	private void paintR19(final IFiscalModelCallback<Mod202> callback, IModelScript<Mod202Key> script) {
		int row = getTable().getRowCount();
		Mod202Key key = script.getKeys()[0];
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		
		final ListBox r19Box = new ListBox();
		r19Box.setWidth("200px");
		r19Box.addItem("NO CONSTA","0");
		r19Box.addItem("- Igual/sup. 10 mill. \u20AC e inferior a 20 mill. \u20AC","1");
		r19Box.addItem("- Igual/sup. 20 mill. \u20AC e inferior a 60 mill. \u20AC","2");
		r19Box.addItem("- Igual/sup. 60 mill. \u20AC.","3");
		int value = (int) callback.getFiscalModel().getAmount(key);
		if (value < 0 || value > 4) value = 0;
		r19Box.setSelectedIndex(value);
		r19Box.setEnabled(callback.getFiscalModel().isNotFinished());
		r19Box.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().putAmount(key,r19Box.getSelectedIndex());		
				calculateAndRefresh( callback );
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 1, r19Box );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 6);
	}
	
	private void paintR21(final IFiscalModelCallback<Mod202> callback, IModelScript<Mod202Key> script) {
		int row = getTable().getRowCount();
		Mod202Key key = script.getKeys()[0];
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		
		final ListBox r21Box = new ListBox();
		r21Box.setWidth("350px");
		r21Box.addItem(AON.MSG.calculation0(), "0");
		r21Box.addItem(AON.MSG.calculation1(), "1");
		r21Box.addItem(AON.MSG.calculation2(), "2");
		int value = (int) callback.getFiscalModel().getAmount(key);
		if (value < 0 || value > 2) value = 0;
		r21Box.setSelectedIndex(value);
		r21Box.setEnabled(callback.getFiscalModel().isNotFinished());
		r21Box.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().putAmount(Mod202Key.X00,r21Box.getSelectedIndex());
				calculationMethodChanged(callback,r21Box.getSelectedIndex());
				calculateAndRefresh( callback );
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 1, r21Box );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 6);
	}

	private void paintR62(final IFiscalModelCallback<Mod202> callback, IModelScript<Mod202Key> script) {
		int row = getTable().getRowCount();
		Mod202Key key = script.getKeys()[0];

		paintLabel(row, callback, script);
		getTable().getFlexCellFormatter().setColSpan(row, 0, 6);
		
		final TextBox textBox = new TextBox();
		textBox.setStyleName(AON.AON_CSS.aonInputText());
		textBox.setMaxLength(22);
		textBox.setVisibleLength(15);
		textBox.setValue(callback.getFiscalModel().getDescription(key));
		textBox.setEnabled(callback.getFiscalModel().isNotFinished());
		textBox.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.getFiscalModel().putDescription(key,textBox.getValue());
				calculateAndRefresh( callback );
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 1, textBox );
	}
	
	protected void calculationMethodChanged(IFiscalModelCallback<Mod202> callback,int method ) {
		
		boolean methodA = callback.getFiscalModel().isNotFinished() && (method == 0);
		boolean methodB1 = callback.getFiscalModel().isNotFinished() && (method == 1);
		boolean methodB2 = callback.getFiscalModel().isNotFinished() && (method == 2);
		boolean methodB = callback.getFiscalModel().isNotFinished() && (methodB1 || methodB2);
		
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
