package com.code.aon.ui.form;

import java.io.Serializable;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;

/**
 * AonUtil includes some common methods.
 */
public class FormUtil {

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FormUtil.class);

	/**
	 * Gets the controller registered in <code>faces-bean-config.xml</code>
	 * with that name.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return the controller
	 */
	public static IController getController(String name) {
		Object o = AonUtil.getRegisteredBean(name);
		if (o instanceof IController) {
			return (IController) o;
		}
		LOGGER.error("{} is not a instance of 'com.code.aon.ui.form.IController'",o);
		return null;
	}

	private static void _remove( Class<? extends ITransferObject> _class, Serializable id, boolean skipDomain, String[] aliases ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(_class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(skipDomain);
		if (! ArrayUtils.isEmpty(aliases) ) {
			Expression exp = null;
			for( String alias : aliases ) {
				if ( exp == null ) {
					exp = ExpressionUtilities.getEqualExpression(bean.getFieldName(alias), id);
				} else {
					Expression exp1 = ExpressionUtilities.getEqualExpression(bean.getFieldName(alias), id);
					exp = ExpressionUtilities.getOrExpression(exp, exp1);
				}
			}
			criteria.addExpression(exp);			
		}
		for( ITransferObject to : bean.getList(criteria) ) {
			bean.remove(to);
		}	
	}
	
	public static void remove( Class<? extends ITransferObject> _class, Serializable id, boolean skipDomain, String ... aliases ) throws ManagerBeanException {
		_remove( _class, id, skipDomain, aliases );
	}

	public static void remove( Class<? extends ITransferObject> _class, Serializable id, String ... aliases ) throws ManagerBeanException {
		_remove( _class, id, false, aliases );
	}
	
}
