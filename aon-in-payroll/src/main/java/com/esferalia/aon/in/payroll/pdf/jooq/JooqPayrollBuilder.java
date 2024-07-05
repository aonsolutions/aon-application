package com.esferalia.aon.in.payroll.pdf.jooq;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.UNPAID;
import static com.esferalia.aon.watson.server.AonDateUtils.getDayOfWeek;
import static com.esferalia.aon.watson.util.AonDateUtils.compare;
import static com.esferalia.aon.watson.util.AonDateUtils.max;
import static com.esferalia.aon.watson.util.AonDateUtils.min;
import static com.esferalia.aon.watson.util.AonStringUtils.containsIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.equalsIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.isEmpty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.DefaultPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.IPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.ContingencyBases.ContingencyBasesBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.IDefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PartTimeParams;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PartTimeParams.PartTimeEntry;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PayrollTypes;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.HolidayDetailRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.occam.api.model.Salary.Payment;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Class containing method/s to print payrolls from database data
 */
public class JooqPayrollBuilder {

	private static String[] WEEK_DAYS = { "DOMINGO", "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO" };
	private static List<String> PRESTATION_CONCEPTS = Arrays.asList("PREST_IT", "MTNAD", "ERE");

	/**
	 * Method to generate a PDF payroll from database data and place it on the
	 * OutputStream passed as parameter
	 * 
	 * @param enterpriseId The id of the enterprise
	 * @param domainName   The database domain name
	 * @param user         The user (may be empty)
	 * @param outputStream The output stream which the PDF will be written on
	 * @param salaryIds    The IDs of the salaries in database
	 */
	public static void generatePayroll(Integer enterpriseId, String domainName, String user, OutputStream outputStream,
			Optional<Double> complementaryLimit, Integer... salaryIds) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, user)) {
			byte[] logo = getLogo(aonContext, enterpriseId);
			Collection<IDefaultPayroll> payrolls = buildPayrolls(aonContext, salaryIds, complementaryLimit, logo);
			printAon(outputStream, payrolls, logo);
		}
	}

	public static void generateClassicPayroll(Integer enterpriseId, String domainName, String user,
			OutputStream outputStream, Optional<Double> complementaryLimit, Integer... salaryIds) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, user)) {
			byte[] logo = getLogo(aonContext, enterpriseId);
			Collection<IDefaultPayroll> payrolls = buildPayrolls(aonContext, salaryIds, complementaryLimit, logo);
			printClassic(outputStream, payrolls, logo);
		}
	}

	/**
	 * Method to generate a PDF payroll from database data and place it on the
	 * OutputStream passed as parameter
	 * 
	 * @param enterpriseId The id of the enterprise
	 * @param domainName   The database domain name
	 * @param outputStream The output stream which the PDF will be written on
	 * @param salaryIds    The IDs of the salaries in database
	 */
	public static void generatePayroll(Integer enterpriseId, String domainName, OutputStream outputStream,
			Optional<Double> complementaryLimit, Integer... salaryIds) {
		generatePayroll(enterpriseId, domainName, "", outputStream, complementaryLimit, salaryIds);
	}

	public static void generateClassicPayroll(Integer enterpriseId, String domainName, OutputStream outputStream,
			Optional<Double> complementaryLimit, Integer... salaryIds) {
		generateClassicPayroll(enterpriseId, domainName, "", outputStream, complementaryLimit, salaryIds);
	}

	/**
	 * Method to generate a PDF payroll from database data and place it on the
	 * OutputStream passed as parameter
	 * 
	 * @param domainName   The database domain name
	 * @param outputStream The output stream which the PDF will be written on
	 * @param salaryIds    The IDs of the salaries in database
	 */
	public static void generatePayroll(String domainName, OutputStream outputStream,
			Optional<Double> complementaryLimit, Integer... salaryIds) {
		generatePayroll(null, domainName, "", outputStream, complementaryLimit, salaryIds);
	}

	public static void generateClassicPayroll(String domainName, OutputStream outputStream,
			Optional<Double> complementaryLimit, Integer... salaryIds) {
		generateClassicPayroll(null, domainName, "", outputStream, complementaryLimit, salaryIds);
	}

	private static byte[] getLogo(AONContext aonContext, Integer enterpriseId) {
		// LOGO
		Optional<InputStream> optLogo = Optional.empty();
		{

			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
					REGISTRY);

			if (attach1 == null || attach1.getData() == null)
				attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
						f -> f.getTypeProperty().eq(LOGO.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
						REGISTRY);

			if (attach1 != null && attach1.getData() != null)
				optLogo = Optional.ofNullable(new ByteArrayInputStream(attach1.getData()));
		}
		return getBytes(optLogo);
	}

	private static Collection<IDefaultPayroll> buildPayrolls(AONContext aonContext, Integer[] salaryIds,
			Optional<Double> complementaryLimit, byte[] logo) {

		// PICK UP THE SALARIES
		Stream<Salary> salaries = AON.getSalaries(aonContext, p -> p.getIdProperty().in(salaryIds));

		return salaries.map(salary -> {
			DefaultPayrollBuilder payrollBuilder = new DefaultPayrollBuilder();
			// PAYROLL RELATED DATA
			{
				payrollBuilder.setTotalDays(salary.getSalaryDays());
				payrollBuilder.setLiquidPeriodStart(salary.getStartDate());
				payrollBuilder.setLiquidPeriodEnd(getSalaryEndDate(salary));

				/**
				 * Comparing salary type
				 */
				switch (salary.getSalaryType()) {
				case SALARY:
					payrollBuilder.setPayrollType(PayrollTypes.Type.SALARY);
					break;
				case SETTLE:
					payrollBuilder.setPayrollType(PayrollTypes.Type.SETTLEMENT);
					break;
				case EXTRA:
					payrollBuilder.setPayrollType(PayrollTypes.Type.EXTRAS);
					break;
				case DELAY:
					payrollBuilder.setPayrollType(PayrollTypes.Type.ARREARS_WAGE);
					break;
				case L00:
				case L03:
				case L13:
				default:
					payrollBuilder.setPayrollType(PayrollTypes.Type.SALARY);
					break;
				}

			}

			// ENTERPRISE RELATED DATA
			{
				payrollBuilder.setCcc(salary.getEnterpriseCCC());
				payrollBuilder.setCif(salary.getEnterpriseDocument());
				payrollBuilder.setEnterprise(salary.getEnterpriseName());

				// ADDRESS FITTING

				RaddressRecord registryAddress = aonContext.getDslContext().select(RADDRESS.asterisk()).from(SALARY)
						.innerJoin(CONTRACT).on(SALARY.CONTRACT.eq(CONTRACT.ID)).innerJoin(WORKPLACE)
						.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(RADDRESS)
						.on(WORKPLACE.ADDRESS.eq(RADDRESS.ID)).where(SALARY.ID.eq(salary.getId()))
						.fetchOneInto(RADDRESS);

				// ----- FOR MAIN ADDRESS -----
				List<RAddress> raddessList = AON
						.getRAddressStream(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
								f -> f.getRegistryProperty().eq(registryAddress.getRegistry())
										.and(f.getDomainProperty().eq(aonContext.getDomainId())))
						.collect(Collectors.toList());

				Optional<RAddress> mainRaddress = raddessList.stream()
						.filter(rad -> rad != null && AonNumberUtils.equals(AonNumberUtils.toByte(0), rad.getType()))
						.findFirst();

				RAddress raddress = null;

				if (raddessList != null && !raddessList.isEmpty()) {
					raddress = mainRaddress.isPresent() ? mainRaddress.get() : raddessList.get(0);
				}

				// ----------------------------
				/*
				 * //----- FOR WORKPLACE ADDRESS -----
				 * 
				 * RAddress raddress = AON.getRAddress( aonContext.getDomainName(),
				 * aonContext.getDomainId(), aonContext.getUser(), f ->
				 * f.getIdProperty().eq(registryAddress.getId()).and(f.getDomainProperty().eq(
				 * aonContext.getDomainId())));
				 * 
				 * //---------------------------------
				 */

				if (null != raddress) {

					String add = !isEmpty(raddress.getFullAddress()) ? raddress.getFullAddress()
							: salary.getEnterpriseAddress();

					String streetType = safeValue(raddress.getStreet_type());
					String number = safeValue(raddress.getNumber());

					String address1 = safeValue(raddress.getAddress());
					String address2 = !isEmpty(raddress.getAddress2()) ? ", " + raddress.getAddress2() : "";
					String address3 = safeValue(raddress.getAddress3());

					String zip = safeValue(raddress.getZip());
					String city = safeValue(raddress.getCity());

					String firstLine = streetType + " " + address1 + " " + number + " " + address3;

					if (firstLine.length() <= 45) {
						firstLine = streetType + " " + address1 + " " + number + " " + address2 + address3;
					}

					String sekandoRain = zip + " " + city;

					if (add != null) {
						if (!isEmpty(firstLine != null ? firstLine.trim() : "") && firstLine.length() < 45
								&& sekandoRain.length() < 45) {
							if (!isEmpty(firstLine))
								payrollBuilder.setAddress(firstLine);
							if (!isEmpty(sekandoRain))
								payrollBuilder.setAddress2(sekandoRain);
						} else {

							String[] address = separateString(add, 40);
							if (address != null && address.length > 1) {
								payrollBuilder.setAddress(address[0] != null ? address[0].trim() : null);
								String secline = "";
								for (int i = 1; i < address.length; i++) {
									secline += address[i];
								}
								if (secline.length() > 46)
									secline = secline.substring(0, 45).concat("...");
								payrollBuilder.setAddress2(secline);
							} else {
								payrollBuilder.setAddress(salary.getEnterpriseAddress());
							}
						}

					}

				}

			}

			// EMPLOYEE RELATED DATA
			{
				payrollBuilder.setAntiquity(salary.getEmployeeSeniorityDate());
				// weird names check
				String employeeName = salary.getEmployeeName().trim();
				if (employeeName != null && employeeName.length() > 1 && employeeName.charAt(0) == ',')
					employeeName = employeeName.substring(1).trim();
				payrollBuilder.setEmployee(employeeName);
				payrollBuilder.setNif(salary.getEmployeeDocument());
				payrollBuilder.setNss(salary.getEmployeeSSNumber());
				payrollBuilder.setProfessionalGroup(salary.getEmployeeCategory());
				payrollBuilder.setQuotationGroup(salary.getEmployeeQuoteGroup());
				payrollBuilder.setTotalSSContributions(salary.getTotalSSContributions());
			}

			// PAYMENTS
			double nonStructBase[] = new double[] { 0d };
			double forceMajeureBase[] = new double[] { 0d };

			{
				payrollBuilder.setAccrualTotal(salary.getTotalPayment());
				HashMap<Integer, ArrayList<PDFPayment>> paymentMap = new HashMap<Integer, ArrayList<PDFPayment>>();
				salary.getPayments().stream().filter(JooqPayrollBuilder::filter).sorted(Comparator.comparing(p -> {
					return !(p.getDescription() == null || p.getDescription().isEmpty()) ? p.getDescription()
							: "zzzzzz";
				})).forEach(p -> {

					String description = AonStringUtils.isNotBlank(p.getDescription())
							? p.getDescription().replaceAll("\\[\\d*\\]", "")
							: p.getDescription();
					if (AonStringUtils.isNotBlank(description) && description.length() > 50) {
						try {
							description = croppedString(description, 260, HELVETICA, 9f);
						} catch (IOException ignored) {
						}
					}

					/**
					 * Getting nonStruct and forceMajeure bases
					 */

					// Structural
					if (p.getPaymentType() == com.esferalia.aon.occam.api.model.type.PaymentType.CRA_0002) {
						nonStructBase[0] += p.getAmount();
					}

					// Force Majeure
					if (p.getPaymentType() == com.esferalia.aon.occam.api.model.type.PaymentType.CRA_0003) {
						forceMajeureBase[0] += p.getAmount();
					}

					PDFPayment accrual = new PDFPayment(p.getAmount(), description);

					// Check this!! (set CRA0001 if not exist)
					if (null == p.getPaymentType())
						p.setPaymentType(com.esferalia.aon.occam.api.model.type.PaymentType.CRA_0001);

					int craKey = p.getPaymentType().ordinal();
					if (Objects.equals(ContextVariable.PPE, p.getName())) {
						craKey = IPayrollTemplate.PPE;
						accrual.setAmount(p.getQuote());
					} else if (Objects.equals(ContextVariable.NOTE, p.getName())) {
						craKey = IPayrollTemplate.NOTE;
					} else if (Objects.equals(ContextVariable.INFO, p.getName())) {
						craKey = IPayrollTemplate.INFO;
					} else if (Objects.equals(ContextVariable.CAUTION, p.getName())) {
						craKey = IPayrollTemplate.WARNING;
					} else if (com.esferalia.aon.occam.api.model.type.PaymentType.CRA_0001.equals(p.getPaymentType())) {
						// TODO: COMPROBAR PREST_IT, ERE% Y MTNAD
						if (PRESTATION_CONCEPTS.contains(p.getName())
								|| AonStringUtils.equals("ERE_", AonStringUtils.substring(p.getName(), 0, 4))) {
							craKey = 100;
						}
					}
					if (!paymentMap.containsKey(craKey)) {
						paymentMap.put(craKey, new ArrayList<PDFPayment>());
					}

					Optional<PDFPayment> repeated = paymentMap.get(craKey).stream().filter(acc -> AonStringUtils
							.equalsIgnoreCase(p.getDescription(), acc.getDescription().orElse(null))).findFirst();

					if (repeated.isPresent()) {
						PDFPayment repAcc = repeated.get();
						repAcc.setAmount(repAcc.getAmount().orElse(0d) + p.getAmount());
					} else {
						paymentMap.get(craKey).add(accrual);
					}
				});
				payrollBuilder.setAccruals(paymentMap);
			}
			// COLLECTING DATA (FOR PERCENTAGES)
			Map<String, List<ContextData>> data = salary.getContextData();

			// DEDUCTIONS
			{
				payrollBuilder.setDeductionTotal(salary.getTotalDeduction());

				ArrayList<String> inserted = new ArrayList<String>();

				HashMap<Integer, ArrayList<PDFDeduction>> deductionMap = new HashMap<Integer, ArrayList<PDFDeduction>>();
				salary.getDeductions().stream().sorted(Comparator.comparing(d -> {
					return !(d.getDescription() == null || d.getDescription().isEmpty()) ? d.getDescription()
							: chooseDescription(d.getDeductionType());
				})).forEach(d -> {
					String deductionName = d.getName();
					DeductionType deductionType = d.getDeductionType();

					if (deductionType == null)
						return;

					List<ContextData> percList = data
							.get("PORCENTAJE_" + getDeductionType(deductionName, deductionType.ordinal()));
					ContextData cd = percList != null ? percList.get(0) : null;
					Double percent = null;
					if (cd != null) {
						if (cd.getExpression() != null)
							percent = Double.parseDouble(cd.getExpression());
						else
							percent = -1d;
					}

					int type = chooseType(deductionType);
					String description = d.getDescription();

					if (description == null || description.isEmpty()) {
						description = chooseDescription(deductionName);
					}
					if (description == null || description.isEmpty()) {
						description = chooseDescription(deductionType);
					}

					PDFDeduction pdfDeductionEntry = new PDFDeduction(d.getAmount(), d.getName(), description, percent,
							deductionType);
					if (!deductionMap.containsKey(type))
						deductionMap.put(type, new ArrayList<>());

					if (deductionMap.get(type).stream().anyMatch(ded -> equalsIgnoreCase(ded.getDescription().get(),
							pdfDeductionEntry.getDescription().get()))) {
						PDFDeduction ded = deductionMap.get(type).stream()
								.filter(d1 -> equalsIgnoreCase(d1.getDescription().get(),
										pdfDeductionEntry.getDescription().get()))
								.findFirst().get();

						ded.setAmount(ded.getAmount().get() + pdfDeductionEntry.getAmount().get());
					} else
						deductionMap.get(type).add(pdfDeductionEntry);
					inserted.add(getDeductionType(deductionName, deductionType.ordinal()));
				});

				if (deductionMap.get(1) == null)
					deductionMap.put(1, new ArrayList<PDFDeduction>());
				if (deductionMap.get(2) == null)
					deductionMap.put(2, new ArrayList<PDFDeduction>());

				if (!inserted.contains("CGC"))
					deductionMap.get(1).add(new PDFDeduction(0d, "CGC", "Contingencias Comunes", 0d));
				if (!inserted.contains("MEI"))
					deductionMap.get(1).add(new PDFDeduction(0d, "MEI", "Mecanismo de Equidad Intergeneracional (MEI)",
							0d, DeductionType.MEI));
				if (!inserted.contains("DESMPL"))
					deductionMap.get(1).add(new PDFDeduction(0d, "DESMPL", "Desempleo", 0d));
				if (!inserted.contains("FP"))
					deductionMap.get(1).add(new PDFDeduction(0d, "FP", "Formación Profesional", 0d));
				if (!inserted.contains("IRPF"))
					deductionMap.get(2).add(new PDFDeduction(0d, "IRPF", "Retribuciones Dinerarias", 0d));

				// EMBARGOS (placed at 'Other deductions' -type 5- field on 'Deductions')
				{
					List<Embargo> embargos = salary.getEmbargos();
					embargos.forEach(e -> {
						PDFDeduction emb = new PDFDeduction(e.getAmount(), null, e.getDescription(), null);
						if (deductionMap.containsKey(5))
							deductionMap.get(5).add(emb);
						else {
							ArrayList<PDFDeduction> deducts = new ArrayList<PDFDeduction>();
							deducts.add(emb);
							deductionMap.put(5, deducts);
						}
					});
				}

				payrollBuilder.setDeductions(deductionMap);

			}

			// COSTS
			Double totalEnterprise = 0d;
			{
				ContingencyBasesBuilder costBuilder = new ContingencyBasesBuilder();
				// SETTING AMOUNTS
				{
					Double commonContApEnterprise = 0d;
					Double meiApEnterprise = 0d;
					Double atEpApEnterprise = 0d;
					Double unemploymentApEnterprise = 0d;
					Double profesFormApEnterprise = 0d;
					Double fogasaApEnterprise = 0d;
					Double forceMajeureApEnterprise = 0d;
					Double noStructApEnterprise = 0d;

					for (Cost cost : salary.getCosts()) {
						totalEnterprise += (cost.getAmount() != null ? cost.getAmount() : 0d);
						switch (cost.getCostType()) {
						case COMMON_CONTINGENCY:
							commonContApEnterprise += safeValue(cost.getAmount());
							break;
						case MEI:
							meiApEnterprise += safeValue(cost.getAmount());
							break;
						case IT:
						case IMS:
							atEpApEnterprise += safeValue(cost.getAmount());
							break;
						case UNEMPLOYMENT:
							unemploymentApEnterprise += safeValue(cost.getAmount());
							break;
						case JOB_TRAINING:
							profesFormApEnterprise += safeValue(cost.getAmount());
							break;
						case FOGASA:
							fogasaApEnterprise += safeValue(cost.getAmount());
							break;
						case STRUCTURAL_OVERTIME:
							forceMajeureApEnterprise += safeValue(cost.getAmount());
							break;
						case NON_STRUCTURAL_OVERTIME:
							noStructApEnterprise += safeValue(cost.getAmount());
							break;
						case ADVANCE_PAYMENT:
						case IN_KIND:
						case IRPF:
						case OTHER:
						case PROFESSIONAL_CONTINGENCY:
						default:
							break;
						}

					}

					// SET COST VALUES
					costBuilder.setCommonContApEnterprise(Optional.ofNullable(commonContApEnterprise));
					costBuilder.setMeiApEnterprise(Optional.ofNullable(meiApEnterprise));
					costBuilder.setAtEpApEnterprise(Optional.ofNullable(atEpApEnterprise));
					costBuilder.setUnemploymentApEnterprise(Optional.ofNullable(unemploymentApEnterprise));
					costBuilder.setProfesFormApEnterprise(Optional.ofNullable(profesFormApEnterprise));
					costBuilder.setFogasaApEnterprise(Optional.ofNullable(fogasaApEnterprise));
					costBuilder.setForceMajeureApEnterprise(Optional.ofNullable(forceMajeureApEnterprise));
					costBuilder.setNoStructApEnterprise(Optional.ofNullable(noStructApEnterprise));

					// REMUNERATION AND PRO. EXT. BASE
					costBuilder.setExtraProrationAmount(Optional.ofNullable(salary.getExtraProrationBase()));
					costBuilder.setMonthlyAmount(Optional.ofNullable(salary.getRemuneration()));
				}

				double[] atEp = new double[] { 0 };

				// SETTING PERCENTAGES
				{
					data.keySet().stream().filter(k -> AonStringUtils.containsIgnoreCase(k, "PORCENTAJE")
							|| AonStringUtils.containsIgnoreCase(k, "TARIFA")).forEach(costName -> {
								ContextData cd = data.get(costName).get(0);

								if (data.get(costName) != null) {
									if (containsIgnoreCase(costName, "PORCENTAJE_CGC_E")) {
										if (cd.getExpression() != null) {
											costBuilder.setCommonContType(
													Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										} else
											costBuilder.setCommonContType(Optional.of(-1.00));
									} else if (containsIgnoreCase(costName, "PORCENTAJE_MEI_E")) {
										if (cd.getExpression() != null) {
											costBuilder.setMeiType(
													Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										} else
											costBuilder.setMeiType(Optional.of(-1.00));
									} else if (containsIgnoreCase(costName, "TARIFA_IMS")
											|| containsIgnoreCase(costName, "TARIFA_IT")) {

										if (cd.getExpression() != null) {
											atEp[0] += Double.parseDouble(cd.getExpression());
											costBuilder.setAtEpType(Optional.ofNullable(atEp[0]));

										} else
											costBuilder.setAtEpType(Optional.of(-1.00));
									} else if (containsIgnoreCase(costName, "PORCENTAJE_DESMPL_E")) {
										if (cd.getExpression() != null)
											costBuilder.setUnemploymentType(
													Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										else
											costBuilder.setUnemploymentType(Optional.of(-1.00));
									} else if (containsIgnoreCase(costName, "PORCENTAJE_FP_E")) {
										if (cd.getExpression() != null)
											costBuilder.setProfesFormType(
													Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										else
											costBuilder.setProfesFormType(Optional.of(-1.00));
									} else if (containsIgnoreCase(costName, "PORCENTAJE_FOGASA")) {
										if (cd.getExpression() != null)
											costBuilder.setFogasaType(
													Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										else
											costBuilder.setFogasaType(Optional.of(-1.00));
									} else if (containsIgnoreCase(costName, "PORCENTAJE_EXTR_E")) {
										if (cd.getExpression() != null)
											costBuilder.setForceMajeureType(
													Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										else
											costBuilder.setForceMajeureType(Optional.of(-1.00));
									} else if (containsIgnoreCase(costName, "PORCENTAJE_NEXTR_E")) {
										if (cd.getExpression() != null)
											costBuilder.setNoStructType(
													Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										else
											costBuilder.setNoStructType(Optional.of(-1.00));
									}
								}
							});
				}

				// SETTING BASES
				{
					costBuilder.setCommonContBase(Optional.ofNullable(salary.getCommonContingenciesBase()));
					costBuilder.setProfessionalContBase(Optional.ofNullable(salary.getProfessionalContingenciesBase()));
					costBuilder.setIrpfRetribDiner(Optional.ofNullable(salary.getMoneyIrpfBase()));
					costBuilder.setIrpfEsp(Optional.ofNullable(salary.getInkindIrpfBase()));
					costBuilder.setTotal(Optional.ofNullable(salary.getTotalEnterprise()));
					costBuilder.setNoStructBase(Optional.of(nonStructBase[0]));
					costBuilder.setForceMajeureBase(Optional.of(forceMajeureBase[0]));
				}
				payrollBuilder.setContingencies(costBuilder.build());
			}
			payrollBuilder.setPayrollTotal(salary.getTotalLiquid());

			// -----PART TIME-----
			managePartTime(aonContext, payrollBuilder, salary, complementaryLimit, logo);
			// -------------------

			return payrollBuilder.build();
		}).collect(Collectors.toList());
	}

	private static class SalaryData {
		String expression;
		Date startDate;
		Date endDate;
		String name;

		public SalaryData(String expression, Date startDate, Date endDate) {
			this.expression = expression;
			this.startDate = startDate;
			this.endDate = endDate;
		}

		public SalaryData(ContextData contextData) {
			if (contextData != null) {
				this.expression = contextData.getExpression();
				this.startDate = contextData.getStartDate();
				this.endDate = contextData.getEndDate();
			}
		}

		public SalaryData() {
		}

		public Date getEndDate() {
			return endDate;
		}

		public SalaryData setEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}

		public Date getStartDate() {
			return startDate;
		}

		public SalaryData setStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}

		public String getExpression() {
			return expression;
		}

		public SalaryData setExpression(String expression) {
			this.expression = expression;
			return this;
		}

		public String getName() {
			return name;
		}

		public SalaryData setName(String name) {
			this.name = name;
			return this;
		}

		private boolean intersectsWith(Date startDate, Date endDate) {
			return compare(max(startDate, this.startDate), min(endDate, this.endDate)) <= 0;
		}

		public static Map<String, List<SalaryData>> convertContextDatas(Map<String, List<ContextData>> contextDatas) {
			if (contextDatas == null) {
				return Collections.emptyMap();
			}
			Map<String, List<SalaryData>> map = new LinkedHashMap<>();
			contextDatas.forEach((k, v) -> {
				List<SalaryData> list = new LinkedList<>();
				v.forEach(data -> {
					list.add(new SalaryData(data));
				});
				map.put(k, list);
			});

			return Collections.unmodifiableMap(map);
		}

	}

	private static Map<String, List<SalaryData>> getHoursData(Map<String, List<SalaryData>> salaryData) {
		if (salaryData == null)
			return Collections.emptyMap();
		List<String> keys = Arrays.stream(WEEK_DAYS).map(str -> "HORAS_" + str)
				.collect(Collectors.toUnmodifiableList());
		Map<String, List<SalaryData>> map = new LinkedHashMap<>();
		salaryData.forEach((k, v) -> {
			if (keys.contains(k)) {
				map.put(k, v);
			}
		});
		return Collections.unmodifiableMap(map);
	}

	private static void managePartTime(AONContext aonContext, DefaultPayrollBuilder payrollBuilder, Salary salary,
			Optional<Double> complementaryLimit, byte[] logo) {

		Map<String, List<SalaryData>> salaryDataTmp = SalaryData.convertContextDatas(salary.getContextData());
		Map<String, List<SalaryData>> contractDataTmp = getContractDataBySalary(aonContext, salary.getId());
		List<SalaryData> partialities = salaryDataTmp.getOrDefault("COEFICIENTE_PARCIALIDAD", Collections.emptyList());
		if (partialities.isEmpty()) {
			partialities = contractDataTmp.getOrDefault("COEFICIENTE_PARCIALIDAD", Collections.emptyList());
		}
		List<SalaryData> workedDays = salaryDataTmp.getOrDefault("DIAS_TRABAJADOS", Collections.emptyList());
		if (workedDays.isEmpty()) {
			workedDays = contractDataTmp.getOrDefault("DIAS_TRABAJADOS", Collections.emptyList());
		}

		Map<String, List<SalaryData>> hoursData = getHoursData(salaryDataTmp);
		Map<String, List<SalaryData>> datas = new LinkedHashMap<>();
		Map<String, List<SalaryData>> contractHoursData = getHoursData(contractDataTmp);
		if (hoursData.isEmpty() && !contractHoursData.isEmpty()) {
			datas.putAll(contractHoursData);
		}
		datas.putAll(salaryDataTmp);
		Map<String, List<SalaryData>> salaryData = Collections.unmodifiableMap(datas);

		boolean isPartiality = partialities.stream().anyMatch(
				sd -> getExpressionValue(sd.getExpression()) != null && getExpressionValue(sd.getExpression()) < 1);

		if (SalaryType.SALARY.equals(salary.getSalaryType()) && isPartiality && !workedDays.isEmpty()) {
			Date date = salary.getStartDate();
			double contractHours = 0;
			PartTimeParams params = new PartTimeParams(date).setEnterpriseCCC(salary.getEnterpriseCCC())
					.setEnterpriseDocument(salary.getEnterpriseDocument()).setEnterpriseName(salary.getEnterpriseName())
					.setEmployeeName(salary.getEmployeeName()).setPaymentDate(salary.getIssueDate());

			Date salaryStart = salary.getStartDate();
			Date salaryEnd = salary.getEndDate();
			Period salaryPeriod = new Period(salaryStart, salaryEnd);

			List<SalaryData> holidays = contractDataTmp.getOrDefault("DIAS_VACACIONES", Collections.emptyList());
			List<SalaryData> noWorkDays = contractDataTmp.getOrDefault("NO_LABORABLE", Collections.emptyList());
			List<Date> holidayList = listHolidays(holidays, salaryEnd);
			List<Date> noWorkDaysList = listHolidays(noWorkDays, salaryEnd);
			
			List<Date> festiveList = listFestives(aonContext, salary.getId(), salaryStart, salaryEnd);

			setHolidays(params, holidayList, salaryPeriod, entry -> entry.setHoliday(true));
			setHolidays(params, noWorkDaysList, salaryPeriod, entry -> entry.setNotWorkingDay(true));

			Set<Date> workedDaysSet = new LinkedHashSet<>();
			while (date.compareTo(salary.getEndDate()) <= 0) {
				int day = AonDateUtils.getDay(date);
				PartTimeEntry entry = new PartTimeEntry();
				if (isWorkedDay(date, salaryData, salary.getEndDate(), holidayList, noWorkDaysList, festiveList)) {
					Double dayHours = getDayHours(date, salaryData, salary.getEndDate());
					entry.setOrdinary(dayHours);
					if (dayHours != null && dayHours > 0) {
						boolean daysData = areThereDaysData(date, salaryData, salary.getEndDate());
						if (daysData || (!daysData && getDayOfWeek(date) > 1 && getDayOfWeek(date) < 7)) {
							params.addEntry(day, entry);
							workedDaysSet.add(date);
							contractHours += dayHours;
						}
					}
				}
				date = AonDateUtils.addDays(date, 1);
			}
			params.setContractHours(contractHours);

			date = salary.getStartDate();
			double limit = complementaryLimit.orElse(0d);

			List<SalaryData> complementaryHoursSD = salaryDataTmp.getOrDefault("HORAS_COMPLEMENTARIAS",
					Collections.emptyList());
			List<SalaryData> complementaryHoursCDNotFiltered = contractDataTmp.getOrDefault("HORAS_COMPLEMENTARIAS",
					Collections.emptyList());

			List<SalaryData> complementaryHoursCD = complementaryHoursCDNotFiltered.stream().filter(ch -> {
				Date chsd = ch.getStartDate();
				Date ched = ch.getEndDate() != null ? ch.getEndDate() : salaryEnd;
				Period chper = new Period(chsd, ched);
				return salaryPeriod.intersects(chper);
			}).collect(Collectors.toList());

			Double chCdSum = complementaryHoursCD.stream().map(ch -> getExpressionValue(ch.getExpression())).reduce(0d,
					(a, b) -> a + b);
			Double chSdSum = complementaryHoursSD.stream().map(ch -> getExpressionValue(ch.getExpression())).reduce(0d,
					(a, b) -> a + b);
			List<SalaryData> complementaryHours = null;
			if (AonNumberUtils.equals(chCdSum, chSdSum)) {
				complementaryHours = complementaryHoursCD;
			} else {
				complementaryHours = complementaryHoursSD;
			}

			for (SalaryData sd : complementaryHours) {
				Date sdStart = sd.getStartDate();
				Date sdEnd = sd.getEndDate() != null ? sd.getEndDate() : salary.getEndDate();
				Period period = new Period(sdStart, sdEnd);
				Double value = getExpressionValue(sd.getExpression());
				if (value != null && value > 0) {
					List<Date> daysList = workedDaysSet.stream().filter(period::contains).collect(Collectors.toList());
					if (!daysList.isEmpty() || AonDateUtils.isSameDay(sdStart, sdEnd)) {
						if (AonDateUtils.isSameDay(sdStart, sdEnd) && daysList.isEmpty()) {
							daysList.add(sdStart);
						}
						int days = daysList.size();
						while (daysList.size() > 1 && value / days < limit) {
							daysList.remove(daysList.size() - 1);
							days = daysList.size();
						}
						final double valuePerDay = value / days;
						double roundedValuePerDay = Math.round(valuePerDay * 100) / 100d;
						double accumulated = 0;
						for (int i = 0; i < daysList.size(); i++) {
							Date d = daysList.get(i);
							int day = AonDateUtils.getDay(d);
							double complValue = roundedValuePerDay;
							if (i == daysList.size() - 1) {
								complValue = value - accumulated;
							}
							params.getEntry(day).setComplementary(complValue);
							accumulated += roundedValuePerDay;
						}
					}
				}
			}

			if (logo != null) {
				params.setEnterpriseSignature(logo);
			}
			payrollBuilder.setPartTimeParams(Optional.of(params));
		}
	}

	private static List<Date> listFestives(AONContext aonContext, Integer salaryId, Date salaryStart, Date salaryEnd) {
		Result<Record> payrollWokplaceRecords = aonContext.getDslContext().select().from(PAYROLL_WORKPLACE)
			.join(WORKPLACE)
			.on(WORKPLACE.ID.eq(PAYROLL_WORKPLACE.WORKPLACE))
			.join(CONTRACT)
			.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
			.join(SALARY)
			.on(SALARY.CONTRACT.eq(CONTRACT.ID))
			.where(SALARY.ID.eq(salaryId))
			.fetch();
		
		if(payrollWokplaceRecords.isEmpty())
			return new ArrayList<>();
		
		Record payrollWokplaceRecord = payrollWokplaceRecords.get(payrollWokplaceRecords.size() - 1);
		
		Integer holidayId = aonContext.getDslContext().select(CALENDAR.HOLIDAY).from(CALENDAR)
				.where(CALENDAR.ID.eq(payrollWokplaceRecord.get(PAYROLL_WORKPLACE.CALENDAR)))
				.fetchOne(CALENDAR.HOLIDAY);
		
		ArrayList<Integer> holidays = new ArrayList<>();
		
		// Get all holidays ids
		while ( holidayId != null ) {
			holidays.add(holidayId);
			
			holidayId = aonContext.getDslContext().select(HOLIDAY.HOLIDAY_)
					.from(HOLIDAY)
					.where(HOLIDAY.ID.eq(holidayId))
					.fetchOne()
					.get(HOLIDAY.HOLIDAY_);
		}
		
		if(holidays.isEmpty())
			return new ArrayList<>();
		
		Result<HolidayDetailRecord> holidayRecords = aonContext.getDslContext().selectFrom(HOLIDAY_DETAIL)
				.where(HOLIDAY_DETAIL.HOLIDAY.in(holidays))
				.and(HOLIDAY_DETAIL.DATE.ge(new java.sql.Date(salaryStart.getTime())))
				.and(HOLIDAY_DETAIL.DATE.le(new java.sql.Date(salaryEnd.getTime())))
				.fetch();
		
		if(holidayRecords.isEmpty())
			return new ArrayList<>();
		
		List<Date> festiveDates = new ArrayList<>();
		
		for(HolidayDetailRecord holidayRecord : holidayRecords) {
			festiveDates.add(holidayRecord.getDate());
		}
		
		return festiveDates;
		
	}

	@FunctionalInterface
	private static interface HolidayCallback {
		void set(PartTimeEntry entry);
	}

	private static void setHolidays(PartTimeParams params, List<Date> holidayList, Period salaryPeriod,
			HolidayCallback callback) {
		params.getEntries().entrySet().stream()
				.filter(entry -> holidayList.stream().filter(d -> salaryPeriod.intersects(new Period(d, d)))
						.map(AonDateUtils::getDay).anyMatch(d -> AonNumberUtils.equals(d, entry.getKey())))
				.forEach(entry -> callback.set(entry.getValue()));
	}

	private static List<Date> listHolidays(List<SalaryData> holidaysData, Date salaryEndDate) {
		if (holidaysData != null) {
			List<Date> dateList = new ArrayList<>();
			holidaysData.stream().filter(Objects::nonNull).forEach(data -> {
				Date sd = data.getStartDate();
				Date ed = data.getEndDate() != null ? data.getEndDate() : salaryEndDate;
				try {
					new Period(sd, ed).forEachDay(cal -> {
						dateList.add(cal.getTime());
					});
				} catch (Exception e) {
					// Falla porque alguien ha puesto la fecha de fin menor que la de inicio
				}
			});
			return dateList;
		}
		return Collections.emptyList();
	}

	private static Map<String, List<SalaryData>> getContractDataBySalary(AONContext aonContext,
			final Integer salaryId) {
		if (aonContext == null || salaryId == null)
			return Collections.emptyMap();

		Map<String, List<SalaryData>> map = new LinkedHashMap<>();

		aonContext.getDslContext().select(CONTRACT_DATA.asterisk()).from(CONTRACT).innerJoin(CONTRACT_DATA)
				.on(CONTRACT.ID.eq(CONTRACT_DATA.CONTRACT)).innerJoin(SALARY).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.where(SALARY.ID.eq(salaryId)).and(CONTRACT_DATA.START_DATE.le(SALARY.END_DATE))
				.fetchStreamInto(CONTRACT_DATA).map(JooqPayrollBuilder::convertContractData)
				.filter(cd -> cd != null && !AonStringUtils.isEmpty(cd.getName())).forEach(cd -> {
					List<SalaryData> cdList = map.getOrDefault(cd.getName(), new LinkedList<>());
					cdList.add(cd);
					map.put(cd.getName(), cdList);
				});
		return map;
	}

	private static SalaryData convertContractData(ContractDataRecord cdr) {
		return new SalaryData().setEndDate(cdr.getEndDate()).setExpression(cdr.getExpression())
				.setStartDate(cdr.getStartDate()).setName(cdr.getName());
	}

	private static void printAon(OutputStream outputStream, Collection<IDefaultPayroll> payrolls, byte[] logo) {
		// PRINT
		try {
			PayrollTemplate template = new PayrollTemplate(payrolls,
					Optional.ofNullable(logo != null ? new ByteArrayInputStream(logo) : null),
					Optional.ofNullable(new Locale("es")));
			template.print(outputStream);
		} catch (CanNotCreatePdfException ignored) {
		}
	}

	private static void printClassic(OutputStream outputStream, Collection<IDefaultPayroll> payrolls, byte[] logo) {
		// PRINT
		try {
			IPayrollTemplate template = new DefaultPayrollTemplate(payrolls,
					Optional.ofNullable(logo != null ? new ByteArrayInputStream(logo) : null),
					Optional.ofNullable(new Locale("es")));
			template.print(outputStream);
		} catch (CanNotCreatePdfException ignored) {
		}
	}

	private static byte[] getBytes(Optional<InputStream> optLogo) {
		try {
			return optLogo.isPresent() ? optLogo.get().readAllBytes() : null;
		} catch (IOException e1) {
			return null;
		}
	}

	private static Double getDayHours(Date date, Map<String, List<SalaryData>> salaryData, Date salaryEnd) {
		if (date == null || salaryData == null || salaryEnd == null)
			return null;
		String name = "HORAS_" + WEEK_DAYS[AonDateUtils.getDayOfWeek(date) - 1];
		Optional<SalaryData> optHoursData = salaryData.getOrDefault(name, Collections.emptyList()).stream()
				.filter(sd -> {
					try {
						Period period = new Period(sd.getStartDate(),
								sd.getEndDate() != null ? sd.getEndDate() : salaryEnd);
						return period.contains(date);
					} catch (IllegalArgumentException e) {
						// Si el periodo es erróneo
						return false;
					}
				}).findFirst();
		if (optHoursData.isPresent()) {
			return getExpressionValue(optHoursData.get().getExpression());
		}
		if (!areThereDaysData(date, salaryData, salaryEnd)) {
			Optional<SalaryData> optPartiality = salaryData
					.getOrDefault("COEFICIENTE_PARCIALIDAD", Collections.emptyList()).stream()
					.filter(sd -> new Period(sd.getStartDate(), sd.getEndDate() != null ? sd.getEndDate() : salaryEnd)
							.contains(date))
					.findFirst();
			if (optPartiality.isPresent()) {
				Double coef = getExpressionValue(optPartiality.get().getExpression());
				return coef != null ? coef * 8 : null;
			}
			return 8d;
		} else {
			return null;
		}
	}

	private static boolean areThereDaysData(Date date, Map<String, List<SalaryData>> salaryData, Date salaryEnd) {
		if (salaryData == null || date == null || salaryEnd == null)
			return false;
		List<String> days = Arrays.asList(WEEK_DAYS);
		return days.stream().anyMatch(day -> salaryData.containsKey("HORAS_" + day)
				&& salaryData.get("HORAS_" + day).stream().anyMatch(sd -> {
					try {
						return new Period(sd.getStartDate(), sd.getEndDate() != null ? sd.getEndDate() : salaryEnd)
								.contains(date);
					} catch (IllegalArgumentException e) {
						// Período erróneo
						return false;
					}
				}));
	}

	private static boolean isWorkedDay(Date date, Map<String, List<SalaryData>> salaryData, Date salaryEnd,
			List<Date> holidayList, List<Date> noWorkDaysList, List<Date> festiveList) {
		if (salaryData == null || date == null || salaryEnd == null)
			return false;
		if (holidayList != null && holidayList.contains(date)) {
			return false;
		}
		if (noWorkDaysList != null && noWorkDaysList.contains(date)) {
			return false;
		}
		
		// Check if its festive day and dont have hours
		Double dayHours = getDayHours(date, salaryData, salaryEnd);
		if (festiveList != null && festiveList.contains(date) && !(dayHours != null && dayHours > 0)) {
			return false;
		}
		
		List<SalaryData> workedDays = salaryData.getOrDefault("DIAS_TRABAJADOS", Collections.emptyList());
		List<SalaryData> realSessions = salaryData.getOrDefault("JORNADAS_REALES", Collections.emptyList());
		boolean isInWorkPeriod = workedDays.stream()
				.anyMatch(sd -> new Period(sd.getStartDate(), sd.getEndDate() != null ? sd.getEndDate() : salaryEnd)
						.contains(date));
		if (isInWorkPeriod) {
			if (areThereDaysData(date, salaryData, salaryEnd)) {
				return true;
			} else if (!realSessions.isEmpty()) {
				// TODO
				return realSessions.stream().anyMatch(
						sd -> new Period(sd.getStartDate(), sd.getEndDate() != null ? sd.getEndDate() : salaryEnd)
								.contains(date));
			} else {
				return AonDateUtils.getDayOfWeek(date) > 1 && AonDateUtils.getDayOfWeek(date) < 7;
			}
		}
		return false;
	}

	/**
	 * Returns if amount and quote are empty
	 * 
	 * @param payment - The payment
	 * @return true | false
	 */
	private static boolean filter(Payment payment) {
		List<String> logConcepts = Arrays.asList(ContextVariable.LOGS);
		List<String> excludedDescriptionWords = Arrays.asList("VACACIONES");
		List<String> excludedConcepts = Arrays.asList(PREST_IT, UNPAID.getName());

		return !(payment.getAmount() == 0 && payment.getQuote() == 0) || logConcepts.contains(payment.getName())
				|| excludedConcepts.contains(payment.getName()) || excludedDescriptionWords.stream()
						.anyMatch(word -> AonStringUtils.containsIgnoreCase(payment.getDescription(), word));
	}

	/**
	 * Get safe value
	 * 
	 * @param value - The value.
	 * @return value | 0
	 */
	private static Double safeValue(Double value) {
		return value != null ? value : 0d;
	}

	/**
	 * Get safe value
	 * 
	 * @param value - The value.
	 * @return value | ""
	 */
	private static String safeValue(String value) {
		return value != null ? value : "";
	}

	private static Double getExpressionValue(String expression) {
		if (expression == null)
			return null;
		try {
			return Double.parseDouble(expression);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static String[] separateString(String str, int length) {
		if (str == null)
			return null;
		if (str.length() <= length) {
			return new String[] { str };
		} else {
			LinkedList<String> strList = new LinkedList<String>();
			String tempStr = str;
			while (!tempStr.isEmpty()) {
				int lng = length;
				if (tempStr.length() <= lng) {
					lng = tempStr.length();
				}
				String line = tempStr.substring(0, lng);
				if (tempStr.charAt(lng - 1) != ' ' && tempStr.length() > lng && line.contains(" ")) {
					int ind = line.lastIndexOf(' ');
					line = tempStr.substring(0, ind);
				}
				strList.add(line);
				if (tempStr.length() <= lng) {
					tempStr = "";
				} else
					tempStr = tempStr.substring(line.length());
			}
			return strList.toArray(new String[strList.size()]);
		}
	}

	/**
	 * Method to get the deduction type int of the DefaultPayroll given the
	 * DeductionType of the payroll
	 * 
	 * @param dt The original DeductionType
	 * @return the int of the type for DefaultPayroll
	 */
	private static int chooseType(DeductionType dt) {
		switch (dt.ordinal()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 13:
			return 1;
		case 6:
			return 2;
		case 7:
			return 3;
		case 8:
			return 4;
		default:
			return 5;
		}
	}

	/**
	 * Method to get the deduction type int of the DefaultPayroll given the int
	 * 
	 * @param dt The original int
	 * @return the int of the type for DefaultPayroll
	 */
	private static int chooseType(int dt) {
		switch (dt) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 13:
			return 1;
		case 6:
			return 2;
		case 7:
			return 3;
		case 8:
			return 4;
		default:
			return 5;
		}
	}

	private static String chooseDescription(String deductionName) {
		if (AonStringUtils.isBlank(deductionName))
			return null;
		switch (deductionName) {
		case "MEI":
		case "MEI_E":
			return "Mecanismo de Equidad Intergeneracional (MEI)";
		default:
			return null;
		}
	}

	/**
	 * Method to get a suitable description for the given DeductionType
	 * 
	 * @param dt The DeductionType enum object
	 * @return a String containing a suitable description
	 */
	private static String chooseDescription(DeductionType dt) {
		switch (dt.ordinal()) {
		case 0:
			return "Contingencias Comunes";
		case 1:
			return "Contingencias Profesionales";
		case 2:
			return "Desempleo";
		case 3:
			return "Formación Profesional";
		case 4:
			return "Horas Extraordinarias (Estruc.)";
		case 5:
			return "Horas Extraordinarias (No Estruc.)";
		case 6:
			return "Retribuciones Dinerarias";
		case 7:
			return "Anticipo";
		case 8:
			return "En Especie";
		case 10:
			return "Embargo";
		case 13:
			return "Mecanismo de Equidad Intergeneracional (MEI)";
		default:
			return "Otras Deducciones";
		}
	}

	/**
	 * Method to get a suitable description for the given int
	 * 
	 * @param dt
	 * @return a String containing a suitable description
	 */
	private static String chooseDescription(int dt) {
		switch (dt) {
		case 0:
			return "Contingencias comunes";
		case 1:
			return "Contingencias profesionales";
		case 2:
			return "Desempleo";
		case 3:
			return "Formación profesional";
		case 4:
			return "Horas extraordinarias (Estruc.)";
		case 5:
			return "Horas extraordinarias (No Estruc.)";
		case 6:
			return "Retribuciones dinerarias";
		case 7:
			return "Anticipo";
		case 8:
			return "En especie";
		case 10:
			return "Embargo";
		case 13:
			return "Mecanismo de Equidad Intergeneracional (MEI)";
		default:
			return "Otras deducciones";
		}
	}

	private static String getDeductionType(String deductionName, Integer type) {
		switch (type) {
		case 0:
			return deductionName;
		case 2:
			return "DESMPL";
		case 3:
			return "FP";
		case 4:
			return "ESTR";
		case 5:
			return "NO_ESTR";
		case 6:
			return "IRPF";
		case 7:
			return "ADELANTO";
		case 8:
			return "EN_ESPECIE";
		case 9:
			return "OTRO";
		case 10:
			return "EMBARGO";
		case 13:
			return "MEI";
		default:
			return null;
		}
	}

	private static Date getSalaryEndDate(Salary salary) {
		Date startDate = salary.getStartDate();
		return salary.getContextData().getOrDefault(ContextVariable.NO_HOLIDAYS.getName(), Collections.emptyList())
				.stream().map(ContextData::getStartDate).filter(d -> d.after(startDate))
				.collect(Collectors.minBy(Date::compareTo)).map(d -> AonDateUtils.addDays(d, -1))
				.orElse(salary.getEndDate());
	}

}
