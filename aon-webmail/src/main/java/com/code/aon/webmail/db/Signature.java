package com.code.aon.webmail.db;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.webmail.ISignature;
import com.esferalia.aon.entity.master.SignatureDB;

@Entity
@Table(name="signature")
public class Signature extends SignatureDB implements ISignature {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	
}