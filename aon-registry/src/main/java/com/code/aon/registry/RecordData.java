package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.RecordDataDB;

@Entity
@Table(name="record_data")
public class RecordData extends RecordDataDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}