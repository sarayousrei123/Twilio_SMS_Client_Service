/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.tsms;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;

/**
 *
 * @author syousrei
 */

@WebServlet("/DeleteMessagesServlet")
public class DeleteMessagesServlet_customer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        JSONArray messagesArray = new JSONArray();
        
        try (Connection conn = PSQL.getConnection()) {
            String query = "SELECT s_id, body, date, status FROM sms";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject message = new JSONObject();
                message.put("s_id", rs.getInt("s_id"));
                message.put("body", rs.getString("body"));
                message.put("date", rs.getString("date"));
                message.put("status", rs.getString("status"));
                messagesArray.put(message);
            }
            out.print(messagesArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        
        
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            jsonBuffer.append(line);
        }
        
        JSONObject jsonRequest = new JSONObject(jsonBuffer.toString());
        JSONArray messageIds = jsonRequest.getJSONArray("messageIds");

        try (Connection conn = PSQL.getConnection()) {
            String query = "DELETE FROM sms WHERE s_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            for (int i = 0; i < messageIds.length(); i++) {
                stmt.setInt(1, messageIds.getInt(i));
                stmt.addBatch(); 
            }

            stmt.executeBatch(); 
            response.getWriter().print("{\"status\": \"success\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
