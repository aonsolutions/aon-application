package com.esferalia.aon.occam.api.model.doc;

import java.net.MalformedURLException;
import java.net.URL;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IExternalStorageVisitor;

import solutions.aon.aws.s3.S3;
import solutions.aon.aws.s3.SCALEWAY;

public class ExternalDoc<T extends Enum<?>> extends Doc<T> {

	private String aonTable;
	private ExternalStorage externalStorage;
	private String s3Bucket;
	private String s3Key;
	private String driveId;
	
	public String getAonTable() {
		return aonTable;
	}
	
	public ExternalDoc<T> setAonTable(String aonTable) {
		this.aonTable = aonTable;
		return this;
	}
	
	public ExternalStorage getExternalStorage() {
		return externalStorage;
	}
	
	public ExternalDoc<T> setExternalStorage(ExternalStorage externalStorage) {
		this.externalStorage = externalStorage;
		return this;
	}
	
	public String getDriveId() {
		return driveId;
	}
	
	public ExternalDoc<T> setDriveId(String driveId) {
		this.driveId = driveId;
		return this;
	}
	
	public String getS3Bucket() {
		return s3Bucket;
	}
	
	public ExternalDoc<T> setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
		return this;
	}
	
	public String getS3Key() {
		return this.s3Key;
	}
	
	public ExternalDoc<T> setS3Key(String s3Key) {
		this.s3Key = s3Key;
		return this;
	}

	@Override
	public URL getDownloadURL() {
		return getDownloadURL(null);
	}
	
	@Override
	public URL getDownloadURL(String contentDisposition) {
		return getExternalStorage().visit(contentDisposition, new IExternalStorageVisitor<URL>() {

			@Override
			public URL visitAon(String contentDisposition) {
				URL url;
				try {
					url = new URL("");
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
				return url;
			}

			@Override
			public URL visitDrive(String contentDisposition) {
				URL url;
				try {
					url = new URL("");
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
				return url;
			}

			@Override
			public URL visitAws(String contentDisposition) {
				return getS3Bucket() != null
					? S3.getInstance().getDownloadURL(getS3Bucket(), getS3Key(), contentDisposition)
					: S3.getInstance().getAonTableDownloadURL(getAonTable(), getS3Key(), contentDisposition);
			}

			@Override
			public URL visitScaleway(String contentDisposition) {
				return getS3Bucket() != null
					? SCALEWAY.getInstance().getDownloadURL(getS3Bucket(), getS3Key(), contentDisposition)
					: SCALEWAY.getInstance().getAonTableDownloadURL(getAonTable(), getS3Key(), contentDisposition);
			}
			
		});
	}
	
}
