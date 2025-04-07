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

@WebServlet("/Customerlistservlet")
public class Customerlistservlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = PSQL.getConnection()) {
            String query = "SELECT username, birthday, sender_id, email, job, address, twillio_sid, twillio_token FROM customer";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                out.println("<table class='data-table' id='customersTable'>");
                out.println("<thead>");
                out.println("<tr>");
                out.println("<th>Customer Name</th>");
                out.println("<th>Phone</th>");
                out.println("<th>Email</th>");
                out.println("<th>Job</th>");
                out.println("<th>Address</th>");
                out.println("</tr>");
                out.println("</thead>");
                out.println("<tbody>");

                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("username") + "</td>");
                    out.println("<td>" + rs.getString("sender_id") + "</td>");
                    out.println("<td>" + rs.getString("email") + "</td>");
                    out.println("<td>" + rs.getString("job") + "</td>");
                    out.println("<td>" + rs.getString("address") + "</td>");
                    out.println("</tr>");
                }     
                
                out.println("</tbody>");
                out.println("</table>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<h3 style='color:red;'>Database error: " + e.getMessage() + "</h3>");
        }
    }
}