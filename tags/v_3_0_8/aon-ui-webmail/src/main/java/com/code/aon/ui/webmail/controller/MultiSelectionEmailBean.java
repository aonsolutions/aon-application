package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Contact;
import com.code.aon.groupware.dao.IContactAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.webmail.MailAccount;

public class MultiSelectionEmailBean {

	private List<SelectionEmail> emails; 

    public void reload() {
        emails = new ArrayList<SelectionEmail>();
    	try{
			WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
			MailAccount account = wmc.getServer().getAccount();
			IManagerBean bean = AonUtil.getController(AonConstants.BEAN_CONTACT).getManagerBean();
			Criteria criteria = new Criteria();
			criteria.addOrder(bean.getFieldName(IContactAlias.CONTACT_NAME));
			List lst = lst = bean.getList(criteria);
            for (int i = 0, max = lst.size(); i < max; i++) {
            	SelectionEmail se = new SelectionEmail();
            	se.setEmail((Contact) lst.get(i));
            	se.setSelected(Boolean.FALSE);
            	emails.add(se);
            }
    	}catch (ManagerBeanException e) {
    		e.printStackTrace();
		}
    }
    
	/**
	 * @return the emails
	 */
	public List<SelectionEmail> getEmails() {
		return emails;
	}

	/**
	 * @param emails the emails to set
	 */
	public void setEmails(List<SelectionEmail> emails) {
		this.emails = emails;
	}
	
	/**
	 * @return the selectedRows
	 */
	public List getSelectedRows() {
	    List<Contact> selectedRows = new ArrayList<Contact>();
        selectedRows.clear();
        for (int i = emails.size()-1; i >= 0 ; i--) {
            if (emails.get(i).isSelected()) {
                selectedRows.add(emails.get(i).getEmail());
            }
        }
		return selectedRows;
	}
    
	public class SelectionEmail{
		
		private boolean selected;
		
		private Contact email;

		/**
		 * @return the selected
		 */
		public boolean isSelected() {
			return selected;
		}

		/**
		 * @param selected the selected to set
		 */
		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		/**
		 * @return the email
		 */
		public Contact getEmail() {
			return email;
		}

		/**
		 * @param email the email to set
		 */
		public void setEmail(Contact email) {
			this.email = email;
		}
		
	}


}
