package cn.loryu.dllinjector;

import javax.swing.*;
import java.awt.*;

public class ReButton extends JButton {
    Process process;
    Color backgroundColor;
    Color color;
    Color selectedColor;

    public ReButton(String name, Process process) {
        this.process = process;
        this.setText(name);
        setFont(new Font("system", Font.PLAIN, 12));
        setBorderPainted(false);
        setForeground(backgroundColor);
        setFocusPainted(false);
        setContentAreaFilled(false);
    }

    public ReButton(String name) {
        this.setText(name);
        setFont(new Font("system", Font.PLAIN, 12));
        setBorderPainted(false);
        setForeground(backgroundColor);
        setFocusPainted(false);
        setContentAreaFilled(false);
    }

    public ReButton(String name, Icon icon) {
        this.setText(name);
        this.setIcon(icon);
        setFont(new Font("system", Font.PLAIN, 12));
        setBorderPainted(false);
        setForeground(backgroundColor);
        setFocusPainted(false);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        if (getModel().isPressed()) {
            g.setColor(color);
        } else {
            g.setColor(backgroundColor);
        }

        if (getModel().isSelected()) {
            g.setColor(selectedColor);
        } else {
            g.setColor(backgroundColor);
        }
        g.fillRect(0, 0, getSize().width - 1, getSize().height - 1);

        super.paintComponent(g);
    }

    /**
     * 设置按钮背景色
     * @param backgroundColor 按钮背景色
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * 设置按钮默认背景色
     * @param color 按钮默认背景色
     */
    public void setButtonBackgroundColor(Color color) {
        this.color = color;
    }

    /**
     * 设置按钮选择颜色
     * @param selectedColor 按按钮选择颜色
     */
    public void setSelectedBackgroundColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

}
