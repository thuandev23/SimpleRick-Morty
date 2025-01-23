package com.thuan.simplemultimodule.component.character

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thuan.network.models.domain.CharacterStatus
import com.thuan.simplemultimodule.ui.theme.RickTextPrimary
import com.thuan.simplemultimodule.ui.theme.SimpleMultiModuleTheme
import com.thuan.simplemultimodule.utils.asColor

@Composable
fun CharacterStatusComponent(characterStatus: CharacterStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.border(
            width = 1.dp,
            color = characterStatus.asColor(),
            shape = RoundedCornerShape(12.dp)
        ).padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Status: ${characterStatus.displayName}",
            color = RickTextPrimary,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewAlive() {
    SimpleMultiModuleTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Alive)
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewDead() {
    SimpleMultiModuleTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Dead)
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewUnknown() {
    SimpleMultiModuleTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Unknown)
    }
}