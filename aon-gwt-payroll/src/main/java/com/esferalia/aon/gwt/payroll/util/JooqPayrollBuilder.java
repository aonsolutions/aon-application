package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.gwt.payroll.util.Utilities.separateString;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;
import static com.esferalia.aon.watson.util.AonStringUtils.containsIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.equalsIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.isEmpty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PayrollTypes;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.ContingencyBases.ContingencyBasesBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.occam.api.model.Salary.Payment;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Class containing method/s to print payrolls from database data
 */
public class JooqPayrollBuilder {
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
			Integer... salaryIds) {
		PayrollTemplate dpt = new PayrollTemplate();
		DefaultPayrollBuilder dpb = new DefaultPayrollBuilder();
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			fillPayroll(outputStream, dpt, dpb, aonContext, salaryIds);
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
			Integer... salaryIds) {
		generatePayroll(enterpriseId, domainName, "", outputStream, salaryIds);
	}

	/**
	 * Method to generate a PDF payroll from database data and place it on the
	 * OutputStream passed as parameter
	 * 
	 * @param domainName   The database domain name
	 * @param outputStream The output stream which the PDF will be written on
	 * @param salaryIds    The IDs of the salaries in database
	 */
	public static void generatePayroll(String domainName, OutputStream outputStream, Integer... salaryIds) {
		generatePayroll(null, domainName, "", outputStream, salaryIds);
	}

	private static void fillPayroll(OutputStream outputStream, PayrollTemplate payrollTemplate, DefaultPayrollBuilder payrollBuilder,
			AONContext aonContext, Integer[] salaryIds) {
		
		// PICK UP THE SALARIES
		Stream<Salary> salaries = AON.getSalaries(aonContext, p -> p.getIdProperty().in(salaryIds));
		Collection<DefaultPayroll> payrolls = salaries.map(salary -> {

			// PAYROLL RELATED DATA
			{
				payrollBuilder.setLiquidPeriodStart(salary.getStartDate());
				payrollBuilder.setLiquidPeriodEnd(salary.getEndDate());
				payrollBuilder.setTotalDays(salary.getSalaryDays());
				
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
				Registry registry = AON.getRegistry(
						aonContext.getDomainName(), 
						aonContext.getDomainId(), 
						"",
						p -> p.getDocumentProperty().eq(salary.getEnterpriseDocument()).and(p.getDomainProperty().eq(aonContext.getDomainId()))
				);

				RAddress raddress = AON.getRAddress(
							aonContext.getDomainName(), 
							aonContext.getDomainId(),
							aonContext.getUser(), 
							p -> p.getRegistryProperty().eq(registry.getId()).and(p.getDomainProperty().eq(aonContext.getDomainId()))
						);
				
				String add = raddress.getFullAddress() != null ? raddress.getFullAddress() : salary.getEnterpriseAddress();

				String streetType = safeValue(raddress.getStreet_type());
				String number = safeValue(raddress.getNumber());
				
				String address1 = safeValue(raddress.getAddress());
				String address2 = isEmpty(raddress.getAddress2()) ? ", " + raddress.getAddress2(): "";
				String address3 = safeValue(raddress.getAddress3());

				String zip = safeValue(raddress.getZip());
				String city = safeValue(raddress.getCity());
				
				String firstLine = "";
				
				if (firstLine.length() > 45) {
					firstLine = streetType + " " + address1 + " " + number + " " + address3;
				}
				else {
					firstLine = streetType + " " + address1 + " " + number + " " + address2 + address3;
				}

				String sekandoRain = zip + " " + city;

				if (add != null) {
					if (firstLine.length() < 45 && sekandoRain.length() < 45) {
						payrollBuilder.setAddress(firstLine);
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
			}
			
			// PAYMENTS
			double nonStructBase[] = new double[]{ 0d };
			double forceMajeureBase[] = new double[]{ 0d };
			
			{	
				payrollBuilder.setAccrualTotal(salary.getTotalPayment());
				HashMap<Integer, ArrayList<PDFPayment>> paymentMap = new HashMap<Integer, ArrayList<PDFPayment>>();
				salary.getPayments()
				.stream()
				.filter(JooqPayrollBuilder::filter)
				.sorted(Comparator.comparing(p -> {return !(p.getDescription() == null || p.getDescription().isEmpty()) ? p.getDescription(): "zzzzzz";}))
				.forEach(p -> {

					String description = p.getDescription().replaceAll("\\[\\d*\\]", "");
					if (description.length() > 50) {
						try {
							description = croppedString(description, 260, HELVETICA, 9f);
						} catch (IOException ignored) {}
					}
					
					/**
					 * Getting nonStruct and forceMajeure bases
					 */
					
					//Structural
					if (p.getPaymentType() == com.esferalia.aon.occam.api.model.type.PaymentType.CRA_0002) {						
						nonStructBase[0] += p.getAmount();
					}
					
					//Force Majeure
					if(p.getPaymentType() == com.esferalia.aon.occam.api.model.type.PaymentType.CRA_0003) {
						forceMajeureBase[0] += p.getAmount();
					}
					

					PDFPayment accrual = new PDFPayment(p.getAmount(), description);
					if (!paymentMap.containsKey(p.getPaymentType().ordinal()))
						paymentMap.put(p.getPaymentType().ordinal(), new ArrayList<PDFPayment>());

					if (paymentMap.get(p.getPaymentType().ordinal()).stream().anyMatch(
							acc -> AonStringUtils.equalsIgnoreCase(p.getDescription(), acc.getDescription().get()))) {
						PDFPayment repAcc = paymentMap.get(p.getPaymentType().ordinal()).stream().findFirst().get();
						repAcc.setAmount(repAcc.getAmount().orElse(0d) + p.getAmount());
					} else
						paymentMap.get(p.getPaymentType().ordinal()).add(accrual);
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
							: Utilities.chooseDescription(d.getDeductionType());
				})).forEach(d -> {
					DeductionType deductionType = d.getDeductionType();
					
					if(deductionType == null) return;
					
					List<ContextData> percList = data.get("PORCENTAJE_" + Utilities.getDeductionType(deductionType.ordinal()));
					ContextData cd = percList != null ? percList.get(0) : null;
					Double percent = null;
					if (cd != null) {
						if (cd.getExpression() != null)
							percent = Double.parseDouble(cd.getExpression());
						else
							percent = -1d;
					}

					int type = Utilities.chooseType(deductionType);
					String description = d.getDescription();
					
					if (description == null || description.isEmpty()) {
						description = Utilities.chooseDescription(deductionType);
					}

					PDFDeduction pdfDeductionEntry = new PDFDeduction(d.getAmount(), description, percent);
					if (!deductionMap.containsKey(type))
						deductionMap.put(type, new ArrayList<PDFDeduction>());

					if (deductionMap.get(type).stream().anyMatch(ded -> equalsIgnoreCase(ded.getDescription().get(), pdfDeductionEntry.getDescription().get()))) {
						PDFDeduction ded = deductionMap.get(type)
								.stream()
								.filter(d1 -> equalsIgnoreCase(d1.getDescription().get(), pdfDeductionEntry.getDescription().get()))
								.findFirst()
								.get();
						
						ded.setAmount(ded.getAmount().get() + pdfDeductionEntry.getAmount().get());
					} else
						deductionMap.get(type).add(pdfDeductionEntry);
					inserted.add(Utilities.getDeductionType(deductionType.ordinal()));
				});

				if (deductionMap.get(1) == null)
					deductionMap.put(1, new ArrayList<PDFDeduction>());
				if (deductionMap.get(2) == null)
					deductionMap.put(2, new ArrayList<PDFDeduction>());

				if (!inserted.contains("CGC"))
					deductionMap.get(1).add(new PDFDeduction(0d, "Contingencias comunes", 0d));
				if (!inserted.contains("DESMPL"))
					deductionMap.get(1).add(new PDFDeduction(0d, "Desempleo", 0d));
				if (!inserted.contains("FP"))
					deductionMap.get(1).add(new PDFDeduction(0d, "Formación profesional", 0d));
				if (!inserted.contains("IRPF"))
					deductionMap.get(2).add(new PDFDeduction(0d, "Retribuciones dinerarias", 0d));

				// EMBARGOS (placed at 'Other deductions' -type 5- field on 'Deductions')
				{
					List<Embargo> embargos = salary.getEmbargos();
					embargos.forEach(e -> {
						PDFDeduction emb = new PDFDeduction(e.getAmount(), e.getDescription(), null);
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
							case IT:
							case IMS:
								atEpApEnterprise += safeValue(cost.getAmount());
								break;
							case UNEMPLOYMENT:
								unemploymentApEnterprise  += safeValue(cost.getAmount());
								break;
							case JOB_TRAINING:
								profesFormApEnterprise  += safeValue(cost.getAmount());
								break;
							case FOGASA:
								fogasaApEnterprise  += safeValue(cost.getAmount());
								break;
							case STRUCTURAL_OVERTIME:
								forceMajeureApEnterprise  += safeValue(cost.getAmount());
								break;
							case NON_STRUCTURAL_OVERTIME:
								noStructApEnterprise  += safeValue(cost.getAmount());
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
											costBuilder.setFogasaType(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										else
											costBuilder.setFogasaType(Optional.of(-1.00));
									}
									else if (containsIgnoreCase(costName, "PORCENTAJE_EXTR_E")) {
										if (cd.getExpression() != null)
											costBuilder.setForceMajeureType(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										else
											costBuilder.setForceMajeureType(Optional.of(-1.00));
									}
									else if (containsIgnoreCase(costName, "PORCENTAJE_NEXTR_E")) {
										if (cd.getExpression() != null)
											costBuilder.setNoStructType(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
										else
											costBuilder.setNoStructType(Optional.of(-1.00));
									}			
									
									System.out.println(costName);
								}
							});
				}

				// SETTING BASES
				{
					costBuilder.setCommonContBase(Optional.ofNullable(salary.getCommonContingenciesBase()));
					costBuilder.setProfessionalContBase(Optional.ofNullable(salary.getProfessionalContingenciesBase()));
					costBuilder.setIrpfRetribDiner(Optional.ofNullable(salary.getIrpfBase()));
					costBuilder.setIrpfEsp(Optional.ofNullable(salary.getInkindIrpfBase()));
					costBuilder.setTotal(Optional.ofNullable(salary.getTotalEnterprise()));
					costBuilder.setNoStructBase(Optional.of(nonStructBase[0]));
					costBuilder.setForceMajeureBase(Optional.of(forceMajeureBase[0]));
				}
				payrollBuilder.setContingencies(costBuilder.build());
			}
			payrollBuilder.setPayrollTotal(salary.getTotalLiquid());

			return payrollBuilder.build();
		}).collect(Collectors.toList());

		// LOGO
		Optional<InputStream> optLogo = Optional.empty();
		{

			Attach attach1 = AON.getAttach(
				aonContext.getDomainName(),
				aonContext.getDomainId(),
				aonContext.getUser(),
				f -> f.getTypeProperty().eq(SIGNATURE.value()
			)
			.and(f.getDomainProperty()
			.eq(aonContext.getDomainId())),REGISTRY);
			
			if (attach1 == null || attach1.getData() == null)
				attach1 = AON.getAttach(
							aonContext.getDomainName(), 
							aonContext.getDomainId(), 
							aonContext.getUser(),
							f -> f.getTypeProperty().eq(LOGO.value()).and(f.getDomainProperty().eq(aonContext.getDomainId())),
							REGISTRY
						);

			if (attach1 != null && attach1.getData() != null)
				optLogo = Optional.ofNullable(new ByteArrayInputStream(attach1.getData()));
		}
		// PRINT
		try {
			PayrollTemplate.print(outputStream, payrolls, optLogo, Optional.ofNullable(new Locale("es")));
		} catch (CanNotCreatePdfException | IOException ignored) {}
	}

	/**
	 * Returns if amount and quote are empty
	 * @param payment - The payment
	 * @return true | false
	 */
	private static boolean filter(Payment payment) {
		return !(payment.getAmount() == 0 && payment.getQuote() == 0);
	}

	/**
	 * Get safe value
	 * @param value - The value.
	 * @return value | 0
	 */
	private static Double safeValue(Double value) {
		return value != null ? value : 0d;
	}
	
	/**
	 * Get safe value
	 * @param value - The value.
	 * @return value | ""
	 */
	private static String safeValue(String value) {
		return value != null ? value : "";
	}
	
}
