package pro.antonshu.client.swing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pro.antonshu.client.net.NettyBFClient;
import pro.antonshu.services.bytebuf.ByteBufService;
import pro.antonshu.services.chunk.ChunkService;

import javax.net.ssl.SSLException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindow extends JFrame {
    private JPanel jointPanel;
    private JPanel serverPanel;
    private JPanel buttonsPanel;
    private JButton refreshButton;
    private JButton downloadButton;
    private JButton sendButton;
    private JList serverFileList;
    private JPanel clientPanel;
    private JTextField clientTextArea1;
    private JPanel clientButtonsPanel;
    private JList clientFileList;
    private JButton clientRefreshButton;
    private JButton clientDownloadButton;
    private JButton clientSendButton;
    private JPanel serverNorthPanel;
    private JLabel frameName;
    private JTextField severTextField;
    private JPanel clientNorthPanel;
    private JLabel clientLabel;
    private JTextField clientTextField;

    private DefaultListModel<String> serverFileListModel;
    private DefaultListModel<String> clientFileListModel;

    private String rootPath;
    private String serverRootPath;
    private String user;
    private NettyBFClient nettyBootstrapClient;
    private ChunkService chunkService;
    private static final Logger logger = LogManager.getLogger(MainWindow.class);

    JPanel getMainPanel() {
        return jointPanel;
    }

    public String getUser() {
        return user;
    }

    /*
     * Логин: ivan. Пароль: 123.
     */

    public MainWindow() throws IOException {

        clientRefreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ByteBuf buf = Unpooled.copiedBuffer(ByteBufService.prepareSendData("req_file_list", user, null));
                nettyBootstrapClient.getChannel().writeAndFlush(buf);
            }
        });


        clientDownloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = clientTextField.getText();
                if (text != null && !text.trim().isEmpty()) {
                    ByteBuf buf = Unpooled.copiedBuffer(ByteBufService.prepareSendData("get_fileName", user, text.getBytes()));
                    nettyBootstrapClient.getChannel().writeAndFlush(buf);
                } else {
                    showMessage("Имя файла пустое. Введите новое имя");
                }
            }
        });


        clientSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = clientTextField.getText();
//                String input = (String) clientFileList.getSelectedValue();
                Path path = Paths.get(rootPath + File.separator + text);
                if (text != null && Files.exists(path) && !text.trim().isEmpty()) {

                    try {
                        ByteBuf buf = Unpooled.copiedBuffer(ByteBufService.prepareSendData("fileName", user, text.getBytes()));
                        nettyBootstrapClient.getChannel().writeAndFlush(buf);

                        chunkService.sendFile(nettyBootstrapClient.getChannel(), path.toString());
                    } catch (IOException ex) {
                        logger.error(user + "error: ", ex);
                        ex.printStackTrace();
                    }
                } else {
                    showMessage("Файла не существует: " + path);
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path pathToFind = Paths.get(serverRootPath + File.separator + user);
                if (!pathToFind.toFile().exists()) {
                    pathToFind.toFile().mkdir();
                }

                try {
                    Files.walkFileTree(pathToFind, new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            serverFileListModel.addElement(file.getFileName().toString());
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


        this.setTitle(String.format("Облачное хранилище. Пользователь %s", user));
        rootPath = "Client/ClientStorage/";
        if (!Files.exists(Paths.get(rootPath))) {
            Files.createDirectory(Paths.get(rootPath));
        }
        serverRootPath = "Server/ServerStorage/";
        if (!Files.exists(Paths.get(serverRootPath))) {
            Files.createDirectory(Paths.get(serverRootPath));
        }

        ChooseDialog choose = new ChooseDialog(this);
        choose.setVisible(true);

        if (!choose.getIsRegistered()) {
            RegisterDialog regDialog = new RegisterDialog(this);
            regDialog.setVisible(true);

            if (!regDialog.isRegistered()) {
                System.exit(0);
            } else {
                user = regDialog.getUser();
            }
        } else {
            LoginDialog loginDialog = new LoginDialog(this);
            loginDialog.setVisible(true);

            if (!loginDialog.isAuthorized()) {
                System.exit(0);
            } else {
                user = loginDialog.getUser();
            }
        }
    }

    private void createUIComponents() {
        serverFileListModel = new DefaultListModel<>();
        clientFileListModel = new DefaultListModel<>();
        serverFileList = new JList();
        clientFileList = new JList();
        serverFileList.setModel(serverFileListModel);
        clientFileList.setModel(clientFileListModel);

        nettyBootstrapClient = new NettyBFClient();
        chunkService = new ChunkService();

        ExecutorService netService = Executors.newCachedThreadPool();
        netService.submit(() -> {
            try {
                nettyBootstrapClient.run(clientFileListModel);
            } catch (InterruptedException | SSLException e) {
                e.printStackTrace();
            }
        });
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(MainWindow.this,
                message,
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
    }
}
