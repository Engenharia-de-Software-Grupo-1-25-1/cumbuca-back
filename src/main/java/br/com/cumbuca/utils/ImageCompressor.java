package br.com.cumbuca.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ImageCompressor {

    private static byte[] comprimir(final byte[] imagemOriginal, final float qualidade) throws IOException {
        final BufferedImage src = ImageIO.read(new ByteArrayInputStream(imagemOriginal));
        if (src == null) throw new IOException("Imagem inválida");

        final int w = src.getWidth();
        final int h = src.getHeight();
        final BufferedImage rgb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = rgb.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.drawImage(src, 0, 0, null);
        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("jpeg");
            if (!it.hasNext()) throw new IOException("Writer JPEG não encontrado");
            final ImageWriter writer = it.next();

            final ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(Math.max(0f, Math.min(1f, qualidade)));
            }

            writer.setOutput(new MemoryCacheImageOutputStream(baos));
            writer.write(null, new IIOImage(rgb, null, null), param);
            writer.dispose();
            return baos.toByteArray();
        }
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
