package com.gmail.jpk.stu.Main.TrackFinder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ArtistSearchFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7580516672710485296L;
	
	private JPanel mainPanel;
	private JTextField searchField;
	
	public ArtistSearchFrame() {
		this.setTitle("Spotify Artist Search");
    	this.setSize(400, 140);
    	this.setLocation(App.SCREEN_SIZE.width/2-this.getSize().width/2, App.SCREEN_SIZE.height/2-this.getSize().height/2);
    	this.setResizable(false);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	mainPanel = new JPanel();
    	mainPanel.setLayout(null);
    	
    	JLabel searchArtistLbl = new JLabel("Search an Artist");
    	searchArtistLbl.setBounds(this.getSize().width/2-(108/2), 10, 95, 20);
    	mainPanel.add(searchArtistLbl);
    	
    	searchField = new JTextField();
    	searchField.setBounds(this.getSize().width/2-(118/2), 40, 105, 20);
    	mainPanel.add(searchField);
    	
    	JButton searchBtn= new JButton("Search");
    	searchBtn.setBounds(this.getSize().width/2-(98/2), 70, 85, 20);
    	searchBtn.addActionListener(new SearchActionListener(this));
    	mainPanel.add(searchBtn);
    	
    	this.getRootPane().setDefaultButton(searchBtn);
    	this.add(mainPanel);
    	this.setVisible(true);
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JTextField getSearchField() {
		return searchField;
	}
}
