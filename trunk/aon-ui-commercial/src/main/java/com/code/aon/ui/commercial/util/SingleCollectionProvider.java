package com.code.aon.ui.commercial.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

public class SingleCollectionProvider implements ICollectionProvider {
	
	private static final Logger LOGGER = Logger.getLogger(SingleCollectionProvider.class.getName());
	
	private ITransferObject to;
	
	public SingleCollectionProvider(ITransferObject to) {
		this.to = to;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")	
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( to.getClass() );
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		Serializable id = bean.getId(to);
		l.add( bean.get(id) );
		return l;
	}	
	
}
