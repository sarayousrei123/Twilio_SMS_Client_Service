package com.mycompany.tsms;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UserProfileServlet")
public class UserProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String username = (String) session.getAttribute("username");
        String query = "SELECT username, birthday, msisdn, email, job, address, twillio_sid, twillio_token FROM customer WHERE username = ?";

        try (Connection conn = PSQL.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            StringBuilder jsonResponse = new StringBuilder("{");

            if (rs.next()) {
                jsonResponse.append("\"username\": \"").append(rs.getString("username")).append("\",")
                            .append("\"birthday\": \"").append(rs.getDate("birthday") != null ? rs.getDate("birthday").toString() : "").append("\",")
                            .append("\"msisdn\": \"").append(rs.getString("msisdn")).append("\",")
                            .append("\"email\": \"").append(rs.getString("email")).append("\",")
                            .append("\"job\": \"").append(rs.getString("job")).append("\",")
                            .append("\"address\": \"").append(rs.getString("address")).append("\",")
                            .append("\"twillio_sid\": \"").append(rs.getString("twillio_sid")).append("\",")
                            .append("\"twillio_token\": \"").append(rs.getString("twillio_token")).append("\"}");
            } else {
                jsonResponse.append("}");
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String username = (String) session.getAttribute("username");

        String birthdayStr = request.getParameter("birthday");
        Date birthday = null;
        if (birthdayStr != null && !birthdayStr.isEmpty()) {
            try {
                birthday = Date.valueOf(birthdayStr);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
        }

        String msisdn = request.getParameter("msisdn");
        String email = request.getParameter("email");
        String job = request.getParameter("job");
        String address = request.getParameter("address");
        String twillio_sid = request.getParameter("twillio_sid");
        String twillio_token = request.getParameter("twillio_token");

        String updateQuery = "UPDATE customer SET birthday=?, msisdn=?, email=?, job=?, address=?, twillio_sid=?, twillio_token=? WHERE username=?";

        try (Connection conn = PSQL.getConnection();
             PreparedStatement pst = conn.prepareStatement(updateQuery)) {

            pst.setDate(1, birthday);
            pst.setString(2, msisdn);
            pst.setString(3, email);
            pst.setString(4, job);
            pst.setString(5, address);
            pst.setString(6, twillio_sid);
            pst.setString(7, twillio_token);
            pst.setString(8, username);

            int updatedRows = pst.executeUpdate();
            if (updatedRows > 0) {
                response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Success</title>");
            out.println("<style>");
            out.println(".overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; }");
            out.println(".modal { background: white; padding: 20px; text-align: center; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.3); width: 300px; }");
            out.println(".btn { padding: 10px 15px; border: none; cursor: pointer; background: #007BFF; color: white; border-radius: 5px; margin-top: 10px; }");
            out.println("</style></head><body>");
            out.println("<div class='overlay'><div class='modal'>");
            out.println("<p>update profile successfully!</p>");
            out.println("<button class='btn' onclick=\"window.location.href='user_profile.html'\">OK</button>");
            out.println("</div></div>");
            out.println("</body></html>");
        
                } else {
                    response.sendRedirect("user_profile.html?status=error");
                }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database update error: " + e.getMessage());
        }
    }
}
