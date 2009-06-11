package com.code.aon.ui.cms.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.time.StopWatch;

import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.GenericPage;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.ModularPage;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.FileUtil;
import com.code.aon.ui.cms.velocity.AlbumGenerator;
import com.code.aon.ui.cms.velocity.ArticleCalendarGenerator;
import com.code.aon.ui.cms.velocity.ArticleGenerator;
import com.code.aon.ui.cms.velocity.CommonGenerator;
import com.code.aon.ui.cms.velocity.DirectAccessGenerator;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;
import com.code.aon.ui.cms.velocity.FaqGenerator;
import com.code.aon.ui.cms.velocity.GenericGenerator;
import com.code.aon.ui.cms.velocity.HiruGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;
import com.code.aon.ui.cms.velocity.MenuGenerator;
import com.code.aon.ui.cms.velocity.ModularPageGenerator;
import com.code.aon.ui.cms.velocity.ProductGenerator;
import com.code.aon.ui.cms.velocity.SportGenerator;
import com.code.aon.ui.util.AonUtil;

public class GeneratorController implements Constants, ICMSConstants {
	
	private static final Logger LOGGER = Logger.getLogger(GeneratorController.class.getName());

	private GeneratorStatusController status;
	
	private StopWatch stopWatch;
	
	private boolean initTransState;
	
	private boolean initSessionState;
	
	private String sessionFactoryName;

	public GeneratorStatusController getStatus() {
		return status;
	}

	public GeneratorController(){
		super();
		status = (GeneratorStatusController)AonUtil.getRegisteredBean(GENERATOR_STATUS);
	}
	
	private void generatorError( Throwable th ) {
		LOGGER.log(Level.SEVERE, th.getMessage(), th);
		String message = AonUtil.getMessage(ICMSConstants.BUNDLE_NAME, "cms_generator_error");
		AonUtil.addErrorMessage( message + " " + th.getMessage());
		throw new AbortProcessingException(th.getMessage(), th);		
	}
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		try {
			initGenerator();
			System.gc();
	
			//Generar index.html del idioma seleccionado
			ModularPageGenerator.generate();
			System.gc();
			//Generar menus
			MenuGenerator.generate();
			System.gc();
			//Generar generic
			GenericGenerator.generate();
			System.gc();
			//Generar faq
			FaqGenerator.generate();
			System.gc();
			//Generar link
			LinkGenerator.generate();
			System.gc();
			//Generar direct access
			DirectAccessGenerator.generate();
			System.gc();
			//Generar image album
			AlbumGenerator.generate();
			System.gc();
			ArticleCalendarGenerator.generate();
			System.gc();
			ArticleGenerator.generate(ArticleType.NEWS);
			System.gc();
			ArticleGenerator.generate(ArticleType.SERVICES);
			System.gc();
			ArticleGenerator.generate(ArticleType.EVENTS);
			System.gc();
			ArticleGenerator.generate(ArticleType.OTHER);
			System.gc();
			//Generar articulo
			DownloadsGenerator.generate();
			System.gc();
			//Generar cursos
			HiruGenerator.generate();
			System.gc();
			//Generar productos
			ProductGenerator.generate();
			System.gc();
			//Generar sports
			SportGenerator.generate();
			System.gc();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}
	}
	
	public void onGenerateDiary(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			ArticleCalendarGenerator.generate();
			System.gc();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}		
	}
	
	public void onGenerateArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {		
			initGenerator();
			ArticleCalendarGenerator.generate();
			System.gc();
			ArticleGenerator.generate(ArticleType.NEWS);
			System.gc();
			ArticleGenerator.generate(ArticleType.SERVICES);
			System.gc();
			ArticleGenerator.generate(ArticleType.EVENTS);
			System.gc();
			ArticleGenerator.generate(ArticleType.OTHER);
			System.gc();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}		
	}

	public void onGenerateCurrentArticle(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			ArticleController controller = (ArticleController)AonUtil.getRegisteredBean(ARTICLE);
			ArticleGenerator.generateArticle((Article) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}
	}

	public void onGenerateCurrentArticleCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			ArticleCategoryController controller = (ArticleCategoryController)AonUtil.getRegisteredBean(ARTICLE_CATEGORY);
			ArticleType[] types_ = ArticleType.values();
			for (int i = 0; i < types_.length; i++) {
				ArticleGenerator.generate(types_[i],(ArticleCategory) controller.getTo());
			}
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateModular(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			ModularPageGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}
	}

	public void onGenerateCurrentModular(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			ModularPageController controller = (ModularPageController)AonUtil.getRegisteredBean(MODULAR_PAGE);
			ModularPageGenerator.generate((ModularPage) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateGenericPages(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			GenericGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}
	
	public void onGenerateCurrentGenericPage(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			GenericPageController controller = (GenericPageController)AonUtil.getRegisteredBean(GENERIC_PAGE);
			GenericGenerator.generate((GenericPage) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}
	
	public void onGenerateLinks(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			LinkGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateCurrentLinkCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			LinkCategoryController controller = (LinkCategoryController)AonUtil.getRegisteredBean(LINK_CATEGORY);
			LinkGenerator.generate((LinkCategory) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateFaqs(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			FaqGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateCurrentFaqCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			FaqCategoryController controller = (FaqCategoryController)AonUtil.getRegisteredBean(FAQ_CATEGORY);
			FaqGenerator.generate((FaqCategory) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateDownloads(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			DownloadsGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}
	
	public void onGenerateCurrentDownloadCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			DownloadCategoryController controller = (DownloadCategoryController)AonUtil.getRegisteredBean(DOWNLOAD_CATEGORY);
			DownloadsGenerator.generate((DownloadCategory) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateAlbums(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			AlbumGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}
		
	public void onGenerateCurrentAlbumCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			AlbumCategoryController controller = (AlbumCategoryController)AonUtil.getRegisteredBean(ALBUM_CATEGORY);
			AlbumGenerator.generate((AlbumCategory) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateDirectAccess(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			DirectAccessGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateCurrentDirectAccessCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			DirectAccessGroupController controller = (DirectAccessGroupController)AonUtil.getRegisteredBean(DIRECT_ACCESS_GROUP);
			DirectAccessGenerator.generate((DirectAccessGroup) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateProducts(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			ProductGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateSports(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			SportGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateHiru(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			HiruGenerator.generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	private void initSession() {
		this.initTransState = HibernateUtil.mustBeginTransaction();
		this.initSessionState = HibernateUtil.mustCloseSession();
		this.sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);		
	}
	
	private void closeSession() {
		try {
			HibernateUtil.commitTransaction(sessionFactoryName);
			HibernateUtil.closeSession(sessionFactoryName);
		} catch (DAOException e) {
		    try {
				HibernateUtil.rollbackTransaction(sessionFactoryName);
			} catch (DAOException e2) {
				LOGGER.log(Level.SEVERE, e2.getMessage(), e2);
			}
		} finally {		
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}
	}
	
	private void initGenerator(){
		stopWatch = new StopWatch();
		stopWatch.start();		
		System.gc();
		initSession();
		status.onInit(null);
		//Copy css and js files from current template
		FileUtil.copyDir(ControllerUtil.getCssTemplatePath(), ControllerUtil.getPreviewPath());
		FileUtil.copyDir(ControllerUtil.getJsTemplatePath(), ControllerUtil.getPreviewPath());
		CommonGenerator.getCommonGenerator().generateLanguagePage();
		CommonGenerator.getCommonGenerator().generateEmailSendPage();
		CommonGenerator.getCommonGenerator().generateSearchPage();
		CommonGenerator.getCommonGenerator().generateCaptchaPage();
	}

	private void finalizeGenerator(){
		stopWatch.stop();
		status.info("Ha tardadado: " + stopWatch.toString());		
		status.info("----------------------------------------------------------------------------------------------------------------------------------------");
		status.info("------------------------------------------------ LA GENERACION HA TERMINADO ------------------------------------------------");
		status.info("----------------------------------------------------------------------------------------------------------------------------------------");
		status.finalized();
		closeSession();
		System.gc();
	}

}
