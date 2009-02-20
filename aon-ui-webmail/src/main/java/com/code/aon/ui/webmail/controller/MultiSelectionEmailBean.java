package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.dao.IWebMailAlias;

public class MultiSelectionEmailBean {

	private ListDataModel model;
	
	private List<SelectionEmail> emails; 
	
	public void reload() {
        emails = new ArrayList<SelectionEmail>();
    	try{
			IManagerBean bean = FormUtil.getController(WebMailConstants.BEAN_CONTACT).getManagerBean();
			Criteria criteria = new Criteria();
			criteria.addOrder(bean.getFieldName(IWebMailAlias.CONTACT_NAME));
			List<ITransferObject> lst = bean.getList(criteria);
            for (int i = 0, max = lst.size(); i < max; i++) {
            	SelectionEmail se = new SelectionEmail();
            	se.setEmail((Contact) lst.get(i));
            	se.setSelected(Boolean.FALSE);
            	emails.add(se);
            }
    	} catch (ManagerBeanException e) {
    		e.printStackTrace();
		}
    	this.model = new ListDataModel( emails );
    }
    
	public ListDataModel getModel() {
		return model;
	}
	
	public void onToggleSelected(ActionEvent event) {
		if ( model.isRowAvailable() ) {
			SelectionEmail selectionEmail = (SelectionEmail) model.getRowData();
			selectionEmail.setSelected(! selectionEmail.isSelected() );
		}
	}	
	
	/**
	 * @return the selectedRows
	 */
	public List<Contact> getSelectedRows() {
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
		 * Checks if is read only selected. Necesario debido a un error en Rich Faces 3.2.0.SR1.
		 * Los selectBooleanCheckBox no se actualizan correctamente en una tabla dentro de un
		 * modalPanel y por eso se actualizan mediante el rowSelector.
		 * 
		 * @return true, if is read only selected
		 */
		public boolean isReadOnlySelected() {
			return selected;
		}

		public void setReadOnlySelected(boolean readOnlySelected) {
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
