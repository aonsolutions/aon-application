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

	@Column(name = "NAME", nullable = false, length = 64, unique = true)
	@Index(name="IDX_APPLICATION_NAME")
    private String name;
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "application")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<Action> actions = new LinkedList<Action>();
	
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
	 * Gets the actions.
	 * 
	 * @return the actions
	 */
	public List<Action> getActions() {
		return this.actions;
	}
	
	/**
	 * Sets the actions.
	 * 
	 * @param addresses the actions
	 */
	public void setActions( List<Action> actions ) {
		this.actions = actions;
	}
    
}