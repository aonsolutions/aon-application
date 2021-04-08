package com.esferalia.aon.in.payroll.pdf.template;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;
import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;
import static com.esferalia.aon.salary.enumeration.DeductionType.JOB_TRAINING;
import static com.esferalia.aon.salary.enumeration.DeductionType.NON_STRUCTURAL_OVERTIME;
import static com.esferalia.aon.salary.enumeration.DeductionType.PROFESSIONAL_CONTINGENCY;
import static com.esferalia.aon.salary.enumeration.DeductionType.STRUCTURAL_OVERTIME;
import static com.esferalia.aon.salary.enumeration.DeductionType.UNEMPLOYMENT;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0001;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0002;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0004;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0013;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0054;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFException;
import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AltaiPDFTemplate implements SalaryPDFTemplate {
	
	
	public static class PDFContract {
		private String ccc;
		private String naf;
		private String nif;
		private String cif;
		private String employeeCode;
		private String enterpriseCode;
		private String employeeName;
		private String enterpriseName;
	

		private Date startDate;
		private Date endDate;
		
		public String getCcc() {
			return ccc;
		}
		
		public String getNaf() {
			return naf;
		}
		
		public String getNif() {
			return nif;
		}
		
		public String getCif() {
			return cif;
		}
		
		public Date getStartDate() {
			return startDate;
		}
		
		public Date getEndDate() {
			return endDate;
		}
		
		public String getEmployeeCode() {
			return employeeCode;
		}
		
		public String getEnterpriseCode() {
			return enterpriseCode;
		}
		
		public String getEmployeeName() {
			return employeeName;
		}
		
		public String getEnterpriseName() {
			return enterpriseName;
		}
		
		public PDFContract setCcc(String ccc) {
			this.ccc = ccc;
			return this;
		}
		
		public PDFContract setNaf(String naf) {
			this.naf = naf;
			return this;
		}
		
		public PDFContract setNif(String nif) {
			this.nif = nif;
			return this;
		}
		
		public PDFContract setCif(String cif) {
			this.cif = cif;
			return this;
		}
		
		public PDFContract setStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}
		
		public PDFContract setEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}
		
		public PDFContract setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
			return this;
		}
		
		public PDFContract setEnterpriseName(String enterpriseName) {
			this.enterpriseName = enterpriseName;
			return this;
		}
	
		public PDFContract setEmployeeCode(String employeeCode) {
			this.employeeCode = employeeCode;
			return this;
		}
		
		public PDFContract setEnterpriseCode(String enterpriseCode) {
			this.enterpriseCode = enterpriseCode;
			return this;
		}

	}
	
	private static final class Payment implements IPayment {
		
		String name;
		double amount;
		PaymentType type;
		String description;
		
		@Override
		public PaymentType getType() {
			return type;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public double getAmount() {
			return amount;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getExpression() {
			return null;
		}
		
		public Payment setName(String name) {
			this.name = name;
			return this;
		}

		public Payment setAmount(double amount) {
			this.amount = amount;
			return this;
		}

		public Payment setType(PaymentType type) {
			this.type = type;
			return this;
		}

		public Payment setDescription(String description) {
			this.description = description;
			return this;
		}
		
		
	}
	
	private static class Deduction implements IDeduction {

		String name;
		double amount;
		DeductionType type;
		String description;

		@Override
		public DeductionType getType() {
			return type;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return name;
		}

		@Override
		public double getAmount() {
			return amount;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getExpression() {
			return null;
		}
		
		public Deduction setName(String name) {
			this.name = name;
			return this;
		}

		public Deduction setAmount(double amount) {
			this.amount = amount;
			return this;
		}

		public Deduction setType(DeductionType type) {
			this.type = type;
			return this;
		}

		public Deduction setDescription(String description) {
			this.description = description;
			return this;
		}		
		
	}

	private static final Locale SPAIN = new Locale("es", "ES");
	
	public static final  AltaiPDFTemplate ALTAI_PDF_TEMPLATE = new AltaiPDFTemplate();



	
	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);
		
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			Matcher matcher =
			find(reader, CODES);
			String employeeCode = string(matcher, "employee");				
			String enterpriseCode = string(matcher, "enterprise");				
			
			matcher = 
			find(reader, NAMES);
			String employeeName = string(matcher, "employee");
			salaryBuilder.setEmployeeName(employeeName);
			String enterpriseName = string(matcher, "enterprise");
			salaryBuilder.setEnterpriseName(enterpriseName);
			
			matcher = 
			find(reader, ADDRESS_SS_NIF, ADDRESS_NIF);
			StringBuffer address = new StringBuffer(string(matcher, "enterprise"));			
			String nif = string(matcher, "nif");
			salaryBuilder.setEmployeeDocument(nif);
			String naf = null ;
			try {
				naf = string(matcher, "naf");
			} catch ( IllegalArgumentException e) {
				matcher = find(reader, ADDRESS_SS);
				naf = string(matcher, "naf");
				address.append(" ").append(string(matcher, "enterprise")); 
			}
			naf = AonStringUtils.remove(naf, '/');
			naf = AonStringUtils.remove(naf, ' ');
			salaryBuilder.setSocialSecurityNumber(naf);
			salaryBuilder.setEnterpriseAddress(address.toString());

			
			
			matcher = 
			find(reader, CIF_CATEGORY);
			String cif = string(matcher, "cif");
			salaryBuilder.setEnterpriseDocument(cif);
			salaryBuilder.setCategory(string(matcher, "category"));
			
			matcher = 
			find(reader, CCC_GROUP_SENIOR);
			String ccc = string(matcher, "ccc");
			ccc = AonStringUtils.remove(ccc, '/');
			ccc = AonStringUtils.remove(ccc, ' ');
			salaryBuilder.setCcc(ccc);
			String quoteGroup = AonStringUtils.leftPad(string(matcher, "group"),2,'0');
			salaryBuilder.setQuoteGroup(quoteGroup);
			salaryBuilder.setSeniorityDate(date(matcher, "senior", "dd/MM/yyyy"));

			
			matcher = find(reader, DATES_DAYS);
			if (EXTRA_DAYS.matcher(matcher.group()).matches())
				throw new SalaryPDFException(String.format("Altai extras haven't dates, skip : %s,%s,%s,%s", ccc, naf,employeeName, enterpriseName) );

			String start = string(matcher, "start");
			String end = string(matcher, "end");
			int year = number(matcher, "year", 2020);
			Date startDate =date(String.format("%s/%d", start, year), "dd/MMMM/yyyy");
			Date endDate = date(String.format("%s/%d", end, year), "dd/MMMM/yyyy");
			salaryBuilder.setTimeUnits(number(matcher, "days", 0));


			salaryBuilder.setStartDate(startDate);
			salaryBuilder.setEndDate(endDate);
			
			salaryBuilder.setContract(
					new PDFContract()
					.setCcc(ccc) 
					.setNaf(naf)
					.setNif(nif)
					.setCif(cif)
					.setEndDate(endDate)
					.setStartDate(startDate)
					.setEmployeeCode(employeeCode)
					.setEnterpriseCode(enterpriseCode)
					.setEmployeeName(employeeName)
					.setEnterpriseName(enterpriseName)
			);
			
			Period period = new Period(startDate, endDate);

			optional(reader, HOURS).ifPresent(
			m -> {
				salaryBuilder.addData(SALARY_HOURS.getName(), new TimedObject<Double>(number(m, "horas", 0.00), period));
			}
			);			
						
			matcher = find(reader, PAYMENTS_I);
			
			matcher = find(reader, PAYMENTS_1);
			
			while ( (matcher = matches(reader, SUPPLEMENTS)) == null ) {
				matcher = find(reader, PAYMENT);
				payment(matcher, (amount,description) ->{
					salaryBuilder.addPayment(
							amount, 
							amount, 
							amount, 
							description, 
							startDate, 
							endDate, 
							new Payment().setType(CRA_0001).setName("SALARIO_BASE"), 
							Collections.emptyMap());
				});
			}
			
			while ( (matcher = matches(reader, EXTRA_HOURS)) == null  ) {
				matcher = find(reader, PAYMENT);
				payment(matcher, (amount,description) ->{
					salaryBuilder.addPayment(
							amount, 
							amount, 
							amount, 
							description, 
							startDate, 
							endDate, 
							new Payment().setType(CRA_0001).setName("PLUS_SALARIAL"), 
							Collections.emptyMap());
				});
			}

			
			payment(matcher, (amount,description) ->{
				salaryBuilder.addPayment(
						amount, 
						amount, 
						amount, 
						description, 
						startDate, 
						endDate, 
						new Payment().setType(CRA_0002).setName("HORAS_EXTRAS"), 
						Collections.emptyMap());
			});
			
			matcher = find(reader, PLUS_HOURS);
			payment(matcher, (amount,description) ->{
				salaryBuilder.addPayment(
						amount, 
						amount, 
						amount, 
						description, 
						startDate, 
						endDate, 
						new Payment().setType(CRA_0001).setName("HORAS_COMPL"), 
						Collections.emptyMap());
			});
			
			matcher = find(reader, EXTRAS  );
			payment(matcher, (amount,description) ->{
				salaryBuilder.addPayment(
						amount, 
						amount, 
						amount, 
						description, 
						startDate, 
						endDate, 
						new Payment().setType(CRA_0004).setName("PAGA_EXTRA"), 
						Collections.emptyMap());
			});
			double inkindIrpfBase []=  {0.00};
			matcher = find(reader, IN_KIND  );
			payment(matcher, (amount,description) ->{
				salaryBuilder.addPayment(
						amount, 
						amount, 
						amount, 
						description, 
						startDate, 
						endDate, 
						new Payment().setType(CRA_0013), 
						Collections.emptyMap());
				inkindIrpfBase[0] += amount;
			});
			
			find(reader, PAYMENTS_2);
			find(reader, COMPENSATIONS);
			
			while ( (matcher = matches(reader, TGSS_COMPENSATIONS)) == null  ) {
				matcher = find(reader, PAYMENT);
				payment(matcher, (amount,description) ->{
					salaryBuilder.addPayment(
							amount, 
							amount, 
							amount, 
							description, 
							startDate, 
							endDate, 
							new Payment().setType(CRA_0001).setName("INDEMNIZACION_SUP"),  //?? CRA
							Collections.emptyMap());
				});
			}
			
			while ( (matcher = matches(reader, END_COMPENSATIONS)) == null  ) {
				matcher = find(reader, PAYMENT);
				payment(matcher, (amount,description) ->{
					salaryBuilder.addPayment(
							amount, 
							amount, 
							amount, 
							description, 
							startDate, 
							endDate, 
							new Payment().setType(CRA_0001).setName("INDEMNIZACION_TGSS"),  //?? CRA
							Collections.emptyMap());
				});
			}
			
			while ( (matcher = matches(reader, NO_PAYMENTS)) == null  ) {
				matcher = find(reader, PAYMENT);
				payment(matcher, (amount,description) ->{
					salaryBuilder.addPayment(
							amount, 
							0.00, 
							0.00, 
							description, 
							startDate, 
							endDate, 
							new Payment().setType(CRA_0054).setName("INDEMNIZACION_FIN"),  //?? CRA
							Collections.emptyMap());
				});
			}
			
			while ( (matcher = matches(reader, TOTAL_PAYMENT)) == null  ) {
				matcher = find(reader, PAYMENT);
				payment(matcher, (amount,description) ->{
					salaryBuilder.addPayment(
							amount, 
							amount, 
							amount, 
							description, 
							startDate, 
							endDate, 
							new Payment().setType(CRA_0001),  //?? CRA
							Collections.emptyMap());
				});
			}
			
			salaryBuilder.setTotalPayment(number(matcher, "total", 0.00));
			
			matcher = find(reader, DEDUCTIONS);
			matcher = find(reader, TGSS_DEDUCTIONS);
			
			matcher = find(reader, CGC);
			double cgcBase = number(matcher, "base", 0.00);
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addDeduction(
						amount, 
						COMMON_CONTINGENCY.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(COMMON_CONTINGENCY).setName("CGC"), 
						Collections.singletonMap("PORCENTAJE_CGC", new TimedObject<Double>(percent, period )));
			});
			
			matcher = find(reader, DESMPL);
			double cgpBase = number(matcher, "base", 0.00);
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addDeduction(
						amount, 
						UNEMPLOYMENT.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(UNEMPLOYMENT).setName("DESMPL"), 
						Collections.singletonMap("PORCENTAJE_DESMPL", new TimedObject<Double>(percent, period )));
			});
			
			matcher = find(reader, FP);
			cgpBase = Math.max(cgpBase,number(matcher, "base", 0.00));
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addDeduction(
						amount, 
						JOB_TRAINING.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(JOB_TRAINING).setName("FP"), 
						Collections.singletonMap("PORCENTAJE_FP", new TimedObject<Double>(percent, period )));
			});
			
			matcher = find(reader, H_EXTRAS_FORCE);
			salaryBuilder.setHExtraBase(number(matcher, "base", 0.00));
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addDeduction(
						amount, 
						STRUCTURAL_OVERTIME.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(STRUCTURAL_OVERTIME).setName("ESTR"), 
						Collections.singletonMap("PORCENTAJE_EXTR", new TimedObject<Double>(percent, period )));
			});

			matcher = find(reader, H_EXTRAS_OTHER );
			salaryBuilder.setNonHExtraBase(number(matcher, "base", 0.00));
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addDeduction(
						amount, 
						NON_STRUCTURAL_OVERTIME.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(NON_STRUCTURAL_OVERTIME).setName("NESTR"), 
						Collections.singletonMap("PORCENTAJE_NEXTR", new TimedObject<Double>(percent, period )));
			});
			
			matcher = find(reader, TOTAL_TGSS );
			salaryBuilder.setTotalSS(number(matcher, "total", 0.00));

			matcher = find(reader, IRPF );
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addDeduction(
						amount, 
						DeductionType.IRPF.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(DeductionType.IRPF).setName("IRPF"), 
						Collections.singletonMap("PORCENTAJE_IRPF", new TimedObject<Double>(percent, period )));
				salaryBuilder.setTotalIrpf(amount);
			}, () -> {
				salaryBuilder.setTotalIrpf(0.000);
			});

			matcher = find(reader, ADVANCE );
			deduction(matcher, (amount) -> {
				salaryBuilder.addDeduction(
						amount, 
						"", 
						startDate, 
						endDate, 
						new Deduction().setType(DeductionType.ADVANCE_PAYMENT).setName("ANTICIPO"), 
						Collections.emptyMap());
			});

			matcher = find(reader, IN_K1ND );
			deduction(matcher, (amount) -> {
				salaryBuilder.addDeduction(
						amount, 
						"", 
						startDate, 
						endDate, 
						new Deduction().setType(DeductionType.IN_KIND).setName("EN_ESPECIE"), 
						Collections.emptyMap());
			});

			matcher = find(reader, OTHERS );
			deduction(matcher, (amount) -> {
				salaryBuilder.addDeduction(
						amount, 
						"", 
						startDate, 
						endDate, 
						new Deduction().setType(DeductionType.OTHER).setName("OTRA"), 
						Collections.emptyMap());
			});

			matcher = find(reader, TOTAL_DEDUCTION );
			salaryBuilder.setTotalDeduction(number(matcher, "total", 0.00));

			matcher = find(reader, TOTAL_LIQUID );
			salaryBuilder.setTotalLiquid(number(matcher, "total", 0.00));
			
			matcher = find(reader, ISSUE);
			Date issueDate = date(matcher, "date", "dd' de 'MMMM' de 'yyyy");
			//salaryBuilder.setIssueDate(issueDate);
			//salaryBuilder.setChargeDate(issueDate);
			salaryBuilder.setIssueDate(endDate);
			salaryBuilder.setChargeDate(endDate);
			
			
			matcher = find(reader, REMUNERATION);
			salaryBuilder.setRemuneration(number(matcher, "base", 0.00));
			
			matcher = find(reader, PRORORRATED);
			salaryBuilder.setProExtBase(number(matcher, "base", 0.00));

			matcher = find(reader, CGC_E);
			double cgcEnterpriseBase = number(matcher, "base", 0.00);
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addCost(
						amount, 
						COMMON_CONTINGENCY.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(COMMON_CONTINGENCY).setName("CGC_E"), 
						Collections.singletonMap("PORCENTAJE_CGC_E", new TimedObject<Double>(percent, period )));
			});
			
			matcher = find(reader, AT_EP);
			double cgpEnterpriseBase = number(matcher, "base", 0.00);
			deduction(matcher, (amount) -> {
				salaryBuilder.addCost(
						amount, 
						PROFESSIONAL_CONTINGENCY.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(PROFESSIONAL_CONTINGENCY).setName("IT_E"), 
						Collections.emptyMap());
				salaryBuilder.addCost(
						0.00, 
						PROFESSIONAL_CONTINGENCY.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(PROFESSIONAL_CONTINGENCY).setName("IMS_E"), 
						Collections.emptyMap());
			});

			matcher = find(reader, DESMPL_E);
			cgpEnterpriseBase = number(matcher, "base", 0.00);
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addCost(
						amount, 
						UNEMPLOYMENT.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(UNEMPLOYMENT).setName("DESMPL_E"), 
						Collections.singletonMap("PORCENTAJE_DESMPL_E", new TimedObject<Double>(percent, period )));
			});
			
			matcher = find(reader, FP_E);
			cgpEnterpriseBase = Math.max(cgpBase,number(matcher, "base", 0.00));
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addCost(
						amount, 
						JOB_TRAINING.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(JOB_TRAINING).setName("FP_E"), 
						Collections.singletonMap("PORCENTAJE_FP_E", new TimedObject<Double>(percent, period )));
			});

			matcher = find(reader, FOGASA);
			cgpEnterpriseBase = Math.max(cgpBase,number(matcher, "base", 0.00));
			deduction(matcher, (amount, percent) -> {
				salaryBuilder.addCost(
						amount, 
						DeductionType.FOGASA.getName(SPAIN), 
						startDate, 
						endDate, 
						new Deduction().setType(DeductionType.FOGASA).setName("FOGASA"), 
						Collections.singletonMap("PORCENTAJE_FOGASA", new TimedObject<Double>(percent, period )));
			});

			matcher = find(reader, IRPF_BASE);
			double irpfBase = number(matcher, "base", 0.00);
			salaryBuilder.setIrpfBase(number(matcher, "base", 0.00));

			salaryBuilder.setCgcBase(cgcBase);
			salaryBuilder.setRawCgcBase(cgcBase);
			salaryBuilder.setCgpBase(cgpBase);
			salaryBuilder.setInkindIrpfBase(inkindIrpfBase[0]);
			salaryBuilder.setMoneyIrpfBase(irpfBase-inkindIrpfBase[0]);
			
			salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(), new TimedObject<Double>(irpfBase, period ));
			salaryBuilder.addData(ContextVariable.CGC_BASE.getName(), new TimedObject<Double>(cgcBase, period ));
			salaryBuilder.addData(ContextVariable.CGP_BASE.getName(), new TimedObject<Double>(cgpBase, period ));
			salaryBuilder.addData(ContextVariable.CGC_BASE_ENTERPRISE.getName(), new TimedObject<Double>(cgcEnterpriseBase, period ));
			salaryBuilder.addData(ContextVariable.CGP_BASE_ENTERPRISE.getName(), new TimedObject<Double>(cgpEnterpriseBase, period ));
			salaryBuilder.addData(ContextVariable.QUOTE_GROUP.getName(), new TimedObject<String>(quoteGroup, period ));
			
			salaryBuilder.addData("__ENTERPRISE_CODE", new TimedObject<String>(enterpriseCode, period ));
			salaryBuilder.addData("__EMPLOYEE_CODE", new TimedObject<String>(employeeCode, period ));
			
			
//			int i = 0;
//			for ( String line = reader.readLine(); line != null; line = reader.readLine()) {
//				System.out.println("*---" + line);
//				if ( ++i > 0 )
//					break;
//			}

			salaryBuilder.getSalary();
		}
		
		
		return this;
	}
	
	private void payment(Matcher matcher, BiConsumer<Double, String> consumer)  {
		
		try {
			double amount = number(matcher, "amount").doubleValue();
			String description = string(matcher, "description");	
			consumer.accept(amount, description);
		} catch (Exception e) {
		}
		
	}
	
	private void deduction(Matcher matcher, BiConsumer<Double, Double> consumer)  {
		
		try {
			double percent = number(matcher, "percent").doubleValue();
			double amount = number(matcher, "amount").doubleValue();
			consumer.accept(amount, percent);
		} catch (Exception e) {
		}
		
	}
	
	private void deduction(Matcher matcher, BiConsumer<Double, Double> consumer, Runnable empty)  {
		
		try {
			double percent = number(matcher, "percent").doubleValue();
			double amount = number(matcher, "amount").doubleValue();
			consumer.accept(amount, percent);
		} catch (Exception e) {
			empty.run();
		}
		
	}

	private void deduction(Matcher matcher, Consumer<Double> consumer)  {
		
		try {
			double amount = number(matcher, "amount").doubleValue();
			consumer.accept(amount);
		} catch (Exception e) {
		}
		
	}

	private Matcher find( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ) {
//				System.out.println(line);
				continue;
			}
			
			return matcher;
		}
		
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
				
	}
	
	private Matcher find( BufferedReader reader, Pattern ...patterns ) throws IOException, UnknownPDFException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			for ( Pattern pattern : patterns ) {
				Matcher matcher = pattern.matcher(line) ;
				if ( matcher.matches() ) {
					return matcher;
				}
			}
		}
		
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  Arrays.stream(patterns).map(p -> p.pattern()).collect(Collectors.joining(","))));
				
	}

	private Matcher next( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		String line = reader.readLine() ; 
		//System.out.println(line);
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() )
			return matcher;
		
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
				
	}	
	
	private Matcher matches( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		reader.mark(256);
		String line = reader.readLine() ; 
		//System.out.println(line);
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() )
			return matcher;
		reader.reset();
		return null;
				
	}		

	private Optional<Matcher> optional( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		reader.mark(256);
		String line = reader.readLine() ; 
		//System.out.println(line);
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() )
			return Optional.of(matcher);
		
		reader.reset();
		
		return Optional.empty();
				
	}	

	private static String string(Matcher matcher, String name ) {
		String group = matcher.group(name);
		group = AonStringUtils.trim(group);
		return group;
	}
	
	private static Date date(Matcher matcher, String name, String pattern  ) throws UnknownPDFException{
		String group = matcher.group(name);
		group = AonStringUtils.trim(group);
		try {
			return new SimpleDateFormat(pattern, SPAIN).parse(group);
		} catch (ParseException e) {
			throw new UnknownPDFException(e);
		}
	}
	
	private static Date date(String str, String pattern  ) throws UnknownPDFException{
		str = AonStringUtils.trim(str);
		try {
			return new SimpleDateFormat(pattern, SPAIN).parse(str);
		} catch (ParseException e) {
			throw new UnknownPDFException(e);
		}
	}

	private static Calendar calendar(Matcher matcher, String name, String pattern  ) throws UnknownPDFException{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date(matcher, name, pattern));
		return calendar;
	}
	
	private static Number number(Matcher matcher, String name) throws ParseException{
		String group = matcher.group(name);
		group = AonStringUtils.trim(group);
		return NumberFormat.getNumberInstance(SPAIN).parse(group);
	}
	
	private static Number number(Matcher matcher, String name, Number def ){
		try {
			String group = matcher.group(name);
			group = AonStringUtils.trim(group);
			return NumberFormat.getNumberInstance(SPAIN).parse(group);
		} catch (ParseException e) {
			return def;
		}
	}
	
	private double number(Matcher matcher, String name, double def ){
		try {
			String group = matcher.group(name);
			group = AonStringUtils.trim(group);
			return NumberFormat.getNumberInstance(SPAIN).parse(group).doubleValue();
		} catch (ParseException | IllegalArgumentException | NullPointerException e) {
			return def;
		}
	}

	private int number(Matcher matcher, String name, int def ){
		try {
			String group = matcher.group(name);
			group = AonStringUtils.trim(group);
			return NumberFormat.getNumberInstance(SPAIN).parse(group).intValue();
		} catch (ParseException e) {
			return def;
		}
	}
	//Empresa------------------------ H225 Trabajador------------------------ 9
	private static final Pattern CODES = 
	Pattern.compile("^Empresa-*\\s+(?<enterprise>[A-Z0-9]+)\\s+Trabajador-*\\s+(?<employee>[0-9]+)"
	, Pattern.CASE_INSENSITIVE);

	//NAVAS GLEMBOTXKY, ABRAHAM          HEREDIA VALENZUELA, MARIA JOSE     
	private static final Pattern NAMES = 
//	Pattern.compile("(?<enterprise>(?:[^\\s]+\\s?)+)\\s{2,}(?<employee>(?:[^\\s]+\\s?)+)\\s*"
	Pattern.compile("(?<enterprise>.{1,35})(?<employee>.+)"
	, Pattern.CASE_INSENSITIVE);
	
	//Domicilio CL VICTOR BALAGUER, 10 N.I.F.: 044009461L Nº. Matrícula: Sec.: 
	private static final Pattern ADDRESS_NIF = 
	Pattern.compile("Domicilio\\s*(?<enterprise>.*)\\s*N.I.F.\\s*:\\s*(?<nif>.*)N..\\s*Matrícula\\s*:\\s*Sec.*"
	, Pattern.CASE_INSENSITIVE);
	
	//RUBI 08191 Núm. afiliación Seg. Social: 08/10366613/95
	private static final Pattern ADDRESS_SS = 
	Pattern.compile("(?<enterprise>.*)Núm.\\s*afiliación\\s*Seg.\\s*Social\\s*:\\s*(?<naf>.*)"
	, Pattern.CASE_INSENSITIVE);

	//Domicilio: CL FUENTECISNEROS, 66 N. afil. Seg. Social: 39/10288240/87 N.I.F.: 72183505Y 
	private static final Pattern ADDRESS_SS_NIF = 
	Pattern.compile("Domicilio\\s*:\\s*(?<enterprise>.*)N.\\s*afil.\\s*Seg.\\s*Social\\s*:\\s*(?<naf>.*)N.I.F.\\s*:\\s*(?<nif>.*)"
	, Pattern.CASE_INSENSITIVE);
	
	//C.I.F.: B55336762 Categ. o grupo prof.: CAMARERO       Sec.: 
	//C.I.F.: 46545854H Categoría o grupo profesional:                
	private static final Pattern CIF_CATEGORY = 
	Pattern.compile("(?:N.\\s*afil.\\s*Seg.\\s*Social\\s*:\\s*(?<naf>.*))?C.I.F.\\s*:\\s*(?<cif>.*)Categ[\\S]*\\s*o\\s*grupo prof[\\S]*\\s*:\\s*(?<category>.*)(?:Sec.\\s*:\\s*(?<sec>.*))?"
	, Pattern.CASE_INSENSITIVE);
	
	//Cód. Cta. de Cotización Seg. Soc.: 28/2397613/58 Grupo Cotización: 9 Fecha Antiguedad: 05/09/2019
	private static final Pattern CCC_GROUP_SENIOR = 
	Pattern.compile("Cód.\\s*Cta.\\s*de\\s*Cotización\\s*Seg.\\s*Soc.\\s*:\\s*(?<ccc>.*)Grupo\\s*Cotización\\s*:\\s*(?<group>.*)Fecha\\s*Antiguedad\\s*:\\s*(?<senior>.*)"
	, Pattern.CASE_INSENSITIVE);
	
	//Período de Liquidación 20/Septiembre a 30/Septiembre de 2.019 Total días: 06,00
	private static final Pattern DATES_DAYS  = 
	Pattern.compile("Período\\s*de\\s*Liquidación\\s*(?<start>[^\\s]+)\\s*[a-]\\s*(?<end>[^\\s]+)\\s*(?:de)?\\s*(?<year>.+)Total\\s*días\\s*:\\s*(?<days>.*)"
	, Pattern.CASE_INSENSITIVE);

	//Período de Liquidación 20/Septiembre a 30/Septiembre de 2.019 Total días: 06,00
	private static final Pattern EXTRA_DAYS  = 
	Pattern.compile("Período\\s*de\\s*Liquidación\\s*(?<paga>Paga\\s*.*)Total\\s*días\\s*:\\s*(?<days>.*)"
	, Pattern.CASE_INSENSITIVE);

	//Total horas: 24,00
	private static final Pattern HOURS  = 
	Pattern.compile("Total\\s*horas\\s*:\\s*(?<hours>.*)"
	, Pattern.CASE_INSENSITIVE);
	
	//I. DEVENGOS TOTALES
	private static final Pattern PAYMENTS_I  = 
	Pattern.compile("I.\\s*DEVENGOS\\s*TOTALES\\s*"
	, Pattern.CASE_INSENSITIVE);

	//1. Percepciones Salariales
	private static final Pattern PAYMENTS_1  = 
	Pattern.compile("1.\\s*Percepciones\\s*Salariales\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern PAYMENT  = 
	Pattern.compile("(?<description>(?:[^\\s]+\\s?)+)\\s*(?<amount>.*)"
	, Pattern.CASE_INSENSITIVE);
	
	//Complementos Salariales
	private static final Pattern SUPPLEMENTS  = 
	Pattern.compile("Complementos\\s*Salariales"
	, Pattern.CASE_INSENSITIVE);
	
	//2. Percepciones no salariales
	private static final Pattern PAYMENTS_2  = 
	Pattern.compile("2.\\s*Percepciones\\s*no\\s*salariales\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	//Horas Extraordinarias
	private static final Pattern EXTRA_HOURS  = 
	Pattern.compile("Horas\\s*Extraordinarias\\s*(?:(?<description>(?:[^\\s]+\\s?)+)\\s*(?<amount>.*))?"
	, Pattern.CASE_INSENSITIVE);	

	//Horas complementarias (contratos a tiempo parcial)
	private static final Pattern PLUS_HOURS  = 
	Pattern.compile("Horas\\s*complementarias\\s*\\([^\\)]*\\)\\s*(?:(?<description>(?:[^\\s]+\\s?)+)\\s*(?<amount>.*))?"
	, Pattern.CASE_INSENSITIVE);	

	//Gratificaciones extraordinarias Pagas Extras      10,28
	private static final Pattern EXTRAS  = 
	Pattern.compile("Gratificaciones\\s*extraordinarias\\s*(?:(?<description>(?:[^\\s]+\\s?)+)\\s*(?<amount>.*))?"
	, Pattern.CASE_INSENSITIVE);	

	//Salario en  especie      10,28
	private static final Pattern IN_KIND  = 
	Pattern.compile("Salario\\s*en\\s*especie\\s*(?:(?<description>(?:[^\\s]+\\s?)+)\\s*(?<amount>.*))?"
	, Pattern.CASE_INSENSITIVE);	

	//Indemnizaciones o suplidos
	private static final Pattern COMPENSATIONS  = 
	Pattern.compile("Indemnizaciones\\s*o\\s*suplidos"
	, Pattern.CASE_INSENSITIVE);
	
	//Prestaciones e Indemnizaciones de la Seguridad Social
	private static final Pattern TGSS_COMPENSATIONS  = 
	Pattern.compile("Prestaciones\\s*e\\s*Indemnizaciones\\s*de\\s*la\\s*Seguridad\\s*Social"
	, Pattern.CASE_INSENSITIVE);
	
	// Indemnizaciones por Traslados, suspensiones o despidos
	private static final Pattern END_COMPENSATIONS  = 
	Pattern.compile("Indemnizaciones\\s*por\\s*Traslados,\\s*suspensiones\\s*o\\s*despidos"
	, Pattern.CASE_INSENSITIVE);

	//Otras percepciones no salariales
	private static final Pattern NO_PAYMENTS  = 
	Pattern.compile("Otras\\s*percepciones\\s*no\\s*salariales"
	, Pattern.CASE_INSENSITIVE);
	
	// A. TOTAL DEVENGADO     327,48
	private static final Pattern TOTAL_PAYMENT  = 
	Pattern.compile("A\\.\\s*TOTAL\\s*DEVENGADO\\s*(?<total>.*)"
	, Pattern.CASE_INSENSITIVE);
	
	// II. DEDUCCIONES
	private static final Pattern DEDUCTIONS  = 
	Pattern.compile("II\\.\\s*DEDUCCIONES"
	, Pattern.CASE_INSENSITIVE);
	
	//1. Aportaciones del trabajador a las cotizaciones a la Seguridad Social y conceptos de aportación conjunta
	private static final Pattern TGSS_DEDUCTIONS  = 
	Pattern.compile("1\\.\\s*Aportaciones\\s*del\\s*trabajador\\s*a\\s*las\\s*cotizaciones\\s*a\\s*la\\s*Seguridad\\s*Social\\s*y\\s*conceptos\\s*de\\s*aportación\\s*conjunta"
	, Pattern.CASE_INSENSITIVE);
	
	//Contingencias comunes  327,48 4,70 %      15,39
	private static final Pattern CGC   = 
	Pattern.compile("Contingencias\\s*comunes\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);

	//	Desempleo  327,48 1,60 %       5,24
	private static final Pattern DESMPL  = 
	Pattern.compile("Desempleo\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//Formación profesional  327,48 0,10 %       0,33
	private static final Pattern FP  = 
	Pattern.compile("Formación\\s*profesional\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);


	//Formación profesional  327,48 0,10 %       0,33
	private static final Pattern H_EXTRAS_FORCE  = 
	Pattern.compile("Fuerza\\s*mayor\\s*o\\s*estructurales\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);

	//Formación profesional  327,48 0,10 %       0,33
	private static final Pattern H_EXTRAS_OTHER  = 
	Pattern.compile("No\\s*estructurales\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);

	//Total aportaciones      20,96
	private static final Pattern TOTAL_TGSS  = 
	Pattern.compile("Total\\s*aportaciones\\s*(?<total>.*)"
	, Pattern.CASE_INSENSITIVE);

	//2. Impuesto sobre la renta de las personas fisicas 02,00 %       6,55
	private static final Pattern IRPF  = 
	Pattern.compile("2\\.\\s*Impuesto\\s*sobre\\s*la\\s*renta\\s*de\\s*las\\s*personas\\s*fisicas\\s*(?<percent>[0-9.,]+)?\\s*%\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);

	//3. Anticipos 
	private static final Pattern ADVANCE  = 
	Pattern.compile("3\\.\\s*Anticipos\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);

	//4. Valor de los productos recibidos en especie 
	private static final Pattern IN_K1ND  = 
	Pattern.compile("4\\.\\s*Valor\\s*de\\s*los\\s*productos\\s*recibidos\\s*en\\s*especie\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//5. Otras deducciones 
	private static final Pattern OTHERS  = 
	Pattern.compile("5\\.\\s*Otras\\s*deducciones\\s*(?<inkind>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//B.  TOTAL  A  DEDUCIR  (1+2+3+4+5)      27,51
	private static final Pattern TOTAL_DEDUCTION  = 
	Pattern.compile("B\\.\\s*TOTAL\\s*A\\s*DEDUCIR\\s*[^\\s]*\\s*(?<total>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//LIQUIDO  TOTAL  A  PERCIBIR  (A-B)     299,97
	private static final Pattern TOTAL_LIQUID  = 
	Pattern.compile("LIQUIDO\\s*TOTAL\\s*A\\s*PERCIBIR\\s*[^\\s]*\\s*(?<total>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//Firma  y  sello  de  la  Empresa  ALCORCON 09 de Abril de 2020
	private static final Pattern ISSUE  = 
	Pattern.compile("Firma\\s*y\\s*sello\\s*de\\s*la\\s*Empresa.*(?<date>[0-9]{2}\\s*de\\s*[A-Z]+\\s*de\\s*[0-9]{4}).*"
	, Pattern.CASE_INSENSITIVE);	
	

	//4. Base sujeta a retención del I.R.P.F.     327,48	
	private static final Pattern IRPF_BASE  = 
	Pattern.compile("4\\.\\s*Base\\s*sujeta\\s*a\\s*retención\\s*del\\s*I\\.R\\.P\\.F\\.\\s*(?<base>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//Importe remuneración mensual     327,48
	private static final Pattern REMUNERATION  = 
	Pattern.compile("Importe\\s*remuneración\\s*mensual\\s*(?<base>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//Importe prorrata pagas extras
	private static final Pattern PRORORRATED  = 
	Pattern.compile("Importe\\s*prorrata\\s*pagas\\s*extras\\s*(?<base>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//Base incapacidad temporal Total     327,48 327,48 23,60% 77,29
	private static final Pattern CGC_E   = 
	Pattern.compile(".*Total\\s*(?<total>[0-9.,]+)?\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%?\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//2. Contingencias profesionales y conceptos AT y EP     327,48 6,54
	private static final Pattern AT_EP   = 
	Pattern.compile(".*AT\\s*y\\s*EP\\s*(?<base>[0-9.,]+)?\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//Desempleo  327,48 1,60 %       5,24
	private static final Pattern DESMPL_E  = 
	Pattern.compile(".*Desempleo\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%?\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//Formación profesional  327,48 0,10 %       0,33
	private static final Pattern FP_E  = 
	Pattern.compile(".*Formación\\s*profesional\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%?\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	//Formación profesional  327,48 0,10 %       0,33
	private static final Pattern FOGASA  = 
	Pattern.compile("Fondo\\s*de\\s*garantía\\s*salarial\\s*(?<base>[0-9.,]+)?\\s*(?<percent>[0-9.,]+)?\\s*%?\\s*(?<amount>[0-9.,]+)?.*"
	, Pattern.CASE_INSENSITIVE);
	
	
	private static void check(Pattern pattern, String str) {
		Matcher matcher =
		pattern.matcher(str);
		matcher.matches();
		for ( int g = 1; g <= matcher.groupCount(); g++) 
			System.out.println(matcher.group(g));
				
	}

	public static void main(String[] args) throws ParseException {
		check(CODES, "Empresa H097 Trabajador 3");
		check(NAMES, "EPB RECAMBIOS DE OCASION SL        MARIA SILVA EURISLENE              ");	
		check(NAMES, "EPB RECAMBIOS DE OCASION SL        BACA PEREZ, FERNANDO               ");
		check(NAMES, "MARIA SANTOS RODRIGUEZ RODRIGUEZ   RODRIGUEZ RODRIGUEZ LUZ STELLA     ");
		check(NAMES, "O2M-OBRAS, MANTENIMIENTOS Y MEJORAS LABRE REYES, JUAN ARTURO   ");
		check(ADDRESS_SS_NIF, "Domicilio: CL FUENTECISNEROS, 66 N. afil. Seg. Social: 39/10288240/87 N.I.F.: 72183505Y");	
		check(ADDRESS_NIF, "Domicilio CL VICTOR BALAGUER, 10 N.I.F.: 044009461L Nº. Matrícula: Sec.: ");
		check(ADDRESS_SS, "RUBI 08191 Núm. afiliación Seg. Social: 08/10366613/95");
		check(CIF_CATEGORY, "C.I.F.: B55336762 Categ. o grupo prof.: CAMARERO       Sec.: ");
		check(CIF_CATEGORY, "N. afil. Seg. Social: 18/10092630/74 C.I.F.: B55336762 Categ. o grupo prof.: CAMARERO       Sec.: ");
		check(CIF_CATEGORY, "C.I.F.: 46545854H Categoría o grupo profesional:                ");
		check(CCC_GROUP_SENIOR, "Cód. Cta. de Cotización Seg. Soc.: 28/2397613/58 Grupo Cotización: 9 Fecha Antiguedad: 05/09/2019");
		check(EXTRA_DAYS, "Período de Liquidación Paga Navidad Total días: 30,00");
		check(DATES_DAYS, "Período de Liquidación 20/Septiembre a 30/Septiembre de 2.019 Total días: 06,00");
		check(DATES_DAYS, "Período de Liquidación 01/Septiembre - 30/Septiembre 2.019 Total días: 30,00");
		check(HOURS, "Total horas: 24,00");
		check(PAYMENTS_I, "I. DEVENGOS TOTALES");
		check(PAYMENTS_1, "1. Percepciones Salariales");
		check(PAYMENTS_2, "2. Percepciones no salariales");
		check(PAYMENT, "Salario Base 4,534*30,00     136,04");
		check(EXTRA_HOURS, "Horas Extraordinarias");
		check(PLUS_HOURS, "Horas complementarias (contratos a tiempo parcial)");
		check(EXTRAS, "Gratificaciones extraordinarias Pagas Extras      10,28");
		check(IN_KIND, "Salario en  especie");
		check(COMPENSATIONS, "Indemnizaciones o suplidos");
		check(TGSS_COMPENSATIONS, "Prestaciones e Indemnizaciones de la Seguridad Social");
		check(END_COMPENSATIONS, "Indemnizaciones por Traslados, suspensiones o despidos");
		check(NO_PAYMENTS, "Otras percepciones no salariales");
		check(TOTAL_PAYMENT, "A. TOTAL DEVENGADO     327,48");
		check(DEDUCTIONS, "II. DEDUCCIONES");
		check(TGSS_DEDUCTIONS, "1. Aportaciones del trabajador a las cotizaciones a la Seguridad Social y conceptos de aportación conjunta");
		check(CGC, "Contingencias comunes  327,48 4,70 %      15,39");
		check(DESMPL,"Desempleo  327,48 1,60 %       5,24");
		check(FP, "Formación profesional  327,48 0,10 %       0,33");
		check(FP, "Formación profesional  %       ");
		check(H_EXTRAS_FORCE, "Fuerza mayor o estructurales %");
		check(H_EXTRAS_OTHER, "No estructurales %");
		check(TOTAL_TGSS, "Total aportaciones      20,96");
		check(IRPF, "2. Impuesto sobre la renta de las personas fisicas 02,00 %       6,55");
		check(ADVANCE, "3. Anticipos");
		check(IN_K1ND, "4. Valor de los productos recibidos en especie");
		check(OTHERS, "5. Otras deducciones");
		check(TOTAL_DEDUCTION, "B.  TOTAL  A  DEDUCIR  (1+2+3+4+5)      27,51");
		check(TOTAL_LIQUID, "LIQUIDO  TOTAL  A  PERCIBIR  (A-B)     299,97");
		check(ISSUE, "Firma  y  sello  de  la  Empresa  ALCORCON 09 de Abril de 2020");
		check(REMUNERATION, "Importe remuneración mensual     327,48");
		check(PRORORRATED, "Importe prorrata pagas extras");
		check(CGC_E, "Base incapacidad temporal Total     327,48 327,48 23,60% 77,29");
		check(CGC_E, "Base incapacidad temporal Total");
		check(CGC_E, "Base incapacidad temporal     799,20 Total     799,20 799,20 23,60% 188,61");
		check(AT_EP, "2. Contingencias profesionales y conceptos AT y EP     327,48 6,54");
		check(AT_EP, "2. Contingencias profesionales y conceptos AT y EP");
		check(DESMPL_E, "de recaudación conjunta Desempleo     327,48 6,70% 21,94");
		check(DESMPL_E, "de recaudación conjunta Desempleo");
		check(FP_E, "Formación profesional     327,48 0,60% 1,96");
		check(FP_E, "Formación profesional");
		check(FOGASA, "Fondo de garantía salarial     327,48 0,20% 0,65");
		check(FOGASA, "Fondo de garantía salarial");
		check(IRPF_BASE, "4. Base sujeta a retención del I.R.P.F.     327,48");
		check(IRPF_BASE, "4. Base sujeta a retención del I.R.P.F.");

	
	
		System.out.println(new SimpleDateFormat("dd' de 'MMMM' de 'yyyy").parse("2 de Abril de 2020"));
		
		System.out.println(new SimpleDateFormat("dd/MMMM").parse("29/Febrero"));
	}









//	Recibí,
//	Determinación de las bases de cotización a la Seguridad Social y conceptos de recaudación conjunta y de la base sujeta a retención del  
//	I.R.P.F. y aportación de la empresa
//	Concepto Base Tipo Aportación empresa
//	1. Contingencias comunes
	

//	3. Base de cotización adicional por horas extras
}
