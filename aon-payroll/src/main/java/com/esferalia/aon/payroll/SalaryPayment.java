package com.esferalia.aon.payroll;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.SalaryPaymentDB;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.payment.IPayment;

@Entity
@Table(name = "salary_payment")
public class SalaryPayment extends SalaryPaymentDB implements IPayment,
		IExpression {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	
	@Override
	@Transient
	public String getName() {
		return getPaymentConcept();
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.SALARY;
	}

	@Override
	@Transient
	public boolean isReadOnly() {
		return true;
	}


	// TODO
	private PaymentType paymentType;

	@Transient
	public PaymentType getPaymentType() {
		if (this.getType() != null) {
			paymentType = this.getType();
		}
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
		this.setType(paymentType);
	}

	@Transient
	public Double getUnits() throws ManagerBeanException {
		String units = getData("UNITS");
		return units == null ? null : Double.parseDouble(units);
	}

	@Transient
	public Double getUnitAmount() throws ManagerBeanException {
		String unitAmount = getData("UNIT_AMOUNT");
		return unitAmount == null ? null : Double.parseDouble(unitAmount);
	}

	private String getData(String name) throws ManagerBeanException {
		Salary salary = getSalary();
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		if (  session.contains(salary)  || salary.getId() == null ) {
			return null;
		} 
		else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryData.class);
			Criteria c = new Criteria();
			c.addEqualExpression(
					bean.getFieldName(IEntityAlias.SALARY_DATA_SALARY_ID), salary.getId());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_NAME),
					String.format("%d_%s", getId(), name ));
			List<?> list = bean.getList(c);
			if (list == null || list.size() == 0)
				return null;
			return ((SalaryData) list.get(0)).getExpression();
		}
	}
	

}
