package org.apache.poi.ss.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ImageUtils {
    public static final int PIXEL_DPI = 96;
    private static final POILogger logger = POILogFactory.getLogger(ImageUtils.class);

    public static Dimension getImageDimension(InputStream is, int type) {
        Dimension size = new Dimension();
        if (type == 5 || type == 6 || type == 7) {
            try {
                ImageInputStream iis = ImageIO.createImageInputStream(is);
                ImageReader r = (ImageReader) ImageIO.getImageReaders(iis).next();
                r.setInput(iis);
                BufferedImage img = r.read(0);
                int[] dpi = getResolution(r);
                if (dpi[0] == 0) {
                    dpi[0] = 96;
                }
                if (dpi[1] == 0) {
                    dpi[1] = 96;
                }
                size.width = (img.getWidth() * 96) / dpi[0];
                size.height = (img.getHeight() * 96) / dpi[1];
                r.dispose();
                iis.close();
            } catch (IOException e) {
                logger.log(POILogger.WARN, (Throwable) e);
            }
        } else {
            logger.log(POILogger.WARN, (Object) "Only JPEG, PNG and DIB pictures can be automatically sized");
        }
        return size;
    }

    public static int[] getResolution(ImageReader r) throws IOException {
        int hdpi = 96;
        int vdpi = 96;
        Element node = (Element) r.getImageMetadata(0).getAsTree("javax_imageio_1.0");
        NodeList lst = node.getElementsByTagName("HorizontalPixelSize");
        if (lst != null && lst.getLength() == 1) {
            double parseFloat = (double) Float.parseFloat(((Element) lst.item(0)).getAttribute("value"));
            Double.isNaN(parseFloat);
            hdpi = (int) (25.4d / parseFloat);
        }
        NodeList lst2 = node.getElementsByTagName("VerticalPixelSize");
        if (lst2 != null && lst2.getLength() == 1) {
            double parseFloat2 = (double) Float.parseFloat(((Element) lst2.item(0)).getAttribute("value"));
            Double.isNaN(parseFloat2);
            vdpi = (int) (25.4d / parseFloat2);
        }
        return new int[]{hdpi, vdpi};
    }
}
