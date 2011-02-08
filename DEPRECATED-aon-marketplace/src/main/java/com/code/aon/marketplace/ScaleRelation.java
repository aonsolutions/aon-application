package com.code.aon.marketplace;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.marketplace.enumeration.ScaleModel;

@Entity
@Table(name="scale_relation")
public class ScaleRelation implements ITransferObject {

	/**
	 * Id.
	 */
	private Integer id;

	/**
	 * Aon Id.
	 */
	private Integer aon_id;

	/**
	 * Scale Id 1.
	 */
	private String scale_id1;

	/**
	 * Scale Id 2.
	 */
	private String scale_id2;

	/**
	 * Data Type
	 */
	private String type;

	/**
	 * Scale Model
	 */
	private ScaleModel scaleModel;

	/**
	 * Constructor
	 */
	public ScaleRelation() {
	}

    @Id
    @GeneratedValue
    @Column(nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAon_id() {
		return aon_id;
	}

	public void setAon_id(Integer aon_id) {
		this.aon_id = aon_id;
	}

	public String getScale_id1() {
		return scale_id1;
	}

	public void setScale_id1(String scale_id1) {
		this.scale_id1 = scale_id1;
	}

	public String getScale_id2() {
		return scale_id2;
	}

	public void setScale_id2(String scale_id2) {
		this.scale_id2 = scale_id2;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name="scale_model")
	public ScaleModel getScaleModel() {
		return scaleModel;
	}

	public void setScaleModel(ScaleModel scaleModel) {
		this.scaleModel = scaleModel;
	}

}
