package server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;
import client.IChatClient;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private static final long serialVersionUID = 1L;
    private Vector<ConnectedClient> connectedClients;

    public ChatServer() throws RemoteException {
        super();
        connectedClients = new Vector<>(10, 1);
    }

    public static void main(String[] args) {
        String host = "192.168.4.7";
        String serviceName = "GroupChatService";

        if (args.length == 2) {
            host = args[0];
            serviceName = args[1];
        }

        System.setProperty("java.rmi.server.hostname", host);
        startRegistry();

        try {
            IChatServer server = new ChatServer();
            Naming.rebind("rmi://" + host + "/" + serviceName, server);
            System.out.println("Servidor RMI aguardando conexoes...");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startRegistry() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry iniciado na porta 1099.");
        } catch (RemoteException e) {
            System.out.println("RMI Registry já estava em execução.");
        }
    }

    @Override
    public void updateChat(String username, String chatMessage) throws RemoteException {
        String text = username + " : " + chatMessage + "\n";
        broadcast(text);
    }

    @Override
    public void registerListener(IChatClient client, String username) throws RemoteException {
        // Registro direto do stub do cliente
        connectedClients.add(new ConnectedClient(username, client));
        System.out.println(new Date() + " - " + username + " conectado.");

        // Envia mensagem de boas-vindas apenas ao novo cliente
        client.messageFromServer("[Servidor]: Bem-vindo " + username + "! Você pode conversar agora.\n");

        // Notifica todos os clientes
        broadcast("[Servidor]: " + username + " entrou no grupo.\n");
        updateUserLists();
    }

    @Override
    public void quitChat(String username) throws RemoteException {
        connectedClients.removeIf(c -> c.getName().equals(username));
        broadcast("[Servidor]: " + username + " saiu da sala.\n");
        updateUserLists();
    }

    @Override
    public void privateDM(int[] privateGroup, String privateMessage) throws RemoteException {
        for (int index : privateGroup) {
            ConnectedClient cc = connectedClients.get(index);
            cc.getClient().messageFromServer(privateMessage);
        }
    }

    private void broadcast(String message) {
        for (ConnectedClient c : connectedClients) {
            try {
                c.getClient().messageFromServer(message);
            } catch (RemoteException e) {
                System.err.println("Erro ao enviar para " + c.getName() + ": " + e.getMessage());
            }
        }
    }

    private void updateUserLists() {
        String[] users = connectedClients.stream()
                            .map(ConnectedClient::getName)
                            .toArray(String[]::new);
        for (ConnectedClient c : connectedClients) {
            try {
                c.getClient().updateUserList(users);
            } catch (RemoteException e) {
                System.err.println("Erro ao atualizar lista para " + c.getName() + ": " + e.getMessage());
            }
        }
    }
}