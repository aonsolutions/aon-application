package com.esferalia.aon.payroll.nomina;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.Finiquito;
import com.esferalia.aon.payroll.FiniquitoDiferencia;
import com.esferalia.aon.payroll.Nomina;
import com.esferalia.aon.payroll.NominaDiferencia;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IBonificacion;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IFiniquito;
import com.esferalia.aon.payroll.core.IFiniquitoDiferencia;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.INominaDiferencia;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;
import com.esferalia.aon.payroll.core.nomina.INominaDAO;
import com.esferalia.aon.payroll.core.nomina.NominaDAOFactory;
import com.esferalia.aon.payroll.core.nomina.NominaParams;
import com.esferalia.aon.payroll.cotizacion.Bonificacion;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class NominaDAO implements INominaDAO {
	
	private static String EMP_ALIAS;
	private static String MES_ALIAS;
	private static String YEAR_ALIAS;
	private static String TIPO_ALIAS;
	private static String EMP_DIF_ALIAS;
	private static String MES_DIF_ALIAS;
	private static String YEAR_DIF_ALIAS;
	
	static {
		NominaDAOFactory.register( new NominaDAO() );
	}
	
	@Override
	public void configure() {	
		try {
			IManagerBean nominaBean = BeanManager.getManagerBean(Nomina.class);
			EMP_ALIAS = nominaBean.getFieldName(IPayrollAlias.NOMINA_EMPLEADO_ID);
			MES_ALIAS = nominaBean.getFieldName(IPayrollAlias.NOMINA_MES);
			YEAR_ALIAS = nominaBean.getFieldName(IPayrollAlias.NOMINA_YEAR);
			TIPO_ALIAS = nominaBean.getFieldName(IPayrollAlias.NOMINA_TIPO_NOMINA);
			IManagerBean nominaDifBean = BeanManager.getManagerBean(NominaDiferencia.class);
			EMP_DIF_ALIAS = nominaDifBean.getFieldName(IPayrollAlias.NOMINA_DIFERENCIA_EMPLEADO_ID);
			MES_DIF_ALIAS = nominaDifBean.getFieldName(IPayrollAlias.NOMINA_DIFERENCIA_MES);
			YEAR_DIF_ALIAS = nominaDifBean.getFieldName(IPayrollAlias.NOMINA_DIFERENCIA_YEAR);
			
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	@Override
	public INomina getNomina(NominaParams params) throws PayrollException {
		try {
			IManagerBean nominaBean = BeanManager.getManagerBean(Nomina.class);
			List<ITransferObject> list = nominaBean.getList(getCriteria(params),0,1);
			if (list.size() > 0) {
				return (INomina) list.get(0);  
			}
			return null;
		} catch (ManagerBeanException e) {
			throw new PayrollException( e ); 
		} 
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<INomina> getNominas(NominaParams params) throws PayrollException {
		try {
			IManagerBean nominaBean = BeanManager.getManagerBean(Nomina.class);
			List<?> list = nominaBean.getList(getCriteria(params),params.getOffset(),params.getCount());
			return (List<INomina>) list;  
		} catch (ManagerBeanException e) {
			throw new PayrollException( e ); 
		} 
	}

	@Override
	public Criteria getCriteria(NominaParams params) {
		Criteria c = new Criteria();
		if (params.getEmpleado() != null && params.getEmpleado().getId() != null) {
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
		return c;
	}

	@Override
	public void accept(IBonificacion bonifacion) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Bonificacion.class);
			bean.insertOrUpdate((Bonificacion) bonifacion);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public Criteria getCriteriaNominaDiferencia(NominaParams params) {
		Criteria c = new Criteria();
		if (params.getEmpleado().getId() != null) {
			c.addEqualExpression(EMP_DIF_ALIAS, params.getEmpleado().getId());
		}
		if (params.getMes() != null) {
			c.addEqualExpression(MES_DIF_ALIAS, params.getMes());
		}
		if (params.getYear() != null) {
			c.addEqualExpression(YEAR_DIF_ALIAS, params.getYear());
		}
		return c;
	}

	@Override
	public INominaDiferencia getNominaDiferencia(NominaParams params) throws PayrollException {
		try {
			IManagerBean nominaBean = BeanManager.getManagerBean(NominaDiferencia.class);
			List<ITransferObject> list = nominaBean.getList(getCriteriaNominaDiferencia(params),0,1);
			if (list.size() > 0) {
				return (INominaDiferencia) list.get(0);  
			}
			return null;
		} catch (ManagerBeanException e) {
			throw new PayrollException( e ); 
		} 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<INominaDiferencia> getNominasDiferencia(NominaParams params) throws PayrollException {
		try {
			IManagerBean nominaBean = BeanManager.getManagerBean(NominaDiferencia.class);
			List<?> list = nominaBean.getList(getCriteriaNominaDiferencia(params));
			return (List<INominaDiferencia>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public IFiniquito getFiniquito(IEmpleado empleado) throws PayrollException {
		try {
			IManagerBean finiquitoBean = BeanManager.getManagerBean(Finiquito.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(finiquitoBean.getFieldName(IPayrollAlias.FINIQUITO_EMPLEADO_ID), empleado.getId());
			criteria.addEqualExpression(finiquitoBean.getFieldName(IPayrollAlias.FINIQUITO_FECHA_BAJA), empleado.getFechaFin());
			criteria.addGreaterThanExpression(finiquitoBean.getFieldName(IPayrollAlias.FINIQUITO_DIAS_VACACIONES), 0);
			criteria.addGreaterThanExpression(finiquitoBean.getFieldName(IPayrollAlias.FINIQUITO_IMPORTE_VACACIONES), 0.0);
			List<ITransferObject> list = finiquitoBean.getList(criteria);
			if (list.size() > 0) {
				return (IFiniquito) list.get(0);  
			}
			return null;
		} catch (ManagerBeanException e) {
			throw new PayrollException( e ); 
		} 
	}
	
	@Override
	public IFiniquitoDiferencia getFiniquitoDiferencia(IEmpleado empleado) throws PayrollException {
		try {
			IManagerBean finiquitodfBean = BeanManager.getManagerBean(FiniquitoDiferencia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(finiquitodfBean.getFieldName(IPayrollAlias.FINIQUITO_DIFERENCIA_EMPLEADO_ID), empleado.getId());
			criteria.addEqualExpression(finiquitodfBean.getFieldName(IPayrollAlias.FINIQUITO_DIFERENCIA_FECHA_BAJA), empleado.getFechaFin());
			criteria.addGreaterThanExpression(finiquitodfBean.getFieldName(IPayrollAlias.FINIQUITO_DIFERENCIA_DIAS_VACACIONES), 0);
			criteria.addGreaterThanExpression(finiquitodfBean.getFieldName(IPayrollAlias.FINIQUITO_DIFERENCIA_IMPORTE_VACACIONES), 0.0);
			List<ITransferObject> list = finiquitodfBean.getList(criteria);
			if (list.size() > 0) {
				return (IFiniquitoDiferencia) list.get(0);  
			}
			return null;
		} catch (ManagerBeanException e) {
			throw new PayrollException( e ); 
		} 
	}

}
