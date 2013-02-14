package com.code.aon.ui.accounting.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.IAccountConstants;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CostCenterControllerListener extends ControllerAdapter {
	
	private String oldValue;
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			String alias = event.getController().getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME);
			Expression exp = ExpressionUtilities.getLikeExpression(alias, IAccountConstants.COST_CENTER_LIKE_PREFIX);
			criteria.addExpression(exp);
			criteria.addOrder(alias);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException { 
		try {
			ApplicationParameter ap = (ApplicationParameter) event.getController().getTo();
			String name = ap.getName();
			IManagerBean bean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_COST_CENTER), name);
			int count = bean.getCount(criteria);
			if ( count > 0 ) {
				throw new ControllerListenerException("No se puede borrar, el centro de costo. Está asignado a cuentas contables.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException { 
		try {
			ApplicationParameter ap = (ApplicationParameter) event.getController().getTo();
			IManagerBean bean = event.getController().getManagerBean();
			int i = 0;
			List<ITransferObject> list = bean.getList(null);
			for (ITransferObject to : list ) {
				ApplicationParameter app = (ApplicationParameter) to;	
				String name = app.getName();
				String sufix = StringUtils.substringAfter(name, IAccountConstants.COST_CENTER_PREFIX);
				try {
					int s = Integer.parseInt(sufix);
					if (s > i ) {
						i = s;
					}
				} catch (NumberFormatException e) {
					// nada
				}
			}
			ap.setName(IAccountConstants.COST_CENTER_PREFIX + (i+1));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			ApplicationParameter ap = (ApplicationParameter) event.getController().getTo();
			IManagerBean bean = event.getController().getManagerBean();
			ITransferObject to = bean.get(ap.getId()); 
			oldValue = ((ApplicationParameter) to).getValue();
			String sessionName = HibernateUtil.getSessionFactoryName(ApplicationParameter.class.getName());
			HibernateUtil.getSession(sessionName).evict(to);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			ApplicationParameter ap = (ApplicationParameter) event.getController().getTo();
			if (!StringUtils.equals(oldValue, ap.getValue())) {
				IManagerBean bean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_COST_CENTER), oldValue );
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to : list) {
					Account account = (Account) to;
					account.setCostCenter(ap.getValue());
					bean.update(account);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Imposible modificar las cuentas contables.");
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	
}
