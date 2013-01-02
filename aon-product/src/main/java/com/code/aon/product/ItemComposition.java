package com.code.aon.product;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ItemCompositionDB;

@Entity
@Table(name="item_composition", uniqueConstraints = @UniqueConstraint(columnNames={"item", "composition_item"}))
@Heritable
public class ItemComposition extends ItemCompositionDB {
	
	private static final long serialVersionUID = 1L;

}