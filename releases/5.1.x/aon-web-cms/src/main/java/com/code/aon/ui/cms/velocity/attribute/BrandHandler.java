package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.cms.Brand;
import com.code.aon.cms.BrandDetail;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.cms.util.ControllerUtil;

public class BrandHandler {

	private String alias;
	
	private String label;

	private String url;

	public BrandHandler(Brand object){
		this(BrandHandler.recoverBranDetail(object));
	}

	public BrandHandler(BrandDetail detail){
		if (detail != null) {
			this.alias = detail.getBrand()==null?"":detail.getBrand().getAlias();
			this.label = detail==null?null:detail.getLabel();
			this.url = Templates.BRAND.getHtmlName();
			this.url = this.url.replaceAll("%NAME%", this.alias);
		}
	}

	private static BrandDetail recoverBranDetail(Brand object){
		if (object != null) {
			Session s = HibernateUtil.getSession();
			String stmt = "SELECT bd FROM BrandDetail bd " +
					"WHERE bd.brand.id = ? " +
					"AND bd.language.id = ?";
			Query query = s.createQuery(stmt);
			query.setInteger(0, object.getId());
			query.setInteger(1, ControllerUtil.getCurrentLanguage().getId());
			List<BrandDetail> list = query.list();
			if (!list.isEmpty())
				return list.get(0);
		}
		return null;
	}

	public String getAlias() {
		return alias;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

}
