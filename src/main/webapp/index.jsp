<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>StartGame</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

    <style>
        .button {
            position: absolute;
            top: 100px;
            left: 200px;
            display: inline-block;
            margin: 0 auto;

            -webkit-border-radius: 10px;

            -webkit-box-shadow: 0px 3px rgba(128, 128, 128, 1), /* gradient effects */ 0px 4px rgba(118, 118, 118, 1),
            0px 5px rgba(108, 108, 108, 1),
            0px 6px rgba(98, 98, 98, 1),
            0px 7px rgba(88, 88, 88, 1),
            0px 8px rgba(78, 78, 78, 1),
            0px 14px 6px -1px rgba(128, 128, 128, 1); /* shadow */

            -webkit-transition: -webkit-box-shadow .1s ease-in-out;
        }

        @import url(https://fonts.googleapis.com/css?family=Roboto:300);

        .login-page {
            width: 360px;
            padding: 8% 0 0;
            margin: auto;
        }

        .form {
            position: relative;
            z-index: 1;
            background: #FFFFFF;
            max-width: 360px;
            margin: 0 auto 100px;
            padding: 45px;
            text-align: center;
            box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);
        }

        .form input {
            font-family: "Roboto", sans-serif;
            outline: 0;
            background: #f2f2f2;
            width: 100%;
            border: 0;
            margin: 0 0 15px;
            padding: 15px;
            box-sizing: border-box;
            font-size: 14px;
        }

        .form button {
            font-family: "Roboto", sans-serif;
            text-transform: uppercase;
            outline: 0;
            background: #4CAF50;
            width: 100%;
            border: 0;
            padding: 15px;
            color: #FFFFFF;
            font-size: 14px;
            -webkit-transition: all 0.3 ease;
            transition: all 0.3 ease;
            cursor: pointer;
        }

        .form button:hover, .form button:active, .form button:focus {
            background: #43A047;
        }

        .form .message {
            margin: 15px 0 0;
            color: #b3b3b3;
            font-size: 12px;
        }

        .form .message a {
            color: #4CAF50;
            text-decoration: none;
        }

        .form .register-form {
            display: none;
        }

        .container {
            position: relative;
            z-index: 1;
            max-width: 300px;
            margin: 0 auto;
        }

        .container:before, .container:after {
            content: "";
            display: block;
            clear: both;
        }

        .container .info {
            margin: 50px auto;
            text-align: center;
        }

        .container .info h1 {
            margin: 0 0 15px;
            padding: 0;
            font-size: 36px;
            font-weight: 300;
            color: #1a1a1a;
        }

        .container .info span {
            color: #4d4d4d;
            font-size: 12px;
        }

        .container .info span a {
            color: #000000;
            text-decoration: none;
        }

        .container .info span .fa {
            color: #EF3B3A;
        }

        body {
            background: #76b852; /* fallback for old browsers */
            background: -webkit-linear-gradient(right, #76b852, #8DC26F);
            background: -moz-linear-gradient(right, #76b852, #8DC26F);
            background: -o-linear-gradient(right, #76b852, #8DC26F);
            background: linear-gradient(to left, #76b852, #8DC26F);
            font-family: "Roboto", sans-serif;
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
        }

        .error-text {
            color: red;
        }

    </style>
</head>
<body>


<div class="login-page">
    <div class="form">
        <form class="login-form" id="login-form" method="post" action="login">
            <input type="text" placeholder="login" id="login" name="login"/>
            <label for="login" style="display: none" class="error-text" id="label-login">${result}</label>
            <input type="password" placeholder="password" id="password" name="password"/>
            <label for="password" style="display: none" class="error-text" id="label-pass">${resultPass}</label>
            <button id="log-button" type="button">Login</button>
            <p class="message">Not registered? <a href="register.jsp">Create an account</a></p>
        </form>
    </div>
</div>

<script>

    $('#label-login').show();
    $('#label-pass').show();

    $('#log-button').click(
        function () {
            var isLoginValid = false;
            var isPassValid = false;
            var login = $('#login').val();
            var password = $('#password').val();
            $('#label-login').hide();
            $('#label-pass').hide();

            if (login === "") {
                $('#label-login').show();
                $('#label-login').text("Данное поле не может быть пустым");
            } else {
                if (login.length > 15) {
                    $('#label-login').show();
                    $('#label-login').text("Логин не должен быть длиннее 15 символов");
                } else {
                    isLoginValid = true;
                }
            }

            if (password === "") {
                $('#label-pass').show();
                $('#label-pass').text("Данное поле не может быть пустым");
            } else {
                if (password.length > 15) {
                    $('#label-pass').show();
                    $('#label-pass').text("Пароль не должен быть длиннее 15 символов");
                } else {
                    isPassValid = true;
                }
            }

            console.log('pass ---' + isPassValid);
            console.log('log ---' + isLoginValid);

            if (isLoginValid && isPassValid) {
                $('#login-form').submit();
            }

        });
</script>

</body>
</html>
