package com.code.aon.webmail.db;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Formula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final long serialVersionUID = 1L;
	
	private Boolean contactGroup = Boolean.FALSE;

	@Override
	@Formula("(select COUNT(*) from contact_detail cd where id = cd.contact_group)")	
	public Boolean getContactGroup() {
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
					list.add( cd.getContactGroup().getEmail() );
				}
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error getting emails from " + getOutlookName(), e );
			}
			String emails = StringUtils.join( list, ", " );
			return StringUtils.trimToNull( emails );
		} 
		return getEmail();
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
		getContactData().setNote(outlookCity);
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
