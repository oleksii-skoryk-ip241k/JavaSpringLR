package com.ip241k.skoryk;

import com.ip241k.skoryk.dao.UserDAO;
import com.ip241k.skoryk.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isBlank()) {
                    int id = Integer.parseInt(idParam);
                    User user = userDAO.getUserById(id);
                    if (user == null) {
                        response.setStatus(404);
                        out.print("{\"error\":\"User not found\"}");
                        return;
                    }
                    out.print(userToJson(user));
                    return;
                }

                List<User> users = userDAO.getAllUsers();
                out.print(usersToJson(users));
                return;
            }

            int id = parseIdFromPath(pathInfo);
            User user = userDAO.getUserById(id);
            if (user == null) {
                response.setStatus(404);
                out.print("{\"error\":\"User not found\"}");
            } else {
                out.print(userToJson(user));
            }
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().print(jsonError(e.getMessage()));
        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.getWriter().print(jsonError("Invalid user id"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String body = readRequestBody(request);
            String username = getJsonField(body, "username");
            String email = getJsonField(body, "email");

            if (username == null || username.isBlank()) {
                response.setStatus(400);
                out.print(jsonError("username is required"));
                return;
            }

            User user = new User(username, email);
            userDAO.createUser(user);

            response.setStatus(201);
            out.print(userToJson(user));
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().print(jsonError(e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try (PrintWriter out = response.getWriter()) {
            int id = parseIdFromPath(pathInfo);
            User existing = userDAO.getUserById(id);
            if (existing == null) {
                response.setStatus(404);
                out.print(jsonError("User not found"));
                return;
            }

            String body = readRequestBody(request);
            String username = getJsonField(body, "username");
            String email = getJsonField(body, "email");

            if (username != null && !username.isBlank()) {
                existing.setUsername(username);
            }
            if (email != null) {
                existing.setEmail(email);
            }

            userDAO.updateUser(existing);
            out.print(userToJson(existing));
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().print(jsonError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.getWriter().print(jsonError(e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try (PrintWriter out = response.getWriter()) {
            int id = parseIdFromPath(pathInfo);
            userDAO.deleteUser(id);
            out.print("{\"status\":\"deleted\",\"id\":" + id + "}");
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().print(jsonError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.getWriter().print(jsonError(e.getMessage()));
        }
    }

    private static int parseIdFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new IllegalArgumentException("User id is required in path");
        }
        String idText = pathInfo.substring(1);
        return Integer.parseInt(idText);
    }

    private static String readRequestBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining("\n"));
    }

    private static String getJsonField(String body, String field) {
        if (body == null || body.isBlank()) {
            return null;
        }
        String search = '"' + field + '"';
        int pos = body.indexOf(search);
        if (pos < 0) {
            return null;
        }
        int colon = body.indexOf(':', pos + search.length());
        if (colon < 0) {
            return null;
        }
        int start = body.indexOf('"', colon + 1);
        if (start < 0) {
            return null;
        }
        int end = body.indexOf('"', start + 1);
        if (end < 0) {
            return null;
        }
        return body.substring(start + 1, end);
    }

    private static String jsonError(String message) {
        return "{\"error\":\"" + escapeJson(message) + "\"}";
    }

    private static String userToJson(User user) {
        return "{\"id\":" + user.getId()
                + ",\"username\":\"" + escapeJson(user.getUsername()) + "\""
                + ",\"email\":\"" + escapeJson(user.getEmail()) + "\"}";
    }

    private static String usersToJson(List<User> users) {
        return users.stream()
                .map(UserServlet::userToJson)
                .collect(Collectors.joining(",", "[", "]"));
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
