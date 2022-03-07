package com.code.aon.aio.service.drive;

import com.google.api.services.drive.model.File;

public class GFile {

	private String id;
	private String name;
	private MimeTypes type;
	private String downloadUrl;
	private String previewUrl;
	
	
	private GFile() {}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
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
	
	
	public static GFile from(File file) {
		
		final GFileBuilder builder = new GFileBuilder();
		
		builder.setId(file.getId())
			.setName(file.getName())
			.setType(MimeTypes.valueOfMime(file.getMimeType()))
			.setDownloadUrl(file.getWebContentLink())
			.setPreviewUrl(file.getWebViewLink())
			;
		
		return builder.build();
	}
	
	
	
	
	public static class GFileBuilder {
		
		private String id;
		private String name;
		private MimeTypes type;
		private String downloadUrl;
		private String previewUrl;
		
		
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
		
		
		public GFile build() {
			
			final GFile file = new GFile();
			
			file.id = this.id;
			file.name = this.name;
			file.type = this.type;
			file.downloadUrl = this.downloadUrl;
			file.previewUrl = this.previewUrl;
			
			return file;
		}
		
		
		
	}
	
}
