package solutions.aon.aws.exceptions;

public class AonAwsS3Exception extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public AonAwsS3Exception() {
        super();
    }
    
    public AonAwsS3Exception(String message) {
        super(message);
    }
    
    public AonAwsS3Exception(Throwable cause) {
        super(cause);
    }
    
    public AonAwsS3Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
