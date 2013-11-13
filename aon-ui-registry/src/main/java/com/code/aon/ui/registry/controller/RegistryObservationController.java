package com.code.aon.ui.registry.controller;

import java.util.Date;

import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryObservationController {

	private RegistryNote observation;
	
	public RegistryNote getObservation() {
		return observation;
	}

	public void setObservation(RegistryNote observation) {
		this.observation = observation;
	}

	public void onRecover(Registry registry) throws ManagerBeanException {
		RegistryNote observation = getRegistryObservation(registry);
		if (observation == null) {
			observation = new RegistryNote();
			observation.setRegistry(registry);
			observation.setNoteDate(new Date());
			observation.setDescription(NoteType.OBSERVATION.getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()));
			observation.setNotetype(NoteType.OBSERVATION);
			setObservation(observation);
		} else {
			setObservation(observation);
		}
	}
	
	public RegistryNote getRegistryObservation(Registry registry) throws ManagerBeanException{
		IManagerBean rNoteBean = BeanManager.getManagerBean(RegistryNote.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rNoteBean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(rNoteBean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE), NoteType.OBSERVATION);
		for (ITransferObject ito : rNoteBean.getList(criteria)) {
			return (RegistryNote)ito;
		}
		return null;
	}

	public void onSave() throws ManagerBeanException {
		IManagerBean rNoteBean = BeanManager.getManagerBean(RegistryNote.class);
		if (observation.getId() == null) {
			rNoteBean.insert(observation);
		} else {
			rNoteBean.update(observation);
		}
	}

	public void onRemove() throws ManagerBeanException {
		IManagerBean rNoteBean = BeanManager.getManagerBean(RegistryNote.class);
		if (observation.getId() != null) {
			rNoteBean.remove(observation);
		}
	}

}
