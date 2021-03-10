package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;

import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.DefaultPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.Contingency_bases.Contingency_bases_builder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollAccrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollDeduction;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.PayrollTypes;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Payment;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.common.util.concurrent.AtomicDouble;
/**
 * Class containing method/s to print payrolls from database data
 */
public class JooqPayrollBuilder {
	/**
	 * Method to generate a PDF payroll from database data and place it on the OutputStream passed as parameter
	 * @param domain The database domain name
	 * @param outputStream The output stream which the PDF will be written on
	 * @param salaryIds The IDs of the salaries in database
	 */
	public static void generatePayroll(String domainName, String user, OutputStream outputStream, Integer enterpriseId, Integer... salaryIds) {
		DefaultPayrollTemplate dpt = new DefaultPayrollTemplate();
		DefaultPayrollBuilder dpb = new DefaultPayrollBuilder();
		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {
			fillPayroll(outputStream, dpt, dpb, aonContext, salaryIds);
		}
	}
	
	public static void generatePayroll(String domainName, OutputStream outputStream, Integer... salaryIds) {
		generatePayroll(domainName, "", outputStream, null, salaryIds);
	}

	private static void fillPayroll(OutputStream outputStream, DefaultPayrollTemplate dpt, DefaultPayrollBuilder dpb,
			AONContext aonContext, Integer[] salaryIds) {
		//PICK UP THE SALARIES
		Stream<Salary> salaries = AON.getSalaries(aonContext,
				p -> p.getIdProperty().in(salaryIds));

		Collection<DefaultPayroll> payrolls = salaries.map(s -> {
			System.out.println("\t"+s.getId());
			
			//PAYROLL RELATED DATA
			{
				dpb.setLiquid_period_start(s.getStartDate());
				dpb.setLiquid_period_end(s.getEndDate());
				dpb.setTotal_days(s.getSalaryDays());
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
				if (add != null) {
					List<String> address = null;
					try {
						address = PDFToolkit.divide_string_to_fit(s.getEnterpriseAddress(), 170, PdfFonts.HELVETICA, 9f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if (address != null && address.size() > 1) {
						dpb.setAddress(address.get(0) != null ? address.get(0).trim() : null);
						dpb.setAddress_2(address.get(1));
					} else {
						dpb.setAddress(s.getEnterpriseAddress());
					}
				}
				
			}
			//EMPLOYEE RELATED DATA
			{
				dpb.setAntiquity(s.getEmployeeSeniorityDate());
				dpb.setEmployee(s.getEmployeeName());
				dpb.setNif(s.getEmployeeDocument());
				dpb.setNss(s.getEmployeeSSNumber());
				dpb.setProfessional_group(s.getEmployeeCategory());
				dpb.setQuotation_group(s.getEmployeeQuoteGroup());
			}
			//PAYMENTS
			{
				dpb.setAccrual_total(s.getTotalPayment());
				HashMap<Integer, ArrayList<DefaultPayrollAccrual>> paymentMap = new HashMap<Integer, ArrayList<DefaultPayrollAccrual>>();
				s.getPayments().stream().filter(JooqPayrollBuilder::filter).sorted(Comparator.comparing(p -> {
					return !(p.getDescription() == null || p.getDescription().isEmpty()) ? p.getDescription() : "zzzzzz"; //Nulls or empties down
				})).forEach(p -> {
					DefaultPayrollAccrual accrual = new DefaultPayrollAccrual(p.getAmount(), p.getDescription().replaceAll("\\[\\d*\\]", ""));
					if (!paymentMap.containsKey(p.getPaymentType().ordinal()))
						paymentMap.put(p.getPaymentType().ordinal(), new ArrayList<DefaultPayrollAccrual>());
					 
					if (paymentMap.get(p.getPaymentType().ordinal()).stream().anyMatch(acc -> AonStringUtils.equalsIgnoreCase(p.getDescription(), acc.getDescription().get()))) {
						DefaultPayrollAccrual repAcc = paymentMap.get(p.getPaymentType().ordinal()).stream().findFirst().get();
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
				dpb.setDeduction_total(s.getTotalDeduction());
				
				ArrayList<String> inserted = new ArrayList<String>();
				
				HashMap<Integer, ArrayList<DefaultPayrollDeduction>> deductionMap = new HashMap<Integer, ArrayList<DefaultPayrollDeduction>>();
				s.getDeductions().stream().sorted(Comparator.comparing(d -> {
					return !(d.getDescription() == null || d.getDescription().isEmpty()) ? d.getDescription() : chooseDescription(d.getDeductionType());
				})).forEach(d -> {
					DeductionType dt = d.getDeductionType();
					List<ContextData> percList = data.get("PORCENTAJE_" + getDeductionType(dt.ordinal()));
					ContextData cd = percList != null ? percList.get(0) : null;
					Double percent = null;
					if (cd != null) {
						if (cd.getExpression() != null)
							percent = Double.parseDouble(cd.getExpression());
						else
							percent = -1d;
					}
					
					int type = chooseType(dt);
					
					String desc = d.getDescription();
					
					if(desc == null || desc.isEmpty()) {
						desc = chooseDescription(dt);
					}
						
					
					DefaultPayrollDeduction dpd = new DefaultPayrollDeduction(d.getAmount(), desc, percent);
					if (!deductionMap.containsKey(type))
						deductionMap.put(type, new ArrayList<DefaultPayrollDeduction>());
					
					if (deductionMap.get(type).stream().anyMatch(ded -> AonStringUtils.equalsIgnoreCase(ded.getDescription().get(), dpd.getDescription().get()))) {
						DefaultPayrollDeduction ded = deductionMap.get(type).stream().filter(d1 -> AonStringUtils.equalsIgnoreCase(d1.getDescription().get(), dpd.getDescription().get())).findFirst().get();
						ded.setAmount(ded.getAmount().get()+dpd.getAmount().get());
					} else
						deductionMap.get(type).add(dpd);
					inserted.add(getDeductionType(dt.ordinal()));
				});
				
				if (deductionMap.get(1)==null)
					deductionMap.put(1, new ArrayList<DefaultPayrollDeduction>());
				if (deductionMap.get(2)==null)
					deductionMap.put(2, new ArrayList<DefaultPayrollDeduction>());
				
				if (!inserted.contains("CGC"))
					deductionMap.get(1).add(new DefaultPayrollDeduction(0d, "Contingencias comunes", 0d));
				if (!inserted.contains("DESMPL"))
					deductionMap.get(1).add(new DefaultPayrollDeduction(0d, "Desempleo", 0d));
				if (!inserted.contains("FP"))
					deductionMap.get(1).add(new DefaultPayrollDeduction(0d, "Formación profesional", 0d));
				if (!inserted.contains("IRPF"))
					deductionMap.get(2).add(new DefaultPayrollDeduction(0d, "Retribuciones dinerarias", 0d));
				dpb.setDeductions(deductionMap);
				
			}
			//COSTS
			Double totalEnterprise = 0d;
			{
				Contingency_bases_builder cbb = new Contingency_bases_builder();
				//INITIALIZING CONTINGENCIES, JUST IN CASE
				{
					cbb.setAt_ep_ap_enterprise(Optional.empty());
					cbb.setAt_ep_type(Optional.empty());
					cbb.setCommon_cont_ap_enterprise(Optional.empty());
					cbb.setCommon_cont_base(Optional.empty());
					cbb.setCommon_cont_type(Optional.empty());
					cbb.setExtra_proration_amount(Optional.empty());
					cbb.setFogasa_ap_enterprise(Optional.empty());
					cbb.setFogasa_type(Optional.empty());
					cbb.setForce_majeure_ap_enterprise(Optional.empty());
					cbb.setForce_majeure_base(Optional.empty());
					cbb.setForce_majeure_type(Optional.empty());
					cbb.setIrpf_esp(Optional.empty());
					cbb.setIrpf_retrib_diner(Optional.empty());
					cbb.setMonthly_amount(Optional.empty());
					cbb.setNo_struct_ap_enterprise(Optional.empty());
					cbb.setNo_struct_base(Optional.empty());
					cbb.setNo_struct_type(Optional.empty());
					cbb.setProfes_form_ap_enterprise(Optional.empty());
					cbb.setProfes_form_type(Optional.empty());
					cbb.setProfessional_cont_base(Optional.empty());
					cbb.setTotal(Optional.empty());
					cbb.setUnemployment_ap_enterprise(Optional.empty());
					cbb.setUnemployment_type(Optional.empty());
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
					
					cbb.setCommon_cont_ap_enterprise(Optional.ofNullable(common_cont_ap_enterprise));
					cbb.setAt_ep_ap_enterprise(Optional.ofNullable(at_ep_ap_enterprise));
					cbb.setUnemployment_ap_enterprise(Optional.ofNullable(unemployment_ap_enterprise));
					cbb.setProfes_form_ap_enterprise(Optional.ofNullable(profes_form_ap_enterprise));
					cbb.setFogasa_ap_enterprise(Optional.ofNullable(fogasa_ap_enterprise));
					cbb.setForce_majeure_ap_enterprise(Optional.ofNullable(force_majeure_ap_enterprise));
					cbb.setNo_struct_ap_enterprise(Optional.ofNullable(no_struct_ap_enterprise));
					
					cbb.setMonthly_amount(Optional.ofNullable(s.getRemuneration()));
				}
				
				//SETTING PERCENTAGES
				{
					data.keySet().stream().filter(k -> AonStringUtils.containsIgnoreCase(k, "PORCENTAJE")).forEach(k -> {
						ContextData cd = data.get(k).get(0);
						if (data.get(k) != null) {
							if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_CGC")) {
								if (cd.getExpression() != null)
									cbb.setCommon_cont_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setCommon_cont_type(Optional.of(-1.00));
							} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_CGP")
									|| AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_IT")) {
								if (cd.getExpression() != null)
									cbb.setAt_ep_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setAt_ep_type(Optional.of(-1.00));
							} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_DESMPL")) {
								if (cd.getExpression() != null)
									cbb.setUnemployment_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setUnemployment_type(Optional.of(-1.00));
							} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_FP")) {
								if (cd.getExpression() != null)
									cbb.setProfes_form_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setProfes_form_type(Optional.of(-1.00));
							} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_FOGASA")) {
								if (cd.getExpression() != null)
									cbb.setFogasa_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
								else
									cbb.setFogasa_type(Optional.of(-1.00));
							}
						}
					});
				}
				//SETTING BASES
				{
					cbb.setCommon_cont_base(Optional.ofNullable(s.getCommonContingenciesBase()));
					cbb.setProfessional_cont_base(Optional.ofNullable(s.getProfessionalContingenciesBase()));
					cbb.setIrpf_retrib_diner(Optional.ofNullable(s.getIrpfBase()));
					cbb.setIrpf_esp(Optional.ofNullable(s.getInkindIrpfBase()));
					cbb.setTotal(Optional.ofNullable(s.getTotalEnterprise()));
				}
				dpb.setContingencies(cbb.build());
			}
			dpb.setPayroll_total(s.getTotalLiquid());
			
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
	
	/**
	 * 
	 * @param type Ordinal of the DeductionType
	 * @return a string containing the key name of the deduction type
	 */
	private static String getDeductionType (Integer type) {
		switch (type) {
			case 0:
				return "CGC";
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
			default:
				return null;
		}
	}
	
	private static int chooseType (DeductionType dt) {
		switch (dt.ordinal()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return 1;
		case 6:
			return 2;
		case 7:
			return  3;
		case 8:
			return 4;
		default:
			return 5;
		}
	}
	
	private static String chooseDescription (DeductionType dt) {
		switch (dt.ordinal()) {
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
		default:
			return "Otras deducciones";
		}
	}
	
	private static boolean filter (Payment payment) {
		return !(payment.getAmount() == 0 && payment.getQuote() == 0);
	}
	
	private static <T extends Enum<?>> T typeOf(Byte ordinal, Class<T> type) {
	if ( ordinal == null )
		return null;
	try {
		return type.getEnumConstants()[ordinal];
	} catch ( Exception e ) {
		return null;
	}
}
	
}
