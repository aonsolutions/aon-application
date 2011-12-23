package com.esferalia.aon.payroll;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.enumeration.IResourceable;
import com.esferalia.aon.entity.master.SalaryPaymentDB;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.payment.IPayment;

@Entity
@Table(name="salary_payment")
public class SalaryPayment extends SalaryPaymentDB implements IPayment, IExpression {
	
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
	
	
	public static void main(String[] args) throws SecurityException, NoSuchMethodException, IntrospectionException {
		Method type = SalaryPayment.class.getMethod("getType");
		System.out.println ( IResourceable.class.isAssignableFrom(type.getReturnType()) );
		PropertyDescriptor descriptors [] = 
			Introspector.getBeanInfo(SalaryPayment.class).getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : descriptors) {
			if ( propertyDescriptor.getName().equals("type") ) 
				System.out.println ( propertyDescriptor.getPropertyType() );
		}
		
	}
}
