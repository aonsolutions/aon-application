package com.esferalia.aon.payroll.empresa;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.Actividad;
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
	public List<IRemesaCertificadoEmpresa> getRemesaCertificados() throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RemesaCertificadoEmpresa.class);
			List<?> list = bean.getList(null);
			return (List<IRemesaCertificadoEmpresa>)list;
		} catch (ManagerBeanException e) {
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
		try {
			Criteria criteria = new Criteria();
			IManagerBean empleadoBean = BeanManager
					.getManagerBean(Empleado.class);
			if (!StringUtils.isEmpty(params.getEmpresa())) {
				criteria.addEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_ACTIVIDAD_EMPRESA_NAME),params.getEmpresa());
			}
			if (!StringUtils.isEmpty(params.getDocumento())) {
				criteria.addEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_REGISTRY_DOCUMENT_VALUE),params.getDocumento());
			}
			if (!StringUtils.isEmpty(params.getNombre())) {
				criteria.addEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_NAME),params.getNombre());
			}
			if (!StringUtils.isEmpty(params.getApellido())) {
				criteria.addEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_SURNAME),params.getApellido());
			}
			if (!StringUtils.isEmpty(params.getApellido2())) {
				criteria.addEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_LAST_NAME),params.getApellido2());
			}
			criteria.addLessThanOrEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_FECHA_FIN), params.getFecha());
			Expression exp1  = ExpressionUtilities.getNotEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_CODCCC), "A");
			Expression exp2  = ExpressionUtilities.getNotEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_CODCCC), "S");
			criteria.addExpression( ExpressionUtilities.getAndExpression(exp1, exp2) );
			criteria.addOrder(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_ACTIVIDAD_EMPRESA_NAME));
			
			List<?> list = empleadoBean.getList(criteria);
			return (List<IEmpleado>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public IRemesaCertificadoEmpresa getNewRemesa(IEmpleado empleado, Date fecha) throws PayrollException {
		IRemesaCertificadoEmpresa remesa = new RemesaCertificadoEmpresa();
		remesa.setEmpresa(empleado.getEmpresa());
		remesa.setFecha(fecha);
		remesa.setCodigoCcc(getNumeroCcc(empleado));
		
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
	public String getNumeroCcc(IEmpleado empleado) throws PayrollException{
		String ccc;
		
		try {
			IManagerBean actividadBean = BeanManager.getManagerBean(Actividad.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(actividadBean.getFieldName(IPayrollAlias.ACTIVIDAD_ID), empleado.getActividad().getId());
			List<?> list = actividadBean.getList(criteria);
			Actividad actividad = (Actividad)list.get(0);
			ccc = getRegimenCode(actividad.getRegimen());

			IManagerBean actCccBean = BeanManager.getManagerBean(ActividadCCC.class);
			criteria = new Criteria();
			criteria.addEqualExpression(actCccBean.getFieldName(IPayrollAlias.ACTIVIDAD_CCC_ID_CDG), actividad.getId());
			criteria.addEqualExpression(actCccBean.getFieldName(IPayrollAlias.ACTIVIDAD_CCC_ID_TIPCCC), actividad.getIndregimen());
			list = actCccBean.getList(criteria);
			if(list!=null && list.size()>0){
				ActividadCCC act = (ActividadCCC)list.get(0);
				ccc += act.getDescripcion();
			}
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
		return ccc;
	}
	
	public String getRegimenCode(Regimen regimen) {
		if(regimen == Regimen.AGRARIO){
			return "0613";
		} else if(regimen == Regimen.GENERAL){
			return "0111";
		} else if(regimen == Regimen.ARTISTAS){
			return "0112";
		} else if(regimen == Regimen.MARITIMO){
			return "08xx";
		}
		return null;
	}
	
}
