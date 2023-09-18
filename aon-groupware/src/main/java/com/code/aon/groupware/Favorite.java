package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.FavoriteDB;

@Entity
@Table(name="favorite")
public class Favorite extends FavoriteDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}