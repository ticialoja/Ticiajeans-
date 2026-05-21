package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.FavProduct
import com.example.model.Category
import com.example.model.Product
import com.example.ui.StoreUiState
import com.example.ui.StoreViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val favoritesList by viewModel.favoritesList.collectAsState()

    // Mode: "catalog" or "favorites"
    var viewMode by remember { mutableStateOf("catalog") }
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Flat, ultra-luxury Natural Tones Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu decorative icon
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF2EBE4))
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Title
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TICIA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "ORGANIC & JEANS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF8C7A6B),
                        letterSpacing = 1.sp
                    )
                }

                // Mode toggle switch
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF2EBE4))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewMode = "catalog" },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (viewMode == "catalog") MaterialTheme.colorScheme.primary else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Vitrine",
                            tint = if (viewMode == "catalog") Color.White else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewMode = "favorites" },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (viewMode == "favorites") MaterialTheme.colorScheme.primary else Color.Transparent)
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favoritos",
                                tint = if (viewMode == "favorites") Color.White else Color(0xFFA67C52),
                                modifier = Modifier.size(16.dp)
                            )
                            if (favoritesList.isNotEmpty() && viewMode != "favorites") {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color.Red)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input Block
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchProduct(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar modelos orgânicos ou jeans...", color = Color(0xFF8C7A6B)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchProduct("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpar",
                                tint = Color(0xFF8C7A6B)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp), // High aesthetic rounding as requested
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFEEEAE1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // Autumn Theme Banner (Direct Integration from Design HTML)
            AnimatedVisibility(
                visible = viewMode == "catalog" && searchQuery.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2EBE4)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Novidades de Outono",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8C7A6B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Coleção Orgânica",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3E38)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFA67C52))
                                .clickable {
                                    viewModel.searchProduct("")
                                    viewModel.loadData()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Explorar",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (viewMode == "catalog") {
                // Category Tag Rows
                if (categories.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory?.name == category.name
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(category) },
                                label = { Text(category.name, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF2EBE4),
                                    labelColor = MaterialTheme.colorScheme.primary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.Transparent,
                                    selectedBorderColor = Color.Transparent,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }

                // Inner list screen state control
                when (val state = uiState) {
                    is StoreUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Carregando catálogo da loja...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    is StoreUiState.Success -> {
                        val filteredProducts = state.products.filter {
                            it.title.lowercase().contains(searchQuery.lowercase()) ||
                            it.description.lowercase().contains(searchQuery.lowercase())
                        }

                        if (filteredProducts.isEmpty()) {
                            EmptyStateView(
                                title = "Nenhum produto encontrado",
                                subtitle = "Tente buscar por " + if (searchQuery.isNotEmpty()) "\"${searchQuery}\"" else "outros termos" + " ou limpe os filtros.",
                                onActionClick = {
                                    viewModel.searchProduct("")
                                    viewModel.loadData()
                                },
                                actionText = "Ver todos"
                            )
                        } else {
                            ProductGrid(
                                products = filteredProducts,
                                favorites = favoritesList,
                                onProductClick = { selectedProductForDetail = it },
                                onFavoriteToggle = { viewModel.toggleFavorite(it) },
                                onVisitStore = { openWebPage(context, it.cleanProductUrl) }
                            )
                        }
                    }
                    is StoreUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Aviso",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = state.message,
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { viewModel.loadData() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Tentar novamente")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tentar Novamente")
                                }
                            }
                        }
                    }
                }
            } else {
                // Favorites/Saved view mode
                val favsMapped = favoritesList.map {
                    Product(
                        id = it.id,
                        title = it.title,
                        url = it.url,
                        imageUrl = it.imageUrl,
                        price = it.price,
                        isSale = it.isSale,
                        category = it.category,
                        description = it.description
                    )
                }.filter {
                    it.title.lowercase().contains(searchQuery.lowercase()) ||
                    it.description.lowercase().contains(searchQuery.lowercase())
                }

                if (favsMapped.isEmpty()) {
                    EmptyStateView(
                        title = "Nenhum favorito salvo",
                        subtitle = "Seus jeans e camisas favoritos de botão salvos offline e de acesso rápido aparecerão aqui. Clique no ícone de coração nos produtos para salvar!",
                        onActionClick = { viewMode = "catalog" },
                        actionText = "Ir para a loja"
                    )
                } else {
                    ProductGrid(
                        products = favsMapped,
                        favorites = favoritesList,
                        onProductClick = { selectedProductForDetail = it },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) },
                        onVisitStore = { openWebPage(context, it.cleanProductUrl) }
                    )
                }
            }
        }
    }

    // Detail dialog popup overlay
    selectedProductForDetail?.let { product ->
        val isFavState = favoritesList.any { it.id == product.id }
        ProductDetailDialog(
            product = product,
            isFavorite = isFavState,
            onDismiss = { selectedProductForDetail = null },
            onFavoriteToggle = { viewModel.toggleFavorite(product) },
            onVisitStore = { openWebPage(context, product.cleanProductUrl) }
        )
    }
}

@Composable
fun ProductGrid(
    products: List<Product>,
    favorites: List<FavProduct>,
    onProductClick: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    onVisitStore: (Product) -> Unit
) {
    val uniqueProducts = remember(products) { products.distinctBy { it.id } }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        items(uniqueProducts, key = { it.id }) { product ->
            val isFavorite = favorites.any { it.id == product.id }
            ProductItemCard(
                product = product,
                isFavorite = isFavorite,
                onProductClick = { onProductClick(product) },
                onFavoriteClick = { onFavoriteToggle(product) },
                onBuyClick = { onVisitStore(product) }
            )
        }
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    isFavorite: Boolean,
    onProductClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    // Elegant organic card following high rounding guidelines
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick() },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEAE1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // High-contrast clean card image
            AsyncImage(
                model = product.cleanImageUrl,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)),
                contentScale = ContentScale.Crop
            )

            // Overlapping Floating Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sale promotion badge
                if (product.isSale) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFA67C52))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "PREÇO DE FÁBRICA",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Heart Favorites Button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = if (isFavorite) Color.Red else Color(0xFF6B4F3B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Information fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Category info tag
            Text(
                text = product.category.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8C7A6B),
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Title
            Text(
                text = product.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF4A3E38),
                modifier = Modifier.height(36.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pricing Area
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.price,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA67C52)
                )
                
                if (product.originalPrice != null) {
                    Text(
                        text = product.originalPrice,
                        fontSize = 11.sp,
                        color = Color(0xFF8C7A6B),
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visit button
            Button(
                onClick = onBuyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ver no Site", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ProductDetailDialog(
    product: Product,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onVisitStore: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFEEEAE1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Heart and Close elements
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorite) Color.Red else Color(0xFF6B4F3B)
                        )
                    }
                    Text(
                        text = "Detalhes do Produto",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color(0xFF8C7A6B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Primary Image container with double rounded edges
                AsyncImage(
                    model = product.cleanImageUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Title info
                Text(
                    text = product.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A3E38)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category and dynamic IDs tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF2EBE4))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = product.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "CÓD: ${product.id}",
                        fontSize = 10.sp,
                        color = Color(0xFF8C7A6B)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Elegantly structured under-title description block
                if (product.description.isNotEmpty()) {
                    Text(
                        text = product.description,
                        fontSize = 12.sp,
                        color = Color(0xFF6B4F3B),
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                HorizontalDivider(color = Color(0xFFEEEAE1))

                Spacer(modifier = Modifier.height(16.dp))

                // Price display & organic manufacturer label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Valor:",
                            fontSize = 11.sp,
                            color = Color(0xFF8C7A6B)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = product.price,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA67C52)
                            )
                            if (product.originalPrice != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = product.originalPrice,
                                    fontSize = 13.sp,
                                    color = Color(0xFF8C7A6B),
                                    textDecoration = TextDecoration.LineThrough,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                        }
                    }

                    // Eco tag details
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Jeans Premium",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Moda Sustentável",
                            fontSize = 10.sp,
                            color = Color(0xFF8C7A6B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CTA button action
                Button(
                    onClick = {
                        onVisitStore()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comprar no site oficial", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    onActionClick: () -> Unit,
    actionText: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Vazio",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                color = Color(0xFF8C7A6B),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = onActionClick,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(actionText)
            }
        }
    }
}

fun openWebPage(context: Context, url: String) {
    if (url.isBlank()) {
        Log.e("HomeScreen", "URL matches blank state, skipping openWebPage call")
        return
    }
    val safeUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
        if (url.startsWith("//")) "https:$url" else "https://$url"
    } else {
        url
    }
    try {
        val webpage: Uri = Uri.parse(safeUrl)
        val intent = Intent(Intent.ACTION_VIEW, webpage).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Log.e("HomeScreen", "Could not load web intent for URL: $safeUrl", e)
    }
}

