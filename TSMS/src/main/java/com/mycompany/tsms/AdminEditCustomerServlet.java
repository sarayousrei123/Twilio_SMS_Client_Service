package com.mycompany.tsms;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/AdminEditCustomerServlet")
public class AdminEditCustomerServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        System.out.println("Received username: " + username);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (username == null || username.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new Gson().toJson(Map.of("error", "No username provided")));
            return;
        }
        
        try (Connection conn = PSQL.getConnection()) {
            String sql = "SELECT * FROM customer WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            Map<String, Object> userData = new HashMap<>();
            if (rs.next()) {
                userData.put("username", rs.getString("username"));
                userData.put("msisdn", rs.getString("msisdn"));
                userData.put("email", rs.getString("email"));
                userData.put("birthday", rs.getString("birthday"));
                userData.put("job", rs.getString("job"));
                userData.put("address", rs.getString("address"));
                userData.put("twillio_sid", rs.getString("twillio_sid"));
                userData.put("twillio_token", rs.getString("twillio_token"));
            } else {
                System.out.println("User not found in database for username: " + username);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(new Gson().toJson(Map.of("error", "User not found")));
                return;
            }
            
            response.getWriter().write(new Gson().toJson(userData));
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new Gson().toJson(Map.of("error", "Database error: " + e.getMessage())));
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String msisdn = request.getParameter("msisdn");
        String email = request.getParameter("email");
        String currentPassword = request.getParameter("current_password");
        String newPassword = request.getParameter("new_password");
        String birthday = request.getParameter("birthday");
        String job = request.getParameter("job");
        String address = request.getParameter("address");
        String twillio_sid = request.getParameter("twillio_sid");     
        String twillio_token = request.getParameter("twillio_token");
        
        if (username == null || username.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new Gson().toJson(Map.of("error", "No username provided")));
            return;
        }
        
        try (Connection conn = PSQL.getConnection()) {
            String selectSql = "SELECT * FROM customer WHERE username = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setString(1, username);
            ResultSet rs = selectStmt.executeQuery();
            
            if (rs.next()) {    
                StringBuilder updateSql = new StringBuilder("UPDATE customer SET msisdn = ?, email = ?, birthday = ?, job = ?, address = ?, twillio_sid = ?, twillio_token = ? ");
                
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    updateSql.append(", password = ?");
                }
                
                updateSql.append(" WHERE username = ?");
                PreparedStatement updateStmt = conn.prepareStatement(updateSql.toString());
                updateStmt.setString(1, msisdn);
                updateStmt.setString(2, email);
                updateStmt.setString(3, birthday);
                updateStmt.setString(4, job);
                updateStmt.setString(5, address);
                updateStmt.setString(6, twillio_sid);
                updateStmt.setString(7, twillio_token);
                
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    updateStmt.setString(8, newPassword);
                    updateStmt.setString(9, username);
                } else {
                    updateStmt.setString(8, username);
                }
                
                updateStmt.executeUpdate();
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
            out.println("<p>Customer edit successfully!</p>");
            out.println("<button class='btn' onclick=\"window.location.href='edit_customer_form.html'\">OK</button>");
            out.println("</div></div>");
            out.println("</body></html>");
                
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(new Gson().toJson(Map.of("error", "User not found")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new Gson().toJson(Map.of("error", "Database error: " + e.getMessage())));
        }
    }
}
