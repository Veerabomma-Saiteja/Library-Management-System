import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class BookController
        implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange)
            throws IOException {

        String method =
            exchange.getRequestMethod();

        String path =
            exchange.getRequestURI().getPath();


        try {

            if (method.equals("GET")
                    && path.equals("/api/books")) {

                getBooks(exchange);

            } else if (
                method.equals("POST")
                && path.equals("/api/books")
            ) {

                addBook(exchange);

            } else if (
                method.equals("DELETE")
                && path.matches(
                    "/api/books/[0-9]+"
                )
            ) {

                deleteBook(exchange);

            } else if (
                method.equals("PUT")
                && path.matches(
                    "/api/books/[0-9]+/issue"
                )
            ) {

                updateStatus(
                    exchange,
                    "Issued"
                );

            } else if (
                method.equals("PUT")
                && path.matches(
                    "/api/books/[0-9]+/return"
                )
            ) {

                updateStatus(
                    exchange,
                    "Available"
                );

            } else {

                sendResponse(
                    exchange,
                    404,
                    "{\"error\":\"Not found\"}"
                );
            }

        } catch (Exception e) {

            sendResponse(
                exchange,
                500,
                "{\"error\":\"" +
                escapeJson(e.getMessage()) +
                "\"}"
            );
        }
    }


    private void getBooks(
            HttpExchange exchange)
            throws Exception {

        StringBuilder json =
            new StringBuilder("[");


        Connection connection =
            DatabaseConnection
                .getConnection();


        String sql =
            "SELECT * FROM books ORDER BY id DESC";


        PreparedStatement statement =
            connection.prepareStatement(sql);


        ResultSet rs =
            statement.executeQuery();


        boolean first = true;


        while (rs.next()) {

            if (!first) {
                json.append(",");
            }

            json.append("{");

            json.append("\"id\":")
                .append(rs.getInt("id"))
                .append(",");

            json.append("\"title\":\"")
                .append(
                    escapeJson(
                        rs.getString("title")
                    )
                )
                .append("\",");

            json.append("\"author\":\"")
                .append(
                    escapeJson(
                        rs.getString("author")
                    )
                )
                .append("\",");

            json.append("\"category\":\"")
                .append(
                    escapeJson(
                        rs.getString("category")
                    )
                )
                .append("\",");

            json.append("\"status\":\"")
                .append(
                    escapeJson(
                        rs.getString("status")
                    )
                )
                .append("\"");

            json.append("}");

            first = false;
        }


        json.append("]");


        rs.close();

        statement.close();

        connection.close();


        sendResponse(
            exchange,
            200,
            json.toString()
        );
    }


    private void addBook(
            HttpExchange exchange)
            throws Exception {

        String body =
            new String(
                exchange.getRequestBody()
                    .readAllBytes(),
                StandardCharsets.UTF_8
            );


        String title =
            getJsonValue(body, "title");

        String author =
            getJsonValue(body, "author");

        String category =
            getJsonValue(body, "category");


        if (
            title.isBlank()
            || author.isBlank()
            || category.isBlank()
        ) {

            sendResponse(
                exchange,
                400,
                "{\"error\":\"All fields are required\"}"
            );

            return;
        }


        Connection connection =
            DatabaseConnection
                .getConnection();


        String sql =
            "INSERT INTO books " +
            "(title, author, category) " +
            "VALUES (?, ?, ?)";


        PreparedStatement statement =
            connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            );


        statement.setString(1, title);

        statement.setString(2, author);

        statement.setString(3, category);


        statement.executeUpdate();


        ResultSet keys =
            statement.getGeneratedKeys();


        int id = 0;

        if (keys.next()) {
            id = keys.getInt(1);
        }


        keys.close();

        statement.close();

        connection.close();


        sendResponse(
            exchange,
            201,
            "{\"message\":\"Book added successfully\",\"id\":"
            + id
            + "}"
        );
    }


    private void deleteBook(
            HttpExchange exchange)
            throws Exception {

        int id =
            getId(exchange);


        Connection connection =
            DatabaseConnection
                .getConnection();


        PreparedStatement statement =
            connection.prepareStatement(
                "DELETE FROM books WHERE id = ?"
            );


        statement.setInt(1, id);


        int rows =
            statement.executeUpdate();


        statement.close();

        connection.close();


        if (rows == 0) {

            sendResponse(
                exchange,
                404,
                "{\"error\":\"Book not found\"}"
            );

            return;
        }


        sendResponse(
            exchange,
            200,
            "{\"message\":\"Book deleted successfully\"}"
        );
    }


    private void updateStatus(
            HttpExchange exchange,
            String status)
            throws Exception {

        int id =
            getId(exchange);


        Connection connection =
            DatabaseConnection
                .getConnection();


        PreparedStatement statement =
            connection.prepareStatement(
                "UPDATE books SET status = ? WHERE id = ?"
            );


        statement.setString(1, status);

        statement.setInt(2, id);


        int rows =
            statement.executeUpdate();


        statement.close();

        connection.close();


        if (rows == 0) {

            sendResponse(
                exchange,
                404,
                "{\"error\":\"Book not found\"}"
            );

            return;
        }


        sendResponse(
            exchange,
            200,
            "{\"message\":\"Status updated successfully\"}"
        );
    }


    private int getId(
            HttpExchange exchange) {

        String path =
            exchange.getRequestURI()
                .getPath();

        String[] parts =
            path.split("/");

        return Integer.parseInt(parts[3]);
    }


    private String getJsonValue(
            String json,
            String key) {

        String search =
            "\"" + key + "\":\"";

        int start =
            json.indexOf(search);

        if (start == -1) {
            return "";
        }

        start += search.length();

        int end =
            json.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        return json.substring(
            start,
            end
        );
    }


    private String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }


    private void sendResponse(
            HttpExchange exchange,
            int status,
            String response)
            throws IOException {

        exchange.getResponseHeaders()
            .set(
                "Content-Type",
                "application/json"
            );

        exchange.getResponseHeaders()
            .set(
                "Access-Control-Allow-Origin",
                "*"
            );

        byte[] bytes =
            response.getBytes(
                StandardCharsets.UTF_8
            );


        exchange.sendResponseHeaders(
            status,
            bytes.length
        );


        OutputStream output =
            exchange.getResponseBody();


        output.write(bytes);

        output.close();
    }
}