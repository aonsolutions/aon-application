package com.code.aon.ui.form.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;

/**
 * ControllerListener prepared for complex search pages.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-nov-2008
 *
 */
public class ControllerSearchListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(ControllerSearchListener.class.getName());
	
	private Criteria criteria;
	
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

	/**
	 * Inits the.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	protected void init() throws ManagerBeanException {
	}

	/**
	 * Complete criteria.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws ExpressionException the expression exception
	 */
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
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