package com.example.kopidannyemil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Cream = Color(0xFFFFF8F0)
private val Brown = Color(0xFF6F432B)
private val Orange = Color(0xFFF4B978)
private val Ink = Color(0xFF29231F)

data class MenuItem(val name: String, val detail: String, val price: Int, val emoji: String)
private val menu = listOf(
    MenuItem("Kopi Susu Gula Aren", "Espresso, susu, gula aren", 22000, "☕"),
    MenuItem("Donat Cokelat", "Donat empuk, glaze cokelat", 12000, "🍩"),
    MenuItem("Sourdough Loaf", "Roti fermentasi alami", 35000, "🍞"),
    MenuItem("Cappuccino", "Espresso, foam susu lembut", 25000, "🥛"),
    MenuItem("Matcha Latte", "Matcha Jepang, susu segar", 27000, "🍵")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KopiApp() }
    }
}

@Composable
fun KopiApp() {
    var page by remember { mutableStateOf("Splash") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(menu.first()) }
    var cart by remember { mutableStateOf(listOf<MenuItem>()) }
    var message by remember { mutableStateOf("") }

    MaterialTheme(colorScheme = lightColorScheme(primary = Brown, secondary = Orange, background = Cream)) {
        Surface(color = Cream, modifier = Modifier.fillMaxSize()) {
            when (page) {
                "Splash" -> Splash { page = "Welcome" }
                "Welcome" -> Welcome(
                    onRegister = { page = "Register" },
                    onLogin = { page = "Login" }
                )
                "Login" -> AuthScreen("Login User", listOf("username", "password"), username, password, name,
                    onUser = { username = it }, onPass = { password = it }, onName = { name = it },
                    button = "Login", onSubmit = { page = "Home"; message = "Selamat datang, ${name.ifBlank { username }}!" },
                    secondary = "Belum punya akun? Register", onSecondary = { page = "Register" })
                "Register" -> AuthScreen("Register", listOf("nama lengkap", "username", "password"), username, password, name,
                    onUser = { username = it }, onPass = { password = it }, onName = { name = it },
                    button = "Register", onSubmit = { page = "Home"; message = "Akun berhasil dibuat. Halo, $name!" },
                    secondary = "Sudah punya akun? Login", onSecondary = { page = "Login" })
                "Home" -> HomeScreen(name.ifBlank { username.ifBlank { "Teman Kopi" } }, cart.size, onAdd = {
                    cart = cart + it; selected = it; message = "${it.name} ditambahkan ke pesanan"
                }, onProfile = { page = "Profile" }, onOrder = { page = "Address" }, message = message)
                "Profile" -> ProfileScreen(name, username, onName = { name = it }, onSave = { message = "Profil berhasil disimpan"; page = "Home" }, onHome = { page = "Home" }, onOrder = { page = "Address" })
                "Address" -> AddressScreen(name, address, detail, onAddress = { address = it }, onDetail = { detail = it }, onSubmit = { page = "Confirm" }, onBack = { page = "Home" }, count = cart.size)
                "Confirm" -> ConfirmScreen(name.ifBlank { username }, cart.sumOf { it.price }, onDone = { cart = emptyList(); page = "Home"; message = "Pesanan berhasil dikirim!" })
            }
        }
    }
}

@Composable
fun Page(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp), content = content)
}
@Composable fun Heading(text: String) { Text(text, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Ink) }
@Composable fun Primary(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Ink)) { Text(text, color = Color.White, fontWeight = FontWeight.SemiBold) }
}
@Composable fun Field(label: String, value: String, onChange: (String) -> Unit, secret: Boolean = false) {
    OutlinedTextField(value, onChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), singleLine = !secret, visualTransformation = if (secret) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None, shape = RoundedCornerShape(12.dp))
}
@Composable fun Splash(next: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("☕", fontSize = 78.sp)
        Spacer(Modifier.height(16.dp)); Heading("Kopi dan Nyemil")
        Text("Get a taste of your wish", color = Brown)
        Spacer(Modifier.height(40.dp)); Primary("Start now", next)
    }
}
@Composable fun Welcome(onRegister: () -> Unit, onLogin: () -> Unit) {
    Page {
        Spacer(Modifier.height(55.dp)); Text("☕", fontSize = 65.sp); Spacer(Modifier.height(20.dp))
        Heading("Sign up"); Text("Join as a member & experience more fun from various meal at Kopi dan Nyemil.", color = Brown)
        Spacer(Modifier.height(35.dp)); Primary("Register", onRegister); Spacer(Modifier.height(12.dp))
        OutlinedButton(onLogin, Modifier.fillMaxWidth().height(52.dp)) { Text("Login") }
    }
}
@Composable fun AuthScreen(title: String, fields: List<String>, user: String, pass: String, name: String,
                          onUser: (String)->Unit, onPass: (String)->Unit, onName: (String)->Unit,
                          button: String, onSubmit: ()->Unit, secondary: String, onSecondary: ()->Unit) {
    Page {
        Spacer(Modifier.height(32.dp)); Heading(title); Spacer(Modifier.height(24.dp))
        fields.forEach { f ->
            when(f) {
                "nama lengkap" -> Field("Nama lengkap", name, onName)
                "username" -> Field("Username", user, onUser)
                else -> Field("Password", pass, onPass, true)
            }; Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(20.dp)); Primary(button, onSubmit); TextButton(onSecondary) { Text(secondary) }
    }
}
@Composable fun HomeScreen(name: String, cartCount: Int, onAdd: (MenuItem)->Unit, onProfile: ()->Unit, onOrder: ()->Unit, message: String) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Halo, $name", fontSize = 23.sp, fontWeight = FontWeight.Bold); Text("Mau ngemil apa hari ini?", color = Brown) }
            IconButton(onClick = onProfile) { Icon(Icons.Default.Person, "Profile") }
        }
        if (message.isNotBlank()) Text(message, Modifier.padding(horizontal = 20.dp), color = Brown)
        LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp)) {
            item { Text("Menu favorit", fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp)) }
            items(menu) { item ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(item.emoji, fontSize = 34.sp, modifier = Modifier.padding(end = 12.dp))
                        Column(Modifier.weight(1f)) { Text(item.name, fontWeight = FontWeight.Bold); Text(item.detail, fontSize = 12.sp, color = Brown); Text("Rp ${"%,d".format(item.price)}", fontWeight = FontWeight.SemiBold) }
                        Button(onClick = { onAdd(item) }, colors = ButtonDefaults.buttonColors(containerColor = Brown)) { Text("+", color = Color.White) }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().background(Color.White).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onProfile, Modifier.weight(1f)) { Icon(Icons.Default.Person, null); Text("Profile") }
            Button(onClick = onOrder, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Ink)) { Icon(Icons.Default.ShoppingBag, null); Text("Order ($cartCount)") }
        }
    }
}
@Composable fun ProfileScreen(name: String, username: String, onName: (String)->Unit, onSave: ()->Unit, onHome: ()->Unit, onOrder: ()->Unit) {
    Page {
        Text("Profile Saya", color = Brown); Spacer(Modifier.height(8.dp)); Heading("Halo, ${name.ifBlank { username.ifBlank { "Nama" } }}")
        Spacer(Modifier.height(24.dp)); Text("Profile Setting", fontWeight = FontWeight.Bold); Spacer(Modifier.height(10.dp))
        Field("Nama", name, onName); Spacer(Modifier.height(24.dp)); Primary("Save", onSave)
        Spacer(Modifier.height(18.dp)); Row { TextButton(onHome) { Text("Home") }; TextButton(onOrder) { Text("Order") } }
    }
}
@Composable fun AddressScreen(name: String, address: String, detail: String, onAddress: (String)->Unit, onDetail: (String)->Unit, onSubmit: ()->Unit, onBack: ()->Unit, count: Int) {
    Page {
        TextButton(onBack) { Text("← Kembali") }; Heading("Alamat Pengiriman"); Text("Pesanan: $count item", color = Brown)
        Spacer(Modifier.height(20.dp)); Field("Nama Lengkap", name, {})
        Spacer(Modifier.height(12.dp)); Field("Alamat", address, onAddress)
        Spacer(Modifier.height(12.dp)); OutlinedTextField(detail, onDetail, label = { Text("Patokan atau detail lengkap") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.height(25.dp)); Primary("Order dan Kirim", onSubmit)
    }
}
@Composable fun ConfirmScreen(name: String, total: Int, onDone: ()->Unit) {
    Page {
        Spacer(Modifier.height(65.dp)); Heading("Halo, $name"); Spacer(Modifier.height(28.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text("✓", fontSize = 64.sp, color = Color(0xFF2EAD71)) }
        Spacer(Modifier.height(20.dp)); Text("Terima kasih sudah memesan. Tunggu ya, pesanan kamu segera kami antarkan!", color = Brown)
        Spacer(Modifier.height(15.dp)); Text("Total: Rp ${"%,d".format(total)}", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(40.dp)); Primary("Oke", onDone)
    }
}
