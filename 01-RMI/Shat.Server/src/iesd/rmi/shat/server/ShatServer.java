package iesd.rmi.shat.server;

import iesd.rmi.shat.common.IMessageService;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ShatServer {
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		try {
			IMessageService proxy = new MessageService();

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("MessageService", proxy);
			System.out.println("Waiting for clients...");
			//registry.unbind("MessageService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}