package pro.antonshu.client.swing;

import pro.antonshu.services.auth.AuthService;
import pro.antonshu.services.auth.impl.AuthServiceImpl;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RegisterDialog extends JDialog {

    private AuthService authService;
    private JTextField tfUsername;
    private JPasswordField tfPassword;
    private JPasswordField tfPasswordConfirm;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JLabel lbPasswordConfirm;
    private JButton btnRegister;
    private JButton btnCancel;
    private boolean registered;
    private String user;

    public RegisterDialog(Frame parent) {
        super(parent, "Регистрация", true);
        this.authService = new AuthServiceImpl();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        lbUsername = new JLabel("Имя пользователя: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        lbPassword = new JLabel("Пароль: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        tfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(tfPassword, cs);

        lbPasswordConfirm = new JLabel("Подтвердите пароль: ");
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(lbPasswordConfirm, cs);

        tfPasswordConfirm = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(tfPasswordConfirm, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        btnRegister = new JButton("Зарегистрироваться");
        btnCancel = new JButton("Отмена");

        JPanel bp = new JPanel();
        bp.add(btnRegister);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (passFieldsEquals()) {
                    if (authService.regNewUser(lbUsername.getText(), String.valueOf(tfPassword.getPassword()))) {
                        registered = true;
                        user = lbUsername.getText();
                        showMessage(true,"Вы успешно зарегистрированы. Закройте окно, чтобы продолжить.");
                    } else {
                        showMessage(false, "Ошибка регистрации. Попробуйте позже");
                        return;
                    }
                } else {
                    showMessage(false, "Пароли не совпадают. Проверьте соответсвие паролей.");
                    return;
                 }
                dispose();
            }
        });

        bp.add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registered = false;
                dispose();
            }
        });

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void showMessage(Boolean OK, String message) {
        int type;
        if(OK) {
            type = 1;
        } else {
            type = 0;
        }
        JOptionPane.showMessageDialog(RegisterDialog.this,
                message,
                "Регистрация",
                type);
    }

    private boolean passFieldsEquals() {
        return String.valueOf(tfPassword.getPassword()).equals(String.valueOf(tfPasswordConfirm.getPassword()));
    }
    public boolean isRegistered() {
        return registered;
    }

    public String getUser() {
        return user;
    }
}
