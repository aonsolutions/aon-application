package com.esferalia.aon.occam.api.model.warehouse;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.product.Item;

public class Packaging {
	
	Item base;
	Item item;
	Item container;
	List<Item> containers;
	Integer copies;
	
	double quantity;
	
	public Item getBase() {
		return base;
	}
	
	public Packaging setBase(Item base) {
		this.base = base;
		return this;
	}
	
	public Item getItem() {
		if(item == null)
			item = new Item();
		return item;
	}
	
	public Packaging setItem(Item item) {
		this.item = item;
		return this;
	}
	
	public Item getContainer() {
		if(container == null)
			container = new Item();
		return container;
	}
	
	public Packaging setContainer(Item container) {
		this.container = container;
		return this;
	}
	
	public List<Item> getContainers() {
		if(containers == null)
			containers = new LinkedList<>();
		return containers;
	}

	public Packaging setContainers(List<Item> containers) {
		this.containers = containers;
		return this;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public Packaging setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public Integer getCopies() {
		return copies;
	}
	
	public Packaging setCopies(Integer copies) {
		this.copies = copies;
		return this;
	}
	
	public Packaging copy() {
		return new Packaging()
			.setBase(getBase())
			.setContainer(getContainer())
			.setContainers(getContainers())
			.setCopies(getCopies())
			.setItem(getItem())
			.setQuantity(getQuantity());
	}
}
