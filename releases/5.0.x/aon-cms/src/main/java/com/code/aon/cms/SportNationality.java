package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "sport_nationality")
public class SportNationality implements ITransferObject {

	private Integer id;

	private String alias;

	private String image;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false,length=32)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Column(nullable=false,length=128)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
