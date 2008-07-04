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
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class GeneratorController extends BasicController implements Constants {

	private GeneratorStatusController status;

	public GeneratorController(){
		super();
		status = (GeneratorStatusController)AonUtil.getRegisteredBean("generator_status");
	}
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		initGenerator();

		if (isModularPageToGenerate){
			//Generar index.html del idioma seleccionado
			ModularPageGenerator.generate();
		}
		
		System.gc();

		if (isMenuToGenerate){
			//Generar menus
			MenuGenerator.generate();
		}
		
		System.gc();

		if (isGenericToGenerate){
			//Generar generic
			GenericGenerator.generate();
		}
		
		System.gc();

		if (isFaqToGenerate){
			FaqGenerator.generate();
		}
		
		System.gc();

		if (isLinkToGenerate){
			//Generar link
			LinkGenerator.generate();
		}
		
		System.gc();

		if (isDirectAccessToGenerate){
			//Generar direct access
			DirectAccessGenerator.generate();
		}
		
		System.gc();

		if (isAlbumToGenerate){
			//Generar image album
			AlbumGenerator.generate();
		}
		
		System.gc();

		if (isArticleCalendarToGenerate){
			ArticleCalendarGenerator.generate();
		}
		
		System.gc();

		if (isArticleNEWSToGenerate){
			ArticleGenerator.generate(ArticleType.NEWS);
		}

		System.gc();

		if (isArticleSERVICESToGenerate){
			ArticleGenerator.generate(ArticleType.SERVICES);
		}

		System.gc();

		if (isArticleEVENTSToGenerate){
			ArticleGenerator.generate(ArticleType.EVENTS);
		}

		System.gc();

		if (isArticleOTHERToGenerate){
			ArticleGenerator.generate(ArticleType.OTHER);
		}
		
		System.gc();

		if (isDownloadsToGenerate){
			//Generar articulo
			DownloadsGenerator.generate();
		}
		
		System.gc();

		if (isHiruCoursesToGenerate){
			//Generar articulo
			HiruGenerator.generate();
		}
		
		System.gc();

		if (isProductsToGenerate){
			//Generar productos
			ProductGenerator.generate();
		}
		
		System.gc();
		
		if (isSportsToGenerate){
			//Generar productos
			SportGenerator.generate();
		}
		
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

	public void onGenerateCurrentModular(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		ModularPageController controller = (ModularPageController)AonUtil.getRegisteredBean("modular_page");
		ModularPageGenerator.generate((ModularPage) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateCurrentGenericPage(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		GenericPageController controller = (GenericPageController)AonUtil.getRegisteredBean("generic_page");
		GenericGenerator.generate((GenericPage) controller.getTo());
		finalizeGenerator();
	}
	
	public void onGenerateCurrentLinkCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		LinkCategoryController controller = (LinkCategoryController)AonUtil.getRegisteredBean("link_category");
		LinkGenerator.generate((LinkCategory) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateCurrentFaqCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		FaqCategoryController controller = (FaqCategoryController)AonUtil.getRegisteredBean("faq_category");
		FaqGenerator.generate((FaqCategory) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateCurrentDownloadCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		DownloadCategoryController controller = (DownloadCategoryController)AonUtil.getRegisteredBean("download_category");
		DownloadsGenerator.generate((DownloadCategory) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateCurrentAlbumCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		AlbumCategoryController controller = (AlbumCategoryController)AonUtil.getRegisteredBean("album_category");
		AlbumGenerator.generate((AlbumCategory) controller.getTo());
		finalizeGenerator();
	}

	public void onGenerateCurrentDirectAccessCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		initGenerator();
		DirectAccessGroupController controller = (DirectAccessGroupController)AonUtil.getRegisteredBean("direct_access_group");
		DirectAccessGenerator.generate((DirectAccessGroup) controller.getTo());
		finalizeGenerator();
	}

	private void initGenerator(){
		status.onInit(null);
		//Copy css and js files from current template
		FileUtil.copyDir(ControllerUtil.getCssTemplatePath(), ControllerUtil.getPreviewPath());
		FileUtil.copyDir(ControllerUtil.getJsTemplatePath(), ControllerUtil.getPreviewPath());
		CommonGenerator.getCommonGenerator().generateLanguagePage();
		CommonGenerator.getCommonGenerator().generateEmailSendPage();
	}

	private void finalizeGenerator(){
		status.addMessage("¡¡¡¡¡ YOUR WEB IS DONE !!!!! ;-DDDD");
		status.finalized();
	}

	private boolean isModularPageToGenerate = true;
	private boolean isMenuToGenerate = true;
	private boolean isGenericToGenerate = true;
	private boolean isFaqToGenerate = true;
	private boolean isLinkToGenerate = true;
	private boolean isDirectAccessToGenerate = true;
	private boolean isAlbumToGenerate = true;
	private boolean isArticleCalendarToGenerate = true;
	private boolean isDownloadsToGenerate = true;
	private boolean isArticleNEWSToGenerate = true;
	private boolean isArticleSERVICESToGenerate = true;
	private boolean isArticleEVENTSToGenerate = true;
	private boolean isArticleOTHERToGenerate = true;
	private boolean isHiruCoursesToGenerate = true;
	private boolean isProductsToGenerate = true;
	private boolean isSportsToGenerate = true;
	
	public boolean isModularPageToGenerate() {
		return isModularPageToGenerate;
	}
	public void setModularPageToGenerate(boolean isModularPageToGenerate) {
		this.isModularPageToGenerate = isModularPageToGenerate;
	}
	public boolean isMenuToGenerate() {
		return isMenuToGenerate;
	}
	public void setMenuToGenerate(boolean isMenuToGenerate) {
		this.isMenuToGenerate = isMenuToGenerate;
	}
	public boolean isGenericToGenerate() {
		return isGenericToGenerate;
	}
	public void setGenericToGenerate(boolean isGenericToGenerate) {
		this.isGenericToGenerate = isGenericToGenerate;
	}
	public boolean isFaqToGenerate() {
		return isFaqToGenerate;
	}
	public void setFaqToGenerate(boolean isFaqToGenerate) {
		this.isFaqToGenerate = isFaqToGenerate;
	}
	public boolean isLinkToGenerate() {
		return isLinkToGenerate;
	}
	public void setLinkToGenerate(boolean isLinkToGenerate) {
		this.isLinkToGenerate = isLinkToGenerate;
	}
	public boolean isDirectAccessToGenerate() {
		return isDirectAccessToGenerate;
	}
	public void setDirectAccessToGenerate(boolean isDirectAccessToGenerate) {
		this.isDirectAccessToGenerate = isDirectAccessToGenerate;
	}
	public boolean isAlbumToGenerate() {
		return isAlbumToGenerate;
	}
	public void setAlbumToGenerate(boolean isAlbumToGenerate) {
		this.isAlbumToGenerate = isAlbumToGenerate;
	}
	public boolean isDownloadsToGenerate() {
		return isDownloadsToGenerate;
	}
	public void setDownloadsToGenerate(boolean isDownloadsToGenerate) {
		this.isDownloadsToGenerate = isDownloadsToGenerate;
	}
	public boolean isArticleCalendarToGenerate() {
		return isArticleCalendarToGenerate;
	}
	public void setArticleCalendarToGenerate(boolean isArticleCalendarToGenerate) {
		this.isArticleCalendarToGenerate = isArticleCalendarToGenerate;
	}
	public boolean isArticleNEWSToGenerate() {
		return isArticleNEWSToGenerate;
	}
	public void setArticleNEWSToGenerate(boolean isArticleNEWSToGenerate) {
		this.isArticleNEWSToGenerate = isArticleNEWSToGenerate;
	}
	public boolean isArticleSERVICESToGenerate() {
		return isArticleSERVICESToGenerate;
	}
	public void setArticleSERVICESToGenerate(boolean isArticleSERVICESToGenerate) {
		this.isArticleSERVICESToGenerate = isArticleSERVICESToGenerate;
	}
	public boolean isArticleEVENTSToGenerate() {
		return isArticleEVENTSToGenerate;
	}
	public void setArticleEVENTSToGenerate(boolean isArticleEVENTSToGenerate) {
		this.isArticleEVENTSToGenerate = isArticleEVENTSToGenerate;
	}
	public boolean isArticleOTHERToGenerate() {
		return isArticleOTHERToGenerate;
	}
	public void setArticleOTHERToGenerate(boolean isArticleOTHERToGenerate) {
		this.isArticleOTHERToGenerate = isArticleOTHERToGenerate;
	}
	public boolean isHiruCoursesToGenerate() {
		return isHiruCoursesToGenerate;
	}
	public void setHiruCoursesToGenerate(boolean isHiruCoursesToGenerate) {
		this.isHiruCoursesToGenerate = isHiruCoursesToGenerate;
	}
	public boolean isProductsToGenerate() {
		return isProductsToGenerate;
	}
	public void setProductsToGenerate(boolean isProductsToGenerate) {
		this.isProductsToGenerate = isProductsToGenerate;
	}
	public boolean isSportsToGenerate() {
		return isSportsToGenerate;
	}
	public void setSportsToGenerate(boolean isSportsToGenerate) {
		this.isSportsToGenerate = isSportsToGenerate;
	}

	public void onSelectAll(ActionEvent event) {
		isModularPageToGenerate = true;
		isMenuToGenerate = true;
		isGenericToGenerate = true;
		isFaqToGenerate = true;
		isLinkToGenerate = true;
		isDirectAccessToGenerate = true;
		isAlbumToGenerate = true;
		isDownloadsToGenerate = true;
		isArticleCalendarToGenerate = true;
		isArticleNEWSToGenerate = true;
		isArticleSERVICESToGenerate = true;
		isArticleEVENTSToGenerate = true;
		isArticleOTHERToGenerate = true;
		isHiruCoursesToGenerate = true;
		isProductsToGenerate = true;
		isSportsToGenerate = true;
	}
	public void onDeselectAll(ActionEvent event) {
		isModularPageToGenerate = false;
		isMenuToGenerate = false;
		isGenericToGenerate = false;
		isFaqToGenerate = false;
		isLinkToGenerate = false;
		isDirectAccessToGenerate = false;
		isAlbumToGenerate = false;
		isArticleCalendarToGenerate = false;
		isDownloadsToGenerate = false;
		isArticleNEWSToGenerate = false;
		isArticleSERVICESToGenerate = false;
		isArticleEVENTSToGenerate = false;
		isArticleOTHERToGenerate = false;
		isHiruCoursesToGenerate = false;
		isProductsToGenerate = false;
		isSportsToGenerate = false;
	}
	

}
