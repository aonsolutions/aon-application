package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.ui.cms.velocity.ArticleGenerator;
import com.code.aon.ui.cms.velocity.BannerGenerator;
import com.code.aon.ui.cms.velocity.DirectAccessGenerator;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;
import com.code.aon.ui.cms.velocity.GenericGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class SidebarOptionHandler {

	private String template;
	
	private Object content;

	public SidebarOptionHandler(SidebarOptionDetail sidebarOptionDetail) {
		template = sidebarOptionDetail.getSidebar_option().getType().getTemplateName();
		if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.ARTICLE)) {
			content = ArticleGenerator.getArticleHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.BANNER_GROUP)) {
			content = BannerGenerator.getBannerCategoryHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.BANNER)) {
			content = BannerGenerator.getBannerHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.GENERIC)) {
			content = GenericGenerator.getGenericHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.LINK)) {
			content = LinkGenerator.getLinkCategoryHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.MENU)) {
			content = MenuGenerator.getMenuHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.DIRECT_ACCESS)) {
			content = DirectAccessGenerator.getDirectAccessGroupHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.ARTICLE_EVENTS_CATEGORY)) {
			content = ArticleGenerator.getArticleCategoryHandler(sidebarOptionDetail.getSidebar_option().getIdent(),ArticleType.EVENTS);
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.ARTICLE_NEWS_CATEGORY)) {
			content = ArticleGenerator.getArticleCategoryHandler(sidebarOptionDetail.getSidebar_option().getIdent(),ArticleType.NEWS);
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.ARTICLE_OTHER_CATEGORY)) {
			content = ArticleGenerator.getArticleCategoryHandler(sidebarOptionDetail.getSidebar_option().getIdent(),ArticleType.OTHER);
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.ARTICLE_SERVICES_CATEGORY)) {
			content = ArticleGenerator.getArticleCategoryHandler(sidebarOptionDetail.getSidebar_option().getIdent(),ArticleType.SERVICES);
		}
		else if (sidebarOptionDetail.getSidebar_option().getType().equals(SidebarType.DOWNLOAD_CATEGORY)) {
			content = DownloadsGenerator.getDownloadsHandler(sidebarOptionDetail.getSidebar_option().getIdent());
		}
	}

	public String getTemplate() {
		return template;
	}

	public Object getContent() {
		return content;
	}
	
}
