package net.aonsolutions.aon.tbai.emision._beans.validators;

import static net.aonsolutions.aon.tbai.toolkit.DataToolkit.isEmpty;

import net.aonsolutions.aon.tbai.toolkit.DataToolkit;

public class EntityValidator {

	
	public static void validateNIF(String nif,String entity_name) throws Exception {
		if(isEmpty(nif)) 					throw new Exception(entity_name + " NIF not found");		
		if(!DataToolkit.checkNIF(nif))  	throw new Exception(entity_name + " NIF is not valid");	
	}
	
	public static void validateNombre(String nombre, String entity_name) throws Exception {
		if(isEmpty(nombre)) 	throw new Exception(entity_name + " name not found");
	}	

	public static void validateCodigoPostal(String codigo, String entity_name) throws Exception {
		if(isEmpty(codigo))				throw new Exception(entity_name + " zip not found");
		if(!codigo.matches("[0-9]{5}"))	throw new Exception(entity_name + " invalid zip format");
	}	
	
	public static void validateDireccion(String direccion, String entity_name) throws Exception {
		if(isEmpty(direccion)) 	throw new Exception(entity_name + " address not found");
	}	

	public static void validateIdType(String idType, String entity_name) throws Exception {
		if(isEmpty(idType))		throw new Exception(entity_name + " id type not found");
	}
	
	public static void validateId(String id, String entity_name) throws Exception {
		if(isEmpty(id))			throw new Exception(entity_name + " id not found");
	}
	
}
