package com.example.sun;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SunApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void setTimeZone() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(2);
		assertThat(sc.getTimeZone()).isEqualTo(2);
	}

	@Test
	void dateTimeToJulianDay() {
		SunCalculator sc = new SunCalculator();
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		assertThat(sc.julianDay).isEqualTo(2459580.5);
	}

	@Test
	void getAzimuth() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		assertThat(sc.getAzimuth()).isEqualTo(349.44018265662);
	}

	@Test
	void getAltitude() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getAzimuth();
		assertThat(sc.getAltitude()).isEqualTo(-58.72850606605919);
	}

	@Test
	void getTransitString() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getAzimuth();
		assertThat(sc.getTransitString()).isEqualTo("12.24 local (UTC +0)");
	}

	@Test
	void getrASun() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getAzimuth();
		sc.solarTransit();
		assertThat(sc.getrASun()).isEqualTo(280.5243812994038);
	}

	@Test
	void getDecSun() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getAzimuth();
		sc.solarTransit();
		assertThat(sc.getDecSun()).isEqualTo(-23.086597378695984);
	}

	@Test
	void getSunRiseString() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getAzimuth();
		sc.solarTransit();
		assertThat(sc.getSunRiseString()).isEqualTo("8.40 local (UTC +0)");
	}

	@Test
	void getSunSetString() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getAzimuth();
		sc.solarTransit();
		sc.riseAndSet();
		assertThat(sc.getSunSetString()).isEqualTo("16.07 local (UTC +0)");
	}
}
