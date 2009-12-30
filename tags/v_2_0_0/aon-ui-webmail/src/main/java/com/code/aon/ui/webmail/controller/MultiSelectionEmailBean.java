package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class MultiSelectionEmailBean {

	private List<SelectionEmail> emails; 
    private List<Contact> selectedRows = new ArrayList<Contact>();

    public void init() {
        emails = new ArrayList<SelectionEmail>();
    	try{
			WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
			MailAccount account = wmc.getServer().getAccount();
			IManagerBean bean = BeanManager.getManagerBean(Contact.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IGroupWareAlias.CONTACT_USER_ID), account.getUser().getId());
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
	
    public void rowSelection(RowSelectorEvent e) {
        selectedRows.clear();
        for (int i = emails.size()-1; i >= 0 ; i--) {
            if (emails.get(i).isSelected()) {
                selectedRows.add(emails.get(i).getEmail());
            }
        }
    }

	/**
	 * @return the selectedRows
	 */
	public List getSelectedRows() {
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
