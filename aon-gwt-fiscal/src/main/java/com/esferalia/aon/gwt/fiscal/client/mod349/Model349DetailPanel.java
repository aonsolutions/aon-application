package com.esferalia.aon.gwt.fiscal.client.mod349;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349Base.Model349BaseCallback;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349Detail.IModel349DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model349DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super(label);
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}
	private int tabIndex;
	
	public Model349DetailPanel(Mod349Detail detail, IModel349DetailCallback callback, Model349BaseCallback callbackM349) {
		
		boolean isGipuzkoa = callbackM349.getMod349().isGipuzkoa();
		boolean isDiffEnabled = callbackM349.getMod349().isDiffEnabled();
				
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		// Clave 		
		FlexTable tab1 = new FlexTable();		
		tab1.getColumnFormatter().setWidth(0, "auto");
		
		tab1.setStyleName(AON.AON_CSS.aonWidthAll());
		tab1.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab1.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.operationData()));
		
 		tab1.setWidget(1, 0, new MediumLabel(AON.MSG.key()));
 		
 		Mod349KeyListBox key = new Mod349KeyListBox();
 		key.setValue(detail.getType());
 		key.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setType(key.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 0, key);
		
		panel.add(tab1);
		
		// Pais / Documento / Nombre
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "150px");
		tab2.getColumnFormatter().setWidth(1, "100px");
		tab2.getColumnFormatter().setWidth(2, "auto");
		
		tab2.setStyleName(AON.AON_CSS.aonWidthAll());
		tab2.addStyleName(AON.AON_CSS.aonNowrap());

		tab2.setWidget(0, 0, new MediumLabel(AON.MSG.country()));
		tab2.setWidget(0, 1, new MediumLabel(AON.MSG.document()));
		tab2.setWidget(0, 2, new MediumLabel(AON.MSG.fullName()));
		
		Mod349CountryListBox country = new Mod349CountryListBox();
		country.setValue(detail.getCountry());
		country.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setCountry(country.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 0, country);
		
		TextBox document = new TextBox();		
		document.setVisibleLength(isGipuzkoa?12:15);		
		document.setMaxLength(isGipuzkoa?12:15);
		document.setStyleName(AON.AON_CSS.aonInputText());
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setDocument(document.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab2.setWidget(1, 1, document);
	
		TextBox name = new TextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setStyleName(AON.AON_CSS.aonInputText());		
		name.setValue(detail.getName());
		name.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setName(name.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab2.setWidget(1, 2, name);
		
		panel.add(tab2);
		
		// Acumulado / Declarado / A declarar / Boton Desglose Facturas / Botón Desglose cálculo por diferencia
		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, "100px");
		tab3.getColumnFormatter().setWidth(1, "100px");
		tab3.getColumnFormatter().setWidth(2, "100px");
		tab3.getColumnFormatter().setWidth(3, "auto");
		
		tab3.setStyleName(AON.AON_CSS.aonWidthAll());
		tab3.addStyleName(AON.AON_CSS.aonNowrap());
		
 		tab3.setWidget(0, 0, new MediumLabel("Acumulado"));
		tab3.setWidget(0, 1, new MediumLabel("Declarado"));
		tab3.setWidget(0, 2, new MediumLabel(isGipuzkoa ? "Base imponible / Importe rectificaci\u00F3n" : "Base imponible / Base imponible rectificada"));
		tab3.setWidget(0, 3, new MediumLabel(""));
		
		DoubleBox amount = new DoubleBox();
		DoubleBox declared = new DoubleBox();
		
		DoubleBox accumulated = new DoubleBox();
		accumulated.setValue(detail.getAccumulated());
		accumulated.setEnabled(false);  // Tal y como estaba antes, acumulado y declarado no se podían modificar, solo "a declarar"		
		accumulated.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {				
				// Recalcular amount con la diferencia de lo acumulado menos lo declarado
				// Dado que este campo no se dejara modificar, no es necesario recalcular la diferencia
//				if (accumulated.getValue() != detail.getAccumulated()) {					
//					double dif = accumulated.getValue()-declared.getValue();
//					amount.setValue(dif,false);
//					detail.setAmount(dif);				
//				}
				detail.setAccumulated(accumulated.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(1, 0, accumulated);
		
		declared.setValue(detail.getDeclared());
		declared.setEnabled(false);  // Tal y como estaba antes, acumulado y declarado no se podían modificar, solo "a declarar"
		declared.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular amount con la diferencia de lo acumulado menos lo declarado
				// Dado que este campo no se dejara modificar, no es necesario recalcular la diferencia
//				if (declared.getValue() != detail.getDeclared()) {
//					double dif = accumulated.getValue()-declared.getValue();
//					amount.setValue(dif,false);
//					detail.setAmount(dif);					
//				}
				detail.setDeclared(declared.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(1, 1, declared);
		
		// Se deja tal y como estaba antes en el modelo viejo, que solo se podía modificar el 
		// importe "a declarar", los importes de acumulado y declarado no se podían modificar
		amount.setValue(detail.getAmount());
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setAmount(amount.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(1, 2, amount);
				
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonNowrap());
		
		Button button = new Button("");
		button.setTitle(FiscalModelKeyInfo.INVOICE.getLabel());
		button.setStyleName(AON.AON_CSS.aonIconCommandButton());
		button.addStyleName(AON.AON_CSS.aonIconInvoice());
		button.setTabIndex(-2); // NO FOCUS
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Model349.SERVICE.getInfo(Model349.getCurrentDomainName(),Model349.getCurrentDomain(),
						   callbackM349.getMod349(), detail, FiscalModelKeyInfo.INVOICE, new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								callbackM349.showError(AON.MSG.errorMessage());
							}

							@Override
							public void onSuccess(String result) {
								callbackM349.showBreakdownPanel(result);
							}
					
						}
					);	
			}
		});
		buttonContainer.add(button);
		
		Button buttonDiff = new Button("");
		buttonDiff.setTitle(FiscalModelKeyInfo.DIFF_INVOICE.getLabel());
		buttonDiff.setStyleName(AON.AON_CSS.aonIconCommandButton());
		buttonDiff.addStyleName(AON.AON_CSS.aonIconDiff());
		buttonDiff.setTabIndex(-2); // NO FOCUS
		buttonDiff.setVisible(isDiffEnabled);			
		buttonDiff.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Model349.SERVICE.getInfo(Model349.getCurrentDomainName(),Model349.getCurrentDomain(),
						   callbackM349.getMod349(), detail, FiscalModelKeyInfo.DIFF_INVOICE, new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								callbackM349.showError(AON.MSG.errorMessage());
							}

							@Override
							public void onSuccess(String result) {
								callbackM349.showBreakdownPanel(result);
							}
					
						}
					);	
				
				
			}
		});
		buttonContainer.add(buttonDiff);			
		
		// Los botones solo se muestran si acumulado o declarado son distintos de cero
		buttonContainer.setVisible(accumulated.getValue() != 0 || declared.getValue() != 0);

		tab3.setWidget(1, 3, buttonContainer);
		
		// Vaciar y cerrar el panel de informacion de desglose 
		callbackM349.cleanBreakdownPanel();

		panel.add(tab3);
		
		// Rectificaciones: Año / Periodo / Importe anterior
		FlexTable tab4 = new FlexTable();
		tab4.getColumnFormatter().setWidth(0, "30px");
		tab4.getColumnFormatter().setWidth(1, "100px");		
		tab4.getColumnFormatter().setWidth(2, "auto");
		
		tab4.setStyleName(AON.AON_CSS.aonWidthAll());
		tab4.addStyleName(AON.AON_CSS.aonMarginTop());
		tab4.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab4.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab4.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab4.getFlexCellFormatter().setColSpan(0, 0, 3);
		tab4.setWidget(0, 0, new InlineLabel("Rectificaciones"));

		tab4.setWidget(1, 0, new MediumLabel(AON.MSG.year()));
		tab4.setWidget(1, 1, new MediumLabel(AON.MSG.period()));
		tab4.setWidget(1, 2, new MediumLabel(isGipuzkoa ? "" :  "Importe declarado anteriormente"));
		
		IntegerBox rectifiedYear = new IntegerBox();
		rectifiedYear.setMaxLength(4);
		rectifiedYear.setVisibleLength(4);
		rectifiedYear.setValue(detail.getRectifiedYear());
		rectifiedYear.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setRectifiedYear(rectifiedYear.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(2, 0, rectifiedYear);
		
		PeriodListBox rectifiedPeriod = new PeriodListBox();
		rectifiedPeriod.addItem( Period.YEAR.getDescription(), Integer.toString( Period.YEAR.ordinal() ) );
		rectifiedPeriod.setValue(detail.getRectifiedPeriod());
		rectifiedPeriod.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setRectifiedPeriod(rectifiedPeriod.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(2, 1, rectifiedPeriod);
		
		// En Gipuzkoa, se pone el importe rectificado, no se pone el importe nuevo y el anterior, como en el resto 
		if (!isGipuzkoa) {
			DoubleBox rectifiedAmount = new DoubleBox();
			rectifiedAmount.setValue(detail.getRectifiedAmount());
			rectifiedAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					detail.setRectifiedAmount(rectifiedAmount.getValue());
					callback.onValueChanged(detail);
				}
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
	}

	@Override
	public void setFocus(boolean focused) {
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}
	
}
