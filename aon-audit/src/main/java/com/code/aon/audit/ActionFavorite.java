package com.code.aon.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ActionFavoriteDB;

@Entity
@Table(name="action_favorite")
public class ActionFavorite extends ActionFavoriteDB implements IAction {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
