package com.code.aon.tas;


import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.tas.dao.ITASAlias;

/**
 * Transfer Object that represents an entity of a Tas Item.
 * 
 * @author Consulting & Development. Inigo Gayarre - 30-ago-2006
 * @since 1.0
 * @version 1.0
 *  
 */

@Entity
@Table(name = "tas_item")
public class TasItem implements ITransferObject, ILookupObject{

	/**
	 * Unique key
	 */
	private Integer id;

	/**
	 * The model of the tas item
	 */
	private Model model;

	/**
	 * The public code of the tas item
	 */
	private String publicCode;

	/**
	 * The private code of the tas item
	 */
	private String privateCode;

	/**
	 * The description code of the tas item
	 */
	private String description;
	
	/**
	 * Additional info
	 */
	private String addInfo;

	/**
	 * Void constructor
	 */
	public TasItem() {
	}

	/**
	 * Constructor with a concrete key
	 * 
	 * @param id
	 */
	public TasItem(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the unique key.
	 * 
	 * @return unique key.
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}

	/**
	 * Assigns the unique key.
	 * 
	 * @param id
	 *            unique key.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the model
	 * 
	 * @return model
	 */
	@ManyToOne
	@JoinColumn(name = "model", nullable = false)
	public Model getModel() {
		return this.model;
	}

	/**
	 * Assigns the model
	 * 
	 * @param model
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * Returns the public code
	 * 
	 * @return public code
	 */
	@Column(length = 25, nullable = false)
	public String getPublicCode() {
		return this.publicCode;
	}

	/**
	 * Assigns the public code
	 * 
	 * @param publicCode
	 */
	public void setPublicCode(String publicCode) {
		this.publicCode = publicCode;
	}

	/**
	 * Return the private code
	 * 
	 * @return private code
	 */
	@Column(length = 25)
	public String getPrivateCode() {
		return this.privateCode;
	}

	/**
	 * Assigns the private code
	 * 
	 * @param privateCode
	 */
	public void setPrivateCode(String privateCode) {
		this.privateCode = privateCode;
	}

	/**
	 * Returns the description
	 * 
	 * @return description
	 */
	@Column(length = 255, nullable = false)
	public String getDescription() {
		return this.description;
	}

	/**
	 * Assigns the description
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the additional info
	 * 
	 * @return the additional info
	 */
	@Column(name="add_info", length=255)
	public String getAddInfo() {
		return addInfo;
	}

	/**
	 * Assigns the additional info
	 * 
	 * @param addInfo
	 */
	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("id").append("='").append(getId()).append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.common.ILookupObject#getLookups()
	 */
	@Transient
	public Map<String, Object> getLookups() {
		Map<String,Object> map = new HashMap<String,Object>();
        map.put(ITASAlias.TAS_ITEM_ID, getId());
        map.put(ITASAlias.TAS_ITEM_PRIVATE_CODE, getPrivateCode());
        map.put(ITASAlias.TAS_ITEM_PUBLIC_CODE, getPublicCode());
        map.put(ITASAlias.TAS_ITEM_DESCRIPTION, getDescription());
        map.put(ITASAlias.TAS_ITEM_MODEL_ID, getModel().getId());
        map.put(ITASAlias.MODEL_ID, getModel().getId());
        map.put(ITASAlias.MODEL_NAME, getModel().getName());
        map.put(ITASAlias.MAKE_NAME, getModel().getMake().getName());
        return map;
	}

}
