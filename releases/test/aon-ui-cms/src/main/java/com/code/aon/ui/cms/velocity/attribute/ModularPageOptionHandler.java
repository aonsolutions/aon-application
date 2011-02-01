package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ModularPageOption;
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
		ModularPageOption mpo = mpod.getModular_page_option();
		ModularPageOptionType optionType = mpo.getType();
		template = optionType.getTemplateName();
		type = optionType.getName();
		Integer id = mpo.getIdent();
		String message = "LA OPCION " + mpo.getAlias() + " DE LA PAGINA MODULAR " + mpo.getModular_page().getAlias();
		switch ( optionType ) {
			case ACTIVITY:
				content = ActivityGenerator.getActivityHandler(id, message);
				break;
			case ALBUM_CATEGORY:
				content = AlbumGenerator.getAlbumCategoryHandler(id, message);
				break;
			case ARTICLE:
				content = ArticleGenerator.getArticleHandler(id, message);
				break;
			case ARTICLE_NEWS:
				content = ArticleGenerator.getArticleCategoryHandler( id, ArticleType.NEWS, message);
				break;
			case ARTICLE_EVENTS:
				content = ArticleGenerator.getArticleCategoryHandler( id, ArticleType.EVENTS, message);
				break;
			case ARTICLE_SERVICES:
				content = ArticleGenerator.getArticleCategoryHandler( id, ArticleType.SERVICES, message);
				break;
			case ARTICLE_OTHER:
				content = ArticleGenerator.getArticleCategoryHandler( id, ArticleType.OTHER, message);
				break;
			case BANNER_GROUP:
				content = BannerGenerator.getBannerCategoryHandler(id, message);
				break;
			case BANNER:
				content = BannerGenerator.getBannerHandler(id, message);
				break;
			case BULLETIN_SUSCRIBE:
				content = BulletinSuscribeGenerator.getBulletinSuscribeHandler();
				break;
			case GENERIC:
				content = GenericGenerator.getGenericHandler(id, message);
				break;
			case DIRECT_ACCESS_GROUP:
				content = DirectAccessGenerator.getDirectAccessGroupHandler(id, message);
				break;
			case DOWNLOADS:
				content = DownloadsGenerator.getDownloadsHandler(id, message);
				break;
			case DIRECT_ACCESS:
				content = DirectAccessGenerator.getDirectAccessHandler(id, message);
				break;
			case LINK_CATEGORY:
				content = LinkGenerator.getLinkCategoryHandler(id, message);
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
