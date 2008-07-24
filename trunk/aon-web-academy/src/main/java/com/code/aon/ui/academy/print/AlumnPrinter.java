package com.code.aon.ui.academy.print;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.academy.print.ReportAlumn;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.person.Person;
import com.code.aon.person.dao.IPersonAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.util.AonUtil;

public class AlumnPrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(AlumnPrinter.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<ReportAlumn> reportAlumnList = new LinkedList<ReportAlumn>();
		try {
			CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
            Iterator iter = customerController.getManagerBean().getList(customerController.getCriteria()).iterator();
            while(iter.hasNext()){
				Customer alumn = (Customer)iter.next();
				ReportAlumn reportAlumn = new ReportAlumn();
				reportAlumn.setAlumn(alumn);
				reportAlumn.setPhone(obtainPhone(alumn.getRegistry()));
				reportAlumn.setCellular(obtainCellular(alumn.getRegistry()));
				reportAlumn.setCourseCode(obtainCourseCode(alumn.getRegistry()));
				reportAlumn.setBirthDate(obtainBirthDate(alumn.getRegistry()));
				reportAlumnList.add(reportAlumn);
			}
			return reportAlumnList;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining courseDetailList Collection", e);
		}
		return reportAlumnList;
	}
	
	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	@SuppressWarnings("unchecked")
	protected String obtainPhone(Registry registry) {
		String phone = "";
		try {
			IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
			criteria.addEqualExpression(registryMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.FIXED_PHONE);
			Iterator iter = registryMediaBean.getList(criteria).iterator();
			if(iter.hasNext()){
				RegistryMedia media = (RegistryMedia)iter.next(); 
				phone += (phone.equals("")?"":" | ") + media.getValue();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining phone of alumn with id= " + registry.getId(), e);
		}
		return phone;
	}
	
	@SuppressWarnings("unchecked")
	protected String obtainCellular(Registry registry) {
		String cellular = "";
		try {
			IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
			criteria.addEqualExpression(registryMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.CELLULAR);
			Iterator iter = registryMediaBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryMedia media = (RegistryMedia)iter.next();
				cellular += (cellular.equals("")?"":" | ") + media.getValue();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining cellular of alumn with id= " + registry.getId(), e);
		}
		return cellular;
	}
	
	@SuppressWarnings("unchecked")
	protected String obtainCourseCode(Registry registry) {
		String course = "";
		try {
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), registry.getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_STATUS), CourseStatus.ACTIVE);
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			criteria.addOrder(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_CODE));
			Iterator iter = courseAlumnBean.getList(criteria).iterator();
			while(iter.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)iter.next();
				course += (course.equals("")?"":" | ") + courseAlumn.getCourse().getCode();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining course of alumn with id= " + registry.getId(), e);
		}
		return course;
	}

	@SuppressWarnings("unchecked")
	protected Date obtainBirthDate(Registry registry) {
		try {
			IManagerBean personBean = BeanManager.getManagerBean(Person.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(personBean.getFieldName(IPersonAlias.PERSON_REGISTRY_ID), registry.getId());
			Iterator iter = personBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((Person)iter.next()).getBirthDate();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining birthDate of alumn with id= " + registry.getId(), e);
		}
		return null;
	}
}