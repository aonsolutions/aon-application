package com.code.aon.tas;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.ModelDB;

@Entity
@Table(name="model")
public class Model extends ModelDB {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getFullName() {
		return ((getMake() != null) ? getMake().getFullName() + " " : "") + ((getName() != null) ? getName() : "");
	}
	public void setFullName(String value) {
	}

}
