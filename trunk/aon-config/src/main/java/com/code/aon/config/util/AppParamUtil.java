package com.code.aon.config.util;

import static com.esferalia.aon.entity.IEntityAlias.APPLICATION_PARAMETER_NAME;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;

public class AppParamUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppParamUtil.class);
	
	public static ApplicationParameter getParameter( AppParam ap ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(APPLICATION_PARAMETER_NAME), ap.getValue());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (ApplicationParameter) list.get(0);
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}
	
	public static ApplicationParameter insertParameter( ApplicationParameter ap ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
			if ( StringUtils.isEmpty(ap.getValue()) ) {
				if ( ap.getId() != null ) {
					bean.remove(ap);
				}
			} else {
				bean.insertOrUpdate( ap );
			}
			return ap;
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
    }	
	
	public static ApplicationParameter insertParameter( AppParam appParam, String value ) {
		ApplicationParameter ap = getParameter(appParam);
		if ( ap == null ) {
			ap = new ApplicationParameter();
			ap.setName(appParam.getValue());
		}
		ap.setValue(value);		
		return insertParameter(ap);
    }	

	public static boolean removeParameter( AppParam appParam ) {
		ApplicationParameter ap = getParameter(appParam);
		if ( ap != null ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
				return bean.remove(ap);
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}	
		return false;
    }	
	
	public static String getValue( AppParam appParam ) {
		ApplicationParameter ap = getParameter(appParam);
		if ( ap != null ) {
			return StringUtils.trimToNull(ap.getValue());
		}
		return null;
	}
	
}
