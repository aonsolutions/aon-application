package com.code.aon.web.help.service.drive;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.model.File;

public class GFile implements Serializable {

	private String id;
	private String name;
	private MimeTypes type;
	private String downloadUrl;
	private String previewUrl;
	private List<String> parents;
	
	private GFile() {}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public MimeTypes getType() {
		return type;
	}
	
	public String getDownloadUrl() {
		return downloadUrl;
	}
	
	public String getPreviewUrl() {
		return previewUrl;
	}
	
	public List<String> getParents() {
		return parents;
	}
	
	
	
	public boolean isPDF() {
		return this.type == MimeTypes.PDF;
	}
	
	public boolean isVideo() {
		return this.type == MimeTypes.MP4;
	}
	
	public boolean isFolder() {
		return this.type == MimeTypes.FOLDER;
	}
	
	public boolean isFaqFolder() {
		return AonStringUtils.equalsIgnoreCase("FAQs", name);
	}
	
	
	public String getExtensionlessName(){
		
		String temp = name;
			
		if(this.isPDF()) {
			temp = this.name.substring(0,name.indexOf(".pdf"));
		}
		
		if(this.isVideo()) {
			temp = this.name.substring(0,name.indexOf(".mp4"));
		}
		
		
		return temp;
	}
	
	
	public static GFile from(File file) {
		
		final GFileBuilder builder = new GFileBuilder();
		
		if(file == null)
			return null;

		builder.setId(file.getId())
			.setName(file.getName())
			.setType(MimeTypes.valueOfMime(file.getMimeType()))
			.setDownloadUrl(file.getWebContentLink())
			.setPreviewUrl(file.getWebViewLink())
			.setParents(file.getParents());
			;
		
		return builder.build();
	}	
	
	public static class GFileBuilder {
		
		private String id;
		private String name;
		private MimeTypes type;
		private String downloadUrl;
		private String previewUrl;
		private List<String> parents;
		
		
		public GFileBuilder() {			
		}
		
		public GFileBuilder setId(String id) {
			this.id = id;
			return this;
		}
		
		public GFileBuilder setName(String name) {
			this.name = name;
			return this;
		}
		
		public GFileBuilder setType(MimeTypes type) {
			this.type = type;
			return this;
		}
		
		public GFileBuilder setDownloadUrl(String downloadUrl) {
			this.downloadUrl = downloadUrl;
			return this;
		}
		
		public GFileBuilder setPreviewUrl(String previewUrl) {
			this.previewUrl = previewUrl;
			return this;
		}
		
		public void setParents(List<String> parents) {
			
			if(parents == null) {
				this.parents = new LinkedList<>();
			} else {
				this.parents = parents;
			}
		}
		
		public GFile build() {
			
			final GFile file = new GFile();
			
			file.id = this.id;
			file.name = this.name;
			file.type = this.type;
			file.downloadUrl = this.downloadUrl;
			file.previewUrl = this.previewUrl;
			file.parents = this.parents;
			
			return file;
		}
		

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GFileBuilder other = (GFileBuilder) obj;
			return id == other.id;
		}

	}
	
}
