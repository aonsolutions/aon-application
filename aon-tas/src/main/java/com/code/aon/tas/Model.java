package com.code.aon.tas;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ModelDB;

@Entity
@Table(name="model")
public class Model extends ModelDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getFullName() {
		return ((getMake() != null) ? getMake().getFullName() + " " : "") + ((getName() != null) ? getName() : "");
	}
	public void setFullName(String value) {
	}

}
