
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

    <style>

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
        .form button:hover,.form button:active,.form button:focus {
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


        .error-text{
            color: red;
        }
    </style>

</head>
<body>

<form id="form-to-redirect" style="display: none" action="redirecting" method="get"></form>

<div class="login-page">
    <div class="form">
        <div class="register-form" id="register-form">
            <input type="text" placeholder="Login" id="login" name="login"/>
            <label for="login" style="display: none" class="error-text" id="label-login"></label>
            <input type="password" placeholder="Password" id="password" name="password"/>
            <label for="password" style="display: none" class="error-text" id="label-pass"></label>
            <button type="button" id="log-button">create</button>
            <p class="message">Already registered? <a href="index.jsp">Sign In</a></p>
        </div>
    </div>
</div>

<script>
    $('#register-form').show();

    $('#log-button').click(
        function () {
            var isLoginValid = false,
             isPassValid = false,
                PassAndLoginArray = [],
            login = $('#login').val(),
            password = $('#password').val();

            $('#label-login').hide();
            $('#label-pass').hide();

            if(login===""){
                $('#label-login').show();
                $('#label-login').text("Данное поле не может быть пустым");
            }else{
                if(login.length>15){
                    $('#label-login').show();
                    $('#label-login').text("Логин не должен быть длиннее 15 символов");
                }else {
                    isLoginValid = true;
                }
            }

            if(password===""){
                $('#label-pass').show();
                $('#label-pass').text("Данное поле не может быть пустым");
            }else{
                if(password.length>20){
                    $('#label-pass').show();
                    $('#label-pass').text("Пароль не должен быть длиннее 15 символов");
                }else {
                    isPassValid = true;
                }
            }

            if(isLoginValid && isPassValid){
                PassAndLoginArray.push(login);
                PassAndLoginArray.push(password);

                $.ajax({
                    url: "/test/registration",
                    type: "GET",
                    dataType: "text",
                    mimeType: "application/json",
                    data: ({data: JSON.stringify(PassAndLoginArray)}),
                    success: function (msg) {
                        if(msg==="Success"){
                            $.ajax({
                                url: "/test/registration",
                                type: "POST",
                                dataType: "text",
                                mimeType: "application/json",
                                data: ({data: JSON.stringify(PassAndLoginArray)}),
                                success:
                                    function (msg) {
                                        if(msg==="false"){
                                            $('#label-login').show();
                                            $('#label-login').text("Данный логин занят, не удалось зарегистрировать вас");
                                        }else if(msg==="true"){
                                            $('#form-to-redirect').submit();
                                        }
                                }
                            });
                        }else if(msg==="Pass"){
                            $('#label-pass').show();
                            $('#label-pass').text("Вы использовали недопустимый символ");
                        }else if(msg==="Log"){
                            $('#label-login').show();
                            $('#label-login').text("Вы использовали недопустимый символ");
                        }else if(msg==="Both"){
                            $('#label-pass').show();
                            $('#label-login').show();
                            $('#label-pass').text("Вы использовали недопустимый символ");
                            $('#label-login').text("Вы использовали недопустимый символ");
                        }
                    }
                });

                PassAndLoginArray.length = 0;
            }

        });

</script>

</body>
</html>
