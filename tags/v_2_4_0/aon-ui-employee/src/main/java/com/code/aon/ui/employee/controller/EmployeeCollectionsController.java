package com.code.aon.ui.employee.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.employee.ExpendituresItems;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.employee.util.Constants;

public class EmployeeCollectionsController {
	
	private List<SelectItem> geoZones;

	public List<SelectItem> getEmployees() throws ManagerBeanException {
		List<SelectItem> employees = new LinkedList<SelectItem>();
		IManagerBean employeeBean = BeanManager.getManagerBean(Employee.class);
		Criteria criteria = new Criteria();
		criteria.addOrder( employeeBean.getFieldName( ICompanyAlias.EMPLOYEE_REGISTRY_SURNAME ) );
		criteria.addOrder( employeeBean.getFieldName( ICompanyAlias.EMPLOYEE_REGISTRY_NAME ) );
		Iterator<ITransferObject> iter = employeeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Employee employee = (Employee) iter.next();
			SelectItem item = new SelectItem(employee.getId(),
					employee.getRegistry().getName() + (employee.getRegistry().getSurname()==null?Constants.EMPTY_STRING:" " + employee.getRegistry().getSurname()) );
			employees.add(item);
		}
		return employees;
	}
	
	public List<SelectItem> getGeoZones() throws ManagerBeanException {
		if (geoZones == null) {
			geoZones = new LinkedList<SelectItem>();
			IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(geozoneBean.getFieldName(IGeoZoneAlias.GEO_ZONE_NAME));
			Iterator<ITransferObject> iter = geozoneBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				GeoZone geozone = (GeoZone) iter.next();
				SelectItem item = new SelectItem(geozone.getId(), geozone.getName());
				geoZones.add(item);
			}
		}
		return geoZones;
	}

	public List<SelectItem> getExpendituresItems() throws ManagerBeanException {
		List<SelectItem> expendituresItems = new LinkedList<SelectItem>();
		IManagerBean expeditureItemsBean = BeanManager.getManagerBean(ExpendituresItems.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(expeditureItemsBean.getFieldName(IEmployeeAlias.EXPENDITURES_ITEMS_NAME));
		Iterator<ITransferObject> iter = expeditureItemsBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			ExpendituresItems item = (ExpendituresItems) iter.next();
			expendituresItems.add( new SelectItem( item.getId(), item.getName() ) );
		}
		return expendituresItems;
	}
	
}