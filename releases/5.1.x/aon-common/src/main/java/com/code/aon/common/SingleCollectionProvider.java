package com.code.aon.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleCollectionProvider implements ICollectionProvider {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SingleCollectionProvider.class);
	
	private ITransferObject to;
	
	public SingleCollectionProvider(ITransferObject to) {
		this.to = to;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
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
