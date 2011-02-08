package com.esferalia.aon.payroll.empleado;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.Percepcion;
import com.esferalia.aon.payroll.PercepcionPK;
import com.esferalia.aon.payroll.Trabajo;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IPercepcion;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.ITrabajo;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;
import com.esferalia.aon.payroll.core.enumeration.TiempoContrato;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class EmpleadoDAO implements IEmpleadoDAO {

	private static String PER_ALIAS = null;
	private static String DOC_ALIAS = null;
	private static String NSS_ALIAS = null;
	private static String NOMBRE_ALIAS = null;
	private static String APEL_ALIAS = null;
	private static String APEL2_ALIAS = null;
	private static String EMPR_ALIAS = null;
	private static String ACTIV_ALIAS = null; 
	private static String CLIENTE_INACTIVO_ALIAS = null; 
	private static String FEC_INI_ALIAS = null; 
	private static String FEC_FIN_ALIAS = null; 

	static {
		EmpleadoDAOFactory.register(new EmpleadoDAO());
	}
	
	@Override
	public void configure() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Empleado.class);
			PER_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_ID);
			DOC_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_REGISTRY_DOCUMENT_VALUE);
			NSS_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_NUM_SS);
			NOMBRE_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_NAME);
			APEL_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_SURNAME);
			APEL2_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_LAST_NAME);
			EMPR_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_ACTIVIDAD_EMPRESA_NAME);
			ACTIV_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_ACTIVIDAD_NAME);
			CLIENTE_INACTIVO_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_ACTIVIDAD_EMPRESA_CLIENTE_INACTIVO_BD);
			FEC_INI_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_FECHA_INICIO);
			FEC_FIN_ALIAS = bean.getFieldName(IPayrollAlias.EMPLEADO_FECHA_FIN);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IEmpleado> getEmpleados(EmpleadoParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Empleado.class);
			Criteria c = getCriteria(params);
			c.addOrder(FEC_INI_ALIAS, false);
			List<?> list = bean.getList(c);
			return (List<IEmpleado>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@Override
	public Criteria getCriteria(EmpleadoParams params) throws PayrollException {
		try {
			Criteria c = new Criteria();

			if (!StringUtils.isBlank(params.getPersonaId())) {
				c.addExpression(PER_ALIAS, params.getPersonaId());
			}
			if (!StringUtils.isBlank(params.getDocumento())) {
				c.addExpression(DOC_ALIAS, params.getDocumento());
			}
			if (!StringUtils.isBlank(params.getNumSS())) {
				c.addExpression(NSS_ALIAS, params.getNumSS());
			}
			if (!StringUtils.isBlank(params.getNombre())) {
				c.addExpression(NOMBRE_ALIAS, params.getNombre());
			}
			if (!StringUtils.isBlank(params.getApellido())) {
				c.addExpression(APEL_ALIAS, params.getApellido());
			}
			if (!StringUtils.isBlank(params.getApellido2())) {
				c.addExpression(APEL2_ALIAS, params.getApellido2());
			}
			if (!StringUtils.isBlank(params.getEmpresa())) {
				c.addExpression(EMPR_ALIAS, params.getEmpresa());
			}
			if (!StringUtils.isBlank(params.getActividad())) {
				c.addExpression(ACTIV_ALIAS, params.getActividad());
			}
			if (params.isClienteActivo()) {
				c.addExpression(CLIENTE_INACTIVO_ALIAS, "N");
			}
			if (params.isFinalizados()) {
				c.addNullExpression(FEC_FIN_ALIAS);
			}
			return c;
		} catch (ExpressionException e) {
			throw new PayrollException(e);
		}
	}

	@Override
	public int getCount(EmpleadoParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Empleado.class);
			return bean.getCount(getCriteria(params));
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IEmpleado> getEmpleados(EmpleadoParams params, int start, int count)
			throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Empleado.class);
			Criteria c = getCriteria(params);
			c.addOrder(FEC_INI_ALIAS, false);
			List<?> list = bean.getList(c, start, count);
			return (List<IEmpleado>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<IPersona> getDistinctEmpleados(EmpleadoParams params, int start, int count)
			throws PayrollException {
		List<IPersona> list = null;
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
        Session session = HibernateUtil.getSession(sessionFactoryName);
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				session.beginTransaction();	
			}
			org.hibernate.Criteria hibernateCriteria = CriteriaUtilities
					.toHibernateCriteria(getCriteria(params), session, IPayrollAlias.EMPLEADO_ENTRY );

			ProjectionList pl = Projections.projectionList()
				.add(Projections.property("persona"), "persona");
			Projection p = 	Projections.distinct( pl );		
			hibernateCriteria.setProjection( p);

			if (start != -1) {
				hibernateCriteria.setFirstResult(start);
			}
			if (count != -1) {
				hibernateCriteria.setMaxResults(count);
			}
			list = (List<IPersona>) hibernateCriteria.list();				
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new PayrollException(he.getCause().getMessage(), he.getCause());
			}
			throw new PayrollException(he);
		} catch (DAOException e) {
			throw new PayrollException(e);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IPercepcion> getPercepciones(IEmpleado empleado) throws PayrollException{
		try {
			IManagerBean bean = BeanManager.getManagerBean(Percepcion.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.PERCEPCION_ID_NUMERO), empleado.getId());
			String fAlias = bean.getFieldName(IPayrollAlias.PERCEPCION_FECHA_FIN);
			Expression or1 = ExpressionUtilities.getGreaterThanExpression(fAlias, Calendar.getInstance().getTime());  
			Expression or2 = ExpressionUtilities.getNullExpression(fAlias);
			c.addExpression(ExpressionUtilities.getOrExpression(or1, or2));
			List<?> list = bean.getList(c);
			return (List<IPercepcion>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public IPercepcion initializePercepcion(IEmpleado empleado) throws PayrollException{
		PercepcionPK id = new PercepcionPK();
		id.setNumero(empleado.getId());
		id.setCdg(getMaxPercepcionesCdg(empleado)+1);
		Percepcion percepcion = new Percepcion();
		percepcion.setId(id);
		return percepcion;
	}
	
	//	metodo para grabar las percepciones de un empleado 
	@Override
	public IPercepcion accept(IPercepcion percepcion) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Percepcion.class);
			Percepcion p = (Percepcion) percepcion;
			return (IPercepcion) bean.insertOrUpdate(p);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	private Integer getMaxPercepcionesCdg(IEmpleado empleado) throws PayrollException{
		try {
			IManagerBean bean = BeanManager.getManagerBean(Percepcion.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.PERCEPCION_ID_NUMERO), empleado.getId());
			List<ITransferObject> list = bean.getList(c);
			Integer max=0;
			for(ITransferObject to: list){
				Percepcion p = (Percepcion)to;
				if(p.getId().getCdg().compareTo((max))>0){
					max = p.getId().getCdg();
				}
			}
			return max;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ITrabajo> getTrabajos(IEmpleado empleado) throws PayrollException{
		try {
			IManagerBean bean = BeanManager.getManagerBean(Trabajo.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.TRABAJO_EMPLEADO_ID), empleado.getId());
			c.addOrder(bean.getFieldName(IPayrollAlias.TRABAJO_FECFIN), false);
			List<?> list = bean.getList(c);
			return (List<ITrabajo>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ITrabajo> getTrabajosTP(IEmpleado empleado)	throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Trabajo.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.TRABAJO_EMPLEADO_ID), empleado.getId());
			c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IPayrollAlias.TRABAJO_TIEMPO_CONTRATO),	TiempoContrato.COMPLETO));
			c.addOrder(bean.getFieldName(IPayrollAlias.TRABAJO_FECFIN), false);
			List<?> list = bean.getList(c);
			return (List<ITrabajo>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	
}
