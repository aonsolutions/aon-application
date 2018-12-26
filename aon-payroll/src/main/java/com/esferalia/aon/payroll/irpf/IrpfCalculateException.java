package com.esferalia.aon.payroll.irpf;

import com.code.aon.AonVersion;

import es.aeat.pret.rw13.jaxb.AEATRetencionesError2013;
import net.aonsolutions.core.aeat.jaxb.AEATRetencionesError2016;
import net.aonsolutions.core.aeat.v2017.jaxb.AEATRetencionesError2017;
import net.aonsolutions.core.aeat.v2018.jaxb.AEATRetencionesError2018;
import net.aonsolutions.core.aeat.v2019.jaxb.AEATRetencionesError2019;

public class IrpfCalculateException extends Exception {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private AEATRetencionesError2013 error2013;
	private AEATRetencionesError2016 error2016;
	private AEATRetencionesError2017 error2017;
	private AEATRetencionesError2018 error2018;
	private AEATRetencionesError2019 error2019;

	public IrpfCalculateException(AEATRetencionesError2019 error2019) {
		this.error2019 = error2019;
	}

	public IrpfCalculateException(AEATRetencionesError2018 error2018) {
		this.error2018 = error2018;
	}


	public IrpfCalculateException(AEATRetencionesError2017 error2017) {
		this.error2017 = error2017;
	}

	public IrpfCalculateException(AEATRetencionesError2016 error2016) {
		this.error2016 = error2016;
	}

	public IrpfCalculateException(AEATRetencionesError2013 error2013) {
		this.error2013 = error2013;
	}

	public AEATRetencionesError2013 getAEATRetencionesError2013() {
		return error2013;
	}
	
	public AEATRetencionesError2016 getAEATRetencionesError2016() {
		return error2016;
	}
	
	public AEATRetencionesError2017 getAEATRetencionesError2017() {
		return error2017;
	}

	public AEATRetencionesError2018 getAEATRetencionesError2018() {
		return error2018;
	}

	public AEATRetencionesError2019 getAEATRetencionesError2019() {
		return error2019;
	}
}
