package com.esferalia.aon.gwt.connect.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.connect.client.ConnectService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.mod200.api.FISCAL;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.mod200.api.model.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.mod200.server.format.Mod2002013Reader;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.thirdparty.guava.common.io.Files;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Connect Servlet", urlPatterns = { "/aon_gwt_connect/Connect" })
public class ConnectServiceImpl extends AonRemoteServiceServlet implements
		ConnectService {

	@Override
	public List<String> importZippedMod2002013(String domainName, int domain)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();

		List<String> messages = new LinkedList<String>();

		try {
			File file = (File) request.getSession().getAttribute(
					"aonMod200BOEZIP");
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				ZipInputStream zipin = new ZipInputStream(in);
				File parent = Files.createTempDir();
				processZIPFiles(parent, zipin, domainName, domain, messages);
			}

			return messages;

		} catch (Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("aonMod200BOEZIP");
		}
	}

	private void processZIPFiles(File parent, ZipInputStream zin,
			String domainName, int parentDomain, List<String> messages)
			throws IOException {

		messages.add("Comienza el proceso de importación");
		messages.add("");

		byte buff[] = new byte[1024];

		for (ZipEntry entry = zin.getNextEntry(); entry != null; entry = zin
				.getNextEntry()) {
			String name = entry.getName();
			File file = new File(parent, name);

			if (entry.isDirectory()) {
				file.mkdirs();
				continue;
			}

			FileOutputStream fout = new FileOutputStream(file);

			for (int read = zin.read(buff, 0, 1024); read > 0; read = zin.read(
					buff, 0, 1024))
				fout.write(buff, 0, read);
			fout.close();

			FileInputStream input = new FileInputStream(file);

			try {
				Mod2002013 mod200 = Mod2002013Reader.getMod2002013(input);
	
				String enterDocument = mod200.getEnterpriseDocument();
				String enterName = mod200.getEnterpriseName();
	
				messages.add("Documento: " + enterDocument);
				messages.add("Nombre: " + enterName);
	
				Domain domain = AON.insertDomain(domainName, parentDomain,
						enterDocument, enterName, messages);
				Company company = AON.getCompanyForDomain(domain.getName(),domain.getId(), getUserLogin());
				int enterpriseID = company.getId();
				mod200.setDomain(domain.getId());
				mod200.setEnterprise(enterpriseID);
				Mod2002013 mod2002013 = FISCAL.getMod2002013ByYear(domain.getName(), domain.getId(), getUserLogin(), 2013);
				if (mod2002013 != null && mod2002013.getId() != null) {
					messages.add("Modelo 200 ya creado en el ejercicio 2013, no se graba");
				} else {
					FISCAL.saveMod2002013(domain.getName(), domain.getId(), getUserLogin(), mod200);
					messages.add("Modelo 200 del ejercicio 2013 grabado.");
				}
				messages.add("");
			} catch (Throwable t) {
				t.printStackTrace();
				messages.add("ERROR: " + t.getMessage());
			}
		}
		messages.add("Fin del proceso de importación");
	}

}
