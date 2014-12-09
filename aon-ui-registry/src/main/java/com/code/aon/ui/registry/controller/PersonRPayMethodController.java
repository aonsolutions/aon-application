package com.code.aon.ui.registry.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.person.Person;
import com.code.aon.ui.util.AonUtil;

public class PersonRPayMethodController extends RegistryPayMethodController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		Person person = (Person) getMasterController().getTo();
		if (isNegotiableDocument()) {
			return getAllBanks(person.getRegistry());
		}
		RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getAllRegistryBanks(person.getRegistry());
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		Person person = (Person) getMasterController().getTo();
		if (isNegotiableDocument()) {
			return getActiveBanks(person.getRegistry());
		}
		RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getActiveRegistryBanks(person.getRegistry());
	}
}