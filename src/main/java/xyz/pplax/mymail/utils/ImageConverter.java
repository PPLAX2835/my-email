package xyz.pplax.mymail.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConverter {
    public static void convertToJpg(String inputFilePath, String outputFilePath) throws IOException {
        // 读取输入文件
        BufferedImage bufferedImage = ImageIO.read(new File(inputFilePath));
        // 写入输出文件
        ImageIO.write(bufferedImage, "jpg", new File(outputFilePath));
    }
}
