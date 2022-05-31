package com.code.aon.web.help.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.help.video.VideoWrapper;

/**
 * Servlet that streams a video to the client using 
 * HTTP partial content delivery specification.
 * implemented by @akrck02 
 */
@WebServlet(name = "VideoStreamServlet", urlPatterns = { "/video" })
public class VideoStreamingServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final int CHUNK_SIZE = 1000000;
       
    public VideoStreamingServlet() {    	
    	super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			final PrintWriter writer = response.getWriter();
			final Map<String, String> headers = Collections.list(request.getHeaderNames())
				    .stream()
				    .collect(Collectors.toMap(h -> h, request::getHeader));
			
			
			// Get range header
			if(!headers.containsKey("range")) {
				response.setStatus(400);
				writer.append("Requires Range header");
				return;
			}
			
			final String range = headers.get("range");
			int[] values = getRangeValues(range);
			
			if(values.length == 0) {
				response.setStatus(400);
				writer.append("Malformed Range header");
				return;
			}
			
			// Get file param
			final String file = request.getParameter("file");
			if(file == null) {
				response.setStatus(400);
				writer.append("No file provided");
				return;
			}
			
			// Get video
			InputStream is = VideoWrapper.getVideo(file);
			long videoSize = is.available();
			
			// Get values
			long start = values[0];
			long end = Math.min(start + CHUNK_SIZE, videoSize - 1);
			final long contentLength = end - start + 1;
				
			// Set response headers
			response.setStatus(206);	
			response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + videoSize);
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Content-Length", contentLength + "");
			response.setHeader("Content-Type", "video/mp4");
	
			// Write the stream		
			ServletOutputStream out = response.getOutputStream();
			int chunk;
			byte[] data = new byte[CHUNK_SIZE];
			
			start = is.skip(start);		
			for (long i = start; i <= end; i += CHUNK_SIZE) {
				chunk = is.read(data, 0, data.length);
				out.write(data, 0, chunk);
			}
			
			is.close();
			out.close();
		
		} catch(IOException e) {
			response.setStatus(500);
		}
		
	}

	
	private int[] getRangeValues(String range) {
		
		final int[] values = new int[2];
		try {
			
			int start = Integer.parseInt(range.substring(range.indexOf("=")+1,range.indexOf("-")));
			values[0] = start;
			
			String endStr = range.substring(range.indexOf("-") + 1);
			
			if(endStr.length() > 0) {
				int end = Integer.parseInt(endStr);
				values[1] = end; 
			} else {
				values[1] = -1;
			}
			
			return values;
			
		} catch(NumberFormatException e) {
			return new int[0];
		}
		
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}

}
