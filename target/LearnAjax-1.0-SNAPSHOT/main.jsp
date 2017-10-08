<%@ page contentType="text/html; charset=UTF-8" %>

<html>

<head>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

    <title>Testing AJAX and Java servlets</title>

    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

    <style>

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

        .end-game-text{
            color: forestgreen;
        }

        .missed-shot {
            background: url("resources/cross.png") no-repeat;
        }

        .got-the-shot {
            background: url("resources/fire.gif") no-repeat;
        }

        .a-player-ship {
            background-color: black;
        }

        .checked-cell {
            background-color: green;
            display: none;
        }

        .a-border-of-table {
            background-color: gray;
        }

        .btn-6d {
            color: darkred;
            width: 250px;
            height: 100px;
        }

        .btn-6d:hover {
            background-color: darkkhaki;
        }

        .flex-tables {
            display: flex;
            flex-direction: row;
            align-items: baseline;
        }


        .view-how-to-shoot {
            text-decoration: none;
            display: block;
            color: #898887;
            padding: 5px;
            position: absolute;
        }

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

    </style>

</head>

<body>

<h2 id="title">Форма для расстановки корабля</h2>

<div id="end-game" style="display: none">
    <form class="form" action="delete-battle" method="get">
        <h2 class="end-game-text" id="first-win" style="display: none">Игра закончена, победил первый игрок</h2>
        <h2 class="end-game-text" id="second-win" style="display: none">Игра закончена, победил второй игрок</h2>
        <button type="submit" class="button">Меню игры</button>
    </form>
</div>


<div id="game-state-field" class="flex-tables" style="display: none">

    <div id="game-player-field" class="flex-tables">

    </div>

    <p id="whose-turn"></p>

    <div id="game-enemy-field" class="flex-tables">

    </div>
</div>


<div id="arrangmentForm" style="float: left">

    <div id="selector-of-ships" style="margin-bottom: 20px">

    </div>

    <div style="margin-bottom: 20px">
        <select id="rotation-selector">
            <option value="1">Горизонтально</option>
            <option value="0">Вертикально</option>
        </select>
    </div>

    <div style="margin-bottom: 20px">
        <button id="add-ship" class="btn-6d">Добавить корабль</button>
    </div>
</div>


<div id="gameField">


</div>

<form id="end-game-form" action=" " method="get" style="display:none"></form>


<script>
    var userField = [],
        arrayOfUnplacedShips = [],
        isHorizontal,
        isNowGameState,
        arrayOfCoordinates = [],
        turnString,
        turn,
        stateOfPlacingShips;
    arrayOfUnplacedShips = initializateArrayOfUnplacedShips(arrayOfUnplacedShips);
    selectorDrawer(arrayOfUnplacedShips);
    initialiseJSarray();
    drawTheStartField();
    userField = ${userArray};
    DrawArrayToTable(userField);
    var enemyField = tableToArray();
    enemyField = initializeBordersOfArray(enemyField);
    DrawEnemyArrayToTable(enemyField, "#game-enemy-field");
    DrawArrayToTableWithSelector(userField, "#game-player-field");

    var inter = setInterval(function () {
        if(isNowGameState){
            $.ajax({
                url: "/test/end",
                type: "GET",
                dataType: "json",
                mimeType: "application/json",
                success: function (msg) {
                    if(msg){
                        $.ajax({
                            url: "/test/end",
                            type: "POST",
                            dataType: "text",
                            mimeType: "application/json",
                            success: function (message) {
                                console.log(1);
                                if(message === "first"){
                                    $('#game-state-field').hide();
                                    $('#end-game').show();
                                    $('#second-win').show();
                                    clearInterval(inter);
                                } else if(message === "second"){
                                    $('#game-state-field').hide();
                                    $('#end-game').show();
                                    $('#first-win').show();
                                    clearInterval(inter);
                                }
                            }
                        });
                    }
                }
            });
        }
    }, 2000);

    var inter2 = setInterval(function () {
        $.ajax({
            url: "/test/TurnChecker",
            type: "POST",
            dataType: "text",
            mimeType: "application/json",
            success: function (msg) {
                console.log(msg);
                $('#whose-turn').html("<p>" + "Ходит игрок - " + msg + "</p>" );
            }
        })
    },2000);


    $('#add-ship').click(function () {
        stateOfPlacingShips = true;
        var valueOfSelector = Number($('#rotation-selector').val());
        if (valueOfSelector === 1) {
            isHorizontal = true;
        } else {
            isHorizontal = false;
        }
    });


    $('#gameField').find('table').on('click', "td",
        function () {

            var x = Number(this.cellIndex);
            var y = Number(this.parentNode.rowIndex);
            var decksSize = $('#selector-of-ships').find('select').val();


            if (stateOfPlacingShips === true) {
                if (areThereShipsToPlaceRemaining(arrayOfUnplacedShips)) {
                    if (isClickOnBorder(userField, x, y)) {
                        alert('Нельзя поставить корабль на границу поля');
                        return;
                    } else {
                        if (canLongShipBePlacedOnClick(x, y, decksSize, isHorizontal)) {
                            if (canShipBePlacedThisWay(userField, x, y, decksSize, isHorizontal)) {
                                drawShips(userField, x, y, decksSize, isHorizontal);
                                removePlacedShipFromArrayOfShips(decksSize);

                                stateOfPlacingShips = false;
                                if (!areThereShipsToPlaceRemaining(arrayOfUnplacedShips)) {
                                    $.ajax({
                                        url: "/test/PreGameState",
                                        type: "GET",
                                        dataType: "json",
                                        mimeType: "application/json",
                                        data: ({data: JSON.stringify(userField)})
                                    });

                                  var interir =  setInterval(function () {
                                        $.ajax({
                                            url: "/test/both",
                                            type: "GET",
                                            dataType: "json",
                                            mimeType: "application/json",
                                            success: function (msg) {
                                                if(msg){
                                                    $('#arrangmentForm').hide();
                                                    $('#gameField').hide();
                                                    $('#title').hide();
                                                    $('#game-state-field').show();
                                                    DrawEnemyArrayToTable(enemyField, '#game-enemy-field');
                                                    DrawArrayToTableWithSelector(userField, '#game-player-field');
                                                    isNowGameState = true;
                                                    clearInterval(interir);
                                                }
                                            }
                                        })
                                    }, 1000);
                                }
                            } else {
                                alert('Вы не можете поставить корабль на данную клетку');
                                return;
                            }
                        } else {
                            alert('Вы пытаетесь поставить длинный корабль слишком близко к краю поля');
                            return;
                        }
                    }
                } else {
                    alert('У вас закончились корабли, переход в режим игры');
                    isNowGameState = true;
                    return;
                }
            } else {
                alert('Вы не нажали кнопку "Добавить корабль"');
                return;
            }
        });


    $('#game-enemy-field').on('click', "td",
        function () {
            var x = Number(this.cellIndex);
            var y = Number(this.parentNode.rowIndex);

            if (!$(this).hasClass()) {

                if (isNowGameState) {
                    $.ajax({
                        url: "/test/TurnChecker",
                        type: "GET",
                        success: function (msg) {
                            turnString = msg;

                            if (turnString === "true") {
                                turn = true;
                            } else if (turnString === "false") {
                                turn = false;
                            }

                            if (turn) {
                                arrayOfCoordinates.push(x);
                                arrayOfCoordinates.push(y);

                                $.ajax({
                                    url: "/test/shooting",
                                    type: "GET",
                                    dataType: "json",
                                    mimeType: "application/json",
                                    data: ({data: JSON.stringify(arrayOfCoordinates)}),
                                    success: function (msg) {
                                        enemyField = msg;
                                        DrawEnemyArrayToTableInGame(enemyField, '#game-enemy-field');
                                        arrayOfCoordinates.length = 0;


                                        $.ajax({
                                            url: "/test/timer",
                                            type: "POST",
                                            dataType: "json",
                                            mimeType: "application/json",
                                            success: function (array) {
                                                userField = array;
                                                DrawEnemyArrayToTable(userField, '#game-player-field');
                                            }
                                        });


                                    }
                                });
                            } else {
                                alert("Сейчас не ваш ход");
                            }

                        }
                    });
                    arrayOfCoordinates.length = 0;
                }
            }
        });


    function DrawArrayToTable(array) {

        var table = '';
        for (var r = 0; r < 11; r++) {
            table += '<tr>';

            for (var i = 0; i < 11; i++) {
                if (array[i][r] === 11) {
                    table += '<td class="a-player-ship">' + '</td>';
                } else if (array[i][r] === 12) {
                    table += '<td class="checked-cell">' + array[i][r] + '</td>';
                } else if (array[i][r] <= 10 && array[i][r] > 0 && array[i][r] !== null) {
                    table += '<td class="a-border-of-table">' + array[i][r] + '</td>';
                } else if (array[i][r] === null) {
                    table += '<td class="a-border-of-table">' + '</td>';
                } else {
                    table += '<td>' + '</td>';
                }


            }
            table += '</tr>';

        }
        $("#gameField").html('<table id="mainField" border="2" align="center" width="400px" cellspacing="0" cellpadding="10">' + table + '</table>');
    }


    function initialiseJSarray() {
        var arrayOfThisRow = [];
        for (var r = 0; r < 12; r++) {
            for (var i = 0; i < 12; i++) {
                arrayOfThisRow.push(0);
            }
            userField.push(arrayOfThisRow);
        }
    }

    function selectorDrawer(array) {
        var select = '';
        for (var i = 0; i < array.length; i++) {
            if (array[i] > 0) {
                if (i === 0) {
                    select += '<option value="1">' + "Однопалубный корабль: " + array[i] + '</option>';
                } else if (i === 1) {
                    select += '<option value="2">' + "Двухпалубный корабль: " + array[i] + '</option>';
                } else if (i === 2) {
                    select += '<option value="3">' + "Трёхпалубный корабль: " + array[i] + '</option>';
                } else if (i === 3) {
                    select += '<option value="4">' + "Четырёхпалубный корабль: " + array[i] + '</option>';
                }
            }
        }

        select = '<select>' + select + '</select>';
        $("#arrangmentForm").find('div').first().html(select);
    }

    function initializateArrayOfUnplacedShips(array) {
        array[0] = 0;
        array[1] = 0;
        array[2] = 1;
        array[3] = 0;
        return array;
    }


    function drawTheStartField() {
        var selector = '';
        selector += '<option value="0">' + "Отмена" + '</option>';
        selector += '<option value="1">' + "Выстрел мимо!" + '</option>';
        selector += '<option value="2">' + "Попадание!" + '</option>';
        selector += '<option value="3">' + "Убил!" + '</option>';
        selector = '<select class="view-how-to-shoot" ">' + selector + '</select>';
        selector = '<div style="display: none">' + selector + '</div>';

        var table = '';
        var xLine = 12;
        var yLine = 12;
        for (var r = 0; r < xLine; r++) {
            table += '<tr>';

            for (var i = 0; i < yLine; i++) {
                table += '<td>' + 0 + selector + '</td>';
            }
            table += '</tr>';

        }

        $("#gameField").html('<table id="mainField" border="2" align="center" width="200px" cellspacing="0" cellpadding="15">' + table + '</table>');
    }

    function areThereShipsToPlaceRemaining(array) {
        if (array[0] > 0 || array[1] > 0 || array[2] > 0 || array[3] > 0) {
            return true;
        } else {
            return false;
        }
    }

    function canShipBePlacedThisWay(array, x, y, shipSize) {
        if (isThereShipOnClikPlace(array, x, y)) {
            return false;
        } else {
            if (roundTheClickChecker(array, x, y, shipSize, isHorizontal)) {
                return true;
            } else {
                return false;
            }
        }
    }

    function tableToArray() {
        var myTableArray = [];


        $("table#mainField tr").each(function () {
            var arrayOfThisRow = [];
            var tableData = $(this).find('td');
            if (tableData.length > 0) {
                tableData.each(function () {
                    if ($(this).hasClass('a-player-ship')) {
                        arrayOfThisRow.push(Number("11"));
                    } else {
                        arrayOfThisRow.push(0);
                    }
                });
                myTableArray.push(arrayOfThisRow);
            }
        });
        return myTableArray;
    }

    function DrawArrayToTable(array) {
        var selector = '';
        selector += '<option value="0">' + "Отмена" + '</option>';
        selector += '<option value="1">' + "Выстрел мимо!" + '</option>';
        selector += '<option value="2">' + "Попадание!" + '</option>';
        selector += '<option value="3">' + "Убил!" + '</option>';
        selector = '<select class="view-how-to-shoot" ">' + selector + '</select>';
        selector = '<div style="display: none">' + selector + '</div>';

        var table = '';
        for (var r = 0; r < 11; r++) {
            table += '<tr>';

            for (var i = 0; i < 11; i++) {
                if (array[i][r] === 11) {
                    table += '<td class="a-player-ship">' + selector + '</td>';
                } else if (array[i][r] === 12) {
                    table += '<td class="checked-cell">' + array[i][r] + selector + '</td>';
                } else if (array[i][r] <= 10 && array[i][r] > 0 && array[i][r] !== null) {
                    table += '<td class="a-border-of-table">' + array[i][r] + '</td>';
                } else if (array[i][r] === null) {
                    table += '<td class="a-border-of-table">' + '</td>';
                } else {
                    table += '<td>' + selector + '</td>';
                }


            }
            table += '</tr>';

        }
        $("#mainField").html('<table border="2" align="center" width="200px" cellspacing="0" cellpadding="10">' + table + '</table>')
    }

    function initializeBordersOfArray(array) {

        for (var i = 0; i < 11; i++) {
            array[0][i] = i;
            array[i][0] = i;
        }
        array[0][0] = 20;

        return array;
    }

    function isClickOnBorder(array, x, y) {
        for (var i = 0; i < 11; i++) {
            if (x === 0 || y === 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    function isThereShipOnClikPlace(array, x, y) {
        if (array[x][y] === 11) {
            return true;
        } else {
            return false;
        }
    }

    function roundTheClickChecker(array, x, y, shipSize, isHorizontal) {
        if (isHorizontal) {
            for (var i = 0; i < Number(shipSize); i++) {
                if (x + i > 10) {
                    return false;
                } else if (!(array[x + i][y] !== 11 &&
                    array[x + 1 + i][y] !== 11 &&
                    array[x + 1 + i][y + 1] !== 11 &&
                    array[x + 1 + i][y - 1] !== 11 &&
                    array[x + i][y + 1] !== 11 &&
                    array[x + i][y - 1] !== 11 &&
                    array[x - 1 + i][y] !== 11 &&
                    array[x - 1 + i][y + 1] !== 11 &&
                    array[x - 1 + i][y] !== 11 &&
                    array[x - 1 + i][y - 1] !== 11)) {
                    return false;
                }
            }
        } else {

            for (var j = 0; j < Number(shipSize); j++) {
                if (y + j > 10) {
                    return false;
                } else if (!(array[x][y + j] !== 11 &&
                    array[x + 1][y + j] !== 11 &&
                    array[x + 1][y + 1 + j] !== 11 &&
                    array[x + 1][y - 1 + j] !== 11 &&
                    array[x][y + 1 + j] !== 11 &&
                    array[x][y - 1 + j] !== 11 &&
                    array[x - 1][y + j] !== 11 &&
                    array[x - 1][y + 1 + j] !== 11 &&
                    array[x - 1][y + j] !== 11 &&
                    array[x - 1][y - 1 + j] !== 11)) {
                    return false;
                }
            }
        }
        return true;
    }

    function drawShips(array, x, y, shipSize, isHorizontal) {
        if (isHorizontal === true) {
            for (var i = 0; i < shipSize; i++) {
                array[x + i][y] = 11;
            }
        } else {
            for (i = 0; i < shipSize; i++) {
                array[x][y + i] = 11;
            }
        }
        DrawArrayToTable(array);
        return array;
    }

    function canLongShipBePlacedOnClick(x, y, shipSize, isHorizontal) {
        if (isHorizontal) {
            if (Number(x) + Number(shipSize) <= 11) {
                return true;
            } else {
                return false;
            }
        } else {
            if (Number(y) + Number(shipSize) <= 11) {
                return true;
            } else {
                return false;
            }
        }
    }

    function removePlacedShipFromArrayOfShips(shipSize) {
        switch (Number(shipSize)) {
            case 1 :
                arrayOfUnplacedShips[0] -= 1;
                break;
            case 2 :
                arrayOfUnplacedShips[1] -= 1;
                break;
            case 3 :
                arrayOfUnplacedShips[2] -= 1;
                break;
            case 4 :
                arrayOfUnplacedShips[3] -= 1;
                break;
        }
        selectorDrawer(arrayOfUnplacedShips);
    }

    function DrawEnemyArrayToTable(array, selectorJq) {

        var table = '';
        for (var r = 0; r < 11; r++) {
            table += '<tr>';

            for (var i = 0; i < 11; i++) {
                if (array[i][r] === 11) {
                    table += '<td class="a-player-ship">' + '</td>';
                } else if (array[i][r] === 12) {
                    table += '<td class="checked-cell">' + array[i][r] + '</td>';
                } else if (array[i][r] <= 10 && array[i][r] > 0 && array[i][r] !== null) {
                    table += '<td class="a-border-of-table">' + array[i][r] + '</td>';
                } else if (array[i][r] === 20) {
                    table += '<td class="a-border-of-table">' + '</td>';
                } else if (array[i][r] === 14) {
                    table += '<td class="got-the-shot">' + '</td>';
                } else if (array[i][r] === 13) {
                    table += '<td class="missed-shot">' + '</td>';
                } else {
                    table += '<td>' + '</td>';
                }


            }
            table += '</tr>';

        }
        $(selectorJq).html('<table border="2"  width="200px" cellspacing="0" cellpadding="15">' + table + '</table>');

    }

    function DrawArrayToTableWithSelector(array, selectorJq) {

        var table = '';
        for (var r = 0; r < 11; r++) {
            table += '<tr>';
            for (var i = 0; i < 11; i++) {

                if (array[i][r] === 11) {
                    table += '<td class="a-player-ship">' + '</td>';
                } else if (array[i][r] === 12) {
                    table += '<td class="checked-cell">' + array[i][r] + '</td>';
                } else if (array[i][r] <= 10 && array[i][r] > 0 && array[i][r] !== null) {
                    table += '<td class="a-border-of-table">' + array[i][r] + '</td>';
                } else if (array[i][r] === 20) {
                    table += '<td class="a-border-of-table">' + '</td>';
                } else if (array[i][r] === 13) {
                    if (r === 0 || i === 0) {
                        table += '<td class="a-border-of-table">' + '</td>';
                    } else {
                        table += '<td class="missed-shot">' + '</td>';
                    }
                } else if (array[i][r] === 14) {
                    table += '<td class="a-player-ship">' + array[i][r] + '</td>';
                } else {
                    table += '<td>' + '</td>';
                }


            }
            table += '</tr>';

        }
        $(selectorJq).html('<table border="2"  width="200px" cellspacing="0" cellpadding="15">' + table + '</table>');
    }

    function DrawEnemyArrayToTableInGame(array, selectorJq) {

        var table = '';
        for (var r = 0; r < 11; r++) {
            table += '<tr>';

            for (var i = 0; i < 11; i++) {
                if (array[i][r] === 12) {
                    table += '<td class="checked-cell">' + array[i][r] + '</td>';
                } else if (array[i][r] <= 10 && array[i][r] > 0 && array[i][r] !== null) {
                    table += '<td class="a-border-of-table">' + array[i][r] + '</td>';
                } else if (array[i][r] === 20) {
                    table += '<td class="a-border-of-table">' + '</td>';
                } else if (array[i][r] === 14) {
                    table += '<td class="got-the-shot">' + '</td>';
                } else if (array[i][r] === 13) {
                    table += '<td class="missed-shot">' + '</td>';
                } else {
                    table += '<td>' + '</td>';
                }

            }
            table += '</tr>';

        }
        $(selectorJq).html('<table border="2"  width="200px" cellspacing="0" cellpadding="15">' + table + '</table>');

    }

</script>

</body>


</html>