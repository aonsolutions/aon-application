package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;

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
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqPayrollBuilder {

	public static void generatePayroll(String serverName, OutputStream outputStream, Integer[] salaryIds) {
		DefaultPayrollTemplate dpt = new DefaultPayrollTemplate();
		DefaultPayrollBuilder dpb = new DefaultPayrollBuilder();
		try (AONContext aonContext = AONContext.getAONContext(serverName, "")) {
			DSLContext ctx = aonContext.getDslContext();

			Stream<com.esferalia.aon.occam.api.model.Salary> salaries = AON.getSalaries(aonContext,
					p -> p.getIdProperty().in(salaryIds));

			Collection<DefaultPayroll> payrolls = salaries.map(s -> {

//				SalaryRecord others = ctx.select().from(SALARY).where(SALARY.ID.eq(s.getId())).fetchOneInto(SALARY);
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
//				}
				
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
				
				dpb.setAccrual_total(s.getTotalPayment());

				HashMap<Integer, ArrayList<DefaultPayrollAccrual>> paymentMap = new HashMap<Integer, ArrayList<DefaultPayrollAccrual>>();

				s.getPayments().stream().forEach(p -> {
					DefaultPayrollAccrual accrual = new DefaultPayrollAccrual(p.getAmount(), p.getDescription());
					if (paymentMap.containsKey(p.getPaymentType().ordinal())) {
						paymentMap.get(p.getPaymentType().ordinal()).add(accrual);
					} else {
						ArrayList<DefaultPayrollAccrual> accrualList = new ArrayList<DefaultPayrollAccrual>();
						accrualList.add(accrual);
						paymentMap.put(p.getPaymentType().ordinal(), accrualList);
					}
				});

				dpb.setAccruals(paymentMap);
//					dpb.setAddress(others.getEnterpriseAddress());
				List<String> address = null;
				try {
					address = PDFToolkit.divide_string_to_fit(s.getEnterpriseAddress(), 200, PdfFonts.HELVETICA, 9f);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (address != null && address.size() > 1) {
					dpb.setAddress(address.get(0));
					dpb.setAddress_2(address.get(1));
				} else {
					dpb.setAddress(s.getEnterpriseAddress());
				}

				dpb.setAntiquity(s.getEmployeeSeniorityDate());
				dpb.setCcc(s.getEnterpriseCCC());
				dpb.setCif(s.getEnterpriseDocument());

				Contingency_bases_builder cbb = new Contingency_bases_builder();

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

				s.getCosts().forEach(c -> {
					if (c.getCostType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
						cbb.setCommon_cont_ap_enterprise(Optional.ofNullable(c.getAmount()));
					else if (c.getCostType().ordinal() == DeductionType.IT.ordinal())
						cbb.setAt_ep_ap_enterprise(Optional.ofNullable(c.getAmount()));
					else if (c.getCostType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
						cbb.setUnemployment_ap_enterprise(Optional.ofNullable(c.getAmount()));
					else if (c.getCostType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
						cbb.setProfes_form_ap_enterprise(Optional.ofNullable(c.getAmount()));
					else if (c.getCostType().ordinal() == DeductionType.FOGASA.ordinal())
						cbb.setFogasa_ap_enterprise(Optional.ofNullable(c.getAmount()));
				});

				Map<String, List<ContextData>> data = s.getContextData();

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
				
				cbb.setMonthly_amount(Optional.ofNullable(s.getRemuneration()));
				cbb.setCommon_cont_base(Optional.ofNullable(s.getCommonContingenciesBase()));
				cbb.setProfessional_cont_base(Optional.ofNullable(s.getProfessionalContingenciesBase()));
				cbb.setIrpf_retrib_diner(Optional.ofNullable(s.getIrpfBase()));
				cbb.setIrpf_esp(Optional.ofNullable(s.getInkindIrpfBase()));
				cbb.setTotal(Optional.ofNullable(s.getTotalEnterprise()));

				dpb.setContingencies(cbb.build());

				dpb.setDeduction_total(s.getTotalDeduction());

				HashMap<Integer, ArrayList<DefaultPayrollDeduction>> deductionMap = new HashMap<Integer, ArrayList<DefaultPayrollDeduction>>();

				s.getDeductions().forEach(d -> {
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
					DefaultPayrollDeduction dpd = new DefaultPayrollDeduction(d.getAmount(), d.getDescription(), percent);
					if (deductionMap.containsKey(dt.ordinal()))
						deductionMap.get(dt.ordinal()).add(dpd);
					else {
						ArrayList<DefaultPayrollDeduction> arr = new ArrayList<DefaultPayrollDeduction>();
						arr.add(dpd);
						deductionMap.put(dt.ordinal(), arr);
					}

				});

				dpb.setDeductions(deductionMap);
				dpb.setEmployee(s.getEmployeeName());
				dpb.setEnterprise(s.getEnterpriseName());
				dpb.setLiquid_period_start(s.getStartDate());
				dpb.setLiquid_period_end(s.getEndDate());
				dpb.setNif(s.getEmployeeDocument());
				dpb.setNss(s.getEmployeeSSNumber());
				dpb.setPayroll_total(s.getTotalLiquid());
				dpb.setProfessional_group(s.getEmployeeCategory());
				dpb.setQuotation_group(s.getEmployeeQuoteGroup());
				dpb.setTotal_days(s.getSalaryDays());

				return dpb.build();
			}).collect(Collectors.toList());

			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
					AttachType.REGISTRY);

			Optional<InputStream> optLogo = Optional.ofNullable(new ByteArrayInputStream(attach1.getData()));

			try {
				dpt.print(outputStream, payrolls, optLogo, Optional.ofNullable(new Locale("es")));
			} catch (CanNotCreatePdfException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

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
}
