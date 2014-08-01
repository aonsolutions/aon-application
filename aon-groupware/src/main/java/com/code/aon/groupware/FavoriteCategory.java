package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.FavoriteCategoryDB;

@Entity
@Table(name="favorite_category")
public class FavoriteCategory extends FavoriteCategoryDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}