package com.esferalia.aon.carrier;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.seller.enumeration.SellerStatus;
import com.esferalia.aon.entity.master.CarrierDB;
import com.esferalia.aon.entity.master.SellerDB;

@Entity
@Table(name="carrier")
@PrimaryKeyJoinColumn(name="registry")
public class Carrier extends CarrierDB implements IRegistry, IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
