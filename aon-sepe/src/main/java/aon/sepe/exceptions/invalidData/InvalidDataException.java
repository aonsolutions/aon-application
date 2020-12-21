package aon.sepe.exceptions.invalidData;

import solutions.aon.sepe.exceptions.SepeException;

public class InvalidDataException extends SepeException{

	public InvalidDataException(){}
	public InvalidDataException(String msg){super(msg);}

	public static void checkCode(Integer statusCode, String msg) throws SepeException {
		switch (statusCode) {
			case 200: 
			case 201: 
				break;
			default: throw new InvalidDataException(msg);
		}
	}
}
