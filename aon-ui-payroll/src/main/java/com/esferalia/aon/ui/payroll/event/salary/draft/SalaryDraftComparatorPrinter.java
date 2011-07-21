package com.esferalia.aon.ui.payroll.event.salary.draft;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.payment.BaseSalary;
import com.esferalia.aon.salary.payment.CompensationOrPrepaidExpenses;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.MovingCompensation;
import com.esferalia.aon.salary.payment.OtherNonWages;
import com.esferalia.aon.salary.payment.OvertimeHours;
import com.esferalia.aon.salary.payment.SalaryInKind;
import com.esferalia.aon.salary.payment.SalarySupplements;
import com.esferalia.aon.salary.payment.SpecialBonuses;
import com.esferalia.aon.salary.payment.SpecialSecurityBenefits;

public class SalaryDraftComparatorPrinter {
	
	private final String BLANK_TEXT = "";
	private final String NO_ELEMENT_TEXT = "n/d";
	private final String RED_STYLE = "aon-label-error";
	private final String NORMAL_STYLE = "aon-outputText";

	private ISalary salary;
	private ISalary draft;
	private Payments payments;
	private Deductions deductions;
	private Bases bases;
	private Bonus bonuses;
	
	public SalaryDraftComparatorPrinter(Contract contract, ISalary dbSalary, Date startDate, Date endDate, Date issueDate) {
		this.salary = dbSalary;
		Contract c = null;
		try {
			c = (Contract) BeanManager.getManagerBean(Contract.class).get(contract.getId());
			c.setSalaryCalculatorContext(null);
			ISalaryCalculatorContext ctx;
			ctx = c.getSalaryCalculatorContext(startDate,endDate,issueDate);
			this.draft = ctx.getSalaryProxy().getSalary();
		} catch (ManagerBeanException e1) {
			String msg = "Imposible mostrar el borrador de la nómina";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (SalaryException e) {
			String msg = "Imposible mostrar el borrador de la nómina";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		initialize();
	}
	
	public void initialize(){
		if(getSalary()==null || getDraft()==null){
			String msg = "No hay nómina o borrador para comparar";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		try {
			decoratePayments();
			decorateDeduction();
			decorateBases();
			decorateBonuses();
			setSalary(null);
			setDraft(null);
		} catch (SalaryException e) {
			String msg = "Imposible comparar el borrador con la nómina";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
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
	public Bonus getBonuses() {
		return bonuses;
	}
	public void setBonuses(Bonus bonuses) {
		this.bonuses = bonuses;
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
//		payments.setSalarySupplements(getSalary().getPayments().getSalarySupplements(), supplementsList);
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
	private void decorateBonuses() {
//		List<SalaryBonus> salaryBonus;
//		List<ContractBonus> contractBonus;
		Bonus bonuses = new Bonus();
		//bonuses.setAmounts(getSalaryBonuses(), getContractBonuses());
		setBonuses(bonuses);
	}

	private List<ITransferObject> getContractBonuses() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractBonus.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_CONTRACT_ID), ((Salary)getSalary()).getContract().getId());
			
			
//			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_START_DATE), getSalary().getStartDate());
//			criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE), getSalary().getEndDate());
			
			
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE), getSalary().getStartDate());
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));				
			
			
			criteria.addOrder(bean.getFieldName(IPayrollAlias.CONTRACT_BONUS_EXPRESSION));
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private List<ITransferObject> getSalaryBonuses() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(SalaryBonus.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_BONUS_SALARY_ID), ((Salary)getSalary()).getId());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.SALARY_BONUS_AMOUNT));
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	/*
	 * SubClases para definir el formato del texto de cada bloque (payment, deduction, bases) 
	 */
	public class Payments{
		
		private List<DecorableAmount> baseSalary;
		private List<DecorableAmount> salarySupplements;
		private List<DecorableAmount> overtimeHours;
		private List<DecorableAmount> specialBonuses;
		private List<DecorableAmount> salaryInKind;
		private List<DecorableAmount> compensationOrPrepaidExpenses;
		private List<DecorableAmount> specialSecurityBenefits;
		private List<DecorableAmount> movingCompensation;
		private List<DecorableAmount> otherNonWages;
		private DecorableAmount totalPayment;
		
		public List<DecorableAmount> getBaseSalary() {
			return baseSalary;
		}
		public void setBaseSalary(List<DecorableAmount> baseSalary) {
			this.baseSalary = baseSalary;
		}
		public List<DecorableAmount> getSalarySupplements() {
			return salarySupplements;
		}
		public void setSalarySupplements(List<DecorableAmount> salarySupplements) {
			this.salarySupplements = salarySupplements;
		}
		public List<DecorableAmount> getOvertimeHours() {
			return overtimeHours;
		}
		public void setOvertimeHours(List<DecorableAmount> overtimeHours) {
			this.overtimeHours = overtimeHours;
		}
		public List<DecorableAmount> getSpecialBonuses() {
			return specialBonuses;
		}
		public void setSpecialBonuses(List<DecorableAmount> specialBonuses) {
			this.specialBonuses = specialBonuses;
		}
		public List<DecorableAmount> getSalaryInKind() {
			return salaryInKind;
		}
		public void setSalaryInKind(List<DecorableAmount> salaryInKind) {
			this.salaryInKind = salaryInKind;
		}
		public List<DecorableAmount> getCompensationOrPrepaidExpenses() {
			return compensationOrPrepaidExpenses;
		}
		public void setCompensationOrPrepaidExpenses(
				List<DecorableAmount> compensationOrPrepaidExpenses) {
			this.compensationOrPrepaidExpenses = compensationOrPrepaidExpenses;
		}
		public List<DecorableAmount> getSpecialSecurityBenefits() {
			return specialSecurityBenefits;
		}
		public void setSpecialSecurityBenefits(List<DecorableAmount> specialSecurityBenefits) {
			this.specialSecurityBenefits = specialSecurityBenefits;
		}
		public List<DecorableAmount> getMovingCompensation() {
			return movingCompensation;
		}
		public void setMovingCompensation(List<DecorableAmount> movingCompensation) {
			this.movingCompensation = movingCompensation;
		}
		public List<DecorableAmount> getOtherNonWages() {
			return otherNonWages;
		}
		public void setOtherNonWages(List<DecorableAmount> otherNonWages) {
			this.otherNonWages = otherNonWages;
		}
		public DecorableAmount getTotalPayment() {
			return totalPayment;
		}
		public void setTotalPayment(DecorableAmount totalPayment) {
			this.totalPayment = totalPayment;
		}
		
		public void setTotalPayment(Double totalPayment2, Double totalPayment3) {
			setTotalPayment(getDecorableAmount(totalPayment2, totalPayment3));
		}
		public void setMovingCompensation(MovingCompensation salaryMovingCompensation, MovingCompensation draftMovingCompensation) {
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftMovingCompensation.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameMovingCompensation(p, salaryMovingCompensation)), getDoubleValue(p)));
			}
			setMovingCompensation(list);
		}
		public void setSpecialSecurityBenefits(SpecialSecurityBenefits salarySpecialSecurityBenefits, 
				SpecialSecurityBenefits draftSpecialSecurityBenefits) {
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftSpecialSecurityBenefits.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameSpecialSecurityBenefits(p, salarySpecialSecurityBenefits)), getDoubleValue(p)));
			}
			setSpecialSecurityBenefits(list);
		}
		public void setSalaryInKind(SalaryInKind salaryInKind, SalaryInKind draftInKind) {
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftInKind.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameSalaryInKind(p, salaryInKind)), getDoubleValue(p)));
			}
			setSalaryInKind(list);
		}
		public void setSpecialBonuses(SpecialBonuses salarySpecialBonuses, SpecialBonuses draftSpecialBonuses) {
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftSpecialBonuses.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameSpecialBonuses(p, salarySpecialBonuses)), getDoubleValue(p)));
			}
			setSpecialBonuses(list);
		}
		public void setOvertimeHours(OvertimeHours salaryOvertimeHours, OvertimeHours draftOvertimeHours) {
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftOvertimeHours.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameOvertimeHours(p, salaryOvertimeHours)), getDoubleValue(p)));
			}
			setOtherNonWages(list);
		}
		public void setBaseSalary(BaseSalary salaryBaseSalary, BaseSalary draftBaseSalary) {
//			setBaseSalary(getDecorableAmount(getDoubleValue(baseSalary2), getDoubleValue(baseSalary3)));
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftBaseSalary.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameBaseSalary(p, salaryBaseSalary)), getDoubleValue(p)));
			}
			setBaseSalary(list);
		}
		
		public void setOtherNonWages(OtherNonWages salaryOtherNonWages, OtherNonWages draftOtherNonWages) {
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftOtherNonWages.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameOtherNonWage(p, salaryOtherNonWages)), getDoubleValue(p)));
			}
			setOtherNonWages(list);
		}
		public void setCompensationOrPrepaidExpenses(
				CompensationOrPrepaidExpenses salaryCompensationOrPrepaidExpenses,
				CompensationOrPrepaidExpenses draftCompensationOrPrepaidExpenses) {
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftCompensationOrPrepaidExpenses.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameCompensationOrPrepaidExpense(p, salaryCompensationOrPrepaidExpenses)), getDoubleValue(p)));
			}
			setCompensationOrPrepaidExpenses(list);
		}
//		public void setSalarySupplements(SalarySupplements salarySalarySupplements,
//				List<IPayment> draftSalarySupplements) {
		public void setSalarySupplements(SalarySupplements salarySalarySupplements,
				SalarySupplements draftSalarySupplements) {
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(IPayment p: draftSalarySupplements.getValues()){
				list.add(getDecorableAmount(getDoubleValue(getSameSalarySupplement(p, salarySalarySupplements)), getDoubleValue(p)));
			}
			setSalarySupplements(list);
		}
		
		
		private IPayment getSameBaseSalary(IPayment draftPayment, BaseSalary list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) 
						&& CommonUtil.round(draftPayment.getAmount()) == CommonUtil.round(p.getAmount())) {
					return p;
				}
			}
			return null;
		}
		private IPayment getSameOvertimeHours(IPayment draftPayment, OvertimeHours list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) 
						&& CommonUtil.round(draftPayment.getAmount()) == CommonUtil.round(p.getAmount())) {
					return p;
				}
			}
			return null;
		}
		private IPayment getSameSpecialBonuses(IPayment draftPayment, SpecialBonuses list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) 
						&& CommonUtil.round(draftPayment.getAmount()) == CommonUtil.round(p.getAmount())) {
					return p;
				}
			}
			return null;
		}
		private IPayment getSameOtherNonWage(IPayment draftPayment, OtherNonWages list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) 
						&& CommonUtil.round(draftPayment.getAmount()) == CommonUtil.round(p.getAmount())) {
					return p;
				}
			}
			return null;
		}
		private IPayment getSameCompensationOrPrepaidExpense(IPayment draftPayment, CompensationOrPrepaidExpenses list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) 
						&& CommonUtil.round(draftPayment.getAmount()) == CommonUtil.round(p.getAmount())) {
					return p;
				}
			}
			return null;
		}
		private IPayment getSameSalarySupplement(IPayment draftPayment, SalarySupplements list) {
			for (IPayment p : list.getValues()) {
				if (draftPayment.getDescription().equals(p.getDescription())
						&& CommonUtil.round(draftPayment.getAmount()) == CommonUtil.round(p.getAmount())) {
					return p;
				}
			}
			return null;
		}
		private IPayment getSameMovingCompensation(IPayment draftPayment, MovingCompensation list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) 
						&& CommonUtil.round(draftPayment.getAmount())==CommonUtil.round(p.getAmount())){
					return p;
				}
			}
			return null;
		}
		private IPayment getSameSpecialSecurityBenefits(IPayment draftPayment, SpecialSecurityBenefits list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) 
						&& CommonUtil.round(draftPayment.getAmount())==CommonUtil.round(p.getAmount())){
					return p;
				}
			}
			return null;
		}
		private IPayment getSameSalaryInKind(IPayment draftPayment, SalaryInKind list) {
			for(IPayment p: list.getValues()){
				if(draftPayment.getDescription().equals(p.getDescription()) 
						&& CommonUtil.round(draftPayment.getAmount())==CommonUtil.round(p.getAmount())){
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
		public DecorableAmount getDecorableAmount(Double salaryValue, Double draftValue){
			DecorableAmount DecorableAmount = new DecorableAmount();
			if(salaryValue==null && draftValue==null){
				DecorableAmount.setTextValue(BLANK_TEXT);
				DecorableAmount.setStyle(NORMAL_STYLE);
			} else if(salaryValue!=null && draftValue!=null){
				DecorableAmount.setDoubleValue(CommonUtil.round(salaryValue));
				DecorableAmount.setStyle(CommonUtil.round(salaryValue)==CommonUtil.round(draftValue)?NORMAL_STYLE:RED_STYLE);
			} else {
				if(salaryValue==null){
					DecorableAmount.setTextValue(NO_ELEMENT_TEXT);	
					DecorableAmount.setStyle(RED_STYLE);
				} else {
					DecorableAmount.setDoubleValue(CommonUtil.round(salaryValue));
					DecorableAmount.setStyle(NORMAL_STYLE);
				}
			}
			return DecorableAmount;
		}
	}
	public class Deductions{
		private DecorableAmount commonContingency;
		private DecorableAmount unemployment;
		private DecorableAmount jobTraining;
		private DecorableAmount structuralOvertime;
		private DecorableAmount nonStructuralOvertime;
		private DecorableAmount socialSecurityContributions;
		private DecorableAmount irpf;
		private DecorableAmount advancePayment;
		private DecorableAmount inKind;
		private DecorableAmount other;
		private DecorableAmount totalDeduction;
		
		public DecorableAmount getCommonContingency() {
			return commonContingency;
		}
		public void setCommonContingency(DecorableAmount commonContingency) {
			this.commonContingency = commonContingency;
		}
		public DecorableAmount getUnemployment() {
			return unemployment;
		}
		public void setUnemployment(DecorableAmount unemployment) {
			this.unemployment = unemployment;
		}
		public DecorableAmount getJobTraining() {
			return jobTraining;
		}
		public void setJobTraining(DecorableAmount jobTraining) {
			this.jobTraining = jobTraining;
		}
		public DecorableAmount getStructuralOvertime() {
			return structuralOvertime;
		}
		public void setStructuralOvertime(DecorableAmount structuralOvertime) {
			this.structuralOvertime = structuralOvertime;
		}
		public DecorableAmount getNonStructuralOvertime() {
			return nonStructuralOvertime;
		}
		public void setNonStructuralOvertime(DecorableAmount nonStructuralOvertime) {
			this.nonStructuralOvertime = nonStructuralOvertime;
		}
		public DecorableAmount getSocialSecurityContributions() {
			return socialSecurityContributions;
		}
		public void setSocialSecurityContributions(DecorableAmount socialSecurityContributions) {
			this.socialSecurityContributions = socialSecurityContributions;
		}
		public DecorableAmount getIrpf() {
			return irpf;
		}
		public void setIrpf(DecorableAmount irpf) {
			this.irpf = irpf;
		}
		public DecorableAmount getAdvancePayment() {
			return advancePayment;
		}
		public void setAdvancePayment(DecorableAmount advancePayment) {
			this.advancePayment = advancePayment;
		}
		public DecorableAmount getInKind() {
			return inKind;
		}
		public void setInKind(DecorableAmount inKind) {
			this.inKind = inKind;
		}
		public DecorableAmount getOther() {
			return other;
		}
		public void setOther(DecorableAmount other) {
			this.other = other;
		}
		public DecorableAmount getTotalDeduction() {
			return totalDeduction;
		}
		public void setTotalDeduction(DecorableAmount totalDeduction) {
			this.totalDeduction = totalDeduction;
		}
		
		public void setTotalDeduction(Double totalDeduction2,
				Double totalDeduction3) {
			setTotalDeduction(getDecorableAmount(totalDeduction2, totalDeduction3));
		}
		public void setOther(IDeduction other2, IDeduction other3) {
			setOther(getDecorableAmount(getDoubleValue(other2), getDoubleValue(other3)));
		}
		public void setInKind(IDeduction inKind2, IDeduction inKind3) {
			setInKind(getDecorableAmount(getDoubleValue(inKind2), getDoubleValue(inKind3)));
		}
		public void setAdvancePayment(IDeduction advancePayment2,
				IDeduction advancePayment3) {
			setAdvancePayment(getDecorableAmount(getDoubleValue(advancePayment2), getDoubleValue(advancePayment3)));
		}
		public void setIrpf(IDeduction irpf2, IDeduction irpf3) {
			setIrpf(getDecorableAmount(getDoubleValue(irpf2), getDoubleValue(irpf3)));
		}
		public void setSocialSecurityContributions(
				Double socialSecurityContributions2,
				Double socialSecurityContributions3) {
			setSocialSecurityContributions(getDecorableAmount(socialSecurityContributions2, socialSecurityContributions3));
		}
		public void setNonStructuralOvertime(IDeduction nonStructuralOvertime2,
				IDeduction nonStructuralOvertime3) {
			setNonStructuralOvertime(getDecorableAmount(getDoubleValue(nonStructuralOvertime2), getDoubleValue(nonStructuralOvertime3)));
		}
		public void setStructuralOvertime(IDeduction structuralOvertime2,
				IDeduction structuralOvertime3) {
			setStructuralOvertime(getDecorableAmount(getDoubleValue(structuralOvertime2), getDoubleValue(structuralOvertime3)));
		}
		public void setJobTraining(IDeduction jobTraining2,
				IDeduction jobTraining3) {
			setJobTraining(getDecorableAmount(getDoubleValue(jobTraining2), getDoubleValue(jobTraining3)));
		}
		public void setUnemployment(IDeduction unemployment2,
				IDeduction unemployment3) {
			setUnemployment(getDecorableAmount(getDoubleValue(unemployment2), getDoubleValue(unemployment3)));
		}
		public void setCommonContingency(IDeduction commonContingency2,
				IDeduction commonContingency3) {
			setCommonContingency(getDecorableAmount(getDoubleValue(commonContingency2), getDoubleValue(commonContingency3)));
		}
		
		private Double getDoubleValue(IDeduction deduction) {
			if(deduction==null){
				return null;
			}
			return deduction.getAmount();
		}
		public DecorableAmount getDecorableAmount(Double salaryValue, Double draftValue){
			DecorableAmount DecorableAmount = new DecorableAmount();
			if(salaryValue==null && draftValue==null){
				DecorableAmount.setTextValue(BLANK_TEXT);
				DecorableAmount.setStyle(NORMAL_STYLE);
			} else if(salaryValue!=null && draftValue!=null){
				DecorableAmount.setDoubleValue(CommonUtil.round(salaryValue));
				DecorableAmount.setStyle(CommonUtil.round(salaryValue)==CommonUtil.round(draftValue)?NORMAL_STYLE:RED_STYLE);
			} else {
				if(salaryValue==null){
					DecorableAmount.setTextValue(NO_ELEMENT_TEXT);	
					DecorableAmount.setStyle(RED_STYLE);
				} else {
					DecorableAmount.setDoubleValue(CommonUtil.round(salaryValue));
					DecorableAmount.setStyle(NORMAL_STYLE);
				}
			}
			return DecorableAmount;
		}
		
	}
	public class Bases{
		private DecorableAmount totalLiquid;
		private DecorableAmount remuneration;
		private DecorableAmount extraPayProration;
		private DecorableAmount commonBase;
		private DecorableAmount professionalBase;
		private DecorableAmount overtimeBase;
		private DecorableAmount irpfBase;
	
		public DecorableAmount getTotalLiquid() {
			return totalLiquid;
		}
		public void setTotalLiquid(DecorableAmount totalLiquid) {
			this.totalLiquid = totalLiquid;
		}
		public DecorableAmount getRemuneration() {
			return remuneration;
		}
		public void setRemuneration(DecorableAmount remuneration) {
			this.remuneration = remuneration;
		}
		public DecorableAmount getExtraPayProration() {
			return extraPayProration;
		}
		public void setExtraPayProration(DecorableAmount extraPayProration) {
			this.extraPayProration = extraPayProration;
		}
		public DecorableAmount getCommonBase() {
			return commonBase;
		}
		public void setCommonBase(DecorableAmount commonBase) {
			this.commonBase = commonBase;
		}
		public DecorableAmount getProfessionalBase() {
			return professionalBase;
		}
		public void setProfessionalBase(DecorableAmount professionalBase) {
			this.professionalBase = professionalBase;
		}
		public DecorableAmount getOvertimeBase() {
			return overtimeBase;
		}
		public void setOvertimeBase(DecorableAmount overtimeBase) {
			this.overtimeBase = overtimeBase;
		}
		public DecorableAmount getIrpfBase() {
			return irpfBase;
		}
		public void setIrpfBase(DecorableAmount irpfBase) {
			this.irpfBase = irpfBase;
		}
		
		public void setIrpfBase(Double irpfBase2, Double irpfBase3) {
			setIrpfBase(getDecorableAmount(irpfBase2, irpfBase3));
		}
		public void setOvertimeBase(Double overtimeBase2, Double overtimeBase3) {
			setOvertimeBase(getDecorableAmount(overtimeBase2, overtimeBase3));
		}
		public void setProfessionalBase(Double professionalBase2,
				Double professionalBase3) {
			setProfessionalBase(getDecorableAmount(professionalBase2, professionalBase3));
		}
		public void setCommonBase(Double commonBase2, Double commonBase3) {
			setCommonBase(getDecorableAmount(commonBase2, commonBase3));
		}
		public void setExtraPayProration(Double extraPayProration2,
				Double extraPayProration3) {
			setExtraPayProration(getDecorableAmount(extraPayProration2, extraPayProration3));
		}
		public void setRemuneration(Double remuneration2, Double remuneration3) {
			setRemuneration(getDecorableAmount(remuneration2, remuneration3));
		}
		public void setTotalLiquid(Double totalLiquid2, Double totalLiquid3) {
			setTotalLiquid(getDecorableAmount(totalLiquid2, totalLiquid3));
		}

		public DecorableAmount getDecorableAmount(Double salaryValue, Double draftValue){
			DecorableAmount DecorableAmount = new DecorableAmount();
			if(salaryValue==null && draftValue==null){
				DecorableAmount.setTextValue(BLANK_TEXT);
				DecorableAmount.setStyle(NORMAL_STYLE);
			} else if(salaryValue!=null && draftValue!=null){
				DecorableAmount.setDoubleValue(CommonUtil.round(salaryValue));
				DecorableAmount.setStyle(CommonUtil.round(salaryValue)==CommonUtil.round(draftValue)?NORMAL_STYLE:RED_STYLE);
			} else {
				if(salaryValue==null){
					DecorableAmount.setTextValue(NO_ELEMENT_TEXT);	
					DecorableAmount.setStyle(RED_STYLE);
				} else {
					DecorableAmount.setDoubleValue(CommonUtil.round(salaryValue));
					DecorableAmount.setStyle(NORMAL_STYLE);
				}
			}
			return DecorableAmount;
		}
	}
	
	public class Bonus {
		private List<DecorableAmount> amounts;
		private DecorableAmount totalAmount;
		
		public DecorableAmount getTotalAmount() {
			return totalAmount;
		}
		public void setTotalAmount(DecorableAmount totalAmount) {
			this.totalAmount = totalAmount;
		}
		public List<DecorableAmount> getAmounts() {
			return amounts;
		}
		public void setAmounts(List<DecorableAmount> amounts) {
			this.amounts = amounts;
		}
		
		public void setAmounts(List<ITransferObject> salaryBonus, List<ITransferObject> draftBonus) {
			Double salaryTotal = new Double(0.0);
			Double draftTotal = new Double(0.0);
			List<DecorableAmount> list = new LinkedList<DecorableAmount>();
			for(ITransferObject to: draftBonus){
				ContractBonus b = (ContractBonus) to;
				SalaryBonus sb = getSameSalaryBonus(b, salaryBonus);
				list.add(getDecorableAmount(sb==null?null:sb.getAmount(), (b==null||b.getExpression()==null)?null:Double.valueOf(b.getExpression())));
				salaryTotal += sb==null?0.0:sb.getAmount();
				draftTotal += (b==null||b.getExpression()==null)?0.0:Double.valueOf(b.getExpression());
			}
			setTotalAmount(getDecorableAmount(salaryTotal, draftTotal));
			setAmounts(list);
		}
		private SalaryBonus getSameSalaryBonus(ITransferObject contractBonus, List<ITransferObject> salaryBonus) {
			ContractBonus cb = (ContractBonus) contractBonus;
			for(ITransferObject to: salaryBonus){
				SalaryBonus sb = (SalaryBonus) to;
				if(cb.getDescription().equals(sb.getDescription()) 
						&& CommonUtil.round(Double.valueOf(cb.getExpression())) == CommonUtil.round(sb.getAmount())) {
					return sb;
				}
			}
			return null;
		}
		
		public DecorableAmount getDecorableAmount(Double salaryValue, Double draftValue){
			DecorableAmount DecorableAmount = new DecorableAmount();
			if(salaryValue==null && draftValue==null){
				DecorableAmount.setTextValue(BLANK_TEXT);
				DecorableAmount.setStyle(NORMAL_STYLE);
			} else if(salaryValue!=null && draftValue!=null){
				DecorableAmount.setDoubleValue(CommonUtil.round(salaryValue));
				DecorableAmount.setStyle(CommonUtil.round(salaryValue)==CommonUtil.round(draftValue)?NORMAL_STYLE:RED_STYLE);
			} else {
				if(salaryValue==null){
					DecorableAmount.setTextValue(NO_ELEMENT_TEXT);	
					DecorableAmount.setStyle(RED_STYLE);
				} else {
					DecorableAmount.setDoubleValue(CommonUtil.round(salaryValue));
					DecorableAmount.setStyle(NORMAL_STYLE);
				}
			}
			return DecorableAmount;
		}
	}
	
	public class DecorableAmount {
		private String style;
		private String textValue;
		private Double doubleValue;
		
		public String getStyle() {
			return style;
		}
		public void setStyle(String style) {
			this.style = style;
		}
		public String getTextValue() {
			return textValue;
		}
		public void setTextValue(String textValue) {
			this.textValue = textValue;
		}
		public Double getDoubleValue() {
			return doubleValue;
		}
		public void setDoubleValue(Double doubleValue) {
			this.doubleValue = doubleValue;
		}
		public boolean isShowAmount(){
			return getDoubleValue()!=null;
		}
	}
	
}