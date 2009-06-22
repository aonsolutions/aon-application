package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.ui.cms.velocity.ActivityGenerator;
import com.code.aon.ui.cms.velocity.AlbumGenerator;
import com.code.aon.ui.cms.velocity.ArticleCalendarGenerator;
import com.code.aon.ui.cms.velocity.ArticleGenerator;
import com.code.aon.ui.cms.velocity.BannerGenerator;
import com.code.aon.ui.cms.velocity.BulletinSuscribeGenerator;
import com.code.aon.ui.cms.velocity.DirectAccessGenerator;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;
import com.code.aon.ui.cms.velocity.GenericGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;

public class ModularPageOptionHandler {

	private String template;
	
	private Object content;

	private String type;
	
	public ModularPageOptionHandler(ModularPageOptionDetail mpod) {
		ModularPageOptionType optionType = mpod.getModular_page_option().getType();
		template = optionType.getTemplateName();
		type = optionType.getName();
		Integer id = mpod.getModular_page_option().getIdent();
		switch ( optionType ) {
			case ACTIVITY:
				content = ActivityGenerator.getActivityHandler(id);
				break;
			case ALBUM_CATEGORY:
				content = AlbumGenerator.getAlbumCategoryHandler(id);
				break;
			case ARTICLE:
				content = ArticleGenerator.getArticleHandler( id );
				break;
			case ARTICLE_NEWS:
				content = ArticleGenerator.getArticleCategoryHandler( id, ArticleType.NEWS);
				break;
			case ARTICLE_EVENTS:
				content = ArticleGenerator.getArticleCategoryHandler( id, ArticleType.EVENTS);
				break;
			case ARTICLE_SERVICES:
				content = ArticleGenerator.getArticleCategoryHandler( id, ArticleType.SERVICES);
				break;
			case ARTICLE_OTHER:
				content = ArticleGenerator.getArticleCategoryHandler( id, ArticleType.OTHER);
				break;
			case BANNER_GROUP:
				content = BannerGenerator.getBannerCategoryHandler(id);
				break;
			case BANNER:
				content = BannerGenerator.getBannerHandler(id);
				break;
			case BULLETIN_SUSCRIBE:
				content = BulletinSuscribeGenerator.getBulletinSuscribeHandler();
				break;
			case GENERIC:
				content = GenericGenerator.getGenericHandler(id);
				break;
			case DIRECT_ACCESS_GROUP:
				content = DirectAccessGenerator.getDirectAccessGroupHandler(id);
				break;
			case DOWNLOADS:
				content = DownloadsGenerator.getDownloadsHandler(id);
				break;
			case DIRECT_ACCESS:
				content = DirectAccessGenerator.getDirectAccessHandler(id);
				break;
			case LINK_CATEGORY:
				content = LinkGenerator.getLinkCategoryHandler(id);
				break;
			case NEXT_ARTICLES:
				content = ArticleCalendarGenerator.getNextArticlesHandler();
				break; 
		}
	}

	public String getTemplate() {
		return template;
	}

	public Object getContent() {
		return content;
	}

	public String getType() {
		return type;
	}

}
