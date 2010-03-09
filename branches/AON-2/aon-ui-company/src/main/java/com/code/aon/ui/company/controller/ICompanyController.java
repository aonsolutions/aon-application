package com.code.aon.ui.company.controller;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.form.IController;

public interface ICompanyController extends IController {

	boolean isPrintHeader();
	void setPrintHeader(boolean printHeader);

	boolean isPrintRecordData();
	void setPrintRecordData(boolean printRecordData);

	boolean obtainPrintHeader() throws ManagerBeanException;

	boolean obtainPrintRecordData() throws ManagerBeanException;

	ApplicationParameter obtainApplicationParameter(String printHeaderParam)  throws ManagerBeanException;

	RegistryAddress getMainAddress();
	void setPhone(RegistryMedia phone);
	void setFax(RegistryMedia fax);
	void setEmail(RegistryMedia email);
	void setWeb(RegistryMedia web);
	void loadAddresses();
	boolean isPhoneDirty();
	RegistryMedia getPhone();
	boolean isFaxDirty();
	RegistryMedia getFax();
	boolean isEmailDirty();
	RegistryMedia getEmail();
	boolean isWebDirty();
	RegistryMedia getWeb();
	boolean isAddressDirty();
	void setAttach(RegistryAttachment obtainRegistryAttachment);

}
