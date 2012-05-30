package com.code.aon.ui.admin;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;


public class SelectTransferObject<V,T extends ITransferObject> {

	private boolean checked;
	
	private V value;
	
	private T to;

	public SelectTransferObject(V value) {
		this.value = value;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public V getValue() {
		return value;
	}

	public T getTo() {
		return to;
	}

	public void setTo(T to) {
		this.to = to;
		setChecked(this.to != null);
	}
	
	public void unregister() throws ManagerBeanException {
		if ( getTo() != null ) {
			IManagerBean bean = BeanManager.getManagerBean(getTo().getClass());
			bean.remove(getTo());			
		}
	}	
	
}
