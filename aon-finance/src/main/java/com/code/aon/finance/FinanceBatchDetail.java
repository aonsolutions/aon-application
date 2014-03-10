package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.FinanceBatchDetailDB;

@Entity
@Table(name="fbatch_detail")
public class FinanceBatchDetail extends FinanceBatchDetailDB {

    private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}