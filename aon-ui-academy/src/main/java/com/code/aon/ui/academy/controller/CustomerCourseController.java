package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.customer.controller.ICustomerConstants.CUSTOMER_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerCourseController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerLoanController.class);

	private boolean showNewWindow;
	
	private boolean history;
	
	private IControllerListener courseFilter;
	
	public boolean isShowNewWindow() {
		return showNewWindow;
	}

	public void setShowNewWindow(boolean showNewWindow) {
		this.showNewWindow = showNewWindow;
	}
	
	public boolean isHistory() {
		return history;
	}

	public void setHistory(boolean history) {
		this.history = history;
		updateModel();		
	}
	
	@Override
	public void clearCriteria() throws ManagerBeanException {
		changeInitExpression();		
		super.clearCriteria();
	}

	private void changeInitExpression() {
		try {
			List<Expression> list = new LinkedList<Expression>();
			if ( isHistory() ) {
				Expression courseInactive = ExpressionUtilities.getEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseStatus.INACTIVE);
				Expression alumnInactive = ExpressionUtilities.getEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_STATUS), CourseAlumnStatus.INACTIVE);
				list.add( ExpressionUtilities.getOrExpression(courseInactive, alumnInactive) );
			} else {
				list.add( ExpressionUtilities.getEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseStatus.ACTIVE) );  
				list.add( ExpressionUtilities.getEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_STATUS), CourseAlumnStatus.ACTIVE) );
			}
			setInitExpressions(list);			
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public boolean isShowTab() throws ManagerBeanException {
		Customer customer = (Customer) getMasterController().getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
		return getManagerBean().getCount(criteria) > 0;
	}

	private void updateModel() {
		try {
			clearCriteria();
			Criteria criteria = getCriteria();
			IController controller = FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
			Serializable id = controller.getManagerBean().getId(controller.getTo());
			criteria.addEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), id);
			onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getCourseList() throws ManagerBeanException {
		IController controller = FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Serializable id = controller.getManagerBean().getId(controller.getTo());
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), id);
		criteria.addEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseStatus.ACTIVE );  
		criteria.addEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_STATUS), CourseAlumnStatus.ACTIVE );
		String courseId = getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID);
		return getManagerBean().getList(new ProjectionList(Projection.property(courseId)), criteria);
	}
	
	public IControllerListener getCourseFilter() {
		if ( this.courseFilter == null ) {
			this.courseFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {
						String courseId = controller.getFieldName(IEntityAlias.COURSE_ID);
						for( Integer id : getCourseList() ) {
							controller.getCriteria().addNotEqualExpression(courseId, id);	
						}
					} catch (ManagerBeanException e) {
						throw new ControllerListenerException(e);
					} 
				}		
			};
		}
		return this.courseFilter;
	}	

}
