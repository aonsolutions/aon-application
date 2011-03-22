package com.esferalia.aon.ui.payroll.controller.wizard;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.file.AFIWriter;

public class ContractBatchWizard {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractBatchWizard.class.getName());

	private int currentStep;
	private static final String[] STEPS = {
			"contractBatchWizard_step0",
			"contractBatchWizard_step1",
			"contractBatchWizard_step2",
			"contractBatchWizard_step3" };
	private DataModel model;
	private ContractBatch batch;
	private AFIWriter afiWriter;
	private FileOutput fileOutput;
	
	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel();
		}
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}

	public ContractBatch getBatch() {
		return batch;
	}
	public void setBatch(ContractBatch batch) {
		this.batch = batch;
	}
	
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

	private void initializeModel() {
		ContractController c = (ContractController)AonUtil.getRegisteredBean("contract");
		List<RemesableContract> list = transformList(c.getWrappedList());
		setModel(new ListDataModel(list));
	}
	private List<RemesableContract> transformList(List<ITransferObject> contracts) {
		List<RemesableContract> list = new ArrayList<RemesableContract>();
		for (ITransferObject to : contracts) {
			Contract c = (Contract)to;
			RemesableContract r = new RemesableContract();
			r.setContract(c);
			list.add(r);
		}
		return list;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	
	
	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onContractSelect(event);
			initializeModel();
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			save();
			onDiskGenerate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 3) {
			onFinish(event);
		} 
	}

	public void onPrevious(ActionEvent event) {
		setCurrentStep(getCurrentStep() - 1);
	}

	public String previous() {
		return STEPS[getCurrentStep()];
	}

	public String next() {
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 3);
	}
	
	public boolean isLast() {
		return (getCurrentStep() == STEPS.length-1);
	}

	/*
	 * ActionListeners
	 */
	public void onStart(ActionEvent event) {
		setCurrentStep(0);
		((ContractController)AonUtil.getRegisteredBean("contract")).onEditSearch(event);
	}
	
	public void onSelect(ActionEvent event) {
		setCurrentStep(1);
	}

	private void onValidate(ActionEvent event) {
		if(!isAnySelected()){
			String msg = "Debe seleccionar algun contrato";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean isAnySelected() {
		for(RemesableContract r: (List<RemesableContract>) getModel().getWrappedData()){
			if(r.isSelected()){
				return true;
			}
		}
		return false;
	}
	private void onFinish(ActionEvent event) {
		onStart(event);
	}
	
	private void onContractSelect(ActionEvent event) {
		ContractController c = (ContractController)AonUtil.getRegisteredBean("contract");
		try {
			c.getCriteria().addEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_STATUS), ContractStatus.PENDING);
			c.onSearch(event);
			c.clearCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.warn(e.getMessage(), e);
		}
	}
	
	public void onSelectAll(ActionEvent event) {
		processAll(true);
	}

	public void onDeselectAll(ActionEvent event) {
		processAll(false);
	}

	private void processAll(boolean selected) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableContract c = (RemesableContract) getModel().getRowData();
			c.setSelected(selected);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void save() {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		List<RemesableContract> list;
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				ContractBatchDetail batchDetail;
				setBatch(new ContractBatch());
				getBatch().setDate(new Date());
				setBatch((ContractBatch)BeanManager.getManagerBean(ContractBatch.class).insert(getBatch()));
				list = (List<RemesableContract>) getModel().getWrappedData();
				for (RemesableContract r: list){
					if (r.isSelected()) {
						batchDetail = new ContractBatchDetail();
						batchDetail.setContractBatch(batch);
						r.getContract().setStatus(ContractStatus.PROCESSED);
						batchDetail.setContract(r.getContract());
						BeanManager.getManagerBean(ContractBatchDetail.class).insert(batchDetail);
						BeanManager.getManagerBean(Contract.class).update(batchDetail.getContract());
					}
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onDiskGenerate(ActionEvent event) {
		try {
//			String loggedUser = AonUtil.getRemoteUser();
//			loggedUser = StringUtils.substringBefore(loggedUser, "@");
			List<Contract> list = new LinkedList<Contract>();
			for(RemesableContract r: (List<RemesableContract>) getModel().getWrappedData()){
				if(r.isSelected()){
					list.add(r.getContract());
				}
			}
//			setFileOutput(getAFIWriter().createAFI(list, loggedUser));
			setFileOutput(getAFIWriter().createAFI(list));
			if (getFileOutput() != null) {
				if (getFileOutput().getErrors().size() > 0) {
					AonUtil.addErrorMessage("Se han producido errores en la generación del fichero.");
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			// No se lanza excepción, que vaya a la última página.
		} 
	}
	
	public void onDownloadDisk(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = getAFIWriter().getEti().getFichero() + ".AFI";
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");

			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(getFileOutput().getFile());
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();

			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}

	
	
}
