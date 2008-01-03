package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.ui.cms.velocity.GenericGenerator;

public class ModularPageOptionHandler {

	private String template;
	
	private Object content;

	public ModularPageOptionHandler(ModularPageOptionDetail mpod) {
		template = mpod.getModular_page_option().getType().getTemplateName();
		if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.ARTICLE)) {
			content = null;
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.BANNER)) {
			content = null;
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.GENERIC)) {
			content = GenericGenerator.getGenericHandler(mpod.getModular_page_option().getIdent());
		}
	}

	public String getTemplate() {
		return template;
	}

	public Object getContent() {
		return content;
	}
	
}
