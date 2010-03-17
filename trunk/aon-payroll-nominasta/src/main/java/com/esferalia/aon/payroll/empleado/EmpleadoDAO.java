package com.esferalia.aon.payroll.empleado;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class EmpleadoDAO implements IEmpleadoDAO {

	private static String DOC_ALIAS = null;
	private static String NSS_ALIAS = null;
	private static String NOMBRE_ALIAS = null;
	private static String APEL_ALIAS = null;
	private static String APEL2_ALIAS = null;
	private static String EMPR_ALIAS = null;

	static {
		EmpleadoDAOFactory.register(new EmpleadoDAO());

		try {
			IManagerBean bean = BeanManager.getManagerBean(Empleado.class);
			DOC_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_REGISTRY_DOCUMENT);
			NSS_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_NUM_SS);
			NOMBRE_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_NAME);
			APEL_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_SURNAME);
			APEL2_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_LAST_NAME);
			EMPR_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_EMPRESA_NAME);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<?> getEmpleados(EmpleadoParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Empleado.class);
			return bean.getList(getCriteria(params));
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}

	}

	@Override
	public Criteria getCriteria(EmpleadoParams params) throws PayrollException {
		try {
			Criteria c = new Criteria();
			if (params.getDocumento() != null) {
				c.addExpression(DOC_ALIAS, params.getDocumento());
			}
			if (params.getNumSS() != null) {
				c.addExpression(NSS_ALIAS, params.getNumSS());
			}
			if (params.getNombre() != null) {
				c.addExpression(NOMBRE_ALIAS, params.getNombre());
			}
			if (params.getApellido() != null) {
				c.addExpression(APEL_ALIAS, params.getApellido());
			}
			if (params.getApellido2() != null) {
				c.addExpression(APEL2_ALIAS, params.getApellido2());
			}
			if (params.getEmpresa() != null) {
				c.addExpression(EMPR_ALIAS, params.getEmpresa());
			}
			return c;
		} catch (ExpressionException e) {
			throw new PayrollException(e);
		}
	}

}
