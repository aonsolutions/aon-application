package com.esferalia.aon.payroll.empresa;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.ActividadCCC;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IActividadCCC;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.empresa.EmpresaDAOFactory;
import com.esferalia.aon.payroll.core.empresa.IEmpresaDAO;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;
import com.esferalia.aon.payroll.core.empresa.RemesaCertificadoEmpresaParams;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;
import com.esferalia.aon.payroll.core.enumeration.FileStatus;
import com.esferalia.aon.payroll.core.enumeration.Regimen;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class EmpresaDAO implements IEmpresaDAO {

	private static String ACTCCC_ACT_ALIAS = null;
	private static String ACTCCC_CCC_ALIAS = null;
	private static String REMESA_CERT_DET_REMESA_ALIAS = null;

	static {
		EmpresaDAOFactory.register(new EmpresaDAO());
	}
	
	@Override
	public void configure() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActividadCCC.class);
			ACTCCC_ACT_ALIAS = bean.getFieldName(IPayrollAlias.ACTIVIDAD_CCC_ID_CDG);
			ACTCCC_CCC_ALIAS = bean.getFieldName(IPayrollAlias.ACTIVIDAD_CCC_ID_TIPCCC);
			IManagerBean beanCertEmp = BeanManager.getManagerBean(RemesaCertificadoEmpresaDetalle.class);
			REMESA_CERT_DET_REMESA_ALIAS = beanCertEmp.getFieldName(IPayrollAlias.REMESA_CERTIFICADO_EMPRESA_DETALLE_REMESA_CERTIFICADO_ID);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IRemesaCertificadoEmpresa> getRemesaCertificados(RemesaCertificadoEmpresaParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RemesaCertificadoEmpresa.class);
			Criteria criteria = new Criteria();
			if (!StringUtils.isEmpty(params.getEmpresa())) {
				criteria.addExpression(bean.getFieldName(IPayrollAlias.REMESA_CERTIFICADO_EMPRESA_EMPRESA_NAME),params.getEmpresa());
			}
			if (params.getFecha() != null) {
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.REMESA_CERTIFICADO_EMPRESA_FECHA),params.getFecha());
			}
			if (!ArrayUtils.isEmpty(params.getEstados())) {
				String status = bean.getFieldName(IPayrollAlias.REMESA_CERTIFICADO_EMPRESA_ESTADO);
				addEnumToCriteria(criteria, status, params.getEstados());
			}
			criteria.addOrder(bean.getFieldName(IPayrollAlias.REMESA_CERTIFICADO_EMPRESA_FECHA), false);
			List<?> list = bean.getList(criteria);
			return (List<IRemesaCertificadoEmpresa>)list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		} catch (ExpressionException e) {
			throw new PayrollException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IRemesaCertificadoEmpresaDetalle> getDetalleRemesaCertificados(IRemesaCertificadoEmpresa remesa) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RemesaCertificadoEmpresaDetalle.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(REMESA_CERT_DET_REMESA_ALIAS, remesa.getId());
			List<?> list = bean.getList(criteria);
			return (List<IRemesaCertificadoEmpresaDetalle>)list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IEmpleado> getEmpleados(RemesaCertificadoEmpresaParams params)
			throws PayrollException {
		final int FROM_YEAR = 2010;
		final int FROM_MONTH = 05;
		final int FROM_DAY = 30;
		try {
			// fecha desde la que se empieza a usar el nuevo metodo de certificados de empresa
			Calendar date = new GregorianCalendar();
			date.set(Calendar.YEAR, FROM_YEAR);
			date.set(Calendar.MONTH, FROM_MONTH);
			date.set(Calendar.DAY_OF_MONTH, FROM_DAY);
			
			Criteria criteria = new Criteria();
			IManagerBean empleadoBean = BeanManager
					.getManagerBean(Empleado.class);
			if (!StringUtils.isEmpty(params.getEmpresa())) {
				criteria.addExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_ACTIVIDAD_EMPRESA_NAME),params.getEmpresa());
			}
			if (!StringUtils.isEmpty(params.getDocumento())) {
				criteria.addEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_REGISTRY_DOCUMENT_VALUE),params.getDocumento());
			}
			if (!StringUtils.isEmpty(params.getNombre())) {
				criteria.addExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_NAME),params.getNombre());
			}
			if (!StringUtils.isEmpty(params.getApellido())) {
				criteria.addExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_SURNAME),params.getApellido());
			}
			if (!StringUtils.isEmpty(params.getApellido2())) {
				criteria.addExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_LAST_NAME),params.getApellido2());
			}
			if (params.getFechaDesde()!=null) {
				criteria.addGreaterThanOrEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_FECHA_FIN), params.getFechaDesde());
			}
			if (params.getFechaHasta()!=null) {
				criteria.addLessThanOrEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_FECHA_FIN), params.getFechaHasta());
			}
			
			Expression exp1  = ExpressionUtilities.getNotEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_CODCCC), "A");
			Expression exp2  = ExpressionUtilities.getNotEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_CODCCC), "S");
			criteria.addExpression( ExpressionUtilities.getAndExpression(exp1, exp2) );
			criteria.addOrder(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_ACTIVIDAD_EMPRESA_NAME));
			criteria.addOrder(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_SURNAME));
			criteria.addOrder(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_FECHA_FIN), false);
			
			List<?> list = empleadoBean.getList(criteria);
			return (List<IEmpleado>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		} catch (ExpressionException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public IRemesaCertificadoEmpresa getNewRemesa(IEmpleado empleado) throws PayrollException {
		IRemesaCertificadoEmpresa remesa = new RemesaCertificadoEmpresa();
		remesa.setEmpresa(empleado.getEmpresa());
		if(empleado.getFechaFin().before(Calendar.getInstance().getTime())){
			remesa.setFecha(Calendar.getInstance().getTime());
		} else {
			remesa.setFecha(new Date(empleado.getFechaFin().getTime()+(1*24*60*60*1000)));
		}
		remesa.setEstado(FileStatus.PENDIENTE);
		return remesa;
	}
	
	@Override
	public IRemesaCertificadoEmpresaDetalle getNewRemesaDetalle(IEmpleado empleado) {
		IRemesaCertificadoEmpresaDetalle detalle = new RemesaCertificadoEmpresaDetalle();
		detalle.setEmpleado(empleado);
		return detalle;
	}
	
	@Override
	public IRemesaCertificadoEmpresa accept(IRemesaCertificadoEmpresa remesa) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RemesaCertificadoEmpresa.class);
			RemesaCertificadoEmpresa r = (RemesaCertificadoEmpresa) remesa;
			return (IRemesaCertificadoEmpresa) bean.insertOrUpdate(r);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public void accept(IRemesaCertificadoEmpresaDetalle detalle) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RemesaCertificadoEmpresaDetalle.class);
			RemesaCertificadoEmpresaDetalle d = (RemesaCertificadoEmpresaDetalle) detalle;
			bean.insertOrUpdate(d);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public String getEmpresaCccEmpleado(IEmpleado empleado) throws PayrollException {
		String ccc;
		ccc = getRegimenCode(empleado.getActividad().getRegimen());
		ccc += getActividadCCC(empleado.getActividad(), empleado.getCuentaCotizacion()).getDescripcion();
		return ccc;
	}
	
	private String getRegimenCode(Regimen regimen) {
		if(regimen == Regimen.AGRARIO){
			return "0613";
		} else if(regimen == Regimen.GENERAL){
			return "0111";
		} else if(regimen == Regimen.ARTISTAS){
			return "0112";
		} else if(regimen == Regimen.MARITIMO){
			return "0811";
		}
		return null;
	}
	
	protected void addEnumToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( value != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}	
	
}
