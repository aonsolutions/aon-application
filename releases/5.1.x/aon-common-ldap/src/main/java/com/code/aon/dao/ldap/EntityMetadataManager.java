package com.code.aon.dao.ldap;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.ITransferObject;

/**
 * The Class EntityMetadataManager.
 */
public class EntityMetadataManager {

	private static Map<Class<? extends ITransferObject>,EntityMetadata> metadastas = new HashMap<Class<? extends ITransferObject>,EntityMetadata>();

	/**
	 * Return the <code>IManagerBean</code> bound to the POJO Class.
	 * 
	 * @param pojoClass the pojo class
	 * 
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
