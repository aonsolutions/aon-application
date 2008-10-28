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
 * Transfer Object that represents the user.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "USER", schema = "AUDIT")
@SequenceGenerator(name="USER_GENERATOR", sequenceName="SEQ_USER",allocationSize=1)
public class User implements ITransferObject {

	private static final long serialVersionUID = 4697467776631297515L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "USER_GENERATOR")
	@Column(name = "ID", nullable = false)
    private Integer id;

	@Column(name = "LOGIN", nullable = false, length = 16)
	@Index(name="IDX_USER_LOGIN")
    private String login;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="DOMAIN", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_USER_DOMAIN")
	private Domain domain;

    /**
     * The empty constructor.
     */
    public User() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public User(Integer id) {
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
     * Gets the login.
     * 
     * @return the login
     */
	public String getLogin() {
        return login;
    }

    /**
     * Sets the login.
     * 
     * @param name the login
     */
    public void setLogin(String login) {
        this.login = login;
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