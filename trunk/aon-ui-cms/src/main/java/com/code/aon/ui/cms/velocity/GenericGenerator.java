package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.GenericPageHandler;

public class GenericGenerator extends Generator {

	public static void generate(VelocityUtil vu) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(GenericPageDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			for (int i=0; i < l.size(); i++) {
				GenericPageDetail gpd = (GenericPageDetail)l.get(i);
				if (gpd.getGeneric_page().isActive()) {
					GenericPageHandler gph = new GenericPageHandler(gpd);
					vu.put("generic", gph);
					if (gph.getMenu() != null) vu.put("menu", gph.getMenu());
					else vu.remove("menu");
					if (gph.getDescription() != null && !gph.getDescription().equals("")) vu.put("description", gph.getDescription());
					else vu.remove("description");
					if (gph.getKeywords() != null && !gph.getKeywords().equals("")) vu.put("keywords", gph.getKeywords());
					else vu.remove("keywords");
					vu.addMessage(" Generando Página Genérica '" + gpd.getGeneric_page().getAlias() + "'.", VelocityUtil.INFO);
					generate(vu, Templates.GENERIC, gpd.getGeneric_page().getAlias());
					vu.remove("generic");
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

}
