package visual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import mediawiki.request.LoginRequest;


public class LoginDialog extends JDialog {

	private LoginRequest result = null;
	
	public LoginDialog(Frame owner){
		super(owner, "Anmeldung", true);
		setSize(new Dimension(300,150));
		
		JPanel listPane = new JPanel();
 
		listPane.setLayout(new BoxLayout(listPane,  BoxLayout.PAGE_AXIS));
		listPane.add(new JLabel("Bitte melde dich an:"));
		listPane.add(Box.createRigidArea(new Dimension(2,0)));
		listPane.add(new JLabel("Benutzername"));
		final JTextField benutzername = new JTextField();
		listPane.add(benutzername);
		listPane.add(new JLabel("Passwort"));
		final JPasswordField passwort = new JPasswordField();
		listPane.add(passwort);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(new JButton(new AbstractAction("Abbrechen") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LoginDialog.this.dispose();
			}
		}));
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(new JButton(new AbstractAction("Login") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				result = new LoginRequest(benutzername.getText(), new String(passwort.getPassword()));
				LoginDialog.this.dispose();
			}
		}));
		
		add(listPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_END);
	}
	
/*	@Override
	public Insets getInsets() {
		return new Insets(5, 5, 5, 5);
	} */
	
	public LoginRequest showLoginDialog()  {
		setVisible(true);
		return result;
	}
	
}
