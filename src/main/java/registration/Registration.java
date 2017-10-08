package registration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@WebServlet(
        urlPatterns = "/registration"
)
public class Registration extends HttpServlet{

    private boolean isLoginValid = false;
    private boolean isPassValid = false;
    private  static char[] convertedString;
    private final String INSERT_NEW = "insert into users values(?,?,?,?)";
    private static String login;
    private static String password;
    private static int queuePosition = 0;
    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        JSONParser jp = new JSONParser();
        String s = httpServletRequest.getParameterValues("data")[0];
        JSONArray arrayOfPassAndLog = null;
        try {
            arrayOfPassAndLog = (JSONArray) jp.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        login = arrayOfPassAndLog.get(0).toString();
        password = arrayOfPassAndLog.get(1).toString();

        isLoginValid = validChecker(login);
        isPassValid = validChecker(password);

        if(isLoginValid && isPassValid){
            httpServletResponse.getWriter().write("Success");
        }else if(isLoginValid && !isPassValid){
            httpServletResponse.getWriter().write("Pass");
        }else if(isPassValid && !isLoginValid){
            httpServletResponse.getWriter().write("Log");
        }else if(!isLoginValid && !isPassValid){
            httpServletResponse.getWriter().write("Both");
        }

        isPassValid = false;
        isLoginValid = false;
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        String query = "select * from users";
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
            while(resultSet.next()){
                queuePosition = resultSet.getInt(1)+1;
                String promLogin = resultSet.getString(2);
                if(promLogin.equals(login)){
                    isLoginValid = false;
                    break;
                }else{
                    isLoginValid = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Connection connection = null;
        PreparedStatement preparedStatement = null;

            if(isLoginValid){
            try {
                Class.forName(JDBC_DRIVER);
                connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
                preparedStatement = connection.prepareStatement(INSERT_NEW);
                preparedStatement.setInt(1,queuePosition);
                preparedStatement.setString(2, login);
                password = md5Apache(password);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4,httpServletRequest.getSession().getId());
                preparedStatement.execute();

                preparedStatement = connection.prepareStatement("insert into statistics_of_players VALUES(?,?,?,?)");
                preparedStatement.setInt(1,queuePosition);
                preparedStatement.setInt(2,0);
                preparedStatement.setInt(3,0);
                preparedStatement.setInt(4,0);
                preparedStatement.execute();

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
                httpServletResponse.getWriter().write("true");
        }else{
            httpServletResponse.getWriter().write("false");
        }
    }
    static boolean validChecker(String checkSubject){
        convertedString = checkSubject.toLowerCase().toCharArray();

        boolean returnStatement = false;

        for(int i=0; i<convertedString.length; i++) {
            if (convertedString[i] == 'q' ||
                    convertedString[i] == 'w' ||
                    convertedString[i] == 'e' ||
                    convertedString[i] == 'r' ||
                    convertedString[i] == 't' ||
                    convertedString[i] == 'y' ||
                    convertedString[i] == 'u' ||
                    convertedString[i] == 'i' ||
                    convertedString[i] == 'o' ||
                    convertedString[i] == 'p' ||
                    convertedString[i] == 'a' ||
                    convertedString[i] == 's' ||
                    convertedString[i] == 'd' ||
                    convertedString[i] == 'f' ||
                    convertedString[i] == 'g' ||
                    convertedString[i] == 'h' ||
                    convertedString[i] == 'j' ||
                    convertedString[i] == 'k' ||
                    convertedString[i] == 'l' ||
                    convertedString[i] == 'z' ||
                    convertedString[i] == 'x' ||
                    convertedString[i] == 'c' ||
                    convertedString[i] == 'v' ||
                    convertedString[i] == 'b' ||
                    convertedString[i] == 'n' ||
                    convertedString[i] == 'm' ||
                    convertedString[i] == '1' ||
                    convertedString[i] == '2' ||
                    convertedString[i] == '3' ||
                    convertedString[i] == '4' ||
                    convertedString[i] == '5' ||
                    convertedString[i] == '6' ||
                    convertedString[i] == '7' ||
                    convertedString[i] == '8' ||
                    convertedString[i] == '9' ||
                    convertedString[i] == '0' ||
                    convertedString[i] == '_'
                    ) {
                returnStatement = true;
            } else {
                return false;
            }
        }
        return returnStatement;
    }

    public static String md5Apache(String st) {
        String md5Hex = DigestUtils.md5Hex(st);

        return md5Hex;
    }
}
