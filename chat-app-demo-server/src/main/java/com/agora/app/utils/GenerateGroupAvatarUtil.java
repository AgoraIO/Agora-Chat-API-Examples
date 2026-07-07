package com.agora.app.utils;

import com.agora.app.service.RestService;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class GenerateGroupAvatarUtil {

    public static BufferedImage getCombinationOfHead(RestService restService, String appkey, String groupId, List<String> urls)
            throws IOException {

        List<URL> paths = new ArrayList<>();
        int size = 9;
        if (urls.size() < size) {
            size = urls.size();
        }
        for (int i = 0; i < size; i++) {
            paths.add(new URL(urls.get(i)));
        }

        List<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();

        int imageSize = 99;
        if (paths.size() <= 4) {
            imageSize = 150;
        }

        log.info("start resize group avatar. appkey : {}, groupId : {}", appkey, groupId);
        long handleTime = System.currentTimeMillis();

        List<CompletableFuture<BufferedImage>> downloadFutures = paths.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> {
                    URL defaultUrl = GenerateGroupAvatarUtil.class.getClassLoader()
                            .getResource("default_avatar.png");
                    if (url.getPath().contains("default_avatar")) {
                        try (DataInputStream dis = new DataInputStream(defaultUrl.openStream())) {
                            return ImageIO.read(dis);
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }

                    try (BufferedInputStream bi = restService.downloadThumbImage(appkey, url.getPath())) {
                        if (bi == null) {
                            DataInputStream dis = new DataInputStream(defaultUrl.openStream());
                            return ImageIO.read(dis);
                        }
                        return ImageIO.read(bi);
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(downloadFutures.toArray(new CompletableFuture[0]));

        int finalImageSize = imageSize;
        allOf.thenRun(() -> {
            List<BufferedImage> images = downloadFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            for (BufferedImage image : images) {
                bufferedImages.add(resize(image, finalImageSize, finalImageSize, true));
            }
        }).join();

        log.info("resize group avatar end. appkey : {}, groupId : {}, time : {}", appkey, groupId, (System.currentTimeMillis() - handleTime));

        int width = 336; // 这是画板的宽高
        int height = 336; // 这是画板的高度
        int cornerRadius = 15; // 圆角半径

        BufferedImage outImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        // 生成画布
        Graphics g = outImage.getGraphics();

        Graphics2D g2d = (Graphics2D) g;

        // 设置背景色
        g2d.setBackground(new Color(227, 230, 232));
        //g2d.setBackground(new Color(231, 0, 4));

        // 通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
        g2d.clearRect(0, 0, width, height);

        // 开始拼凑 根据图片的数量判断该生成那种样式的组合头像目前为4中
        int j = 1;
        int k = 1;
        for (int i = 1; i <= bufferedImages.size(); i++) {
            if (bufferedImages.size() == 9) {
                int padding = (width - imageSize * 3) / 4; // 图片间距
                if (i <= 3) {
                    int x = imageSize * i + padding * i - imageSize;
                    int y = padding;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                } else if (i <= 6) {
                    int x = imageSize * j + padding * j - imageSize;
                    int y = imageSize + padding * 2;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    j++;
                } else {
                    int x = imageSize * k + padding * k - imageSize;
                    int y = imageSize * 2 + padding * 3;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    k++;
                }
            } else if (bufferedImages.size() == 8) {
                int padding = (width - imageSize * 3) / 4; // 图片间距
                int borderPadding = (width - imageSize * 2 - padding * 2) / 2;
                if (i <= 2) {
                    int x = imageSize * (i - 1) + padding * (i - 1) + borderPadding;
                    int y = padding;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                } else if (i <= 5) {
                    int x = imageSize * j + padding * j - imageSize;
                    int y = imageSize + padding * 2;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    j++;
                } else {
                    int x = imageSize * k + padding * k - imageSize;
                    int y = imageSize * 2 + padding * 3;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    k++;
                }
            } else if (bufferedImages.size() == 7) {
                int padding = (width - imageSize * 3) / 4; // 图片间距
                int borderPadding = (width - imageSize) / 2;
                if (i <= 1) {
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(borderPadding, padding, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), borderPadding, padding, null);
                } else if (i <= 4) {
                    int x = imageSize * j + padding * j - imageSize;
                    int y = imageSize + padding * 2;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    j++;
                } else {
                    int x = imageSize * k + padding * k - imageSize;
                    int y = imageSize * 2 + padding * 3;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    k++;
                }
            } else if (bufferedImages.size() == 6) {
                int padding = (width - imageSize * 3) / 4; // 图片间距
                int borderPadding = (width - imageSize * 2 - padding) / 2;
                if (i <= 3) {
                    int x = imageSize * i + padding * i - imageSize;
                    int y = borderPadding;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                } else {
                    int x = imageSize * j + padding * j - imageSize;
                    int y = imageSize + borderPadding + padding;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    j++;
                }
            } else if (bufferedImages.size() == 5) {
                int padding = (width - imageSize * 3) / 4; // 图片间距
                int topBorderPadding = (width - imageSize * 2 - padding) / 2;
                int leftBorderPadding = (width - imageSize * 2 - padding * 2) / 2;
                if (i <= 2) {
                    int x = imageSize * (i - 1) + padding * (i - 1) + leftBorderPadding;
                    int y = topBorderPadding;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                } else {
                    int x = imageSize * j + padding * j - imageSize;
                    int y = imageSize + topBorderPadding + padding;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    j++;
                }
            } else if (bufferedImages.size() == 4) {
                int padding = (width - imageSize * 2) / 3;
                if (i <= 2) {
                    int x = imageSize * i + padding * i - imageSize;
                    int y = padding;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                } else {
                    int x = imageSize * j + padding * j - imageSize;
                    int y = imageSize + padding * 2;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    j++;
                }
            } else if (bufferedImages.size() == 3) {
                int padding = (width - imageSize * 2) / 3;
                int borderPadding = (width - imageSize) / 2;
                if (i <= 1) {
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(borderPadding, padding, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), borderPadding, padding, null);
                } else {
                    int x = imageSize * j + padding * j - imageSize;
                    int y = imageSize + padding * 2;
                    RoundRectangle2D
                            roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                    g2d.setClip(roundedRectangle);
                    g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
                    j++;
                }

            } else if (bufferedImages.size() == 2) {
                int padding = (width - imageSize * 2) / 3;
                int x = imageSize * i + padding * i - imageSize;
                int y = (height - imageSize) / 2;
                RoundRectangle2D
                        roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                g2d.setClip(roundedRectangle);
                g2d.drawImage(bufferedImages.get(i - 1), x, y, null);

            } else if (bufferedImages.size() == 1) {
                int x = (width - imageSize) / 2;
                int y = x;
                RoundRectangle2D
                        roundedRectangle = new RoundRectangle2D.Float(x, y, imageSize, imageSize, cornerRadius, cornerRadius);
                g2d.setClip(roundedRectangle);
                g2d.drawImage(bufferedImages.get(i - 1), x, y, null);
            }

            // 需要改变颜色的话在这里绘上颜色。可能会用到AlphaComposite类
        }

        return outImage;
    }

    /**
     * 图片缩放
     *
     * @param bi      图片数据流
     * @param height   高度
     * @param width    宽度
     * @param bb       比例不对时是否需要补白
     */
    private static BufferedImage resize(BufferedImage bi, int height, int width,
            boolean bb) {

        double ratio = 0; // 缩放比例

        //File f = new File(dis);
        //            BufferedImage bi = ImageIO.read(dis);
        Image itemp = bi.getScaledInstance(width, height,
                Image.SCALE_SMOOTH);
        // 计算比例
        if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
            if (bi.getHeight() > bi.getWidth()) {
                ratio = (new Integer(height)).doubleValue()
                        / bi.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue() / bi.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(
                    AffineTransform.getScaleInstance(ratio, ratio), null);
            itemp = op.filter(bi, null);
        }
        if (bb) {
            // copyimg(filePath, "D:\\img");
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);
            if (width == itemp.getWidth(null)) {
                g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
                        itemp.getWidth(null), itemp.getHeight(null),
                        Color.white, null);
            } else {
                g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
                        itemp.getWidth(null), itemp.getHeight(null),
                        Color.white, null);
            }
            g.dispose();
            itemp = image;
        }

        return (BufferedImage) itemp;
    }
}
