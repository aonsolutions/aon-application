package com.esferalia.aon.in.payroll.pdf.jooq;

import static com.esferalia.aon.in.payroll.pdf.maker.PdfMaker.printSettlement;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.watson.util.AonNumberUtils.toByte;
import static com.esferalia.aon.watson.util.AonStringUtils.trimToEmpty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.SettlePrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement.SettlementBuilder;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Deduction;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqPDFSettlementBuilder {
	
	public enum Dismissal {
		 UNFAIR("Despido Improcedente"),
		 OBJECTIVE("Despido por Causas Objetivas"),
		 WORK_END("Fin Contrato Fijo de Obra"),
		 TEMP_END("Fin Contrato Temporal"),
		 RETIREMENT("Jubilación del Empresario"),
		 DEFINITE_END("Fin Contrato Duración Determinada"),
		 CONDITIONS_CHANGE("Baja Voluntaria Modificación Condiciones"),
		 NOT_PASS_TRIAL_PERIOD("Baja por no Superar el Periodo de Prueba"),
		 DEATH_OF_EMPLOYEE("Fallecimiento del Trabajador"),
		;

		private String description;

		private Dismissal(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		};
	} 
	
	private static final Locale LOCALE_ES = new Locale("es", "ES");
	private static final String INDEMN_CAUSE = "CAUSA_INDEMNIZACION";
	private static final String PERCENT_PROFIX = "PORCENTAJE_";
	
	private Settlement settlement;
	private byte[] logo;
	private byte[] signature;
	private OutputStream os;
	private String domainName;
	private String login;
	

	public JooqPDFSettlementBuilder(String domainName, String login, Integer salaryId, OutputStream os) throws Exception{
		this.domainName = domainName;
		this.login = login;
		this.os = os;
		this.logo = new byte[0];
		this.signature = new byte[0];
		SettlementBuilder builder = buildSettle(SALARY.ID.eq(salaryId));
		if (builder != null) {
			this.settlement = builder.build();
		} else {
			throw new Exception("SETTLEMENT NOT FOUND");
		}
	}
	
	public void write() throws CanNotCreatePdfException, IOException {
		try (
			InputStream logoIS = null == this.logo ? null : new ByteArrayInputStream(this.logo);
			InputStream signatureIS = null == this.signature ? null : new ByteArrayInputStream(this.signature);) {
			
			printSettlement(os, new SettlePrintConfiguration(this.settlement, logoIS, signatureIS, new Locale("Es")));
		}
	}
	
	private SettlementBuilder buildSettle(Condition condition) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, login);
		) {
			 DSLContext ctx = aonContext.getDslContext();
			SettlementBuilder builder = new SettlementBuilder();
			
			Record settlementRecord = ctx.select()
			.from(SALARY)
			.innerJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.eq(SALARY.DOMAIN))
			.innerJoin(CONTRACT).on(SALARY.CONTRACT.eq(CONTRACT.ID))
			.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
			.innerJoin(RADDRESS).on(WORKPLACE.ADDRESS.eq(RADDRESS.ID))
			.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
			.leftJoin(SALARY_DATA).on(SALARY_DATA.SALARY.eq(SALARY.ID).and(SALARY_DATA.NAME.eq(INDEMN_CAUSE)))
			.where(condition)
			.and(SALARY.TYPE.eq(toByte(SalaryType.SETTLE.ordinal())))
			.fetchAny();
			
			if (settlementRecord == null)
				return null; 
			
			Map<Integer, ArrayList<PDFPayment>> payments = new LinkedHashMap<>();
			Map<Integer, ArrayList<PDFDeduction>> deductions = new LinkedHashMap<>();
			
			Attach logoAttach = AON.getAttach(
					this.domainName, 
					settlementRecord.get(SALARY.DOMAIN), 
					this.login,
					f -> f.getTypeProperty().eq(LOGO.value()).and(f.getDomainProperty().eq(aonContext.getDomainId())),
					REGISTRY
				);
			this.signature = getSignature(domainName, settlementRecord.get(SALARY.DOMAIN), login);
			
			if (logoAttach != null && !logoAttach.isEmpty()) {
				this.logo = logoAttach.getData();
			}
			
			Stream<Salary> salaries = AON.getSalaries(aonContext, f -> f.getIdProperty().eq(settlementRecord.get(SALARY.ID)));
			Salary salary = salaries.findFirst().orElse(null);
			if (salary != null) {
				salary.getPayments().stream().filter(Objects::nonNull).forEach(payment -> {
					ArrayList<PDFPayment> payList = payments.getOrDefault(payment.getPaymentType() != null ? payment.getPaymentType().ordinal() : null, new ArrayList<>());
					payList.add(new PDFPayment(payment.getAmount(), payment.getDescription()));
					payments.put(payment.getPaymentType() != null ? payment.getPaymentType().ordinal() : 0, payList);
				});
				salary.getDeductions().stream().filter(Objects::nonNull).forEach(deduction -> {
					ArrayList<PDFDeduction> dedList = deductions.getOrDefault(getDeductionKey(deduction.getDeductionType()), new ArrayList<>());
					Double percent = findDeductionPercent(salary, deduction.getName());
					percent = AonNumberUtils.zeroIfNull(percent) > 0 ? percent : getCalculatedPercent(salary, deduction);
					dedList.add(new PDFDeduction(deduction.getAmount(), deduction.getName() , deduction.getDescription() != null ? deduction.getDescription() : getDeductionDescription(deduction.getName(), deduction.getDeductionType()), percent, deduction.getDeductionType()));
					deductions.put(getDeductionKey(deduction.getDeductionType()), dedList);			
				});
				salary.getEmbargos().stream().filter(Objects::nonNull).forEach(embargo -> {
					ArrayList<PDFDeduction> dedList = deductions.getOrDefault(getDeductionKey(DeductionType.EMBARGO), new ArrayList<>());
					dedList.add(new PDFDeduction(embargo.getAmount(), null, embargo.getDescription() != null ? embargo.getDescription() : "EMBARGO", null, DeductionType.EMBARGO));
					deductions.put(getDeductionKey(DeductionType.EMBARGO), dedList);
				});
			}
			
			
			boolean existRepresentative = ctx.select()
			.from(RDIR_STAFF)
			.where(RDIR_STAFF.REGISTRY.eq(settlementRecord.get(ENTERPRISE.REGISTRY)))
			.fetchStreamInto(RDIR_STAFF)
			.filter(Objects::nonNull)
			.anyMatch(reg -> reg.getRepresentativeLabor().intValue() == 1 || reg.getRepresentative().intValue() == 1);
			
			String dismissalStr = settlementRecord.get(SALARY_DATA.EXPRESSION);
			
			Dismissal dismissal = dismissalStr != null ? Dismissal.valueOf(dismissalStr) : null;
			
			RAddress raddress = AON.getRAddress(domainName, settlementRecord.get(SALARY.DOMAIN), login, f -> f.getIdProperty().eq(settlementRecord.get(RADDRESS.ID)));
			raddress = raddress != null ? raddress : new RAddress();
			
			builder
			.setEmployeeName(trimToEmpty(settlementRecord.get(SALARY.EMPLOYEE_NAME)).toUpperCase(LOCALE_ES))
			.setEmployeeNIF(settlementRecord.get(SALARY.EMPLOYEE_DOCUMENT))
			.setEmployeeAntiquity(settlementRecord.get(SALARY.SENIORITY_DATE))
			.setEmployeeCategory(trimToEmpty(settlementRecord.get(SALARY.CATEGORY)).toUpperCase(LOCALE_ES))
			.setEnterpriseName(trimToEmpty(settlementRecord.get(SALARY.ENTERPRISE_NAME)).toUpperCase(LOCALE_ES))
			.setEnterpriseAddress(!AonStringUtils.isEmpty(raddress.getFullAddress()) ? raddress.getFullAddress() : settlementRecord.get(SALARY.ENTERPRISE_ADDRESS))
			.setEnterpriseNIF(settlementRecord.get(SALARY.ENTERPRISE_DOCUMENT))
			.setEndDate(settlementRecord.get(SALARY.END_DATE))
			.setDeductionTotal(settlementRecord.get(SALARY.TOTAL_DEDUCTION))
			.setAccrualTotal(settlementRecord.get(SALARY.TOTAL_PAYMENT))
			.setTotal(settlementRecord.get(SALARY.TOTAL_LIQUID))
			.setDate(settlementRecord.get(SALARY.ISSUE_DATE))
			.setExistRepresentative(existRepresentative)
			.setLocation(settlementRecord.get(RADDRESS.CITY))
			.setEndCause(dismissal != null ? dismissal.getDescription() : null)
			.setPayments(payments)
			.setDeductions(deductions);
			
			return builder;
		}
		
		
	}
	
	public static byte[] getSignature(String domainName, Integer domainId, String login) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, login)) {
			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.SIGNATURE.value())
							.and(f.getDomainProperty().eq(domainId)),
					AttachType.REGISTRY);
			if (attach1 == null || attach1.getData() == null)
				attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
							.and(f.getDomainProperty().eq(domainId)),
					AttachType.REGISTRY);
			if (attach1 != null && attach1.getData() != null)
				return attach1.getData();
		} catch (Exception e) {}
		return null;
	}
	
	private Double getCalculatedPercent(Salary salary, Deduction deduction) {
		try {
			if (deduction == null || deduction.getDeductionType() == null || deduction.getAmount() == null)
				return null;
			com.esferalia.aon.salary.enumeration.DeductionType reference = com.esferalia.aon.salary.enumeration.DeductionType.valueOf(deduction.getDeductionType().toString());
			if (!reference.isSsDeduction() && !reference.isTaxDeduction())
				return null;
			else if (reference.isSsDeduction()) {
							
				return deduction.getDeductionType().accept(new DeductionType.Visitor<Double>() {

					@Override
					public Double visitCommonContigency(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getCommonContingenciesBase()) > 0 ? deduction.getAmount() / salary.getCommonContingenciesBase() * 100: null;
					}

					@Override
					public Double visitProfessionalContigency(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getProfessionalContingenciesBase()) > 0 ? deduction.getAmount() / salary.getProfessionalContingenciesBase() * 100: null;
					}

					@Override
					public Double visitUnemployent(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getProfessionalContingenciesBase()) > 0 ? deduction.getAmount() / salary.getProfessionalContingenciesBase() * 100: null;
						
					}

					@Override
					public Double visitJobTraining(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getProfessionalContingenciesBase()) > 0 ? deduction.getAmount() / salary.getProfessionalContingenciesBase() * 100 : null;
					}

					@Override
					public Double visitStructuralOvertime(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getEstructuralOvertimeBase()) > 0 ? deduction.getAmount() / salary.getEstructuralOvertimeBase() * 100 : null;
					}

					@Override
					public Double visitNonStructuralOvertime(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getNonEstructuralOvertimeBase()) > 0 ? deduction.getAmount() / salary.getNonEstructuralOvertimeBase() * 100 : null;
					}

					@Override
					public Double visitFogasa(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getProfessionalContingenciesBase()) > 0 ? deduction.getAmount() / salary.getProfessionalContingenciesBase() * 100 : null;
					}

					@Override
					public Double visitIT(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getProfessionalContingenciesBase()) > 0 ? deduction.getAmount() / salary.getProfessionalContingenciesBase() * 100 : null;
					}

					@Override
					public Double visitIMS(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getProfessionalContingenciesBase()) > 0 ? deduction.getAmount() / salary.getProfessionalContingenciesBase() * 100 : null;
					}

					@Override
					public Double visitBonus(DeductionType deductionType) {
						return AonNumberUtils.zeroIfNull(salary.getProfessionalContingenciesBase()) > 0 ? deduction.getAmount() / salary.getProfessionalContingenciesBase() * 100 : null;
					}
					
				});

			} else if (salary.getIrpfBase() != null && salary.getIrpfBase() > 0){
				return deduction.getAmount() / salary.getIrpfBase() * 100 ;
			} else
				return null;
		} catch (Exception e) {
			return null;
		}
		
	}

	private static Double findDeductionPercent(Salary salary, String name) {
		List<String> dataList = salary.getContextData(PERCENT_PROFIX + trimToEmpty(name), Collectors.toList());
		if (dataList != null && dataList.size() == 1) {
			try {
				return Double.parseDouble(dataList.get(0));
			} catch (NullPointerException | NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	private static String getDeductionDescription(String name, DeductionType type) {
		if (type == null)
			return "Otros";
		if ( AonStringUtils.equals("MEI", name))
			return "Mecanismo de equidad intergeneracional";
		return type.accept(new DeductionType.Visitor<String> () {
						
			@Override
			public String visitCommonContigency(DeductionType deductionType) {
				return "Contingencias comunes";
			}

			@Override
			public String visitProfessionalContigency(DeductionType deductionType) {
				return "Contingencias profesionales";
			}

			@Override
			public String visitUnemployent(DeductionType deductionType) {
				return "Desempleo";
			}

			@Override
			public String visitJobTraining(DeductionType deductionType) {
				return "Formación profesional";
			}

			@Override
			public String visitStructuralOvertime(DeductionType deductionType) {
				return "Horas extraordinarias estructurales";
			}

			@Override
			public String visitNonStructuralOvertime(DeductionType deductionType) {
				return "Horas extraordinarias de fuerza mayor";
			}

			@Override
			public String visitIrpf(DeductionType deductionType) {
				return "Retribuciones dinerarias";
			}

			@Override
			public String visitAdvancePayment(DeductionType deductionType) {
				return "Anticipo";
			}

			@Override
			public String visitInkind(DeductionType deductionType) {
				return "En especie";
			}

			@Override
			public String visitOther(DeductionType deductionType) {
				return "Otras";
			}

			@Override
			public String visitFogasa(DeductionType deductionType) {
				return "FOGASA";
			}

			@Override
			public String visitIT(DeductionType deductionType) {
				return "Incapacidad temporal";
			}

			@Override
			public String visitIMS(DeductionType deductionType) {
				return "IMS";
			}

			@Override
			public String visitEmbargo(DeductionType deductionType) {
				return "Embargos";
			}

			@Override
			public String visitBonus(DeductionType deductionType) {
				return "Bonificaciones";
			}
			
		});
	}
	
	private static int getDeductionKey(DeductionType type) {
		if (type == null)
			return 6;
		return type.accept(new DeductionType.Visitor<Integer> () {

			@Override
			public Integer visitCommonContigency(DeductionType deductionType) {
				return 1;
			}

			@Override
			public Integer visitProfessionalContigency(DeductionType deductionType) {
				return 1;
			}

			@Override
			public Integer visitUnemployent(DeductionType deductionType) {
				return 1;
			}

			@Override
			public Integer visitJobTraining(DeductionType deductionType) {
				return 1;
			}

			@Override
			public Integer visitStructuralOvertime(DeductionType deductionType) {
				return 1;
			}

			@Override
			public Integer visitNonStructuralOvertime(DeductionType deductionType) {
				return 1;
			}

			@Override
			public Integer visitIrpf(DeductionType deductionType) {
				return 2;
			}

			@Override
			public Integer visitAdvancePayment(DeductionType deductionType) {
				return 3;
			}

			@Override
			public Integer visitInkind(DeductionType deductionType) {
				return 4;
			}

			@Override
			public Integer visitOther(DeductionType deductionType) {
				return 6;
			}

			@Override
			public Integer visitFogasa(DeductionType deductionType) {
				return 6;
			}

			@Override
			public Integer visitIT(DeductionType deductionType) {
				return 6;
			}

			@Override
			public Integer visitIMS(DeductionType deductionType) {
				return 6;
			}

			@Override
			public Integer visitEmbargo(DeductionType deductionType) {
				return 5;
			}

			@Override
			public Integer visitBonus(DeductionType deductionType) {
				return 6;
			}
		});
	}
	
}
