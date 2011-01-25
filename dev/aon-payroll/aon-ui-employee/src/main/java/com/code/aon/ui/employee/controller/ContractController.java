package com.code.aon.ui.employee.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractData;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.employee.enumeration.ContractCode;
import com.code.aon.employee.enumeration.ContractOption;
import com.code.aon.employee.enumeration.ContractType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.EnterpriseTree;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ContractController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class.getName());
	
	private Enterprise enterprise;
	private ContractData contractData;
	private List<SelectItem> workPlaces;
	private List<SelectItem> enterpriseCCCs;
	private ContractOption contractOption;
	private ContractType contractType;
	private boolean autonomous;
	
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public ContractData getContractData() {
		return contractData;
	}

	public void setContractData(ContractData contractData) {
		this.contractData = contractData;
	}

	public List<SelectItem> getWorkPlaces() {
		if(workPlaces==null){
			workPlaces = new LinkedList<SelectItem>();
		}
		return workPlaces;
	}

	public void setWorkPlaces(List<SelectItem> workPlaces) {
		this.workPlaces = workPlaces;
	}
	
	public List<SelectItem> getEnterpriseCCCs() {
		if(enterpriseCCCs==null){
			enterpriseCCCs = new LinkedList<SelectItem>();
		}
		return enterpriseCCCs;
	}

	public void setEnterpriseCCCs(List<SelectItem> enterpriseCCCs) {
		this.enterpriseCCCs = enterpriseCCCs;
	}

	public ContractOption getContractOption() {
		return contractOption;
	}

	public void setContractOption(ContractOption contractOption) {
		this.contractOption = contractOption;
	}

	public ContractType getContractType() {
		return contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}
	
	public boolean isAutonomous() {
		return autonomous;
	}

	public void setAutonomous(boolean autonomous) {
		this.autonomous = autonomous;
	}

	public List<SelectItem> getContractCodes() {
		List<SelectItem> list=null;
		list = new LinkedList<SelectItem>();
		if(getContractType()!=null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (ContractCode c : getContractType().getCodes()) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				list.add(item);
			}
		}
		return list;
	}
	public List<SelectItem> getContractTypes() {
		List<SelectItem> list;
		list = new LinkedList<SelectItem>();
		if(getContractOption()!=null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (ContractType t : getContractOption().getTypes()) {
				String name = t.getName(locale);
				SelectItem item = new SelectItem(t, name);
				list.add(item);
			}
		}
		return list;
	}

	public void onDownloadSelected( ActionEvent event ) {
		try {
			Contract c = (Contract) getTo();
			download(c);
		} catch (IOException e) {
			String msg = "Imposible descargar el Contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onDownloadContract( ActionEvent event ) {
		try {
			Contract c = (Contract)this.getModel().getRowData();
			download(c);
		} catch (IOException e) {
			String msg = "Imposible descargar el Contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ManagerBeanException e) {
			String msg = "Imposible descargar el Contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	private void download(Contract c) throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		byte[] buffer = c.getDocument();
		InputStream in = new ByteArrayInputStream(buffer);
		int bytes = in.read(buffer);
		while (bytes != -1) {
			response.getOutputStream().write(buffer, 0, bytes);
			bytes = in.read(buffer);
		}
		in.close();
		response.setContentType(MimeType.MIME_PDF.getName()); 
		response.flushBuffer();
		context.responseComplete();
	}
    
	public void onEdit( ActionEvent event ) {
		try {
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
			select( event, tree.getContract() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onEdit exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}						
	}    
	public void onShowPayments( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			ContractPaymentController c = (ContractPaymentController) FormUtil.getController("contractPayment");
			c.reset(false);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IEmployeeAlias.CONTRACT_PAYMENT_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las percepciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	public void onShowDeductions( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			ContractDeductionController c = (ContractDeductionController) FormUtil.getController("contractDeduction");
			c.reset(false);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las deducciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	public void onShowDetails( ActionEvent event ) {
		AonUtil.addWarningMessage("No implementado");
		throw new AbortProcessingException("No implementado");
	}

	public void onEnterpriseChanged( LookupChangeEvent event ) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			setEnterprise((Enterprise)event.getNewValue());
			try {
				loadWorkPlaces();
				loadEnterpriseCCCs();
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los centros de trabajo (" + e.getMessage() +")";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}
		} else {
			setEnterprise(null);
		}
	}
	
	private void loadWorkPlaces() throws ManagerBeanException{
		workPlaces = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			WorkPlace w = (WorkPlace)to; 
			String name = w.getDescription();
			SelectItem item = new SelectItem(w, name);
			workPlaces.add(item);
		}
	}

	private void loadEnterpriseCCCs() throws ManagerBeanException {
		IManagerBean ecBean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(ecBean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), getEnterprise().getId());
		List<ITransferObject> ecList = ecBean.getList(criteria);
		enterpriseCCCs = new LinkedList<SelectItem>();
		for(ITransferObject to: ecList){
			EnterpriseCCC ccc = (EnterpriseCCC) to;
			String name = ccc.getCcc();
			SelectItem item = new SelectItem(ccc, name);
			enterpriseCCCs.add(item);
		}
	}
	
	public void onWorkPlaceChanged( ActionEvent event ) {
//		if(getAonFile().getSize()>FILE_SIZE){
//			
//		}
	}
	
	
	
	private static final long FILE_SIZE = 2*1024*1024;
	
	private AonFile aonFile;
	
	public AonFile getAonFile() {
		return this.aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	
	public void fileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName( item.getFileName() );
			f.setMimeType( MimeType.get(item.getContentType()) );
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
}
