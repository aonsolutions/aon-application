package com.esferalia.aon.payroll.test;

import junit.framework.TestCase;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.ParteConfirmacionIT;
import com.esferalia.aon.payroll.dao.IPayrollAlias;


public class ParteConfirmacionITTest extends TestCase{

	public void testParteIT() throws Exception {
		try {
			AonPayroll.configure();
			IManagerBean bean = BeanManager.getManagerBean(ParteConfirmacionIT.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName( IPayrollAlias.PARTE_CONFIRMACION_IT_PROCESADO_BD);
			criteria.addEqualExpression(alias, "N");
			bean.getList( criteria,0,10 );
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
