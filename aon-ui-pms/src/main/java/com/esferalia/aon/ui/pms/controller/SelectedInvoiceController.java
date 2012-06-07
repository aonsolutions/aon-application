package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;

public class SelectedInvoiceController implements IPmsConstants, ICollectionProvider {
	
	private ITransferObject to;
	
	public ITransferObject getTo() {
		return to;
	}

	public void setTo(ITransferObject to) {
		this.to = to;
	}

	/**
	 * Get a collection that contains current <code>ITransferObject</code>
	 * associated to controller. To use in reports.
	 * 
	 * @return Collection
	 */
	public Collection<ITransferObject> getCollection() {
		if (this.getTo() != null) {
			List<ITransferObject> l = new LinkedList<ITransferObject>();
			l.add(getTo());
			return l;
		}
		return null;
	}

	/**
	 * Get a collection that contains current <code>ITransferObject</code>
	 * associated to controller. To use in reports.
	 * 
	 * @return Collection
	 * @throws ManagerBeanException
	 */
	public Collection<ITransferObject> getCollection(boolean forceRefresh) throws ManagerBeanException {
		if (!forceRefresh) {
			return this.getCollection();
		}
		if (this.getTo() != null) {
			List<ITransferObject> l = new LinkedList<ITransferObject>();
			IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
			Integer savedToId = ((Invoice)this.getTo()).getId();
			ITransferObject refreshed = bean.get(savedToId);
			l.add(refreshed);
			return l;
		}
		return null;
	}

		
}