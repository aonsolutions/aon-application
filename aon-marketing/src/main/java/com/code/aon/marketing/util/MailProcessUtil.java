package com.code.aon.marketing.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.marketing.MailProcess;
import com.code.aon.marketing.Template;
import com.code.aon.marketing.enumeration.MailProcessType;
import com.code.aon.webmail.db.MailAccount;

public class MailProcessUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(MailProcessUtil.class);
	
	private final static String AON_MAIL_PROCESS = "AON_MAIL_PROCESS";
	
	private static ITransferObject getTo( Class<? extends ITransferObject> classz, String id ) {
		ITransferObject to = null;
		try {
			if ( NumberUtils.isDigits(id) ) {
				IManagerBean bean = BeanManager.getManagerBean(classz);
				to = bean.get(Integer.valueOf(id));				
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return to;
	}

	private static String getKey( MailProcessType type ) {
		return AON_MAIL_PROCESS + "_" + type.ordinal() + "_1";
	}
	
	public static MailProcess get( MailProcessType type ) {
		ApplicationParameter ap = AppParamUtil.getParameter(getKey(type));
		if ( ap != null ) {
			String[] ids = StringUtils.split(ap.getValue());
			MailAccount mailAccount = (MailAccount) getTo(MailAccount.class, ids[0]);
			if ( mailAccount != null ) {
				Template template = null;
				if ( ids.length > 1 ) {
					template = (Template) getTo(Template.class, ids[1]);	
				}
				MailProcess mailProcess = new MailProcess();
				mailProcess.setMailAccount(mailAccount);
				mailProcess.setType(type);
				mailProcess.setTemplate(template);
				mailProcess.setPrincipal(Integer.parseInt(ap.getName().substring(ap.getName().length() - 1)));
				return mailProcess;
			}
		}

		return null;
	}

	public static boolean remove( MailProcess mp ) {
		return AppParamUtil.removeParameter(getKey(mp.getType()));
	}
	
	public static void save( MailProcess mp ) {
		if ( (mp.getMailAccount() != null) && (mp.getMailAccount().getId() != null) ) {
			String value = mp.getMailAccount().getId().toString();
			if ( (mp.getTemplate() != null) && (mp.getTemplate().getId() != null) ) {
				value += " " + mp.getTemplate().getId().toString();
			}
			AppParamUtil.insertParameter(getKey(mp.getType()), value);
		}
	}
	
}
