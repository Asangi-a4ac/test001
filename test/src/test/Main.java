package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        BufferedImage original = null;
        try {
            original = ImageIO.read(new File("ita_kisaragi.png")); // 編集する画像を持ってくる
        } catch (IOException e) {
            e.printStackTrace();
        }
        int iWidth = original.getWidth();               // 画像の横幅
        int iHeight = original.getHeight();             // 画像の縦幅
        int[] iColor = new int[iWidth * iHeight];       // 横幅と縦幅を元に色情報を保存する配列作成
        int[] iChangeColor = new int[iWidth * iHeight]; // 同じサイズで修正したデータを保存するための配列

        // 修正するBufferedImage変数を予め作成する。
        BufferedImage img = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_4BYTE_ABGR);

        // 画像の色情報をgetRGBで持ってくる
        original.getRGB(0, 0, iWidth, iHeight, iColor, 0, iWidth);
        
        double amplifier = 0.900;

        for(int i = 0; i < iHeight; i++) {
            for(int j = 0; j < iWidth; j++) {
                int iChangeIndex = (i * iWidth) + j;

                int iTargetIndex = (i * iWidth) + j;
                
                int r = (iColor[iTargetIndex] & 0x00FF0000) >> 16;
            	int g = (iColor[iTargetIndex] & 0x0000FF00) >> 8;
            	int b = (iColor[iTargetIndex] & 0x000000FF) >> 0;
            	
            	r = (int)(r * amplifier) & 0xFF;
            	g = (int)(g * amplifier) & 0xFF;
            	b = (int)(b * amplifier) & 0xFF;

                iChangeColor[iChangeIndex] = 0xFF000000 | (r << 16) | (g << 8) | (b << 0);
            }
        }
        img.setRGB(0, 0, iWidth, iHeight, iChangeColor, 0, iWidth);

        try {
            ImageIO.write(img, "png", new File("./out.png")); // 画像を保存
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}