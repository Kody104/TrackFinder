package com.gmail.jpk.stu.Main.TrackFinder;

public class ComparisonOptions {
	
	private static float danceabilityMargin = 0.1f;
	private static float energyMargin = 0.1f;
	private static float valenceMargin = 0.1f;
	private static float tempoMargin = 25.0f;
	private static float acousticnessMargin = 0.25f;
	private static float instrumentalnessMargin = 0.25f;
	private static float speechinessMargin = 0.15f;
	
	public static float getDanceabilityMargin() {
		return danceabilityMargin;
	}
	
	public static void setDanceabilityMargin(float danceabilityMargin) {
		ComparisonOptions.danceabilityMargin = danceabilityMargin;
	}
	
	public static float getEnergyMargin() {
		return energyMargin;
	}
	
	public static void setEnergyMargin(float energyMargin) {
		ComparisonOptions.energyMargin = energyMargin;
	}
	
	public static float getValenceMargin() {
		return valenceMargin;
	}
	
	public static void setValenceMargin(float valenceMargin) {
		ComparisonOptions.valenceMargin = valenceMargin;
	}

	public static float getTempoMargin() {
		return tempoMargin;
	}

	public static void setTempoMargin(float tempoMargin) {
		ComparisonOptions.tempoMargin = tempoMargin;
	}

	public static float getAcousticnessMargin() {
		return acousticnessMargin;
	}

	public static void setAcousticnessMargin(float acousticnessMargin) {
		ComparisonOptions.acousticnessMargin = acousticnessMargin;
	}

	public static float getInstrumentalnessMargin() {
		return instrumentalnessMargin;
	}

	public static void setInstrumentalnessMargin(float instrumentalnessMargin) {
		ComparisonOptions.instrumentalnessMargin = instrumentalnessMargin;
	}

	public static float getSpeechinessMargin() {
		return speechinessMargin;
	}

	public static void setSpeechinessMargin(float speechinessMargin) {
		ComparisonOptions.speechinessMargin = speechinessMargin;
	}
}
