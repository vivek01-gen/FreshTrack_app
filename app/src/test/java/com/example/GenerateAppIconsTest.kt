package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GenerateAppIconsTest {

  @Test
  fun generateMipmapIcons() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val densities = mapOf(
      "mdpi" to 48,
      "hdpi" to 72,
      "xhdpi" to 96,
      "xxhdpi" to 144,
      "xxxhdpi" to 192
    )

    for ((density, size) in densities) {
      val normalIcon = drawIcon(context, size, isRound = false)
      saveBitmap(normalIcon, "/app/src/main/res/mipmap-$density/ic_launcher.webp")

      val roundIcon = drawIcon(context, size, isRound = true)
      saveBitmap(roundIcon, "/app/src/main/res/mipmap-$density/ic_launcher_round.webp")
    }

    // Also write a 512x512 high-res Play Store icon
    val playStoreIcon = drawIcon(context, 512, isRound = false)
    saveBitmap(playStoreIcon, "/app/src/main/res/drawable/play_store_512.png", Bitmap.CompressFormat.PNG)
    
    println("App icons generated successfully for all screen densities in mipmap folders!")
  }

  private fun drawIcon(context: Context, size: Int, isRound: Boolean): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val drawableBg = context.resources.getDrawable(R.drawable.ic_launcher_background, context.theme)
    val drawableFg = context.resources.getDrawable(R.drawable.ic_launcher_foreground, context.theme)

    if (isRound) {
      val path = Path().apply {
        addCircle(size / 2f, size / 2f, size / 2f, Path.Direction.CW)
      }
      canvas.clipPath(path)
    }

    drawableBg?.setBounds(0, 0, size, size)
    drawableBg?.draw(canvas)

    drawableFg?.setBounds(0, 0, size, size)
    drawableFg?.draw(canvas)

    return bitmap
  }

  private fun saveBitmap(bitmap: Bitmap, absolutePath: String, format: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP) {
    val file = File(absolutePath)
    file.parentFile.mkdirs()
    FileOutputStream(file).use { out ->
      bitmap.compress(format, 100, out)
    }
  }
}
