package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FileOutputStream;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleDocumentDetail;
import com.code.aon.ui.cms.util.ControllerUtil;

public class ArticleDocumentController extends BasicI18nController {

	private static final String ARTICLE_DOCUMENTS = "article_documents";
	
	private Article currentArticle;
	
	private File articleDocumentsDirectory;

	public ArticleDocumentController() {
		articleDocumentsDirectory = new File( ControllerUtil.getDocumentsPath(), ARTICLE_DOCUMENTS);
		if (! articleDocumentsDirectory.exists() ) {
			articleDocumentsDirectory.mkdirs();
		}
	}

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

	public void fileUploaded(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		String uploadName = FilenameUtils.getName(item.getFileName());
		uploadName = uploadName.replaceAll("[^A-Za-z0-9._-]+", "");
		File file = new File( articleDocumentsDirectory, uploadName );
        FileOutputStream outputStream = null;
        try{
			byte[] data = item.getData();
	        outputStream = new FileOutputStream(file);
	        outputStream.write(data);
			outputStream.close();					
			String fileName = File.separator + ARTICLE_DOCUMENTS + File.separator + file.getName();
	        ((ArticleDocumentDetail)this.getToI18n()).setFile(fileName);
        }catch (Throwable th) {
        	IOUtils.closeQuietly(outputStream);
		}
	}

}
