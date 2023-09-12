package com.code.aon.web.help.service.vimeo;

import com.vimeo.networking2.*;
import com.vimeo.networking2.enums.ProjectItemType;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mariawoodruff
 *
 */
public class VimeoService {
	private VimeoResult vimeoResult;
	public boolean showVimeo = true;
	public boolean showDrive = true;
	public List<ProjectItem> mainItems;
	public List<Video> videos;
	public ProjectItem selectedItem;
	public String driveFolderName;
	
	public VimeoService() {
        String accessToken = "";
        
		vimeoResult = new VimeoResult(accessToken);

		Optional<List<Video>> videosWithoutParentFolder = Optional.of(new LinkedList<Video>());
		
		// .orElse()
		videos = vimeoResult.getVideosFromUser().orElse(new LinkedList<Video>());
		
		
		for (Video video : videos) {
			if (video.getParentFolder() == null) {
				if (videosWithoutParentFolder.isPresent()) {
					videosWithoutParentFolder.get().add(video);
				}		
			}
		}

		Optional<List<Folder>> folders = vimeoResult.getFoldersFromUser();
		mainItems = new LinkedList<ProjectItem>();
		
		for (Folder folder : folders.orElse(new LinkedList<Folder>())) {
			ProjectItem projectItem = new ProjectItem(ProjectItemType.FOLDER.getValue(), folder, null);
			mainItems.add(projectItem);
		}
		
		for (Video video : videosWithoutParentFolder.orElse(new LinkedList<Video>())) {
			ProjectItem projectItem = new ProjectItem(ProjectItemType.VIDEO.getValue(), null, video);
			mainItems.add(projectItem);
		}
	}

	/**
	 * Getter of showVimeo
	 * @return showVimeo
	 */
	public boolean isShowVimeo() {
		return showVimeo;
	}

	/**
	 * Setter of showVimeo
	 * @param showVimeo
	 */
	public void setShowVimeo(boolean showVimeo) {
		this.showVimeo = showVimeo;
	}
	
	/**
	 * Getter of showDrive
	 * @return showDrive
	 */
	public boolean isShowDrive() {
		return showDrive;
	}

	/**
	 * Setter of showDrive
	 * @param showDrive
	 */
	public void setShowDrive(boolean showDrive) {
		this.showDrive = showDrive;
	}

	/**
	 * Getter of mainItems
	 * @return mainItems
	 */
	public List<ProjectItem> getMainItems() {
		return mainItems;
	}

	/**
	 * Setter of mainItems
	 * @param mainItems
	 */
	public void setMainItems(List<ProjectItem> mainItems) {
		this.mainItems = mainItems;
	}

	/**
	 * Getter of selectedItem
	 * @return selectedItem
	 */
	public ProjectItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * Setter of selectedItem
	 * @param selectedItem
	 */
	public void setSelectedItem(ProjectItem selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	/**
	 * Get the items of a folder
	 * @param folder the folder
	 * @return a list of items
	 */
	public List<ProjectItem> getItemsFromFolder(Folder folder) {
    	return vimeoResult.getItemsFromFolder(folder).get();
    }
	
	public int countVideos(Folder folder) {
		Optional<List<ProjectItem>> videos = vimeoResult.getItemsFromFolder(folder);
		
		int numberOfVideos = 0;
		
		if (videos.isPresent()) {
			for (ProjectItem projectItem : videos.get()) {
				if (projectItem.getRawType().equals("video"))
					numberOfVideos++;
			}
		}
		
		return numberOfVideos;
	}
}
