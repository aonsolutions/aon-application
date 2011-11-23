package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.alfresco.webservice.util.Constants;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.dao.AlfrescoCategoryDAO;
import com.code.aon.document.dao.AlfrescoDAO;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AlfrescoCategoryController extends BasicController {

	private AlfrescoDAO alfrescoDAO;
	
	private BasicManagerBean alfrescoManagerBean;
	
	private List<AlfrescoCategory> categories;
	
	private Map<String, AlfrescoCategory> categoryMap;
	
	private List<SelectItem> categoryTree;

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			String user = mc.getPrincipal().getShortName();
			this.alfrescoDAO = new AlfrescoCategoryDAO(user, user);
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

	public List<SelectItem> getCategories() throws ManagerBeanException {
		if ( categoryTree == null ) {
			updateCategories();
		}
		return categoryTree;
	}	
	
	public void updateCategories() throws ManagerBeanException {
		categoryTree = new LinkedList<SelectItem>();
		categories = new LinkedList<AlfrescoCategory>();
		categoryMap = new HashMap<String, AlfrescoCategory>();
    	Criteria criteria = new Criteria();
    	criteria.addOrder(alfrescoManagerBean.getFieldName(Constants.PROP_NAME));
    	for( ITransferObject to : alfrescoManagerBean.getList(criteria) ) {
    		AlfrescoCategory category = (AlfrescoCategory) to;
			SelectItem item = new SelectItem(category, category.getName());
			categoryTree.add(item);
			categories.add(category);
			categoryMap.put(category.getId().getUuid(), category);
    	}		
	}
	
	public AlfrescoCategory getCategory( String uuid ) {
		return categoryMap.get(uuid);
	}
	
}
