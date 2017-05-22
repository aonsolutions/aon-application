package com.code.aon.data;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.DataResponseDB;

@Entity
@Table(name="data_response")
@Heritable(force=true)
public class DataResponse extends DataResponseDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}