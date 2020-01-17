package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


public class ImageUtils {

    public static Map<Object, String> getImageFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable == null) {
            return null;
        }

        Map<Object, String> result = new LinkedHashMap<>();

        //如果复制的是文件
        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            java.util.List fileList = null;
            try {
                fileList = (java.util.List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            for (Object o : fileList) {
                File file = (File) o;
                if (!file.isFile()) {
                    continue;
                }

                if (!file.canRead()) {
                    continue;
                }

                String name = file.getName().toLowerCase();


                int dotIndex = name.lastIndexOf(".");
                if (dotIndex <= 0) {
                    continue;
                }

                String suffix = name.substring(dotIndex);
                if (!(suffix.equalsIgnoreCase(".png") || suffix.equalsIgnoreCase(".jpg") ||
                        suffix.equalsIgnoreCase(".jpeg") || suffix.equalsIgnoreCase(".gif") ||
                        suffix.equalsIgnoreCase(".bmp"))) {
                    continue;
                }

                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(file);
                } catch (IOException e) {

                }
                if (bufferedImage == null) {
                    continue;
                }

                result.put(file,suffix);
            }

            return result;
        }

        if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image image = null;
            try {
                image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            result.put(toBufferedImage(image),".png");
            return result;
        }

        return null;
    }

    public static BufferedImage toBufferedImage(Image src) {
        if (src instanceof BufferedImage) {
            return (BufferedImage) src;
        }
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        if (w < 0 || h < 0) {
            return null;
        }
        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage dest = new BufferedImage(w, h, type);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dest;
    }

    public static void saveImage(BufferedImage image, File target) {
        try {
            FileOutputStream result = new FileOutputStream(target);
            ImageIO.write(image, "PNG", result);
            result.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
