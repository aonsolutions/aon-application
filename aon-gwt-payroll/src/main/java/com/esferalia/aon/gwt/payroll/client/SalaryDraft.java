package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.AgreementDraft.isDisabled;
import static com.esferalia.aon.gwt.payroll.client.AgreementDraft.isEnabled;
import static com.esferalia.aon.gwt.payroll.client.Constants.DESCRIPTION_MAX_LENGTH;
import static com.esferalia.aon.gwt.payroll.client.Constants.DESCRIPTION_SIZE;
import static com.esferalia.aon.gwt.payroll.client.Constants.EXPRESSION_MAX_LENGTH;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.gwt.common.shared.NumberUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject.Calculate;
import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject.CalculateCallback;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CompositeBonus;
import com.esferalia.aon.gwt.payroll.shared.CompositeDeduction;
import com.esferalia.aon.gwt.payroll.shared.CompositePayment;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.DelegateVariable;
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
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.Salary.TypeVisitor;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.gwt.payroll.shared.StringTimeLineVariable;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedDeductionVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedPaymentVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
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
import com.google.gwt.event.dom.client.MouseOverEvent;
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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
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
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Grid;
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
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
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

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class SalaryDraft extends ResizeComposite
		implements CalculateCallback, SalarySelect.Listener, UndoManager.Listener{

	private static Logger LOGGER = Logger.getLogger("");

	private static final int DRAFT_PANEL_INDEX = 0;
	private static final int PDF_VIEWER_INDEX = 1;

	public static final String CUSTOM = "CUSTOM";
	public static final String ONLY_THIS_MONTH = "ONLY_THIS_MONTH";
	public static final String FROM_THIS_MONTH = "FROM_THIS_MONTH";
	
	public static final String A3 = SettleType.A3.name();
	public static final String LETTER = SettleType.LETTER.name();
	public static final String JASPER = SettleType.JASPER.name();
	public static final String STANDARD = SalaryType.STANDARD.name();
	public static final String STANDARD_NEW = SalaryType.STANDARD_NEW.name();
	public static final String CLASSIC_NEW = SalaryType.CLASSIC_NEW.name();
	public static final String STANDARD_COLS = SalaryType.STANDARD_COLS.name();
	public static final String RECIBE = SalaryType.RECIBE.name();
	public static final String RECIBE_CRA = SalaryType.RECIBE_CRA.name();

	private static final DateTimeFormat DATE_SHORT = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH_NUM_DAY);
	
	// Listener to fireSettleMessage
	static interface Listener {
		void fireSettleMessage(String message);
	}
	
	public static enum SettleType {
		A3,
		LETTER,
		JASPER
	}

	public static enum SalaryType {
		JASPER,
		CLASSIC_NEW,
		RECIBE,
		RECIBE_CRA,
		STANDARD,
		STANDARD_NEW,
		STANDARD_COLS
	}

	private static Map<Administration, String> ADMINISTRATION_ICONS = new HashMap<Administration, String>() {
		private static final long serialVersionUID = 784424826829639284L;

		{
			put(Administration.ALAVA, AON.AON_ICON_ARABA);
			put(Administration.BIZKAIA, AON.AON_ICON_BIZKAIA);
			put(Administration.GIPUZKOA, AON.AON_ICON_GIPUZKOA);
			put(Administration.NAVARRA, AON.AON_ICON_NAVARRA);
			put(Administration.COMMON_TERRITORY, AON.AON_ICON_AET);
		}
	};

	private static Map<String, String> IRPF_ICONS = new HashMap<String, String>() {
		private static final long serialVersionUID = 784424826829639284L;

		{
			put(null, AON.AON_ICON_AET);
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


	private static Deduction.Type SYSTEM_DEDUCTION[] = { Deduction.Type.IRPF, Deduction.Type.COMMON_CONTINGENCY,
			Deduction.Type.PROFESSIONAL_CONTINGENCY, Deduction.Type.UNEMPLOYMENT, Deduction.Type.JOB_TRAINING,
			Deduction.Type.STRUCTURAL_OVERTIME, Deduction.Type.NON_STRUCTURAL_OVERTIME, Deduction.Type.FOGASA };


	private List<Scope> SCOPE_STEPS = Arrays.asList(Scope.CONTRACT, Scope.AGREEMENT, Scope.SYSTEM);

	private static final String PORCENTAJE_IRPF = "PORCENTAJE_IRPF";
	private static final String PORCENTAJE_CGC = "PORCENTAJE_CGC";
	private static final String PORCENTAJE_DESMPL = "PORCENTAJE_DESMPL";
	private static final String PORCENTAJE_DESMPL_E = "PORCENTAJE_DESMPL_E";
	private static final String PORCENTAJE_FOGASA = "PORCENTAJE_FOGASA";
	private static final String PORCENTAJE_SHORT = "PORCENTAJE_CORTA_DURACION";
	private static final String PORCENTAJE_OFF = "PORCENTAJE_EXONERADO";
	private static final String PORCENTAJE_BACK = "PORCENTAJE_REINCORPORACION";

	// @formatter:off
	private static String[] SKIP_VARIABLES = { 
			
			"GRUPO_COTIZACION", "TC2", "OCUPACION",
			
			"CONVENIO", "SISTEMA", "NETO", "BRUTO", "GTZDO", "_OLD", // functions
			"GET_VARIABLE", "SI", "MAX", "MIN", "ABS", // functions

			"ANTICIPO_ATRASOS", PORCENTAJE_IRPF, PORCENTAJE_DESMPL, "PORCENTAJE_DESMPL_E", //

			"BASE_CGC",
			"BASE_CGC_BRUTA",
			"BASE_CGP", 
			"BASE_CGP_BRUTA",
			"BASE_CGC_E", 
			"BASE_CGP_E", // internals
			"BASE_CGC_MIN_HORA",
			
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
			"DIAS_ERE","DIAS_ERE_FZA", "DIAS_ERE_FZA_EXONERADO",
			"DIAS_PATERNIDAD", // internals
			"DIAS_TRABAJADOS", 
			"DIAS_AUSENCIA",
			"DIAS_INACTIVIDAD",
			"DIAS_NOMINA",
			"DIAS_NATURALES_MES",
			"DIAS_COTIZADOS",
			
			"COEFICIENTE_TRABAJADO",

			"CONTEXT", 
			"SELF", 
			"THIS", 
			"CONCEPTO", // context
			"MENSUALIDAD",

			"HORAS_LUNES", 
			"HORAS_MARTES", 
			"HORAS_MIERCOLES", 
			"HORAS_JUEVES", 
			"HORAS_VIERNES", 
			"HORAS_SABADO", 
			"HORAS_DOMINGO", 
			"HORAS_NOMINA", 
			"HORAS_TRABAJADAS", 

			"OCUPACION_IT", "OCUPACION_IMS", 
			"PREST_IT", "SALARIO_BASE", "GARANTIZADO",
			
			"POR_HORAS", "CONTEXT", "UTILIZADA", "IS_READ",
			
			// PERCENTS
			"TARIFA_IT",
			"TARIFA_IMS",
			"PORCENTAJE_IRPF", 
			"PORCENTAJE_CGC",
			"PORCENTAJE_CGC_E",
			"PORCENTAJE_FOGASA",
			"PORCENTAJE_FP",
			"PORCENTAJE_FP_E",
			"PORCENTAJE_DESMPL",
			"PORCENTAJE_DESMPL_E",
			"PORCENTAJE_EXONERADO",
			"PORCENTAJE_REINCORPORACION",
			
			"DIAS_ERE",	
			"DIAS_ERE_FZA",	
			"DIAS_ERE_FZA_EXONERADO",	
			"DIAS_HUELGA",	
			
			"COEFICIENTE_ERE",	
			"COEFICIENTE_ERE_FZA",	
			"COEFICIENTE_ERE_FZA_EXONERADO",	
			"COEFICIENTE_HUELGA",
			
			"COEFICIENTE_IT",	
			
			"ERE_TOTAL",
			"REINCORPORADO_ERE",
			
			"CAUSA_INACTIVIDAD",
			
			"JORNADAS_TEORICAS",
			
			"BONIFICACION_TUTORIA",
			"BONIFICACION_FORMACION_CONTINUA",
			
			"MODELO_COTIZACION_AGRARIO"
			
			
	};

	// @formatter:off
	private static String[] SKIP_NULL_VARIABLES = {

	};

	// @formatter:on
	static interface HasStyleName  extends  HasVisibility{
		
		String getStyleName();
	}

	static class VisibilityImpl implements HasVisibility , HasStyleName{

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
		
		@Override
		public String getStyleName() {
			return getStyleName(elem);
		}
		
		private static String getStyleName(com.google.gwt.dom.client.Element elem) {
			StringBuffer styleName = new StringBuffer();
			
			styleName.append(UIObject.getStyleName(elem));
			for ( int i = 0 ; i < elem.getChildCount(); i++)
				if ( (com.google.gwt.dom.client.Element.is(elem.getChild(i))) )
					styleName.append(getStyleName((com.google.gwt.dom.client.Element.as(elem.getChild(i)))));
			
			
			return styleName.toString();
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

		default T create(Variable variable, SalaryDraftObject salaryDraftObject) {
			return create(variable);
		}

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
	
	static class ConstantEditorFactory implements VariableEditorFactory<ConstantLabel> {
		
		private String names [];
		
		
		public ConstantEditorFactory(String... names ) {
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
		public ConstantLabel create(Variable variable) {
			ConstantLabel constantLabel = new ConstantLabel();
			constantLabel.ensureDebugId("editor-" + variable.getName().toLowerCase());
			return constantLabel;
		}
	}

	static class AgreementConstantEditorFactory implements VariableEditorFactory<ConstantLabel> {
		
		@Override
		public boolean accept(Variable variable) {
			return variable.getScope() == Scope.AGREEMENT && isSomething(variable);
		}
		
		@Override
		public ConstantLabel create(Variable variable) {
			ConstantLabel constantLabel = new ConstantLabel();
			constantLabel.ensureDebugId("editor-" + variable.getName().toLowerCase());
			return constantLabel;
		}
		
		private static boolean isSomething(Variable variable) {
			if ( variable.getValue() == null )
				return false;
			String str = String.valueOf(variable.getValue());
			if ( AonStringUtils.isBlank(str))
				return false;
			try {
				double d = Double.parseDouble(str) ;
				return d != 0.00;
			} catch ( Throwable t ) {
				return false;
			}
		}
	}

	static class EventConstantEditorFactory implements VariableEditorFactory<EventConstantLabel> {
		
		private String patterns [];
		private SalaryDraft salaryDraft;
		
		
		public EventConstantEditorFactory(String... patterns ) {
			this.patterns = patterns;
		}
		
		@Override
		public boolean accept(Variable variable) {
			for ( String pattern : patterns )
				if ( RegExp.compile(pattern).test(variable.getName()) )
					return true;
			
			
			return false;
		}
		
		@Override
		public EventConstantLabel create(Variable variable) {
			EventConstantLabel constantLabel = new EventConstantLabel();
			constantLabel.ensureDebugId(variable.getName().toLowerCase());
			constantLabel.addClickHandler(e -> EmployeeTree.showEmployeeEvents(variable.getName(), getYears(variable)));
			return constantLabel;
		}
		
		@Override
		public EventConstantLabel create(Variable variable, SalaryDraftObject salaryDraftObject) {
			EventConstantLabel constantLabel = new EventConstantLabel();
			constantLabel.ensureDebugId(variable.getName().toLowerCase());
			constantLabel.addClickHandler(e -> EmployeeTree.showEmployeeEvents(variable.getName(), getYears(salaryDraftObject)));
			return constantLabel;
		}
		
		protected int [] getYears(Variable variable) {
			Integer startYear = DateUtils.getYear(variable.getStartDate());
			Integer endYear = DateUtils.getYear(variable.getEndDate());
			return IntStream.rangeClosed(startYear, endYear).toArray();
		}

		protected int [] getYears(SalaryDraftObject salaryDraftObject) {
			Integer startYear = DateUtils.getYear(salaryDraftObject.getStartDate());
			Integer endYear = DateUtils.getYear(salaryDraftObject.getEndDate());
			return IntStream.rangeClosed(startYear, endYear).toArray();
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

			//textListBox.addItem(variable.getValue().toString(), variable.getValue().toString());

			textListBox.addItem("COTIZACI\u00D3N MENSUAL", "30");

			textListBox.addItem("COTIZACI\u00D3N DIARIA", "DIAS_NATURALES_MES");

			//if (variable.getExpression() != null) {
			//	String expression = variable.getExpression();
			//	if (expression.matches("30(\\.0+)?")) {
			//		textListBox.selectValue("30");
			//		return textListBox;
			//	} else if (expression.matches("DIAS_NATURALES_MES")) {
			//	}
			//}
			
			double monthDays = AonNumberUtils.todouble(variable.getExpression());
			if ( monthDays == 30.00 ) {
				textListBox.selectValue("30");
			} else {
				textListBox.selectValue("DIAS_NATURALES_MES");
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

	static class DelayFactory implements VariableEditorFactory<TextListBox> {

		private String name;

		public DelayFactory(String name) {
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
					if (AonStringUtils.isBlank(value))
						return "NADA";
					try {
						Payment.Type delay = Payment.Type.valueOf(Payment.Type.class, value);
						String contextVariable =  getContextVariable(delay);
						return contextVariable;
					} catch ( Exception e ) {
						return null;
					}
				}
				
				private String getContextVariable(Payment.Type e) {
					switch (e) {
					case CRA_0009:
						return "CRA_0009";
					case CRA_0012:
						return "CRA_0012";
					case CRA_0010:
						return "CRA_0010";
					case CRA_0011:
						return "CRA_0011";
					case CRA_0008:
						return "CRA_0008";
					}
					return null;
				}
			};

			for (Payment.Type e : new Payment.Type[] { 
					Payment.Type.CRA_0008, 
					Payment.Type.CRA_0009, 
					Payment.Type.CRA_0010, 
					Payment.Type.CRA_0011, 
					Payment.Type.CRA_0012 
			} )
				textListBox.addItem(e.getDescription(), e.name());
			
			textListBox.ensureDebugId("editor-" + variable.getName().toLowerCase());
			textListBox.addStyleName("aon-WriteOnly");

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
					if (AonStringUtils.isBlank(value))
						return "NADA";
					
					try {
						Dismissal dismissal = Dismissal.valueOf(Dismissal.class, value);
						return getContextVariable(dismissal);
					} catch ( Exception e) {
						return null;
					}

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
					case RETIREMENT:
						return "JUBILACION";
					case CONDITIONS_CHANGE:
						return "CAMBIO_CONDICIONES";
					case NOT_PASS_TRIAL_PERIOD:
						return "BAJA_PERIODO_PRUEBA";
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
				prevDays += (int) Math.ceil(days);
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

			Variable var = newVariable();
			var.setExpression(StringUtils.isEmpty(value) ? "REMOVE_VARIABLE()" : value);

			salaryDraftObject.addDraftVariable(var);
			// salaryDraftObject.calculate(SalaryDraft.this);
			SalaryDraft.this.calculate(getNextVariableFocusCallback());
		}

		protected void setValue(Object value) {
			if (value == null)
				editor.setValue(null);
			else if (value instanceof Double)
				editor.setValue(formatValue((Double) value));
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

		protected Variable newVariable() {
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
		HasVisibility hasVisibility;

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

		public void setHideButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onExpressionChange(item, "HIDE()");
				}
			});
		}

		public void setEnableButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onRemove(item, "REMOVE()");
				}
			});
		}
		
		public void setDisableButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onRecover(item, "CONVENIO()");
				}
			});
		}

		public void setDisableAgreementButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onRecover(item, AgreementDraft.enable(item.getExpression()));
				}
			});
		}

		public void setEnableAgreementButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onRecover(item, "CONVENIO()");
				}
			});
		}

		public void setIssueDateListBox(final ListBox listBox) {
			listBox.addChangeHandler( new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Short month = Short.parseShort(listBox.getSelectedValue());
					onIssueDateChange(item, month == -1 ? null: month  );
				}
			});
			
		}
		
		public void setHasVisibility(HasVisibility hasVisibility) {
			this.hasVisibility = hasVisibility;
		}
		
		public boolean isVisible() {
			return hasVisibility.isVisible();
		}

		public void setVisible(boolean visible) {
			hasVisibility.setVisible(visible);
		}

		protected int getRowIndex(ClickEvent event) {
			return paymentsTable.getCellForEvent(event).getRowIndex();
		}

		abstract void onEdit();

		abstract void onExpand(ClickEvent event);

		abstract void onCollapse(ClickEvent event);

		abstract void onRemove(I item, String expression);

		abstract void onRecover(I item, String expression);
		
		abstract void onIssueDateChange(I item, Short month);

		abstract void onExpressionChange(I item, String expression);

		abstract void onDescriptionChange(I item, String description);

	}

	class PaymentChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers & Focusable>
			extends ItemChangeHandler<T, Payment> implements PaymentDialog.Callback {

		Button expandButton;
		
		public PaymentChangeHandler(Payment payment) {
			super(payment);
		}

		void expand() {
			expandButton.click();
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
			paymentDialog.setTypeListVisible();
			paymentDialog.setNumberFormat(AON.CURRENCY_FORMAT);
			paymentDialog.setContextProvider(salaryDraftObject);
			paymentDialog.setConcept(getConcept());
			paymentDialog.setName(item.getName());
			paymentDialog.setMonth(item.getMonth());
			paymentDialog.setType(item.getType());
			paymentDialog.setReceiptType(item.getSalaryType());
			// TODO : description template ?
			paymentDialog.setDescription(item.getDescriptionTemplate());
			paymentDialog.setPaymentExpression(item.getExpression()); //
			paymentDialog.setIrpfExpression(item.getIrpfExpression());
			paymentDialog.setQuoteExpression(item.getQuoteExpression());
			
			paymentDialog.setAvailablePayments(salaryDraftObject.getPayments());
			
			paymentDialog.setReadOnly(!isEditable());
			
			paymentDialog.setEnabledTypeListBox(!isSettle());

			paymentDialog.center();
			paymentDialog.show(this);
		}

		private boolean isEditable() {
			if ( isSettle() && isSystem(item) ) 
				return false;
			if ( isDefault(item) )
				return true;
			if ( isAutoGenerated(item))
				return false;
			if ( isFromAgreemen(item))
				return false;
			return isContract(item);
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
		void onIssueDateChange(Payment payment, Short month) {
			payment.setScope(Scope.SALARY);
			payment.setMonth(month);
			payment.setSalaryType(Type.SALARY);
			salaryDraftObject.addDraftPayment(payment);
			salaryDraftObject.calculate(SalaryDraft.this);
		}

		@Override
		void onRemove(Payment payment, String expression) {
			payment.setScope(Scope.SALARY);
			payment.setExpression(expression);
			salaryDraftObject.remove(payment, SalaryDraft.this);
		}

		@Override
		void onRecover(Payment payment, String expression) {
			payment.setScope(Scope.SALARY);
			payment.setExpression(expression);
			salaryDraftObject.recover(payment, SalaryDraft.this);
		}
		
		@Override
		public void setExpandButton(Button expandButton) {
			this.expandButton = expandButton;
			super.setExpandButton(expandButton);
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
	
	class VariablePaymentChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers & Focusable & HasEnabled> extends PaymentChangeHandler<T>{
		
		VariableChangeHandler<T> variableChangeHandler;
		
		public VariablePaymentChangeHandler(Payment payment, String name) {
			super(payment);
			variableChangeHandler = new VariableChangeHandler<T>(getVariable(name)) {
				@Override
				protected Variable newVariable() {
					return new StringTimeLineVariable.Builder()
							.setImplicit(true)
							.setScope(Scope.SALARY)
							.setName(variable.getName())
							.setEndDate(variable.getEndDate())
							.setStartDate(variable.getStartDate())
							.create()
							;
				}
				
				@Override
				protected CalculateCallback getNextVariableFocusCallback() {
					return VariablePaymentChangeHandler.this.getNextVariableFocusCallback();
				}
			};
		}
		
		@Override
		public void setExpressionWidget(T editor) {
			enable(editor, true);
			setEditable(editor, true);
			variableChangeHandler.setEditor(editor);
		}
		
		

		
		protected StringVariable newVariable(String name) {
			return new StringVariable.Builder()
					.setName(name)
					.setImplicit(true)
					.setScope(Scope.SALARY)
					.setEndDate(item.getEndDate())
					.setStartDate(item.getStartDate())
					.create();
		}

		protected Variable getVariable(String name) {
			Variable variable = 
			salaryDraftObject.getContext().stream()
			.filter( v -> !(v instanceof UndefinedVariable))
			.filter(v -> AonStringUtils.equals(v.getName(), name))
			.filter(v -> Objects.equals(v.getStartDate(), item.getStartDate()))
			.filter(v -> Objects.equals(v.getEndDate(), item.getEndDate()))
			//.filter(this::intersects)
			.findFirst().orElse(newVariable(name))
			;
			
			variable.setValue(Math.round(item.getAmount()*1000.00)/1000.00);
			
			return variable;
		}
		
		protected boolean intersects(Variable v) {
			Date maxStart = AonDateUtils.max(v.getStartDate(), item.getStartDate());
			Date minEnd = AonDateUtils.min(v.getEndDate(), item.getEndDate());
			return AonDateUtils.compare(maxStart, minEnd) <= 0;
		}
		
		protected CalculateCallback getNextVariableFocusCallback() {
			class ExpandPaymentFocusCallback implements CalculateCallback {
				
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
					//info("onCalculateSucces (" + item.getDescription() +", " + item.getId() +")");
					PaymentChangeHandler<?> handler = getPaymentChangeHandlerFor(item.getId());
					//info("onCalculateSucces (" + handler + ")");
					if ( handler != null ) {
						handler.expand();
					}
				}
			}
			return new ExpandPaymentFocusCallback();
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
			
			Item<Deduction.Type> concept = dialog.getConcept();
			item.setConceptId(concept != null ? concept.getId() : null);
			item.setName(concept != null ? concept.getName() : null);

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
			deductionDialog.setConcept(getConcept());
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
		void onIssueDateChange(Deduction item, Short month) {
		}

		@Override
		void onRemove(Deduction item, String expression) {
		}

		@Override
		void onRecover(Deduction item, String expression) {
		}
		

		private Deduction getConcept() {
			if (item.getName() == null)
				return null;
			for (Deduction deduction: availableDeductions)
				if (StringUtils.equals(deduction.getName(), item.getName()))
					return deduction;
			return null;
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
			int row = getRowIndex(event);
			int childs = ((CompositeBonus) item).getChilds().size();
			for (int i = 1; i <= childs; i++)
				paymentsTable.getRowFormatter().getElement(row + i).getStyle().clearDisplay();
			;
		}

		@Override
		void onCollapse(ClickEvent event) {
			int row = getRowIndex(event);
			int childs = ((CompositeBonus) item).getChilds().size();
			for (int i = 1; i <= childs; i++)
				paymentsTable.getRowFormatter().getElement(row + i).getStyle().setDisplay(Display.NONE);
			;
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
		void onIssueDateChange(Bonus item, Short month) {
		}

		@Override
		void onRemove(Bonus item, String expression) {
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
				deduction.setConceptId(item.getId());
				deduction.setDescriptionTemplate(item.getDescription());
				
			} else {
				deduction.setType(Deduction.Type.OTHER);
				deduction.setDescriptionTemplate(descriptionBox.getValue());
			}
			deduction.setScope(Scope.SALARY);
			//deduction.setDescription(descriptionBox.getValue());
			deduction.setSalaryType(salaryDraftObject.getType());
			deduction.setStartDate(salaryDraftObject.getEndDate());
			deduction.setStartDate(salaryDraftObject.getStartDate());
			

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

		class NewCRA000PaymentHandler extends NewPaymentHandler {
		
		@Override
		protected void addDrafItem(Payment payment, String expression) {
			Payment draftPayment = new Payment();
			draftPayment.setScope(Scope.SALARY);
			draftPayment.setExpression(expression);
			draftPayment.setIrpfExpression("_P");
			draftPayment.setType(Payment.Type.CRA_0000);
			draftPayment.setDescriptionTemplate(descriptionBox.getText());
			draftPayment.setEndDate(salaryDraftObject.getEndDate());
			draftPayment.setStartDate(salaryDraftObject.getStartDate());
			draftPayment.setSalaryType(salaryDraftObject.getType());

			salaryDraftObject.addDraftPayment(draftPayment);
		}

		@Override
		protected void showSuggestions(SuggestBox suggestBox, Collection<? extends Suggestion> suggestions,
				boolean isDisplayStringHTML, boolean isAutoSelectEnabled, SuggestionCallback callback) {
		}
	
		@Override
		protected void onEdit() {
			PaymentDialog paymentDialog = new PaymentDialog();
			//paymentDialog.setReadOnly(true);
			paymentDialog.setTypeListVisible();
			paymentDialog.setEnabledTypeListBox(false);
			paymentDialog.setType(Payment.Type.CRA_0000);
			paymentDialog.setNumberFormat(AON.CURRENCY_FORMAT);
			paymentDialog.setContextProvider(salaryDraftObject);

			paymentDialog.center();
			paymentDialog.show(this);

		}
	
	}

	class NewPaymentHandler extends NewItemHandler<Payment> implements PaymentDialog.Callback {

		// -------------------------------------------- NewItemHandler<Payment>

		@Override
		protected void onEdit() {
			PaymentDialog paymentDialog = new PaymentDialog();
			paymentDialog.setTypeListVisible();
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

				String clazz = "payment_concept";
//				if (StringUtils.isEmpty(payment.getName()))
//					clazz = payment.getScope() == Scope.CONTRACT ? "employee_payment" : "enterprise_payment";
//				else
//					clazz = "payment_concept";

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
		
		@ClassName("margin-top30")
		String marginTop30();
		
		String issueLabel();

		String issueTextBox();
		
		String container();
	}

	interface Binder extends UiBinder<Widget, SalaryDraft> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	static interface Template extends SafeHtmlTemplates {

		@Template("<span style=\"padding-right: 4.00em;\">{0}</span><span style=\"{1}; color: black; float:right\">{2}</span>")
		SafeHtml fiscalModelItem(String title, SafeStyles style, String message);
	}

	private static final Template template = GWT.create(Template.class);	
	
	@UiField
	FlowPanel toolbar;
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	VerticalPanel scrolledPanel;
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	Panel draftPanel;
	@UiField
	FullViewer pdfViewer;

	@UiField
	SalarySelect salarySelect;
	@UiField
	FlexTable contextTable;
	@UiField
	FlexTable eventsTable;
	@UiField
	Widget eventsTableSpace;

	@UiField
	FlexTable paymentsTable;

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
	ValueLabel employeeGroupLabel;
	@UiField
	Label dbEmployeeGroupLabel;
	@UiField
	ValueLabel employeeContractLabel;
	@UiField
	Label dbEmployeeContractLabel;
	@UiField
	ValueLabel employeeOcupationLabel;
	@UiField
	Label dbEmployeeOcupationLabel;
	@UiField
	ValueInlineHTML employeeWorkedHoursLabel;
	@UiField
	Label dbEmployeeWorkedHoursLabel;
	@UiField
	Label employeeWorkedHoursTitle;
	@UiField
	ValueInlineHTML employeeWorkedDaysLabel;
	@UiField
	Label dbEmployeeWorkedDaysLabel;
	@UiField
	Label employeeWorkedDaysTitle;
	@UiField
	ValueInlineHTML employeePartialFactorLabel;
	@UiField
	Label dbEmployeePartialFactorLabel;
	@UiField
	Label employeePartialFactorTitle;
	@UiField
	Button employeeWorkedDaysButton;
	@UiField
	Button employeeWorkedHoursButton;
	@UiField
	Button employeePartialFactorButton;
	@UiField
	Panel employeeHoursFactorDaysPanel;
	
	Widget employeePartialFactorWidget;

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
	Label ssCgcBaseLabel;
	@UiField
	ValueTextBox cgpBaseLabel;
	@UiField
	Label dbCgpBaseLabel;
	@UiField
	Label ssCgpBaseLabel;
	@UiField
	ValueLabel irpfBaseLabel;
	@UiField
	Label dbIrpfBaseLabel;
	@UiField
	ValueLabel hExtraBaseLabel;
	@UiField
	Label dbHExtraBaseLabel;
	@UiField
	Label ssHExtraBaseLabel;
	@UiField
	ValueLabel nonHExtraBaseLabel;
	@UiField
	Label dbNonHExtraBaseLabel;
	@UiField
	Label ssNonHExtraBaseLabel;
	@UiField
	ValueLabel prorationBaseLabel;
	@UiField
	Label dbProrationBaseLabel;

	@UiField
	ValueTextBox totalPaymentLabel;
	@UiField
	Label dbTotalPaymentLabel;
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
	Button settleButton;
	@UiField
	Button extraButton;
	@UiField
	Button delayButton;
	@UiField
	Button fiscalModelsButton;

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
	CheckBox notDefinedVarsCheck;
	@UiField
	CheckBox disabledPaymentsCheck;
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
	ListBox settlePreviewListBox;
	
	@UiField
	InlineLabel toolbarTitleLabel;
	
	
	@UiField
	Grid headerGrid;
	@UiField
	ScrollPanel contentScrollPanel;
	@UiField
	HorizontalPanel footerHorizontalPanel;
	
	

	private int zoom;
	private Scope scope;
	private List<HasVisibility> dbUIObjects;
	private List<HasStyleName> ssUIObjects;
	private SalaryDraftObject salaryDraftObject;
	private Map<Event.Type, String[]> eventStyles;
	private List<Bonus> availableBonus = new ArrayList<Bonus>();
	private List<Payment> availablePaymens = new ArrayList<Payment>();
	private List<Deduction> availableDeductions = new ArrayList<Deduction>();

	private HasValue<String> fxhasValue;

	private Payment totalsPayment = null;
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
	
	private List<Listener> listeners;
	
//	private boolean dummies = false;
//	private MenuItem dummiesMenuItem;
	
	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.MONTH_ABBR);

	private Timer fiscalModelsPopupTimer ;
	
	public SalaryDraft() {
		initWidget(binder.createAndBindUi(this));
		initPaymentsTable();
		initPrintPreview();
		scope = Scope.CONTRACT;
		salarySelect.addListener(this);
		showDraft();
		
		dockLayoutPanel.addStyleName(style.container());
		
		listeners = new LinkedList<>();

		zoom = Constants.DEFAULT_ZOOM;
		initEvents();
		initNotDefinedVarsCheck();
		initDisabledPaymentsCheck();
		initEventsStyles(style);
		initSalaryDb();
		initSalarySs();
		export2JS(this);
		employeePartialFactorWidget = new Label();
		cgcBaseLabel.setText(IJsonNames.PRODUCT);
		
		Window.addResizeHandler(e -> resizeContentPanel());
	}
	
	public void setToolbarTitle(String title) {
		toolbarTitleLabel.setText(title );
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	void fireSettleMessage(String message) {
		for (Listener listener : listeners)
			listener.fireSettleMessage(message);
	}
	
	public void calculate() {
		salaryDraftObject.calculate(this);
	}

	public void setSalaryDraftObject(SalaryDraftObject salaryDraftObject) {
		//info("setSalaryDraftObject");
		showDraft();
		this.salaryDraftObject = salaryDraftObject;
		onChangedSalaryDraftObject(salaryDraftObject);
	}
	
	public SalaryDraftObject getSalaryDraftObject() {
		return salaryDraftObject;
	}

	@Override
	public void onChange(SalarySelect salarySelect) {
		setFiscalModelIcon(fiscalModelsButton);
		fiscalModelsButton.setVisible(hasFiscalModels());
		delayButton.setVisible(!hasFiscalModels() && isDelay());
		extraButton.setVisible(!hasFiscalModels() && isExtra());
		settleButton.setVisible(!hasFiscalModels() && isSettle());
		salaryButton.setVisible(!hasFiscalModels() && isSalary());
		
		

		calculateAndSync();
		
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
		return Calculate.STANDARD;
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
		
		setAutomatic(  isAutomatic());
		setReadOnly(  isReadOnly());
		
		setFiscalModelIcon(fiscalModelsButton);
		fiscalModelsButton.setVisible(hasFiscalModels());
		delayButton.setVisible(!hasFiscalModels() && isDelay());
		extraButton.setVisible(!hasFiscalModels() && isExtra());
		settleButton.setVisible(!hasFiscalModels() && isSettle());
		salaryButton.setVisible(!hasFiscalModels() && isSalary());
		
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
	
	@UiHandler("fiscalModelsButton")
	void onFiscalModelsButtonOver(MouseOverEvent e) {
		if ( fiscalModelsPopupTimer  != null )
			fiscalModelsPopupTimer.cancel();
		fiscalModelsPopupTimer = new Timer () {
			@Override
			public void run() {
				ContextMenu contextMenu = new ContextMenu();
				salaryDraftObject.getFiscalModels()
				.forEach( fiscalModel -> contextMenu.addItem("Modelo " + fiscalModel.getModelFullName(), newMenuBar(fiscalModel)));
				contextMenu.showRelativeTo(fiscalModelsButton);
			}
		};
		fiscalModelsPopupTimer.schedule(600);
	}
	
	
	@UiHandler(
		{"employeeWorkedDaysButton", 
		"employeeWorkedHoursButton",
		"employeePartialFactorButton"})
	void onWorkedDaysClick(ClickEvent event) {
		EmployeeTree.showEmployeeCalendar();
	}

	@UiHandler("irpfPreviewButton")
	void onIrpfPreviewClick(ClickEvent event) {
		irpfPrint();
	}

	@UiHandler("totalLiquidLabel")
	void onLiquidChanges(ChangeEvent event) {

		Payment draftPayment = new Payment();

		String expression = totalLiquidLabel.getValue();

		draftPayment.setExpression("/*read-only*/NETO(" + (StringUtils.isBlank(expression) ? "0.00" : expression + " * DIAS_TRABAJADOS/DIAS_MES" ) + ")/**/");
		draftPayment.setScope(Scope.SALARY);
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setType(Payment.Type.DEFAULT);
		
		//totalLiquidPayment = salaryDraftObject.getPayments().stream().filter(p -> isNeto(p)).findFirst().orElseGet(null);
		
		if (totalsPayment != null) {
			draftPayment.setId(totalsPayment.getId());
			draftPayment.setDescriptionTemplate(totalsPayment.getDescriptionTemplate());

		} else {
			draftPayment.setDescriptionTemplate("SUPLEMENTO NETO");
		}

		draftPayment.setEndDate(salaryDraftObject.getEndDate());
		draftPayment.setStartDate(salaryDraftObject.getStartDate());
		draftPayment.setSalaryType(salaryDraftObject.getType());

		salaryDraftObject.addDraftPayment(draftPayment);

		salaryDraftObject.calculate(this);

		totalsPayment = draftPayment;

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

	@UiHandler("totalPaymentLabel")
	void onPaymentsChanges(ChangeEvent event) {

		Payment draftPayment = new Payment();

		String expression = totalPaymentLabel.getValue();

		draftPayment.setExpression("/*read-only*/BRUTO(" + (StringUtils.isBlank(expression) ? "0.00" : expression) + ")/**/");
		draftPayment.setScope(Scope.SALARY);
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setType(Payment.Type.DEFAULT);
		
		if (totalsPayment != null) {
			draftPayment.setId(totalsPayment.getId());
			draftPayment.setDescriptionTemplate(totalsPayment.getDescription());

		} else {
			draftPayment.setDescriptionTemplate("SUPLEMENTO BRUTO");
		}

		draftPayment.setEndDate(salaryDraftObject.getEndDate());
		draftPayment.setStartDate(salaryDraftObject.getStartDate());
		draftPayment.setSalaryType(salaryDraftObject.getType());

		salaryDraftObject.addDraftPayment(draftPayment);

		salaryDraftObject.calculate(this);

		totalsPayment = draftPayment;

	}

	@UiHandler("totalPaymentLabel")
	void onPaymentsBlur(BlurEvent event) {
		try {
			String value = totalPaymentLabel.getValue();
			totalPaymentLabel.setText(format(StringUtils.isBlank(value) ? 0 : Double.valueOf(value)));
		} catch (Exception e) {
			totalPaymentLabel.setText(format(salaryDraftObject.getTotalPayment()));
		}
	}

	@UiHandler("totalPaymentLabel")
	void onPaymentsFocus(FocusEvent event) {
		Double totalPayment = salaryDraftObject.getTotalPayment();
		totalPaymentLabel
				.setText(String.valueOf(NumberUtils.isNotValid(totalPayment) ? 0.00 : AON.round(totalPayment)));
	}
	
	@UiHandler("settlePreviewListBox")
	void onSettlePreviewChange(ChangeEvent event) {
		printSettle();
	}
	
	private void setFiscalModelIcon(Widget widget) {
		if ( salaryDraftObject == null )
			return;
		List<FiscalModel> fiscalModels = 
		salaryDraftObject.getFiscalModels();
		
		if ( fiscalModels == null || fiscalModels.isEmpty() )
			return;
		
		ADMINISTRATION_ICONS.values().forEach(widget::removeStyleName);
		
		Administration administration =
		fiscalModels.stream()
		.map(FiscalModel::getAdministration)
		.filter(Objects::nonNull)
		.reduce(Administration.COMMON_TERRITORY, (a1,a2) -> a1.compareTo(a2) <= 0 ? a1 : a2 );
		
		widget.addStyleName(ADMINISTRATION_ICONS.getOrDefault(administration, AON.AON_ICON_AET));
	}
	
	private MenuBar newMenuBar(FiscalModel fiscalModel) {
		MenuBar menuBar = new MenuBar(true);
		FiscalStatus status = fiscalModel.getStatus();
		if ( status == null ) {
			status = FiscalStatus.MISSING;
		}
		menuBar.addItem(template.fiscalModelItem("Estado", SafeStylesUtils.forFontWeight(FontWeight.BOLD), status.getName()), () -> {} );
		
		String document = fiscalModel.getDocument();
		if ( document == null ) {
			document = "";
		}
		menuBar.addItem(template.fiscalModelItem("Documento", SafeStylesUtils.forFontWeight(FontWeight.BOLD), document), () -> {} );

		Double result = AonNumberUtils.todouble(fiscalModel.getDeclarationResult()); 
		menuBar.addItem(template.fiscalModelItem("Resultado", SafeStylesUtils.forFontWeight(FontWeight.BOLD), format(result)), () -> {} );

		return menuBar;
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
		
		dbEmployeeContractLabel.setVisible(visible);
		dbEmployeeGroupLabel.setVisible(visible);
		dbEmployeeOcupationLabel.setVisible(visible);
		dbEmployeeWorkedDaysLabel.setVisible(employeeWorkedDaysLabel.isVisible() && visible);
		dbEmployeeWorkedHoursLabel.setVisible(employeeWorkedHoursLabel.isVisible() && visible);
		dbEmployeePartialFactorLabel.setVisible(employeePartialFactorLabel.isVisible() && visible);
		
		for (HasVisibility obj : dbUIObjects)
			obj.setVisible(visible);
		
		showDbTimeRulePanel();
	}

	private void setSsVisible(boolean visible) {
		ssCgcBaseLabel.setVisible(visible);
		ssCgpBaseLabel.setVisible(visible);
		ssHExtraBaseLabel.setVisible(visible);
		ssNonHExtraBaseLabel.setVisible(visible);

		for (HasVisibility obj : ssUIObjects)
			obj.setVisible(visible);
		
		//showSsTimeRulePanel();
	}

	private void showDraft() {
		deckPanel.showWidget(DRAFT_PANEL_INDEX);

		closePreviewButton.setVisible(false);
		settlePreviewListBox.setVisible(false);
		
		fxButton.setVisible(true);
		undoButton.setVisible(true);
		redoButton.setVisible(true);
		undoAllButton.setVisible(true);
		costsCheck.setVisible(true);
		salarySelect.setVisible(true);
		irpfPreviewButton.setVisible(true);
		printPreviewButton.setVisible(true);
		tgssCheck.setVisible(isSalary());
		dbSalaryCheck.setVisible(hasDbSalary());
		eventsCheck.setVisible(hasEvents());
		notDefinedVarsCheck.setVisible(true);
		disabledPaymentsCheck.setVisible(true);
		
		setFiscalModelIcon(fiscalModelsButton);
		fiscalModelsButton.setVisible(hasFiscalModels());
		delayButton.setVisible(!hasFiscalModels() && isDelay());
		extraButton.setVisible(!hasFiscalModels() && isExtra());
		settleButton.setVisible(!hasFiscalModels() && isSettle());
		salaryButton.setVisible(!hasFiscalModels() && isSalary());

		acceptButton.setEnabled(hasDrafts() && !isAutomatic() );
	}

	private void showPreview() {
		deckPanel.showWidget(PDF_VIEWER_INDEX);
		
		closePreviewButton.setVisible(true);
		settlePreviewListBox.setVisible(isSettle());


		fxButton.setVisible(false);
		costsCheck.setVisible(false);
		salarySelect.setVisible(false);
		tgssCheck.setVisible(false);
		eventsCheck.setVisible(false);
		dbSalaryCheck.setVisible(false);
		irpfPreviewButton.setVisible(false);
		printPreviewButton.setVisible(false);
		notDefinedVarsCheck.setVisible(false);
		disabledPaymentsCheck.setVisible(false);
		
	}

	private void showIrpfPreview() {
		deckPanel.showWidget(PDF_VIEWER_INDEX);

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
		fiscalModelsButton.setVisible(false);
		tgssCheck.setVisible(false);
		dbSalaryCheck.setVisible(false);
		irpfPreviewButton.setVisible(false);
		printPreviewButton.setVisible(false);
		settlePreviewListBox.setVisible(false);
		notDefinedVarsCheck.setVisible(false);
		disabledPaymentsCheck.setVisible(false);


	}

	boolean isPreviewVisible() {
		return deckPanel.getVisibleWidgetIndex() == PDF_VIEWER_INDEX;
	}

	protected boolean hasDrafts() {
		return salaryDraftObject == null ? false : salaryDraftObject.hasDrafts();
	}

	protected boolean isSettle() {
		return salaryDraftObject == null ? false : salaryDraftObject.getType() == Type.SETTLE;
	}

	private boolean isReadOnly() {
		return salaryDraftObject == null ? false : Arrays.asList(/*Type.EXTRA,*/ Type.DELAY).contains(salaryDraftObject.getType());
	}

	private boolean isAutomatic() {
		return salaryDraftObject == null ? false : Arrays.asList(Type.EXTRA, Type.DELAY, Type.SETTLE).contains(salaryDraftObject.getType());
	}


	private void onChangedSalaryDraftObject(SalaryDraftObject salaryDraftObject) {
		
		calculateAndSync();
			

		syncSalarySelect();

		// clean...???
		cgcBaseDeduction = null;
		cgpBaseDeduction = null;

		// Sync undo & redo controls
		salaryDraftObject.addUndoManagerListener(this);
		
		redoButton.setEnabled(false);
		undoButton.setEnabled(false);
		acceptButton.setEnabled(false);
		undoAllButton.setEnabled(false);

	}

	protected void calculateAndSync() {
		salaryDraftObject.calculate(new CalculateCallback() {
			
			@Override
			public Calculate getCalculate() {
				return SalaryDraft.this.getCalculate();
			}
			
			@Override
			public void onCalculateSucces(SalaryDraftObject object) {
				SalaryDraft.this.onCalculateSucces(object);
//				if ( isCostsVisible() )
//					SalaryDraft.this.salaryDraftObject.synchronize(SalaryDraft.this);
			}
			
			@Override
			public void onCalculateFailure(Throwable throwable) {
				SalaryDraft.this.onCalculateFailure(throwable);
			}
		});
	}

	private Boolean isCostsVisible() {
		return costsCheck.getValue();
	}
	

	private void dumpSalaryDraft(boolean displayChanges) {

		enterpriseCCCLabel.setText(salaryDraftObject.getEnterpriseCCC());

		employeeSSLabel.setText(salaryDraftObject.getEmployeeSS());
		employeeNameLabel.setText(salaryDraftObject.getEmployeeName());
		employeeDocumentLabel.setText(salaryDraftObject.getEmployeeDocument());
		employeeSeniorityLabel.setText(format(salaryDraftObject.getEmployeeSeniorityDate()) );
		employeeAgreementCategoryLabel.setText(salaryDraftObject.getEmployeeAgreementCategory());

		Date startDate = salaryDraftObject.getStartDate();
		Date endDate = salaryDraftObject.getEndDate();
		periodLabel.setText(format(startDate) + " - " + format(endDate));
		daysLabel.setText(Integer.toString(salaryDraftObject.getTimeUnits()));

		Double cgcBase = salaryDraftObject.getCgcBase();
		cgcBaseLabel.setText(format(cgcBase), displayChanges);
		dbCgcBaseLabel.setText(format(salaryDraftObject.getDbCgcBase()));
		setDbStyleName(dbCgcBaseLabel, cgcBaseLabel);
		ssCgcBaseLabel.setText(format(salaryDraftObject.getSsCgcBase()));
		setDbStyleName(ssCgcBaseLabel, cgcBaseLabel);

		Double cgpBase = salaryDraftObject.getCgpBase();
		cgpBaseLabel.setText(format(cgpBase), displayChanges);
		dbCgpBaseLabel.setText(format(salaryDraftObject.getDbCgpBase()));
		setDbStyleName(dbCgpBaseLabel, cgpBaseLabel);
		ssCgpBaseLabel.setText(format(salaryDraftObject.getSsCgpBase()));
		setDbStyleName(ssCgpBaseLabel, cgpBaseLabel);

		irpfBaseLabel.setText(format(salaryDraftObject.getIrpfBase()), displayChanges);
		dbIrpfBaseLabel.setText(format(salaryDraftObject.getDbIrpfBase()));
		setDbStyleName(dbIrpfBaseLabel, irpfBaseLabel);
		hExtraBaseLabel.setText(format(salaryDraftObject.gethExtraBase()), displayChanges);
		dbHExtraBaseLabel.setText(format(salaryDraftObject.getDbHExtraBase()));
		setDbStyleName(dbHExtraBaseLabel, hExtraBaseLabel);
		ssHExtraBaseLabel.setText(format(salaryDraftObject.getSsHExtraBase()));
		setDbStyleName(ssHExtraBaseLabel, hExtraBaseLabel);
		nonHExtraBaseLabel.setText(format(salaryDraftObject.getNonHExtraBase()), displayChanges);
		dbNonHExtraBaseLabel.setText(format(salaryDraftObject.getDbNonHExtraBase()));
		setDbStyleName(dbNonHExtraBaseLabel, nonHExtraBaseLabel);
		ssNonHExtraBaseLabel.setText(format(salaryDraftObject.getSsNonHExtraBase()));
		setDbStyleName(ssNonHExtraBaseLabel, nonHExtraBaseLabel);
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
		
		employeeContractLabel.setText(getValueOf("TC2"));
		employeeContractLabel.setTitle(getTitleOfTC2());
		dbEmployeeContractLabel.setText(getDbValueOf("TC2"));
		setDbStyleName(dbEmployeeContractLabel, employeeContractLabel);
		
		employeeOcupationLabel.setText(getValueOf("OCUPACION"));
		employeeOcupationLabel.setTitle(getTitleOf("OCUPACION",Employee.Occupation.class));
		dbEmployeeOcupationLabel.setText(getDbValueOf("OCUPACION"));
		setDbStyleName(dbEmployeeOcupationLabel, employeeOcupationLabel);
		
		employeeGroupLabel.setText(getValueOf("GRUPO_COTIZACION"));
		dbEmployeeGroupLabel.setText(getDbValueOf("GRUPO_COTIZACION"));
		setDbStyleName(dbEmployeeGroupLabel, employeeGroupLabel);
		
		boolean hoursBase = getValuesOf("BASE_HORARIA").anyMatch(Boolean::valueOf);

		double workHours = getValuesOf("HORAS_TRABAJADAS").collect(Collectors.summingDouble( AonNumberUtils::todouble));
		employeeWorkedHoursLabel.setText(formatValue(workHours));
		employeeWorkedHoursLabel.setVisible(isSalary() && hoursBase );
		employeeWorkedHoursTitle.setVisible(employeeWorkedHoursLabel.isVisible());
		employeeWorkedHoursButton.setVisible(employeeWorkedHoursLabel.isVisible() );
		double dbWorkHours = getDbValuesOf("HORAS_TRABAJADAS").collect(Collectors.summingDouble( AonNumberUtils::todouble));
		dbEmployeeWorkedHoursLabel.setText(formatValue(dbWorkHours));
		setDbStyleName(dbEmployeeWorkedHoursLabel, employeeWorkedHoursLabel);
		
		Variable partialFactorsVars [] = getVariablesOf("COEFICIENTE_PARCIALIDAD").toArray(Variable[]::new);

		double partialFactor = 
		Arrays.stream(partialFactorsVars)
		.flatMapToDouble( v -> DoubleStream.generate(()-> AonNumberUtils.todouble(v.getValue())).limit( DateUtils.getDaysBetween(v.getStartDate(), v.getEndDate())+1l) )
		.average().orElse(1.00);
		
		variableChangeHandlers = new ArrayList<VariableChangeHandler<?>>();

		employeePartialFactorWidget.removeFromParent();
		employeePartialFactorWidget = (partialFactor != 1.00  && partialFactorsVars.length == 1) ? getVariableWidget(partialFactorsVars[0], partialFactorsVars[0].getScope(), true ): new Label();
		employeePartialFactorWidget.setVisible(isSalary() && !hoursBase  && partialFactor != 1.00 && partialFactorsVars.length == 1); 
		employeeHoursFactorDaysPanel.add(employeePartialFactorWidget);
		
		employeePartialFactorLabel.setText(formatValue(partialFactor));
		employeePartialFactorLabel.setVisible(isSalary() && !hoursBase  && partialFactor != 1.00 && partialFactorsVars.length > 1 );
		employeePartialFactorTitle.setVisible(employeePartialFactorLabel.isVisible() );
		employeePartialFactorButton.setVisible(employeePartialFactorLabel.isVisible() );

		double workDays = getValuesOf("DIAS_TRABAJADOS").collect(Collectors.summingDouble( AonNumberUtils::todouble));
		employeeWorkedDaysLabel.setText(formatValue(workDays));
		employeeWorkedDaysLabel.setVisible(isSalary() && !hoursBase  && partialFactor == 1.00 && workDays > 0 );
		employeeWorkedDaysTitle.setVisible(employeeWorkedDaysLabel.isVisible());
		employeeWorkedDaysButton.setVisible(employeeWorkedDaysLabel.isVisible());
		double dbWorkDays = getDbValuesOf("DIAS_TRABAJADOS").collect(Collectors.summingDouble( AonNumberUtils::todouble));
		dbEmployeeWorkedDaysLabel.setText(formatValue(workDays));
		setDbStyleName(dbEmployeeWorkedDaysLabel, employeeWorkedDaysLabel);
		
		String seniorityYears = 
		getValuesOf("A\u00D1OS_ANTIGUEDAD").distinct().map(AonNumberUtils::todouble).filter( d -> d > 0)
		.sorted().map( d -> Integer.toString(d.intValue()) ).collect(Collectors.joining(","));
		if ( AonStringUtils.equals("1", seniorityYears) ) {
			employeeSeniorityLabel.setText(employeeSeniorityLabel.getText() + "  ( " + seniorityYears + " A\u00D1O )");
		}else if ( AonStringUtils.isNotBlank(seniorityYears) ) {
			employeeSeniorityLabel.setText(employeeSeniorityLabel.getText() + "  ( " + seniorityYears + " A\u00D1OS )");
		}
			
		//salaryDraftObject.getContext().forEach( v -> info(v.getName() + " = " + v.getValue()));

		clearDbWidgets();
		clearSsWidgets();
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
		//insertBlankPaymentRow();
		
		Scope nextScope = null;
		boolean show = false; //scope.compareTo(Scope.CONTRACT) >= 0;


		List<Variable> context = getContext(salaryDraftObject);
		List<Variable> variables = context.stream()
				.filter(v->!skipVariable(v))
				.filter( v-> !alreadyDisplayed(v) )
				//.filter(v->!isPaymentVariable(v))
				.collect(Collectors.toList());
		//List<Variable> constants = getConstants(context);
		
		
		List<Variable> visibleContext  = new ArrayList<Variable>();
		//visibleContext.addAll(constants);
		visibleContext.addAll(variables);
		/* employeePartialFactorWidget */
		if ( partialFactor != 1.00  && !employeePartialFactorWidget.isVisible()) {
			List<Variable> partialVariables = getVariablesOf("COEFICIENTE_PARCIALIDAD").collect(Collectors.toList()); 
			visibleContext.removeAll(partialVariables);
			visibleContext.addAll(partialVariables.stream().map( v -> DelegateVariable.getVariable(v, Scope.CONTRACT)).collect(Collectors.toList()));
		}
		
		
		
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
		if (isCostsVisible()) 
			showCosts(true);
		
		initEventsCheck();
		dumpEvents(salaryDraftObject.getEvents());
		// Events visible
		eventsTable.setVisible(eventsCheck.isVisible() && eventsCheck.getValue());
		eventsTableSpace.setVisible(eventsTable.isVisible()/*eventsTable.getRowCount() > 0*/);
		showPaymentsEvents(eventsTable.isVisible());
		
		
		resizeContentPanel();
	}

	public Stream<String> getValuesOf(String name) {
		return salaryDraftObject.getContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase(name, v.getName()))
		.map(Variable::getValue)
		.filter(Objects::nonNull)
		.map(String::valueOf )
		;
	}

	public Stream<String> getDbValuesOf(String name) {
		return salaryDraftObject.getContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase(name, v.getName()))
		.map(Variable::getValue)
		.filter(Objects::nonNull)
		.map(String::valueOf )
		;
	}

	public Stream<Variable> getVariablesOf(String name) {
		return salaryDraftObject.getContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase(name, v.getName()))
		.filter( v -> v.getValue() != null)
		;
	}

	public Stream<Variable> getDraftVariablesOf(String name) {
		return salaryDraftObject.getDrafContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase(name, v.getName()))
		;
	}

	public String getValueOf(String name) {
		return salaryDraftObject.getContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase(name, v.getName()))
		.map(Variable::getValue)
		.filter(Objects::nonNull)
		.map(String::valueOf )
		.distinct()
		.collect(Collectors.joining(","))
		;
	}

	public String getDbValueOf(String name) {
		return salaryDraftObject.getDbContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase(name, v.getName()))
		.map(Variable::getValue)
		.filter(Objects::nonNull)
		.map(String::valueOf )
		.distinct()
		.collect(Collectors.joining(","))
		;
	}

	public <T extends Enum<T> & HasDescription > String getTitleOf(String name, Class<T> enumType) {
		return salaryDraftObject.getContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase(name, v.getName()))
		.map(Variable::getValue)
		.filter(Objects::nonNull)
		.map(String::valueOf )
		.map(v -> valueOf(enumType, v))
		.filter(Objects::nonNull)
		.map( t -> t.getDescription())
		.collect(Collectors.joining(","))
		;
	}

	public String getDbValueOf(Variable variable) {
		return salaryDraftObject.getDbContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase(variable.getName(), v.getName()))
		.filter(v ->  intersects(variable,  v))
		.map(Variable::getValue)
		.filter(Objects::nonNull)
		.map( v -> format(v, variable))
		.distinct()
		.collect(Collectors.joining(","))
		;
	}

	public String getTitleOfTC2() {
		return salaryDraftObject.getContext().stream()
		.filter(v-> AonStringUtils.equalsIgnoreCase("TC2", v.getName()))
		.map(Variable::getValue)
		.filter(Objects::nonNull)
		.map(String::valueOf )
		.map(Employee.TC2::getDescriptionByCode)
		.filter(AonStringUtils::isNotBlank)
		.collect(Collectors.joining(","))
		;
	}

	private void onHideShowNotDefinedVars() {
		contextTable.clear();
		contextTable.removeAllRows();
		
		Scope nextScope = null;
		boolean show = false; 
		
		List<Variable> context = getContext(salaryDraftObject);
		List<Variable> variables = context.stream()
				.filter(v->!skipVariable(v))
//				.filter(v->!isPaymentVariable(v))
				.collect(Collectors.toList());
		List<Variable> constants = getConstants(context);
		
		List<Variable> visibleContext  = new ArrayList<Variable>();
		visibleContext.addAll(constants);
		visibleContext.addAll(variables);
		
		for (Scope step : SCOPE_STEPS) {
			nextScope = dumpContext(visibleContext, step, show, nextScope);
			if (step.compareTo(scope) <= 0)
				break;
		}
	}

	private void onHideShowDisabledPayments() {
		boolean showDisablePayments = disabledPaymentsCheck.getValue();
		paymentChangeHandlers.forEach(p -> p.setVisible(showDisablePayments || isEnabled(p.item)));
	}

	private void initTgssCheck(){
		// clean old styles 
		tgssCheck.removeStyleName(style.textOk());
		tgssCheck.removeStyleName(style.textWarn());
		tgssCheck.removeStyleName(style.textError());
		
		tgssCheck.setVisible(isSalary());
		
		Double diffs = getDiffsWithSsSalary();
		if ( diffs == null )
			; 
		else if ( diffs == 0.00 )
			tgssCheck.addStyleName(style.textOk());
		else if (diffs < 0.20)
			tgssCheck.addStyleName(style.textWarn());
		else 
			tgssCheck.addStyleName(style.textError());
		
	}


	private void initEventsCheck(){
		eventsCheck.setVisible(hasEvents());
		eventsCheck.setValue(hasAonInfoEvents());
	}
	
	

	private void initDbSalaryCheck(){
		dbSalaryCheck.setVisible(hasDbSalary());
		Widget dbDiffWidget = getDiffsWithDbSalary();
		boolean hasDiffsWithDbSalary = dbDiffWidget != null;
		//dbSalaryCheck.setValue(hasDiffsWithDbSalary, false);
		dbSalaryCheck.addStyleName(hasDiffsWithDbSalary ? style.textError() : style.textOk());
		dbSalaryCheck.removeStyleName(hasDiffsWithDbSalary ? style.textOk() : style.textError());
		//if ( hasDiffsWithDbSalary )
		//	scrollPanel.ensureVisible(dbDiffWidget);
		
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
				availablePaymens.addAll(filterPayments(result, Scope.AGREEMENT));
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

	@UiHandler("extraButton")
	void onExtraButtonClick(ClickEvent event) {
		salaryDraftObject.emitSalary(SalaryDraft.this);
	}

	@UiHandler("settleButton")
	void onSettleButtonClick(ClickEvent event) {
		salaryDraftObject.emitSalary(new CalculateCallback() {
			
			@Override
			public void onCalculateSucces(SalaryDraftObject salaryDraftObject) {
				String message = "El fichero Certific@2 se ha generado correctmente. Para poder visualizarlo y comunicarlo dirijase a: Contratos > " + salaryDraftObject.getEmployeeName() + " > Mas > Cetific@2";
				fireSettleMessage(message);
			}
			
			@Override
			public void onCalculateFailure(Throwable throwable) {
				fireSettleMessage(throwable.getMessage());
			}
			
			@Override
			public Calculate getCalculate() {
				return null;
			}
		});
	}
	
	@UiHandler("salaryButton")
	void onSalaryButtonClick(ClickEvent event) {

		salaryDraftObject.save(new CalculateCallback() {

			@Override
			public Calculate getCalculate() {
				return SalaryDraft.this.getCalculate();
			}
			@Override
			public void onCalculateSucces(SalaryDraftObject object) {
				SalaryDraft.this.onCalculateSucces(object);
				SalaryDraft.this.salaryDraftObject.emitSalary(SalaryDraft.this);
			}

			@Override
			public void onCalculateFailure(Throwable throwable) {
				SalaryDraft.this.onCalculateFailure(throwable);
			}
		});
			
	}

	@UiHandler("delayButton")
	void onDelayButtonClick(ClickEvent event) {
		
		List<Variable> irpfPercentDraftVars =
		getDraftVariablesOf(PORCENTAJE_IRPF)
		.collect(Collectors.toList()); 
		salaryDraftObject.getDrafContext().removeAll(irpfPercentDraftVars);
		
		salaryDraftObject.save(new CalculateCallback() {

			@Override
			public Calculate getCalculate() {
				return SalaryDraft.this.getCalculate();
			}
			@Override
			public void onCalculateSucces(SalaryDraftObject object) {
				//SalaryDraft.this.onCalculateSucces(object);
				salaryDraftObject.getDrafContext().addAll(irpfPercentDraftVars);
				SalaryDraft.this.salaryDraftObject.emitSalary(SalaryDraft.this);
			}

			@Override
			public void onCalculateFailure(Throwable throwable) {
				SalaryDraft.this.onCalculateFailure(throwable);
			}
		});
			
	}


	@UiHandler("tgssCheck")
	void onTgssCheckChanged(ValueChangeEvent<Boolean> event) {
		showTimeRulePanel();
		showDbTimeRulePanel();
		setSsVisible(hasSsSalary() && event.getValue());
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
	private void initSalarySs() {
		ssUIObjects = new LinkedList<HasStyleName>();
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
	
	private void initNotDefinedVarsCheck() {
		notDefinedVarsCheck.addValueChangeHandler(e -> onHideShowNotDefinedVars());
	}

	private void initDisabledPaymentsCheck() {
		disabledPaymentsCheck.addValueChangeHandler(e -> onHideShowDisabledPayments());
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
		paymentsTable.getColumnFormatter().setWidth(1, "13%"); // CUANTIA
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
//		if ( isCostsVisible()) 
//			salaryDraftObject.synchronize(this);
	}
	
	//@UiHandler("saveButton")
	void onDownloadClick(ClickEvent event) {
		String fileName = 
				salaryDraftObject.getEmployeeName() + " " 
				+ DateTimeFormat.getFormat(PredefinedFormat.MONTH).format(salaryDraftObject.getChargeDate())
				+".pdf";
		// ***************************
		// ***************************
//		pdfViewer.download(fileName);
		// ***************************
		// ***************************
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
		
	}

	private void clearDbWidgets() {
		dbUIObjects.clear();
	}

	private void clearSsWidgets() {
		ssUIObjects.clear();
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

	private void addSsWidget(HasStyleName widget) {
		ssUIObjects.add(widget);
	}

	private void dumpEvents(List<Event> events) {

		int row = eventsTable.getRowCount();
		for (Event event : events) {
			dumpEvent(row++, event);
		}

	}

	private void dumpEvent(int row, Event event) {
		Button headButton = new Button();
		
		headButton.setStyleName( 
		isAonInfoEvent(event) ? AON.AON_ICON_INFO: AON.AON_ICON_EXCEPTION);
		
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
	
	private boolean isAonInfoEvent(Event event) {
		return event.getType() == Event.Type.INFO 
		|| ( event.getMessage() != null  && event.getMessage().contains("Solutions") );		
	}

	private List<PaymentChangeHandler<?>> dumpPayments(List<Payment> payments) {
		
		List<PaymentChangeHandler<?>> handlers = new ArrayList<PaymentChangeHandler<?>>(payments.size());

		for (Payment payment : payments) {

			int row = paymentsTable.getRowCount();

			Event event = getEvent4(payment);
			
			if (payment.getAmount() != null && event == null) {

				PaymentChangeHandler<TextBox> handler = newPaymentChangeHandler(payment);
				dumpPayment(payment, row, getIconRowStyle(payment), handler);

				handlers.add(handler);
				handler.setHasVisibility(new VisibilityImpl(paymentsTable.getRowFormatter().getElement(row)));
				handler.setVisible(disabledPaymentsCheck.getValue() || isEnabled(payment));

			} else if (payment.getId() != null ) {
				PaymentChangeHandler<TextBox> handler = newPaymentChangeHandler(payment);
				String styles[] = eventStyles.get(event == null ? Event.Type.WARNING : event.getType());
				dumpPayment(payment, row, getIconRowStyle(payment), handler);
				handlers.add(handler);
				handler.setHasVisibility(new VisibilityImpl(paymentsTable.getRowFormatter().getElement(row)));
				handler.setVisible(disabledPaymentsCheck.getValue() || isEnabled(payment));
			} else {
				String styles[] = eventStyles.get(event == null ? Event.Type.ERROR : event.getType());
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
		NewPaymentHandler newPaymentHandler = newPaymentHandler();
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

	private NewPaymentHandler newPaymentHandler() {
		return salaryDraftObject.getType().accept(
			new TypeVisitor<NewPaymentHandler>() {

			@Override
			public NewPaymentHandler visitSalary(Type type) {
				return new NewPaymentHandler();
			}

			@Override
			public NewPaymentHandler visitExtra(Type type) {
				return new NewCRA000PaymentHandler();
			}

			@Override
			public NewPaymentHandler visitSettle(Type type) {
				return new NewCRA000PaymentHandler();
			}

			@Override
			public NewPaymentHandler visitDelay(Type type) {
				return new NewPaymentHandler();
			}

		});
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

		if (deduction.getAmount() != null) {
			Double percent = getPercent(deduction, salaryDraftObject);
			dumpSystemDeduction(deduction, percent, deduction.getDescription(), row++, expandButton);

			if (deduction instanceof CompositeDeduction) {
				for (Deduction child : ((CompositeDeduction) deduction).getChilds()) {

					dumpSystemItem(child,
							"  " + child.getDescription(),
							row, null, null);

					paymentsTable.getRowFormatter().getElement(row++).getStyle().setDisplay(Display.NONE);
				}
			}

		} else {
			Double dbPercent = getDbPercent(deduction, salaryDraftObject);
			String styles[] = eventStyles.get(Event.Type.ERROR);
			dumpDbSystemDeduction(deduction, dbPercent, deduction.getDescription(), row++, styles[0], styles[1]);
		}
		return row;
	}

	private int dumpSystemBonus(int row, Bonus bonus, Button expandButton ) {
		
		String description = bonus.getDescription();

		if (bonus.getAmount() != null) {
			Double percent = getPercent(bonus, salaryDraftObject);
			dumpSystemBonus(bonus, percent, description, row++, expandButton);
		} 

		return row;
	}
	
	private void dumpBonus(int row, Bonus bonus, Button expandButton) {
		String description = bonus.getDescription();
		//info("Dump Bonus '"+ description + "' " + DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM).format(bonus.getStartDate()));
		dumpItem(bonus, row, description, getIconRowStyle(bonus), new BonusChangeHandler<TextBox>(bonus), true, null, expandButton, true);
	}

	private void dumpBonus(int row, Bonus bonus, String iconStyleName , Button expandButton) {
		String description = bonus.getDescription();
		dumpItem(bonus, row, description, iconStyleName, new BonusChangeHandler<TextBox>(bonus), true, null, expandButton, true);
	}

	private void dumpPayment(Payment payment, int row, String iconStyleName,
			ItemChangeHandler<TextBox, Payment> handler) {
		boolean editable = 
		isDefault(payment)
		|| ( !isAutoGenerated(payment) 
			&& isContract(payment) 
			&& !isFromAgreemen(payment) );
		
		dumpPayment(payment, row, iconStyleName, handler, editable );
	}


	private void dumpPayment(Payment payment, int row, String iconStyleName,
			ItemChangeHandler<TextBox, Payment> handler, boolean isEditable) {

		Widget quoteTextBox = null;

		quoteTextBox = newQuoteTextBox(payment, row);
		
		
		if ( isNeto(payment)) {
			totalsPayment = payment;
			quoteTextBox = newTextBox("NETO");
			((HasClickHandlers)quoteTextBox).addClickHandler((e) -> totalLiquidLabel.setFocus(true));
		}

		if ( isBruto(payment)) {
			totalsPayment = payment;
			quoteTextBox = newTextBox("BRUTO");
			((HasClickHandlers)quoteTextBox).addClickHandler((e) -> totalPaymentLabel.setFocus(true));
		}

		Button expandButton = null;
		if (payment instanceof CompositePayment) {
			expandButton = new Button();
			expandButton.setTabIndex(Short.MAX_VALUE);
			expandButton.setStyleName(AON.AON_ICON_EXPAND);
			expandButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		}
		
		

		if (payment instanceof CompositePayment) {
			dumpItem(payment, row, iconStyleName, handler, false, quoteTextBox, expandButton, isEditable);
			for (Payment child : ((CompositePayment) payment).getChilds()) {
				child.setDescription(formatChildDescription(child, salaryDraftObject));
				dumpChildPayment(child, ++row, iconStyleName );
				paymentsTable.getRowFormatter().getElement(row).getStyle().setDisplay(Display.NONE);
			}
		} else {
			String description = payment.getDescription();			
			dumpItem(payment, row, description, iconStyleName, handler, false, quoteTextBox, expandButton, isEditable);
		}
		
		ensureDebugId(paymentsTable.getRowFormatter().getElement(row), "payment-row-" + row);
		
	}

	private void dumpChildPayment(Payment childPayment, int row, String iconStyleName) {

		Widget quoteTextBox = null;

		quoteTextBox = newQuoteTextBox(childPayment, row);
		
		//payment.setType(Payment.Type.CRA_0000);
		consoleLog(childPayment.getDescription() + " / " + childPayment.getType());
		
		Button expandButton = new Button();
		expandButton.setEnabled(false);
		expandButton.setTabIndex(Short.MAX_VALUE);
		expandButton.setStyleName(iconStyleName);
		expandButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		
		PaymentChangeHandler<TextBox> paymentChangeHandler = newPaymentChangeHandler(childPayment);

		dumpItem(childPayment, row, childPayment.getDescription(), AON.AON_ICON_BLANK, paymentChangeHandler, false, quoteTextBox, expandButton, false, false);

		ensureDebugId(paymentsTable.getRowFormatter().getElement(row), "payment-row-" + row);
		
	}
	
	private PaymentChangeHandler<TextBox> newPaymentChangeHandler(Payment payment){
		if ( payment instanceof CompositePayment ) {
			return new PaymentChangeHandler<TextBox>(payment);
		}
		
		String variableName = getImplicitVariableName(payment.getExpression());
		if ( variableName == null ) {
			return new PaymentChangeHandler<TextBox>(payment);
		}
		
		return new VariablePaymentChangeHandler<TextBox>(payment, variableName);
	}
	
	public native void consoleLog(String msg) /*-{
		console.log(msg);
	}-*/;
	


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
		dumpItem(item, row, item.getDescription(), iconStyleName, handler, isDeduction, labelWidget, expandButton, isEditable);
	}
	
	private <I extends Item> void dumpItem(I item, int row, String description, String iconStyleName, ItemChangeHandler<TextBox, I> handler,
			boolean isDeduction, Widget labelWidget, Button expandButton, boolean isEditable) {
		dumpItem(item, row, description, iconStyleName, handler, isDeduction, labelWidget, expandButton, isEditable, true);
	}

	private <I extends Item> void dumpItem(I item, int row, String description, String iconStyleName, ItemChangeHandler<TextBox, I> handler,
			boolean isDeduction, Widget labelWidget, Button expandButton, boolean isEditable, boolean isRemovable) {

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
		
		//editButton.setEnabled(isEditable);

		if (labelWidget == null)
			paymentsTable.setHTML(row, 1, "&nbsp;");
		else
			paymentsTable.setWidget(row, 1, labelWidget);

		TextBox descriptionBox = new TextBox();
		enable(descriptionBox, isEditable);
		//String description = item.getDescription();
		descriptionBox.setText(description != null ? description : item.getDescriptionTemplate());
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		descriptionBox.getElement().setAttribute("size", Integer.toString(DESCRIPTION_SIZE));
		descriptionBox.setMaxLength(DESCRIPTION_MAX_LENGTH);
		
		handler.setDescriptionWidget(descriptionBox);
		descriptionBox.ensureDebugId("description-box-" + row );
		if(
		!isExtra() &&
		!isSettle() &&
		!isReadOnly() && 
		handler != null && 
		iconStyleName != AON.AON_ICON_BLANK &&
		(
		item.getType() == Payment.Type.CRA_0004 
		|| item.getType() == Payment.Type.CRA_0005 )
		) {
			paymentsTable.setWidget(row, 2, createSpecialPay(item, descriptionBox, row, handler));
		}else {
			paymentsTable.setWidget(row, 2, descriptionBox);
		}
		
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
		amountsPanel.getElement().getStyle().setWidth(100, Unit.PCT);
		amountsPanel.add(amountBox);
		amountsPanel.add(dbAmountLabel);
		amountsPanel.setCellWidth(dbAmountLabel, "50%");
		amountsPanel.setCellHorizontalAlignment(dbAmountLabel, HorizontalAlignmentConstant.startOf(Direction.RTL));

		VisibilityImpl dbWidget = new VisibilityImpl(dbAmountLabel.getElement().getParentElement());
		dbWidget.setVisible(salaryDraftObject.hasDbSalary() && dbSalaryCheck.getValue());
		addDbWidget(dbWidget);

//		InlineLabel ssAmountLabel = new InlineLabel();
//		ssAmountLabel.setText(format(item.getSsAmount()));
//		ssAmountLabel.setVisible(salaryDraftObject.hasSsSalary());
//		ssAmountLabel.addStyleName(AON.AON_TEXT_RIGHT);
//		setDbStyleName(ssAmountLabel, amountBox.getText(), ssAmountLabel.getText());
//		amountBox.ensureDebugId("ss-amount-label-" + row );
//		amountsPanel.add(ssAmountLabel);
//		amountsPanel.setCellWidth(ssAmountLabel, "50%");
//		VisibilityImpl ssWidget = new VisibilityImpl(ssAmountLabel.getElement().getParentElement());
//		ssWidget.setVisible(salaryDraftObject.hasSsSalary() && tgssCheck.getValue());
//		addSsWidget(ssWidget);

		paymentsTable.setWidget(row, isDeduction ? 4 : 3, amountsPanel);
		paymentsTable.getCellFormatter().addStyleName(row, isDeduction ? 4 : 3, AON.AON_TEXT_RIGHT);

		paymentsTable.setHTML(row, isDeduction ? 3 : 4, "&nbsp;");

		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		buttonsPanel.getElement().getStyle().setWidth(100, Unit.PCT);
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		Scope itemScope = item.getScope();
		
		
		if ( isExtra() 
			/*|| isSettle() */ ) {
			// 
		}
		else if ( !isRemovable ) {
			
		}
		else if ( isExtra() 
				&& itemScope.compareTo(Scope.SALARY) < 0 ) {
			//info("SETREMOVEEXTRABUTTON:" + item.getDescription() + "," + item.getExpression() +", " + item.getScope() );
			Button deleteButton = getEnableButton();
			deleteButton.setTabIndex(Short.MAX_VALUE);
			buttonsPanel.add(deleteButton);
			handler.setDeleteButton(deleteButton);
			deleteButton.ensureDebugId("agreement-button-" + row );
		}
		else if (itemScope.compareTo(Scope.AGREEMENT) > 0 
			&& item.isDefinedAt(Scope.AGREEMENT)
			&& isRemove(item) ) {
			//info("SETDISABLEBUTTON:" + item.getDescription() + "," + item.getExpression() +", " + item.getScope());
			Button agreementButton = getDisableButton();
			agreementButton.setTabIndex(Short.MAX_VALUE);
			buttonsPanel.add(agreementButton);
			handler.setDisableButton(agreementButton);
			agreementButton.ensureDebugId("agreement-button-" + row );
			paymentsTable.getRowFormatter().addStyleName(row, "aon-Disabled");
		}
		else if (itemScope.compareTo(Scope.AGREEMENT) == 0 
				&& isDisabled(item) ) {
			//info("SETDISABLEAGREEMENTBUTTON:" + item.getDescription() + "," + item.getExpression() +", " + item.getScope());
			Button agreementButton = getDisableButton();
			agreementButton.setTabIndex(Short.MAX_VALUE);
			buttonsPanel.add(agreementButton);
			handler.setDisableAgreementButton(agreementButton);
			agreementButton.ensureDebugId("agreement-button-" + row );
			paymentsTable.getRowFormatter().addStyleName(row, "aon-Disabled");
		}
		else if (item.isDefinedAt(Scope.AGREEMENT) &&
			itemScope.compareTo(Scope.AGREEMENT) > 0) {
			//info("SETENABLEBUTTON:" + item.getDescription() + "," + item.getExpression() +", " + item.getScope() );
			Button agreementButton = getEnableButton();
			agreementButton.setTabIndex(Short.MAX_VALUE);
			buttonsPanel.add(agreementButton);
			handler.setEnableButton(agreementButton);
			//handler.setEnableAgreementButton(agreementButton);
			agreementButton.ensureDebugId("agreement-button-" + row );

		} else if (item.isDefinedAt(Scope.AGREEMENT) ||
			itemScope.compareTo(Scope.AGREEMENT) == 0) {
			//info("SETENABLEAGREEMENTBUTTON:" + item.getDescription() + "," + item.getExpression() +", " + item.getScope());
			Button agreementButton = getEnableButton();
			agreementButton.setTabIndex(Short.MAX_VALUE);
			buttonsPanel.add(agreementButton);
			handler.setEnableButton(agreementButton);
			agreementButton.ensureDebugId("agreement-button-" + row );
		} 
		else if (isHideable(item) ) {
			//info("SETHIDEBUTTON:" + item.getDescription() + "," + item.getExpression() +", " + item.getScope());
			Button hideButton = getEnableButton();
			hideButton.setTabIndex(Short.MAX_VALUE);
			buttonsPanel.add(hideButton);
			handler.setHideButton(hideButton);
			hideButton.ensureDebugId("agreement-button-" + row );
		} else {
			Button deleteButton = new Button();
			deleteButton.setTabIndex(Short.MAX_VALUE);
			deleteButton.setStyleName(AON.AON_ICON_DELETE);
			deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
			buttonsPanel.add(deleteButton);
			enable(deleteButton, !isSystem(item) && !isRemove(item) && isEditable);
			deleteButton.ensureDebugId("delete-button-" + row );
			handler.setDeleteButton(deleteButton);
		}


		paymentsTable.setWidget(row, 5, buttonsPanel);
		paymentsTable.getCellFormatter().addStyleName(row, 5, AON.AON_TEXT_RIGHT);

		formatRow(row);
		

		if (item.getScope() == Scope.SALARY) {
			paymentsTable.getRowFormatter().addStyleName(row, AON.AON_DATA_TABLE_ROW_HIGHLIGHT);
			paymentsTable.getRowFormatter().addStyleName(row - 1, AON.AON_DATA_TABLE_ROW_HIGHLIGHT_TOP);
		} // highlight dirty, not saved items.
		
	}
	
	private <I extends Item> HorizontalPanel createSpecialPay(I item, TextBox descriptionBox, int row, ItemChangeHandler<TextBox, I> handler) {
		//Horizontal panel for concept and issue date
		HorizontalPanel descriptionHPanel = new HorizontalPanel();
		descriptionHPanel.setWidth("100%");
		descriptionHPanel.getElement().getStyle().setPaddingRight(3.00, Unit.PX);

		descriptionBox.setWidth("100%");
		descriptionHPanel.add(descriptionBox);

		descriptionBox.getElement().getParentElement().getStyle().setPadding(0.00, Unit.PX);
		descriptionBox.getElement().getParentElement().getStyle().setBorderStyle(BorderStyle.NONE);
		descriptionBox.getElement().getParentElement().getStyle().setWidth(100, Unit.PCT);

		//Issue label and value
		Label issueDateLabel = new Label("COBRO");
		issueDateLabel.addStyleName(style.issueLabel());

		descriptionHPanel.add(issueDateLabel);
		issueDateLabel.getElement().getParentElement().getStyle().setBorderStyle(BorderStyle.NONE);
		issueDateLabel.getElement().getParentElement().getStyle().setPaddingRight(0, Unit.PX);

		
		Date date = new Date();
		date.setDate(1);

		ListBox issueDateListBox = new ListBox();
		issueDateListBox.ensureDebugId("issueDate-listbox-" + row);
		
		issueDateListBox.addItem("Prorrat.", "-1");

		for (int month = 0; month < 12; month++) {
			date.setMonth(month);
			issueDateListBox.addItem(MONTH_FORMAT.format(date), Integer.toString(month));
		}

		issueDateListBox.setWidth("56px");

		Short month = item.getMonth();
		issueDateListBox.setSelectedIndex(month == null ? 0 : month + 1);

		issueDateListBox.addStyleName(style.issueTextBox());
		handler.setIssueDateListBox(issueDateListBox);

		descriptionHPanel.add(issueDateListBox);
		issueDateListBox.getElement().getParentElement().getStyle().setPadding(0.00, Unit.PX);
		issueDateListBox.getElement().getParentElement().getStyle().setBorderStyle(BorderStyle.NONE);

		return descriptionHPanel;
		
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

		Variable percentVariable = getPercentVariable(AonStringUtils.defaultIfBlank(deduction.getName()));
		if (percentVariable == null)
			percentVariable = getPercentVariable(getType(deduction, Deduction.Type.OTHER));
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


	private void dumpSystemItem(Item<?> item, String description, int row, Widget percentageWidget,
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
			
			ItemChangeHandler<TextBox,?> handler = 
			item instanceof Deduction ? 
			new DeductionChangeHandler<TextBox>((Deduction) item) : 
			new BonusChangeHandler<TextBox>((Bonus) item) ;
			
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
		amountLabel.setText(format(item.getAmount()));
		// very ugly !!!!
		boolean isCost = false;
		for ( String style: iconStyles ) 
			isCost |= "_cost".equals(style);
		
		String name =  getDebugId(item);
		
		amountLabel.ensureDebugId(name + ( isCost ? "_cost"  : "" ) );

		InlineLabel dbAmountLabel = new InlineLabel();
		dbAmountLabel.setText(format(item.getDbAmount()));
		setDbStyleName(dbAmountLabel, amountLabel);

		InlineLabel ssAmountLabel = new InlineLabel();
		ssAmountLabel.setText(format(item.getSsAmount()));
		setDbStyleName(ssAmountLabel, amountLabel);

		amountsPanel.add(amountLabel);
		amountsPanel.add(dbAmountLabel);
		amountsPanel.add(ssAmountLabel);

		amountsPanel.setWidth("100%");
		amountsPanel.setCellWidth(dbAmountLabel, "50%");
		amountsPanel.setCellWidth(ssAmountLabel, "50%");
		amountsPanel.setCellHorizontalAlignment(amountLabel, HorizontalAlignmentConstant.startOf(Direction.RTL));
		amountsPanel.setCellHorizontalAlignment(dbAmountLabel, HorizontalAlignmentConstant.startOf(Direction.RTL));
		amountsPanel.setCellHorizontalAlignment(ssAmountLabel, HorizontalAlignmentConstant.startOf(Direction.RTL));

		VisibilityImpl dbVisibilityImpl = new VisibilityImpl(dbAmountLabel.getElement().getParentElement());
		addDbWidget(dbVisibilityImpl);
		dbVisibilityImpl.setVisible(salaryDraftObject.hasDbSalary() && dbSalaryCheck.getValue());
		
		VisibilityImpl ssVisibilityImpl = new VisibilityImpl(ssAmountLabel.getElement().getParentElement());
		if ( isSSDeduction(item) || isSSBonus(item)) {
			addSsWidget(ssVisibilityImpl);
			ssVisibilityImpl.setVisible(salaryDraftObject.hasSsSalary() && tgssCheck.getValue());
		} else {
			ssVisibilityImpl.setVisible(false);
		}

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

	private void dumpSystemBonus(Bonus bonus, Double percent, String description, int row,
			Button expandButton, String... iconStyles) {
		Widget percentWidget = newPercentWidget(bonus, percent);
		dumpSystemItem(bonus, description, row, percentWidget, expandButton, iconStyles);
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
			
			// Check agreement variables whit empty value
			if(!notDefinedVarsCheck.getValue() && AonStringUtils.isBlank(variable.getExpression()) && scope.equals(Scope.AGREEMENT))
				continue;

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

		T editor = createEditor(variable, salaryDraftObject);
		int width = editor instanceof ListBox ? size2px(20) + 6 : size2px(20);
		editor.asWidget().getElement().getStyle().setWidth(width, Unit.PX);

		VariableChangeHandler<T> variableChangeHandler = createVariableChangeHandler(variable);// new
																								// VariableChangeHandler<T>(variable);

		Label label = getLabel(variable);
		htmlPanel.add(label);
		variableChangeHandler.setLabel(label);

		HorizontalPanel valuePanel = new HorizontalPanel();
		valuePanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);

		//TextBox variableTextBox = new ExpressionBox();
		//variableTextBox.setMaxLength(EXPRESSION_MAX_LENGTH);

		variableChangeHandler.setEditor(editor);

		valuePanel.add(editor);

		valuePanel.add(new InlineHTML("&nbsp;"));
		
		try {
			InlineLabel dbAmountLabel = new InlineLabel();
			dbAmountLabel.setText(getDbValueOf(variable));
			dbAmountLabel.setVisible(salaryDraftObject.hasDbSalary());
			dbAmountLabel.addStyleName(AON.AON_TEXT_RIGHT);

			setDbStyleName(dbAmountLabel, format(editor.getValue(), variable), dbAmountLabel.getText());
		
			valuePanel.add(dbAmountLabel);

			VisibilityImpl dbWidget = new VisibilityImpl(dbAmountLabel.getElement().getParentElement());
			dbWidget.setVisible(salaryDraftObject.hasDbSalary() && dbSalaryCheck.getValue());
			addDbWidget(dbWidget);
		} catch ( Exception e ) {
			info(e.getMessage());
		}

		//valuePanel.setCellWidth(dbAmountLabel, "50%");
		
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
			if ( itemButton  != null ) {
				itemButton.setTabIndex(Short.MAX_VALUE);
				valuePanel.add(itemButton);
				// not show payments of variables at 'to' ...
				itemButton.setValue(
				(variable.getScope().compareTo(Scope.CONTRACT) >= 0) 
				|| (show && variable.getScope().compareTo(Scope.AGREEMENT) >= 0), true);
				itemButton.ensureDebugId("item-button-" + debugName );
			}
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
				notDefinedVarsCheck.setValue(true);
				List<Variable> contextCopy = new ArrayList<Variable>(context);
				dumpContext(contextCopy, expandScope, false, null);
				expandButton.removeStyleName(AON.AON_ICON_EXPANDALL);
				expandButton.setStyleName(AON.AON_ICON_COLLAPSEALL, true);
				expandLabel.setText("Ocultar variables del " + SCOPE_DESCRIPTIONS.get(expandScope));
				SalaryDraft.this.scope = expandScope;
			}

			private void collapse() {
				notDefinedVarsCheck.setValue(false);
				
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
				pdfViewer.open(dataURI);
			}

		});
	}
	
	private void printLetter() {
		salaryDraftObject.downloadLetter("application/pdf", new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String dataURI) {
				showPreview();
				pdfViewer.open(dataURI);
			}

		});
	}

	private void printSettle() {
		
		SettleType type = SettleType.valueOf(settlePreviewListBox.getSelectedValue());
		switch (type) {
		case LETTER:
			printLetter();
			break;
		default:
			print();
			break;
		}

		
	}

	private void printSalary() {
		print();
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
				pdfViewer.open(dataURI);
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
				printSalary();
				return null;
			}
			
			@Override
			public Void visitSettle(Type type) {
				printSettle();
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

	private static String format(Date date) {
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

	private Button getEnableButton() {
		Button agreementButton = new Button();
		agreementButton.setStyleName(AON.AON_ICON_ENABLE);
		agreementButton.setStyleName(AON.AON_NO_MARGIN, true);
		agreementButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		return agreementButton;
	}

	private Button getDisableButton() {
		Button agreementButton = new Button();
		agreementButton.setStyleName(AON.AON_ICON_DISABLE);
		agreementButton.setStyleName(AON.AON_NO_MARGIN, true);
		agreementButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		return agreementButton;
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
					if (!isCostsVisible()) {
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
		
		if ( isSystemBonus(bonus) )
			dumpSystemBonus(idx, bonus, null);
		else 
			dumpBonus(idx, bonus, null);

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(idx); col++) {
			fomatter.addStyleName(idx, col, textStyleName);
		}

		return paymentsTable.getRowFormatter().getElement(idx);
	}

	private void showCosts() {
		showCosts(isCostsVisible());
	}

	private void showCosts(boolean show) {
		
		int costsBeforeRow = paymentsTable.getRowCount()
				- (/* 1 new line */+1 /* blanks line */);

		int costsCount = salaryDraftObject.getCosts().stream()
		.collect(Collectors.summingInt(c ->  1 + ((c instanceof CompositeDeduction) ? ((CompositeDeduction)c).getChilds().size() : 0)));

		if (show) {
			//info("showCosts(" + show +")" );
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

	private void dumpCosts(int beforeRow ) {
		List<Deduction> costs = salaryDraftObject.getCosts();
		for (int i = 0; i < costs.size(); i++) {
			Deduction cost = costs.get(i);
			paymentsTable.insertRow(beforeRow );
			
			Double percent = getPercent(cost, salaryDraftObject);
			Deduction.Type type = getType(cost, Deduction.Type.OTHER);

			Button expandButton = null;
			if (cost instanceof CompositeDeduction) {
				expandButton = new Button();
				expandButton.setTabIndex(Short.MAX_VALUE);
				expandButton.setStyleName(AON.AON_ICON_EXPAND);
				expandButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
			}


			dumpSystemDeduction(cost, percent, cost.getDescription(), beforeRow++ , expandButton, "_cost");

			if (cost instanceof CompositeDeduction) {
				for (Deduction child : ((CompositeDeduction) cost).getChilds()) {
					paymentsTable.insertRow(beforeRow );
					dumpSystemItem(child,"  " + child.getDescription() ,beforeRow, null, null);

					paymentsTable.getRowFormatter().getElement(beforeRow++).getStyle().setDisplay(Display.NONE);
				}
			}
			
			
//			dumpSystemDeduction(cost, percent, description, beforeRow + i, null, AON.AON_ICON_COST,
//					AON.AON_EDIT_DATA_TABLE_BUTTON, AON.AON_PADDING_LEFT);
		}
	}

	private void dumpBonuses(int beforeRow) {
		List<Bonus> bonuses = salaryDraftObject.getBonuses();
		for (int i = 0; i < bonuses.size(); i++) {
			Bonus bonus = bonuses.get(i);
			paymentsTable.insertRow(beforeRow);
			
			Button expandButton = null;
			if (bonus instanceof CompositeBonus) {
				expandButton = new Button();
				expandButton.setTabIndex(Short.MAX_VALUE);
				expandButton.setStyleName(AON.AON_ICON_EXPAND);
				expandButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
			}			
			
			if (bonus.getAmount() != null) {
				if ( isSystemBonus(bonus) ) {
					dumpSystemBonus(beforeRow++, bonus, expandButton);
					if (bonus instanceof CompositeBonus) {
						for (Bonus child : ((CompositeBonus) bonus).getChilds()) {
							paymentsTable.insertRow(beforeRow );
							dumpSystemItem(child,"  " + child.getDescription() ,beforeRow, null, null);
							paymentsTable.getRowFormatter().getElement(beforeRow++).getStyle().setDisplay(Display.NONE);
						}
					}
				}
				else { 
					dumpBonus(beforeRow++, bonus, expandButton);
				}
			} else {
				String styles[] = eventStyles.get(Event.Type.WARNING);
				if ( !isSystemBonus(bonus) ) {
					dumpBonus(beforeRow, bonus, styles[0], expandButton);
				}
				addStyle(paymentsTable, beforeRow++, styles[1]);
			}

		}
		paymentsTable.insertRow(beforeRow);
		newBonusHandler = insertNewBonusRow(beforeRow);
		newBonusHandler.initSuggestionItems();
	}
	
	
	

	private void hideCosts(int beforeRow) {

		int costs = salaryDraftObject.getCosts().stream()
				.collect(Collectors.summingInt(c ->  1 + ((c instanceof CompositeDeduction) ? ((CompositeDeduction)c).getChilds().size() : 0)));
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
		Scope scope = 
		item.isDefinedAt(Scope.AGREEMENT)? 
		Scope.AGREEMENT : item.getScope();
		
		switch (scope) {
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
		case FOGASA:
			return getContextVariable(PORCENTAJE_FOGASA);
		case UNEMPLOYMENT:
			return getContextVariable(PORCENTAJE_DESMPL);
		default:
			return null;
		}
	}

	private Variable getPercentVariable(String name) {
		
		switch (name) {
		case "DESMPL_E":
			return getContextVariable(PORCENTAJE_DESMPL_E);
		default:
			return null;
		}
	}

	private Variable getPercentVariable(Bonus.Type type) {
		

		switch (type) {
		case ERE:
			return getContextVariable(PORCENTAJE_OFF);
		default:
			return null;
		}
	}


	private Widget newPercentWidget(Deduction deduction, Double percent) {
		Deduction.Type type = getType(deduction, Deduction.Type.OTHER);
		switch (type) {
		case IRPF:
			return newIrpfPercentBox(deduction, percent);
		case OTHER:
		case BONUS:
		case IN_KIND:
			return newPercentLabel("");
		case UNEMPLOYMENT:
			return newPercentBox("PORCENTAJE_" + deduction.getName(), deduction, percent);
		case COMMON_CONTINGENCY:
			return newPercentLabel(deduction, percent, getPercentVariable(deduction.getExpression(), salaryDraftObject));
		default:
			return newPercentLabel(deduction, percent, getPercentVariable(type));
		}
	}
	
	private Widget newPercentWidget(Bonus bonus, Double percent) {
		if ( percent == null )
			return null;
		if ( bonus.getType() == null )
			return null; //newPercentLabel(bonus, percent, null);
		
		switch (bonus.getType()) {
		case ERE:
			return newPercentBox(getPercentName(bonus), bonus, percent);
		default:
			return newPercentLabel(bonus, percent, getPercentVariable(bonus.getType()));
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
	
	private StringTimeLineVariable newStringTimeLineVariable(String name) {
		StringTimeLineVariable var = new StringTimeLineVariable();
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
					irpfPercentTexTBox.setText(String.valueOf(AonNumberUtils.isValid(percent) ? 0.00 : percent));
			}

			// --------------------------------------------------- Blur Handler

			@Override
			public void onBlur(BlurEvent event) {
				irpfPercentTexTBox.setText(formatPercent(AonNumberUtils.isNotValid(percent) ? 0.00 : percent));
			}

			// ------------------------------------------------- Change Handler
			@Override
			public void onChange(ChangeEvent event) {

				StringVariable var = SalaryDraft.this.newStringVariable(PORCENTAJE_IRPF);
				String value = irpfPercentTexTBox.getValue();
				var.setExpression(AonStringUtils.isEmpty(value) ? "REMOVE_VARIABLE()" : value);

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
		setEditable(irpfPercentTexTBox, true);


		Panel irpfPercentPanel = new HorizontalPanel();
		irpfPercentPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		irpfPercentPanel.add(irpfPercentTexTBox);
		Variable irpfPercentVar = getContextVariable(PORCENTAJE_IRPF);
		if (irpfPercentVar == null) {
			irpfPercentVar = newStringVariable(PORCENTAJE_IRPF);
		}

		try {
			InlineLabel dbIrpfLabel = new InlineLabel();
			String dbPercent = getDbValueOf(PORCENTAJE_IRPF);
			dbIrpfLabel.setText(formatPercent(AonStringUtils.isBlank(dbPercent) ? "0.00" : dbPercent));
			dbIrpfLabel.setVisible(salaryDraftObject.hasDbSalary());
			dbIrpfLabel.addStyleName(AON.AON_TEXT_RIGHT);

			setDbStyleName(dbIrpfLabel, irpfPercentTexTBox.getText(), dbIrpfLabel.getText());
		
			irpfPercentPanel.add(dbIrpfLabel);

			VisibilityImpl dbWidget = new VisibilityImpl(dbIrpfLabel.getElement().getParentElement());
			dbWidget.setVisible(salaryDraftObject.hasDbSalary() && dbSalaryCheck.getValue());
			addDbWidget(dbWidget);
		} catch ( Exception e ) {
			info(e.getMessage());
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

	private Widget newPercentBox(final String variable, final Item deduction, final Double percent) {

		final TextBox percentTexTBox = new ExpressionBox();
		percentTexTBox.ensureDebugId("textBox_"+variable);

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

	private PaymentChangeHandler<?> getPaymentChangeHandlerFor(Integer id) {
		for (PaymentChangeHandler<?> handler : paymentChangeHandlers) {
			if (Objects.equals(handler.item.getId(),id)) {
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

	private boolean isDelay(){
		return salaryDraftObject != null && salaryDraftObject.getType() == Salary.Type.DELAY;
	}

	private boolean isSalary(){
		return salaryDraftObject != null && salaryDraftObject.getType() == Salary.Type.SALARY;
	}

	private boolean hasDbSalary(){
		return salaryDraftObject != null  && salaryDraftObject.hasDbSalary();
	}
	
	private boolean hasSsSalary(){
		return salaryDraftObject != null  && salaryDraftObject.hasSsSalary();
	}

	private boolean hasEvents() {
		return salaryDraftObject != null && salaryDraftObject.hasEvents();
	}
	
	private boolean hasFiscalModels() {
		return salaryDraftObject != null && salaryDraftObject.hasFiscalModels();
	}

	private boolean hasAonInfoEvents() {
		if ( salaryDraftObject == null )
			return false;
		for ( Event event: salaryDraftObject.getEvents() )
			if ( isAonInfoEvent(event))
				return true;
		
		return false;
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
	
	private Double getDiffsWithSsSalary(){
		if ( salaryDraftObject == null ) 
			return null;
		
		if ( !salaryDraftObject.hasSsSalary() )	
			return null;
		
		double diffs = 0.00;
		
		double cgcBase = parse(format(salaryDraftObject.getCgcBase()));
		double ssCgcBase = parse(format(salaryDraftObject.getSsCgcBase()));
		double cgpBase = parse(format(salaryDraftObject.getCgpBase()));
		double ssCgpBase = parse(format(salaryDraftObject.getSsCgpBase()));
		double hExtraBase = parse(format(salaryDraftObject.gethExtraBase()));
		double ssHExtraBase = parse(format(salaryDraftObject.getSsHExtraBase()));
		double nonHExtraBase = parse(format(salaryDraftObject.getNonHExtraBase()));
		double ssnonHExtraBase = parse(format(salaryDraftObject.getNonHExtraBase()));
		
		diffs  +=  Math.abs( cgcBase - ssCgcBase );
		diffs  +=  Math.abs( cgpBase - ssCgpBase );
		diffs  +=  Math.abs( hExtraBase - ssHExtraBase );
		diffs  +=  Math.abs( nonHExtraBase - ssnonHExtraBase );
		
		Deduction[] ssDeductions = salaryDraftObject.getDeductions()
		.stream().filter(SalaryDraft::isSSDeduction).toArray(Deduction[]::new);
		
		for (Deduction deduction : ssDeductions ) {
			double amount = parse(format(deduction.getAmount()));
			double ssAomunt = parse(format(deduction.getSsAmount()));
			diffs  +=  Math.abs( amount - ssAomunt );
		}

		Deduction[] ssCosts = salaryDraftObject.getCosts()
		.stream().filter(SalaryDraft::isSSDeduction).toArray(Deduction[]::new);

		for (Deduction cost : ssCosts) {
			double amount = parse(format(cost.getAmount()));
			double ssAomunt = parse(format(cost.getSsAmount()));
			diffs  +=  Math.abs( amount - ssAomunt );
		}

		Bonus[] ssBonuses = salaryDraftObject.getBonuses()
		.stream().filter(SalaryDraft::isSSBonus).toArray(Bonus[]::new);
		for (Bonus bonus : ssBonuses) {
			double amount = parse(format(bonus.getAmount()));
			double ssAomunt = parse(format(bonus.getSsAmount()));
			diffs  +=  Math.abs( amount - ssAomunt );
		}

		return diffs;
	}

	private List<Variable> getConstants(List<Variable> context) {
		List<Variable> summingConstants = new ArrayList<Variable>();

		//try {
		//	summingConstants.add(newNumberVariable(context, "DIAS_TRABAJADOS"));
		//} catch ( Exception e ) {
		//}

		//try {
		//	summingConstants.add(newNumberVariable(context, "HORAS_TRABAJADAS"));
		//	return summingConstants;
		//} catch ( Exception e ) {
		//}
		
		Object partialFactor = getContextValue("COEFICIENTE_PARCIALIDAD", salaryDraftObject );
		
		if ( partialFactor == null )
			return summingConstants;
		
		if ( Double.parseDouble(String.valueOf(partialFactor)) == 1.00 )
			return summingConstants;
		
		//try {
		//	summingConstants.add(newNumberVariable(context, "HORAS_NOMINA"));
		//} catch ( Exception e ) {
		//}

		return summingConstants; 
	}
	
	private NumberVariable newNumberVariable(List<Variable> context, String name) {
		Double value = 
			context.stream()
				.filter(v->v.getName().equals(name))
				.filter(v-> v.getValue() != null )
				.collect(Collectors.summingDouble(v->Double.parseDouble(String.valueOf(v.getValue()))));
		
		if  ( value == 0.00 )
			throw new NullPointerException();
		
		NumberVariable workedHoursVariable = new NumberVariable();
		workedHoursVariable.setName(name);
		workedHoursVariable.setValue(value);
		workedHoursVariable.setScope(Scope.CONTRACT);
		workedHoursVariable.setEndDate(salaryDraftObject.getEndDate());
		workedHoursVariable.setStartDate(salaryDraftObject.getStartDate());
		return workedHoursVariable;
	}

	private NumberVariable newNumberVariable(String name, Double value ) {
		NumberVariable workedHoursVariable = new NumberVariable();
		workedHoursVariable.setName(name);
		workedHoursVariable.setValue(value);
		workedHoursVariable.setScope(Scope.CONTRACT);
		workedHoursVariable.setEndDate(salaryDraftObject.getEndDate());
		workedHoursVariable.setStartDate(salaryDraftObject.getStartDate());
		return workedHoursVariable;
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
		if ( automatic ) { 
			acceptButton.setEnabled(!automatic);
		}
		
		totalPaymentLabel.setReadOnly(automatic);
		totalLiquidLabel.setReadOnly(automatic);
		
	}
	
	private void setReadOnly(boolean readOnly) {
		if ( readOnly ) {
			fxButton.setEnabled(!readOnly);
			//undoButton.setEnabled(!readOnly);
			//redoButton.setEnabled(!readOnly);
			//undoAllButton.setEnabled(!readOnly);
		}

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
		
		salaryDraftObject.calculate(this);
		
	}
	
	private boolean isPaymentVariable(Variable variable) {
		
		for ( Payment p : SalaryDraft.this.availablePaymens )
			if ( AonStringUtils.equals(p.getName(), variable.getName()))
				return true;
		
		return false;
	}
	
	private boolean isFromAgreemen(Payment payment) {
		return salaryDraftObject.hasAgreementCounterPart(payment) || 
				payment.isDefinedAt(Scope.AGREEMENT);
	}
	
	private List<Payment> filterPayments(List<Payment> payments, Scope scope) {
		Set<String> names = 
		salaryDraftObject.getPayments().stream()
		.filter(p -> p.getScope() == scope )
		.map(p -> p.getName() )
		.filter ( n -> n != null)
		.collect(Collectors.toSet())
		;
		
		return payments.stream()
		.filter( p-> p.getName() == null || !names.contains(p.getName()) )
		.collect(Collectors.toList());
	}
	
	private boolean alreadyDisplayed(String name) {
		VariableChangeHandler<?> handler = getVariableChangeHandlerFor(name);
		return handler != null && ( handler.editor instanceof IsWidget );	
	}

	private boolean alreadyDisplayed(Variable variable) {
		return getVariableChangeHandlerFor(variable.getName()) != null;	
	}
	
	private Widget newQuoteTextBox(Payment payment, int row) {
		
		Double quote = payment.getQuote();
		Double amount = payment.getAmount();

		TextBox quoteTextBox = newTextBox(format(quote));
		quoteTextBox.ensureDebugId("quote-label-" + row );
		
		Panel quotePanel = new HorizontalPanel();
		quotePanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		quotePanel.add(quoteTextBox);

		Button ssButton = new Button();
		ssButton.setStyleName(AON.AON_ICON_BONUS_SMALL);
		ssButton.setStyleName(AON.AON_NO_MARGIN, true);
		ssButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		ssButton.setEnabled(false);
		quotePanel.add(ssButton);
		
		quotePanel.setVisible(amount != null && !amount.equals(quote));

		String quoteExpression = payment.getQuoteExpression();
		if ( AonStringUtils.isBlank(quoteExpression))
			return quotePanel;
		String variableName = getImplicitVariableName(quoteExpression);
		if ( variableName == null  ) 
			return quotePanel;

		quotePanel.setVisible(!(payment instanceof CompositePayment));

		enable(quoteTextBox, true );
		setEditable(quoteTextBox, true );

		Variable variable = 
		salaryDraftObject.getContext().stream()
		.filter( v -> !(v instanceof UndefinedVariable))
		.filter(v -> AonStringUtils.equals(v.getName(), variableName))
		.filter(v -> Objects.equals(v.getStartDate(), payment.getStartDate()))
		.filter(v -> Objects.equals(v.getEndDate(), payment.getEndDate()))
		.findFirst().orElse(
		new StringTimeLineVariable.Builder()
		.setImplicit(true)
		.setScope(Scope.SALARY)
		.setName(variableName)
		.setEndDate(payment.getEndDate())
		.setStartDate(payment.getStartDate())
		.create());
		
		variable.setValue(payment.getQuote());
		
		VariableChangeHandler<TextBox> handler = 
		new VariableChangeHandler<TextBox>(variable) {
			@Override
			protected Variable newVariable() {
				return new StringTimeLineVariable.Builder()
						.setImplicit(true)
						.setScope(Scope.SALARY)
						.setName(variable.getName())
						.setEndDate(variable.getEndDate())
						.setStartDate(variable.getStartDate())
						.create()
						;
			}
			
			@Override
			protected CalculateCallback getNextVariableFocusCallback() {
				class ExpandPaymentFocusCallback implements CalculateCallback {
					
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
						PaymentChangeHandler<?> handler = getPaymentChangeHandlerFor(payment.getId());
						if ( handler != null ) {
							handler.expand();
						}
					}
				}
				return new ExpandPaymentFocusCallback();
			}
		};
		handler.setEditor(quoteTextBox);
		
		variableChangeHandlers.add(handler);
		
		return quotePanel;
	}
	
	private void resizeContentPanel() {
		
		int height = Window.getClientHeight() 
				- contentScrollPanel.getAbsoluteTop()
				- footerHorizontalPanel.getOffsetHeight()
				;
		
		height -= 25 ;
		
		contentScrollPanel.setHeight(Integer.toString(height)+"px");
		//LOGGER.info("contentScrollPanel : " + height );
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
		
		// super private, insane
		if ( AonStringUtils.startsWith(name, "__"))
			return true;
		// IRPF quotas & bases
		if ( AonStringUtils.startsWith(name, "CRA_00"))
			return true;
		
		for (String skip : SKIP_VARIABLES) {
			if (skip.equals(name))
				return true;
		}
		
		

		if (variable instanceof UndefinedPaymentVariable && !displayNow((UndefinedPaymentVariable) variable))
			return true;
		
		Object value = variable.getValue(); 
		for (String skip : SKIP_NULL_VARIABLES) {
			if (skip.equals(name)) {
				if ( value == null )
					return true;
				String str = String.valueOf(value);
				if ( AonStringUtils.isBlank(str))
					return true;
				try {
					return Double.parseDouble(str) == 0.00;
				} catch ( Exception e ) {
					
				}
				break;
			}
		}

		return false;
	}

	static <T extends IsWidget & HasValue<String> & HasAllFocusHandlers & Focusable & HasEnabled> T createEditor(
			Variable variable, SalaryDraftObject salaryDraftObject) {
		
		VariableEditorFactory [] empty = {};
		
		Object quoteGroup = salaryDraftObject.getVariable("GRUPO_COTIZACION"); 
		
		for (VariableEditorFactory<T> factory : QUOTE_VARIABLE_EDITOR_FACTORIES.getOrDefault(quoteGroup, empty)) {
			if (factory.accept(variable))
				return factory.create(variable, salaryDraftObject);
		}

		for (VariableEditorFactory<T> factory : COMMON_VARIABLE_EDITOR_FACTORIES) {
			if (factory.accept(variable))
				return factory.create(variable, salaryDraftObject);
		}
		return null;
	}

	static <T extends IsWidget & HasValue<String> & HasAllFocusHandlers & Focusable & HasEnabled> T createEditor(
			Variable variable) {
		
	for (VariableEditorFactory<T> factory : COMMON_VARIABLE_EDITOR_FACTORIES) {
			if (factory.accept(variable))
				return factory.create(variable);
		}
		return null;
	}

	// ------------------------------------------------------- Static 'Library'

	private static boolean isSystemBonus(Bonus bonus) {
		if (bonus.getScope() == Scope.SYSTEM)
			return true;

		String expression = bonus.getExpression();

		if (StringUtils.isBlank(expression))
			return false;

		return expression.startsWith("/*epoch:");
	}
	
	private static boolean isSystemDeduction(Deduction deduction) {
		for (Deduction.Type type : SYSTEM_DEDUCTION)
			if (type == deduction.getType())
				return true;
		
		return deduction.getScope() == Scope.SYSTEM;
	}

	private static Double getSsPercent(Deduction deduction, SalaryDraftObject draftObject) {
		return getPercent(getType(deduction, Deduction.Type.OTHER), deduction.getSsAmount(), Double.NaN,
				draftObject.getSsCgcBase(), draftObject.getSsCgpBase(), draftObject.getSsHExtraBase(),
				draftObject.getSsNonHExtraBase());
	}

	private static Double getDbPercent(Deduction deduction, SalaryDraftObject draftObject) {
		return getPercent(getType(deduction, Deduction.Type.OTHER), deduction.getDbAmount(), draftObject.getDbIrpfBase(),
				draftObject.getDbCgcBase(), draftObject.getDbCgpBase(), draftObject.getDbHExtraBase(),
				draftObject.getDbNonHExtraBase());
	}

	private static Double getPercent(Deduction deduction, SalaryDraftObject draftObject) {
		
		try {
			if ("CGC_E_TEMP".equalsIgnoreCase(deduction.getName())) {
				Object value =  getContextValue(PORCENTAJE_SHORT, draftObject);
				if ( value instanceof Number )
					return ((Number) value ).doubleValue();
				else
					return Double.parseDouble(value.toString());
			}
		} catch ( Exception e ) {
			
		}
		
		try {
			Variable var = getPercentVariable(deduction.getExpression(), draftObject);
			if ( var != null ) {
				return Double.parseDouble(var.getValue().toString());
			}
		} catch (Exception e ) {
			
		}

		Double cgcBase = null; 
		Double cgpBase = null;
		switch (deduction.getName()) {
		case "IT_E":
		case "IMS_E":
		case "FP_E":
		case "CGC_E":
		case "ATEP_E":
		case "EXTR_E":
		case "NEXTR_E":
		case "DESMPL_E":
		case "FOGASA_E":
			cgcBase = getContextSumValue("BASE_CGC_E", draftObject);
			cgpBase = getContextSumValue("BASE_CGP_E", draftObject);
			break;

		default:
			cgcBase = getContextSumValue("BASE_CGC", draftObject);
			cgpBase = getContextSumValue("BASE_CGP", draftObject);
			break;
		} 		
		
		
		return getPercent(getType(deduction, Deduction.Type.OTHER), 
				deduction.getAmount(), 
				draftObject.getIrpfBase(),
				cgcBase, 
				cgpBase,
				draftObject.gethExtraBase(),
				draftObject.getNonHExtraBase()
				);
	}
	

	private static Variable getPercentVariable(String str, SalaryDraftObject draftObject) {
		RegExp regExp = 
		RegExp.compile("PORCENTAJE_[A-Z_]+");
		
		MatchResult r = regExp.exec(str);
		
		if ( r != null )
			return getContextVariable(r.getGroup(0), draftObject);
		
		return null;
	}

	private static Variable getContextVariable(String name, SalaryDraftObject salaryDraftObject) {
		for (Variable var : salaryDraftObject.getDrafContext())
			if (StringUtils.equals(var.getName(), name))
				return var;

		for (Variable var : salaryDraftObject.getContext())
			if (StringUtils.equals(var.getName(), name))
				return var;

		return null;
	}	
	
	private static String getPercentName(Bonus bonus) {
		String expression = bonus.getExpression();
		
		if ( AonStringUtils.contains(expression, PORCENTAJE_OFF))
			return PORCENTAJE_OFF;
		else if ( AonStringUtils.contains(expression, PORCENTAJE_BACK))
			return PORCENTAJE_BACK;
		return null;

	}

	private static Double getPercent(Bonus bonus, SalaryDraftObject draftObject) {
		
		try {
			RegExp percentage = RegExp.compile("\\((.*)%\\)","i");
			MatchResult result = percentage.exec(bonus.getDescription());
			return Double.parseDouble(result.getGroup(1).replace(',', '.'));
		} catch ( Throwable t ) {
			return null;
		}
		
	}
	
	private static Optional<Variable> getPercentVariable(Bonus bonus, SalaryDraftObject draftObject) {
		
		try {
			
			String name = getPercentName(bonus);
			
			return 
			draftObject.getContext().stream()
			.filter( v -> v.getName().equals(name))
			.filter( v -> v.getStartDate().compareTo(bonus.getEndDate()) <= 0)
			.filter( v -> v.getEndDate().compareTo(bonus.getStartDate()) >= 0)
			.findFirst()
			;

		} catch ( Exception e ) {
			return Optional.empty();
		}
		
	}

	private static double getContextSumValue(String name, SalaryDraftObject draftObject) {
		
		return 
		draftObject.getContext().stream()
		.filter(v -> AonStringUtils.equals(name, v.getName()))
		.filter(v -> v.getValue() != null )
		.map( v -> v.getValue().toString() )
		.collect(Collectors.summingDouble(v -> {
			try {
				return Double.parseDouble(v.toString());
			} 
			catch (Throwable t) { 
				return 0.00; 
			}
		}))
		;
		
	}

	private static Double getPercent(Deduction.Type type, Double amount, Double irpfBase, Double cgcBase,
			Double cgpBase, Double hExtraBase, Double nonHExtraBase) {
		if ( amount == null )
			return null;
		
		switch (type) {
		case IRPF:
			return NumberUtils.isValid(irpfBase) ? amount / irpfBase * 100 : null;
		// case COMMON_CONTINGENCY:
		// return amount / cgcBase * 100;
		case FOGASA:
		case JOB_TRAINING:
		case UNEMPLOYMENT:
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


	private static Object getContextValue(String name, SalaryDraftObject draftObject) {
		for (Variable var : draftObject.getDrafContext())
			if (StringUtils.equals(var.getName(), name))
				return var.getValue();

		for (Variable var : draftObject.getContext())
			if (StringUtils.equals(var.getName(), name))
				return var.getValue();

		return null;
	}

	public static String format(Double amount) {
		return NumberUtils.isNotValid(amount) ? AON.CURRENCY_FORMAT.format(AON.round(0.00)) : AON.CURRENCY_FORMAT.format(AON.round(amount));
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

	private static String formatPercent(Object amount) {
		try {
			return format(Double.parseDouble(String.valueOf(amount))) + " %";
		} catch ( Throwable t ) {
			return "";
		}
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

	private static TextBox newTextBox(String str) {
		ExpressionBox quoteExpressionBox = new ExpressionBox();
		quoteExpressionBox.setText(str);
		quoteExpressionBox.setReadOnly(true);
		// padding-left : 5px, to align vertically with IRPF Widget.
		quoteExpressionBox.setVisibleLength(AonStringUtils.length(str));
		quoteExpressionBox.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		return quoteExpressionBox;
	}
	
	
	

	private static Widget newPercentLabel(Item<?> item, Double percent, Variable percentVar) {
		info(item.getExpression() + ", " + percent + ", " +(percentVar != null  ? percentVar.getName() : "NULL"));
		if (NumberUtils.isNotValid(percent))
			return newPercentLabel(percentVar == null ? formatPercent(0.00) : formatPercent(percentVar.getValue()));
		else
			return newPercentLabel(percentVar == null ? formatPercent(percent) : formatPercent(percentVar.getValue()) );
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
	private final static VariableEditorFactory COMMON_VARIABLE_EDITOR_FACTORIES[] = { 
			new MonthDaysEditorFactory("DIAS_MES"),
			new DateEditorFactory("FECHA_PREAVISO"),
			//new DateEditorFactory("INICIO_PAGO_DIRECTO"),
			new EnumNameListBoxFactory<Employee.Occupation>("OCUPACION", Employee.Occupation.class),
			new DelayFactory("CAUSA_ATRASO"),
			new DismissalFactory("CAUSA_INDEMNIZACION"),
			new StringsListBoxFactory("GRUPO_COTIZACION",
					new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11" }),
			new StringsListBoxFactory("TC2", Employee.TC2.getCodes(), Employee.TC2.getDescriptions()),
			new CalendarConstantEditorFactory(
					"HORAS_NOMINA", 
					"HORAS_TRABAJADAS", 
					"HORAS_SEMANA", 
					"DIAS_NOMINA", 
					"DIAS_TRABAJADOS", 
					"DIAS_PAGA", 
					"DIAS_COTIZADOS", 
					"DIAS_HUELGA", 
					"DIAS_NATURALES_MES", 
					"COEFICIENTE_ERE",  
					"COEFICIENTE_ERE_FZA", 
					"COEFICIENTE_ERE_FZA_EXONERADO", 
					"COEFICIENTE_HUELGA"  ), 
			new EventConstantEditorFactory("ATRASO", "PAGA_EXTRA_[0-9]+_[0-9]+"), 
			new AgreementConstantEditorFactory(), 
			new ConstantEditorFactory("SMI"), 
			new BooleanEditorFactory(), 
			new DefaultEditorFactory() };
	
	private final static VariableEditorFactory MONTHLY_VARIABLE_EDITOR_FACTORIES[] = { 
			new ConstantEditorFactory("DIAS_MES")};
	
	private final static Map<Object,VariableEditorFactory[] > QUOTE_VARIABLE_EDITOR_FACTORIES = new HashMap() {
		{
			put("01", MONTHLY_VARIABLE_EDITOR_FACTORIES);
			put("02", MONTHLY_VARIABLE_EDITOR_FACTORIES);
			put("03", MONTHLY_VARIABLE_EDITOR_FACTORIES);
			put("04", MONTHLY_VARIABLE_EDITOR_FACTORIES);
			put("05", MONTHLY_VARIABLE_EDITOR_FACTORIES);
			put("06", MONTHLY_VARIABLE_EDITOR_FACTORIES);
			put("07", MONTHLY_VARIABLE_EDITOR_FACTORIES);
		}
	};

	// @formatter:on

	private static <T extends UIObject & HasValue<String> & HasAllFocusHandlers & Focusable & HasEnabled>  void enable(T textBox, boolean enabled) {

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

	private static void enable(Button button, boolean enabled) {

		if (button.isEnabled() == enabled)
			return;

		//widget.setEnabled(enabled);
		button.setVisible(enabled);
	}

	private static <T extends UIObject >  void setEditable(T textBox, boolean editable) {
		textBox.setStyleName("aon-Editable", editable);
	}

		private static final DateTimeFormat START_DATE_FORMAT = DateTimeFormat.getFormat("dd '-'");
	private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat.getFormat("dd 'de' MMMM");

	private static String formatChildDescription(Item<?> child, SalaryDraftObject salaryDraftObject) {

		return child.getDescription() + " ";

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
	
	private static <T extends Item<?>> boolean isNeto(T item) {
		return AonStringUtils.startsWith(item.getExpression(), "/*read-only*/NETO" );
	}
	
	private static <T extends Item<?>> boolean isBruto(T item) {
		return AonStringUtils.startsWith(item.getExpression(), "/*read-only*/BRUTO" );
	}

	private static <T extends Item<?>> boolean isReadOnly(T item) {
		String expression = item.getExpression();
		return expression != null && expression.contains("read-only");
	}

	private static <T extends Item<?>> boolean isSystem(T item) {
		return Scope.SYSTEM == item.getScope() || item.isDefinedAt(Scope.SYSTEM);
	}

	private static <T extends Item<?>> boolean isAgreement(T item) {
		return Scope.AGREEMENT == item.getScope() || item.isDefinedAt(Scope.AGREEMENT);
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
	
	private static boolean isContract(Payment payment) {
		return 
				(payment.getScope() == Scope.CONTRACT 
				|| payment.getScope() == Scope.SALARY )
				;
	}

	private static boolean isHideable(Item payment) {
		return AonStringUtils.startsWith(payment.getExpression(), "/*hideable*/" );
	}

	private static boolean isDefault(Payment payment) {
		return AonStringUtils.startsWith(payment.getExpression(), "/*default*/" );
	}
	
	private static void info(String message) {
		if ( LogConfiguration.loggingIsEnabled())
			LOGGER.log(Level.INFO, message);
	}
	
	private static boolean isSSBonus(Item<?> item) {
		return
		(item instanceof Bonus) && 
		isSystemBonus((Bonus)item);
	}

	private static boolean isSSDeduction(Item<?> item) {
		Enum<?> type = item.getType();
		if (
			type == Deduction.Type.BONUS
			|| type == Deduction.Type.FOGASA
			|| type == Deduction.Type.JOB_TRAINING
			|| type == Deduction.Type.UNEMPLOYMENT
			|| type == Deduction.Type.COMMON_CONTINGENCY
			|| type == Deduction.Type.STRUCTURAL_OVERTIME
			|| type == Deduction.Type.NON_STRUCTURAL_OVERTIME
			|| type == Deduction.Type.PROFESSIONAL_CONTINGENCY
			)
			return true;
		
		return false;
	}

	// -------------------------------------------------- ContrataEmployee.Init & Setters
	
	public void setUndoAllButton(AonToolbarButton undoSalaryAllButton) {
		this.undoAllButton = undoSalaryAllButton;
	}
	
	public void setUndoButton(AonToolbarButton undoButton) {
		this.undoButton = undoButton;
	}

	public void setRedoButton(AonToolbarButton redoButton) {
		this.redoButton = redoButton;
	}

	public void setAcceptButton(AonToolbarButton acceptButton) {
		this.acceptButton = acceptButton;
	}

	public void setSalaryButton(AonToolbarButton salaryButton) {
		this.salaryButton = salaryButton;
	}

	public void setExtraButton(AonToolbarButton extraButton) {
		this.extraButton = extraButton;
	}

	public void setSettleButton(AonToolbarButton settleButton) {
		this.settleButton = settleButton;
	}

	public void setDelayButton(AonToolbarButton delayButton) {
		this.delayButton = delayButton;
	}

	public void setFxButton(AonToolbarButton fxButton) {
		this.fxButton = fxButton;
	}

	public void setPrintPreviewButton(AonToolbarButton printPreviewButton) {
		this.printPreviewButton = printPreviewButton;
	}

	public void setIrpfPreviewButton(AonToolbarButton irpfPreviewButton) {
		this.irpfPreviewButton = irpfPreviewButton;
	}

	public void setClosePreviewButton(AonToolbarButton closePreviewButton) {
		this.closePreviewButton = closePreviewButton;
	}
	
	public void setSalarySelect(SalarySelect salarySelect) {
		this.salarySelect = salarySelect;
		salarySelect.addListener(this);
	}
	
	public void setSettlePreviewListBox(ListBox settlePreviewListBox) {
		this.settlePreviewListBox = settlePreviewListBox;
	}
	
	public void setTgssCheck(CheckBox tgssCheck) {
		this.tgssCheck = tgssCheck;
	}
	
	public void setCostsCheck(CheckBox costsCheck) {
		this.costsCheck = costsCheck;
	}
	
	public void setDBSalaryCheck(CheckBox dbSalaryCheck) {
		this.dbSalaryCheck = dbSalaryCheck;
	}
	
	public void setEvenstCheck(CheckBox eventsCheck) {
		this.eventsCheck = eventsCheck;
		initEvents();
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void onUndoAll() {
		salaryDraftObject.clearDrafts();
		salaryDraftObject.calculate(SalaryDraft.this);
	}
	
	public void onUndo() {
		salaryDraftObject.undo();
		salaryDraftObject.calculate(SalaryDraft.this);
	}
	
	public void onRedo() {
		salaryDraftObject.redo();
		salaryDraftObject.calculate(SalaryDraft.this);
	}
	
	public void onAccept() {
		salaryDraftObject.save(this);
	}
	
	public void onSalary() {
		salaryDraftObject.save(new CalculateCallback() {
	
			@Override
			public Calculate getCalculate() {
				return SalaryDraft.this.getCalculate();
			}
			
			@Override
			public void onCalculateSucces(SalaryDraftObject object) {
				SalaryDraft.this.onCalculateSucces(object);
				SalaryDraft.this.salaryDraftObject.emitSalary(SalaryDraft.this);
			}
	
			@Override
			public void onCalculateFailure(Throwable throwable) {
				SalaryDraft.this.onCalculateFailure(throwable);
			}
		});
	}
	
	public void onExtra() {
		salaryDraftObject.emitSalary(this);
	}

	public void onSettle() {
		salaryDraftObject.emitSalary(this);
	}
	
	public void onFx() {
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
	
	public void onTgssCheckChange() {
		showTimeRulePanel();
		showDbTimeRulePanel();
	}
	
	public void onCostsCheck2Change() {
		showCosts();
	}
	
	public void onDbSalaryCheckChange(ValueChangeEvent<Boolean> e) {
		setDbVisible(e.getValue());
	}
	
	public void onEventsCheckChange(ValueChangeEvent<Boolean> e) {
		eventsTable.setVisible(e.getValue());
		eventsTableSpace.setVisible(eventsTable.isVisible());
		showPaymentsEvents(eventsTable.isVisible());
	}
	
	public void onPrintPreview() {
		printPreview();
	}
	
	public void onIRPFPreview() {
		irpfPrint();
	}
	
	public void onSave() {
		String fileName = 
				salaryDraftObject.getEmployeeName() + " " 
				+ DateTimeFormat.getFormat(PredefinedFormat.MONTH).format(salaryDraftObject.getChargeDate())
				+".pdf";
		// ***************************
		// ***************************
//		pdfViewer.download(fileName);
		// ***************************
		// ***************************
	}
	
	public void onClosePreview() {
		showDraft();
	}
	
	public void onSettlePreviewLBChange() {
		printSettle();
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
		scrollPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
	
	private static Deduction.Type getType(Item<Deduction.Type> item, Deduction.Type def) {
		if ( item == null )
			return def;
		
		Deduction.Type t = item.getType();
		if ( t != null )
			return t;
	
		String name = item.getName();
		if ( name == null )
			return def;
		switch (name) {
		case "FOGASA":
		case "FOGASA_IT":
			return Deduction.Type.FOGASA;
		case "IT_E":
		case "IMS_E":
			return Deduction.Type.PROFESSIONAL_CONTINGENCY;
		case "IRPF":
			return Deduction.Type.IRPF;

		default:
			return def;
		}
		
	}
	
	private static <T extends Enum<T>> T valueOf (Class<T> enumType, String name)  {
		try {
			return Enum.valueOf(enumType, name);
		} catch ( Exception e ) {
			return null;
		}
	}
	
	
	private static String formatValue(Double value) {
		if ( AonNumberUtils.isNotValid(value)  )
			return "0.00";
		return NumberFormat.getFormat("#,##0.00#").format(Math.round(value * 1000.00) / 1000.00);
	}
	
	
	private static String getImplicitVariableName(String expression ) {
		MatchResult result = RegExp.compile("var:([A-Z_0-9]+)").exec(expression);
		return result != null ? result.getGroup(1): null;
	}
	
	private static boolean intersects ( Period p1, Period p2) {
		Date maxStart = AonDateUtils.max(p1.getStart(), p2.getStart());
		Date minEnd = AonDateUtils.min(p1.getEnd(), p2.getEnd());
		return AonDateUtils.compare(maxStart, minEnd) <= 0;
		
	}
	
	private static boolean intersects( Variable v1, Variable v2) {
		Period p1 = new Period(v1.getStartDate(), v1.getEndDate());
		Period p2 = new Period(v2.getStartDate(), v2.getEndDate());
		return intersects(p1, p2);
	}
	
	private static String format(Object obj, Variable variable) {
		if ( obj == null )
			return null;
		
		if ( AonStringUtils.equals("DIAS_NATURALES_MES", obj.toString()))
			obj = Integer.toString(DateUtils.getLastDayOfMonth(variable.getEndDate()).getDate());

		else if ( AonStringUtils.equals("IMPROCEDENTE",obj.toString()))
			return Dismissal.UNFAIR.getDescription();
		else if ( AonStringUtils.equals("FIN_TEMPORAL",obj.toString()))
			return Dismissal.TEMP_END.getDescription();
		else if ( AonStringUtils.equals("FIN_OBRA",obj.toString()))
			return Dismissal.WORK_END.getDescription();
		else if ( AonStringUtils.equals("FIN",obj.toString()))
			return Dismissal.DEFINITE_END.getDescription();
		else if ( AonStringUtils.equals("PROCEDENTE",obj.toString()))
			return Dismissal.OBJECTIVE.getDescription();
		else if ( AonStringUtils.equals("CAMBIO_CONDICIONES",obj.toString()))
			return Dismissal.CONDITIONS_CHANGE.getDescription();
		else if ( AonStringUtils.equals("JUBILACION",obj.toString()))
			return Dismissal.RETIREMENT.getDescription();
		else if ( AonStringUtils.equals(Dismissal.UNFAIR.name(),obj.toString()))
			return Dismissal.UNFAIR.getDescription();
		else if ( AonStringUtils.equals(Dismissal.TEMP_END.name(),obj.toString()))
			return Dismissal.TEMP_END.getDescription();
		else if ( AonStringUtils.equals(Dismissal.WORK_END.name(),obj.toString()))
			return Dismissal.WORK_END.getDescription();
		else if ( AonStringUtils.equals(Dismissal.DEFINITE_END.name(),obj.toString()))
			return Dismissal.DEFINITE_END.getDescription();
		else if ( AonStringUtils.equals(Dismissal.OBJECTIVE.name(),obj.toString()))
			return Dismissal.OBJECTIVE.getDescription();
		else if ( AonStringUtils.equals(Dismissal.CONDITIONS_CHANGE.name(),obj.toString()))
			return Dismissal.CONDITIONS_CHANGE.getDescription();
		else if ( AonStringUtils.equals(Dismissal.RETIREMENT.name(),obj.toString()))
			return Dismissal.RETIREMENT.getDescription();
		else if ( AonStringUtils.equals(Dismissal.NOT_PASS_TRIAL_PERIOD.name(),obj.toString()))
			return Dismissal.NOT_PASS_TRIAL_PERIOD.getDescription();
		
		try {
			double value = Double.parseDouble(obj.toString());
			value = BigDecimal.valueOf(value)
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			return format(value);
		} catch ( Exception e ) {
			
		}
		try {
			double value = parse(obj.toString());
			value = BigDecimal.valueOf(value)
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			return format(value);
		} catch ( Exception e ) {
			
		}
		return obj.toString();
	}
	
	private static String getDebugId(Item<?> item) {
		Enum<?> type = item.getType();
		if ( type == null )
			return "unknown";
		
		String name = item.getName();
		if ( AonStringUtils.equalsIgnoreCase(name, "IT_E"))
			return "it";
		if ( AonStringUtils.equalsIgnoreCase(name, "IMS_E"))
			return "ims";
		
		return type.name().toLowerCase();
	}

	
}
