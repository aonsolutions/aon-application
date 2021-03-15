package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

public class Pointer {

	private float x;
	private float y;
	
	public Pointer(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/** <p><b>Description:</b> <i>Instances a pointer. </i></p> */
	public static Pointer instance(float x, float y) {
		return new Pointer(x, y);
	}

	/** <p><b>Description:</b> <i>Move down the pointer. </i></p> */
	public void down(float pixels) 		{y -= pixels;}
	
	/** <p><b>Description:</b> <i>Move up the pointer. </i></p> */
	public void up(float pixels) 		{y += pixels;}
	
	/** <p><b>Description:</b> <i>Move left the pointer. </i></p> */
	public void left(float pixels) 		{x -= pixels;}
	
	/** <p><b>Description:</b> <i>Move right the pointer. </i></p> */
	public void right(float pixels) 	{x += pixels;}
	
	
	public float x() {return x;}
	public void x(float x) {this.x = x;}

	public float y() {return y;}
	public void y(float y) {this.y = y;}
	
	
	
}
