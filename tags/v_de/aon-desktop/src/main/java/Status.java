
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Status implements Serializable {

	static final long serialVersionUID = 1L;
	
	static final int UNDEFINED = -1;
	static final int USER_NOT_FOUND = 0;			
	static final int USER_KEY_FILE_OWNER = 3;
	static final int USER_NOT_KEY_FILE_OWNER = 4;
	static final int SYNC_KEY = 5;
	static final int NOT_SYNC_KEY = 6;
	static final int FIRST_TIME = 7;
	static final int INSTALL = 8;
	static final int USING = 9;
	
	private int status;
	
	private List attachments = new ArrayList();
	
	public Status() {
		setStatus(UNDEFINED);
	}
	
	public Status(int status) {
		setStatus(status);
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List getAttachments() {
		return attachments;
	}

	public void setAttachments(List attachments) {
		this.attachments = attachments;
	}
}
