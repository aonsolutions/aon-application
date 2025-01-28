package aon.solutions.in.issues.aws.lambda;

import com.esferalia.aon.occam.api.model.task.Task;

import jakarta.mail.internet.MimeMessage;

public class MimeMessage2Task {
	
	public static Task newTask(MimeMessage mimeMessage) {
		return new Task();
	}

}
