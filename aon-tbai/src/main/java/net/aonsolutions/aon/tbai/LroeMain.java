package net.aonsolutions.aon.tbai;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.security.User;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import net.aonsolutions.aon.tbai.lroe.LROE140_2_1;
import net.aonsolutions.aon.tbai.lroe.LROE240_2;
import net.aonsolutions.aon.tbai.lroe.LROEInfo;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LroeMain {
	
	public LROEResponse alta(InvoiceCommunication ic) {
		LROEResponse response = null;
		LROEInfo info = null;
		if(!ic.getInvoice().isSales()) {
			if(FiscalModelType.M140.equals(ic.getModel()) && InvoiceCommunicationOperation.REGISTER.equals(ic.getOperation())) {
				LROE140_2_1 lroe = new LROE140_2_1();
				info = LROE140_2_1.buildInfo(OperacionEnum.A_00, lroe.getEjercicio(ic.getConfiguration(), ic.getInvoice()));
				response = lroe.alta(ic);
			} else if(FiscalModelType.M240.equals(ic.getModel()) && InvoiceCommunicationOperation.REGISTER.equals(ic.getOperation())) {
				LROE240_2 lroe = new LROE240_2();
				info = LROE240_2.buildInfo(OperacionEnum.A_00, lroe.getEjercicio(ic.getConfiguration(), ic.getInvoice()));
				response = lroe.alta(ic.getConfiguration(), ic.getCompany(), ic.getInvoice());
			}
		}
		if(response != null) {
			LroeData.saveResponse(ic.getCompany().getDomain(), new User().setLogin(""), ic.getInvoice(), response, info);
		}
		return response;
	}
}
