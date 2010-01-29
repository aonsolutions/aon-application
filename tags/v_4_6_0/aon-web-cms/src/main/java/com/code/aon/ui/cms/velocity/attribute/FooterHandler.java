package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.FooterBannerCategory;
import com.code.aon.cms.FooterDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.velocity.BannerGenerator;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class FooterHandler {
	
	private static final Logger LOGGER = Logger.getLogger(FooterHandler.class.getName());

	private List<MenuOptionHandler> menu;

	private List<BannerCategoryHandler> bannerCategory;

	private String content;
	
	public FooterHandler(FooterDetail footer) {
		content = footer.getContent();
		menu = MenuGenerator.getMenuOptionList(footer.getFooter().getMenu());
		bannerCategory = getBanners(footer.getFooter().getId());
	}

	public List<MenuOptionHandler> getMenu() {
		return menu;
	}

	public List<BannerCategoryHandler> getBannerCategory() {
		return bannerCategory;
	}

	public String getContent() {
		return content;
	}

	private ArrayList<BannerCategoryHandler> getBanners(Integer id){
		ArrayList<BannerCategoryHandler> list = new ArrayList<BannerCategoryHandler>();
		try{
			IManagerBean bean = BeanManager.getManagerBean(FooterBannerCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FOOTER_BANNER_CATEGORY_FOOTER_ID), id);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				FooterBannerCategory fbc = (FooterBannerCategory) iterator.next();
				list.add(BannerGenerator.getBannerCategoryHandler(fbc.getBannerCategory()));
			}
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		return list;
	}
	
}
