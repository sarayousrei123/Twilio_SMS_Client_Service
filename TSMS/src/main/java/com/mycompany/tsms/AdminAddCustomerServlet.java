package com.mycompany.tsms;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdminAddCustomerServlet")
public class AdminAddCustomerServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String msisdn = request.getParameter("msisdn");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String birthday = request.getParameter("birthday");
        String job = request.getParameter("job");
        String address = request.getParameter("address");
        String twillio_sid = request.getParameter("twillio_sid");
        String twillio_token = request.getParameter("twillio_token");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            PSQL db = new PSQL();
            conn = db.getConnection();

            String sql = "INSERT INTO customer (sender_id, username, birthday, password, job, email, address, twillio_sid, twillio_token) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msisdn);
            pstmt.setString(2, username);
            pstmt.setString(3, birthday);
            pstmt.setString(4, password);
            pstmt.setString(5, job);
            pstmt.setString(6, email);
            pstmt.setString(7, address);
            pstmt.setString(8, twillio_sid);
            pstmt.setString(9, twillio_token);

            pstmt.executeUpdate();

            
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
            out.println("<p>Customer added successfully!</p>");
            out.println("<button class='btn' onclick=\"window.location.href='add_customer.html'\">OK</button>");
            out.println("</div></div>");
            out.println("</body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
