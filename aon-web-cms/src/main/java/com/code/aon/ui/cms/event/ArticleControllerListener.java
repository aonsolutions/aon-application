package com.code.aon.ui.cms.event;

import java.io.File;
import java.util.List;

import com.code.aon.cms.Article;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
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
		ArticleController fc = (ArticleController)event.getController();
		Article f = (Article)event.getController().getTo();
		f.setArticleCategory(fc.getCurrentArticleCategory());
		f.setPosition(getLastPosition(fc));
		f.setArticleType(fc.getCurrentType());
		generateThumbnail(f);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		Article f = (Article)event.getController().getTo();
		generateThumbnail(f);
	}
	
	private int getLastPosition(ArticleController fc) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(fc.getManagerBean().getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), "" + fc.getCurrentArticleCategory().getId());
			criteria.addOrder(fc.getManagerBean().getFieldName(ICMSAlias.ARTICLE_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)fc.getManagerBean().getList(criteria);
			if (list.size() > 0) {
				Article f = (Article)list.get(0);
				position = f.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
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
