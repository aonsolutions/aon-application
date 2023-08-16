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

public class VimeoService {
	private VimeoResult vimeoResult;
	public Optional<List<Video>> videos;
	public Optional<List<Category>> categories;
	public String hola = "hola que tal";
	
	public VimeoService() {
		String clientSecret = "cLTggCPyStdkgr4coTTxDgLG7yy5A3rkbL2noL6pOZbWScURjEoTXekmHcHT6hbYKyYZ9kNwr469pl1jnTKK4DmKxWfLZe37mgJwQI6skdATU0ddoGKsbv/POxSXYId+";
        String accessToken = "e04d9400ead6905f0053efac004bc451";
        
		vimeoResult = new VimeoResult(clientSecret, accessToken);
		
		videos = vimeoResult.getVideosFromUser();
		categories = vimeoResult.getAllCategoriesFromUser(videos);
		
	}
	
	// Getters and setters
    public String getHola() {
        return hola;
    }

    public void setHola(String hola) {
        this.hola = hola;
    }
    
    public Optional<List<Video>> getVideos() {
		return videos;
	}

	public void setVideos(Optional<List<Video>> videos) {
		this.videos = videos;
	}

	public Optional<List<Category>> getCategories() {
		return categories;
	}

	public void setCategories(Optional<List<Category>> categories) {
		this.categories = categories;
	}
	
	public boolean hasVideos() {
		if (videos.isPresent())
			return true;
		else
			return false;
	}
	
	public boolean hasCategories() {
		if (categories.isPresent())
			return true;
		else
			return false;
	}
	
	/**
	 * Returns the a map with each category or subcategory and it's lists of videos
	 * @return the map
	 */
	public Optional<Map<Category, List<Video>>> getAllVideosInAllCategories() {
        if (videos.isPresent() && categories.isPresent()) {
           return Optional.of(vimeoResult.getAllVideosInAllCategories(videos.get(), categories.get()));
        } else {
            return Optional.empty();
        }
	}
	
	/**
	 * Returns the subcategories of a category
	 * @param category the category 
	 * @return a list of categories, the categories and subcategories are the same type of object
	 */
	public Optional<List<Category>> getSubcategories(Category category) {
		List<Category> subcategories = vimeoResult.getSubcategories(category);
		
		if (subcategories.isEmpty())
			return Optional.empty();
		else
			return Optional.of(subcategories);
	}
	
	public String playVideo(Video video) {
		return vimeoResult.playVideo(video);
	}
	
	public String getVideoName(Video video) {
		return vimeoResult.getVideoName(video);
	}
	
	public String getVideoLink(Video video) {
		return video.getLink();
	}
}
