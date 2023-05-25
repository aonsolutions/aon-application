package com.code.aon.admin;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProfileDB;

@Entity
@Table(name="profile")
public class Profile extends ProfileDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<ProfileRole> roles = new HashSet<ProfileRole>();

	@OneToMany(mappedBy = "profile", cascade={CascadeType.REMOVE})
	public Set<ProfileRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<ProfileRole> roles) {
		this.roles = roles;
	}

}