package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.ui.cms.velocity.AlbumGenerator;
import com.code.aon.ui.cms.velocity.ArticleCalendarGenerator;
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
		Integer ident = sidebarOptionDetail.getSidebar_option().getIdent();
		switch ( sidebarOptionDetail.getSidebar_option().getType() ) {
			case ALBUM_CATEGORY:
				content = AlbumGenerator.getAlbumCategoryHandler(ident);
				break;
			case ARTICLE:
				content = ArticleGenerator.getArticleHandler(ident);
				break;
			case ARTICLE_EVENTS_CATEGORY:
				content = ArticleGenerator.getArticleCategoryHandler(ident,ArticleType.EVENTS);
				break;
			case ARTICLE_NEWS_CATEGORY:
				content = ArticleGenerator.getArticleCategoryHandler(ident,ArticleType.NEWS);
				break;
			case ARTICLE_OTHER_CATEGORY:
				content = ArticleGenerator.getArticleCategoryHandler(ident,ArticleType.OTHER);
				break;
			case ARTICLE_SERVICES_CATEGORY:
				content = ArticleGenerator.getArticleCategoryHandler(ident,ArticleType.SERVICES);
				break;
			case BANNER:
				content = BannerGenerator.getBannerHandler(ident);
				break;
			case BANNER_GROUP:
				content = BannerGenerator.getBannerCategoryHandler(ident);
				break;
			case DIARY_CALENDAR:
				content = ArticleCalendarGenerator.getDiaryCalendarHandler();
				break; 
			case DIRECT_ACCESS:
				content = DirectAccessGenerator.getDirectAccessGroupHandler(ident);
				break;
			case DOWNLOAD_CATEGORY:
				content = DownloadsGenerator.getDownloadsHandler(ident);
				break;
			case GENERIC:
				content = GenericGenerator.getGenericHandler(ident);
				break;
			case LINK:
				content = LinkGenerator.getLinkCategoryHandler(ident);
				break;
			case MENU:
				content = MenuGenerator.getMenuHandler(ident);
		}
	}

	public String getTemplate() {
		return template;
	}

	public Object getContent() {
		return content;
	}
	
}
