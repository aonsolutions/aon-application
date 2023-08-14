package com.code.aon.web.help.service.vimeo;

import com.vimeo.networking2.*;
import com.vimeo.networking2.config.VimeoApiConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VimeoResult {

	private String clientId;
    private static VimeoApiClient vimeoApiClient;

    public VimeoResult(String clientSecret, String accessToken) {
		
		VimeoApiConfiguration configuration = new VimeoApiConfiguration.Builder(accessToken)
			.build();

		final Authenticator authenticator = Authenticator.create(configuration);
		this.vimeoApiClient = VimeoApiClient.create(configuration, authenticator);

		getUserId();
    }

	private void getUserId() {
        vimeoApiClient.fetchUser("https://api.vimeo.com/me", null, null, new VimeoCallback<User>() {
            @Override
            public void onSuccess(VimeoResponse.Success<User> response) {
                User user = response.getData();
                String userId = user.getIdentifier();
				clientId = userId;
            }

            @Override
            public void onError(VimeoResponse.Error errorResponse) {
                System.out.println("Can't get the user id: " + errorResponse.getMessage());
            }
        });
    }
    
    
    /**
     * Returns a list of the videos from the user
     * @return the list of videos
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
                System.out.println("Can't get the videos of the user: " + errorResponse.getMessage());
                future.completeExceptionally(new RuntimeException("Error fetching videos from user"));
            }
        });

        try {
            return Optional.ofNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error fetching videos from user: " + e.getMessage());
            return Optional.empty();
        }
    }

    
    /**
     * Returns the categories from all the videos from the user
     * @param videos the list of videos from the user
     * @return the list of categories
     */
    public Optional<List<Category>> getAllCategoriesFromUser(Optional<List<Video>> videos) {
		if (videos.isEmpty()) {
			return Optional.empty();
		}

    	List<Category> categories = new ArrayList<>();
		List<Video> videoList = videos.get();

    	
    	for (Video video : videoList) {
    		for (Category category : video.getCategories()) {
    			if (!categories.contains(category)) {
    				categories.add(category);
    			}
    		}
    	}
    	
    	return Optional.of(categories);
    }
	
    
    /**
     * Returns the categories of the given video
     * @param video the video
     * @return the categories of the video
     */
	public List<Category> getCategories(Video video) {
		return video.getCategories();
	}
	
	
	/**
	 * Get the subcategories of a category
	 * @param category the category that we want the subcategories from
	 * @return the subcategories as a list of categories
	 */
	public List<Category> getSubcategories(Category category) {
		return category.getSubcategories();
	}
	
	
	/**
	 * Returns the user's videos from that category or subcategory (both are the same type of object, Category)
	 * @param category the category that we want the videos from
	 * @param videoIds the list of the user's videos
	 * @return the list of the user's videos from that category
	 */
	public List<Video> getVideoListFromCategory(Category category, List<Video> videos) {
		List<Video> videosInCategories = new ArrayList<>();
		
		if (!videos.isEmpty()) {
			for (Video video : videos) {
				if (video.getCategories().contains(category)) {
					videosInCategories.add(video);
				}
			}
		}
		
		return videosInCategories;
	}
	
    /**
     * Returns a map with each category or subcategory (both are the same type of object, Category) and all its videos
     * @param videos the videos from the user
     * @param allCategories all the categories that are in all the videos from the user
     * @return a map with each category or subcategory and all its videos
     */
    public Map<Category, List<Video>> getAllVideosInAllCategories(List<Video> videos, List<Category> allCategories) {
    	Map<Category, List<Video>> videosInCategories = new HashMap<>();
    	
    	for (Category category : allCategories) {
    		videosInCategories.put(category, getVideoListFromCategory(category, videos));
    	}
    	
    	return videosInCategories;
    }
	
	
	/**
	 * Returns the html to reproduce that video
	 * @param video the video that we want to reproduce
	 * @return the html to reproduce that video
	 */
	public String playVideo(Video video) {
		String url = "https://player.vimeo.com/video/" + video.getUri();
		String html = "<iframe src=\"" + url + "\" width=\"640\" height=\"360\" frameborder=\"0\" allowfullscreen></iframe>";
		return html;
	}
	
	public String getVideoName(Video video) {
		return video.getName();
	}
	
	/**
	 * Returns the upload url for the thumbnail of the video
	 * @param video
	 * @return the link with the upload url
	 */
    public Optional<String> getVideoThumbnail(Video video) {
    	if (video != null && video.getPictures() != null) {
    		PictureCollection thumbnail = video.getPictures();
            return Optional.of(thumbnail.getLink());
        } else {
        	System.out.println("The video does not have a thumbnail.");
			return Optional.empty();
        }
    }
}
