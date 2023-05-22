package com.code.aon.common;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Stack;

import jakarta.persistence.Transient;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.annotations.Cascade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.common.dao.hibernate.ReplicationMode;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

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

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BasicManagerBean.class);
	
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
	
	@Override
	public void initializePOJO(ITransferObject to) throws ManagerBeanException {
		_initializePOJO(to, new Stack<Class<? extends ITransferObject>>());
	}
	
	private void _initializePOJO(ITransferObject to,Stack<Class<? extends ITransferObject>> pojoDependences) throws ManagerBeanException {
		try {
			Class<? extends ITransferObject> clazz = to.getClass();
			LOGGER.debug("Initializing " + clazz.getName());
			pojoDependences.push(clazz);
			PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
			LOGGER.debug("Found " + pds.length + " properties");
			for (PropertyDescriptor pd : pds) {
				Class<?> fieldClass = pd.getPropertyType();
				String name = pd.getName();
				if (needInitialize(to, pd)) {
					if (ITransferObject.class.isAssignableFrom(fieldClass)) {
						LOGGER.debug("Initializing TO " + name + " property");
						ITransferObject childTO = (ITransferObject) fieldClass.newInstance();
						if (!pojoDependences.contains(fieldClass)) {
							_initializePOJO(childTO,pojoDependences);
							pojoDependences.pop();
						}
						PropertyUtils.setProperty(to, name, childTO);
						LOGGER.debug("Assigned TO " + fieldClass + " to " + clazz.getName());
					} else if (!fieldClass.getName().startsWith("java")) {
						LOGGER.debug("Initializing " + name + " property");
						Object o = fieldClass.newInstance();
						PropertyUtils.setProperty(to, name, o);
						LOGGER.debug("Assigned " + fieldClass + " to " + clazz.getName());
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
	
	@Override
	public ITransferObject createNewTo() throws ManagerBeanException {
		try {
			ITransferObject to = getDao().newTo();
			initializePOJO(to);
			return to;
		} catch (DAOException e) {
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}
	
	/**
	 * Return when it is necessary to restore and when not.
	 * 
	 * @param bean
	 * @param pd
	 * @return boolean
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private boolean needRestore(Object bean, PropertyDescriptor pd) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return (pd.getWriteMethod() != null)
				&& (!pd.getReadMethod().isAnnotationPresent(Transient.class))
				&& (!pd.getReadMethod().isAnnotationPresent(Cascade.class));
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void restoreNullSubPOJOs(ITransferObject to) throws ManagerBeanException {
		try {
			Class<? extends ITransferObject> clazz = to.getClass();
			LOGGER.debug("Restoring null values on " + clazz.getName());
			PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
			LOGGER.debug("Found " + pds.length + " properties");
			for (PropertyDescriptor pd : pds) {
				Class fieldClass = pd.getPropertyType();
				String name = pd.getName();
				if (ITransferObject.class.isAssignableFrom(fieldClass)) {
					if ( needRestore(to, pd) ) {
						LOGGER.debug("Initializing TO " + name + " property");
						ITransferObject childTO = (ITransferObject) PropertyUtils.getProperty(to, name);
						if (childTO != null ) {
							IManagerBean bean = BeanManager.getManagerBean(fieldClass);
							Serializable id = bean.getId(childTO);
							if (id == null) {
								if(!pd.getReadMethod().isAnnotationPresent(AonPOJOInitializationInvalidateRestoreNull.class)){
									PropertyUtils.setProperty(to, name, null);
									LOGGER.debug("Assigned NULL to " + fieldClass);
								}
							}
						}
					}
				}
			}
		} catch (SecurityException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new ManagerBeanException(e.getMessage());
		}
	}
	
	@Override
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

	@Override
	public boolean remove(Serializable pk) throws ManagerBeanException {
		try {
			ManagerBeanEvent evt = new ManagerBeanEvent( pk );
			fireVetoableBeanRemoved(evt);
			boolean ret = getDao().remove(pk);
			fireBeanRemoved(evt);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}
	
	@Override
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

	@Override
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

	@Override
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

	@Override
	public ITransferObject replicate(ITransferObject to, ReplicationMode mode)
			throws ManagerBeanException {
		try {
			ITransferObject ret = getDao().replicate(to, mode);
			return ret;
		} catch (DAOException e) {
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
		if (getVetoListeners() != null) {
			getVetoListeners().vetoableBeanInserted( evt );
		}
	}

	/**
     * Fire an existing ManagerBeanEvent to any registered vetoListeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireVetoableBeanUpdated( ManagerBeanEvent evt ) throws ManagerBeanVetoListenerException{
		if (getVetoListeners() != null) {
			getVetoListeners().vetoableBeanUpdated( evt );
		}
	}

	/**
     * Fire an existing ManagerBeanEvent to any registered vetoListeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireVetoableBeanRemoved( ManagerBeanEvent evt ) throws ManagerBeanVetoListenerException{
		if (getVetoListeners() != null) {
			getVetoListeners().vetoableBeanRemoved( evt );
		}
	}
	
	/**
     * Fire an existing ManagerBeanEvent to any registered listeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireBeanInserted( ManagerBeanEvent evt ) throws ManagerBeanException{
		if (getListeners() != null) {
			getListeners().beanInserted( evt );
		}
	}
	
	/**
     * Fire an existing ManagerBeanEvent to any registered listeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireBeanUpdated( ManagerBeanEvent evt ) throws ManagerBeanException{
		if (getListeners() != null) {
			getListeners().beanUpdated( evt );
		}
	}
	
	/**
     * Fire an existing ManagerBeanEvent to any registered listeners.
	 * 
	 * @param evt the ManagerBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireBeanRemoved( ManagerBeanEvent evt ) throws ManagerBeanException{
		if (getListeners() != null) {
			getListeners().beanRemoved( evt );
		}
	}
	
}