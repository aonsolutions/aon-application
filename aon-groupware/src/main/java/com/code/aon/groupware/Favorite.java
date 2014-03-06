package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.FavoriteDB;

@Entity
@Table(name="favorite")
public class Favorite extends FavoriteDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}