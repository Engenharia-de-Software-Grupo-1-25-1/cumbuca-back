package br.com.cumbuca.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCompressor {

    private static byte[] comprimir(final byte[] imagemOriginal, final float qualidade) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemOriginal));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

        final ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(qualidade);
        }

        writer.setOutput(new MemoryCacheImageOutputStream(baos));
        writer.write(null, new IIOImage(bufferedImage, null, null), param);
        writer.dispose();

        return baos.toByteArray();
    }

    public static byte[] comprimirAte100KB(final byte[] imagemOriginal) throws IOException {
        final int LIMITE = 100_000;
        float qualidade = 0.9f;
        byte[] comprimida = imagemOriginal;

        while (comprimida.length > LIMITE && qualidade > 0.4f) {
            comprimida = comprimir(imagemOriginal, qualidade);
            qualidade -= 0.1f;
        }

        return comprimida;
    }
}
