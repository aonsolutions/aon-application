package com.code.aon.marketplace.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.marketplace.Scale;
import com.code.aon.marketplace.ScaleRelation;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IMarketplaceAlias {



	/** 
	* DAOConstantsEntry for Scale entity.
	*/ 
	DAOConstantsEntry SCALE_ENTRY = DAOConstants.getDAOConstant(Scale.class);

	/** 
	* Alias value: Scale_code1
	* Hibernate value: Scale.code1
	*/
	String  SCALE_CODE1 = SCALE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Scale_code2
	* Hibernate value: Scale.code2
	*/
	String  SCALE_CODE2 = SCALE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Scale_code3
	* Hibernate value: Scale.code3
	*/
	String  SCALE_CODE3 = SCALE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Scale_code4
	* Hibernate value: Scale.code4
	*/
	String  SCALE_CODE4 = SCALE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Scale_code5
	* Hibernate value: Scale.code5
	*/
	String  SCALE_CODE5 = SCALE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Scale_enddate
	* Hibernate value: Scale.enddate
	*/
	String  SCALE_ENDDATE = SCALE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Scale_id
	* Hibernate value: Scale.id
	*/
	String  SCALE_ID = SCALE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Scale_inidate
	* Hibernate value: Scale.inidate
	*/
	String  SCALE_INIDATE = SCALE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Scale_programPath
	* Hibernate value: Scale.programPath
	*/
	String  SCALE_PROGRAM_PATH = SCALE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Scale_scaleModel
	* Hibernate value: Scale.scaleModel
	*/
	String  SCALE_SCALE_MODEL = SCALE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Scale_serie
	* Hibernate value: Scale.serie
	*/
	String  SCALE_SERIE = SCALE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Scale_verified
	* Hibernate value: Scale.verified
	*/
	String  SCALE_VERIFIED = SCALE_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for ScaleRelation entity.
	*/ 
	DAOConstantsEntry SCALE_RELATION_ENTRY = DAOConstants.getDAOConstant(ScaleRelation.class);

	/** 
	* Alias value: ScaleRelation_aon_id
	* Hibernate value: ScaleRelation.aon_id
	*/
	String  SCALE_RELATION_AON_ID = SCALE_RELATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ScaleRelation_id
	* Hibernate value: ScaleRelation.id
	*/
	String  SCALE_RELATION_ID = SCALE_RELATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ScaleRelation_scaleModel
	* Hibernate value: ScaleRelation.scaleModel
	*/
	String  SCALE_RELATION_SCALE_MODEL = SCALE_RELATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ScaleRelation_scale_id1
	* Hibernate value: ScaleRelation.scale_id1
	*/
	String  SCALE_RELATION_SCALE_ID1 = SCALE_RELATION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ScaleRelation_scale_id2
	* Hibernate value: ScaleRelation.scale_id2
	*/
	String  SCALE_RELATION_SCALE_ID2 = SCALE_RELATION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ScaleRelation_type
	* Hibernate value: ScaleRelation.type
	*/
	String  SCALE_RELATION_TYPE = SCALE_RELATION_ENTRY.getAliasNames()[5];


}