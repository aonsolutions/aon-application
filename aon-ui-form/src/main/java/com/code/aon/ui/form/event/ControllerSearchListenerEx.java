package com.code.aon.ui.form.event;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.IController;

/**
 * ControllerListener prepared for complex search pages.
 * 
 * @author Consulting & Development. Aimar Tellitu - 15-nov-2011
 *
 */
public class ControllerSearchListenerEx extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerSearchListenerEx.class);
	
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
	public void beforeModelSearched(ControllerEvent event) throws ControllerListenerException {
		try {
			this.currentController = event.getController();
			Criteria criteria = this.currentController.getCriteria();
			completeCriteria( criteria );
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
		values = ArrayUtils.removeElement(values, null);
		if (! ArrayUtils.isEmpty(values) ) {
			criteria.addInExpression(alias, Arrays.asList(values));
		}
	}	

}