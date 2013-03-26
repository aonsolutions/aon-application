package com.code.aon.dao.ldap.util;

import com.code.aon.dao.ldap.ILdapTransferObject;

public interface IPerson extends ILdapTransferObject {

	public String getDisplayName();
	public void setDisplayName(String displayName);

	public String getName();
	public void setName(String name);

	public String getSurname();
	public void setSurname(String surname);
	
	public String getOutlookName();
	public void setOutlookName(String outlookName);

	public String getOrganization();
	public void setOrganization(String organization);

	public String getPhone();
	public void setPhone(String phone);

	public String getCellularPhone();
	public void setCellularPhone(String cellularPhone);

	public String getFax();
	public void setFax(String fax);

	public String getEmail();
	public void setEmail(String email);

	public String getAddress();
	public void setAddress(String address);

	public String getPostalCode();
	public void setPostalCode(String postalCode);

	public String getNote();
	public void setNote(String note);

	public String getCity();
	public void setCity(String city);

	public String getOutlookCity();
	public void setOutlookCity(String outlookCity);

	public String getState();
	public void setState(String state);

	public String getCategory();
	public void setCategory(String category);

	public String getOrganizationPhone();
	public void setOrganizationPhone(String organizationPhone);

	public String getOrganizationFax();
	public void setOrganizationFax(String organizationFax);

	public String getOrganizationAddress();
	public void setOrganizationAddress(String organizationAddress);

	public String getOrganizationPostalCode();
	public void setOrganizationPostalCode(String organizationPostalCode);

	public String getOrganizationCity();
	public void setOrganizationCity(String organizationCity);

	public String getOrganizationState();
	public void setOrganizationState(String organizationState);

	public String getWeb();
	public void setWeb(String web);

	public String getCountry();
	public void setCountry(String country);

	public String getTitle();
	public void setTitle(String title);
		
}