package com.code.aon.ui.company.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.CNAE;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseActivity;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.enumeration.CCCType;
import com.code.aon.company.enumeration.EnterpriseActivityType;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.registry.controller.RegistryController;

public class EnterpriseController extends RegistryController implements ICompanyConstants {
	
	private EnterpriseActivity activity;
	
	private EnterpriseCCC ccc;
	
	private boolean activityDirty;
	
	private boolean cccDirty;

    public EnterpriseActivity getActivity() {
		return activity;
	}

	public void setActivity(EnterpriseActivity activity) {
		this.activity = activity;
	}

	public EnterpriseCCC getCCC() {
		return ccc;
	}

	public void setCCC(EnterpriseCCC ccc) {
		this.ccc = ccc;
	}
	
	public boolean isActivityDirty() {
		return activityDirty;
	}

	public void setActivityDirty(boolean activityDirty) {
		this.activityDirty = activityDirty;
	}

	public boolean isCCCDirty() {
		return cccDirty;
	}

	public void setCCCDirty(boolean cccDirty) {
		this.cccDirty = cccDirty;
	}

	/**
     * Gets the addresses of the enterprise.
     * 
     * @return the addresses of the enterprise
     * @throws ManagerBeanException 
     */
    public List<SelectItem> getAddresses() throws ManagerBeanException {
    	LinkedList<SelectItem> addresses = new LinkedList<SelectItem>();
    	Enterprise enterprise = (Enterprise) getTo();
		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), enterprise.getRegistry().getId());
		criteria.addOrder(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS));
		List<ITransferObject> list = registryAddressBean.getList(criteria);
		for (ITransferObject to : list) {
			RegistryAddress rAddress = (RegistryAddress)to;
			addresses.add(new SelectItem(rAddress, rAddress.getAddress()));
		}
    	return addresses;
    }	
    
    /**
     * Gets the CCCs of the enterprise.
     * 
     * @return the CCCs of the enterprise
     * @throws ManagerBeanException 
     */
    public List<SelectItem> getCCCs() throws ManagerBeanException {
    	LinkedList<SelectItem> cccs = new LinkedList<SelectItem>();
    	Enterprise enterprise = (Enterprise) getTo();
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addOrder(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_CCC));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			EnterpriseCCC ccc = (EnterpriseCCC)to;
			cccs.add(new SelectItem(ccc, ccc.getCCC()));
		}
    	return cccs;
    }	    

	private void loadMainActivity() throws ManagerBeanException {
		Enterprise enterprise = (Enterprise) getTo();
		IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(activityBean.getFieldName(ICompanyAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addEqualExpression(activityBean.getFieldName(ICompanyAlias.ENTERPRISE_ACTIVITY_TYPE), EnterpriseActivityType.PRINCIPAL);
		List<ITransferObject> activities = activityBean.getList(criteria);
		if (! activities.isEmpty() ) {
			setActivity( (EnterpriseActivity) activities.get(0) );
			IManagerBean cccBean = BeanManager.getManagerBean(EnterpriseCCC.class);
			Criteria cccCriteria = new Criteria();
			cccCriteria.addEqualExpression(cccBean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ID), getActivity().getId());
			cccCriteria.addEqualExpression(cccBean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL);
			List<ITransferObject> cccs = cccBean.getList(cccCriteria);
			if (! cccs.isEmpty() ) {
				setCCC( (EnterpriseCCC) cccs.get(0) );
			}
		}
	}    
	
	public void saveMainActivity() throws ManagerBeanException {
		Enterprise enterprise = (Enterprise) getTo();
		if ( isActivityDirty() ) {
			IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
			getActivity().setEnterprise(enterprise);
			activityBean.insertOrUpdate(getActivity());
			setActivityDirty(false);
		}
		if ( isCCCDirty() ) {
			IManagerBean cccBean = BeanManager.getManagerBean(EnterpriseCCC.class);	
			getCCC().setActivity(getActivity());
			if ( getCCC().getGeozone() == null ) {
				getCCC().setGeozone(getMainGeoZone(enterprise));
			}
			cccBean.insertOrUpdate(getCCC());
			setCCCDirty(false);
		}
	}    	
	
	public void resetMainActivity() {
		setActivity( new EnterpriseActivity() );
		getActivity().setType( EnterpriseActivityType.PRINCIPAL );
		getActivity().setCnae( new CNAE() );
		setCCC( new EnterpriseCCC() );    		
		getCCC().setType( CCCType.PRINCIPAL );
    	setActivityDirty(false);
    	setCCCDirty(false);		
	}
    
    public void initMainActiviy() throws ManagerBeanException {
    	resetMainActivity();
    	if (! isNew() ) {
    		loadMainActivity();
    	}
    }
    
    public void onCNAEChanged( LookupChangeEvent event ) {
    	setActivityDirty(true);
    	if ( event.getNewValue() != null ) {
    		CNAE cnae = (CNAE) event.getNewValue();
    		getActivity().setDescription( cnae.getTitle() );
    	}
    }

    public void activityChanged( ValueChangeEvent event ) {
    	setActivityDirty(true);
    }
    
    public void cccChanged( ValueChangeEvent event ) {
    	setCCCDirty(true);
    }
    
    private GeoZone getMainGeoZone( Enterprise enterprise ) throws ManagerBeanException {
    	GeoZone geoZone = null;
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), enterprise.getRegistry().getId());
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
		List<ITransferObject> addresses = bean.getList(criteria);
		if (! addresses.isEmpty() ) {
			geoZone = ((RegistryAddress) addresses.get(0)).getGeozone();
		}
    	return geoZone;
    }
    
}