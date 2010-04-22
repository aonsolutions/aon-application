package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
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

public class GeneratorController extends BasicController implements Constants {

	public void onGenerate(ActionEvent event) throws ManagerBeanException {

		if (isLanguajePageToGenerate){
			//Generar index.php de seleccion automatica de idioma
			CommonGenerator.getCommonGenerator().generateLanguagePage();
		}
		
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

	}

	private boolean isLanguajePageToGenerate = true;
	private boolean isModularPageToGenerate = true;
	private boolean isMenuToGenerate = true;
	private boolean isGenericToGenerate = true;
	private boolean isFaqToGenerate = true;
	private boolean isLinkToGenerate = true;
	private boolean isDirectAccessToGenerate = true;
	private boolean isAlbumToGenerate = true;
	private boolean isArticleToGenerate = true;
	private boolean isDownloadsToGenerate = true;

	public boolean isLanguajePageToGenerate() {
		return isLanguajePageToGenerate;
	}
	public void setLanguajePageToGenerate(boolean isLanguajePageToGenerate) {
		this.isLanguajePageToGenerate = isLanguajePageToGenerate;
	}
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
		isLanguajePageToGenerate = true;
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
		isLanguajePageToGenerate = false;
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
	
	private static String checkMem(String data) {
		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long maxMemory = Runtime.getRuntime().maxMemory();
		long memoryUsed = totalMemory-freeMemory;
		data += "-------------> "+(memoryUsed/(1024*1024))+" of "+(maxMemory/(1024*1024))+" MB used";
		return data;
	}	
	
}
