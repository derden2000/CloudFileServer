package pro.antonshu.client.swing;

import javax.swing.*;
import java.io.IOException;

public class SwingApp {

    public static void main(String[] args) {
        try {
            MainWindow mainWindow = new MainWindow();
            JFrame frame = new JFrame();
            frame.setContentPane(mainWindow.getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBounds(200,200, 570, 500);
            frame.setTitle(String.format("Облачное хранилище. Пользователь %s", mainWindow.getUser()));
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
