package com.zacle.spendtrack.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.R
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.model.Category
import kotlinx.datetime.Instant

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
    onAttachmentSelected: (String) -> Unit,
    onDateSelected: (Instant) -> Unit,
    onTransactionSaved: () -> Unit,
    modifier: Modifier = Modifier,
    amount: Int = 0,
    transactionDate: Instant? = null,
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.4f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            TransactionEntryAmount(
                onAmountChanged = onAmountChanged,
                amount = amount,
                contentColor = contentColor
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
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun TransactionEntryAmount(
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
            text = stringResource(id = R.string.how_much),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(start = 16.dp)
        )
        TextField(
            value = "$$amount",
            onValueChange = { entry -> onAmountChanged(entry.filter { it.isDigit() }.toIntOrNull() ?: 0) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
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
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
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
    onAttachmentSelected: (String) -> Unit,
    onDateSelected: (Instant) -> Unit,
    onTransactionSaved: () -> Unit,
    modifier: Modifier = Modifier,
    transactionDate: Instant? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                onAttachmentSelected = onAttachmentSelected
            )
            TransactionDate(
                onDateSelected = onDateSelected,
                transactionDate = transactionDate
            )
            Spacer(modifier = Modifier.weight(1f))
            SpendTrackButton(
                text = stringResource(id = R.string.save),
                onClick = onTransactionSaved
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
    STOutline(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.categories),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(end = 2.dp)
                    .align(Alignment.CenterStart)
            )
            Icon(
                imageVector = SpendTrackIcons.dropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
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
    onAttachmentSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    STOutline(
        modifier = modifier
            .fillMaxWidth()
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
}

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

    STOutline(
        modifier = modifier
            .fillMaxWidth()
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
                    name = R.string.food_drinks,
                    icon = R.drawable.food_dinning,
                    color = "#FF7043"
                ),
                Category(
                    categoryId = "2",
                    name = R.string.remaining,
                    icon = R.drawable.travel,
                    color = "#5C6BC0"
                ),
                Category(
                    categoryId = "3",
                    name = R.string.exceeded_limit,
                    icon = R.drawable.travel,
                    color = "#4DB6AC"
                ),
            ),
            selectedCategoryId = "",
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