package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.RelationshipDB;

@Entity
@Table(name="relationship")
@Heritable
public class Relationship extends RelationshipDB{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

   
}