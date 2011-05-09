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

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;

public class ShatAppletClient extends JApplet implements IMailBoxListener {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextArea jTextArea = null;
	private JPanel jPanel = null;
	private JTextField jTextField = null;
	private JButton jButton = null;
	private static final String host = "localhost"; // @jve:decl-index=0:
	private String userName;
	private String userLang;
	private IMessageService stub; // @jve:decl-index=0:
	private IMailbox mb;  //  @jve:decl-index=0:
	private ITranslator translator;
	private JLabel lblLanguage = null;

	public ShatAppletClient() {
		super();
	}

	public void init() {
		this.setSize(400, 200);
		this.setContentPane(getJContentPane());
		userName = getParameter("Name");
		userLang = getParameter("Language");
		lblLanguage.setText(userName + " | " + userLang);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			stub = (IMessageService) registry.lookup("MessageService");
			mb = new Mailbox(userName, userLang);
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
			lblLanguage = new JLabel();
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout);
			jPanel.setPreferredSize(new Dimension(1, 30));
			jPanel.add(getJTextField(), null);
			jPanel.add(getJButton(), null);
			jPanel.add(lblLanguage, null);
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
						stub.MulticastMessage(jTextField.getText(), mb);
						jTextField.setText("");
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
		if (langFrom.equals(userLang)) {
			ShowMessage(message);
			return;
		}
		if (translator != null && langFrom.equals(translator.SourceLang())) {
			try {
				ShowMessage(translator.Translate(message, langFrom, mb.GetLanguage()));
			} catch (Exception e) {
				ShowMessage("Couldn't access translator!");
				e.printStackTrace();
			}
		} else {
			try {
				translator = stub.GetTranslator(langFrom, mb.GetLanguage());
				if (translator == null) {
					// Try to get from other clients
					translator = stub.GetTranslatorFromClient(langFrom);
					if (translator == null) {
						ShowMessage("<#no_trans> " + message);
						return;
					}
				}
			} catch (RemoteException e) {
				ShowMessage("Couldn't get any translator!");
				e.printStackTrace();
				return;
			}
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

}  //  @jve:decl-index=0:visual-constraint="10,10"