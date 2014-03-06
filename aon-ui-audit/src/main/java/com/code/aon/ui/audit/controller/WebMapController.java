package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.OptionGroup;
import com.code.aon.ui.util.AonUtil;

public class WebMapController implements IAuditConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
	}

	private ActionDeniedController getDeniedController() {
		return (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
	}
	
	public String getTemplate() throws IOException {
		Map<ApplicationCategory,List<ApplicationOption>> map = new TreeMap<ApplicationCategory, List<ApplicationOption>>();
		for( ApplicationCategory category : getDeniedController().getCategories() ) {
			if ( category.isRendered() ) {
				List<ApplicationOption> list = new ArrayList<ApplicationOption>();
				for( OptionGroup group : category.getGroups() ) {
					if ( group.isRendered() ) {
						for( ApplicationOption option : group.getOptions() ) {
							if ( option.isRendered() ) {
								list.add(option);	
							}
						}						
					}
				}
				if (! list.isEmpty() ) {
					map.put(category, list);
				}
			}
		}
		return getOptionController().getTemplate(WEB_MAP_TEMPLATE,
				CATEGORIES_VM, getDeniedController().getCategories(),
				CATEGORY_MAP_VM, map );
	}	

}
