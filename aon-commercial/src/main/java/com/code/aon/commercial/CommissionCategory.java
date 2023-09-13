package com.code.aon.commercial;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CommissionCategoryDB;

@Entity
@Table(name="commission_category")
public class CommissionCategory extends CommissionCategoryDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
