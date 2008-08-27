package com.code.aon.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the application.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "APPLICATION", schema = "AUDIT")
@SequenceGenerator(name="APPLICATION_GENERATOR", sequenceName="SEQ_APPLICATION",allocationSize=1)
public class Application implements ITransferObject {

	private static final long serialVersionUID = 3375874695541393974L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "APPLICATION_GENERATOR")
	@Column(name = "ID", nullable = false)
    private Integer id;

	@Column(name = "NAME", nullable = false, length = 64)
	@Index(name="IDX_APPLICATION_NAME")
    private String name;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="DOMAIN", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_APPLICATION_DOMAIN")
	private Domain domain;

    /**
     * The empty constructor.
     */
    public Application() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Application(Integer id) {
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

	/**
	 * Gets the domain.
	 * 
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * Sets the domain.
	 * 
	 * @param domain the new domain
	 */
	public void setDomain(Domain domain) {
		this.domain = domain;
	}

}