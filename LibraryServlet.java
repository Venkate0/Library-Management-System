import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LibraryServlet")
public class LibraryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // JDBC database URL, username, and password
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library1";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "mysql";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String role = request.getParameter("role");
        String action = request.getParameter("action");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            if (role.equals("librarian")) {
                if (action.equals("addBook")) 
                {
                    String idParam = request.getParameter("id");
                    String title = request.getParameter("title");
                    String author = request.getParameter("author");
                    String quantityParam = request.getParameter("quantity");

                    // Check if any of the parameters are null or empty
                    if (idParam == null || title == null || author == null || quantityParam == null ) 
                    {
                        out.println("Invalid book details. Please fill in all the fields.");
                    }
                    else
                    {
                        try {
                            int id = Integer.parseInt(idParam);
                            int quantity = Integer.parseInt(quantityParam);

                            PreparedStatement stmt = conn.prepareStatement(
                                    "INSERT INTO books (id, title, author, quantity) VALUES (?, ?, ?, ?)");
                            stmt.setInt(1, id);
                            stmt.setString(2, title);
                            stmt.setString(3, author);
                            stmt.setInt(4, quantity);

                            int rows = stmt.executeUpdate();
                            if (rows > 0) {
                                out.println("Book added successfully!");
                            } else {
                                out.println("Failed to add book!");
                            }
                            stmt.close();
                        } catch (NumberFormatException e) {
                            out.println("Invalid id or quantity. Please enter valid integer values.");
                        } catch (SQLException e) {
                            out.println("An error occurred: " + e.getMessage());
                        }
                    }
                } else if (action.equals("viewBooks")) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM books");

                    out.println("<table>");
                    out.println("<tr><th>ID</th><th>Title</th><th>Author</th><th>Quantity</th></tr>");

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        int quantity = rs.getInt("quantity");

                        out.println("<tr><td>" + id + "</td><td>" + title + "</td><td>" + author + "</td><td>"
                                + quantity + "</td></tr>");
                    }

                    out.println("</table>");
                    rs.close();
                    stmt.close();
                } else if (action.equals("deleteBook")) {
                    String bookIdParam = request.getParameter("bookId");

                    if (bookIdParam == null || bookIdParam.isEmpty()) {
                        out.println("Invalid book ID. Please provide a valid book ID.");
                    } else {
                        int bookId = Integer.parseInt(bookIdParam);

                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id = ?");
                        stmt.setInt(1, bookId);

                        int rows = stmt.executeUpdate();
                        if (rows > 0) {
                            out.println("Book deleted successfully!");
                        } else {
                            out.println("Failed to delete book!");
                        }
                        stmt.close();
                    }
                } else if (action.equals("issueBook")) {
                    String bookIdParam = request.getParameter("bookId");
                    String userIdParam = request.getParameter("userId");

                    if (bookIdParam == null || bookIdParam.isEmpty() || userIdParam == null || userIdParam.isEmpty()) {
                        out.println("Invalid book ID or user ID. Please provide valid IDs.");
                    } else {
                        int bookId = Integer.parseInt(bookIdParam);
                        int userId = Integer.parseInt(userIdParam);

                        PreparedStatement stmt = conn
                                .prepareStatement("INSERT INTO issued_books (book_id, user_id) VALUES (?, ?)");
                        stmt.setInt(1, bookId);
                        stmt.setInt(2, userId);

                        int rows = stmt.executeUpdate();
                        if (rows > 0) {
                            out.println("Book issued successfully!");
                        } else {
                            out.println("Failed to issue book!");
                        }
                        stmt.close();
                    }
                } else if (action.equals("returnBook")) {
                    String bookIdParam = request.getParameter("bookId");

                    if (bookIdParam == null || bookIdParam.isEmpty()) {
                        out.println("Invalid book ID. Please provide a valid book ID.");
                    } else {
                        int bookId = Integer.parseInt(bookIdParam);

                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM issued_books WHERE book_id = ?");
                        stmt.setInt(1, bookId);

                        int rows = stmt.executeUpdate();
                        if (rows > 0) {
                            out.println("Book returned successfully!");
                        } else {
                            out.println("Failed to return book!");
                        }
                        stmt.close();
                    }
                }
            } else if (role.equals("student")) {
                if (action.equals("viewBooks")) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM books");

                    out.println("<table>");
                    out.println("<tr><th>ID</th><th>Title</th><th>Author</th><th>Quantity</th></tr>");

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        int quantity = rs.getInt("quantity");

                        out.println("<tr><td>" + id + "</td><td>" + title + "</td><td>" + author + "</td><td>"
                                + quantity + "</td></tr>");
                    }

                    out.println("</table>");
                    rs.close();
                    stmt.close();
                } else if (action.equals("requestBook")) {
                    String bookIdParam = request.getParameter("bookId");

                    if (bookIdParam == null || bookIdParam.isEmpty()) {
                        out.println("Invalid book ID. Please provide a valid book ID.");
                    } else {
                        int bookId = Integer.parseInt(bookIdParam);

                        PreparedStatement stmt = conn
                                .prepareStatement("INSERT INTO requested_books (book_id) VALUES (?)");
                        stmt.setInt(1, bookId);

                        int rows = stmt.executeUpdate();
                        if (rows > 0) {
                            out.println("Book requested successfully!");
                        } else {
                            out.println("Failed to request book!");
                        }
                        stmt.close();
                    }
                } else if (action.equals("returnBook")) {
                    String bookIdParam = request.getParameter("bookId");

                    if (bookIdParam == null || bookIdParam.isEmpty()) {
                        out.println("Invalid book ID. Please provide a valid book ID.");
                    } else {
                        int bookId = Integer.parseInt(bookIdParam);

                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM issued_books WHERE book_id = ?");
                        stmt.setInt(1, bookId);

                        int rows = stmt.executeUpdate();
                        if (rows > 0) {
                            out.println("Book returned successfully!");
                        } else {
                            out.println("Failed to return book!");
                        }
                        stmt.close();
                    }
                }
            }

            conn.close();
        } catch (Exception e) {
            out.println("An error occurred: " + e.getMessage());
        }
    }
}
