package com.esferalia.aon.payroll;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
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

	private static final long serialVersionUID = 1L;

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

	public static void main(String[] args) throws SecurityException,
			NoSuchMethodException, IntrospectionException {
		Method type = SalaryPayment.class.getMethod("getType");
		System.out.println(IResourceable.class.isAssignableFrom(type
				.getReturnType()));
		PropertyDescriptor descriptors[] = Introspector.getBeanInfo(
				SalaryPayment.class).getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : descriptors) {
			if (propertyDescriptor.getName().equals("type"))
				System.out.println(propertyDescriptor.getPropertyType());
		}

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
		IManagerBean bean = BeanManager.getManagerBean(SalaryData.class);
		Criteria c = new Criteria();
		c.addEqualExpression(
				bean.getFieldName(IEntityAlias.SALARY_DATA_SALARY_ID), getSalary().getId());
		c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_NAME),
				String.format("%d_%s", getId(), name ));
		List<?> list = bean.getList(c);
		if (list == null || list.size() == 0)
			return null;
		return ((SalaryData) list.get(0)).getExpression();
	}

}
