package com.code.aon.registry;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RegistryNoteDB;

@Entity
@Table(name="rnote")
public class RegistryNote extends RegistryNoteDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    private int SHORT_DESC_LENGTH = 45; 

	@Transient
	public String getShortComments() {
		if (getComments() != null && getComments().length()>SHORT_DESC_LENGTH)
			return getComments().substring(0,SHORT_DESC_LENGTH)+"...";
		return getComments();
	}

}