package com.code.aon.ui.customer.util;

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.ui.util.AonUtil;

public class CustomerValidationManager {

	public boolean isBlocked(Customer customer) {
		try {
			if (customer.getStatus() == CustomerStatus.BLOCKED) {
				RegistryNote observation = getRegistryObservation(customer.getRegistry());
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				AonUtil.addErrorMessage("[" + CustomerStatus.BLOCKED.getName(locale) + "]" + ": "
						+ observation.getComments());
				return true;
			}
		} catch (ManagerBeanException e) {
			// Nothing
		}
		return false;
	}

	private RegistryNote getRegistryObservation(Registry registry) throws ManagerBeanException {
		IManagerBean rNoteBean = BeanManager.getManagerBean(RegistryNote.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rNoteBean
				.getFieldName(IRegistryAlias.REGISTRY_NOTE_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(rNoteBean.getFieldName(IRegistryAlias.REGISTRY_NOTE_NOTETYPE),
				NoteType.OBSERVATION);
		List<ITransferObject> l = rNoteBean.getList(criteria);
		if (!l.isEmpty()) {
			return (RegistryNote) l.iterator().next();
		} else {
			return null;
		}
	}

}
