/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.tsms;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 *
 * @author syousrei
 */

@WebServlet("/GetSessionDataServlet")
public class GetSessionDataServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (session != null && session.getAttribute("username") != null) {
            JSONObject json = new JSONObject();
            json.put("username", session.getAttribute("username"));
            json.put("role", session.getAttribute("role"));
            json.put("email", session.getAttribute("email"));
            json.put("msisdn", session.getAttribute("msisdn"));

            response.getWriter().write(json.toString());
        } else {
            response.getWriter().write("{}"); 
        }
    }
}
