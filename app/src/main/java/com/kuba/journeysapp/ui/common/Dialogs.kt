package com.kuba.journeysapp.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kuba.journeysapp.R

@Composable
fun ConfirmDialog(
    onConfirmListener: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    @DrawableRes icon: Int? = null,
    iconTint: Color = LocalContentColor.current,
    confirmButtonLabel: String = stringResource(R.string.confirm),
    cancelButtonLabel: String = stringResource(R.string.cancel),
    onDismissListener: () -> Unit = {}
) {
    AlertDialog(
        icon = {
            icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = "$title + dialog icon",
                    tint = iconTint
                )
            }
        },
        title = { title?.let { Text(it) } },
        text = { text?.let { Text(it) } },
        onDismissRequest = onDismissListener,
        confirmButton = {
            TextButton(onConfirmListener) {
                Text(confirmButtonLabel)
            }
        },
        dismissButton = {
            TextButton(onDismissListener) {
                Text(cancelButtonLabel)
            }
        },
        modifier = modifier
    )
}