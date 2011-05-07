package iesd.rmi.shat.server;

import iesd.rmi.shat.common.IMailbox;
import iesd.rmi.shat.common.IMessageService;
import iesd.rmi.shat.common.ITranslator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MessageService extends UnicastRemoteObject implements
		IMessageService {
	
	private static final long serialVersionUID = 1L;
	private List<IMailbox> mailboxes;

	public MessageService() throws RemoteException {
		super();
		mailboxes = new LinkedList<IMailbox>();
	}

	@Override
	public void RegisterMailbox(IMailbox mailBox) throws RemoteException {
		synchronized (mailboxes) {
			mailboxes.add(mailBox);
		}
	}

	@Override
	public void UnRegisterMailbox(IMailbox mailBox) throws RemoteException {
		synchronized (mailboxes) {
			mailboxes.remove(mailBox);
		}
	}

	@Override
	public void MulticastMessage(String message, String langFrom)
			throws RemoteException {
		List<IMailbox> removeList = new LinkedList<IMailbox>();
		for (IMailbox mb : mailboxes) {
			try {
				mb.ReceiveMessage("[" + mb.GetName() + "] " + message, langFrom);
			} catch (Exception e) {
				removeList.add(mb);
			}
		}
		for (IMailbox trashed : removeList) {
			mailboxes.remove(trashed);
		}
	}

	@Override
	public ITranslator GetTranslator(String langFrom, String langTo)
			throws RemoteException {
		if ((langFrom.equals("en") && langTo.equals("pt"))
				|| (langFrom.equals("pt") && langTo.equals("en")))
			return new Translator();
		return null;
	}

	@Override
	public List<IMailbox> GetClientsWithTranslator(String langFrom,
			String langTo) throws RemoteException {
		Iterator<IMailbox> iter = mailboxes.listIterator();
		List<IMailbox> mbs;
		IMailbox mb;

		mbs = new LinkedList<IMailbox>();
		while (iter.hasNext()) {
			mb = iter.next();
			if (mb.HasTranslator(langFrom, langTo))
				mbs.add(mb);
		}
		return mbs;
	}
}