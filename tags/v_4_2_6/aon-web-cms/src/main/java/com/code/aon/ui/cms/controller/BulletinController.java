package com.code.aon.ui.cms.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.IOUtils;

import com.code.aon.cms.Article;
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
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.email.Emailer;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.CommonGenerator;
import com.code.aon.ui.cms.velocity.attribute.ArticleHandler;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;


public class BulletinController extends BasicI18nController implements ICMSConstants {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}


	private Date publishDate = new Date();
	
	private Date expireDate;

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public List<SelectItem> getArticleList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Article.class);
		Criteria criteria = new Criteria();
		if (publishDate!=null)
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE),publishDate);
		if (expireDate!=null)
			criteria.addLessThanOrEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE),expireDate);
		criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Article article = (Article)list.get(i);
			int id = article.getId();
			String name = article.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onSelectArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
		BulletinArticleController c = (BulletinArticleController)FormUtil.getController(BULLETIN_ARTICLE);
		IManagerBean moBean = BeanManager.getManagerBean(BulletinArticle.class);
		Bulletin bulletin = (Bulletin) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.BULLETIN_ARTICLE_BULLETIN_ID), "" + bulletin.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.BULLETIN_ARTICLE_POSITION));
		c.setCurrentBulletin(bulletin);
		c.setCriteria(criteria);
		c.onSearch(event);
	}

	public void onInit(ActionEvent event){
		GeneratorStatusController status = (GeneratorStatusController)AonUtil.getRegisteredBean(GENERATOR_STATUS);
		status.onInit(event);
		this.onSearch(event);
	}

	public void onGenerate(ActionEvent event){
		GeneratorStatusController status = (GeneratorStatusController)AonUtil.getRegisteredBean(GENERATOR_STATUS);
		status.onInit(event);
		
		BufferedWriter buff = null;
		List<ITransferObject> list;
		List<ITransferObject> listBulletinArticle;
		List<ITransferObject> listBulletinEmail;
		List<ITransferObject> article_list;
		List<ArticleHandler> article_content = new ArrayList<ArticleHandler>();
		Address[] emails = null;
		try{
			Bulletin bulletin = null; 
			BulletinDetail bulletinDetail = null;
			ArticleDetail articleDetail = null;
			Criteria criteria = null;
			IManagerBean bean;
				
			bulletin = (Bulletin)getTo(); 
			bulletinDetail = null;
			criteria = new Criteria();
			criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.BULLETIN_DETAIL_BULLETIN_ID), bulletin.getId());
			list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
			for (ITransferObject toDetail: list) {
				bulletinDetail = (BulletinDetail)toDetail;
				
				bean = BeanManager.getManagerBean(BulletinArticle.class); 
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BULLETIN_ARTICLE_BULLETIN_ID), bulletin.getId());
				listBulletinArticle = (List<ITransferObject>)bean.getList(criteria);
				for (ITransferObject bulletinArticle: listBulletinArticle) {
					
					bean = BeanManager.getManagerBean(ArticleDetail.class); 
					criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), ((BulletinArticle)bulletinArticle).getArticle().getId());
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), bulletinDetail.getLanguage().getId());
					article_list = (List<ITransferObject>)bean.getList(criteria);
					if (!article_list.isEmpty()){
						articleDetail = (ArticleDetail) article_list.get(0);
						ArticleHandler ah = new ArticleHandler(articleDetail);
						article_content.add(ah);
					}
					
				}

				bean = BeanManager.getManagerBean(BulletinEmail.class); 
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BULLETIN_EMAIL_LANGUAGE_ID), bulletinDetail.getLanguage().getId());
				listBulletinEmail = (List<ITransferObject>)bean.getList(criteria);
				emails = new Address[listBulletinEmail.size()];
				int i = 0;
				for (ITransferObject bulletinEmail: listBulletinEmail) {
					String email = ((BulletinEmail)bulletinEmail).getEmail();
					emails[i]=new InternetAddress(email);
					++i;
				}

				VelocityUtil vu = CommonGenerator.getCommonGenerator().initVelocityUtil();
				
				File template = new File( ControllerUtil.getCurrentVmTemplatePath(), Templates.BULLETIN.getTemplateName() );  
			    if (!template.exists()){
			    	status.error("No se ha encontrado plantilla " 
			    			+ Templates.BULLETIN.getTemplateName());
			    }else{
			    	StringWriter writer = new StringWriter();
			    	buff = new BufferedWriter(writer);
			        vu.put("title", bulletinDetail.getTitle());
			        vu.put("content", bulletinDetail.getContent());
			        vu.put("articles", article_content);
					vu.generate(template, buff, "Bulletin");
			        vu.remove("articles");
			        vu.remove("content");
			        vu.remove("title");
			        
			        status.info("Enviando mails...");

					Emailer emailer = new Emailer();
					emailer.sendEmail(emails,
							bulletinDetail.getTitle(),
							writer.toString());
					
					status.info("Mails enviados");

			    }
			}
		} catch (AuthenticationFailedException e) {
			status.error("Error de autentificacion.");
		} catch (Throwable th) {
			status.error(th.getMessage());
		}finally {
			article_content = null;
			list = null;
			article_list = null;
			listBulletinArticle = null;
			listBulletinEmail = null;
			IOUtils.closeQuietly(buff);
	    }

	}
	
	// END SENDER
	
}