package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.customer.controller.ICustomerConstants.CUSTOMER_CONTROLLER_NAME;
import static com.code.aon.ui.customer.controller.ICustomerConstants.CUSTOMER_SEARCH_LISTENER_NAME;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.Segment;
import com.code.aon.ui.customer.event.CustomerSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AlumnReclassificationController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(AlumnReclassificationController.class);
	
	private Segment segment;
	
	private CustomerStatus customerStatus;	

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public CustomerStatus getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(CustomerStatus customerStatus) {
		this.customerStatus = customerStatus;
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		this.customerStatus = null;
		try {
			setSegment((Segment)BeanManager.getManagerBean(Segment.class).createNewTo());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}	
	
    public String getSegmentList() throws ManagerBeanException {
    	if ( getModel().isRowAvailable() ) {
    		Customer customer = (Customer) getSelectedTO();
    		IManagerBean rsBean = BeanManager.getManagerBean(RegistrySegment.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(rsBean.getFieldName(IEntityAlias.REGISTRY_SEGMENT_REGISTRY_ID), customer.getRegistry().getId());
    		List<String> segments = new LinkedList<String>();
    		for( ITransferObject to : rsBean.getList(criteria) ) {
    			segments.add( ((RegistrySegment)to).getSegment().getName() );
    		}
    		return StringUtils.join(segments, ", ");
    	}
		return null;
	}	
	
	public void onAssign(ActionEvent event){
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			IManagerBean rsBean = BeanManager.getManagerBean(RegistrySegment.class);
			for( Serializable id : getCheckList() ) {
				Customer customer = (Customer) customerBean.get(id);
				if( (segment != null) && (segment.getId() != null)){
					RegistrySegment rs = new RegistrySegment();
					rs.setRegistry(customer.getRegistry());
					rs.setSegment(segment);
					rsBean.insert(rs);
				}
				if (customerStatus != null) {
					customer.setStatus(customerStatus);
				}
				customerBean.update(customer);
			}
			updateCustomerController(event, getCheckList());
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to assign segment or status to selected alumns");
			LOGGER.error( "Unable to assign segment or status to selected alumns", e);
			throw new AbortProcessingException(e);
		}
	}

	protected void updateCustomerController( ActionEvent event, Collection<Serializable> list ) throws ManagerBeanException {
		IController controller = FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
		controller.clearCriteria();
		Criteria criteria = controller.getCriteria();
		CustomerSearchListener csl = (CustomerSearchListener) AonUtil.getRegisteredBean(CUSTOMER_SEARCH_LISTENER_NAME);
		csl.setCustomerStatuses(null);
		criteria.addInExpression(controller.getFieldName(IEntityAlias.CUSTOMER_ID), list );
		controller.onSearch(event);
	}	
}