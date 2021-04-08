package com.esferalia.aon.gwt.payroll.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.Accrual;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.Deduction;
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
import com.esferalia.aon.watson.util.AonStringUtils;
/**
 * Class containing method/s to print payrolls from database data
 */
public class JooqPayrollBuilder {
	/**
	 * Method to generate a PDF payroll from database data and place it on the OutputStream passed as parameter
	 * @param enterpriseId The id of the enterprise
	 * @param domainName The database domain name
	 * @param user The user (may be empty)
	 * @param outputStream The output stream which the PDF will be written on
	 * @param salaryIds The IDs of the salaries in database
	 */
	public static void generatePayroll(Integer enterpriseId, String domainName, String user, OutputStream outputStream, Integer... salaryIds) {
		PayrollTemplate dpt = new PayrollTemplate();
		DefaultPayrollBuilder dpb = new DefaultPayrollBuilder();
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			fillPayroll(outputStream, dpt, dpb, aonContext, salaryIds);
		}
	}
	/**
	 * Method to generate a PDF payroll from database data and place it on the OutputStream passed as parameter
	 * @param enterpriseId The id of the enterprise
	 * @param domainName The database domain name
	 * @param outputStream The output stream which the PDF will be written on
	 * @param salaryIds The IDs of the salaries in database
	 */
	public static void generatePayroll(Integer enterpriseId, String domainName, OutputStream outputStream, Integer... salaryIds) {
		generatePayroll(enterpriseId, domainName, "", outputStream, salaryIds);
	}
	/**
	 * Method to generate a PDF payroll from database data and place it on the OutputStream passed as parameter
	 * @param domainName The database domain name
	 * @param outputStream The output stream which the PDF will be written on
	 * @param salaryIds The IDs of the salaries in database
	 */
	public static void generatePayroll(String domainName, OutputStream outputStream, Integer... salaryIds) {
		generatePayroll(null, domainName, "", outputStream, salaryIds);
	}

	private static void fillPayroll(OutputStream outputStream, PayrollTemplate dpt, DefaultPayrollBuilder dpb,
			AONContext aonContext, Integer[] salaryIds) {
		//PICK UP THE SALARIES
		Stream<Salary> salaries = AON.getSalaries(aonContext,
				p -> p.getIdProperty().in(salaryIds));

		Collection<DefaultPayroll> payrolls = salaries.map(s -> {
			
			//PAYROLL RELATED DATA
			{
				dpb.setLiquidPeriodStart(s.getStartDate());
				dpb.setLiquidPeriodEnd(s.getEndDate());
				dpb.setTotalDays(s.getSalaryDays());
				if (s.getSalaryType().ordinal() == SalaryType.SALARY.ordinal())
					dpb.setPayrollType(PayrollTypes.Type.SALARY);
				else if (s.getSalaryType().ordinal() == SalaryType.EXTRA.ordinal())
					dpb.setPayrollType(PayrollTypes.Type.EXTRAS);
				else if (s.getSalaryType().ordinal() == SalaryType.SETTLE.ordinal())
					dpb.setPayrollType(PayrollTypes.Type.SETTLEMENT);
				else if (s.getSalaryType().ordinal() == SalaryType.DELAY.ordinal())
					dpb.setPayrollType(PayrollTypes.Type.ARREARS_WAGE);
				else
					dpb.setPayrollType(PayrollTypes.Type.SALARY);
			}
			//ENTERPRISE RELATED DATA
			{
				dpb.setCcc(s.getEnterpriseCCC());
				dpb.setCif(s.getEnterpriseDocument());
				dpb.setEnterprise(s.getEnterpriseName());
				//ADDRESS FITTING
				
				Registry registry = AON.getRegistry(aonContext.getDomainName()
						, aonContext.getDomainId()
						, ""
						, p -> p.getDocumentProperty().eq(s.getEnterpriseDocument()).and(p.getDomainProperty().eq(aonContext.getDomainId())));
				
				
				RAddress raddress = AON.getRAddress(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(), p -> p.getRegistryProperty().eq(registry.getId()).and(p.getDomainProperty().eq(aonContext.getDomainId())));
				String add = raddress.getFullAddress() != null ? raddress.getFullAddress() : s.getEnterpriseAddress();
				
				String streetType = raddress.getStreet_type() != null ? raddress.getStreet_type() : "";
				String address1 = raddress.getAddress() != null ? raddress.getAddress() : "";
				String number = raddress.getNumber() != null ? raddress.getNumber() : "";
				String address2 = raddress.getAddress2() != null && !raddress.getAddress2().isEmpty() ? ", " + raddress.getAddress2() : "";
				String address3 = raddress.getAddress3() != null ? raddress.getAddress3() : "";
				
				String zip = raddress.getZip() != null ? raddress.getZip() : "";
				String city = raddress.getCity() != null ? raddress.getCity() : "";
				
				String firstLine = streetType + " " + address1 + " " + number + " " + address2 + address3;
				
				if (firstLine.length() > 45)
					firstLine = streetType + " " + address1 + " " + number + " " + address3;
				
				String sekandoRain = zip + " " + city;
				
				
				if (add != null) {
					
					if (firstLine.length() < 45 && sekandoRain.length() < 45) {
						dpb.setAddress(firstLine);
						dpb.setAddress2(sekandoRain);
					} else {
						String[] address = null;
						//address = get_lines(add, 170, PdfFonts.HELVETICA, 9f);
						address = Utilities.separateString(add, 40);
						if (address != null && address.length > 1) {
							dpb.setAddress(address[0] != null ? address[0].trim() : null);
							String secline = "";
							for (int i = 1; i<address.length; i++) {
								secline += address[i];
							}
							if (secline.length() > 46)
								secline = secline.substring(0, 45).concat("...");
							dpb.setAddress2(secline);
						} else {
							dpb.setAddress(s.getEnterpriseAddress());
						}
					}
					
					
				}
				
			}
			//EMPLOYEE RELATED DATA
			{
				dpb.setAntiquity(s.getEmployeeSeniorityDate());		
				//weird names check
				String employeeName = s.getEmployeeName().trim();
				if (employeeName != null && employeeName.length() > 1 && employeeName.charAt(0) == ',')
					employeeName = employeeName.substring(1).trim();	
				dpb.setEmployee(employeeName);
				dpb.setNif(s.getEmployeeDocument());
				dpb.setNss(s.getEmployeeSSNumber());
				dpb.setProfessionalGroup(s.getEmployeeCategory());
				dpb.setQuotationGroup(s.getEmployeeQuoteGroup());
			}
			//PAYMENTS
			{
				dpb.setAccrualTotal(s.getTotalPayment());
				HashMap<Integer, ArrayList<Accrual>> paymentMap = new HashMap<Integer, ArrayList<Accrual>>();
				s.getPayments().stream().filter(JooqPayrollBuilder::filter).sorted(Comparator.comparing(p -> {
					return !(p.getDescription() == null || p.getDescription().isEmpty()) ? p.getDescription() : "zzzzzz"; //Nulls or empties down
				})).forEach(p -> {
					
					String description = p.getDescription().replaceAll("\\[\\d*\\]", "");
					if (description.length() > 50) {
						try {
							description = PDFToolkit.croppedString(description, 260, PdfFonts.HELVETICA, 9f);
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}
					
					Accrual accrual = new Accrual(p.getAmount(), description);
					if (!paymentMap.containsKey(p.getPaymentType().ordinal()))
						paymentMap.put(p.getPaymentType().ordinal(), new ArrayList<Accrual>());
					 
					if (paymentMap.get(p.getPaymentType().ordinal()).stream().anyMatch(acc -> AonStringUtils.equalsIgnoreCase(p.getDescription(), acc.getDescription().get()))) {
						Accrual repAcc = paymentMap.get(p.getPaymentType().ordinal()).stream().findFirst().get();
						repAcc.setAmount(repAcc.getAmount().orElse(0d)+p.getAmount());
					} else
						paymentMap.get(p.getPaymentType().ordinal()).add(accrual);
				});
				dpb.setAccruals(paymentMap);
			}
			//COLLECTING DATA (FOR PERCENTAGES)
			Map<String, List<ContextData>> data = s.getContextData();
			//DEDUCTIONS
			{
				dpb.setDeductionTotal(s.getTotalDeduction());
				
				ArrayList<String> inserted = new ArrayList<String>();
				
				HashMap<Integer, ArrayList<Deduction>> deductionMap = new HashMap<Integer, ArrayList<Deduction>>();
				s.getDeductions().stream().sorted(Comparator.comparing(d -> {
					return !(d.getDescription() == null || d.getDescription().isEmpty()) ? d.getDescription() : Utilities.chooseDescription(d.getDeductionType());
				})).forEach(d -> {
					DeductionType dt = d.getDeductionType();
					List<ContextData> percList = data.get("PORCENTAJE_" + Utilities.getDeductionType(dt.ordinal()));
					ContextData cd = percList != null ? percList.get(0) : null;
					Double percent = null;
					if (cd != null) {
						if (cd.getExpression() != null)
							percent = Double.parseDouble(cd.getExpression());
						else
							percent = -1d;
					}
					
					
					int type = Utilities.chooseType(dt);
					
					String desc = d.getDescription();
					
					if(desc == null || desc.isEmpty()) {
						desc = Utilities.chooseDescription(dt);
					}
						
					
					Deduction dpd = new Deduction(d.getAmount(), desc, percent);
					if (!deductionMap.containsKey(type))
						deductionMap.put(type, new ArrayList<Deduction>());
					
					if (deductionMap.get(type).stream().anyMatch(ded -> AonStringUtils.equalsIgnoreCase(ded.getDescription().get(), dpd.getDescription().get()))) {
						Deduction ded = deductionMap.get(type).stream().filter(d1 -> AonStringUtils.equalsIgnoreCase(d1.getDescription().get(), dpd.getDescription().get())).findFirst().get();
						ded.setAmount(ded.getAmount().get()+dpd.getAmount().get());
					} else
						deductionMap.get(type).add(dpd);
					inserted.add(Utilities.getDeductionType(dt.ordinal()));
				});
				
				if (deductionMap.get(1)==null)
					deductionMap.put(1, new ArrayList<Deduction>());
				if (deductionMap.get(2)==null)
					deductionMap.put(2, new ArrayList<Deduction>());
				
				if (!inserted.contains("CGC"))
					deductionMap.get(1).add(new Deduction(0d, "Contingencias comunes", 0d));
				if (!inserted.contains("DESMPL"))
					deductionMap.get(1).add(new Deduction(0d, "Desempleo", 0d));
				if (!inserted.contains("FP"))
					deductionMap.get(1).add(new Deduction(0d, "Formación profesional", 0d));
				if (!inserted.contains("IRPF"))
					deductionMap.get(2).add(new Deduction(0d, "Retribuciones dinerarias", 0d));
				
				
				//EMBARGOS (placed at 'Other deductions' -type 5- field on 'Deductions')
				{
					List<Embargo> embargos = s.getEmbargos();
					embargos.forEach(e -> {
						Deduction emb = new Deduction(e.getAmount(), e.getDescription(), null);
						if (deductionMap.containsKey(5))
							deductionMap.get(5).add(emb);
						else {
							ArrayList<Deduction> deducts = new ArrayList<Deduction>();
							deducts.add(emb);
							deductionMap.put(5, deducts);
						}
					});
				}
				
				
				dpb.setDeductions(deductionMap);
				
			}
			//COSTS
			Double totalEnterprise = 0d;
			{
				ContingencyBasesBuilder cbb = new ContingencyBasesBuilder();
				//INITIALIZING CONTINGENCIES, JUST IN CASE
				{
					cbb.setAtEpApEnterprise(Optional.empty());
					cbb.setAtEpType(Optional.empty());
					cbb.setCommonContApEnterprise(Optional.empty());
					cbb.setCommonContBase(Optional.empty());
					cbb.setCommonContType(Optional.empty());
					cbb.setExtraProrationAmount(Optional.empty());
					cbb.setFogasaApEnterprise(Optional.empty());
					cbb.setFogasaType(Optional.empty());
					cbb.setForceMajeureApEnterprise(Optional.empty());
					cbb.setForceMajeureBase(Optional.empty());
					cbb.setForceMajeureType(Optional.empty());
					cbb.setIrpfEsp(Optional.empty());
					cbb.setIrpfRetribDiner(Optional.empty());
					cbb.setMonthlyAmount(Optional.empty());
					cbb.setNoStructApEnterprise(Optional.empty());
					cbb.setNoStructBase(Optional.empty());
					cbb.setNoStructType(Optional.empty());
					cbb.setProfesFormApEnterprise(Optional.empty());
					cbb.setProfesFormType(Optional.empty());
					cbb.setProfessionalContBase(Optional.empty());
					cbb.setTotal(Optional.empty());
					cbb.setUnemploymentApEnterprise(Optional.empty());
					cbb.setUnemploymentType(Optional.empty());
				}
				//SETTING AMOUNTS
				{
					Double common_cont_ap_enterprise = 0d;
					Double at_ep_ap_enterprise = 0d;
					Double unemployment_ap_enterprise = 0d;
					Double profes_form_ap_enterprise = 0d;
					Double fogasa_ap_enterprise = 0d;
					Double force_majeure_ap_enterprise = 0d;
					Double no_struct_ap_enterprise = 0d;
					
					for (Cost c : s.getCosts()) {
						
						totalEnterprise += (c.getAmount() != null ? c.getAmount() : 0d);
						
						if (c.getCostType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
							common_cont_ap_enterprise += (c.getAmount() != null ? c.getAmount() : 0d);
						else if (c.getCostType().ordinal() == DeductionType.IT.ordinal()
								|| c.getCostType().ordinal() == DeductionType.IMS.ordinal())
							at_ep_ap_enterprise += (c.getAmount() != null ? c.getAmount() : 0d);
						else if (c.getCostType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
							unemployment_ap_enterprise += (c.getAmount() != null ? c.getAmount() : 0d);
						else if (c.getCostType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
							profes_form_ap_enterprise += (c.getAmount() != null ? c.getAmount() : 0d);
						else if (c.getCostType().ordinal() == DeductionType.FOGASA.ordinal())
							fogasa_ap_enterprise += (c.getAmount() != null ? c.getAmount() : 0d);
						else if (c.getCostType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal())
							force_majeure_ap_enterprise += (c.getAmount() != null ? c.getAmount() : 0d);
						else if (c.getCostType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal())
							no_struct_ap_enterprise += (c.getAmount() != null ? c.getAmount() : 0d);
						
					}
					
					cbb.setCommonContApEnterprise(Optional.ofNullable(common_cont_ap_enterprise));
					cbb.setAtEpApEnterprise(Optional.ofNullable(at_ep_ap_enterprise));
					cbb.setUnemploymentApEnterprise(Optional.ofNullable(unemployment_ap_enterprise));
					cbb.setProfesFormApEnterprise(Optional.ofNullable(profes_form_ap_enterprise));
					cbb.setFogasaApEnterprise(Optional.ofNullable(fogasa_ap_enterprise));
					cbb.setForceMajeureApEnterprise(Optional.ofNullable(force_majeure_ap_enterprise));
					cbb.setNoStructApEnterprise(Optional.ofNullable(no_struct_ap_enterprise));
					
					//REMUNERATION AND PRO. EXT. BASE
					{						
						cbb.setExtraProrationAmount(Optional.ofNullable(s.getExtraProrationBase()));
						cbb.setMonthlyAmount(Optional.ofNullable(s.getRemuneration()));
					}
				}
				
				//SETTING PERCENTAGES
				{
					data.keySet().stream().filter(k -> AonStringUtils.containsIgnoreCase(k, "PORCENTAJE")).forEach(k -> {
						ContextData cd = data.get(k).get(0);
						if (data.get(k) != null) {
							if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_CGC")) {
								if (cd.getExpression() != null)
									cbb.setCommonContType(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setCommonContType(Optional.of(-1.00));
							} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_CGP")
									|| AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_IT")) {
								if (cd.getExpression() != null)
									cbb.setAtEpType(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setAtEpType(Optional.of(-1.00));
							} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_DESMPL")) {
								if (cd.getExpression() != null)
									cbb.setUnemploymentType(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setUnemploymentType(Optional.of(-1.00));
							} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_FP")) {
								if (cd.getExpression() != null)
									cbb.setProfesFormType(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setProfesFormType(Optional.of(-1.00));
							} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_FOGASA")) {
								if (cd.getExpression() != null)
									cbb.setFogasaType(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setFogasaType(Optional.of(-1.00));
							}
						}
					});
				}
				//SETTING BASES
				{
					
					cbb.setCommonContBase(Optional.ofNullable(s.getCommonContingenciesBase()));
					cbb.setProfessionalContBase(Optional.ofNullable(s.getProfessionalContingenciesBase()));
					cbb.setIrpfRetribDiner(Optional.ofNullable(s.getIrpfBase()));
					cbb.setIrpfEsp(Optional.ofNullable(s.getInkindIrpfBase()));
					cbb.setTotal(Optional.ofNullable(s.getTotalEnterprise()));
				}
				dpb.setContingencies(cbb.build());
			}
			dpb.setPayrollTotal(s.getTotalLiquid());
			
			return dpb.build();
		}).collect(Collectors.toList());
				
		
		//LOGO
		Optional<InputStream> optLogo = Optional.empty();
		{
			
			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.SIGNATURE.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
					AttachType.REGISTRY);
			if (attach1 == null || attach1.getData() == null)
				attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
					AttachType.REGISTRY);
			
			if (attach1 != null && attach1.getData() != null)
				optLogo = Optional.ofNullable(new ByteArrayInputStream(attach1.getData()));
		}
		//PRINT
		try {
			dpt.print(outputStream, payrolls, optLogo, Optional.ofNullable(new Locale("es")));
		} catch (CanNotCreatePdfException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

	
	private static boolean filter (Payment payment) {
		return !(payment.getAmount() == 0 && payment.getQuote() == 0);
	}
	
}
