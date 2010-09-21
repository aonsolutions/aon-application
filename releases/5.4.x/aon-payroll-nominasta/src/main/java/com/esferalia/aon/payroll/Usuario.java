package com.esferalia.aon.payroll;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IUsuario;



@Entity
@Table(name="perfil")
public class Usuario implements ITransferObject, IUsuario  {

	private static final long serialVersionUID = 6486083449496243031L;
	
    private String login;
    private String autorizacion;

	@Id     
    @Column(name="cdg", unique=true, nullable=false, length=10)
    @Override
    public String getLogin() {
        return this.login;
    }
    public void setLogin(String login) {
        this.login= login;
    }

	@Override
	@Column(name="autoriza", length=8)
	public String getAutorizacion() {
		return autorizacion;
	}
	@Override
	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}

}


