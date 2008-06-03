/**
 * 
 */
package com.code.aon.ui.employee.event;

import java.util.ResourceBundle;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.Expenditures;
import com.code.aon.employee.ExpendituresItems;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.employee.controller.ExpendituresController;
import com.code.aon.ui.employee.util.Constants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * The listener class for receiving expenditures events. 
 * 
 * @author iayerbe
 *
 */
public class ExpendituresControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = -5438237066694446245L;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ExpendituresController ec = (ExpendituresController) event.getController();
		Expenditures e = (Expenditures) ec.getTo();
		if ( e.getDate().before( ec.getResource().getStartingDate() ) ) {
			String baseName = 
				AonUtil.getConfigurationController().getApplicationBundles().get( Constants.APPLICATION_BUNDLE_BASE_NAME );
			ResourceBundle bundle = ResourceBundle.getBundle( baseName );
			throw new ControllerListenerException( bundle.getString( "aon_employee_expenditures_date_exception" ) );
		}
		e.setResource( ec.getResource().getId() );
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ExpendituresController ec = (ExpendituresController) event.getController();
		Expenditures e = (Expenditures) ec.getTo();
		if ( e.getDate().before( ec.getResource().getStartingDate() ) ) {
			String baseName = 
				AonUtil.getConfigurationController().getApplicationBundles().get( Constants.APPLICATION_BUNDLE_BASE_NAME );
			ResourceBundle bundle = ResourceBundle.getBundle( baseName );
			throw new ControllerListenerException( bundle.getString( "aon_employee_expenditures_date_exception" ) );
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ExpendituresController ec = (ExpendituresController) event.getController();
		loadItem( (Expenditures) ec.getTo() );
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ExpendituresController ec = (ExpendituresController) event.getController();
		loadItem( (Expenditures) ec.getTo() );
	}

	private void loadItem(Expenditures e) {
		try {
			IManagerBean bean = BeanManager.getManagerBean( ExpendituresItems.class );
			Criteria criteria = new Criteria();
			String field = bean.getFieldName(IEmployeeAlias.EXPENDITURES_ITEMS_ID);
			criteria.addEqualExpression( field, e.getItem().getId() );
			ExpendituresItems item = (ExpendituresItems) bean.getList( criteria ).get( 0 );
			e.setItem( item );
		} catch (ManagerBeanException e1) {
		}
	}
}
