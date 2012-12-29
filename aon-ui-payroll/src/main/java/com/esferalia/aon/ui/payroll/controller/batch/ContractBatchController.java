package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.ContractBatchAttachment;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContractBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.AFIWriter;


public class ContractBatchController extends BasicController {
	
	private AFIWriter afiWriter;
	private FileOutput fileOutput;
	private boolean recorded;
	
	private AFIWriter getAFIWriter() {
		if (afiWriter == null) {
			afiWriter = new AFIWriter();
		}
		return afiWriter;
	}
	
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

	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
        IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		IManagerBean contractBatchDetailBean = BeanManager.getManagerBean(ContractBatchDetail.class);
        ContractListController listController = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			Contract contract = (Contract) iterator.next();
            contract.setStatus(ContractStatus.PROCESSED);
            contractBean.update(contract);
            ContractBatchDetail contractBatchDetail = new ContractBatchDetail();
			contractBatchDetail.setContract(contract);
			contractBatchDetail.setContractBatch((ContractBatch) getTo());
			contractBatchDetailBean.insert(contractBatchDetail);
        }
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean contractBatchDetailBean = BeanManager.getManagerBean(ContractBatchDetail.class);
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
        BatchDetailController contractBatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = contractBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	ContractBatchDetail contractBatchDetail = (ContractBatchDetail) iterator.next();
        	contractBatchDetail.getContract().setStatus(ContractStatus.PENDING);
        	contractBean.update(contractBatchDetail.getContract());
        	contractBatchDetailBean.remove(contractBatchDetail);
        }
		contractBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchContracts(ActionEvent event) {
		ContractListController list = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
		list.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		ContractListController list = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
		list.onEditSearch(event);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		onInit(event);
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		ContractBatch b = (ContractBatch) getTo();
		b.setStatus(FileStatus.PENDING);
		super.onAccept(event);
		onSearchContracts(event);
	}
	
	public void onInit(ActionEvent event) {
		onSearchContracts(event);
		try {
			checkDiskCreated();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}
	
	@Override
	public void onReset(ActionEvent event) {
		setRecorded(false);
		super.onReset(event);
		ContractBatch b = (ContractBatch) getTo();
		b.setStatus(FileStatus.PENDING);
	}

	public void onCreateDisk(ActionEvent event) {
		try {
			File file = getAFIWriter().createAFI(getContractList()).getFile();
			IManagerBean bean = BeanManager.getManagerBean(ContractBatchAttachment.class);
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				ContractBatchAttachment attach;
				attach = new ContractBatchAttachment();
				attach.setContractBatch((ContractBatch) getTo());
				attach.setMimeType(MimeType.MIME_TXT);
				attach.setDescription(getAFIWriter().getEti().getFichero());
				attach.setSize(null);
				attach.setAttachmentType(ContractBatchAttachmentType.AFI_DOCUMENT);
				attach.setScope(null);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
				setRecorded(true);
				changeBatchStatus(FileStatus.GENERATED);
				ContractBatchAttachController controller = (ContractBatchAttachController) FormUtil.getController("contractBatchAttach");
				controller.initializeModel();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		}
	}
	
	public void changeBatchStatus(FileStatus status) {
		ContractBatch b = (ContractBatch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractBatchAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_BATCH_ATTACHMENT_CONTRACT_BATCH_ID), ((ContractBatch)getTo()).getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_BATCH_ATTACHMENT_ATTACHMENT_TYPE), ContractBatchAttachmentType.AFI_DOCUMENT);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}

	private List<Contract> getContractList() {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_DETAIL_CONTROLLER_NAME);
		try {
			controller.getCriteria().setSkipDomainFilter(DomainManager.isDomainManagementAvailable());
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Contract> list = new LinkedList<Contract>();
		for(ITransferObject to: controller.getWrappedList()){
			ContractBatchDetail detail = (ContractBatchDetail) to;
			list.add(detail.getContract());
		}
		return list;
	}

}
