package com.mycompany.tsms;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.mycompany.tsms.PSQL;
@WebServlet("/ChkLoginServlet")
public class ChkLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession(true);
        String contextPath = request.getContextPath();
        
        try (Connection conn = PSQL.getConnection()) {

            String sqlAdmin = "SELECT role FROM account WHERE username = ? AND password = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlAdmin)) {
                pst.setString(1, username);
                pst.setString(2, password);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        session.setAttribute("username", username);
                        session.setAttribute("role", "admin");
                        response.sendRedirect(contextPath + "/admin_profile.html");
                        return;
                    }
                }
            }
            
                
            String sqlUser = "SELECT username FROM customer WHERE username = ? AND password = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlUser)) {
                pst.setString(1, username);
                pst.setString(2, password);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        session.setAttribute("username", username);
                        session.setAttribute("role", "customer");
                        response.sendRedirect(contextPath + "/send_Messages.html");
                        return;
                    }
                }
            }
            
            response.sendRedirect(contextPath + "/login.html?error=Invalid username or password");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.html");
    }
}
