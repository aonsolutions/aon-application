package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.code.aon.ui.form.IDataModelDataProvider;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.core.it.ParteITParams;

public class RemesaITWizard implements Serializable, IDataModelDataProvider,ICriteriaProvider{

	private static final long serialVersionUID = 7495900117871108096L;
	
	private IParteITDAO parteITDAO;
	private int currentStep;
	private static final String[] STEPS = { "remesaITWizard_step0","remesaITWizard_step1","remesaITWizard_step2","remesaITWizard_step3"};
	private ParteITParams params;
	private Map<Serializable,IParteIT> checks;
	private DataModel model;
	private DataModel selectedModel;
	private FileOutput fileOutput;
	
	public ParteITParams getParams() {
		if (params == null) {
			setParams( new ParteITParams());
		}
		return params;
	}
	public void setParams(ParteITParams params) {
		this.params = params;
	}
	
	public Map<Serializable,IParteIT> getChecks() {
		if ( checks == null) {
			setChecks(new HashMap<Serializable,IParteIT>());;
		}
		return checks;
	}
	public void setChecks(Map<Serializable,IParteIT> checks) {
		this.checks = checks;
	}
	public DataModel getModel() {
		if (model == null) {
			model = new ExtendedPageDataModel(this, this);	
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
	public FileOutput getFileOutput() {
		return fileOutput;
	}
	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
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

	// ***************************************************
	public void onStart(ActionEvent event) {
		setParams(null);
		setChecks(null);
		setModel(null);
		setCurrentStep(0);
	}

	private void onSearch(ActionEvent event) {
		try {
			if (!getParams().isAlta() && !getParams().isBaja() && !getParams().isConfirmacion()) {
				String msg = "Realice alguna selección";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (model == null) {
				model = new ExtendedPageDataModel(this, this);
			}
			((ExtendedPageDataModel) model).update(0, getPageLimit());
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	private void onValidate(ActionEvent event) {
		Collection<IParteIT> c = getChecks().values();
		setSelectedModel( new ArrayDataModel(c.toArray()) );
	}
	
	public void onDiskGenerate(ActionEvent event) {
		
	}
	
	private void onFinish(ActionEvent event) {
		// TODO Auto-generated method stub
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
			IParteIT parte = (IParteIT) getModel().getRowData();
			processCheck(parte,selected);
		}
	}
	
	public boolean isSelected() {
		IParteIT parte = getParteIT();
		boolean selected = getChecks().containsKey(parte.getId()); 
		return selected;
	}
	public void setSelected(boolean selected) {
		IParteIT parte = getParteIT();
		processCheck(parte,selected);
	}

	private void processCheck(IParteIT parte, boolean selected) {
		if (selected) {
			check( parte );
		} else {
			uncheck( parte );
		}
	}
	private void uncheck(IParteIT parte) {
		if (getChecks().containsKey(parte.getId())) {
			getChecks().remove(parte.getId());
		}
	}
	private void check(IParteIT parte) {
		if (!getChecks().containsKey(parte.getId())) {
			getChecks().put(parte.getId(),parte);
		}
	}
	
	private IParteIT getParteIT() {
		return (IParteIT) getModel().getRowData();
	}
	
	public IParteITDAO getParteITDAO() {
		if (parteITDAO == null) {
			parteITDAO = ParteITDAOFactory.getInstance().getParteITDAO();
		}
		return parteITDAO;
	}
	@Override
	public int getPageLimit() {
		return 20;
	}
	@Override
	public int getRowCount() throws ManagerBeanException {
		try {
			return getParteITDAO().getCount(getParams());
		} catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		try {
			List<?> list = getParteITDAO().getPartes(getParams(), start, count);
			return (List<ITransferObject>) list;
		} catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}

	@Override
	public Criteria getCriteria() throws ManagerBeanException {
		try {
			return getParteITDAO().getCriteria(getParams());
		} catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}

}
