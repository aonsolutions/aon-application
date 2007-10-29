package com.code.aon.ui.cms.velocity;

import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.util.VelocityUtil;

public class HomepageGenerator extends Generator {
	
	public static void generate(VelocityUtil vu) {
		vu.addMessage(" Generando Pagina principal 'index.html'.", VelocityUtil.INFO);
		generate(vu, Templates.HOME, "");
	}
	
}
