package net.aonsolutions.aon.api.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BartenderServlet", urlPatterns = {"/ms/api/bartender/*"})
public class BartenderServlet extends HttpServlet {

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String line = "";
            while((line = req.getReader().readLine()) != null){
                System.out.println(line);
            }
        } catch (IOException e) {
        }
    }
    
}
