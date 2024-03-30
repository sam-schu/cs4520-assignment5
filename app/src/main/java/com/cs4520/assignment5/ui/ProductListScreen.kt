import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs4520.assignment5.logic.ProductsViewModel

@Composable
fun ProductListScreen(
    vm: ProductsViewModel = viewModel()
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LinearProgressIndicator()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductListScreenPreview() {
    ProductListScreen()
}