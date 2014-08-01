package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.registry.IRegistry;
import com.esferalia.aon.entity.master.TrainingCenterDB;

@Entity
@Table(name="training_center")
@PrimaryKeyJoinColumn(name="registry")
@Heritable
public class TrainingCenter extends TrainingCenterDB implements IRegistry {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
		
}
