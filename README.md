 
Introduction
In assignment 1, you coded a Movie Controller class and  templates to return data as an html document.  And you coded an entity class and a Spring repository interface to retrieve and insert data to a database.
Objectives in this assignment:
    • learn how to code a spring Rest Controller.  A spring controller returns html,  a rest controller returns data in json format.  A Rest Controller is intended to be used by JavaScript AJAX requests or by mobile applications or other computer to computer data exchanges.
    • code a method that uses spring RestTemplate to call an external Rest service “api.openweathermap.org”  to retrieve current weather information for a city.
    • create  spring Service classes for city information that combines information from the city table in the MySQL sample world database with current weather data.
    • use TDD test strategy to write test cases for the service and rest controller classes.  These test classes will use MOCKS isolate the code under test.  Calls to the repository and to the remote weather service will be stubbed out with spring MOCKS that return data for the test so that an actual database or actual weather service will not be used in the test.  This will make the test repeatable and predictable and faster to execute.
Requirements
    • To get city information on a city such as Miami, user enters the URL in the browser address bar localhost:8080/cities/Miami
    • The controller  class method will call the City Service class which will return data from the world.city sample table and get current weather for the city.
    • The html page returned by the controller will contain city information, the country name, the current temp and local time of the temperature reading. 


 
    • A Rest Controller
    • A user can enter a URL localhost:8080/api/cities/Miami
    • this URL will be routed to a RestController that will return the same data in JSON format  for consumption by an AJAX call or mobile or other application.
```json
{"id":3839,
 "name":"Miami",
 "countryCode":"USA",
 "countryName":"United States",
 "district":"Florida",
 "population":362470,
 "temp":73.61,
 "time":"2:07 PM"}
```
The weather service 
Using a browser copy and paste the following URL 
http://api.openweathermap.org/data/2.5/weather?q=Miami&appid=4cdc90111e0528db2d929c9090ee6e3c
You will see the weather information for Miami returned as a text string in JSON format.
```json
{"cord":{"lon":-80.19, "lat":25.77},
 "weather":[{"id":804,
             "main":"Clouds", 
             "description":"overcast clouds", 
             "icon":"04d"}],
"base":"stations", 
"main":{"temp":298.83, 
        "feels_like":298.58, 
        "temp_min":297.59, 
        "temp_max":300.37, 
        "pressure":1015,
        "humidity":61}, 
"visibility":16093,
"wind":{"speed":4.1,
        "deg":130}, "clouds":{"all":90},
"dt":1580502359,
"sys":{"type":1,
       "id":4896,
       "country":"US",
       "sunrise":1580472307,
       "sunset":1580511782},
"timezone":-18000,
"id":4164138,
"name":"Miami",
"cod":200}
```
To make it more readable I have split the text into multiples lines with indentation.  The attribute “temp” is the current temperature, “dt” is the time of the measurement, “timezone” is the offset (in seconds) from GMT and “name” is the city name.  “cod” is the return status code (200 means OK).   The temperature is returned in degrees Kelvin which has to be converted to Fahrenheit or Celsius.
The conversion formula from Kelvin to Fahrenheit is (temp - 273.15) * 9.0/5.0 + 32.0;
The link  https://openweathermap.org/current   has more documentation about the JSON attributes.
 A couple of important things to notice about the URL for openweathermap.
    • It contains a version number /2.5/ so that if in the future the format of the URL or returned data changes, the application can support both older and newer formats.  This is important when designing your api.
    • The ? character in the URL marks the start of query parameters:  q= and appid=.  These are parameters passed to the server program as part of the URL.  The q= parameter is for the city you are requesting weather. 
    • The appid parameter is an application key.
        ◦ Openweather (like many web site) requires the use of an appid= parameter.  The api key value can be obtained by registering with the web site.  This appid is used to control the number of requests that an application can make to the server in a certain amount of time and identify who is making these requests.  If an application starts making too many requests, the server may block requests with this appid.
        ◦ You can register at openweathermap and obtain your own api key (it is free), or you can use mine.  However if everyone in the class uses my key and starts making a lot of requests, the server may block it.  You have to wait 10 minutes before making the next request.

Creating Java package.  You should create a project package structure similar to the following

|  			**src/main/java** 		          |  			contents  			   			 		                                                                                                                                                                                                                               |
|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  			   **cst438hw2**            |  			 Cst438hw2Application.java  				generated by Spring tools suite 			  			   			 		                                                                                                                                                                           |
| 		   **cst438hw2.controller**		  |  			 CityController.java 				 CityRestController.java 			  			   			 		                                                                                                                                                                                         |
|  			   **cst438hw2.domain**	     |  			   			  			 Entity classes for City, Country to 			access the city and country tables in the MySQL world sample 			database.  			   			  			 Spring repositories: CityRepository, 			CountryRepository  			   			  			 Helper classes: CityInfo.java   			TimeAndTemp.java  			   			 		 |
|  			   **cst438hw2.service** 		   |  			 CityService 				 WeatherService 			  			   			 		                                                                                                                                                                                                          |
|  			**src/test/java**		           |  			   			  			   			 		                                                                                                                                                                                                                                    |
|  			   **cst438hw2**            |  			 test class generated by 				Spring tools  				 			  			   			 		                                                                                                                                                                                               |
|  			   **cst438hw2.controller**		 |  			 CityRestControllerTest.java 			  			   			 		                                                                                                                                                                                                          |
|  			   **cst438h22.service**			 		   |  			 CityServiceTest.java 			  			   			 		                                                                                                                                                                                                                 |


Part 1 – write a City controller application
Database classes 
Similar to what you did in homework 1,  you will create an entity class for City in order to retrieve data from the city table.  Create a CityRepository interface  
```java
package cst438hw2.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
	List<City> findByName(String name);
}

Define a Country entity class that includes countryCode and countryName (it does not need to include all the columns of the country table).  Create a CountryRepository interface  
package cst438hw2.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {	
	Country findByCode(String code);
}

CityService class
To get data from the database, code a CityService service class.  This class will use the CountryRepository and CityRepository to obtain information on the city and country.
package cst438hw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cst438hw2.domain.*;

@Service
public class CityService {
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private WeatherService weatherService;
	
	public CityInfo getCityInfo(String cityName) {
     //  TODO  your code goes here
     }
}
```
```java
Weather Service class
To get time and temperature from the api.openweathermap.org weather server, create a WeatherService class. 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import cst438hw2.domain.TempAndTime;

@Service
public class WeatherService {
	
	private static final Logger log =
                LoggerFactory.getLogger(WeatherService.class);
	private RestTemplate restTemplate;
	private String weatherUrl;
	private String apiKey;
	
	public WeatherService(    //1
			@Value("${weather.url}") final String weatherUrl, 
			@Value("${weather.apikey}") final String apiKey ) {
		this.restTemplate = new RestTemplate();
		this.weatherUrl = weatherUrl;
		this.apiKey = apiKey; 
	}
	
	public  TempAndTime getTempAndTime(String cityName) {
		ResponseEntity<JsonNode> response = 
                 restTemplate.getForEntity(
				weatherUrl + "?q=" + cityName + "&appid=" +
                      apiKey,
				JsonNode.class);
		JsonNode json = response.getBody();    // 2
		log.info("Status code from weather server:" +
                     response.getStatusCodeValue());
		double temp = json.get("main").get("temp").asDouble();
		long time = json.get("dt").asLong();
		int timezone = json.get("timezone").asInt();
		return new TempAndTime(temp, time, timezone);
	}
}


What is happening in this code.
    • The line marked //1 is the constructor.  The value for weatherUrl and apiKey are obtained from the application.properties file.  Update the application.properties file with the lines 
weather.url = http://api.openweathermap.org/data/2.5/weather
weather.apiKey = 4cdc90111e0528db2d929c9090ee6e3c
    • the line marked ‘// 2’ takes the text returned by the server and parses it into a tree like data structure called JsonNode.  Using the JsonNode object returned by getBody, attributes such as “dt” or “timezone” can be obtained by a get method call.   The “temp” attribute is nested inside an attribute named “main”.  So it first do get “main”, and second do get “temp” within “main”.

```
```java
package cst438hw2.domain;

public class TempAndTime {
	public double temp;
	public long time;
	public int timezone;
	
	public TempAndTime(double temp, long time, int timezone){
		this.temp = temp;
		this.time = time;
		this.timezone = timezone;
	}
 }
TempAndTime is a helper class used to return multiple values from the getTimeAndTemp method.
Controller class
Create a CityController class that handles http get request.  The @PathVariable annotation tells spring to parse the URL and put the text that occurs after /cities/ into the cityName parameter.
package cst438hw2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import cst438hw2.service.CityService;

@Controller
public class CityController {
		
	@GetMapping("/cities/{city}")
	public String getCityInfo(@PathVariable("city") String cityName, 
                               Model model) {
      // TODO your code goes here
     }
}

Test out the CityController class.  You should be able to get a page similar to one shown on page 2.

Part 2 – write a City REST controller 
    • the rest controller should have a the following mapping 
@GetMapping("/api/cities/{city}")
    • the rest controller will  use the CityService and WeatherService classes.

package cst438hw2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import cst438hw2.domain.*;
import cst438hw2.service.CityService;

@RestController
public class CityRestController {
	
	@Autowired
	private CityService cityService;
	
	@GetMapping("/api/cities/{city}")
	public CityInfo getWeather(@PathVariable("city") String cityName) {
		// TODO your code goes here
	}
}
```

Part 3 – write unit tests for CityService and CityRestController classes.
    • write a test CityServiceTest that test the city service class with MOCKS for the database repositories and weather service.
    • write a test  CityRestControllerTest for your Rest Controller using MOCKS for the CityService class.
    • Your tests  should test both valid and invalid city name.  There are some city names with multiple cities; such as Los Angeles.   Make sure your Rest Controller has tests for  all 3 situations.

Questions:
    • why don’t we have test cases for City, Country, CityRepository, CountryRepository classes?
        ◦ These classes have very little (or no) application logic.  They are classes for accessing the database.
    • why don’t we have test cases for WeatherService like we have for CityService? 
        ◦ The WeatherService is mostly code to call the remote service at openweathermap.org.  To test this would require an end to end test which is not a unit test.
        ◦ There should be little (or no) application logic in this service.
    • Why do we need separate classes for the services
        ◦ this allows writing the application logic once and using it in both CityController and CityRestController classes.  Remember:  DRY = Don’t Repeat Yourself
    • Why do we need 2 services:  one for CityService and another for WeatherService
        ◦ by putting all the code that handles the request and response to the remote weather server,  it is easier to use MOCKS to stub out this service for unit testing.  In Spring, you cannot stub out part of a class.  If the two services were combined, we would not be to use MOCKS to unit test the service.

What to submit for this assignment
    • create a GitHub repository for this assignment and commit and push your code to the repository.

Thinking ahead :  Assignment 2a
    • do a pair programmer code review of your controller, services and test classes.  Record review comments in the GitHub pull request.
