package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.NoticeDB;

@Entity
@Table(name="notice")
public class Notice extends NoticeDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}