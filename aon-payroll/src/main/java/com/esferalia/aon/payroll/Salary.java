package com.esferalia.aon.payroll;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.annotations.Formula;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.SalaryDB;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.bonus.Bonuses;
import com.esferalia.aon.salary.bonus.BonusesFactoryContext;
import com.esferalia.aon.salary.bonus.BonusesFactoryManager;
import com.esferalia.aon.salary.bonus.IBonusesFactory;
import com.esferalia.aon.salary.bonus.IBonusesFactoryContext;
import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.cost.CostsFactoryContext;
import com.esferalia.aon.salary.cost.CostsFactoryManager;
import com.esferalia.aon.salary.cost.ICostsFactory;
import com.esferalia.aon.salary.cost.ICostsFactoryContext;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.DeductionsFactoryContext;
import com.esferalia.aon.salary.deduction.DeductionsFactoryManager;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.deduction.IDeductionsFactoryContext;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.IPaymentsFactoryContext;
import com.esferalia.aon.salary.payment.Payments;
import com.esferalia.aon.salary.payment.PaymentsFactoryContext;
import com.esferalia.aon.salary.payment.PaymentsFactoryManager;

@Entity
@Table(name = "salary")
public class Salary extends SalaryDB implements ISalary, ISalaryProxy {

	static {
		PaymentsFactoryManager payManager = PaymentsFactoryManager
				.getInstance();
		payManager.addFactory(new SalaryPaymentsFactory());
		DeductionsFactoryManager dedManager = DeductionsFactoryManager
				.getInstance();
		dedManager.addFactory(new SalaryDeductionsFactory());
		BonusesFactoryManager bonusManager = BonusesFactoryManager
				.getInstance();
		bonusManager.addFactory(new SalaryBonusesFactory());
		CostsFactoryManager costManager = CostsFactoryManager
				.getInstance();
		costManager.addFactory(new SalaryCostsFactory());
	}

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private int issueMonth;
	private int issueYear;
	private Month month;
	private Integer year;

	private Set<SalaryData> salaryDatas = new HashSet<SalaryData>();
	private Set<SalaryCost> salaryCosts = new HashSet<SalaryCost>();
	private Set<SalaryBonus> salaryBonus = new HashSet<SalaryBonus>();
	private Set<SalaryEmbargo> salaryEmbargos = new HashSet<SalaryEmbargo>();
	private Set<SalaryPayment> salaryPayments = new HashSet<SalaryPayment>();
	private Set<SalaryDeduction> salaryDeductions = new HashSet<SalaryDeduction>();
	// OTHERS
	private IDeductionsFactoryContext dedContext;
	private IPaymentsFactoryContext payContext;
	private IBonusesFactoryContext bonusContext;
	private ICostsFactoryContext costContext;
	@Transient
	private Payments payments;
	@Transient
	private Deductions deductions;
	@Transient
	private Bonuses bonuses;
	@Transient
	private Costs costs;

	@Formula("month(issue_date)")
	public int getIssueMonth() {
		return issueMonth;
	}

	public void setIssueMonth(int issueMonth) {
		this.issueMonth = issueMonth;
	}

	@Transient
	public Month getMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getEndDate());
		month = Month.getMonthByValue(cal.get(Calendar.MONTH));
		return month;
	}

	@Transient
	public Integer getYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getEndDate());
		year = cal.get(Calendar.YEAR);
		return year;
	}

	@Formula("year(issue_date)")
	public int getIssueYear() {
		return issueYear;
	}

	public void setIssueYear(int issueYear) {
		this.issueYear = issueYear;
	}

	@Override
	@Transient
	public boolean isFullTime() {
		// TODO Identificar cuando la nomina es por dias u horas
		return true;
	}

	@Transient
	public int getSeniority() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getSeniorityDate());
		int seniorityYear = cal.get(Calendar.YEAR);
		return getYear() - seniorityYear;
	}
	
	@Override
	@Transient
	public Double getInKindIrpfBase() {
		return super.getInkindIrpfBase();
	}
	
	// *******************************************************
	// ********************** DATA ***************************
	// *******************************************************
	@Transient
	public String getSalaryData(String name) {
		for (SalaryData salaryData : salaryDatas) {
			if ( name.equals(salaryData.getName()) )
				return salaryData.getExpression();
		}
		return null;
	}

	@Transient
	public <T> T getSalaryData(String name, Class<T> type) {
		for (SalaryData salaryData : salaryDatas) {
			if ( name.equals(salaryData.getName()) )
				return ExpressionContext.eval(salaryData.getExpression(), type);
		}
		return null;
	}

	@OneToMany(mappedBy = "salary", cascade = { CascadeType.ALL })
	public Set<SalaryData> getSalaryDatas() {
		return salaryDatas;
	}

	public void setSalaryDatas(Set<SalaryData> salaryDatas) {
		this.salaryDatas = salaryDatas;
	}

	// *******************************************************
	// ********************* COSTOS **************************
	// *******************************************************
	@OneToMany(mappedBy = "salary", cascade = { CascadeType.ALL })
	public Set<SalaryCost> getSalaryCosts() {
		return salaryCosts;
	}

	public void setSalaryCosts(Set<SalaryCost> salaryCosts) {
		this.salaryCosts = salaryCosts;
	}

	// *******************************************************
	// ******************** EMBARGOS *************************
	// *******************************************************
	@OneToMany(mappedBy = "salary", cascade = { CascadeType.ALL })
	public Set<SalaryEmbargo> getSalaryEmbargos() {
		return salaryEmbargos;
	}

	public void setSalaryEmbargos(Set<SalaryEmbargo> salaryEmbargos) {
		this.salaryEmbargos = salaryEmbargos;
	}

	// *******************************************************
	// **************** BONIFICACIONES **********************
	// *******************************************************
	@OneToMany(mappedBy = "salary", cascade = { CascadeType.ALL })
	public Set<SalaryBonus> getSalaryBonus() {
		return salaryBonus;
	}

	public void setSalaryBonus(Set<SalaryBonus> salaryBonus) {
		this.salaryBonus = salaryBonus;
	}

	// *******************************************************
	// ****************** DEVENGOS ***************************
	// *******************************************************

	@OneToMany(mappedBy = "salary", cascade = { CascadeType.ALL })
	public Set<SalaryPayment> getSalaryPayments() {
		return salaryPayments;
	}

	public void setSalaryPayments(Set<SalaryPayment> salaryPayments) {
		this.salaryPayments = salaryPayments;
	}

	// *******************************************************
	// ****************** DEDUCCIONES ************************
	// *******************************************************

	@OneToMany(mappedBy = "salary", cascade = { CascadeType.ALL })
	public Set<SalaryDeduction> getSalaryDeductions() {
		return salaryDeductions;
	}

	public void setSalaryDeductions(Set<SalaryDeduction> salaryDeductions) {
		this.salaryDeductions = salaryDeductions;
	}

	@Override
	@Transient
	public ISalary getSalary() {
		return this;
	}

	@Transient
	@Override
	public Deductions getDeductions() throws SalaryException {
		if (deductions == null) {
			DeductionsFactoryManager manager = DeductionsFactoryManager
					.getInstance();
			IDeductionsFactory factory = manager
					.getFactory(getDeductionsFactoryContext());
			setDeductions(factory.getDeductions(getDeductionsFactoryContext()));
		}
		return deductions;
	}

	public void setDeductions(Deductions deductions) throws SalaryException {
		this.deductions = deductions;
	}

	// *******************************************************
	// **************** BONIFICACIONES ***********************
	// *******************************************************
	
	@Transient
	public Bonuses getBonuses() throws SalaryException {
		if (bonuses == null) {
			BonusesFactoryManager manager = BonusesFactoryManager
					.getInstance();
			IBonusesFactory factory = manager
					.getFactory(getBonusesFactoryContext());
			setBonuses(factory.getBonuses(getBonusesFactoryContext()));
		}
		return bonuses;
	}

	public void setBonuses(Bonuses bonuses) throws SalaryException {
		this.bonuses = bonuses;
	}
	
	// *******************************************************
	// ******************** COSTES ***************************
	// *******************************************************

	@Transient
	@Override
	public Costs getEnterpriseCosts() throws SalaryException {
		if (costs == null) {
			CostsFactoryManager manager = CostsFactoryManager
					.getInstance();
			ICostsFactory factory = manager
					.getFactory(getCostsFactoryContext());
			setEnterpriseCosts(factory.getCosts(getCostsFactoryContext()));
		}
		return costs;
	}
	
	public void setEnterpriseCosts(Costs costs) throws SalaryException {
		this.costs = costs;
	}

	@SuppressWarnings("unchecked")
	@Transient
	public Collection<SalaryCost> getCosts() throws SalaryException {
		try {
			Collection<SalaryCost> costs;
			String sessionName = HibernateUtil
					.getSessionFactoryName(Salary.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			// Si el Salary está conectado a la session de Hibernate utilizamos
			// la potencia
			// que nos da la obtención de colecciones tipo LAZY. En caso
			// contrario vamos por
			// el FrameWork.
			if (session.contains(this) || this.getId() == null) {
				costs = this.getSalaryCosts();
			} else {
				IManagerBean bean = BeanManager
						.getManagerBean(SalaryCost.class);
				Criteria c = new Criteria();
				c.addEqualExpression(
						bean.getFieldName(IEntityAlias.SALARY_COST_SALARY_ID),
						this.getId());
				List<?> list = bean.getList(c);
				costs = (Collection<SalaryCost>) list;
			}
			return costs;
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Transient
	public Collection<SalaryBonus> getBonus() throws SalaryException {
		try {
			Collection<SalaryBonus> bonus;
			String sessionName = HibernateUtil
					.getSessionFactoryName(Salary.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			// Si el Salary está conectado a la session de Hibernate utilizamos
			// la potencia
			// que nos da la obtención de colecciones tipo LAZY. En caso
			// contrario vamos por
			// el FrameWork.
			if (session.contains(this) || this.getId() == null) {
				bonus = this.getSalaryBonus();
			} else {
				IManagerBean bean = BeanManager
						.getManagerBean(SalaryBonus.class);
				Criteria c = new Criteria();
				c.addEqualExpression(
						bean.getFieldName(IEntityAlias.SALARY_BONUS_SALARY_ID),
						this.getId());
				List<?> list = bean.getList(c);
				bonus = (Collection<SalaryBonus>) list;
			}
			return bonus;
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	@Transient
	@Override
	public Collection<SalaryPayment> getPaymentS() throws SalaryException {
		try {
			Collection<SalaryPayment> payments;
			String sessionName = HibernateUtil
					.getSessionFactoryName(Salary.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			if (session.contains(this) || this.getId() == null) {
				payments = this.getSalaryPayments();
			} else {
				IManagerBean bean = BeanManager
						.getManagerBean(SalaryPayment.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean
						.getFieldName(IEntityAlias.SALARY_PAYMENT_SALARY_ID),
						this.getId());
				List<?> list = bean.getList(c);
				payments = (Collection<SalaryPayment>) list;
			}
			return payments;
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	@Transient
	@Override
	public Collection<SalaryDeduction> getDeductionS() throws SalaryException {
		try {
			Collection<SalaryDeduction> deductions;
			String sessionName = HibernateUtil
					.getSessionFactoryName(Salary.class.getName());
			Session session = HibernateUtil.getSession(sessionName);
			if (session.contains(this) || this.getId() == null) {
				deductions = this.getSalaryDeductions();
			} else {
				IManagerBean bean = BeanManager
						.getManagerBean(SalaryDeduction.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean
						.getFieldName(IEntityAlias.SALARY_DEDUCTION_SALARY_ID),
						this.getId());
				List<?> list = bean.getList(c);
				deductions = (Collection<SalaryDeduction>) list;
			}
			return deductions;
		} catch (ManagerBeanException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	@Transient
	@Override
	public Payments getPayments() throws SalaryException {
		if (payments == null) {
			PaymentsFactoryManager manager = PaymentsFactoryManager
					.getInstance();
			IPaymentsFactory factory = manager
					.getFactory(getPaymentsFactoryContext());
			setPayments(factory.getPayments(getPaymentsFactoryContext()));
		}
		return payments;
	}

	public void setPayments(Payments payments) throws SalaryException {
		this.payments = payments;
	}

	@Transient
	public IDeductionsFactoryContext getDeductionsFactoryContext() {
		if (dedContext == null) {
			DeductionsFactoryContext dfc = new DeductionsFactoryContext();
			dfc.setSalaryProxy(this);
			dedContext = dfc;
		}
		return dedContext;
	}

	@Transient
	public IPaymentsFactoryContext getPaymentsFactoryContext() {
		if (payContext == null) {
			PaymentsFactoryContext pfc = new PaymentsFactoryContext();
			pfc.setSalaryProxy(this);
			payContext = pfc;
		}
		return payContext;
	}
	
	@Transient
	public IBonusesFactoryContext getBonusesFactoryContext() {
		if (bonusContext == null) {
			BonusesFactoryContext cfc = new BonusesFactoryContext();
			cfc.setSalaryProxy(this);
			bonusContext = cfc;
		}
		return bonusContext;
	}

	@Transient
	public ICostsFactoryContext getCostsFactoryContext() {
		if (costContext == null) {
			CostsFactoryContext cfc = new CostsFactoryContext();
			cfc.setSalaryProxy(this);
			costContext = cfc;
		}
		return costContext;
	}
}
