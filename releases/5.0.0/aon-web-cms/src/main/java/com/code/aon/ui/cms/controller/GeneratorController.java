package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.IOException;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
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
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.velocity.ActivityGenerator;
import com.code.aon.ui.cms.velocity.AlbumGenerator;
import com.code.aon.ui.cms.velocity.ArticleCalendarGenerator;
import com.code.aon.ui.cms.velocity.ArticleGenerator;
import com.code.aon.ui.cms.velocity.CommonGenerator;
import com.code.aon.ui.cms.velocity.DirectAccessGenerator;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;
import com.code.aon.ui.cms.velocity.FaqGenerator;
import com.code.aon.ui.cms.velocity.GeneratorContext;
import com.code.aon.ui.cms.velocity.GenericGenerator;
import com.code.aon.ui.cms.velocity.HiruGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;
import com.code.aon.ui.cms.velocity.MenuGenerator;
import com.code.aon.ui.cms.velocity.ModularPageGenerator;
import com.code.aon.ui.cms.velocity.ProductGenerator;
import com.code.aon.ui.cms.velocity.SportGenerator;
import com.code.aon.ui.util.AonUtil;

public class GeneratorController implements Constants, ICMSConstants {
	
	private GeneratorStatusController status;
	
	private GeneratorApplicationController applicationController;
	
	private GeneratorContext context;
	
	private StopWatch stopWatch;
	
	private boolean initTransState;
	
	private boolean initSessionState;
	
	private String sessionFactoryName;

	public GeneratorController(){
		status = (GeneratorStatusController)AonUtil.getRegisteredBean(GENERATOR_STATUS);
	}

	public GeneratorStatusController getStatus() {
		return status;
	}
	
	public GeneratorContext getContext() {
		return context;
	}

	public void onGenerate(GeneratorApplicationController applicationController) throws ManagerBeanException {
		this.applicationController = applicationController;		
		try {
			initGenerator();
	
			//Generar index.html del idioma seleccionado
			new ModularPageGenerator().generate();
			//Generar menus
			new MenuGenerator().generate();
			//Generar generic
			new GenericGenerator().generate();
			//Generar faq
			new FaqGenerator().generate();
			//Generar link
			new LinkGenerator().generate();
			//Generar direct access
			new DirectAccessGenerator().generate();
			//Generar image album
			new AlbumGenerator().generate();
			new ArticleCalendarGenerator().generate();
			ArticleGenerator ag = new ArticleGenerator();
			ag.generate(ArticleType.NEWS);
			ag.generate(ArticleType.SERVICES);
			ag.generate(ArticleType.EVENTS);
			ag.generate(ArticleType.OTHER);
			//Generar articulo
			new DownloadsGenerator().generate();
			//Generar cursos
			new HiruGenerator().generate();
			//Generar productos
			new ProductGenerator().generate();
			//Generar sports
			new SportGenerator().generate();
			//Generar actividades
			new ActivityGenerator().generate();			
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}
	}
	
	public void onGenerateDiary(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new ArticleCalendarGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}		
	}
	
	public void onGenerateArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {		
			initGenerator();
			new ArticleCalendarGenerator().generate();
			ArticleGenerator ag = new ArticleGenerator();
			ag.generate(ArticleType.NEWS);
			ag.generate(ArticleType.SERVICES);
			ag.generate(ArticleType.EVENTS);
			ag.generate(ArticleType.OTHER);
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}		
	}

	public void onGenerateCurrentArticle(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			ArticleController controller = (ArticleController)AonUtil.getRegisteredBean(ARTICLE);
			new ArticleGenerator().generateArticle((Article) controller.getTo());
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
				new ArticleGenerator().generate(types_[i],(ArticleCategory) controller.getTo());
			}
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateModular(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new ModularPageGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}
	}

	public void onGenerateCurrentModular(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			ModularPageController controller = (ModularPageController)AonUtil.getRegisteredBean(MODULAR_PAGE);
			new ModularPageGenerator().generate((ModularPage) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateGenericPages(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new GenericGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}
	
	public void onGenerateCurrentGenericPage(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			GenericPageController controller = (GenericPageController)AonUtil.getRegisteredBean(GENERIC_PAGE);
			new GenericGenerator().generate((GenericPage) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}
	
	public void onGenerateLinks(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new LinkGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateCurrentLinkCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			LinkCategoryController controller = (LinkCategoryController)AonUtil.getRegisteredBean(LINK_CATEGORY);
			new LinkGenerator().generate((LinkCategory) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateFaqs(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new FaqGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateCurrentFaqCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			FaqCategoryController controller = (FaqCategoryController)AonUtil.getRegisteredBean(FAQ_CATEGORY);
			new FaqGenerator().generate((FaqCategory) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateDownloads(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new DownloadsGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}
	
	public void onGenerateCurrentDownloadCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			DownloadCategoryController controller = (DownloadCategoryController)AonUtil.getRegisteredBean(DOWNLOAD_CATEGORY);
			new DownloadsGenerator().generate((DownloadCategory) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateAlbums(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new AlbumGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}
		
	public void onGenerateCurrentAlbumCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			AlbumCategoryController controller = (AlbumCategoryController)AonUtil.getRegisteredBean(ALBUM_CATEGORY);
			new AlbumGenerator().generate((AlbumCategory) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateDirectAccess(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new DirectAccessGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateCurrentDirectAccessCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			DirectAccessGroupController controller = (DirectAccessGroupController)AonUtil.getRegisteredBean(DIRECT_ACCESS_GROUP);
			new DirectAccessGenerator().generate((DirectAccessGroup) controller.getTo());
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateProducts(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new ProductGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}			
	}

	public void onGenerateSports(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new SportGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateHiru(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new HiruGenerator().generate();
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}						
	}

	public void onGenerateActivity(ActionEvent event) throws ManagerBeanException, ExpressionException {
		try {
			initGenerator();
			new ActivityGenerator().generate();
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
		HibernateUtil.closeSession(sessionFactoryName);
		if (initTransState != HibernateUtil.mustBeginTransaction()) {
			HibernateUtil.setBeginTransaction(initTransState);
		}
		if (initSessionState != HibernateUtil.mustCloseSession()) {
			HibernateUtil.setCloseSession(initSessionState);
		}
	}
	
	private void initGenerator(){
		context = new GeneratorContext();
		stopWatch = new StopWatch();
		stopWatch.start();		
		System.gc();
		initSession();
		status.onInit(null);
		//Copy css and js files from current template
		try {
			File cssPath = ControllerUtil.getCssTemplatePath();
			if ( cssPath.exists() ) {
				FileUtils.copyDirectoryToDirectory(cssPath, ControllerUtil.getPreviewPath());	
			}
			File jsPath = ControllerUtil.getJsTemplatePath();
			if ( jsPath.exists() ) {
				FileUtils.copyDirectoryToDirectory(jsPath, ControllerUtil.getPreviewPath());
			}
			new CommonGenerator().generateBasicPages();			
		} catch (IOException e) {
			generatorError(e);
		}
	}

	private void finalizeGenerator() {
		finalizeGenerator(false);
	}
	
	private void finalizeGenerator( boolean withErrors ) {
		stopWatch.stop();
		status.info("Duración de la generación: " + stopWatch.toString());		
		status.info("----------------------------------------------------------------------------------------------------------------------------------------");
		if ( withErrors ) {
			status.info( AonUtil.getMessage(ICMSConstants.BUNDLE_NAME, CMS_GENERATOR_ERROR) );
		} else {
			status.info( AonUtil.getMessage(ICMSConstants.BUNDLE_NAME, CMS_GENERATOR_FINISHED) );
		}		
		status.info("----------------------------------------------------------------------------------------------------------------------------------------");
		status.finalized();
		closeSession();
		context = null;
		System.gc();
	}

	private void generatorError( Throwable th ) {
		if ( applicationController != null ) {
			this.applicationController.unlock();
		}
		status.error( th.getMessage() );
		finalizeGenerator( true );
		throw new AbortProcessingException(th.getMessage(), th);		
	}
		
}
