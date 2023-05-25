package com.code.aon.webmail.db;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.webmail.IContact;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ContactDB;

@Entity
@Table(name="contact")
public class Contact extends ContactDB implements IContact {

	private final static Logger LOGGER = LoggerFactory.getLogger(Contact.class);
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Boolean contactGroup;
	
	private List<Contact> contacts;
	
	private Set<ContactDetail> details = new HashSet<ContactDetail>();

	@OneToMany(mappedBy = "contact", cascade={CascadeType.REMOVE})
	public Set<ContactDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<ContactDetail> details) {
		this.details = details;
	}

	@Override
	@Transient
	public Boolean getContactGroup() {
		if ( contactGroup == null ) {
			contactGroup = getContactData()==null || getContactData().getId() == null;
		}
		return contactGroup;
	}

	@Override
	public void setContactGroup(Boolean contactGroup) {
		this.contactGroup = contactGroup;
	}
	
	@Transient
	public String getEmails() {
		if ( getContactGroup() ) {
			List<String> list = new LinkedList<String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContactDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTACT_DETAIL_CONTACT_GROUP_ID), getId());
				for ( ITransferObject to : bean.getList(criteria) ) {
					ContactDetail cd = (ContactDetail) to;
					list.add( cd.getContact().getEmail() );
				}
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error getting emails from " + getOutlookName(), e );
			}
			String emails = StringUtils.join( list, ", " );
			return StringUtils.trimToNull( emails );
		} 
		return getEmail();
	}	

	@Transient
	public List<Contact> getContacts() {
		if ( contacts == null ) {
			contacts = new LinkedList<Contact>();
			for ( ContactDetail cd : getContactDetails() ) {
				contacts.add( cd.getContact() );
			}
		}
		return contacts;
	}
	
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	@Transient
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<ContactDetail> getContactDetails() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContactDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTACT_DETAIL_CONTACT_GROUP_ID), getId());
			return (List) bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error getting emails from " + getOutlookName(), e );
		}		
		return null;
	}

	public void insertContacts( boolean _new) throws ManagerBeanException {
		List<Contact> newContacts = new LinkedList<Contact>(this.contacts);
		IManagerBean bean = BeanManager.getManagerBean(ContactDetail.class);
		if (! _new ) {
			List<ContactDetail> oldContacts = getContactDetails();
			for( ContactDetail cd : oldContacts ) {
				if ( newContacts.contains(cd.getContact()) ) {
					newContacts.remove(cd.getContact());
				} else {
					bean.remove(cd);
				}
			}
		}
		for( Contact contact : newContacts ) {
			ContactDetail cd = new ContactDetail();
			cd.setContactGroup(this);
			cd.setContact(contact);
			bean.insert(cd);		
		}
	}
	
	public void deleteContacts() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContactDetail.class);
		List<ContactDetail> contacts = getContactDetails();
		for( ContactDetail cd : contacts ) {
			bean.remove(cd);
		}
	} 
	
	private String getEmailLarge( String displayName, String email ) {
		if ( email != null ) {
			String name = displayName;
			if (! StringUtils.isAsciiPrintable(name) ) {
				name = "\"" + name + "\"";
			}
			return name + " <" + email + ">";			
		}
		return null;
	}

	@Override
	@Transient
	public String getEmailLarge() {
		if ( getContactGroup() ) {
			List<String> list = new LinkedList<String>();
			if ( getContacts() != null ) {
				for( Contact gc : getContacts() ) {
					if ( ! StringUtils.isBlank(gc.getEmail()) ) {
						String email = getEmailLarge( gc.getDisplayName(), gc.getEmail() );
						list.add( email );						
					}
				}				
			}
			String emails = StringUtils.join( list, ", " );
			return StringUtils.trimToNull( emails );			
		}
		return getEmailLarge( getDisplayName(), getEmail() );
	}

	@Override
	@Transient
	public String getEmailSummary() {
		String emails = getEmails();
		return StringUtils.abbreviate( emails, 80 );
	}
	
	@Override
	@Transient
	public String getSurname() {
		return getContactData().getSurname();
	}

	@Override
	public void setSurname(String surname) {
		getContactData().setSurname(surname);
	}

	@Override
	@Transient
	public String getOutlookName() {
		return getContactData().getOutlookName();
	}

	@Override
	public void setOutlookName(String outlookName) {
		getContactData().setOutlookName(outlookName);
	}

	@Override
	@Transient
	public String getOrganization() {
		return getContactData().getOrganization();
	}

	@Override
	public void setOrganization(String organization) {
		getContactData().setOrganization(organization);
	}

	@Override
	@Transient
	public String getPhone() {
		if ( getContactData() != null ) {
			return getContactData().getPhone();	
		}
		return null;
	}

	@Override
	public void setPhone(String phone) {
		getContactData().setPhone(phone);
	}

	@Override
	@Transient
	public String getCellularPhone() {
		if ( getContactData() != null ) {
			return getContactData().getCellularPhone();	
		}
		return null;
	}

	@Override
	public void setCellularPhone(String cellularPhone) {
		getContactData().setCellularPhone(cellularPhone);
	}

	@Override
	@Transient
	public String getFax() {
		return getContactData().getFax();
	}

	@Override
	public void setFax(String fax) {
		getContactData().setFax(fax);
	}

	@Override
	@Transient
	public String getEmail() {
		if ( getContactData() != null ) {
			return getContactData().getEmail();	
		}
		return null;
	}

	@Override
	public void setEmail(String email) {
		getContactData().setEmail(email);
	}

	@Override
	@Transient
	public String getAddress() {
		return getContactData().getAddress();
	}

	@Override
	public void setAddress(String address) {
		getContactData().setAddress(address);
	}

	@Override
	@Transient
	public String getPostalCode() {
		return getContactData().getPostalCode();
	}

	@Override
	public void setPostalCode(String postalCode) {
		getContactData().setPostalCode(postalCode);
	}

	@Override
	@Transient
	public String getNote() {
		return getContactData().getNote();
	}

	@Override
	public void setNote(String note) {
		getContactData().setNote(note);
	}

	@Override
	@Transient
	public String getOutlookCity() {
		return getContactData().getOutlookCity();
	}

	@Override
	public void setOutlookCity(String outlookCity) {
		getContactData().setOutlookCity(outlookCity);
	}

	@Override
	@Transient
	public String getState() {
		return getContactData().getState();
	}

	@Override
	public void setState(String state) {
		getContactData().setState(state);		
	}

	@Override
	@Transient
	public String getOrganizationPhone() {
		return getContactData().getOrganizationPhone();
	}

	@Override
	public void setOrganizationPhone(String organizationPhone) {
		getContactData().setOrganizationPhone(organizationPhone);
	}

	@Override
	@Transient
	public String getOrganizationFax() {
		return getContactData().getOrganizationFax();
	}

	@Override
	public void setOrganizationFax(String organizationFax) {
		getContactData().setOrganizationFax(organizationFax);
	}

	@Override
	@Transient
	public String getOrganizationAddress() {
		return getContactData().getOrganizationAddress();
	}

	@Override
	public void setOrganizationAddress(String organizationAddress) {
		getContactData().setOrganizationAddress(organizationAddress);	
	}

	@Override
	@Transient
	public String getOrganizationPostalCode() {
		return getContactData().getOrganizationPostalCode();
	}

	@Override
	public void setOrganizationPostalCode(String organizationPostalCode) {
		getContactData().setOrganizationPostalCode(organizationPostalCode);
	}

	@Override
	@Transient
	public String getOrganizationCity() {
		return getContactData().getOrganizationCity();
	}

	@Override
	public void setOrganizationCity(String organizationCity) {
		getContactData().setOrganizationCity(organizationCity);
	}

	@Override
	@Transient
	public String getOrganizationState() {
		return getContactData().getOrganizationState();
	}

	@Override
	public void setOrganizationState(String organizationState) {
		getContactData().setOrganizationState(organizationState);
	}

	@Override
	@Transient
	public String getWeb() {
		return getContactData().getWeb();
	}

	@Override
	public void setWeb(String web) {
		getContactData().setWeb(web);
	}

	@Override
	@Transient
	public String getCountry() {
		return getContactData().getCountry();
	}

	@Override
	public void setCountry(String country) {
		getContactData().setCountry(country);
	}

	@Override
	@Transient
	public String getTitle() {
		return getContactData().getTitle();
	}

	@Override
	public void setTitle(String title) {
		getContactData().setTitle(title);
	}

}
