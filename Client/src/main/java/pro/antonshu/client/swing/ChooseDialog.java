package pro.antonshu.client.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChooseDialog extends JDialog {

    private JButton btnLogin;
    private JButton btnRegister;
    private boolean isRegistered;

    public ChooseDialog(Frame owner) {
        super(owner, "Выберите действие", true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        btnLogin = new JButton("Войти");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRegistered=true;
                dispose();
            }
        });

        btnRegister = new JButton("Зарегистрироваться");
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            isRegistered=false;
            dispose();
            }
        });

        panel.add(btnLogin);
        panel.add(btnRegister);

        getContentPane().add(panel, BorderLayout.CENTER);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                WindowEvent windowClosing = new WindowEvent(owner, WindowEvent.WINDOW_CLOSING);
                owner.dispatchEvent(windowClosing);
            }
        });

    }

    public boolean getIsRegistered() {
        return isRegistered;
    }
}
