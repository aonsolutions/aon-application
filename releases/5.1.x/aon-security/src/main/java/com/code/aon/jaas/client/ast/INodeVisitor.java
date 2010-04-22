package com.code.aon.jaas.client.ast;

import com.code.aon.jaas.storage.ApplicationsStorage;
import com.code.aon.jaas.storage.ConfigurationStorage;
import com.code.aon.jaas.storage.DomainStorage;

/**
 * Interfaz visitante de los nodos definidos en el módulo de seguridad.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public interface INodeVisitor {

    /**
     * Visita el nodo que genera el fichero de contexto.
     * 
     * @param storage ConfigurationStorage
     */
    void visitConfiguration(ConfigurationStorage storage);

    /**
     * Visita el nodo <code>ApplicationsStorage</code>, donde estan las aplicaciones desplegadas 
     * para el módulo de seguridad.
     * 
     * @param storage ApplicationsStorage
     */
    void visitDeployed(ApplicationsStorage storage);

    /**
     * Visita el nodo <code>DomainStorage</code>, donde esta la entidad desplegada 
     * para el módulo de seguridad.
     * 
     * @param storage DomainStorage
     */
    void visitDeployed(DomainStorage storage);

    /**
     * Visita el nodo <code>IApplication</code>
     * 
     * @param application IApplication
     */
    void visitApplication(IApplication application);

    /**
     * Visita el nodo <code>IDomain</code>
     * 
     * @param domain IDomain
     */
    void visitDomain(IDomain domain);

    /**
     * Visita el nodo <code>IDataSourceMetaData</code>
     * 
     * @param metadata IDataSourceMetaData
     */
    void visitDataSourceMetaData(IDataSourceMetaData metadata);

    /**
     * Visita el nodo <code>IAccessPolicy</code>
     * 
     * @param accessPolicy IAccessPolicy
     */
    void visitAccessPolicy(IAccessPolicy accessPolicy);

    /**
     * Visita el nodo <code>IDomainApplication</code>
     * 
     * @param application
     */
    void visitDomainApplication(IDomainApplication application);

    /**
     * Visita el nodo <code>IRelation</code>
     * 
     * @param relation IRelation
     */
    void visitRelation(IRelation relation);

    /**
     * Visita el nodo <code>IUser</code>
     * 
     * @param user IUser
     */
    void visitUser(IUser user);

}