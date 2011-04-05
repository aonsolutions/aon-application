package com.esferalia.aon.ui.payroll.event.salary.draft;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.payment.CompensationOrPrepaidExpenses;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.OtherNonWages;
import com.esferalia.aon.salary.payment.SalarySupplements;



public class SalaryDraftComparatorPrinter {
	
	private final String RED_STYLE = "aon-label-error";
	private final String NORMAL_STYLE = "aon-outputText";

	private ISalary salary;
	private ISalary draft;
	private Payments payments;
	private Deductions deductions;
	private Bases bases;
	
	public SalaryDraftComparatorPrinter() {
//		this.salary = salary;
//		this.draft = draft;
	}
	
	public void initialize(){
		if(getSalary()==null || getDraft()==null){
			String msg = "No hay nomina o borrador para comparar";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		try {
			decoratePayments();
			decorateDeduction();
			decorateBases();
		} catch (SalaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ISalary getSalary() {
		return salary;
	}
	public void setSalary(ISalary salary) {
		this.salary = salary;
	}
	public ISalary getDraft() {
		return draft;
	}
	public void setDraft(ISalary draft) {
		this.draft = draft;
	}
	public Payments getPayments() {
		return payments;
	}
	public void setPayments(Payments payments) {
		this.payments = payments;
	}
	public Deductions getDeductions() {
		return deductions;
	}
	public void setDeductions(Deductions deductions) {
		this.deductions = deductions;
	}
	public Bases getBases() {
		return bases;
	}
	public void setBases(Bases bases) {
		this.bases = bases;
	}

	private void decoratePayments() throws SalaryException {
		Payments payments = new Payments();
		payments.setBaseSalary(getSalary().getPayments().getBaseSalary(), getDraft().getPayments().getBaseSalary());
		payments.setOvertimeHours(getSalary().getPayments().getOvertimeHours(), getDraft().getPayments().getOvertimeHours());
		payments.setSpecialBonuses(getSalary().getPayments().getSpecialBonuses(), getDraft().getPayments().getSpecialBonuses());
		payments.setSalaryInKind(getSalary().getPayments().getSalaryInKind(), getDraft().getPayments().getSalaryInKind());
		payments.setSpecialSecurityBenefits(getSalary().getPayments().getSpecialSecurityBenefits(), getDraft().getPayments().getSpecialSecurityBenefits());
		payments.setMovingCompensation(getSalary().getPayments().getMovingCompensation(), getDraft().getPayments().getMovingCompensation());
		payments.setTotalPayment(getSalary().getTotalPayment(), getDraft().getTotalPayment());
		payments.setSalarySupplements(getSalary().getPayments().getSalarySupplements(), getDraft().getPayments().getSalarySupplements());
		payments.setCompensationOrPrepaidExpenses(getSalary().getPayments().getCompensationOrPrepaidExpenses(), getDraft().getPayments().getCompensationOrPrepaidExpenses());
		payments.setOtherNonWages(getSalary().getPayments().getOtherNonWages(), getDraft().getPayments().getOtherNonWages());
		setPayments(payments);
	}
	private void decorateDeduction() throws SalaryException {
		Deductions deductions = new Deductions();
		deductions.setCommonContingency(getSalary().getDeductions().getCommonContingency(), getDraft().getDeductions().getCommonContingency());
		deductions.setUnemployment(getSalary().getDeductions().getUnemployment(), getDraft().getDeductions().getUnemployment());
		deductions.setJobTraining(getSalary().getDeductions().getJobTraining(), getDraft().getDeductions().getJobTraining());
		deductions.setStructuralOvertime(getSalary().getDeductions().getStructuralOvertime(), getDraft().getDeductions().getStructuralOvertime());
		deductions.setNonStructuralOvertime(getSalary().getDeductions().getNonStructuralOvertime(), getDraft().getDeductions().getNonStructuralOvertime());
		deductions.setSocialSecurityContributions(getSalary().getDeductions().getSocialSecurityContributions(), getDraft().getDeductions().getSocialSecurityContributions());
		deductions.setIrpf(getSalary().getDeductions().getIrpf(), getDraft().getDeductions().getIrpf());
		deductions.setAdvancePayment(getSalary().getDeductions().getAdvancePayment(), getDraft().getDeductions().getAdvancePayment());
		deductions.setInKind(getSalary().getDeductions().getInKind(), getDraft().getDeductions().getInKind());
		deductions.setOther(getSalary().getDeductions().getOther(), getDraft().getDeductions().getOther());
		deductions.setTotalDeduction(getSalary().getTotalDeduction(), getDraft().getTotalDeduction());
		setDeductions(deductions);
	}
	private void decorateBases() {
		Bases bases = new Bases();
		bases.setTotalLiquid(getSalary().getTotalLiquid(), getDraft().getTotalLiquid());
		bases.setRemuneration(getSalary().getRemuneration(), getDraft().getRemuneration());
		bases.setExtraPayProration(getSalary().getExtraPayProration(), getDraft().getExtraPayProration());
		bases.setCommonBase(getSalary().getCommonBase(), getDraft().getCommonBase());
		bases.setProfessionalBase(getSalary().getProfessionalBase(), getDraft().getProfessionalBase());
		bases.setOvertimeBase(getSalary().getOvertimeBase(), getDraft().getOvertimeBase());
		bases.setIrpfBase(getSalary().getIrpfBase(), getDraft().getIrpfBase());
		setBases(bases);
	}



	/*
	 * SubClases para definir el formato del texto de cada bloque (payment, deduction, bases) 
	 */
	public class Payments{
		
		private Decorable baseSalary;
		private List<Decorable> salarySupplements;
		private Decorable overtimeHours;
		private Decorable specialBonuses;
		private Decorable salaryInKind;
		private List<Decorable> compensationOrPrepaidExpenses;
		private Decorable specialSecurityBenefits;
		private Decorable movingCompensation;
		private List<Decorable> otherNonWages;
		private Decorable totalPayment;
		
		public Decorable getBaseSalary() {
			return baseSalary;
		}
		public void setBaseSalary(Decorable baseSalary) {
			this.baseSalary = baseSalary;
		}
		public List<Decorable> getSalarySupplements() {
			return salarySupplements;
		}
		public void setSalarySupplements(List<Decorable> salarySupplements) {
			this.salarySupplements = salarySupplements;
		}
		public Decorable getOvertimeHours() {
			return overtimeHours;
		}
		public void setOvertimeHours(Decorable overtimeHours) {
			this.overtimeHours = overtimeHours;
		}
		public Decorable getSpecialBonuses() {
			return specialBonuses;
		}
		public void setSpecialBonuses(Decorable specialBonuses) {
			this.specialBonuses = specialBonuses;
		}
		public Decorable getSalaryInKind() {
			return salaryInKind;
		}
		public void setSalaryInKind(Decorable salaryInKind) {
			this.salaryInKind = salaryInKind;
		}
		public List<Decorable> getCompensationOrPrepaidExpenses() {
			return compensationOrPrepaidExpenses;
		}
		public void setCompensationOrPrepaidExpenses(
				List<Decorable> compensationOrPrepaidExpenses) {
			this.compensationOrPrepaidExpenses = compensationOrPrepaidExpenses;
		}
		public Decorable getSpecialSecurityBenefits() {
			return specialSecurityBenefits;
		}
		public void setSpecialSecurityBenefits(Decorable specialSecurityBenefits) {
			this.specialSecurityBenefits = specialSecurityBenefits;
		}
		public Decorable getMovingCompensation() {
			return movingCompensation;
		}
		public void setMovingCompensation(Decorable movingCompensation) {
			this.movingCompensation = movingCompensation;
		}
		public List<Decorable> getOtherNonWages() {
			return otherNonWages;
		}
		public void setOtherNonWages(List<Decorable> otherNonWages) {
			this.otherNonWages = otherNonWages;
		}
		public Decorable getTotalPayment() {
			return totalPayment;
		}
		public void setTotalPayment(Decorable totalPayment) {
			this.totalPayment = totalPayment;
		}
		
		public void setTotalPayment(Double totalPayment2, Double totalPayment3) {
			setTotalPayment(getDecorable(totalPayment2, totalPayment3));
		}
		public void setMovingCompensation(IPayment movingCompensation2,
				IPayment movingCompensation3) {
			setMovingCompensation(getDecorable(getDoubleValue(movingCompensation2), getDoubleValue(movingCompensation3)));
		}
		public void setSpecialSecurityBenefits(
				IPayment specialSecurityBenefits2,
				IPayment specialSecurityBenefits3) {
			setSpecialSecurityBenefits(getDecorable(getDoubleValue(specialSecurityBenefits2), getDoubleValue(specialSecurityBenefits3)));
		}
		public void setSalaryInKind(IPayment salaryInKind2,
				IPayment salaryInKind3) {
			setSalaryInKind(getDecorable(getDoubleValue(salaryInKind2), getDoubleValue(salaryInKind3)));
		}
		public void setSpecialBonuses(IPayment specialBonuses2,
				IPayment specialBonuses3) {
			setSpecialBonuses(getDecorable(getDoubleValue(specialBonuses2), getDoubleValue(specialBonuses3)));
		}
		public void setOvertimeHours(IPayment overtimeHours2,
				IPayment overtimeHours3) {
			setOvertimeHours(getDecorable(getDoubleValue(overtimeHours2), getDoubleValue(overtimeHours3)));
		}
		public void setBaseSalary(IPayment baseSalary2, IPayment baseSalary3) {
			setBaseSalary(getDecorable(getDoubleValue(baseSalary2), getDoubleValue(baseSalary3)));
		}
		
		public void setOtherNonWages(OtherNonWages salaryOtherNonWages,
				OtherNonWages draftOtherNonWages) {
			List<Decorable> list = new LinkedList<Decorable>();
			for(IPayment p: draftOtherNonWages.getValues()){
				list.add(getDecorable(getDoubleValue(getSameOtherNonWage(p, salaryOtherNonWages)), getDoubleValue(p)));
			}
			setOtherNonWages(list);
		}
		public void setCompensationOrPrepaidExpenses(
				CompensationOrPrepaidExpenses salaryCompensationOrPrepaidExpenses,
				CompensationOrPrepaidExpenses draftCompensationOrPrepaidExpenses) {
			List<Decorable> list = new LinkedList<Decorable>();
			for(IPayment p: draftCompensationOrPrepaidExpenses.getValues()){
				list.add(getDecorable(getDoubleValue(getSameCompensationOrPrepaidExpense(p, salaryCompensationOrPrepaidExpenses)), getDoubleValue(p)));
			}
			setCompensationOrPrepaidExpenses(list);
		}
		public void setSalarySupplements(SalarySupplements salarySalarySupplements,
				SalarySupplements draftSalarySupplements) {
			List<Decorable> list = new LinkedList<Decorable>();
			for(IPayment p: draftSalarySupplements.getValues()){
//				list.add(getDecorable(getDoubleValue(getSameSalarySupplement(p, salarySalarySupplements)), getDoubleValue(p)));
				Decorable d = getDecorable(getDoubleValue(getSameSalarySupplement(p, salarySalarySupplements)), getDoubleValue(p));
				if(d!=null){
					list.add(d);
				}
			}
			setSalarySupplements(list);
		}
		
		
		private IPayment getSameOtherNonWage(IPayment draftPayment,
				OtherNonWages list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) && draftPayment.getAmount()==p.getAmount()){
					return p;
				}
			}
			return null;
		}
		private IPayment getSameCompensationOrPrepaidExpense(IPayment draftPayment,
				CompensationOrPrepaidExpenses list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) && draftPayment.getAmount()==p.getAmount()){
					return p;
				}
			}
			return null;
		}
		private IPayment getSameSalarySupplement(IPayment draftPayment,
				SalarySupplements list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) && draftPayment.getAmount()==p.getAmount()){
					return p;
				}
			}
			return null;
		}
		private Double getDoubleValue(IPayment payment) {
			if(payment==null){
				return null;
			}
			return payment.getAmount();
		}
		public Decorable getDecorable(Double salaryValue, Double draftValue){
			Decorable decorable = new Decorable();
			if(salaryValue==null && draftValue==null){
				decorable.setValue("");
				decorable.setStyle(NORMAL_STYLE);
			} else if(salaryValue!=null && draftValue!=null){
				decorable.setValue(salaryValue.toString());
				decorable.setStyle((salaryValue.equals(draftValue))?NORMAL_STYLE:RED_STYLE);
			} else {
				if(salaryValue==null){
					decorable.setValue("ND");	
					decorable.setStyle(RED_STYLE);
					// PEDAZO DE TXURRO PARA SALIR DEL PASO
					decorable = null;
				} else {
					decorable.setValue(salaryValue.toString());
					decorable.setStyle(NORMAL_STYLE);
				}
			}
			return decorable;
		}
	}
	public class Deductions{
		private Decorable commonContingency;
		private Decorable unemployment;
		private Decorable jobTraining;
		private Decorable structuralOvertime;
		private Decorable nonStructuralOvertime;
		private Decorable socialSecurityContributions;
		private Decorable irpf;
		private Decorable advancePayment;
		private Decorable inKind;
		private Decorable other;
		private Decorable totalDeduction;
		
		public Decorable getCommonContingency() {
			return commonContingency;
		}
		public void setCommonContingency(Decorable commonContingency) {
			this.commonContingency = commonContingency;
		}
		public Decorable getUnemployment() {
			return unemployment;
		}
		public void setUnemployment(Decorable unemployment) {
			this.unemployment = unemployment;
		}
		public Decorable getJobTraining() {
			return jobTraining;
		}
		public void setJobTraining(Decorable jobTraining) {
			this.jobTraining = jobTraining;
		}
		public Decorable getStructuralOvertime() {
			return structuralOvertime;
		}
		public void setStructuralOvertime(Decorable structuralOvertime) {
			this.structuralOvertime = structuralOvertime;
		}
		public Decorable getNonStructuralOvertime() {
			return nonStructuralOvertime;
		}
		public void setNonStructuralOvertime(Decorable nonStructuralOvertime) {
			this.nonStructuralOvertime = nonStructuralOvertime;
		}
		public Decorable getSocialSecurityContributions() {
			return socialSecurityContributions;
		}
		public void setSocialSecurityContributions(Decorable socialSecurityContributions) {
			this.socialSecurityContributions = socialSecurityContributions;
		}
		public Decorable getIrpf() {
			return irpf;
		}
		public void setIrpf(Decorable irpf) {
			this.irpf = irpf;
		}
		public Decorable getAdvancePayment() {
			return advancePayment;
		}
		public void setAdvancePayment(Decorable advancePayment) {
			this.advancePayment = advancePayment;
		}
		public Decorable getInKind() {
			return inKind;
		}
		public void setInKind(Decorable inKind) {
			this.inKind = inKind;
		}
		public Decorable getOther() {
			return other;
		}
		public void setOther(Decorable other) {
			this.other = other;
		}
		public Decorable getTotalDeduction() {
			return totalDeduction;
		}
		public void setTotalDeduction(Decorable totalDeduction) {
			this.totalDeduction = totalDeduction;
		}
		
		public void setTotalDeduction(Double totalDeduction2,
				Double totalDeduction3) {
			setTotalDeduction(getDecorable(totalDeduction2, totalDeduction3));
		}
		public void setOther(IDeduction other2, IDeduction other3) {
			setInKind(getDecorable(getDoubleValue(other2), getDoubleValue(other3)));
		}
		public void setInKind(IDeduction inKind2, IDeduction inKind3) {
			setInKind(getDecorable(getDoubleValue(inKind2), getDoubleValue(inKind3)));
		}
		public void setAdvancePayment(IDeduction advancePayment2,
				IDeduction advancePayment3) {
			setAdvancePayment(getDecorable(getDoubleValue(advancePayment2), getDoubleValue(advancePayment3)));
		}
		public void setIrpf(IDeduction irpf2, IDeduction irpf3) {
			setIrpf(getDecorable(getDoubleValue(irpf2), getDoubleValue(irpf3)));
		}
		public void setSocialSecurityContributions(
				Double socialSecurityContributions2,
				Double socialSecurityContributions3) {
			setSocialSecurityContributions(getDecorable(socialSecurityContributions2, socialSecurityContributions3));
		}
		public void setNonStructuralOvertime(IDeduction nonStructuralOvertime2,
				IDeduction nonStructuralOvertime3) {
			setNonStructuralOvertime(getDecorable(getDoubleValue(nonStructuralOvertime2), getDoubleValue(nonStructuralOvertime3)));
		}
		public void setStructuralOvertime(IDeduction structuralOvertime2,
				IDeduction structuralOvertime3) {
			setStructuralOvertime(getDecorable(getDoubleValue(structuralOvertime2), getDoubleValue(structuralOvertime3)));
		}
		public void setJobTraining(IDeduction jobTraining2,
				IDeduction jobTraining3) {
			setJobTraining(getDecorable(getDoubleValue(jobTraining2), getDoubleValue(jobTraining3)));
		}
		public void setUnemployment(IDeduction unemployment2,
				IDeduction unemployment3) {
			setUnemployment(getDecorable(getDoubleValue(unemployment2), getDoubleValue(unemployment3)));
		}
		public void setCommonContingency(IDeduction commonContingency2,
				IDeduction commonContingency3) {
			setCommonContingency(getDecorable(getDoubleValue(commonContingency2), getDoubleValue(commonContingency3)));
		}
		
		private Double getDoubleValue(IDeduction deduction) {
			if(deduction==null){
				return null;
			}
			return deduction.getAmount();
		}
		public Decorable getDecorable(Double salaryValue, Double draftValue){
			Decorable decorable = new Decorable();
			if(salaryValue==null && draftValue==null){
				decorable.setValue("");
				decorable.setStyle(NORMAL_STYLE);
			} else if(salaryValue!=null && draftValue!=null){
				decorable.setValue(salaryValue.toString());
				decorable.setStyle((salaryValue.equals(draftValue))?NORMAL_STYLE:RED_STYLE);
			} else {
				if(salaryValue==null){
					decorable.setValue("ND");	
					decorable.setStyle(RED_STYLE);
				} else {
					decorable.setValue(salaryValue.toString());
					decorable.setStyle(NORMAL_STYLE);
				}
			}
			return decorable;
		}
		
	}
	public class Bases{
		private Decorable totalLiquid;
		private Decorable remuneration;
		private Decorable extraPayProration;
		private Decorable commonBase;
		private Decorable professionalBase;
		private Decorable overtimeBase;
		private Decorable irpfBase;
	
		public Decorable getTotalLiquid() {
			return totalLiquid;
		}
		public void setTotalLiquid(Decorable totalLiquid) {
			this.totalLiquid = totalLiquid;
		}
		public Decorable getRemuneration() {
			return remuneration;
		}
		public void setRemuneration(Decorable remuneration) {
			this.remuneration = remuneration;
		}
		public Decorable getExtraPayProration() {
			return extraPayProration;
		}
		public void setExtraPayProration(Decorable extraPayProration) {
			this.extraPayProration = extraPayProration;
		}
		public Decorable getCommonBase() {
			return commonBase;
		}
		public void setCommonBase(Decorable commonBase) {
			this.commonBase = commonBase;
		}
		public Decorable getProfessionalBase() {
			return professionalBase;
		}
		public void setProfessionalBase(Decorable professionalBase) {
			this.professionalBase = professionalBase;
		}
		public Decorable getOvertimeBase() {
			return overtimeBase;
		}
		public void setOvertimeBase(Decorable overtimeBase) {
			this.overtimeBase = overtimeBase;
		}
		public Decorable getIrpfBase() {
			return irpfBase;
		}
		public void setIrpfBase(Decorable irpfBase) {
			this.irpfBase = irpfBase;
		}
		
		public void setIrpfBase(Double irpfBase2, Double irpfBase3) {
			setIrpfBase(getDecorable(irpfBase2, irpfBase3));
		}
		public void setOvertimeBase(Double overtimeBase2, Double overtimeBase3) {
			setOvertimeBase(getDecorable(overtimeBase2, overtimeBase3));
		}
		public void setProfessionalBase(Double professionalBase2,
				Double professionalBase3) {
			setProfessionalBase(getDecorable(professionalBase2, professionalBase3));
		}
		public void setCommonBase(Double commonBase2, Double commonBase3) {
			setCommonBase(getDecorable(commonBase2, commonBase3));
		}
		public void setExtraPayProration(Double extraPayProration2,
				Double extraPayProration3) {
			setExtraPayProration(getDecorable(extraPayProration2, extraPayProration3));
		}
		public void setRemuneration(Double remuneration2, Double remuneration3) {
			setRemuneration(getDecorable(remuneration2, remuneration3));
		}
		public void setTotalLiquid(Double totalLiquid2, Double totalLiquid3) {
			setTotalLiquid(getDecorable(totalLiquid2, totalLiquid3));
		}

		public Decorable getDecorable(Double salaryValue, Double draftValue){
			Decorable decorable = new Decorable();
			if(salaryValue==null && draftValue==null){
				decorable.setValue("");
				decorable.setStyle(NORMAL_STYLE);
			} else if(salaryValue!=null && draftValue!=null){
				decorable.setValue(salaryValue.toString());
				decorable.setStyle((salaryValue.equals(draftValue))?NORMAL_STYLE:RED_STYLE);
			} else {
				if(salaryValue==null){
					decorable.setValue("ND");	
					decorable.setStyle(RED_STYLE);
				} else {
					decorable.setValue(salaryValue.toString());
					decorable.setStyle(NORMAL_STYLE);
				}
			}
			return decorable;
		}
	}
	
	public class Decorable {
		private String style;
		private String value;
		public String getStyle() {
			return style;
		}
		public void setStyle(String style) {
			this.style = style;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	
	

}