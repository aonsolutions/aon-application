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
		template = mpod.getModular_page_option().getType().getTemplateName();
		type = mpod.getModular_page_option().getType().getName();
		if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.ARTICLE)) {
			content = ArticleGenerator.getArticleHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.ARTICLE_NEWS)) {
			content = ArticleGenerator.getArticleCategoryHandler(mpod.getModular_page_option().getIdent(),ArticleType.NEWS);
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.ARTICLE_EVENTS)) {
			content = ArticleGenerator.getArticleCategoryHandler(mpod.getModular_page_option().getIdent(),ArticleType.EVENTS);
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.ARTICLE_SERVICES)) {
			content = ArticleGenerator.getArticleCategoryHandler(mpod.getModular_page_option().getIdent(),ArticleType.SERVICES);
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.ARTICLE_OTHER)) {
			content = ArticleGenerator.getArticleCategoryHandler(mpod.getModular_page_option().getIdent(),ArticleType.OTHER);
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.BANNER_GROUP)) {
			content = BannerGenerator.getBannerCategoryHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.BANNER)) {
			content = BannerGenerator.getBannerHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.GENERIC)) {
			content = GenericGenerator.getGenericHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.DIRECT_ACCESS_GROUP)) {
			content = DirectAccessGenerator.getDirectAccessGroupHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.DOWNLOADS)) {
			content = DownloadsGenerator.getDownloadsHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.DIRECT_ACCESS)) {
			content = DirectAccessGenerator.getDirectAccessHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.LINK_CATEGORY)) {
			content = LinkGenerator.getLinkCategoryHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.ACTIVITY)) {
			content = ActivityGenerator.getActivityHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.ALBUM_CATEGORY)) {
			content = AlbumGenerator.getAlbumCategoryHandler(mpod.getModular_page_option().getIdent());
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.BULLETIN_SUSCRIBE)) {
			content = BulletinSuscribeGenerator.getBulletinSuscribeHandler();
		}
		else if (mpod.getModular_page_option().getType().equals(ModularPageOptionType.NEXT_ARTICLES)) {
			content = ArticleCalendarGenerator.getNextArticlesHandler();
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
