package com.mycompany.tsms;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdminDeleteCustomerServlet")
public class AdminDeleteCustomerServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        try {
            PSQL db = new PSQL();
            Connection conn = db.getConnection();
            
            
            String checkSql = "SELECT COUNT(*) FROM customer WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int userCount = rs.getInt(1);
            
            if (userCount == 0) { 
                PrintWriter out = response.getWriter();
                   response.setContentType("text/html");
                out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Success</title>");
            out.println("<style>");
            out.println(".overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; }");
            out.println(".modal { background: white; padding: 20px; text-align: center; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.3); width: 300px; }");
            out.println(".btn { padding: 10px 15px; border: none; cursor: pointer; background: #007BFF; color: white; border-radius: 5px; margin-top: 10px; }");
            out.println("</style></head><body>");
            out.println("<div class='overlay'><div class='modal'>");
            out.println("<p>Customer not found!</p>");
            out.println("<button class='btn' onclick=\"window.location.href='delete_customer.html'\">OK</button>");
            out.println("</div></div>");
            out.println("</body></html>");
            } else {
                
                String sql = "DELETE FROM customer WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.executeUpdate();  
                PrintWriter out = response.getWriter();
                   response.setContentType("text/html");
                out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Success</title>");
            out.println("<style>");
            out.println(".overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; }");
            out.println(".modal { background: white; padding: 20px; text-align: center; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.3); width: 300px; }");
            out.println(".btn { padding: 10px 15px; border: none; cursor: pointer; background: #007BFF; color: white; border-radius: 5px; margin-top: 10px; }");
            out.println("</style></head><body>");
            out.println("<div class='overlay'><div class='modal'>");
            out.println("<p>Customer delete successfully!</p>");
            out.println("<button class='btn' onclick=\"window.location.href='delete_customer.html'\">OK</button>");
            out.println("</div></div>");
            out.println("</body></html>");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

}
