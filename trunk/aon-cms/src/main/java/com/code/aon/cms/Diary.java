package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "diary")
public class Diary implements ITransferObject {

	private Integer id;

	private boolean categories;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "is_categories", nullable = false)
	public boolean isCategories() {
		return categories;
	}

	public void setCategories(boolean categories) {
		this.categories = categories;
	}

}
