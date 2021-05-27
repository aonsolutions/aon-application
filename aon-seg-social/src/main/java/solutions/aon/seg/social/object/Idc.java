package solutions.aon.seg.social.object;

import java.util.Date;

public class Idc {
	private String descripcion;
	private Date fecha;
	public Idc(String descripcion, Date fecha) {
		super();
		this.descripcion = descripcion;
		this.fecha = fecha;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	@Override
	public String toString() {
		return "Idc [descripcion=" + descripcion + ", fecha=" + fecha + "]";
	}
	
}