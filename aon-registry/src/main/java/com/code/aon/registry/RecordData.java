package com.code.aon.registry;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RecordDataDB;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="record_data")
@AttributeOverride(name="type0Unique", 
                   column=@Column(name="type_0_unique", insertable=false, updatable=false))
public class RecordData extends RecordDataDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}