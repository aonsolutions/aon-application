package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.SegmentDB;

@Entity
@Table(name="tag")
public class Tag extends SegmentDB {

	private static final long serialVersionUID = 1L;
	
}