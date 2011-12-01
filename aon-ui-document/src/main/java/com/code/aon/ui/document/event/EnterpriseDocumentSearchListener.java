package com.code.aon.ui.document.event;


import static com.code.aon.document.IAlfrescoConstants.CREATED_SHORT;
import static com.code.aon.document.IAlfrescoConstants.DESCRIPTION_SHORT;
import static com.code.aon.document.IAlfrescoConstants.ENTERPRISE_ID_SHORT;
import static com.code.aon.document.IAlfrescoConstants.MIME_TYPE;
import static com.code.aon.document.IAlfrescoConstants.NAME_SHORT;
import static com.code.aon.document.IAlfrescoConstants.PROJECT_ID_SHORT;
import static com.code.aon.document.IAlfrescoConstants.TITLE_SHORT;
import static com.code.aon.document.dao.AlfrescoCategoryDAO.EMPTY_CATEGORY;
import static com.code.aon.ui.document.controller.IDocumentConstants.BUNDLE_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.INPUT_SEARCH_TEXT;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.document.controller.EnterpriseDocumentController;
import com.code.aon.ui.document.controller.IEnterpriseController;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseDocumentSearchListener extends ControllerSearchListenerEx implements IEnterpriseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocumentSearchListener.class);
	
	private static final String MAX_CONSTANT = "MAX";
	private static final String MIN_CONSTANT = "MIN";
	private static final String PATH_FIELD = "PATH";
	private static final String TEXT_FIELD = "TEXT";

	private Enterprise enterprise;
	private Project project;
	private Date[] createdDate;
	private Date[] modifiedDate;
	private Date[] referenceDate;
	private String name;
	private String description;
	private String title;
	private String text;
	private String mainText;
	private MimeType type;
	private List<AlfrescoCategory> categories;
	private boolean showList;
	private IControllerListener projectListener;
	
	public EnterpriseDocumentSearchListener() {
		reset();
		setMainText( AonUtil.getMessage(BUNDLE_NAME, INPUT_SEARCH_TEXT) );
		this.projectListener = new EnterpriseProjectListener(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date[] getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date[] createdDate) {
		this.createdDate = createdDate;
	}

	public Date[] getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date[] modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date[] getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date[] referenceDate) {
		this.referenceDate = referenceDate;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMainText() {
		return mainText;
	}

	public void setMainText(String mainText) {
		this.mainText = mainText;
	}

	public MimeType getType() {
		return type;
	}

	public void setType(MimeType type) {
		this.type = type;
	}
	
	public List<AlfrescoCategory> getCategories() {
		return categories;
	}
	
	public void setCategories(List<AlfrescoCategory> categories) {
		this.categories = categories;
		if (categories.isEmpty()) {
			categories.add(EMPTY_CATEGORY);
		}		
	}

	public int getCategoriesSize() {
		return (categories != null) ? categories.size() : 0;
	}
	
	public boolean isShowList() {
		return showList;
	}

	public void setShowList(boolean showList) {
		this.showList = showList;
	}

	public String getShowOpened() {
		return Boolean.toString(!this.showList);
	}

	public void setShowOpened(String showOpened) {
		this.showList = ! Boolean.parseBoolean(showOpened);
	}

	public void onClear(ActionEvent event) {
		reset();
	}
	
	public void reset() {
		setName(null);
		setDescription(null);
		setTitle(null);
		setCreatedDate(new Date[2]);
		setModifiedDate(new Date[2]);
		setReferenceDate(new Date[2]);
		setText(null);
		setType(null);
		setCategories( new LinkedList<AlfrescoCategory>() );
		setShowList(false);
		try {
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			setProject((Project) projectBean.createNewTo());
			Enterprise enterprise = null;
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			if ( mc.isMainEnterprise() ) {
				IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
				enterprise = (Enterprise) enterpriseBean.createNewTo();
			} else {
				enterprise = mc.getLoggedUser().getEnterprise();				
			}
			setEnterprise(enterprise);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

	@Override
	public void completeCriteria( Criteria criteria ) {
		if (! StringUtils.isEmpty(getName())) {
			criteria.addEqualExpression( NAME_SHORT, getName());
		}
		if (! StringUtils.isEmpty(getTitle())) {
			criteria.addEqualExpression( TITLE_SHORT, getTitle());
		}
		if (! StringUtils.isEmpty(getDescription())) {
			criteria.addEqualExpression( DESCRIPTION_SHORT, getDescription());
		}
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression( PROJECT_ID_SHORT, getProject().getId());			
		}
		if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
			criteria.addEqualExpression( ENTERPRISE_ID_SHORT, getEnterprise().getId());			
		}
		addDateRange(criteria, CREATED_SHORT, getCreatedDate());
		if (! StringUtils.isEmpty(getText()) ) {
			criteria.addEqualExpression( TEXT_FIELD, getText());
		}
		if ( getType() != null ) {
			criteria.addEqualExpression( MIME_TYPE, getType().getName());
		}
		if (! getCategories().isEmpty() ) {
			addCategoriesToCriteria(criteria, getCategories());
		}
		setShowList(true);
	}
	
	private void addDateRange( Criteria criteria, String alias, Date[] dates ) {
		if ( (dates[0] != null) || (dates[1] != null) ) {
			Object minor = (dates[0] != null) ? dates[0] : MIN_CONSTANT;
			Object mayor = (dates[1] != null) ? dates[1] : MAX_CONSTANT;
			criteria.addBetweenExpression( alias, minor, mayor );
		}		
	}

	private void addCategoriesToCriteria( Criteria criteria, List<AlfrescoCategory> categories ) { 
		Expression expToAdd = null;
		for( AlfrescoCategory category : categories ) {
			if ( (category != null) && (category.getId() != null) ) {
				String value = category.getSearchValue();
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(PATH_FIELD, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(PATH_FIELD, value);
					expToAdd = ExpressionUtilities.getAndExpression(expToAdd, exp);
				}				
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}		
	
	public void onAddCategory(ActionEvent event) {
		getCategories().add(EMPTY_CATEGORY);
	}
	
	public void onRemoveCategory(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		getCategories().remove(index);
		if (getCategories().isEmpty()) {
			getCategories().add(EMPTY_CATEGORY);
		}
	}		

	public IControllerListener getProjectListener() {
		return projectListener;
	}

	public void onMainSearch(ActionEvent event) {
		setText( getMainText() );
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		edc.onSearch(event);
	}
	
}