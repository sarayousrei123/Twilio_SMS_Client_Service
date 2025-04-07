package com.mycompany.tsms;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
           
                
            session.removeAttribute("username");
            session.removeAttribute("msisdn");
            session.removeAttribute("email");
            session.removeAttribute("birthday");        
            session.removeAttribute("job");
            session.removeAttribute("address");
            session.removeAttribute("twillio_sid");
            session.removeAttribute("twillio_token");
            session.removeAttribute("role");

        session.invalidate();
            
            response.sendRedirect(request.getContextPath() + "/login.html");
        } else {
            
            response.sendRedirect(request.getContextPath() + "/login.html");
      
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}