import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
 
public class Loader {
    
    public static String readImage(String imagePath) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imagePath));
        }
        catch(IOException e) {
            System.out.println("Could not open image from "+imagePath);
            return "";
        }

        final byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        try {
            return new String(pixels, "ISO-8859-1");
        }
        catch(UnsupportedEncodingException e) {
            System.out.println("ISO-8859-1 encoding failed");
            return "";
        }
    }

}