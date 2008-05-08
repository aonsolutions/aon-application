package com.code.aon.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Stack;
import java.util.logging.Logger;

import javax.persistence.Transient;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.event.IManagerBeanListener;
import com.code.aon.common.event.IManagerBeanVetoListener;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerSupport;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.event.ManagerBeanVetoListenerSupport;

/**
 * Basic implementation of the <code>IManagerBean</code> class. 
 * 
 * @author 	Consulting & Development. Aimar Tellitu - 27-jun-2005
 * @since 	1.0
 * @see 	com.code.aon.common.IManagerBean
 * @see 	com.code.aon.common.BasicFinderBean
 * 
 */
public class BasicManagerBean extends BasicFinderBean implements IManagerBean {

	private static final Logger LOGGER = Logger.getLogger(BasicManagerBean.class.getName());
	
    // Manages the listeners.
	private ManagerBeanListenerSupport listeners;

    // Manages the vetoListeners.
	private ManagerBeanVetoListenerSupport vetoListeners;
	
	private Stack<Class> pojoDependences;

	/**
	 * Construct a BasicManagerBean.
	 * 
	 * @param dao
	 */
	public BasicManagerBean(IDAO dao) {
		super(dao);
	}

	/**
	 * Listener registration method. Tell the ManagerBeanListenerSupport to add a new
	 * <code>IManagerBeanListener</code>.
	 * 
	 * @param listener
	 */
	public void addManagerBeanListener( IManagerBeanListener listener ) {
		if (listeners == null) {
			listeners = new ManagerBeanListenerSupport();
		}
		listeners.addListener( listener );
	}

	/**
	 * Listener registration method. Tell the ManagerBeanVetoListenerSupport to add a new
	 * <code>IManagerBeanVetoListener</code>.
	 * 
	 * @param vetoListener
	 */
	public void addManagerBeanVetoListener( IManagerBeanVetoListener vetoListener ) {
		if (vetoListeners == null) {
			vetoListeners = new ManagerBeanVetoListenerSupport(); 
		}
		vetoListeners.addListener( vetoListener );
	}

	/**
	 * Return POJO dependences.
	 * 
	 * @return Stack<Class>
	 */
	private Stack<Class> getPojoDependences() {
		if (pojoDependences == null) {
			pojoDependences = new Stack<Class>();
		}
		return pojoDependences;
	}

	/**
	 * Return when it is necessary to initialize and when not.
	 * 
	 * @param bean
	 * @param pd
	 * @return boolean
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private boolean needInitialize(Object bean, PropertyDescriptor pd) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return (!pd.getPropertyType().isEnum())
				&& (!pd.getPropertyType().isArray())
				&& ((pd.getPropertyType().getModifiers() & (Modifier.INTERFACE | Modifier.ABSTRACT)) == 0)
				&& (pd.getWriteMethod() != null)
				&& (!pd.getReadMethod().isAnnotationPresent(Transient.class) && (PropertyUtils.getProperty(bean, pd
						.getName()) == null));
	}
	
	/**
	 * Initialize POJO.
	 * 
	 * @param to
	 * @throws ManagerBeanException
	 */
	public void initializePOJO(ITransferObject to) throws ManagerBeanException {
		try {
			Class clazz = to.getClass();
			LOGGER.fine("Initializing " + clazz.getName());
			getPojoDependences().push(clazz);
			PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
			LOGGER.fine("Found " + pds.length + " properties");
			for (PropertyDescriptor pd : pds) {
				Class fieldClass = pd.getPropertyType();
				String name = pd.getName();
				if (needInitialize(to, pd)) {
					if (ITransferObject.class.isAssignableFrom(fieldClass)) {
						LOGGER.fine("Initializing TO " + name + " property");
						ITransferObject childTO = (ITransferObject) fieldClass.newInstance();
						if (!getPojoDependences().contains(fieldClass)) {
							initializePOJO(childTO);
							getPojoDependences().pop();
						}
						PropertyUtils.setProperty(to, name, childTO);
						LOGGER.fine("Assigned TO " + fieldClass + " to " + clazz.getName());
					} else if (!fieldClass.getName().startsWith("java")) {
						LOGGER.fine("Initializing " + name + " property");
						Object o = fieldClass.newInstance();
						PropertyUtils.setProperty(to, name, o);
						LOGGER.fine("Assigned " + fieldClass + " to " + clazz.getName());
					}
				}
			}
		} catch (SecurityException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (InstantiationException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new ManagerBeanException(e.getMessage());
		}
	}
	
	public ITransferObject createNewTo() throws ManagerBeanException {
		try {
			Class clazz = getPOJOClass();
			Object o = clazz.newInstance();
			if (o instanceof ITransferObject) {
				ITransferObject to = (ITransferObject) o;
				initializePOJO(to);
				return to;
			}
			String msg = "Can not create new POJO." + clazz.getName() + " must be a implementation of ITransferObject";
			LOGGER.severe(msg);
			throw new ManagerBeanException(msg);
		} catch (InstantiationException e) {
			LOGGER.severe(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.severe(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IManagerBean#remove(com.code.aon.common.ITransferObject)
	 */
	public boolean remove(ITransferObject to) throws ManagerBeanException {
		try {
			ManagerBeanEvent evt = new ManagerBeanEvent( to );
			fireVetoableBeanRemoved(evt);
			boolean ret = getDao().remove(to);
			fireBeanRemoved(evt);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.IManagerBean#update(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject update(ITransferObject to) throws ManagerBeanException {
		try {
			ManagerBeanEvent evt = new ManagerBeanEvent( to );
			fireVetoableBeanUpdated(evt);
			ITransferObject ret = getDao().update(to);
			fireBeanUpdated(evt);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.IManagerBean#insert(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject insert(ITransferObject to)
			throws ManagerBeanException {
		try {
			ManagerBeanEvent evt = new ManagerBeanEvent( to );
			fireVetoableBeanInserted(evt);
			ITransferObject ret = getDao().insert(to);
			fireBeanInserted(evt);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.IManagerBean#insert(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject insertOrUpdate(ITransferObject to)
			throws ManagerBeanException {
		try {
			ManagerBeanEvent evt = new ManagerBeanEvent( to );
			fireVetoableBeanInserted(evt);
			ITransferObject ret = getDao().insertOrUpdate(to);
			fireBeanInserted(evt);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}
	
	/**
     * Fire an existing ManagerBeanEvent to any registered vetoListeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireVetoableBeanInserted( ManagerBeanEvent evt ) throws ManagerBeanVetoListenerException {
		if (vetoListeners != null) {
			vetoListeners.vetoableBeanInserted( evt );
		}
	}

	/**
     * Fire an existing ManagerBeanEvent to any registered vetoListeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireVetoableBeanUpdated( ManagerBeanEvent evt ) throws ManagerBeanVetoListenerException{
		if (vetoListeners != null) {
			vetoListeners.vetoableBeanUpdated( evt );
		}
	}

	/**
     * Fire an existing ManagerBeanEvent to any registered vetoListeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireVetoableBeanRemoved( ManagerBeanEvent evt ) throws ManagerBeanVetoListenerException{
		if (vetoListeners != null) {
			vetoListeners.vetoableBeanRemoved( evt );
		}
	}
	
	/**
     * Fire an existing ManagerBeanEvent to any registered listeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireBeanInserted( ManagerBeanEvent evt ) throws ManagerBeanException{
		if (listeners != null) {
			listeners.beanInserted( evt );
		}
	}
	
	/**
     * Fire an existing ManagerBeanEvent to any registered listeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireBeanUpdated( ManagerBeanEvent evt ) throws ManagerBeanException{
		if (listeners != null) {
			listeners.beanUpdated( evt );
		}
	}
	
	/**
     * Fire an existing ManagerBeanEvent to any registered listeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireBeanRemoved( ManagerBeanEvent evt ) throws ManagerBeanException{
		if (listeners != null) {
			listeners.beanRemoved( evt );
		}
	}
	
}