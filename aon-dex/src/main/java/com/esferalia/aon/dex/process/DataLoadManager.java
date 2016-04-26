package com.esferalia.aon.dex.process;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.dex.IDataLoadConstants;
import com.esferalia.aon.dex.shared.Identity;
import com.esferalia.aon.dex.shared.PosShiftDex;

public class DataLoadManager extends CommonLoadManager implements IDataLoadConstants {

	public String processDocument(String documentXml) {
		try {
			String identityXml = IDENTITY_OPEN_TAG + StringUtils.substringBetween(documentXml, IDENTITY_OPEN_TAG, IDENTITY_CLOSE_TAG) + IDENTITY_CLOSE_TAG;
			StringReader reader = new StringReader(identityXml);
			JAXBContext context = JAXBContext.newInstance(Identity.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Identity identity = (Identity)unmarshaller.unmarshal(reader);
			if (POS_SHIFT_TOKEN.equals(identity.getToken())) {
				reader = new StringReader(documentXml);
				context = JAXBContext.newInstance(PosShiftDex.class);
				unmarshaller = context.createUnmarshaller();
				PosShiftDex posShiftDex = (PosShiftDex)unmarshaller.unmarshal(reader);

				PosShiftLoadManager loadManager = new PosShiftLoadManager();
				return loadManager.processPosShift(posShiftDex, identity.getDomain());
			}
			return documentError("Token no valido!", 0, 0);
		} catch (JAXBException ex) {
			return documentError(ex.getMessage(), 0, 0);
		}
	}

}