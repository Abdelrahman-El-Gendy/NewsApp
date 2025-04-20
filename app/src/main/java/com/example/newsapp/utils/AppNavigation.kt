import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.Presentation.screen.FavoritesScreen
import com.example.newsapp.Presentation.screen.RegisterScreen
import com.example.newsapp.Presentation.screen.MainScreen
import com.example.newsapp.utils.WebViewScreen

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("main") {
            MainScreen(navController)
        }
        composable("webview?url={url}") { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            WebViewScreen(url = url)
        }
        composable("favorites") {
            FavoritesScreen(navController)
        }


    }
}
