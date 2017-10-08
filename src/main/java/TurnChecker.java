
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(
        urlPatterns = "/TurnChecker"
)
public class TurnChecker extends HttpServlet{

    public static boolean hasPlayerMadeShot = false;
    public static int[][] cloneOfArray;
    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

            ResultSet resultSetUser1 = statement.executeQuery("select * from users where id = '"+user1Id+"'");
            resultSetUser1.first();
            String user1SessionId = resultSetUser1.getString(4);
            ResultSet resultSetUser2 = statement.executeQuery("select * from users where id = '"+user2Id+"'");
            resultSetUser2.first();
            String user2SessionId = resultSetUser2.getString(4);
            ResultSet resultSetTurnChecker = statement.executeQuery("select * from turn_checker where battle_id = '"+battleId+"'");
            resultSetTurnChecker.first();
            boolean firstUserTurn = resultSetTurnChecker.getBoolean(3),
                    secondUserTurn = resultSetTurnChecker.getBoolean(4);

            if(request.getSession().getId().equals(user1SessionId)){
                if(firstUserTurn){
                    response.getWriter().write("true");
                }else{
                    response.getWriter().write("false");
                }
            }else if(request.getSession().getId().equals(user2SessionId)){
                if(secondUserTurn){
                    response.getWriter().write("true");
                }else{
                    response.getWriter().write("false");
                }
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

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String query = "select * from users WHERE session_id = '"+httpServletRequest.getSession().getId()+"'";
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
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

            ResultSet resultSet1 = statement.executeQuery("select * from users where id = '"+user1Id+"'");
            resultSet1.first();
            String login1 = resultSet1.getString(2);

            ResultSet resultSet2 = statement.executeQuery("select * from users where id = '"+user2Id+"'");
            resultSet2.first();
            String login2 = resultSet2.getString(2);

            ResultSet resultSetTurnChecker = statement.executeQuery("select * from turn_checker where battle_id = '"+battleId+"'");
            resultSetTurnChecker.first();
            boolean firstUserTurn = resultSetTurnChecker.getBoolean(3);


            if(firstUserTurn){
                httpServletResponse.getWriter().write(login1);
            }else{
                httpServletResponse.getWriter().write(login2);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
