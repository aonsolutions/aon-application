package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.groupware.Contact;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.dao.IWebMailAlias;


public class AutoCompleteEmailDictionary {

    private static Logger LOGGER = Logger.getLogger(AutoCompleteEmailDictionary.class.getName());

    private static List dictionary;

    public AutoCompleteEmailDictionary() {
        // initialize the ditionary
        try {
        	LOGGER.info("initializing dictionary");
            init();
        } catch (Exception e) {
        	LOGGER.severe("Error initializtin sorting list");
        }
    }

    public static final Comparator LABEL_COMPARATOR = new Comparator() {
        String s1;
        String s2;

        public int compare(Object o1, Object o2) {

            if (o1 instanceof SelectItem) {
                s1 = ((SelectItem) o1).getLabel();
            } else {
                s1 = o1.toString();
            }

            if (o2 instanceof SelectItem) {
                s2 = ((SelectItem) o2).getLabel();
            } else {
                s2 = o2.toString();
            }
            return s1.compareToIgnoreCase(s2);
        }
    };

    public List getDictionary() {
        return dictionary;
    }

    public static void init() {
        List emails = null;

    	try{
			WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
			MailAccount account = wmc.getServer().getAccount();
			IManagerBean bean = BeanManager.getManagerBean(Contact.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IGroupWareAlias.CONTACT_USER_ID), account.getUser().getId());
			emails = bean.getList(criteria);
    	}catch (ManagerBeanException e) {
    		e.printStackTrace();
		}

        if (emails != null) {
            dictionary = new ArrayList(emails.size());
            Contact tmpEmail;
            for (int i = 0, max = emails.size(); i < max; i++) {
            	tmpEmail = (Contact) emails.get(i);
                if (tmpEmail != null && tmpEmail.getEmail() != null) {
                    dictionary.add(new SelectItem(tmpEmail, tmpEmail.getEmail()));
                }
            }
            emails.clear();
            Collections.sort(dictionary, LABEL_COMPARATOR);
        }

    }
}