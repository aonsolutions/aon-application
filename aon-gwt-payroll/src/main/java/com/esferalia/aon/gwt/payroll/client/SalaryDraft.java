package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.shared.DateUtils.addMonths2Date;
import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.gwt.common.shared.DateUtils.getLastDayOfMonth;
import static com.esferalia.aon.gwt.payroll.client.Constants.DESCRIPTION_MAX_LENGTH;
import static com.esferalia.aon.gwt.payroll.client.Constants.EXPRESSION_MAX_LENGTH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.gwt.common.shared.NumberUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject.Calculate;
import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject.CalculateCallback;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CompositeDeduction;
import com.esferalia.aon.gwt.payroll.shared.CompositePayment;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Employee.Dismissal;
import com.esferalia.aon.gwt.payroll.shared.Event;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.HasBonus;
import com.esferalia.aon.gwt.payroll.shared.HasDeduction;
import com.esferalia.aon.gwt.payroll.shared.HasPayment;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.gwt.payroll.shared.ItemComparator;
import com.esferalia.aon.gwt.payroll.shared.NoHolidaysVariable;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedDeductionVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedPaymentVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.RowLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.Timeline;
import com.esferalia.aon.gwt.visualization.client.visualizations.Tooltip;
import com.esferalia.aon.js.payroll.client.Reports;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.OnMouseOverHandler;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class SalaryDraft extends ResizeComposite
		implements CalculateCallback, SalarySelect.Listener, UndoManager.Listener{

	public static final String CUSTOM = "CUSTOM";
	public static final String ONLY_THIS_MONTH = "ONLY_THIS_MONTH";
	public static final String FROM_THIS_MONTH = "FROM_THIS_MONTH";
	
	public static final String A3 = SettleType.A3.name();
	public static final String LETTER = SettleType.LETTER.name();
	public static final String JASPER = SettleType.JASPER.name();
	public static final String STANDARD = SalaryType.STANDARD.name();
	public static final String STANDARD_NEW = SalaryType.STANDARD_NEW.name();
	public static final String STANDARD_COLS = SalaryType.STANDARD_COLS.name();
	public static final String RECIBE = SalaryType.RECIBE.name();
	public static final String RECIBE_CRA = SalaryType.RECIBE_CRA.name();

	private static final DateTimeFormat DATE_SHORT = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH_NUM_DAY);
	
	
	
	
	public static enum SettleType {
		A3,
		LETTER,
		JASPER
	}

	public static enum SalaryType {
		JASPER,
		RECIBE,
		RECIBE_CRA,
		STANDARD,
		STANDARD_NEW,
		STANDARD_COLS
	}

	private static Map<String, String> IRPF_ICONS = new HashMap<String, String>() {
		private static final long serialVersionUID = 784424826829639284L;

		{
			put("01", AON.AON_ICON_ARABA);
			put("48", AON.AON_ICON_BIZKAIA);
			put("20", AON.AON_ICON_GIPUZKOA);
			put("31", AON.AON_ICON_NAVARRA);
		}
	};

	@SuppressWarnings("serial")
	private static Map<Scope, String> SCOPE_DESCRIPTIONS = new HashMap<Scope, String>() {
		{
			put(Scope.AGREEMENT, "Convenio");
			put(Scope.APPLICATION, "Sistema");
			put(Scope.SYSTEM, "Sistema");
			put(Scope.CONTRACT, "Contrato");
		}
	};

	private static Map<String, String> COSTS_DESCRIPTIONS = new HashMap<String, String>() {

		private static final long serialVersionUID = 4224861477651976669L;

		{
			put("ECSS_E", "Prestaci\u00f3n por Incapacidad Temporal a cargo del INSS");
			put("ATEP_E", "Accidentes de Trabajo y Enfermedades Profesionales");
			put("IT_E", "Accidentes de Trabajo y Enfermedades Profesionales IT");
			put("IMS_E", "Accidentes de Trabajo y Enfermedades Profesionales IMS");
			put("FOGASA_E", "Fondo de Garant\u00eda Salarial ( FOGASA )");

		}
	};

	private static Deduction.Type SYSTEM_DEDUCTION[] = { Deduction.Type.IRPF, Deduction.Type.COMMON_CONTINGENCY,
			Deduction.Type.PROFESSIONAL_CONTINGENCY, Deduction.Type.UNEMPLOYMENT, Deduction.Type.JOB_TRAINING,
			Deduction.Type.STRUCTURAL_OVERTIME, Deduction.Type.NON_STRUCTURAL_OVERTIME, Deduction.Type.FOGASA };

	private static Map<Deduction.Type, String> DEDUCTION_DESCRIPTIONS = new HashMap<Deduction.Type, String>() {

		private static final long serialVersionUID = 4930183777517542277L;

		{
			put(Deduction.Type.IRPF, "IRPF");
			put(Deduction.Type.COMMON_CONTINGENCY, "Contingencias Comunes");
			put(Deduction.Type.PROFESSIONAL_CONTINGENCY, "Contingencias Profesionales");
			put(Deduction.Type.UNEMPLOYMENT, "Desempleo");
			put(Deduction.Type.JOB_TRAINING, "Formaci&oacute;n Profesional");
			put(Deduction.Type.STRUCTURAL_OVERTIME, "Horas Extraordinarias Fuerza Mayor");
			put(Deduction.Type.NON_STRUCTURAL_OVERTIME, "Resto Horas Extraordinarias");
		}
	};

	private List<Scope> SCOPE_STEPS = Arrays.asList(Scope.CONTRACT, Scope.AGREEMENT, Scope.SYSTEM);

	private static final String PORCENTAJE_IRPF = "PORCENTAJE_IRPF";
	private static final String PORCENTAJE_DESMPL = "PORCENTAJE_DESMPL";

	// @formatter:off
	private static String[] SKIP_VARIABLES = { 
			
			"CONVENIO", "SISTEMA", "NETO", "BRUTO", "GTZDO", "_OLD", // functions
			"GET_VARIABLE", "SI", "MAX", "MIN", "ABS", // functions

			"ANTICIPO_ATRASOS", PORCENTAJE_IRPF, PORCENTAJE_DESMPL, "PORCENTAJE_DESMPL_E", //

			"BASE_CGC", "BASE_CGP", "BASE_CGC_E", "BASE_CGP_E", // internals
			
			"BASE_CGC_MAX_MES", "BASE_CGC_MIN_MES", 
			"BASE_CGP_MAX_MES", "BASE_CGP_MIN_MES",
			"BASE_CGC_MAX_DIA", "BASE_CGC_MIN_DIA", 
			"BASE_CGP_MAX_DIA", "BASE_CGP_MIN_DIA",
			"REDUCCION_CGC_E_01", "REDUCCION_CGC_E_02",

			"BASE_ESTR", "BASE_NESTR", "TOTAL_DEVENGADO", // internals
			"ECSS", "DIAS_IT", 
			"DIAS_ENFERMEDAD_COMUN_4_15", // internals
			"DIAS_MATERNIDAD", 
			"DIAS_ENFERMEDAD_PROFESIONAL", // internals
			"DIAS_ENFERMEDAD_COMUN_1_3", // internals
			"DIAS_ENFERMEDAD_COMUN_16_20", // internals
			"DIAS_ENFERMEDAD_COMUN_21", // internals
			"DIAS_ENFERMEDAD_COMUN_366", // internals
			"DIAS_ENFERMEDAD_PROFESIONAL_366", // internals
			"DIAS_ENFERMEDAD_COMUN_CARENCIA", // internals
			"DIAS_ERE", "DIAS_PATERNIDAD", // internals
			"DIAS_TRABAJADOS", 

			"CONTEXT", "SELF", "THIS", // context

			"HORAS_LUNES", 
			"HORAS_MARTES", 
			"HORAS_MIERCOLES", 
			"HORAS_JUEVES", 
			"HORAS_VIERNES", 
			"HORAS_SABADO", 
			"HORAS_DOMINGO", 
			"HORAS_NOMINA", 
			"HORAS_TRABAJADAS", 

			"OCUPACION_IT", "OCUPACION_IMS", "PREST_IT" };

	private static String[] SUMMING_CONSTANTS = {
			"DIAS_TRABAJADOS",
			"HORAS_TRABAJADAS"
	};
	// @formatter:on

	static class VisibilityImpl implements HasVisibility {

		private com.google.gwt.dom.client.Element elem;

		public VisibilityImpl(com.google.gwt.dom.client.Element elem) {
			this.elem = elem;
		}

		@Override
		public boolean isVisible() {
			return UIObject.isVisible(elem);
		}

		@Override
		public void setVisible(boolean visible) {
			UIObject.setVisible(elem, visible);
		}

	}

	static class MyValueChangeEvent extends ValueChangeEvent<String> {

		protected MyValueChangeEvent(String value) {
			super(value);
		}

	}

	static class TextDateBox implements IsWidget, HasValue<String>, HasAllFocusHandlers, Focusable, HasEnabled {

		private DateBox datebox;

		public TextDateBox() {
			datebox = new DateBox();
		}

		// --------------------------------------------------- HasValue<String>
		@Override
		public String getValue() {
			return "\"" + format(datebox.getValue()) + "\"";
		}

		@Override
		public void setValue(String value) {
			datebox.setValue(parse(value));
		}

		@Override
		public void setValue(String value, boolean fire) {
			datebox.setValue(parse(value), fire);
		}

		@Override
		public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
			return datebox.addValueChangeHandler(new ValueChangeHandler<Date>() {

				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					handler.onValueChange(new MyValueChangeEvent(format(event.getValue())));
				}
			});
		}

		// ----------------------------------------------------------- IsWidget

		public Widget asWidget() {
			return datebox.asWidget();
		}

		// ---------------------------------------------------------- Focusable

		@Override
		public int getTabIndex() {
			return datebox.getTabIndex();
		}

		@Override
		public void setTabIndex(int index) {
			datebox.setTabIndex(index);
		}

		public void setFocus(boolean focused) {
			datebox.setFocus(focused);
		}

		@Override
		public void setAccessKey(char key) {
			datebox.setAccessKey(key);
		}

		// ------------------------------------------------ HasAllFocusHandlers

		@Override
		public void fireEvent(GwtEvent<?> event) {
			datebox.fireEvent(event);
		}

		@Override
		public HandlerRegistration addBlurHandler(BlurHandler handler) {
			return datebox.addDomHandler(handler, BlurEvent.getType());
		}

		@Override
		public HandlerRegistration addFocusHandler(FocusHandler handler) {
			return datebox.addDomHandler(handler, FocusEvent.getType());
		}

		// --------------------------------------------------------- HasEnabled

		@Override
		public boolean isEnabled() {
			return datebox.isEnabled();
		}

		@Override
		public void setEnabled(boolean enabled) {
			datebox.setEnabled(enabled);
		}
		
		// ----------------------------------------------------------- Delegate
		
		public final void ensureDebugId(String id) {
			datebox.ensureDebugId(id);
		}

		
		
		// ------------------------------------------------------------ Private
		private static Date parse(String str) {

			return StringUtils.isBlank(str) ? null : AON.DATE_FORMAT.parse(str);
		}

		private static String format(Date date) {
			return date == null ? null : AON.DATE_FORMAT.format(date);
		}

	}

	static class TextListBox extends ListBox implements HasValue<String> {

		@Override
		public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
			return addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					handler.onValueChange(new MyValueChangeEvent(TextListBox.this.getValue()));
				}
			});
		}

		@Override
		public String getValue() {
			return "\"" + getValue(getSelectedIndex()) + "\"";
		}

		@Override
		public void setValue(String value) {
			selectValue(value);
		}

		@Override
		public void setValue(String value, boolean fireEvents) {
			setValue(value);

		}

		// --------------------------------------------------------------------

		protected void selectValue(String value) {
			for (int i = 0; i < getItemCount(); i++) {
				if (StringUtils.equals(getValue(i), value)) {
					setSelectedIndex(i);
					return;
				}
			}
		}

	}

	static interface Factory<T, V> {
		T create(V v);
	}

	static interface VariableFactory<T> extends Factory<T, Variable> {
		T create(Variable variable);

		boolean accept(Variable variable);
	}
	
	static interface VariableEditorFactory<T extends IsWidget & HasValue<String> & HasAllFocusHandlers & Focusable & HasEnabled>
			extends VariableFactory<T> {
	}

	static interface VariableChangeHandlerFactory<T extends VariableChangeHandler<?>> extends VariableFactory<T> {
	}

	static class DefaultEditorFactory implements VariableEditorFactory<TextBox> {

		@Override
		public boolean accept(Variable variable) {
			return true;
		}

		@Override
		public TextBox create(Variable variable) {
			TextBox textBox = new ExpressionBox();
			textBox.setMaxLength(EXPRESSION_MAX_LENGTH);
			textBox.ensureDebugId("editor-" + variable.getName().toLowerCase());
			return textBox;
		}

	}
	
	static class AllFocusSuggestBox extends SuggestBox implements HasAllFocusHandlers{
		
		public AllFocusSuggestBox() {
			super();
		}

		public AllFocusSuggestBox(SuggestOracle oracle) {
			super(oracle);
		}

		public AllFocusSuggestBox(SuggestOracle oracle, ValueBoxBase<String> box) {
			super(oracle, box);
		}

		@Override
		public HandlerRegistration addBlurHandler(BlurHandler handler) {
			return getValueBox().addBlurHandler(handler);
		}

		@Override
		public HandlerRegistration addFocusHandler(FocusHandler handler) {
			return getValueBox().addFocusHandler(handler);
		}

		
	}
	
	static class CalendarConstantEditorFactory implements VariableEditorFactory<CalendarConstantLabel> {
		
		private String names [];
		
		
		public CalendarConstantEditorFactory(String... names ) {
			this.names = names;
		}
		
		@Override
		public boolean accept(Variable variable) {
			for ( String name : names )
				if ( name.equals(variable.getName()))
					return true;
			
			return false;
		}
		
		@Override
		public CalendarConstantLabel create(Variable variable) {
			CalendarConstantLabel constantLabel = new CalendarConstantLabel();
			constantLabel.ensureDebugId("editor-" + variable.getName().toLowerCase());
			constantLabel.addClickHandler(e -> EmployeeTree.showEmployeeCalendar());
			return constantLabel;
		}
	}
	
	static class WorkHoursEditorFactory implements VariableEditorFactory<AllFocusSuggestBox> {
		
		private String names [];
		
		public WorkHoursEditorFactory(String ...names) {
			this.names = names;
		}

		@Override
		public boolean accept(Variable variable) {
			
			for ( String name : names )
				if ( name.equals(variable.getName()))
					return true;
			
			return false;
		}

		@Override
		public AllFocusSuggestBox create(Variable variable) {
			
			ExpressionBox textBox = new ExpressionBox();
			textBox.setMaxLength(EXPRESSION_MAX_LENGTH);
			
			MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
			oracle.add("NO_LABORABLE");
			
			
			AllFocusSuggestBox allFocusSuggestBox = new AllFocusSuggestBox(oracle, textBox){
				
				@Override
				public void setValue(String value) {
					setText(value);
				}

				@Override
				public void setText(String text) {
					if ( text == null ) {
						text = "NO_LABORABLE";
						super.setText(text);
						return;
					}
					
					Double d = null ;
					try {
							d = Double.parseDouble(text);
					} catch ( NullPointerException | NumberFormatException ne){
						try {
							d = SalaryDraft.parse(text);
						} catch ( NullPointerException | NumberFormatException n3){
						}
					}

					if ( AonNumberUtils.equals(d, -1.00))
						text = "NO_LABORABLE";
					
					super.setText(text);
				}
				
			};
			
			allFocusSuggestBox.ensureDebugId("editor-" + variable.getName().toLowerCase());
			
			return allFocusSuggestBox;
			
		}

	}

	static class DateEditorFactory<E extends Enum<?> & HasDescription> implements VariableEditorFactory<TextDateBox> {

		private String name;

		public DateEditorFactory(String name) {
			this.name = name;
		}

		@Override
		public boolean accept(Variable variable) {
			return name.equals(variable.getName());
		}

		@Override
		public TextDateBox create(Variable variable) {
			TextDateBox textDateBox = new TextDateBox();

			textDateBox.ensureDebugId("editor-" + variable.getName().toLowerCase());

			return textDateBox;
		}

	}

	static class DaysEditorFactory<E extends Enum<?> & HasDescription> implements VariableEditorFactory<TextListBox> {

		private String name;

		public DaysEditorFactory(String name) {
			this.name = name;
		}

		@Override
		public boolean accept(Variable variable) {
			return name.equals(variable.getName());
		}

		@Override
		public TextListBox create(Variable variable) {
			TextListBox textListBox = new TextListBox() {
				@Override
				public String getValue() {
					return getValue(getSelectedIndex());
				}

			};

			int lastDay = DateUtils.getLastDayOfMonth(variable.getStartDate()).getDate();
			textListBox.addItem(String.valueOf(lastDay), String.valueOf(lastDay));

			textListBox.addItem("30", "30");

			try {
				int value = Integer.valueOf(variable.getValue().toString());
				if (value != 30 && value != lastDay)
					textListBox.addItem(variable.getValue().toString(), variable.getValue().toString());
			} catch (Exception e) {

			}

			textListBox.ensureDebugId("editor-" + variable.getName().toLowerCase());

			return textListBox;
		}

	}

	static class MonthDaysEditorFactory<E extends Enum<?> & HasDescription>
			implements VariableEditorFactory<TextListBox> {

		private String name;

		public MonthDaysEditorFactory(String name) {
			this.name = name;
		}

		@Override
		public boolean accept(Variable variable) {
			return name.equals(variable.getName());
		}

		@Override
		public TextListBox create(Variable variable) {
			TextListBox textListBox = new TextListBox() {
				@Override
				public String getValue() {
					return getValue(getSelectedIndex());
				}

			};

			textListBox.addItem(variable.getValue().toString(), variable.getValue().toString());

			textListBox.addItem("COTIZACI\u00D3N MENSUAL", "30");

			textListBox.addItem("COTIZACI\u00D3N DIARIA", "DIAS_NATURALES_MES");

			if (variable.getExpression() != null) {
				String expression = variable.getExpression();
				if (expression.matches("30(\\.0+)?")) {
					textListBox.selectValue("30");
					return textListBox;
				} else if (expression.matches("DIAS_NATURALES_MES")) {
					textListBox.selectValue("DIAS_NATURALES_MES");
					return textListBox;
				}
			}

			textListBox.ensureDebugId("editor-" + variable.getName().toLowerCase());

			return textListBox;
		}

	}

	static class EnumNameListBoxFactory<E extends Enum<?> & HasDescription>
			implements VariableEditorFactory<TextListBox> {

		private String name;
		private Class<E> enunn;

		public EnumNameListBoxFactory(String name, Class<E> enunn) {
			this.name = name;
			this.enunn = enunn;
		}

		@Override
		public boolean accept(Variable variable) {
			return name.equals(variable.getName());
		}

		@Override
		public TextListBox create(Variable variable) {
			TextListBox textListBox = new TextListBox();
			for (E e : enunn.getEnumConstants())
				textListBox.addItem(e.getDescription(), e.name());

			textListBox.ensureDebugId("editor-" + variable.getName().toLowerCase());
			
			return textListBox;
		}

	}

	static class StringsListBoxFactory implements VariableEditorFactory<TextListBox> {

		private String name;
		private String values[];
		private String labels[];

		public StringsListBoxFactory(String name, String values[], String labels[]) {
			this.name = name;
			this.values = values;
			this.labels = labels;
		}

		public StringsListBoxFactory(String name, String... values) {
			this.name = name;
			this.values = values;
			this.labels = values;
		}

		@Override
		public boolean accept(Variable variable) {
			return name.equals(variable.getName());
		}

		@Override
		public TextListBox create(Variable variable) {
			TextListBox textListBox = new TextListBox() {
				@Override
				public void setValue(String value) {
					if (value != null)
						super.setValue(value.replaceAll("^[\"'](.*)[\"']$", "$1"));
				}

			};
			for (int i = 0; i < values.length; i++)
				textListBox.addItem(labels[i], values[i]);

			textListBox.ensureDebugId("editor-" + variable.getName().toLowerCase());
			
			return textListBox;
		}

	}

	static class DismissalFactory implements VariableEditorFactory<TextListBox> {

		private String name;

		public DismissalFactory(String name) {
			this.name = name;
		}

		@Override
		public boolean accept(Variable variable) {
			return name.equals(variable.getName());
		}

		@Override
		public TextListBox create(Variable variable) {

			TextListBox textListBox = new TextListBox() {

				@Override
				public String getValue() {

					String value = super.getValue(getSelectedIndex());
					if (StringUtils.isBlank(value))
						return "NADA";

					Dismissal dismissal = Dismissal.valueOf(Dismissal.class, value);

					return getContextVariable(dismissal);
				}

				private String getContextVariable(Dismissal e) {

					switch (e) {
					case UNFAIR:
						return "IMPROCEDENTE";
					case TEMP_END:
						return "FIN_TEMPORAL";
					case WORK_END:
						return "FIN_OBRA";
					case DEFINITE_END:
						return "FIN";
					case OBJECTIVE:
						return "PROCEDENTE";
					case CONDITIONS_CHANGE:
						return "CAMBIO_CONDICIONES";
					}
					return null;
				}
			};

			textListBox.addItem("-", (String) null);

			for (Dismissal e : Dismissal.values())
				textListBox.addItem(e.getDescription(), e.name());

			textListBox.ensureDebugId("editor-" + variable.getName().toLowerCase());

			return textListBox;
		}

	}

	static class EnumIntListBoxFactory<E extends Enum<?> & HasDescription>
			implements VariableEditorFactory<TextListBox> {

		private String name;
		private Class<E> enunn;

		public EnumIntListBoxFactory(String name, Class<E> enunn) {
			this.name = name;
			this.enunn = enunn;
		}

		@Override
		public boolean accept(Variable variable) {
			return name.equals(variable.getName());
		}

		@Override
		public TextListBox create(Variable variable) {
			TextListBox textListBox = new TextListBox();
			for (E e : enunn.getEnumConstants())
				textListBox.addItem(e.getDescription(), e.name());

			textListBox.ensureDebugId("editor-" + variable.getName().toLowerCase());
			
			return textListBox;
		}

	}

	static class BooleanEditorFactory implements VariableEditorFactory<TextListBox> {

		public BooleanEditorFactory() {
		}

		@Override
		public boolean accept(Variable variable) {
			Object value = variable.getValue();

			if (value == null)
				return false;

			String str = value.toString();

			return str.equals(String.valueOf(true)) || str.equals(String.valueOf(false));
		}

		@Override
		public TextListBox create(Variable variable) {
			TextListBox textListBox = new TextListBox() {
				@Override
				public String getValue() {
					return getValue(getSelectedIndex());
				}
			};
			// textListBox.setC
			textListBox.addItem("SI", String.valueOf(true));
			textListBox.addItem("NO", String.valueOf(false));

			textListBox.ensureDebugId("editor-" + variable.getName().toLowerCase());

			return textListBox;
		}

	}

	class NoHolidaysChangeHanlder<T extends HasValue<String> & HasAllFocusHandlers & Focusable>
			extends VariableChangeHandler<T> {

		public NoHolidaysChangeHanlder(Variable variable) {
			super(variable);
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {

			double noHolidays = Double.parseDouble(event.getValue());

			List<Variable> noHolidaysVars = new ArrayList();
			for (Variable var : salaryDraftObject.getDrafContext())
				if (var.getName().equals(variable.getName()))
					noHolidaysVars.add(var);

			salaryDraftObject.removeDraftVariables(noHolidaysVars);

			Date startDate = DateUtils.copyDateOnly(salaryDraftObject.asSalaryPreview().getIssueDate());
			DateUtils.addDays2Date(startDate, 1);

			int prevDays = 0;
			NoHolidaysVariable noHolidaysVar = null;

			while (noHolidays > 0) {

				int monthDays = DateUtils.getDaysBetween(startDate, DateUtils.getLastDayOfMonth(startDate)) + 1;

				double days = Math.min(noHolidays, monthDays);

				noHolidaysVar = new NoHolidaysVariable();
				noHolidaysVar.setDays(days);
				noHolidaysVar.setPrevDays(prevDays);
				noHolidaysVar.setImplicit(false);
				noHolidaysVar.setScope(Scope.SALARY);
				noHolidaysVar.setName(variable.getName());
				noHolidaysVar.setSalaryDraft(salaryDraftObject.asSalaryPreview());

				salaryDraftObject.addDraftVariable(noHolidaysVar);

				noHolidays -= days;
				prevDays = (int) Math.ceil(days);
				DateUtils.addDays2Date(startDate, prevDays);

			}

			//salaryDraftObject.setDraftPeriod(salaryDraftObject.getDraftStartDate(), noHolidaysVar.getEndDate());

			SalaryDraft.this.calculate(getNextVariableFocusCallback());

		}

	}

	class VariableChangeHandler<T extends HasValue<String> & HasAllFocusHandlers & Focusable > 
			implements FocusHandler, BlurHandler, ValueChangeHandler<String> {

		protected T editor;
		protected Timer reset;
		protected Variable variable;

		public VariableChangeHandler(Variable variable) {
			this.variable = variable;
			this.reset = new Timer() {
				@Override
				public void run() {
					setValue(VariableChangeHandler.this.variable.getValue());
				}
			};
		}

		public void setLabel(Label label) {
			label.addDoubleClickHandler(new DoubleClickHandler() {

				@Override
				public void onDoubleClick(DoubleClickEvent event) {
					InputDialog inputDialog = new InputDialog("Renombrar...", "Nuevo Nombre") {
						@Override
						public void onAccept() {
							String newName = getInputValue();
							if (variable.getName().equals(newName))
								return;
							salaryDraftObject.renameVariable(variable, newName);
							salaryDraftObject.calculate(SalaryDraft.this);

						}
					};
					inputDialog.setInputValue(variable.getName());
					inputDialog.center();
					inputDialog.show();
				}
			});
		}

		public void setEditor(T editor) {
			this.editor = editor;
			this.editor.addBlurHandler(this);
			this.editor.addFocusHandler(this);
			this.editor.addValueChangeHandler(this);
			setValue(variable.getValue());
		}

		@Override
		public void onFocus(FocusEvent event) {
			setValue(variable.getExpression());
			fxButton.setEnabled(true);
			fxhasValue = editor;
		}

		@Override
		public void onBlur(BlurEvent event) {
			reset.schedule(300);
			fxButton.setEnabled(false);
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			reset.cancel();

			String value = editor.getValue();

			StringVariable var = newVariable();
			var.setExpression(StringUtils.isEmpty(value) ? "REMOVE_VARIABLE()" : value);

			salaryDraftObject.addDraftVariable(var);
			// salaryDraftObject.calculate(SalaryDraft.this);
			SalaryDraft.this.calculate(getNextVariableFocusCallback());
		}

		protected void setValue(Object value) {
			if (value == null)
				editor.setValue(null);
			else if (value instanceof Double)
				editor.setValue(SalaryDraft.format((Double) value));
			else if (value instanceof Date)
				editor.setValue(AON.DATE_FORMAT.format((Date) value));
			else
				editor.setValue(String.valueOf(value));
		}

		protected CalculateCallback getNextVariableFocusCallback() {
			class NextVariableFocusCallback implements CalculateCallback {
				private String name = VariableChangeHandler.this.variable.getName();
				
				@Override
				public Calculate getCalculate() {
					return SalaryDraft.this.getCalculate();
				}
				
				@Override
				public void onCalculateFailure(Throwable throwable) {
					onCalculateSucces(null);
				}

				@Override
				public void onCalculateSucces(SalaryDraftObject object) {
					VariableChangeHandler<?> handler = getNextVariableChangeHandler(name);
					if (handler != null)
						handler.editor.setFocus(true);
				}
			}
			return new NextVariableFocusCallback();
		}

		protected StringVariable newVariable() {
			// @formatter:off
			return new StringVariable.Builder().setImplicit(true).setScope(Scope.SALARY).setName(variable.getName())
					.setEndDate(variable.getEndDate()).setStartDate(variable.getStartDate()).create();
			// @formatter:on
		}
		
	}

	abstract class ItemChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers & Focusable, I extends Item> {

		I item;
		T expressionWidget;
		T descriptionWidget;
		UIObject editButton;

		public ItemChangeHandler(I item) {
			this.item = item;
		}

		public void setDescriptionWidget(final T widget) {
			descriptionWidget = widget;
			descriptionWidget.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					widget.setValue(item.getDescriptionTemplate());
					// TODO :
				}
			});
			descriptionWidget.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					if (!StringUtils.equals(widget.getValue(), item.getDescriptionTemplate()))
						onDescriptionChange(item, widget.getValue());
					else {
						String text = item.getDescription();
						widget.setValue(StringUtils.isEmpty(text)? item.getDescriptionTemplate(): text);
					}
				}
			});
		}

		public void setExpressionWidget(final T widget) {
			expressionWidget = widget;
			expressionWidget.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					widget.setValue(item.getExpression());
					fxButton.setEnabled(true);
					fxhasValue = widget;
				}
			});
			expressionWidget.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					if (!StringUtils.equals(widget.getValue(), item.getExpression())) {
						onExpressionChange(item, widget.getValue());
					} else {
						String text = format(item.getAmount());
						widget.setValue(text != null ? text : item.getExpression());
					}

					fxButton.setEnabled(false);
				}
			});
		}

		public <B extends UIObject & HasClickHandlers>  void setEditButton(B editButton) {
			this.editButton = editButton;
			editButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onEdit();
				}
			});
		}

		public void setExpandButton(final Button expandButton) {
			expandButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (isExpand(expandButton)) {
						onExpand(event);
						setCollapse(expandButton);
					} else {
						onCollapse(event);
						setExpand(expandButton);
					}
				}

				private void setExpand(Button button) {
					button.removeStyleName(AON.AON_ICON_COLLAPSE);
					button.setStyleName(AON.AON_ICON_EXPAND, true);
				}

				private void setCollapse(Button button) {
					button.removeStyleName(AON.AON_ICON_EXPAND);
					button.setStyleName(AON.AON_ICON_COLLAPSE, true);
				}

				private boolean isExpand(Button button) {
					String styleName = button.getStyleName();
					return styleName.contains(AON.AON_ICON_EXPAND);
				}

			});
		}

		public void setDeleteButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onExpressionChange(item, "REMOVE()");
				}
			});
		}

		public void setAgreementButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onRecover(item, "CONVENIO()");
				}
			});
		}

		abstract void onEdit();

		abstract void onExpand(ClickEvent event);

		abstract void onCollapse(ClickEvent event);

		abstract void onRecover(I item, String expression);

		abstract void onExpressionChange(I item, String expression);

		abstract void onDescriptionChange(I item, String description);

	}

	class PaymentChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers & Focusable>
			extends ItemChangeHandler<T, Payment> implements PaymentDialog.Callback {

		public PaymentChangeHandler(Payment payment) {
			super(payment);
		}

		// --------------------------------------------------------------------
		@Override
		public void onAccept(PaymentDialog dialog) {
			item.setScope(Scope.SALARY);
			item.setType(dialog.getType());
			item.setMonth(dialog.getMonth());
			item.setName(dialog.getName());
			//item.setDescription(dialog.getDescription()); TODO: ???
			item.setDescriptionTemplate(dialog.getDescription());
			item.setExpression(dialog.getPaymentExpression());
			item.setIrpfExpression(dialog.getIrpfExpression());
			item.setQuoteExpression(dialog.getQuoteExpression());
			item.setSalaryType(salaryDraftObject.getType());
			salaryDraftObject.addDraftPayment(item);
			salaryDraftObject.calculate(SalaryDraft.this);

		}

		@Override
		void onEdit() {
			PaymentDialog paymentDialog = new PaymentDialog();
			paymentDialog.setNumberFormat(AON.CURRENCY_FORMAT);
			paymentDialog.setContextProvider(salaryDraftObject);
			paymentDialog.setConcept(getConcept());
			paymentDialog.setMonth(item.getMonth());
			paymentDialog.setType(item.getType());
			paymentDialog.setReceiptType(item.getSalaryType());
			// TODO : description template ?
			paymentDialog.setDescription(item.getDescription());
			paymentDialog.setPaymentExpression(item.getExpression()); //
			paymentDialog.setIrpfExpression(item.getIrpfExpression());
			paymentDialog.setQuoteExpression(item.getQuoteExpression());
			
			paymentDialog.setAvailablePayments(salaryDraftObject.getPayments());

			paymentDialog.center();
			paymentDialog.show(this);
		}

		@Override
		void onExpand(ClickEvent event) {
			int row = getRowIndex(event);
			int childs = ((CompositePayment) item).getChilds().size();
			for (int i = 1; i <= childs; i++)
				paymentsTable.getRowFormatter().getElement(row + i).getStyle().clearDisplay();
			;
		}

		@Override
		void onCollapse(ClickEvent event) {
			int row = getRowIndex(event);
			int childs = ((CompositePayment) item).getChilds().size();
			for (int i = 1; i <= childs; i++)
				paymentsTable.getRowFormatter().getElement(row + i).getStyle().setDisplay(Display.NONE);
			;
		}

		@Override
		void onDescriptionChange(Payment payment, String description) {
			payment.setScope(Scope.SALARY);
			payment.setDescriptionTemplate(description);
			salaryDraftObject.addDraftPayment(payment);
			SalaryDraft.this.calculate(getExpressionFocusCallback());
		}

		@Override
		void onExpressionChange(Payment payment, String expression) {
			payment.setScope(Scope.SALARY);
			payment.setExpression(expression);
			salaryDraftObject.addDraftPayment(payment);
			SalaryDraft.this.calculate(getNextPaymentFocusCallback());
		}

		@Override
		void onRecover(Payment payment, String expression) {
			payment.setScope(Scope.SALARY);
			payment.setExpression(expression);
			salaryDraftObject.recoverDraftPayment(payment);
			SalaryDraft.this.calculate(getNextPaymentFocusCallback());
		}
		// --------------------------------------------------------------------
		private Payment getConcept() {
			if (item.getName() == null)
				return null;
			for (Payment payment : availablePaymens)
				if (StringUtils.equals(payment.getName(), item.getName()))
					return payment;
			return null;
		}

		private int getRowIndex(ClickEvent event) {
			return paymentsTable.getCellForEvent(event).getRowIndex();
		}

		private CalculateCallback getNextPaymentFocusCallback() {

			class NextItemFocusCallback implements CalculateCallback {

				private int id = PaymentChangeHandler.this.item.getId();

				@Override
				public Calculate getCalculate() {
					return SalaryDraft.this.getCalculate();
				}

				@Override
				public void onCalculateFailure(Throwable throwable) {
				}

				@Override
				public void onCalculateSucces(SalaryDraftObject object) {
					PaymentChangeHandler<?> handler = SalaryDraft.this.getNextPaymentChangeHandlerFor(id);
					if (handler != null)
						handler.descriptionWidget.setFocus(true);
					else
						newPaymentHandler.descriptionBox.setFocus(true);
				}
			}

			return new NextItemFocusCallback();
		}

		private CalculateCallback getExpressionFocusCallback() {

			class ExpressionFocusCallback implements CalculateCallback {

				private int id = PaymentChangeHandler.this.item.getId();

				@Override
				public Calculate getCalculate() {
					return SalaryDraft.this.getCalculate();
				}
				@Override
				public void onCalculateFailure(Throwable throwable) {
					onCalculateSucces(null);
				}

				@Override
				public void onCalculateSucces(SalaryDraftObject object) {
					PaymentChangeHandler<?> handler = SalaryDraft.this.getPaymentChangeHandlerFor(id);
					if (handler != null)
						handler.expressionWidget.setFocus(true);

				}
			}

			return new ExpressionFocusCallback();
		}
	}

	class DeductionChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers & Focusable>
			extends ItemChangeHandler<T, Deduction> implements DeductionDialog.Callback {

		// --------------------------------------------------------------------
		@Override
		public void onAccept(DeductionDialog dialog) {

			item.setScope(Scope.SALARY);
			item.setType(dialog.getType());
			//item.setDescription(dialog.getDescription());
			item.setExpression(dialog.getDeductionExpression());
			item.setDescriptionTemplate(dialog.getDescription());

			if (item.getType() == Deduction.Type.EMBARGO)
				salaryDraftObject.addDraftEmbargo(item);
			else
				salaryDraftObject.addDraftDeduction(item);

			salaryDraftObject.calculate(SalaryDraft.this);

		}

		@Override
		void onEdit() {
			DeductionDialog deductionDialog = new DeductionDialog();
			deductionDialog.setType(item.getType());
			deductionDialog.setContextProvider(salaryDraftObject);
			// TODO: description template ?
			deductionDialog.setDescription(item.getDescription());
			deductionDialog.setDeductionExpression(item.getExpression());

			deductionDialog.center();
			deductionDialog.show(this);
		}

		@Override
		void onExpand(ClickEvent event) {
			int row = getRowIndex(event);
			int childs = ((CompositeDeduction) item).getChilds().size();
			for (int i = 1; i <= childs; i++)
				paymentsTable.getRowFormatter().getElement(row + i).getStyle().clearDisplay();
			;
		}

		@Override
		void onCollapse(ClickEvent event) {
			int row = getRowIndex(event);
			int childs = ((CompositeDeduction) item).getChilds().size();
			for (int i = 1; i <= childs; i++)
				paymentsTable.getRowFormatter().getElement(row + i).getStyle().setDisplay(Display.NONE);
			;
		}

		public DeductionChangeHandler(Deduction deduction) {
			super(deduction);
		}

		@Override
		void onDescriptionChange(Deduction item, String description) {
			item.setScope(Scope.SALARY);
			item.setDescriptionTemplate(description);

			if (item.getType() == Deduction.Type.EMBARGO)
				salaryDraftObject.addDraftEmbargo(item);
			else
				salaryDraftObject.addDraftDeduction(item);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

		@Override
		void onExpressionChange(Deduction item, String expression) {
			item.setScope(Scope.SALARY);

			item.setExpression(expression);

			if (item.getType() == Deduction.Type.EMBARGO)
				salaryDraftObject.addDraftEmbargo(item);
			else
				salaryDraftObject.addDraftDeduction(item);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

		@Override
		void onRecover(Deduction item, String expression) {
		}

		private int getRowIndex(ClickEvent event) {
			return paymentsTable.getCellForEvent(event).getRowIndex();
		}
	}

	class BonusChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers & Focusable>
			extends ItemChangeHandler<T, Bonus> implements BonusDialog.Callback {

		// --------------------------------------------------------------------
		@Override
		public void onAccept(BonusDialog dialog) {

			item.setScope(Scope.SALARY);
			item.setType(dialog.getType());
			//item.setDescription(dialog.getDescription());
			item.setExpression(dialog.getDeductionExpression());
			item.setDescriptionTemplate(dialog.getDescription());

			salaryDraftObject.addDraftBonus(item);
			salaryDraftObject.calculate(SalaryDraft.this);

		}

		@Override
		void onEdit() {
			BonusDialog bonusDialog = new BonusDialog();
			bonusDialog.setType(item.getType());
			bonusDialog.setContextProvider(salaryDraftObject);
			//TODO:  description template ?
			bonusDialog.setDescription(item.getDescription());
			bonusDialog.setDeductionExpression(item.getExpression());

			bonusDialog.center();
			bonusDialog.show(this);
		}

		@Override
		void onExpand(ClickEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		void onCollapse(ClickEvent event) {
			// TODO Auto-generated method stub

		}

		public BonusChangeHandler(Bonus bonus) {
			super(bonus);
		}

		@Override
		void onDescriptionChange(Bonus item, String description) {
			item.setScope(Scope.SALARY);
			item.setDescriptionTemplate(description);

			salaryDraftObject.addDraftBonus(item);
			salaryDraftObject.calculate(SalaryDraft.this);
		}

		@Override
		void onExpressionChange(Bonus item, String expression) {
			item.setScope(Scope.SALARY);
			item.setExpression(expression);

			salaryDraftObject.addDraftBonus(item);
			salaryDraftObject.calculate(SalaryDraft.this);
		}

		@Override
		void onRecover(Bonus item, String expression) {
		}
	}

	abstract class NewItemHandler<T extends Item> extends DefaultSuggestionDisplay {

		Button recoverButton;
		TextBox expressionBox;
		SuggestBox descriptionBox;
		MultiWordSuggestOracle oracle;

		Map<String, T> itemsConceptsMap = new HashMap<String, T>();

		public void setDescriptionBox(SuggestBox descriptionBox) {
			this.descriptionBox = descriptionBox;
			

			this.descriptionBox.getValueBox().addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					if (((DefaultSuggestionDisplay) NewItemHandler.this.descriptionBox.getSuggestionDisplay())
							.isSuggestionListShowing())
						return;

					String value = NewItemHandler.this.descriptionBox.getValue();
					if (!StringUtils.isBlank(value)) {
						onValueChange(NewItemHandler.this.descriptionBox,
								NewItemHandler.this.descriptionBox.getValue());
					}
				}
			});
			this.descriptionBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {
					onValueChange(NewItemHandler.this.descriptionBox, event.getSelectedItem().getReplacementString());
				}
			});
			
			this.descriptionBox.getValueBox().addKeyDownHandler( (event) -> {
//				Window.alert("KeyDown: " + event.getNativeEvent().getKeyCode() 
//						+ ", " + ( event.isControlKeyDown() && KeyCodes.KEY_SPACE == event.getNativeEvent().getKeyCode()));
				if ( KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode() )
					this.descriptionBox.hideSuggestionList();
				else if ( event.isControlKeyDown() && KeyCodes.KEY_SPACE == event.getNativeEvent().getKeyCode())
					this.descriptionBox.showSuggestionList();
			});
		}

		public void setOracle(MultiWordSuggestOracle oracle) {
			this.oracle = oracle;
		}

		public void setExpressionBox(TextBox expressionBox) {
			this.expressionBox = expressionBox;

			this.expressionBox.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					onValueChange(NewItemHandler.this.expressionBox, NewItemHandler.this.descriptionBox.getValue());
				}
			});
		}

		protected void initSuggestionItems() {

			for (T item : getAvailableItems()) {
				String suggestion = getSuggestionString(item);
				if (!StringUtils.isEmpty(suggestion)) {
					oracle.add(suggestion);
					itemsConceptsMap.put(suggestion, item);
				}
			}
			
			oracle.setDefaultSuggestionsFromText(itemsConceptsMap.keySet());

		}

		protected void onValueChange(UIObject source, String suggestion) {

			T item = itemsConceptsMap.get(suggestion);

			String expression = null;
			if (item != null) {
				expression = item.getExpression();
			}

			if (StringUtils.isBlank(expression)) {

				expression = expressionBox.getValue();
				if (StringUtils.isBlank(expression) && (source != NewItemHandler.this.expressionBox)) {
					expressionBox.setVisible(true);
					// wait for event's loop to terminate.
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							expressionBox.setFocus(true);
						}
					});

					return;
				}
			}
			addDrafItem(item, expression);

			calculate(item);
		}

		public void setNewButton(HasClickHandlers newButton) {
			newButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onEdit();
				}
			});
		}

		protected T getItem(String description) {
			return itemsConceptsMap.get(description);
		}

		protected void calculate(T item) {
			salaryDraftObject.calculate(SalaryDraft.this);
		}

		protected abstract void onEdit();

		protected abstract List<T> getAvailableItems();

		protected abstract void addDrafItem(T item, String expr);

	}

	class NewDeductionHandler extends NewItemHandler<Deduction> implements DeductionDialog.Callback {

		// ------------------------------------------- NewItemHandler<Deduction>

		@Override
		protected void onEdit() {
			// TODO Auto-generated method stub
			DeductionDialog deductionDialog = new DeductionDialog();
			deductionDialog.setContextProvider(salaryDraftObject);

			deductionDialog.center();
			deductionDialog.show(this);

		}

		@Override
		protected List<Deduction> getAvailableItems() {
			return SalaryDraft.this.availableDeductions;
		}

		@Override
		protected void addDrafItem(Deduction item, String expr) {
			Deduction deduction = new Deduction();

			deduction.setExpression(expr);
			if (item != null) {
				deduction.setType(item.getType());
				deduction.setName(item.getName());
			} else {
				deduction.setType(Deduction.Type.OTHER);
			}
			deduction.setScope(Scope.SALARY);
			//deduction.setDescription(descriptionBox.getValue());
			deduction.setSalaryType(salaryDraftObject.getType());
			deduction.setStartDate(salaryDraftObject.getEndDate());
			deduction.setStartDate(salaryDraftObject.getStartDate());
			deduction.setDescriptionTemplate(descriptionBox.getValue());

			if (deduction.getType() == Deduction.Type.EMBARGO)
				salaryDraftObject.addDraftEmbargo(deduction);
			else
				salaryDraftObject.addDraftDeduction(deduction);
		}

		// ------------------------------------------- DeductionDialog.Callback

		@Override
		public void onAccept(DeductionDialog dialog) {
			Deduction deduction = new Deduction();

			deduction.setScope(Scope.SALARY);
			deduction.setType(dialog.getType());
			//deduction.setDescription(dialog.getDescription());
			deduction.setSalaryType(salaryDraftObject.getType());
			deduction.setStartDate(salaryDraftObject.getEndDate());
			deduction.setStartDate(salaryDraftObject.getStartDate());
			deduction.setExpression(dialog.getDeductionExpression());
			deduction.setDescriptionTemplate(dialog.getDescription());

			Item<Deduction.Type> concept = dialog.getConcept();
			deduction.setConceptId(concept != null ? concept.getId() : null);
			deduction.setName(concept != null ? concept.getName() : null);

			if (deduction.getType() == Deduction.Type.EMBARGO)
				salaryDraftObject.addDraftEmbargo(deduction);
			else
				salaryDraftObject.addDraftDeduction(deduction);

			calculate(deduction);
		}

	}

	class NewPaymentHandler extends NewItemHandler<Payment> implements PaymentDialog.Callback {

		// -------------------------------------------- NewItemHandler<Payment>

		@Override
		protected void onEdit() {
			PaymentDialog paymentDialog = new PaymentDialog();
			paymentDialog.setNumberFormat(AON.CURRENCY_FORMAT);
			paymentDialog.setContextProvider(salaryDraftObject);

			paymentDialog.center();
			paymentDialog.show(this);

		}

		@Override
		protected List<Payment> getAvailableItems() {
			return SalaryDraft.this.availablePaymens;
		}

		@Override
		protected void calculate(Payment payment) {
			SalaryDraft.this.calculate(getNewPaymentFocusCallback());
		}

		@Override
		protected void addDrafItem(Payment payment, String expression) {
			Payment draftPayment = new Payment();

			draftPayment.setExpression(expression);
			if (payment != null) {
				draftPayment.setName(payment.getName());
				draftPayment.setType(payment.getType());
				draftPayment.setConceptId(payment.getId());
				//draftPayment.setDescription(payment.getDescription());
				draftPayment.setIrpfExpression(payment.getIrpfExpression());
				draftPayment.setQuoteExpression(payment.getQuoteExpression());
				draftPayment.setDescriptionTemplate(payment.getDescription());
			} else {
				draftPayment.setIrpfExpression("_P");
				draftPayment.setQuoteExpression("_P");
				draftPayment.setType(Payment.Type.DEFAULT);
				//draftPayment.setDescription(descriptionBox.getText());
				draftPayment.setDescriptionTemplate(descriptionBox.getText());
			}
			draftPayment.setScope(Scope.SALARY);
			draftPayment.setEndDate(salaryDraftObject.getEndDate());
			draftPayment.setStartDate(salaryDraftObject.getStartDate());
			draftPayment.setSalaryType(salaryDraftObject.getType());
			// draftPayment.setMonth(deduction.getMonth());

			salaryDraftObject.addDraftPayment(draftPayment);

		}

		@Override
		protected void showSuggestions(SuggestBox suggestBox, Collection<? extends Suggestion> suggestions,
				boolean isDisplayStringHTML, boolean isAutoSelectEnabled, SuggestionCallback callback) {

			Collection<Suggestion> mySuggestions = new ArrayList<Suggestion>(suggestions.size());

			for (Suggestion suggestion : suggestions) {
				String replacementString = suggestion.getReplacementString();
				Payment payment = getItem(replacementString);

				SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

				String clazz = null;
				if (StringUtils.isEmpty(payment.getName()))
					clazz = payment.getScope() == Scope.CONTRACT ? "employee_payment" : "enterprise_payment";
				else
					clazz = "payment_concept";

				htmlBuilder.appendHtmlConstant("<span class=\"" + clazz + "\" >");
				htmlBuilder.appendHtmlConstant(suggestion.getDisplayString());
				htmlBuilder.appendHtmlConstant("</span>");

				mySuggestions.add(new MultiWordSuggestOracle.MultiWordSuggestion(replacementString,
						htmlBuilder.toSafeHtml().asString()));
			}

			super.showSuggestions(suggestBox, mySuggestions, isDisplayStringHTML, isAutoSelectEnabled, callback);
		}

		// --------------------------------------------- PaymentDialog.Callback

		@Override
		public void onAccept(PaymentDialog dialog) {
			Payment draftPayment = new Payment();
			Payment concept = dialog.getConcept();
			draftPayment.setConceptId(concept != null ? concept.getId() : null);
			draftPayment.setName(concept != null ? concept.getName() : null);
			draftPayment.setType(dialog.getType());
			draftPayment.setScope(Scope.SALARY);
			draftPayment.setMonth(dialog.getMonth());
			draftPayment.setEndDate(SalaryDraft.this.salaryDraftObject.getEndDate());
			draftPayment.setStartDate(SalaryDraft.this.salaryDraftObject.getStartDate());
			//draftPayment.setDescription(dialog.getDescription());
			draftPayment.setDescriptionTemplate(dialog.getDescription());
			draftPayment.setExpression(dialog.getPaymentExpression());
			draftPayment.setIrpfExpression(dialog.getIrpfExpression());
			draftPayment.setQuoteExpression(dialog.getQuoteExpression());
			draftPayment.setSalaryType(SalaryDraft.this.salaryDraftObject.getType());
			salaryDraftObject.addDraftPayment(draftPayment);
			calculate(draftPayment);
		}

		private CalculateCallback getNewPaymentFocusCallback() {

			class NextItemFocusCallback implements CalculateCallback {

				@Override
				public Calculate getCalculate() {
					return SalaryDraft.this.getCalculate();
				}

				@Override
				public void onCalculateFailure(Throwable throwable) {
					onCalculateSucces(null);
				}

				@Override
				public void onCalculateSucces(SalaryDraftObject object) {
					newPaymentHandler.descriptionBox.setFocus(true);
				}
			}

			return new NextItemFocusCallback();
		}

	}

	class NewBonusHandler extends NewItemHandler<Bonus> implements BonusDialog.Callback {

		// ------------------------------------------- NewItemHandler<Bonus>

		@Override
		protected void onEdit() {
			BonusDialog bonusDialog = new BonusDialog();
			bonusDialog.setContextProvider(salaryDraftObject);

			bonusDialog.center();
			bonusDialog.show(this);

		}

		@Override
		protected List<Bonus> getAvailableItems() {
			return SalaryDraft.this.availableBonus;
		}

		@Override
		protected void addDrafItem(Bonus item, String expr) {
			Bonus bonus = new Bonus();

			bonus.setExpression(expr);
			if (item != null) {
				bonus.setType(item.getType());
				bonus.setName(item.getName());
				bonus.setConceptId(item.getId());
			} else {
				bonus.setType(Bonus.Type.SOCIAL_SECURITY); // TODO: Sure?
			}
			bonus.setScope(Scope.SALARY);
			//bonus.setDescription(descriptionBox.getValue());
			bonus.setDescriptionTemplate(descriptionBox.getValue());
			bonus.setSalaryType(salaryDraftObject.getType());
			bonus.setStartDate(salaryDraftObject.getEndDate());
			bonus.setStartDate(salaryDraftObject.getStartDate());

			salaryDraftObject.addDraftBonus(bonus);
		}

		// ------------------------------------------- DeductionDialog.Callback

		@Override
		public void onAccept(BonusDialog dialog) {
			Bonus bonus = new Bonus();

			bonus.setScope(Scope.SALARY);
			bonus.setType(dialog.getType());
			//bonus.setDescription(dialog.getDescription());
			bonus.setSalaryType(salaryDraftObject.getType());
			bonus.setStartDate(salaryDraftObject.getEndDate());
			bonus.setStartDate(salaryDraftObject.getStartDate());
			bonus.setExpression(dialog.getDeductionExpression());
			bonus.setDescriptionTemplate(dialog.getDescription());

			Item<Bonus.Type> concept = dialog.getConcept();
			bonus.setConceptId(concept != null ? concept.getId() : null);
			bonus.setName(concept != null ? concept.getName() : null);

			calculate(bonus);
		}

	}

	abstract class AbstractVarHandler implements ClickHandler {

		private Variable variable;

		public AbstractVarHandler(Variable variable) {
			this.variable = variable;
		}

		@Override
		public void onClick(ClickEvent event) {
			StringVariable var = new StringVariable();
			var.setImplicit(false);
			var.setScope(Scope.SALARY); // DRAFT
			var.setName(variable.getName());
			var.setEndDate(variable.getEndDate());
			var.setStartDate(variable.getStartDate());
			var.setExpression(getExpression(var));
			salaryDraftObject.addDraftVariable(var);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

		abstract String getExpression(Variable var);

	}

	class VariableRemoveHandler implements ClickHandler {

		private Variable variable;

		public VariableRemoveHandler(Variable variable) {
			this.variable = variable;
		}

		@Override
		public void onClick(ClickEvent event) {
			StringVariable var = new StringVariable();
			var.setImplicit(false);
			var.setScope(Scope.SALARY); // DRAFT
			var.setName(variable.getName());
			var.setEndDate(variable.getEndDate());
			var.setStartDate(variable.getStartDate());
			var.setExpression("REMOVE_VARIABLE()");
			
			salaryDraftObject.addDraftVariable(var);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

	}

	abstract class RecoverItemHandler<T extends Item> implements ClickHandler {

		private T item;

		public RecoverItemHandler(T item) {
			this.item = item;
		}

		@Override
		public void onClick(ClickEvent event) {
			addDrafItem(item);
			salaryDraftObject.calculate(SalaryDraft.this);

		}

		abstract void addDrafItem(T item);

	}

	class RecoverPaymentHandler extends RecoverItemHandler<Payment> {

		public RecoverPaymentHandler(Payment payment) {
			super(payment);
		}

		@Override
		void addDrafItem(Payment payment) {
			fillPayment(payment);
			salaryDraftObject.addDraftPayment(payment);
		}

		void fillPayment(Payment draftPayment) {

			if (draftPayment.getType() != null)
				return;

			for (Payment payment : SalaryDraft.this.availablePaymens) {
				if (StringUtils.equals(payment.getName(), draftPayment.getName())) {
					draftPayment.setConceptId(payment.getId());
					draftPayment.setType(payment.getType());
					draftPayment.setIrpfExpression(payment.getIrpfExpression());
					draftPayment.setQuoteExpression(payment.getQuoteExpression());

					draftPayment.setScope(Scope.SALARY);
					draftPayment.setEndDate(salaryDraftObject.getEndDate());
					draftPayment.setStartDate(salaryDraftObject.getStartDate());
					draftPayment.setSalaryType(salaryDraftObject.getType());
				}
			}
		}

	}

	class RecoverDeductionHandler implements ClickHandler {

		private Deduction deduction;

		public RecoverDeductionHandler(Deduction deduction) {
			this.deduction = deduction;
		}

		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			salaryDraftObject.addDraftDeduction(deduction);
			salaryDraftObject.calculate(SalaryDraft.this);

		}
	}

	interface MyStyle extends CssResource {
		@ClassName("cell-label")
		String cellLabel();

		@ClassName("cell-warn")
		String cellWarn();

		@ClassName("cell-changed")
		String cellChanged();

		@ClassName("text-ok")
		String textOk();

		@ClassName("text-warn")
		String textWarn();

		@ClassName("text-error")
		String textError();

		@ClassName("value-changed")
		String valueChanged();

		@ClassName("context-tab-button-selected")
		String contextTabButtonSelected();

		@ClassName("section-even")
		String sectionEven();

		@ClassName("section-odd")
		String sectionOdd();

		@ClassName("db-section-even-er")
		String dbSectionEvenEr();

		@ClassName("db-section-odd-er")
		String dbSectionOddEr();

		@ClassName("db-section-even-ok")
		String dbSectionEvenOk();

		@ClassName("db-section-odd-ok")
		String dbSectionOddOk();
	}

	interface Binder extends UiBinder<Widget, SalaryDraft> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	ScrollPanel scrollPanel;
	@UiField
	VerticalPanel scrolledPanel;
	@UiField
	DeckPanel deckPanel;
	@UiField
	Panel draftPanel;
	@UiField
	Viewer pdfViewer;

	@UiField
	ListBox zoomListBox;
	@UiField
	SalarySelect salarySelect;
	@UiField
	FlexTable contextTable;
	@UiField
	SimplePanel contextTimeLinePanel;
	@UiField
	Button contextTableButton;
	@UiField
	Button contextTimeLineButton;
	@UiField
	DeckPanel contextDeckPanel;
	@UiField
	FlexTable eventsTable;
	@UiField
	Widget eventsTableSpace;

	@UiField
	FlexTable paymentsTable;

	@UiField
	Label enterpriseNameLabel;
	@UiField
	Label enterpriseAddressLabel;
	@UiField
	Label enterpriseCityLabel;
	@UiField
	Label enterpriseCCCLabel;

	@UiField
	Label employeeNameLabel;
	@UiField
	Label employeeSSLabel;
	@UiField
	Label employeeDocumentLabel;
	@UiField
	Label employeeSeniorityLabel;
	@UiField
	Label employeeAgreementCategoryLabel;

	@UiField
	Label periodLabel;
	@UiField
	Label daysLabel;

	@UiField
	ValueLabel remunerationLabel;
	@UiField
	Label dbRemunerationLabel;
	@UiField
	ValueTextBox cgcBaseLabel;
	@UiField
	Label dbCgcBaseLabel;
	@UiField
	ValueTextBox cgpBaseLabel;
	@UiField
	Label dbCgpBaseLabel;
	@UiField
	ValueLabel irpfBaseLabel;
	@UiField
	Label dbIrpfBaseLabel;
	@UiField
	ValueLabel hExtraBaseLabel;
	@UiField
	Label dbHExtraBaseLabel;
	@UiField
	ValueLabel nonHExtraBaseLabel;
	@UiField
	Label dbNonHExtraBaseLabel;
	@UiField
	ValueLabel prorationBaseLabel;
	@UiField
	Label dbProrationBaseLabel;

	@UiField
	ValueLabel totalPaymentLabel;
	@UiField
	Label dbTotalPaymentLabel;
	@UiField
	ValueTextBox totalPaymentsLabel;
	@UiField
	Label dbTotalPaymentsLabel;
	@UiField
	ValueTextBox totalLiquidLabel;
	@UiField
	Label dbTotalLiquidLabel;
	@UiField
	ValueLabel totalDeductionLabel;
	@UiField
	Label dbTotalDeductionLabel;

	@UiField
	Button acceptButton;
	@UiField
	Button salaryButton;

	@UiField
	Button fxButton;

	@UiField
	Button undoButton;
	@UiField
	Button redoButton;
	@UiField
	Button undoAllButton;


	@UiField
	CheckBox tgssCheck;
	@UiField
	CheckBox costsCheck;
	@UiField
	CheckBox eventsCheck;
	@UiField
	CheckBox dbSalaryCheck;

	@UiField
	Button closePreviewButton;
	@UiField
	Button irpfPreviewButton;
	@UiField
	Button printPreviewButton;
	

	@UiField
	MyStyle style;

	@UiField
	HorizontalPanel timeRulePanel;
	
	@UiField
	Button saveButton;

	@UiField
	ListBox settlePreviewListBox;
	@UiField
	ListBox salaryPreviewListBox;

	private int zoom;
	private Scope scope;
	private List<HasVisibility> dbUIObjects;
	private SalaryDraftObject salaryDraftObject;
	private Map<Event.Type, String[]> eventStyles;
	private List<Bonus> availableBonus = new ArrayList<Bonus>();
	private List<Payment> availablePaymens = new ArrayList<Payment>();
	private List<Deduction> availableDeductions = new ArrayList<Deduction>();

	private HasValue<String> fxhasValue;

	private Payment totalPayments = null;
	private Payment totalLiquidPayment = null;
	private Deduction cgcBaseDeduction = null;
	private Deduction cgpBaseDeduction = null;

	// managing the focus
	private NewPaymentHandler newPaymentHandler;
	private NewDeductionHandler newDeductionHandler;
	private NewBonusHandler newBonusHandler;
	private List<PaymentChangeHandler<?>> paymentChangeHandlers;
	private List<VariableChangeHandler<?>> variableChangeHandlers;
	
	private final ContentAsistManager contentAssistManager = new ContentAsistManager();

	private PopupPanel morePopup;
	private boolean autoSave = true;
	private MenuItem autoSaveMenuItem;
	
	private boolean dummies = false;
	private MenuItem dummiesMenuItem;


	public SalaryDraft() {
		initWidget(binder.createAndBindUi(this));
		initPaymentsTable();
		initPrintPreview();
		scope = Scope.CONTRACT;
		salarySelect.addListener(this);
		showDraft();
		showContextTable();

		zoom = Constants.DEFAULT_ZOOM;
		initEvents();
		initEventsStyles(style);
		initSalaryDb();
		export2JS(this);
	}

	public void setSalaryDraftObject(SalaryDraftObject salaryDraftObject) {
		showDraft();
		showContextTable();
		this.salaryDraftObject = salaryDraftObject;
		onChangedSalaryDraftObject(salaryDraftObject);
	}
	
	public SalaryDraftObject getSalaryDraftObject() {
		return salaryDraftObject;
	}

	@Override
	public void onChange(SalarySelect salarySelect) {
		salaryDraftObject.calculate(this);
	}

	@Override
	public void onChange(UndoManager undoManager) {
		redoButton.setEnabled(salaryDraftObject.canRedo());
		undoButton.setEnabled(salaryDraftObject.canUndo());

		acceptButton.setEnabled(salaryDraftObject.hasDrafts());
		undoAllButton.setEnabled(salaryDraftObject.hasDrafts());
	}
	// ------------------------------------------------------------------------
	@Override
	public Calculate getCalculate() {
		return dummies ? Calculate.DUMMIES : Calculate.STANDARD;
	}
	
	@Override
	public void onCalculateSucces(SalaryDraftObject salaryDraftObject) {
		boolean draftObjectChanged = this.salaryDraftObject != salaryDraftObject;
		if (draftObjectChanged)
			this.salaryDraftObject = salaryDraftObject;

		// I don't like it. But almost it's clear enough.
		if (isPreviewVisible()) {
			printPreview();
		}
		
		
		dumpSalaryDraft(!draftObjectChanged);

		salarySelect.setSalaryPreview(salaryDraftObject.asSalaryPreview());

		loadContentAssistManager();
		
		setAutomatic(  Arrays.asList(Type.EXTRA, Type.DELAY, Type.SETTLE).contains(salaryDraftObject.getType()));
		setReadOnly(  Arrays.asList(Type.EXTRA, Type.DELAY).contains(salaryDraftObject.getType()));
		
		showTimeRulePanel();
		showDbTimeRulePanel();
	}

	@Override
	public void onCalculateFailure(Throwable throwable) {
		// TODO Auto-generated method stub
		Window.alert(throwable.getMessage());
	}

	// ------------------------------------------------------------ @UIHandlers

//	@UiHandler("printButton")
//	void onPrintButtonClick(ClickEvent event) {
//		pdfViewer.print();
//	}

	@UiHandler("irpfPreviewButton")
	void onIrpfPreviewClick(ClickEvent event) {
		irpfPrint();
	}

	@UiHandler("totalLiquidLabel")
	void onLiquidChanges(ChangeEvent event) {

		Payment draftPayment = new Payment();

		String expression = totalLiquidLabel.getValue();

		// draftPayment.setName("NETO");
		draftPayment.setExpression("NETO(" + (StringUtils.isBlank(expression) ? "0.00" : expression) + ")");
		draftPayment.setScope(Scope.SALARY);
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setType(Payment.Type.DEFAULT);
		if (totalLiquidPayment != null) {
			draftPayment.setId(totalLiquidPayment.getId());
			//draftPayment.setDescription(totalLiquidPayment.getDescription());
			draftPayment.setDescriptionTemplate(totalLiquidPayment.getDescription());

		} else {
			//draftPayment.setDescription("Suplemento Neto");
			draftPayment.setDescriptionTemplate("Suplemento Neto");
		}

		draftPayment.setEndDate(salaryDraftObject.getEndDate());
		draftPayment.setStartDate(salaryDraftObject.getStartDate());
		draftPayment.setSalaryType(salaryDraftObject.getType());
		// draftPayment.setMonth(deduction.getMonth());

		salaryDraftObject.addDraftPayment(draftPayment);

		salaryDraftObject.calculate(this);

		totalLiquidPayment = draftPayment;

	}

	@UiHandler("totalLiquidLabel")
	void onLiquidBlur(BlurEvent event) {
		try {
			String value = totalLiquidLabel.getValue();
			totalLiquidLabel.setText(format(StringUtils.isBlank(value) ? 0 : Double.valueOf(value)));
		} catch (Exception e) {
			totalLiquidLabel.setText(format(salaryDraftObject.getTotalLiquid()));
		}
	}

	@UiHandler("totalLiquidLabel")
	void onLiquidFocus(FocusEvent event) {
		Double liquid = salaryDraftObject.getTotalLiquid();
		totalLiquidLabel.setText(String.valueOf(NumberUtils.isNotValid(liquid) ? 0.00 : AON.round(liquid)));
	}

	@UiHandler("cgcBaseLabel")
	void onCgcBaseChange(ChangeEvent event) {
		String expression = cgcBaseLabel.getValue();

		if (cgcBaseDeduction == null) {
			cgcBaseDeduction = new Deduction();
			cgcBaseDeduction.setScope(Scope.SALARY);
			//cgcBaseDeduction.setDescription("BASE_CGC");
			cgcBaseDeduction.setDescriptionTemplate("BASE_CGC");
			cgcBaseDeduction.setType(Deduction.Type.OTHER);
			cgcBaseDeduction.setSalaryType(salaryDraftObject.getType());
		}

		// @formatter:off
		cgcBaseDeduction
				.setExpression("if ( " + salaryDraftObject.getType().getVariable() + " ) {" + " BASE_CGC = /*user*/ "
						+ expression + "/**/; " + " BUILDER.setCgcBase(BASE_CGC);" + "}" + " REMOVE();");
		// @formatter:on

		salaryDraftObject.addDraftDeduction(cgcBaseDeduction);

		salaryDraftObject.calculate(this);

	}

	@UiHandler("cgcBaseLabel")
	void onCgcBaseBlur(BlurEvent event) {
		cgcBaseLabel.setText(format(salaryDraftObject.getCgcBase()));
	}

	@UiHandler("cgcBaseLabel")
	void onCgcBaseFocus(FocusEvent event) {
		if (cgcBaseDeduction != null) {
			cgcBaseLabel.setText(cgcBaseDeduction.getExpression());
		} else {
			Double cgcBase = salaryDraftObject.getCgcBase();
			cgcBaseLabel.setText(String.valueOf(NumberUtils.isNotValid(cgcBase) ? 0.00 : AON.round(cgcBase)));
		}
	}

	@UiHandler("cgpBaseLabel")
	void onCgpBaseChange(ChangeEvent event) {
		String expression = cgpBaseLabel.getValue();

		if (cgpBaseDeduction == null) {
			cgpBaseDeduction = new Deduction();
			cgpBaseDeduction.setScope(Scope.SALARY);
			//cgpBaseDeduction.setDescription("BASE_CGP");
			cgpBaseDeduction.setType(Deduction.Type.OTHER);
			cgpBaseDeduction.setDescriptionTemplate("BASE_CGP");
			cgpBaseDeduction.setSalaryType(salaryDraftObject.getType());
		}
		// @formatter:off
		cgpBaseDeduction
				.setExpression("if ( " + salaryDraftObject.getType().getVariable() + " ) {" + " BASE_CGP = /*user*/ "
						+ expression + "/**/; " + " BUILDER.setCgpBase(BASE_CGP); " + "}" + " REMOVE();");
		// @formatter:on

		salaryDraftObject.addDraftDeduction(cgpBaseDeduction);

		salaryDraftObject.calculate(this);

	}

	@UiHandler("cgpBaseLabel")
	void onCgpBaseBlur(BlurEvent event) {
		cgpBaseLabel.setText(format(salaryDraftObject.getCgpBase()));
	}

	@UiHandler("cgpBaseLabel")
	void onCgpBaseFocus(FocusEvent event) {
		if (cgpBaseDeduction != null) {
			cgpBaseLabel.setText(cgpBaseDeduction.getExpression());
		} else {
			Double cgpBase = salaryDraftObject.getCgpBase();
			cgpBaseLabel.setText(String.valueOf(NumberUtils.isNotValid(cgpBase) ? 0.00 : AON.round(cgpBase)));
		}
	}

	@UiHandler("totalPaymentsLabel")
	void onPaymentsChanges(ChangeEvent event) {

		Payment draftPayment = new Payment();

		String expression = totalPaymentsLabel.getValue();

		draftPayment.setExpression("BRUTO(" + (StringUtils.isBlank(expression) ? "0.00" : expression) + ")");
		// draftPayment.setName("BRUTO");
		draftPayment.setScope(Scope.SALARY);
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setType(Payment.Type.DEFAULT);
		if (totalPayments != null) {
			draftPayment.setId(totalPayments.getId());
			//draftPayment.setDescription(totalPayments.getDescription());
			draftPayment.setDescriptionTemplate(totalPayments.getDescription());

		} else {
			//draftPayment.setDescription("Suplemento Bruto");
			draftPayment.setDescriptionTemplate("Suplemento Bruto");
		}

		draftPayment.setEndDate(salaryDraftObject.getEndDate());
		draftPayment.setStartDate(salaryDraftObject.getStartDate());
		draftPayment.setSalaryType(salaryDraftObject.getType());
		// draftPayment.setMonth(deduction.getMonth());

		salaryDraftObject.addDraftPayment(draftPayment);

		salaryDraftObject.calculate(this);

		totalPayments = draftPayment;

	}

	@UiHandler("totalPaymentsLabel")
	void onPaymentsBlur(BlurEvent event) {
		try {
			String value = totalPaymentsLabel.getValue();
			totalPaymentsLabel.setText(format(StringUtils.isBlank(value) ? 0 : Double.valueOf(value)));
		} catch (Exception e) {
			totalPaymentsLabel.setText(format(salaryDraftObject.getTotalPayment()));
		}
	}

	@UiHandler("totalPaymentsLabel")
	void onPaymentsFocus(FocusEvent event) {
		Double totalPayment = salaryDraftObject.getTotalPayment();
		totalPaymentsLabel
				.setText(String.valueOf(NumberUtils.isNotValid(totalPayment) ? 0.00 : AON.round(totalPayment)));
	}
	
	@UiHandler("settlePreviewListBox")
	void onSettlePreviewChange(ChangeEvent event) {
		printSettle();
	}

	@UiHandler("salaryPreviewListBox")
	void onSalaryPreviewChange(ChangeEvent event) {
		printSalary();
	}

	private void setDbVisible(boolean visible) {
		dbCgcBaseLabel.setVisible(visible);
		dbCgpBaseLabel.setVisible(visible);
		dbHExtraBaseLabel.setVisible(visible);
		dbIrpfBaseLabel.setVisible(visible);
		dbNonHExtraBaseLabel.setVisible(visible);
		dbProrationBaseLabel.setVisible(visible);
		dbRemunerationLabel.setVisible(visible);
		dbTotalDeductionLabel.setVisible(visible);
		dbTotalLiquidLabel.setVisible(visible);
		dbTotalPaymentLabel.setVisible(visible);
		dbTotalPaymentsLabel.setVisible(visible);

		for (HasVisibility obj : dbUIObjects)
			obj.setVisible(visible);
		
		showDbTimeRulePanel();
	}

	private void showDraft() {
		showWidget(draftPanel);

		saveButton.setVisible(false);
		zoomListBox.setVisible(false);
		closePreviewButton.setVisible(false);
		settlePreviewListBox.setVisible(false);
		salaryPreviewListBox.setVisible(false);
		
		fxButton.setVisible(true);
		undoButton.setVisible(true);
		redoButton.setVisible(true);
		undoAllButton.setVisible(true);
		costsCheck.setVisible(true);
		salarySelect.setVisible(true);
		acceptButton.setVisible(true);
		salaryButton.setVisible(true);
		irpfPreviewButton.setVisible(true);
		printPreviewButton.setVisible(true);
		tgssCheck.setVisible(isSalary());
		dbSalaryCheck.setVisible(hasDbSalary());
		eventsCheck.setVisible(hasEvents());
	}


	private void showPreview() {
		showWidget(pdfViewer);

		saveButton.setVisible(true);
		zoomListBox.setVisible(true);
		closePreviewButton.setVisible(true);
		settlePreviewListBox.setVisible(isSettle());
		salaryPreviewListBox.setVisible(isSalary() || isExtra());


		fxButton.setVisible(false);
		costsCheck.setVisible(false);
		salarySelect.setVisible(false);
		tgssCheck.setVisible(false);
		eventsCheck.setVisible(false);
		dbSalaryCheck.setVisible(false);
		irpfPreviewButton.setVisible(false);
		printPreviewButton.setVisible(false);
	}

	private void showIrpfPreview() {
		showWidget(pdfViewer);

		saveButton.setVisible(true);
		zoomListBox.setVisible(true);
		closePreviewButton.setVisible(true);
		
		fxButton.setVisible(false);
		undoButton.setVisible(false);
		redoButton.setVisible(false);
		eventsCheck.setVisible(false);
		undoAllButton.setVisible(false);
		costsCheck.setVisible(false);
		salarySelect.setVisible(false);
		acceptButton.setVisible(false);
		salaryButton.setVisible(false);
		tgssCheck.setVisible(false);
		dbSalaryCheck.setVisible(false);
		irpfPreviewButton.setVisible(false);
		printPreviewButton.setVisible(false);
		settlePreviewListBox.setVisible(false);
		salaryPreviewListBox.setVisible(false);

	}

	boolean isPreviewVisible() {
		return isWidgetVisible(pdfViewer);
	}

	private void showWidget(Widget widget) {
		deckPanel.showWidget(deckPanel.getWidgetIndex(widget));
	}

	private boolean isWidgetVisible(Widget w) {
		int index = deckPanel.getVisibleWidget();
		Widget visibleWidget = deckPanel.getWidget(index);
		return visibleWidget == w;
	}

	private void showContextTable() {
		contextDeckPanel.showWidget(contextDeckPanel.getWidgetIndex(contextTable));
		contextTableButton.addStyleName(style.contextTabButtonSelected());
		contextTimeLineButton.removeStyleName(style.contextTabButtonSelected());
	}

	protected boolean isSettle() {
		return salaryDraftObject.getType() == Type.SETTLE;
	}

	private void showContextTimeLine() {

		// Date startDate = getFirstDayOfYear(salaryDraftObject.getStartDate());
		// Date endDate = getLastDayOfYear(salaryDraftObject.getEndDate());

		final Date startDate = addMonths2Date(getFirstDayOfMonth(salaryDraftObject.getStartDate()), -1);
		final Date endDate = addMonths2Date(getLastDayOfMonth(salaryDraftObject.getEndDate()), 1);

		final TreeSet<String> names = new TreeSet<String>();
		for (Variable v : salaryDraftObject.getContext())
			names.add(v.getName());
		for (Variable v : salaryDraftObject.getDrafContext())
			names.add(v.getName());

		salaryDraftObject.getVariables(names.toArray(new String[names.size()]), startDate, endDate,
				new AsyncCallback<List<Variable>>() {

					private int clientX = -1;
					private int clientY = -1;
					private Tooltip tooltip = new Tooltip(){
						Tooltip setup(){
							
							titlePanel.setVisible(false);
							return this;
						}
					}.setup(); 

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(final List<Variable> variables) {

						final int offsetWidth = contextTable.getOffsetWidth();

						contextDeckPanel.showWidget(contextDeckPanel.getWidgetIndex(contextTimeLinePanel));
						contextTableButton.removeStyleName(style.contextTabButtonSelected());
						contextTimeLineButton.addStyleName(style.contextTabButtonSelected());
						contextTimeLinePanel.setWidth(offsetWidth + "px");

						VisualizationUtils.loadVisualizationApi(new Runnable() {
							@Override
							public void run() {

								Options options = Options.create();
								options.setWidth(offsetWidth);

								Timeline timeline = Timeline.create();

								RowLabelStyle rowStyle = RowLabelStyle.create();
								rowStyle.setFontSize("12");
								rowStyle.setTextAlign("left");
								timeline.setRowLabelStyle(rowStyle);

								options.setTimeline(timeline);

								DataTable data = DataTable.create();
								data.addColumn(ColumnType.STRING, "Variable");
								data.addColumn(ColumnType.STRING, "Value");
								data.addColumn(ColumnType.DATE, "Start");
								data.addColumn(ColumnType.DATE, "End");

								// for (Variable variable : salaryDraftObject
								// .getContext()) {
								// if (skipVariable(variable.getName()))
								// continue;
								// if (variable instanceof UndefinedVariable)
								// variables.add(variable);
								// }

								Collections.sort(variables, new Comparator<Variable>() {
									@Override
									public int compare(Variable v1, Variable v2) {
										return v1.getName().compareTo(v2.getName());
									}
								});

								for (Variable variable : variables) {
									int row = data.addRow();

									data.setValue(row, 0, variable.getName());

									data.setValue(row, 2,
											variable.getStartDate() != null ? variable.getStartDate() : startDate);
									data.setValue(row, 3,
											variable.getEndDate() != null ? variable.getEndDate() : endDate);

									Object value = variable.getValue();
									if (value == null)
										data.setValue(row, 1, "Sin definir");
									else
										data.setValue(row, 1, getValueAsString(variable));

								}

								final TimeLineChart timeLineChart = new TimeLineChart(data, options);
								contextTimeLinePanel.setWidget(timeLineChart);
								timeLineChart.addOnMouseOverHandler(new OnMouseOverHandler() {
									@Override
									public void onMouseOverEvent(OnMouseOverEvent event) {
										Variable variable = variables.get(event.getRow());
										tooltip.setName(variable.getName());
										tooltip.setStartDate(variable.getStartDate());
										tooltip.setEndDate(variable.getEndDate());

										IsWidget isWidget = newVariableEditor(variable);
										tooltip.setValueEditor(isWidget.asWidget());

										tooltip.showToolTip(clientX, clientY);

									}
								});
								timeLineChart.addMouseMoveHandler(new MouseMoveHandler() {

									@Override
									public void onMouseMove(MouseMoveEvent event) {
										clientX = event.getClientX();
										clientY = event.getClientY();
									}
								});
							}
						}, TimeLineChart.PACKAGE);
					}

					private String getValueAsString(Variable v) {
						Object value = v.getValue();
						if ("true".equalsIgnoreCase(value.toString()))
							return "SI";
						else if ("false".equalsIgnoreCase(value.toString()))
							return "NO";
						else if (v.getName().equalsIgnoreCase("TC2"))
							return Employee.TC2.getDescriptionByCode(value.toString());
						else if (v.getName().equalsIgnoreCase("OCUPACION"))
							return Employee.Occupation.valueOf(value.toString()).getDescription();
						else if (v.getName().startsWith("PORCENTAJE"))
							try {
								return formatPercent(Double.parseDouble(value.toString()));
							} catch (NumberFormatException e) {
								return v.getValue().toString();
							}
						else if (v.getName().startsWith("COEFICIENTE"))
							try {
								return formatPercent(Double.parseDouble(value.toString()) * 100);
							} catch (NumberFormatException e) {
								return v.getValue().toString();
							}
						else {
							try {
								return format(Double.parseDouble(value.toString()));
							} catch (NumberFormatException e) {
								return v.getValue().toString();
							}
						}
					}

					private <T extends IsWidget & HasValue<String> & HasAllFocusHandlers & Focusable> T newVariableEditor(
							Variable variable) {
						T editor = createEditor(variable);
						int width = editor instanceof ListBox ? size2px(25) + 6 : size2px(25);
						editor.asWidget().getElement().getStyle().setWidth(width, Unit.PX);
						editor.setValue(getValueAsString(variable));

						VariableChangeHandler<T> variableChangeHandler = new VariableChangeHandler<T>(variable);

						variableChangeHandler.setEditor(editor);

						return editor;
					}

				});

	}

	private void onChangedSalaryDraftObject(SalaryDraftObject salaryDraftObject) {
		
		salaryDraftObject.calculate(this);

		syncSalarySelect();

		// clean...???
		cgcBaseDeduction = null;
		cgpBaseDeduction = null;

		// Sync undo & redo controls
		salaryDraftObject.addUndoManagerListener(this);
		redoButton.setEnabled(salaryDraftObject.canRedo());
		undoButton.setEnabled(salaryDraftObject.canUndo());
		acceptButton.setEnabled(salaryDraftObject.hasDrafts());
		undoAllButton.setEnabled(salaryDraftObject.hasDrafts());

	}

	private void dumpSalaryDraft(boolean displayChanges) {

		enterpriseNameLabel.setText(salaryDraftObject.getEnterpriseName());
		enterpriseCCCLabel.setText(salaryDraftObject.getEnterpriseCCC());
		enterpriseAddressLabel.setText(salaryDraftObject.getEnterpriseAddress());

		employeeSSLabel.setText(salaryDraftObject.getEmployeeSS());
		employeeNameLabel.setText(salaryDraftObject.getEmployeeName());
		employeeDocumentLabel.setText(salaryDraftObject.getEmployeeDocument());
		employeeSeniorityLabel.setText(format(salaryDraftObject.getEmployeeSeniorityDate()));
		employeeAgreementCategoryLabel.setText(salaryDraftObject.getEmployeeAgreementCategory());

		Date startDate = salaryDraftObject.getStartDate();
		Date endDate = salaryDraftObject.getEndDate();
		periodLabel.setText(format(startDate) + " - " + format(endDate));
		daysLabel.setText(Integer.toString(salaryDraftObject.getTimeUnits()));

		totalPaymentsLabel.setText(format(salaryDraftObject.getTotalPayment()), displayChanges);
		dbTotalPaymentsLabel.setText(format(salaryDraftObject.getDbTotalPayment()));
		setDbStyleName(dbTotalPaymentsLabel, totalPaymentsLabel);

		Double cgcBase = salaryDraftObject.getCgcBase();
		cgcBaseLabel.setText(format(cgcBase), displayChanges);
		dbCgcBaseLabel.setText(format(salaryDraftObject.getDbCgcBase()));
		setDbStyleName(dbCgcBaseLabel, cgcBaseLabel);

		Double cgpBase = salaryDraftObject.getCgpBase();
		cgpBaseLabel.setText(format(cgpBase), displayChanges);
		dbCgpBaseLabel.setText(format(cgpBase));
		setDbStyleName(dbCgpBaseLabel, cgpBaseLabel);

		irpfBaseLabel.setText(format(salaryDraftObject.getIrpfBase()), displayChanges);
		dbIrpfBaseLabel.setText(format(salaryDraftObject.getDbIrpfBase()));
		setDbStyleName(dbIrpfBaseLabel, irpfBaseLabel);
		hExtraBaseLabel.setText(format(salaryDraftObject.gethExtraBase()), displayChanges);
		dbHExtraBaseLabel.setText(format(salaryDraftObject.getDbHExtraBase()));
		setDbStyleName(dbHExtraBaseLabel, hExtraBaseLabel);
		nonHExtraBaseLabel.setText(format(salaryDraftObject.getNonHExtraBase()), displayChanges);
		dbNonHExtraBaseLabel.setText(format(salaryDraftObject.getDbNonHExtraBase()));
		setDbStyleName(dbNonHExtraBaseLabel, nonHExtraBaseLabel);
		prorationBaseLabel.setText(format(salaryDraftObject.getProrationBase()), displayChanges);
		dbProrationBaseLabel.setText(format(salaryDraftObject.getDbProrationBase()));
		setDbStyleName(dbProrationBaseLabel, prorationBaseLabel);

		remunerationLabel.setText(format(salaryDraftObject.getRemuneration()), displayChanges);
		dbRemunerationLabel.setText(format(salaryDraftObject.getDbRemuneration()));
		setDbStyleName(dbRemunerationLabel, remunerationLabel);

		totalPaymentLabel.setText(format(salaryDraftObject.getTotalPayment()), displayChanges);
		dbTotalPaymentLabel.setText(format(salaryDraftObject.getDbTotalPayment()));
		setDbStyleName(dbTotalPaymentLabel, totalPaymentLabel);

		totalDeductionLabel.setText(format(salaryDraftObject.getTotalDeduction()), displayChanges);
		dbTotalDeductionLabel.setText(format(salaryDraftObject.getDbTotalDeduction()));
		setDbStyleName(dbTotalDeductionLabel, totalDeductionLabel);

		totalLiquidLabel.setText(format(salaryDraftObject.getTotalLiquid()), displayChanges);
		dbTotalLiquidLabel.setText(format(salaryDraftObject.getDbTotalLiquid()));
		setDbStyleName(dbTotalLiquidLabel, totalLiquidLabel);

		clearDbWidgets();
		clearEventsTable();
		clearContextTable();
		clearPaymentsTable();
		
		initAvailablePayments();
		initAvailableDeductions();
		initAvailableBonus();
		List<Payment> payments = salaryDraftObject.getPayments();
		paymentChangeHandlers = dumpPayments(payments);
		newPaymentHandler = insertNewPaymentRow();
		insertBlankPaymentRow();

		List<Deduction> deductions = salaryDraftObject.getDeductions();
		dumpDeductions(deductions);
		List<Deduction> embargos = salaryDraftObject.getEmbargos();
		dumpDeductions(embargos);
		newDeductionHandler = insertNewDeductionRow();
		insertBlankPaymentRow();
		insertBlankPaymentRow();
		
		Scope nextScope = null;
		boolean show = false; //scope.compareTo(Scope.CONTRACT) >= 0;

		variableChangeHandlers = new ArrayList<VariableChangeHandler<?>>();

		List<Variable> context = getContext(salaryDraftObject);
		List<Variable> variables = context.stream()
				.filter(v->!skipVariable(v))
				.collect(Collectors.toList());
		List<Variable> constants = getConstants(context);
		
		List<Variable> visibleContext  = new ArrayList<Variable>();
		visibleContext.addAll(constants);
		visibleContext.addAll(variables);
		
		//dumpContext(constants, Scope.CONTRACT, true, null);

		for (Scope step : SCOPE_STEPS) {
			nextScope = dumpContext(visibleContext, step, show, nextScope);
			if (step.compareTo(scope) <= 0)
				break;
		}
		
		initTgssCheck();
		initDbSalaryCheck();		

		setDbVisible(salaryDraftObject.hasDbSalary() && dbSalaryCheck.getValue());
		acceptButton.setEnabled(salaryDraftObject.hasDrafts());
		undoButton.setEnabled(salaryDraftObject.canUndo());
		redoButton.setEnabled(salaryDraftObject.canRedo());
		if (costsCheck.getValue())
			showCosts(true);
		
		initEventsCheck();
		dumpEvents(salaryDraftObject.getEvents());
		// Events visible
		eventsTable.setVisible(eventsCheck.isVisible() && eventsCheck.getValue());
		eventsTableSpace.setVisible(eventsTable.isVisible()/*eventsTable.getRowCount() > 0*/);
		showPaymentsEvents(eventsTable.isVisible());
	}

	

	private void initTgssCheck(){
		tgssCheck.setVisible(isSalary());
	}


	private void initEventsCheck(){
		eventsCheck.setVisible(hasEvents());
	}

	private void initDbSalaryCheck(){
		dbSalaryCheck.setVisible(hasDbSalary());
		Widget dbDiffWidget = getDiffsWithDbSalary();
		boolean hasDiffsWithDbSalary = dbDiffWidget != null;
		//dbSalaryCheck.setValue(hasDiffsWithDbSalary, false);
		dbSalaryCheck.addStyleName(hasDiffsWithDbSalary ? style.textError() : style.textOk());
		dbSalaryCheck.removeStyleName(hasDiffsWithDbSalary ? style.textOk() : style.textError());
		if ( hasDiffsWithDbSalary )
			scrollPanel.ensureVisible(dbDiffWidget);
		
	}

	private void initAvailablePayments() {
		availablePaymens.clear();
		salaryDraftObject.getPaymentConcepts(new AsyncCallback<List<Payment>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<Payment> result) {
				availablePaymens.addAll(result);
				SalaryDraft.this.newPaymentHandler.initSuggestionItems();
			}
		});
	}

	private void initAvailableDeductions() {
		availableDeductions.clear();
		salaryDraftObject.getDeductionConcepts(new AsyncCallback<List<Deduction>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<Deduction> result) {
				availableDeductions.addAll(result);
				SalaryDraft.this.newDeductionHandler.initSuggestionItems();
			}
		});
	}

	private void initAvailableBonus() {
		availableBonus.clear();
		salaryDraftObject.getBonusConcepts(new AsyncCallback<List<Bonus>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<Bonus> result) {
				availableBonus.addAll(result);
				if (SalaryDraft.this.newBonusHandler != null)
					SalaryDraft.this.newBonusHandler.initSuggestionItems();
			}
		});
	}

	private <T extends IsWidget & HasValue<String> & HasAllFocusHandlers & Focusable> VariableChangeHandler<T> createVariableChangeHandler(
			Variable variable) {
		switch (variable.getName()) {
		case "DIAS_VACACIONES_NO_DISFRUTADOS":
			return new NoHolidaysChangeHanlder<T>(variable);
		default:
			return new VariableChangeHandler<T>(variable);
		}
	}

	@UiHandler("fxButton")
	void onFxHelperMouseDown(MouseDownEvent event) {
		final FxDialog fxDialog = new FxDialog(salaryDraftObject);
		fxDialog.setExpression(fxhasValue.getValue());
		fxDialog.center();
		fxDialog.show();

		fxDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				((Focusable) fxhasValue).setFocus(true);
				if (fxDialog.isAccepted()) {
					fxhasValue.setValue(fxDialog.getExpression());
					for (Variable var : fxDialog.getVariables()) {
						var.setScope(Scope.SALARY);
						var.setImplicit(false);
						var.setStartDate(SalaryDraft.this.salaryDraftObject.getStartDate());
						var.setEndDate(SalaryDraft.this.salaryDraftObject.getEndDate());
						SalaryDraft.this.salaryDraftObject.addDraftVariable(var);
					}
				}
			}
		});
	}


	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		salaryDraftObject.save(this);
	}

	@UiHandler("salaryButton")
	void onSalaryButtonClick(ClickEvent event) {
		if (autoSave)
			salaryDraftObject.saveITData(new CalculateCallback() {
				@Override
				public Calculate getCalculate() {
					return SalaryDraft.this.getCalculate();
				}

				@Override
				public void onCalculateSucces(SalaryDraftObject object) {
					
					salaryDraftObject.save(new CalculateCallback() {

						@Override
						public Calculate getCalculate() {
							return SalaryDraft.this.getCalculate();
						}
						@Override
						public void onCalculateSucces(SalaryDraftObject object) {
							SalaryDraft.this.onCalculateSucces(object); // TODO:
																		// It's
																		// necessary
																		// ?
							SalaryDraft.this.salaryDraftObject.emitSalary(SalaryDraft.this);
						}

						@Override
						public void onCalculateFailure(Throwable throwable) {
							SalaryDraft.this.onCalculateFailure(throwable);
						}
					});
				}

				@Override
				public void onCalculateFailure(Throwable throwable) {
					SalaryDraft.this.onCalculateFailure(throwable);
				}
			});
		else
			salaryDraftObject.emitSalary(this);
	}

	@UiHandler("contextTableButton")
	void onContextTableButtonClick(ClickEvent event) {
		showContextTable();
	}

	@UiHandler("contextTimeLineButton")
	void onContextTimeLineButtonClick(ClickEvent event) {
		showContextTimeLine();
	}
	@UiHandler("tgssCheck")
	void onTgssCheckChanged(ValueChangeEvent<Boolean> event) {
		showTimeRulePanel();
		showDbTimeRulePanel();
	}

	private void syncSalarySelect() {
		salaryDraftObject.getExtras(new AsyncCallback<List<Extra>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<Extra> extras) {
				salarySelect.setExtras(extras);
			}
		});
	}

	private void initSalaryDb() {
		dbSalaryCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setDbVisible(event.getValue());
			}
		});
		dbUIObjects = new LinkedList<HasVisibility>();
	}

	private void initEvents() {
		eventsCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				eventsTable.setVisible(event.getValue());
				eventsTableSpace.setVisible(eventsTable.isVisible());
				showPaymentsEvents(eventsTable.isVisible());
			}
		});
	}

	private void initPaymentsTable() {

		paymentsTable.setText(0, 0, "CUANTIA");
		paymentsTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		paymentsTable.setText(0, 1, "CONCEPTO");
		paymentsTable.setText(0, 2, "DEVENGOS");
		paymentsTable.setText(0, 3, "DEDUCCIONES");
		paymentsTable.getFlexCellFormatter().setColSpan(0, 3, 2);

		paymentsTable.getRowFormatter().addStyleName(0, AON.AON_DATA_TABLE_ROW_ODD);
		for (int i = 0; i < paymentsTable.getCellCount(0); i++) {
			paymentsTable.getCellFormatter().addStyleName(0, i, AON.AON_BOLD);
			paymentsTable.getCellFormatter().addStyleName(0, i, AON.AON_TEXT_CENTER);
		}

		paymentsTable.getColumnFormatter().setWidth(0, "2%");
		paymentsTable.getColumnFormatter().setWidth(1, "12%"); // CUANTIA
		// 2 ...
		paymentsTable.getColumnFormatter().setWidth(3, "18%"); // DEVENGO
		paymentsTable.getColumnFormatter().setWidth(4, "12%"); // DEDUCCION
		paymentsTable.getColumnFormatter().setWidth(5, "2%");

	}

	private void initEventsStyles(MyStyle myStyle) {
		eventStyles = new HashMap<Event.Type, String[]>();
		eventStyles.put(Event.Type.INFO, new String[] { "", "" });
		eventStyles.put(Event.Type.DEBUG, new String[] { "", "" });
		eventStyles.put(Event.Type.ERROR, new String[] { "aon-icon-exception", myStyle.textError() });
		eventStyles.put(Event.Type.WARNING, new String[] { AON.AON_ICON_WARN, myStyle.textWarn() });
	}

	// -------------------------------------------------------------------------
	@UiHandler("undoButton")
	void onUndoButtonClick(ClickEvent event) {
		salaryDraftObject.undo();
		salaryDraftObject.calculate(SalaryDraft.this);
	}

	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		salaryDraftObject.clearDrafts();
		salaryDraftObject.calculate(SalaryDraft.this);
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		salaryDraftObject.redo();
		salaryDraftObject.calculate(SalaryDraft.this);
	}

	@UiHandler("costsCheck")
	void onCostsCheckChange(ValueChangeEvent<Boolean> event) {
		showCosts();
	}
	
	@UiHandler("saveButton")
	void onDownloadClick(ClickEvent event) {
		String fileName = 
				salaryDraftObject.getEmployeeName() + " " 
				+ DateTimeFormat.getFormat(PredefinedFormat.MONTH).format(salaryDraftObject.getChargeDate())
				+".pdf";
		pdfViewer.download(fileName);
	}

	// -------------------------------------------------------------------------

	private void initPrintPreview() {
		printPreviewButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				printPreview();
			}
		});

		closePreviewButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showDraft();
			}
		});

		for (int zoom = Constants.MIN_ZOOM; zoom < Constants.DEFAULT_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		int selectedIndex = zoomListBox.getItemCount();
		for (int zoom = Constants.DEFAULT_ZOOM; zoom < Constants.MAX_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) Constants.MAX_ZOOM / 100));
		zoomListBox.setSelectedIndex(selectedIndex);
		zoomListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = SalaryDraft.this.zoomListBox.getSelectedIndex();
				String text = SalaryDraft.this.zoomListBox.getItemText(index);
				SalaryDraft.this.zoom = (int) (Constants.PERCENT_FORMAT.parse(text));
				pdfViewer.scale(zoom / 100.00);
			}
		});
		
	}

	private void clearDbWidgets() {
		dbUIObjects.clear();
	}

	private void clearEventsTable() {
		eventsTable.removeAllRows();
	}

	private void clearContextTable() {
		contextTable.removeAllRows();
	}

	private void clearPaymentsTable() {
		for (int i = paymentsTable.getRowCount() - 1; i > 0; i--)
			paymentsTable.removeRow(i);
	}

	private void addDbWidget(HasVisibility widget) {
		dbUIObjects.add(widget);
	}

	private void dumpEvents(List<Event> events) {

		int row = eventsTable.getRowCount();
		for (Event event : events) {
			dumpEvent(row++, event);
		}

	}

	private void dumpEvent(int row, Event event) {
		Button headButton = new Button();
		
		headButton.setStyleName(( event.getType() == Event.Type.INFO 
				|| ( event.getMessage() != null && event.getMessage().contains("Solutions") ) ) 
				? AON.AON_ICON_INFO: AON.AON_ICON_EXCEPTION);
		headButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);

		eventsTable.setWidget(row, 0, headButton);

		eventsTable.setHTML(row, 1, event.getMessage());
		eventsTable.getCellFormatter().getElement(row, 1).getStyle().setWhiteSpace(WhiteSpace.NORMAL);
		eventsTable.getCellFormatter().getElement(row, 1).getStyle().setProperty("maxWidth", 55, Unit.EM);

		String styles[] = eventStyles.get(event.getType());

		StyleToggleButton itemButton = null;
		if (event instanceof HasPayment)
			itemButton = getPaymentButton((HasPayment) event, styles[0], styles[1]);
		else if (event instanceof HasDeduction)
			itemButton = getDeductionButton((HasDeduction) event, styles[0], styles[1]);
		else if (event instanceof HasBonus)
			itemButton = getBonusButton((HasBonus) event, styles[0], styles[1]);

		if (itemButton != null) {
			eventsTable.setWidget(row, 2, itemButton);
			itemButton.setValue(true, true); // down
		} else {
			eventsTable.setHTML(row, 2, "&nbsp;");
		}

		eventsTable.getCellFormatter().getElement(row, 0).getStyle().setPropertyPx("borderRightWidth", 0);
		eventsTable.getCellFormatter().getElement(row, 1).getStyle().setPropertyPx("borderLeftWidth", 0);
		eventsTable.getCellFormatter().getElement(row, 1).getStyle().setPropertyPx("borderRightWidth", 0);
		eventsTable.getCellFormatter().getElement(row, 2).getStyle().setPropertyPx("borderLeftWidth", 0);

		for (int col = 0; col < eventsTable.getCellCount(row); col++)
			eventsTable.getCellFormatter().addStyleName(row, col, "aon-panelGrid-odd");
	}

	private List<PaymentChangeHandler<?>> dumpPayments(List<Payment> payments) {
		
		List<PaymentChangeHandler<?>> handlers = new ArrayList<PaymentChangeHandler<?>>(payments.size());

		for (Payment payment : payments) {

			int row = paymentsTable.getRowCount();

			// if (!displayNow(payment))
			// continue;
			Event event = getEvent4(payment);
			
			if (payment.getAmount() != null && event == null) {

				PaymentChangeHandler<TextBox> handler = new PaymentChangeHandler<TextBox>(payment);
				dumpPayment(payment, row, getIconRowStyle(payment), handler);

				handlers.add(handler);

			} else if (payment.getId() != null ) {
				PaymentChangeHandler<TextBox> handler = new PaymentChangeHandler<TextBox>(payment);
				String styles[] = eventStyles.get(event == null ? Event.Type.WARNING : event.getType());
				//dumpPayment(payment, row, styles[0], handler);
				dumpPayment(payment, row, getIconRowStyle(payment), handler);
				handlers.add(handler);
				//addStyle(paymentsTable, row, styles[1]);

			} else {
				String styles[] = eventStyles.get(event == null ? Event.Type.ERROR : event.getType());
				//dumpDbItem(payment, row, styles[0], styles[1],
				//		new RecoverPaymentHandler(payment), false);
				dumpDbItem(payment, row,getIconRowStyle(payment), "none",
						new RecoverPaymentHandler(payment), false);
			}
		}

		return handlers;

	}

	private void showPaymentsEvents(boolean show) {
		
		int row = 0;

		for (PaymentChangeHandler<?> handler : paymentChangeHandlers) {
			
			row++;
			
			Payment payment = handler.item;
			Event event = getEvent4(payment);
			
			if (payment.getAmount() != null && event == null) {

			} else if (payment.getId() != null ) {
				String styles[] = eventStyles.get(event == null ? Event.Type.WARNING : event.getType());
				addStyle(paymentsTable, row, styles[1], show);
				handler.editButton.setStyleName(styles[0], show);
				handler.editButton.setStyleName(getIconRowStyle(payment), !show);

			} else {
				String styles[] = eventStyles.get(event == null ? Event.Type.ERROR : event.getType());
				addStyle(paymentsTable, row, styles[1], show);
				handler.editButton.setStyleName(styles[0], show);
				handler.editButton.setStyleName(getIconRowStyle(payment), !show);
			}
		}


	}

	private void formatRow(int row) {
		paymentsTable.getCellFormatter().getElement(row, 0).getStyle().setPropertyPx("borderRightWidth", 0);
		paymentsTable.getCellFormatter().getElement(row, 1).getStyle().setPropertyPx("borderLeftWidth", 0);
		if (paymentsTable.getCellCount(row) > 5) {
			paymentsTable.getCellFormatter().getElement(row, 4).getStyle().setPropertyPx("borderRightWidth", 0);
			paymentsTable.getCellFormatter().getElement(row, 5).getStyle().setPropertyPx("borderLeftWidth", 0);
		}

		paymentsTable.getRowFormatter().addStyleName(row,
				row % 2 == 0 ? AON.AON_DATA_TABLE_ROW_ODD : AON.AON_DATA_TABLE_ROW_EVEN);
	}

	private NewPaymentHandler insertNewPaymentRow() {
		int row = paymentsTable.getRowCount();

		NewPaymentHandler newPaymentHandler = new NewPaymentHandler();
		Button newButton = new Button();
		newButton.setTabIndex(Short.MAX_VALUE);
		newButton.setStyleName(AON.AON_ICON_RESET); // clear gwt-Button
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		newPaymentHandler.setNewButton(newButton);
		newButton.ensureDebugId("button-new-payment");

		paymentsTable.setWidget(row, 0, newButton);
		paymentsTable.setHTML(row, 1, "&nbsp;");

		MultiWordSuggestOracle paymentsOracle = new MultiWordSuggestOracle();
		SuggestBox descriptionBox = new SuggestBox(paymentsOracle, new TextBox(), newPaymentHandler);
		descriptionBox.setAutoSelectEnabled(false);
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);
		newPaymentHandler.setOracle(paymentsOracle);
		newPaymentHandler.setDescriptionBox(descriptionBox);
		descriptionBox.ensureDebugId("description-box-new-payment");

		TextBox amountBox = new ExpressionBox();
		amountBox.getElement().getStyle().setWidth(98, Unit.PCT);
		amountBox.addStyleName(AON.AON_TEXT_RIGHT);
		amountBox.setVisible(false);
		paymentsTable.setWidget(row, 3, amountBox);
		newPaymentHandler.setExpressionBox(amountBox);
		amountBox.ensureDebugId("amount-box-new-payment");

		paymentsTable.setHTML(row, 4, "&nbsp;");
		paymentsTable.setHTML(row, 5, "&nbsp;");

		formatRow(row);

		return newPaymentHandler;
	}

	private NewDeductionHandler insertNewDeductionRow() {
		int row = paymentsTable.getRowCount();
		NewDeductionHandler newDeductionHandler = new NewDeductionHandler();

		Button newButton = new Button();
		newButton.setTabIndex(Short.MAX_VALUE);
		newButton.setStyleName(AON.AON_ICON_RESET); // clear gwt-Button
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		newDeductionHandler.setNewButton(newButton);

		paymentsTable.setWidget(row, 0, newButton);
		paymentsTable.setHTML(row, 1, "&nbsp;");

		final MultiWordSuggestOracle deductionsOracle = new MultiWordSuggestOracle();
		SuggestBox descriptionBox = new SuggestBox(deductionsOracle);
		descriptionBox.setAutoSelectEnabled(false);
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);
		newDeductionHandler.setOracle(deductionsOracle);
		newDeductionHandler.setDescriptionBox(descriptionBox);

		paymentsTable.setHTML(row, 3, "&nbsp;");

		TextBox amountBox = new ExpressionBox();
		amountBox.getElement().getStyle().setWidth(98, Unit.PCT);
		amountBox.addStyleName(AON.AON_TEXT_RIGHT);
		amountBox.setVisible(false);
		paymentsTable.setWidget(row, 4, amountBox);
		newDeductionHandler.setExpressionBox(amountBox);
		paymentsTable.getFlexCellFormatter().setColSpan(row, 4, 2);

		// paymentsTable.setHTML(tr, 5, "&nbsp;");

		formatRow(row);

		return newDeductionHandler;
	}

	private NewBonusHandler insertNewBonusRow(int row) {

		NewBonusHandler newBonusHandler = new NewBonusHandler();

		Button newButton = new Button();
		newButton.setTabIndex(Short.MAX_VALUE);
		newButton.setStyleName(AON.AON_ICON_RESET); // clear gwt-Button
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		newBonusHandler.setNewButton(newButton);

		paymentsTable.setWidget(row, 0, newButton);
		paymentsTable.setHTML(row, 1, "&nbsp;");

		final MultiWordSuggestOracle deductionsOracle = new MultiWordSuggestOracle();
		SuggestBox descriptionBox = new SuggestBox(deductionsOracle);
		descriptionBox.setAutoSelectEnabled(false);
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);
		newBonusHandler.setOracle(deductionsOracle);
		newBonusHandler.setDescriptionBox(descriptionBox);

		paymentsTable.setHTML(row, 3, "&nbsp;");

		TextBox amountBox = new ExpressionBox();
		amountBox.getElement().getStyle().setWidth(98, Unit.PCT);
		amountBox.addStyleName(AON.AON_TEXT_RIGHT);
		amountBox.setVisible(false);
		paymentsTable.setWidget(row, 4, amountBox);
		newBonusHandler.setExpressionBox(amountBox);
		paymentsTable.getFlexCellFormatter().setColSpan(row, 4, 2);

		// paymentsTable.setHTML(tr, 5, "&nbsp;");

		formatRow(row);

		return newBonusHandler;
	}

	private void insertBlankPaymentRow() {
		int row = paymentsTable.getRowCount();
		paymentsTable.setHTML(row, 0, "&nbsp;");
		paymentsTable.setHTML(row, 1, "&nbsp;");
		paymentsTable.setHTML(row, 2, "&nbsp;");
		paymentsTable.setHTML(row, 3, "&nbsp;");
		paymentsTable.setHTML(row, 4, "&nbsp;");
		paymentsTable.setHTML(row, 5, "&nbsp;");

		formatRow(row);
	}

	private void dumpDeductions(List<Deduction> deductions) {

		int row = paymentsTable.getRowCount();
		for (Deduction deduction : deductions) {
			row = dumpDeduction(row, deduction);
		}

	}

	private int dumpDeduction(int row, Deduction deduction) {
		Button expandButton = null;

		if (isSystemDeduction(deduction)) {
			row = dumpSystemDeduction(row, deduction, expandButton);
		} else {
			if (deduction.getAmount() != null)
				dumpItem(deduction, row++, getIconRowStyle(deduction),
						new DeductionChangeHandler<TextBox>(deduction), true, expandButton);
			else if (deduction.getId() == null) {
				String styles[] = eventStyles.get(Event.Type.ERROR);
				dumpDbItem(deduction, row++, styles[0], styles[1], new RecoverDeductionHandler(deduction), false);

			} else {
				// REMOVE() deductions
				if (isCgcBaseDeduction(deduction))
					cgcBaseDeduction = deduction;
				else if (isCgpBaseDeduction(deduction))
					cgpBaseDeduction = deduction;
			}

		}
		return row;
	}

	private int dumpSystemDeduction(int row, Deduction deduction, Button expandButton) {
		if (deduction instanceof CompositeDeduction) {
			expandButton = new Button();
			expandButton.setTabIndex(Short.MAX_VALUE);
			expandButton.setStyleName(AON.AON_ICON_EXPAND);
			expandButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		}

		
		String description = DEDUCTION_DESCRIPTIONS.get(deduction.getType());
		if (description == null)
			description = deduction.getDescription();

		if (deduction.getAmount() != null) {
			Double percent = getPercent(deduction, salaryDraftObject);
			dumpSystemDeduction(deduction, percent, description, row++, expandButton);

			if (deduction instanceof CompositeDeduction) {
				for (Deduction child : ((CompositeDeduction) deduction).getChilds()) {

					dumpSystemItem(child,
							"  " + description + " " + formatChildDescriptionSuffix(child, salaryDraftObject),
							row, null, null);

					paymentsTable.getRowFormatter().getElement(row++).getStyle().setDisplay(Display.NONE);
				}
			}

		} else {
			Double dbPercent = getDbPercent(deduction, salaryDraftObject);
			String styles[] = eventStyles.get(Event.Type.ERROR);
			dumpDbSystemDeduction(deduction, dbPercent, description, row++, styles[0], styles[1]);
		}
		return row;
	}

	private void dumpPayment(Payment payment, int row, String iconStyleName,
			ItemChangeHandler<TextBox, Payment> handler) {
		dumpPayment(payment, row, iconStyleName, handler, !isAutoGenerated(payment) );
	}


	private void dumpPayment(Payment payment, int row, String iconStyleName,
			ItemChangeHandler<TextBox, Payment> handler, boolean isEditable) {

		Widget labelWidget = null;

		Double amount = payment.getAmount();
		Double quote = payment.getQuote();

		if (amount != null && !amount.equals(quote)) {
			labelWidget = newPercentLabel(format(quote));
			labelWidget.addStyleName(AON.AON_ICON_BONUS_SMALL);
			labelWidget.getElement().getStyle().setPaddingRight(16, Unit.PX);
			labelWidget.getElement().getStyle().setProperty("backgroundPosition", "center right");
		}

		Button expandButton = null;
		if (payment instanceof CompositePayment) {
			expandButton = new Button();
			expandButton.setTabIndex(Short.MAX_VALUE);
			expandButton.setStyleName(AON.AON_ICON_EXPAND);
			expandButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		}

		dumpItem(payment, row, iconStyleName, handler, false, labelWidget, expandButton, isEditable);

		if (payment instanceof CompositePayment) {
			for (Payment child : ((CompositePayment) payment).getChilds()) {
				child.setDescription(formatChildDescription(child, salaryDraftObject));
				dumpPayment(child, ++row, iconStyleName, new PaymentChangeHandler(payment), false);
				paymentsTable.getRowFormatter().getElement(row).getStyle().setDisplay(Display.NONE);
			}
		}
		
		ensureDebugId(paymentsTable.getRowFormatter().getElement(row), "payment-row-" + row);
		
	}

	private <I extends Item> void dumpItem(I item, int row, String iconStyleName, ItemChangeHandler<TextBox, I> handler,
			boolean isDeduction) {
		dumpItem(item, row, iconStyleName, handler, isDeduction, null, null, true);

	}

	private <I extends Item> void dumpItem(I item, int row, String iconStyleName, ItemChangeHandler<TextBox, I> handler,
			boolean isDeduction, Button expandButton) {
		dumpItem(item, row, iconStyleName, handler, isDeduction, null, null, true);

	}

	private <I extends Item> void dumpItem(I item, int row, String iconStyleName, ItemChangeHandler<TextBox, I> handler,
			boolean isDeduction, Widget labelWidget, Button expandButton, boolean isEditable) {

		// first cell for edit other stuff buttons.
		Button editButton = new Button();
		editButton.setTabIndex(Short.MAX_VALUE);
		editButton.setStyleName(iconStyleName);
		editButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		editButton.ensureDebugId("edit-button-" + row );
		
		handler.setEditButton(editButton);
		if (expandButton != null) {
			HorizontalPanel editPanel = new HorizontalPanel();
			editPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
			editPanel.add(editButton);
			editPanel.add(expandButton);
			paymentsTable.setWidget(row, 0, editPanel);
			handler.setExpandButton(expandButton);

		} else {
			// remove 'aon-editDataTable-button' margin & paddind.
			// We don't like it here.
			paymentsTable.setWidget(row, 0, editButton);
		}
		enable(editButton, isEditable);

		if (labelWidget == null)
			paymentsTable.setHTML(row, 1, "&nbsp;");
		else
			paymentsTable.setWidget(row, 1, labelWidget);

		TextBox descriptionBox = new TextBox();
		enable(descriptionBox, isEditable);
		String description = item.getDescription();
		descriptionBox.setText(description != null ? description : item.getDescriptionTemplate());
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		descriptionBox.setMaxLength(DESCRIPTION_MAX_LENGTH);
		paymentsTable.setWidget(row, 2, descriptionBox);
		handler.setDescriptionWidget(descriptionBox);
		descriptionBox.ensureDebugId("description-box-" + row );

		String expression = item.getExpression();
		boolean isReadOnly = SpecialExpresion.isReadOnly(expression);
		TextBox amountBox = new ExpressionBox();
		enable(amountBox, isEditable && !isReadOnly);
		String amount = format(item.getAmount());
		amountBox.setText(amount != null ? amount : item.getExpression());
		amountBox.getElement().getStyle().setWidth(98, Unit.PCT);
		amountBox.addStyleName(AON.AON_TEXT_RIGHT);
		amountBox.setMaxLength(EXPRESSION_MAX_LENGTH);
		handler.setExpressionWidget(amountBox);
		contentAssistManager.addValueBox(amountBox);
		amountBox.ensureDebugId("expression-box-" + row );

		InlineLabel dbAmountLabel = new InlineLabel();
		dbAmountLabel.setText(format(item.getDbAmount()));
		dbAmountLabel.setVisible(salaryDraftObject.hasDbSalary());
		dbAmountLabel.addStyleName(AON.AON_TEXT_RIGHT);
		setDbStyleName(dbAmountLabel, amountBox.getText(), dbAmountLabel.getText());
		amountBox.ensureDebugId("db-amount-label-" + row );

		HorizontalPanel amountsPanel = new HorizontalPanel();
		amountsPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		amountsPanel.add(amountBox);
		amountsPanel.add(dbAmountLabel);
		amountsPanel.setCellWidth(dbAmountLabel, "50%");
		amountsPanel.setCellHorizontalAlignment(dbAmountLabel, HorizontalAlignmentConstant.startOf(Direction.RTL));
		VisibilityImpl dbWidget = new VisibilityImpl(dbAmountLabel.getElement().getParentElement());

		dbWidget.setVisible(salaryDraftObject.hasDbSalary() && dbSalaryCheck.getValue());

		addDbWidget(dbWidget);

		paymentsTable.setWidget(row, isDeduction ? 4 : 3, amountsPanel);

		paymentsTable.setHTML(row, isDeduction ? 3 : 4, "&nbsp;");

		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		buttonsPanel.getElement().getStyle().setWidth(100, Unit.PCT);
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		Scope itemScope = item.getScope();
		if (itemScope.compareTo(Scope.AGREEMENT) > 0 && item.isDefinedAt(Scope.AGREEMENT)) {
			Button agreementButton = getAgreementButton();
			agreementButton.setTabIndex(Short.MAX_VALUE);
			buttonsPanel.add(agreementButton);
			handler.setAgreementButton(agreementButton);
			enable(agreementButton, isEditable);
			agreementButton.ensureDebugId("agreement-button-" + row );
		}

		Button deleteButton = new Button();
		deleteButton.setTabIndex(Short.MAX_VALUE);
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		buttonsPanel.add(deleteButton);
		enable(deleteButton, !isReadOnly(item) && !isRemove(item) && isEditable);
		deleteButton.ensureDebugId("delete-button-" + row );

		handler.setDeleteButton(deleteButton);

		paymentsTable.setWidget(row, 5, buttonsPanel);
		paymentsTable.getCellFormatter().addStyleName(row, 5, AON.AON_TEXT_RIGHT);

		formatRow(row);
		

		if (item.getScope() == Scope.SALARY) {
			paymentsTable.getRowFormatter().addStyleName(row, AON.AON_DATA_TABLE_ROW_HIGHLIGHT);
			paymentsTable.getRowFormatter().addStyleName(row - 1, AON.AON_DATA_TABLE_ROW_HIGHLIGHT_TOP);
		} // highlight dirty, not saved items.
		
	}

	private <I extends Item> void dumpDbItem(I item, int row, String iconStyleName, String textStyleName,
			ClickHandler handler, boolean isDeduction) {

		// first cell for edit other stuff buttons.
		Button editButton = new Button();
		editButton.setStyleName(iconStyleName);
		editButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		// remove 'aon-editDataTable-button' margin & paddind.
		// We don't like it here.
		paymentsTable.setWidget(row, 0, editButton);

		paymentsTable.setHTML(row, 1, "&nbsp;");

		Label descriptionLabel = new InlineLabel(item.getDescription());
		// descriptionLabel.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionLabel);

		InlineLabel dbAmountLabel = new InlineLabel();
		dbAmountLabel.setText(format(item.getDbAmount()));
		dbAmountLabel.setVisible(salaryDraftObject.hasDbSalary());
		dbAmountLabel.addStyleName(AON.AON_TEXT_RIGHT);
		setDbStyleName(dbAmountLabel, null, dbAmountLabel.getText());

		HorizontalPanel amountsPanel = new HorizontalPanel();
		amountsPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		amountsPanel.setWidth("100%");
		amountsPanel.add(new InlineLabel());
		amountsPanel.add(dbAmountLabel);
		amountsPanel.setCellWidth(dbAmountLabel, "50%");
		amountsPanel.setCellHorizontalAlignment(dbAmountLabel, HorizontalAlignmentConstant.startOf(Direction.RTL));

		paymentsTable.setWidget(row, isDeduction ? 4 : 3, amountsPanel);

		paymentsTable.setHTML(row, isDeduction ? 3 : 4, "&nbsp;");

		// paymentsTable.getFlexCellFormatter().setColSpan(row, 4, 2);

		Button recoverButton = new Button();
		recoverButton.setStyleName(AON.AON_ICON_CANCEL);
		recoverButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentsTable.setWidget(row, 5, recoverButton);
		paymentsTable.getCellFormatter().addStyleName(row, 5, AON.AON_TEXT_RIGHT);
		recoverButton.addClickHandler(handler);
		
		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(row); col++) {
			fomatter.addStyleName(row, col, textStyleName);
		}

		formatRow(row);

		if (item.getScope() == Scope.SALARY) {
			paymentsTable.getRowFormatter().addStyleName(row, AON.AON_DATA_TABLE_ROW_HIGHLIGHT);
			paymentsTable.getRowFormatter().addStyleName(row - 1, AON.AON_DATA_TABLE_ROW_HIGHLIGHT_TOP);
		} // highlight dirty, not saved items.

		addDbWidget(new VisibilityImpl(paymentsTable.getRowFormatter().getElement(row)));
	}

	private void dumpSystemDeduction(Deduction deduction, Double percent, String description, int row,
			Button expandButton, String... iconStyles) {
		Widget percentWidget = newPercentWidget(deduction, percent);
		

		dumpSystemItem(deduction, description, row, percentWidget, expandButton, iconStyles);

		Variable percentVariable = getPercentVariable(deduction.getType());
		if (percentVariable == null)
			return;

		if (percentVariable instanceof UndefinedDeductionVariable) {
			Label warnLabel = new InlineHTML("&nbsp;");
			warnLabel.addStyleName(AON.AON_ICON_WARN);
			warnLabel.addStyleName(style.cellWarn());
			paymentsTable.setWidget(row, 0, warnLabel);
			return;
		}

		if (percentVariable.getScope() != Scope.SALARY)
			return;

		for (int col : new int[] { 0, 1 }) {
			paymentsTable.getCellFormatter().addStyleName(row, col, AON.AON_DATA_TABLE_CELL_HIGHLIGHT);
			paymentsTable.getCellFormatter().addStyleName(row - 1, col, AON.AON_DATA_TABLE_CELL_HIGHLIGHT_TOP);
		}
		Label changedLabel = new InlineHTML("&nbsp;");
		changedLabel.addStyleName(AON.AON_ICON_CHANGED);
		changedLabel.addStyleName(style.cellChanged());
		paymentsTable.setWidget(row, 0, changedLabel);
	}

	private void dumpSystemItem(Item<?> deduction, String description, int row, Widget percentageWidget,
			Button expandButton, String... iconStyles) {

		InlineLabel iconLabel = new InlineLabel();
		for (String iconStyle : iconStyles) {
			iconLabel.addStyleName(iconStyle);
		}

		if (expandButton != null) {
			HorizontalPanel panel = new HorizontalPanel();
			panel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
			panel.add(iconLabel);
			panel.add(expandButton);
			paymentsTable.setWidget(row, 0, panel);
			DeductionChangeHandler<TextBox> handler = new DeductionChangeHandler<TextBox>((Deduction) deduction);
			handler.setExpandButton(expandButton);
		} else {
			paymentsTable.setWidget(row, 0, iconLabel);
		}
		if (percentageWidget != null)
			paymentsTable.setWidget(row, 1, percentageWidget);
		/* paymentsTable.setText(row, 1, deduction.getDescription()); */

		paymentsTable.getCellFormatter().addStyleName(row, 0, AON.AON_TEXT_CENTER);

		paymentsTable.setHTML(row, 2, description);

		HorizontalPanel amountsPanel = new HorizontalPanel();
		amountsPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		InlineLabel amountLabel = new InlineLabel();
		amountLabel.setText(format(deduction.getAmount()));
		// very ugly !!!!
		boolean isCost = false;
		for ( String style: iconStyles ) 
			isCost |= AON.AON_ICON_COST.equals(style);
		
		amountLabel.ensureDebugId(deduction.getType().name().toLowerCase() + ( isCost ? "_cost"  : "" ) );

		InlineLabel dbAmountLabel = new InlineLabel();
		dbAmountLabel.setText(format(deduction.getDbAmount()));
		setDbStyleName(dbAmountLabel, amountLabel);
		amountsPanel.add(amountLabel);
		amountsPanel.add(dbAmountLabel);

		amountsPanel.setWidth("100%");
		amountsPanel.setCellWidth(dbAmountLabel, "50%");
		amountsPanel.setCellHorizontalAlignment(amountLabel, HorizontalAlignmentConstant.startOf(Direction.RTL));
		amountsPanel.setCellHorizontalAlignment(dbAmountLabel, HorizontalAlignmentConstant.startOf(Direction.RTL));

		VisibilityImpl visibilityImpl = new VisibilityImpl(dbAmountLabel.getElement().getParentElement());
		addDbWidget(visibilityImpl);
		visibilityImpl.setVisible(salaryDraftObject.hasDbSalary() && dbSalaryCheck.getValue());

		paymentsTable.setWidget(row, 4, amountsPanel);

		paymentsTable.getCellFormatter().addStyleName(row, 4, AON.AON_TEXT_RIGHT);
		paymentsTable.getFlexCellFormatter().setColSpan(row, 4, 2);

		formatRow(row);

	}

	private void dumpDbSystemDeduction(Deduction deduction, Double percent, String description, int row,
			String iconStyleName, String textStyleName) {
		dumpSystemDeduction(deduction, percent, description, row, null);

		// first cell for edit other stuff buttons.
		Button iconButton = new Button();
		iconButton.setStyleName(iconStyleName);
		iconButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		// remove 'aon-editDataTable-button' margin & paddind.
		// We don't like it here.
		paymentsTable.setWidget(row, 0, iconButton);

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(row); col++) {
			fomatter.addStyleName(row, col, textStyleName);
		}

	}

	/*
	 * 
	 * @param context
	 * 
	 * @param to
	 * 
	 * @param show
	 */
	private Scope dumpContext(List<Variable> context, Scope toScope, boolean show, Scope lastScope) {
		final int cols = 3;

		int count = contextTable.getRowCount() * cols;

		ListIterator<Variable> iterator = context.listIterator();
		while (iterator.hasNext()) {

			Variable variable = iterator.next();


			Scope scope = variable.getScope();
			if (scope.compareTo(toScope) < 0) {
				if (!(variable instanceof UndefinedVariable)) {
					continue;
				}
			}

			Widget variableWidget ; 
			try {
				variableWidget  = getVariableWidget(variable, toScope, show);
			} catch ( SkipVariableException e ){
				continue;
			}
			
			int row = count / cols;
			int col = count % cols;
			contextTable.setWidget(row, col, variableWidget);

			contextTable.getRowFormatter().addStyleName(row,
					row % 2 == 0 ? AON.AON_DATA_TABLE_ROW_ODD : AON.AON_DATA_TABLE_ROW_EVEN);

			contextTable.getColumnFormatter().setWidth(col, (100 / cols) + "%");

			if (variable.getScope() == Scope.SALARY) {
				contextTable.getCellFormatter().addStyleName(row, col, AON.AON_DATA_TABLE_CELL_HIGHLIGHT);
				if (row > 0)
					contextTable.getCellFormatter().addStyleName(row - 1, col, AON.AON_DATA_TABLE_CELL_HIGHLIGHT_TOP);
			} // highlight dirty, not saved variables.

			count++;
			iterator.remove();

		}

		int row = count / cols;
		int col = count % cols;

		if (context.isEmpty()) {
			if (row == 0 || col == 0)
				return null;
			for (; col < cols; col++)
				contextTable.addCell(row);
			return null;
		}

		Variable variable = context.get(0);
		final Scope nextScope = variable.getScope();

		if (lastScope == nextScope)
			return lastScope;

		final List<Variable> remainContext = new ArrayList<Variable>(context);

		Widget expandWidget = getContextExpandWidget(nextScope, row, col, remainContext);

		contextTable.setWidget(row, col, expandWidget);
		contextTable.getFlexCellFormatter().setColSpan(row, col, cols - col);

		return nextScope;

	}

	private <T extends IsWidget & HasValue<String> & HasAllFocusHandlers & Focusable & HasEnabled> Widget getVariableWidget(
			final Variable variable, Scope scope, boolean show) {

		HTMLPanel htmlPanel = new HTMLPanel("");

		T editor = createEditor(variable);
		int width = editor instanceof ListBox ? size2px(20) + 6 : size2px(20);
		editor.asWidget().getElement().getStyle().setWidth(width, Unit.PX);

		VariableChangeHandler<T> variableChangeHandler = createVariableChangeHandler(variable);// new
																								// VariableChangeHandler<T>(variable);

		Label label = getLabel(variable);
		htmlPanel.add(label);
		variableChangeHandler.setLabel(label);

		Panel valuePanel = new HorizontalPanel();
		valuePanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);

		//TextBox variableTextBox = new ExpressionBox();
		//variableTextBox.setMaxLength(EXPRESSION_MAX_LENGTH);

		variableChangeHandler.setEditor(editor);

		valuePanel.add(editor);

		valuePanel.add(new InlineHTML("&nbsp;"));

		boolean enabled = editor.isEnabled() ;
		

		String debugName = variable.getName().toLowerCase();

		if (!(variable instanceof UndefinedVariable)) {
			enabled &= wasUniqueDraftPeriod(variable);
			editor.setEnabled(enabled);
			if (enabled && scope.compareTo(Scope.AGREEMENT) > 0 && variable.isDefinedAt(Scope.AGREEMENT)) {
				Button agreementVarButton = getAgreementVarButton(variable);
				agreementVarButton.setTabIndex(Short.MAX_VALUE);
				valuePanel.add(agreementVarButton);
				agreementVarButton.ensureDebugId("agreement-button-" + debugName );
			}
			if ( enabled ){
				Button deleteButton = getDeleteButton(variable);
				deleteButton.setTabIndex(Short.MAX_VALUE);
				valuePanel.add(deleteButton);
				deleteButton.ensureDebugId("delete-button-" + debugName );
			}
		} else if (variable instanceof UndefinedPaymentVariable) {
			String styles[] = eventStyles.get(Event.Type.WARNING);

			StyleToggleButton itemButton = getPaymentButton((UndefinedPaymentVariable) variable, styles[0], styles[1]);
			itemButton.setTabIndex(Short.MAX_VALUE);
			valuePanel.add(itemButton);
			// not show payments of variables at 'to' ...
			itemButton.setValue(show && variable.getScope().compareTo(Scope.AGREEMENT) >= 0, true);
			itemButton.ensureDebugId("item-button-" + debugName );
		} else if (variable instanceof UndefinedDeductionVariable) {
			String styles[] = eventStyles.get(Event.Type.WARNING);

			StyleToggleButton itemButton = getDeductionButton((UndefinedDeductionVariable) variable, styles[0],
					styles[1]);
			itemButton.setTabIndex(Short.MAX_VALUE);

			valuePanel.add(itemButton);
			itemButton.setValue(show && variable.getScope() == Scope.SALARY, true);
			itemButton.ensureDebugId("item-button-" + debugName );
		}

		if (enabled && scope.compareTo(Scope.APPLICATION) > 0
				&& (variable.isDefinedAt(Scope.SYSTEM) || variable.isDefinedAt(Scope.APPLICATION))) {
			Button systemButton = getSystemVarButton(variable);
			systemButton.setTabIndex(Short.MAX_VALUE);
			valuePanel.add(systemButton);
			systemButton.ensureDebugId("system-button-" + debugName );
		}

		htmlPanel.add(valuePanel);

		variableChangeHandlers.add(variableChangeHandler);

		return htmlPanel;
	}

	private Widget getContextExpandWidget(final Scope expandScope, final int row, final int col,
			final List<Variable> context) {

		Panel expandPanel = new HorizontalPanel();
		expandPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		boolean collapse = (SalaryDraft.this.scope.compareTo(expandScope) <= 0);
		final Label expandLabel = new Label(
				(collapse ? "Ocultar" : "Mostrar") + " variables del " + SCOPE_DESCRIPTIONS.get(expandScope));
		expandPanel.add(expandLabel);
		final Button expandButton = new Button();
		expandButton.setTabIndex(Short.MAX_VALUE);
		expandButton.setStyleName(collapse ? AON.AON_ICON_COLLAPSEALL : AON.AON_ICON_EXPANDALL);
		expandButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		expandPanel.add(expandButton);
		// this code, is for make this row's height equal to rows with variables
		TextBox hiddenTextBox = new TextBox();
		hiddenTextBox.setVisibleLength(1);
		hiddenTextBox.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		expandPanel.add(hiddenTextBox);

		HTMLPanel htmlPanel = new HTMLPanel("");
		HTML blank = new HTML("&nbsp;");
		blank.setStyleName(style.cellLabel());
		htmlPanel.add(blank);
		htmlPanel.add(expandPanel);

		expandButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (SalaryDraft.this.scope.compareTo(expandScope) <= 0)
					collapse();
				else
					expand();
			}

			private void expand() {
				List<Variable> contextCopy = new ArrayList<Variable>(context);
				dumpContext(contextCopy, expandScope, false, null);
				expandButton.removeStyleName(AON.AON_ICON_EXPANDALL);
				expandButton.setStyleName(AON.AON_ICON_COLLAPSEALL, true);
				expandLabel.setText("Ocultar variables del " + SCOPE_DESCRIPTIONS.get(expandScope));
				SalaryDraft.this.scope = expandScope;
			}

			private void collapse() {
				for (int i = contextTable.getRowCount() - 1; i > row; i--)
					contextTable.removeRow(i);
				for (int i = contextTable.getCellCount(row) - 1; i > col; i--)
					contextTable.removeCell(row, i);

				expandButton.removeStyleName(AON.AON_ICON_COLLAPSEALL);
				expandButton.setStyleName(AON.AON_ICON_EXPANDALL, true);
				expandLabel.setText("Mostrar variables del " + SCOPE_DESCRIPTIONS.get(expandScope));
				SalaryDraft.this.scope = SCOPE_STEPS.get(SCOPE_STEPS.indexOf(expandScope) - 1);

			}

		});
		
		expandButton.ensureDebugId("expand-button-" + expandScope.name().toLowerCase());

		return htmlPanel;
	}

	private void print() {
		salaryDraftObject.download("application/pdf", new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String dataURI) {
				showPreview();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
			}

		});
	}

	private void printSettle() {
		
		SettleType type = SettleType.valueOf(settlePreviewListBox.getSelectedValue());
		switch (type) {
		case A3:
			Reports.a3Letter(salaryDraftObject,  dataURI -> {
				SalaryDraft.this.showPreview();
				SalaryDraft.this.pdfViewer.setDocument(dataURI, zoom / 100.00 );
			});
			break;
		case LETTER:
			Reports.defLetter(salaryDraftObject,  dataURI -> {
				SalaryDraft.this.showPreview();
				SalaryDraft.this.pdfViewer.setDocument(dataURI, zoom / 100.00 );
			});
			break;
		default:
			print();
			break;
		}

		
	}

	private void printSalary() {
		
		SalaryType type = SalaryType.valueOf(salaryPreviewListBox.getSelectedValue());
		switch (type) {
		case STANDARD:
			Reports.standard(salaryDraftObject,  dataURI -> {
				SalaryDraft.this.showPreview();
				SalaryDraft.this.pdfViewer.setDocument(dataURI, zoom / 100.00 );
			});
			break;
		case STANDARD_NEW:
			Reports.standard_new(salaryDraftObject,  dataURI -> {
				SalaryDraft.this.showPreview();
				SalaryDraft.this.pdfViewer.setDocument(dataURI, zoom / 100.00 );
			});
			break;
		case STANDARD_COLS:
			Reports.standard_cols(salaryDraftObject,  dataURI -> {
				SalaryDraft.this.showPreview();
				SalaryDraft.this.pdfViewer.setDocument(dataURI, zoom / 100.00 );
			});
			break;
		case RECIBE:
			Reports.recibe(salaryDraftObject,  dataURI -> {
				SalaryDraft.this.showPreview();
				SalaryDraft.this.pdfViewer.setDocument(dataURI, zoom / 100.00 );
			});
			break;
		case RECIBE_CRA:
			Reports.recibe_cra(salaryDraftObject,  dataURI -> {
				SalaryDraft.this.showPreview();
				SalaryDraft.this.pdfViewer.setDocument(dataURI, zoom / 100.00 );
			});
			break;
		default:
			print();
			break;
		}

		
	}

	private void irpfPrint() {

		salaryDraftObject.downloadIrpf("application/pdf", new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String dataURI) {
				showIrpfPreview();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
			}

		});
	}

	private void printPreview() {
		salaryDraftObject.getType()
		.accept( new Salary.TypeVisitor<Void>() {
			@Override
			public Void visitDelay(Type type) {
				print();
				return null;
			}
			
			@Override
			public Void visitExtra(Type type) {
				print();
				return null;
			}
			
			@Override
			public Void visitSalary(Type type) {
				print();
				return null;
			}
			
			@Override
			public Void visitSettle(Type type) {
				printSettle();
				return null;
			}

			@Override
			public Void visitNotEnjoyedVacations(Type type) {
				print();
				return null;
			}
			
		});
	}

	
	private void setDbStyleName(Label l2, HasText l1) {
		setDbStyleName(l2, l1.getText(), l2.getText());
	}

	private void setDbStyleName(Widget widget, String s1, String s2) {

		if (StringUtils.equals(s1, s2)) {
			widget.removeStyleName(style.textError());
			widget.addStyleName(style.textOk());
		} else {
			widget.removeStyleName(style.textOk());
			widget.addStyleName(style.textError());
		}
	}

	private String format(Date date) {
		return date == null ? null : DATE_FORMAT.format(date);
	}

	private Label getLabel(Variable variable) {
		StringBuffer text = new StringBuffer(variable.getName());

		try {
			if (!wasUniqueDraftPeriod(variable) && !(variable instanceof UndefinedVariable)) {
				Date startDate = variable.getStartDate();
				Date endDate = variable.getEndDate();
				DateTimeFormat format = DateTimeFormat.getFormat("dd/MM");
				text.append(" ( " + format.format(startDate) + " - " + format.format(endDate) + " )");
			}
		} catch (Exception e) {
		}

		Label label = new Label(text.toString());

		label.setStyleName(style.cellLabel());
		if (variable instanceof UndefinedVariable) {
			label.setStyleName(AON.AON_ICON_WARN, true);
			label.setStyleName(style.cellWarn(), true);
		} else if (variable.getScope() == Scope.SALARY) {
			label.setStyleName(AON.AON_ICON_CHANGED, true);
			label.setStyleName(style.cellChanged(), true);
		}

		return label;
	}

	private boolean wasUniqueDraftPeriod(Variable variable) {
		int count = 0;
		for ( Variable var : salaryDraftObject.getContext() )
			if ( var.getName().equals(variable.getName()))
				count++;
		return count <= 1;
	}

	private Button getSystemVarButton(Variable variable) {
		return getSystemVarButton(variable, AON.AON_ICON_CONFIG);
	}

	private Button getSystemVarButton(Variable variable, String iconStyle) {
		Button sysButton = new Button();
		sysButton.setStyleName(iconStyle);
		sysButton.setStyleName(AON.AON_NO_MARGIN, true);
		sysButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		sysButton.addClickHandler(new AbstractVarHandler(variable) {
			@Override
			String getExpression(Variable var) {
				return getSystemExpression(var);
			}
		});
		return sysButton;
	}

	private Button getAgreementVarButton(Variable variable) {
		Button agreementButton = getAgreementButton();
		agreementButton.addClickHandler(new AbstractVarHandler(variable) {
			@Override
			String getExpression(Variable var) {
				return getAgreementExpression(var);
			}
		});
		return agreementButton;
	}

	private Button getAgreementButton() {
		Button agreementButton = new Button();
		agreementButton.setStyleName(AON.AON_ICON_AGREEMENT);
		agreementButton.setStyleName(AON.AON_NO_MARGIN, true);
		agreementButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		return agreementButton;
	}

	private Button getDeleteButton(Variable variable) {
		Button deleteButton = new Button();
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_NO_MARGIN, true);
		deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		deleteButton.addClickHandler(new VariableRemoveHandler(variable));
		return deleteButton;
	}

	private StyleToggleButton getPaymentButton(final HasPayment hasPayment, final String iconStyleName,
			final String textStyleName) {
		
		if ( hasPayment.getPayment() == null)
			return null;
		
		if ( calculated(hasPayment))
			return null;
		
		StyleToggleButton paymentButton = new StyleToggleButton("aon-icon-file-entrance", "aon-icon-file-exit-cancel");
		
		paymentButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			private Element tr;

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean down = event.getValue();
				if (down) {
					tr = showPayment(hasPayment.getPayment(), iconStyleName, textStyleName);
				} else {
					removePayment(tr);
					removePayment(hasPayment.getPayment());
					removePaymentChangeHandlerFor(hasPayment.getPayment().getId());
				}

			}
		});
		
		
		
		return paymentButton;
	}

	private StyleToggleButton getDeductionButton(final HasDeduction hasDeduction, final String iconStyleName,
			final String textStyleName) {

		if ( hasDeduction.getDeduction() == null)
			return null;

		StyleToggleButton paymentButton = new StyleToggleButton("aon-icon-file-entrance", "aon-icon-file-exit-cancel");

		paymentButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			private Element tr;

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				try {
					boolean down = event.getValue();
					if (down) {
						tr = showDeduction(hasDeduction.getDeduction(), iconStyleName, textStyleName);
					} else {
						removePayment(tr);
						removeDeduction(hasDeduction.getDeduction());
					}
				} catch ( Throwable t ) {
					// Why ????
				}

			}
		});

		return paymentButton;
	}

	private StyleToggleButton getBonusButton(final HasBonus hasBonus, final String iconStyleName,
			final String textStyleName) {
		
		if ( hasBonus.getBonus() == null )
			return null;
		
		StyleToggleButton paymentButton = new StyleToggleButton("aon-icon-file-entrance", "aon-icon-file-exit-cancel");

		paymentButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			private Element tr;

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean down = event.getValue();
				if (down) {
					if (!costsCheck.getValue()) {
						costsCheck.setValue(true);
						showCosts(true);
					}
					tr = showBonus(hasBonus.getBonus(), iconStyleName, textStyleName);
				} else {
					removePayment(tr);
					removeBonus(hasBonus.getBonus());
				}

			}
		});

		return paymentButton;
	}

	private void removePayment(Element tr) {
		RowFormatter formatter = paymentsTable.getRowFormatter();
		for (int row = 0; row < paymentsTable.getRowCount(); row++) {
			if (tr == formatter.getElement(row)) {
				paymentsTable.removeRow(row);
				return;
			}
		}
	}

	private void removeBonus(Bonus bonus) {
		List<Bonus> bonuses = salaryDraftObject.getBonuses();
		for (int i = 0; i < bonuses.size(); i++) {
			if (bonus == bonuses.get(i)) {
				bonuses.remove(i);
				return;
			}
		}
	}

	private void removePayment(Payment payment) {
		List<Payment> payments = salaryDraftObject.getPayments();
		for (int i = 0; i < payments.size(); i++) {
			if (payment == payments.get(i)) {
				payments.remove(i);
				return;
			}
		}

	}

	private void removeDeduction(Deduction deduction) {
		List<Deduction> deductions = salaryDraftObject.getDeductions();
		for (int i = 0; i < deductions.size(); i++) {
			if (deduction == deductions.get(i)) {
				deductions.remove(i);
				return;
			}
		}
	}

	private Element showPayment(Payment payment, String iconStyleName, String textStyleName) {

		List<Payment> payments = salaryDraftObject.getPayments();
		ItemComparator comparator = new ItemComparator();

		int idx = 0;
		int row = 0;
		for (; idx < payments.size(); idx++) {
			Payment p = payments.get(idx);
			if (comparator.compare(p, payment) > 0)
				break;
			row += (p instanceof CompositePayment) ? 1 + ((CompositePayment) p).getChilds().size() : 1;
		}

		payments.add(idx, payment);

		row += 1; // We add one due to header

		paymentsTable.insertRow(row);
		PaymentChangeHandler<TextBox> handler = new PaymentChangeHandler<TextBox>(payment);
		dumpPayment(payment, row, iconStyleName, handler);
		paymentChangeHandlers.add(handler);

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(row); col++) {
			fomatter.addStyleName(row, col, textStyleName);
		}

		return paymentsTable.getRowFormatter().getElement(row);

	}

	private Element showDeduction(Deduction deduction, String iconStyleName, String textStyleName) {

		List<Deduction> deductions = salaryDraftObject.getDeductions();
		ItemComparator<Deduction.Type> comparator = new ItemComparator<Deduction.Type>();
		
		int idx = 0;
		for (; idx < deductions.size(); idx++)
			if (comparator.compare(deductions.get(idx), deduction) > 0)
				break;


		deductions.add(idx, deduction);

		idx += salaryDraftObject.getPayments().size() + 3; // We add one due to
															// header
		paymentsTable.insertRow(idx);
		if (isSystemDeduction(deduction)) {
			dumpSystemDeduction(idx, deduction, null /*expandButton*/);
		} else {
			dumpItem(deduction, idx, iconStyleName, new DeductionChangeHandler<TextBox>(deduction), true);
		}
		
		//dumpDeduction(idx, deduction);

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(idx); col++) {
			fomatter.addStyleName(idx, col, textStyleName);
		}

		return paymentsTable.getRowFormatter().getElement(idx);
	}

	private Element showBonus(Bonus bonus, String iconStyleName, String textStyleName) {

		List<Bonus> bonuses = salaryDraftObject.getBonuses();
		bonuses.add(bonus);

		int idx = paymentsTable.getRowCount() - 3;
		paymentsTable.insertRow(idx);

		dumpItem(bonus, idx, iconStyleName, new BonusChangeHandler<TextBox>(bonus), true);

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(idx); col++) {
			fomatter.addStyleName(idx, col, textStyleName);
		}

		return paymentsTable.getRowFormatter().getElement(idx);
	}

	private void showCosts() {
		showCosts(costsCheck.getValue());
	}

	private void showCosts(boolean show) {

		int costsBeforeRow = paymentsTable.getRowCount()
				- (/* 1 new line */+2 /* blanks line */);

		int costsCount = salaryDraftObject.getCosts().size();

		if (show) {
			dumpCosts(costsBeforeRow);
			dumpBonuses(costsBeforeRow + costsCount);
		} else {
			int bonusCount = salaryDraftObject.getBonuses().size();
			hideCosts(costsBeforeRow - (costsCount + bonusCount));

			int bonusBeforeRow = paymentsTable.getRowCount()
					- (2 /* new line */ + 1 /* blanks line */);
			hideBonus(bonusBeforeRow - (bonusCount));
		}
	}

	private void dumpCosts(int beforeRow) {
		List<Deduction> costs = salaryDraftObject.getCosts();
		for (int i = 0; i < costs.size(); i++) {
			Deduction cost = costs.get(i);
			paymentsTable.insertRow(beforeRow + i);
			Double percent = getPercent(cost, salaryDraftObject);
			Deduction.Type type = cost.getType();

			String description = COSTS_DESCRIPTIONS.get(cost.getName());
			if (AonStringUtils.isBlank(description))
				description = cost.getDescription();
			if (AonStringUtils.isBlank(description))
				description = type != null ? type.getDescription() : Deduction.Type.OTHER.getDescription();
			

			dumpSystemDeduction(cost, percent, description, beforeRow + i, null, AON.AON_ICON_COST,
					AON.AON_EDIT_DATA_TABLE_BUTTON, AON.AON_PADDING_LEFT);
		}
	}

	private void dumpBonuses(int beforeRow) {
		List<Bonus> bonuses = salaryDraftObject.getBonuses();
		for (int i = 0; i < bonuses.size(); i++) {
			Bonus bonus = bonuses.get(i);
			paymentsTable.insertRow(beforeRow + i);
			if (bonus.getAmount() != null) {
				dumpItem(bonus, beforeRow + i, getIconRowStyle(bonus), new BonusChangeHandler<TextBox>(bonus), true);
			} else {
				String styles[] = eventStyles.get(Event.Type.WARNING);
				dumpItem(bonus, beforeRow + i, styles[0], new BonusChangeHandler<TextBox>(bonus), true);
				addStyle(paymentsTable, beforeRow + i, styles[1]);
			}

		}
		paymentsTable.insertRow(beforeRow + bonuses.size());
		newBonusHandler = insertNewBonusRow(beforeRow + bonuses.size());
		newBonusHandler.initSuggestionItems();
	}

	private void hideCosts(int beforeRow) {

		int costs = salaryDraftObject.getCosts().size();
		for (int i = 0; i < costs; i++)
			paymentsTable.removeRow(beforeRow);

	}

	private void hideBonus(int beforeRow) {

		newBonusHandler = null;
		int bonuses = salaryDraftObject.getBonuses().size();
		for (int i = 0; i < bonuses; i++)
			paymentsTable.removeRow(beforeRow);

		paymentsTable.removeRow(beforeRow); // newBonusRow

	}

	// TODO : ???
	private static boolean displayNow(Payment payment) {
		return true;
		/*
		 * Short month = payment.getMonth(); if (month == null) return true;
		 * Date start = salaryDraftObject.getStartDate(); if (month <
		 * start.getMonth()) return false; Date end =
		 * salaryDraftObject.getEndDate(); return (month <= end.getMonth());
		 */
	}

	private static boolean displayNow(UndefinedPaymentVariable var) {
		return displayNow(var.getPayment());
	}

	private String getIconRowStyle(Item item) {

		switch (item.getScope()) {
		case SALARY:
			return AON.AON_ICON_ROW_SELECTOR_CHANGED;
		case AGREEMENT:
			return AON.AON_ICON_ROW_SELECTOR_C;
		case SYSTEM:
			return AON.AON_ICON_ROW_SELECTOR_S;
		default:
			return AON.AON_ICON_ROW_SELECTOR;
		}
	}

	private Variable getPercentVariable(Deduction.Type type) {

		switch (type) {
		case IRPF:
			return getContextVariable(PORCENTAJE_IRPF);
		case UNEMPLOYMENT:
			return getContextVariable(PORCENTAJE_DESMPL);
		default:
			return null;
		}
	}

	private Widget newPercentWidget(Deduction deduction, Double percent) {
		switch (deduction.getType()) {
		case IRPF:
			return newIrpfPercentBox(deduction, percent);
		case UNEMPLOYMENT:
			return newPercentBox("PORCENTAJE_" + deduction.getName(), deduction, percent);
		default:
			return newPercentLabel(deduction, percent, getPercentVariable(deduction.getType()));
		}
	}

	private Variable getContextVariable(String name) {
		for (Variable var : salaryDraftObject.getDrafContext())
			if (StringUtils.equals(var.getName(), name))
				return var;

		for (Variable var : salaryDraftObject.getContext())
			if (StringUtils.equals(var.getName(), name))
				return var;

		return null;
	}

	private StringVariable newStringVariable(String name) {
		StringVariable var = new StringVariable();
		var.setName(name);
		var.setImplicit(false);
		var.setScope(Scope.SALARY); // DRAFT
		var.setEndDate(salaryDraftObject.getEndDate());
		var.setStartDate(salaryDraftObject.getStartDate());
		return var;
	}

	private Widget newIrpfPercentBox(final Deduction irpf, final Double percent) {

		final TextBox irpfPercentTexTBox = new ExpressionBox();
		irpfPercentTexTBox.ensureDebugId("irpfPercentTexTBox");

		class IrpfPercentHandler implements FocusHandler, BlurHandler, ChangeHandler {

			// -------------------------------------------------- Focus Handler
			@Override
			public void onFocus(FocusEvent event) {
				Variable irpfPercent = SalaryDraft.this.getContextVariable(PORCENTAJE_IRPF);
				if (irpfPercent != null)
					irpfPercentTexTBox.setText(irpfPercent.getExpression());
				else
					irpfPercentTexTBox.setText(String.valueOf(NumberUtils.isValid(percent) ? 0.00 : percent));
			}

			// --------------------------------------------------- Blur Handler

			@Override
			public void onBlur(BlurEvent event) {
				irpfPercentTexTBox.setText(formatPercent(NumberUtils.isNotValid(percent) ? 0.00 : percent));
			}

			// ------------------------------------------------- Change Handler
			@Override
			public void onChange(ChangeEvent event) {

				StringVariable var = SalaryDraft.this.newStringVariable(PORCENTAJE_IRPF);
				String value = irpfPercentTexTBox.getValue();
				var.setExpression(StringUtils.isEmpty(value) ? "REMOVE_VARIABLE()" : value);

				salaryDraftObject.addDraftVariable(var);
				salaryDraftObject.calculate(SalaryDraft.this);
			}

			// ----------------------------------------------------------------

		}
		;
		irpfPercentTexTBox.setVisibleLength(5);
		irpfPercentTexTBox.setText(formatPercent(NumberUtils.isNotValid(percent) ? 0.00 : percent));
		IrpfPercentHandler irpfPercentHandler = new IrpfPercentHandler();
		irpfPercentTexTBox.addBlurHandler(irpfPercentHandler);
		irpfPercentTexTBox.addFocusHandler(irpfPercentHandler);
		irpfPercentTexTBox.addChangeHandler(irpfPercentHandler);

		Panel irpfPercentPanel = new HorizontalPanel();
		irpfPercentPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		irpfPercentPanel.add(irpfPercentTexTBox);
		Variable irpfPercentVar = getContextVariable(PORCENTAJE_IRPF);
		if (irpfPercentVar == null) {
			irpfPercentVar = newStringVariable(PORCENTAJE_IRPF);
		}

		if (isSystemVariable(irpfPercentVar)) {
			String irpfIcon = IRPF_ICONS.get(salaryDraftObject.getCommunity());

			Button aeatButton = getSystemVarButton(irpfPercentVar, irpfIcon != null ? irpfIcon : AON.AON_ICON_AET);

			aeatButton.setEnabled(false);
			irpfPercentPanel.add(aeatButton);
		} else {
			Button systemButton = getSystemVarButton(irpfPercentVar, AON.AON_ICON_CONFIG);
			systemButton.setTabIndex(Short.MAX_VALUE);
			irpfPercentPanel.add(systemButton);
		}

		return irpfPercentPanel;
	}

	private Widget newPercentBox(final String variable, final Deduction deduction, final Double percent) {

		final TextBox percentTexTBox = new ExpressionBox();

		class PercentHandler implements FocusHandler, BlurHandler, ChangeHandler {

			// -------------------------------------------------- Focus Handler
			@Override
			public void onFocus(FocusEvent event) {
				Variable percentVariable = SalaryDraft.this.getContextVariable(variable);
				if (percentVariable != null)
					percentTexTBox.setText(percentVariable.getExpression());
				else
					percentTexTBox.setText(String.valueOf(NumberUtils.isValid(percent) ? 0.00 : percent));
			}

			// --------------------------------------------------- Blur Handler

			@Override
			public void onBlur(BlurEvent event) {
				percentTexTBox.setText(formatPercent(NumberUtils.isNotValid(percent) ? 0.00 : percent));
			}

			// ------------------------------------------------- Change Handler
			@Override
			public void onChange(ChangeEvent event) {

				StringVariable var = SalaryDraft.this.newStringVariable(variable);
				String value = percentTexTBox.getValue();

				var.setExpression(StringUtils.isEmpty(value) ? "REMOVE_VARIABLE()" : value);

				salaryDraftObject.addDraftVariable(var);
				salaryDraftObject.calculate(SalaryDraft.this);
			}

			// ----------------------------------------------------------------

		}
		;
		percentTexTBox.setVisibleLength(5);
		percentTexTBox.setText(formatPercent(NumberUtils.isNotValid(percent) ? 0.00 : percent));
		PercentHandler irpfPercentHandler = new PercentHandler();
		percentTexTBox.addBlurHandler(irpfPercentHandler);
		percentTexTBox.addFocusHandler(irpfPercentHandler);
		percentTexTBox.addChangeHandler(irpfPercentHandler);

		Panel percentPanel = new HorizontalPanel();
		percentPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		percentPanel.add(percentTexTBox);
		Variable percentVar = getContextVariable(variable);

		if (percentVar == null) {
			percentVar = newStringVariable(variable);
			return percentPanel;
		}

		// if (isSystemVariable(percentVar)) {
		// Button button =
		// getSystemVarButton(percentVar,AON.AON_ICON_SEGSOCIAL_SMALL);
		// button.setEnabled(false);
		// percentPanel.add(button);
		// } else {
		// Button systemButton = getSystemVarButton(percentVar,
		// AON.AON_ICON_CONFIG);
		// systemButton.setTabIndex(Short.MAX_VALUE);
		// percentPanel.add(systemButton);
		// }

		return percentPanel;
	}

	private void calculate() {
		salaryDraftObject.calculate(this);
	}

	private void calculate(final CalculateCallback callback) {
		salaryDraftObject.calculate(new CalculateCallback() {

			@Override
			public Calculate getCalculate() {
				return SalaryDraft.this.getCalculate();
			}

			@Override
			public void onCalculateFailure(Throwable throwable) {
				SalaryDraft.this.onCalculateFailure(throwable);
				callback.onCalculateFailure(throwable);
			}

			@Override
			public void onCalculateSucces(SalaryDraftObject object) {
				SalaryDraft.this.onCalculateSucces(object);
				callback.onCalculateSucces(object);
			}

		});
	}

	private VariableChangeHandler<?> getVariableChangeHandlerFor(String name) {
		for (VariableChangeHandler<?> handler : variableChangeHandlers)
			if (StringUtils.equals(handler.variable.getName(), name))
				return handler;
		return null;
	}

	private VariableChangeHandler<?> getNextVariableChangeHandler(String name) {
		for (Iterator<VariableChangeHandler<?>> iterator = variableChangeHandlers.iterator(); iterator.hasNext();) {
			VariableChangeHandler<?> handler = iterator.next();
			if (StringUtils.equals(handler.variable.getName(), name))
				return iterator.hasNext() ? iterator.next() : null;
		}
		return null;
	}

	private PaymentChangeHandler<?> getPaymentChangeHandlerFor(int id) {
		for (PaymentChangeHandler<?> handler : paymentChangeHandlers) {
			if (handler.item.getId() == id) {
				return handler;
			}
		}
		return null;
	}

	private PaymentChangeHandler<?> getNextPaymentChangeHandlerFor(int id) {
		for (Iterator<PaymentChangeHandler<?>> iterator = paymentChangeHandlers.iterator(); iterator.hasNext();) {
			PaymentChangeHandler<?> handler = iterator.next();
			if (handler.item.getId() == id) {
				return iterator.hasNext() ? iterator.next() : null;
			}
		}
		return null;
	}

	private void removePaymentChangeHandlerFor(int id) {
		for (ListIterator<PaymentChangeHandler<?>> iterator = paymentChangeHandlers.listIterator(); iterator
				.hasNext();) {
			PaymentChangeHandler<?> handler = iterator.next();
			if (handler.item.getId() == id) {
				iterator.remove();
				return;
			}
		}

	}

	private int size2px(int size) {
		int visibleLength = totalLiquidLabel.getVisibleLength();
		int offsetWidth = totalLiquidLabel.getOffsetWidth();
		return size * offsetWidth / visibleLength;
	}

	private void loadContentAssistManager() {

		class ProposalsLoader implements AsyncCallback<ContextDescriptor> {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ContextDescriptor result) {
				contentAssistManager.cleanAll();
				contentAssistManager.addAll(result);
			}

		}

		salaryDraftObject.getContext(new ProposalsLoader());
	}
	
	private Event getEvent4(Payment p) {
		for ( Event event: salaryDraftObject.getEvents() )
			if ( event instanceof HasPayment )
				if ( ((HasPayment) event).getPayment().equals(p))
					return event;
		return null;
	}
	
	private int getRow4(Payment p) {
		return 0;
	}

	private boolean calculated(HasPayment payment) {
		for ( Payment p: salaryDraftObject.getPayments() )
				if ( payment.getPayment().equals(p))
					return true;
		return false;
	}
	
	private boolean isExtra(){
		return salaryDraftObject != null && salaryDraftObject.getType() == Salary.Type.EXTRA;
	}

	private boolean isSalary(){
		return salaryDraftObject != null && salaryDraftObject.getType() == Salary.Type.SALARY;
	}

	private boolean hasDbSalary(){
		return salaryDraftObject != null  && salaryDraftObject.hasDbSalary();
	}
	
	private boolean hasEvents() {
		return salaryDraftObject != null && salaryDraftObject.hasEvents();
	}
	
	private Widget getDiffsWithDbSalary(){
		if ( salaryDraftObject == null ) 
			return null;
		
		if ( !salaryDraftObject.hasDbSalary() )	
			return null;
			
		if ( !AonStringUtils.equals(totalPaymentLabel.getText(), dbTotalPaymentLabel.getText()))
			return dbTotalPaymentLabel;

		if ( !AonStringUtils.equals(totalLiquidLabel.getText(), dbTotalLiquidLabel.getText()))
			return dbTotalLiquidLabel;

		if ( !AonStringUtils.equals(cgcBaseLabel.getText(), dbCgcBaseLabel.getText()))
			return dbCgcBaseLabel;

		if ( !AonStringUtils.equals(cgpBaseLabel.getText(), dbCgpBaseLabel.getText()))
			return dbCgpBaseLabel;

		if ( !AonStringUtils.equals(irpfBaseLabel.getText(), dbIrpfBaseLabel.getText()))
			return dbIrpfBaseLabel;

		if ( !AonStringUtils.equals(prorationBaseLabel.getText(), dbProrationBaseLabel.getText()))
			return dbProrationBaseLabel;

		if ( !AonStringUtils.equals(totalDeductionLabel.getText(), dbTotalDeductionLabel.getText()))
			return dbTotalDeductionLabel;
		
		if ( !AonStringUtils.equals(hExtraBaseLabel.getText(), dbHExtraBaseLabel.getText()))
			return dbHExtraBaseLabel;

		if ( !AonStringUtils.equals(nonHExtraBaseLabel.getText(), dbNonHExtraBaseLabel.getText()))
			return dbNonHExtraBaseLabel;


		return null;
	}
	
	private List<Variable> getConstants(List<Variable> context) {
		List<Variable> summingConstants = 
				context.stream()
				.filter(v->isSummingConstant(v))
				.filter(v-> v.getValue() != null )
				.collect(Collectors.groupingBy(
						Variable::getName,
						Collectors.summingDouble(v->Double.parseDouble(String.valueOf(v.getValue())))
				))
				.entrySet().stream()
				.filter(e -> e.getValue() > 0.00 )
				.map(e -> {
					NumberVariable v = new NumberVariable();
					v.setName(e.getKey());
					v.setValue(e.getValue());
					v.setScope(Scope.CONTRACT);
					v.setEndDate(salaryDraftObject.getEndDate());
					v.setStartDate(salaryDraftObject.getStartDate());
					return  v;
					})
				.collect(Collectors.toList());
		
		return summingConstants; 
	}

	public void addDraftVariable(String name , String expression ) { 
		StringVariable var = new StringVariable
		.Builder()
		.setName(name)
		.setImplicit(true)
		.setScope(Scope.SALARY)
		.setExpression(expression)
		.create();		
		salaryDraftObject.addDraftVariable(var);
	}
	
	private native void export2JS(SalaryDraft salaryDraft) /*-{
		$wnd.calculate = $entry(function() {
			salaryDraft.@com.esferalia.aon.gwt.payroll.client.SalaryDraft::calculate()();
		});
		$wnd.addDraftVariable = $entry( function(name, expr) {
			salaryDraft.@com.esferalia.aon.gwt.payroll.client.SalaryDraft::addDraftVariable(Ljava/lang/String;Ljava/lang/String;)(name,expr);
		});
	}-*/;

	private void setAutomatic(boolean automatic) {

//		acceptButton.setEnabled(!readOnly);
		acceptButton.setVisible(!automatic);
		
		totalPaymentsLabel.setReadOnly(automatic);
		totalLiquidLabel.setReadOnly(automatic);
		
		
		
	}
	
	private void setReadOnly(boolean readOnly) {

//		fxButton.setEnabled(!readOnly);
		fxButton.setVisible(!readOnly);
//		undoButton.setEnabled(!readOnly);
		undoButton.setVisible(!readOnly);
//		redoButton.setEnabled(!readOnly);
		redoButton.setVisible(!readOnly);
//		undoAllButton.setEnabled(!readOnly);
		undoAllButton.setVisible(!readOnly);

		contextTable.setStyleName("aon-ReadOnly", readOnly);
		paymentsTable.setStyleName("aon-ReadOnly", readOnly);
	}
	
	private void showTimeRulePanel() {
		timeRulePanel.clear();

		if ( tgssCheck.getValue() == false  ) {
			timeRulePanel.setVisible(false);
			return;
		}
		
		int startDay = this.salaryDraftObject.getStartDate().getDate();
		int endDay = this.salaryDraftObject.getEndDate().getDate();
		int days = ( endDay - startDay ) +1 ;  
		
		if ( days == 1 ) 
			return; // only one day, no sense this rule 
		
		Integer sections [] = 
		this.salaryDraftObject.getContext().
		stream().filter(var -> "BASE_CGC".equals(var.getName()))
		.map(var -> var.getStartDate().getDate()).sorted()
		.toArray(Integer[]::new);
		
		for ( int i = startDay; i <= endDay; i++ ) 
		{
			int day = i;
			Label dayLabel = new Label(( i < 10 ? "0" : "" ) + Integer.toString(day));
			dayLabel.addStyleName("aon-text-center");

			int section = 0;//Math.abs(Arrays.binarySearch(sections, day) );
			for ( Integer s: sections ) {
				if ( s > day )
					break;
				section++;
			} //TODO: Not work properly .
				
			dayLabel.addStyleName(new String [] {style.sectionEven(), style.sectionOdd()}[section % 2 ] );
			dayLabel.addDoubleClickHandler(e -> { onDraftSection(day); });
			
			Date dayDate = new Date(this.salaryDraftObject.getStartDate().getYear(), 
					this.salaryDraftObject.getStartDate().getMonth(), day);
			if ( salaryDraftObject.hasDraftSection(dayDate) ) {
				dayLabel.addStyleName("aon-bold");
				dayLabel.addStyleName("aon-black");
			}
			
			timeRulePanel.add(dayLabel);
		}

		timeRulePanel.setVisible(true);
		
	}
	
	private void showDbTimeRulePanel() {

		if ( !hasDbSalary() || dbSalaryCheck.getValue() == false || tgssCheck.getValue() == false  ) {
			showTimeRulePanel();
			return;
		}
		
		int startDay = this.salaryDraftObject.getStartDate().getDate();
		int endDay = this.salaryDraftObject.getEndDate().getDate();
		int days = ( endDay - startDay ) +1 ;  
		
		if ( days == 1 ) 
			return; // only one day, no sense this rule 
		
		Integer sections [] = 
		this.salaryDraftObject.getContext().
		stream().filter(var -> "BASE_CGC".equals(var.getName()))
		.map(var -> var.getStartDate().getDate()).sorted()
		.toArray(Integer[]::new);
		
		Integer dbSections [] = 
		this.salaryDraftObject.getDbContext().
		stream().filter(var -> "BASE_CGC".equals(var.getName()))
		.map(var -> var.getStartDate().getDate()).sorted()
		.toArray(Integer[]::new);

		for ( int i = startDay; i <= endDay; i++ ) 
		{
			int day = i;
			
			Widget dayLabel = timeRulePanel.getWidget(i-1); 
			
			dayLabel.removeStyleName(style.sectionOdd());
			dayLabel.removeStyleName(style.sectionEven());
			
			int dbSection = 0;
			for ( Integer s: dbSections ) {
				if ( s > day )
					break;
				dbSection++;
			} //TODO: Not work properly .
				
			if ( sections.length > dbSection
				&& dbSections[dbSection] == sections[dbSection]
				&& dbSections[dbSection-1] == sections[dbSection-1])
				dayLabel.addStyleName(new String [] {style.dbSectionEvenOk(), style.dbSectionOddOk()}[dbSection % 2 ] );
			else if ( sections.length == dbSection 
					&& dbSections[dbSection] == sections[dbSection] )
				dayLabel.addStyleName(new String [] {style.dbSectionEvenOk(), style.dbSectionOddOk()}[dbSection % 2 ] );
			else
				dayLabel.addStyleName(new String [] {style.dbSectionEvenEr(), style.dbSectionOddEr()}[dbSection % 2 ] );
			
			
		}

		
	}
	private void onDraftSection(int day)  {
		Date section = DateUtils.copyDateOnly(this.salaryDraftObject.getStartDate());
		section.setDate(day);
		
		if ( salaryDraftObject.hasDraftSection( section ) )
			salaryDraftObject.removeDraftSection(section);
		else 
			salaryDraftObject.addDraftSection(section);
		
		calculate();
		
	}

	// ------------------------------------------------------- Static 'Library'
	static boolean skipVariable(String name) {
		for (String skip : SKIP_VARIABLES)
			if (skip.equals(name))
				return true;
		return false;
	}

	static boolean skipVariable(Variable variable) {
		String name = variable.getName();
		for (String skip : SKIP_VARIABLES) {
			if (skip.equals(name))
				return true;
		}

		if (variable instanceof UndefinedPaymentVariable && !displayNow((UndefinedPaymentVariable) variable))
			return true;

		return false;
	}

	static boolean isSummingConstant(Variable variable) {
		String name = variable.getName();
		for (String constant : SUMMING_CONSTANTS)
			if (constant.equals(name))
				return true;
		
		return false;
	}

	static <T extends IsWidget & HasValue<String> & HasAllFocusHandlers & Focusable & HasEnabled> T createEditor(
			Variable variable) {
		for (VariableEditorFactory<T> factory : VARIABLE_EDITOR_FACTORIES) {
			if (factory.accept(variable))
				return factory.create(variable);
		}
		return null;
	}

	// ------------------------------------------------------- Static 'Library'

	private static boolean isSystemDeduction(Deduction deduction) {
		for (Deduction.Type type : SYSTEM_DEDUCTION)
			if (type == deduction.getType())
				return true;
		
		return deduction.getScope() == Scope.SYSTEM;
	}

	private static Double getDbPercent(Deduction deduction, SalaryDraftObject draftObject) {
		return getPercent(deduction.getType(), deduction.getDbAmount(), draftObject.getDbIrpfBase(),
				draftObject.getDbCgcBase(), draftObject.getDbCgpBase(), draftObject.getDbHExtraBase(),
				draftObject.getDbNonHExtraBase());
	}

	private static Double getPercent(Deduction deduction, SalaryDraftObject draftObject) {
		return getPercent(deduction.getType(), deduction.getAmount(), draftObject.getIrpfBase(),
				draftObject.getCgcBase(), draftObject.getCgpBase(), draftObject.gethExtraBase(),
				draftObject.getNonHExtraBase());
	}

	private static Double getPercent(Deduction.Type type, Double amount, Double irpfBase, Double cgcBase,
			Double cgpBase, Double hExtraBase, Double nonHExtraBase) {
		if ( amount == null )
			return null;
		
		switch (type) {
		case IRPF:
			return NumberUtils.isValid(irpfBase) ? amount / irpfBase * 100 : null;
		// case JOB_TRAINING:
		// case UNEMPLOYMENT:
		// case COMMON_CONTINGENCY:
		// return amount / cgcBase * 100;
		case FOGASA:
		case PROFESSIONAL_CONTINGENCY:
			return NumberUtils.isValid(cgpBase) ? amount / cgpBase * 100 : null;
		case STRUCTURAL_OVERTIME:
			return NumberUtils.isValid(hExtraBase) ? amount / hExtraBase * 100 : null;
		case NON_STRUCTURAL_OVERTIME:
			return NumberUtils.isValid(nonHExtraBase) ? amount / nonHExtraBase * 100 : null;
		default:
			return NumberUtils.isValid(cgcBase) ? amount / cgcBase * 100 : null;
		}
	}

	public static String format(Double amount) {
		return NumberUtils.isNotValid(amount) ? null : AON.CURRENCY_FORMAT.format(AON.round(amount));
	}

	protected static String getSuggestionString( Item item) {
		String suggestion = item.getDescription();
		
		if (!StringUtils.isEmpty(suggestion))
			suggestion = RegExp.compile(
					"\\(.*\\)","i").replace(suggestion, "");
			
		if (!StringUtils.isEmpty(item.getName())) {
			if (!StringUtils.isEmpty(suggestion))
				suggestion += " ( " + item.getName() + " )";
			else
				suggestion = item.getName();
				
		}
		
		Enum type = item.getType();
		if ( type != null ) 
			if (!StringUtils.isEmpty(suggestion))
				suggestion = type.name().replace("CRA_", "") + " " + suggestion; 
			else 
				suggestion = type.name().replace("CRA_", ""); 
		
		return suggestion;
	}

	private static Double parse(String str) {
		return str == null ? 0.00 : AON.CURRENCY_FORMAT.parse(str);
	}

	private static String formatPercent(Double amount) {
		return format(amount) + " %";
	}

	private static String _toMVELExpression(String str) {
		StringBuffer buffer = new StringBuffer();
		int inOutPos[] = { 0 };
		while (inOutPos[0] < str.length()) {
			try {
				buffer.append(AON.CURRENCY_FORMAT.parse(str, inOutPos));
			} catch (NumberFormatException e) {
				// works inOutPos[0] is a left value
				buffer.append(str.charAt(inOutPos[0]++));
			}
		}
		return buffer.toString();
	}

	private static Widget newPercentLabel(String str) {
		InlineLabel percentageLabel = new InlineLabel();
		percentageLabel.setText(str);
		// padding-left : 5px, to align vertically with IRPF Widget.
		percentageLabel.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		return percentageLabel;
	}

	private static Widget newPercentLabel(Item<?> item, Double percent, Variable percentVar) {
		if (NumberUtils.isNotValid(percent))
			return newPercentLabel(percentVar == null ? item.getDescription() : String.valueOf(percentVar.getValue()));
		else
			return newPercentLabel(formatPercent(percent));
	}

	private static boolean isSystemVariable(Variable var) {
		if (var.getScope() == Scope.SYSTEM)
			return true;

		String expression = var.getExpression();

		if (StringUtils.isBlank(expression))
			return false;

		String name = var.getName();

		return expression.matches("\\s*SISTEMA\\s*\\(\\s*('" + name + "'|\"" + name + "\")\\s*\\)\\s*");

	}

	private static String getSystemExpression(Variable var) {
		return "SISTEMA('" + var.getName() + "')";
	}

	private static String getAgreementExpression(Variable var) {
		return "CONVENIO('" + var.getName() + "')";
	}

	// @formatter:off
	private final static VariableEditorFactory VARIABLE_EDITOR_FACTORIES[] = { 
			new MonthDaysEditorFactory("DIAS_MES"),
			new DateEditorFactory("FECHA_PREAVISO"),
			//new DateEditorFactory("INICIO_PAGO_DIRECTO"),
			new EnumNameListBoxFactory<Employee.Occupation>("OCUPACION", Employee.Occupation.class),
			new DismissalFactory("CAUSA_INDEMNIZACION"),
			new StringsListBoxFactory("GRUPO_COTIZACION",
					new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11" }),
			new StringsListBoxFactory("TC2", Employee.TC2.getCodes(), Employee.TC2.getDescriptions()),
			new CalendarConstantEditorFactory("HORAS_NOMINA", "HORAS_TRABAJADAS", "HORAS_SEMANA", "DIAS_NOMINA", "DIAS_TRABAJADOS", "DIAS_PAGA", "DIAS_COTIZADOS", "DIAS_HUELGA", "DIAS_NATURALES_MES", "COEFICIENTE_ERE" ), 
			new BooleanEditorFactory(), 
			new DefaultEditorFactory() };

	// @formatter:on

	private static void enable(TextBox textBox, boolean enabled) {

		if (textBox.isEnabled() == enabled)
			return;

		textBox.setEnabled(enabled);

		if (!enabled) {
			textBox.getElement().getStyle().setColor("inherit");
			textBox.getElement().getStyle().setBackgroundColor("inherit");
			textBox.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		} else {
			textBox.getElement().getStyle().clearColor();
			textBox.getElement().getStyle().clearBackgroundColor();
			textBox.getElement().getStyle().clearBorderStyle();
		}
	}

	private static <W extends HasEnabled & HasVisibility> void enable(W widget, boolean enabled) {

		if (widget.isEnabled() == enabled)
			return;

		widget.setEnabled(enabled);
		widget.setVisible(enabled);
	}

	private static final DateTimeFormat START_DATE_FORMAT = DateTimeFormat.getFormat("dd '-'");
	private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat.getFormat("dd 'de' MMMM");

	private static String formatChildDescription(Item<?> child, SalaryDraftObject salaryDraftObject) {

		return child.getDescription() + " " + formatChildDescriptionSuffix(child, salaryDraftObject);

	}

	private static String formatChildDescriptionSuffix(Item<?> child, SalaryDraftObject salaryDraftObject) {

		Date childStart = child.getStartDate();
		Date childEnd = child.getEndDate();

		Date draftStart = salaryDraftObject.getStartDate();
		Date draftEnd = salaryDraftObject.getEndDate();

		if (childStart.equals(childEnd))
			return DateTimeFormat
					.getFormat("EEEE dd 'de' MMMM" + (draftStart.getYear() == draftEnd.getYear() ? "" : " yyyy"))
					.format(childStart);

		if (childStart.getMonth() == childEnd.getMonth())
			return DateTimeFormat.getFormat("dd").format(childStart) + " - "
					+ DateTimeFormat
							.getFormat("dd 'de' MMMM" + (draftStart.getYear() == draftEnd.getYear() ? "" : " yyyy"))
							.format(childEnd);

		return DateTimeFormat.getFormat("dd 'de' MMMM").format(childStart) + " - "
				+ DateTimeFormat.getFormat("dd 'de' MMMM" + (draftStart.getYear() == draftEnd.getYear() ? "" : " yyyy"))
						.format(childEnd);

	}

	private static void setWarnStyles(Widget cgcBaseLabel, boolean warn, String title) {
		cgcBaseLabel.setTitle(warn ? title : "");
		setStyles(cgcBaseLabel, warn, AON.AON_ICON_WARN, AON.AON_PADDING_LEFT);
	}

	private static void setStyles(Widget widget, boolean add, String... styles) {
		for (String style : styles)
			widget.setStyleName(style, add);
	}

	private static void addStyle(FlexTable table, int row, String style) {
		CellFormatter fomatter = table.getCellFormatter();
		for (int col = 0; col < table.getCellCount(row); col++)
			fomatter.addStyleName(row, col, style);
	}

	private static void removeStyle(FlexTable table, int row, String style) {
		CellFormatter fomatter = table.getCellFormatter();
		for (int col = 0; col < table.getCellCount(row); col++)
			fomatter.removeStyleName(row, col, style);
	}

	private static void addStyle(FlexTable table, int row, String style, boolean add) {
		if ( add )
			addStyle(table, row, style);
		else 
			removeStyle(table, row, style);
	}

	private static void setIconStyle(FlexTable table, int row, String style) {
		
//		CellFormatter fomatter = table.getCellFormatter();
//		for (int col = 0; col < table.getCellCount(row); col++)
//			fomatter.addStyleName(row, col, style);
	}

	private static <T extends Item<?>> boolean isReadOnly(T item) {
		String expression = item.getExpression();
		return expression != null && expression.contains("read-only");
	}


	private static <T extends Item<?>> boolean isRemove(T item) {
		return StringUtils.equalsIgnoreCase("REMOVE()", item.getExpression());
	}

	private static <T extends Item<?>> boolean isCgcBaseDeduction(T item) {
		return StringUtils.equalsIgnoreCase("BASE_CGC", item.getDescription());
	}

	private static <T extends Item<?>> boolean isCgpBaseDeduction(T item) {
		return StringUtils.equalsIgnoreCase("BASE_CGP", item.getDescription());
	}

	private static List<Variable> getContext(SalaryDraftObject salaryDraftObject) {
		List<Variable> context = new ArrayList();
		for (Variable var : salaryDraftObject.getContext()) {

			if (var instanceof UndefinedVariable)
				if (contains(context, var.getName()))
					continue;

			context.add(var);

		}
		return context;
	}

	private static boolean contains(List<Variable> vars, String name) {
		for (Variable v : vars)
			if (v.getName().equals(name))
				return true; // Already at context
		return false;
	}
	
	private static boolean isAutoGenerated(Payment payment) {
		return payment.getConceptId() != null && payment.getConceptId() == Integer.MAX_VALUE;
	}
	
}
