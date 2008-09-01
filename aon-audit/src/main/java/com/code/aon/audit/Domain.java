package com.code.aon.audit;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

	@Column(name = "ENABLE_AUDIT", nullable = false)
	private boolean enableAudit;
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "domain")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<User> users = new LinkedList<User>();	

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "domain")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<DomainApplication> applications = new LinkedList<DomainApplication>();	
	
    /**
     * The empty constructor.
     */
    public Domain() {
    	this.enableAudit = true;
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Domain(Integer id) {
    	this();
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
	 * Checks if is enable audit.
	 * 
	 * @return true, if is enable audit
	 */
	public boolean isEnableAudit() {
		return enableAudit;
	}

	/**
	 * Sets the enable audit.
	 * 
	 * @param enableAudit the new enable audit
	 */
	public void setEnableAudit(boolean enableAudit) {
		this.enableAudit = enableAudit;
	}
 
	/**
	 * Gets the users.
	 * 
	 * @return the users
	 */
	public List<User> getUsers() {
		return this.users;
	}
	
	/**
	 * Sets the users.
	 * 
	 * @param users the users
	 */
	public void setUsers( List<User> users ) {
		this.users = users;
	}
	
	/**
	 * Gets the applications.
	 * 
	 * @return the applications
	 */
	public List<DomainApplication> getApplications() {
		return this.applications;
	}
	
	/**
	 * Sets the applications.
	 * 
	 * @param applications the applications
	 */
	public void setApplications( List<DomainApplication> applications ) {
		this.applications = applications;
	}	
}