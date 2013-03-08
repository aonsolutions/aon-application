package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.sun.star.beans.GetDirectPropertyTolerantResult;

public class FxDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, FxDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	static enum Category { 
		ALL("Todos"), DATE("Fecha"), INFO("Información"), LOGIC("Lógico"), MATH(
				"Mátemáticas"), TEXT("Texto"), VAR("Variables");

		String name;

		private Category(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		static Category getByName(String name) {
			for (Category category : Category.values())
				if (category.name.equals(name))
					return category;
			return null;

		}
	}

	static enum Function {

		FALSE("FALSO", "Devuelve el valor lógico FALSO."), NOT("NO",
				"Cambia FALSO por VERDADERO y VERDADERO por FALSO.",
				"valor_lógico"), OR(
				"O",
				"Comprueba si alguno de los argumentos es VERDADERO, y devuelve VERDADERO o FALS0. Devuelve FALSO si todos los argumentos son FALSOS.",
				"valor_lógico 1", "valor_lógico 2", "..."), IF(
				"SI",
				"Comprueba si se cumple una condición y devuelve un valor si se evalúa como VERDADERO y otro valor si se evalúa como FALSO.",
				"prueba_lógica", "valor_si_verdadero", "valor_si_falso"), TRUE(
				"VERDADERO", "Devuelve el valor lógico VERDADERO."), AND(
				"O",
				"Comprueba si todos los argumentos son VERDADEROS. Devuelve VERDADERO si todos los argumentos son VERDADEROS.",
				"valor_lógico 1", "valor_lógico 2", "..."),

		NOW("AHORA",
				"Devuelve la fecha y hora actuales con formato de fecha y hora."), DAYS(
				"DIAS", "Calcula el número de dias entre dos fechas.",
				"fecha_inicial", "fecha_final"), DAYS360(
				"DIAS360",
				"Calcula el número de dias entre dos fechas basándose en un año de 360 días (doce mese de 30 días).",
				"fecha_inicial", "fecha_final"), TODAY("HOY",
				"Devuelve la fecha actual con formato de fecha."),

		ABS("HOY",
				"Devuelve el valor absoluto de un número, es decir, un número sin signo.");

		private Function(String name, String description, String... params) {
			this.name = name;
			this.description = description;
			this.params = params;
		}

		String name;
		String description;
		String params[];

		public String getName() {
			return name;
		}

		public String[] getParams() {
			return params;
		}

		public String getDescription() {
			return description;
		}

		public String getSyntax() {
			StringBuffer buff = new StringBuffer();
			buff.append(name);
			buff.append("(");
			for (int i = 0; i < params.length; i++)
				buff.append((i > 0 ? "," : "") + params[i]);
			buff.append(")");
			return buff.toString();
		}

		static Function getByName(String name) {
			for (Function function : Function.values())
				if (function.name.equals(name))
					return function;
			return null;
		}
	}

	private static Map<Category, Function[]> CATEGORY_FUNCTIONS_MAP = new HashMap<Category, Function[]>() {
		{
			put(Category.LOGIC, new Function[] { Function.FALSE, Function.NOT,
					Function.OR, Function.IF, Function.TRUE, Function.AND, });
			put(Category.DATE, new Function[] { Function.NOW, Function.DAYS,
					Function.DAYS360, Function.TODAY });
			put(Category.MATH, new Function[] { Function.ABS });
		}
	};

	@UiField
	ListBox categoryListBox;
	@UiField
	ListBox functionListBox;

	@UiField
	Label nameLabel;
	@UiField
	Label syntaxLabel;
	@UiField
	Label descriptionLabel;

	public FxDialog() {

		setCaption("Asistente");
		setWidget(binder.createAndBindUi(this));

		for (Category category : Category.values()) {
			categoryListBox.addItem(category.getName());
		}

		categoryListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int index = categoryListBox.getSelectedIndex();
				String name = categoryListBox.getItemText(index);
				Category category = Category.getByName(name);
				onCategorySelected(category);
			}
		});

		categoryListBox.setSelectedIndex(0);

		functionListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int index = functionListBox.getSelectedIndex();
				String name = functionListBox.getItemText(index);
				Function function = Function.getByName(name);
				onFunctionSelected(function);
			}
		});
	}

	private void onCategorySelected(Category category) {
		Function functions[] = CATEGORY_FUNCTIONS_MAP.get(category);
		for (Function function : functions) {
			Document.get();
			functionListBox.addItem(function.getName());
		}
	}

	private void onFunctionSelected(Function function) {
		nameLabel.setText(function.getName());
		syntaxLabel.setText(function.getSyntax());
		descriptionLabel.setText(function.getDescription());
	}
}
