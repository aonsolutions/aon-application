package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.RegistryMediaDB;

@Entity
@Table(name="rmedia")
public class RegistryMedia extends RegistryMediaDB {

	private static final long serialVersionUID = 1L;


}