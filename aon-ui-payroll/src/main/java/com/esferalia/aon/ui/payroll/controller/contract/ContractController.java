package com.esferalia.aon.ui.payroll.controller.contract;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import com.code.aon.company.EnterpriseActivity;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractDuration;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ContractController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class.getName());
	private static final int MAX_FILE_SIZE_MB = 3;
	private static final int MAX_FILE_SIZE = MAX_FILE_SIZE_MB*1024*1024;
	
	private Enterprise enterprise;
	private List<SelectItem> workPlaces;
	private List<SelectItem> enterpriseCCCs;
	private List<SelectItem> activities;
	private ContractOption contractOption;
	private ContractType contractType;
	private ContractCode contractCode;
	private QuoteGroup quoteGroup;
	private Double irpf;
	private AonFile aonFile;
	
	private ContractDuration contractDuration;
	private ContractWorkingDay contractWorkingDay;
	private String tc2Code;
	
	
	public String getTc2Code() {
		return tc2Code;
	}
	public void setTc2Code(String tc2Code) {
		this.tc2Code = tc2Code;
	}
	public ContractDuration getContractDuration() {
		return contractDuration;
	}
	public void setContractDuration(ContractDuration contractDuration) {
		this.contractDuration = contractDuration;
	}
	public ContractWorkingDay getContractWorkingDay() {
		return contractWorkingDay;
	}
	public void setContractWorkingDay(ContractWorkingDay contractWorkingDay) {
		this.contractWorkingDay = contractWorkingDay;
	}
	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public Double getIrpf() {
		return irpf;
	}
	public void setIrpf(Double irpf) {
		this.irpf = irpf;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public List<SelectItem> getWorkPlaces() {
		if(workPlaces==null){
			loadWorkPlaces();
		}
		return workPlaces;
	}
	public void setWorkPlaces(List<SelectItem> workPlaces) {
		this.workPlaces = workPlaces;
	}
	
	public List<SelectItem> getEnterpriseCCCs() {
		if(enterpriseCCCs==null){
			loadEnterpriseCCCs();
		}
		return enterpriseCCCs;
	}
	public void setEnterpriseCCCs(List<SelectItem> enterpriseCCCs) {
		this.enterpriseCCCs = enterpriseCCCs;
	}

	public List<SelectItem> getActivities() {
		if(activities==null){
			loadActivities();
		}
		return activities;
	}
	public void setActivities(List<SelectItem> activities) {
		this.activities = activities;
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

	public ContractCode getContractCode() {
		return contractCode;
	}
	public void setContractCode(ContractCode contractCode) {
		this.contractCode = contractCode;
	}

	public QuoteGroup getQuoteGroup() {
		return quoteGroup;
	}
	public void setQuoteGroup(QuoteGroup quoteGroup) {
		this.quoteGroup = quoteGroup;
	}
	
	public void onShowVariables( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			IController c = FormUtil.getController(IPayrollConstants.CONTRACT_DATA_CONTROLLER);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	
	public void onShowPayments( ActionEvent event ) {
		ContractPaymentController c = (ContractPaymentController) FormUtil.getController(IPayrollConstants.CONTRACT_PAYMENT_CONTROLLER);
		c.reset(false);
		c.initialize();
	}
	
	public void onShowDeductions( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			ContractDeductionController c = (ContractDeductionController) FormUtil.getController(IPayrollConstants.CONTRACT_DEDUCTION_CONTROLLER);
			c.reset(false);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_DEDUCTION_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las deducciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	public void onShowEmbargos( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			ContractEmbargoController c = (ContractEmbargoController) FormUtil.getController(IPayrollConstants.CONTRACT_EMBARGO_CONTROLLER);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_EMBARGO_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar los embargos del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	public void onShowBonus( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			ContractBonusController c = (ContractBonusController) FormUtil.getController(IPayrollConstants.CONTRACT_BONUS_CONTROLLER);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_BONUS_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las bonificaciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	
	public void onEnterpriseChanged( LookupChangeEvent event ) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			setEnterprise((Enterprise)event.getNewValue());
		} else {
			setEnterprise(null);
		}
		
		setActivities(null);
		setWorkPlaces(null);
		setEnterpriseCCCs(null);
	}
	
	public void onActivityChanged( ActionEvent event ) {
		setEnterpriseCCCs(null);
	}

	private void loadWorkPlaces() {
		setWorkPlaces(new LinkedList<SelectItem>());
		Contract contract = (Contract) getTo();
		contract.setWorkPlace(null);
		if (getEnterprise() != null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to : list) {
					WorkPlace w = (WorkPlace)to; 
					String name = w.getDescription();
					SelectItem item = new SelectItem(w, name);
					getWorkPlaces().add(item);
				}
				if ( list.size() > 0 ) {
					contract.setWorkPlace((WorkPlace)list.get(0));
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los Centros de Trabajo de la empresa. (" + e.getMessage() +")";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}						
		}
	}

	private void loadActivities() {
		setActivities( new LinkedList<SelectItem>());
		Contract contract = (Contract) getTo();
		contract.setActivity(null);
		if (getEnterprise() != null) {
			try {
				IManagerBean ecBean = BeanManager.getManagerBean(EnterpriseActivity.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(ecBean.getFieldName(ICompanyAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), getEnterprise().getId());
				List<ITransferObject> ecList = ecBean.getList(criteria);
				for(ITransferObject to: ecList){
					EnterpriseActivity ea = (EnterpriseActivity) to;
					String name = ea.getDescription() + " - (" + ea.getCnae().getCode() + ") " + ea.getCnae().getTitle();
					SelectItem item = new SelectItem(ea, name);
					getActivities().add(item);
				}
				if ( ecList.size() > 0 ) {
					contract.setActivity((EnterpriseActivity)ecList.get(0));
				}
				
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar las Actividades de la empresa. (" + e.getMessage() +")";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}						
		}
	}

	private void loadEnterpriseCCCs() {
		setEnterpriseCCCs( new LinkedList<SelectItem>());
		Contract contract = (Contract) getTo();
		contract.setEnterpriseCCC(null);
		if (contract.getActivity() != null && contract.getActivity().getId() != null) {
			try {
				IManagerBean ecBean = BeanManager.getManagerBean(EnterpriseCCC.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(ecBean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ID), contract.getActivity().getId());
				List<ITransferObject> ecList = ecBean.getList(criteria);
				for(ITransferObject to: ecList){
					EnterpriseCCC ccc = (EnterpriseCCC) to;
					String name = ccc.getCcc();
					SelectItem item = new SelectItem(ccc, name);
					getEnterpriseCCCs().add(item);
				}
				if ( ecList.size() > 0 ) {
					contract.setEnterpriseCCC((EnterpriseCCC)ecList.get(0));
				}
				
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los CCC de la empresa. (" + e.getMessage() +")";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}						
		}
	}

//	 * ************************************
//	 * 			DOWNLOAD & UPLOAD METHODS		
//	 * ************************************
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
		searchContractAttachDocument(c);
		byte[] buffer = getAonFile().getData();
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
	public void onFileUploaded( ActionEvent event ) {
		if(getAonFile().getSize()>MAX_FILE_SIZE){
			setAonFile(null);
			String msg = "El tamaño del archivo excede de lo permitido ("+MAX_FILE_SIZE_MB+" Mb)";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		saveContractAttach(getAonFile().getData());
	}
	
	private void saveContractAttach(byte[] data) {
		try {
			ContractAttachment attach = new ContractAttachment();
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			attach.setContract((Contract) this.getTo());
			attach.setData(data);
			attach.setAttachDate(new Date());
			attach.setAttachmentType(ContractAttachmentType.PDF_DOCUMENT);
			attach.setMimeType(MimeType.MIME_PDF);
			attach.setDescription("documento_contrato");
			bean.insertOrUpdate(attach);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			// NADA, no se guarda el documento
		}
	}
	
	public void searchContractAttachDocument(Contract contract) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.PDF_DOCUMENT);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				ContractAttachment attach = (ContractAttachment) list.get(0);
				AonFile f = new AonFile();
				f.setData(attach.getData());
				f.setFileName( attach.getDescription() );
				f.setMimeType( attach.getMimeType() );
				setAonFile(f);
			} else {
				setAonFile(null);
			}
		} catch (ManagerBeanException e) {
			// NADA, el documento se queda vacio
		}
	}
	
	public boolean getExistDocument(){
		try {
			if(this.getModel().isRowAvailable()){
				Contract c = (Contract)this.getModel().getRowData();
				setAonFile(null);
				searchContractAttachDocument(c);
				if(getAonFile()!=null){
					return true;
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener el documento. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
		}
		return false;
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
	 
//	 * ************************************
//	 * 			TREE METHODS		
//	 * ************************************
	 
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
	
	public void onLoadCalendar( ActionEvent event ) {
		// TODO implementar la busqueda del calendario. si la entidad no tiene calendario, 
		// buscar el calendario en sus entidades superiores: contract -> workplace -> enterprise -> agreement
		Contract c = (Contract) getTo();
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(ICompanyConstants.CALENDAR_CONTROLLER_NAME);
		controller.setEnterpriseName(c.getWorkPlace().getEnterprise().getRegistry().getFullName());
		controller.setWorkPlaceName(c.getWorkPlace().getDescription());
		controller.setContractName(c.getPerson().getFullName());
		controller.setCalendarId(c.getCalendar().getId());
		controller.onInitialize(event);
	}	
}
