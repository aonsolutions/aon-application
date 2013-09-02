package com.esferalia.aon.ui.sepe.controller.batch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
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
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;


public class ContrataBatchController extends BasicController {
	
	private FileOutput fileOutput;
	private boolean recorded;
	
	// SEPE COMMUNICATION
//	private boolean showLoginWindow;
//	private boolean showCommunicationWindow;
//	private ISepeCommunicator communicator;	
	
	
//	public boolean isCommunicationResponseReceived(){
//		return obtainContrataResponseAttach()!=null;
//	}
	
//	@Override
//	public boolean isShowLoginWindow() {
//		return showLoginWindow;
//	}
//
//	public void setShowLoginWindow(boolean showLoginWindow) {
//		this.showLoginWindow = showLoginWindow;
//	}

//	public boolean isShowCommunicationWindow() {
//		return showCommunicationWindow;
//	}
//
//	public void setShowCommunicationWindow(boolean showCommunicationWindow) {
//		this.showCommunicationWindow = showCommunicationWindow;
//	}

//	public ISepeCommunicator getCommunicator() {
//		if(communicator==null){
//			communicator = new ContrataCommunicator();
//		}
//		return communicator;
//	}
//
//	public void setCommunicator(ISepeCommunicator communicator) {
//		this.communicator = communicator;
//	}

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
				ContrataListController list = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
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
		LinesController controller = (LinesController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_ATTACH_CONTROLLER_NAME);
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
			ContrataListController list = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
			list.onSearch(event);
		}
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		ContrataListController list = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
		list.onEditSearch(event);
	}
	
	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean contrataBatchDetailBean = BeanManager.getManagerBean(ContrataBatchDetail.class);
		ContrataListController listController = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
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
        BatchDetailController contrataBatchDetailController = (BatchDetailController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
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
        LinesController batchDetailController = (LinesController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchContracts(ActionEvent event) {
		ContrataListController list = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
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
				attach.setAttachmentType(SepeBatchAttachmentType.GENERATED_FILE);
				attach.setScope(null);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
				setRecorded(true);
				changeBatchStatus(FileStatus.GENERATED);
				ContrataBatchAttachController controller = (ContrataBatchAttachController) FormUtil.getController(ISepeConstants.CONTRATA_BATCH_ATTACH_CONTROLLER_NAME);
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
		
		final String XML_OPEN_TAG = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";
		final String XML_CONTRATOS_OPEN_TAG = "<CONTRATOS>";
		final String XML_CONTRATOS_END_TAG = "</CONTRATOS>";
		final String XML_NEW_LINE = "\n";
		
		int offset = XML_OPEN_TAG.length() + XML_NEW_LINE.length() + XML_CONTRATOS_OPEN_TAG.length() + XML_NEW_LINE.length();
		int lenght_increase = offset + XML_CONTRATOS_END_TAG.length() + XML_NEW_LINE.length();
		
		try {
			LinesController batchDetailController = (LinesController)FormUtil.getController(ISepeConstants.CONTRATA_BATCH_DETAIL_CONTROLLER_NAME);
			List<Integer> contractIds = new LinkedList<Integer>();
			for(ITransferObject to: batchDetailController.getWrappedList()){
				contractIds.add(((ContrataBatchDetail)to).getContract().getId());
			}
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contractIds);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SEPE_CONTRACT_FILE);
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
	
	
	// -----------------------
	// SEPE COMMUNICATION
	// -----------------------
	
//	@Override
//	public void onSendSepeFile(ActionEvent event){
//		ContrataBatchAttachment attach = obtainContrataFileAttach();
//		if(!isShowLoginWindow()){
//			getCommunicator().initialize();
//		}
//		if( getCommunicator().isLoginRequired() ){
//			setShowLoginWindow(true);
//		} else {
//			getCommunicator().setDataCommunication(true);
//			getCommunicator().setDocument(new String(attach.getData()));
//			String result = getCommunicator().communicate();
//			saveResponseFile(result);
//			setShowLoginWindow(false);
//		}
//	}
//	
//	@Override
//	public void onSepeDataQuery(ActionEvent event){
//		ContrataBatchAttachment attach = obtainContrataResponseAttach();
//		String document = getCommunicator().obtainCommunicationNumber(attach.getData());
//		if( StringUtils.isBlank(document) ){
//			String msg = "No se puede obtener el número del envío de la comunicación.";
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
//		}
//		if(!isShowLoginWindow()){
//			getCommunicator().initialize();
//		}
//		if( getCommunicator().isLoginRequired() ){
//			setShowLoginWindow(true);
//		} else {
//			getCommunicator().setDataQuery(true);
//			getCommunicator().setDocument(document);
//			String result = getCommunicator().communicate();
//			saveResponseFile(result);
//			setShowLoginWindow(false);
//		}
//	}
	
//	private ContrataBatchAttachment obtainContrataFileAttach(){
//		return obtainContrataAttach(PayrollBatchAttachmentType.GENERATED_DOCUMENT);
//	}
//	
//	private ContrataBatchAttachment obtainContrataResponseAttach(){
//		return obtainContrataAttach(PayrollBatchAttachmentType.RETURN_DOCUMENT);
//	}
//
//	private ContrataBatchAttachment obtainContrataStatusAttach(){
//		return obtainContrataAttach(null);
//	}
//	
//	private ContrataBatchAttachment obtainContrataAttach(PayrollBatchAttachmentType type){
////		try {
////			if(getParams()!=null && getContract()!=null && getContract().getId()!=null){
////				IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
////				Criteria criteria = new Criteria();
////				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), getContract().getId());
////				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), type);
////				List<ITransferObject> list = bean.getList(criteria);
////				if(!list.isEmpty()){
////					return (ContractAttachment) list.get(0);
////				}
////			}
////		} catch (ManagerBeanException e) {
////			// NOTHING TO DO
////		}
//		return null;
//	}
	
	
	

}
