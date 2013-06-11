package com.esferalia.aon.payroll.irpf;

import es.aeat.pret.rw13.jaxb.AEATRetencionesError2013;

public class IrpfCalculateException extends Exception {

	private AEATRetencionesError2013 error2013;

	public IrpfCalculateException(AEATRetencionesError2013 error2013) {
		this.error2013 = error2013;
	}

	public AEATRetencionesError2013 getAEATRetencionesError2013() {
		return error2013;

	}

}
