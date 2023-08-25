package com.code.aon.web.help.service.vimeo;

import com.vimeo.networking2.*;
import com.vimeo.networking2.enums.ProjectItemType;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class VimeoService {
	private VimeoResult vimeoResult;
	public boolean showVimeo = true;
	public boolean showDrive = true;
	public List<ProjectItem> mainItems;
	public Folder selectedFolder;
	
	public VimeoService() {
        String accessToken = "";
        
		vimeoResult = new VimeoResult(accessToken);

		Optional<List<Video>> videosWithoutParentFolder = Optional.of(new LinkedList<Video>());
		Optional<List<Video>> videos = vimeoResult.getVideosFromUser();
		
		if (videos.isPresent() && videos != null) {
			for (Video video : videos.get()) {
				if (video.getParentFolder() == null) {
					videosWithoutParentFolder.get().add(video);
				}
			}
		}

		Optional<List<Folder>> folders = vimeoResult.getFoldersFromUser();
		mainItems =new LinkedList<ProjectItem>();
		
		for (Folder folder : folders.get()) {
			ProjectItem projectItem = new ProjectItem(ProjectItemType.FOLDER.getValue(), folder, null);
			mainItems.add(projectItem);
		}
		
		for (Video video : videosWithoutParentFolder.get()) {
			ProjectItem projectItem = new ProjectItem(ProjectItemType.VIDEO.getValue(), null, video);
			mainItems.add(projectItem);
		}
	}

	public boolean isShowVimeo() {
		return showVimeo;
	}

	public void setShowVimeo(boolean showVimeo) {
		this.showVimeo = showVimeo;
	}
	
	public void hideVimeo() {
        showVimeo = false;
    }
	
	public boolean isShowDrive() {
		return showDrive;
	}

	public void setShowDrive(boolean showDrive) {
		this.showDrive = showDrive;
	}

	public List<ProjectItem> getMainItems() {
		return mainItems;
	}

	public void setMainItems(List<ProjectItem> mainItems) {
		this.mainItems = mainItems;
	}
	
	public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

	public Optional<List<ProjectItem>> getItemsFromFolder(Folder folder) {
    	return vimeoResult.getItemsFromFolder(folder);
    }
	
	
}
