package com.code.aon.jaas.client.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRelation;

/**
 * This represents the relation between Roles, Profiles and USers.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-jun-2004
 * @since 1.0
 *  
 */
public class Relation implements IRelation {

	/**
	 * Determines if a de-serialized file is compatible with this class.
	 *
	 * Maintainers must change this value if and only if the new version
	 * of this class is not compatible with old versions. See Sun docs
	 * for details.
	 *
	 * Not necessary to include in first version of the class, but
	 * included here as a reminder of its importance.
	 */
	private static final long serialVersionUID = -3173483951646349924L;

	/**
     * Profile or User identifier.
     */
    private String id;

    /**
     * Profile or User relations. These can be Roles or Profiles.
     */
    private List<String> relations = new LinkedList<String>();

    /**
     * Constructor. 
     */
    public Relation() {
    }

    /**
     * Constructor specifying Profile or User identifier.
     * 
     * @param id String
     */
    public Relation(String id) {
        setId(id);
    }

    /**
     * Assign the identifier.
     * 
     * @param id
     */
    public void setId(String id) {
    	this.id = id;
    }

	/**
	 * @param relations The relations to set.
	 */
	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

	/**
     * Adds a relation, a Role or Profile.
     * 
     * @param name String
     */
    public void addRelation(String name) {
        if (!this.relations.contains(name)) {
        	this.relations.add(name);
        }
    }

    /**
     * Removes a relation, a Role or Profile.
     * 
     * @param name String
     */
    public void removeRelation(String name) {
    	this.relations.remove(name);
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.client.ast.INode#getId()
     */
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitRelation(this);
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.client.ast.IRelation#relations()
     */
    public List<String> relations() {
        return Collections.unmodifiableList(this.relations);
    }

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IRelation#getRelations()
	 */
	public String getRelations() {
		return this.relations.toString();
	}

}