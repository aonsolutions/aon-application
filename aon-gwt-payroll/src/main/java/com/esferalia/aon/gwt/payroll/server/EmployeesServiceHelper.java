package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.in.payroll.tgss.idc.Idc.isBonus;
import static com.esferalia.aon.in.payroll.tgss.idc.IdcHighlighter.ERROR;
import static com.esferalia.aon.in.payroll.tgss.idc.IdcHighlighter.WARN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_ENTERPRISE_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_16_20;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_1_3;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_21;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_366;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS_4_15;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATIONAL_DISEASE_DAYS_366;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.watson.util.AonDateUtils.add;
import static java.util.Calendar.DAY_OF_MONTH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.mvel2.CompileException;
import org.mvel2.MVEL;

import com.code.aon.common.AonException;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.Event;
import com.esferalia.aon.gwt.payroll.shared.Event.Type;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.OutOfDateException;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.PaymentEvent;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SaltraCredentialsNotFoundException;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.sql.SQLAgreementDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLSalaryDraftCalculatorContext;
import com.esferalia.aon.gwt.payroll.sql.SQLSettleDraftCalculatorContext;
import com.esferalia.aon.in.payroll.SistemaRED2AON;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.tgss.idc.AllIdcHighlighter;
import com.esferalia.aon.in.payroll.tgss.idc.Idc.IdcContractData;
import com.esferalia.aon.in.payroll.tgss.idc.IdcHighlighter;
import com.esferalia.aon.in.payroll.tgss.idc.IdcHighlighter.Setup;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.Occupation;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.GenericContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.ISystemPayment;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementContextFactory;
import com.esferalia.aon.payroll.calculator.sql.SQLContractDelayCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractExtraCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractPPECalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.AgreementContextKey;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.CCCContextKey;
import com.esferalia.aon.payroll.calculator.sql.SQLSystemExpressionContextFactory;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.FullHideException;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InterruptedException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;
//import com.google.api.client.util.Objects;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.ForbiddenException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.NoQueryData;
import solutions.aon.seg.social.exception.invalid.NotAllowedContributionAccount;
import solutions.aon.seg.social.object.Idc;

public class EmployeesServiceHelper {

	private static final BigDecimal TEN = new BigDecimal(10);

	
	public static final String REMOVE = "REMOVE()";
	
	
	@FunctionalInterface
	private interface ThrowableRunnable<T extends Exception> {
	   void apply() throws T;
	}
	
	private static class TooManyValuesException extends Exception {
		
	}

	public static String getTA(Connection connection, String domainName, Integer domainId, String userLogin, Integer userId, Integer contractId) throws SQLException, IOException, SegSocialException{
		
		Contract contract = 
		PAYROLL.
		getContract(domainName, domainId, userLogin, p -> p.getIdProperty().eq(contractId))
		.orElseThrow(() -> new IOException() );
		Date date = contract.getStartDate();
		String ccc = contract.getEnterpriseCCC();
		String naf = contract.getPersonSsNumber();
		String regime = contract.getEnterpriseCCCRegime().getCode();	
		
//		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
		byte data [] =  SistemaRED.getTA(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf, date);
		return Base64.getEncoder().encodeToString(data);
	}



	public static String getIDC(Connection connection, String domainName, Integer domainId, String userLogin, Integer userId, Integer contractId, Date date) throws SQLException, IOException, SegSocialException{
		
		Contract contract = 
		PAYROLL.
		getContract(domainName, domainId, userLogin, p -> p.getIdProperty().eq(contractId))
		.orElseThrow(() -> new IOException() );
		String ccc = contract.getEnterpriseCCC();
		String naf = contract.getPersonSsNumber();
		String regime = contract.getEnterpriseCCCRegime().getCode();	
		
//		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

		byte data [] =  SistemaRED.getIDC(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf, date);
		return Base64.getEncoder().encodeToString(data);
	}

	public static String getIDCNSS(Connection connection, String domainName, Integer domainId, String userLogin, Integer userId, Integer contractId, Date date) throws SQLException, IOException, SegSocialException{
		
		Contract contract = 
		PAYROLL.
		getContract(domainName, domainId, userLogin, p -> p.getIdProperty().eq(contractId))
		.orElseThrow(() -> new IOException() );
		String ccc = contract.getEnterpriseCCC();
		String naf = contract.getPersonSsNumber();
		String regime = contract.getEnterpriseCCCRegime().getCode();	
		
//		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
		
		byte data [] =  SistemaRED.getIDCNSS(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf, date);
		return Base64.getEncoder().encodeToString(data);
	}
	

	public static String checkIDCNSS(Connection connection, String domainName, Integer domainId, String userLogin, Integer userId, Integer contractId, Date date, String idcBase64 ) throws SQLException, IOException, SegSocialException, UnknownPDFException{
		
		class SalaryItemResult extends TimedResult<Double> {
			
			private String expression;
			private String description;
			
			public SalaryItemResult(Double value, Period period, Map<String, ITimedVariable<?>> context, String description, String expression) {
				super(value, period, context);
				this.expression = expression;
				this.description = description;
			}
			
			public String getDescription() {
				return description;
			}

			public String getExpression() {
				return AonStringUtils.defaultString(expression, "").replaceAll("/\\*.*\\*/", "");
			}
			
		}
		
		byte [] idcData = Base64.getDecoder().decode(idcBase64);
		
		byte[] idcHighlightData = IdcHighlighter.highlight(idcData, new AllIdcHighlighter() {
			
			private List<ThrowableRunnable<IOException>> delayed = new LinkedList<>();

			private java.sql.Date idcEndDate;
			private java.sql.Date idcStartDate;
			
			private SQLContractSalaryCalculatorContext ctx = null;
			private com.esferalia.aon.payroll.Salary salary = null;
			private List<SalaryItemResult> salaryBonus = new ArrayList<>();
			
			private Number parseNumber(String str) {
				return AonNumberUtils.todouble(str.replace(',', '.'));
			}

			private java.sql.Date parseDate(String string) throws IOException {
				if ( AonStringUtils.isBlank(string))
					return new java.sql.Date(AonDateUtils.getLastDayOfMonth(new Date()).getTime());
				try {
					Date date = new SimpleDateFormat("dd-MM-yyyy").parse(string);
					return new java.sql.Date(date.getTime());
				} catch (ParseException e) {
					throw new IOException(e);
				}		
			}
			
			private String formatDate(Date date){
					return new SimpleDateFormat("dd-MM-yyyy").format(date);
			}

			private String formatPeriod(Period period){
				 MessageFormat periodForm = new MessageFormat("{0}..{1} ( {2} )");
				 ChoiceFormat daysForm = new ChoiceFormat(new double[]{1,2}, new String[]{"1 día","{2,number} días"});
				 periodForm.setFormatByArgumentIndex(2, daysForm);
				 return periodForm.format(new Object[] {formatDate(period.getStart()), formatDate(period.getEnd()), period.getDays()});
			}
			
			// ------------------------------------------------------- Disabled
			@Override
			public void onEmployeeName(String name, IdcHighlighter idcHighlighter) throws IOException {
				// Employee's name really don´t matter so much 
			}

			@Override
			public void onEnterpriseName(String name, IdcHighlighter idcHighlighter) throws IOException {
				// Enterprise's name really don´t matter so much 
			}
			
			@Override
			public void onEmployeeBirthDate(String date, IdcHighlighter idcHighlighter) throws IOException {
				// Employee's birth date really don´t matter so much
				
			}
			
			// -------------------------------------------------------- Delayed
			
			@Override
			public void onEnterpriseIpf(String type, String ipf, IdcHighlighter idcHighlighter) throws IOException {
				delayed.add ( () -> {
					if ( !AonStringUtils.equalsIgnoreCase(ctx.getEnterpriseDocument(), ipf) )
						idcHighlighter.highlight(ipf, ERROR);
				});
			}
			
			@Override
			public void onEnterpriseCCC(String ccc, IdcHighlighter idcHighlighter) throws IOException {
				delayed.add ( () -> {
					
					String ctxCcc = ctx.getCcc();

					String province =AonStringUtils.substring(ccc, 0, 2);  
					String ctxProvince =AonStringUtils.substring(ctxCcc, 0, 2);  

					if (!AonStringUtils.equalsIgnoreCase(ctxProvince, province) ) 
						idcHighlighter.highlight(province, ctxProvince, ERROR );
					
					String num =AonStringUtils.substring(ccc, 2);  
					String ctxNum =AonStringUtils.substring(ctxCcc, 2);  

					if (!AonStringUtils.equalsIgnoreCase(ctxNum, num) ) 
						idcHighlighter.highlight(num, ctxNum, ERROR );
				});
			}
			
			@Override
			public void onEnterpriseActivity(String code, String description, IdcHighlighter idcHighlighter)
					throws IOException {
				delayed.add ( () -> {
					Integer cnae2009 = AonNumberUtils.toInteger(code);
					if ( !AonNumberUtils.equals(ctx.getCnae2009(), cnae2009 ) ) {
						idcHighlighter.highlight(code, description, AonStringUtils.defaultIfBlank(AonNumberUtils.toString(ctx.getCnae2009()) , "EMPRESA SIN ACTIVIDAD ECONÓMICA"), ERROR);
					}
				});
			}
			
			@Override
			public void onEnterpriseRegime(String regime, IdcHighlighter idcHighlighter) throws IOException {
				delayed.add ( () -> {
					String regex =	getRegimeRegex(ctx.getCCCType());  
					if ( !AonStringUtils.containsMatching(regime, regex) ) {
						idcHighlighter.highlight(escapeLiteral(regime), ctx.getCCCType().getName(new Locale("es_ES")), ERROR);
					}
				});
			}
			
			@Override
			public void onEmployeeIpf(String type, String ipf, IdcHighlighter idcHighlighter) throws IOException {
				delayed.add ( () -> {
					if ( !documentsEquals(ctx.getEmployeeDocument(), ipf) )
						idcHighlighter.highlight(ipf, AonStringUtils.defaultIfBlank(ctx.getEmployeeDocument() , "TRABAJADOR SIN DOCUMENTO DE IDENTIDAD"), ERROR);
				});
			}
			

			@Override
			public void onEmployeeNaf(String province, String num, IdcHighlighter idcHighlighter) throws IOException {
				delayed.add ( () -> {
					String employeeSSNumber = ctx.getSocialSecurityNumber();
					String employeeProvince = employeeSSNumber.substring(0,2);
					String employeeNum = employeeSSNumber.substring(2);
					if ( !AonStringUtils.equalsIgnoreCase(employeeProvince, province) )
						idcHighlighter.highlight(province, ERROR);
					if ( !AonStringUtils.equalsIgnoreCase(employeeNum, num) )
						idcHighlighter.highlight(num, ERROR);
				});
			}
			
			// ----------------------------------------------------------- Live
			
			@Override
			public void onIdcPeriod(String start, String end, IdcHighlighter idcHighlighter) throws IOException {
				this.idcStartDate = parseDate(start);
				this.idcEndDate = parseDate(end);
				
				 try {
					this.ctx = getContractSalaryCalculatorContext(connection, contractId, idcStartDate, idcEndDate, idcEndDate);
					
					for( ThrowableRunnable<IOException> t : delayed) {
						t.apply();
					}
				} catch (ExpressionException | SQLException e) {
					// highlight period...
					super.onIdcPeriod(start, end, idcHighlighter);
				}
			}
			
			@Override
			public void onContractCCC(String ccc, IdcHighlighter idcHighlighter) throws IOException {
				String ctxCcc = ctx.getCcc();
				if ( AonStringUtils.isBlank(ccc)) { 
					idcHighlighter.insert("C.C.C.:", ctxCcc, ERROR);
				} else {
					String regime =AonStringUtils.substring(ccc, 0, 4);  
					String ctxRegime =	getCCCRegimeCode(ctx.getCCCType());  

					if (!AonStringUtils.equalsIgnoreCase(ctxRegime, regime) ) 
						idcHighlighter.highlight(regime, ctxRegime, ERROR );

					String province =AonStringUtils.substring(ccc, 4, 6);  
					String ctxProvince =AonStringUtils.substring(ctxCcc, 0, 2);  

					if (!AonStringUtils.equalsIgnoreCase(ctxProvince, province) ) 
						idcHighlighter.highlight(province, ctxProvince, ERROR );
					
					String num =AonStringUtils.substring(ccc, 6);  
					String ctxNum =AonStringUtils.substring(ctxCcc, 2);  

					if (!AonStringUtils.equalsIgnoreCase(ctxNum, num) ) 
						idcHighlighter.highlight(num, ctxNum, ERROR );
				}
			}
			
			@Override
			public void onStart(String date, IdcHighlighter idcHighlighter) throws IOException {
				checkDate(ContextVariable.CONTRACT_START, date, idcHighlighter, WARN );
			}

			@Override
			public void onContractStart(String date, IdcHighlighter idcHighlighter) throws IOException {
				checkDate(ContextVariable.CONTRACT_START, date, idcHighlighter, WARN );
			}
			
			@Override
			public void onQuoteGroup(String group, String monthly, IdcHighlighter idcHighlighter) throws IOException {
				checkString(ContextVariable.QUOTE_GROUP, group, idcHighlighter, isSubmitted(idcEndDate) ? WARN : ERROR);
				
				if ( AonStringUtils.equalsIgnoreCase("s", monthly) ) {
					
					List<ITimedVariable<Number>> monthDaysList = getVars(ContextVariable.MONTH_DAYS);
					Collections.reverse(monthDaysList);
					for (ITimedVariable<Number> monthDays : monthDaysList) {
						int ctxMonthDays = monthDays.getValue(monthDays.getPeriod()).intValue();
						if ( ctxMonthDays !=  30 ) {
							Date endDate = monthDays.getPeriod().getEnd();
							idcHighlighter.highlight(
							monthly, 
							"REVISE "+ new SimpleDateFormat("MMMM' de 'yyyy").format(endDate).toUpperCase() + ", TIENE COTIZACIÓN DIARIA",
							isSubmitted(endDate) ? WARN : ERROR);
						}
					}
					
				}
			}
			
			@Override
			public void onContractType(String code, String description, IdcHighlighter idcHighlighter)
					throws IOException {
				String tc2;
				try {
					tc2 = getValue(ContextVariable.TC2, String.class );
					if ( !AonStringUtils.equalsIgnoreCase(tc2, code) ) {
						String tc2Description = getContractDescription(tc2);
						idcHighlighter.highlight(code, description, AonStringUtils.defaultIfBlank(tc2 + " " + tc2Description, "TRABAJADOR SIN CONTRATO"), isSubmitted(idcEndDate) ? WARN : ERROR);
					}
				} catch (TooManyValuesException e) {
					idcHighlighter.highlight(code, description, isSubmitted(idcEndDate) ? WARN : ERROR);
				}
			}
			
			@Override
			public void onOccupation(String occupation, String description, IdcHighlighter idcHighlighter)
					throws IOException {
				String ctxOccupation = null;
				try {
					ctxOccupation = getValue(ContextVariable.OCCUPATION, String.class );
					ctxOccupation = AonStringUtils.defaultIfBlank(ctxOccupation);
					if ( !AonStringUtils.equalsIgnoreCase(occupation, ctxOccupation) ) {
						String ctxDescription = getOccupationDescription(ctxOccupation);
						if ( AonStringUtils.isBlank(occupation))
							idcHighlighter.insert("OCUPACION\\*:", ctxDescription, isSubmitted(idcEndDate) ? WARN : ERROR);
						else 
							idcHighlighter.highlight(occupation+"\\s+"+description, AonStringUtils.defaultIfBlank(ctxDescription, "TRABAJADOR SIN OCUPACIÓN"), isSubmitted(idcEndDate) ? WARN : ERROR);
					}
				} catch (TooManyValuesException e) {
					idcHighlighter.highlight(occupation, description, isSubmitted(idcEndDate) ? WARN : ERROR);
				}
			}

			@Override
			public void onQuotes(String it, String ims, String unemployment, IdcHighlighter idcHighlighter)
					throws IOException {
				checkNumber(ContextVariable.IT_RATE, it, idcHighlighter, isSubmitted(idcEndDate) ? WARN : ERROR);
				checkNumber(ContextVariable.IMS_RATE, ims, idcHighlighter, isSubmitted(idcEndDate) ? WARN : ERROR);
				checkNumber(String.format("%s + %s", ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT ), unemployment, idcHighlighter, isSubmitted(idcEndDate) ? WARN : ERROR);
			}
			
			@Override
			public void onCoefficient(String partial, String reduction, IdcHighlighter idcHighlighter)
					throws IOException {
				try {
					BigDecimal partialFactor = BigDecimal.valueOf(AonNumberUtils.toDouble(AonStringUtils.defaultIfBlank(partial, "1000")));
					BigDecimal ctxPartialFactor = BigDecimal.valueOf(getValue(ContextVariable.PARTIAL_FACTOR, Double.class)).multiply(BigDecimal.valueOf(1000L));
					if ( AonNumberUtils.equals(partialFactor, ctxPartialFactor) )
						return ; // Nothing
					else if (AonStringUtils.isBlank(partial) )
						idcHighlighter.insert("COEF\\.\\s*TIEMPO\\s*PARCIAL\\s*:", new DecimalFormat("###.##").format(ctxPartialFactor), isSubmitted(idcEndDate) ? WARN : ERROR);
					else 
						idcHighlighter.highlight(partial, new DecimalFormat("###.##").format(ctxPartialFactor), isSubmitted(idcEndDate) ? WARN : ERROR);
				} catch (TooManyValuesException e) {
					idcHighlighter.insert("COEF\\.\\s*TIEMPO\\s*PARCIAL\\s*:", "", isSubmitted(idcEndDate) ? WARN : ERROR);
				} 

			}
			
			// ----------------------------------------------------------- PECs
			
			@Override
			public void onPEC(String code, String description, String tipo, String quota, String start, String end,
					IdcHighlighter idcHighlighter) throws IOException {
				
				if ( isBonus(code, quota) )
					checkBonus(code, description, tipo, quota, start, end, idcHighlighter);
				
				if ( isIT(code, quota) )
					checkIT(code, description, start, end, idcHighlighter);
				
			}

			
			// -------------------------------------------------------- Private
			
			private boolean isIT(String code, String quota) {
				switch (code) {
				case "21":
				case "22":
				case "23":
				case "24":
				
				case "29":

				case "31":
					return true;
				default:
					return false;
				}
			}
			
			private void calculate() throws SalaryException {
				if ( salary == null ) {
					salary = new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>(new SalaryBuilder() {
						public void addBonus(Double amount, String description, Date startDate, Date endDate, IBonus bonus, java.util.Map<String,ITimedVariable<?>> context) {
							salaryBonus.add(new SalaryItemResult(amount, new Period(startDate, endDate), context, description, bonus.getExpression()));
						}
					}).calculate(ctx);
				}
			}
			
			public List<SalaryItemResult> getSalaryBonus() throws SalaryException {
				calculate();
				return salaryBonus;
			}
			
			private List<ITimedResult<Double>> getPECBonus(String code, String description, String tipo, String quota, String start, String end) 
			throws ParseException, ExpressionException, IOException  {
				String expression;
				expression = com.esferalia.aon.in.payroll.tgss.idc.Idc.getBonusExpression(code, tipo, quota);
				return ctx.getExpressionContext().eval(expression, parseDate(start), parseDate(end), Double.class );
			}

			private void checkIT(String code, String description, String start, String end, IdcHighlighter idcHighlighter)
					throws IOException {
				ITimedVariable<?> itDays = null; 
				// ITs 
				Date startDate = parseDate(start);
				Date endDate = parseDate(end);
				Period period = new Period(startDate, endDate);
				
				if (AonStringUtils.equalsIgnoreCase(code, "29")) { // IT.CC.COLAB.EXCL.15D
					 itDays = getDays(startDate, endDate, COMMON_DISEASE_DAYS_1_3, COMMON_DISEASE_DAYS_4_15);
				} else if (AonStringUtils.equalsIgnoreCase(code, "21")) {  // IT.CC.PAGO DELEGADO
					 itDays = getDays(startDate, endDate, COMMON_DISEASE_DAYS_16_20, COMMON_DISEASE_DAYS_21 );
				} else if (AonStringUtils.equalsIgnoreCase(code, "22")) {  // IT.CC.PAGO DIRECTO
					 itDays = getDays(startDate, endDate, COMMON_DISEASE_DAYS_366);
				} 
				
				else if (AonStringUtils.equalsIgnoreCase(code, "23")) { // IT.AT.PAGO DELEGADO
					 itDays = getDays(startDate, endDate, ContextVariable.OCCUPATIONAL_DISEASE_DAYS );
				} else if (AonStringUtils.equalsIgnoreCase(code, "24")) { // IT.AT.PAGO DIRECTO
					 itDays = getDays(startDate, endDate, OCCUPATIONAL_DISEASE_DAYS_366 );
				} 
				
				else if (AonStringUtils.equalsIgnoreCase(code, "31")) { // MATERNIDAD/PATERNIDAD TIEMPO COMPLETO
					itDays = getDays(startDate, endDate, MATERNITY_DAYS, PATERNITY_DAYS );
				} else if (AonStringUtils.equalsIgnoreCase(code, "34")) { // RIESGO DURANTE EL EMBARAZO
					itDays = getDays(startDate, endDate, MATERNITY_DAYS );
				}
				
				if ( itDays == null ) {
					 idcHighlighter.highlightAll(String.format("%s NO ENCONTRADA." , description), isSubmitted(end) ? WARN : ERROR);
				} else if( itDays.getPeriod().compareTo(period) != 0 ) {
					 idcHighlighter.highlightAll(String.format("%s ERRÓNEA. %s" , description, formatPeriod(itDays.getPeriod())), isSubmitted(end) ? WARN : ERROR);
				} else {
					 idcHighlighter.annotateAll(String.format("%s . %s", description, formatPeriod(itDays.getPeriod())));
				}
			}

			private void checkBonus(String code, String description, String tipo, String quota, String start, String end,
					IdcHighlighter idcHighlighter) throws IOException {
				
				List<SalaryItemResult> salaryBonus = null;
				try {
					salaryBonus = getSalaryBonus();
				} catch (SalaryException e) {
					idcHighlighter.highlightAll("NO SE HAN PODIDO CALCULAR LAS BONIFICACIONES. REVISE EL BORRADOR.", isSubmitted(end) ? WARN : ERROR );
				}
				
				List<ITimedResult<Double>> pecBonus = null;
				try {
					pecBonus = getPECBonus(code, description, tipo, quota, start, end);
				} catch (ExpressionException | ParseException | IOException e1) {
					idcHighlighter.highlightAll("LO SENTIMOS. BONIFICACIÓN NO SOPORTADA. CONTACTE CON AON.", isSubmitted(end) ? WARN : ERROR);
					return;
				}
				
				for ( SalaryItemResult bonus: salaryBonus ) {
					if ( pecBonus.removeIf(ifEqualsTo(bonus)) ) {
						idcHighlighter.annotateAll("BONIFICACIÓN ENCONTRADA," + AonStringUtils.upperCase(bonus.getDescription()) + "'" + bonus.getExpression() + "'" );
					}
				}
				
				if ( !pecBonus.isEmpty() ) {
					idcHighlighter.highlightAll("BONIFICACIÓN NO ENCONTRADA. REVISE LAS BONIFICACIONES Y/O PECULIARIDADES", isSubmitted(end) ? WARN : ERROR);
				} else {
				}
				
				
			}
			
			private Predicate<ITimedResult<Double>> ifEqualsTo (ITimedResult<Double> result ) {
				return r -> AonNumberUtils.compare(r.getValue(), result.getValue(), 1) == 0;
			}

			private void checkDate(ContextVariable var, String string, IdcHighlighter idcHighlighter, Setup setup ) throws IOException {
				check(var.getName(), string, parseDate(string), Date.class, (d1,d2) -> Objects.equals(d1, d2), idcHighlighter, setup);
			}

			private void checkString(ContextVariable var, String string, IdcHighlighter idcHighlighter, Setup setup) throws IOException {
				check(var.getName(), string, string, String.class, AonStringUtils::equalsIgnoreCase, idcHighlighter, setup);
			}

			private void checkNumber(ContextVariable var, String string, IdcHighlighter idcHighlighter, Setup setup ) throws IOException {
				check(var.getName(), string, parseNumber(string), Number.class, AonNumberUtils::equals, idcHighlighter, setup);
			}
			
			private void checkNumber(String expression, String string, IdcHighlighter idcHighlighter, Setup setup ) throws IOException {
				check(expression, string, parseNumber(string), Number.class, AonNumberUtils::equals, idcHighlighter, setup);
			}

			private <T> void check(String expression, String string, T t, Class<T> clazz, BiFunction<T, T, Boolean> equals,  IdcHighlighter idcHighlighter, Setup setup ) throws IOException {
				try {
					T value = getValue(expression, clazz);
					if ( !equals.apply(value, t))
						idcHighlighter.highlight(string, toString(value, "TRABAJADOR SIN " + AonStringUtils.upperCase(expression)), setup);
				} catch ( TooManyValuesException e) {
					idcHighlighter.highlight(string, "", setup);
				}
			}
			
			private <T> T getValue( ContextVariable var, Class<T> type) throws TooManyValuesException{
				return getValue(var.getName(), type);
			}
			
			private <T> T getValue( String name, Class<T> type) throws TooManyValuesException{
				List<ITimedResult<T>> vars;
				try {
					vars = ctx.getExpressionContext().eval(name, ctx.getStartDate(), ctx.getEndDate(), type);
				} catch (ExpressionException e) {
					return null;
				}
				List<T> values = vars.stream().map(ITimedResult::getValue).distinct().collect(Collectors.toList());
				if ( values.isEmpty()) 
					return null;
				else if ( values.size() > 1)
					throw new TooManyValuesException();
				else 
					return values.get(0);
			}
			
			private String getContractDescription(String tc2) {
				if ( AonStringUtils.isBlank(tc2)) 
					return "";
				try {
					return new ContractType().getContractType(Integer.parseInt(tc2)).getContractTypeDescription();
				} catch ( Exception  e) {
					return "TIPO CONTRATO DESCONOCIDO";
				}
			}
			
			private String getOccupationDescription(String occupation) {
				if ( AonStringUtils.isBlank(occupation)) 
					return "";
				try {
					return Occupation.getOccupation().getOrDefault(occupation.toLowerCase(), ( occupation + ". OCUPACIÓN DESCONOCIDA").toUpperCase() );
				} catch ( Exception  e) {
					return occupation + ". OCUPACIÓN DESCONOCIDA";
				}
			}

			private <T> String toString(T t, String nullDefault) {
				if ( t instanceof Date ) {
					return new SimpleDateFormat("dd-MM-yyyy").format((Date)t);
				} else if( t instanceof Number) {
					return new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(new Locale("ES"))).format(t);
				}
				else {
					return java.util.Objects.toString(t, nullDefault);
				}
			}
			
			private boolean documentsEquals(String doc1, String doc2) {
				if ( Objects.equals(doc1, doc2) ) 
					return true;
				else if ( doc1 == null ) 
					return false;
				else if ( doc2 == null)
					return false; 

				return doc1.trim().replaceFirst("^0+", "").equalsIgnoreCase( doc2.trim().replaceFirst("^0+", ""));
			}
			
			
			private ITimedVariable<?> getDays(Date startDate, Date endDate, ContextVariable ...vars) {
				Date prevDate = add(startDate, DAY_OF_MONTH,-1);
				ITimedVariable<?> days =  
				Arrays.stream(vars)
				.map(ContextVariable::getName)
				.map(name -> ctx.getExpressionContext().getVariables(name, startDate, endDate))
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.sorted((r1,r2) -> r1.getPeriod().compareTo(r2.getPeriod()))
				.reduce(new TimedObject<>(0.00, prevDate, prevDate), 
						(r1,r2) -> {
							Period p1 = r1.getPeriod();
							if ( p1.getEnd() == null )
								return r1; 
								
							Period p2 = r2.getPeriod();
							Date nextDate = add(p1.getEnd(), DAY_OF_MONTH,1);
							if ( nextDate.before(p2.getStart()) )
								return r1; 
							
							Number v1 = (Number) r1.getValue(r1.getPeriod());
							Number v2 = (Number) r2.getValue(r2.getPeriod());
							
							return new TimedObject<>(v1.doubleValue()+v2.doubleValue(), Period.max(startDate, p1.getStart()), Period.min( endDate, p2.getEnd()));
						} 
				);
				
				return days.getPeriod().intersects(new Period(startDate, endDate)) ? days : null;
			}
			
			private <T>  List<ITimedVariable<T>> getVars(ContextVariable var) {
				List<ITimedVariable<T>> vars = ctx.getExpressionContext().getVariables(var);
				Collections.sort(vars, (v1,v2) -> v1.getPeriod().compareTo(v2.getPeriod()));
				return vars;
			}


			private boolean isSubmitted(String endDate) throws IOException {
				return isSubmitted(parseDate(endDate));
			}

			private boolean isSubmitted(Date endDate) {
				Date today = new Date();
				Date firstDayOfCurrentMonth =  AonDateUtils.getFirstDayOfMonth(today);
				Date firstDayOfPreviousMonth = AonDateUtils.add(firstDayOfCurrentMonth, Calendar.MONTH, -1);
				return endDate.before(firstDayOfPreviousMonth);
			}
			
			
		});
		
		return Base64.getEncoder().encodeToString(idcHighlightData);
	}

	public static List<Date> getIDCDates(Connection connection, String domainName, Integer domainId, String userLogin, Integer userId, Integer contractId, Date date) throws IOException, SegSocialException{
		
		Contract contract = 
		PAYROLL.
		getContract(domainName, domainId, userLogin, p -> p.getIdProperty().eq(contractId))
		.orElseThrow(() -> new IOException() );
		String ccc = contract.getEnterpriseCCC();
		String naf = contract.getPersonSsNumber();
		String regime = contract.getEnterpriseCCCRegime().getCode();	
		
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

		List<Date> dates = new ArrayList<Date>();
		Collection<Idc> idcs = SistemaRED.getIDCDates(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, naf);
		idcs.stream().filter(idc -> AonStringUtils.equalsIgnoreCase(idc.getDescripcion(), "ALTA")).forEach( idc -> dates.add(idc.getFecha() ));
		return dates;
	}


	public static EmployeeStatus getStatus(Connection connection, String domainName, Integer domainId, String userLogin, Integer userId, Integer contractId) throws IOException, SegSocialException{
		
		Contract contract = 
		PAYROLL.
		getContract(domainName, domainId, userLogin, p -> p.getIdProperty().eq(contractId))
		.orElseThrow(IOException::new);
//		String nif = contract.getPersonDocument();
		String nss = contract.getPersonSsNumber();
		String ccc = contract.getEnterpriseCCC();
		String regime = contract.getEnterpriseCCCRegime().getCode();
		Date endDate = contract.getEndDate();
		Date startDate = contract.getStartDate();
		
		try {
			EmployeeStatus.AndEmployeeStatus employeeStatus = new EmployeeStatus.AndEmployeeStatus();
			
			Date date = new Date();
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            com.esferalia.aon.occam.api.model.payroll.Employee employee = SistemaRED2AON.getEmployeeToIDC(userLogin, userId, domainName, domainId, regime, ccc, nss, Optional.empty());
            

			if ( AonUtils.notEquals(employee.getCcc(), ccc)) {
				employeeStatus.and(
						new EmployeeStatus.MismatchedCCC()
						.setAonCCC(ccc)
						.setSsCCC(employee.getCcc()));
			}
			
			// check fecha_alta == start_date
//			{
//				Date ssStartDate = employee.getFra();
//				if ( AonUtils.notEquals(ssStartDate, startDate)) {
//					employeeStatus.and(
//							new EmployeeStatus.MismatchedStartDate()
//							.setAonStartDate(startDate)
//							.setSsStartDate(ssStartDate));
//				}
//			}
			
			// check fecha_baja == end_date
//			employee.getFrb().ifPresentOrElse(ssEndDate -> {
//				if ( AonUtils.notEquals(ssEndDate, endDate)) {
//					employeeStatus.and(
//							new EmployeeStatus.MismatchedStartDate()
//							.setAonStartDate(startDate)
//							.setSsStartDate(ssEndDate));
//				}
//			},
//			() -> {
//				if ( endDate != null ) {
//					employeeStatus.and(new EmployeeStatus.EndDateNotFound());
//				}
//			});
			
			Date currentDate = new Date();
			
			employee.getEndDate().ifPresent(ssEndDate -> {
				if ( AonUtils.notEquals(ssEndDate, endDate) && DateUtils.getDaysBetween(currentDate, ssEndDate) < 30) {
					employeeStatus.and(
							new EmployeeStatus.MismatchedStartDate()
							.setAonStartDate(startDate)
							.setSsStartDate(ssEndDate));
				}
			});
			
			employee.getEndDate().orElseGet(() -> {
				if ( endDate != null && DateUtils.getDaysBetween(currentDate, endDate) < 30 ) {
					employeeStatus.and(new EmployeeStatus.EndDateNotFound());
				}
				return null;
			});
			
			if(contract.getEndDate() == null || sqlDate.before(contract.getEndDate())) {
				
				LinkedList<ContractData> dataList = PAYROLL
				.getContractDataList(domainName, domainId, userLogin, p -> 
				p.getContractProperty().eq(contractId)
				.and(p.getEndDateProperty().isNull().or(p.getEndDateProperty().ge(sqlDate))) 
				);
			
				// check tipo_contrato == tc2 			
				if ( employee.getContractType().isPresent() ) {
					String ssContractType = employee.getContractType().get();
					String aonContractType = getString(dataList, ContextVariable.TC2, "");
					if ( AonStringUtils.compareIgnoreCase(aonContractType, ssContractType ) != 0 && !AonStringUtils.endsWith(ssContractType, "9") ) {
						employeeStatus.and(
								new EmployeeStatus.MismatchedContractType()
								.setAonContractType(aonContractType)
								.setSsContractType(ssContractType)
								.setVariables(Collections.singletonList(
										new StringVariable.Builder()
										.setName(TC2.getName())
										.setStartDate(employee.getStartDate())
										.create()))
								);
					}
				}
				
				// check grupo_cotizacion == quote_group 
				if ( employee.getQuoteGroup().isPresent()) {
					String ssQuoteGroup = employee.getQuoteGroup().get();
					String aonQuoteGroup = getString(dataList, ContextVariable.QUOTE_GROUP, "");
					if ( AonStringUtils.compareIgnoreCase(ssQuoteGroup, aonQuoteGroup ) != 0 ) {
						employeeStatus.and(
								new EmployeeStatus.MismatchedQuoteGroup()
								.setAonQuoteGroup(aonQuoteGroup)
								.setSsQuoteGroup(ssQuoteGroup)
								.setVariables(Collections.singletonList(
										new StringVariable.Builder()
										.setName(QUOTE_GROUP.getName())
										.setStartDate(employee.getStartDate())
										.create()))
								);
					} 
				}
			}
			
			
			{
				/* check ocupacion == occupation 
				try {
					String ssOccupation = statusJsonObject.getString(Saltra.OCUPACION);
					String aonOccupation = getString(dataList, ContextVariable.OCCUPATION, "");
					if ( AonStringUtils.compareIgnoreCase(aonOccupation, ssOccupation ) != 0 ) {
						employeeStatus.and(
								new EmployeeStatus.MismatchedOccupation()
								.setSsOccupation(ssOccupation)
								.setAonOccupation(aonOccupation));
					}									
				} catch ( JSONException e  ) {
					dataList.stream()
					.filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(),ContextVariable.OCCUPATION.getName()))
					.findFirst().ifPresent(d -> employeeStatus.and(new EmployeeStatus.OccupationNotFound()) );
				}*/
			}
			
			{
			}

			try {
				EmployeeStatus.isUp2Date(employeeStatus); 
				employeeStatus.and(new EmployeeStatus.Up2Date());
			} catch ( OutOfDateException e ) {	
			}
			
			EmployeeStatus.trace(employeeStatus);
			
			return employeeStatus;	
			
		} catch ( ForbiddenException e ) {
			return new EmployeeStatus.Forbidden();
		} catch ( NoQueryData e ) {
			return new EmployeeStatus.NoQueryData().setMessage(e.getMessage());
		} catch (UnknownPDFException e) {
          e.printStackTrace();
          return new EmployeeStatus.UnknownError().setMessage(e.getMessage());
        } catch ( NotAllowedContributionAccount e) {
			return new EmployeeStatus.NotAuthorizedCCC();
		} 
		catch ( SaltraCredentialsNotFoundException | CertificateNotFoundException e ) {
			return new EmployeeStatus.CredentialsNotFound();
		} catch ( SegSocialException e  ) {
			return new EmployeeStatus.UnknownError().setMessage(e.getMessage());
		} 
	}
	
	public static Map<String,List<Variable>> getSSContractData(Connection connection, String domainName, Integer domainId, String userLogin, Integer userId, Integer contractId, Date date) {
		Contract contract;
		try {
			contract = PAYROLL.
			getContract(domainName, domainId, userLogin, p -> p.getIdProperty().eq(contractId))
			.orElseThrow(() -> new IOException() );
			String ccc = contract.getEnterpriseCCC();
			String naf = contract.getPersonSsNumber();
			String regime = contract.getEnterpriseCCCRegime().getCode();	
			
//			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			return getSSContractData(certificate, regime, ccc, naf, date);
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}

	public static Map<String,List<Variable>> getSSContractData(Certificate certificate, String regime, String ccc, String nss, Date date)  {
		Map<String, List<Variable>> ssContractData = new HashMap<String, List<Variable>>();
		
		Collection<Idc> idcDates;
		try {
			idcDates = SistemaRED.getIDCDates(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, nss);
		} catch (SegSocialException e1) {
			return ssContractData;
		}
		
		List<Date> dates  = idcDates.stream().map(idc -> idc.getFecha() ).filter( d -> d.compareTo(date)>=0).sorted((d1,d2) -> d2.compareTo(d1)).collect(Collectors.toList());
		
		ContextVariable stringVars  [] = new ContextVariable [] {QUOTE_GROUP, OCCUPATION, TC2};
		ContextVariable numberVars  [] = new ContextVariable [] {PARTIAL_FACTOR, CGC_ENTERPRISE_PERCENT};
		
		
		for ( ContextVariable v : stringVars )
			ssContractData.put(v.getName(), new LinkedList<Variable>());
		for ( ContextVariable v : numberVars )
			ssContractData.put(v.getName(), new LinkedList<Variable>());
		
		Date endDate = null;
		for (Date startDate : dates) {				
			byte data[] = null;
			try {
				data = SistemaRED.getIDC(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, nss, startDate);
				Map<ContextVariable, Collection<IdcContractData>> contractDatas = com.esferalia.aon.in.payroll.tgss.idc.Idc.getContractData(data);
				for ( ContextVariable v : stringVars ) {
					for ( IdcContractData idcContractData : contractDatas.getOrDefault(v, Collections.emptyList()) ) {
						StringVariable variable = new StringVariable();
						variable.setName(v.getName());
						variable.setValue(idcContractData.data());
						variable.setStartDate(idcContractData.startDate());
						variable.setEndDate(idcContractData.endDate());
						ssContractData.get(v.getName()).add(variable);
						variable.setExpression(String.format("\"%s\"", idcContractData.data()).toString());
					}
				}
				for ( ContextVariable v : numberVars ) {
					for ( IdcContractData idcContractData : contractDatas.getOrDefault(v, Collections.emptyList()) ) {
						NumberVariable variable = new NumberVariable();
						variable.setName(v.getName());
						variable.setValue(idcContractData.data());
						variable.setStartDate(idcContractData.startDate());
						variable.setEndDate(idcContractData.endDate());				
						ssContractData.get(v.getName()).add(variable);
						variable.setExpression(idcContractData.data().toString());
					}
				}
				endDate = AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1);
				
			} catch (Exception e) {
				try (OutputStream os = new FileOutputStream( File.createTempFile("IDC", "pdf")) )  {
					os.write(data);
				} catch ( IOException ioException ) {
				}
				
				e.printStackTrace();
			}
		}
		
		return ssContractData;
	}
	
	private static String getString(List<ContractData> dataList, ContextVariable contextVariable, String def ) {
		return
		dataList.stream()
		.filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(),contextVariable.getName()))
		.findFirst()
		.map(data -> MVEL.evalToString(data.getExpression()))
		.orElse(def)
		;		
	}

	/**
	 * 
	 * @param connection
	 * @param draft
	 * @param domainId
	 * @param parentDomainId
	 * @throws SQLException
	 */
	public static void calculate(Connection connection, AgreementDraft draft,
			Integer domainId, Integer parentDomainId) throws SQLException {
		SortedSet<Date> datesWithChanges = parentDomainId == null
				? SQLAgreementDraft.getDatesWithChanges(connection,
						draft.getId(), domainId)
				: SQLAgreementDraft.getDatesWithChanges(connection,
						draft.getId(), domainId, parentDomainId);

		Set<Payment> dbPayments = SQLAgreementDraft.getPayments(connection,
				draft.getId(), draft.getStartDate(), draft.getEndDate(),
				domainId, parentDomainId);

		@SuppressWarnings("unchecked")
		Collection<Payment> payments = new CompositeItems<Payment>(
				draft.getDraftPayments(), dbPayments);

		Set<Event> errors = new HashSet<Event>();
		Set<String> variables = new HashSet<String>();
		Set<String> paymentsNames = new HashSet<String>();

		Set<Payment> allPayments = new HashSet<Payment>();
		for (Payment payment : payments) {

			if (hide(payment, dbPayments))
				continue;

			
			String paymentName = payment.getName();
			
			try {
				String expression = 
				getUserScript(payment.getExpression());
				Set<String> exprVariables =
				ExpressionContext.getVariableSet(
						expression);				
				variables.addAll(exprVariables);
				if ( !exprVariables.contains(paymentName) )
					paymentsNames.add(paymentName);

			} catch (Exception e) {
				paymentsNames.add(paymentName);
			}

			try {
				String irpfExpression = 
				getUserScript(payment.getIrpfExpression());
				Set<String> irpfVariables =
				ExpressionContext.getVariableSet(
						irpfExpression);				
				variables.addAll(irpfVariables);
			} catch (Exception e) {
			}

			try {
				String quoteExpression = 
				getUserScript(payment.getQuoteExpression());
				Set<String> quoteVariables =
				ExpressionContext.getVariableSet(
						quoteExpression);				
				variables.addAll(quoteVariables);
			} catch (Exception e) {
			}
			
			allPayments.add(payment);
		}

		variables.removeAll(paymentsNames);

		// Filter ContextVariable
		List<String> contextVariables = new LinkedList<String>();
		for (ContextVariable ctxVar : ContextVariable.values())
			if (ctxVar.isInternal())
				contextVariables.add(ctxVar.getName());
		variables.removeAll(contextVariables);

		// This is awfull ... very awful
		List<String> privateVariables = new LinkedList<String>();
		for (String var : variables) {
			if (var.endsWith("_ACTUAL"))
				privateVariables.add(var);
			if (var.endsWith("_HELP"))
				privateVariables.add(var);
		}
		variables.removeAll(privateVariables);

		/*
		 * Clean system variables. Set<String> systemVars =
		 * getSystemVariables(connection, draft.getStartDate(),
		 * draft.getEndDate()); variables.removeAll(systemVars);
		 */

		Set<Level> dbLevels = SQLAgreementDraft.getLevels(connection,
				draft.getId(),domainId, parentDomainId);

		Set<Level> allLevels = new HashSet<Level>(dbLevels);

		for (Level draftLevel : draft.getDraftLevels()) {
			allLevels.remove(draftLevel);
			if (!AonStringUtils.equals(REMOVE, draftLevel.getDescription()))
				allLevels.add(draftLevel);
		}

		Set<Extra> dbExtras = SQLAgreementDraft.getExtras(connection,
				draft.getId());

		Set<Extra> allExtras = new HashSet<Extra>(dbExtras);

		for (Extra draftExtra : draft.getDraftExtras()) {
			allExtras.remove(draftExtra);
			if (!AonStringUtils.equals(REMOVE, draftExtra.getIssueDate()))
				allExtras.add(draftExtra);
		}

		Map<Integer, Set<String>> dbCategories = SQLAgreementDraft
				.getCategories(connection, draft.getId(), domainId, parentDomainId);

		Map<Integer, Set<String>> allCategories = new TreeMap<Integer, Set<String>>(
				dbCategories);

		Map<Integer, Set<String>> draftCategories = draft.getDraftCategories();
		allCategories.putAll(draftCategories);

		Level agreementData = new Level();
		agreementData.setId(0);
		allLevels.add(agreementData);

		SalaryTable dbSalaryTable = SQLAgreementDraft.getSalaryTable(connection,
				draft.getId(), draft.getStartDate(), draft.getEndDate(), domainId, parentDomainId);
		SalaryTable allSalaryTable = new SalaryTable(dbSalaryTable);
		
		if(draft.getDraftSalaryTable().size() != 0)
			allSalaryTable.putAll(draft.getDraftSalaryTable());
		
		for ( Variable var: allSalaryTable.getAllVariables() ) {
			String expression = 
			getUserScript(var.getExpression());
			try {
				Set<String> exprVariables =
				ExpressionContext.getVariableSet(
						expression);
				variables.addAll(exprVariables);
			} catch ( Exception e ) {
				errors.add(
				new Event()
				.setType(Type.ERROR)
				.setMessage(e.getMessage()));
			}
		}

		draft.setLevels(allLevels);
		draft.setExtras(allExtras);
		draft.setVariables(filterVariables(variables)); // * No draft
		draft.setPayments(allPayments);
		draft.setSalaryTable(allSalaryTable);
		draft.setCategoriesMap(allCategories);
		draft.setDatesWithChanges(datesWithChanges);

		List<Integer> domainIds = new ArrayList<Integer>();
		if (parentDomainId != null)
			domainIds.add(parentDomainId);
		domainIds.add(domainId);
		SQLAgreementContextFactory  agreementCtxFactory = null;
		
		try {
			agreementCtxFactory = 
					newSQLAgreementContextFactory(connection, draft.getStartDate(), draft.getEndDate());
		} catch (Throwable e) {
			return;
			// TODO: 
		}
		
		try {
			eval(agreementCtxFactory, draft.getId(), allLevels, allSalaryTable,
					draft.getStartDate(), draft.getEndDate(),
					domainIds.toArray(new Integer[] {}));
		} catch ( Throwable t) {
			// TODO: 
		}
		
		try {
			Set<Event> allEvents = eval(agreementCtxFactory, draft.getId(), allPayments, draft.getStartDate(), draft.getEndDate());
			allEvents.addAll(errors);
			draft.setEvents(allEvents);
		} catch ( Throwable t) {
			// TODO: 
		}
		
	}
	
	public static Set<String> getVariables(Connection connection, AgreementInfo agreement, Integer domainId, Integer parentDomainId) throws SQLException {
		Date defaultStartDate =  DateUtils.getFirstDayOfMonth(DateUtils.getDate(0, 2018));
		Date startDate = agreement.getSortedDates().isEmpty() ? defaultStartDate
				: (Date) agreement.getSortedDates().toArray()[agreement.getSortedDates().size() -1];
		Date endDate = null; 
		
		Set<Payment> dbPayments = SQLAgreementDraft.getPayments(connection,
				agreement.getId(), startDate, endDate, domainId, parentDomainId);

		@SuppressWarnings("unchecked")
		Collection<Payment> payments = new CompositeItems<>(agreement.getPayments(), dbPayments);

		Set<String> variables = new HashSet<>();
		Set<String> paymentsNames = new HashSet<>();

		for (Payment payment : payments) {

			if (hide(payment, dbPayments))
				continue;

			String paymentName = payment.getName();
			
			try {
				String expression = getUserScript(payment.getExpression());
				Set<String> exprVariables = ExpressionContext.getVariableSet(expression);				
				variables.addAll(exprVariables);
				if ( !exprVariables.contains(paymentName) )
					paymentsNames.add(paymentName);

			} catch (Exception e) {
				paymentsNames.add(paymentName);
			}

			try {
				String irpfExpression = getUserScript(payment.getIrpfExpression());
				Set<String> irpfVariables = ExpressionContext.getVariableSet(irpfExpression);				
				variables.addAll(irpfVariables);
			} catch (Exception e) {}

			try {
				String quoteExpression = getUserScript(payment.getQuoteExpression());
				Set<String> quoteVariables = ExpressionContext.getVariableSet(quoteExpression);				
				variables.addAll(quoteVariables);
			} catch (Exception e) {}
			
		}

		variables.removeAll(paymentsNames);

		// Filter ContextVariable
		List<String> contextVariables = new LinkedList<>();
		for (ContextVariable ctxVar : ContextVariable.values())
			if (ctxVar.isInternal())
				contextVariables.add(ctxVar.getName());
		variables.removeAll(contextVariables);

		// This is awfull ... very awful
		List<String> privateVariables = new LinkedList<>();
		for (String var : variables) {
			if (var.endsWith("_ACTUAL"))
				privateVariables.add(var);
			if (var.endsWith("_HELP"))
				privateVariables.add(var);
		}
		variables.removeAll(privateVariables);

		SalaryTable dbSalaryTable = SQLAgreementDraft.getSalaryTable(connection, agreement.getId(), startDate, endDate, domainId, parentDomainId);
		SalaryTable allSalaryTable = new SalaryTable(dbSalaryTable);
		
		for ( Variable var: allSalaryTable.getAllVariables() ) {
			String expression = getUserScript(var.getExpression());
			try {
				Set<String> exprVariables = ExpressionContext.getVariableSet(expression);
				variables.addAll(exprVariables);
			} catch ( Exception e ) {}
		}

		return variables;
	}

	private static Set<String> filterVariables(Set<String> variables) {
		if(variables.isEmpty())
			return Collections.emptySet();
		
		Set<String> filteredVariables = new HashSet<String>();
		
		// Filter variables list
		List<String> filterVars = new ArrayList<String>();
		filterVars.add("TRUE");
		filterVars.add("FALSE");
		filterVars.add("AÑOS_TRABAJADOS");
		filterVars.add("DIAS_COTIZADOS");
		filterVars.add("COEFICIENTE_PARCIALIDAD");
		filterVars.add("DIAS_LABORALES");
		filterVars.add("DIAS_LUNES");
		filterVars.add("DIAS_MARTES");
		filterVars.add("DIAS_MIERCOLES");
		filterVars.add("DIAS_JUEVES");
		filterVars.add("DIAS_VIERNES");
		filterVars.add("DIAS_SABADO");
		filterVars.add("DIAS_DOMINGO");
		filterVars.add("BASE_REGULADORA");
		filterVars.add("DIAS_TRABAJADOS");
		filterVars.add("AÑOS_ANTIGUEDAD");
		
		for(String var : variables)
			if(	!filterVars.contains(var) && 
				!AonStringUtils.containsIgnoreCase(var, "HIDE") && 
				!AonStringUtils.containsIgnoreCase(var, "DIAS_ENFERMEDAD"))
				
				filteredVariables.add(var);
		
		return filteredVariables;
	}

//	public static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getSalaryCalculatorContext(
//			final Connection conn, final SalaryDraft draft,
//			IContractSalaryCalculatorContext.IListener listener)
//					throws ExpressionException, SQLException {
//		return getSalaryCalculatorContextImpl(conn, draft, listener);
//	}

	public static List<Bonus> getAvailableBonuses(Connection conn,
			int employeeId, Integer... domains)
					throws IllegalArgumentException {
		try {
			List<Bonus> availableBonuses = new ArrayList<Bonus>();
			AON.getAvailableBonuses(new AONContext(conn), props -> {
				return props.getDomainProperty().in(domains);
				// .and(props.getIsUnknowProperty().eq(true));
			}).map(b -> {
				Bonus bonus = new Bonus();
				bonus.setId(b.getId());
				bonus.setExpression(b.getExpression());
				bonus.setDescription(b.getDescription());
				if (b.getType() != null)
					bonus.setType(Bonus.Type.values()[b.getType().ordinal()]);
				return bonus;
			}).forEach(bonus -> availableBonuses.add(bonus));
			;

			return availableBonuses;

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	protected static Map<String, boolean[]> getDefinedMap(
			IContractSalaryCalculatorContext ctx) {
		Map<String, boolean[]> definedMap = new HashMap<String, boolean[]>();

		Date startDate = ctx.getStartDate();
		Date endDate = ctx.getEndDate();

		for (String name : ctx.getSystemExpressionContext().variablesSet()) {
			boolean defined[] = new boolean[Scope.NUM_VALUES];
			defined[Scope.SYSTEM.ordinal()] = true;
			definedMap.put(name, defined);
		}

		for (ISystemPayment p : ctx.getSystemPayments()) {
			if (AonStringUtils.isBlank(p.getName()))
				continue;

			boolean defined[] = new boolean[Scope.NUM_VALUES];
			defined[Scope.SYSTEM.ordinal()] = true;
			definedMap.put(p.getName(), defined);

		}

		// Agreement
		ExpressionContext agreementCtx = ctx.getAgreementExpressionContext();
		for (String name : agreementCtx.variablesSet()) {
			ITimedVariable<?> var = agreementCtx.getVariable(name, startDate,
					endDate);
			if (!(var instanceof IExpressionVariable<?>))
				continue;
			ExpressionScope scope = ((IExpressionVariable<?>) var)
					.getExpression().getScope();
			if (scope != ExpressionScope.AGREEMENT)
				continue;

			boolean defined[] = definedMap.get(name);
			if (defined == null) {
				defined = new boolean[Scope.NUM_VALUES];
				definedMap.put(name, defined);
			}
			defined[Scope.AGREEMENT.ordinal()] = true;
		}

		for (IContractPayment p : ctx.getAgreementPayments()) {
			if (AonStringUtils.isBlank(p.getName()))
				continue;

			boolean defined[] = new boolean[Scope.NUM_VALUES];
			defined[Scope.AGREEMENT.ordinal()] = true;
			definedMap.put(p.getName(), defined);

		}

		ExpressionContext implicitCtx = ctx.getImplicitExpressionContext();
		for (String name : ctx.getImplicitExpressionContext().variablesSet()) {

			ITimedVariable<?> var = agreementCtx.getVariable(name, startDate,
					endDate);
			if (var instanceof IExpressionVariable<?>)
				continue;
			// Implicit variables don't come from expression

			boolean defined[] = definedMap.get(name);
			if (defined == null) {
				defined = new boolean[Scope.NUM_VALUES];
				definedMap.put(name, defined);
			}
			defined[Scope.APPLICATION.ordinal()] = true;
		}

		return definedMap;
	}

	// ------------------------------------------------------------------------

	private static Variable copy(Variable var) {
		Variable copy = new StringVariable();
		copy.setImplicit(true);
		copy.setName(var.getName());
		copy.setScope(var.getScope());
		copy.setEndDate(var.getEndDate());
		copy.setStartDate(var.getStartDate());
		copy.setExpression(var.getExpression());
		return copy;
	}

	private static boolean hide(Payment payment, Set<Payment> parents) {
		if (payment.getConceptId() == null)
			return false;
		if (!AonStringUtils.equals(REMOVE, payment.getExpression()))
			return false;

		if (payment.getId() < 0)
			return true;

		return parents.stream()
				.filter(parent -> AonUtils.equals(parent.getConceptId(),payment.getConceptId())
						&& parent.getDomain().equals(payment.getDomain()))
				.findAny().isPresent();
	}

	private static void eval(SQLAgreementContextFactory agreementCtxFactory, int agreementId,
			Set<Level> levels, SalaryTable salaryTable, Date start, Date end,
			Integer... domainIds) {
			// try to resolve some variables. Here we go.

			LinkedList<Variable> defVars = new LinkedList<Variable>(
					salaryTable.getVariables(0));

			for (Level level : levels) {
				ExpressionContext levelCtx = new ExpressionContext();

				for (Integer domainId : domainIds) {
					AgreementContextKey levelKey = new AgreementContextKey(
							domainId, agreementId, level.getId(), 0);
					levelCtx.add(agreementCtxFactory.create(levelKey));
				}

				for (Variable defVar : defVars)
					if (!salaryTable.contains(level.getId(), defVar.getName()))
						salaryTable.put(level.getId(), copy(defVar));

				LinkedList<Variable> levelVars = new LinkedList<Variable>(
						salaryTable.getVariables(level.getId()));

				eval(levelCtx, levelVars, start, end);
			}
	}

	private static Set<Event> eval(SQLAgreementContextFactory agreementCtxFactory, int agreementId,
			Collection<Payment> payments, Date start, Date end) {
		
		Set<Event> events = new HashSet<Event>();
		
		List<Payment> hide = new ArrayList<Payment>();

		ExpressionContext agreementDataCtx = agreementCtxFactory.getSystemExpressionContext();
		for ( Payment payment: payments ) {
			try {
				agreementDataCtx.eval(payment.getExpression(), start, end);
			}catch ( CheckException e ) {
				events.add(
				new PaymentEvent()
				.setPayment(payment)
				.setType(Type.WARNING)
				.setMessage(e.getMessage()));
			}catch (CompileException e){
				events.add(
				new PaymentEvent()
				.setPayment(payment)
				.setType(Type.ERROR)
				.setMessage(e.getMessage()));
			}catch ( FullHideException e ) {
				events.add(
				new Event()
				.setType(Type.INFO)
				.setMessage(e.getMessage()));
				hide.add(payment);
			}
			catch (ExpressionException e) {
			}
		}
		
		// TODO: Make this outside please
		for ( Payment payment: hide )
			payments.remove(payment);
		
		return events;
	}
	

	private static SQLAgreementContextFactory newSQLAgreementContextFactory(Connection conn, Date start, Date end)
			throws SQLException, ExpressionException {
		SQLSystemExpressionContextFactory systemCtxFactory = new SQLSystemExpressionContextFactory(
				conn, start, end,
				ISQLContractSalaryCalculatorContext.NEWER);

		Supplier<ExpressionContext> systemCtxSupplier = () -> systemCtxFactory
				.create(new CCCContextKey(CCCType.PRINCIPAL, SSRegimeType.GENERAL, null));

		SQLAgreementContextFactory agreementCtxFactory = new SQLAgreementContextFactory(
				conn, systemCtxSupplier, start, end,
				ISQLContractSalaryCalculatorContext.NEWER);
		return agreementCtxFactory;
	}

	private static void eval(ExpressionContext ctx, LinkedList<Variable> vars,
			Date start, Date end) {
		int errors = 0;
		while (errors < vars.size()) {
			Variable var = vars.pop();
			try {
				List<ITimedResult<Object>> results = ctx
						.eval(var.getExpression(), start, end);
				errors = 0;
				for (ITimedResult<Object> result : results) {
					var.setValue(result.getValue());
					ctx.putVariable(var.getName(), result);
					// System.out.println(var.getName() + " = " +
					// var.getValue());
				}
			} catch (UndefinedVariablesException e) {
				vars.add(var);
				errors++;
			} catch (CompileException e) {
				var.setValue(generateErrorMessage(e));
				errors++;
			} catch (ExpressionException e) {
				errors++;
				// Nothing to do... Only report this error. This will be
				// very hepfull.
			}
		}
	}

	
	private static String generateErrorMessage(CompileException e) {
		char expr[] = e.getExpr();
		int cursor = e.getCursor();
		return String.format("Error sintactico cerca de '%s'",
				showCodeNearError(expr, cursor));
	}

	private static CharSequence showCodeNearError(char[] expr, int cursor) {
		if (expr == null)
			return "???";

		int end = Math.min(cursor + 10, expr.length - 1);
		int start = Math.max(0, end - 20);

		while (start < end && Character.isWhitespace(expr[start]))
			start++;

		CharSequence cs = null;

		try {
			cs = String.copyValueOf(expr, start, end - start);
		} catch (StringIndexOutOfBoundsException e) {
			throw e;
		}

		return cs;
	}

	private static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getSalaryCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
					throws ExpressionException, SQLException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());

		class SalaryCalculatorContextImpl
				extends SQLContractSalaryCalculatorContext {

			public SalaryCalculatorContextImpl(Connection connection,
					Date startDate, Date endDate, Date issueDate, Date chargeDate,
					Criteria criteria)
							throws SQLException, ExpressionException {
				super(connection, startDate, endDate, issueDate, chargeDate, criteria);
			}

			public Object __br(Date date) throws ExpressionException, SQLException, SalaryException {
				
				Date today = resetTime(Calendar.getInstance().getTime());
				Date endDate = AonDateUtils.add(getEndDate(), Calendar.DAY_OF_MONTH,1); // TODO: +1?
				if ( endDate.after(today) )
					return super.br(date);
				
				// delay
				int contractId = getId();
				Optional<Salary> salary = 
						AON.getSalaries(new AONContext(connection),
						p -> p.getIsSalaryProperty().eq(true)
						.and(p.getContractProperty().eq(contractId))
						.and(p.getStartDateProperty().le(getEnd()))
						.and(p.getEndDateProperty().ge(getStart())))
				.findAny();
				
				if ( !salary.isPresent() )
					return super.br(date);
				
				// DELAYs ?
				
				Date contractStart = super.getDate(CONTRACT, ContractColumns.START_DATE);
				if ( contractStart.before(getFirstDayOfMonth(date)))
					date = AonDateUtils.add(date, Calendar.MONTH, -1);
				
				if ( isFullTime() )
					return super.calculateBr(date);
				
				double br = (Double) super.calculateBr(date);
				int i = 1;
				for ( ; i <= 2 && contractStart.before(getFirstDayOfMonth(date)); i++) {
					date = AonDateUtils.add(date, Calendar.MONTH, -1);
					br += (Double) super.calculateBr(date);
				}
				 return br / i;
			}
			
			@Override
			protected IIrpfCalculatorContext getIrpfCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Criteria criteria) {
				try {
//					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
//							conn, startDate, endDate, endDate, criteria) {
//
//						@Override
//						public double getIrpf() {
//							return 0.00;
//						}
//
//					};
					
					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = getUnderlyingIrpfSQLCalculatorContext(conn, startDate, endDate, criteria);
					
					SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
							draft, sqlContractSalaryCalculatorCtx);

					return new SQLIrpfCalculatorContext(conn, startDate,
							endDate, sqlDraftSalaryCalculatorCtx) {
						@Override
						public String getNif() {
							return "87449445H";
						}

						@Override
						public String getApellidosNombre() {
							return "TORVALDS BENEDICT LINUS";
						}

						@Override
						public String getRetenedorNif() {
							return "Z7896423E";
						}

						@Override
						public String getRetenedorApellidosNombre() {
							return "LINUX FOUNDATION";
						}

						@Override
						public int getAñoNacimiento() {
							Date birthDate = SalaryCalculatorContextImpl.this.getDate(SQLConstants.PERSON, SQLConstants.PersonColumns.BIRTH_DATE);
							return birthDate != null ? AonDateUtils.get(birthDate, Calendar.YEAR) : 0;
						}

					};
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				}
			}

			@Override
			protected ISQLContractSalaryCalculatorContext getPaymentCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Date issueDate, Criteria criteria, final double x) {
				try {
					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
							conn, startDate, endDate, issueDate, criteria) {

						@Override
						public double getIrpf() {
							return 0.00;
						}

						@Override
						public Object gross(double liquid, Date start, Date end)
								throws ExpressionException, SQLException {
							return x;
						}

						@Override
						public Object liquid(double liquid, Date start,
								Date end) throws ExpressionException,
										SQLException, SalaryException {
							throw new InterruptedException(String.format(
									"Lo sentimos. La funci\u00F3n BRUTO es incompatible con la funci\u00F3n NETO. Elija una de las dos. :-("));
						}

						@Override
						protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
								Connection conn, Date startDate, Date endDate,
								Date issueDate, Criteria criteria, int start,
								int end) {
							ISQLContractSalaryCalculatorContext draftCtx;
							try {
								SQLNoItContractSalaryCalculatorContext sqlCtx = new SQLNoItContractSalaryCalculatorContext(
										conn, startDate, endDate, issueDate,
										criteria, start, end);
								draftCtx = new SQLSalaryDraftCalculatorContext(
										draft, sqlCtx);
								draftCtx.next();
								return draftCtx;
							} catch (ExpressionException e) {
								throw new ExpressionExceptionWrapper(e);
							} catch (SQLException e) {
								throw new ExpressionExceptionWrapper(
										new ExpressionException(e));
							}
						}

						@Override
						public Collection<IContractDeduction> getContractDeductions()
								throws AonException {
							return Collections.emptyList();
						}

						@Override
						public Collection<IContractEmbargo> getContractEmbargos()
								throws AonException {
							return Collections.emptyList();
						}

						@Override
						public Collection<IContractBonus> getContractBonus()
								throws AonException {
							return Collections.emptyList();
						}

						@Override
						public Collection<IContractCost> getContractCosts()
								throws AonException {
							return Collections.emptyList();
						}

					};
					SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
							draft, sqlContractSalaryCalculatorCtx);

					sqlDraftSalaryCalculatorCtx.setListener(
							SalaryCalculatorContextImpl.this.getListener());

					sqlDraftSalaryCalculatorCtx.next();
					return sqlDraftSalaryCalculatorCtx;

				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				}
			}

			@Override
			protected IContractSalaryCalculatorContext getLiquidCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Date issueDate, Criteria criteria, final double solve,
					final double liquid) {
				try {
					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
							conn, startDate, endDate, issueDate, criteria) {

						@Override
						public Object liquid(double liquid, Date start,
								Date end) throws ExpressionException,
										SQLException {
							return solve;
						}

						@Override
						public Object gross(double gross, Date start, Date end)
								throws ExpressionException, SQLException,
								SalaryException {
							throw new InterruptedException(String.format(
									"Lo sentimos. La funci\u00F3n NETO es incompatible con la funci\u00F3n BRUTO. Elija una de las dos. :-("));
						}

						@Override
						protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
								Connection conn, Date startDate, Date endDate,
								Date issueDate, Criteria criteria, int start,
								int end) {
							ISQLContractSalaryCalculatorContext draftCtx;
							try {
								SQLNoItContractSalaryCalculatorContext sqlCtx = new SQLNoItContractSalaryCalculatorContext(
										conn, startDate, endDate, issueDate,
										criteria, start, end);
								draftCtx = new SQLSalaryDraftCalculatorContext(
										draft, sqlCtx);
								draftCtx.next();
								return draftCtx;
							} catch (ExpressionException e) {
								throw new ExpressionExceptionWrapper(e);
							} catch (SQLException e) {
								throw new ExpressionExceptionWrapper(
										new ExpressionException(e));
							}
						}

						@Override
						protected IIrpfCalculatorContext getIrpfCalculatorContext(
								Connection conn, Date startDate, Date endDate,
								Criteria criteria) {
							try {
								SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
										conn, startDate, endDate, endDate,
										criteria) {

									@Override
									public double getIrpf() {
										return 0.00;
									}

									@Override
									public Object liquid(double _liquid,
											Date start, Date end)
													throws ExpressionException,
													SQLException {
										return solve * (_liquid / liquid);
									}
									
									@Override
									protected void loadContractLeave(ExpressionContext ctx) throws SQLException, ExpressionException {
									}

								};
								
								SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
										draft, sqlContractSalaryCalculatorCtx);

								return new SQLIrpfCalculatorContext(conn,
										startDate, endDate,
										sqlDraftSalaryCalculatorCtx) {
									@Override
									public String getNif() {
										return "87449445H";
									}

									@Override
									public String getApellidosNombre() {
										return "TORVALDS BENEDICT LINUS";
									}

									@Override
									public String getRetenedorNif() {
										return "Z7896423E";
									}

									@Override
									public String getRetenedorApellidosNombre() {
										return "LINUX FOUNDATION";
									}

									@Override
									public int getAñoNacimiento() {
										return 1969;
									};

								};
							} catch (SQLException e) {
								throw new ExpressionExceptionWrapper(
										new ExpressionException(e));
							} catch (ExpressionException e) {
								throw new ExpressionExceptionWrapper(e);
							}
						}

						@Override
						public Collection<IContractEmbargo> getContractEmbargos() throws AonException {
							return Collections.emptyList();
						}

					};
					SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
							draft, sqlContractSalaryCalculatorCtx);

					sqlDraftSalaryCalculatorCtx.setListener(
							SalaryCalculatorContextImpl.this.getListener());

					sqlDraftSalaryCalculatorCtx.next();
					return sqlDraftSalaryCalculatorCtx;

				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				}
			}

			@Override
			protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Date issueDate, Criteria criteria, int start, int end) {

				ISQLContractSalaryCalculatorContext draftCtx;
				try {
					SQLNoItContractSalaryCalculatorContext sqlCtx = new SQLNoItContractSalaryCalculatorContext(
							conn, startDate, endDate, issueDate, criteria,
							start, end);
					draftCtx = new SQLSalaryDraftCalculatorContext(draft,
							sqlCtx);
					draftCtx.next();
					return draftCtx;
				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				}

			}
			
			@Override
			protected void throwGuarenteeException(IContractSalaryCalculatorContext ctx)
					throws GuarenteeException {
				super.throwGuarenteeException(((SQLSalaryDraftCalculatorContext)ctx).getCtx());
			}

			

		}

		SalaryCalculatorContextImpl ctx = 
			new SalaryCalculatorContextImpl(conn,
				draft.getStartDate(), 
				draft.getEndDate(), 
				draft.getIssueDate(), 
				draft.getChargeDate(),
				criteria);

		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = new SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>(
				draft, ctx);

		draftCtx.setListener(listener);
		draftCtx.next();

		return draftCtx;
	}

	private static String tableCol(String table, String col) {
		return String.format("%1$s.%2$s", table, col);
	}

	public static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getExtraCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
					throws ExpressionException, SQLException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				EmployeesServiceImpl.tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractExtraCalculatorContext(
				conn, draft.getStartDate(), draft.getEndDate(),
				draft.getIssueDate(), draft.getChargeDate(), criteria);


		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = new SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>(
				draft, ctx) {
			
			@Override
			public Collection<IContractPayment> getContractPayments() throws AonException {
				 return hasDraftPayments() ? getDraftPayments() : getSuperContractPayments();
			}
			
			
		};
		

		draftCtx.setListener(listener);
		draftCtx.next();
		
		return draftCtx;
	}

	public static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getSettleCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
			throws ExpressionException, SQLException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());

		SQLSettleDraftCalculatorContext draftCtx = new SQLSettleDraftCalculatorContext(
				draft, conn, draft.getStartDate(), draft.getEndDate(),
				draft.getIssueDate(), draft.getChargeDate(), criteria);
		draftCtx.next();
		draftCtx.setListener(listener);
		return draftCtx;
	}

	public static String getUserScript(String script) {
		
		if (AonStringUtils.isBlank(script))
			return script;
		
		return script.replaceAll("\"/\\*user\\*/(.*)/\\*\\*/\"", "$1");
		
	}

	static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getDelayCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
			throws ExpressionException, SQLException {
	
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(EmployeesServiceImpl.tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());
	
		SQLContractSalaryCalculatorContext ctx = new SQLContractDelayCalculatorContext(
				conn, draft.getStartDate(), draft.getEndDate(), draft.getIssueDate(), draft.getChargeDate(), criteria) {
			
			@Override
			protected <T extends ISalary> ISalaryBuilder<T> getSalaryBuilder(ISalaryBuilder<T> salaryBuilder) {
				return new RoundSalaryBuilder<T>(salaryBuilder, round(2)) {
					@Override
					public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
							Map<String, ITimedVariable<?>> context) {
						tax = round(tax);
						quote = round(quote);
						super.addZeroPayment(quote, tax, startDate, endDate, payment, context);
					}
				};
			}
	
		};
	
		ctx.setListener(listener);
//		ctx.next();
	
		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = new SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>(
				draft, ctx) {
			@Override
			protected Collection<IContractPayment> getDraftPayments() {
				return Collections.emptyList();
			}
		};
		draftCtx.setListener(listener);
		draftCtx.next();
//		draftCtx.loadDraftContext(ctx.getExpressionContext());

		return draftCtx;
	}
	
	static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getPPEDelayCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
			throws ExpressionException, SQLException {
	
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(EmployeesServiceImpl.tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());
	
		SQLContractSalaryCalculatorContext ctx = new SQLContractPPECalculatorContext(
				conn, draft.getStartDate(), draft.getEndDate(), draft.getIssueDate(), draft.getChargeDate(), criteria) {
	
		};
	
		ctx.setListener(listener);
		ctx.next();
	
		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = new SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>(
				draft, ctx) {
			@Override
			protected Collection<IContractPayment> getDraftPayments() {
				return Collections.emptyList();
			}
		};
		draftCtx.setListener(listener);
		
		draftCtx.loadDraftContext(ctx.getExpressionContext());

		return draftCtx;
	}

	public static SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> getSalaryCalculatorContext(
			final Connection conn, final SalaryDraft draft,
			final IContractSalaryCalculatorContext.IListener listener)
			throws ExpressionException, SQLException {
	
		SalaryType salaryType = draft.getType() != null ? SalaryType.values()[draft.getType().ordinal()] : SalaryType.SALARY;
		return salaryType
				.accept(new SalaryTypeVisitor<SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>>() {
					@Override
					public SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> visitSalary(
							SalaryType salaryType) {
						try {
							return getSalaryCalculatorContextImpl(conn, draft,
											listener);
						} catch (SQLException e) {
							throw new IllegalArgumentException(e);
						} catch (ExpressionException e) {
							throw new ExpressionExceptionWrapper(e);
						}
					}
	
					@Override
					public SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> visitDelay(
							SalaryType salaryType) {
						try {
							
							SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> delayCalculatorContext = 
							getDelayCalculatorContextImpl(conn, draft,listener);

							if ( isPPEDelay(delayCalculatorContext) ) {
								return getPPEDelayCalculatorContextImpl(conn, draft, 
										listener);
							}
							
							return delayCalculatorContext;
						
						} catch (SQLException e) {
							throw new IllegalArgumentException(e);
						} catch (ExpressionException e) {
							throw new ExpressionExceptionWrapper(e);
						}
					}

					private boolean isPPEDelay(SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> ctx) {
						try {
							List<ITimedResult<PaymentType>> results = 
							ctx.getExpressionContext().eval(ContextVariable.DELAY_CAUSE.getName(), ctx.getStartDate(), ctx.getEndDate(), PaymentType.class);
							return results.stream().anyMatch( r -> r.getValue() == PaymentType.CRA_0033);
						} catch ( Exception e ) {
							return false;
						}
					}
	
					@Override
					public SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> visitSettle(
							SalaryType salaryType) {
						try {
							return getSettleCalculatorContextImpl(conn, draft,
									listener);
						} catch (SQLException e) {
							throw new IllegalArgumentException(e);
						} catch (ExpressionException e) {
							throw new ExpressionExceptionWrapper(e);
						}
					}
	
					@Override
					public SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> visitExtra(
							SalaryType salaryType) {
						try {
							return getExtraCalculatorContextImpl(conn, draft,
											listener);
						} catch (SQLException e) {
							throw new IllegalArgumentException(e);
						} catch (ExpressionException e) {
							throw new ExpressionExceptionWrapper(e);
						}
					}
					
					@Override
					public SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> visitProcedural(
							SalaryType salaryType) {
						try {
							return getSalaryCalculatorContextImpl(conn, draft,
											listener);
						} catch (SQLException e) {
							throw new IllegalArgumentException(e);
						} catch (ExpressionException e) {
							throw new ExpressionExceptionWrapper(e);
						}
					}
	
				});
	}
	
	
	public static EnterprisePayroll geteEnterprisePayroll(String title, InputStream logo, Date month, ICollectionProvider salariesProvider/*, ICollectionProvider sldProvider*/) throws ManagerBeanException {
		
		Map<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<String, Map<String,EnterprisePayrollEntry>>();
		Collection<com.esferalia.aon.payroll.Salary> salaries = salariesProvider.getCollection(true);
		
		String enterprise = "";
		
		for ( com.esferalia.aon.payroll.Salary salary : salaries ) {
			
			String workplace = salary.getContract().getWorkPlace().getDescription();
			
			Map<String, EnterprisePayrollEntry> workplace_entries = 
			entries.computeIfAbsent(workplace, s -> new TreeMap<String, EnterprisePayrollEntry>());
			
			
			String employee = String.format("%s_%s", salary.getEmployeeName(),workplace_entries.size());//salary.getEmployeeName();
			EnterprisePayrollEntry enterprisePayrollEntry = 
			salary2EnterprisePayrollEntry(salary );
			workplace_entries.put(employee, enterprisePayrollEntry);
			
			enterprise = salary.getEnterpriseName();
			
		}
		
		Map<SalaryType, SalaryType> SLD_SALARY_TYPES_MAP = new HashMap<SalaryType, SalaryType>();
		
		Map<String, Map<String, EnterprisePayrollEntry>> ss_entries = new HashMap<String, Map<String,EnterprisePayrollEntry>>();

		Collection<com.esferalia.aon.payroll.Salary> sld = Collections.emptyList(); //sldProvider.getCollection(true);
		for ( com.esferalia.aon.payroll.Salary salary : sld ) {
			
			String workplace = salary.getContract().getWorkPlace().getDescription();
			
			Map<String, EnterprisePayrollEntry> workplace_entries = 
			ss_entries.computeIfAbsent(workplace, s -> new TreeMap<String, EnterprisePayrollEntry>());
			
			
			String employee = String.format("%s_%s", salary.getEmployeeName(),SLD_SALARY_TYPES_MAP.getOrDefault(salary.getType(), SalaryType.SALARY));//salary.getEmployeeName();
			EnterprisePayrollEntry enterprisePayrollEntry = 
			salary2EnterprisePayrollEntry(salary );
			workplace_entries.put(employee, enterprisePayrollEntry);
			
		}
		
		
		
		
		EnterprisePayroll enterprisePayroll = new EnterprisePayroll(logo, month, title , enterprise, entries, ss_entries);
		
		return enterprisePayroll;
	}
	

	public static <S extends com.esferalia.aon.payroll.Salary >  S calculate(Connection conn, SalaryDraft draft, GenericContractSalaryCalculator<S, ISQLContractSalaryCalculatorContext> calculator) {

        	try {
        	    ISQLContractSalaryCalculatorContext ctx = 
        		    getSalaryCalculatorContext(conn, draft, irpfOut -> {});
        		return calculator.calculate(ctx);
        	} catch (ExpressionException | SQLException | SalaryException e) {
        		throw new IllegalArgumentException(e);
        	}  
	}

	public static <T extends ISalaryBuilder<ISalary>, L extends SalaryDraftBuilder> void calculate(
			Connection conn, SalaryDraft draft, T salaryBuilder, L draftBuilder, GenericContractSalaryCalculator<ISalary,ISQLContractSalaryCalculatorContext> calculator) {
	
		calculator.setSalaryBuilder(salaryBuilder);
		calculator.setListener(draftBuilder);
	
		ISQLContractSalaryCalculatorContext ctx;
		try {
			ctx = getSalaryCalculatorContext(conn, draft, draftBuilder);
			draftBuilder.setAgreementPayments(ctx.getAgreementPayments());
			draftBuilder.setDefined(getDefinedMap(ctx));
			calculator.calculate(ctx);
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (SalaryException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} 
	}
	
	private static EnterprisePayrollEntry salary2EnterprisePayrollEntry(ISalary salary) {
		return  new EnterprisePayrollEntry(
				EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM,
				salary.getSocialSecurityNumber(),
				salary.getCcc(),
				salary.getStartDate(),
				salary.getEndDate(),
				salary.getIssueDate(),
				salary.getEmployeeName(),
				salary.getType().getName(new Locale("es")),
				salary.getTotalPayment(),
				salary.getSocialSecurityContributions(),
				salary.getTotalIrpf(),
				salary.getTotalDeduction(),
				salary.getTotalLiquid(),
				salary.getTotalEnterprise(),
				salary.getTotalPayment() + salary.getTotalEnterprise(),
				salary.getSocialSecurityContributions() + salary.getTotalEnterprise(),0.00);
	}




	public static UnaryOperator<BigDecimal> round(int scale) {
		return d -> d.setScale(scale, RoundingMode.HALF_UP);
	}

	private static SQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(Connection connection, Integer contractId, Date startDate, Date endDate, Date issueDate) 
	throws ExpressionException, SQLException {
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),contractId);
		
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, criteria);
		
		ctx.next();
		
		return ctx;
	}



	protected static String getCCCRegimeCode(CCCType cccType) {
		switch (cccType) {
		case PRINCIPAL:
		case FELLOWS:
		case LEARNING:
		case TRAINING:
		case ASSIMILATEDS:
		case TRADE_REPRESENTATIVE:
			return "0111";
		case HOME_EMPLOYEES:
			return "0138";
		case AGRICULTURAL:
			return "0163";
		case ARTIST:
			return "0112";
		default:
			return "0111";
		}
	}
	
	protected static String getRegimeRegex(CCCType cccType) {
		switch (cccType) {
		case PRINCIPAL:
		case FELLOWS:
		case LEARNING:
		case TRAINING:
		case ASSIMILATEDS:
		case TRADE_REPRESENTATIVE:
			return "GENERAL";
		case HOME_EMPLOYEES:
			return "HOGAR";
		case AGRICULTURAL:
			return "AGARARIO";
		case ARTIST:
			return "ARTISTA";
		default:
			return "GENERAL";
		}
	}
}
