package com.code.aon.geozone.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IGeoZoneAlias {



	/** 
	* DAOConstantsEntry for GeoTree entity.
	*/ 
	DAOConstantsEntry GEO_TREE_ENTRY = DAOConstants.getDAOConstant(GeoTree.class);

	/** 
	* Alias value: GeoTree_id
	* Hibernate value: GeoTree.id
	*/
	String  GEO_TREE_ID = GEO_TREE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: GeoTree_parent
	* Hibernate value: GeoTree.parent
	*/
	String  GEO_TREE_PARENT = GEO_TREE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: GeoTree_parent_id
	* Hibernate value: GeoTree.parent.id
	*/
	String  GEO_TREE_PARENT_ID = GEO_TREE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: GeoTree_parent_name
	* Hibernate value: GeoTree.parent.name
	*/
	String  GEO_TREE_PARENT_NAME = GEO_TREE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: GeoTree_child
	* Hibernate value: GeoTree.child
	*/
	String  GEO_TREE_CHILD = GEO_TREE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: GeoTree_child_id
	* Hibernate value: GeoTree.child.id
	*/
	String  GEO_TREE_CHILD_ID = GEO_TREE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: GeoTree_child_name
	* Hibernate value: GeoTree.child.name
	*/
	String  GEO_TREE_CHILD_NAME = GEO_TREE_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for GeoZone entity.
	*/ 
	DAOConstantsEntry GEO_ZONE_ENTRY = DAOConstants.getDAOConstant(GeoZone.class);

	/** 
	* Alias value: GeoZone_id
	* Hibernate value: GeoZone.id
	*/
	String  GEO_ZONE_ID = GEO_ZONE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: GeoZone_name
	* Hibernate value: GeoZone.name
	*/
	String  GEO_ZONE_NAME = GEO_ZONE_ENTRY.getAliasNames()[1];


}