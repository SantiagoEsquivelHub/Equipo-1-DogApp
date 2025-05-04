package com.example.clase8.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clase8.model.Inventory
import com.example.clase8.model.Product
import com.example.clase8.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var inventoryViewModel: InventoryViewModel

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var inventoryRepository: InventoryRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        inventoryRepository = Mockito.mock(InventoryRepository::class.java)
        application = Mockito.mock(Application::class.java)
        inventoryViewModel = InventoryViewModel(application, inventoryRepository)
    }

    @Test
    fun `test método totalProducto`() {
        val price = 10
        val quantity = 5
        val expectedResult = (price * quantity).toDouble()

        val result = inventoryViewModel.totalProducto(price, quantity)

        assertEquals(expectedResult, result, 0.0)
    }

    @Test
    fun `test método getProducts`() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        val mockProducts = mutableListOf(
            Product(0, "zapatos", "hola como estas")
        )
        Mockito.`when`(inventoryRepository.getProducts()).thenReturn(mockProducts)

        inventoryViewModel.getProducts()

        assertEquals(mockProducts, inventoryViewModel.listProducts.value)
        Dispatchers.resetMain()
    }

    @Test
    fun testSaveInventory_success() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        val inventory = Inventory(id = 1, name = "Item1", price = 10, quantity = 5)

        Mockito.`when`(inventoryRepository.saveInventory(inventory))
            .thenAnswer { invocation ->
                invocation.getArgument<Inventory>(0)
            }

        inventoryViewModel.saveInventory(inventory)

        Mockito.verify(inventoryRepository).saveInventory(inventory)
        Dispatchers.resetMain()
    }
}
