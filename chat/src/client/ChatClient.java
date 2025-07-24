package client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JOptionPane;
import server.IChatServer;

public class ChatClient extends UnicastRemoteObject implements IChatClient {
    private static final long serialVersionUID = 1L;
    private ChatUI chatUI;
    private String serverHost = "192.168.4.7";
    private String serviceName = "GroupChatService";
    private String username;
    protected IChatServer server;
    protected boolean connectionProblem = false;

    public ChatClient(ChatUI chatUI, String username) throws RemoteException {
        super(); // exporta o stub do cliente
        this.chatUI = chatUI;
        this.username = username;
    }

    public void startClient() throws RemoteException {
        try {
            connectionProblem = false;

            // Configura o hostname para o IP do cliente
            System.setProperty("java.rmi.server.hostname", "192.168.3.200");

            // Conecta ao servidor RMI
            server = (IChatServer) Naming.lookup("rmi://" + serverHost + "/" + serviceName);

            // Registra este stub diretamente no servidor
            server.registerListener(this, username);

            System.out.println("Registrado no servidor com sucesso. Aguardando mensagens...");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                chatUI.frame,
                "Não foi possível conectar ao servidor de chat. Por favor, tente novamente mais tarde.",
                "Erro de conexão",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
            connectionProblem = true;
        }
    }

    @Override
    public void messageFromServer(String message) throws RemoteException {
        chatUI.textArea.append(message);
        chatUI.textArea.setCaretPosition(chatUI.textArea.getDocument().getLength());
    }

    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {
        chatUI.privateMsgButton.setEnabled(currentUsers.length > 1);
        chatUI.userPanel.remove(chatUI.clientPanel);
        chatUI.setClientPanel(currentUsers);
        chatUI.clientPanel.repaint();
        chatUI.clientPanel.revalidate();
    }
}