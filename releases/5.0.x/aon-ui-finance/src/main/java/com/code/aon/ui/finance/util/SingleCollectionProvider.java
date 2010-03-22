package com.code.aon.ui.finance.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

public class SingleCollectionProvider implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SingleCollectionProvider.class.getName());
	
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
