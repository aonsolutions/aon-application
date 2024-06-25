package solutions.aon.circe.exception;

public class UnknownAuthorizedException extends ElementNotFoundException{
	

	public UnknownAuthorizedException(String msg) {
		super(msg);
		}
	public UnknownAuthorizedException() {
	}

}
