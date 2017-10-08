package database;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(
        urlPatterns = "/delete-battle"
)
public class DeleteBattleFromDataBase extends HttpServlet{

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

            ResultSet resultSetBattle = statement.executeQuery("select * from battles WHERE user1_id = '" + connectedUser + "' || battles.user2_id = '" + connectedUser + "'");
            if(!resultSetBattle.first()){
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/find-game.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
            }else {

                resultSetBattle.first();
                int battleId = resultSetBattle.getInt(1);
                int user1Id = resultSetBattle.getInt(2);
                int user2Id = resultSetBattle.getInt(3);

                ResultSet resultSetStatistics1 = statement.executeQuery("select * from statistics_of_players where user_id = '" + user1Id + "'");
                resultSetStatistics1.first();
                int winCount1 = resultSetStatistics1.getInt(2), lossCount1 = resultSetStatistics1.getInt(3);
                ResultSet resultSetStatistics2 = statement.executeQuery("select * from statistics_of_players where user_id = '" + user2Id + "'");
                resultSetStatistics2.first();
                int winCount2 = resultSetStatistics2.getInt(2), lossCount2 = resultSetStatistics2.getInt(3);

                ResultSet resultSetTurnChecker = statement.executeQuery("select * from turn_checker where battle_id = '" + battleId + "'");

                resultSetTurnChecker.first();
                int whichPlayerLose = resultSetTurnChecker.getInt(7);


                if (!resultSetTurnChecker.first()) {
                    PreparedStatement preparedStatement;
                    if (connectedUser == user1Id) {

                        if (whichPlayerLose == 1) {
                            lossCount1++;
                            winCount2++;
                            preparedStatement = connection.prepareStatement("update statistics_of_players set loss_count = '" + lossCount1 + "' where user_id = '" + connectedUser + "'");
                            preparedStatement.execute();
                            preparedStatement = connection.prepareStatement("update statistics_of_players set wins_count = '"+winCount2+"' where user_id = '"+user2Id+"'");
                            preparedStatement.execute();
                        } else if (whichPlayerLose == 2) {
                            winCount1++;
                            lossCount2++;
                            preparedStatement = connection.prepareStatement("update statistics_of_players set wins_count = '" + winCount1 + "'  where user_id = '" + connectedUser + "'");
                            preparedStatement.execute();
                            preparedStatement = connection.prepareStatement("update statistics_of_players set loss_count = '" + lossCount2 + "' where user_id = '" + user2Id + "'");
                            preparedStatement.execute();
                        }
                    } else if (connectedUser == user2Id) {

                        if (whichPlayerLose == 1) {
                            winCount2++;
                            lossCount1++;
                            preparedStatement = connection.prepareStatement("update statistics_of_players set wins_count = '" + winCount2 + "'  where user_id = '" + connectedUser + "'");
                            preparedStatement.execute();
                            preparedStatement = connection.prepareStatement("update statistics_of_players set loss_count = '" + lossCount1 + "' where user_id = '" + user1Id + "'");
                            preparedStatement.execute();
                        } else if (whichPlayerLose == 2) {
                            lossCount2++;
                            winCount1++;
                            preparedStatement = connection.prepareStatement("update statistics_of_players set loss_count = '" + lossCount2 + "' where user_id = '" + connectedUser + "'");
                            preparedStatement.execute();
                            preparedStatement = connection.prepareStatement("update statistics_of_players set wins_count = '"+winCount1+"' where user_id = '"+user1Id+"'");
                            preparedStatement.execute();
                        }
                    }

                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/find-game.jsp");
                    rd.forward(httpServletRequest, httpServletResponse);
                } else {
                    PreparedStatement preparedStatement;
                    if (connectedUser == user1Id) {


                        if (whichPlayerLose == 1) {
                            lossCount1++;
                            winCount2++;
                            preparedStatement = connection.prepareStatement("update statistics_of_players set loss_count = '" + lossCount1 + "' where user_id = '" + connectedUser + "'");
                            preparedStatement.execute();
                            preparedStatement = connection.prepareStatement("update statistics_of_players set wins_count = '"+winCount2+"' where user_id = '"+user2Id+"'");
                            preparedStatement.execute();
                        } else if (whichPlayerLose == 2) {
                            winCount1++;
                            lossCount2++;
                            preparedStatement = connection.prepareStatement("update statistics_of_players set wins_count = '" + winCount1 + "'  where user_id = '" + connectedUser + "'");
                            preparedStatement.execute();
                            preparedStatement = connection.prepareStatement("update statistics_of_players set loss_count = '" + lossCount2 + "' where user_id = '" + user2Id + "'");
                            preparedStatement.execute();
                        }
                    } else if (connectedUser == user2Id) {

                        if (whichPlayerLose == 1) {
                            winCount2++;
                            lossCount1++;
                            preparedStatement = connection.prepareStatement("update statistics_of_players set wins_count = '" + winCount2 + "'  where user_id = '" + connectedUser + "'");
                            preparedStatement.execute();
                            preparedStatement = connection.prepareStatement("update statistics_of_players set loss_count = '" + lossCount1 + "' where user_id = '" + user1Id + "'");
                            preparedStatement.execute();
                        } else if (whichPlayerLose == 2) {
                            lossCount2++;
                            winCount1++;
                            preparedStatement = connection.prepareStatement("update statistics_of_players set loss_count = '" + lossCount2 + "' where user_id = '" + connectedUser + "'");
                            preparedStatement.execute();
                            preparedStatement = connection.prepareStatement("update statistics_of_players set wins_count = '"+winCount1+"' where user_id = '"+user1Id+"'");
                            preparedStatement.execute();
                        }
                    }

                    preparedStatement = connection.prepareStatement("delete from turn_checker where battle_id = '" + battleId + "'");
                    preparedStatement.execute();
                    preparedStatement = connection.prepareStatement("delete from userfields where battle_id = '" + battleId + "'");
                    preparedStatement.execute();
                    preparedStatement = connection.prepareStatement("delete from battles where id = '" + battleId + "'");
                    preparedStatement.execute();

                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/find-game.jsp");
                    rd.forward(httpServletRequest, httpServletResponse);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
