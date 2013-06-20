package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContrataBatch;
import com.esferalia.aon.payroll.ContrataBatchAttachment;
import com.esferalia.aon.payroll.ContrataBatchDetail;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.PayrollBatchAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;



public class ContrataBatchController extends BasicController {
	
	private FileOutput fileOutput;
	private boolean recorded;
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	public boolean isRecorded() {
		return recorded;
	}

	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}
	
	public void onInit(ActionEvent event) {
		try {
			checkDiskCreated();
			if(!isRecorded()){
				ContractListController list = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRATA_LIST_CONTROLLER_NAME);
				list.onSearch(event);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}
	
	public void changeBatchStatus(FileStatus status) {
		ContrataBatch batch = (ContrataBatch) getTo();
		if(batch != null){
			batch.setStatus(status);
			super.accept(null);
		}
	}
	
	private void checkDiskCreated() throws ManagerBeanException {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CONTRATA_BATCH_ATTACH_CONTROLLER_NAME);
		if(controller.getRowCount()>0){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}
	
	@Override
	public void select(ActionEvent event) {
		super.select(event);
		onInit(event);
	}
	
	@Override
	public void onReset(ActionEvent event) {
		setRecorded(false);
		super.onReset(event);
		ContrataBatch batch = (ContrataBatch) getTo();
		batch.setDate(new Date());
		batch.setStatus(FileStatus.PENDING);
		if(!isRecorded()){
			ContractListController list = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRATA_LIST_CONTROLLER_NAME);
			list.onSearch(event);
		}
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		ContractListController list = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRATA_LIST_CONTROLLER_NAME);
		list.onEditSearch(event);
	}
	
	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean contrataBatchDetailBean = BeanManager.getManagerBean(ContrataBatchDetail.class);
        ContractListController listController = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRATA_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			Contract contract = (Contract) iterator.next();
            ContrataBatchDetail contrataBatchDetail = new ContrataBatchDetail();
			contrataBatchDetail.setContract(contract);
			contrataBatchDetail.setContrataBatch((ContrataBatch) getTo());
			contrataBatchDetailBean.insert(contrataBatchDetail);
        }
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean contrataBatchDetailBean = BeanManager.getManagerBean(ContrataBatchDetail.class);
        BatchDetailController contrataBatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = contrataBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	ContrataBatchDetail contrataBatchDetail = (ContrataBatchDetail) iterator.next();
        	contrataBatchDetailBean.remove(contrataBatchDetail);
        }
		contrataBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchContracts(ActionEvent event) {
		ContractListController list = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRATA_LIST_CONTROLLER_NAME);
		list.onSearch(event);
	}
	
	public void onCreateDisk(ActionEvent event) {
		try {
			byte[] data = createData();
			IManagerBean bean = BeanManager.getManagerBean(ContrataBatchAttachment.class);
			if (data != null) {
				ContrataBatchAttachment attach;
				attach = new ContrataBatchAttachment();
				attach.setContrataBatch((ContrataBatch) getTo());
				attach.setMimeType(MimeType.MIME_XML);
				attach.setDescription(getFileDescription());
				attach.setSize(null);
				attach.setAttachmentType(PayrollBatchAttachmentType.GENERATED_DOCUMENT);
				attach.setScope(null);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
				setRecorded(true);
				changeBatchStatus(FileStatus.GENERATED);
				ContrataBatchAttachController controller = (ContrataBatchAttachController) FormUtil.getController(IPayrollConstants.CONTRATA_BATCH_ATTACH_CONTROLLER_NAME);
				controller.initializeModel();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateContrataFile ["+e.getMessage()+"]");
		}
	}
	
	private String getFileDescription() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(new Date());
	}

	private byte[] createData() {
		
		final String XML_OPEN_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
		final String XML_CONTRATOS_OPEN_TAG = "<CONTRATOS>";
		final String XML_CONTRATOS_END_TAG = "</CONTRATOS>";
		final String XML_NEW_LINE = "\n";
		
		int offset = XML_OPEN_TAG.length() + XML_NEW_LINE.length() + XML_CONTRATOS_OPEN_TAG.length() + XML_NEW_LINE.length();
		int lenght_increase = offset + XML_CONTRATOS_END_TAG.length() + XML_NEW_LINE.length();
		
		try {
			LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
			List<Integer> contractIds = new LinkedList<Integer>();
			for(ITransferObject to: batchDetailController.getWrappedList()){
				contractIds.add(((ContrataBatchDetail)to).getContract().getId());
			}
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contractIds);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA_FILE);
			criteria.setSkipDomainFilter(true);
			// TODO: averiguar el orden de la info dentro del fichero
//			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID));
			List<ITransferObject> list = bean.getList(criteria);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(XML_OPEN_TAG.getBytes());
			os.write(XML_NEW_LINE.getBytes());
			os.write(XML_CONTRATOS_OPEN_TAG.getBytes());
			os.write(XML_NEW_LINE.getBytes());
			for(ITransferObject to: list){
				ContractAttachment attach = (ContractAttachment) to;
				os.write(attach.getData(), offset, attach.getData().length-lenght_increase);
			}
			os.write(XML_CONTRATOS_END_TAG.getBytes());
			return os.toByteArray();
		} catch (ManagerBeanException e) {
			String msg = "El fichero no se ha podido generar.";
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "El fichero no se ha podido generar.";
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}
	
	public List<SelectItem> getBatchFileTypes(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		for(BatchFileType type: BatchFileType.values()){
			SelectItem item = new SelectItem(type, type.getValue());
			list.add(item);
		}
		return list;
	}
	
	public enum BatchFileType implements IStringEnum {
		
		CONTRACT("Contrato"),

		PRORROGATION("Prórroga"),

//	    Transformación a indefinido
//	    Llamamiento de fijo discontinuo

		BASIC_COPY("Copia Básica"),
		
//	    Contrato de grupo
//	    Horas Complementarias
//	    Incluir contrato de Oficina de Empleo
		
		LEARNING_ANNEX("Anexo de Formación"),
		
//	    Corrección de contrato
//	    Corrección de prórroga
//	    Corrección de transformaciones
//	    Corrección de llamamientos
//	    Corrección de horas complementarias
		
		;
		
		BatchFileType(String value){
			this.value = value;
		}

		private String value;

		@Override
		public String getValue() {
			return value;
		}
		
	}

}
