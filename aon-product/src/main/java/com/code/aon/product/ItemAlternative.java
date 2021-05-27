package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ItemAlternativeDB;

@Entity
@Table(name="item_alternative", uniqueConstraints = @UniqueConstraint(columnNames={"item", "alternative_item"}))
@Heritable
public class ItemAlternative extends ItemAlternativeDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}