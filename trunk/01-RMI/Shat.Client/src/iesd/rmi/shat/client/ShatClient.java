package iesd.rmi.shat.client;

import iesd.rmi.shat.common.IMailbox;
import iesd.rmi.shat.common.IMessageService;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ShatClient {
	private static final String host = "localhost";

	public static void main(String[] args) {
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		try {
			Registry registry = LocateRegistry.getRegistry(host);
			IMessageService stub = (IMessageService)registry.lookup("MessageService");

			IMailbox mb = new Mailbox(System.getenv("Name"), System.getenv("Language"));
			stub.RegisterMailbox(mb);

			System.out.println("::.. Aplicação CHAT cliente ..::");

			String msg = null;
			Scanner input = new Scanner(System.in);
			while (!(msg = input.nextLine()).equalsIgnoreCase("quit")) {
				try {
					stub.MulticastMessage(msg, mb.GetLanguage());
				} catch (Exception e) {
					System.out.println("Server unavailable.");
				}
			}

			try {
				stub.UnRegisterMailbox(mb);
			} catch (Exception e) { }
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}