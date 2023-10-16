package no.shhsoft.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ImageUtils {

    private static final JPEGImageWriteParam JPEG_PARAMS_FAST = getFastParamsWithQuality(0.69f);
    private static final JPEGImageWriteParam JPEG_PARAMS_GOOD = getGoodParamsWithQuality(0.8f);
    private static final ImageWriter JPEG_IMAGE_WRITER = ImageIO.getImageWritersByFormatName("JPEG").next();

    static {
        ImageIO.setUseCache(false);
    }

    public static byte[] jpegEncodeFast(final BufferedImage image) {
        return jpegEncode(image, JPEG_PARAMS_FAST);
    }

    public static byte[] jpegEncodeGood(final BufferedImage image) {
        return jpegEncode(image, JPEG_PARAMS_GOOD);
    }

    public static byte[] jpegEncode(final BufferedImage image, final JPEGImageWriteParam param) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(65536);
        final MemoryCacheImageOutputStream bout = new MemoryCacheImageOutputStream(out);
        /* 2020-01-16: Newer versions of Java do not allow to save images with alpha channel as JPEG. */
        final BufferedImage imageWithoutAlpha = removeAlpha(image, false);
        try {
            JPEG_IMAGE_WRITER.setOutput(bout);
            JPEG_IMAGE_WRITER.write(null, new IIOImage(imageWithoutAlpha, null, null), param);
            bout.close();
            return out.toByteArray();
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        } finally {
            imageWithoutAlpha.flush();
        }
    }

    public static BufferedImage removeAlpha(final BufferedImage image, final boolean flushOriginal) {
        if (!hasAlphaChannel(image)) {
            return image;
        }
        final BufferedImage imageCopy = copy(image, BufferedImage.TYPE_INT_RGB);
        if (flushOriginal) {
            image.flush();
        }
        return imageCopy;
    }

    public static boolean hasAlphaChannel(final BufferedImage image) {
        return image.getTransparency() != BufferedImage.OPAQUE;
    }

    /**
     *
     * @param data
     * @return <code>null</code> if unable to decode the image.
     */
    public static BufferedImage decode(final byte[] data) {
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            final BufferedImage image = ImageIO.read(in);
            in.close();
            return image;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public static BufferedImage makeRgbOrArgb(final BufferedImage image, final boolean flushOriginal) {
        final int type = image.getType();
        if (type == BufferedImage.TYPE_INT_RGB || type == BufferedImage.TYPE_INT_ARGB) {
            return image;
        }
        final BufferedImage imageCopy = copy(image, hasAlphaChannel(image) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        if (flushOriginal) {
            image.flush();
        }
        return imageCopy;
    }

    public static BufferedImage copy(final BufferedImage image, final int newType) {
        image.setAccelerationPriority(1.0f);
        final BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), newType);
        final Graphics2D graphics = copy.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        copy.setAccelerationPriority(1.0f);
        return copy;
    }

    public static boolean isLandscape(final BufferedImage image) {
        return image.getWidth() > image.getHeight();
    }

    public static BufferedImage limitHeight(final BufferedImage image, final int newHeight) {
        if (image.getHeight() <= newHeight) {
            return image;
        }
        return fitHeight(image, newHeight);
    }

    public static BufferedImage fitHeight(final BufferedImage image, final int newHeight) {
        if (image.getHeight() == newHeight) {
            return image;
        }
        final double scale = (double) newHeight / image.getHeight();
        final int newWidth = (int) (image.getWidth() * scale);
        return resize(image, newWidth, newHeight);
    }

    public static BufferedImage limitWidth(final BufferedImage image, final int newWidth) {
        if (image.getWidth() <= newWidth) {
            return image;
        }
        return fitWidth(image, newWidth);
    }

    public static BufferedImage fitWidth(final BufferedImage image, final int newWidth) {
        if (image.getWidth() == newWidth) {
            return image;
        }
        final double scale = (double) newWidth / image.getWidth();
        final int newHeight = (int) (image.getHeight() * scale);
        return resize(image, newWidth, newHeight);
    }

    public static BufferedImage limit(final BufferedImage image, final int maxWidth, final int maxHeight) {
        final double widthRatio = (double) maxWidth / image.getWidth();
        final double heightRatio = (double) maxHeight / image.getHeight();
        if (widthRatio < heightRatio) {
            return limitWidth(image, maxWidth);
        }
        return limitHeight(image, maxHeight);
    }

    public static BufferedImage fit(final BufferedImage image, final int width, final int height) {
        final double widthRatio = (double) width / image.getWidth();
        final double heightRatio = (double) height / image.getHeight();
        if (widthRatio < heightRatio) {
            return fitWidth(image, width);
        }
        return fitHeight(image, height);
    }

    public static BufferedImage resize(final BufferedImage origImage, final int newWidth, final int newHeight) {
        final BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(origImage, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();
        scaledImage.setAccelerationPriority(1.0f);
        return scaledImage;
    }

    public static BufferedImage cropToSquare(final BufferedImage origImage) {
        final int size = Math.min(origImage.getWidth(), origImage.getHeight());
        final BufferedImage croppedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics2D = croppedImage.createGraphics();
        graphics2D.drawImage(origImage, -(origImage.getWidth() - size) / 2, -(origImage.getHeight() - size) / 2, null);
        graphics2D.dispose();
        croppedImage.setAccelerationPriority(1.0f);
        return croppedImage;
    }

    public static JPEGImageWriteParam getFastParamsWithQuality(final float quality) {
        final JPEGImageWriteParam params = new JPEGImageWriteParam(null);
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(quality);
        params.setOptimizeHuffmanTables(false);
        params.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
        return params;
    }

    public static JPEGImageWriteParam getGoodParamsWithQuality(final float quality) {
        final JPEGImageWriteParam params = new JPEGImageWriteParam(null);
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(quality);
        params.setOptimizeHuffmanTables(true);
        params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
        return params;
    }

}
