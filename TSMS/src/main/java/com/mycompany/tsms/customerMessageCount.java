package com.mycompany.tsms;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/customerMessageCount")
public class customerMessageCount extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = PSQL.getConnection()) {
            
            String customerQuery = "SELECT COUNT(*) AS customerCount FROM customer"; 
            PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
            ResultSet customerResult = customerStmt.executeQuery();
            int customerCount = 0;
            if (customerResult.next()) {
                customerCount = customerResult.getInt("customerCount");
            }

            
            String messageQuery = "SELECT COUNT(*) AS messageCount FROM sms"; 
            PreparedStatement messageStmt = conn.prepareStatement(messageQuery);
            ResultSet messageResult = messageStmt.executeQuery();
            int messageCount = 0;
            if (messageResult.next()) {
                messageCount = messageResult.getInt("messageCount");
            }

            
            String jsonResponse = String.format("{\"customerCount\": %d, \"messageCount\": %d}", customerCount, messageCount);
            out.print(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"Error fetching data\"}");
        }
    }
}