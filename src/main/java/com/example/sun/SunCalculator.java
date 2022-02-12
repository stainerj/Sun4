package com.example.sun;

import java.text.DecimalFormat;

public class SunCalculator {

    private final DecimalFormat df2 = new DecimalFormat("00.00");
    private final DecimalFormat df4 = new DecimalFormat("0.0000");
    private final DecimalFormat dfw = new DecimalFormat("##");
    private final DecimalFormat dfm = new DecimalFormat("00");
    private final DecimalFormat dfn = new DecimalFormat("0");

    private double year;
    private double month;
    private double day;
    private double timeDay;//time of day, 24h clock
    private double latitude;
    private double longitude;
    private double timeZone;
    private double julianDayRaw;//Julian Day number not accounting for time of day. (Ends .5 - exact time at midnight)
    public double julianDay;//Julian Day number exact
    private String dateTimeString;//date and time as a string
    private String latLongString;//latitude and longitude as a string
    private double rASun;//right ascension of sun
    private double decSun;//declination of sun
    private double meanAnomDeg;//the mean anomaly in degrees
    private double trueAnomaly;//true anomaly
    private double eclipticLongSun;//ecliptic longitude of the sun
    private double hourAngle;
    private double azimuth;
    private double altitude;
    private String daylightString;//day/night/twilight sky
    private double jTransit; //j (time of transit, measured in terms of Julian date)
    private String transitString;//transit (solar noon) time as a string
    private double jRise;//sunrise time (Julian date)
    private double jSet;//sunset time (Julian date)
    private String sunRiseString;//sunrise time as a string
    private String sunSetString;//sunset time as a string

    private final double J2K = 2451545;//J2000
    private final double M0 = 357.5291;//mean anomaly degrees
    private final double M1 = 0.98560028;//mean anomaly degrees per day
    private final double ELOP = 102.9373;//ecliptic longitude of perihelion
    private final double OOTE = 23.4393;//obliquity of the ecliptic
    private final double THETAZERO = 280.1470;//sidereal constant for earth
    private final double THETAONE = 360.9856235;//sidereal constant for earth
    private final double JZERO = 0.0009;
    private final double JONE = 0.0053;
    private final double JTWO = -0.0068;

    //auto-generated accessors and mutators
    public double getYear() {
        return year;
    }

    public void setYear(double year) {
        this.year = year;
    }

    public double getMonth() {
        return month;
    }

    public void setMonth(double month) {
        this.month = month;
    }

    public double getDay() {
        return day;
    }

    public void setDay(double day) {
        this.day = day;
    }

    public double getTimeDay() {
        return timeDay;
    }

    public void setTimeDay(double timeDay) {
        this.timeDay = timeDay;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    //convert to negative value so east positive and west negative
    public void setLongitude(double longitude) {
        this.longitude = -(longitude);
    }

    public double getTimeZone() {
        return timeZone;
    }

    //set time zone
    public void setTimeZone (double specifyTimeZone) {
        timeZone = specifyTimeZone;
    }

    //convert (gregorian) calender date to julian day number - exact method including time of day
    //exact whole number date gives 12.00 hours (noon), date given as e.g. 0.5 corresponds to 00.00 hours (midnight, UTC)
    public void dateTimeToJulianDay(double year, double month, double day, double timeDay) {
        double hour = Math.floor(timeDay);
        double minutes = timeDay - hour;
        double minDecFraction = (minutes/60)*100;
        double decTime = (hour + minDecFraction)/24;

        //keep year and month as entered for display
        double yearDisplay = year;
        double monthDisplay = month;

        if ((month == 1) || (month == 2)) {
            year = (year - 1);
            month = (month + 12);
        }

        double a = Math.floor(year/100);
        double b = Math.floor(a/4);
        double c = 2 - a + b;
        double e = Math.floor(365.25*(year+4716));
        double f = Math.floor(30.6001*(month+1));

        julianDayRaw = c + day + e + f - 1524.5;

        dateTimeString = dfw.format(yearDisplay) + "/"
                + dfw.format(monthDisplay) + "/" + dfw.format(day) + " " + df2.format(timeDay) + " (local)";

        julianDay = julianDayRaw + decTime;

        //compensate for time zone offset
        julianDay = julianDay - (timeZone/24);
    }

    //convert input date and time to julian day
    public String getJulianDay() {
        dateTimeToJulianDay(year, month, day, timeDay);
        return df4.format(julianDay);
    }

    //return the date and time as a string
    public String getDateTimeString() { return dateTimeString; }

    //return latitude and longitude as a string
    public String getLatLongString() {
        latLongString =  df2.format(latitude) + ", " + df2.format(-longitude);
        return latLongString;
    }

    //return right ascension & declination after equatorialCoordinates has executed
    public String getrASun () {
        meanAnomaly();
        equationCentre();
        eclipticalCoordinates();
        equatorialCoordinates();
        return df4.format(rASun); }

    public String getDecSun() {
        return df4.format(decSun); }

    //calculate azimuth and altitude; return azimuth
    public String getAzimuth() {
        observer();
        return df4.format(azimuth);
    }

    //return altitude
    public String getAltitude() {
        return df4.format(altitude); }

    //return day/night/twilight sky state
    public String getDaylightString() { return daylightString; }

    //calculate transit time and return as string
    public String getTransitString() {
        solarTransit();
        transitString = julianDayToTime(jTransit, julianDayRaw);
        return transitString;
    }

    //calculate sunrise and sunset times; return sunrise time as a string
    public String getSunRiseString() {
        riseAndSet();
        sunRiseString = julianDayToTime(jRise, julianDayRaw);
        return sunRiseString;
    }

    //return sunset time as a string
    public String getSunSetString() {
        sunSetString = julianDayToTime(jSet, julianDayRaw);
        return sunSetString;
    }

    //calculate the mean anomaly in degrees
    public void meanAnomaly() {
        double theta= M0 + M1*(julianDay - J2K);
        meanAnomDeg = theta%360;
    }

    //calculate the equation of centre - trig functions take arguments in radians
    public void equationCentre() {
        final double C1 = 1.9148;
        final double C2 = 0.0200;
        final double C3 = 0.0003;

        //convert to radians
        double meanAnomRad = degreesToRadians(meanAnomDeg);
        double equationOfCentre = (C1 * Math.sin(meanAnomRad)) + (C2 * Math.sin(2 * meanAnomRad)) + (C3 * Math.sin(3 * meanAnomRad));
        trueAnomaly = meanAnomDeg + equationOfCentre;
    }

    //calculate the ecliptic longitude of the sun
    public void eclipticalCoordinates() {
        double lambda = trueAnomaly + ELOP + 180;
        eclipticLongSun = lambda%360;
    }

    //calculate the right ascension and declination of the sun
    public void equatorialCoordinates() {
        //constant approximations for right ascension and declination (earth)
        //not needed using alternative method only

        //convert to radians
        double eclipticLongSunRad = degreesToRadians(eclipticLongSun);
        double sinLambdaSun = Math.sin(eclipticLongSunRad); //sine of the ecliptic longitude
        double cosLambdaSun = Math.cos(eclipticLongSunRad); //cosine of the ecliptic longitude
        //calculate right ascension using alternative method
        //αsun = arctan(sin λsun cos ε, cos λsun). Returns angle in radians.
        rASun = Math.atan2(sinLambdaSun*Math.cos(degreesToRadians(OOTE)), cosLambdaSun);
        rASun = radiansToDegrees(rASun);
        if (rASun < 0) {
            rASun = 360 + rASun; //convert to range 0-360 if negative. What about greater than or equal to 360?
        }
        //calculate declination using alternative method
        //δsun = arcsin(sin λsun sin ε). Returns angle in radians.
        decSun = Math.asin(sinLambdaSun*Math.sin(degreesToRadians(OOTE)));
        decSun = radiansToDegrees(decSun);
        //are alternative method values more accurate? Practically the same. Assume full equations are slightly better.
    }

    //calculate sun position for earth observer
    public void observer() {
        double thetaSidereal = THETAZERO + THETAONE * (julianDay - J2K) - longitude; //raw angle, remove multiples of 360
        double sidereal = thetaSidereal%360; //sidereal time
        hourAngle = sidereal - rASun;

        //convert to radians
        double hourAngleRad = degreesToRadians(hourAngle);
        double latitudeRad = degreesToRadians(latitude);
        double decSunRad = degreesToRadians(decSun);
        azimuth = Math.atan2(Math.sin(hourAngleRad), Math.cos(hourAngleRad)*Math.sin(latitudeRad) - Math.tan(decSunRad)*Math.cos(latitudeRad));
        azimuth = radiansToDegrees(azimuth) + 180; //convert from radians to degrees, add 180 degrees for conventional azimuth (north=0)

        altitude = Math.asin(Math.sin(latitudeRad)*Math.sin(decSunRad) + Math.cos(latitudeRad)*Math.cos(decSunRad)* Math.cos(hourAngleRad));
        altitude = radiansToDegrees(altitude); //convert from radians to degrees
        //azimuth 0 = north

        //determine day/night/twilight sky state
        if (altitude >= 0) {
            daylightString = "DAY";
        }
        else if (0 > altitude && altitude >= -6) {
            daylightString = "CIVIL TWILIGHT";
        }
        else if (-6 > altitude && altitude >= -12) {
            daylightString = "NAUTICAL TWILIGHT";
        }
        else if (-12 > altitude && altitude >= -18) {
            daylightString = "ASTRONOMICAL TWILIGHT";
        }
        else
            daylightString = "NIGHT";
    }

    //calculate transit time (julian day)
    public void solarTransit() {
        double n;
        double nNearInt; //nearest whole integer to n
        double jApprox; //approx value of j

        //n(*) = (J - J2000 - J0)
        n = (julianDay-J2K-JZERO) - longitude/360;
        nNearInt = Math.round(n);//nearest whole number
        jApprox = J2K + JZERO + longitude/360 + nNearInt;

        //re-calculate meanAnom for jApprox
        meanAnomDeg = meanAnomalyFromJulianDayTime(jApprox);

        double meanAnomRad = degreesToRadians(meanAnomDeg);
        double lSun = meanAnomDeg + ELOP + 180;
        //convert to range 0-360 if equal to or over 360.
        if (lSun >= 360) {
            lSun = lSun - 360;
        }
        double lSunRad = degreesToRadians(lSun);

        //Jtransit J(*) + J1 sin M + J2 sin(2 Lsun)
        jTransit = jApprox + (JONE*(Math.sin(meanAnomRad))) + (JTWO*(Math.sin(2*lSunRad)));

        //repetition method to increase accuracy ~ jTransit = jTransit - hourAngleForJTransit/360
        do {
            meanAnomalyFromJulianDayTime(jTransit);
            equationCentre();
            eclipticalCoordinates();
            equatorialCoordinates();
            hourAngleFromJulianDayTime(jTransit);
            jTransit = jTransit - (hourAngle/360);
        } while (hourAngle > 0.0001);//close to zero
    }

    //calculate the mean anomaly in degrees
    public double meanAnomalyFromJulianDayTime(double julianDayTime) {
        double theta= M0 + M1*(julianDayTime - J2K);
        meanAnomDeg = theta%360;
        return meanAnomDeg;
    }

    //calculate hour angle from julian day time
    public void hourAngleFromJulianDayTime(double julianDayTime) {
        double thetaSidereal = THETAZERO + THETAONE * (julianDayTime - J2K) - longitude; //raw angle, remove multiples of 360
        double sidereal = thetaSidereal%360; //sidereal time
        hourAngle = sidereal - rASun;
    }

    //convert Julian day number to time of day in hours and minutes
    public String julianDayToTime (double jTime, double julianDayZero) {
        double timeHours = (jTime - julianDayZero)*24;
        double timeMinutes = (timeHours - (Math.floor(timeHours)));
        timeHours = timeHours - timeMinutes + timeZone; //convert hours to local time
        timeMinutes = timeMinutes*60;
        if (timeMinutes >= 59.5) {
            timeMinutes = 0;
            timeHours = timeHours + 1;
        }
        if (timeHours < 0) {
            timeHours = timeHours + 24;
        }
        if (timeHours >= 24) {
            timeHours = timeHours - 24;
        }

        //Format time with UTC offset for time zone
        if (timeZone < 0) {
            double timeZoneAbs = -timeZone;
            return dfw.format(timeHours) + "." + dfm.format(timeMinutes) + " local (UTC -" + dfn.format(timeZoneAbs) + ")";
        }
        else
            return dfw.format(timeHours) + "." + dfm.format(timeMinutes) + " local (UTC +" + dfn.format(timeZone) + ")";
    }

    //calculate sunrise & sunset times
    public void riseAndSet() {
        //constant for atmospheric refraction effect and apparent solar disc diameter (earth, degrees)
        final double H0 = -0.83;
        double correction;

        //H = arccos((sin h₀ − sin φ sin δ)/(cos φ cos δ))
        double H =  Math.acos((Math.sin(degreesToRadians(H0)) - (Math.sin(degreesToRadians(latitude)) *
                Math.sin(degreesToRadians(decSun))))/(Math.cos(degreesToRadians(latitude)) *
                Math.cos(degreesToRadians(decSun))));

        //Jrise = jTransit - H/360 (*J3=1)
        jRise = jTransit - (radiansToDegrees(H)/360);//repetition method available     //first estimate

        //start do while loop for repetition method here (sunrise)
        do {
            meanAnomalyFromJulianDayTime(jRise);
            equationCentre();
            eclipticalCoordinates();
            equatorialCoordinates();
            //H = arccos((sin h₀ − sin φ sin δ)/(cos φ cos δ))
            double hRise =  Math.acos((Math.sin(degreesToRadians(H0)) - (Math.sin(degreesToRadians(latitude)) *
                    Math.sin(degreesToRadians(decSun))))/(Math.cos(degreesToRadians(latitude)) *
                    Math.cos(degreesToRadians(decSun))));
            //Jrise = jTransit - H/360 (*J3=1)
            jRise = jTransit - (radiansToDegrees(hRise)/360);//repetition method     //repeat estimate
            correction = ((hRise - H)/360);
        } while (correction > 0.0001 || correction < -0.0001);

        //Jset = jTransit + H/360 (*J3=1)
        jSet = jTransit + (radiansToDegrees(H)/360);//repetition method available     //first estimate

        //start do while loop for repetition method here (sunset)
        do {
            meanAnomalyFromJulianDayTime(jSet);
            equationCentre();
            eclipticalCoordinates();
            equatorialCoordinates();
            //H = arccos((sin h₀ − sin φ sin δ)/(cos φ cos δ))
            double hSet =  Math.acos((Math.sin(degreesToRadians(H0)) - (Math.sin(degreesToRadians(latitude)) *
                    Math.sin(degreesToRadians(decSun))))/(Math.cos(degreesToRadians(latitude)) *
                    Math.cos(degreesToRadians(decSun))));
            //Jrise = jTransit - H/360 (*J3=1)
            jSet = jTransit + (radiansToDegrees(hSet)/360);//repetition method     //repeat estimate
            correction = ((hSet - H)/360);
        } while (correction > 0.0001 || correction < -0.0001);
    }

    //convert degrees to radians
    public double degreesToRadians(double degrees)
    {
        return degrees * 0.0174532925199;
    }

    //convert radians to degrees
    public double radiansToDegrees(double radians)
    {
        return radians / 0.0174532925199;
    }


    //consider alternative approach - to return an object of all required output data.
}
