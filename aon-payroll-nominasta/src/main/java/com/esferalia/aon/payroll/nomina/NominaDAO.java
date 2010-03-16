package com.esferalia.aon.payroll.nomina;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.Nomina;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.calc.INominaDAO;
import com.esferalia.aon.payroll.core.calc.NominaDAOFactory;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.impl.calc.NominaParams;

public class NominaDAO implements INominaDAO {
	
	private static String EMP_ALIAS;
	private static String MES_ALIAS;
	private static String YEAR_ALIAS;
	private static String TIPO_ALIAS;
	
	static {
		NominaDAOFactory.setNominaDAO( new NominaDAO() );
		
		try {
			IManagerBean nominaBean = BeanManager.getManagerBean(Nomina.class);
			EMP_ALIAS = nominaBean.getFieldName(IPayrollAlias.NOMINA_EMPLEADO_ID);
			MES_ALIAS = nominaBean.getFieldName(IPayrollAlias.NOMINA_MES);
			YEAR_ALIAS = nominaBean.getFieldName(IPayrollAlias.NOMINA_YEAR);
			TIPO_ALIAS = nominaBean.getFieldName(IPayrollAlias.NOMINA_TIPO_NOMINA);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public INomina getNomina(NominaParams params) throws PayrollException {
		try {
			IManagerBean nominaBean = BeanManager.getManagerBean(Nomina.class);
			Criteria c = new Criteria();
			if (params.getEmpleado().getId() != null) {
				c.addEqualExpression(EMP_ALIAS, params.getEmpleado().getId());
			}
			if (params.getMes() != null) {
				c.addEqualExpression(MES_ALIAS, params.getMes());
			}
			if (params.getYear() != null) {
				c.addEqualExpression(YEAR_ALIAS, params.getYear());
			}
			if (params.getTipo() == TipoNomina.NORMAL) {
				c.addEqualExpression(TIPO_ALIAS, "N");	
			}
			if (params.getTipo() == TipoNomina.ATRASO) {
				c.addEqualExpression(TIPO_ALIAS, "A");	
			}

			System.out.println( "**********************" );
			System.out.println( params.toString() );
			System.out.println( "**********************" );
			
			List<ITransferObject> list = nominaBean.getList(c,0,1);
			if (list.size() > 0) {
				return (INomina) list.get(0);  
			}
			return null;
		} catch (ManagerBeanException e) {
			throw new PayrollException( e ); 
		} 
	}

}
