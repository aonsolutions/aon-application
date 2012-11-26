package com.code.aon.ui.cms.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.IOUtils;

import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.Bulletin;
import com.code.aon.cms.BulletinArticle;
import com.code.aon.cms.BulletinDetail;
import com.code.aon.cms.BulletinEmail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.email.Emailer;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.GeneratorContext;
import com.code.aon.ui.cms.velocity.IVelocityConstants;
import com.code.aon.ui.cms.velocity.attribute.ArticleHandler;
import com.code.aon.ui.form.FormUtil;


public class BulletinController extends BasicI18nController implements ICMSConstants, Constants, IVelocityConstants {

	public void selectArticles() throws ManagerBeanException, ExpressionException {
		BulletinArticleController c = (BulletinArticleController)FormUtil.getController(BULLETIN_ARTICLE);
		IManagerBean moBean = BeanManager.getManagerBean(BulletinArticle.class);
		Bulletin bulletin = (Bulletin) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.BULLETIN_ARTICLE_BULLETIN_ID), "" + bulletin.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.BULLETIN_ARTICLE_POSITION));
		c.setCurrentBulletin(bulletin);
		c.setCriteria(criteria);
		c.onSearch(null);
	}

	public void onGenerate(ActionEvent event){
		LogPanelController logger = LogPanelController.getInstance();
		
		GeneratorContext context = new GeneratorContext( logger );
		BufferedWriter buff = null;
		try{
			Bulletin bulletin = (Bulletin)getTo(); 
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.BULLETIN_DETAIL_BULLETIN_ID), bulletin.getId());
			List<ITransferObject> list = getManagerBeanI18n().getList(criteria);
			List<ArticleHandler> article_content = new ArrayList<ArticleHandler>();
			for (ITransferObject toDetail: list) {
				BulletinDetail bulletinDetail = (BulletinDetail)toDetail;
				
				IManagerBean bean = BeanManager.getManagerBean(BulletinArticle.class); 
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BULLETIN_ARTICLE_BULLETIN_ID), bulletin.getId());
				List<ITransferObject> listBulletinArticle = bean.getList(criteria);
				for (ITransferObject bulletinArticle: listBulletinArticle) {
					
					bean = BeanManager.getManagerBean(ArticleDetail.class); 
					criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), ((BulletinArticle)bulletinArticle).getArticle().getId());
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), bulletinDetail.getLanguage().getId());
					List<ITransferObject> article_list = bean.getList(criteria);
					if (!article_list.isEmpty()){
						ArticleDetail articleDetail = (ArticleDetail) article_list.get(0);
						ArticleHandler ah = new ArticleHandler(articleDetail);
						article_content.add(ah);
					}
					
				}

				bean = BeanManager.getManagerBean(BulletinEmail.class); 
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BULLETIN_EMAIL_LANGUAGE_ID), bulletinDetail.getLanguage().getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BULLETIN_EMAIL_ACTIVE), Boolean.TRUE);
				List<ITransferObject> listBulletinEmail = bean.getList(criteria);
				Address[] emails = new Address[listBulletinEmail.size()];
				int i = 0;
				for (ITransferObject bulletinEmail: listBulletinEmail) {
					String email = ((BulletinEmail)bulletinEmail).getEmail();
					emails[i]=new InternetAddress(email);
					++i;
				}

				VelocityUtil vu = context.initVelocityUtil();
				
				File template = new File( ControllerUtil.getCurrentVmTemplatePath(), Templates.BULLETIN.getTemplateName() );  
			    if (!template.exists()){
			    	logger.error("No se ha encontrado plantilla " 
			    			+ Templates.BULLETIN.getTemplateName());
			    }else{
			    	StringWriter writer = new StringWriter();
			    	buff = new BufferedWriter(writer);
			        vu.put(TITLE_KEY, bulletinDetail.getTitle());
			        vu.put(CONTENT_KEY, bulletinDetail.getContent());
			        vu.put(ARTICLES_KEY, article_content);
					vu.generate(template, buff, "Bulletin");
			        vu.remove(ARTICLES_KEY);
			        vu.remove(CONTENT_KEY);
			        vu.remove(TITLE_KEY);
			        
			        logger.info("Enviando mails...");

					Emailer emailer = new Emailer();
					emailer.sendEmail(emails,
							bulletinDetail.getTitle(),
							writer.toString());
					
					logger.info("Mails enviados");

			    }
			}
		} catch (AuthenticationFailedException e) {
			logger.error("Error de autentificacion.");
		} catch (Throwable th) {
			logger.error(th.getMessage());
		} finally {
			IOUtils.closeQuietly(buff);
	    }
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		BulletinDetail bd = (BulletinDetail) getModelRowdataI18n();
		if (bd != null) label = bd.getTitle();
		return label;
	}	
	
}