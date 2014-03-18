package com.code.aon.ui.admin;

import java.io.Serializable;
import java.util.Comparator;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;


public class SelectTransferObject<V,T extends ITransferObject> implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean checked;
	
	private V value;
	
	private T to;
	
	private String label;

	public SelectTransferObject(V value) {
		this.value = value;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
	
	@SuppressWarnings("rawtypes")
	public static Comparator<SelectTransferObject> getComparator() {
		return new Comparator<SelectTransferObject>() {
			@Override
			public int compare(SelectTransferObject o1, SelectTransferObject o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}	    		
		};		
	}
	
}
