package iesd.rmi.shat.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMailbox extends Remote {
	public String GetLanguage() throws RemoteException;
	public String GetName() throws RemoteException;
	public void ReceiveMessage(String message, String langFrom) throws RemoteException;
	public void AddListener(IMailBoxListener listener) throws RemoteException;
	public boolean HasTranslator(String langFrom, String langTo) throws RemoteException;
	public ITranslator GetTranslator(String langFrom, String langTo) throws RemoteException;
	
}