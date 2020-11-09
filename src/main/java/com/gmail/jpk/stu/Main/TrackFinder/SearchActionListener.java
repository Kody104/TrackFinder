package com.gmail.jpk.stu.Main.TrackFinder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Paging;

public class SearchActionListener implements ActionListener {
	private ArtistSearchFrame parentFrame;
	private AlbumTrackViewFrame childFrame;
	
	public SearchActionListener(ArtistSearchFrame parentFrame) {
		this.parentFrame = parentFrame;
		childFrame = new AlbumTrackViewFrame();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String toSearch = parentFrame.getSearchField().getText();
		if(toSearch.length() > 0) {
			Artist searchedArtist = null;
			Paging<Artist> artistsReturned = SpotifyHelper.searchArtist_Sync(toSearch);
			if(artistsReturned != null) {
				for(Artist artist : artistsReturned.getItems()) {
					if(artist.getName().equalsIgnoreCase(toSearch)) {
						searchedArtist = artist;
						break;
					}
				}
				if(searchedArtist == null) {
					searchedArtist = artistsReturned.getItems()[0];
				}
			}
			if(searchedArtist != null) {
				childFrame.setArtist(searchedArtist);
				String[] albumsStrings = SpotifyHelper.searchArtistsAlbumsString_Sync(searchedArtist.getId());
				if(albumsStrings.length > 0) {
					childFrame.updateAlbumComboBox(albumsStrings);
				}
			}
		}
		childFrame.setVisible(true);
	}
}
