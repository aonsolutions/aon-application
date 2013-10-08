package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contract.pdf.clauses.Clauses;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContractClausesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractClausesController.class.getName());

	private ContractData enterpriseClausesData;
	
	private boolean useEnterpriseClauses;
	
	private RegistryAttachment selectedClause;

	private String additionalClauses;

	
	public boolean isUseEnterpriseClauses() {
		return useEnterpriseClauses;
	}

	public void setUseEnterpriseClauses(boolean useEnterpriseClauses) {
		this.useEnterpriseClauses = useEnterpriseClauses;
	}

	public RegistryAttachment getSelectedClause() {
		return selectedClause;
	}

	public void setSelectedClause(RegistryAttachment selectedClause) {
		this.selectedClause = selectedClause;
	}

	public String getAdditionalClauses() {
		return additionalClauses;
	}
	
	public void setAdditionalClauses(String additionalClauses) {
		this.additionalClauses = additionalClauses;
	}
	
	public boolean isExistEnterpriseClauses() throws ManagerBeanException{
		return obtainEnterpriseClausesCount()>0;
	}
	
	public Contract getContract(){
		return (Contract) ((IController)AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER)).getTo();
	}
	
	public List<SelectItem> getClausesList() throws ManagerBeanException {
		List<SelectItem> enterpriseClausesList = new LinkedList<SelectItem>();
		for( ITransferObject to : obtainEnterpriseClausesList() ) {
			RegistryAttachment attach = (RegistryAttachment) to;
			SelectItem item = new SelectItem(attach, attach.getDescription());
			enterpriseClausesList.add(item);			
		}
		if(enterpriseClausesList.size()!=1){
			SelectItem item = new SelectItem(null, " - ");
			enterpriseClausesList.add(0, item);
		}
		return enterpriseClausesList;
	}
	
	public IAttachment getContractClauses(){
		initialize();
		try{
			if( isUseEnterpriseClauses() ){
				return (IAttachment) obtainEnterpriseClausesList(Integer.parseInt(enterpriseClausesData.getExpression())).get(0);
			} else if( StringUtils.isNotBlank(getAdditionalClauses()) ){
				IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getContract().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.CONTRACT_CLAUSES);
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					return (IAttachment) list.get(0);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido obtener el texto de clausulas adicionales");
			AonUtil.addErrorMessage(e.getMessage());
		}	
		return null;
	}
	
	private void reset() {
		setUseEnterpriseClauses(false);
		setSelectedClause(null);
		setAdditionalClauses(null);
	}
	
	private void initialize() {
		reset();
		try {
			enterpriseClausesData = obtainClausesContractData();
			loadContractClauses();
			
			if( StringUtils.isBlank(getAdditionalClauses()) && (enterpriseClausesData==null || StringUtils.isBlank(enterpriseClausesData.getExpression())) ){
				setUseEnterpriseClauses(false);
			} else if( isExistEnterpriseClauses() && enterpriseClausesData!=null && StringUtils.isNumeric(enterpriseClausesData.getExpression()) ){
				setUseEnterpriseClauses(true);
				setSelectedClause((RegistryAttachment) obtainEnterpriseClausesList(Integer.parseInt(enterpriseClausesData.getExpression())).get(0));
			} else {
				setUseEnterpriseClauses(false);
				setSelectedClause(null);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido obtener el texto de clausulas adicionales");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	private ContractData obtainClausesContractData() throws ManagerBeanException {
		SEPEUtils utils = new SEPEUtils();
		ContractData data = utils.getContractDataMap(getContract(), getContract().getStartDate(), null).get(ContextVariable.ENTERPRISE_CLAUSES.getName());
		if( data!=null && data.getId()!=null ){
			return data;
		}
		return null;
	}
	
	private void loadContractClauses() throws ManagerBeanException {
		try {
			ContractPdfController pdfDocument = (ContractPdfController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_PDF_CONTROLLER_NAME);
			pdfDocument.setDocumentType(ContractAttachmentType.CONTRACT_CLAUSES);
			pdfDocument.loadDocument(false);
			if(!pdfDocument.isNew()){
				setAdditionalClauses(pdfDocument.getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(Clauses.CLAUSES_CONTENT).getValue());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido obtener el texto de clausulas adicionales");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (UnsupportedContractDocumentException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido obtener el texto de clausulas adicionales");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private int obtainEnterpriseClausesCount() throws ManagerBeanException {
		PayrollUtils utils = PayrollUtils.getInstance();
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), utils.getCurrentDomainEnterprise().getRegistry().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ENTERPRISE_CONTRACT_CLAUSES);
		return bean.getCount(criteria);
	}

	private List<ITransferObject> obtainEnterpriseClausesList() throws ManagerBeanException{
		return obtainEnterpriseClausesList(null);
	}
	
	private List<ITransferObject> obtainEnterpriseClausesList(Integer attachId) throws ManagerBeanException{
		PayrollUtils utils = PayrollUtils.getInstance();
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		if(attachId !=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_ID), attachId);
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), utils.getCurrentDomainEnterprise().getRegistry().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ENTERPRISE_CONTRACT_CLAUSES);
		return bean.getList(criteria);
	}
	
	public void onAdditionalClausesShow(ActionEvent event) throws ManagerBeanException{
		initialize();
	}
	
	public void accept() {
		try {
			if( isUseEnterpriseClauses() ){
				if(getSelectedClause()==null){
					throw new AbortProcessingException("Seleccione de la lista las clausulas adicionales del contrato");
				}
				saveContractDataEnterpriseClauses(getSelectedClause().getId().toString());
			} else {
				saveContractDataEnterpriseClauses(Boolean.FALSE.toString());
				if( StringUtils.isNotBlank(getAdditionalClauses()) ){
					generateAdditionalClauseDocument();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se han podido guardar los datos de las clausulas adicionales");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void generateAdditionalClauseDocument(){
		ContractPdfController pdfDocument = (ContractPdfController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_PDF_CONTROLLER_NAME);
		try {
			pdfDocument.setDocumentType(ContractAttachmentType.CONTRACT_CLAUSES);
			pdfDocument.loadDocument(false);
			pdfDocument.getContractPdfWriter().buildPdf(false);
			pdfDocument.getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(Clauses.CLAUSES_CONTENT).setValue(getAdditionalClauses());
			pdfDocument.saveDocument();
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de anexo de clausulas adicionales");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void saveContractDataEnterpriseClauses(String value) throws ManagerBeanException {
		if(enterpriseClausesData==null){
			enterpriseClausesData = new ContractData();
			enterpriseClausesData.setContract(getContract());
			enterpriseClausesData.setName(ContextVariable.ENTERPRISE_CLAUSES.getName());
			enterpriseClausesData.setStartDate(new Date());
			enterpriseClausesData.setEndDate(null);
		}
		enterpriseClausesData.setExpression(value);
		BeanManager.getManagerBean(ContractData.class).insertOrUpdate(enterpriseClausesData);
	}
	
	public void removeContractEnterpriseClauses() throws ManagerBeanException {
		if(enterpriseClausesData!=null && enterpriseClausesData.getId()!=null){
			BeanManager.getManagerBean(ContractData.class).remove(enterpriseClausesData);
		}
	}

}
