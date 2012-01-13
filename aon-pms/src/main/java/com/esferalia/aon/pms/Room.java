package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.asset.IAsset;
import com.esferalia.aon.entity.master.RoomDB;

@Entity
@Table(name="room")
public class Room extends RoomDB implements IAsset{

	private static final long serialVersionUID = 1L;

}
