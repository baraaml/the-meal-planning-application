package com.example.mealflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.ui.theme.MealFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MealFlowTheme {
                MyApp()
            }
        }
    }
}

//----------------------------------------------------------------------------
// Function to move between pages
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Home Page") {
        composable("Home Page") {
            HomePage(navController)
        }
        composable("Register") {
            RegisterPage(navController)
        }
        composable("Log in") {
            LoginPage(navController)
        }
    }
}

//-----------------------------------
// Home Page
@Composable
fun HomePage(navController: NavController) {
    Image(
        painter = painterResource(id = R.drawable.background_main_page),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
    Box(
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
            {

            }
            Image(
                painter = painterResource(id = R.drawable.mealflow),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//                Text(
//                    text = "Welcome,\n" +
//                            "Meal Flow",
//                    fontSize = 40.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .padding(bottom = 70.dp)
//                )
            DynamicButton(
                onClick = {
                    navController.navigate("Log in")
                },
                textOnButton = "Log in",
                buttonWidthDynamic = 232,
                modifier = Modifier
                    .padding(top = 35.dp)
            )
            DynamicButton(
                onClick = {
                    navController.navigate("Register")
                },
                textOnButton = "Register",
                buttonWidthDynamic = 232,
                modifier = Modifier
                    .padding(top = 50.dp)
            )
            Spacer(modifier = Modifier.padding(bottom = 70.dp))

        }
    }
}

//-----------------------------------
// Register Page
@Composable
fun RegisterPage(navController: NavController)
{
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 50.dp))
    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        {

        }
        Text(
            text = "Join our community",
            Modifier.padding(start = 20.dp, top = 50.dp ,end = 20.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
            )
        RegisterInput()
        Button(
            onClick = { /*TODO*/ } ,
            Modifier
                .padding(20.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Sign up",
                color = Color.White // لون النص الأسود
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "You already have an account? ")
            Text(text = "Login",
                Modifier
                    .clickable(onClick = {/*TODO*/}
                    ),
                color = Color.Blue
            )
        }

        OrDivider("OR")

        Button(
            onClick = { /*TODO*/ } ,
            Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)

        ) {
            Icon(painter = painterResource(
                id = R.drawable.google_icon_icons_com_62736),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 10.dp))
            Text(
                text = "Sign up using Google",
                color = Color.White // لون النص الأسود
            )
        }

        }
    }
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewRegisterPage()
{
    RegisterPage(navController = rememberNavController())
}
//---------------------------------------------------------------

@Composable
fun OrDivider(textDivider:String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color.Black
        )
        Text(
            text = textDivider,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = Color.Black.copy(alpha = 0.7f)
        )
        Divider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color.Black
        )
    }

}
// Function to put text between line
@Preview(showBackground = true)
@Composable
fun PreviewOrDivider() {
    OrDivider("Test")
}
//--------------------------------------------------
//--------------------------------------------------
//-------------------------------------------------
//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPage(navController: NavController) {
    var text by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Box {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
            {

            }
            Row {
                BackButton(
                    onClick = {
                        navController.navigate("Home Page")
                    },
                    Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 100.dp))
            }
            Text(
                text = stringResource(id = R.string.REmail),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(start = 20.dp, top = 40.dp)
            )

            Column {
                // Input field
                OutlinedTextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    label = { Text("Enter Email") },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black), // تحديد لون النص
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                )

                // حقل كلمة السر
                OutlinedTextField(
                    value = password,
                    onValueChange = { newPassword -> password = newPassword },
                    label = { Text("Enter your password") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) {
                                    R.drawable.eye_view_icon
                                } else {
                                    R.drawable.eye_closed_icon
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { passwordVisible = !passwordVisible }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black), // تحديد لون النص
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(
                        id = R.string.ForgotPassword
                    ),
                    Modifier
                        .clickable(onClick = {/*TODO*/ }
                        ),
                    color = Color(0xFF009951)
                )
            }
            FixedButton(
                onClick = { /*TODO*/ },
                textOnButton = "Log in"
            )
            OrDivider("Or log in with ")

            RowLoginIcons()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(
                        id = R.string.NotAccount
                    )
                )
                Text(
                    text = stringResource(
                        id = R.string.Register
                    ),
                    Modifier
                        .clickable(onClick = {/*TODO*/ })
                        .padding(start = 5.dp),
                    color = Color(0xFF009951)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoginPage()
{
    LoginPage(navController = rememberNavController())
}
//-----------------------------------------
//-----------------------------------------
//Button
    @Composable
    fun BackButton(onClick: () -> Unit, modifier: Modifier) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .padding(20.dp)
                .size(56.dp)  // تحديد الحجم الدائري
                .background(Color(0xFF009951), CircleShape) // اللون الأخضر والشكل الدائري
                .border(1.dp, Color.Black, CircleShape)// حدود سوداء
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "رجوع",
                tint = Color.White
            )
        }
    }

    @Composable
    fun FixedButton(onClick: () -> Unit, textOnButton: String, modifier: Modifier = Modifier) {
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(50.dp))// حدود سوداء
            , shape = RoundedCornerShape(50.dp), // تقليل انحناء الحواف هنا أيضًا
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009951))
        ) {
            Text(
                text = textOnButton,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White // لون النص الأسود
            )
        }
    }

    @Composable
    fun DynamicButton(
        onClick: () -> Unit, textOnButton: String,
        modifier: Modifier = Modifier,
        buttonWidthDynamic: Int
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .width(buttonWidthDynamic.dp)
                .padding(20.dp)
                .height(50.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(50.dp))// حدود سوداء
            , shape = RoundedCornerShape(50.dp), // تقليل انحناء الحواف هنا أيضًا
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009951))
        ) {
            Text(
                text = textOnButton,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White // لون النص الأسود
            )
        }
    }

//-------------------------------------------------------
//-------------------------------------------------------
//----------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewButton() {
    Column {
        DynamicButton(onClick = { /*TODO*/ }, textOnButton = "Sign in", buttonWidthDynamic = 200)
        FixedButton(onClick = { /*TODO*/ }, "Log in")
    }
}

//-------------------------------------------------------
//-------------------------------------------------------

@Composable
fun RegisterInput()
{
    var text by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    // Input field
    OutlinedTextField(
        value = text,
        onValueChange = { newText -> text = newText },
        label = { Text("Add an Email") },
        singleLine = true,
        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
    )
    // حقل كلمة السر
    OutlinedTextField(
        value = password,
        onValueChange = { newPassword -> password = newPassword },
        label = { Text("Enter your password") },
        trailingIcon = {
            Icon(
                painter = painterResource(
                    id = if (passwordVisible) {
                        R.drawable.eye_view_icon
                    } else {
                        R.drawable.eye_closed_icon
                    }
                ),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { passwordVisible = !passwordVisible }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
    )
    // حقل كلمة السر
    OutlinedTextField(
        value = repassword,
        onValueChange = { newPassword -> repassword = newPassword },
        label = { Text("Re-Enter your password") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
    )

}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegisterInput()
{
    Column {
        RegisterInput()
    }
}

//--------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RowLoginIcons() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        IconButton(
            onClick = {/*TODO*/ },
            modifier = Modifier
                .padding(10.dp)
                .size(44.dp)  // تحديد الحجم الدائري
                .border(1.dp, Color.Black, CircleShape)// حدود سوداء
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google_icon_icons_com_62736),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }
        IconButton(
            onClick = {/*TODO*/ },
            modifier = Modifier
                .padding(10.dp)
                .size(44.dp)  // تحديد الحجم الدائري
                .border(1.dp, Color.Black, CircleShape)// حدود سوداء
        ) {
            Icon(
                painter = painterResource(id = R.drawable.facebook_logo_icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }
        IconButton(
            onClick = {/*TODO*/ },
            modifier = Modifier
                .padding(10.dp)
                .size(44.dp)  // تحديد الحجم الدائري
                .border(1.dp, Color.Black, CircleShape)// حدود سوداء
        ) {
            Icon(
                painter = painterResource(id = R.drawable.apple_logo_icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewRowLoginIcons() {
    RowLoginIcons()
}
//-------------------------------------------------------
//-------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}
