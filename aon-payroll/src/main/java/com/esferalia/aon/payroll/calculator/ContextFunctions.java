package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CHECK;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARENTEED;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.INPUT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTHS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAYMENT_VARIABLE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SECTION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WARNING;
import static com.esferalia.aon.watson.server.AonDateUtils.getDaysBetweenDates;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mvel2.MVEL;
import org.mvel2.util.MethodStub;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementPaymentsFactory.IExtraPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.MacroException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.FullHideException;
import com.esferalia.aon.salary.expression.HideException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.RemoveException;
import com.esferalia.aon.salary.expression.Variables.PeriodMap;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ContextFunctions {

	private static final String _OLD = "_OLD";
	private static final String _GROSS = "_BRUTO";
	private static final String _SECTION = "_SECTION";
	private static final String _PRORATION = "_PRORATION";
	private static final String _FRACTIONATE = "_FRACTIONATE";
	private static final String MONTHS_IMPL = "MESESIMPL";

	public static class UselessGuaranteeException extends CheckException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private static Locale ES = new Locale("es", "ES");

		private static String EQ_MSG = "Garantizado sin efecto. El importe de los conceptos garantizados (%,.2f) es igual para los per\u00EDodos con I.T que sin ella.";
		private static String ER_MSG = "Garantizado sin efecto. El importe de los conceptos garantizados es mayor para los per\u00EDodos con I.T (%,.2f) que sin ella (%,.2f).";

		public UselessGuaranteeException(double amount) {
			super(String.format(ES, EQ_MSG, amount));
		}

		public UselessGuaranteeException(double amount, double amountIt) {
			super(String.format(ES, ER_MSG, amountIt, amount));
		}

	}

	public static enum Years {
		ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7);

		private int years;

		private Years(int years) {
			this.years = years;
		}

		public int getYears() {
			return years;
		}
	}

	public static enum Guaranteed {
		BR, NET, RAW;

	}

	public static void hide() throws HideException {
		throw new HideException();
	}

	public static void hide(String msg) throws HideException {
		throw new FullHideException(msg);
	}

	public static void remove() throws RemoveException {
		throw new RemoveException();
	}

	public static Double excess(Double amount, ExpressionContext context) {
		List<ITimedVariable<?>> vars = context.getTimedVariables(ContextVariable.ALL);
		if (vars == null || vars.isEmpty())
			return 0.00;
		ITimedVariable<?> var = vars.get(0);
		Double all = (Double) var.getValue(var.getPeriod());
		return Math.max(all - amount, 0.00);
	}

	public static void isDef(String name, String msg, ExpressionContext context) throws CheckException {
		if (!context.isDef(name)) {
			throw new InvalidVariables(msg, name);
		}
	}

	public static void section(Date date) throws MacroException {
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(", ContextVariable.SECTION),
						String.format("%s\\(%s,", _SECTION, ContextVariable.CONTEXT));
			}
		};
	}

	public static void check(boolean condition, String msg) throws CheckException {
		if (!condition) {
			throw new CheckException(msg);
		}
	}

	public static void check(boolean condition, String format, Object... args) throws CheckException {
		if (!condition) {
			throw new CheckException(String.format(format, args));
		}
	}

	public static void checkVar(String name, boolean condition, String msg) throws CheckException {
		if (!condition) {
			throw new InvalidVariables(msg, name);
		}
	}

	public static void warning(String msg) throws CheckException {
		throw new CheckException(msg);
	}


	public static int getMonths(Date start, Date end, double days) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);

		int months = 0;

		while (days > 0 && (startCalendar.compareTo(endCalendar) <= 0)) {
			days -= CommonUtil.daysInMonth(startCalendar.getTime());
			startCalendar.add(Calendar.MONTH, 1);
			months++;
		}

		return months;
	}

	public static void input(String script, String msg) throws CheckException, MacroException{
		
		if (AonStringUtils.isBlank(script))
			throw new CheckException(msg);
		
		try {
			if ( AonStringUtils.isBlank(MVEL.eval(script, String.class)))
				throw new CheckException(msg);
		} catch ( CheckException e){
			throw e;
		}
		catch ( Throwable t ) {
			
		}
		
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return script;
			}
		};
	}

	// ------------------------------------------------------------------------
	// SECTION
	// ------------------------------------------------------------------------

	public static void section(ExpressionContext context, Date date) {
		
		for ( String name : new String[ ]{
				ContextVariable.PREST_IT,
				ContextVariable.CGC_BASE.getName(),
				ContextVariable.CGP_BASE.getName(),
				ContextVariable.MATERNITY_BASE.getName(),
				ContextVariable.STRUCTURAL_OVERTIME_BASE.getName(),
				ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName(),
				})
		{
			for ( ITimedVariable<Object> var : context.getVariables(name) ) {
				if ( var.getPeriod().contains(date)) {
					
					Period varPeriod = var.getPeriod();
					Double varValue = ((Number)var.getValue(varPeriod)).doubleValue();
					long varDays = getDaysBetweenDates(varPeriod.getStart(), varPeriod.getEnd())+ 1;
					
					ITimedVariable<Object> firstVariable = new ITimedVariable<Object>() {
						
						@Override
						public Period getPeriod() {
							return new Period(varPeriod.getStart(), date);
						}
						
						@Override
						public Object getValue(Period period) {
							long days = getDaysBetweenDates(period.getStart(), period.getEnd()) +1;
							return varValue * days / varDays;
						}
					};
					
					context.putVariable(name, firstVariable);

					ITimedVariable<Object> lastVariable = new ITimedVariable<Object>() {
						@Override
						public Period getPeriod() {
							return new Period( AonDateUtils.addDays(date, 1), varPeriod.getEnd());
						}
						
						@Override
						public Object getValue(Period period) {
							long days = getDaysBetweenDates(period.getStart(), period.getEnd()) +1;
							return varValue * days / varDays;
						}
					};
					
					context.putVariable(name, lastVariable);
				} // split;
			}
		}
		
	}
	

	// ------------------------------------------------------------------------
	// ANTIGÜEDAD
	// ------------------------------------------------------------------------

	public static Double old(ExpressionContext context, Double amount, Years years) {
		double seniority = getDouble(context, ContextVariable.SENIORITY);
		return amount * (int) (seniority / years.getYears());
	}

	public static Double seniority(Double amount, Years years) throws MacroException {
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(", ContextVariable.OLD),
						String.format("%s\\(%s,", _OLD, ContextVariable.CONTEXT));
			}
		};
	}

	public static Double old(ExpressionContext context, Double amount, Integer... years) {

		double seniority = getDouble(context, ContextVariable.SENIORITY);

		double old = 0;
		for (int year : years) {
			if (seniority >= year) {
				old += amount;
			}
		}
		return old;
	}

	public static Double seniority() throws MacroException {
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(", ContextVariable.OLD),
						String.format("%s\\(%s,", _OLD, ContextVariable.CONTEXT));
			}
		};
	}

	public static Double seniority(Double amount, Integer year1) throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1) {
		return old(context, amount, new Integer[] { year1 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2) throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2) {
		return old(context, amount, new Integer[] { year1, year2 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3) throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3) {
		return old(context, amount, new Integer[] { year1, year2, year3 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3, Integer year4)
			throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3,
			Integer year4) {
		return old(context, amount, new Integer[] { year1, year2, year3, year4 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3, Integer year4,
			Integer year5) throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3,
			Integer year4, Integer year5) {
		return old(context, amount, new Integer[] { year1, year2, year3, year4, year5 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3, Integer year4,
			Integer year5, Integer year6) throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3,
			Integer year4, Integer year5, Integer year6) {
		return old(context, amount, new Integer[] { year1, year2, year3, year4, year5, year6 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3, Integer year4,
			Integer year5, Integer year6, Integer year7) throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3,
			Integer year4, Integer year5, Integer year6, Integer year7) {
		return old(context, amount, new Integer[] { year1, year2, year3, year4, year5, year6, year7 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3, Integer year4,
			Integer year5, Integer year6, Integer year7, Integer year8) throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3,
			Integer year4, Integer year5, Integer year6, Integer year7, Integer year8) {
		return old(context, amount, new Integer[] { year1, year2, year3, year4, year5, year6, year7, year8 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3, Integer year4,
			Integer year5, Integer year6, Integer year7, Integer year8, Integer year9) throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3,
			Integer year4, Integer year5, Integer year6, Integer year7, Integer year8, Integer year9) {
		return old(context, amount, new Integer[] { year1, year2, year3, year4, year5, year6, year7, year8, year9 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3, Integer year4,
			Integer year5, Integer year6, Integer year7, Integer year8, Integer year9, Integer year10)
					throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3,
			Integer year4, Integer year5, Integer year6, Integer year7, Integer year8, Integer year9, Integer year10) {
		return old(context, amount,
				new Integer[] { year1, year2, year3, year4, year5, year6, year7, year8, year9, year10 });
	}

	public static Double seniority(Double amount, Integer year1, Integer year2, Integer year3, Integer year4,
			Integer year5, Integer year6, Integer year7, Integer year8, Integer year9, Integer year10, Integer year11)
					throws MacroException {
		return seniority();
	}

	public static Double old(ExpressionContext context, Double amount, Integer year1, Integer year2, Integer year3,
			Integer year4, Integer year5, Integer year6, Integer year7, Integer year8, Integer year9, Integer year10,
			Integer year11) {
		return old(context, amount,
				new Integer[] { year1, year2, year3, year4, year5, year6, year7, year8, year9, year10, year11 });
	}

	// ------------------------------------------------------------------------
	// GUARANTEED
	// ------------------------------------------------------------------------
	public static Double guaranteed(ExpressionContext context, Double amount, int start, int end, LeaveType... types) {
		return null;
	}

	public static Double guaranteed(ExpressionContext context, Guaranteed gtzdo, int start, int end,
			LeaveType... types) {
		return null;
	}

	// ------------------------------------------------------------------------
	// PRORATION
	// ------------------------------------------------------------------------

	public static Double proration() throws MacroException{ 
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(", ContextVariable.PRORATION),
						String.format("%s\\(%s, _P,", _PRORATION, ContextVariable.CONTEXT));
			}
		};
	}

	public static Double proration(Double amount) throws MacroException{ 
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(", ContextVariable.PRORATION),
						String.format("%s\\(%s,", _PRORATION, ContextVariable.CONTEXT));
			}
		};
	}


	public static Double proration(ExpressionContext context, Double amount) {
		
		Number guarenteed = ExpressionContext.getCurrentBindings().get(GUARENTEED, v -> (Number)v , 0.00);
		amount -= guarenteed.doubleValue();
		
		if ( amount <= 0.00 )
			return 0.00;

		Object payment = ExpressionContext.getCurrentBindings().get(PAYMENT_VARIABLE);
		

		if ( payment == null )
			return amount / 12.00;
		
		
		while ( payment instanceof IHasPayment<?> )
			payment = ((IHasPayment<?> )payment).getPayment();
		
		if ( !( payment instanceof IExtraPayment) )
			return amount / 12.00;
		

		IExtraPayment extraPayment = (IExtraPayment) payment;
		Period currentPeriod = ExpressionContext.getCurrentBindings().getPeriod();
		Date currentDate = currentPeriod.getEnd();
		
		Calendar extraEndCalendar = parseExtraDate(extraPayment.getExtraEndDate(), currentDate);
		Calendar extraStartCalendar = parseExtraDate(extraPayment.getExtraStartDate(), currentDate);
		
		List<Integer> extraMonths = new ArrayList<Integer>();
		extraStartCalendar.set(Calendar.DATE, 1);
		extraEndCalendar.set(Calendar.DATE, 1);
		while (extraStartCalendar.compareTo(extraEndCalendar)<=0 ) {
			extraMonths.add(extraStartCalendar.get(Calendar.MONTH));
			extraStartCalendar.add(Calendar.MONTH, 1);
		}
		
		if ( !extraMonths.contains(AonDateUtils.get(currentDate, Calendar.MONTH))) 
			return 0.00;
		
		return amount / extraMonths.size()  ;
	}
	
	public static Double fractionate(Double amount) throws MacroException{ 
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(", ContextVariable.FRACTIONATE),
						String.format("%s\\(%s,", _FRACTIONATE, ContextVariable.CONTEXT));
			}
		};
	}


	public static Double fractionate(ExpressionContext context, Double amount) {
		double totalWorkedDays = 0.00;
		for ( ITimedVariable<?> var: context.getVariables(ContextVariable.WORKED_DAYS) )
			totalWorkedDays += ((Number) var.getValue(var.getPeriod())).doubleValue();
		
		if ( totalWorkedDays == 0.00 )
			throw new ExpressionExceptionWrapper(new UndefinedContextVariablesException(ContextVariable.WORKED_DAYS));
		
		double currentWorkedDays = ((Number)ExpressionContext.getCurrentBindings().get(ContextVariable.WORKED_DAYS)).doubleValue();
		
		if ( currentWorkedDays == 0.00 )
			throw new ExpressionExceptionWrapper(new UndefinedContextVariablesException(ContextVariable.WORKED_DAYS));

		return amount * currentWorkedDays / totalWorkedDays;
	}

	private static Calendar parseExtraDate(String str, Date date) {
		Matcher matcher =  Pattern.compile("(?<date>\\d+)/(?<month>\\d+)(\\s+(?<year>[-+]?\\d+))?").matcher(str);
		matcher.matches();
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, Integer.parseInt(matcher.group("date")));
		calendar.set(Calendar.MONTH, Integer.parseInt(matcher.group("month"))-1);
		if ( matcher.group("year") != null )
			calendar.add(Calendar.YEAR, Integer.parseInt(matcher.group("year")));
		
		return calendar;	
	}

	// ------------------------------------------------------------------------
	// Private methods
	// ------------------------------------------------------------------------

	private static double getDouble(ExpressionContext context, ContextVariable var) {
		PeriodMap bindings = ExpressionContext.getCurrentBindings();
		try {
			Object value = bindings.get(var.getName());
			return value == null ? 0.00 : ((Number) value).doubleValue();
		} catch (Exception e) {
			try {
				for (ITimedResult<Double> result : context.eval(var.getName(), bindings.getPeriod().getStart(),
						bindings.getPeriod().getEnd(), Double.class)) {
					Object value = bindings.get(var.getName()); // read ???
					if ( result.getValue() != null) 
						return result.getValue() ;
				}
			} catch (Throwable t) {

			}
			throw e;
		}

	}

	// ------------------------------------------------------------------------
	// Private Static methods (library)
	// ------------------------------------------------------------------------
	private static void loadSectionFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {

		// WARNING function
		try {
			Method section = ContextFunctions.class.getMethod("section", Date.class);

			MethodStub sectionStub = new MethodStub(section);
			context.setVariable(SECTION, sectionStub, startDate, endDate);
			
			Method _section = ContextFunctions.class.getMethod("section", ExpressionContext.class, Date.class);

			MethodStub _sectionStub = new MethodStub(_section);
			context.setVariable(_SECTION, _sectionStub, startDate, endDate);
			
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadHideFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		try {
			Method remove = ContextFunctions.class.getMethod("hide");

			MethodStub removeStub = new MethodStub(remove);

			context.setVariable(ContextVariable.HIDE, removeStub, startDate, endDate);
			
			context.setVariable("PARENT", removeStub, startDate, endDate);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void loadRemoveFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		try {
			Method remove = ContextFunctions.class.getMethod("remove");

			MethodStub removeStub = new MethodStub(remove);

			context.setVariable(ContextVariable.REMOVE, removeStub, startDate, endDate);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void loadMonthsFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		try {

			Method months = ContextFunctions.class.getMethod("getMonths", Date.class, Date.class, double.class);

			MethodStub monthsStub = new MethodStub(months);

			context.setVariable(MONTHS_IMPL, monthsStub, startDate, endDate);

			String functionScript = String.format("%s = def (days) { %s(%s, %s, days) };", MONTHS, MONTHS_IMPL, START,
					END);

			context.eval(functionScript, startDate, endDate);

		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadWarnFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {

		// WARNING function
		try {
			Method warning = ContextFunctions.class.getMethod("warning", String.class);

			MethodStub warningStub = new MethodStub(warning);

			context.setVariable(WARNING, warningStub, startDate, endDate);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadCheckFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {

		// WARNING function
		try {
			Method check = ContextFunctions.class.getMethod("check", boolean.class, String.class);

			MethodStub warningStub = new MethodStub(check);

			context.setVariable(CHECK, warningStub, startDate, endDate);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadInputFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {

		// WARNING function
		try {
			Method input = ContextFunctions.class.getMethod("input", String.class, String.class);

			MethodStub inputStub = new MethodStub(input);

			context.setVariable(INPUT, inputStub, startDate, endDate);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadCheckVarFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {

		// WARNING function
		try {
			Method checkVar = ContextFunctions.class.getMethod("checkVar", String.class, boolean.class, String.class);

			MethodStub warningStub = new MethodStub(checkVar);

			context.setVariable(ContextVariable.CHECK_VAR, warningStub, startDate, endDate);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadIsDefFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		try {
			Method isDef = ContextFunctions.class.getMethod("isDef", String.class, String.class,
					ExpressionContext.class);

			MethodStub isDefStub = new MethodStub(isDef);
			context.setVariable("ISDEF", isDefStub, startDate, endDate);
			String functionScript = String.format("%s = def(variable, msg) { ISDEF(variable, msg, %s) };",
					ContextVariable.ISDEF, ContextVariable.CONTEXT);

			context.eval(functionScript, startDate, endDate);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadExcessFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		try {
			Method isDef = ContextFunctions.class.getMethod("excess", Double.class, ExpressionContext.class);
			MethodStub excessStub = new MethodStub(isDef);
			context.setVariable("_EXCESS", excessStub, startDate, endDate);
			String functionScript = String.format("%s = def(amount){ _EXCESS(amount, %s) };", ContextVariable.EXCESS,
					ContextVariable.CONTEXT);
			context.eval(functionScript, startDate, endDate);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadSeniorityFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		try {
			context.setVariable(ContextVariable.ONE, Years.ONE, startDate, endDate);
			context.setVariable(ContextVariable.TWO, Years.TWO, startDate, endDate);
			context.setVariable(ContextVariable.THREE, Years.THREE, startDate, endDate);
			context.setVariable(ContextVariable.FOUR, Years.FOUR, startDate, endDate);
			context.setVariable(ContextVariable.FIVE, Years.FIVE, startDate, endDate);
			context.setVariable(ContextVariable.SIX, Years.SIX, startDate, endDate);
			context.setVariable(ContextVariable.SEVEN, Years.SEVEN, startDate, endDate);

			Method old = ContextFunctions.class.getMethod("old", ExpressionContext.class, Double.class, Integer.class);
			MethodStub oldStub = new MethodStub(old);
			context.setVariable(_OLD, oldStub, startDate, endDate);

			Method seniority = ContextFunctions.class.getMethod("seniority");
			MethodStub seniorStub = new MethodStub(seniority);
			context.setVariable(ContextVariable.OLD, seniorStub, startDate, endDate);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void loadProrationFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {

		// WARNING function
		try {
			Method _proration = ContextFunctions.class.getMethod("proration", ExpressionContext.class, Double.class);
			MethodStub _prorationStub = new MethodStub(_proration);
			context.setVariable(_PRORATION, _prorationStub, startDate, endDate);

			Method proration = ContextFunctions.class.getMethod("proration", Double.class);
			MethodStub prorationStub = new MethodStub(proration);
			context.setVariable(ContextVariable.PRORATION, prorationStub, startDate, endDate);

		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private static void loadFractionateFunction(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		try {
			Method _fractionate = ContextFunctions.class.getMethod("fractionate", ExpressionContext.class, Double.class);
			MethodStub _FractionateStub = new MethodStub(_fractionate);
			context.setVariable(_FRACTIONATE, _FractionateStub, startDate, endDate);
			Method fractionate = ContextFunctions.class.getMethod("fractionate", Double.class);
			MethodStub fractionateStub = new MethodStub(fractionate);
			for ( Period p: context.getPeriods(ContextVariable.WORKED_DAYS))
				context.setVariable(ContextVariable.FRACTIONATE, fractionateStub, p.getStart(), p.getEnd());
			
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}

	}

	public static void loadFunctions(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		loadInputFunction(context, startDate, endDate);
		loadCheckFunction(context, startDate, endDate);
		loadCheckVarFunction(context, startDate, endDate);
		loadWarnFunction(context, startDate, endDate);
		loadMonthsFunction(context, startDate, endDate);
		loadIsDefFunction(context, startDate, endDate);
		loadHideFunction(context, startDate, endDate);
		loadRemoveFunction(context, startDate, endDate);
		loadExcessFunction(context, startDate, endDate);
		loadSeniorityFunction(context, startDate, endDate);
		loadSectionFunction(context, startDate, endDate);
		loadProrationFunction(context, startDate, endDate);
	}
	
	public static void loadDaysFunctions(ExpressionContext context, Date startDate, Date endDate)
			throws ExpressionException {
		loadFractionateFunction(context, startDate, endDate);
	}

	public static void main(String[] args) throws Throwable {
		Pattern pattern = Pattern.compile("(?<day>\\d+)/(?<month>\\d+)(\\s+(?<year>[-+]?\\d+))?");
		Matcher matcher =  pattern.matcher("1/12");
		if ( matcher.matches() ) {
			int day = Integer.parseInt(matcher.group("day"));
			int month = Integer.parseInt(matcher.group("month"));
			int year = matcher.group("year") == null ? 0 : Integer.parseInt(matcher.group("year"));
			
		}
		
		
		
	}
	
}
