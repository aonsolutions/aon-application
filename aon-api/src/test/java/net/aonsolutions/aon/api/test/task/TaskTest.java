package net.aonsolutions.aon.api.test.task;

import static org.mockito.Mockito.when;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.github.javafaker.Faker;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.excel.TaskExcel;

public class TaskTest extends AbstractOccamTest {
	Faker faker = new Faker();
	
	@Mock
    HttpServletRequest request;
 
    @Mock
    HttpServletResponse response;
    
    @Mock
    private OutputStream myOutputStream;
 
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    Map<String, String> headers = new HashMap<>();
    
    @Test
    @Ignore
	public void test() {
		headers.put(IConstants.DOMAIN_NAME, DOMAIN_NAME);
		headers.put(IConstants.DOMAIN_ID, DOMAIN_ID.toString());
		headers.put(IConstants.DOMAIN_LOGIN, USER);
		when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
		when(request.getHeader(IConstants.DOMAIN_NAME)).thenReturn("sig.rvasquez.net");
		when(request.getHeader(IConstants.DOMAIN_ID)).thenReturn("5");
		when(request.getHeader(IConstants.DOMAIN_LOGIN)).thenReturn("portal");
		
		buildExcel();
	}
    
	private void buildExcel() {
		
		Domain domain = new Domain().setId(5).setName("sig.rvasquez.net");

		List<Task> tasks = AON_SOLUTIONS.getTaskParentOrChildStream(domain, new User(), 
				f -> f.getDomainProperty().eq(domain.getId()) 
				.and(f.getStatusProperty().in(Arrays.asList(TaskStatus.PENDING.value(), TaskStatus.IN_PROGRESS.value()).toArray(Byte[]::new)))
		).collect(Collectors.toCollection(LinkedList::new));
	
		try{
			TaskExcel.buildExcel(new FileOutputStream(System.getProperty("user.home")+"/Documentos/test.xls"), tasks);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
