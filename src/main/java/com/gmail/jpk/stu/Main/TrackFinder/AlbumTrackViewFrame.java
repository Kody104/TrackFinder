package com.gmail.jpk.stu.Main.TrackFinder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.wrapper.spotify.enums.Modality;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

public class AlbumTrackViewFrame extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 4275008654469266591L;

	private AlbumTrackViewFrame thisFrame;

	private Artist artist;

	private AlbumSimplified currentAlbum;
	private Paging<TrackSimplified> currentTracklist;
	private AudioFeatures[] currentAudioFeatures;

	private JMenu compareMenu;
	private JMenu similarMenu;
	private JCheckBoxMenuItem toggleLimiter;
	private JComboBox<String> albumComboBox;
	private JComboBox<String> metadataComboBox;
	private JTextArea tracklistDisplay;
	private JTextArea compareDisplay;
	private JTextArea metadataDescription;

	private List<AlbumData> savedAlbums;
	private AlbumData loadedAlbum;

	private String currentSummarizedData;
	private String loadedSummarizedData;

	public AlbumTrackViewFrame() {
		thisFrame = this;
		this.setTitle("Spotify Track Metadata Viewer");
		this.setSize(800, 600);
		this.setLocation(App.SCREEN_SIZE.width/2-this.getSize().width/2, App.SCREEN_SIZE.height/2-this.getSize().height/2);
    	this.setResizable(false);
    	this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    	savedAlbums = new ArrayList<AlbumData>();
    	currentSummarizedData = "";
    	loadedSummarizedData = "";

    	JPanel panel = new JPanel();
    	panel.setLayout(null);

    	JMenuBar menuBar = new JMenuBar();
    	JMenu menu = new JMenu("File");
    	menu.getAccessibleContext().setAccessibleDescription(
    			"The file menu");

    	JMenuItem saveItem = new JMenuItem("Save Album Metadata");
    	saveItem.setMnemonic(KeyEvent.VK_S);
    	saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    	saveItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveAlbum();
			}
    	});
    	menu.add(saveItem);

    	compareMenu = new JMenu("Compare");
    	compareMenu.setMnemonic(KeyEvent.VK_C);
    	compareMenu.getAccessibleContext().setAccessibleDescription(
    			"The compare menu");

    	updateCompareMenu();
    	menu.add(compareMenu);

    	similarMenu = new JMenu("Similarities");
    	similarMenu.setMnemonic(KeyEvent.VK_M);
    	similarMenu.getAccessibleContext().setAccessibleDescription(
    			"The similarities menu");
    	updateSimilarMenu();
    	menu.add(similarMenu);

    	toggleLimiter = new JCheckBoxMenuItem("Quick Search");
    	toggleLimiter.setState(true);
    	toggleLimiter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SpotifyHelper.setQuickSearch(toggleLimiter.getState());
			}
    	});

    	JMenuItem exitItem = new JMenuItem("Close");
    	exitItem.setMnemonic(KeyEvent.VK_O);
    	exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    	exitItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispatchEvent(new WindowEvent(thisFrame, WindowEvent.WINDOW_CLOSING));
			}
    	});
    	menu.add(exitItem);
    	menuBar.add(menu);

    	this.setJMenuBar(menuBar);

    	JLabel albumSelectLbl = new JLabel("Select album");
    	albumSelectLbl.setBounds(156, 10, 85, 20);
    	panel.add(albumSelectLbl);

    	String[] metadataSelections = {"Duration", "Key", "Modality", "Time Signature", "Acousticness", "Danceability", "Energy", "Instrumentalness", "Liveness", "Loudness", "Speechiness", "Valence", "Tempo"};
    	metadataComboBox = new JComboBox<String>(metadataSelections) {
			private static final long serialVersionUID = 1L;

			@Override
    		public void setSelectedIndex(int anIndex) {
    			super.setSelectedIndex(anIndex);
    			updateTracklistDisplay(anIndex);
    			if(loadedAlbum != null) {
    				updateCompareDisplay(anIndex);
    			}
    		}
    	};
    	metadataComboBox.setBounds(125, 70, 140, 20);
    	panel.add(metadataComboBox);

    	albumComboBox = new JComboBox<String>() {
			private static final long serialVersionUID = 1L;

			@Override
    		public void setSelectedIndex(int anIndex) {
    			super.setSelectedIndex(anIndex);
    			setSelectedAlbum(SpotifyHelper.searchArtistAlbums_Sync(artist.getId()).getItems()[anIndex]);
    			metadataComboBox.setSelectedIndex(0);
    		}
    	};
    	albumComboBox.setBounds(125, 40, 140, 20);
    	panel.add(albumComboBox);

    	tracklistDisplay = new JTextArea();
    	tracklistDisplay.setBounds(this.getSize().width/2, 10, 1000, 1000);
    	tracklistDisplay.setEditable(false);

    	JScrollPane scrollPane1 = new JScrollPane(tracklistDisplay);
    	scrollPane1.setBounds(this.getSize().width/2, 10, 370, 540/2);
    	panel.add(scrollPane1);

    	compareDisplay = new JTextArea();
    	compareDisplay.setBounds(this.getSize().width, 280, 1000, 1000);
    	compareDisplay.setEditable(false);

    	JScrollPane scrollPane2 = new JScrollPane(compareDisplay);
    	scrollPane2.setBounds(this.getSize().width/2, 290, 370, 500/2);
    	panel.add(scrollPane2);

    	JLabel metadataDescLbl = new JLabel("Description");
    	metadataDescLbl.setBounds(163, 200, 85, 20);
    	panel.add(metadataDescLbl);

    	metadataDescription = new JTextArea();
    	metadataDescription.setEditable(false);
    	metadataDescription.setLineWrap(true);
    	metadataDescription.setWrapStyleWord(true);
    	metadataDescription.setBackground(UIManager.getColor("Panel.background"));
    	metadataDescription.setFont(UIManager.getDefaults().getFont("Label.font"));
    	metadataDescription.setBounds(10, 230, 375, 319/2);
    	panel.add(metadataDescription);

    	JButton summarizeAlbumBtn = new JButton("Summarize");
    	summarizeAlbumBtn.setBounds(135, this.getSize().height-150, 120, 50);
    	summarizeAlbumBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tracklistDisplay.setText(tracklistDisplay.getText() + currentSummarizedData);
				if(loadedAlbum != null) {
					compareDisplay.setText(compareDisplay.getText() + loadedSummarizedData);
				}
			}
    	});
    	panel.add(summarizeAlbumBtn);

    	this.add(panel);
	}

	private void updateTracklistDisplay(int index) {
		currentSummarizedData = "";
		if(currentTracklist == null && currentAlbum == null) {
			return;
		}
		String toDisplay = String.format("--- %s by %s ---\n", currentAlbum.getName(), currentAlbum.getArtists()[0].getName());
		switch(index) {
			case 0:
			{
				int secs = 0;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Duration: %s seconds\n", track.getTrackNumber(), track.getName(), "" + (track.getDurationMs() / 1000));
					secs += track.getDurationMs();
				}
				secs = ((secs / 1000) / currentTracklist.getItems().length);
				metadataDescription.setText("The duration of the track in seconds.");
				currentSummarizedData = String.format("\n\nAverage duration of album: %d seconds.", secs);
				break;
			}
			case 1:
			{
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					String key = "";
					switch(currentAudioFeatures[i].getKey()) {
						case -1:
						{
							key = "Undetected";
							break;
						}
						case 0:
						{
							key = "C";
							break;
						}
						case 1:
						{
							key = "C# / Db";
							break;
						}
						case 2:
						{
							key = "D";
							break;
						}
						case 3:
						{
							key = "D# / Eb";
							break;
						}
						case 4:
						{
							key = "E";
							break;
						}
						case 5:
						{
							key = "F";
							break;
						}
						case 6:
						{
							key = "F# / Gb";
							break;
						}
						case 7:
						{
							key = "G";
							break;
						}
						case 8:
						{
							key = "G# / Ab";
							break;
						}
						case 9:
						{
							key = "A";
							break;
						}
						case 10:
						{
							key = "A# / Bb";
							break;
						}
						case 11:
						{
							key = "B";
							break;
						}
						default:
						{
							key = "UNDEFINED";
							break;
						}
					}
					toDisplay += String.format("%d. %s   |   Key: %s\n", track.getTrackNumber(), track.getName(), key);
				}
				metadataDescription.setText("The estimated overall key of the track.");
				break;
			}
			case 2:
			{
				int majorCount = 0;
				int minorCount = 0;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Modality: %s\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getMode().toString());
					if(currentAudioFeatures[i].getMode() == Modality.MAJOR) majorCount++; else minorCount++;
				}
				metadataDescription.setText("Mode indicates the modality (major or minor) of a track, the type of scale from which its melodic content is derived.");
				currentSummarizedData = String.format("\n\nAlbum is " + (majorCount == minorCount ? "even." : (majorCount > minorCount ? "mostly major." : "mostly minor.")));
				break;
			}
			case 3:
			{
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Time Signature: %s\n", track.getTrackNumber(), track.getName(), "" + currentAudioFeatures[i].getTimeSignature());
				}
				metadataDescription.setText("An estimated overall time signature of a track. The time signature (meter) is a notational convention to specify how many beats are in each bar (or measure).");
				break;
			}
			case 4:
			{
				float acoustics = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Acousticness: %.2f\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getAcousticness());
					acoustics += currentAudioFeatures[i].getAcousticness();
				}
				acoustics /= currentTracklist.getItems().length;
				metadataDescription.setText("A confidence measure from 0.0 to 1.0 of whether the track is acoustic. 1.0 represents high confidence the track is acoustic.");
				currentSummarizedData = String.format("\n\nAverage acousticness of album: %.2f", acoustics);
				break;
			}
			case 5:
			{
				float danceability = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Danceability: %.2f\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getDanceability());
					danceability += currentAudioFeatures[i].getDanceability();
				}
				danceability /= currentTracklist.getItems().length;
				metadataDescription.setText("Danceability describes how suitable a track is for dancing based on a combination of musical elements including tempo, rhythm stability, beat strength, and overall regularity. A value of 0.0 is least danceable and 1.0 is most danceable.");
				currentSummarizedData = String.format("\n\nAverage dance-ability of album: %.2f", danceability);
				break;
			}
			case 6:
			{
				float energy = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Energy: %.2f\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getEnergy());
					energy += currentAudioFeatures[i].getEnergy();
				}
				energy /= currentTracklist.getItems().length;
				metadataDescription.setText("Energy is a measure from 0.0 to 1.0 and represents a perceptual measure of intensity and activity. Typically, energetic tracks feel fast, loud, and noisy. For example, death metal has high energy, while a Bach prelude scores low on the scale. Perceptual features contributing to this attribute include dynamic range, perceived loudness, timbre, onset rate, and general entropy.");
				currentSummarizedData = String.format("\n\nAverage energy of album: %.2f", energy);
				break;
			}
			case 7:
			{
				float instrumentals = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Instrumentalness: %.2f\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getInstrumentalness());
					instrumentals += currentAudioFeatures[i].getInstrumentalness();
				}
				instrumentals /= currentTracklist.getItems().length;
				metadataDescription.setText("Predicts whether a track contains no vocals. “Ooh” and “aah” sounds are treated as instrumental in this context. Rap or spoken word tracks are clearly “vocal”. The closer the instrumentalness value is to 1.0, the greater likelihood the track contains no vocal content. Values above 0.5 are intended to represent instrumental tracks, but confidence is higher as the value approaches 1.0.");
				currentSummarizedData = String.format("\n\nAverage instrumentalness of album: %.2f", instrumentals);
				break;
			}
			case 8:
			{
				float liveness = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Liveness: %.2f\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getLiveness());
					liveness += currentAudioFeatures[i].getLiveness();
				}
				liveness /= currentTracklist.getItems().length;
				metadataDescription.setText("Detects the presence of an audience in the recording. Higher liveness values represent an increased probability that the track was performed live. A value above 0.8 provides strong likelihood that the track is live.");
				currentSummarizedData = String.format("\n\nAverage liveness of album: %.2f", liveness);
				break;
			}
			case 9:
			{
				float loudness = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Loudness: %.2f dB\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getLoudness());
					loudness += currentAudioFeatures[i].getLoudness();
				}
				loudness /= currentTracklist.getItems().length;
				metadataDescription.setText("The overall loudness of a track in decibels (dB). Loudness values are averaged across the entire track and are useful for comparing relative loudness of tracks. Loudness is the quality of a sound that is the primary psychological correlate of physical strength (amplitude). Values typical range between -60 and 0 db.");
				currentSummarizedData = String.format("\n\nAverage loudness of album: %.2f dB", loudness);
				break;
			}
			case 10:
			{
				float speech = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Speechiness %.2f\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getSpeechiness());
					speech += currentAudioFeatures[i].getSpeechiness();
				}
				speech /= currentTracklist.getItems().length;
				metadataDescription.setText("Speechiness detects the presence of spoken words in a track. The more exclusively speech-like the recording (e.g. talk show, audio book, poetry), the closer to 1.0 the attribute value. Values above 0.66 describe tracks that are probably made entirely of spoken words. Values between 0.33 and 0.66 describe tracks that may contain both music and speech, either in sections or layered, including such cases as rap music. Values below 0.33 most likely represent music and other non-speech-like tracks.");
				currentSummarizedData = String.format("\n\nAverage speechiness of album: %.2f", speech);
				break;
			}
			case 11:
			{
				float valence = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Valence: %.2f\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getValence());
					valence += currentAudioFeatures[i].getValence();
				}
				valence /= currentTracklist.getItems().length;
				metadataDescription.setText("A measure from 0.0 to 1.0 describing the musical positiveness conveyed by a track. Tracks with high valence sound more positive (e.g. happy, cheerful, euphoric), while tracks with low valence sound more negative (e.g. sad, depressed, angry).");
				currentSummarizedData = String.format("\n\nAverage valence of album: %.2f", valence);
				break;
			}
			case 12:
			{
				float tempo = 0.0f;
				for(int i = 0; i < currentTracklist.getItems().length; i++) {
					TrackSimplified track = currentTracklist.getItems()[i];
					toDisplay += String.format("%d. %s   |   Tempo: %.2f BPM\n", track.getTrackNumber(), track.getName(), currentAudioFeatures[i].getTempo());
					tempo += currentAudioFeatures[i].getTempo();
				}
				tempo /= currentTracklist.getItems().length;
				metadataDescription.setText("The overall estimated tempo of a track in beats per minute (BPM). In musical terminology, tempo is the speed or pace of a given piece and derives directly from the average beat duration.");
				currentSummarizedData = String.format("\n\nAverage tempo of album: %.2f BPM", tempo);
				break;
			}
		}
		tracklistDisplay.setText(toDisplay);
	}

	private void updateCompareDisplay(int index) {
		loadedSummarizedData = "";
		if(loadedAlbum == null) {
			return;
		}
		String toDisplay = String.format("--- %s by %s ---\n", loadedAlbum.getAlbum().getName(), loadedAlbum.getAlbum().getArtists()[0].getName());
		switch(index) {
			case 0:
			{
				int secs = 0;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Duration: %s seconds\n", track.getTrackNumber(), track.getName(), "" + (track.getDurationMs() / 1000));
					secs += track.getDurationMs();
				}
				secs = ((secs / 1000) / loadedAlbum.getTracklist().length);
				loadedSummarizedData = String.format("\n\nAverage duration of album: %d seconds.", secs);
				break;
			}
			case 1:
			{
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					String key = "";
					switch(loadedAlbum.getAudioFeature(i).getKey()) {
						case -1:
						{
							key = "Undetected";
							break;
						}
						case 0:
						{
							key = "C";
							break;
						}
						case 1:
						{
							key = "C# / Db";
							break;
						}
						case 2:
						{
							key = "D";
							break;
						}
						case 3:
						{
							key = "D# / Eb";
							break;
						}
						case 4:
						{
							key = "E";
							break;
						}
						case 5:
						{
							key = "F";
							break;
						}
						case 6:
						{
							key = "F# / Gb";
							break;
						}
						case 7:
						{
							key = "G";
							break;
						}
						case 8:
						{
							key = "G# / Ab";
							break;
						}
						case 9:
						{
							key = "A";
							break;
						}
						case 10:
						{
							key = "A# / Bb";
							break;
						}
						case 11:
						{
							key = "B";
							break;
						}
						default:
						{
							key = "UNDEFINED";
							break;
						}
					}
					toDisplay += String.format("%d. %s   |   Key: %s\n", track.getTrackNumber(), track.getName(), key);
				}
				break;
			}
			case 2:
			{
				int majorCount = 0;
				int minorCount = 0;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Modality: %s\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getMode().toString());
					if(loadedAlbum.getAudioFeature(i).getMode() == Modality.MAJOR) majorCount++; else minorCount++;
				}
				loadedSummarizedData = String.format("\n\nAlbum is " + (majorCount == minorCount ? "even." : (majorCount > minorCount ? "mostly major." : "mostly minor.")));
				break;
			}
			case 3:
			{
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Time Signature: %s\n", track.getTrackNumber(), track.getName(), "" + loadedAlbum.getAudioFeature(i).getTimeSignature());
				}
				break;
			}
			case 4:
			{
				float acoustics = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Acousticness: %.2f\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getAcousticness());
					acoustics += loadedAlbum.getAudioFeature(i).getAcousticness();
				}
				acoustics /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage acousticness of album: %.2f", acoustics);
				break;
			}
			case 5:
			{
				float danceability = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Danceability: %.2f\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getDanceability());
					danceability += loadedAlbum.getAudioFeature(i).getDanceability();
				}
				danceability /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage dance-ability of album: %.2f", danceability);
				break;
			}
			case 6:
			{
				float energy = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Energy: %.2f\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getEnergy());
					energy += loadedAlbum.getAudioFeature(i).getEnergy();
				}
				energy /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage energy of album: %.2f", energy);
				break;
			}
			case 7:
			{
				float instrumentals = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Instrumentalness: %.2f\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getInstrumentalness());
					instrumentals += loadedAlbum.getAudioFeature(i).getInstrumentalness();
				}
				instrumentals /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage instrumentalness of album: %.2f", instrumentals);
				break;
			}
			case 8:
			{
				float liveness = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Liveness: %.2f\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getLiveness());
					liveness += loadedAlbum.getAudioFeature(i).getLiveness();
				}
				liveness /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage liveness of album: %.2f", liveness);
				break;
			}
			case 9:
			{
				float loudness = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Loudness: %.2f dB\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getLoudness());
					loudness += loadedAlbum.getAudioFeature(i).getLoudness();
				}
				loudness /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage loudness of album: %.2f dB", loudness);
				break;
			}
			case 10:
			{
				float speech = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Speechiness %.2f\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getSpeechiness());
					speech += loadedAlbum.getAudioFeature(i).getSpeechiness();
				}
				speech /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage speechiness of album: %.2f", speech);
				break;
			}
			case 11:
			{
				float valence = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Valence: %.2f\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getValence());
					valence += loadedAlbum.getAudioFeature(i).getValence();
				}
				valence /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage valence of album: %.2f", valence);
				break;
			}
			case 12:
			{
				float tempo = 0.0f;
				for(int i = 0; i < loadedAlbum.getTracklist().length; i++) {
					TrackSimplified track = loadedAlbum.getTrack(i);
					toDisplay += String.format("%d. %s   |   Tempo: %.2f BPM\n", track.getTrackNumber(), track.getName(), loadedAlbum.getAudioFeature(i).getTempo());
					tempo += loadedAlbum.getAudioFeature(i).getTempo();
				}
				tempo /= loadedAlbum.getTracklist().length;
				loadedSummarizedData = String.format("\n\nAverage tempo of album: %.2f BPM", tempo);
				break;
			}
		}
		compareDisplay.setText(toDisplay);
	}

	private void updateSimilarMenu() {
		similarMenu.removeAll();
		if(currentTracklist != null) {
			for(int i = 0; i < currentTracklist.getItems().length; i++) {
				TrackSimplified track = currentTracklist.getItems()[i];
				AudioFeatures af = currentAudioFeatures[i];
				String trackName = track.getName();
				if(track.getName().length() > 24) { // Shorten name
					trackName = track.getName().substring(0, 22);
					trackName += "...";
				}
				JMenuItem addItem = new JMenuItem(String.format("%d. - %s", track.getTrackNumber(), trackName));
				addItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						List<AlbumData> tracks = findSimilarTracks(track, af);
						displaySimilarTracks(track, tracks);
					}
				});
				similarMenu.add(addItem);
			}
			similarMenu.addSeparator();
			similarMenu.add(toggleLimiter);
		}
		else {
			JMenuItem similarDefault = new JMenuItem("No current tracklist");
			similarDefault.setEnabled(false);
			similarMenu.add(similarDefault);
		}
		compareDisplay.setText(toDisplay);
	}
	private void updateCompareMenu() {
		compareMenu.removeAll();
		if(savedAlbums.size() > 0) {
			for(int i = 0; i < savedAlbums.size(); i++) {
				AlbumData sad = savedAlbums.get(i);
				String albumDisplayName = sad.getAlbum().getName();
				if(sad.getAlbum().getName().length() > 24) {
					albumDisplayName = sad.getAlbum().getName().substring(0, 22);
					albumDisplayName += "...";
				}
				JMenuItem addItem = new JMenuItem(albumDisplayName);
				if(i < 10) { // Make sure we don't go over the 0-9 numbers.
					addItem.setMnemonic(48+i); // 48 is the int of VK_0. It counts up by 1 to 57 until VK_9.
					addItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							loadAudioFeaturesOfTrack(sad);
							updateCompareDisplay(metadataComboBox.getSelectedIndex());
						}
					});
					compareMenu.add(addItem);
				}
			}
			compareMenu.addSeparator();
	    	JMenuItem deleteItem = new JMenuItem("Delete saved albums");
	    	deleteItem.addActionListener(new ActionListener() {
	    		@Override
	    		public void actionPerformed(ActionEvent e) {
	    			deleteSavedAlbums();
	    		}
	    	});
	    	compareMenu.add(deleteItem);
		}
		else {
			JMenuItem compareDefault = new JMenuItem("No albums saved");
	    	compareDefault.setEnabled(false);
	    	compareMenu.add(compareDefault);
		}
	}

	private void displaySimilarTracks(TrackSimplified ts, List<AlbumData> tracks) {
		String toDisplay = String.format("[[%s]]\n--- Similar Tracks ---\n", ts.getName());
		if(tracks.size() == 0) {
			compareDisplay.setText(toDisplay);
			return;
		}
		for(AlbumData sad : tracks) {
			if(!sad.getTrack(0).getId().equals(ts.getId())) {
				toDisplay += String.format("%d. - %s by %s(%s)\n", sad.getTrack(0).getTrackNumber(), sad.getTrack(0).getName(), sad.getTrack(0).getArtists()[0].getName(), sad.getAlbum().getName());
			}
		}
		compareDisplay.setText(toDisplay);
	}

	/**
	 * Find and return an array of similar tracks to the 'key' audio features given.
	 * @param track	The track to find similar music to
	 * @param key	The audio features to find similar music to
	 * @return	The array of tracks that are similar to 'key' track
	 */
	private List<AlbumData> findSimilarTracks(TrackSimplified track, AudioFeatures key) {
		boolean quickSearch = SpotifyHelper.getQuickSearch();
		List<AlbumData> similarTracks = new ArrayList<AlbumData>();
		Artist[] artists = SpotifyHelper.getRelatedArtistsToArtist(track.getArtists()[0].getId());
		if(artists != null && artists.length > 0) {
			int artistCount = 0;
			for(Artist a : artists) {
				if(quickSearch && artistCount > 3) {
					break;
				}
				Paging<AlbumSimplified> albums = SpotifyHelper.searchArtistAlbums_Sync(a.getId());
				if(albums != null && albums.getItems().length > 0) {
					int albumCount = 0;
					for(AlbumSimplified alb : albums.getItems()) {
						if(quickSearch && albumCount > 0) {
							break;
						}
						Paging<TrackSimplified> tracks = SpotifyHelper.searchAlbumsTracklist_Sync(alb.getId());
						if(tracks != null && tracks.getItems().length > 0) {
							int trackCount = 0;
							for(TrackSimplified t : tracks.getItems()) {
								if(quickSearch && trackCount > 5) {
									break;
								}
								AudioFeatures af = SpotifyHelper.getTrackAudioFeatures_Sync(t.getId());
								if(af != null) {
									if(Math.abs(key.getDanceability() - af.getDanceability()) <= ComparisonOptions.getDanceabilityMargin()
											&& Math.abs(key.getEnergy() - af.getEnergy()) <= ComparisonOptions.getEnergyMargin()
											&& Math.abs(key.getValence() - af.getValence()) <= ComparisonOptions.getValenceMargin()
											&& Math.abs(key.getTempo() - af.getTempo()) <= ComparisonOptions.getTempoMargin()
											&& Math.abs(key.getAcousticness() - af.getAcousticness()) <= ComparisonOptions.getAcousticnessMargin()
											&& Math.abs(key.getInstrumentalness() - af.getInstrumentalness()) <= ComparisonOptions.getInstrumentalnessMargin()
											&& Math.abs(key.getSpeechiness() - af.getSpeechiness()) <= ComparisonOptions.getSpeechinessMargin()) { // Are these tracks within the margin of each others data?
										AlbumData data = new AlbumData(alb, t, af);
										similarTracks.add(data);
									}
								}
								trackCount++;
							}
						}
						albumCount++;
					}
				}
			}
			artistCount++;
		}
		/*for(AlbumData sad : savedAlbums) {
			for(int i = 0; i < sad.getTracklist().length; i++) {
				AudioFeatures af = sad.getAudioFeature(i);
				if(Math.abs(key.getDanceability() - af.getDanceability()) <= ComparisonOptions.getDanceabilityMargin()
						&& Math.abs(key.getEnergy() - af.getEnergy()) <= ComparisonOptions.getEnergyMargin()
						&& Math.abs(key.getValence() - af.getValence()) <= ComparisonOptions.getValenceMargin()
						&& Math.abs(key.getTempo() - af.getTempo()) <= ComparisonOptions.getTempoMargin()
						&& Math.abs(key.getAcousticness() - af.getAcousticness()) <= ComparisonOptions.getAcousticnessMargin()
						&& Math.abs(key.getInstrumentalness() - af.getInstrumentalness()) <= ComparisonOptions.getInstrumentalnessMargin()
						&& Math.abs(key.getSpeechiness() - af.getSpeechiness()) <= ComparisonOptions.getSpeechinessMargin()) { // Are these tracks within the margin of each others data?
					similarTracks.add(new AlbumData(sad.getAlbum(), sad.getTrack(i), sad.getAudioFeature(i)));
				}
			}
		}*/
		System.out.println("Similar Track Size: " + similarTracks.size() + "\n");
		return similarTracks;
	}
	private void saveAlbum() {
		if(currentAlbum != null && currentTracklist != null && currentAudioFeatures != null) {
			boolean isDuplicate = false; // Make sure we don't add duplicate albums
			for(AlbumData sad : savedAlbums) {
				if(sad.getAlbum().getId().equals(currentAlbum.getId())) {
					isDuplicate = true;
					break;
				}
			}
			if(!isDuplicate) {
				savedAlbums.add(new AlbumData(currentAlbum, currentTracklist, currentAudioFeatures));
				updateCompareMenu();
				updateSimilarMenu();
			}
		}
	}

	private void deleteSavedAlbums() {
		if(savedAlbums.size() > 0) {
			for(int i = 0; i < savedAlbums.size(); i++) {
				savedAlbums.remove(i);
				i--;
			}
			updateCompareMenu();
			updateSimilarMenu();
			loadedAlbum = null;
			compareDisplay.setText("");
		}
	}

	private void loadAudioFeaturesOfTrack(AlbumData sad) {
		if(sad != null) {
			this.loadedAlbum = sad;
		}
	}

	private void setSelectedAlbum(AlbumSimplified selectedAlbum) {
		currentAlbum = selectedAlbum;
		currentTracklist = SpotifyHelper.searchAlbumsTracklist_Sync(selectedAlbum.getId());
		currentAudioFeatures = new AudioFeatures[currentTracklist.getItems().length];
		for(int i = 0; i < currentAudioFeatures.length; i++) {
			currentAudioFeatures[i] = SpotifyHelper.getTrackAudioFeatures_Sync(currentTracklist.getItems()[i].getId());
		}
		updateSimilarMenu();
	}

	public void updateAlbumComboBox(String[] str) {
		albumComboBox.removeAllItems();
		for(String s : str) {
			albumComboBox.addItem(s);
		}
		albumComboBox.setSelectedIndex(0);
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}
 }
