package com.code.aon.ui.registry.util;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.NoteType;

public abstract class RegistryValidationManager {

	public abstract boolean isBlocked(ITransferObject to);

	protected RegistryNote getRegistryObservation(Registry registry) throws ManagerBeanException {
		IManagerBean rNoteBean = BeanManager.getManagerBean(RegistryNote.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rNoteBean.getFieldName(IRegistryAlias.REGISTRY_NOTE_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(rNoteBean.getFieldName(IRegistryAlias.REGISTRY_NOTE_NOTETYPE), NoteType.OBSERVATION);
		List<ITransferObject> rNoteList = rNoteBean.getList(criteria);
		if (!rNoteList.isEmpty()) {
			return (RegistryNote)rNoteList.iterator().next();
		}
		return null;
	}

}
