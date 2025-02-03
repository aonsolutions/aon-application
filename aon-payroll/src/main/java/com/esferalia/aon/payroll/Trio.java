package com.esferalia.aon.payroll;

import java.io.Serializable;

public class Trio<A, B, C> implements Serializable {
	 
	  public A fst;
	  public B snd;
	  public C thr;
	 
	  public Trio() {
		  super();
	  }
	  
	  public Trio(A fst, B snd, C thr) {
	    this.fst = fst;
	    this.snd = snd;
	    this.thr = thr;
	  }
	 
	  public A getFirst() { return fst; }
	  public B getSecond() { return snd; }
	  public C getThird() { return thr; }
	 
	  public void setFirst(A v) { fst = v; }
	  public void setSecond(B v) { snd = v; }
	  public void setThird(C v) { thr = v; }
	 
	  public String toString() {
	    return "Trio[" + fst + "," + snd + "," + thr + "]";
	  }
	 
	  private static boolean equals(Object x, Object y) {
	    return (x == null && y == null) || (x != null && x.equals(y));
	  }
	 
	  public boolean equals(Object other) {
	     return
	      other instanceof Trio &&
	      equals(fst, ((Trio)other).fst) &&
	      equals(snd, ((Trio)other).snd) &&
	      equals(thr, ((Trio)other).thr);
	  }
	 
	  public static <A,B,C> Trio<A,B,C> of(A a, B b, C c) {
	    return new Trio<A,B,C>(a,b,c);
	  }
	}
