package com.tools;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qrtools.QRCodeReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("二维码工具");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 350);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel titleLabel = new JLabel("二维码工具", JLabel.CENTER);
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            JLabel descriptionLabel = new JLabel("<html><div style='text-align: center;'>选择下方功能开始使用<br>支持多屏幕选择区域</div></html>", JLabel.CENTER);
            descriptionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            mainPanel.add(descriptionLabel, BorderLayout.CENTER);

            JButton recognizeButton = new JButton("识别二维码");
            recognizeButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            recognizeButton.setPreferredSize(new Dimension(200, 50));
            recognizeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    QRCodeReader.startQRCodeRecognition();
                }
            });

            JButton generateButton = new JButton("生成二维码");
            generateButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            generateButton.setPreferredSize(new Dimension(200, 50));
            generateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showQRCodeGeneratorDialog();
                }
            });

            JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            buttonPanel.add(recognizeButton);
            buttonPanel.add(generateButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }

    private static void showQRCodeGeneratorDialog() {
        JDialog dialog = new JDialog((Frame) null, "生成二维码", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("输入要生成二维码的内容", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.5);

        JPanel leftPanel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel leftLabel = new JLabel("输入内容", JLabel.CENTER);
        leftLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        leftPanel.add(leftLabel, BorderLayout.NORTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel qrCodeLabel = new JLabel("", JLabel.CENTER);
        qrCodeLabel.setPreferredSize(new Dimension(300, 300));
        rightPanel.add(qrCodeLabel, BorderLayout.CENTER);

        JLabel rightLabel = new JLabel("二维码预览", JLabel.CENTER);
        rightLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        rightPanel.add(rightLabel, BorderLayout.NORTH);

        JButton saveButton = new JButton("保存二维码");
        saveButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (qrCodeLabel.getIcon() != null) {
                    saveQRCodeImage(qrCodeLabel);
                } else {
                    JOptionPane.showMessageDialog(dialog, "请先生成二维码", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        rightPanel.add(saveButton, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("关闭");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateQRCode(textArea, qrCodeLabel);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateQRCode(textArea, qrCodeLabel);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateQRCode(textArea, qrCodeLabel);
            }
        });

        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                updateQRCode(textArea, qrCodeLabel);
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateQRCode(textArea, qrCodeLabel);
            }
        });

        dialog.setVisible(true);
    }

    private static void updateQRCode(JTextArea textArea, JLabel qrCodeLabel) {
        String content = textArea.getText().trim();
        if (content.isEmpty()) {
            qrCodeLabel.setIcon(null);
            qrCodeLabel.setText("请输入内容");
            return;
        }

        try {
            BufferedImage qrCodeImage = generateQRCode(content);
            qrCodeLabel.setIcon(new ImageIcon(qrCodeImage));
            qrCodeLabel.setText("");
        } catch (WriterException ex) {
            qrCodeLabel.setIcon(null);
            qrCodeLabel.setText("生成失败");
        }
    }

    private static BufferedImage generateQRCode(String content) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private static void saveQRCodeImage(JLabel qrCodeLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存二维码");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG图片 (*.png)", "png"));
        fileChooser.setSelectedFile(new File("qrcode.png"));

        int userSelection = fileChooser.showSaveDialog(qrCodeLabel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }

            ImageIcon icon = (ImageIcon) qrCodeLabel.getIcon();
            BufferedImage image = (BufferedImage) icon.getImage();

            try {
                ImageIO.write(image, "PNG", fileToSave);
                JOptionPane.showMessageDialog(qrCodeLabel, "二维码保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(qrCodeLabel, "保存失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}