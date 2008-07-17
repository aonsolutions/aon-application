package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.LengthValidator;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleDocumentDetail;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;

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

	private UploadedFile inputFile;
	private long maximumSize = -1;;
	private String currentPath = ControllerUtil.getDocumentsPath();

	public UploadedFile getInputFile() {
		return inputFile;
	}

	public void setInputFile(UploadedFile inputFile) {
		this.inputFile = inputFile;
	}
	
	public void fileUploaded( ActionEvent event ){
		if ( this.inputFile!= null ) {
			long size = this.inputFile.getSize();
			String upload_name = inputFile.getName();
			upload_name = upload_name.replace('\\', '/');
			upload_name = upload_name.substring(upload_name.lastIndexOf('/'));
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
			if ( (maximumSize != -1) && (size > maximumSize) ) {
				FacesContext ctx = FacesContext.getCurrentInstance();
				FacesMessage message = AonUtil.getMessage( ctx,
						LengthValidator.MAXIMUM_MESSAGE_ID, new Object[]{maximumSize, fileName} );
				ctx.addMessage(AonUtil.AON_ERROR, message);
			} else {
		        FileOutputStream outputStream = null;
		        try{
					byte[] data = this.inputFile.getBytes();
			        outputStream = new FileOutputStream(file);
			        outputStream.write(data);
					outputStream.close();					
			        ((ArticleDocumentDetail)this.getToI18n()).setFile(fileName);
		        }catch (Exception e) {
		        	try {outputStream.close();} catch (Exception e1) {}
				}
			}
		}
	}

}
