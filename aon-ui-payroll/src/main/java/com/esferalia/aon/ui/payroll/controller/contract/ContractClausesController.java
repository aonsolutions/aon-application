package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contract.pdf.clauses.Clauses;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;


public class ContractClausesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractClausesController.class.getName());

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
	
	public List<SelectItem> getClausesList() throws ManagerBeanException {
		List<SelectItem> enterpriseClausesList = new LinkedList<SelectItem>();
		for( ITransferObject to : obtainEnterpriseClausesList() ) {
			RegistryAttachment attach = (RegistryAttachment) to;
			SelectItem item = new SelectItem(attach, attach.getDescription());
			enterpriseClausesList.add(item);			
		}
		return enterpriseClausesList;
	}
	
	private void initialize() {
		setUseEnterpriseClauses(false);
		setSelectedClause(null);
		setAdditionalClauses(null);
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
		PayrollUtils utils = PayrollUtils.getInstance();
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), utils.getCurrentDomainEnterprise().getRegistry().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ENTERPRISE_CONTRACT_CLAUSES);
		return bean.getList(criteria);
	}
	
	public void onAdditionalClausesShow(ActionEvent event) throws ManagerBeanException{
		initialize();
		ContractPdfController pdfDocument = (ContractPdfController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_PDF_CONTROLLER_NAME);
		try {
			pdfDocument.setDocumentType(ContractAttachmentType.CONTRACT_CLAUSES);
			pdfDocument.loadDocument(false);
			if(pdfDocument.isNew()){
				if(pdfDocument.getContract()!=null && obtainEnterpriseClausesCount()>0){
					setUseEnterpriseClauses(true);
				}
			} else {
				setAdditionalClauses(pdfDocument.getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(Clauses.CLAUSES).getValue());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de la copia basica");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (UnsupportedContractDocumentException e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de la copia basica");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onGenerateAdditionalClause(ActionEvent event){
		generateAdditionalClauseDocument();
	}
	
	public void generateAdditionalClauseDocument(){
		ContractPdfController pdfDocument = (ContractPdfController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_PDF_CONTROLLER_NAME);
		try {
			pdfDocument.getContractPdfWriter().getPdfDocument().getPdfFieldsMap().get(Clauses.CLAUSES).setValue(getAdditionalClauses());
			pdfDocument.saveDocument();
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("No se ha podido generar el documento de la copia basica");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
}
