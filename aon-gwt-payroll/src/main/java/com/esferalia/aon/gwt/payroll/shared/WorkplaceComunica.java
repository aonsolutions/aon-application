package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class WorkplaceComunica implements Serializable {
	
	private Map<Integer, WorkplaceComunicaInfo> worplaces;
	private Map<Integer, WorkplaceComunicaInfo> deleteWorkplaces;
	
	public WorkplaceComunica() {
		super();
		this.worplaces = new HashMap<Integer, WorkplaceComunicaInfo>();
		this.deleteWorkplaces = new HashMap<Integer, WorkplaceComunicaInfo>();
	}

	public Map<Integer, WorkplaceComunicaInfo> getWorkplaces() {
		return worplaces;
	}

	public void setWorkplaces(Map<Integer, WorkplaceComunicaInfo> worplaces) {
		this.worplaces = worplaces;
	}
	
	public Map<Integer, WorkplaceComunicaInfo> getDeletedWorkplaces() {
		return deleteWorkplaces;
	}

	public void setDeletedWorkplaces(Map<Integer, WorkplaceComunicaInfo> deleteWorkplaces) {
		this.deleteWorkplaces = deleteWorkplaces;
	}

	public void insertWokplace(Integer workplaceId, String description, Integer addressId) {
		this.worplaces.put(workplaceId, new WorkplaceComunicaInfo(workplaceId, description, addressId));
	}

	public void deleteWorkplace(Integer workplaceId) {
		if(workplaceId > 0) {
			WorkplaceComunicaInfo workplaceComunicaInfo = this.worplaces.get(workplaceId);
			this.deleteWorkplaces.put(workplaceId, workplaceComunicaInfo);
		}
		this.worplaces.remove(workplaceId);
	}
	
}
