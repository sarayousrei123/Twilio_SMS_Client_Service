package com.mycompany.tsms;

import com.mycompany.tsms.PSQL;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/TwilioSmsServlet")
public class TwilioSmsServlet extends HttpServlet {
    private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");   

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String messageBody = request.getParameter("body");
        
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        
        try {
            Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                messageBody
            ).create();
            
            saveMessageToDB(from, to, messageBody, "sent");
            response.sendRedirect("message_history.html?success=true");
        } catch (Exception e) {
            saveMessageToDB(from, to, messageBody, "failed");
            response.sendRedirect("message_history.html?error=true");
        }
    }
    
    private void saveMessageToDB(String from, String to, String body, String status) {
        try (Connection conn = PSQL.getConnection()) {
            String sql = "INSERT INTO sms (from_msisdn, to_msisdn, body, date, status) VALUES (?, ?, ?, ?, ?) ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, from);
            stmt.setString(2, to);
            stmt.setString(3, body);
            stmt.setObject(4, LocalDateTime.now());
            stmt.setString(5, status);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
