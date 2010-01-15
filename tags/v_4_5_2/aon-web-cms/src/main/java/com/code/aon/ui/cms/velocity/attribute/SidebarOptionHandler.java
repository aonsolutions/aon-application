package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.SidebarOption;
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

	public SidebarOptionHandler(SidebarOption so) {
		template = so.getType().getTemplateName();
		Integer ident = so.getIdent();
		String message = "LA OPCION " + so.getAlias() + " DEL LATERAL " + so.getSidebar().getAlias();
		switch ( so.getType() ) {
			case ALBUM_CATEGORY:
				content = AlbumGenerator.getAlbumCategoryHandler(ident, message);
				break;
			case ARTICLE:
				content = ArticleGenerator.getArticleHandler(ident, message);
				break;
			case ARTICLE_EVENTS_CATEGORY:
				content = ArticleGenerator.getArticleCategoryHandler(ident,ArticleType.EVENTS, message);
				break;
			case ARTICLE_NEWS_CATEGORY:
				content = ArticleGenerator.getArticleCategoryHandler(ident,ArticleType.NEWS, message);
				break;
			case ARTICLE_OTHER_CATEGORY:
				content = ArticleGenerator.getArticleCategoryHandler(ident,ArticleType.OTHER, message);
				break;
			case ARTICLE_SERVICES_CATEGORY:
				content = ArticleGenerator.getArticleCategoryHandler(ident,ArticleType.SERVICES, message);
				break;
			case BANNER:
				content = BannerGenerator.getBannerHandler(ident, message);
				break;
			case BANNER_GROUP:
				content = BannerGenerator.getBannerCategoryHandler(ident, message);
				break;
			case DIARY_CALENDAR:
				content = ArticleCalendarGenerator.getDiaryCalendarHandler();
				break; 
			case DIRECT_ACCESS:
				content = DirectAccessGenerator.getDirectAccessGroupHandler(ident, message);
				break;
			case DOWNLOAD_CATEGORY:
				content = DownloadsGenerator.getDownloadsHandler(ident, message);
				break;
			case GENERIC:
				content = GenericGenerator.getGenericHandler(ident, message);
				break;
			case LINK:
				content = LinkGenerator.getLinkCategoryHandler(ident, message);
				break;
			case MENU:
				content = MenuGenerator.getMenuHandler(ident, message);
				break;
		}
	}

	public String getTemplate() {
		return template;
	}

	public Object getContent() {
		return content;
	}
	
}
