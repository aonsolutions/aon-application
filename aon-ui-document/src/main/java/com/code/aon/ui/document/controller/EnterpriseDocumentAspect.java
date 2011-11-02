package com.code.aon.ui.document.controller;

import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Utils;

public class EnterpriseDocumentAspect {
	
	public static final String DOMAIN = "{http://aon.esferalia.com/models/catalogue/1.0}";
	
	public static final String ASPECT_NAME = DOMAIN + "enterpriseDocument";
	
	public static final String ENTERPRISE_ID = DOMAIN + "enterpriseId";
	
	private Integer enterpriseId;
	
	public EnterpriseDocumentAspect(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
	public CMLAddAspect getAspect( ParentReference parent ) {
		CMLAddAspect aspect = new CMLAddAspect();
		aspect.setAspect(ASPECT_NAME);
		NamedValue[] properties = new NamedValue[1];
		properties[0] = Utils.createNamedValue(ENTERPRISE_ID, enterpriseId.toString());
		aspect.setProperty(properties);
		aspect.setWhere(new Predicate(new Reference[] { parent }, null, null));
		aspect.setWhere_id("1");		
		return aspect;
	}
	
}
