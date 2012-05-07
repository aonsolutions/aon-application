package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.PosDB;

@Entity
@Table(name="pos")
public class Pos extends PosDB {

	private static final long serialVersionUID = 1L;

}
