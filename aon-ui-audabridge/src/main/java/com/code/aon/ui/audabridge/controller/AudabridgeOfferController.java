package com.code.aon.ui.audabridge.controller;



import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.audabridge.AudaBridgeException;
import com.code.aon.ui.audabridge.AudaBridgeManager;
import com.code.aon.ui.audabridge.CreateAssessmentRequest;
import com.code.aon.ui.audabridge.response.CalculationDataResponse;
import com.code.aon.ui.audabridge.response.Operacion;
import com.code.aon.ui.audabridge.response.Pieza;
import com.code.aon.ui.audabridge.response.Pintura;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;


public class AudabridgeOfferController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private AudabridgeParametersController paramsController;
	private CalculationDataResponse calculationData;
	private CreateAssessmentRequest assessment;
	private Offer offer;
	
	public AudabridgeParametersController getParamsController() {
		if (paramsController == null) {
			paramsController = (AudabridgeParametersController) AonUtil.getRegisteredBean( AudabridgeParametersController.AUDABRIDGE_PARAMS_BEAN_NAME );			
		}
		return paramsController;
	}

	public CalculationDataResponse getCalculationData() {
		return calculationData;
	}
	public void setCalculationData(CalculationDataResponse calculationData) {
		this.calculationData = calculationData;
	}
	
	public CreateAssessmentRequest getAssessment() {
		return assessment;
	}
	public void setAssessment(CreateAssessmentRequest assessment) {
		this.assessment= assessment;
	}
	
	public Offer getOffer() {
		return offer;
	}
	public void setOffer(Offer offer) {
		this.offer = offer;
	}
	
	private void initializeAssessmentRequest() {
		setAssessment(new CreateAssessmentRequest());
		getAssessment().setCustomerId( getParamsController().getCustomerId() );
		getAssessment().setBodyworkRate( getParamsController().getBodyworkHourRate() );
		getAssessment().setLabourRate( getParamsController().getLabourHourRate() );
		getAssessment().setPaintRate( getParamsController().getPaintHourRate() );
		
	}
	
	public void onCreateAssessment(ActionEvent event) {
		try {
			initializeAssessmentRequest();
			boolean ok = true;
			ok = checkAccessKey();
			ok = ok && checkCustomerId();
			if (!ok) {
				throw new AbortProcessingException("Error en Parámetros");	
			}
			AudaBridgeManager manager = new AudaBridgeManager();
			getAssessment().setReferenceNumber( getOffer().getReferenceCode() );
			String wan = manager.createAssessmentRequest(getParamsController().getAccessKey(), getAssessment());
			BasicController c = (BasicController) FormUtil.getController(ICommercialConstants.OFFER_CONTROLLER_NAME);
			Offer offer = (Offer) c.getTo();
			offer.setExternalReference(wan);
			c.accept(event);
		} catch (AudaBridgeException e) {
			String msg = e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	

	public void onCallAudaGate(ActionEvent event) {
		try {
			boolean ok = true;
			ok = checkAccessKey();
			ok = ok && checkAudaPlusPath();
			if (!ok) {
				throw new AbortProcessingException("Error en Parámetros");	
			}
			AudaBridgeManager manager = new AudaBridgeManager();
			String token = manager.getToken(getParamsController().getAccessKey(), getOffer().getExternalReference());
			FacesContext faces = FacesContext.getCurrentInstance();
	        HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
	        response.setContentType("application/bat");
	        response.setHeader("Content-disposition", "attachment; filename=\"audagate.bat\"");
	        ServletOutputStream output = response.getOutputStream();
	        StringReader reader = new StringReader(getParamsController().getAudaPlusPath() + " " + token);
	        IOUtils.copy(reader, output);
	        output.close();
	        reader.close();
	        response.flushBuffer();
	        faces.responseComplete();
		} catch (IOException e) {
			String msg = "Unable to load defaultParameters";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (AudaBridgeException e) {
			String msg = e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onImportAssessment(ActionEvent event) {
		boolean ok = true;
		ok = checkAccessKey();
		ok = ok && checkOperationItem();
		ok = ok && checkSparePartItem();
		ok = ok && checkDiscountItem();
		ok = ok && checkPaintItem();
		if (!ok) {
			throw new AbortProcessingException("Error en Parámetros");	
		}
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				
				AudaBridgeManager manager = new AudaBridgeManager();
				CalculationDataResponse data = manager.getCalculationData(getParamsController().getAccessKey(), getOffer().getExternalReference());
				setCalculationData(data);
				if (data.getTotalGeneral() == null) {
					throw new AbortProcessingException("La valoración AudaTex está vacia.");
				}
				createOfferDetails();
				createOfferAttachments();
				IController c = FormUtil.getController(ICommercialConstants.OFFER_DETAIL_CONTROLLER_NAME);
				c.onSearch(event);
				c = FormUtil.getController(ICommercialConstants.OFFER_ATTACH_CONTROLLER_NAME);
				c.onSearch(event);
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception ex) {
				ex.printStackTrace();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					// nothing
				}
				String msg = "Error durante la importación de la valoración:  " + ex.getMessage();
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private void createOfferDetails() throws ManagerBeanException {
		Offer offer = getOffer();
		boolean detailedIntegration = (getParamsController().getIntegrationLevel() == 1);
		if (detailedIntegration) {
			createDetailedOfferDetails(offer);		
		} else {
			createBasicOfferDetails(offer);
		}
	}
	private void createBasicOfferDetails(Offer offer) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(OfferDetail.class);
		int line = 0;
		if (getCalculationData().getListaPiezas() != null && getCalculationData().getListaPiezas().size() > 1) {
			Item part = getParamsController().getSparePartItem();
			OfferDetail detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setItem(part);
			detail.setDescription(part.getProduct().getName());
			detail.setQuantity(1);
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setPrice( getCalculationData().getTotalGeneral().getTotalPiezas() );
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);
		}
		if (getCalculationData().getListaPintura() != null && getCalculationData().getListaPintura().size() > 1) {
			Item paint = getParamsController().getPaintItem();
			OfferDetail detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setItem(paint);
			detail.setDescription(paint.getProduct().getName());
			detail.setQuantity(1);
			detail.setPrice( getCalculationData().getTotalGeneral().getTotalPintura() );
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);
		}
		if (getCalculationData().getListaOperaciones() != null && getCalculationData().getListaOperaciones().size() > 1) {
			Item oper = getParamsController().getOperationItem();
			OfferDetail detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setItem(oper);
			detail.setDescription(oper.getProduct().getName());
			detail.setQuantity(1);
			detail.setPrice( getCalculationData().getTotalGeneral().getTotalMo() );
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);
		}
		
		if (getCalculationData().getTotalGeneral().getDescuentos() != 0) {
			Item disc = getParamsController().getDiscountItem();
			OfferDetail detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setItem(disc);
			detail.setDescription(disc.getProduct().getName());
			detail.setQuantity(1);
			detail.setPrice( getCalculationData().getTotalGeneral().getDescuentos() );
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);
		}
	}
	
	private void createDetailedOfferDetails(Offer offer) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(OfferDetail.class);
		int line = 0;
		if (getCalculationData().getListaPiezas() != null && getCalculationData().getListaPiezas().size() > 1) {
			OfferDetail detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setDescription("PIEZAS");
			detail.setQuantity(0);
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setPrice( 0 );
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);

			Item part = getParamsController().getSparePartItem();
			for (Pieza pieza: getCalculationData().getListaPiezas()) {
				detail = new OfferDetail();
				detail.setOffer(offer);
				detail.setLine(++line);
				detail.setItem(part);
				detail.setDescription(pieza.getNumeroPieza() + " - "  + pieza.getDescripcion());
				detail.setQuantity(1);
				detail.setPrice(pieza.getPrecio());
				detail.setDiscountExpression( new DiscountExpression("0.0"));
				detail.setStatus(OfferDetailStatus.PENDING);
				bean.insert(detail);
			}
		}
		
		if (getCalculationData().getListaPintura() != null && getCalculationData().getListaPintura().size() > 1) {
			OfferDetail detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setDescription("PINTURA");
			detail.setQuantity(0);
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setPrice( 0 );
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);
			Item paint = getParamsController().getPaintItem();
			for (Pintura pintura: getCalculationData().getListaPintura()) {
				detail = new OfferDetail();
				detail.setOffer(offer);
				detail.setLine(++line);
				detail.setItem(paint);
				detail.setDescription(pintura.getDescripcionPintura() + " ("+ pintura.getDescripcionPieza()+")");
				detail.setQuantity(1);
				detail.setPrice(pintura.getImporteMaterial());
				detail.setDiscountExpression( new DiscountExpression("0.0"));
				detail.setStatus(OfferDetailStatus.PENDING);
				bean.insert(detail);
			}
		}
		
		if (getCalculationData().getListaOperaciones() != null && getCalculationData().getListaOperaciones().size() > 1) {
			OfferDetail detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setDescription("MANO DE OBRA");
			detail.setQuantity(0);
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setPrice( 0 );
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);

			Item oper = getParamsController().getOperationItem();
			for (Operacion operacion: getCalculationData().getListaOperaciones()) {
				detail = new OfferDetail();
				detail.setOffer(offer);
				detail.setLine(++line);
				detail.setItem(oper);
				detail.setDescription(operacion.getNumeroOperacion() + " - "  + operacion.getDescripcion());
				detail.setQuantity(1);
				detail.setPrice(operacion.getImporte());
				detail.setDiscountExpression( new DiscountExpression("0.0"));
				detail.setStatus(OfferDetailStatus.PENDING);
				bean.insert(detail);
			}
		}
		
		if (getCalculationData().getTotalGeneral().getDescuentos() != 0) {
			OfferDetail detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setDescription("OTROS CONCEPTOS");
			detail.setQuantity(0);
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setPrice( 0 );
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);

			Item disc = getParamsController().getDiscountItem();
			detail = new OfferDetail();
			detail.setOffer(offer);
			detail.setLine(++line);
			detail.setItem(disc);
			detail.setDescription(disc.getProduct().getName());
			detail.setQuantity(1);
			detail.setPrice( getCalculationData().getTotalGeneral().getDescuentos() );
			detail.setDiscountExpression( new DiscountExpression("0.0"));
			detail.setStatus(OfferDetailStatus.PENDING);
			bean.insert(detail);
		}
	}
	
	private void createOfferAttachments() throws AudaBridgeException, IOException, ManagerBeanException {
		Offer offer = getOffer();
		AudaBridgeManager manager = new AudaBridgeManager();
		IManagerBean bean = BeanManager.getManagerBean(OfferAttachment.class);
		OfferAttachment attach = new OfferAttachment();
		attach.setOffer(offer);
		Reader reader = manager.getXMLRequest(getParamsController().getAccessKey(), getOffer().getExternalReference());
		byte[] arr = IOUtils.toByteArray(reader); 
		attach.setData(arr);
		attach.setDescription("Valoración XML AudaTex " + getOffer().getExternalReference());
		attach.setMimeType(MimeType.MIME_XML);
		bean.insert(attach);
		
		attach = new OfferAttachment();
		attach.setOffer(offer);
		InputStream in = manager.getPDFRequest(getParamsController().getAccessKey(), getOffer().getExternalReference());
		arr = IOUtils.toByteArray(in); 
		attach.setData(arr);
		attach.setDescription("Valoración PDF AudaTex " + getOffer().getExternalReference());
		attach.setMimeType(MimeType.MIME_PDF);
		bean.insert(attach);
		
	}
	
	private boolean checkAccessKey() {
		if (StringUtils.isBlank(getParamsController().getAccessKey())) {
			String msg = "No se ha definido la clave de acceso en los parámetros AudaBridge.";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		return true;
	}
	
	private boolean checkAudaPlusPath() {
		if (StringUtils.isBlank(getParamsController().getAudaPlusPath())) {
			String msg = "No se ha definido el path a AudaGate en los parámetros AudaBridge.";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		return true;
	}
	
	private boolean checkCustomerId() {
		if (StringUtils.isBlank(getAssessment().getCustomerId())) {
			String msg = "No se ha definido el número de abonado en los parámetros AudaBridge.";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		return true;
	}

	private boolean checkOperationItem() {
		if (getParamsController().getOperationItem() == null || getParamsController().getOperationItem().getId() == null) {
			AonUtil.addErrorMessage("No existe definición para las Operaciones en los parámetros AudaBridge'");
			return false;
		}
		return true;
	}
	
	private boolean checkSparePartItem() {
		if (getParamsController().getSparePartItem() == null || getParamsController().getSparePartItem().getId() == null) {
			AonUtil.addErrorMessage("No existe definición para las Piezas en los parámetros AudaBridge'");
			return false;
		}
		return true;
	}
	
	private boolean checkPaintItem() {
		if (getParamsController().getPaintItem() == null || getParamsController().getPaintItem().getId() == null) {
			AonUtil.addErrorMessage("No existe definición para la Pintura en los parámetros AudaBridge'");
			return false;
		}
		return true;
	}
	
	private boolean checkDiscountItem() {
		if (getParamsController().getDiscountItem() == null || getParamsController().getDiscountItem().getId() == null) {
			AonUtil.addErrorMessage("No existe definición para los Descuentos en los parámetros AudaBridge'");
			return false;
		}
		return true;
	}
	
	
// ************************************************************************
/*
	public void onReport(ActionEvent event) {
		try {
			AudaBridgeManager manager = new AudaBridgeManager();
			InputStream input = manager.getPDFRequest(getAccessKey(), getOffer().getExternalReference());
			
			FacesContext faces = FacesContext.getCurrentInstance();
	        HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
	
	        response.setContentType( MimeType.MIME_PDF.getName() );
	        response.setHeader("Content-disposition", "attachment; filename=\"valoracion_"+ getOffer().getExternalReference() +".pdf\"");
	
	        ServletOutputStream output = response.getOutputStream();
	        IOUtils.copy(input, output);
	        output.close();
	        input.close();
	        response.flushBuffer();
	        faces.responseComplete();

		} catch (AudaBridgeException e) {
			String msg = e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (IOException e) {
			String msg = e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

*/
}


