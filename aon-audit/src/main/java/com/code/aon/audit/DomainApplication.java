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

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the domain application.
 * 
 * @author Consulting & Development. Aimar Tellitu - 28-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "DOMAIN_APPLICATION", schema = "AUDIT")
@SequenceGenerator(name="DOMAIN_APPLICATION_GENERATOR", sequenceName="SEQ_DOMAIN_APPLICATION",allocationSize=1)
public class DomainApplication implements ITransferObject {

	private static final long serialVersionUID = 487088709544792491L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DOMAIN_APPLICATION_GENERATOR")
	@Column(name = "ID", nullable = false)
    private Integer id;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="DOMAIN", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_DOMAIN_APPLICATION_DOMAIN")
	private Domain domain;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="APPLICATION", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_DOMAIN_APPLICATION_APPLICATION")
	private Application application;
	
	@Column(name = "AUDIT_LEVEL", nullable = false)
	private AuditLevel auditLevel;	
	
    /**
     * The empty constructor.
     */
    public DomainApplication() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public DomainApplication(Integer id) {
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

	/**
	 * Gets the application.
	 * 
	 * @return the application
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Sets the application.
	 * 
	 * @param application the new application
	 */
	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * Gets the audit level.
	 * 
	 * @return the audit level
	 */
	public AuditLevel getAuditLevel() {
		return auditLevel;
	}

	/**
	 * Sets the audit level.
	 * 
	 * @param auditLevel the new audit level
	 */
	public void setAuditLevel(AuditLevel auditLevel) {
		this.auditLevel = auditLevel;
	}
	
}