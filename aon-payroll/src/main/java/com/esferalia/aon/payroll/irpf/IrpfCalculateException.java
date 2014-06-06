package com.esferalia.aon.payroll.irpf;

import com.code.aon.AonVersion;

import es.aeat.pret.rw13.jaxb.AEATRetencionesError2013;

public class IrpfCalculateException extends Exception {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private AEATRetencionesError2013 error2013;

	public IrpfCalculateException(AEATRetencionesError2013 error2013) {
		this.error2013 = error2013;
	}

	public AEATRetencionesError2013 getAEATRetencionesError2013() {
		return error2013;

	}

}
