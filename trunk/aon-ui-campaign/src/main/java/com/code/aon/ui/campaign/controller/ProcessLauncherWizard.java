package com.code.aon.ui.campaign.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.campaign.ActivityProcess;
import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.Process;
import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.campaign.enumeration.CampaignStatus;
import com.code.aon.campaign.enumeration.CampaignType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.WorkGroup;
import com.code.aon.customer.Customer;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.project.Dossier;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.project.enumeration.TaskPeriod;
import com.code.aon.project.enumeration.TaskSource;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.project.util.CampaignTaskManager;
import com.code.aon.ui.util.AonUtil;

public class ProcessLauncherWizard implements Serializable {

	private static final long serialVersionUID = 8114094812276365212L;

	private static final Logger LOGGER = Logger.getLogger(ProcessLauncherWizard.class.getName());

	private static final String[] STEPS = { "process_wizard_step0", "process_wizard_step1",
			"process_wizard_step2", "process_wizard_step3" };

	private int currentStep;

	private Process process;

	private String description;

	private Date startDate;

	private Date endDate;

	private WorkGroup workGroup;

	private CampaignType type;

	private CampaignStatus status;

	private List<CampaignDossier> dossiers;
	private DataModel dossiersModel;

	private Customer customer;
	private Dossier dossier;

	private boolean newDossier;
	private boolean existingDossier;

	private List<SelectItem> availableDossiers;

	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public WorkGroup getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	public CampaignType getType() {
		return type;
	}

	public void setType(CampaignType type) {
		this.type = type;
	}

	public CampaignStatus getStatus() {
		return status;
	}

	public void setStatus(CampaignStatus status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}

	public boolean isNewDossier() {
		return newDossier;
	}

	public void setNewDossier(boolean newDossier) {
		this.newDossier = newDossier;
		this.existingDossier = !this.newDossier;
		setDossier(new Dossier());
		getDossier().setNumber("<<new>>");
	}

	public boolean isExistingDossier() {
		return existingDossier;
	}

	public void setExistingDossier(boolean existingDossier) {
		this.existingDossier = existingDossier;
		this.newDossier = !this.existingDossier;
		setDossier(null);
	}

	public List<SelectItem> getAvailableDossiers() {
		if (availableDossiers == null) {
			loadAvailableDossiers();
		}
		return availableDossiers;
	}

	public void setAvailableDossiers(List<SelectItem> availableDossiers) {
		this.availableDossiers = availableDossiers;
	}

	@SuppressWarnings("unchecked")
	private void loadAvailableDossiers() {
		setAvailableDossiers(new LinkedList<SelectItem>());
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(Dossier.class);
			Criteria criteria = new Criteria();
			if (getCustomer() != null) {
				criteria.addEqualExpression(managerBean
						.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID), getCustomer().getId());
			}
			criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.DOSSIER_STATUS),
					DossierStatus.ACTIVE);
			criteria.addOrder(managerBean.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID));
			criteria.addOrder(managerBean.getFieldName(IProjectAlias.DOSSIER_NUMBER));
			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Dossier dossier = (Dossier) iterator.next();
				StringBuilder sb = new StringBuilder(dossier.getNumber());
				if (getCustomer() == null) {
					sb.append(" (");
					sb.append(dossier.getCustomer().getRegistry().getAlias());
					sb.append(")");
				}
				SelectItem item = new SelectItem(dossier, sb.toString());
				availableDossiers.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading dossiers!", e);
		}
	}

	public List<CampaignDossier> getDossiers() {
		if (dossiers == null) {
			dossiers = new ArrayList<CampaignDossier>();
		}
		return dossiers;
	}

	public void setDossiers(List<CampaignDossier> dossiers) {
		this.dossiers = dossiers;
	}

	public DataModel getDossiersModel() {
		if (dossiersModel == null) {
			dossiersModel = new ListDataModel(getDossiers());
		}
		return dossiersModel;
	}

	public void setDossiersModel(DataModel dossiersModel) {
		this.dossiersModel = dossiersModel;
	}

	private void initializeController() {
		setProcess(null);
		setDescription(null);
		setStartDate(new Date());
		setEndDate(new Date());
		setWorkGroup(null);
		setType(CampaignType.MANUAL);
		setStatus(CampaignStatus.IN_PROGRESS);
		setCustomer(null);
		setDossiers(null);
		setDossiersModel(null);
		setAvailableDossiers(null);
		setExistingDossier(true);
	}

	private String determineStep() {
		if (getProcess() == null) {
			setCurrentStep(0);
		} else if (getDescription() == null || getStartDate() == null || getEndDate() == null
				|| getWorkGroup() == null) {
			setCurrentStep(1);
		} else {
			setCurrentStep(2);
		}
		return STEPS[getCurrentStep()];
	}

	public String start() {
		initializeController();
		return determineStep();
	}

	public String startWithProcess() {
		initializeController();
		return determineStep();
	}

	public String previous() {
		setCurrentStep(getCurrentStep() - 1);
		return STEPS[getCurrentStep()];
	}

	public String next() {
		setCurrentStep(getCurrentStep() + 1);
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 3);
	}

	public boolean isFinaliceAvailable() {
		return (getCurrentStep() == 3);
	}

	public void processChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			if (getDescription() == null) {
				Process process = (Process) event.getNewValue();
				setDescription(process.getDescription());
			}
		}
	}

	public void customerChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setAvailableDossiers(null);
		}
	}

	public void dossierChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			if (getCustomer() == null) {
				Dossier dossier = (Dossier) event.getNewValue();
				Customer c = dossier.getCustomer();
				setCustomer(c);
				ValueChangeEvent e = new ValueChangeEvent(event.getComponent(), null, c);
				customerChanged(e);
			}
		}
	}

	public void addExistingDossier(ActionEvent event) {
		addDossier();
		setDossier(null);
	}

	public void addNewDossier(ActionEvent event) {
		Dossier dossier = getDossier();
		dossier.setCustomer(getCustomer());
		addDossier();
		setDossier(new Dossier());
	}

	private void addDossier() {
		CampaignDossier cd = new CampaignDossier();
		cd.setDossier(getDossier());
		if (validate(cd)) {
			getDossiers().add(cd);
			setCustomer(null);
			setAvailableDossiers(null);
		}
	}

	private boolean validate(CampaignDossier cd) {
		if (cd.getDossier() == null) {
			String msg = "Dossier no puede estar vacio!";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		if (cd.getDossier().getCustomer() == null) {
			String msg = "Cliente no puede estar vacio!";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		if (StringUtils.isEmpty(cd.getDossier().getNumber())) {
			String msg = "Proyecto no puede estar vacio!";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		if (cd.getDossier().getDossierType() == null) {
			String msg = "Tipo de Proyecto no puede estar vacio!";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void removeDossier(ActionEvent event) {
		int index = getDossiersModel().getRowIndex();
		List list = (List) getDossiersModel().getWrappedData();
		list.remove(index);
	}

	@SuppressWarnings("unchecked")
	public String finalice() {
		try {
			IManagerBean dossierBean = BeanManager.getManagerBean(Dossier.class);
			IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
			IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
			IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
			IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
			IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
			
			Criteria criteria = new Criteria();
			String alias = processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_PROCESS_ID);
			criteria.addEqualExpression( alias , getProcess().getId());
			criteria.addOrder(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_POSITION));
			List list = processDetailBean.getList(criteria);
			ProcessDetail pd = (ProcessDetail) list.get(0);
			
			Campaign c = new Campaign();
			c.setActivityType(null);
			c.setDescription(getDescription());
			c.setEndDate(getEndDate());
			c.setProcess(getProcess());
			c.setStartDate(getStartDate());
			c.setStatus(getStatus());
			c.setType(CampaignType.MANUAL);
			c.setWorkGroup(getWorkGroup());
			c = (Campaign) campaignBean.insert( c );
			for (CampaignDossier cd: dossiers) {
				if (cd.getDossier().getId() == null ) {
					Dossier d = cd.getDossier();
					d.setStatus(DossierStatus.ACTIVE);
					d = (Dossier) dossierBean.insert(d);
					cd.setDossier(d);
				}
				cd.setCampaign(c);
				campaignDossierBean.insert(cd);
				
				Task task = new Task();
				task.setDescription(pd.getDescription() + " [" + c.getDescription() + "]");
				task.setDossier(cd.getDossier());
	            task.setDueDate(CampaignTaskManager.calculateDueDate(cd.getCampaign(), pd.getDateReference(), pd.getDays()));
	            task.setPercent(0);
	            task.setPriority(Priority.NORMAL);
				task.setRepeatPeriod(TaskPeriod.NONE);
	            task.setSource(TaskSource.PROCESS);
	            task.setStartDate(new Date());
	            task.setStatus(TaskStatus.PENDING);
	            task.setWorkGroup(pd.getWorkgroup());
				taskBean.insert(task);

				ActivityProcess ap = new ActivityProcess();
				ap.setActivity(null);
				ap.setCampaign(c);
				ap.setProcessDetail(pd);
				ap.setTask(task);
				activityProcessBean.insert(ap);
				
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}

		return null;
	}

}
