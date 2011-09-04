package com.esferalia.aon.ui.payroll.controller.wizard;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.FanBatch;
import com.esferalia.aon.payroll.FanBatchDetail;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.FANWriter;

public class FanBatchWizard {

	private static final Logger LOGGER = LoggerFactory.getLogger(FanBatchWizard.class.getName());
	
	private static final String[] STEPS = {
		"fanBatchWizard_step0",
		"fanBatchWizard_step1",
		"fanBatchWizard_step2",
		"fanBatchWizard_step3",
		"fanBatchWizard_step4" };
	private int currentStep;
	private FANWriter fanWriter;
	private FileOutput fileOutput;
	
	private Integer year;
	private Month startMonth;
	private Month endMonth;

	private DataModel model;
	private DataModel selectedModel;
	
	private FanBatch batch;
	
	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel();
		}
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	public DataModel getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(DataModel selectedModel) {
		this.selectedModel = selectedModel;
	}
	public FanBatch getBatch() {
		return batch;
	}
	public void setBatch(FanBatch batch) {
		this.batch = batch;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Month getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(Month startMonth) {
		this.startMonth = startMonth;
	}
	public Month getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(Month endMonth) {
		this.endMonth = endMonth;
	}
	
	private FANWriter getFANWriter() {
		if (fanWriter == null) {
			fanWriter = new FANWriter();
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
	
	private void initializeModel() {
		BasicController c = ( BasicController ) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_CONTROLLER);
		setModel(new ListDataModel(transformList( c.getWrappedList())));
	}
	
	private List<RemesableEnterprise> transformList(List<ITransferObject> enterprises) {
		List<RemesableEnterprise> list = new ArrayList<RemesableEnterprise>();
		for (ITransferObject to : enterprises) {
			Enterprise enterprise= (Enterprise)to;
			RemesableEnterprise r = new RemesableEnterprise();
			r.setEnterprise(enterprise);
			list.add(r);
		}
		return list;
	}
	
	//************************************
	// Navigation
	//************************************
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
		return (getCurrentStep() < 4);
	}
	
	public boolean isLast() {
		return (getCurrentStep() == STEPS.length-1);
	}
	
	//************************************
	// Action Listeners 
	//************************************
	
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onEnterpriseSearch(event);
			initializeModel();
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			save();
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 3) {
			onDiskGenerate(event);
			setCurrentStep(getCurrentStep() + 1);
		}  else if (getCurrentStep() == 4) {
			onFinish(event);
		}
	}

	public void onPrevious(ActionEvent event) {
		setCurrentStep(getCurrentStep() - 1);
	}

	public void onStart(ActionEvent event) {
		setSelectedModel(null);
		Calendar cal = Calendar.getInstance();
		setYear(cal.get(Calendar.YEAR));
		setStartMonth(Month.getMonthByValue(cal.get(Calendar.MONTH)));
		setEndMonth(Month.getMonthByValue(cal.get(Calendar.MONTH)));
		setCurrentStep(0);
		BasicController controller = ( BasicController ) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_CONTROLLER);
		controller.onEditSearch(event);
	}
	
	public void onSelect(ActionEvent event) {
		setCurrentStep(1);
	}

	@SuppressWarnings("unchecked")
	private void onValidate(ActionEvent event) {
		if(!isAnySelected()){
			String msg = "Debe seleccionar alguna empresa";
			AonUtil.addErrorMessage(msg);
			setCurrentStep(getCurrentStep() - 1);
		} else {
			List<RemesableEnterprise> list = new LinkedList<RemesableEnterprise>();
			for (RemesableEnterprise remesable : (List<RemesableEnterprise>) getModel().getWrappedData()) {
				if (remesable.isSelected()) {
					list.add(remesable);
				}
			}
			setSelectedModel(new ListDataModel(list));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onRemoveSelected(ActionEvent event) {
		List<RemesableEnterprise> list = (List<RemesableEnterprise>) getSelectedModel().getWrappedData();
		if(list.size()>1){
			RemesableEnterprise r = (RemesableEnterprise) getSelectedModel().getRowData();
			r.setSelected(false);
			list.remove(r);
		} else {
			String msg = "No se puede generar una remesa sin ninguna empresa seleccionada";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean isAnySelected() {
		for(RemesableEnterprise r: (List<RemesableEnterprise>) getModel().getWrappedData()){
			if(r.isSelected()){
				return true;
			}
		}
		return false;
	}
	private void onFinish(ActionEvent event) {
		onStart(event);
	}
	
	private void onEnterpriseSearch(ActionEvent event) {
		BasicController controller = ( BasicController ) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_CONTROLLER);
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
			RemesableEnterprise c = (RemesableEnterprise) getModel().getRowData();
			c.setSelected(selected);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void save() {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		List<RemesableEnterprise> list;
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				setBatch(new FanBatch());
				getBatch().setDate(new Date());
				FanBatchDetail batchDetail;
				setBatch( (FanBatch) BeanManager.getManagerBean(FanBatch.class).insert(getBatch()) );
				list = (List<RemesableEnterprise>) getModel().getWrappedData();
				for (RemesableEnterprise r: list){
					if (r.isSelected()) {
						batchDetail = new FanBatchDetail();
						batchDetail.setFanBatch(getBatch());
						batchDetail.setEnterprise(r.getEnterprise());
						BeanManager.getManagerBean(FanBatchDetail.class).insert(batchDetail);
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
			List<Enterprise> list = new LinkedList<Enterprise>();
			for(RemesableEnterprise r: (List<RemesableEnterprise>) getModel().getWrappedData()){
				if(r.isSelected()){
					list.add(r.getEnterprise());
				}
			}
			setFileOutput(getFANWriter().createFAN(list, getYear(), getStartMonth(), getStartMonth()));
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
			String fileName = getFANWriter().getEti().getFichero() + ".FAN";
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

	public class RemesableEnterprise implements Serializable {
		
		private static final long serialVersionUID = -3883614375241389901L;

		private boolean selected;
		private Enterprise enterprise;

		public Enterprise getEnterprise() {
			return enterprise;
		}
		public void setEnterprise(Enterprise enterprise) {
			this.enterprise = enterprise;
		}
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
	}
	
}
