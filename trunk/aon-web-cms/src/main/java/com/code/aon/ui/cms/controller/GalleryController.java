package com.code.aon.ui.cms.controller;

import java.io.File;
import java.util.ArrayList;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.IGalleryController;
import com.code.aon.ui.form.BasicController;

public abstract class GalleryController extends BasicController implements IGalleryController{

	private String currentPath = revoverFilesPath();

	private String getRelativePath(String path) {
		String base = new File(revoverFilesPath()).getAbsolutePath(); 
		if (path.startsWith(base)) {
			if (path.length() == base.length()) return "/";
			else return path.substring(base.length()).replace('\\', '/');
		}
		return "";
	}

	public void chargeImageList() {
		ArrayList<Image> list = new ArrayList<Image>();
		File currentDir = new File(this.currentPath);
		if (!currentDir.exists()) currentDir.mkdirs();
		File dir = new File(this.currentPath);
		File files[] = dir.listFiles(getFilenameFilter());
		for (int i=0; i < files.length; i++) {
			File temp = files[i];
			if (!temp.isDirectory()) {
				Image img = new Image();
				img.setName(temp.getName());
				img.setPath(temp.getAbsolutePath());
				img.setRelativePath(getRelativePath(temp.getAbsolutePath()));
				list.add(img);
			}
		}
		model = new ListDataModel(list);
	}

	public String getCurrentRelativePath() {
		return getRelativePath(new File(currentPath).getAbsolutePath());
	}
	
	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public DataModel getModel() throws ManagerBeanException {
		if (model == null) {
			chargeImageList();
		}
		return model;
	}

/*
	public void action(ActionEvent event){
		InputFile inputFile = (InputFile)event.getSource();
		//file has been saved
		if (inputFile.getStatus() == InputFile.SAVED) {
			//Recargamos la imagenes
			chargeImageList(this.currentPath);
		}

		//invalid file, happens when clicking on upload without
		//selecting a file, or a file with no contents.
		if (inputFile.getStatus() == InputFile.INVALID) {
			inputFile.getFileInfo().getException().printStackTrace();
		}

		//file size exceeded the limit
		if (inputFile.getStatus() == InputFile.SIZE_LIMIT_EXCEEDED) {
			inputFile.getFileInfo().getException().printStackTrace();
		}

		//indicate that the request size is not specified.
		if (inputFile.getStatus() == InputFile.UNKNOWN_SIZE) {
			inputFile.getFileInfo().getException().printStackTrace();
		}
	}
*/
}
