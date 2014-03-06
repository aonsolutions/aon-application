package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.IContact;

public class MultiSelectionEmailBean extends DataScrollerState {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectionEmail> emails; 

	public MultiSelectionEmailBean() {
		setPageLimit(10);
	}

	public void init( boolean loadContacts ) {
		if ( loadContacts ) {
	        emails = new ArrayList<SelectionEmail>();
    		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
			List<IContact> contacts = mailConfig.getContact().getEmailContacts();
			if ( contacts != null ) {
	            for ( IContact contact : contacts ) {
	            	SelectionEmail se = new SelectionEmail();
	            	se.setEmail( contact );
	            	se.setSelected(Boolean.FALSE);
		            emails.add(se);
	            }					
			}
	    	setModel(new SerializableListDataModel(emails));
		} else {
			for( SelectionEmail email : emails ) {
				email.setSelected( false );
			}
		}
    }
	
	public void onSelect(ActionEvent event) {
		if ( getDirectModel().isRowAvailable() ) {
			SelectionEmail selectionEmail = (SelectionEmail) getDirectModel().getRowData();
			selectionEmail.setSelected(! selectionEmail.isSelected() );
		}
	}	

	private void setSelectAllPageContacts( boolean value ) {
		int start = (getPage() - 1) * getPageLimit();
		int last = Math.min(getDirectModel().getRowCount(), getPageLimit());
		for( int i = 0; i < last; i++ ) {
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
	public List<IContact> getSelectedRows() {
	    List<IContact> selectedRows = new ArrayList<IContact>();
	    for( SelectionEmail se : emails ) {
	    	if ( se.isSelected() ) {
                selectedRows.add(se.getEmail());	    		
	    	}
	    }
		return selectedRows;
	}
    
	public class SelectionEmail{
		
		private boolean selected;
		
		private IContact email;

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
		public IContact getEmail() {
			return email;
		}

		/**
		 * @param email the email to set
		 */
		public void setEmail(IContact email) {
			this.email = email;
		}
		
	}


}
