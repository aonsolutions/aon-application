package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.registry.Category;

public class S3Category extends Category {

	private static final long serialVersionUID = 4674846006716485119L;
	
	Integer parent;
	byte isVisible;
	byte isDeletable;
	
	public byte getIsVisible() {
		return isVisible;
	}

	public S3Category setIsVisible(byte isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	public byte getIsDeletable() {
		return isDeletable;
	}

	public S3Category setIsDeletable(byte isDeletable) {
		this.isDeletable = isDeletable;
		return this;
	}

	public Integer getParent() {
		return parent;
	}

	public S3Category setParent(Integer parent) {
		this.parent = parent;
		return this;
	}

	public S3Category(){
		super();
	}
	
	@Override
	public S3Category setDescription(String description) {
		super.setDescription(description);
		return this;
	}

	@Override
	public S3Category setDomain(Integer domain) {
		super.setDomain(domain);
		return this;
	}

	@Override
	public S3Category setId(Integer id) {
		super.setId(id);
		return this;
	}

	@Override
	public S3Category setRattach(Integer rattach) {
		super.setRattach(rattach);
		return this;
	}

	@Override
	public S3Category setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public S3Category setScope(Integer scope) {
		super.setScope(scope);
		return this;
	}

	@Override
	public S3Category setType(Byte type) {
		super.setType(type);
		return this;
	}

	@Override
	public S3Category setUrl(String url) {
		super.setUrl(url);
		return this;
	}
}
