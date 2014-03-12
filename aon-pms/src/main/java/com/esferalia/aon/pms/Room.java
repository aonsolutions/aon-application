package com.esferalia.aon.pms;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.code.aon.asset.AssetFeature;
import com.code.aon.asset.IAsset;
import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.RoomDB;

@Entity
@Table(name="room")
public class Room extends RoomDB implements IAsset{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<AssetFeature> features = new HashSet<AssetFeature>();

	public Room() {
		setActive(true);
	}

	@OneToMany(mappedBy = "asset", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<AssetFeature> getFeatures() {
		return this.features;
	}
	public void setFeatures(Set<AssetFeature> features) {
		this.features = features;
	}

}
