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
	protected void exportPOJO(Map additionalContext, POJOClass element) {
		
		Iterator<?> iterator = element.getAllPropertiesIterator();
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
		
		super.exportPOJO(additionalContext, element);
	}
	
}
