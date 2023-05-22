package com.code.aon.ui.fiscal.controller.mod340;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.tax.model.MOD340.MOD340Format;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.mod340.Model340Parameters;
import com.code.aon.ui.fiscal.file.MOD340Writer;
import com.code.aon.ui.util.AonUtil;

public class Mod340Controller implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Model340Parameters params;
	
	public Model340Parameters getParams() {
		if (params == null) {
			params = new Model340Parameters();
		}
		return params;
	}
	public void setParams(Model340Parameters params) {
		this.params = params;
	}
	
	public MOD340Format getFormat() {
		for (MOD340Format format : MOD340Format.values() ) {
			if ( format.getAdministration() == params.getAdministration() && params.getYear() >= format.getYear() ) {
				return format; 
			}
		}
		throw new IllegalArgumentException("No existe formato para " + params.getAdministration() + " - " + params.getYear());
	}

	public void onReset(ActionEvent event) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		getParams().setDate(c.getTime());
		getParams().setYear(c.get(Calendar.YEAR));
		getParams().setPeriod( Period.getQuarterlyPeriod( c.get(Calendar.MONTH )) );
		getParams().setFromDate(getParams().getPeriod().getStartDate(getParams().getYear()));	
		getParams().setToDate(getParams().getPeriod().getDueDate(getParams().getYear()));
		getParams().setSecurityLevel(null);
	}

	
	public void onCreateDisk(ActionEvent event)  {
		try {
			Period period = getParams().getPeriod();
			getParams().setFromDate(period.getStartDate(getParams().getYear()));	
			getParams().setToDate(period.getDueDate(getParams().getYear()));
			getParams().setDomain(AonUtil.getAuthPrincipal().getDomain());
			MOD340Writer mod340Writer = new MOD340Writer(getParams(), getFormat());
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			res.setContentType(MimeType.MIME_TXT.getName());
			res.setHeader("Content-Disposition", "attachment; filename=\"mod340.txt\";");
			res.setCharacterEncoding("iso-8859-1");
			PrintWriter writer = res.getWriter();
			try {
				mod340Writer.createMOD340(writer);
			} catch (ManagerBeanException e) {
				System.out.println( e.getMessage() );
				writer.write(e.getMessage());
			}			
			writer.flush();
			res.flushBuffer();
			ctx.responseComplete();
		} catch (	IllegalArgumentException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException( e.getMessage() , e );
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto");
			throw new AbortProcessingException( e );
		}
	}
	
}
