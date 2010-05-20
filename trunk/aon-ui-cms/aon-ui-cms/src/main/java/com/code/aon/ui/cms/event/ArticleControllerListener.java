package com.code.aon.ui.cms.event;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.code.aon.cms.Album;
import com.code.aon.cms.Article;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.ArticleController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ArticleControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Article f = (Article)event.getController().getTo();
		generateThumbnail(f);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		Article f = (Article)event.getController().getTo();
		generateThumbnail(f);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ArticleController c = (ArticleController)event.getController();
		try {
			c.onSelectRelatedArticles(null);
			c.onSelectArticleDocuments(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		} catch (ExpressionException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ArticleController c = (ArticleController)event.getController();
		try {
			c.onSelectRelatedArticles(null);
			c.onSelectArticleDocuments(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		} catch (ExpressionException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	private void generateThumbnail(Article a) {
		if ( StringUtils.isBlank(a.getThumbnail()) ) {
			if (! StringUtils.isBlank(a.getImage()) ) {
				File file = ControllerUtil.getImagePath(a.getImage());
				File thumb = ImageUtil.resize(file, Album.DEFAULT_THUMBNAIL_WIDTH);
				String path = ControllerUtil.getRelativePath(ControllerUtil.getImagesPath(), thumb);
				a.setThumbnail( path );
			}
		}
	}
	
}
