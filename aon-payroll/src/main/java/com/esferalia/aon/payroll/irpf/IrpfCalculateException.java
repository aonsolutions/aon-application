package com.esferalia.aon.payroll.irpf;

import com.code.aon.AonVersion;

import es.aeat.pret.rw13.jaxb.AEATRetencionesError2013;
import net.aonsolutions.core.aeat.v2020.jaxb.AEATRetencionesError2020;
import net.aonsolutions.core.aeat.v2021.jaxb.AEATRetencionesError2021;
import net.aonsolutions.core.aeat.v2022.jaxb.AEATRetencionesError2022;
import net.aonsolutions.core.aeat.v2023.jaxb.AEATRetencionesError2023;

public class IrpfCalculateException extends Exception {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private AEATRetencionesError2013 error2013;
	private AEATRetencionesError2020 error2020;
	private AEATRetencionesError2021 error2021;
	private AEATRetencionesError2022 error2022;
	private AEATRetencionesError2023 error2023;

	public IrpfCalculateException(AEATRetencionesError2023 error2023) {
		this.error2023 = error2023;
	}

	public IrpfCalculateException(AEATRetencionesError2022 error2022) {
		this.error2022 = error2022;
	}

	public IrpfCalculateException(AEATRetencionesError2021 error2021) {
		this.error2021 = error2021;
	}

	public IrpfCalculateException(AEATRetencionesError2020 error2020) {
		this.error2020 = error2020;
	}

	public IrpfCalculateException(AEATRetencionesError2013 error2013) {
		this.error2013 = error2013;
	}

	public AEATRetencionesError2013 getAEATRetencionesError2013() {
		return error2013;
	}

	public AEATRetencionesError2020 getAEATRetencionesError2020() {
		return error2020;
	}
	
	public AEATRetencionesError2021 getAEATRetencionesError2021() {
		return error2021;
	}
	
	public AEATRetencionesError2022 getAEATRetencionesError2022() {
		return error2022;
	}
	
	public AEATRetencionesError2023 getAEATRetencionesError2023() {
		return error2023;
	}
}
