package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

// interface remota para ChatClient que define os métodos que o servidor pode chamar
// e que o cliente deve implementar
public interface IChatClient extends Remote {
  public void messageFromServer(String message) throws RemoteException;
  public void updateUserList(String[] currentUsers) throws RemoteException;
}