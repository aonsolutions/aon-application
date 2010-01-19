package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

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

public class GeneratorController implements Constants {

	private GeneratorStatusController status;

	public GeneratorStatusController getStatus() {
		return status;
	}

	public GeneratorController(){
		super();
		status = (GeneratorStatusController)AonUtil.getRegisteredBean("generator_status");
	}
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {
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
	}
	
	public void onGenerateDiary(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		ArticleCalendarGenerator.generate();
		System.gc();
		finalizeGenerator();
	}
	
	public void onGenerateArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
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
	}

	public void onGenerateCurrentArticle(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		ArticleController controller = (ArticleController)AonUtil.getRegisteredBean("article");
		ArticleGenerator.generateArticle((Article) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateCurrentArticleCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		ArticleCategoryController controller = (ArticleCategoryController)AonUtil.getRegisteredBean("article_category");
		ArticleType[] types_ = ArticleType.values();
		for (int i = 0; i < types_.length; i++) {
			ArticleGenerator.generate(types_[i],(ArticleCategory) controller.getTo());
		}
		finalizeGenerator();
	}

	public void onGenerateModular(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		ModularPageGenerator.generate();
		finalizeGenerator();
	}

	public void onGenerateCurrentModular(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		ModularPageController controller = (ModularPageController)AonUtil.getRegisteredBean("modular_page");
		ModularPageGenerator.generate((ModularPage) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateGenericPages(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		GenericGenerator.generate();
		finalizeGenerator();
	}
	
	public void onGenerateCurrentGenericPage(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		GenericPageController controller = (GenericPageController)AonUtil.getRegisteredBean("generic_page");
		GenericGenerator.generate((GenericPage) controller.getTo());
		finalizeGenerator();
	}
	
	public void onGenerateLinks(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		LinkGenerator.generate();
		finalizeGenerator();
	}

	public void onGenerateCurrentLinkCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		LinkCategoryController controller = (LinkCategoryController)AonUtil.getRegisteredBean("link_category");
		LinkGenerator.generate((LinkCategory) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateFaqs(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		FaqGenerator.generate();
		finalizeGenerator();
	}

	public void onGenerateCurrentFaqCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		FaqCategoryController controller = (FaqCategoryController)AonUtil.getRegisteredBean("faq_category");
		FaqGenerator.generate((FaqCategory) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateDownloads(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		DownloadsGenerator.generate();
		finalizeGenerator();
	}
	
	public void onGenerateCurrentDownloadCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		DownloadCategoryController controller = (DownloadCategoryController)AonUtil.getRegisteredBean("download_category");
		DownloadsGenerator.generate((DownloadCategory) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateAlbums(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		AlbumGenerator.generate();
		finalizeGenerator();
	}
		
	public void onGenerateCurrentAlbumCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		AlbumCategoryController controller = (AlbumCategoryController)AonUtil.getRegisteredBean("album_category");
		AlbumGenerator.generate((AlbumCategory) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateDirectAccess(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		DirectAccessGenerator.generate();
		finalizeGenerator();
	}

	public void onGenerateCurrentDirectAccessCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		DirectAccessGroupController controller = (DirectAccessGroupController)AonUtil.getRegisteredBean("direct_access_group");
		DirectAccessGenerator.generate((DirectAccessGroup) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateProducts(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		ProductGenerator.generate();
		finalizeGenerator();
	}

	public void onGenerateSports(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		SportGenerator.generate();
		finalizeGenerator();
	}

	public void onGenerateHiru(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		HiruGenerator.generate();
		finalizeGenerator();
	}

	private void initGenerator(){
		System.gc();
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
		status.addMessage("END: ----------------------------------------------------------------------------------------------------------------------------------------");
		status.addMessage("END: ------------------------------------------------- LA GENERACION A TERMINADO -------------------------------------------------");
		status.addMessage("END: ----------------------------------------------------------------------------------------------------------------------------------------");
		status.finalized();
		System.gc();
	}

}
