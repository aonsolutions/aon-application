package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RecordDataDB;

@Entity
@Table(name="record_data")
public class RecordData extends RecordDataDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}