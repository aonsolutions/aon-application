package com.esferalia.aon.gwt.template.server;

import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductInfo implements IsSerializable{
	Product product;
	LinkedList<Tag> tagList;
	Vector<Item> item;
	Integer row;
	Boolean isProduct;
	Set<ProductTag> tags;
	Item downloadItem;

	public Integer getRow() {
		return row;
	}
	public ProductInfo setRow(Integer row) {
		this.row = row;
		return this;
	}
	public Boolean getIsProduct() {
		return isProduct;
	}
	public ProductInfo setIsProduct(Boolean isProduct) {
		this.isProduct = isProduct;
		return this;
	}
	public Product getProduct() {
		return product;
	}
	public ProductInfo setProduct(Product product) {
		this.product = product;
		return this;
	}
	public LinkedList<Tag> getTagList() {
		return tagList;
	}
	public ProductInfo setTagList(LinkedList<Tag> tagList) {
		this.tagList = tagList;
		return this;
	}
	public Vector<Item> getItem() {
		return item;
	}
	public ProductInfo setItem(Vector<Item> item) {
		this.item = item;
		return this;
	}
	public Set<ProductTag> getTags() {
		return tags;
	}
	public ProductInfo setTags(Set<ProductTag> tags) {
		this.tags = tags;
		return this;
	}
	public Item getDownloadItem() {
		return downloadItem;
	}
	public ProductInfo setDownloadItem(Item downloadItem) {
		this.downloadItem = downloadItem;
		return this;
	}

	
	
}
