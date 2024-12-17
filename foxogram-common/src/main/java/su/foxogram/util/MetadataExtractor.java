package su.foxogram.util;

import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MetadataExtractor {

	public static void removeMetadata(String fileExtension, InputStream inputStream, OutputStream outputStream) throws IOException {
		switch (fileExtension.toLowerCase()) {
			case ".jpeg", ".jpg":
				removeExifMetadata(inputStream, outputStream);
				break;
			case ".png":
				removePngMetadata(inputStream, outputStream);
				break;
		}
	}

	private static void removeExifMetadata(InputStream inputStream, OutputStream outputStream) throws IOException {
		try {
			new ExifRewriter().removeExifMetadata(inputStream, outputStream);
		} catch (Exception e) {
			throw new IOException("Failed to remove EXIF metadata", e);
		}
	}

	private static void removePngMetadata(InputStream inputStream, OutputStream outputStream) throws IOException {
		BufferedImage image = ImageIO.read(inputStream);

		ImageIO.write(image, "png", outputStream);
	}
}
