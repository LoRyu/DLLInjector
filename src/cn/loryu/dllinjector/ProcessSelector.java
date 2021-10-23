package cn.loryu.dllinjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessSelector extends JFrame {
    List<ReButton> reButtonList = new ArrayList<>();

    JPanel jp = new JPanel();
    JScrollPane jspg = new JScrollPane(jp, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    public ProcessSelector() {
        this.setTitle("Select a process");
//        this.setSize(400, 500);
        this.setBounds(((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width) - Frame.instance.getX() - 400 > 400 ? Frame.instance.getX() + Frame.instance.getWidth() : Frame.instance.getX() - 400, Frame.instance.getY() < (400 / 2) ? Frame.instance.getY() : Frame.instance.getY() - (400 / 2), 400, 500);
//        setLocationRelativeTo(null);
        setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Frame.instance.processSelector = null;
            }
        });
        this.jp.setLayout(null);
        jp.setBackground(Frame.MAIN_GREY);

        int y = 1;
        for (Process p : Main.processList) {
            ReButton reButton = new ReButton(p.getName() + p.getId(), p);
            reButtonList.add(reButton);
            reButton.setBounds(10, y, 300, 25);
            reButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            reButton.setForeground(Frame.TEXT_WHITE);
            reButton.setBackgroundColor(Frame.MAIN_GREY);
            reButton.setButtonBackgroundColor(Frame.PRESS);
            reButton.setSelectedBackgroundColor(Frame.SELECTED);
            reButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    reButton.setBackgroundColor(Frame.FOCUS);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    reButton.setBackgroundColor(Frame.MAIN_GREY);
                }
            });
            reButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (p.isSelected()) {
                        Frame.instance.processInfo.setText("Process:");
                        Frame.instance.process = null;
                    }else {
                        Frame.instance.processInfo.setText("Process:   Name: " + p.getName() + "PID: " + p.getId());
                        Frame.instance.process = p;
                    }
                    p.setSelected(!p.isSelected());
                    reButton.setSelected(p.isSelected());
                    for (ReButton r : reButtonList) {
                        if (r != e.getSource()) {
                            r.process.setSelected(false);
                            r.setSelected(r.process.isSelected());
                        }
                    }
                }
            });
            jp.add(reButton);
            y += 25;
        }

        this.jp.setPreferredSize(new Dimension(800, y));
        this.add(jspg);

        repaint();
        this.setVisible(true);
    }
}
