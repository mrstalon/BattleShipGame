package gameMenuCode;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(
        urlPatterns = "/view-stats"
)
public class ViewStatistics extends HttpServlet{

    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String query = "select * from users WHERE session_id = '"+httpServletRequest.getSession().getId()+"'";
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection connection = DriverManager.getConnection(HOST, USERNAME,PASSWORD);
            Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.first();
            int connectedUser = resultSet.getInt(1);

            ResultSet resultSetOfStats = statement.executeQuery("select * from statistics_of_players where user_id = '"+connectedUser+"'");
            resultSetOfStats.first();
            Integer wins = resultSetOfStats.getInt(2);
            Integer loss = resultSetOfStats.getInt(3);
            Double winsToLoss = (double) (wins / loss);



            httpServletRequest.setAttribute("wins", wins.toString());
            httpServletRequest.setAttribute("loss", loss.toString());
            httpServletRequest.setAttribute("winsToLoss", winsToLoss.toString());
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/stats.jsp");
            rd.forward(httpServletRequest, httpServletResponse);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doPost(httpServletRequest, httpServletResponse);
    }
}
