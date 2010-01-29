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
		if (customer.getStatus() == CustomerStatus.BLOCKED) {
			String obs = null;
			try {
				RegistryNote observation = getRegistryObservation(customer.getRegistry());
				obs = observation!=null?observation.getComments():null;
			} catch (ManagerBeanException e) {
				// Si falla, no saldrá el mensaje en pantalla. Se desprecia el error a posta.
			}
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			AonUtil.addErrorMessage("[" + CustomerStatus.BLOCKED.getName(locale) + "]"
					+ (obs == null ? "" : ": " + obs));
			return true;
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
		}
		return null;
	}

}
