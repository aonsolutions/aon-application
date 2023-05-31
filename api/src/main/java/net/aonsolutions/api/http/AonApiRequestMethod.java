package net.aonsolutions.api.http;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum AonApiRequestMethod implements Serializable{
	 GET
	 	{ @Override public <R,T> R visit(AonApiRequestMethodVisitor<R,T> v, T t) { return v.visitGet(t);} }
	,POST
 		{ @Override public <R,T> R visit(AonApiRequestMethodVisitor<R,T> v, T t) { return v.visitPost(t);} }
	,PUT
 		{ @Override public <R,T> R visit(AonApiRequestMethodVisitor<R,T> v, T t) { return v.visitPut(t);} }
	,DELETE
		{ @Override public <R,T> R visit(AonApiRequestMethodVisitor<R,T> v, T t) { return v.visitDelete(t);} }
	;
	
	public static Optional<AonApiRequestMethod> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}

	public abstract <R,T> R visit(AonApiRequestMethodVisitor<R,T> visitor, T t);
	public static interface AonApiRequestMethodVisitor<R,T> {
		 R visitGet( T t );
		 R visitPost( T t );
		 R visitPut( T t );
		 R visitDelete( T t );
	}
	
}
