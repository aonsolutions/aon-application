package com.code.aon.fiscal.model;

import com.code.aon.common.AonException;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;

public interface IFiscalModelManager {

	boolean accept(FiscalModelType type);
	
	IFiscalDeclaration initializeFiscalModel( FiscalModel fiscalModel ) throws AonException;
	IFiscalDeclaration refreshFiscalModel( IFiscalDeclaration declaration, FiscalModel fiscalModel );
	IFiscalDeclaration initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException;

	IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel ) throws AonException;


}
