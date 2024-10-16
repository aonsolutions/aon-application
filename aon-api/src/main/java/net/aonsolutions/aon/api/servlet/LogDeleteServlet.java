package net.aonsolutions.aon.api.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/deleteLogs")
public class LogDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String logFilePath = "/WEB-INF/logs/bankErrorsLog.log";
//        File logFile = new File(logFilePath);
//        
//        System.out.println("Log file path: " + logFilePath);
//
//        if (logFile.exists() && logFile.isFile()) {
//            if (logFile.delete()) {
//                System.out.println("Log file deleted successfully.");
//                request.getSession().setAttribute("message", "Log file deleted successfully.");
//            } else {
//                System.out.println("Failed to delete log file.");
//                request.getSession().setAttribute("message", "Failed to delete log file.");
//            }
//        } else {
//            System.out.println("Log file does not exist.");
//            request.getSession().setAttribute("message", "Log file does not exist.");
//        }
//
//        // Redirigir a la vista de logs después de eliminar el archivo
//        response.sendRedirect("viewLogs");
    }
 }

