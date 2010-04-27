package com.esferalia.aon.ui.payroll.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.enumeration.TipoOperacionIT;
import com.esferalia.aon.payroll.core.it.IParteConfirmacionIT;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.core.it.ParteITParams;
import com.esferalia.aon.payroll.core.remesa.IRemesaINSS;
import com.esferalia.aon.payroll.core.remesa.IRemesaParteIT;
import com.esferalia.aon.ui.payroll.file.FDIWriter;

public class RemesaITWizard implements Serializable {

	private static final long serialVersionUID = 7495900117871108096L;

	private IParteITDAO parteITDAO;
	private int currentStep;
	private static final String[] STEPS = { "remesaITWizard_step0", "remesaITWizard_step1", "remesaITWizard_step2", "remesaITWizard_step3", "remesaITWizard_step4" };
	private ParteITParams params;
	private DataModel model;
	private DataModel selectedModel;
	private FileOutput fileOutput;
	private FDIWriter fdiWriter;
	IRemesaINSS remesaINSS;
	
	public void setRemesaINSS(IRemesaINSS remesaINSS) {
		this.remesaINSS = remesaINSS;
	}
	public IRemesaINSS getRemesaINSS(){
		return remesaINSS;
	}

	public ParteITParams getParams() {
		if (params == null) {
			setParams(new ParteITParams());
		}
		return params;
	}

	public void setParams(ParteITParams params) {
		this.params = params;
	}

	private FDIWriter getFDIWriter() {
		if (fdiWriter == null) {
			fdiWriter = new FDIWriter();
		}
		return fdiWriter;
	}

	public DataModel getModel() {
		try {
			if (model == null) {
				model = initializeModel();
			}
			return model;
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private DataModel initializeModel() throws PayrollException {
		List<RemesableIT> pList = null;
		List<RemesableIT> cList = null;
		if (getParams().isAlta() || getParams().isBaja()) {
			pList = new LinkedList<RemesableIT>(); 
			List<IParteIT> partes = getParteITDAO().getPartes(getParams());
			for (IParteIT parteIT : partes) {
				if (!parteIT.isBajaProcesada()) {
					RemesableIT r = new RemesableIT();
					r.setOperacion(TipoOperacionIT.BAJA);
					r.setParteIT(parteIT);
					pList.add(r);
				}
				if (!parteIT.isAltaProcesada()) {
					RemesableIT r = new RemesableIT();
					r.setOperacion(TipoOperacionIT.ALTA);
					r.setParteIT(parteIT);
					pList.add(r);
				}
			}
		}
		if (getParams().isConfirmacion()) {
			cList = new LinkedList<RemesableIT>();
			List<IParteConfirmacionIT> confs = getParteITDAO().getPartesConfirmacion(getParams());
			for (IParteConfirmacionIT conf : confs) {
				RemesableIT r = new RemesableIT();
				r.setOperacion(TipoOperacionIT.CONFIRMACION);
				r.setConfirmacionIT(conf);
				r.setParteIT(conf.getParteIT());
				cList.add(r);
			}
		}
		List<RemesableIT> orderedList = null;
		if (cList != null && pList != null) {
			if (pList.size()==0) {
				orderedList = cList;
			} else if (pList.size()==0) {
				orderedList = pList;
			} else {
				orderedList = sortList(pList,cList);	
			}
		} else {
			orderedList = pList;
			if (pList == null) {
				orderedList = cList;
			}
			if (orderedList == null) {
				orderedList = new LinkedList<RemesableIT>();
			}
		}
		return new ListDataModel(orderedList);
	}

	private List<RemesableIT> sortList(List<RemesableIT> pList, List<RemesableIT> cList) {
		List<RemesableIT> list = new LinkedList<RemesableIT>();
		int x = 0;
		int y = 0;
		while (list.size() < (pList.size()+cList.size())) {
			RemesableIT a = pList.get(x);
			RemesableIT b = cList.get(y);
			String empresaA = a.getParteIT().getEmpleado().getActividad().getEmpresa().getName();
			int personaA = a.getParteIT().getEmpleado().getPersona().getId();
			long fechaA = a.getParteIT().getFechaBaja().getTime();
			String empresaB = b.getParteIT().getEmpleado().getActividad().getEmpresa().getName();
			int personaB = b.getParteIT().getEmpleado().getPersona().getId();
			long fechaB = b.getParteIT().getFechaBaja().getTime();
			boolean addA = false;
			boolean addB = false;
			if ( empresaA.compareToIgnoreCase(empresaB)<0 ) {
				addA = true;
			} else if ( empresaA.compareToIgnoreCase(empresaB)>0 ) {
				addB = true;
			} else {
				if ( personaA < personaB ) {
					addA = true;
				} else if ( personaA > personaB ) {
					addB = true;
				} else {
					if ( fechaA < fechaB ) {
						addB = true;
					} else if ( fechaA > fechaB ) {
						addB = true;
					} else {
						addA = true;
						addB = true;
					}
				}
			}
			if (addA) {
				list.add(a);
				++x;
			}
			if (x == pList.size()) {
				list.addAll(cList.subList(y,cList.size()));
			} else {
				if (addB) {
					list.add(b);
					++y;
				}
				if (y == cList.size()) {
					list.addAll(pList.subList(x,pList.size()));
				}
			}
		};
		return list;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public DataModel getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(DataModel selectedModel) {
		this.selectedModel = selectedModel;
	}

	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	// *********************************************
	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			save();
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 3) {
			onDiskGenerate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 4) {
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
		return (getCurrentStep() < 4);
	}

	// ***************************************************
	public void onStart(ActionEvent event) {
		setParams(null);
		setModel(null);
		setSelectedModel(null);
		setCurrentStep(0);
	}

	private void onSearch(ActionEvent event) {
		if (!getParams().isAlta() && !getParams().isBaja() && !getParams().isConfirmacion()) {
			String msg = "Realice alguna selección";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		model = null;
	}

	@SuppressWarnings("unchecked")
	private void onValidate(ActionEvent event) {
		List<RemesableIT> list = new LinkedList<RemesableIT>();
		for (RemesableIT remesable : (List<RemesableIT>) getModel().getWrappedData()) {
			if (remesable.isSelected()) {
				list.add(remesable);
			}
		}
		setSelectedModel(new ListDataModel(list));
	}
	
	@SuppressWarnings("unchecked")
	public void onRemoveSelected(ActionEvent event) {
		RemesableIT r = (RemesableIT) getSelectedModel().getRowData();
		r.setSelected(false);
		List<RemesableIT> list = (List<RemesableIT>) getSelectedModel().getWrappedData();
		list.remove(r);
	}
	
	@SuppressWarnings("unchecked")
	public void onDiskGenerate(ActionEvent event) {
		try {
			String loggedUser = AonUtil.getRemoteUser();
			loggedUser = StringUtils.substringBefore(loggedUser, "@");
			List<RemesableIT> list = (List<RemesableIT>) getSelectedModel().getWrappedData();
			setFileOutput(getFDIWriter().createFDI(list, loggedUser));
			if (getFileOutput() != null) {
				if (getFileOutput().getErrors().size() > 0) {
					AonUtil.addErrorMessage("Se han producido errores en la generación del fichero.");
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			// No se lanza excepción, que vaya a la última página.
		}
	}
	
	@SuppressWarnings("unchecked")
	public void save() {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		List<RemesableIT> list = (List<RemesableIT>) getSelectedModel().getWrappedData();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				IRemesaINSS remesa = getParteITDAO().initializeRemesa();	
				setRemesaINSS(getParteITDAO().accept(remesa));
				for (RemesableIT r: list){
					IRemesaParteIT remesaParteIT = r.getNewRemesaParteIT();
					remesaParteIT.setRemesaINSS(remesa);
					getParteITDAO().accept(remesaParteIT);	
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
		try {
			for (RemesableIT r: list){
				r.setParteProcesado();
			}
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onDownloadDisk(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = getFDIWriter().getEti().getFichero() + ".FDI";
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");

			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(getFileOutput().getFile());
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();

			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}

	private void onFinish(ActionEvent event) {
		onStart(event);
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
			RemesableIT r = (RemesableIT) getModel().getRowData();
			r.setSelected(selected);
		}
	}

	public IParteITDAO getParteITDAO() {
		if (parteITDAO == null) {
			parteITDAO = ParteITDAOFactory.getInstance().getParteITDAO();
		}
		return parteITDAO;
	}
	
	public List<IRemesaINSS> getRemesaINSSList(){
		try {
//			RemesaINSSParams params = new RemesaINSSParams();
//			params.setId(null);
//			return getParteITDAO().getRemesaINSS(params);
			return getParteITDAO().getRemesaINSS(null);
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

}
