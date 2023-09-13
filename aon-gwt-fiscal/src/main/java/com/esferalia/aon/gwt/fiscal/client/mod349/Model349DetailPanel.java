package com.esferalia.aon.gwt.fiscal.client.mod349;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349.Model349Callback;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349Detail.IModel349DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model349DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private static final String WIDTH_100PX = "100px";

	private static class Model349Smaller extends InlineLabel {
		private Model349Smaller(String label) {
			super(label);
			setStyleName(AON.CSS.aonFontSmaller());
		}
	}
	private int tabIndex;
	
	public Model349DetailPanel(Model349Callback callbackM349, Mod349 mod349, Mod349Detail detail, IModel349DetailCallback detailCallback) {
		
		boolean isGipuzkoa = mod349.isGipuzkoa();
		boolean isDiffEnabled = mod349.isDiffEnabled();
				
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		// Clave 		
		FlexTable tab1 = new FlexTable();		
		tab1.getColumnFormatter().setWidth(0, "auto");
		
		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());
		
		tab1.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.operationData()));
		
 		tab1.setWidget(1, 0, new Model349Smaller(AON.MSG.key()));
 		
 		Mod349KeyListBox key = new Mod349KeyListBox();
 		key.setValue(detail.getType());
 		key.addChangeHandler( event -> {
			detail.setType(key.getValue());
			detailCallback.onValueChanged(detail);
		});
		tab1.setWidget(2, 0, key);
		
		panel.add(tab1);
		
		// Pais / Documento / Nombre
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "150px");
		tab2.getColumnFormatter().setWidth(1, WIDTH_100PX);
		tab2.getColumnFormatter().setWidth(2, "auto");
		
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());

		tab2.setWidget(0, 0, new Model349Smaller(AON.MSG.country()));
		tab2.setWidget(0, 1, new Model349Smaller(AON.MSG.document()));
		tab2.setWidget(0, 2, new Model349Smaller(AON.MSG.fullName()));
		
		Mod349CountryListBox country = new Mod349CountryListBox();
		country.setValue(detail.getCountry());
		country.addChangeHandler( event -> {
			detail.setCountry(country.getValue());
			detailCallback.onValueChanged(detail);
		});
		tab2.setWidget(1, 0, country);
		
		TextBox document = new TextBox();		
		document.setVisibleLength(isGipuzkoa?12:15);		
		document.setMaxLength(isGipuzkoa?12:15);
		document.setStyleName(AON.CSS.aonInputText());
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(event -> {
			detail.setDocument(document.getValue());
			detailCallback.onNameChanged(detail);
		});
		tab2.setWidget(1, 1, document);
	
		TextBox name = new TextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setStyleName(AON.CSS.aonInputText());		
		name.setValue(detail.getName());
		name.addValueChangeHandler(event -> {
			detail.setName(name.getValue());
			detailCallback.onNameChanged(detail);
		});
		tab2.setWidget(1, 2, name);
		
		panel.add(tab2);
		
		// Acumulado / Declarado / A declarar / Boton Desglose Facturas / Botón Desglose cálculo por diferencia
		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, WIDTH_100PX);
		tab3.getColumnFormatter().setWidth(1, WIDTH_100PX);
		tab3.getColumnFormatter().setWidth(2, WIDTH_100PX);
		tab3.getColumnFormatter().setWidth(3, "auto");
		
		tab3.setStyleName(AON.CSS.aonWidthAll());
		tab3.addStyleName(AON.CSS.aonNowrap());
		
 		tab3.setWidget(0, 0, new Model349Smaller("Acumulado"));
		tab3.setWidget(0, 1, new Model349Smaller("Declarado"));
		tab3.setWidget(0, 2, new Model349Smaller(isGipuzkoa ? "Base imponible / Importe rectificaci\u00F3n" : "Base imponible / Base imponible rectificada"));
		tab3.setWidget(0, 3, new Model349Smaller(""));
		
		DoubleBox amount = new DoubleBox();
		DoubleBox declared = new DoubleBox();
		
		DoubleBox accumulated = new DoubleBox();
		accumulated.setValue(detail.getAccumulated());
		accumulated.setEnabled(false);  // Tal y como estaba antes, acumulado y declarado no se podían modificar, solo "a declarar"		
		accumulated.addValueChangeHandler(event -> {				
			detail.setAccumulated(accumulated.getValue());
			detailCallback.onValueChanged(detail);
		});
		tab3.setWidget(1, 0, accumulated);
		
		declared.setValue(detail.getDeclared());
		declared.setEnabled(false);  // Tal y como estaba antes, acumulado y declarado no se podían modificar, solo "a declarar"
		declared.addValueChangeHandler(event -> {
			detail.setDeclared(declared.getValue());
			detailCallback.onValueChanged(detail);
		});
		tab3.setWidget(1, 1, declared);
		
		// Se deja tal y como estaba antes en el modelo viejo, que solo se podía modificar el 
		// importe "a declarar", los importes de acumulado y declarado no se podían modificar
		amount.setValue(detail.getAmount());
		amount.addValueChangeHandler(event -> {
			detail.setAmount(amount.getValue());
			detailCallback.onValueChanged(detail);
		});
		tab3.setWidget(1, 2, amount);
				
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.CSS.aonNowrap());
		
		AonTableButton button = new AonTableButton(FiscalModelKeyInfo.INVOICE.getLabel(), AON.CSS.aonIconData());
		button.setTabIndex(-2); // NO FOCUS
		button.addClickHandler(event -> 
			Model349.SERVICE.getInfo(callbackM349.getOptions().getOccam(),
				   mod349, detail, FiscalModelKeyInfo.INVOICE, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						callbackM349.showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(String result) {
						callbackM349.showInfoPanel(result);
					}
			
				}
			));
		buttonContainer.add(button);
		
		AonTableButton buttonDiff = new AonTableButton(FiscalModelKeyInfo.DIFF_INVOICE.getLabel(), AON.CSS.aonIconDiff());
		buttonDiff.setTabIndex(-2); // NO FOCUS
		buttonDiff.setVisible(isDiffEnabled && !detail.isRectification());
		buttonDiff.addClickHandler(event -> 
			Model349.SERVICE.getInfo(callbackM349.getOptions().getOccam(), mod349, detail, FiscalModelKeyInfo.DIFF_INVOICE, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						callbackM349.showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(String result) {
						callbackM349.showInfoPanel(result);
					}
			
				}
			));
		buttonContainer.add(buttonDiff);			
		
		// Los botones solo se muestran si acumulado o declarado son distintos de cero
		buttonContainer.setVisible(accumulated.getValue() != 0 || declared.getValue() != 0);

		tab3.setWidget(1, 3, buttonContainer);
		
		// Vaciar y cerrar el panel de informacion de desglose
		callbackM349.cleanAndCloseInfoPanel();		

		panel.add(tab3);
		
		// Rectificaciones: Año / Periodo / Importe anterior
		FlexTable tab4 = new FlexTable();
		tab4.getColumnFormatter().setWidth(0, "30px");
		tab4.getColumnFormatter().setWidth(1, WIDTH_100PX);		
		tab4.getColumnFormatter().setWidth(2, "auto");
		
		tab4.setStyleName(AON.CSS.aonWidthAll());
		tab4.addStyleName(AON.CSS.aonMarginTop());
		tab4.addStyleName(AON.CSS.aonNowrap());
		
		tab4.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab4.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab4.getFlexCellFormatter().setColSpan(0, 0, 3);
		tab4.setWidget(0, 0, new InlineLabel("Rectificaciones"));

		tab4.setWidget(1, 0, new Model349Smaller(AON.MSG.year()));
		tab4.setWidget(1, 1, new Model349Smaller(AON.MSG.period()));
		tab4.setWidget(1, 2, new Model349Smaller(isGipuzkoa ? "" :  "Importe declarado anteriormente"));
		
		IntegerBox rectifiedYear = new IntegerBox();
		rectifiedYear.setMaxLength(4);
		rectifiedYear.setVisibleLength(4);
		rectifiedYear.setValue(detail.getRectifiedYear());
		rectifiedYear.addValueChangeHandler(event -> {
			detail.setRectifiedYear(rectifiedYear.getValue());
			detailCallback.onValueChanged(detail);
		});
		tab4.setWidget(2, 0, rectifiedYear);
		
		PeriodListBox rectifiedPeriod = new PeriodListBox();
		rectifiedPeriod.addItem( Period.YEAR.getDescription(), Integer.toString( Period.YEAR.ordinal() ) );
		rectifiedPeriod.setValue(detail.getRectifiedPeriod());
		rectifiedPeriod.addChangeHandler( event -> {
			detail.setRectifiedPeriod(rectifiedPeriod.getValue());
			detailCallback.onValueChanged(detail);
		});
		tab4.setWidget(2, 1, rectifiedPeriod);
		
		// En Gipuzkoa, se pone el importe rectificado, no se pone el importe nuevo y el anterior, como en el resto 
		if (!isGipuzkoa) {
			DoubleBox rectifiedAmount = new DoubleBox();
			rectifiedAmount.setValue(detail.getRectifiedAmount());
			rectifiedAmount.addValueChangeHandler(event -> {
				detail.setRectifiedAmount(rectifiedAmount.getValue());
				detailCallback.onValueChanged(detail);
			});
			tab4.setWidget(2, 2, rectifiedAmount);
		}
		
		panel.add(tab4);		
	
		scroll.setWidget(panel);
		setWidget(scroll);
	}


	@Override
	public int getTabIndex() {
		return tabIndex;
	}

	@Override
	public void setAccessKey(char key) {
		// Nothing
	}

	@Override
	public void setFocus(boolean focused) {
		// Nothing
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}
	
}
