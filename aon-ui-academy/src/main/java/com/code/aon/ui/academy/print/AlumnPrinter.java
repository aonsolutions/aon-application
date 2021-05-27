package com.code.aon.ui.academy.print;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.AonVersion;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.controller.RegistryObservationController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AlumnPrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(AlumnPrinter.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		List<ReportAlumn> reportAlumnList = new LinkedList<ReportAlumn>();
		try {
			CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
            Iterator iter = customerController.getManagerBean().getList(customerController.getCriteria()).iterator();
            while(iter.hasNext()){
				Customer alumn = (Customer)iter.next();
				ReportAlumn reportAlumn = new ReportAlumn();
				reportAlumn.setAlumn(alumn);
				reportAlumn.setPhone(obtainPhone(alumn.getRegistry()));
				reportAlumn.setCellular(obtainCellular(alumn.getRegistry()));
				reportAlumn.setCourseCode(obtainCourseCode(alumn.getRegistry()));
				reportAlumn.setBirthDate(obtainBirthDate(alumn.getRegistry()));
				reportAlumn.setObservation(getObservation(alumn.getRegistry()));
				reportAlumnList.add(reportAlumn);
			}
			return reportAlumnList;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining courseDetailList Collection", e);
		}
		return reportAlumnList;
	}
	
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	@SuppressWarnings("rawtypes")
	protected String obtainPhone(Registry registry) {
		String phone = "";
		try {
			IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
			criteria.addEqualExpression(registryMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.FIXED_PHONE);
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
	
	protected String obtainCellular(Registry registry) {
		String cellular = "";
		try {
			IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
			criteria.addEqualExpression(registryMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.CELLULAR);
			Iterator<ITransferObject> iter = registryMediaBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryMedia media = (RegistryMedia)iter.next();
				cellular += (cellular.equals("")?"":" | ") + media.getValue();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining cellular of alumn with id= " + registry.getId(), e);
		}
		return cellular;
	}
	
	protected String obtainCourseCode(Registry registry) {
		String course = "";
		try {
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), registry.getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_STATUS), CourseStatus.ACTIVE);
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_CODE));
			Iterator<ITransferObject> iter = courseAlumnBean.getList(criteria).iterator();
			while(iter.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)iter.next();
				course += (course.equals("")?"":" | ") + courseAlumn.getCourse().getCode();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining course of alumn with id= " + registry.getId(), e);
		}
		return course;
	}

	protected Date obtainBirthDate(Registry registry) {
		try {
			IManagerBean personBean = BeanManager.getManagerBean(Person.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_ID), registry.getId());
			Iterator<ITransferObject> iter = personBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((Person)iter.next()).getBirthDate();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining birthDate of alumn with id= " + registry.getId(), e);
		}
		return null;
	}
	
	private RegistryNote getObservation(Registry registry){
		RegistryObservationController controller = (RegistryObservationController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_OBSERVATION_CONTROLLER_NAME);
		try {
			return controller.getRegistryObservation(registry);
		} catch (ManagerBeanException e) {
			// nada
		}
		return null;
	}


	public static class ReportAlumn implements ITransferObject {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private Customer alumn;
		private String phone;
		private String cellular;
		private String courseCode;	
		private Date birthDate;
		private RegistryNote observation;
		
		public Customer getAlumn() {
			return alumn;
		}
		public void setAlumn(Customer alumn) {
			this.alumn = alumn;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getCellular() {
			return cellular;
		}
		public void setCellular(String cellular) {
			this.cellular = cellular;
		}
		public String getCourseCode() {
			return courseCode;
		}
		public void setCourseCode(String courseCode) {
			this.courseCode = courseCode;
		}
		public Date getBirthDate() {
			return birthDate;
		}
		public void setBirthDate(Date birthDate) {
			this.birthDate = birthDate;
		}
		public RegistryNote getObservation() {
			return observation;
		}
		public void setObservation(RegistryNote observation) {
			this.observation = observation;
		}	
	}
}