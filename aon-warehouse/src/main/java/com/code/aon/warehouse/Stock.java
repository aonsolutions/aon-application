package com.code.aon.warehouse;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.StockDB;

@Entity
@Table(name="stock", uniqueConstraints = @UniqueConstraint(columnNames={"warehouse", "item"}))
public class Stock extends StockDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	// Utilizado en los listados valorados.
	@Transient
	public Stock getTo() {
		return this;
	}
	
}