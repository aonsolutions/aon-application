package com.code.aon.ui.messaging.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.dao.IWebMailAlias;

public class SMSContactController {

	private static final String BEAN_CONTACT = "contact";
	
    private Criteria criteria = new Criteria();
	private ListDataModel model;
	private List<SelectionContact> contacts = new ArrayList<SelectionContact>();
	
	private String displayName, name, surname;
	
	public ListDataModel getModel() {
		return model;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void onClear(ActionEvent event) {
		criteria = new Criteria();
	}

	@SuppressWarnings("unchecked")
	public void onSearch(ActionEvent event) {
		try {
			init();
			addExpressions(); 
			IManagerBean bean = FormUtil.getController( BEAN_CONTACT ).getManagerBean();
			criteria.addOrder( bean.getFieldName( IWebMailAlias.CONTACT_NAME ) );
			List lst = bean.getList( criteria );
            for (int i = 0, max = lst.size(); i < max; i++) {
            	SelectionContact sc = new SelectionContact();
            	sc.setContact( (Contact) lst.get(i) );
            	sc.setSelected( Boolean.FALSE );
            	contacts.add( sc );
            }
    	} catch (ManagerBeanException e) {
		}
    	this.model = new ListDataModel( contacts );
	}

	public void onToggleSelected(ActionEvent event) {
		if ( model.isRowAvailable() ) {
			SelectionContact selectionEmail = (SelectionContact) model.getRowData();
			selectionEmail.setSelected(! selectionEmail.isSelected() );
		}
	}	
	
	/**
	 * @return the selectedRows
	 */
	public List<Contact> getSelectedRows() {
	    List<Contact> selectedRows = new ArrayList<Contact>();
        selectedRows.clear();
        for (int i = contacts.size()-1; i >= 0 ; i--) {
            if (contacts.get(i).isSelected()) {
                selectedRows.add(contacts.get(i).getContact());
            }
        }
		return selectedRows;
	}

	public void init() {
		this.criteria = new Criteria();
	    this.contacts.clear();
		this.model = null;
	}

	public class SelectionContact{
		
		private boolean selected;
		
		private Contact contact;

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
		 * Checks if is read only selected. 
		 * Necesario debido a un error en Rich Faces 3.2.0.SR1.
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
		 * @return the contact
		 */
		public Contact getContact() {
			return contact;
		}

		/**
		 * @param the contact
		 */
		public void setContact(Contact contact) {
			this.contact = contact;
		}
		
	}

	private void addExpressions() throws ManagerBeanException {
		if (! StringUtils.isEmpty(displayName) ) { 
			addExpression( "Contact_displayName", displayName );
		}
		if (! StringUtils.isEmpty(name) ) {
			addExpression( "Contact_name", name );
		}
		if (! StringUtils.isEmpty(surname) ) { 
			addExpression( "Contact_surname", surname);
		}
	}

	private void addExpression(String key, String value) throws ManagerBeanException {
		try {
			if (value.length() > 0) {
				IManagerBean bean = FormUtil.getController( BEAN_CONTACT ).getManagerBean();
				criteria.addExpression( bean.getFieldName( key ), value);
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException();
		}
	}

}
