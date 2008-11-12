package com.code.aon.ui.finance.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ControllerSearchListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(ControllerSearchListener.class.getName());
	
	private Criteria criteria;
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			if ( criteria != getController().getCriteria() ) {			
				completeCriteria();
				criteria = getController().getCriteria();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
		}
	}

	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		this.criteria = null;
		try {			
			init();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
		}
	}

	protected void init() throws ManagerBeanException {
	}

	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
	}
	
	protected void addEnumToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( value != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}	

}