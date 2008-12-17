package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.form.SelectWindowController;

public class TargetLookupController extends SelectWindowController {
	
	private static final Logger LOGGER = Logger.getLogger(TargetLookupController.class.getName());

	private static final String PHONE = "Registry_phone";

	private static final String CELLULAR = "Registry_cellular";

	@Override
	@SuppressWarnings("unchecked")
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) { 
		try {
			// BUSCAR LOS TELEFONOS
			IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID),map.get("Target_registry_id"));
			Iterator iter = rMediaBean.getList(criteria).iterator();

			if (iter.hasNext()) {
				while (iter.hasNext()){
					RegistryMedia rmedia = (RegistryMedia)iter.next();
					if (MediaType.FIXED_PHONE == rmedia.getMediaType()){
						map.put(PHONE, rmedia.getValue());
					}else if (MediaType.CELLULAR == rmedia.getMediaType()){
						map.put(CELLULAR, rmedia.getValue());
					}
				}
			} else {
				map.put(PHONE, "");
				map.put(CELLULAR, "");
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing lookup map", e);
		}
	}
}