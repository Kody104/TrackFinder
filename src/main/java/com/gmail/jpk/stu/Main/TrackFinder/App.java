package com.gmail.jpk.stu.Main.TrackFinder;

import java.awt.Dimension;
import java.awt.Toolkit;

public class App 
{	
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static long REFRESH_TIMER = -1L;
	
    public static void main(String[] args) {
    	ArtistSearchFrame mainWindow = new ArtistSearchFrame();
    }
}
