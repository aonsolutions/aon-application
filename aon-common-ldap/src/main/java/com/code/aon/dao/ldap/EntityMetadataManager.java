package com.code.aon.dao.ldap;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

public class EntityMetadataManager {

	/**
	 * Map of registered Entity Metadatas.
	 */
	private static Map<Class<? extends ITransferObject>,EntityMetadata> metadastas = new HashMap<Class<? extends ITransferObject>,EntityMetadata>();

	/**
	 * Return the <code>IManagerBean</code> bound to the POJO Class.
	 * 
	 * @param pojoClass
	 * @return The requested <code>IManagerBean</code>.
	 */
	public static EntityMetadata getMetadata(Class<? extends ITransferObject> pojoClass) {
        EntityMetadata metadata = metadastas.get( pojoClass );
		if ( metadata == null ) {
			metadata = new EntityMetadata( pojoClass );
			metadastas.put( pojoClass, metadata );
		}
		return metadata;
	}
	
}
