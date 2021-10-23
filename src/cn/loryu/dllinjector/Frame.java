package cn.loryu.dllinjector;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class Frame extends JFrame {
    public static Frame instance = null;
    static Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32.dll", Kernel32.class, W32APIOptions.ASCII_OPTIONS);

    public static final Color FRAME_BACKGROUND = Color.decode("#1F2630");
    public static final Color MAIN_GREY = Color.decode("#262E36");
    public static final Color SELECTED = Color.decode("#006BD6");
    public static final Color PRESS = Color.decode("#0052BD");
    public static final Color FOCUS = Color.decode("#1A85F0");
    public static final Color UNSELECTED = Color.decode("#303842");
    public static final Color TEXT_WHITE = Color.decode("#D4D4D5");

    ArrayList<ReButton> buttonList = new ArrayList<>();

    Container container = this.getContentPane();

    ReButton injectButton = new ReButton("Inject");
    ReButton selectProcessButton = new ReButton("Select a process");
    ReButton selectDllFileButton = new ReButton("Select a DLL");
    JLabel processInfo = new JLabel("Process: ");
    JLabel dllFileInfo = new JLabel("DLL: ");

    Process process = null;
    String dllPath = "";
    String lastPath = System.getProperty("user.dir");
    ProcessSelector processSelector = null;

    public Frame() {
        setTitle("Dll Injector");
        setSize(420, 160);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setBackground(FRAME_BACKGROUND);
        instance = this;
    }

    public void init() {
        container.revalidate();
        container.setBackground(FRAME_BACKGROUND);

        buttonList.add(injectButton);
        buttonList.add(selectProcessButton);
        buttonList.add(selectDllFileButton);

        injectButton.setBounds(((400 - 100) / 2) + 155, ((150 - 20) / 2) + 10, 80, 30);
        selectProcessButton.setBounds(15, ((150 - 20) / 2) + 10, 130, 30);
        selectDllFileButton.setBounds(((400 - 100) / 2) + 10, ((150 - 20) / 2) + 10, 130, 30);
        processInfo.setBounds(15, ((150 - 20) / 2) - 60, this.getWidth(), 30);
        dllFileInfo.setBounds(15, ((150 - 20) / 2) - 30, this.getWidth(), 30);

        processInfo.setForeground(TEXT_WHITE);
        processInfo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        dllFileInfo.setForeground(TEXT_WHITE);
        dllFileInfo.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        for (ReButton r : buttonList) {
            r.setBackgroundColor(UNSELECTED);
            r.setButtonBackgroundColor(PRESS);
            r.setSelectedBackgroundColor(SELECTED);
            r.setForeground(TEXT_WHITE);
            r.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            r.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    r.setBackgroundColor(FOCUS);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    r.setBackgroundColor(FOCUS);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    r.setBackgroundColor(SELECTED);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    r.setBackgroundColor(UNSELECTED);
                }
            });
        }

        selectProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (processSelector == null) {
                    Main.processMap.clear();
                    Main.processNameList.clear();
                    Main.processList.clear();
                    Main.getAllProcess();
                    processSelector = new ProcessSelector();
                }
            }
        });

        selectDllFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fcDlg = new JFileChooser(lastPath);
                fcDlg.setDialogTitle("Select a dll");
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "DLL File(*.dll)", "dll");
                fcDlg.setFileFilter(filter);
                int returnVal = fcDlg.showOpenDialog(Frame.instance);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    dllPath = fcDlg.getSelectedFile().getPath();
                    lastPath = dllPath;
                    dllFileInfo.setText("DLL: " + dllPath);
                }
                ((ReButton)e.getSource()).setBackgroundColor(UNSELECTED);
            }
        });

        injectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (process != null) {
                    if (!dllPath.equals("") && new File(dllPath).exists()) {
                        String filename = new File(dllPath).getName();// 文件名
                        String[] strArray = filename.split("\\.");
                        int suffixIndex = strArray.length -1;
                        if (strArray[suffixIndex].equals("dll")) {
                            try {
                                boolean injectResult = injectDLL(Integer.parseInt(process.getId()), dllPath);
                                if(injectResult) {
                                    error("Injected successfully!", 1);
                                }else {
                                    error("Injected failed!", 0);
                                }
                            }catch (Exception e1) {
                                e1.printStackTrace();
                                error("Injected failed!", 0);
                            }
                        }else {
                            error("Please Select a DLL", 2);
                        }
                    }else {
                        error("Please Select a DLL", 2);
                    }
                }else {
                    error("Please Select a Process", 2);
                }
            }
        });

        container.add(injectButton);
        container.add(selectProcessButton);
        container.add(selectDllFileButton);
        container.add(processInfo);
        container.add(dllFileInfo);

        repaint();

        setVisible(true);
    }

    public static boolean injectDLL(int processID, String dllName) {
        BaseTSD.DWORD_PTR processAccess = new BaseTSD.DWORD_PTR(0x43A);

        WinNT.HANDLE hProcess = kernel32.OpenProcess(processAccess, new WinDef.BOOL(false), new BaseTSD.DWORD_PTR(processID));
        if(hProcess == null) {
            System.out.println("Handle was NULL! Error: " + kernel32.GetLastError());
            return false;
        }

        BaseTSD.DWORD_PTR loadLibraryAddress = kernel32.GetProcAddress(kernel32.GetModuleHandle("KERNEL32"), "LoadLibraryA");
        if(loadLibraryAddress.intValue() == 0) {
            System.out.println("Could not find LoadLibrary! Error: " + kernel32.GetLastError());
            return false;
        }

        WinDef.LPVOID dllNameAddress = kernel32.VirtualAllocEx(hProcess, null, (dllName.length() + 1), new BaseTSD.DWORD_PTR(0x3000), new BaseTSD.DWORD_PTR(0x4));
        if(dllNameAddress == null) {
            System.out.println("dllNameAddress was NULL! Error: " + kernel32.GetLastError());
            return false;
        }

        Pointer m = new Memory(dllName.length() + 1);
        m.setString(0, dllName);

        boolean wpmSuccess = kernel32.WriteProcessMemory(hProcess, dllNameAddress, m, dllName.length(), null).booleanValue();
        if(!wpmSuccess) {
            System.out.println("WriteProcessMemory failed! Error: " + kernel32.GetLastError());
            return false;
        }

        BaseTSD.DWORD_PTR threadHandle = kernel32.CreateRemoteThread(hProcess, 0, 0, loadLibraryAddress, dllNameAddress, 0, 0);
        if(threadHandle.intValue() == 0) {
            System.out.println("threadHandle was invalid! Error: " + kernel32.GetLastError());
            return false;
        }

        kernel32.CloseHandle(hProcess);

        return true;
    }

    public static void error(String message, int type) {
        Font messageFont = new Font("微软雅黑", Font.PLAIN, 12);
        Font dialogButtonFont = new Font("微软雅黑", Font.PLAIN, 12);
        UIManager.put("OptionPane.messageFont", messageFont);
        UIManager.put("OptionPane.buttonFont", dialogButtonFont);
        JOptionPane.showMessageDialog(Frame.instance, message, "Info", type);
    }

}
