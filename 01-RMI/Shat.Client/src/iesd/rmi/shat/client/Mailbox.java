package iesd.rmi.shat.client;

import iesd.rmi.shat.common.IMailBoxListener;
import iesd.rmi.shat.common.IMailbox;
import iesd.rmi.shat.common.ITranslator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Mailbox extends UnicastRemoteObject implements IMailbox {
	private static final long serialVersionUID = 1L;
	private String _language = "PT";
	private String _name = "Guest";
	private List<IMailBoxListener> _listeners;

	public Mailbox(String name, String language) throws RemoteException {
		super();
		_name = name;
		_language = language;
	}

	@Override
	public void ReceiveMessage(String message, String langFrom) throws RemoteException {
		for (IMailBoxListener l : _listeners) {
			l.YouGotMail(message, langFrom);
		}
	}

	@Override
	public String GetLanguage() throws RemoteException { return _language; }
	
	@Override
	public String GetName() throws RemoteException { return _name; }
	
	@Override
	public void AddListener(IMailBoxListener listener) throws RemoteException {
		synchronized (listener) {
			if (_listeners == null)
				_listeners = new LinkedList<IMailBoxListener>();
			_listeners.add(listener);
		}
	}

	@Override
	public ITranslator GetTranslator(final String langFrom) throws RemoteException {
		if (_language.equals(langFrom)) { // Only if this mailbox speaks the source language
			return new ITranslator() {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public String Translate(String text, String from, String to) {
					Translate.setHttpReferrer("www.isel.pt");
					try {
						return Translate.execute("*" + text, Language.fromString(from), Language.fromString(to));
					} catch (Exception e) {
						e.printStackTrace();
						return "Something went wrong...";
					}
				}
				
				@Override
				public String SourceLang() { return langFrom; }
			};
		}
		return null;
	}
}