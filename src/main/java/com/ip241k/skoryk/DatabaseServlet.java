package com.ip241k.skoryk;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/database")
public class DatabaseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://db:3306/javaSpringLR", "admin", "12345678");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"username\":\"").append(rs.getString("username")).append("\",")
                    .append("\"email\":\"").append(rs.getString("email")).append("\"")
                    .append("}");
                if (!rs.isLast()) {
                    json.append(",");
                }
            }
            json.append("]");
            out.print(json.toString());
            conn.close();
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://db:3306/javaSpringLR", "admin", "12345678");

            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, email) VALUES (?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.executeUpdate();

            out.print("{\"status\":\"success\", \"added\":\"" + username + "\"}");
            conn.close();
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }
        out.flush();
    }
}