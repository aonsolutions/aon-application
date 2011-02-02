package com.code.aon.ui.company.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the CompanyAddressController.
 */
public class CompanyAddressListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAddressListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress rAddress = (RegistryAddress) event.getController().getTo();
		if(rAddress.getAddressType() == null){
			rAddress.setAddressType(AddressType.DELEGATION);
		}
	}	

	/**
	 * Removes the workPlace related with the current RegistryAddress
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress address = (RegistryAddress) event.getController().getTo();
		if( address.getAddressType().equals(AddressType.MAIN) || address.getAddressType().equals(AddressType.DELEGATION) ) {
			List<WorkPlace> workPlaces = obtainWorkPlaces( address );
			try {
				if (! workPlaces.isEmpty() ) {
					IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);	
					for( WorkPlace workPlace : workPlaces ) {
						workPlace.setActive( false );
						workPlaceBean.insertOrUpdate( workPlace );
					}
				}
			} catch (ManagerBeanException e) {
				//	TODO i18n
				String message = "No se puede borrar esta dirección: " + address.getId() 
								+ " ya que está asociada al Centro de Trabajo: " + workPlaces.get(0).getDescription()
								+ ", desvincule 1º la relación de Empleados que trabajan en él.";
				throw new ControllerListenerException( message, e);
			}
		}
	}

	/**
	 * Obtains the workPlace related with the RegistryAddress
	 * 
	 * @param registryAddress the registry address
	 * 
	 * @return the work place
	 */
	@SuppressWarnings("unchecked")
	private List<WorkPlace> obtainWorkPlaces(RegistryAddress registryAddress) {
		try {
			IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workPlaceBean.getFieldName(ICompanyAlias.WORK_PLACE_ADDRESS_ID), registryAddress.getId());
			return (List) workPlaceBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining workPlace with address= " + registryAddress.getId(), e);
		}
		return null;
	}

}