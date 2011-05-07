package iesd.rmi.shat.client;

import iesd.rmi.shat.common.IMailBoxListener;
import iesd.rmi.shat.common.IMailbox;
import iesd.rmi.shat.common.IMessageService;
import iesd.rmi.shat.common.ITranslator;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ShatAppletClient extends JApplet implements IMailBoxListener {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextArea jTextArea = null;
	private JPanel jPanel = null;
	private JTextField jTextField = null;
	private JButton jButton = null;
	private static final String host = "localhost"; // @jve:decl-index=0:
	private IMessageService stub; // @jve:decl-index=0:
	private IMailbox mb;  //  @jve:decl-index=0:
	private ITranslator translator;

	public ShatAppletClient() {
		super();
	}j

	public void init() {
		this.setSize(300, 200);
		this.setLocation(100, 100);
		this.setContentPane(getJContentPane());
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			stub = (IMessageService) registry.lookup("MessageService");
			mb = new Mailbox(getParameter("Name"), getParameter("Language"));
			mb.AddListener(this);
			stub.RegisterMailbox(mb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		try {
			stub.UnRegisterMailbox(mb);
		} catch (Exception e) {
		}
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(),
					BoxLayout.Y_AXIS));
			jContentPane.add(getJTextArea(), null);
			jContentPane.add(getJPanel(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setPreferredSize(new Dimension(1, 150));
			jTextArea.setEditable(false);
			jTextArea.setRows(10);
		}
		return jTextArea;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new FlowLayout());
			jPanel.setPreferredSize(new Dimension(1, 30));
			jPanel.add(getJTextField(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new Dimension(215, 20));
		}
		return jTextField;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {

		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Send...");
			jButton.setPreferredSize(new Dimension(72, 18));
			jButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					try {
						String msg = jTextField.getText();
						stub.MulticastMessage(msg, mb.GetLanguage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return jButton;
	}

	@Override
	public void YouGotMail(String message, String langFrom) {
		try {
			if (langFrom.equals(mb.GetLanguage())) {
				ShowMessage(message);
				return;
			}
		} catch (RemoteException e1) {
			ShowMessage("Could not access local mailbox!");
		}
		if (translator == null) {
			try {
				translator = stub.GetTranslator(langFrom, mb.GetLanguage());
				if (translator == null) {
					List<IMailbox> clients = stub.GetClientsWithTranslator(langFrom, mb.GetLanguage());
					translator = clients.size() > 0 ? 
							clients.get(0).GetTranslator(langFrom, mb.GetLanguage()) : null;
				}
				if (translator == null) {
					throw new IllegalStateException();
				}
			} catch (RemoteException e) {
				ShowMessage("No translator available after asking server!");
				e.printStackTrace();
				return;
			} catch (IllegalStateException ie) {
				ShowMessage("No translator available after asking all known clients!");
				ie.printStackTrace();
				return;
			}
		}
		try {
			ShowMessage(translator.Translate(message, langFrom, mb.GetLanguage()));
		} catch (Exception e) {
			ShowMessage("Error comunicating with translator!");
			e.printStackTrace();
		}
	}
	
	private void ShowMessage(final String message) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					jTextArea.append(message + "\n");
				}
			});
			return;
		}
		jTextArea.append(message);
	}

}