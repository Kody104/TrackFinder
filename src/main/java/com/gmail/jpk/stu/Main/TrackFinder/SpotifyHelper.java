package com.gmail.jpk.stu.Main.TrackFinder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.core5.http.ParseException;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsRelatedArtistsRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;

public class SpotifyHelper {
	public static boolean quickSearch = true;
	private static int queries = 0;
	private static final String clientId = "b2398df50ee0449b933bb3e72abdff18";
	private static final String clientSecret = "0a786b62e04e49848498c75560baf2f8";
	private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8888/callback");
	private static final String refreshToken = "AQAL2RC18Nc-04ARqKpz6FolsklC_3HQ8C8KOC6s988R1EyFuqSfvz6A7ZRlN5AfQKn3jZFzKKBmFE-JFsN9pj28KX2l3WIKlfsMEyxlGGE0Mfp6zXwB3vubZOLkRS4tXB0";
	private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
			.setClientId(clientId)
			.setClientSecret(clientSecret)
			.setRedirectUri(redirectUri)
			.setRefreshToken(refreshToken)
			.build();
	public static void setQuickSearch(boolean quickSearch) {
		SpotifyHelper.quickSearch = quickSearch;
	}
	public static boolean getQuickSearch() {
		return SpotifyHelper.quickSearch;
	}
	private static void query() {
		queries++;
		System.out.println("Queries: " + queries + "(Quick Search: " + quickSearch + ")" + "\n");
		if(!quickSearch) {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	 private static void authorizationCodeRefresh_Sync() {
		 query();
		 try {
			 final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
			 final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
			 
			 spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
			 App.REFRESH_TIMER = System.currentTimeMillis() + 3540000L;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
	 }
	 /**
	  * Search through SpotifyAPI for a search term and a search type.
	  * @param search	The string that you are searching for.
	  * @param mot	The type of search. (Example: Artist, Album, Genre, Etc.)
	  * @return	The search result of the search
	  */
	 public static SearchResult searchItem_Sync(String search, ModelObjectType mot) {
		 query();
		 try {
			 if(System.currentTimeMillis() >= App.REFRESH_TIMER) {
				 authorizationCodeRefresh_Sync();
			 }
			 final SearchItemRequest searchItemRequest = spotifyApi.searchItem(search, mot.toString()).build();
			 final SearchResult searchResult = searchItemRequest.execute();
			 
			 return searchResult;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
		 return null;
	 }
	 /**
	  * Search through SpotifyAPI for artists and return them.
	  * @param artistName	The name of the artists to return
	  * @return	The artists returned from the search
	  */
	 public static Paging<Artist> searchArtist_Sync(String artistName) {
		 query();
		 try {
			 if(System.currentTimeMillis() >= App.REFRESH_TIMER) {
				 authorizationCodeRefresh_Sync();
			 }
			 final SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(artistName).build();
			 final Paging<Artist> artistsResult = searchArtistsRequest.execute();
			 
			 return artistsResult;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
		 return null;
	 }
	 /**
	  * Returns the top tracks of a specified artist id.
	  * @param id	The artist's id	
	  * @return	The top tracks of the artist
	  */
	 public static Track[] getArtistsTopTracks_Sync(String id) {
		 query();
		 try {
			 if(System.currentTimeMillis() >= App.REFRESH_TIMER) {
				 authorizationCodeRefresh_Sync();
			 }
			 final GetArtistsTopTracksRequest getArtistsTopTracksRequest = spotifyApi.getArtistsTopTracks(id, CountryCode.US).build();
			 final Track[] topTracks = getArtistsTopTracksRequest.execute();
			 
			 return topTracks;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
		 return null;
	 }
	 /**
	  * Return the albums of an artist by the artist ID.
	  * @param id	The id of the artist
	  * @return	The albums of the artist.
	  */
	 public static Paging<AlbumSimplified> searchArtistAlbums_Sync(String id) {
		 query();
		 try {
			 if(System.currentTimeMillis() >= App.REFRESH_TIMER) {
				 authorizationCodeRefresh_Sync();
			 }
			 final GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(id)
					 .market(CountryCode.US).build();
			 final Paging<AlbumSimplified> artistsAlbums = getArtistsAlbumsRequest.execute();
			 
			 return artistsAlbums;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
		 return null;
	 }
	 
	 public static String[] searchArtistsAlbumsString_Sync(String id) {
		 final Paging<AlbumSimplified> albumsReturned = searchArtistAlbums_Sync(id);
		 List<String> albumsListed = new ArrayList<String>();
		 
		 for(AlbumSimplified as : albumsReturned.getItems()) {
			 albumsListed.add(as.getName());
		 }
		 String[] albumsStrings = new String[albumsListed.size()];
		 for(int i = 0; i < albumsStrings.length; i++) {
			 albumsStrings[i] = albumsListed.get(i);
		 }
		 return albumsStrings;
	 }
	 /**
	  * Returns the tracklist of an album by the album ID.
	  * @param id	The id of the album
	  * @return	The tracklist of the album
	  */
	 public static Paging<TrackSimplified> searchAlbumsTracklist_Sync(String id) {
		 query();
		 try {
			 if(System.currentTimeMillis() >= App.REFRESH_TIMER) {
				 authorizationCodeRefresh_Sync();
			 }
			 final GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(id).build();
			 final Paging<TrackSimplified> albumTracklist = getAlbumsTracksRequest.execute();
			 
			 return albumTracklist;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
		 return null;
	 }
	 
	 public static String[] searchAlbumsTracklistStrings_Sync(String id) {
		 final Paging<TrackSimplified> tracklistReturned = searchAlbumsTracklist_Sync(id);
		 List<String> tracklistListed = new ArrayList<String>();
		 
		 for(TrackSimplified ts : tracklistReturned.getItems()) {
			 tracklistListed.add(ts.getName());
		 }
		 String[] tracklistStrings = new String[tracklistListed.size()];
		 for(int i = 0; i < tracklistStrings.length; i++) {
			 tracklistStrings[i] = tracklistListed.get(i);
		 }
		 return tracklistStrings;
	 }
	 /**
	  * Returns the related artists to the artist id given.
	  * @param id	The id of the artist
	  * @return	The artists that are related to the artist
	  */
	 public static Artist[] getRelatedArtistsToArtist(String id) {
		 query();
		 try {
			 if(System.currentTimeMillis() >= App.REFRESH_TIMER) {
				 authorizationCodeRefresh_Sync();
			 }
			 final GetArtistsRelatedArtistsRequest getArtistsRelatedArtists = spotifyApi.getArtistsRelatedArtists(id).build();
			 final Artist[] relatedArtists = getArtistsRelatedArtists.execute();
			 
			 return relatedArtists;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
		 return null;
	 }
	 /**
	  * Returns the track's audio features by the track ID.
	  * @param id	The id of the track
	  * @return	The audio features of the track
	  */
	 public static AudioFeatures getTrackAudioFeatures_Sync(String id) {
		 query();
		 try {
			 if(System.currentTimeMillis() >= App.REFRESH_TIMER) {
				 authorizationCodeRefresh_Sync();
			 }
			 final GetAudioFeaturesForTrackRequest getAudioFeatures = spotifyApi.getAudioFeaturesForTrack(id).build();
			 final AudioFeatures trackAudioFeatures = getAudioFeatures.execute();
			 
			 return trackAudioFeatures;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
		 return null;
	 }
	 /**
	  * Returns the track's audio analysis by the track ID.
	  * @param id	The id of the track
	  * @return	The audio analysis of the track
	  */
	 public static AudioAnalysis getTrackAudioAnalysis_Sync(String id) {
		 query();
		 try {
			 if(System.currentTimeMillis() >= App.REFRESH_TIMER) {
				 authorizationCodeRefresh_Sync();
			 }
			 final GetAudioAnalysisForTrackRequest getAudioAnalysis = spotifyApi.getAudioAnalysisForTrack(id).build();
			 final AudioAnalysis trackAudioAnalysis = getAudioAnalysis.execute();
			 
			 return trackAudioAnalysis;
		 } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		 }
		 return null;
	 }
}
