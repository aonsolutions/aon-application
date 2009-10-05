package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.dao.IWebMailAlias;

public class MultiSelectionEmailBean {

	private static final Logger LOGGER = Logger.getLogger(MultiSelectionEmailBean.class.getName());
	
	private ListDataModel model;
	
	private List<SelectionEmail> emails; 

	private int pageSize = 10;
	
	private int currentPage;
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void init( boolean loadContacts ) {
		this.currentPage = 1;
		if ( loadContacts ) {
	        emails = new ArrayList<SelectionEmail>();
	    	try{
				IManagerBean bean = FormUtil.getController(WebMailConstants.BEAN_CONTACT).getManagerBean();
				Criteria criteria = new Criteria();			
				String email = bean.getFieldName(IWebMailAlias.CONTACT_EMAIL);
				String contacts = bean.getFieldName(IWebMailAlias.CONTACT_CONTACTS);
				Expression exp1 = ExpressionUtilities.getNotNullExpression(email);
				Expression exp2 = ExpressionUtilities.getNotNullExpression(contacts);
				criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));				
				criteria.addOrder(bean.getFieldName(IWebMailAlias.CONTACT_DISPLAY_NAME));
				List<ITransferObject> lst = bean.getList(criteria);
	            for (int i = 0, max = lst.size(); i < max; i++) {
	            	SelectionEmail se = new SelectionEmail();
	            	se.setEmail((Contact) lst.get(i));
	            	se.setSelected(Boolean.FALSE);
	            	emails.add(se);
	            }
	    	} catch (ManagerBeanException e) {
	    		LOGGER.log( Level.SEVERE, e.getMessage(), e );
			}
	    	this.model = new ListDataModel( emails );
		} else {
			for( SelectionEmail email : emails ) {
				email.setSelected( false );
			}
		}
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

	private void setSelectAllPageContacts( boolean value ) {
		int start = (this.currentPage - 1) * this.pageSize;
		for( int i = 0; i < this.pageSize; i++ ) {
			this.emails.get(i+start).setSelected(value);
		}
	}	
	
	public void onSelectAllPageContacts(ActionEvent event) {
		setSelectAllPageContacts(true);
	}

	public void onDeselectAllPageContacts(ActionEvent event) {
		setSelectAllPageContacts(false);
	}
	
	/**
	 * @return the selectedRows
	 */
	public List<Contact> getSelectedRows() {
	    List<Contact> selectedRows = new ArrayList<Contact>();
        for (int i = 0; i < emails.size(); i++) {
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
