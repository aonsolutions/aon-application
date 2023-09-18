package com.code.aon.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ItemAlternativeDB;

@Entity
@Table(name="item_alternative", uniqueConstraints = @UniqueConstraint(columnNames={"item", "alternative_item"}))
@Heritable
public class ItemAlternative extends ItemAlternativeDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}