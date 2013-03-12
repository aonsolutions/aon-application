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


package es.inteco.xbrl.pgc.utils;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;



/**
 *
 *
 * Clase que transforma ficheros mediante una hoja de estilos.
 * 
 * El sistema utiliza esta clase combinada con un proceso batch independiente del resto de la aplicación
 * durante el proceso de visualización, consiguiendo así poder llevar a cabo la transformación mediante
 * XSLT 2.0 (saxon9).  
 * 
 * Este procedimiento es necesario ya que el resto de componentes utilizan librerías saxon en versiones
 * inferiores.
 *
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


public abstract class XSLTransformer
{
    static Logger logger = Logger.getLogger(XSLTransformer.class);

    /**
     * Punto de entrada de ejecución
     * @param args
     * Array de parametros de entrada en el siguiente orden <br>1. Documento a transformar, <br>2. Hoja de estilos utilizada en la transformación
     * <br>3. Documento de salida generado tras la transformacion, <br>4. opcionalmente, parametros de entrada a la XSL. Será una lista de parametros
     * separados entre si por ';' y con el formato parametro1=valor1;parametro2=valor2...
     * <br><br>Los path con espacios en blanco, deben enviarse entrecomillados
     * 
     */
    public static void main(String args[])
    {
	try
	{
	    if ( (args==null) || (args.length < 3) || (args.length > 4))
	    {
		logger.error("FAILED. Waiting for 3 or 4 parameters: input document, style sheet, output document and, optionally, xsl param list");
		System.err.append("FAILED. Waiting for 3 or 4 parameters: input document, style sheet, output document and, optionally, xsl param list");
		System.exit(-1);
	    }

	    String inputDocument=args[0];
	    String stylesheet=args[1]; 
	    String outputDocument=args[2];
	    String paramsList=null;  //paramsList = all params a semicolon separated like: param1=value1;param2=value2

	    if (args.length==4)
	    {
		paramsList=args[3];
	    }
	    
	    File testExistsFile=null;
	    testExistsFile= new File(inputDocument);
	    if (!(testExistsFile.exists())){
		logger.error("Input path [" + inputDocument + "] does not exists");
		System.err.append("Input path [" + inputDocument + "] does not exists");
		System.exit(-1);
	    }
	    testExistsFile= new File(stylesheet);
	    if (!(testExistsFile.exists())){
		logger.error("Style sheet [" +  stylesheet + "] does not exists");
		System.err.append("Style sheet [" +  stylesheet + "] does not exists");
		System.exit(-1);
	    }

	    logger.info("Start transformation process input[" + inputDocument + "], style sheet[" + stylesheet + "], param[" + paramsList + "]" );

	    transform(inputDocument,stylesheet,outputDocument,paramsList);

	    logger.info("Transformation process input[" + inputDocument + "], style sheet[" + stylesheet + "], param[" + paramsList + "] finish successfully" );
	    
	    System.exit(0);
	}
	catch(Throwable e)
	{
	    logger.error(e);
	    System.err.append(e.toString());
	    System.exit(-1);
	}
    }


    /**
     * Método encargado de realizar la transformación XSL
     * 
     * @param inputPath
     * Documento origen
     * @param xslPath
     * Hoja de estilos
     * @param outputPath
     * Documento de salida
     * @param xslParamList
     * Lista de parametros para la hoja de estilos. Será una lista de parámetros
     * separados entre si por ';' y con el formato parametro1=valor1;parametro2=valor2...
     * Los path's con espacios en blanco, deben enviarse entre comillas.
     */
    private static void transform(String inputPath, String xslPath, String outputPath, String xslParamList)
    {
	try
	{
	    TransformerFactory tFactory = TransformerFactory.newInstance();

	    Transformer transformer = tFactory.newTransformer(new StreamSource(xslPath));
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	    if (!(xslParamList==null ||  xslParamList.equals("")))
	    {
		try
		{
		    String[] arrParams = xslParamList.split(";");
		    for (int i = 0 ; i < arrParams.length ; i++) 
		    {
			String individualParam = arrParams[i];
			if (individualParam.contains("="))
			{
			    String[] newParam = individualParam.split("=");
			    logger.info("set parameter for xsl name: [" + (String)newParam[0] + "] value: [" + (String)newParam[1] + "]");
			    transformer.setParameter((String)newParam[0], (String)newParam[1]);	
			}
		    }
		}
		catch(Throwable e)
		{
		    logger.error("Unexpecting error during xsl parms process " ,e);
		    System.err.append("Unexpecting error during xsl parms process " + e.toString());
		    System.exit(-1);
		}
	    }

	    //send result transformation to a file
	    StreamResult resultToFile = new StreamResult(new FileOutputStream(outputPath));
	    
	    //mod may05, para que acepte nombres de fichero con tilde
	    inputPath = "file:///" + inputPath;
	    
	    transformer.transform(new StreamSource(inputPath), resultToFile);
	} 
	catch (Throwable e)
	{
	    logger.error(e);
	    System.err.append(e.toString());
	    System.exit(-1);
	}	
    }
}
