package com.code.aon.ui.company.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the company maintenance.
 */
public class CompanyController extends CompanyParentController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class.getName());
	

	/** The uploaded logo file. */
	private AonFile logoFile;

	/** The uploaded signature file. */
	private AonFile signatureFile;
	
	private String ediSupport;
	private String ediCompanyCode;
	
	

	public String getEdiSupport() {
		return ediSupport;
	}
	public void setEdiSupport(String ediSupport) {
		this.ediSupport = ediSupport;
	}
	
	public boolean isEdiSupportEnabled() {
		return StringUtils.isNotBlank(AppParamUtil
				.getValue(AppParam.EDI_SUPPORT));
	}
	
	public String getEdiCompanyCode() {
		return ediCompanyCode;
	}
	public void setEdiCompanyCode(String ediCompanyCode) {
		this.ediCompanyCode = ediCompanyCode;
	}
	
	/**
	 * Gets the uploaded logo file.
	 * 
	 * @return the file
	 */
	public AonFile getLogoFile() {
		return this.logoFile;
	}
	public AonFile getAonFile() {
		return this.logoFile;
	}

	/**
	 * Sets the logo file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setLogoFile(AonFile logoFile) {
		if ( this.logoFile != null ) {
			this.logoFile.clean();	
		}		
		this.logoFile = logoFile;
	}

	/**
	 * Gets the uploaded signature file.
	 * 
	 * @return the file
	 */
	public AonFile getSignatureFile() {
		return this.signatureFile;
	}
	
	/**
	 * Sets the signature file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setSignatureFile(AonFile signatureFile) {
		if ( this.signatureFile != null ) {
			this.signatureFile.clean();	
		}				
		this.signatureFile = signatureFile;
	}

	
	public void logoFileUploaded(UploadEvent event) {
		setLogoFile(AttachmentUtil.fileUploaded(event));
	}

	public void signatureFileUploaded(UploadEvent event) {
		setSignatureFile(AttachmentUtil.fileUploaded(event));
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createCurrentLogoContent(OutputStream out, Object data) throws IOException {
		if (getLogoFile() != null && (getLogoFile().getSize() >0)) {
			out.write(getLogoFile().getData());
		}
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createLogoContent(OutputStream out, Object data) throws IOException {
		if (getLogoAttach() != null) {
			out.write(getLogoAttach().getData());
		}
	}
	
	public String getLogoMimeType() {
		if ( getLogoAttach()!= null && getLogoAttach().getMimeType() != null ) {
			return getLogoAttach().getMimeType().getName();
		}
		return "*";	
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createCurrentSignatureContent(OutputStream out, Object data) throws IOException {
		if (getSignatureFile() != null && (getSignatureFile().getSize() > 0) ) {
			out.write(getSignatureFile().getData());
		}
	}


	public static Enterprise addEnterprise(Company company, Scope scope) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
		Enterprise enterprise = new Enterprise();
		enterprise.setDomain(company.getDomain());
		enterprise.setRegistry(company);
		enterprise.setScope(scope);
		return (Enterprise) bean.insert(enterprise);
	}

	public static Scope obtainScope( Integer domain ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SCOPE_DOMAIN), domain);
		criteria.addOrder(bean.getFieldName(IEntityAlias.SCOPE_ID));
		List<ITransferObject> list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (Scope) list.get(0);
		}
		return null;
	}
	
	public static void insertWorkPlace(RegistryAddress address, Enterprise enterprise) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		WorkPlace workPlace = new WorkPlace();
		workPlace.setDomain(enterprise.getDomain());
		workPlace.setEnterprise(enterprise);
		workPlace.setScope(enterprise.getScope());
		workPlace.setDescription(ICompanyConstants.PRINCIPAL);
		workPlace.setAddress(address);
		workPlace.setActive(true);
		bean.insert(workPlace);
	}	
	
}