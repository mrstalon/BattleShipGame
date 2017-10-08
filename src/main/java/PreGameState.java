
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(
        urlPatterns = "/PreGameState"
)
public class PreGameState extends HttpServlet{

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

            ResultSet resultSetTurnChecker = statement.executeQuery("select * from turn_checker where battle_id = '"+battleId+"'");
            resultSetTurnChecker.first();
            boolean firstUserTurn = resultSetTurnChecker.getBoolean(3),
                    secondUserTurn = resultSetTurnChecker.getBoolean(4);

            if(connectedUser == user1Id){

                String parsedUser1Field = request.getParameterValues("data")[0];


                PreparedStatement preparedStatement = null;
                preparedStatement = connection.prepareStatement("update userfields set first_user_field = '"+parsedUser1Field+"' where battle_id = '"+battleId+"'");
                preparedStatement.execute();


                if(!secondUserTurn){
                    firstUserTurn = true;
                }

            }else if(connectedUser == user2Id){
                String parsedUser2Field = request.getParameterValues("data")[0];


                if(!firstUserTurn){
                    secondUserTurn = true;
                }

                PreparedStatement preparedStatement = null;
                preparedStatement = connection.prepareStatement("update userfields set second_user_field = '"+parsedUser2Field+"' where battle_id = '"+battleId+"'");
                preparedStatement.execute();
            }

            PreparedStatement preparedStatement = null;
            preparedStatement = connection.prepareStatement("update turn_checker set first_user_turn = '"+convertBooleanToInt(firstUserTurn)+"', second_user_turn = '"+convertBooleanToInt(secondUserTurn)+"'");
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        }

    private int convertBooleanToInt(boolean bool) {
        int convertedBool;

        if (bool) {
            convertedBool = 1;
        } else {
            convertedBool = 0;
        }
        return convertedBool;
    }

}
