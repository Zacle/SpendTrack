package com.zacle.spendtrack.core.designsystem.component

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.CategoryKeyResource
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.util.convertInstantToLocalDateTime
import com.zacle.spendtrack.core.model.util.formatLocalDateTime
import com.zacle.spendtrack.core.shared_resources.R
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import android.graphics.Color as AndroidColor

@Composable
fun RecordTransaction(
    name: String,
    description: String,
    categories: List<Category>,
    selectedCategoryId: String,
    onAmountChanged: (Int) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAttachmentSelected: (ImageData?) -> Unit,
    onDateSelected: (Instant) -> Unit,
    onTransactionSaved: () -> Unit,
    modifier: Modifier = Modifier,
    amount: Int = 0,
    transactionDate: Instant? = null,
    receiptUriImage: ImageData? = null,
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    isUploading: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.3f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            TransactionEntryAmount(
                onAmountChanged = onAmountChanged,
                amount = amount,
                contentColor = contentColor,
                title = stringResource(id = R.string.how_much)
            )
        }
        TransactionEntry(
            name = name,
            description = description,
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = onCategorySelected,
            onNameChanged = onNameChanged,
            onDescriptionChanged = onDescriptionChanged,
            onAttachmentSelected = onAttachmentSelected,
            onDateSelected = onDateSelected,
            onTransactionSaved = onTransactionSaved,
            transactionDate = transactionDate,
            receiptUriImage = receiptUriImage,
            isUploading = isUploading,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun TransactionEntryAmount(
    title: String,
    onAmountChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    amount: Int = 0,
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(start = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            Text(
                text = "$",
                style = MaterialTheme.typography.displayMedium,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
            TextField(
                value = if (amount > 0) "$amount" else "",
                onValueChange = { entry -> onAmountChanged(entry.toIntOrNull() ?: 0) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = contentColor,
                    unfocusedTextColor = contentColor
                ),
                textStyle = TextStyle(
                    color = contentColor,
                    fontStyle = MaterialTheme.typography.displayMedium.fontStyle,
                    fontSize = MaterialTheme.typography.displayMedium.fontSize,
                    fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                placeholder = {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.displayMedium,
                        color = contentColor.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }
}

@Composable
fun TransactionEntry(
    name: String,
    description: String,
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (Category) -> Unit,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAttachmentSelected: (ImageData?) -> Unit,
    onDateSelected: (Instant) -> Unit,
    onTransactionSaved: () -> Unit,
    modifier: Modifier = Modifier,
    transactionDate: Instant? = null,
    receiptUriImage: ImageData? = null,
    isUploading: Boolean = false
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryDropdown(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected
            )
            STTextField(
                name = name,
                onValueChange = onNameChanged,
                placeholder = stringResource(id = R.string.name),
                singleLine = true
            )
            STTextField(
                name = description,
                onValueChange = onDescriptionChanged,
                placeholder = stringResource(id = R.string.description)
            )
            Attachment(
                onAttachmentSelected = onAttachmentSelected,
                receiptUriImage = receiptUriImage
            )
            TransactionDate(
                onDateSelected = onDateSelected,
                transactionDate = transactionDate
            )
            Spacer(modifier = Modifier.weight(1f))
            SpendTrackButton(
                text = stringResource(id = R.string.save),
                onClick = onTransactionSaved,
                isUploading = isUploading,
                enabled = !isUploading,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val category = categories.find { it.categoryId == selectedCategoryId }

    val context = LocalContext.current

    Box {
        STOutline(
            modifier = modifier
                .clickable { expanded = !expanded }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedCategoryId.isNotEmpty() && category != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .align(Alignment.CenterStart)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(12.dp)
                                .background(Color(AndroidColor.parseColor(category.color)))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = CategoryKeyResource.getStringResourceForCategory(
                                context = context,
                                categoryKey = category.key
                            ),
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.categories),
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .align(Alignment.CenterStart)
                    )
                }
                Icon(
                    imageVector = SpendTrackIcons.dropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            scrollState = scrollState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 5.dp,
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 5.dp
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(12.dp)
                                    .background(Color(AndroidColor.parseColor(category.color)))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = CategoryKeyResource.getStringResourceForCategory(
                                    context = context,
                                    categoryKey = category.key
                                ),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
fun STTextField(
    name: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = false
) {
    STOutline(
        modifier = modifier
            .fillMaxWidth()
    ) {
        TextField(
            value = name,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontStyle = MaterialTheme.typography.labelLarge.fontStyle,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                fontFamily = MaterialTheme.typography.labelLarge.fontFamily
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            singleLine = singleLine,
        )
    }
}

@Composable
fun Attachment(
    onAttachmentSelected: (ImageData?) -> Unit,
    modifier: Modifier = Modifier,
    receiptUriImage: ImageData? = null
) {
    var showPicturePickerDialog by remember { mutableStateOf(false) }

    if (receiptUriImage == null) {
        STOutline(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    showPicturePickerDialog = true
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.attachment),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.add_attachment),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()         // Fill the available width
                .height(200.dp)         // Set a fixed height for the image box
                .padding(16.dp)
        ) {
            when (receiptUriImage) {
                is ImageData.UriImage -> {
                    val uri = receiptUriImage.uri
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
                is ImageData.BitmapImage -> {
                    val bitmap = receiptUriImage.bitmap
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                is ImageData.LocalPathImage -> {
                    val path = receiptUriImage.path
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(path))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Local Receipt Image"
                    )
                }
            }
            // Cross Icon to remove the image, positioned at the top right of the Box
            IconButton(
                onClick = { onAttachmentSelected(null) },
                modifier = Modifier
                    .size(32.dp)               // Size of the cross button
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape) // Background circle
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear, // Cross icon
                    contentDescription = "Remove image",
                    tint = Color.White,                // Icon color
                    modifier = Modifier.size(16.dp)    // Size of the cross icon
                )
            }
        }
    }

    if (showPicturePickerDialog) {
        PicturePickerModalBottomSheet(
            onAttachmentSelected = onAttachmentSelected,
            dismissPicturePickerDialog = { showPicturePickerDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicturePickerModalBottomSheet(
    onAttachmentSelected: (ImageData?) -> Unit,
    dismissPicturePickerDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            onAttachmentSelected(uri?.let { ImageData.UriImage(it) })
            dismissPicturePickerDialog()
        }
    )

    val cameraPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            onAttachmentSelected(bitmap?.let { ImageData.BitmapImage(it) })
            dismissPicturePickerDialog()
        }
    )

    val context = LocalContext.current
    val cameraPermissionMessage = stringResource(id = R.string.camera_permission)
    val cameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraPickerLauncher.launch(null)
            } else {
                Toast.makeText(context, cameraPermissionMessage, Toast.LENGTH_SHORT).show()
            }
        }
    )

    ModalBottomSheet(
        onDismissRequest = dismissPicturePickerDialog,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PicturePicker(
                name = stringResource(id = R.string.camera),
                painter = painterResource(id = SpendTrackIcons.camera),
                contentDescription = stringResource(id = R.string.camera_description),
                onClick = {
                    cameraPermission.launch(Manifest.permission.CAMERA)
                }
            )
            Spacer(modifier = Modifier.width(32.dp))
            PicturePicker(
                name = stringResource(id = R.string.gallery),
                painter = painterResource(id = SpendTrackIcons.image),
                contentDescription = stringResource(id = R.string.gallery_description),
                onClick = {
                    galleryPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }
}

@Composable
fun PicturePicker(
    name: String,
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDate(
    onDateSelected: (Instant) -> Unit,
    modifier: Modifier = Modifier,
    transactionDate: Instant? = null
) {
    val context = LocalContext.current
    val text =
        if (transactionDate == null) stringResource(id = R.string.select_date)
        else formatLocalDateTime(context, convertInstantToLocalDateTime(transactionDate))

    var openDialog by remember { mutableStateOf(false) }

    STOutline(
        modifier = modifier
            .fillMaxWidth()
            .clickable { openDialog = !openDialog }
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(end = 2.dp)
                    .align(Alignment.CenterStart)
            )
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }

    if (openDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = transactionDate?.toEpochMilliseconds() ?: Clock.System.now().toEpochMilliseconds(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
                }

                override fun isSelectableYear(year: Int): Boolean {
                    val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
                    return currentYear >= year
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        onDateSelected(Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!))
                    }
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Previews() {
    SpendTrackTheme {
        TransactionEntry(
            name = "",
            description = "",
            categories = listOf(
                Category(
                    categoryId = "1",
                    key = "food_dining",
                    icon = R.drawable.food_dinning,
                    color = "#FF7043"
                ),
                Category(
                    categoryId = "2",
                    key = "entertainment",
                    icon = R.drawable.travel,
                    color = "#5C6BC0"
                ),
                Category(
                    categoryId = "3",
                    key = "travel",
                    icon = R.drawable.travel,
                    color = "#4DB6AC"
                ),
            ),
            selectedCategoryId = "1",
            onCategorySelected = {},
            onNameChanged = {},
            onDescriptionChanged = {},
            onAttachmentSelected = {},
            onDateSelected = {},
            onTransactionSaved = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordTransactionPreview() {
    SpendTrackTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            RecordTransaction(
                name = "",
                description = "",
                categories = emptyList(),
                selectedCategoryId = "",
                onCategorySelected = {},
                onNameChanged = {},
                onDescriptionChanged = {},
                onAttachmentSelected = {},
                onDateSelected = {},
                onTransactionSaved = {},
                onAmountChanged = {},
                amount = 0,
                transactionDate = null
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordTransactionWithUriPreview() {
    SpendTrackTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            RecordTransaction(
                name = "",
                description = "",
                categories = emptyList(),
                selectedCategoryId = "",
                onCategorySelected = {},
                onNameChanged = {},
                onDescriptionChanged = {},
                onAttachmentSelected = {},
                onDateSelected = {},
                onTransactionSaved = {},
                onAmountChanged = {},
                amount = 0,
                transactionDate = null,
                receiptUriImage = ImageData.UriImage(Uri.parse("https://picsum.photos/id/237/200/300"))
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PicturePickerModalSheetPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                PicturePicker(
                    name = stringResource(id = R.string.camera),
                    painter = painterResource(id = SpendTrackIcons.camera),
                    contentDescription = stringResource(id = R.string.camera_description),
                    onClick = {  }
                )
                Spacer(modifier = Modifier.width(32.dp))
                PicturePicker(
                    name = stringResource(id = R.string.gallery),
                    painter = painterResource(id = SpendTrackIcons.image),
                    contentDescription = stringResource(id = R.string.gallery_description),
                    onClick = {  }
                )
            }
        }
    }
}