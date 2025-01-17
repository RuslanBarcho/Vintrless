package pw.vintr.vintrless.tools.extensions

import java.awt.Image
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.ImageIcon

fun BufferedImage.isBlank(): Boolean {
    // Get the RGB values of the first pixel
    val firstPixel = getRGB(0, 0)

    // Iterate through all pixels to check if they are the same as the first pixel
    for (x in 0 until width) {
        for (y in 0 until height) {
            if (getRGB(x, y) != firstPixel) {
                return false
            }
        }
    }

    // If all pixels are the same, the image is considered blank
    return true
}

/**
 * Compares two [Icon] objects to check if they are equal.
 *
 * @param other The other [Icon] to compare with.
 * @return `true` if the icons are equal, `false` otherwise.
 */
fun Icon.isEqualTo(other: Icon): Boolean {
    // Check if the icons are the same instance
    if (this === other) return true

    // Check if the icons have the same dimensions
    if (this.iconWidth != other.iconWidth || this.iconHeight != other.iconHeight) {
        return false
    }

    // If both icons are ImageIcon, compare their underlying images
    if (this is ImageIcon && other is ImageIcon) {
        return this.image.isEqualTo(other.image)
    }

    // If the icons are not ImageIcon, assume they are not equal
    return false
}

/**
 * Compares two [Image] objects to check if they are equal.
 *
 * @param other The other [Image] to compare with.
 * @return `true` if the images are equal, `false` otherwise.
 */
fun Image.isEqualTo(other: Image): Boolean {
    // Check if the images are the same instance
    if (this === other) return true

    // Compare image dimensions
    if (this.getWidth(null) != other.getWidth(null) || this.getHeight(null) != other.getHeight(null)) {
        return false
    }

    // Convert images to BufferedImage for pixel comparison
    val bufferedImage1 = this.toBufferedImage()
    val bufferedImage2 = other.toBufferedImage()

    // Compare pixel data
    for (x in 0 until bufferedImage1.width) {
        for (y in 0 until bufferedImage1.height) {
            if (bufferedImage1.getRGB(x, y) != bufferedImage2.getRGB(x, y)) {
                return false
            }
        }
    }

    return true
}

/**
 * Converts an [Image] to a [BufferedImage].
 *
 * @return The converted [BufferedImage].
 */
fun Image.toBufferedImage(): BufferedImage {
    val bufferedImage = BufferedImage(
        this.getWidth(null),
        this.getHeight(null),
        BufferedImage.TYPE_INT_ARGB
    )
    val graphics = bufferedImage.createGraphics()
    graphics.drawImage(this, 0, 0, null)
    graphics.dispose()
    return bufferedImage
}
