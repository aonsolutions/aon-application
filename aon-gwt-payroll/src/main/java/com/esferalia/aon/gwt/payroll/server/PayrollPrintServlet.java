package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Condition;
import org.jooq.DSLContext;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.util.JooqPayrollBuilder;
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
import com.esferalia.aon.jooq.tables.Salary;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.client.util.Base64;

//http://ayudat.aonsolutions.net:8080/aon-aio/aon_gwt_payroll//print_payroll/

//@SuppressWarnings("serial")
//@WebServlet(
//		name = "PDF-Payroll", 
//		urlPatterns = { 
//				"/aon_gwt_aio/print_payroll/*" ,
//				"/aon_gwt_payroll/print_payroll/*" 
//		}
//)
//@SuppressWarnings("serial")
//@WebServlet(name = "Salary-PDF", 
//	urlPatterns = { 
//			"/aon_gwt_aio/salary_exporter/*",
//			"/aon_gwt_payroll/salary_exporter/*" 
//	})
public class PayrollPrintServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		DefaultPayrollTemplate dpt = new DefaultPayrollTemplate();
		DefaultPayrollBuilder dpb = new DefaultPayrollBuilder();
		
		
		
		String requestURI = req.getRequestURI();
		String salaryRequestStr = AonServletUtils.getFileName(requestURI);
		String paramsStr = decode(salaryRequestStr.getBytes());
		Map<String, String> params = createParams(paramsStr);
		String fileName = params.get("name");
		
		Integer selectedSalaries = Integer.parseInt(params.get("selectedSalaries"));
//		Condition condition = getConditionSalaryIds(params, selectedSalaries);
		resp.setContentType(MimeType.MIME_PDF.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\";");
		

			JooqPayrollBuilder.generatePayroll(req.getServerName(), resp.getOutputStream(), getSalaryIds(params, selectedSalaries).toArray(new Integer[selectedSalaries]));
//			DSLContext ctx = aonContext.getDslContext();
//			
//			Stream<com.esferalia.aon.occam.api.model.Salary> salaries = AON.getSalaries(aonContext, p -> p.getIdProperty().in(getSalaryIds(params, selectedSalaries).toArray(new Integer[getSalaryIds(params, selectedSalaries).size()])));
//			
//			Collection<DefaultPayroll> payrolls = salaries.map(s -> {
//				
//				SalaryRecord others = ctx.select().from(SALARY).where(SALARY.ID.eq(s.getId())).fetchOneInto(SALARY);
//				
//				switch (others.getType().intValue()) {
//				case 0:
//					dpb.setPayrollType(PayrollTypes.Type.SALARY);
//					break;
//				case 1:
//					dpb.setPayrollType(PayrollTypes.Type.EXTRAS);
//					break;
//				case 2:
//					dpb.setPayrollType(PayrollTypes.Type.SETTLEMENT);
//					break;
//				case 3:
//					dpb.setPayrollType(PayrollTypes.Type.ARREARS_WAGE);
//					break;
//				default:
//					dpb.setPayrollType(PayrollTypes.Type.SALARY);
//			}
//				
//				dpb.setAccrual_total(s.getTotalPayment());
//				
//				
//				HashMap<Integer, ArrayList<DefaultPayrollAccrual>> paymentMap = new HashMap<Integer, ArrayList<DefaultPayrollAccrual>>();
//				
//				s.getPayments().stream().forEach(p -> {
//					DefaultPayrollAccrual accrual = new DefaultPayrollAccrual(p.getAmount(),
//					p.getDescription());
//					if (paymentMap.containsKey(p.getPaymentType().ordinal())) {
//						paymentMap.get(p.getPaymentType().ordinal()).add(accrual);
//					} else {
//						ArrayList<DefaultPayrollAccrual> accrualList = new ArrayList<DefaultPayrollAccrual>();
//						accrualList.add(accrual);
//						paymentMap.put(p.getPaymentType().ordinal(), accrualList);
//					}
//				});
//				
//				
//				
//				dpb.setAccruals(paymentMap);
////				dpb.setAddress(others.getEnterpriseAddress());
//				
//				
//				List<String> address = null;				
//				try {
//					address = PDFToolkit.divide_string_to_fit(others.getEnterpriseAddress(), 200, PdfFonts.HELVETICA, 9f);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				
//				if (address != null && address.size()>1) {
//						dpb.setAddress(address.get(0));
//						dpb.setAddress_2(address.get(1));
//				} else {
//					dpb.setAddress(others.getEnterpriseAddress());
//				}
//				
//				
//				
//				
//				
//				
//				System.out.println("OTHER: "+others.getSeniorityDate()+", METHOD'S: "+s.getEmployeeSeniorityDate());
//				dpb.setAntiquity(others.getSeniorityDate());
//				dpb.setCcc(s.getEnterpriseCCC());
//				dpb.setCif(s.getEnterpriseDocument());
//				
//				
//				
//				
//				
//				Contingency_bases_builder cbb = new Contingency_bases_builder();
//				
//				cbb.setAt_ep_ap_enterprise(Optional.empty());
//				cbb.setAt_ep_type(Optional.empty());
//				cbb.setCommon_cont_ap_enterprise(Optional.empty());
//				cbb.setCommon_cont_base(Optional.empty());
//				cbb.setCommon_cont_type(Optional.empty());
//				cbb.setExtra_proration_amount(Optional.empty());
//				cbb.setFogasa_ap_enterprise(Optional.empty());
//				cbb.setFogasa_type(Optional.empty());
//				cbb.setForce_majeure_ap_enterprise(Optional.empty());
//				cbb.setForce_majeure_base(Optional.empty());
//				cbb.setForce_majeure_type(Optional.empty());
//				cbb.setIrpf_esp(Optional.empty());
//				cbb.setIrpf_retrib_diner(Optional.empty());
//				cbb.setMonthly_amount(Optional.empty());
//				cbb.setNo_struct_ap_enterprise(Optional.empty());
//				cbb.setNo_struct_base(Optional.empty());
//				cbb.setNo_struct_type(Optional.empty());
//				cbb.setProfes_form_ap_enterprise(Optional.empty());
//				cbb.setProfes_form_type(Optional.empty());
//				cbb.setProfessional_cont_base(Optional.empty());
//				cbb.setTotal(Optional.empty());
//				cbb.setUnemployment_ap_enterprise(Optional.empty());
//				cbb.setUnemployment_type(Optional.empty());
//				
//				s.getCosts().forEach(c -> {
//					if (c.getCostType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
//						cbb.setCommon_cont_ap_enterprise(Optional.ofNullable(c.getAmount()));
//					else if (c.getCostType().ordinal() == DeductionType.IT.ordinal())
//						cbb.setAt_ep_ap_enterprise(Optional.ofNullable(c.getAmount()));
//					else if (c.getCostType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
//						cbb.setUnemployment_ap_enterprise(Optional.ofNullable(c.getAmount()));
//					else if (c.getCostType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
//						cbb.setProfes_form_ap_enterprise(Optional.ofNullable(c.getAmount()));
//					else if (c.getCostType().ordinal() == DeductionType.FOGASA.ordinal())
//						cbb.setFogasa_ap_enterprise(Optional.ofNullable(c.getAmount()));
//				});
//				
//				Map<String, List<ContextData>> data = s.getContextData();
//				
//				data.keySet().stream().filter(k -> AonStringUtils.containsIgnoreCase(k, "PORCENTAJE")).forEach(k -> {
//					ContextData cd = data.get(k).get(0);
//					if (data.get(k) != null) {
//						if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_CGC")) {
//							if (cd.getExpression()!=null)
//								cbb.setCommon_cont_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
//							else
//								cbb.setCommon_cont_type(Optional.of(-1.00));	
//						} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_CGP") || AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_IT")) {
//							if (cd.getExpression() != null)
//								cbb.setAt_ep_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
//							else
//								cbb.setAt_ep_type(Optional.of(-1.00));
//						} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_DESMPL")) {
//							if (cd.getExpression() != null)
//								cbb.setUnemployment_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
//							else
//								cbb.setUnemployment_type(Optional.of(-1.00));
//						} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_FP")) {
//							if (cd.getExpression() != null)
//								cbb.setProfes_form_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
//							else
//								cbb.setProfes_form_type(Optional.of(-1.00));
//						} else if (AonStringUtils.containsIgnoreCase(k, "PORCENTAJE_FOGASA")) {
//							if (cd.getExpression() != null)
//								cbb.setFogasa_type(Optional.ofNullable(Double.parseDouble(cd.getExpression())));
//							else
//								cbb.setFogasa_type(Optional.of(-1.00));
//						}
//					}
//				});
//				
//				cbb.setMonthly_amount(Optional.ofNullable(others.getRemuneration()));
//				cbb.setCommon_cont_base(Optional.ofNullable(s.getCommonContingenciesBase()));
//				cbb.setProfessional_cont_base(Optional.ofNullable(s.getProfessionalContingenciesBase()));
//				cbb.setIrpf_retrib_diner(Optional.ofNullable(s.getIrpfBase()));
//				cbb.setIrpf_esp(Optional.ofNullable(s.getInkindIrpfBase()));
//				cbb.setTotal(Optional.ofNullable(s.getTotalEnterprise()));
//				
//				dpb.setContingencies(cbb.build());
//				
//				dpb.setDeduction_total(s.getTotalDeduction());
//				
//				HashMap<Integer, ArrayList<DefaultPayrollDeduction>> deductionMap = new HashMap<Integer, ArrayList<DefaultPayrollDeduction>>();
//				
//				s.getDeductions().forEach(d -> {
//					DeductionType dt = d.getDeductionType();
//					List<ContextData> percList = data.get("PORCENTAJE_"+getDeductionType(dt.ordinal()));
//					ContextData cd = percList!=null?percList.get(0):null;
//					Double percent = null;
//					if (cd != null) {
//						if (cd.getExpression() != null)
//							percent = Double.parseDouble(cd.getExpression());
//						else
//							percent = -1d;
//					}
//					DefaultPayrollDeduction dpd = new DefaultPayrollDeduction(d.getAmount(), d.getDescription(), percent);
//					if (deductionMap.containsKey(dt.ordinal()))
//						deductionMap.get(dt.ordinal()).add(dpd);
//					else {
//						ArrayList<DefaultPayrollDeduction> arr = new ArrayList<DefaultPayrollDeduction>();
//						arr.add(dpd);
//						deductionMap.put(dt.ordinal(), arr);
//					}
//					
//				});
//				
//				dpb.setDeductions(deductionMap);
//				dpb.setEmployee(s.getEmployeeName());
//				dpb.setEnterprise(s.getEnterpriseName());
//				dpb.setLiquid_period_start(s.getStartDate());
//				dpb.setLiquid_period_end(s.getEndDate());
//				dpb.setNif(s.getEmployeeDocument());
//				dpb.setNss(others.getSocialSecurityNumber());
//				dpb.setPayroll_total(s.getTotalLiquid());
//				dpb.setProfessional_group(others.getCategory());
//				dpb.setQuotation_group(s.getEmployeeQuoteGroup());
//				dpb.setTotal_days(s.getSalaryDays());
//				
//				
//				
//				
//				
//				
//				
//				
//				return dpb.build() ;	
//			}).collect(Collectors.toList());
//			
//			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
//			f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
//			.and(f.getDomainProperty().eq(aonContext.getDomainId())),
//			AttachType.REGISTRY);
//	
//	
//	Optional<InputStream> optLogo = Optional.ofNullable(new ByteArrayInputStream(attach1.getData()));
//	
//	try {
//		dpt.print(resp.getOutputStream(), payrolls, optLogo, Optional.ofNullable(new Locale("es")));
//	} catch (CanNotCreatePdfException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
			
			
			
//			System.out.println(ctx.select().from(SALARY).where(condition).getSQL());
//			
//			Stream<SalaryRecord> borutoStream = ctx.select().from(SALARY).where(condition).fetchStreamInto(SALARY);
//			
//			LinkedList<DefaultPayroll> payrolls = new LinkedList<DefaultPayroll>();
//			
//			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
//					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
//					.and(f.getDomainProperty().eq(aonContext.getDomainId())),
//					AttachType.REGISTRY);
//			
//			
//			Optional<InputStream> optLogo = Optional.ofNullable(new ByteArrayInputStream(attach1.getData()));
//			
//			borutoStream.forEach(r -> {
//				resp.setContentType(MimeType.MIME_PDF.getName());
//			resp.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\";");
//			
//				
//				switch (r.getType().intValue()) {
//					case 0:
//						dpb.setPayrollType(PayrollTypes.Type.SALARY);
//						break;
//					case 1:
//						dpb.setPayrollType(PayrollTypes.Type.EXTRAS);
//						break;
//					case 2:
//						dpb.setPayrollType(PayrollTypes.Type.SETTLEMENT);
//						break;
//					case 3:
//						dpb.setPayrollType(PayrollTypes.Type.ARREARS_WAGE);
//						break;
//					default:
//						dpb.setPayrollType(PayrollTypes.Type.SALARY);
//				}
//				
//				
//				
//				List<String> address = null;
//				
//				try {
//					address = PDFToolkit.divide_string_to_fit(r.getEnterpriseAddress(), 200, PdfFonts.HELVETICA, 9f);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				
//				System.out.println("WEA: "+address);
//				
//				dpb.setEnterprise(r.getEnterpriseName());
//				
//				if (address != null && address.size()>1) {
//						dpb.setAddress(address.get(0));
//						dpb.setAddress_2(address.get(1));
//				} else {
//					dpb.setAddress(r.getEnterpriseAddress());
//				}
//				
//				dpb.setCcc(r.getCcc());
//				dpb.setCif(r.getEnterpriseDocument());
//				dpb.setEmployee(r.getEmployeeName());
//				dpb.setNif(r.getEmployeeDocument());
//				dpb.setNss(r.getSocialSecurityNumber());
//				dpb.setProfessional_group(r.getCategory());
//				dpb.setQuotation_group(r.getQuoteGroup());
//				dpb.setAntiquity(r.getSeniorityDate());
//
//				dpb.setLiquid_period_start(r.getStartDate());
//				dpb.setLiquid_period_end(r.getEndDate());
//				dpb.setTotal_days(r.getTimeUnits());
//				
//				
//				
////				RattachRecord attach = ctx.select()
////						.from(RATTACH)
////						.innerJoin(REGISTRY).onKey()
////						.where(RATTACH.REGISTRY.eq(REGISTRY.ID))
////						.and(REGISTRY.DOCUMENT.eq(r.getEnterpriseDocument()))
////						.and(RATTACH.TYPE.eq((byte)0))
////						.fetchAnyInto(RATTACH);
//
//
//				Stream<SalaryPaymentRecord> paymentsStream = ctx.select().from(SALARY_PAYMENT)
//						.where(SALARY_PAYMENT.SALARY.eq(r.getId())).fetchStreamInto(SALARY_PAYMENT);
//
//				HashMap<Integer, ArrayList<DefaultPayrollAccrual>> paymentMap = new HashMap<Integer, ArrayList<DefaultPayrollAccrual>>();
//
//				paymentsStream.forEach(p -> {
//					DefaultPayrollAccrual accrual = new DefaultPayrollAccrual(p.getAmount(),
//							p.getDescription());
//					if (paymentMap.containsKey(p.getType().intValue())) {
//						paymentMap.get(p.getType().intValue()).add(accrual);
//					} else {
//						ArrayList<DefaultPayrollAccrual> accrualList = new ArrayList<DefaultPayrollAccrual>();
//						accrualList.add(accrual);
//						paymentMap.put(p.getType().intValue(), accrualList);
//					}
//				});
//
//				dpb.setAccruals(paymentMap);
//
//				dpb.setAccrual_total(r.getTotalPayment());
//				
//				Stream<SalaryDataRecord> percentages = ctx.select()
//						.from(SALARY_DATA)
//						.where(SALARY_DATA.NAME.like("%PORCENTAJE%"))
//						.and(SALARY_DATA.SALARY.eq(r.getId()))
//						.fetchStreamInto(SALARY_DATA);
//				
//				Map<String, String> map_percent = percentages.collect(HashMap::new, (m,v)->m.put(v.getName(), v.getExpression()), HashMap::putAll);
//				
//				System.out.println(map_percent);
//
//				Stream<SalaryDeductionRecord> deductionsStream = ctx.select()
//						.from(SALARY_DEDUCTION)
//						.leftJoin(SALARY_DATA).on(SALARY_DATA.NAME.eq("PORCENTAJE_"+SALARY_DEDUCTION.DEDUCTION_CONCEPT))
//						.where(SALARY_DEDUCTION.SALARY.eq(r.getId()))
//						.fetchStreamInto(SALARY_DEDUCTION);
//				
//				
//				System.out.println(ctx.select()
//				.from(SALARY_DEDUCTION)
//				.leftJoin(SALARY_DATA).on(SALARY_DATA.NAME.eq("PORCENTAJE_"+SALARY_DEDUCTION.DEDUCTION_CONCEPT))
//				.where(SALARY_DEDUCTION.SALARY.eq(r.getId())).getSQL());
//
//				HashMap<Integer, ArrayList<DefaultPayrollDeduction>> deductionMap = new HashMap<Integer, ArrayList<DefaultPayrollDeduction>>();
//				
//				System.out.println(deductionMap);
//
//				deductionsStream.forEach(d -> {
//					
//					
//					
////					if (map_percent.containsKey("PORCENTAJE_"+d.getDeductionConcept())) {
////						
////					}
//					
//					String sType = getDeductionType(d.getType());
//					
//					System.out.println("PORCENTAJE_"+sType);
//					
//					//System.out.println("PARAMETROS:\n\t"+d.getAmount()+"\n\t"+d.getDescription());
//					DefaultPayrollDeduction deduction = null;
//					try {
//						deduction = new DefaultPayrollDeduction(d.getAmount(),
//							d.getDescription(), Double.parseDouble(map_percent.get("PORCENTAJE_"+sType)));
//					} catch (NullPointerException e) {
//						deduction = new DefaultPayrollDeduction(d.getAmount(),
//								d.getDescription(), null);
//					}
//					
//					
//					if (deductionMap.containsKey(d.getType().intValue())) {
//						deductionMap.get(d.getType().intValue()).add(deduction);
//					} else {
//						ArrayList<DefaultPayrollDeduction> deductionList = new ArrayList<DefaultPayrollDeduction>();
//						deductionList.add(deduction);
//						deductionMap.put(d.getType().intValue(), deductionList);
//					}
//					
//				});
//				
//				dpb.setDeductions(deductionMap);
//				dpb.setDeduction_total(r.getTotalDeduction());
//				
//				Stream<SalaryCostRecord> costsStream = ctx.select()
//						.from(SALARY_COST)
//						.where(SALARY_COST.SALARY.eq(r.getId()))
//						.fetchStreamInto(SALARY_COST);
//				
//				Contingency_bases_builder cbb = new Contingency_bases_builder();
//				
//				cbb.setAt_ep_ap_enterprise(Optional.empty());
//				cbb.setAt_ep_type(Optional.empty());
//				cbb.setCommon_cont_ap_enterprise(Optional.empty());
//				cbb.setCommon_cont_base(Optional.empty());
//				cbb.setCommon_cont_type(Optional.empty());
//				cbb.setExtra_proration_amount(Optional.empty());
//				cbb.setFogasa_ap_enterprise(Optional.empty());
//				cbb.setFogasa_type(Optional.empty());
//				cbb.setForce_majeure_ap_enterprise(Optional.empty());
//				cbb.setForce_majeure_base(Optional.empty());
//				cbb.setForce_majeure_type(Optional.empty());
//				cbb.setIrpf_esp(Optional.empty());
//				cbb.setIrpf_retrib_diner(Optional.empty());
//				cbb.setMonthly_amount(Optional.empty());
//				cbb.setNo_struct_ap_enterprise(Optional.empty());
//				cbb.setNo_struct_base(Optional.empty());
//				cbb.setNo_struct_type(Optional.empty());
//				cbb.setProfes_form_ap_enterprise(Optional.empty());
//				cbb.setProfes_form_type(Optional.empty());
//				cbb.setProfessional_cont_base(Optional.empty());
//				cbb.setTotal(Optional.empty());
//				cbb.setUnemployment_ap_enterprise(Optional.empty());
//				cbb.setUnemployment_type(Optional.empty());
//
//				costsStream.forEach(c -> {
//					if (c.getCostConcept().equalsIgnoreCase("CGC_E"))
//						cbb.setCommon_cont_ap_enterprise(Optional.ofNullable(c.getAmount()));
//					else if (c.getCostConcept().equalsIgnoreCase("IT_E") || c.getCostConcept().equalsIgnoreCase("CGP_E"))
//						cbb.setAt_ep_ap_enterprise(Optional.ofNullable(c.getAmount()));
//					else if (c.getCostConcept().equalsIgnoreCase("DESMPL_E"))
//						cbb.setUnemployment_ap_enterprise(Optional.ofNullable(c.getAmount()));
//					else if (c.getCostConcept().equalsIgnoreCase("FP_E"))
//						cbb.setProfes_form_ap_enterprise(Optional.ofNullable(c.getAmount()));
//					else if (c.getCostConcept().equalsIgnoreCase("FOGASA") || c.getCostConcept().equalsIgnoreCase("FOGASA_E"))
//						cbb.setFogasa_ap_enterprise(Optional.ofNullable(c.getAmount()));
//				});
//				
//				Stream<SalaryDataRecord> dataStream = ctx.select()
//						.from(SALARY_DATA)
//						.where(SALARY_DATA.SALARY.eq(r.getId()))
//						.and(
//							SALARY_DATA.NAME.like("%PORCENTAJE%")
//							.or(SALARY_DATA.NAME.like("%BASE%"))
//						)
//						.fetchStreamInto(SALARY_DATA);
//				
//				
//				
//				
//				dataStream.forEach(d -> {
//					if (d.getName().equalsIgnoreCase("PORCENTAJE_CGC_E")) {
//						if (d.getExpression()!=null)
//							cbb.setCommon_cont_type(Optional.ofNullable(Double.parseDouble(d.getExpression())));
//						else
//							cbb.setCommon_cont_type(Optional.of(-1.00));	
//					} else if (d.getName().equalsIgnoreCase("PORCENTAJE_CGP_E") || d.getName().equalsIgnoreCase("PORCENTAJE_IT_E")) {
//						if (d.getExpression() != null)
//							cbb.setAt_ep_type(Optional.ofNullable(Double.parseDouble(d.getExpression())));
//						else
//							cbb.setAt_ep_type(Optional.of(-1.00));
//					} else if (d.getName().equalsIgnoreCase("PORCENTAJE_DESMPL_E")) {
//						if (d.getExpression() != null)
//							cbb.setUnemployment_type(Optional.ofNullable(Double.parseDouble(d.getExpression())));
//						else
//							cbb.setUnemployment_type(Optional.of(-1.00));
//					} else if (d.getName().equalsIgnoreCase("PORCENTAJE_FP_E")) {
//						if (d.getExpression() != null)
//							cbb.setProfes_form_type(Optional.ofNullable(Double.parseDouble(d.getExpression())));
//						else
//							cbb.setProfes_form_type(Optional.of(-1.00));
//					} else if (d.getName().equalsIgnoreCase("PORCENTAJE_FOGASA_E") || d.getName().equalsIgnoreCase("PORCENTAJE_FOGASA")) {
//						if (d.getExpression() != null)
//							cbb.setFogasa_type(Optional.ofNullable(Double.parseDouble(d.getExpression())));
//						else
//							cbb.setFogasa_type(Optional.of(-1.00));
//					}
//				});
//				
//				cbb.setMonthly_amount(Optional.ofNullable(r.getRemuneration()));
//				cbb.setCommon_cont_base(Optional.ofNullable(r.getCgcBase()));
//				cbb.setProfessional_cont_base(Optional.ofNullable(r.getCgpBase()));
//				cbb.setIrpf_retrib_diner(Optional.ofNullable(r.getIrpfBase()));
//				cbb.setIrpf_esp(Optional.ofNullable(r.getInkindIrpfBase()));
//				cbb.setTotal(Optional.ofNullable(r.getTotalEnterprise()));
//				dpb.setContingencies(cbb.build());
//				dpb.setPayroll_total(r.getTotalLiquid());
//				
//				DefaultPayroll payroll = dpb.build();
//				payrolls.add(payroll);
//				System.out.println(payroll);
//			});
//
//			try {
//				dpt.print(resp.getOutputStream(), payrolls, optLogo, Optional.ofNullable(new Locale("es")));
//			} catch (CanNotCreatePdfException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//
	}
	
	
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
	
	private static String getCostType (Byte type) {
		int intType = type.intValue();
		switch (intType) {
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
	
	private Condition getConditionSalaryIds(Map<String, String> params, Integer selectedSalaries) {
		Collection<Integer> _selectedSalaries = getSalaryIds(params, selectedSalaries);
		Condition condition ;
		condition = Salary.SALARY.ID.in(_selectedSalaries);
		
		return condition;
	}
	
	private /*Condition*/Collection<Integer> getSalaryIds(Map<String, String> params, Integer selectedSalaries) {
		ArrayList<Integer> _selectedSalaries = new ArrayList<>();

		if(0 != selectedSalaries) {
			for(int i=0; i<selectedSalaries; i++) {
				String idString = params.get("salary"+i+"Id");
				
				if(idString.contains("."))
					idString = idString.split("\\.")[0];
				
				_selectedSalaries.add(Integer.parseInt(idString));
			}
		}
		return _selectedSalaries;
//		Condition condition ;
//		condition = Salary.SALARY.ID.in(_selectedSalaries);
//		
//		return condition;
	}
	
	private Map<String, String> createParams(String paramsStr) {
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		
		String params = paramsStr.substring(1);
		
		String[] paramsArr = params.split("&");
		for(int i=0; i < paramsArr.length; i++) {
			String key = paramsArr[i].split("=")[0];
			String value = paramsArr[i].split("=")[1];
			
			paramsMap.put(key, value);
		}
		
		return paramsMap;
	}
	
	public String decode(byte[] value){
		String decode = "";
		decode = new String(Base64.decodeBase64(value));
		System.out.println(decode);
		return decode;
	}

}
