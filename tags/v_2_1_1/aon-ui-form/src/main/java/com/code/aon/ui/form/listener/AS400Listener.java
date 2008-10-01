package com.code.aon.ui.form.listener;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * AS400Listener is used if the program is running in an AS400.
 */
public class AS400Listener extends ControllerAdapter {

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = Logger.getLogger(AS400Listener.class
			.getName());
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ITransferObject to = event.getController().getTo();
		initializeNumbersAndStrings( to );
	}

	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ITransferObject to = event.getController().getTo();
		initializeNumbersAndStrings( to );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ITransferObject to = event.getController().getTo();
		rtrimStringProperties( to );
	}

	/**
	 * Rtrim string properties.
	 * 
	 * @param to the to
	 */
	private void rtrimStringProperties( ITransferObject to ) {
		try {	
			Class clazz = to.getClass();
			PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
			LOGGER.fine("Found " + pds.length + " properties");
			for (PropertyDescriptor pd : pds) {
				Class<?> fieldClass = pd.getPropertyType();
				if ( fieldClass.isAssignableFrom(String.class) ) {
					String value = (String) PropertyUtils.getProperty( to, pd.getName() );
					if ( value != null ) {
						String newValue = StringUtils.stripEnd( value, null );
						if (! value.equals(newValue) ) {
							PropertyUtils.setProperty( to, pd.getName(), newValue );
						}
					}
				}
			}
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, "Error rtrim strings in " + to, th);			
		}
	}

	/**
	 * Initialize numbers and strings.
	 * 
	 * @param to the to
	 */
	private void initializeNumbersAndStrings( ITransferObject to ) {
		try {	
			Class clazz = to.getClass();
			PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
			LOGGER.fine("Found " + pds.length + " properties");
			for (PropertyDescriptor pd : pds) {
				Class fieldClass = pd.getPropertyType();
				if ( Number.class.isAssignableFrom(fieldClass) ) {
					Object value = PropertyUtils.getProperty( to, pd.getName() );
					if ( value == null ) {
						Constructor constructor = fieldClass.getConstructor(new Class[]{String.class});
						Object newValue = constructor.newInstance( new Object[] {"0"} );
						PropertyUtils.setProperty( to, pd.getName(), newValue );
					}
				} else if ( String.class.isAssignableFrom(fieldClass) ) {
					Object value = PropertyUtils.getProperty( to, pd.getName() );
					if ( value == null ) {
						PropertyUtils.setProperty( to, pd.getName(), "" );
					}
				}
			}
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, "Error rtrim strings in " + to, th);			
		}
	}
	
}
