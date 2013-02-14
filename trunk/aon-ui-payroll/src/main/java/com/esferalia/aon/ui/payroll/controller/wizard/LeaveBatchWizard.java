package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.LeaveBatchDetail;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.ui.payroll.file.FDIWriter;

public class LeaveBatchWizard implements Serializable {

	private static final long serialVersionUID = -8246127875116090284L;

	private int currentStep;
	private static final String[] STEPS = { "leaveBatchWizard_step0", "leaveBatchWizard_step1", "leaveBatchWizard_step2", "leaveBatchWizard_step3", "leaveBatchWizard_step4" };
	private LeaveReportType[] reportTypes;
	private DataModel model;
	private DataModel selectedModel;
	private FileOutput fileOutput;
	private FDIWriter fdiWriter;
	private LeaveBatch batch;
	
	public LeaveBatch getBatch() {
		return batch;
	}
	public void setBatch(LeaveBatch batch) {
		this.batch = batch;
	}

	public LeaveReportType[] getReportTypes() {
		return reportTypes;
	}
	public void setReportTypes(LeaveReportType[] reportTypes) {
		this.reportTypes = reportTypes;
	}
	private FDIWriter getFDIWriter() {
		if (fdiWriter == null) {
			fdiWriter = new FDIWriter();
		}
		return fdiWriter;
	}

	public DataModel getModel() {
		try {
			if (model == null) {
				model = initializeModel();
			}
			return model;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private DataModel initializeModel() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
		Criteria criteria = new Criteria();
		Expression expr = null;
		for(LeaveReportType t: getReportTypes()){
			if(expr == null){
				expr = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), t); 
			} else {
				expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), t));
			}
		}
		criteria.addExpression(expr);
		Expression expr1 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_STATUS), ContractLeaveStatus.PENDING );
		Expression expr2 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_STATUS), ContractLeaveStatus.RETURNED );
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));		
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID));
		List<RemesableLeave> list =  new LinkedList<RemesableLeave>();
		for (ITransferObject to : bean.getList(criteria)) {
			ContractLeaveDetail d = (ContractLeaveDetail) to;
			RemesableLeave r = new RemesableLeave();
			r.setReportType(d.getType());
			r.setLeaveDetail(d);
			list.add(r);
		}
		return new ListDataModel(list);
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

	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	public List<SelectItem> getReportTypesList() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for( LeaveReportType type : LeaveReportType.values() ) {
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			list.add(item);			
		}
		return list;
	}

// *********************************************
	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
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
		} else if (getCurrentStep() == 4) {
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
		return (getCurrentStep() < 4);
	}

//	// ***************************************************
	public void onStart(ActionEvent event) {
		setReportTypes(null);
		setModel(null);
		setSelectedModel(null);
		setCurrentStep(0);
	}

	private void onSearch(ActionEvent event) {
		if (getReportTypes()==null) {
			String msg = "Realice alguna selección";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		model = null;
	}

	@SuppressWarnings("unchecked")
	private void onValidate(ActionEvent event) {
		List<RemesableLeave> list = new LinkedList<RemesableLeave>();
		for (RemesableLeave remesable : (List<RemesableLeave>) getModel().getWrappedData()) {
			if (remesable.isSelected()) {
				list.add(remesable);
			}
		}
		setSelectedModel(new ListDataModel(list));
	}
	
	@SuppressWarnings("unchecked")
	public void onRemoveSelected(ActionEvent event) {
		RemesableLeave r = (RemesableLeave) getSelectedModel().getRowData();
		r.setSelected(false);
		List<RemesableLeave> list = (List<RemesableLeave>) getSelectedModel().getWrappedData();
		list.remove(r);
	}
	
	public void onDiskGenerate(ActionEvent event) {
		try {
			String loggedUser = AonUtil.getRemoteUser();
			loggedUser = StringUtils.substringBefore(loggedUser, "@");
			setFileOutput(getFDIWriter().createFDI(getSavedLeaves(getBatch()), loggedUser));
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
	
	@SuppressWarnings("unchecked")
	private List<ContractLeaveDetail> getSavedLeaves(LeaveBatch batch) {
		List<ContractLeaveDetail> list = new LinkedList<ContractLeaveDetail>(); 
		for(RemesableLeave r: (List<RemesableLeave>) getSelectedModel().getWrappedData()){
			list.add(r.getLeaveDetail());
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public void save() {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				setBatch(new LeaveBatch());
				getBatch().setDate(new Date());
				insertBatch(getBatch());
				List<RemesableLeave> list = (List<RemesableLeave>) getSelectedModel().getWrappedData();
				IManagerBean bdb = BeanManager.getManagerBean(LeaveBatchDetail.class);
				IManagerBean ldb = BeanManager.getManagerBean(ContractLeaveDetail.class);
				for (RemesableLeave r: list){
					LeaveBatchDetail batchDetail = new LeaveBatchDetail();
					batchDetail.setLeaveBatch(getBatch());
					batchDetail.setContractLeaveDetail(r.getLeaveDetail());
					bdb.insert(batchDetail);
					r.getLeaveDetail().setStatus(ContractLeaveStatus.PROCESSED);
					ldb.update(r.getLeaveDetail());
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

	private void insertBatch(LeaveBatch batch) throws ManagerBeanException {
		if(batch!=null){
			IManagerBean bean = BeanManager.getManagerBean(LeaveBatch.class);
			setBatch((LeaveBatch) bean.insert(batch));
		}
	}
	
	public void onDownloadDisk(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = getFDIWriter().getEti().getFichero() + ".FDI";
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

	private void onFinish(ActionEvent event) {
		onStart(event);
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
			RemesableLeave r = (RemesableLeave) getModel().getRowData();
			r.setSelected(selected);
		}
	}

}
