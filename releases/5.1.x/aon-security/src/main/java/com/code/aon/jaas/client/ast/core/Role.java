package com.code.aon.jaas.client.ast.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IPermission;
import com.code.aon.jaas.client.ast.IRole;

/**
 * This class represents a Role declared in the web.xml file.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public class Role implements IRole {

    /** Role identifier. */
    private String id;

    /** Map of reloe permissions. */
    private Map<String, IPermission> permissions = new LinkedHashMap<String, IPermission>();

    /**
     * Create a <code>Role</code> instance.
     * 
     * @param id
     * @return
     */
    public static final IRole createInstance(String id) {
        Role role = new Role(id);
        return role;
    }

    /**
     * Constructor
     */
    public Role() {
    }

    /**
     * Constructor using the identifier.
     * 
     * @param id
     */
    public Role(String id) {
        this.id = id;
    }

    /**
     * Assign Role identifier.
     * 
     * @param string
     */
    public void setId(String string) {
        id = string;
    }

    /**
     * Add a role permission.
     * 
     * @param permission
     */
    public void addPermission(IPermission permission) {
        putPermission(permission.getId(), permission);
    }

    /**
     * Put a Role permission.
     * 
     * @param key
     *            String
     * @param permission
     *            IPermission
     */
    public void putPermission(String key, IPermission permission) {
        if (!permissions.containsKey(key)) {
            permissions.put(key, permission);
        }
    }

    /* (non-Javadoc)
     * 
     * @see com.code.aon.security.policy.ast.INode#getId()
     */
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * 
     * @see com.code.aon.security.policy.ast.IRole#getPermission(java.lang.String)
     */
    public IPermission getPermission(String key) {
        return (IPermission) permissions.get(key);
    }

    /* (non-Javadoc)
     * 
     * @see com.code.aon.security.policy.ast.IRole#getPermissions()
     */
    public Collection getPermissions() {
        return Collections.unmodifiableCollection(permissions.values());
    }

    /* (non-Javadoc)
     * 
     * @see com.code.aon.security.domain.ast.INode#accept(com.code.aon.security.domain.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException(); 
//        visitor.visitRole(this);
    }

}