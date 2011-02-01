
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;

public class KeyServlet extends HttpServlet {

	static final long serialVersionUID = 1L;

	private IManagerBean bean;
	
	private IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.bean == null ) {
			this.bean = BeanManager.getManagerBean(User.class);
		}
		return this.bean;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException {
		try {			
			res.setContentType("application/octet-stream");		
			// Object streams	
			ObjectInputStream ois = new ObjectInputStream(req.getInputStream());					
			ObjectOutputStream oos = new ObjectOutputStream(res.getOutputStream());
			// Leer usuario y clave (parte del estado)
			AuthPrincipal userPrincipal = null;
			Principal principal = req.getUserPrincipal();
			if ( principal instanceof AuthPrincipal ) {
				userPrincipal = (AuthPrincipal) principal;
			} else {
				userPrincipal = new AuthPrincipal( principal.getName() );
			}
			String username = userPrincipal.getShortName();
			//Buscamos el usuario en la base de datos.
        	Criteria criteria = new Criteria();
        	criteria.addExpression( getManagerBean().getFieldName(IConfigAlias.USER_LOGIN), username);
            List<ITransferObject> list = getManagerBean().getList(criteria);
			User user = null;
			if (list.size() > 0) {
            	user = (User)list.get(0);
			} 

			Status status = (Status)ois.readObject();			

			String key = (String)status.getAttachments().get(0);			
			status = new Status();
			// Comprobar que el usuario tiene el mismo contenido en el servidor				
			if (key.equals(user.getAon_key())) {
				status.setStatus(Status.SYNC_KEY);
				req.getSession().setAttribute("AON_KEY_VALIDATOR_OK", true);
				// Modificar la clave (para el applet)
				key = String.valueOf(System.currentTimeMillis());					
				// y la de la base de datos
				user.setAon_key(key);
				user.setStatus(2);
				getManagerBean().update(user);
				// Añadir la nueva clave como attachment del estado
				status.getAttachments().add(key);
			}
			else {
				req.getSession().invalidate();
				status.setStatus(Status.NOT_SYNC_KEY);
			}
			// Enviar estado (y attachments) al applet
			oos.writeObject(status);
			oos.flush();			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
