package com.code.aon.ui.payroll.controller;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

/**
 * Envia a PDF la colección del controlador que se está mostrando en pantalla. Necesita de 2 parametros 
 * en la petición que llega del cliente:
 * 
 * <f:param name="reportKey" value="tiposDocumentoList" /> --> Indica el identificador del listado a generar
 * 	definido en el fichero report-config.xml y obtiene también el nombre del controlador, en este caso se 
 * 	trata de tiposDocumento.
 * 
 * <f:param name="outputFormat" value="PDF" /> --> Indica el formato de salida.
 * 
 * @author iayerbe
 *
 */
public class Printer implements ICollectionProvider {

	/** Printer Logger instance. */
	private static final Logger LOGGER = Logger.getLogger( Printer.class.getName() );

	IController controller;

	/**
	 * Print selected controller list.
	 * 
	 * @param event
	 */
	public void print(ActionEvent event) {
		ReportManager rm = new ReportManager();
		ensureParams( rm, FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap() );
		try {
			rm.onExecute();
		} catch (ReportException e) {
			String message = e.getMessage();
			LOGGER.severe( message );
			AonUtil.addErrorMessage( message );
		} catch (DAOException e) {
			String message = e.getMessage();
			LOGGER.severe( message );
			AonUtil.addErrorMessage( message );
		}
	}

	@SuppressWarnings("deprecation")
	private void ensureParams(ReportManager rm, Map<String, String> parameters) {
		String key = parameters.get("reportKey");
		if (key != null) {
			rm.setReportKey(key);
			String controllerName = key.substring( 0, key.indexOf( "List" ) );
			FacesContext ctx = FacesContext.getCurrentInstance();
			controller = 
				(IController) ctx.getApplication().getVariableResolver().resolveVariable( ctx, controllerName );
		}
		String of = parameters.get("outputFormat");
		if (of == null) {
			rm.setOutputFormat(OutputFormat.PDF);
		} else {
			OutputFormat ouf = OutputFormat.get(of);
			if (ouf != null) {
				rm.setOutputFormat(ouf);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
    	try {
			return (Collection) controller.getModel().getWrappedData();
		} catch (ManagerBeanException e) {
			LOGGER.severe( e.getMessage() );
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
    	if (!forceRefresh) {
    		return this.getCollection();
    	}
    	return (Collection) controller.getModel().getWrappedData();
	}

}
