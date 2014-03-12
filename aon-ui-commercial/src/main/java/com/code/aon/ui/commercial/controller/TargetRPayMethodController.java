package com.code.aon.ui.commercial.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.commercial.Target;
import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;
import com.code.aon.ui.util.AonUtil;

public class TargetRPayMethodController extends RegistryPayMethodController{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		if (isNegotiableDocument()) {
			Target target = (Target) getMasterController().getTo();
			return getAllBanks(target.getRegistry());
		}
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getAllCompanyBanks();
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		if (isNegotiableDocument()) {
			Target target = (Target) getMasterController().getTo();
			return getActiveBanks(target.getRegistry());
		}
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getActiveCompanyBanks();
	}

}
