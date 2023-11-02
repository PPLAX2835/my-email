package xyz.pplax.mymail.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    /**
     * 根据文件绝对路径删除文件
     * @param filename
     * @return
     * @throws IOException
     */
    public static boolean deleteStaticFile(String filename) throws IOException {
        File myDelFile = new File(filename);
        return myDelFile.delete();
    }

    /**
     * 删除文件夹
     * @param folder
     * @return
     */
    private static boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        return false;
                    }
                }
            }
        }
        return folder.delete();
    }

    /**
     * 根据路径删除文件夹
     * @param path
     * @return
     */
    private static boolean deleteFolder(String path) {
        File folder = new File(path);
        return deleteFolder(folder);
    }

    /**
     * 根据传入的路径创建文件夹
     * @param path
     */
    public static void createFolder(String path) {
        File folder = new File(path);

        // 检查文件夹是否已存在
        if (folder.exists()) {
            // 删除已存在的文件夹
            boolean deleted = deleteFolder(folder);
            if (deleted) {
                // 重新创建文件夹
                boolean created = folder.mkdirs();
                if (created) {
                    System.out.println("文件夹创建成功");
                } else {
                    System.out.println("文件夹创建失败");
                }
            } else {
                System.out.println("文件夹删除失败，无法创建新文件夹");
            }
        } else {
            // 创建文件夹
            boolean created = folder.mkdirs();
            if (created) {
                System.out.println("文件夹创建成功");
            } else {
                System.out.println("文件夹创建失败");
            }
        }
    }

    /**
     * 根据关键词删指定目录下的文件
     * @param directoryPath
     * @param pattern
     */
    public static void deleteFilesWithPattern(String directoryPath, String pattern) {
        File directory = new File(directoryPath);

        // 检查目录是否存在
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            // 遍历目录下的文件
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().contains(pattern)) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            System.out.println("已删除文件: " + file.getAbsolutePath());
                        } else {
                            System.out.println("无法删除文件: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            System.out.println("目录不存在或不是一个有效的目录");
        }
    }

}
