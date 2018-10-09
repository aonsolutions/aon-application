package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfData;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfRegularization;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

class IrpfDocuments extends AbstractSpinnable<IDocument> implements IDocument {

	private List<Irpf> irpfs;

	private DomainEmployeesServiceAsync employeesService;

	public IrpfDocuments(Irpf irpf, DomainEmployeesServiceAsync employeesService) {
		this(Arrays.asList(irpf), employeesService);
	}

	public IrpfDocuments(List<Irpf> irpfs,
			DomainEmployeesServiceAsync employeesService) {
		this.irpfs = irpfs;
		this.employeesService = employeesService;
		last();
	}

	@Override
	public int size() {
		return irpfs.size();
	}

	@Override
	public IDocument current() {
		return this;
	}

	@Override
	public void print() {
		download();
	}

	@Override
	public void download() {
		download("pdf");
	}

	@Override
	public void download(String format) {
		Irpf irpf = irpfs.get(getCurrentIndex());
		IrpfData irpfData = irpf.getIrpfData();
		IrpfResult irpfResult = irpf.getIrpfResult();
		IrpfRegularization irpfRegularization = irpf.getIrpfRegularization();
		String printURL = URL.encode(GWT.getModuleBaseURL()
				+ "irpf/"
				+ irpfData.getId()
				+ "_"
				+ irpfResult.getId()
				+ (irpfRegularization != null ? "_"
						+ irpfRegularization.getId() : "") + "." + format);
		Window.open(printURL, "_blank", null);
	}

	@Override
	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		Irpf irpf = irpfs.get(getCurrentIndex());
		employeesService.getIrpfReceiptHTML(irpf, zoom, callback);
	}

	@Override
	public String[] getSupportedFormats() {
		// TODO Auto-generated method stub
		return new String[] {};
	}

	public List<Irpf> getIrpfs() {
		return irpfs;
	}

	public void setCurrent(Irpf irpf) {
		setCurrentIndex(irpfs.indexOf(irpf));
	}
}