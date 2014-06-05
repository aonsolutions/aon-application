package com.code.aon.google.apis;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;

	public class FileDownloadProgressListener implements MediaHttpDownloaderProgressListener {

		@Override
		public void progressChanged(MediaHttpDownloader downloader) {
			switch (downloader.getDownloadState()) {
			  case MEDIA_IN_PROGRESS:
				  DriveUtils.View.header2("Download is in progress: " + downloader.getProgress());
				break;
		      case MEDIA_COMPLETE:
		    	  DriveUtils.View.header2("Download is Complete!");
		        break;
		    }
		  }
		}