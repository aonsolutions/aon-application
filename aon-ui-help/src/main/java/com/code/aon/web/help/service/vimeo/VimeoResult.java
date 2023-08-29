package com.code.aon.web.help.service.vimeo;

import com.vimeo.networking2.*;
import com.vimeo.networking2.config.VimeoApiConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author mariawoodruff
 *
 */
public class VimeoResult {

    private static VimeoApiClient vimeoApiClient;

    public VimeoResult(String accessToken) {
		
		VimeoApiConfiguration configuration = new VimeoApiConfiguration.Builder(accessToken)
			.build();

		final Authenticator authenticator = Authenticator.create(configuration);
		VimeoResult.vimeoApiClient = VimeoApiClient.create(configuration, authenticator);
    }


	/**
	 * Returns all the folders that the user has in the main directory
	 * @return a list of folders
	 */
	public Optional<List<Folder>> getFoldersFromUser() {
        CompletableFuture<List<Folder>> future = new CompletableFuture<>();

        vimeoApiClient.fetchFolderList("https://api.vimeo.com/me/projects/", null, null, null, new VimeoCallback<FolderList>()  {
            @Override
            public void onSuccess(VimeoResponse.Success<FolderList> successResponse) {
                List<Folder> folders = successResponse.getData().getData();
                future.complete(folders);
            }

            @Override
            public void onError(VimeoResponse.Error errorResponse) {
                System.out.println("Can't get the folders of the user: " + errorResponse.getHttpStatusCode() + " " + errorResponse.getMessage());
                future.completeExceptionally(new RuntimeException());
            }
        });

        try {
            return Optional.ofNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

	/**
	 * Returns a list of the videos that belongs to the user
	 * @return a list of videos
	 */
	public Optional<List<Video>> getVideosFromUser() {
        CompletableFuture<List<Video>> future = new CompletableFuture<>();

        vimeoApiClient.fetchVideoList("https://api.vimeo.com/me/videos", null, null, null, new VimeoCallback<VideoList>()  {
            @Override
            public void onSuccess(VimeoResponse.Success<VideoList> successResponse) {
                List<Video> videos = successResponse.getData().getData();
                future.complete(videos);
            }

            @Override
            public void onError(VimeoResponse.Error errorResponse) {
                System.out.println("Can't get the folder's videos: " + errorResponse.getHttpStatusCode() + " " + errorResponse.getMessage());
                future.completeExceptionally(new RuntimeException());
            }
        });

        try {
            return Optional.ofNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }
	
	/**
	 * Returns the item of a folder
	 * @param folder the folder
	 * @return a list of items
	 */
	public Optional<List<ProjectItem>> getItemsFromFolder(Folder folder) {
		CompletableFuture<List<ProjectItem>> future = new CompletableFuture<>();

        vimeoApiClient.fetchProjectItemList("https://api.vimeo.com" + folder.getUri() + "/items/", null, null, null, new VimeoCallback<ProjectItemList>()  {
            @Override
            public void onSuccess(VimeoResponse.Success<ProjectItemList> successResponse) {
                List<ProjectItem> items = successResponse.getData().getData();
                List<ProjectItem> result = new LinkedList<>();
                
                for (ProjectItem projectItem : items) {
                	if (projectItem.getRawType().equals("folder")) {
                		result.add(projectItem);
                	}
                }
                
                future.complete(items);
            }

            @Override
            public void onError(VimeoResponse.Error errorResponse) {
                System.out.println("Can't get the items of the folder " + folder.getName() + ": " + errorResponse.getHttpStatusCode() + " " + errorResponse.getMessage());
                future.completeExceptionally(new RuntimeException());
            }
        });

        try {
            return Optional.ofNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
	}
	
}
