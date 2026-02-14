package com.tools;

import com.qrtools.QRCodeReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("二维码识别工具");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel titleLabel = new JLabel("二维码识别工具", JLabel.CENTER);
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            JLabel descriptionLabel = new JLabel("<html><div style='text-align: center;'>点击下方按钮开始识别二维码<br>支持多屏幕选择区域</div></html>", JLabel.CENTER);
            descriptionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            mainPanel.add(descriptionLabel, BorderLayout.CENTER);

            JButton startButton = new JButton("开始识别二维码");
            startButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            startButton.setPreferredSize(new Dimension(200, 50));
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    QRCodeReader.startQRCodeRecognition();
                }
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(startButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}