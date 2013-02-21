package com.code.aon.ui.academy.controller;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.CustomerFee;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerFeeToGroupController extends GroupSelectionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerFeeToGroupController.class);
	
	private CustomerFee fee;
	
	private boolean longDescription;
	
	public CustomerFee getFee() {
		return fee;
	}

	public void setFee(CustomerFee fee) {
		this.fee = fee;
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		initializeFee();
	}
	
	public void onAssign(ActionEvent event){
		try {
			IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
			if(fee.getQuantity() <= 0){
				String message = "Quantity must be > 0";
				AonUtil.addErrorMessage(message);
				LOGGER.error(message);
				throw new AbortProcessingException(message);
			} else if(! getCheckList().isEmpty()){
				for( Serializable id : getCheckList() ) {
					Course course = (Course) courseBean.get(id);
					createCustomerFeesToCourse(course);
				}
			}
			updateCourseController(event);
		} catch (ManagerBeanException e) {
			String message = "Unable to add fee to selected courses";
			AonUtil.addErrorMessage(message);
			LOGGER.error(message, e);
			throw new AbortProcessingException(e);
		}
	}

	private void createCustomerFeesToCourse(Course course) throws ManagerBeanException {
		IManagerBean courseAlumBean = BeanManager.getManagerBean(CourseAlumn.class);
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseAlumBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId());
		fee.setWorkPlace(course.getWorkPlace());
		for( ITransferObject to : courseAlumBean.getList(criteria) ) {
			CourseAlumn courseAlumn = (CourseAlumn) to;
			if ( courseAlumn.getStatus() == CourseAlumnStatus.ACTIVE ) {
				fee.setCustomer(courseAlumn.getCustomer());
				customerFeeBean.insert(fee);				
			}
		}
	}

	private void initializeFee() {
		fee = new CustomerFee();
		fee.setItem(new Item());
		fee.getItem().setProduct(new Product());
		fee.setCustomer(null);
		fee.setDescription("");
		fee.setDiscountExpression(new DiscountExpression("0.0"));
		fee.setWorkPlace(null);
		fee.setInitialDate(new Date());
		fee.setBillingDate(new Date());
		fee.setSecurityLevel(SecurityLevel.OFFICIAL);
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		if (StringUtils.equals(fee.getItem().getProduct().getName().trim(), fee.getDescription().trim())) {
			String longDescription = fee.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				fee.setDescription(fee.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}
	
	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			fee.setItem(item);
			fee.setDescription(item.getFullName());
			fee.setPrice(item.getPrice());
		}
	}	
	
	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}
		
}