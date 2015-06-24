package com.esferalia.aon.gwt.connect.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.connect.client.ConnectService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod200Reader;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.thirdparty.guava.common.io.Files;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Connect Servlet", urlPatterns = { "/aon_gwt_connect/Connect" })
public class ConnectServiceImpl extends AonRemoteServiceServlet implements ConnectService {
	

	@Override
	public void importZippedMod2002013(String domainName, int domain)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			File file = (File) request.getSession().getAttribute("aonMod200BOEZIP");
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				ZipInputStream zipin = new ZipInputStream(in);
				File parent = Files.createTempDir();
				processZIPFiles(parent, zipin,domainName, domain);
			}
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("aonMod200BOEZIP");
		}
	}
	
	private static void processZIPFiles(File parent, ZipInputStream zin,String domainName, int parentDomain) throws IOException {

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

			Mod2002013 mod200 = Mod200Reader.getMod2002013(input);

			String enterDocument = mod200.getEnterpriseDocument();
			String enterName = mod200.getEnterpriseName();
			
			Domain domain = AON.insertDomain(domainName,parentDomain, enterDocument, enterName);
			Company company = AON.getCompanyForDomain(domain.getName(), domain.getId());
			int enterpriseID = company.getId();
			mod200.setDomain(domain.getId());
			mod200.setEnterprise(enterpriseID);
			AON.saveMod2002013(domain.getName(),domain.getId(), mod200);

		}
	}

}
