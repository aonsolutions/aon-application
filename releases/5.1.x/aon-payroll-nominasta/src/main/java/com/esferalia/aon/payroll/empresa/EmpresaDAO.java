package com.esferalia.aon.payroll.empresa;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.ActividadCCC;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IActividadCCC;
import com.esferalia.aon.payroll.core.empresa.EmpresaDAOFactory;
import com.esferalia.aon.payroll.core.empresa.IEmpresaDAO;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class EmpresaDAO implements IEmpresaDAO {

	private static String ACTCCC_ACT_ALIAS = null;
	private static String ACTCCC_CCC_ALIAS = null;

	static {
		EmpresaDAOFactory.register(new EmpresaDAO());
	}
	
	@Override
	public void configure() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActividadCCC.class);
			ACTCCC_ACT_ALIAS = bean.getFieldName(IPayrollAlias.ACTIVIDAD_CCC_ID_CDG);
			ACTCCC_CCC_ALIAS = bean.getFieldName(IPayrollAlias.ACTIVIDAD_CCC_ID_TIPCCC);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IActividadCCC getActividadCCC(IActividad actividad, CuentaCotizacion ccc) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActividadCCC.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(ACTCCC_ACT_ALIAS, actividad.getId());
			String c = null;
			if (ccc == CuentaCotizacion.PRINCIPAL) {
				c = "P";
			} else if (ccc == CuentaCotizacion.ALTO_CARGO) {
				c = "A";
			} else if (ccc == CuentaCotizacion.APRENDIZ) {
				c = "R";
			} else if (ccc == CuentaCotizacion.ASIMILADO) {
				c = "S";
			} 
			criteria.addEqualExpression(ACTCCC_CCC_ALIAS, c );
			List<?> list = bean.getList(criteria);
			if (list.size() > 0) {
				return (IActividadCCC) list.get(0);	
			}
			return null;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
}
