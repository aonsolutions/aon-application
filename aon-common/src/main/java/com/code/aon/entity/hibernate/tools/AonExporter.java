package com.code.aon.entity.hibernate.tools;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

public class AonExporter extends GenericExporter{
	@Override
	protected void exportPersistentClass(Map additionalContext, POJOClass pojo) {
		Iterator<?> iterator = pojo.getAllPropertiesIterator();
		boolean isConfidentialable = false;
		boolean isDomainContainer = false;
		while (iterator.hasNext()) {
			Property property = (Property)iterator.next();
			if (property.getName().equals("securityLevel")) {
				isConfidentialable = true;
			}
			if (property.getName().equals("domain")) {
				isDomainContainer = true;
			}
		}
		additionalContext.put("isConfidentialable",isConfidentialable);
		additionalContext.put("isDomainContainer",isDomainContainer);
		super.exportPersistentClass(additionalContext, pojo);
	}
	
	@Override
	protected void exportPOJO(Map additionalContext, POJOClass pojo) {
		Iterator<?> iterator = pojo.getAllPropertiesIterator();
		while (iterator.hasNext()) {
			Property property = (Property)iterator.next();
			if (property.getValue() instanceof ToOne) {
				ToOne toOne = (ToOne)property.getValue();
				toOne.setLazy(false);
			}
			else if (property.getValue() instanceof Collection) {
				Collection collection = (Collection)property.getValue();
				collection.setLazy(true);
			}
			else {
				property.setLazy(true);
			}
		}
		super.exportPOJO(additionalContext, pojo);
	}
	
}
