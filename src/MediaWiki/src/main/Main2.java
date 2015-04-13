package main;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import util.Util;
import visual.TaskPanel;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaTask;
import mediawiki.WikimediaThread;
import mediawiki.event.RequestEvent;
import mediawiki.event.RequestListener;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.request.LoginRequest;
import mediawiki.task.CommonscatTask;
import mediawiki.task.CoordinatesTask;
import mediawiki.task.GNDSetInformationTask;
import mediawiki.task.NormdatenTask;
import mediawiki.task.PersonendatenTask;

public class Main2 {
	
	static WikimediaConnection wikidata;
	static Connection connect;
	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String username = Config.USERNAME;
		String password = Config.PASSWORD;
		
		wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest(username, password));
		wikidata.setBot(true);
		
		WikimediaConnection wikipedia = new WikimediaConnection("de","wikipedia.org");
		
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost/kasparbot?user=kasparbot&password="+password);
		
		WikimediaTask[] tasks = new WikimediaTask[]{
		//		new GNDSetInformationTask(wikidata, connect),
		};
		
		JFrame f = new JFrame("KasparBot");
		f.setSize(500, 200);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JLabel l = new JLabel();
		f.add(l, BorderLayout.SOUTH);
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		for(WikimediaTask t : tasks){
			WikimediaThread th = new WikimediaThread(t);
			main.add(new TaskPanel(th));
			th.start();
		}
		
		f.add(new JScrollPane(main), BorderLayout.CENTER);
		f.setVisible(true);
		
		wikidata.addRequestListener(new RequestListener() {
			
			@Override
			public void requestPerformed(RequestEvent r) {
					l.setText(wikidata.getEditCount()+"");
			}
		});
		
		System.out.println(wikidata.getStatistic());
		System.out.println(wikidata.getEditCount());
	}

}
