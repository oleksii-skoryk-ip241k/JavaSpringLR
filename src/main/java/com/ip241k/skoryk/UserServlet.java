package com.ip241k.skoryk;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Імітація @RequestParam
        String requestParam = request.getParameter("param");

        // Імітація @PathVariable
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(400);
            return;
        }
        String pathVar = pathInfo.substring(1);

        // Вивід HTML на сторінці
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<h1>Hello, Servlet!</h1>");
        out.println("<h3>Дані з запиту:</h3>");
        out.println(String.format("<p>@RequestParam: %s</p>", requestParam));
        out.println(String.format("<p>@PathVariable: %s</p>", pathVar));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Робота з сесією
        HttpSession session = request.getSession();
        session.setAttribute("param", "pam-pam");

        // Робота з кукі
        Cookie cookie = new Cookie("name", "oreo");
        cookie.setMaxAge(10000);
        response.addCookie(cookie);

        // Повернення JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("{\"status\": 200}");
        out.flush();
    }
}
