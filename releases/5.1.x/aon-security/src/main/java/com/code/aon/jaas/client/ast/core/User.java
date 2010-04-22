package com.code.aon.jaas.client.ast.core;

import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IUser;

/**
 * Usuario de la aplicación.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 02-jun-2004
 * @since 1.0
 *  
 */
public class User implements IUser {

    /**
     * Indica el identificador, nombre y la descripcion del usuario.
     */
    private String id, name, description;

    /**
     * Indica la contraseña del usuario.
     */
    private String passwd;

    /**
     * Constructor
     */
    public User() {
    }

    /**
     * Constructor con parámetros
     * 
     * @param id String
     * @param name String
     * @param description String
     */
    public User(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Asigna el identificador
     * 
     * @param string
     */
    public void setId(String string) {
        id = string;
    }

    /**
     * Asigna el nombre
     * 
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    /**
     * Asigna la descripción
     * 
     * @param string
     */
    public void setDescription(String string) {
        description = string;
    }

    /**
     * Asigna la contraseña
     * 
     * @param string
     */
    public void setPasswd(String string) {
        passwd = string;
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.policy.ast.IUser#getId()
     */
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.policy.ast.INode#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.policy.ast.IUser#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.policy.ast.IUser#getPasswd()
     */
    public String getPasswd() {
        return passwd;
    }

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IUser#changePasswd(java.lang.String)
	 */
	public void changePasswd(String passwd) {
		setPasswd(passwd);
	}

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.policy.ast.INode#accept(com.code.aon.security.policy.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitUser(this);
    }

    /**
     * Create an instance of this class using the values passed by parameter.
     * 
     * @param id
     * @param name
     * @param description
     * @param password
     * @return
     */
    public static final IUser getInstance(String id, String name, String description, String password) {
    	User user = new User();
    	user.setId( id );
    	user.setName( name );
    	user.setDescription( description );
    	user.setPasswd( password);
    	return user;
    }
}