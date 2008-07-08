package com.code.aon.ui.cms.event;

import java.io.File;

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
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		ArticleController controller = (ArticleController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ArticleController controller = (ArticleController)event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		ArticleController controller = (ArticleController)event.getController();
		controller.setPageLimit(10);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ArticleController fc = (ArticleController)event.getController();
		Article f = (Article)event.getController().getTo();
		f.setArticleCategory(fc.getCurrentArticleCategory());
		f.setPosition(fc.orderedControllerSupport.getLastPosition(fc));
		f.setArticleType(fc.getCurrentType());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void generateThumbnail(Article a){
		if (a.getThumbnail()==null ||
				a.getThumbnail().trim().isEmpty()){
			if (a.getImage()!=null && !a.getImage().trim().isEmpty()){
				String thumb = ImageUtil.resize(ControllerUtil.getImagesPath()+a.getImage(),ImageUtil.DEF_MAX_SIZE);
				thumb = thumb.substring(ControllerUtil.getImagesPath().length(), thumb.length());
				try{
					thumb = thumb.replaceAll(File.separator, "/");
				}catch(Exception e){
					thumb = thumb.replaceAll(File.separator+File.separator, "/");
				}
				a.setThumbnail(thumb);
			}
		}
	}
	
}
