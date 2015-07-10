package com.code.aon.ui.form;

import java.io.Serializable;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.TypeResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.ConstantExpression;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.ast.RelationalExpression;
import com.code.aon.ql.ast.RelationalType;
import com.code.aon.ql.ast.impl.ConstantExpressionImpl;
import com.code.aon.ql.ast.impl.RelationalExpressionImpl;
import com.code.aon.ql.util.ExpressionException;
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

	private static void addAliases( IManagerBean bean, Serializable id, Criteria criteria, String[] aliases ) throws ManagerBeanException {
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
	}
	
	private static void _remove( Class<? extends ITransferObject> _class, Serializable id, boolean skipDomain, String[] aliases ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(_class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(skipDomain);
		addAliases(bean, id, criteria, aliases);
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

	private static boolean _hasReferences( Class<? extends ITransferObject> _class, Serializable id, boolean skipDomain, String[] aliases ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(_class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(skipDomain);
		addAliases(bean, id, criteria, aliases);
		return bean.getCount(criteria) > 0;
	}

	public static boolean hasReferences( Class<? extends ITransferObject> _class, Serializable id, boolean skipDomain, String ... aliases ) throws ManagerBeanException {
		return _hasReferences( _class, id, skipDomain, aliases );
	}

	public static boolean hasReferences( Class<? extends ITransferObject> _class, Serializable id, String ... aliases ) throws ManagerBeanException {
		return _hasReferences( _class, id, false, aliases );
	}
	
	
	private static boolean isOnlyTextExpression( Expression expression, String pojo, String alias ) {
		if ( expression instanceof RelationalExpression ) {
			RelationalExpression re = (RelationalExpression) expression;
			if ( re.getType()==RelationalType.EQUAL &&  
				(re.getLeftExpression() instanceof IdentExpression) &&
				(re.getRightExpression() instanceof ConstantExpression) ) {
				TypeResolver typeResolver = new TypeResolver(pojo);
				Type type = typeResolver.getType(alias);
				return typeResolver.isString(type);
			}
		}			
		return false;
	}
	
	private static void updateTextExpression( Expression expression ) {
		RelationalExpressionImpl re = (RelationalExpressionImpl) expression;
		re.setType(RelationalType.LIKE);
		ConstantExpressionImpl ce = (ConstantExpressionImpl) re.getRightExpression();
		ce.setData( "%" + ce.getData().toString() + "%" );
	}	

	public static Expression getExpression( Criteria criteria, String pojo, String alias, String value ) throws ManagerBeanException {
		Expression exp = null;
		String _value = StringUtils.trimToEmpty(value); 
		try {
			if (_value.charAt(0) == '=') {
				exp = ExpressionUtilities.getExpression(_value.substring(1), alias);
			} else {
				exp = ExpressionUtilities.getExpression(value, alias);
				if ( isOnlyTextExpression(exp, pojo, alias) ) {
					updateTextExpression(exp);
				}
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
		return exp;
	}	
}
