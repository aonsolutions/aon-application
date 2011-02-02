package com.esferalia.aon.payroll.commons;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.Usuario;
import com.esferalia.aon.payroll.core.IUsuario;
import com.esferalia.aon.payroll.core.commons.CommonsPayrollDAOFactory;
import com.esferalia.aon.payroll.core.commons.ICommonsPayrollDAO;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;
import com.esferalia.aon.payroll.cotizacion.TipoBonificacion;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class CommonsPayrollDAO implements ICommonsPayrollDAO {
	
	private static String BONIFICACION_DESCRIPCION;

	static {
		CommonsPayrollDAOFactory.register(new CommonsPayrollDAO());
	}
	
	@Override
	public void configure() {	
		try {
			IManagerBean tipoBonificacionBean = BeanManager.getManagerBean(TipoBonificacion.class);
			BONIFICACION_DESCRIPCION = tipoBonificacionBean.getFieldName(IPayrollAlias.TIPO_BONIFICACION_DESCRIPCION);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ITipoBonificacion> getTiposBonificacion(String condition)
			throws PayrollException {
		try {
			Criteria criteria=null;
			if(condition!=null || condition!=""){
				criteria = new Criteria();
				criteria.addExpression(ExpressionUtilities.getLikeExpression(BONIFICACION_DESCRIPCION, condition));
			}
			IManagerBean bean = BeanManager.getManagerBean(TipoBonificacion.class);
			List<?> list = bean.getList(criteria);
			return (List<ITipoBonificacion>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@Override
	public IUsuario getUsuarioActivo(String loggedUser) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Usuario.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IPayrollAlias.USUARIO_LOGIN);
			criteria.addEqualExpression(alias, loggedUser.toUpperCase());
			List<?> list = bean.getList(criteria);
			if (list.size() < 1 ) {
				throw new PayrollException("El usuario '"+ loggedUser + " no tiene perfil definido.'");	
			}
			return (IUsuario) list.get(0);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	
}
