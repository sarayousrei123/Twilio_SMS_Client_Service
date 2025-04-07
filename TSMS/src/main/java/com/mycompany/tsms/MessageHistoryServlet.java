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
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/MessageHistoryServlet")
public class MessageHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String username = (String) session.getAttribute("username");
        JSONArray messagesArray = new JSONArray();

        try (Connection conn = PSQL.getConnection()) {
           String sql = "SELECT  to_msisdn, body, date, status FROM sms " +
             "WHERE from_msisdn = (SELECT sender_id FROM customer WHERE username = ?) " +
             "ORDER BY date DESC";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, username);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        JSONObject message = new JSONObject();
                       // message.put("fromMsisdn", rs.getString("from_msisdn"));
                        message.put("toMsisdn", rs.getString("to_msisdn"));
                        message.put("body", rs.getString("body"));
                        message.put("date", rs.getString("date"));
                        message.put("status", rs.getString("status") != null ? rs.getString("status") : "Unknown");
                        messagesArray.put(message);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
            return;
        }

        out.print(messagesArray.toString());
        out.flush();
    }
}
