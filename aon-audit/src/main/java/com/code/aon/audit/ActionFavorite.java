package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ActionFavoriteDB;

@Entity
@Table(name="action_favorite")
public class ActionFavorite extends ActionFavoriteDB {

	private static final long serialVersionUID = 1L;
	
}
