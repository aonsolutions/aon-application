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


package es.inteco.xbrl.pgc.apifacade.xbreeze;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ubmatrix.xbrl.common.exception.src.CoreException;
import ubmatrix.xbrl.common.formatter.src.Formatter;
import ubmatrix.xbrl.common.formatter.src.FormatterException;
import ubmatrix.xbrl.common.formatter.src.IFormatter;
import ubmatrix.xbrl.common.memo.registry.src.DiscreteMemoRegistry;
import ubmatrix.xbrl.common.memo.registry.src.IMemoRegistry;
import ubmatrix.xbrl.common.memo.src.IMemo;
import ubmatrix.xbrl.common.memo.uriResolver.src.IURIResolver;
import ubmatrix.xbrl.common.memo.uriResolver.src.URIResolver;
import ubmatrix.xbrl.common.src.Configuration;
import ubmatrix.xbrl.common.src.IDTS;
import ubmatrix.xbrl.common.src.IPrefixResolver;
import ubmatrix.xbrl.common.src.ISimpleProgressNotify;
import ubmatrix.xbrl.common.src.LocationHandleType;
import ubmatrix.xbrl.common.utility.src.CommonUtilities;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.DTSDiscoverer;
import ubmatrix.xbrl.locationController.xbrlLocationController.locationHandle.src.FileLocationHandle;
import ubmatrix.xbrl.src.Xbrl;
import ubmatrix.xbrl.validation.src.FunctionUriResolverArgs;
import ubmatrix.xbrl.validation.src.IFunctionUriResolver;
import ubmatrix.xbrl.validation.src.IFunctionUriResolverArgs;
import ubmatrix.xbrl.validation.src.IValidation;
import ubmatrix.xbrl.validation.src.IValidator;
import ubmatrix.xbrl.validation.src.ValidatorEngine;
import ubmatrix.xbrl.validation.src.ValidationState;


/**
 *
 * Clase: UbmXbrlValidator
 *
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 *
 * @version 1.0, 14/01/2009
 * @author difusioncalidad@inteco.es
 *
 */


public class UbmXbrlValidator implements ISimpleProgressNotify
{

    private ArrayList m_memoList = new ArrayList();
    private String m_basePath = "";
    private IFormatter m_formatter = new Formatter();

    private IURIResolver m_resolver = new URIResolver();
    private String m_language = "en";
    private static HashMap m_functions;
    private static Iterator m_inputSet;
    private static IMemoRegistry m_discreteMemoRegistry;
    private static ISimpleProgressNotify listener;
    private static ArrayList m_memos;

    /**
     * Default constructor - mainly initializes the Xbrl Processor
     * 
     */

    public UbmXbrlValidator() throws Exception {

	DiscreteMemoRegistry memoRegistry = new DiscreteMemoRegistry();
	long cookie = 0;

	cookie = memoRegistry.attach(this);

	boolean success = Xbrl.initialize(memoRegistry);
	memoRegistry.detach(cookie);

	if (!success)
	    throw new Exception("Failed to initialize");

	Configuration config = Configuration.getInstance();
	m_basePath = config.getCoreRoot();
	m_functions = new HashMap();
	m_inputSet = null;
	m_discreteMemoRegistry = new DiscreteMemoRegistry();
	m_memos = new ArrayList();

	if (m_basePath == null)
	    System.out
		    .println("The environmental variable COREROOT is not defined. Please add COREROOT as environment variable and points to your XBRL Processor install directory.");

    }

    /**
     * Accessor method - returns the base path, which is the COREROOT
     * 
     * @return
     * base path, which is the COREROOT
     * 
     */

    public String getBasePath()
    {

	return m_basePath;
    }

    /**
     * Accessor method - returns the list of memos generated during loading the
     * file
     * 
     * 
     */

    public ArrayList getMemoList()
    {

	return m_memoList;
    }

    public void clearMemoList()
    {

	m_memoList.clear();
    }

    /**
     * This method illustrates the use of Localizer, URIResolver and Formatter.
     * IMemo is passed to this method, from which it gets the memo particles.
     * Then it calls the URIResolver with the memoUri and the language, which in
     * turn calls the Localizer to return the localized string for that memoUri
     * and language. Note, in this example, English language 'en' is used, if
     * you want to try different language, make sure you have the appropriate
     * localizer file. Finally, the localized string and the particles are
     * passed to Formatter which returns the formatted string for displaying the
     * error message
     * 
     * @param e
     * 
     * 
     * @return
     * the localized string for that memoUri and language
     * 
     */

    public String getSubstitutedLocalizedString(IMemo e) throws FormatterException
    {

	// Resolve the memo uri
	Object[] particles = e.getParticles();

	// Call the localizer with the memoUri and the language, and get back
	// the localized string.
	// Note, the localized string might contain placeholders like {0}
	String localizedString = m_resolver.getUnsubstitutedLocalizedString(e.getMemoURI(), m_language);

	return (localizedString.equals(e.getMemoURI())) ? e.getMemoURI() : m_formatter.getSubstitutedString(localizedString, particles);

    }

    /**
     * This method loads a file from the specified fileUri. A fileType,
     * indicating whether it is a Taxonomy, Linkbase or Instance document, is
     * also passed. After successful load, this method returns the DTS.
     * 
     * @param fileUri
     * 
     * 
     * @param fileType
     * 
     * 
     * @return
     * DTS - Discoverable Taxonomy Set
     * 
     * 
     */

    public IDTS load(String fileUri, int fileType) throws CoreException
    {

	IDTS dts = null;
	DiscreteMemoRegistry discreteMemoRegistry = new DiscreteMemoRegistry();

	long cookie = 0;
	cookie = discreteMemoRegistry.attach(this);

	FileLocationHandle handle = new FileLocationHandle(fileUri);

	switch (fileType)
	{

	case LocationHandleType.c_Taxonomy:
	    handle.setLocationHandleType(LocationHandleType.c_Taxonomy);
	    break;
	case LocationHandleType.c_Linkbase:
	    handle.setLocationHandleType(LocationHandleType.c_Linkbase);
	    break;
	case LocationHandleType.c_Instance:
	    handle.setLocationHandleType(LocationHandleType.c_Instance);
	    break;
	}

	handle.setMemoRegistry(discreteMemoRegistry);

	try
	{
	    dts = DTSDiscoverer.establishEntryPoint(handle);

	} finally
	{

	    if (dts != null)
		discreteMemoRegistry.detach(cookie);
	}

	return dts;
    }

    /**
     * This method validates a valid DTS passed to. It validates based on the
     * validators registerd.
     * 
     * @param dts
     * 
     * 
     */

    public void validate(IDTS dts) throws Exception
    {
	if (dts != null)
	{

	    ValidatorEngine validatorEngine = new ValidatorEngine();

	    DiscreteMemoRegistry discreteMemoRegistry = new DiscreteMemoRegistry();
	    long cookie = discreteMemoRegistry.attach(this);

	    validatorEngine.setMemoRegistry(discreteMemoRegistry);
	    validatorEngine.validate(dts);
	    discreteMemoRegistry.detach(cookie);
	}
    }

    /**
     * This overloaded method validates the DTS passed against FRTA if the
     * option is true
     * 
     * @param dts
     * 
     * 
     * @param FRTA
     * 
     * 
     */

    public void validate(IDTS dts, boolean FRTA)
    {

	try
	{

	    if (dts != null)
	    {

		ValidatorEngine validatorEngine = new ValidatorEngine();
		enableFRTAValidation(FRTA);

		DiscreteMemoRegistry discreteMemoRegistry = new DiscreteMemoRegistry();
		long cookie = discreteMemoRegistry.attach(this);

		validatorEngine.setMemoRegistry(discreteMemoRegistry);
		validatorEngine.update();

		validatorEngine.validate(dts);
		discreteMemoRegistry.detach(cookie);
	    }

	} catch (Exception ex1)
	{
	    // log exception

	    // TODO: IGNORED ON PURPOSE
	    // ex1.printStackTrace();
	}
    }

    // Helper function to enable / disable validator depending on FRTA is on or
    // off
    private void enableFRTAValidation(boolean on)
    {

	// This classification uri must match with the classification Uri in the
	// XbrlConfiguraton.xml file
	final String c_validationClassificationUri = "validation://ubmatrix.com/Xbrl/Validation#BestPractices";
	// This logical must also match with that in the XbrlConfiguraton.xml
	// file
	final String c_logicalUri = "Frta";

	try
	{

	    ValidationState validationState = ValidationState.getInstance();
	    HashMap validationStates = validationState.getValidations();

	    IValidation validation = ((IValidation) (validationStates.get(c_validationClassificationUri)));

	    Iterator validators = validation.getValidators();

	    while (validators.hasNext())
	    {

		IValidator validator = ((IValidator) validators.next());
		if (validator.getLogicalUri().equals(c_logicalUri))
		{

		    if (on)
		    {

			validator.setEnabled(true);
			break;

		    } else
		    {

			validator.setEnabled(false);
			break;
		    }
		}
	    }

	} catch (Exception ex)
	{
	    // log any exception

	    // TODO: IGNORED ON PURPOSE
	    // ex.printStackTrace();
	}
    }

    /**
     * The main entry point for the application.
     * 
     */

    public static void main(String[] args)
    {

	String fileUri = "";
	int fileType = -1;
	IDTS dts = null;

	try
	{

	    UbmXbrlValidator example = new UbmXbrlValidator();

	    System.out.println("Example 1. Validating BasicCalculation taxonomy - Press any key to proceed");

	    // First - load the taxonomy. Following path assumes that you have
	    // already installed
	    // Xbrl Processor

	    String basePath = example.getBasePath();

	    basePath = basePath.replace('/', '\\');

	    fileUri = CommonUtilities.getFullPath(basePath + "/XbrlFiles/Patterns-2005-01-30/BasicCalculation.xsd");
	    fileType = LocationHandleType.c_Taxonomy;

	    // get the DTS
	    dts = example.load(fileUri, fileType);
	    if (example.m_memoList.size() > 0)
	    {

		System.out.println("Following errors occurred while loading the document");
		// print any error memos
		for (int i = 0; i < example.m_memoList.size(); ++i)
		{

		    IMemo memo = (IMemo) example.m_memoList.get(i);
		    System.out.println(example.getSubstitutedLocalizedString(memo));
		}
	    }

	    // before proceeding for validation, clear any load related memos
	    example.clearMemoList();

	    // try to validate the dts with whatever loaded in case there is a
	    // failure , otherwise, work with
	    // full DTS
	    example.validate(dts);

	    // print any error memos
	    for (int i = 0; i < example.m_memoList.size(); ++i)
	    {

		IMemo memo = (IMemo) example.m_memoList.get(i);
		System.out.println(example.getSubstitutedLocalizedString(memo));
	    }
	    System.out.println("Validation Completed");

	    // Example 2. An instance document
	    System.out.println("");
	    System.out.println("Example 2. Validating BasicCalculation-instance instance document - Press any key to proceed");

	    fileUri = CommonUtilities.getFullPath(basePath + "/XbrlFiles/Patterns-2005-01-30/BasicCalculation-instance.xml");
	    fileType = LocationHandleType.c_Instance;

	    // get the DTS
	    dts = example.load(fileUri, fileType);
	    if (example.m_memoList.size() > 0)
	    {

		System.out.println("Following errors occurred while loading the document");
		// print any error memos
		for (int i = 0; i < example.m_memoList.size(); ++i)
		{

		    IMemo memo = (IMemo) example.m_memoList.get(i);
		    System.out.println(example.getSubstitutedLocalizedString(memo));
		}
	    }

	    // before proceeding for validation, clear any load related memos
	    example.clearMemoList();

	    // try to validate the dts with whatever loaded in case there is a
	    // failure , otherwise, work with
	    // full DTS
	    example.validate(dts);

	    // print any error memos
	    for (int i = 0; i < example.m_memoList.size(); ++i)
	    {

		IMemo memo = (IMemo) example.m_memoList.get(i);
		System.out.println(example.getSubstitutedLocalizedString(memo));
	    }

	    System.out.println("Validation Completed");

	    // Example 3. An invalid taxonomy - should generate memos
	    System.out.println();
	    System.out.println("Example 3. Validating an Xbrl invalid taxonomy - Press any key to proceed");

	    fileUri = CommonUtilities.getFullPath(basePath + "/samples/validation/example/102-02-MissingPeriodType.xsd");
	    fileType = LocationHandleType.c_Taxonomy;

	    // get the DTS
	    dts = example.load(fileUri, fileType);
	    if (example.m_memoList.size() > 0)
	    {

		System.out.println("Following errors occurred while loading the document");
		// print any error memos
		for (int i = 0; i < example.m_memoList.size(); ++i)
		{

		    IMemo memo = (IMemo) example.m_memoList.get(i);
		    System.out.println(example.getSubstitutedLocalizedString(memo));
		}
	    }

	    // before proceeding for validation, clear any load related memos
	    example.clearMemoList();

	    // try to validate the dts with whatever loaded in case there is a
	    // failure , otherwise, work with
	    // full DTS
	    example.validate(dts);

	    // print any error memos
	    for (int i = 0; i < example.m_memoList.size(); ++i)
	    {

		IMemo memo = (IMemo) example.m_memoList.get(i);
		System.out.println(example.getSubstitutedLocalizedString(memo));
	    }
	    System.out.println("Validation Completed");

	    // Example 4. An instance document
	    System.out.println("");
	    System.out.println("Example 4. Validating an instance document to generate calculation trace errors - Press any key to proceed");

	    fileUri = CommonUtilities.getFullPath(basePath + "/samples/validation/example/397-01-InconsistentInstance-invalid.xbrl");
	    fileType = LocationHandleType.c_Instance;

	    example.clearMemoList();
	    // get the DTS
	    dts = example.load(fileUri, fileType);
	    if (example.m_memoList.size() > 0)
	    {

		System.out.println("Following errors occurred while loading the document");
		// print any error memos
		for (int i = 0; i < example.m_memoList.size(); ++i)
		{

		    IMemo memo = (IMemo) example.m_memoList.get(i);
		    System.out.println(example.getSubstitutedLocalizedString(memo));
		}
	    }

	    m_inputSet = dts.find("/'domain://ubmatrix.com/Xbrl/Taxonomy#Role'").getEnumerator();

	    // before proceeding for validation, clear any load related memos
	    example.clearMemoList();

	    // try to validate the dts with whatever loaded in case there is a
	    // failure , otherwise, work with
	    // full DTS
	    example.validate(dts);

	    // print any error memos
	    for (int i = 0; i < example.m_memoList.size(); ++i)
	    {

		IMemo memo = (IMemo) example.m_memoList.get(i);
		System.out.println(example.getSubstitutedLocalizedString(memo));
	    }
	    System.out.println();
	    System.out.println("Validation Completed");
	    System.out.println();
	    System.out.println("Now the calculation trace is generated");
	    LoadSystemFunctions();
	    String fileName = CommonUtilities.getFullPath(basePath + "/CalTrace.xml");
	    HandleTrace(fileName, dts);
	    System.out.println();
	    System.out.println("Calculation trace completed");

	    // Example 5. Enable FRTA validator and do validation of an invalid
	    // FRTA document
	    // This example wil generate error memos.
	    System.out.println("");
	    System.out.println("Example 5. Validating a FRTA invalid taxonomy - Press any key to proceed");

	    fileUri = CommonUtilities.getFullPath(basePath + "/samples/validation/example/202-02-ImproperID-invalid.xsd");
	    fileType = LocationHandleType.c_Taxonomy;

	    // get the DTS
	    dts = example.load(fileUri, fileType);

	    if (example.m_memoList.size() > 0)
	    {

		System.out.println("Following errors occurred while loading the document");
		// print any error memos
		for (int i = 0; i < example.m_memoList.size(); ++i)
		{

		    IMemo memo = (IMemo) example.m_memoList.get(i);
		    System.out.println(example.getSubstitutedLocalizedString(memo));
		}
	    }

	    // before proceeding for validation, clear any load related memos
	    example.clearMemoList();

	    // try to validate the dts with whatever loaded in case there is a
	    // failure , otherwise, work with
	    // full DTS

	    // enable FRTA module
	    boolean FRTA = true;
	    example.validate(dts, FRTA);

	    // print any error memos
	    for (int i = 0; i < example.m_memoList.size(); ++i)
	    {

		IMemo memo = (IMemo) example.m_memoList.get(i);
		System.out.println(example.getSubstitutedLocalizedString(memo));
	    }
	    System.out.println("Validation Completed");

	} catch (Exception ex)
	{

	    System.out.println(ex.getStackTrace());
	}

    }

    protected static void LoadSystemFunctions()
    {
	ValidationState validationState = ValidationState.getInstance();
	Iterator registeredFunctions = validationState.getFunctions();

	while (registeredFunctions.hasNext())
	{
	    Map.Entry de = (Map.Entry) registeredFunctions.next();

	    IFunctionUriResolver resolver = (IFunctionUriResolver) de.getValue();

	    Iterator functions = resolver.getAvailableFunctions();
	    if (functions != null)
	    {
		// Update the vtable with functions available in this validator
		while (functions.hasNext())
		{
		    String function = (String) ((Map.Entry) functions.next()).getValue();
		    if (m_functions.containsKey(function))
			m_functions.remove(function);

		    m_functions.put(function, resolver);
		}
	    }
	}
    }

    private static void HandleTrace(String input, IDTS dts) throws CoreException, ParseException
    {
	String[] args = ExtractParameters(input);
	String command = "hasinvalidcalculation";
	boolean commandExists = VerifyCommand(command);

	if (commandExists)
	{
	    IPrefixResolver prefixResolver = dts.getEntryNode().getLocationHandle().getPrefixResolver();

	    // long cookie = m_discreteMemoRegistry.attach(listener);
	    IFunctionUriResolverArgs inputArgs = CreateArgs(command, dts, m_inputSet, null, // previous
											    // input
											    // set
		    args, null, // options
		    prefixResolver, m_discreteMemoRegistry, false);

	    IFunctionUriResolver resolver = (IFunctionUriResolver) m_functions.get(command);
	    if (resolver == null)
	    {
		System.out.println("Invalid function: " + command);
		return;
	    }

	    // Execute the function with args
	    try
	    {
		m_memos.clear();
		long cookie = m_discreteMemoRegistry.attach(listener);
		resolver.executeMethod(inputArgs);
		m_discreteMemoRegistry.detach(cookie);
	    } catch (CoreException ex)
	    {

		System.out.println("Exception thrown from " + command + ": " + ex.getMessage());
		System.out.println("");
	    }
	}
    }

    /**
     * A factory like method for creating method args
     * 
     * @param name
     * 
     * 
     * @param dts
     * 
     * 
     * @param inputSet
     * 
     * 
     * @param prevInputSet
     * 
     * 
     * @param args
     * 
     * 
     * @param options
     * 
     * 
     * @param prefixResolver
     * 
     * 
     * @param memoRegistry
     * 
     * 
     * @param negate
     * 
     * 
     * @return
     * 
     * 
     */

    protected static IFunctionUriResolverArgs CreateArgs(String name, IDTS dts, Iterator inputSet, Iterator prevInputSet, String[] args, HashMap options,
	    IPrefixResolver prefixResolver, IMemoRegistry memoRegistry, boolean negate)
    {
	FunctionUriResolverArgs functionArgs = null;

	try
	{
	    functionArgs = new FunctionUriResolverArgs();

	    functionArgs.setArgs(args);
	    functionArgs.setDTS(dts);
	    functionArgs.setName(name);
	    functionArgs.setOptions(options);
	    functionArgs.setInputSet(inputSet);
	    functionArgs.setPrevInputSet(prevInputSet);
	    functionArgs.setNegate(negate);
	    functionArgs.setPrefixResolver(prefixResolver);
	    functionArgs.setMemoRegistry(memoRegistry);
	} catch (Exception e)
	{
	    functionArgs = null;
	}

	return functionArgs;
    }

    private static String[] ExtractParameters(String input)
    {
	String[] inputStr = new String[1];
	inputStr[0] = input;

	return inputStr;
    }

    private static boolean VerifyCommand(String command)
    {
	if (!m_functions.containsKey(command.toLowerCase()))
	    return false;

	return true;
    }

    public void cancel()
    {

	// TODO: Add ValidationExample.Cancel implementation
    }

    public void pause(boolean flag)
    {
	// TODO: Add ValidationExample.Pause implementation
    }

    public void onStart(Object type, long start, long stop, boolean cancelAllowed, String message)
    {

	// TODO: Add ValidationExample.OnStart implementation
    }

    public boolean onProgress(Object type, long rate, Object[] parameters)
    {

	IMemo memo = (IMemo) parameters[0];
	String action = (String) parameters[1];

	if ((memo != null) && (action.equals("Add")))
	{

	    synchronized (m_memoList)
	    {
		m_memoList.add(memo);
	    }
	}
	return true;
    }

    public void onStop(Object type, String message)
    {

	// TODO: Add ValidationExample.OnStop implementation
    }
}
