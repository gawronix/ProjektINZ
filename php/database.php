<?php
$server = '145.239.90.206';
$port = '3306';
$username = 'youstats';
$password = 'wmiuam';
$database = 'youstats';

try{
	$conn = new PDO("mysql:host=$server;$port;dbname=$database;", $username, $password);
} catch(PDOException $e){
	die( "Connection failed: " . $e->getMessage());
}
