package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;


public class ImageUtils {

    public static Image getImageFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                return (Image) transferable.getTransferData(DataFlavor.imageFlavor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
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
}
