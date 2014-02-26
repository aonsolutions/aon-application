package com.code.aon.ui.product.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicTemplateController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemCatalogueReportController extends BasicTemplateController implements ICollectionProvider {
	
	private WorkPlace workPlace;
	
	private Department department;
	
	private List<ItemCalalogueReport> list;
	
	public List<ItemCalalogueReport> getList() {
		return list;
	}

	public void setList(List<ItemCalalogueReport> list) {
		this.list = list;
	}
	
	private DataModel model;
	
	public DataModel getModel() {
		if(model==null){
			model = new ListDataModel(getList());
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
		setDepartment(null);
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<SelectItem> getDepartments() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(WorkplaceDepartment.class);
		Criteria criteria = new Criteria();
		if(getWorkPlace()!=null && getWorkPlace().getId()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), getWorkPlace().getId());
		} else {
			CompanyCollectionsController collections = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			criteria.addInExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), collections.getCurrentUserWorkPlacesIds());
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_DEPARTMENT_NAME));
		List<Department> includedList = new ArrayList<Department>();
		for (ITransferObject ito : bean.getList(criteria)) {
			WorkplaceDepartment wd = (WorkplaceDepartment)ito;
			if( !includedList.contains(wd.getDepartment()) ){
				includedList.add(wd.getDepartment());
				SelectItem item = new SelectItem(wd.getDepartment(), wd.getDepartment().getName());
				list.add(item);
			}
		}
		return list;
	}
	
	public void onEditSearch(ActionEvent event) throws ManagerBeanException {
		setWorkPlace(null);
		setDepartment(null);
		setModel(null);
	}
	
	public String afterSearchAction() {
		return listAction();
	}
	
	public String searchAction() {
		return getBeanName()+IController.SEARCH_SUFFIX;
	}
	
	public String listAction() {
		return getBeanName()+IController.LIST_SUFFIX;
	}
	
	public void onSearch(ActionEvent event) throws HibernateException, ManagerBeanException {
		Date startDate = new Date();
		System.out.println("********* INICIO: "+startDate);
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		HibernateUtil.setCloseSession(false);
		Query query = session.createQuery(
                " select ci, wd" +
                " from CatalogueItem as ci, WorkplaceDepartment as wd" +
				" where ci.catalogue=wd.catalogue" +
				" and ci.item.status=" + ProductStatus.ACTIVE.ordinal() +
				getWorkPlaceClause() + 
				getDepartmentClause() + 
                " and " + DomainManager.getSQLWhereClause("ci.domain") +
                " order by wd.workPlace, wd.department, ci.item.product.code");
		setList(new LinkedList<ItemCatalogueReportController.ItemCalalogueReport>());
		Iterator<?> iter = query.list().iterator();
		Date queryDate = new Date();
		System.out.println("********* QUERY: "+ queryDate);
		System.out.println("********* Tiempo transcurrido: "+ (queryDate.getTime() - startDate.getTime()) + " milisegundos");
		while (iter.hasNext()){
			Object[] o = (Object[]) iter.next();
			ItemCalalogueReport item = new ItemCalalogueReport();
			item.setCatalogueItem( (CatalogueItem) o[0] );
			item.setWorkplaceDepartment( (WorkplaceDepartment) o[1] );
			getList().add(item);
		}
		HibernateUtil.setCloseSession(true);
		Date endDate = new Date();
		System.out.println("********* FIN: "+ endDate);
		System.out.println("********* Tiempo transcurrido: "+ (endDate.getTime() - queryDate.getTime()) + " milisegundos");
		System.out.println("********* Tiempo TOTAL: "+ (endDate.getTime() - startDate.getTime()) + " milisegundos");
	}
	
	private String getWorkPlaceClause() throws ManagerBeanException{
		CompanyCollectionsController collections = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		String clause = " and wd.workPlace";
		if( getWorkPlace()!=null && getWorkPlace().getId()!=null ){
			clause += " = " + getWorkPlace().getId();
		} else {
			clause += " in (" + StringUtils.join(collections.getCurrentUserWorkPlacesIds(), ",") + ")";
		}
		return clause;
	}

	private String getDepartmentClause() {
		String clause = "";
		if( getDepartment()!=null && getDepartment().getId()!=null ){
			clause = " and wd.department=" + getDepartment().getId();
		}
		return clause;
	}

	@Override
	public Collection<ItemCalalogueReport> getCollection() {
		return getList();
	}

	@Override
	public Collection<ItemCalalogueReport> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}
	
	public class ItemCalalogueReport {
		private CatalogueItem catalogueItem;
		private WorkplaceDepartment workplaceDepartment;
		public CatalogueItem getCatalogueItem() {
			return catalogueItem;
		}
		public void setCatalogueItem(CatalogueItem catalogueItem) {
			this.catalogueItem = catalogueItem;
		}
		public WorkplaceDepartment getWorkplaceDepartment() {
			return workplaceDepartment;
		}
		public void setWorkplaceDepartment(WorkplaceDepartment workplaceDepartment) {
			this.workplaceDepartment = workplaceDepartment;
		}
	}
	
}