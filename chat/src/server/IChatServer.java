package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import client.IChatClient;

// Interface remota para ChatServer
// Define os métodos que o servidor de chat deve implementar
public interface IChatServer extends Remote {
    void updateChat(String username, String chatMessage) throws RemoteException;
    void registerListener(IChatClient client, String username) throws RemoteException;
    void quitChat(String username) throws RemoteException;
    void privateDM(int[] privateGroup, String privateMessage) throws RemoteException;
}