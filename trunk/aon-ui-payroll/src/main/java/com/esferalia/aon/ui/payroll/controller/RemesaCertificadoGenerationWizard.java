package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.empresa.EmpresaDAOFactory;
import com.esferalia.aon.payroll.core.empresa.IEmpresaDAO;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;
import com.esferalia.aon.payroll.core.empresa.RemesaCertificadoEmpresaParams;

public class RemesaCertificadoGenerationWizard implements Serializable {

	private static final long serialVersionUID = -8284038117326971930L;

	private int currentStep;
	private static final String[] STEPS = {
			"remesaCertificadoGenerationWizard_step0",
			"remesaCertificadoGenerationWizard_step1",
			"remesaCertificadoGenerationWizard_step2" };
	private RemesaCertificadoEmpresaParams params;
	private FileOutput fileOutput;
	private DataModel model;
	private DataModel selectedModel;
	private IEmpresaDAO empresaDAO;
	// private CertificateWriter certificateWriter;
	private List<IRemesaCertificadoEmpresa> listaRemesas;

	// private List<IRemesaCertificadoEmpresaDetalle> detailList;

	public RemesaCertificadoEmpresaParams getParams() {
		if (params == null) {
			params = new RemesaCertificadoEmpresaParams();
		}
		return params;
	}

	public void setParams(RemesaCertificadoEmpresaParams params) {
		this.params = params;
	}

	public List<IRemesaCertificadoEmpresa> getListaRemesas() {
		if (listaRemesas == null) {
			listaRemesas = new ArrayList<IRemesaCertificadoEmpresa>();
		}
		return listaRemesas;
	}

	public void setListaRemesas(List<IRemesaCertificadoEmpresa> listaRemesas) {
		this.listaRemesas = listaRemesas;
	}

	// public List<IRemesaCertificadoEmpresaDetalle> getDetailList() {
	// return detailList;
	// }
	// public void setDetailList(List<IRemesaCertificadoEmpresaDetalle>
	// detailList) {
	// this.detailList = detailList;
	// }
	//
	// private CertificateWriter getCertificateWriter() {
	// if (certificateWriter == null) {
	// certificateWriter = new CertificateWriter();
	// }
	// return certificateWriter;
	// }

	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public IEmpresaDAO getEmpresaDAO() {
		if (empresaDAO == null) {
			empresaDAO = EmpresaDAOFactory.getInstance().getEmpresaDAO();
		}
		return empresaDAO;
	}

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel();
		}
		return model;
		// try {
		// if (model == null) {
		// model = initializeModel();
		// }
		// } catch (PayrollException e) {
		// AonUtil.addErrorMessage(e.getMessage());
		// throw new AbortProcessingException(e);
		// }
	}

	public DataModel getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(DataModel selectedModel) {
		this.selectedModel = selectedModel;
	}

	private void initializeModel() throws PayrollException {
		List<RemesableEmpleadoCertificate> list = transformList(getEmpresaDAO()
				.getEmpleados(params));
		setModel(new ListDataModel(list));
		// return new ListDataModel(list);
	}

	private List<RemesableEmpleadoCertificate> transformList(
			List<IEmpleado> empleados) {
		List<RemesableEmpleadoCertificate> list = new ArrayList<RemesableEmpleadoCertificate>();
		for (IEmpleado e : empleados) {
			RemesableEmpleadoCertificate r = new RemesableEmpleadoCertificate();
			r.setEmpleado(e);
			list.add(r);
		}

		return list;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			onFinish(event);
		}
	}

	public void onPrevious(ActionEvent event) {
		setCurrentStep(getCurrentStep() - 1);
	}

	public String previous() {
		return STEPS[getCurrentStep()];
	}

	public String next() {
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 2);
	}

	// ***************************************************
	public void onStart(ActionEvent event) {
		setParams(null);
		setModel(null);
		setSelectedModel(null);
		setListaRemesas(null);
		setCurrentStep(0);
	}

	@SuppressWarnings("unchecked")
	private void onValidate(ActionEvent event) {
		if (!isAnyEmpleadoSelected()) {
			String msg = "Debe seleccionar algún empleado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		IEmpleado empleado;
		List<RemesableEmpleadoCertificate> list = new LinkedList<RemesableEmpleadoCertificate>();
		for (RemesableEmpleadoCertificate remesable : (List<RemesableEmpleadoCertificate>) getModel()
				.getWrappedData()) {
			if (remesable.isSelected()) {
				list.add(remesable);
				empleado = remesable.getEmpleado();
				if (!existEmpresa(empleado.getEmpresa())) {
					try {
						getListaRemesas().add(
								getEmpresaDAO().getNewRemesa(empleado,
										getParams().getFecha()));
					} catch (PayrollException e) {
						// NADA
					}
				}
			}
		}
		setSelectedModel(new ListDataModel(list));
	}

	private boolean isAnyEmpleadoSelected() {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel().getRowData();
			if(r.isSelected()){
				return true;
			}
		}
		return false;
	}

	private boolean existEmpresa(IEmpresa empresa) {
		Iterator<?> iterator = getListaRemesas().iterator();
		while (iterator.hasNext()) {
			IRemesaCertificadoEmpresa remesa = (IRemesaCertificadoEmpresa) iterator
					.next();
			if (remesa.getEmpresa().equals(empresa)) {
				return true;
			}
		}
		return false;
	}

	public void onDiskGenerate(ActionEvent event) {
		// try {
		// String loggedUser = AonUtil.getRemoteUser();
		// loggedUser = StringUtils.substringBefore(loggedUser, "@");
		// setFileOutput(getCertificateWriter().createCertificate(getRemesa(),
		// getDetailList()));
		// if (getFileOutput() != null) {
		// if (getFileOutput().getErrors().size() > 0) {
		// AonUtil.addErrorMessage("Se han producido errores en la generación del fichero.");
		// }
		// }
		// } catch (ManagerBeanException e) {
		// AonUtil.addErrorMessage(e.getMessage());
		// // No se lanza excepción, que vaya a la última página.
		// }
	}

	public void onDownloadDisk(ActionEvent event) {
		// try {
		// FacesContext faces = FacesContext.getCurrentInstance();
		// HttpServletResponse response = (HttpServletResponse)
		// faces.getExternalContext().getResponse();
		// String fileName =
		// getCertificateWriter().getCertificate().getFichero();
		// response.setContentType(MimeType.MIME_XML.getName());
		// response.setHeader("Content-disposition", "attachment; filename=\"" +
		// fileName + ".xml\";");
		//
		// ServletOutputStream output = response.getOutputStream();
		// InputStream input = new FileInputStream(getFileOutput().getFile());
		// int size = IOUtils.copy(input, output);
		// if (size > 0) {
		// response.setHeader("Content-Length", String.valueOf(size));
		// }
		// output.close();
		// input.close();
		//
		// response.flushBuffer();
		// faces.responseComplete();
		// } catch (IOException e) {
		// AonUtil.addErrorMessage(e.getMessage());
		// throw new AbortProcessingException(e);
		// }

	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}

	private void onFinish(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				for (IRemesaCertificadoEmpresa r : getListaRemesas()) {
					// r.setCodigoCcc();
					IRemesaCertificadoEmpresa remesa = getEmpresaDAO()
							.accept(r);
					List<IRemesaCertificadoEmpresaDetalle> list = getRemesaDetalleList(remesa);
					for (IRemesaCertificadoEmpresaDetalle d : list) {
						getEmpresaDAO().accept(d);
					}
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}

		onStart(event);
	}

	@SuppressWarnings("unchecked")
	private List<IRemesaCertificadoEmpresaDetalle> getRemesaDetalleList(
			IRemesaCertificadoEmpresa remesa) {
		List<IRemesaCertificadoEmpresaDetalle> list = new ArrayList<IRemesaCertificadoEmpresaDetalle>();

		for (RemesableEmpleadoCertificate d : (List<RemesableEmpleadoCertificate>) getSelectedModel()
				.getWrappedData()) {
			if (d.getEmpleado().getEmpresa().equals(remesa.getEmpresa())) {
				IRemesaCertificadoEmpresaDetalle detalle = getEmpresaDAO()
						.getNewRemesaDetalle(d.getEmpleado());
				detalle.setRemesaCertificado(remesa);
				detalle.getEmpleado().setId(d.getEmpleado().getId().intValue());
				detalle.setFechaBaja(d.getEmpleado().getFechaFin());
				detalle.setCausaSuspension(d.getCausaSuspension());
				list.add(detalle);
			}
		}
		return list;
	}

	public void onSelect(ActionEvent event) {
		setCurrentStep(1);
	}

	private void onSearch(ActionEvent event) {
		try {
			initializeModel();
		} catch (PayrollException e) {
			// NADA
		}
	}

	public void onSelectAll(ActionEvent event) {
		processAll(true);
	}

	public void onDeselectAll(ActionEvent event) {
		processAll(false);
	}

	private void processAll(boolean selected) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel()
					.getRowData();
			r.setSelected(selected);
		}
	}

}
