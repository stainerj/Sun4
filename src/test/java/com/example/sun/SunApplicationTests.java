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
	void getrASun() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		assertThat(sc.getrASun()).isEqualTo("281.0587");
	}

	@Test
	void getDecSun() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getrASun();
		assertThat(sc.getDecSun()).isEqualTo("-23.0499");
	}

	@Test
	void getAzimuth() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getrASun();
		sc.observer();
		assertThat(sc.getAzimuth()).isEqualTo("349.4402");
	}

	@Test
	void getAltitude() {
		SunCalculator sc = new SunCalculator();
		sc.setTimeZone(0);
		sc.dateTimeToJulianDay(2022, 1, 1, 0.00);
		sc.setLatitude(54);
		sc.setLongitude(-5);
		sc.getrASun();
		sc.getAzimuth();
		assertThat(sc.getAltitude()).isEqualTo("-58.7285");
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
