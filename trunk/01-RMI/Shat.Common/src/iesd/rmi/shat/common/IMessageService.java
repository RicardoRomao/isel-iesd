package iesd.rmi.shat.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMessageService extends Remote {
	public void RegisterMailbox(IMailbox mailBox) throws RemoteException;
	public void UnRegisterMailbox(IMailbox mailBox) throws RemoteException;
	public void MulticastMessage(String message, String langFrom) throws RemoteException;
	public ITranslator GetTranslator(String langFrom, String langTo) throws RemoteException;
	public List<IMailbox> GetClientsWithTranslator(String langFrom, String langTo) throws RemoteException;
}