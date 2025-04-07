package com.mycompany.tsms;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String birthday = request.getParameter("birthday");
        String phone = request.getParameter("phone");
        String job = request.getParameter("job");
        String address = request.getParameter("address");
        String senderid = request.getParameter("sender_id");
        String twilioSid = request.getParameter("twilio_sid");
        String twilioAuth = request.getParameter("twilio_auth_token");
        //String verificationCode = String.format("%06d", (int) (Math.random() * 1000000));

        String sql = "INSERT INTO customer (username,email,password,birthday,msisdn,job,address, sender_id, twillio_sid,twillio_token) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = PSQL.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, password);
            pst.setString(4, birthday);
            pst.setString(5, phone);
            pst.setString(6, job);
            pst.setString(7, address);
            pst.setString(8, senderid);
            pst.setString(9, twilioSid);
            pst.setString(10, twilioAuth);
            //pst.setString(10, verificationCode);

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
              /*  // Send Verification Code via Twilio
                Twilio.init(twilioSid, twilioAuth);
                Message.creator(
                        new PhoneNumber(phone),
                        new PhoneNumber("+YOUR_TWILIO_PHONE"),
                        "Your verification code is: " + verificationCode
                ).create();
*/
               response.sendRedirect(request.getContextPath() + "/verify.html");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Registration failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
}
