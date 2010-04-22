
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.List;

import javax.faces.context.FacesContext;
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

public class UserServlet extends HttpServlet {

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
			// Object streams.
			ObjectOutputStream oos = new ObjectOutputStream(res.getOutputStream());
			// Recuperar el usuario desde applet.
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
        	criteria.addEqualExpression( getManagerBean().getFieldName(IConfigAlias.USER_AVAILABLE), true);
            List<ITransferObject> list = getManagerBean().getList(criteria);
			Status status = new Status();
			User user = null;
			Integer userId = null;
			if (list.size() > 0) {
            	user = (User)list.get(0);
            	userId = user.getId();
			} 
            else {
            	req.getSession().invalidate();
				status.setStatus(Status.USER_NOT_FOUND);
			}

			if (userId != null) {
				if (user.getValidate()) {
					if (user.getStatus().intValue() == 0) {
						status.setStatus(Status.FIRST_TIME);
						user.setStatus(new Integer(1));
						getManagerBean().update(user);
		            	req.getSession().invalidate();
					}
					else {
						if (user.getStatus().intValue() == 1) status.setStatus(Status.INSTALL);
						else status.setStatus(Status.USER_KEY_FILE_OWNER);
						status.getAttachments().add("\\aonSolutions\\Key-" + userId.intValue() + ".asc");
					}
				}
				else {
					status.setStatus(Status.USER_NOT_KEY_FILE_OWNER);
					req.getSession().setAttribute("AON_KEY_VALIDATOR_OK", true);
				}
			} 
            else {
            	req.getSession().invalidate();
				status.setStatus(Status.USER_NOT_FOUND);
			}
			// Enviar el estado al applet			
			oos.writeObject(status);
			oos.flush();		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
