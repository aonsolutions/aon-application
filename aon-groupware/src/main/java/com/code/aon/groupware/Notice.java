package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.NoticeDB;

@Entity
@Table(name="notice")
public class Notice extends NoticeDB {

	private static final long serialVersionUID = 1L;
	
}