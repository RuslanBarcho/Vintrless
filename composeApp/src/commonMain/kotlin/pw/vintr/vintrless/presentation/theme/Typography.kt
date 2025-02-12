package pw.vintr.vintrless.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.gilroy_extra_bold
import vintrless.composeapp.generated.resources.rubik_bold

// Default empty typography
val Typography = Typography()

@Composable
fun Rubik() = FontFamily(
    Font(Res.font.rubik_regular, FontWeight.Normal),
    Font(Res.font.rubik_medium, FontWeight.Medium),
    Font(Res.font.rubik_bold, FontWeight.Bold),
)

@Composable
fun Gilroy() = FontFamily(
    Font(Res.font.gilroy_extra_bold),
)

@Composable
fun JBMono() = FontFamily(
    Font(Res.font.jetbrains_mono_nl_medium),
)

@Composable
fun BaseRubikStyle() = TextStyle(fontFamily = Rubik())

@Composable
fun RubikRegular() = BaseRubikStyle().copy(fontWeight = FontWeight.Normal)
@Composable
fun RubikMedium() = BaseRubikStyle().copy(fontWeight = FontWeight.Medium)
@Composable
fun RubikBold() = BaseRubikStyle().copy(fontWeight = FontWeight.Bold)

@Composable
fun RubikRegular14() = RubikRegular().copy(fontSize = 14.sp)
@Composable
fun RubikRegular16() = RubikRegular().copy(fontSize = 16.sp)
@Composable
fun RubikMedium12() = RubikMedium().copy(fontSize = 12.sp)
@Composable
fun RubikMedium14() = RubikMedium().copy(fontSize = 14.sp)
@Composable
fun RubikMedium16() = RubikMedium().copy(fontSize = 16.sp)
@Composable
fun RubikMedium18() = RubikMedium().copy(fontSize = 18.sp)
@Composable
fun RubikBold12() = RubikBold().copy(fontSize = 12.sp)

@Composable
fun BaseGilroyStyle() = TextStyle(fontFamily = Gilroy())

@Composable
fun Gilroy10() = BaseGilroyStyle().copy(fontSize = 10.sp)
@Composable
fun Gilroy11() = BaseGilroyStyle().copy(fontSize = 11.sp)
@Composable
fun Gilroy12() = BaseGilroyStyle().copy(fontSize = 12.sp)
@Composable
fun Gilroy14() = BaseGilroyStyle().copy(fontSize = 14.sp)
@Composable
fun Gilroy16() = BaseGilroyStyle().copy(fontSize = 16.sp)
@Composable
fun Gilroy18() = BaseGilroyStyle().copy(fontSize = 18.sp)
@Composable
fun Gilroy20() = BaseGilroyStyle().copy(fontSize = 20.sp)
@Composable
fun Gilroy24() = BaseGilroyStyle().copy(fontSize = 24.sp)
@Composable
fun Gilroy32() = BaseGilroyStyle().copy(fontSize = 32.sp)
@Composable
fun Gilroy36() = BaseGilroyStyle().copy(fontSize = 36.sp)

@Composable
fun BaseJBMonoStyle() = TextStyle(fontFamily = JBMono())

@Composable
fun JBMono12() = BaseJBMonoStyle().copy(fontSize = 12.sp)
