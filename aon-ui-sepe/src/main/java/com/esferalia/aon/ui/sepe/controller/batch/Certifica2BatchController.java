package com.esferalia.aon.ui.sepe.controller.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;

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
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.ui.sepe.controller.CertificadosController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.batch.Certifica2ListController.RemesableContract;
import com.esferalia.aon.ui.sepe.file.CertificadosWriter;

public class Certifica2BatchController extends BasicController {

	private CertificadosWriter certificadosWriter;
	private FileOutput fileOutput;
	private boolean recorded;
	
	private CertificadosWriter getCertificadosWriter() {
		if (certificadosWriter == null) {
			certificadosWriter = new CertificadosWriter();
		}
		return certificadosWriter;
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
		IManagerBean certifica2BatchDetailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		Certifica2ListController listController = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
        checkAllSuspensionCauses(listController.getRemesableContracts(), listController.getCheckHandler().getCheckedList());
		Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
		
		for(Certifica2ListController.RemesableContract remesable: listController.getRemesableContracts().values()){
			Certifica2BatchDetail certifica2BatchDetail = new Certifica2BatchDetail();
			certifica2BatchDetail.setContract(remesable.getContract());
			certifica2BatchDetail.setCertifica2Batch((Certifica2Batch) getTo());
			certifica2BatchDetail.setSuspensionCause(remesable.getSuspensionCause());
			certifica2BatchDetailBean.insert(certifica2BatchDetail);

			Contract contract = (Contract) iterator.next();
			// TODO: include a new contract status to indicate that the contract is batched for certific@2 or not
//			contract.setStatus(ContractStatus.BATCHED);
			contractBean.update(contract);
		}
        listController.getCheckHandler().clearCheckedList();
        listController.getRemesableContracts().clear();
        loadDetails();
        onSearchContracts(event);
	}
	
	protected Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
		}
		return diffDays;
	}
	
	private void checkAllSuspensionCauses(
			Map<Integer, RemesableContract> remesableContracts,
			ArrayList<Object> checkedList) {
		if(checkedList.size()!=remesableContracts.size()){
			String message = "Debe seleccionar la causa de suspension de los empleados seleccionados.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}

	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean certifica2BatchDetailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
        BatchDetailController certifica2BatchDetailController = (BatchDetailController)FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = certifica2BatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	Certifica2BatchDetail certifica2BatchDetail = (Certifica2BatchDetail) iterator.next();
        	// TODO: include a new contract status to indicate that the contract is batched for certific@2 or not
//        	certifica2BatchDetail.getContract().setSepeStatus(ContractStatus.PENDING);
        	contractBean.update(certifica2BatchDetail.getContract());
        	certifica2BatchDetailBean.remove(certifica2BatchDetail);
        }
        certifica2BatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchContracts(ActionEvent event) throws ManagerBeanException {
		Certifica2ListController list = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		list.clearCriteria();
		list.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		Certifica2ListController list = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		list.clearCriteria();
		list.onEditSearch(event);
	}
	
	public void onInit(ActionEvent event) {
		try {
			onSearchContracts(event);
			checkDiskCreated();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}

	public void onCreateDisk(ActionEvent event) {
		try {
			Certifica2Batch batch = (Certifica2Batch)getTo();
			List<ITransferObject> detailList = getCertifica2DetailList((Certifica2Batch) this.getTo());
			File file = getCertificadosWriter().createFile(batch, detailList);
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				Certifica2BatchAttachment attach;
				attach = new Certifica2BatchAttachment();
				attach.setCertifica2Batch((Certifica2Batch) getTo());
				attach.setMimeType(MimeType.MIME_XML);
				attach.setDescription(getCertificadosWriter().getFileName());
				attach.setSize(null);
				attach.setAttachmentType(SepeBatchAttachmentType.GENERATED_FILE);
				attach.setScope(null);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
				setRecorded(true);
				changeBatchStatus(FileStatus.GENERATED);
				Certifica2BatchAttachController controller = (Certifica2BatchAttachController) FormUtil.getController("certifica2BatchAttach");
				controller.initializeModel();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		}
	}
	
	public void changeBatchStatus(FileStatus status) {
		Certifica2Batch b = (Certifica2Batch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		LinesController controller = (LinesController)FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_ATTACH_CONTROLLER_NAME);
		if(controller.getRowCount()>0){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}
	
	private List<ITransferObject> getCertifica2DetailList(Certifica2Batch batch) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID), batch.getId());
			criteria.addOrder("Certifica2BatchDetail.contract.enterpriseCCC.activity.type");
			criteria.addOrder("Certifica2BatchDetail.contract.enterpriseCCC.ccc");
			criteria.addOrder("Certifica2BatchDetail.contract.person.registry.document");
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			// NADA, que siga con la generacion del fichero
			System.out.println("");
		}
		return null;
	}
	
	
	public void onInitCertificados(ActionEvent event){
		Certifica2Batch batch =  (Certifica2Batch) this.getTo();
		if(batch.getStatus() == FileStatus.GENERATED){
			CertificadosController certificadosController = (CertificadosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CERTIFICADOS_CONTROLLER_NAME);
			certificadosController.initialize(batch);
		}
	}
	
}
