package visual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.NumberFormat;

import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import mediawiki.MediaWikiTask;
import mediawiki.MediaWikiThread;
import mediawiki.event.CompletedListener;
import mediawiki.event.ProgressChangeEvent;
import mediawiki.event.ProgressChangeListener;

public class TaskPanel extends JPanel implements ProgressChangeListener<MediaWikiTask,Double>, CompletedListener {

	private JProgressBar progress = null;
	private MediaWikiThread task = null;
	
	public TaskPanel(MediaWikiThread t){
		task = t;
		setLayout(new BorderLayout());
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		add(new JLabel(t.getTask().getClass().getSimpleName()), BorderLayout.NORTH);
		
		progress = new JProgressBar(SwingConstants.HORIZONTAL){
			@Override
			public String getString() {
				NumberFormat nf = NumberFormat.getPercentInstance();
				nf.setMaximumFractionDigits(2);
				Date d = task.getTask().getETA();
				String s = "";
				if(d != null)
					s = " – "+DateFormat.getInstance().format(d);
				return nf.format(getPercentComplete())+s;
			}
		};
		progress.setMaximum(t.getTask().getTogo());
		progress.setValue(t.getTask().getDone());
		progress.setStringPainted(true);
		add(progress,BorderLayout.CENTER);
		
		add(new JButton(new AbstractAction("Anhalten") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				task.getTask().setStopped(true);
				TaskPanel.this.setVisible(false);
				TaskPanel.this.setEnabled(false);
			}
		}), BorderLayout.EAST);
		t.getTask().addProgressChangeListener(this);
		t.getTask().addCompletedListener(this);
	}

	@Override
	public void progressChanged(ProgressChangeEvent<MediaWikiTask,Double> r) {
		progress.setValue(r.getReferer().getDone());
		progress.setMaximum(r.getReferer().getTogo());
	}

	@Override
	public void completed() {
		setVisible(false);
		getParent().remove(this);
	}
}
