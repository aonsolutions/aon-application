/**
 * 
 */
package com.code.aon.planner.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="calendar")
public class Calendar implements ITransferObject {
	
	private Integer id;
	
	private String description;

	private boolean addSpreadEventAllowed;

    private byte[] data;
    
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the addSpreadEventAllowed
	 */
	@Column(name="allow_spread")
	public boolean isAddSpreadEventAllowed() {
		return addSpreadEventAllowed;
	}

	/**
	 * @param addSpreadEventAllowed the addSpreadEventAllowed to set
	 */
	public void setAddSpreadEventAllowed(boolean addSpreadEventAllowed) {
		this.addSpreadEventAllowed = addSpreadEventAllowed;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
        return data;
    }

	/**
	 * @param data
	 */
    public void setData(byte[] data) {
        this.data = data;
    }
}
