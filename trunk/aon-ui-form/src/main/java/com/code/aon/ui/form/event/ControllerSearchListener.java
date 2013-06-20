package com.code.aon.ui.form.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;

/**
 * ControllerListener prepared for complex search pages.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-nov-2008
 *
 */
public class ControllerSearchListener extends ControllerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerSearchListener.class);
	
	private Criteria criteria;
	
	private IController currentController;
	
	@Override
	public IController getController() {
		if ( this.currentController != null ) {
			return this.currentController;
		}
		return super.getController();
	}

	/**
	 * Return the name of the field that corresponds to the parameter alias.
	 * 
	 * @param alias
	 * @return String
	 * @throws ManagerBeanException
	 */
	public String getFieldName(String alias) throws ManagerBeanException {
		return getController().getFieldName(alias);
	}	
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			this.currentController = event.getController();
			Criteria criteria = this.currentController.getCriteria();
			if ( this.criteria != criteria ) {			
				completeCriteria( criteria );
				this.criteria = criteria;
			}
			this.currentController = null;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (ExpressionException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		this.criteria = null;
		try {			
			init();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	/**
	 * Inits the.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	protected void init() throws ManagerBeanException {
	}

	/**
	 * Complete criteria.
	 * @param criteria 
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws ExpressionException the expression exception
	 */
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
	}
	
	/**
	 * Adds the enum to criteria.
	 * 
	 * @param criteria the criteria
	 * @param alias the alias
	 * @param values the values
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
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