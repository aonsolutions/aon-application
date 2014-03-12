package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.AllotmentItemDB;

@Entity
@Table(name="allotment_item")
public class AllotmentItem extends AllotmentItemDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
