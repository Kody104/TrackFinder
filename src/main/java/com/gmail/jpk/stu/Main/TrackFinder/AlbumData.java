package com.gmail.jpk.stu.Main.TrackFinder;

import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

public class AlbumData {
	
	private AlbumSimplified album;
	private TrackSimplified[] tracklist;
	private AudioFeatures[] audioFeatures;
	
	public AlbumData(AlbumSimplified album, Paging<TrackSimplified> tracks, AudioFeatures[] audios) {
		this.album = album;
		this.tracklist = new TrackSimplified[tracks.getItems().length];
		this.audioFeatures = audios;
		for(int i = 0; i < tracks.getItems().length; i++) {
			tracklist[i] = tracks.getItems()[i];
		}
	}
	
	public AlbumData(AlbumSimplified album, TrackSimplified track, AudioFeatures audio) {
		this.album = album;
		this.tracklist = new TrackSimplified[1];
		this.audioFeatures = new AudioFeatures[1];
		this.tracklist[0] = track;
		this.audioFeatures[0] = audio;
	}
	
	public AlbumSimplified getAlbum() {
		return album;
	}
	
	public TrackSimplified getTrack(int index) {
		if(index < tracklist.length) {
			return tracklist[index];
		}
		return null;
	}
	
	public TrackSimplified[] getTracklist() {
		return tracklist;
	}
	
	public AudioFeatures getAudioFeature(int index) {
		if(index < audioFeatures.length) {
			return audioFeatures[index];
		}
		return null;
	}
	
	public AudioFeatures[] getAudioFeatures() {
		return audioFeatures;
	}
}
