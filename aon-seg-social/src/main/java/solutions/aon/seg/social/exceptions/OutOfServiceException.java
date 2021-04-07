package solutions.aon.seg.social.exceptions;

public class OutOfServiceException extends SegSocialException{

	public OutOfServiceException(String string) {
		super(string);
	}
	
	public OutOfServiceException(String string, Throwable motivation) {
		super(string, motivation);
	}
	
}
