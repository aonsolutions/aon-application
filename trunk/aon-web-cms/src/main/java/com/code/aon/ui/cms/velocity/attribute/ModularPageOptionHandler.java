package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.ui.cms.util.MenuOptionUtil;
import com.code.aon.ui.cms.velocity.ArticleGenerator;
import com.code.aon.ui.cms.velocity.BannerGenerator;
import com.code.aon.ui.cms.velocity.DirectAccessGenerator;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;
import com.code.aon.ui.cms.velocity.GenericGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;

public class ModularPageOptionHandler {

	private String template;
	
	private Object content;

	public ModularPageOptionHandler(ModularPageOptionDetail mpod) {
		template = mpod.getModular_page_option().getType().getTemplateName();
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
	}

	public String getTemplate() {
		return template;
	}

	public Object getContent() {
		return content;
	}

}
