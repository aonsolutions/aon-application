package com.code.aon.registry;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.RegistryNoteDB;

@Entity
@Table(name="rnote")
public class RegistryNote extends RegistryNoteDB {
	
	private static final long serialVersionUID = 1L;
    private int SHORT_DESC_LENGTH = 45; 

	@Transient
	public String getShortComments() {
		if (getComments() != null && getComments().length()>SHORT_DESC_LENGTH)
			return getComments().substring(0,SHORT_DESC_LENGTH)+"...";
		return getComments();
	}

}