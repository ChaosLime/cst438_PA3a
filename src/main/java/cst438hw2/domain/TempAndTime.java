package cst438hw2.domain;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class TempAndTime {
	public double temp;
	public long time;

	public int timezone;

	public TempAndTime(double temp, long time, int timezone){
		this.temp = temp;
		this.time = time;
		this.timezone = timezone;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	public double getFarTemp() {
		Double farTemp = (getTemp() - 273.15) * (9.0 / 5.0) + 32;
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(farTemp));
	}

	public String getStringTime() {

		long timeOffset = time + timezone;
		Instant epoch = Instant.ofEpochSecond(timeOffset);
		int HH = epoch.atZone(ZoneOffset.UTC).getHour();
		int mm = epoch.atZone(ZoneOffset.UTC).getMinute();
		
		String timeColonPattern = "hh:mm a";
		DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern(timeColonPattern);
		LocalTime colonTime = LocalTime.of(HH, mm);
		
		String formattedTimeStr = timeColonFormatter.format(colonTime).toString();
		
		return formattedTimeStr;
		
	}

 }