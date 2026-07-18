package vegabobo.languageselector.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier
            .padding(start = 18.dp, end = 18.dp)
            // Extra top space visually separates each section (Pinned/Suggested/All);
            // smaller bottom space keeps the header tied to its rows.
            .padding(top = 20.dp, bottom = 6.dp)
    )
}
