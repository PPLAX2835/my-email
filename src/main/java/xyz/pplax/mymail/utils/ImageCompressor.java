package xyz.pplax.mymail.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCompressor {

    /**
     * 压缩图片
     *
     * @param srcPath   原始图片路径
     * @param targetPath 目标图片路径
     * @param quality   压缩质量，范围为0-1
     * @throws IOException
     */
    public static void compress(String srcPath, String targetPath, float quality) throws IOException {
        File srcFile = new File(srcPath);
        BufferedImage srcImage = ImageIO.read(srcFile);

        int width = (int) (srcImage.getWidth() * quality);
        int height = (int) (srcImage.getHeight() * quality);

        Image targetImage = srcImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(targetImage, 0, 0, null);

        ImageIO.write(outputImage, "jpg", new File(targetPath));
    }
}