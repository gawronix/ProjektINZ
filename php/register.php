<?php

session_start();

if( isset($_SESSION['user_id']) ){
	header("Location: /public_html/index.html");
}

require '/php/database.php';

$message = '';

if(!empty($_POST['email']) && !empty($_POST['password'])):
	
	// Enter the new user in the database
	$sql = "INSERT INTO users (email, password) VALUES (:email, :password)";
	$stmt = $conn->prepare($sql);

	$stmt->bindParam(':email', $_POST['email']);
	$stmt->bindParam(':password', password_hash($_POST['password'], PASSWORD_BCRYPT));

	if( $stmt->execute() ):
		$message = 'Utworzono nowego użytkownika';
	else:
		$message = 'Wystąpił problem przy zakładaniu konta';
	endif;

endif;

?>

<!DOCTYPE html>
<html>
<head>
	<title>Zarejestruj się</title>
	<link rel="stylesheet" type="text/css" href="assets/css/style.css">
	<link href='http://fonts.googleapis.com/css?family=Comfortaa' rel='stylesheet' type='text/css'>
</head>
<body>

	<div class="header">
		<a href="/">index</a>
	</div>

	<?php if(!empty($message)): ?>
		<p><?= $message ?></p>
	<?php endif; ?>

	<h1>Rejestracja</h1>
	<span>or <a href="/php/login.php">zaloguj się</a></span>

	<form action="/php/register.php" method="POST">
		
		<input type="text" placeholder="Enter your email" name="email">
		<input type="password" placeholder="and password" name="password">
		<input type="password" placeholder="confirm password" name="confirm_password">
		<input type="submit" value="Register">

	</form>

</body>
</html>
