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
import org.apache.velocity.runtime.parser.node.GetExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.enterprise.EnterpriseCCCController;
import com.esferalia.aon.ui.payroll.file.AFIWriter;

public class FanBatchWizard {

	
	private static final String[] STEPS = {
		"fanBatchWizard_step0",
		"fanBatchWizard_step1",
		"fanBatchWizard_step2",
		"fanBatchWizard_step3" };

	private static final Logger LOGGER = LoggerFactory.getLogger(FanBatchWizard.class.getName());

	private int currentStep;
	private DataModel model;
	private ContractBatch batch;
	private AFIWriter fanWriter;
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
	
	private AFIWriter getFANWriter() {
		if (fanWriter == null) {
			fanWriter = new AFIWriter();
		}
		return fanWriter;
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
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
			onEnterpriseCCCSelect(event);
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

	private void initializeModel() {
		BasicController c = getController();
		setModel(new ListDataModel(transformList( c.getWrappedList())));
	}

	private List<RemesableEnterpriseCCC> transformList(List<ITransferObject> enterpriseCCCs) {
		List<RemesableEnterpriseCCC> list = new ArrayList<RemesableEnterpriseCCC>();
		for (ITransferObject to : enterpriseCCCs) {
			EnterpriseCCC enterpriseCCC = (EnterpriseCCC)to;
			RemesableEnterpriseCCC r = new RemesableEnterpriseCCC();
			r.setEnterpriseCCC(enterpriseCCC);
			list.add(r);
		}
		return list;
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
		BasicController controller = getController();
		controller.onEditSearch(event);
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
		for(RemesableEnterpriseCCC r: (List<RemesableEnterpriseCCC>) getModel().getWrappedData()){
			if(r.isSelected()){
				return true;
			}
		}
		return false;
	}
	private void onFinish(ActionEvent event) {
		onStart(event);
	}
	
	private void onEnterpriseCCCSelect(ActionEvent event) {
		BasicController controller = getController();
		try {
			controller.onSearch(event);
			controller.clearCriteria();
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
			List<Contract> list = new LinkedList<Contract>();
			for(RemesableContract r: (List<RemesableContract>) getModel().getWrappedData()){
				if(r.isSelected()){
					list.add(r.getContract());
				}
			}
			setFileOutput(getFANWriter().createAFI(list));
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
			String fileName = getFANWriter().getEti().getFichero() + ".AFI";
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

	private BasicController getController() {
		return ( BasicController ) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_CCC_CONTROLLER);
	}
	
	
}
