package com.esferalia.aon.payroll.test;

import java.util.List;

import junit.framework.TestCase;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.ActividadCCC;
import com.esferalia.aon.payroll.dao.IPayrollAlias;


public class ActividadCCCTest extends TestCase{

	public void testEmpresa() throws Exception {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActividadCCC.class);
			Criteria criteria = new Criteria();
			String ACTCCC_ACT_ALIAS = bean.getFieldName(IPayrollAlias.ACTIVIDAD_CCC_ID_CDG);
			String ACTCCC_CCC_ALIAS = bean.getFieldName(IPayrollAlias.ACTIVIDAD_CCC_ID_TIPCCC);
			criteria.addEqualExpression(ACTCCC_ACT_ALIAS, 762 );
			String c = "P";
			criteria.addEqualExpression(ACTCCC_CCC_ALIAS, c );
			List<ITransferObject> list = bean.getList( null,0,10 );
			System.out.println( list.get(0) );
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
