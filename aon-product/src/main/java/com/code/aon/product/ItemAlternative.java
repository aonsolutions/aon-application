package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.esferalia.aon.entity.master.ItemAlternativeDB;

@Entity
@Table(name="item_alternative", uniqueConstraints = @UniqueConstraint(columnNames={"item", "alternative_item"}))
public class ItemAlternative extends ItemAlternativeDB {
	
	private static final long serialVersionUID = 1L;
	
}