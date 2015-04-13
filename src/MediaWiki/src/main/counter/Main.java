package main.counter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Main {

	static final JLabel jep = new JLabel();
	static Timer timer = null;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
		} catch (InstantiationException e2) {
		} catch (IllegalAccessException e2) {
		} catch (UnsupportedLookAndFeelException e2) {
		}
		final JFrame jf = new JFrame("Countdown Version 1.0 -  Tim Seppelt");
		jf.setSize(800,200);
		try {
			jf.setIconImage(ImageIO.read(Main.class.getResourceAsStream("pic.png")));
		} catch (IOException e2) {
		}
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jep.setFont(new Font(null,Font.BOLD,80));
		jep.setBackground(Color.WHITE);
		jf.add(jep,BorderLayout.AFTER_LINE_ENDS);
		final Container c = new Container();
		final JTextField date = new JTextField();
		date.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				timer.stop();
			}
			public void keyPressed(KeyEvent arg0) {}
		});
		c.add(new Container(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 2888708611853243888L;

			{setLayout(new GridLayout(0,1));add(new JLabel("Datum (TT.MM.JJJJ)"));add(date);}
		});
		final JTextField time = new JTextField();
		time.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				timer.stop();
			}
			public void keyPressed(KeyEvent arg0) {}
		});
		c.add(new Container(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -2186841496401372656L;

			{setLayout(new GridLayout(0,1));add(new JLabel("Zeit (HH:MM:SS)"));add(time);}
		});
		if(new File(System.getProperty("user.home")+"/.countdown").exists()){
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(new File(System.getProperty("user.home")+"/.countdown")));
			} catch (Exception e1) {}
			date.setText(p.getProperty("date") != null ? p.getProperty("date") : "");
			time.setText(p.getProperty("time") != null ? p.getProperty("time") : "");
		}
		c.add(new JButton(new AbstractAction("Start") {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1025304716490269020L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!date.getText().matches("[0-3][0-9].[0-1][0-9].[0-9][0-9][0-9][0-9]")){
					JOptionPane.showMessageDialog(jf, "Das Datum ist nicht valide!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(!time.getText().matches("[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")){
					JOptionPane.showMessageDialog(jf, "Die Zeit ist nicht valide!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Properties p = new Properties();
				p.setProperty("date", date.getText());
				p.setProperty("time", time.getText());
				try {
					p.save(new FileOutputStream(System.getProperty("user.home")+"/.countdown"), "Dies ist die Speicherdatei für Countdown Version 1.0 - Tim Seppelt. Das Programm wird diese Datei beim Starten generieren!");
				} catch (Exception e1) {}
				timer = new Timer(50, new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						jep.setText());
					}
				});
				timer.start();
			}
		}));
		c.add(new JButton(new AbstractAction("Stop") {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -165182169994470114L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(timer != null)
					timer.stop();
			}
		}));
		JSlider js = new JSlider(10,700,jep.getFont().getSize());
		js.setToolTipText("Schriftgröße");
		js.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				jep.setFont(jep.getFont().deriveFont((float)((JSlider)e.getSource()).getValue()));
			}
		});
		c.add(js);
		c.setLayout(new FlowLayout(FlowLayout.LEADING));
		Container c2 = new Container();
		c2.setLayout(new BorderLayout());
		c2.add(c,BorderLayout.CENTER);
		jf.add(c2,BorderLayout.NORTH);
		c2.add(new JButton(new AbstractAction("Ausblenden") {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -6667346913090554609L;

			@Override
			public void actionPerformed(ActionEvent e) {
				c.setVisible(! c.isVisible());
				((JButton)e.getSource()).setText(c.isVisible() ? "Ausblenden" : "Einblenden");
			}
		}),BorderLayout.EAST);
		jf.setVisible(true);
	}

}
