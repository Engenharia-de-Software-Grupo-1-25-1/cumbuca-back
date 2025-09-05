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

    private static byte[] comprimir(byte[] imagemOriginal, float qualidade) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemOriginal));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(qualidade);
        }

        writer.setOutput(new MemoryCacheImageOutputStream(baos));
        writer.write(null, new IIOImage(bufferedImage, null, null), param);
        writer.dispose();

        return baos.toByteArray();
    }

    public static byte[] comprimirAte100KB(byte[] imagemOriginal) throws IOException {
        final int LIMITE = 100_000;
        float qualidade = 0.9f;
        byte[] comprimida = imagemOriginal;
    
        while (comprimida.length > LIMITE && qualidade > 0.1f) {
            comprimida = comprimir(imagemOriginal, qualidade);
            qualidade -= 0.1f;
        }
    
        return comprimida;
    }
    
}

