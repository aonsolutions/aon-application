/**
 *
 * API XBRL-PGC2007 is a set of packages for the treatment of instances XBRL
 * (eXtensible Business Reporting Language) corresponding to the taxonomy PGC2007.
 * The General Plan of Accounting 2007 is the legal text that regulates the accounting of
 * the companies in Spain.
 *
 * This program is part of the API XBRL-PGC2007.
 *
 * Copyright (C) 2009  INTECO (Instituto Nacional de Tecnologías de la
 * Comunicación, S.A.)
 *
 * Authors: Members of Software Quality Department inside INTECO
 *
 * E-mail: difusioncalidad@inteco.es
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 3 of the License, or (at your opinion) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program. If not, see http://www.gnu.org/licenses/
 */


package es.inteco.xbrl.pgc.errors.exceptions;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.errors.ErrorMessages;
import es.inteco.xbrl.pgc.errors.GenericErrorsHandler;
import es.inteco.xbrl.pgc.utils.PGCUtils;

/**
 *
 *
 * Clase base de errores, es la clase principal para la notificación de errores encontrados en los
 * servicios de PGC2007.
 * 
 * 
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 * @version 1.0, 14/01/2009
 * @author difusioncalidad@inteco.es
 *
 */


public class XBRLPGCException extends Exception
{

    private static final Logger logger = Logger.getLogger(XBRLPGCException.class);
    
    protected transient String[] parameters = null;
    protected String message = "";
    protected String codeError = null;
    
    private static final long serialVersionUID = 1L;

    /**
     * Indica que no se ha podido deserializar el fichero de configuración global.xml, posiblemente porque
     * su formato no es válido.  Se debe comprobar que este fichero valida correctamente con el esquema 
     * al cual hace referencia, es decir, pgc07-globa.xsd.
     */
    public static final String errorInGlobalFileDeserialization = "errorInGlobalFileDeserialization";//"Failed in deserialization of Global file {0}"
    /**
     * Indica que no se ha podido deserializar el fichero de para un mapa determinado, posiblemente porque
     * su formato no es válido.  Se debe comprobar que este fichero valida correctamente con el esquema 
     * definido para mapas, es decir, pgc07-mapa.xsd.
     */
    public static final String errorInMapFileDeserialization = "errorInMapFileDeserialization";//"Failed in deserialization of map file {0}"
    /**
     * Indica que no ha podido ser deserializado un documento de entrada en formato común, cuyo elemento raíz es
     * Report. Es difícil que este error se produzca en los servicios PGC2007 debido a que los documentos de entrada
     * son validados contra el esquema definido para ello (pgc07-io-interface.xsd) previamente a su deserialización.
     */
    public static final String errorInReportFileDeserialization = "errorInReportFileDeserialization";//"Failed in deserialization of report file {0}"
    /**
     * No se encuentra el fichero de configuración global.xml, el cual debe estar en el directorio config.
     */
    public static final String notFoundReportInGlobal = "notFoundReportInGlobal";//"Report {0} not found in global file"
    /**
     * No se ha encontrado la configuración para un determinado report en el fichero de configuración global.xml.
     */
    public static final String notFoundModuleForReportInGlobal = "notFoundModuleForReportInGlobal";//"Module {0} not found in report {1}"
    /**
     * El juego de caracteres utilizado en el documento de entrada no es correcto, el cual debe ser UTF-8.
     */
    public static final String unsuportedEncoding = "unsuportedEncoding";//"Encoding not supported"
    /**
     * No ha sido creado, o no hay permisos de acceso al directorio temporal definido en la configuración del sistema,
     * es decir en el parámetro "tempDirectory" del fichero de propiedades pgc.properties, que se encuentra en el directorio
     * config del sistema.
     */
    public static final String notExistTempDirectory = "notExistTempDirectory";//"Not exist temp directory {0}"
    /**
     * Se ha producido un error mientra se intentaba escribir el XBRL obtenido tras el proceso de transformación.
     */
    public static final String failedWriteXbrlDocument = "failedWriteXbrlDocument";//"Failed attempting to write {0} to {1}"
    /**
     * El juego de caracteres utilizado en la instancia XBRL de entrada no está soportado por la aplicación, el cual debe ser
     * UTF-8.
     */
    public static final String unsuportedEncodingInstanceByte = "unsuportedEncodingInstanceByte";//"Error in obtaining instance as byte[]. Encoding not supported"
    /**
     * Error producido mientras se intentaba obtener un array de bytes de la instancia resultado del proceso de transformación.
     */
    public static final String failedGettingInstanceByte = "failedGettingInstanceByte";//"Error in obtaining instance as byte[]"
    /**
     * Error en la inicialización del procesador de XBRL estándard.
     */
    public static final String errorInicializingProcessor = "errorInicializingProcessor";//"Error initializing processor"
    /**
     * Error mientras se asignan prefijos y paths.
     */
    public static final String errorGettingXPathPrefixMaps = "errorGettingXPathPrefixMaps";//"Error associating prefixes and namespaces for XPath expressions"
    /**
     * Error creando los namespaces básicos para la creación de una instancia XBRL.
     */
    public static final String errorCreatingNamespaces = "errorCreatingNamespaces";//"Failed to create instance namespaces"
    /**
     * Error creando los namespaces básicos para la creación de una instancia XBRL.
     */
    public static final String coreErrorCreatingNamespaces = "coreErrorCreatingNamespaces";//"Failed to create instance namespaces (core error)"
    /**
     * Error creando instancia XBRL.
     */
    public static final String errorCreatingXbrlInstance = "errorCreatingXbrlInstance";//"Failed to create instance. Input file:{0} NameSpace:{1} Schema:{2}"
    /**
     * Error creando un elemento XBRL Unit.
     */
    public static final String errorCreatingUnit = "errorCreatingUnit";//"Failed to create an Unit element in instance"
    /**
     * Error creando un elemento XBRL Measure.
     */
    public static final String errorCreatingMeasure = "errorCreatingMeasure";//"Failed to create a Measure element in instance"
    /**
     * Error creando un elemento XBRL Context.
     */
    public static final String errorCreatingContext = "errorCreatingContext";//"Failed to create a Context element in instance"
    /**
     * Error creando un elemento XBRL periodo del tipo duration.
     */
    public static final String errorCreatingDurationPeriod = "errorCreatingDurationPeriod";//"Failed to create a Duration Period element in instance"
    /**
     * Error creando un elemento XBRL periodo del tipo instant.
     */
    public static final String errorCreatingInstantPeriod= "errorCreatingInstantPeriod";//"Failed to create a Instant Period element in instance"
    /**
     * Error creando un elemento XBRL Concept
     */
    public static final String errorCreatingConcept= "errorCreatingConcept";//"Failed to create a Concept element in instance"
    /**
     * Error creando un elemento XBRL Tuple.
     */
    public static final String errorCreatingTuple= "errorCreatingTuple";//"Failed to create a Tuple element in instance"
    /**
     * Error creando un elemento XBRL Dimension.
     */
    public static final String errorCreatingDimension= "errorCreatingDimension";//"Failed to create a Dimension element in instance"
    /**
     * Error creando un elemento hijo para una tupla XBRL.
     */
    public static final String errorCreatingTupleChild= "errorCreatingTupleChild";//"Failed to create a nested Tuple element in instance"
    /**
     * Error leyendo el atributo periodType, para determinar el tipo de periodo de un contexto dado.
     */
    public static final String errorReadingPeridodTypeAttribute= "errorReadingPeridodTypeAttribute";//"Error reading PeriodType attribute"
    /**
     * Error creando un elemento XBRL FootNoteLink.
     */
    public static final String errorCreatingFootNoteLink= "errorCreatingFootNoteLink";//"Failed to create a footnote link in instance"
    /**
     * Error creando un elemento XBRL FootNote.
     */
    public static final String errorCreatingFootNote= "errorCreatingFootNote";//"Failed to create a FootNote element in instance. Text:{0}"
    /**
     * Se han encontrados errores en la validación de la instancia.
     */
    public static final String errorValidatingInstance= "errorValidatingInstance";//"Error during validation process for instance:{0}"
    /**
     * Error cargando la instancia antes de su validación.
     */
    public static final String errorValidatingInstanceNotLoad= "errorValidatingInstanceNotLoad";//"Error during validation process for instance:{0}. Loading document was not completed successfully"
    /**
     * Error obteniendo codeLabel para concpeto.
     */
    public static final String errorGettingCodeLabel= "errorGettingCodeLabel";//"Error reading a CodeLabel from DTS"
    /**
     * Error obteniendo codelabel para concepto.
     */
    public static final String coreErrorGettingCodeLabel= "coreErrorGettingCodeLabel";//"Error reading a CodeLabel from DTS (Core error)"

    
    /**
     * Error obteniendo el schemaRef de una instancia
     */
    public static final String errorGettingSchema= "errorGettingSchema";
    
    
    public static final String errorAddMembersToConceptMap= "errorAddMembersToConceptMap";//"Error adding member to conceptMap"
    public static final String errorCreatingConceptMap= "errorCreatingConceptMap";//"Failed to create a ConceptMap element in DTS"
    public static final String errorGettingMembers= "errorGettingMembers";//"Error getting members of hypercube"
    public static final String errorCreatingStatement= "errorCreatingStatement";//"Failed to create a Statement element. Schema:{0} Prefix:{1} Name:{2}"
    
    
    /**
     * Error producido mientras se intentaba cargar la taxonomía.
     */
    public static final String errorLoadingTaxonomy= "errorLoadingTaxonomy";//"Failed to create taxonomy. Schema:{0} Taxonomy file:{1} Uri:{2}"
    public static final String hyperCubeNotFoundInRoleGDM= "hyperCubeNotFoundInRoleGDM";//"Error during dimension map generation. Not exists hypercube in role {0}"
    public static final String errorCreatingStatementDimensionMap= "errorCreatingStatementDimensionMap";//"Error creating dimensional map Statement. RoleUri:{0}, Map:{1}, NameSpace:{2}, Prefix:{3}"
    public static final String errorGettingRoleFromDTS= "errorGettingRoleFromDTS";//"Error getting role from DTS. Role uri:{0}"
    public static final String roleNotFoundInMap= "roleNotFoundInMap";//"Error during map generation. Not exists role {0} for map {1}"
    public static final String errorCreatingMap= "errorCreatingMap";//"Error during map generation. RoleUri:{0}, Map:{1}, NameSpace:{2}, Prefix:{3}"    

    
    /**
     * Error cargando instancia.
     */
    public static final String errorLoadingInstance= "errorLoadingInstance";//"Error loading instance. Document path:{0}"
    /**
     * Error obteniendo esquema de la instancia.
     */
    public static final String errorGettingSchemaFromInstance= "errorGettingSchemaFromInstance";//"Error getting schema from instance"
    /**
     * Error obteniendo fact de la instancia.
     */
    public static final String errorCreatingXBRLFact = "errorCreatingXBRLFact";//"Error getting fact from XBRL instance"
    /**
     * Error obteniendo contexto de la instancia.
     */
    public static final String errorCreatingXBRLContext = "errorCreatingXBRLContext";//"Error getting context from XBRL instance"
    /**
     * Error comprobando si un fact es tupla.
     */
    public static final String errorCheckingFactIsTuple = "errorCheckingFactIsTuple";//"Error checking if fact is tuple"
    /**
     * Error obteniendo contexto desde fact.
     */
    public static final String errorExtractingContextFromFact ="errorExtractingContextFromFact";//"Error extracting context from fact"
    /**
     * Error convirtiendo iterador de facts a tabla.
     */
    public static final String errorTranslatingFactsFromIteratorToTable ="errorTranslatingFactsFromIteratorToTable";//"Error translating facts from iterator to table associating context"
    /**
     * Error obteniendo tabla de facts.
     */
    public static final String errorGettingGlobalFactsTable = "errorGettingGlobalFactsTable";//"Error getting global facts table"
    /**
     * Error obteniendo tabla de hijos de tupla.
     */
    public static final String errorGettingGlobalTupleChildsTable = "errorGettingGlobalTupleChildsTable";//"Error getting global tuple childs table"
    /**
     * Error obteniendo tabla de tuplas.
     */
    public static final String errorGettingGlobalTupleTable = "errorGettingGlobalTupleTable";//"Error getting global tuple table"
    /**
     * Error obteniendo la tabla de tuplas para una tupla dada.
     */
    public static final String errorGettingGlobalTupleTableByParent = "errorGettingGlobalTupleTableByParent";//"Error getting global tuple table (by parent)"
    /**
     * Error obteniendo la tabla de contextos de una instancia dada.
     */
    public static final String errorGettingContextTable = "errorGettingContextTable";//"Error getting context table by instance"
    /**
     * Error obteniendo la lista de unidades de una instancia XBRL dada.
     */
    public static final String errorGettingUnitList = "errorGettingUnitList";//"Error getting unit list by instance"
    /**
     * Fichero no encontrado.
     */
    public static final String fileNotFound = "fileNotFound";//"File {0} not found {1}"
    /**
     * Error creando un fact, debido a que su concepto no se encuentra en la taxonomía.
     */
    public static final String errorCreatingFactConceptNotFound= "errorCreatingFactConceptNotFound";//"Error creating fact. Concept not found. Search expression: {0}"
    /**
     * Error creando una tupla, debido a que su concepto no se encuentra en la taxonomía.
     */
    public static final String errorCreatingTupleConceptNotFound= "errorCreatingTupleConceptNotFound";//"Error creating tuple fact. Concept not found. Search expression: {0}"
    /**
     * Error creando hijo de una tupla, debido a que su concepto no se encuentra en la taxonomía.
     */
    public static final String errorCreatingTupleChildConceptNotFound= "errorCreatingTupleChildConceptNotFound";//"Error creating tuple child. Concept not found. Search expression: {0}"
    /**
     * Error comprobando si un fact dado es de tipo instant.
     */
    public static final String errorCheckingPeridoTypeIsInstant= "errorCheckingPeridoTypeIsInstant";//"PeriodType query error, cheking if it's instant. Concept not found. Search expression: {0}"
    /**
     * Error creando footnote, debido a que no se encuentra el role especificado para la footnote.
     */
    public static final String errorCreatingFootNoteLinkLinkRoleNotFound= "errorCreatingFootNoteLinkLinkRoleNotFound";//"Error creating footnote link. LinkRole not found. Search expression: {0}"
    public static final String errorCreatingFootNoteNoteRoleNotFound= "errorCreatingFootNoteNoteRoleNotFound";//"Error creating FootNote. Note role not found. Search expression: {0}"
    public static final String errorCreatingFootNoteArcRoleNotFound= "errorCreatingFootNoteArcRoleNotFound";//"Error creating FootNote. Arc role not found. Search expression: {0}"
    /**
     * Error al intentar cerrar una instancia XBRL.
     */
    public static final String closingInstance ="closingInstance";
    /**
     * No existe domain member para un módulo dado.
     */
    public static final String notExistDomainMemberForModule="notExistDomainMemberForModule";
    /**
     * Error genérico ocurrido durante el proceso de validación de una instancia XBRL.
     */
    public static final String genericErrorValidatingInstance="genericErrorValidatingInstance";
    /**
     * Error intentando visualizar una instancia XBRL.
     */
    public static final String errorTryingViewInstance="errorTryingViewInstance";
    public static final String errorTryingViewInstanceExternal="errorTryingViewInstanceExternal";
    public static final String errorTryingViewInstanceResultFile="errorTryingViewInstanceResultFile";
    /**
     * Error debido a que existe un contexto si escenario, probablemente porque se haya especificado
     * la dimensión como un segmento en lugar de escenario que es lo correcto.
     */
    public static final String errorContextWithoutScenario="errorContextWithoutScenario";
    

    /**
     * Se produce cuando el schemaRef en la instancia de entrada no es correcto y no puede
     * ser resuelto.
     */
    public static final String canNotResolveSchemaRef="canNotResolveSchemaRef";
    
    
    
    public XBRLPGCException(String codeError, Throwable cause)
    {
	this.codeError = codeError;
	setMessage(codeError, cause);
    }

    public XBRLPGCException(String codeError,  String[] parameters, Throwable cause)
    {
	this.codeError = codeError;
	this.parameters = parameters;
	setMessage(codeError, cause);
    }

    public XBRLPGCException(String codeError, String[] parameters)
    {
	this.codeError = codeError;
	this.parameters = parameters;
	setMessage(codeError,null);
	
    }
    
    protected void setMessage(String codeError, Throwable cause)
    {

	String messageFormat = ErrorMessages.getInstance().getMessage(codeError);
	if ((messageFormat != null) && (parameters != null) )
	{
	    message = PGCUtils.format(messageFormat, parameters);
	}
	else
	{
	    message = codeError;
	}
	if (cause != null)
	{
	    message = message + " original cause: " + cause.toString();
	}
	
	try
	{
	    GenericErrorsHandler errorsHandler = new GenericErrorsHandler();
	    
	    ArrayList<String> errorParameterList = null;
	    try{errorParameterList = new ArrayList<String>(Arrays.asList(parameters));}catch(Exception e){}
	    
	    errorsHandler.addError(codeError, message, errorParameterList);
	    message = errorsHandler.toString();
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }
    
    
    @Override
    public String getMessage()
    {

	return message;
    }

    public final String getCodeError()
    {
        return codeError;
    }

    public final void setCodeError(String codeError)
    {
        this.codeError = codeError;
    }

}
