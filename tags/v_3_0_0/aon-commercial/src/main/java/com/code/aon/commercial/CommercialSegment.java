package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the Commercial Segment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 18-jul-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="commercial_segment")
public class CommercialSegment implements ITransferObject {

	private static final long serialVersionUID = 9206472703813079907L;

	/** The id. */
    private Integer id;

    /** The name. */
    private String name;

    /**
     * The empty constructor.
     */
    public CommercialSegment() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public CommercialSegment(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @Column(length=32, nullable = false)
	public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

}