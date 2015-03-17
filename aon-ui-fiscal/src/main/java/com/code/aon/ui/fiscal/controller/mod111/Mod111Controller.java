package com.code.aon.ui.fiscal.controller.mod111;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.file.tax.model.MOD111.MOD111Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.IFiscalConstants;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.fiscal.aeat.AeatUtils;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD111Writer;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod111Controller extends FiscalModelController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M111;
	}
	
	@Override
	public boolean isDifEnabled() {
		return true;
	}

	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD111Writer mod111Writer = new MOD111Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD111Format format = MOD111Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod111Writer.createMOD111(list, format) );
		    if (getFileOutput() != null) {
		    	if (getFileOutput().getErrors().size() > 0) {
		    		AonUtil.addErrorMessageFromBundle(FINANCE_BATCH_DISK_ERROR);
		        }
		    }
		    if (isAeatValidable()) {
		    	validateAeatFile();	
		    }
		} catch (IllegalArgumentException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}

	@Override
	public MimeType getMimeType() {
		FiscalModel fm = (FiscalModel) getTo();
		MOD111Format format = MOD111Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}	

	@Override
	public void initialize() throws AonException {
		super.initialize();
		FiscalModel to = (FiscalModel) getTo();
		IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), IFiscalConstants.FS_MOD111_RECEIVER_COUNT);
		List<ITransferObject> list = bean.getList(criteria);
		to.setReadRetentionFromAccount(false);
		int i = 1;
		if (list != null && list.size() > 0 ) {
			ApplicationParameter appParam = (ApplicationParameter) list.get(0);
			try {
				i = Integer.parseInt( appParam.getValue() );
				to.setReadRetentionFromAccount(true);
				to.setReceiverCount(i);
				to.setReceiverInKindCount(1);
			} catch (NumberFormatException e) {
				// Nothing;
			}
		}
		
	}
	
	@Override
	protected String getFormPage() {
		return "mod111_form";
	}

	public String getInfoMessage() {
		return "Para la generaci\u00F3n del modelo, se leer\u00E1n los apuntes de tipo n\u00F3mina, y de ellos, "
			 + "las l\u00ED­neas de percepciones monetarias (y en especie) computar\u00E1n el saldo de las "
			 + "diferentes percepciones  y las l\u00ED­neas de las retenciones monetarias (y en especie) "
			 + "computar\u00E1n el saldo de las diferentes retenciones.  Para identificar las percepciones"
			 + " y retenciones del apunte, las cuentas deber\u00E1n coincidir con las indicadas en los "
			 + "par\u00E1metros contables a tal efecto.";
	}
	
	public String aeatReport() {
		FiscalModel fiscalModel = (FiscalModel) getTo();
		if (fiscalModel.getYear() > 2014) {
			try {
				if (getFileOutput() == null) {
					onCreateDisk(null);
				}
				InputStream input = getFileOutput().getFile() != null
						?new FileInputStream(getFileOutput().getFile())
						:new ByteArrayInputStream(getFileOutput().getContent());

				FacesContext faces = FacesContext.getCurrentInstance();
	            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
	            String fileName = getAutomaticFileName();
	            response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\";");
				AeatUtils.printMod111(getDeclaration().getHeader().getYear(),
						getDeclaration().getHeader().getPeriod(),
						input,response.getOutputStream());					
		        response.flushBuffer();
		        faces.responseComplete();
		        return null;
			} catch (FileNotFoundException e) {
				AonUtil.addErrorMessage(e.getMessage()); 
				throw new AbortProcessingException(e.getMessage(),e);
			} catch (UnsupportedEncodingException e) {
				AonUtil.addErrorMessage(e.getMessage()); 
				throw new AbortProcessingException(e.getMessage(),e);
			} catch (IOException e) {
				AonUtil.addErrorMessage(e.getMessage()); 
				throw new AbortProcessingException(e.getMessage(),e);
			} catch (AonException e) {
				AonUtil.addErrorMessage(e.getMessage()); 
				throw new AbortProcessingException(e.getMessage(),e);
			}
		}
		return super.aeatReport();
	}
	
}
