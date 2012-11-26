package com.code.aon.warehouse;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.esferalia.aon.entity.master.StockDB;

@Entity
@Table(name="stock", uniqueConstraints = @UniqueConstraint(columnNames={"warehouse", "item"}))
public class Stock extends StockDB {
	
	private static final long serialVersionUID = 1L;

	// Utilizado en los listados valorados.
	@Transient
	public Stock getTo() {
		return this;
	}
	
}