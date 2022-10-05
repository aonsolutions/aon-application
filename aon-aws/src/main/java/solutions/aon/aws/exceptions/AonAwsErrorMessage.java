package solutions.aon.aws.exceptions;

public enum AonAwsErrorMessage {

	S3_BUCKET_NOT_EXIST("El contenedor de s3 no existe.");
	
	String message;
	
	private AonAwsErrorMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public AonAwsErrorMessage setMessage(String message) {
		this.message = message;
		return this;
	}
}
