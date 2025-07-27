package server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;
import client.IChatClient;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private static final long serialVersionUID = 1L; // É necessário porque o servidor é serializado para comunicação RMI.
    private Vector<ConnectedClient> connectedClients; // Lista de clientes conectados

    public ChatServer() throws RemoteException {
        super();
        connectedClients = new Vector<>(10, 1); // Inicializa a lista de clientes conectados com capacidade inicial de 10 e fator de carga de 1
    }

    public static void main(String[] args) {
        String host = "192.168.4.7";
        String serviceName = "GroupChatService";

        if (args.length == 2) {
            host = args[0];
            serviceName = args[1];
        }

        // Configura o hostname do servidor RMI
        System.setProperty("java.rmi.server.hostname", host);
        startRegistry();

        try {
            // Cria e registra o servidor RMI
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
            // Tenta iniciar o RMI Registry na porta padrão 1099
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry iniciado na porta 1099.");
        } catch (RemoteException e) {
            System.out.println("RMI Registry já estava em execução.");
        }
    }

    @Override
    public void updateChat(String username, String chatMessage) throws RemoteException {
        String text = username + " : " + chatMessage + "\n";
        broadcast(text); // Envia a mensagem para todos os clientes conectados
    }

    @Override
    public void registerListener(IChatClient client, String username) throws RemoteException {
        connectedClients.add(new ConnectedClient(username, client)); // Adiciona o novo cliente à lista de clientes conectados
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
        broadcast("[Servidor]: " + username + " saiu da sala.\n"); // Notifica todos os clientes sobre a saída
        updateUserLists();
    }

    @Override // Envia uma mensagem privada para um grupo de clientes especificado
    public void privateDM(int[] privateGroup, String privateMessage) throws RemoteException {
        for (int index : privateGroup) { // Envia a mensagem privada para os clientes especificados
            ConnectedClient cc = connectedClients.get(index);
            cc.getClient().messageFromServer(privateMessage); // Envia a mensagem privada para o cliente
        }
    }

    // Envia uma mensagem para todos os clientes conectados
    private void broadcast(String message) {
        for (ConnectedClient c : connectedClients) {
            try {
                c.getClient().messageFromServer(message); // Envia a mensagem para o cliente
            } catch (RemoteException e) {
                System.err.println("Erro ao enviar para " + c.getName() + ": " + e.getMessage());
            }
        }
    }

    // Atualiza a lista de usuários conectados e envia para todos os clientes
    private void updateUserLists() {
        String[] users = connectedClients.stream() // Cria um array de nomes de usuários conectados
                            .map(ConnectedClient::getName) // Mapeia cada ConnectedClient para seu nome
                            .toArray(String[]::new); // Converte a lista de nomes em um array
        for (ConnectedClient c : connectedClients) {
            try {
                c.getClient().updateUserList(users); // Envia a lista de usuários conectados para o cliente
            } catch (RemoteException e) {
                System.err.println("Erro ao atualizar lista para " + c.getName() + ": " + e.getMessage());
            }
        }
    }
}