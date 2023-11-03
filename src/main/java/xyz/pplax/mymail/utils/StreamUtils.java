package xyz.pplax.mymail.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
/**
 * 此类用于演示关于流的读写方法
 *
 */
public class StreamUtils {
    /**
     * 功能：将输入流转换成 byte[]
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static byte[] streamToByteArray(InputStream is) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();//创建输出流对象
        byte[] b = new byte[1024];
        int len;
        while ((len = is.read(b)) != -1) {
            bos.write(b, 0, len);
        }
        byte[] array = bos.toByteArray();
        bos.close();
        return array;
    }
}
/**
 * 功能：将 InputStream 转换成 String
 * @param
 * @return
 * @throws Exception
 */