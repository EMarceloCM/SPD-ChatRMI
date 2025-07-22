package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;
import client.IChatClient;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private static final long serialVersionUID = 1L; // garante que ao desserializar o objeto, a versão do serializado é compatível com a versão do código atual
    private Vector<ConnectedClient> connectedClients;

    public ChatServer() throws RemoteException {
        super();
		// inicializa a lista de clientes conectados com capacidade inicial de 10 e fator de carga de 1
        connectedClients = new Vector<>(10, 1);
    }

    public static void main(String[] args) {
        startRegistryRMI();  
        String host = "localhost";
        String service = "GroupChatService";

        if (args.length == 2) {
            host = args[0];
            service = args[1];
        }

        try {
            IChatServer server = new ChatServer(); 					// cria uma instância do servidor de chat
            Naming.rebind("rmi://" + host + "/" + service, server); // registra o servidor no registry RMI
            System.out.println("servirdor RMI aguardando conexoes...");
        } catch (Exception e) {
            System.out.println("Erro! Problema ao iniciar o servidor.");
            e.printStackTrace();
        }
    }

    private static void startRegistryRMI() {
        try {
            // Cria um registry RMI na porta padrão 1099
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("Registro RMI iniciado com sucesso.");
        } catch (RemoteException e) {
            System.out.println("O registro RMI já esta em execucao ou ocorreu erro ao iniciar.");
        }
    }

    public String sayHi(String clientName) throws RemoteException {
        System.out.println(clientName + " enviou uma mensagem de saudacao.");
        return "Bem-vindo " + clientName + " a sala de chat!\n";
    }

    @Override
    public void updateChat(String name, String mensage) throws RemoteException { // recebe a mensagem do cliente e envia para todos os demais
        String text = name + " : " + mensage + "\n";
        sendToAll(text);
    }

    @Override
    public void passIdentity(RemoteRef ref) throws RemoteException { // recebe a referência do cliente
        System.out.println("Referencia recebida: " + ref.toString());
    }

    @Override
    public void registerListener(String[] details) throws RemoteException {
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println("\n" + details[0] + " entrou na sala de chat");
        System.out.println("Host de " + details[0] + ": " + details[1]);
        System.out.println("RMI de " + details[0] + ": " + details[2]);
        registerClient(details);
    }

    private void registerClient(String[] details) {
        try {
            IChatClient newClient = (IChatClient) Naming.lookup("rmi://" + details[1] + "/" + details[2]); // procura o cliente no registro RMI

            connectedClients.addElement(new ConnectedClient(details[0], newClient)); // adiciona o cliente à lista de clientes conectados

            // Saudação ao novo cliente
            newClient.messageFromServer(
              "[Servidor]: Bem-vindo " + details[0] + "! Voce pode conversar agora.\n");

            // Notifica todos os clientes
            sendToAll("[Servidor]: " + details[0] + " entrou no grupo.\n");

            updateUserList();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            System.out.println("Erro ao registrar cliente: " + details[0]);
            e.printStackTrace();
        }
    }

    private void updateUserList() {
        String[] users = getUserList();
        for (ConnectedClient c : connectedClients) {
            try {
                c.getClient().updateUserList(users); // envia a lista de usuários conectados para cada cliente
            } catch (RemoteException e) {
                System.out.println("Erro ao atualizar lista de usuarios para: " + c.getName() + "\n");
                e.printStackTrace();
            }
        }
    }

    private String[] getUserList() {
        String[] everybody = new String[connectedClients.size()]; // cria um array do tamanho da lista de clientes conectados
        for (int i = 0; i < everybody.length; i++) {
            everybody[i] = connectedClients.get(i).getName(); // preenche o array com os nomes dos clientes conectados
        }
        return everybody;
    }

    private void sendToAll(String text) throws RemoteException {
        for (ConnectedClient c : connectedClients) {
            c.getClient().messageFromServer(text);
        }
    }

    @Override
    public void quitChat(String user) throws RemoteException {
        for (ConnectedClient c : connectedClients) {
            if (c.getName().equals(user)) {
                System.out.println(user + " saiu da sala de chat.\n");
                System.out.println(new Date(System.currentTimeMillis()));
                connectedClients.remove(c);
                break;
            }
        }
        if (!connectedClients.isEmpty()) {
            updateUserList();
        }
    }

    @Override
    public void privateDM(int[] privateGroup, String privateMessage) throws RemoteException {
        for (int index : privateGroup) { // todos os clientes marcados para receber a mensagem privada
            ConnectedClient cc = connectedClients.get(index);
            cc.getClient().messageFromServer(privateMessage);
        }
    }
}