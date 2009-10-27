
import java.applet.Applet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class AonSolutionsApplet extends Applet {

	static final long serialVersionUID = 1L;

	static final String USER_SERVLET_URL = "/user";
	static final String KEY_SERVLET_URL = "/key";

	private String APP_URL;
	
	String path = "";
	File keyFile;
	
	private URLConnection connectUrl(String url) throws Exception {		
		URL serverURL = new URL(getDocumentBase(), url);
		URLConnection serverCon = serverURL.openConnection();	
		serverCon.setRequestProperty("Content-Type", "application/octet-stream");			
		serverCon.setDoInput(true);
		serverCon.setDoOutput(true);
		serverCon.setUseCaches(false);
		serverCon.connect();
		return serverCon;
	}
	
	public void init() {
		APP_URL = getParameter("url");
		try {
			// Conectar al servlet de usuarios
			URLConnection serverCon = connectUrl(APP_URL + USER_SERVLET_URL);
			// Object streams			
			ObjectOutputStream oos = new ObjectOutputStream(serverCon.getOutputStream());			
			// Enviar el usuario			
			oos.flush();
			// Leer el estado desde el servlet
			ObjectInputStream ois = new ObjectInputStream(serverCon.getInputStream());
			Status status = (Status)ois.readObject();			
			switch (status.getStatus()) {
				case Status.USER_NOT_FOUND:
					goTo("/userNotFound.html");
					break;
				case Status.USER_NOT_KEY_FILE_OWNER:
					goTo("/home.iface");
					break;
				case Status.FIRST_TIME:
					goTo("/firstTimeUser.html");
					break;
				default:
					if (status.getStatus() == Status.INSTALL || status.getStatus() == Status.USER_KEY_FILE_OWNER) {
						String filename = (String)status.getAttachments().get(0);
						// Buscar el fichero de claves
						File[] list = File.listRoots();
						boolean keyFileFound = false;
						for (int i = 0; i < list.length; i++) {
							File file = list[i];
							path = file.toString() + filename;						
							keyFile = new File(path);
							if (keyFile.exists()) {
								keyFileFound = true;
								break;
							}
						}

						if (keyFileFound) {
							String key = readFile(path);
							if (KeyValidator.keyValid(key)) {
								// Check the write permission
								if (!keyFile.canWrite() || !isWritable(keyFile)) {
									goTo("/readOnlyKeyFile.html");
								}
								else {
									// Conectar al servlet de claves
									URLConnection kServletCon = connectUrl(APP_URL + KEY_SERVLET_URL);
									oos = new ObjectOutputStream(kServletCon.getOutputStream());
									// Enviar el usuario y la clave
									status = new Status();
									status.getAttachments().add(key);								
									oos.writeObject(status);
									oos.flush();
									try {
										ois = new ObjectInputStream(kServletCon.getInputStream());
									}
									catch (Exception ex) {
										ex.printStackTrace();
									}
									// Recuperar el estado desde el servlet 
									status = (Status)ois.readObject();
									if (status.getStatus() == Status.SYNC_KEY) {
										String newKey = (String)status.getAttachments().get(0);
										// Create the new file
										byte[] newKeyContentBytes = newKey.getBytes();
										File newKeyFile = new File(path);
										newKeyFile.createNewFile();
										FileOutputStream nfos = new FileOutputStream(newKeyFile);
										nfos.write(newKeyContentBytes);
										nfos.flush();
										nfos.close();
										goTo("/home.iface");
									}
									else if (status.getStatus() == Status.NOT_SYNC_KEY) {
											goTo("/notSyncKey.html");
									}
								}
							}
							else {
								goTo("/invalidKey.html");						
							}
						}
						else {
							if (status.getStatus() == Status.INSTALL) {
								goTo("/firstTimeUser.html");
							}
							else {
								goTo("/noKeyFile.html");
							}
						}
					}
			}
		} catch (Exception ex) {	
			ex.printStackTrace();
		}			
	}
	
	private boolean isWritable(File file) {
		try {
			FileOutputStream nfos = new FileOutputStream(file);
			nfos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private String readFile(String fn) throws Exception {
		FileInputStream fin = new FileInputStream(fn);
		BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
		return myInput.readLine();
	}
	
	private void goTo(String filename) {
		try {
			URL u = new URL(APP_URL + filename);
			getAppletContext().showDocument(u);
		} catch (Exception e) {
		}
	}
}
