package com.code.aon.ui.cms.controller.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.ql.Criteria;

public class OrderedControllerListenerSupport {

	private List<IOrderedControllerListener> listeners;

	public void addOrderedControllerListener(IOrderedControllerListener l) {
		if (listeners == null) {
			listeners = new LinkedList<IOrderedControllerListener>();
		}
		if (!listeners.contains(l))
			listeners.add(l);
	}

	public void removeOrderedControllerListener(IOrderedControllerListener l) {
		listeners.remove(l);
	}

	public void fireBeforeUseCriteria(Criteria criteria){
		if (listeners != null && !listeners.isEmpty()) {
			Iterator<IOrderedControllerListener> iter = listeners.iterator();
			while (iter.hasNext()) {
				IOrderedControllerListener l = iter.next();
				l.fireBeforeUseCriteria(criteria);
			}
		}
	}
}
