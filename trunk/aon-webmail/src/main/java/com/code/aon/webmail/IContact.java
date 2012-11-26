package com.code.aon.webmail;



public interface IContact {

	String getEmailLarge();
	String getEmailSummary();
	
	Boolean getContactGroup();
	void setContactGroup(Boolean contactGroup);
	
	String getDisplayName();
	void setDisplayName(String displayName);

	String getSurname();
	void setSurname(String surname);
	
	String getOutlookName();
	void setOutlookName(String outlookName);

	String getOrganization();
	void setOrganization(String organization);

	String getPhone();
	void setPhone(String phone);

	String getCellularPhone();
	void setCellularPhone(String cellularPhone);

	String getFax();
	void setFax(String fax);

	String getEmail();
	void setEmail(String email);

	String getAddress();
	void setAddress(String address);

	String getPostalCode();
	void setPostalCode(String postalCode);

	String getNote();
	void setNote(String note);

	String getOutlookCity();
	void setOutlookCity(String outlookCity);

	String getState();
	void setState(String state);

	String getOrganizationPhone();
	void setOrganizationPhone(String organizationPhone);

	String getOrganizationFax();
	void setOrganizationFax(String organizationFax);

	String getOrganizationAddress();
	void setOrganizationAddress(String organizationAddress);

	String getOrganizationPostalCode();
	void setOrganizationPostalCode(String organizationPostalCode);

	String getOrganizationCity();
	void setOrganizationCity(String organizationCity);

	String getOrganizationState();
	void setOrganizationState(String organizationState);

	String getWeb();
	void setWeb(String web);

	String getCountry();
	void setCountry(String country);

	String getTitle();
	void setTitle(String title);
	
}
