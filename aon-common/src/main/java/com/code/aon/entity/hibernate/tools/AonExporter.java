package com.code.aon.entity.hibernate.tools;

import java.util.Map;

import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

public class AonExporter extends GenericExporter{
	
	@Override
	protected void exportPOJO(Map additionalContext, POJOClass element) {
		super.exportPOJO(additionalContext, element);
	}
	
}
