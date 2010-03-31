package com.esferalia.aon.payroll.commons;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.TipoBonificacion;
import com.esferalia.aon.payroll.core.CommonsPayrollDAOFactory;
import com.esferalia.aon.payroll.core.ICommonsPayrollDAO;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;

public class CommonsPayrollDAO implements ICommonsPayrollDAO {

//	private static String PER_ALIAS = null;

	static {
		CommonsPayrollDAOFactory.register(new CommonsPayrollDAO());

		try {
			IManagerBean bean = BeanManager.getManagerBean(TipoBonificacion.class);
//			PER_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_ID);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITipoBonificacion> getTiposBonificacion()
			throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(TipoBonificacion.class);
			List<?> list = bean.getList(null);
			return (List<ITipoBonificacion>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
}
