package com.code.aon.ui.company.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.Company;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.controller.PrintParametersController;
import com.code.aon.ui.company.controller.RegistryInfo;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

public class ReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportScriptlet.class.getName());
	
	/**
	 * REPORT TEMPLATE PARAMETERS
	 */
	public static final String PARAM_COMPANY = "company";
	public static final String PARAM_PRINT_HEADER = "printHeader";
	public static final String PARAM_PRINT_RECORD_DATA = "printRecordData";
	public static final String PARAM_PRINT_LOGO = "printLogo";
	public static final String PARAM_LOGO_IMAGE_FILE = "logoImageFile";
	public static final String PARAM_ADDRESS = "address";
	public static final String PARAM_PHONE = "phone";
	public static final String PARAM_FAX = "fax";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_WEB = "web";
	
	/**
	 * REPORT TEMPLATE FIELDS
	 */
	public static final String FIELD_DOMAIN = "domain";
	public static final String FIELD_ID = "id";
	
	
	private RegistryAttachment logoAttach;
	private RegistryAttachment signatureAttach;
	private RecordData recordData;
	private PrintParametersController printParams;
	private RegistryMedia phone;
	private RegistryMedia fax;
	private RegistryMedia email;
	private RegistryMedia web;
	
	
	
	@Override
	public void beforeReportInit() throws JRScriptletException {
		super.beforeReportInit();
		printParams = null;
	}
	
	
	public Company getCompany(){
		return getPrintParamsController().getCompany();
	}

	protected PrintParametersController getPrintParamsController() {
		if(printParams==null){
			loadPrintParams();
		}		
		return printParams;
	}
	
	

	private void loadPrintParams() {
		try {
			if(super.getFieldValue(FIELD_DOMAIN)!=null){
				Integer domain = ((Integer)super.getFieldValue(FIELD_DOMAIN));
				if(super.getFieldValue(FIELD_ID)!=null){
					printParams = new PrintParametersController();
					printParams.onInit(domain);
				} else {
					printParams = (PrintParametersController) AonUtil
							.getRegisteredBean(ICompanyConstants.PRINT_PARAMETERS_CONTROLLER_NAME);
				}
				loadParams();
			}
		} catch (JRScriptletException e) {
			LOGGER.error("report template must have 'domain' field. ", e);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	protected void loadParams() throws ManagerBeanException {
		
		ByteArrayInputStream logo = null;
		try {
			logoAttach = obtainCompanyLogo();
			if(logoAttach != null){
				logo = new ByteArrayInputStream(logoAttach.getData());
			}
		} catch (ManagerBeanException e) {
			String msg = "ERROR: imposible obtener el logo al generar el informe";
			LOGGER.error(msg,e);
		}
		
		try {
			signatureAttach = obtainCompanySignature();
			
		} catch (ManagerBeanException e) {
			String msg = "ERROR: imposible obtener la firma al generar el informe";
			LOGGER.error(msg,e);
		}
		
		IManagerBean recordDataBean = BeanManager.getManagerBean(RecordData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(recordDataBean.getFieldName(IEntityAlias.RECORD_DATA_REGISTRY_ID), getCompany().getId());
		Iterator<ITransferObject> iter = recordDataBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			recordData = (RecordData)iter.next();
		}
		
		Criteria criteriaMedia = new Criteria();
		IManagerBean beanMedia = BeanManager.getManagerBean(RegistryMedia.class);
		String registryIdFieldName = beanMedia.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID);
		criteriaMedia.addEqualExpression(registryIdFieldName, getCompany().getId());
		List<ITransferObject> mediaList = beanMedia.getList(criteriaMedia);
		Iterator<ITransferObject> mediaIter = mediaList.iterator();
		while (mediaIter.hasNext()){
			RegistryMedia rmedia = (RegistryMedia)mediaIter.next();
			switch (rmedia.getMediaType()) {
				case FIXED_PHONE:
					phone = rmedia;
					break;
				case FAX:
					fax = rmedia;
					break;
				case EMAIL:
					email = rmedia;
					break;
				case WEB:
					web = rmedia;
					break;
				default:
					break;
			}
		}
		
		setParameter(PARAM_PRINT_HEADER, isPrintHeader());
		setParameter(PARAM_PRINT_RECORD_DATA, isPrintRecordData());
		setParameter(PARAM_PRINT_LOGO, isPrintLogo());
		setParameter(PARAM_COMPANY, getPrintParamsController().getCompany());
		setParameter(PARAM_ADDRESS, RegistryInfo.getMainAddress(getCompany()));
		setParameter(PARAM_LOGO_IMAGE_FILE, logo);
		setParameter(PARAM_PHONE, phone);
		setParameter(PARAM_FAX, fax);
		setParameter(PARAM_EMAIL, email);
		setParameter(PARAM_WEB, web);
		
	}
	
	protected void setParameter(String key, Object obj){
		try {
			super.parametersMap.get(key).setValue(obj);
		} catch (Exception e) {
			LOGGER.error("Parameter "+key+" does not exist!!!");
		}
	}
	
	public RegistryAddress getAddress() throws ManagerBeanException {
		return RegistryInfo.getMainAddress(getCompany());
	}
	
	public RegistryMedia getPhone() throws ManagerBeanException {
		return phone;
	}
	
	public RegistryMedia getFax() throws ManagerBeanException {
		return fax;
	}
	
	public RegistryMedia getEmail() throws ManagerBeanException {
		return email;
	}
	
	public RegistryMedia getWeb() throws ManagerBeanException {
		return web;
	}
	
	public RecordData getCompanyRecordData() throws ManagerBeanException{
		return recordData;
	}
	
	public InputStream getLogoFile() {
		if(logoAttach != null){
			return new ByteArrayInputStream(logoAttach.getData());
		}
		return null;
	}
	
	public InputStream getSignatureFile() {
		if(signatureAttach != null){
			return new ByteArrayInputStream(signatureAttach.getData());
		}
		return null;
	}
	
	public RegistryAttachment obtainCompanyLogo() throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, getCompany().getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
	
	public RegistryAttachment obtainCompanySignature() throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, getCompany().getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.SIGNATURE);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
	
	public InputStream getSaleInvoiceBackgroundFile() {
		if(getPrintParamsController().getReportBackground().getSaleInvoiceBackgroundFile()!=null){
			byte[] data = getPrintParamsController().getReportBackground().getSaleInvoiceBackgroundFile().getData();
			if(data != null && data.length>0){
				return new ByteArrayInputStream(data);
			}
		}
		return null;
	}
	public InputStream getSalesBackgroundFile() {
		if(getPrintParamsController().getReportBackground().getSalesBackgroundFile()!=null){
			byte[] data = getPrintParamsController().getReportBackground().getSalesBackgroundFile().getData();
			if(data != null && data.length>0){
				return new ByteArrayInputStream(data);
			}
		}
		return null;
	}
	public InputStream getDeliveryBackgroundFile() {
		if(getPrintParamsController().getReportBackground().getDeliveryBackgroundFile()!=null){
			byte[] data = getPrintParamsController().getReportBackground().getDeliveryBackgroundFile().getData();
			if(data != null && data.length>0){
				return new ByteArrayInputStream(data);
			}
		}
		return null;
	}
	public InputStream getOfferBackgroundFile() {
		if(getPrintParamsController().getReportBackground().getOfferBackgroundFile()!=null){
			byte[] data = getPrintParamsController().getReportBackground().getOfferBackgroundFile().getData();
			if(data != null && data.length>0){
				return new ByteArrayInputStream(data);
			}
		}
		return null;
	}
	
	public boolean isPrintHeader() {
		return AppParamUtil.getValueAsBoolean(AppParam.APP_PRINT_HEADER_PARAM);
	}

	public boolean isPrintLogo() {
		return AppParamUtil.getValueAsBoolean(AppParam.APP_PRINT_LOGO_PARAM);
	}
	
	public boolean isPrintRecordData() {
		return AppParamUtil.getValueAsBoolean(AppParam.APP_PRINT_RECORD_DATA_PARAM);
	}
	
	public boolean isPrintProject() {
		return AppParamUtil.getValueAsBoolean(AppParam.APP_PRINT_PROJECT_PARAM);
	}
	
}