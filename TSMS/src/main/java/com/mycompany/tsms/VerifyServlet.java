package com.mycompany.tsms;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.mycompany.tsms.PSQL;


/**
 *
 * @author abbady
 */

@WebServlet("/VerifyServlet")
public class VerifyServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String msisdn = request.getParameter("msisdn");
            String ACCOUNT_SID = "AC00000000000000000000000000000000";
        String AUTH_TOKEN = "00000000000000000000000000000000";
        
        String TWILIO_NUMBER = "+201000000000";
        
        Random random = new Random();
        int verificationCode = random.nextInt(9000) + 1000;
        
        try {
            
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                new PhoneNumber("+2" + msisdn),
                new PhoneNumber(TWILIO_NUMBER),
                "Your TSMS verification code is: " + verificationCode
            ).create();
            
            
            HttpSession session = request.getSession();
            session.setAttribute("verification_code", String.valueOf(verificationCode));
            session.setAttribute("msisdn", msisdn);
            
            response.sendRedirect(request.getContextPath() + "/verify.html");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/verify.html?error=Failed to send verification code");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String savedCode = (String) session.getAttribute("verification_code");
        String msisdn = (String) session.getAttribute("msisdn");
        String enteredCode = request.getParameter("code");
        
        if (savedCode != null && savedCode.equals(enteredCode)) {
            try (Connection conn = PSQL.getConnection()) {
                
                String sql = "UPDATE customer SET sms_validate = TRUE WHERE msisdn = ?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, msisdn);
                    int rowsUpdated = pst.executeUpdate();
                    
                    if (rowsUpdated > 0) {
                            
                        session.removeAttribute("verification_code");
                        session.removeAttribute("msisdn");
                        
                        response.sendRedirect(request.getContextPath() + "/user_profile.html");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/verify.html?error=User not found");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/verify.html?error=Database error");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/verify.html?error=Invalid code");
        }
    }
}
