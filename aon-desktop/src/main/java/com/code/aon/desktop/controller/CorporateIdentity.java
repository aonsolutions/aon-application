package com.code.aon.desktop.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.desktop.report.CompanyReport;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;

public class CorporateIdentity implements ICollectionProvider{

	private CompanyReport companyReport;

	public CompanyReport getCompanyReport() {
		return companyReport;
	}

	public CorporateIdentity()  {
		companyReport = new CompanyReport();
        try {
        	Company company = recoverCompany();
        	if (company!=null){
        		companyReport.setCompany(company);
        		companyReport.setAddress(recoverCompanyAddress(company));
        		companyReport.setCellular(recoverCompanyMediasString(company,MediaType.CELLULAR));
        		companyReport.setEmail(recoverCompanyMediasString(company,MediaType.EMAIL));
        		companyReport.setFax(recoverCompanyMediasString(company,MediaType.FAX));
        		companyReport.setPhone(recoverCompanyMediasString(company,MediaType.FIXED_PHONE));
        		companyReport.setWeb(recoverCompanyMediasString(company,MediaType.WEB));
        	}
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
	}

	private Company recoverCompany() throws ManagerBeanException{
    	IManagerBean bean = BeanManager.getManagerBean(Company.class);
        List list = bean.getList(null);
        if (list.size() > 0) {
            return (Company)list.get(0);
        }
        return null;
	}

	private RegistryAddress recoverCompanyAddress(Company company) throws ManagerBeanException{
    	IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), company.getId());
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
    	List list = bean.getList(criteria);
    	if (list.isEmpty())
    		return null;
    	return (RegistryAddress)list.iterator().next();
	}

	private String recoverCompanyMediasString(Company company, MediaType type_) throws ManagerBeanException{
		String data_ = new String();
		List<ITransferObject> list = recoverCompanyMedias(company, type_);
		for (ITransferObject transferObject : list) {
			data_ += ((RegistryMedia)transferObject).getValue();
			data_ += " ";
		}
		return data_;
	}

	private List<ITransferObject> recoverCompanyMedias(Company company, MediaType type_) throws ManagerBeanException{
    	IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), company.getId());
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE), type_);
        return bean.getList(criteria);
	}

	public Collection getCollection() {
		List<CompanyReport> list = new LinkedList<CompanyReport>();
		list.add(companyReport);
		return list;
	}

	public Collection getCollection(boolean arg0) throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the attach as input stream.
	 * 
	 * @return the attach as input stream
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws IOException the IO exception
	 */
	public InputStream getAttachAsInputStream() throws IOException, ManagerBeanException{
		File file = File.createTempFile("image", ".tmp");
		RegistryAttachment attach = obtainCompanyLogo();
		if(attach != null){
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(attach.getData());
			outputStream.close();
			return new FileInputStream(file);
		}
		return null;
	}

	/**
	 * Obtains company logo.
	 * 
	 * @return the registry attachment
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	private RegistryAttachment obtainCompanyLogo() throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), companyReport.getCompany().getId());
		Iterator iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
}
