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
	public void setRow(Integer row) {
		this.row = row;
	}
	public Boolean getIsProduct() {
		return isProduct;
	}
	public void setIsProduct(Boolean isProduct) {
		this.isProduct = isProduct;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public LinkedList<Tag> getTagList() {
		return tagList;
	}
	public void setTagList(LinkedList<Tag> tagList) {
		this.tagList = tagList;
	}
	public Vector<Item> getItem() {
		return item;
	}
	public void setItem(Vector<Item> item) {
		this.item = item;
	}
	public Set<ProductTag> getTags() {
		return tags;
	}
	public void setTags(Set<ProductTag> tags) {
		this.tags = tags;
	}
	public Item getDownloadItem() {
		return downloadItem;
	}
	public void setDownloadItem(Item downloadItem) {
		this.downloadItem = downloadItem;
	}

	
	
}
