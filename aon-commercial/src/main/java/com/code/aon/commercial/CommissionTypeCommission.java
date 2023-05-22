package com.code.aon.commercial;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CommissionTypeCommissionDB;

@Entity
@Table(name="commission_type_commission")
public class CommissionTypeCommission extends CommissionTypeCommissionDB  {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
