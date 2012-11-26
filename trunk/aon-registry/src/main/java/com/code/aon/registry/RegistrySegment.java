package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.RegistrySegmentDB;

@Entity
@Table(name="rsegment")
public class RegistrySegment extends RegistrySegmentDB {
	
	private static final long serialVersionUID = 1L;

}