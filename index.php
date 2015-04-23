<?php
/**
 * Created by PhpStorm.
 * User: K. Sauter
 * Date: 23.04.2015
 * Time: 08:56
 */

//Base Farben:
//INSERT INTO `lightControl`.`rgb_farben` (`id`, `name`, `red`, `green`, `blue`) VALUES (NULL, 'Rot', '255', '0', '0');
//INSERT INTO `lightControl`.`rgb_farben` (`id`, `name`, `red`, `green`, `blue`) VALUES (NULL, 'Grün', '0', '255', '0');
//INSERT INTO `lightControl`.`rgb_farben` (`id`, `name`, `red`, `green`, `blue`) VALUES (NULL, 'Blau', '0', '0', '255');

ini_set("display_errors", 1);
error_reporting(0);

mysql_connect("kevin-sauter.de", "lightControl", "toor01");
mysql_select_db("lightControl");

if(isset($_POST['farbe'])){
    mysql_query("INSERT INTO `lightControl`.`rgb_control` (`id`, `requestName`, `status`) VALUES (NULL, '" . $_POST['auswahl'] . "', '0');");
    echo "Gespeichert!";
    die();
}

function textfarbe($r, $g, $b){
    $y = (299 * $r + 587 * $g + 114 * $b) / 1000;
    return $y >= 128 ? "black" : "white";
}

echo'<form method="post"><p><select name="auswahl" size="1">';

$res = mysql_query("SELECT * FROM rgb_farben");
while($row = mysql_fetch_array($res)) {
    echo "<option style=\"color: " . textfarbe($row['red'],  $row['green'],  $row['blue']) . ";";
    echo "background-color: rgb(" . $row['red'] . ", " . $row['green'] . ", " . $row['blue'] . ") \">" . $row['name'] . "</option>";
}

echo '</select></p><input type="submit" name="farbe" value="absenden"></form>';

mysql_close();


