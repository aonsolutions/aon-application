package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.util.AonUtil;

public class WebMapController implements IAuditConstants {
	
	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
	}
		
	public String getTemplate() throws IOException {
		Map<String,List<ApplicationOption>> map = new TreeMap<String, List<ApplicationOption>>();
		for( ApplicationOption option : getOptionController().getOptions() ) {
			String category = option.getCategory().getName();
			List<ApplicationOption> list = map.get(category);
			if ( list == null ) {
				list = new ArrayList<ApplicationOption>();
				map.put(category, list);
			}
			list.add(option);
		}
		return getOptionController().getTemplate(WEB_MAP_TEMPLATE,
				CATEGORIES_VM, getOptionController().getCategories(),
				CATEGORY_MAP_VM, map );
	}	

}
