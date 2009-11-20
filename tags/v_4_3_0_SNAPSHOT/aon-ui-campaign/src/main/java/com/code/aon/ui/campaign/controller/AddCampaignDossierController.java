package com.code.aon.ui.campaign.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.project.Activity;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Dossier;
import com.code.aon.project.DossierType;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.project.util.CampaignTaskManager;
import com.code.aon.ui.util.AonUtil;

public class AddCampaignDossierController {

    private static final Logger LOGGER = Logger.getLogger(AddCampaignDossierController.class.getName());

	private boolean addPanelVisible;
	private List<CampaignDossier> checked;
	private List<CampaignDossier> dossiers;
	private DataModel model;
	private List<SelectItem> availableDossiers;
	
	private CampaignTaskManager campaignTaskManager;
	private Customer customer;
	private Dossier dossier;
	private DossierType dossierType;
	private ActivityType activityType;
	private String dossierNumber;
	
	private CampaignTaskManager getCampaignTaskManager() {
		if (campaignTaskManager == null) {
			campaignTaskManager = new CampaignTaskManager();
		}
		return campaignTaskManager;
	}

	public List<CampaignDossier> getChecked() {
		if (checked == null) {
			setChecked( new LinkedList<CampaignDossier>());
		}
		return checked;
	}
	public void setChecked(List<CampaignDossier> checked) {
		this.checked = checked;
	}

	public List<CampaignDossier> getDossiers() {
		if (dossiers == null) {
			setDossiers( new LinkedList<CampaignDossier>());
		}
		return dossiers;
	}
	public void setDossiers(List<CampaignDossier> dossiers) {
		this.dossiers = dossiers;
	}

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel( getDossiers() );
		}
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}

	public boolean isAddPanelVisible() {
		return addPanelVisible;
	}
	public void setAddPanelVisible(boolean addPanelVisible) {
		this.addPanelVisible = addPanelVisible;
	}
		
	public void showAddPanel(ActionEvent event) {
		setAddPanelVisible(true);
		initialize();
	}
	public void hideAddPanel(ActionEvent event) {
		setAddPanelVisible(false);
		initialize();
	}
	
	private void initialize() {
		setDossiers(null);
		setChecked(null);
		setModel(null);
		setCustomer( new Customer() );
		setDossier(null);
		setDossierType(null);
		setActivityType(null);
		setDossierNumber(null);
		setAvailableDossiers(null);
	}
	public boolean getRowChecked() {
    	CampaignDossier to = (CampaignDossier) model.getRowData();
        return getChecked().contains(to);
    }
    public void setRowChecked(boolean rowChecked) {
        if (rowChecked) {
        	CampaignDossier to = (CampaignDossier) model.getRowData();
            if (!getChecked().contains(to)) {
            	getChecked().add(to);
            }
        } else {
        	Dossier to = (Dossier) model.getRowData();
            if (getChecked().contains(to)) {
            	getChecked().remove(to);
            }
        }
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

	public DossierType getDossierType() {
		return dossierType;
	}

	public void setDossierType(DossierType dossierType) {
		this.dossierType = dossierType;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getDossierNumber() {
		return dossierNumber;
	}

	public void setDossierNumber(String dossierNumber) {
		this.dossierNumber = dossierNumber;
	}

    public void onAddCampaignDossiers(ActionEvent event) {
        try {
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
            for (CampaignDossier campaignDossier: getDossiers()) {
                campaignDossier = (CampaignDossier) campaignDossierBean.insert(campaignDossier);
                getCampaignTaskManager().addCampaignTask(campaignDossier, 0, null,null);
            }
            CampaignDossierController campaignDossierController = (CampaignDossierController)FormUtil.getController("campaignDossier");
            campaignDossierController.onSearch(null);
            hideAddPanel(event);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error adding campaign dossier", e);
        }
    }

	public void addExistingDossier(ActionEvent event) {
		try {
	        Campaign campaign = (Campaign)FormUtil.getController("campaign").getTo();
			if (getDossier() != null) {
				addDossier(campaign,getDossier());	
			} else {
				if (getActivityType() == null) {
					IManagerBean dossierBean = BeanManager.getManagerBean(Dossier.class);
					Criteria criteria = new Criteria();
					if (getCustomer() != null && getCustomer().getId() != null) {
						criteria.addEqualExpression(dossierBean.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID), getCustomer().getId());
					}
					if (getDossierType() != null && getDossierType().getId() != null) {
						criteria.addEqualExpression(dossierBean.getFieldName(IProjectAlias.DOSSIER_DOSSIER_TYPE_ID), getDossierType().getId());
					}
					if (!StringUtils.isBlank(getDossierNumber())) {
						criteria.addEqualExpression(dossierBean.getFieldName(IProjectAlias.DOSSIER_NUMBER), getDossierNumber());
					}
					criteria.addEqualExpression(dossierBean.getFieldName(IProjectAlias.DOSSIER_STATUS), DossierStatus.ACTIVE);
					List<ITransferObject> list = dossierBean.getList(criteria);
					for (ITransferObject to:list) {
						Dossier d = (Dossier) to;
						addDossier(campaign,d);	
					}
				} else {
					IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_ACTIVITY_TYPE_ID), getActivityType().getId());
					if (getCustomer() != null && getCustomer().getId() != null) {
						criteria.addEqualExpression("Activity.dossier.customer.id", getCustomer().getId());
					}
					List<ITransferObject> list = activityBean.getList(criteria);
					for (ITransferObject to:list) {
						Activity a = (Activity) to;
						addDossier(campaign,a.getDossier());
					}
				}
			}
		}catch (ManagerBeanException e) {
			String msg = "Imposible realizar la búsqueda de expedientes";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
    
	private void addDossier(Campaign campaign,Dossier dossier) throws ManagerBeanException {
		// Si el dossier ya está añadido en la campaña, se excluye.
		IManagerBean cdBean = BeanManager.getManagerBean(CampaignDossier.class);
		Criteria c = new Criteria();
		c.addEqualExpression(cdBean.getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CAMPAIGN_ID), campaign.getId());
		c.addEqualExpression(cdBean.getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_DOSSIER_ID), dossier.getId());
		List<ITransferObject> ex = cdBean.getList(c);
		if (ex.size() == 0) {
			CampaignDossier cd = new CampaignDossier();
			cd.setCampaign(campaign);
			cd.setDossier(dossier);
			boolean added = false;
			for (CampaignDossier saved: getDossiers()) {
				if (saved.getDossier().equals(cd.getDossier())){
					added = true;
				}
			}
			if (!added) {
				getDossiers().add(cd);	
			}
		}

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
	
	public void dossierChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			if (getCustomer() == null) {
				Dossier dossier = (Dossier) event.getNewValue();
				Customer c = dossier.getCustomer();
				setCustomer(c);
				LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), c);
				customerChanged(e);
			}
		}
	}
	
	public void customerChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null) {
			setAvailableDossiers(null);
		}
	}

	@SuppressWarnings("unchecked")
	public void removeDossier(ActionEvent event) {
		int index = getModel().getRowIndex();
		List list = (List) getModel().getWrappedData();
		list.remove(index);
	}
   
}