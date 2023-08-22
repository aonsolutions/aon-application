package com.code.aon.web.help.service.vimeo;

import com.vimeo.networking2.*;
import com.vimeo.networking2.enums.ProjectItemType;

import java.util.List;
import java.util.Optional;

public class VimeoService {
	private VimeoResult vimeoResult;
	public Optional<List<Folder>> folders;
	
	public VimeoService() {
        String accessToken = "";
        
		vimeoResult = new VimeoResult(accessToken);

		folders = vimeoResult.getFoldersFromUser();
	}

	public Optional<List<Folder>> getFolders() {
		return folders;
	}

	public void setFolders(Optional<List<Folder>> folders) {
		this.folders = folders;
	}
	
	/**
	 * Returns the name of the video
	 * @param video the video
	 * @return the name of the video
	 */
	public String getVideoName(Video video) {
		return vimeoResult.getVideoName(video);
	}
	
	/**
	 * Returns the link of the video to reproduce it
	 * @param video the video
	 * @return the link of the video
	 */
	public String getVideoLink(Video video) {
		return vimeoResult.getVideoLink(video);
	}
	
	/**
	 * Returns the name of the folder
	 * @param folder the folder
	 * @return the name of the folder
	 */
	public String getFolderName(Folder folder) {
		return vimeoResult.getFolderName(folder);
	}

	public Optional<List<ProjectItem>> getItemsFromFolder(Folder folder) {
    	return vimeoResult.getItemsFromFolder(folder);
    }
    
    public Video getVideoFromItem(ProjectItem projectItem) {
    	return projectItem.getVideo();
    }
    
    public Folder getFolderFromItem(ProjectItem projectItem) {
    	return projectItem.getFolder();
    }
}
