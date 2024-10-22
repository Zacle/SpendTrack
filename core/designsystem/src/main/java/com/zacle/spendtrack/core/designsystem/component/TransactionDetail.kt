package com.zacle.spendtrack.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.CategoryKeyResource
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.util.convertInstantToLocalDateTime
import com.zacle.spendtrack.core.model.util.dateToString
import com.zacle.spendtrack.core.shared_resources.R
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import kotlin.time.Duration.Companion.days

@Composable
fun TransactionDetailScreen(
    amount: Int,
    name: String,
    date: Instant,
    type: String,
    key: String,
    description: String,
    currency: String,
    receiptUriImage: ImageData?,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    isTransactionDeleted: Boolean = false,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(fraction = 0.3f)
                    .fillMaxWidth()
                    .background(
                        color = color,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                TransactionDetailTopScreen(
                    amount = amount,
                    name = name,
                    date = date,
                    color = color,
                    contentColor = contentColor,
                    currency = currency
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            TransactionDetailBottomScreen(
                description = description,
                receiptUriImage = receiptUriImage,
                onEdit = onEdit,
                isTransactionDeleted = isTransactionDeleted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
        TransactionType(
            type = type,
            key = key,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-160).dp)
        )
    }
}

@Composable
fun TransactionDetailTopScreen(
    amount: Int,
    name: String,
    date: Instant,
    currency: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "$amount$currency",
                style = MaterialTheme.typography.displayMedium,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = dateToString(
                    localDateTime = convertInstantToLocalDateTime(date),
                    givenFormat = "EEEE d MMMM yyyy hh:mma"
                ),
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TransactionDetailBottomScreen(
    description: String,
    receiptUriImage: ImageData?,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    isTransactionDeleted: Boolean = false
) {
    Surface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            if (description.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (receiptUriImage != null) {
                Text(
                    text = stringResource(R.string.attachment),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()         // Fill the available width
                        .height(250.dp)         // Set a fixed height for the image box
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
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            SpendTrackButton(
                text = stringResource(R.string.edit),
                onClick = onEdit,
                enabled = !isTransactionDeleted
            )
        }
    }
}

@Composable
fun TransactionType(
    type: String,
    key: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.type),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = type,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.category),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = CategoryKeyResource.getStringResourceForCategory(
                        context = LocalContext.current,
                        categoryKey = key
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveTransactionModalSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.remove_transaction_title),
    description: String = stringResource(R.string.remove_transaction_description)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Row {
                SpendTrackButton(
                    text = stringResource(R.string.no),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(32.dp))
                SpendTrackButton(
                    text = stringResource(R.string.yes),
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TransactionDeletedDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = SpendTrackIcons.done,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = stringResource(R.string.transaction_removed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionDetailScreenPreview() {
    SpendTrackTheme {
        TransactionDetailScreen(
            amount = 100,
            name = "Buy some groceries",
            date = Clock.System.now().minus(5.days),
            type = "Expense",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                    "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam",
            key = "shopping",
            receiptUriImage = null,
            onEdit = {},
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            currency = "$"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionDeletedDialogPreview() {
    SpendTrackTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            TransactionDeletedDialog({})
        }
    }
}