package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.FTPUtil;
import com.code.aon.ui.cms.util.FileUtil;
import com.code.aon.ui.cms.velocity.AlbumGenerator;
import com.code.aon.ui.cms.velocity.ArticleGenerator;
import com.code.aon.ui.cms.velocity.CommonGenerator;
import com.code.aon.ui.cms.velocity.DirectAccessGenerator;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;
import com.code.aon.ui.cms.velocity.FaqGenerator;
import com.code.aon.ui.cms.velocity.GenericGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;
import com.code.aon.ui.cms.velocity.MenuGenerator;
import com.code.aon.ui.cms.velocity.ModularPageGenerator;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class GeneratorController extends BasicController implements Constants {

	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		GeneratorStatusController status = (GeneratorStatusController)AonUtil.getRegisteredBean("generator_status");
		status.onInit(event);
		
		//Copy css and js files from current template
		FileUtil.copyDir(ControllerUtil.getCssTemplatePath(), ControllerUtil.getPreviewPath());
		FileUtil.copyDir(ControllerUtil.getJsTemplatePath(), ControllerUtil.getPreviewPath());

		CommonGenerator.getCommonGenerator().generateLanguagePage();
		
		if (isModularPageToGenerate){
			//Generar index.html del idioma seleccionado
			ModularPageGenerator.generate();
		}

		if (isMenuToGenerate){
			//Generar menus
			MenuGenerator.generate();
		}

		if (isGenericToGenerate){
			//Generar generic
			GenericGenerator.generate();
		}

		if (isFaqToGenerate){
			FaqGenerator.generate();
		}

		if (isLinkToGenerate){
			//Generar link
			LinkGenerator.generate();
		}

		if (isDirectAccessToGenerate){
			//Generar direct access
			DirectAccessGenerator.generate();
		}

		if (isAlbumToGenerate){
			//Generar image album
			AlbumGenerator.generate();
		}

		if (isArticleToGenerate){
			ArticleGenerator.generate();
		}

		if (isDownloadsToGenerate){
			//Generar articulo
			DownloadsGenerator.generate();
		}

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
	private boolean isArticleToGenerate = true;
	private boolean isDownloadsToGenerate = true;

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
	public boolean isArticleToGenerate() {
		return isArticleToGenerate;
	}
	public void setArticleToGenerate(boolean isArticleToGenerate) {
		this.isArticleToGenerate = isArticleToGenerate;
	}
	public boolean isDownloadsToGenerate() {
		return isDownloadsToGenerate;
	}
	public void setDownloadsToGenerate(boolean isDownloadsToGenerate) {
		this.isDownloadsToGenerate = isDownloadsToGenerate;
	}
	public void onSelectAll(ActionEvent event) {
		isModularPageToGenerate = true;
		isMenuToGenerate = true;
		isGenericToGenerate = true;
		isFaqToGenerate = true;
		isLinkToGenerate = true;
		isDirectAccessToGenerate = true;
		isAlbumToGenerate = true;
		isArticleToGenerate = true;
		isDownloadsToGenerate = true;
	}
	public void onDeselectAll(ActionEvent event) {
		isModularPageToGenerate = false;
		isMenuToGenerate = false;
		isGenericToGenerate = false;
		isFaqToGenerate = false;
		isLinkToGenerate = false;
		isDirectAccessToGenerate = false;
		isAlbumToGenerate = false;
		isArticleToGenerate = false;
		isDownloadsToGenerate = false;
	}
	
}
