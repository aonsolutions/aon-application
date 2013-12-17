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
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContractClausesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractClausesController.class.getName());

	private ContractInfo enterpriseClausesInfo;
	
	private boolean useEnterpriseClauses;
	
	private RegistryAttachment selectedEnterpriseClause;

	private String customClauses;

	private boolean showContractClausesWindow;
	
	public boolean isShowContractClausesWindow() {
		return showContractClausesWindow;
	}

	public void setShowContractClausesWindow(boolean showContractClausesWindow) {
		this.showContractClausesWindow = showContractClausesWindow;
	}

	public boolean isUseEnterpriseClauses() {
		return useEnterpriseClauses;
	}

	public void setUseEnterpriseClauses(boolean useEnterpriseClauses) {
		this.useEnterpriseClauses = useEnterpriseClauses;
	}

	public RegistryAttachment getSelectedEnterpriseClause() {
		return selectedEnterpriseClause;
	}

	public void setSelectedEnterpriseClause(RegistryAttachment selectedEnterpriseClause) {
		this.selectedEnterpriseClause = selectedEnterpriseClause;
	}
	
	public String getCustomClauses() {
		return customClauses;
	}

	public void setCustomClauses(String customClauses) {
		this.customClauses = customClauses;
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
		try{
			if( isExistEnterpriseClauses() && enterpriseClausesInfo!=null && StringUtils.isNumeric(enterpriseClausesInfo.getExpression()) ){
				if(enterpriseClausesInfo!=null && !StringUtils.equalsIgnoreCase(enterpriseClausesInfo.getExpression(), Boolean.FALSE.toString())){
					return (IAttachment) obtainEnterpriseClausesList(Integer.parseInt(enterpriseClausesInfo.getExpression())).get(0);
				}
			} else if( StringUtils.isNotBlank(getCustomClauses()) ){
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
		setSelectedEnterpriseClause(null);
		setCustomClauses(null);
	}
	
	public void initialize() {
		reset();
		try {
			enterpriseClausesInfo = obtainClausesContractInfo();
			loadContractCustomClauses();
			
			if( StringUtils.isBlank(getCustomClauses()) && (enterpriseClausesInfo==null || StringUtils.isBlank(enterpriseClausesInfo.getExpression())) ){
				setUseEnterpriseClauses(false);
			} else if( isExistEnterpriseClauses() && enterpriseClausesInfo!=null && StringUtils.isNumeric(enterpriseClausesInfo.getExpression()) ){
				setUseEnterpriseClauses(true);
				setSelectedEnterpriseClause((RegistryAttachment) obtainEnterpriseClausesList(Integer.parseInt(enterpriseClausesInfo.getExpression())).get(0));
			} else {
				setUseEnterpriseClauses(false);
				setSelectedEnterpriseClause(null);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido obtener el texto de clausulas adicionales");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	private ContractInfo obtainClausesContractInfo() throws ManagerBeanException {
		SEPEUtils utils = SEPEUtils.getInstance();
		ContractInfo info = utils.getContractInfoMap(getContract(), getContract().getStartDate(), null).get(ContractVariable.ENTERPRISE_CLAUSES.getValue());
		if( info!=null && info.getId()!=null ){
			return info;
		}
		return null;
	}
	
	private void loadContractCustomClauses() throws ManagerBeanException {
		try {
			ContractPdfController pdfDocument = (ContractPdfController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_PDF_CONTROLLER_NAME);
			pdfDocument.setDocumentType(ContractAttachmentType.CONTRACT_CLAUSES);
			pdfDocument.loadDocument(false);
			if(!pdfDocument.isNew()){
				setCustomClauses(pdfDocument.getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(Clauses.CLAUSES_CONTENT).getValue());
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
	
	public void onAdditionalClausesShow(ActionEvent event){
		initialize();
	}
	
	public void accept(ActionEvent event) {
		accept();
		initialize();
		ContractController contract = (ContractController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER);
		contract.setSelectedTab(ContractController.ADDITIONAL_CLAUSES_TAB_NAME);
	}
	
	public void accept() {
		try {
			if( isUseEnterpriseClauses() ){
				if(getSelectedEnterpriseClause()==null){
					AonUtil.addErrorMessage("Seleccione de la lista las clausulas adicionales del contrato");
					throw new AbortProcessingException("Seleccione de la lista las clausulas adicionales del contrato");
				}
				saveContractInfoEnterpriseClauses(getSelectedEnterpriseClause().getId().toString());
			} else {
				saveContractInfoEnterpriseClauses(Boolean.FALSE.toString());
				if( StringUtils.isNotBlank(getCustomClauses()) ){
					generateAdditionalClauseDocument();
				} else {
					removeContractCustomClauses();
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
			pdfDocument.getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(Clauses.CLAUSES_CONTENT).setValue(getCustomClauses());
			pdfDocument.saveDocument();
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de anexo de clausulas adicionales");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void saveContractInfoEnterpriseClauses(String value) throws ManagerBeanException {
		if(enterpriseClausesInfo==null){
			enterpriseClausesInfo = new ContractInfo();
			enterpriseClausesInfo.setContract(getContract());
			enterpriseClausesInfo.setName(ContractVariable.ENTERPRISE_CLAUSES.getValue());
			enterpriseClausesInfo.setStartDate(new Date());
			enterpriseClausesInfo.setEndDate(null);
		}
		enterpriseClausesInfo.setExpression(value);
		BeanManager.getManagerBean(ContractInfo.class).insertOrUpdate(enterpriseClausesInfo);
	}
	
	public void removeContractEnterpriseClauses() throws ManagerBeanException {
		if(enterpriseClausesInfo!=null && enterpriseClausesInfo.getId()!=null){
			BeanManager.getManagerBean(ContractData.class).remove(enterpriseClausesInfo);
		}
	}

	public void removeContractCustomClauses() throws ManagerBeanException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getContract().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.CONTRACT_CLAUSES);
			List<ITransferObject> list = bean.getList(criteria);
			ContractAttachment attach = null;
			if(!list.isEmpty()){
				attach = (ContractAttachment) list.get(0);
				BeanManager.getManagerBean(ContractAttachment.class).remove(attach.getId());
			}
		} catch (Exception e) {
			AonUtil.addErrorMessage("No se han podido borrar las clausulas personalizadas.");
		}
	}

}
