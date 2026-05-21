package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.Product
import com.example.ui.screens.ProductItemCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val mockProduct = Product(
        id = "130953552",
        title = "Camisa de Botão Unissex Personagens Mickey Black",
        url = "https://ticia.lojavirtualnuvem.com.br/",
        imageUrl = "",
        price = "R$ 49,90",
        isSale = true,
        category = "Camisas"
    )

    composeTestRule.setContent { 
      MyApplicationTheme { 
        ProductItemCard(
            product = mockProduct,
            isFavorite = true,
            onProductClick = {},
            onFavoriteClick = {},
            onBuyClick = {}
        )
      } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
