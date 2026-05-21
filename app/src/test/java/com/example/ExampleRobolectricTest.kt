package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.StoreViewModel
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun testViewModelInitialization() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoreViewModel(application)
    assertNotNull(viewModel)
    assertNotNull(viewModel.favoritesList.value)
  }
}

