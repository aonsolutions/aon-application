package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FileOutputStream;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleDocumentDetail;
import com.code.aon.ui.cms.util.ControllerUtil;

public class ArticleDocumentController extends BasicI18nController {

	private Article currentArticle;

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public Article getCurrentArticle() {
		return currentArticle;
	}

	public void setCurrentArticle(Article currentArticle) {
		this.currentArticle = currentArticle;
	}

	private String currentPath = ControllerUtil.getDocumentsPath();

	public void fileUploaded(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		String upload_name = item.getFileName();
		upload_name = upload_name.replace('\\', '/');
		if (upload_name.lastIndexOf('/')!=-1)
			upload_name = upload_name.substring(upload_name.lastIndexOf('/'));
		upload_name = upload_name.replaceAll("[^A-Za-z0-9._-]+", "");
		String fileName = File.separator+"article_documents";
		File file_dir = new File( currentPath+fileName);
		if (!file_dir.exists()) {
			file_dir.mkdir();
		}
		if (upload_name.startsWith("/"))
			fileName += upload_name;
		else
			fileName += "/" + upload_name;
		File file = new File( currentPath+fileName);
        FileOutputStream outputStream = null;
        try{
			byte[] data = item.getData();
	        outputStream = new FileOutputStream(file);
	        outputStream.write(data);
			outputStream.close();					
	        ((ArticleDocumentDetail)this.getToI18n()).setFile(fileName);
        }catch (Throwable th) {
        	IOUtils.closeQuietly(outputStream);
		}
	}

}
