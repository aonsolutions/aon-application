package com.esferalia.aon.occam.api.model.doc;

import java.net.URL;

import solutions.aon.aws.s3.S3;

public class S3Doc<T extends Enum<?>> extends Doc<T> {

	private String aonTable;
	private String s3Bucket;
	private String s3Key;
	
	public String getS3Bucket() {
		return s3Bucket;
	}
	
	public S3Doc<T> setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
		return this;
	}
	
	public String getS3Key() {
		return this.s3Key;
	}
	
	public S3Doc<T> setS3Key(String s3Key) {
		this.s3Key = s3Key;
		return this;
	}

	@Override
	public URL getDownloadURL() {
		return s3Bucket != null
			? S3.getDownloadURL(s3Bucket, s3Key)
			: S3.getAonTableDownloadURL(aonTable, s3Key);
	}
	
	@Override
	public URL getDownloadURL(String contentDisposition) {
		return s3Bucket != null
			? S3.getDownloadURL(s3Bucket, s3Key, contentDisposition)
			: S3.getAonTableDownloadURL(aonTable, s3Key, contentDisposition);
	}
	
}
