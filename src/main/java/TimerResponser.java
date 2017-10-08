import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(
        urlPatterns = "/timer"
)
public class TimerResponser extends HttpServlet{

    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";


    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String query = "select * from users WHERE session_id = '"+request.getSession().getId()+"'";
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

            ResultSet resultSetBattle = statement.executeQuery("select * from battles WHERE user1_id = '"+connectedUser+"' || battles.user2_id = '"+connectedUser+"'");
            resultSetBattle.first();
            int battleId = resultSetBattle.getInt(1);
            int user1Id = resultSetBattle.getInt(2);
            int user2Id = resultSetBattle.getInt(3);
            ResultSet resultSetUserFields = statement.executeQuery("select * from userfields where battle_id = '"+battleId+"'");
            resultSetUserFields.first();

            String notParsedUser1Field = resultSetUserFields.getString(3),
                    notParsedUser2Field = resultSetUserFields.getString(4);

            if(connectedUser == user1Id){
                response.getWriter().write(notParsedUser1Field);
            }else if(connectedUser == user2Id){
                response.getWriter().write(notParsedUser2Field);
            }



        } catch (SQLException e) {
            try(Connection connection = DriverManager.getConnection(HOST, USERNAME,PASSWORD);
                Statement statement = connection.createStatement()
            ) {
                ResultSet resultSet = statement.executeQuery(query);
                resultSet.first();
                int connectedUser = resultSet.getInt(1);

                ResultSet resultSetBattle = statement.executeQuery("select * from battles WHERE user1_id = '"+connectedUser+"' || battles.user2_id = '"+connectedUser+"'");
                resultSetBattle.first();
                int battleId = resultSetBattle.getInt(1);

                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("delete from turn_checker where battle_id = '"+battleId+"'");
                preparedStatement.execute();
                preparedStatement = connection.prepareStatement("delete from userfields where battle_id = '"+battleId+"'");
                preparedStatement.execute();
                preparedStatement = connection.prepareStatement("delete from battles where id = '"+battleId+"'");
                preparedStatement.execute();
                connection.close();
            } catch (SQLException r) {
                r.printStackTrace();
            }
            e.printStackTrace();
        }


    }
}
