package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.document.dao.AlfrescoCategoryDAO;
import com.code.aon.document.dao.AlfrescoDAO;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AlfrescoCategoryController extends BasicController {

	private AlfrescoDAO alfrescoDAO;
	
	private BasicManagerBean alfrescoManagerBean;

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			this.alfrescoDAO = new AlfrescoCategoryDAO(mc.getAlfrescoUser(), mc.getAlfrescoPassword());
			this.alfrescoManagerBean = new BasicManagerBean(this.alfrescoDAO);			
		}
		return this.alfrescoManagerBean;
	}
	
	@Override
	protected String getIdAlias() throws ManagerBeanException {
		return "ID";
	}
	
	public AlfrescoDAO getAlfrescoDAO() {
		return alfrescoDAO;
	}
	
}
