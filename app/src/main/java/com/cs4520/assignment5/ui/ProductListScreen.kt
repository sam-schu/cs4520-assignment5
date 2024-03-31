import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs4520.assignment5.R
import com.cs4520.assignment5.logic.CategorizedProduct
import com.cs4520.assignment5.logic.DisplayProducts
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
        when (val displayProducts = vm.displayProducts.value) {
            is DisplayProducts.ProductsNotLoaded -> {
                LinearProgressIndicator()
            }
            is DisplayProducts.ProductList -> {
                LazyColumn {
                    items(displayProducts.products) {product ->
                        Product(product)
                    }
                }
            }
            is DisplayProducts.LoadUnsuccessful -> {
                val errorText = stringResource(when (displayProducts.reason) {
                    is DisplayProducts.LoadUnsuccessful.Reason.ServerError ->
                        R.string.products_error_text
                    is DisplayProducts.LoadUnsuccessful.Reason.ServerNoProducts ->
                        R.string.server_no_products_text
                    is DisplayProducts.LoadUnsuccessful.Reason.OfflineNoProducts ->
                        R.string.offline_no_products_text
                })
                Text(
                    text = errorText,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun Product(product: CategorizedProduct) {
    val backgroundColor: Int
    val productImage: Int
    when (product) {
        is CategorizedProduct.Equipment -> {
            backgroundColor = R.color.product_equipment_background
            productImage = R.drawable.equipment
        }
        is CategorizedProduct.Food -> {
            backgroundColor = R.color.product_food_background
            productImage = R.drawable.food
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(colorResource(backgroundColor))
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Image(
            painter = painterResource(productImage),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(R.string.product_type_img_description),
            modifier = Modifier
                .size(50.dp)
        )
        Spacer(
            modifier = Modifier
                .width(15.dp)
        )
        Column {
            Text(
                text = product.name
            )
            product.expiryDate?.let {
                Text(
                    text = it
                )
            }
            Text(
                text = stringResource(R.string.product_price_text, product.price)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductListScreenPreview() {
    ProductListScreen()

    viewModel<ProductsViewModel>().loadProductData()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductPreview() {
    Product(CategorizedProduct.Equipment("Product", "2024-01-01", 10.0))
}
