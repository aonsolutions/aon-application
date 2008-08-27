package com.code.aon.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the domain.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "DOMAIN", schema = "AUDIT")
@SequenceGenerator(name="DOMAIN_GENERATOR", sequenceName="SEQ_DOMAIN",allocationSize=1)
public class Domain implements ITransferObject {

	private static final long serialVersionUID = -4791862077115749394L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DOMAIN_GENERATOR")
	@Column(name = "ID", nullable = false)
    private Integer id;

	@Column(name = "NAME", nullable = false, length = 64, unique = true)
	@Index(name="IDX_DOMAIN_NAME")
    private String name;

    /**
     * The empty constructor.
     */
    public Domain() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Domain(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
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