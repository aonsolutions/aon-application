package com.code.aon.commercial;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.CommercialActivityDB;

@Entity
@Table(name="commercial_activity")
public class CommercialActivity extends CommercialActivityDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}