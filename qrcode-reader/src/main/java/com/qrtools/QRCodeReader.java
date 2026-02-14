package com.qrtools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QRCodeReader {
    private static Rectangle selectedArea;
    private static List<BufferedImage> screenImages = new ArrayList<>();
    private static List<JFrame> frames = new ArrayList<>();

    public static void main(String[] args) {
        startQRCodeRecognition();
    }

    public static void startQRCodeRecognition() {
        // 重置静态变量
        selectedArea = null;
        screenImages.clear();
        frames.clear();

        try {
            // 获取所有屏幕设备
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gd = ge.getScreenDevices();

            // 捕获所有屏幕的图像
            Robot robot = new Robot();
            for (GraphicsDevice device : gd) {
                Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
                BufferedImage screenImage = robot.createScreenCapture(screenBounds);
                screenImages.add(screenImage);
            }

            // 为每个屏幕创建全屏半透明窗口
            for (GraphicsDevice device : gd) {
                Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
                createScreenFrame(robot, screenBounds);
            }

        } catch (AWTException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法捕获屏幕: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createScreenFrame(Robot robot, Rectangle screenBounds) {
        final JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setBounds(screenBounds);
        frame.setAlwaysOnTop(true);
        frame.setBackground(new Color(0, 0, 0, 30));

        // 设置窗口背景为半透明
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // 绘制选定区域（相对于当前屏幕）
                if (selectedArea != null) {
                    // 计算选择区域在当前屏幕中的位置
                    Rectangle screenLocalArea = getLocalArea(selectedArea, screenBounds);
                    if (screenLocalArea != null) {
                        // 绘制选定区域的边框
                        g.setColor(Color.RED);
                        g.drawRect(screenLocalArea.x, screenLocalArea.y, screenLocalArea.width, screenLocalArea.height);
                        g.drawRect(screenLocalArea.x - 1, screenLocalArea.y - 1, screenLocalArea.width + 2, screenLocalArea.height + 2);
                    }
                }
            }
        };

        panel.setOpaque(false);
        frame.add(panel);
        frames.add(frame);

        // 鼠标事件处理
        final Point startPoint = new Point();
        final Point endPoint = new Point();

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint.setLocation(e.getPoint());
                selectedArea = null;
                repaintAllFrames();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint.setLocation(e.getPoint());
                // 计算选定区域（全局坐标）
                int x = Math.min(startPoint.x, endPoint.x) + screenBounds.x;
                int y = Math.min(startPoint.y, endPoint.y) + screenBounds.y;
                int width = Math.abs(endPoint.x - startPoint.x);
                int height = Math.abs(endPoint.y - startPoint.y);
                selectedArea = new Rectangle(x, y, width, height);

                // 关闭所有窗口
                disposeAllFrames();

                // 处理选定区域
                if (width > 0 && height > 0) {
                    try {
                        // 找到对应的屏幕图像
                        BufferedImage selectedImage = getSelectedImage(selectedArea);
                        if (selectedImage != null) {
                            // 识别二维码
                            String result = QRCodeDecoder.decode(selectedImage);
                            if (result != null) {
                                // 显示结果并复制到剪贴板
                                showResult(result);
                            } else {
                                JOptionPane.showMessageDialog(null, "未识别到二维码", "提示", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "处理图像时出错: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint.setLocation(e.getPoint());
                // 计算临时选定区域（全局坐标）
                int x = Math.min(startPoint.x, endPoint.x) + screenBounds.x;
                int y = Math.min(startPoint.y, endPoint.y) + screenBounds.y;
                int width = Math.abs(endPoint.x - startPoint.x);
                int height = Math.abs(endPoint.y - startPoint.y);
                selectedArea = new Rectangle(x, y, width, height);
                repaintAllFrames();
            }
        });

        frame.setVisible(true);
    }

    private static Rectangle getLocalArea(Rectangle globalArea, Rectangle screenBounds) {
        // 计算全局区域在当前屏幕中的局部区域
        int localX = globalArea.x - screenBounds.x;
        int localY = globalArea.y - screenBounds.y;
        
        // 检查是否在当前屏幕范围内
        if (localX + globalArea.width <= 0 || localX >= screenBounds.width ||
            localY + globalArea.height <= 0 || localY >= screenBounds.height) {
            return null;
        }
        
        return new Rectangle(localX, localY, globalArea.width, globalArea.height);
    }

    private static BufferedImage getSelectedImage(Rectangle selectedArea) {
        // 找到选择区域所在的屏幕并返回对应的图像
        for (int i = 0; i < screenImages.size(); i++) {
            BufferedImage screenImage = screenImages.get(i);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gd = ge.getScreenDevices();
            Rectangle screenBounds = gd[i].getDefaultConfiguration().getBounds();
            
            // 检查选择区域是否在当前屏幕内
            int localX = selectedArea.x - screenBounds.x;
            int localY = selectedArea.y - screenBounds.y;
            
            if (localX >= 0 && localY >= 0 && 
                localX + selectedArea.width <= screenBounds.width && 
                localY + selectedArea.height <= screenBounds.height) {
                return screenImage.getSubimage(localX, localY, selectedArea.width, selectedArea.height);
            }
        }
        return null;
    }

    private static void repaintAllFrames() {
        for (JFrame frame : frames) {
            frame.repaint();
        }
    }

    private static void disposeAllFrames() {
        for (JFrame frame : frames) {
            frame.dispose();
        }
    }

    private static void showResult(String result) {
        // 显示结果对话框
        JOptionPane.showMessageDialog(null, result, "二维码内容", JOptionPane.INFORMATION_MESSAGE);

        // 复制到剪贴板
        StringSelection stringSelection = new StringSelection(result);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }
}