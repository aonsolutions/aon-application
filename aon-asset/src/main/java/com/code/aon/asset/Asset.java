package com.code.aon.asset;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AssetDB;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name="asset")
public class Asset extends AssetDB {

	private static final long serialVersionUID = 1L;
}
