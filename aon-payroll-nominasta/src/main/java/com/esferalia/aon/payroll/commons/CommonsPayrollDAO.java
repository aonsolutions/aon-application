package com.esferalia.aon.payroll.commons;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.TipoBonificacion;
import com.esferalia.aon.payroll.core.commons.CommonsPayrollDAOFactory;
import com.esferalia.aon.payroll.core.commons.ICommonsPayrollDAO;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;

public class CommonsPayrollDAO implements ICommonsPayrollDAO {

	static {
		CommonsPayrollDAOFactory.register(new CommonsPayrollDAO());
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
