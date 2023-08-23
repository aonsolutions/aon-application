package com.code.aon.web.help.service.vimeo;

import com.vimeo.networking2.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class VimeoService {
	private VimeoResult vimeoResult;
	public Optional<List<Folder>> folders;
	public Optional<List<Video>> videosWithoutParentFolder;
	

	public VimeoService() {
        String accessToken = "";
        
		vimeoResult = new VimeoResult(accessToken);

		folders = vimeoResult.getFoldersFromUser();
		videosWithoutParentFolder = Optional.of(new LinkedList<Video>());
		
		Optional<List<Video>> videos = vimeoResult.getVideosFromUser();
		
		if (videos.isPresent() && videos != null) {
			for (Video video : videos.get()) {
				if (video.getParentFolder() == null) {
					videosWithoutParentFolder.get().add(video);
				}
			}
		}
	}

	public Optional<List<Folder>> getFolders() {
		return folders;
	}

	public void setFolders(Optional<List<Folder>> folders) {
		this.folders = folders;
	}
	
	public Optional<List<Video>> getVideosWithoutParentFolder() {
		return videosWithoutParentFolder;
	}

	public void setVideosWithoutParentFolder(Optional<List<Video>> videosWithoutParentFolder) {
		this.videosWithoutParentFolder = videosWithoutParentFolder;
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
