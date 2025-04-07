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
import javax.servlet.http.HttpSession;

@WebServlet("/InboxServlet")
public class InboxServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            out.println("<p>User not logged in. Please log in first.</p>");
            return;
        }

        String username = (String) session.getAttribute("username");

        try (Connection conn = PSQL.getConnection()) {
            String getMsisdnQuery = "SELECT sender_id FROM customer WHERE username = ?";
            try (PreparedStatement msisdnStmt = conn.prepareStatement(getMsisdnQuery)) {
                msisdnStmt.setString(1, username);
                ResultSet msisdnRs = msisdnStmt.executeQuery();

                if (msisdnRs.next()) {
                    String userMsisdn = msisdnRs.getString("sender_id");

                    String query = "SELECT from_msisdn, body, date FROM sms WHERE from_msisdn = ? ORDER BY date DESC";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, userMsisdn);
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            String fromMsisdn = rs.getString("from_msisdn");
                            String body = rs.getString("body");
                            String date = rs.getString("date");

                            out.println("<div class='message'>");
                            out.println("<div class='message-status'><span class='status-indicator'></span></div>");
                            out.println("<div class='message-content'>");
                            out.println("<div class='message-header'>");
                            out.println("<h3 class='sender'>" + fromMsisdn + "</h3>");
                            out.println("<span class='date'>" + date + "</span>");
                            out.println("</div>");
                            out.println("<p class='message-text'>" + body + "</p>");
                            out.println("</div>");
                            out.println("<div class='message-actions'>");
                            out.println("<button class='action-btn' title='Delete'><i class='fas fa-trash'></i></button>");
                            out.println("<button class='action-btn' title='Mark as unread'><i class='fas fa-envelope'></i></button>");
                            out.println("</div>");
                            out.println("</div>");
                        }
                    }
                } else {
                    out.println("<p>No messages found for this user.</p>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error retrieving messages.</p>");
        }
    }
}
