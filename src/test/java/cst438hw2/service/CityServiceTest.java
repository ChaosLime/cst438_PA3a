package cst438hw2.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import java.util.Vector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import cst438hw2.domain.City;
import cst438hw2.domain.CityInfo;
import cst438hw2.domain.CityRepository;
import cst438hw2.domain.Country;
import cst438hw2.domain.CountryRepository;
import cst438hw2.domain.TempAndTime;

@SpringBootTest
public class CityServiceTest {

  @MockBean
  private WeatherService weatherService;

  @Autowired
  private CityService cityService;

  @MockBean
  private CityRepository cityRepository;

  @MockBean
  private CountryRepository countryRepository;

  @Test
  public void contextLoads() {}

  @Test
  public void validCity() throws Exception {
    Country country = new Country("AAA", "TestCountry");
    City city = new City(1, "TestCity", "AAA", "DistrictTest", 99999);

    Vector<City> cities = new Vector<City>();
    cities.add(city);

    // test temp at 99.0F, and 12:00PM epoch + GMT offset of -14400
    given(weatherService.getTempAndTime("TestCity"))
        .willReturn(new TempAndTime(310.37, 1593964800, -14400));

    given(cityRepository.findByName("TestCity")).willReturn(cities);

    given(countryRepository.findByCode("AAA")).willReturn(country);

    CityInfo cityResult = cityService.getCityInfo("TestCity");
    CityInfo expectedResult =
        new CityInfo(1, "TestCity", "AAA", "TestCountry", "DistrictTest", 99999, 99.0, "12:00 PM");

    assertThat(cityResult).isEqualTo(expectedResult);
  }

  @Test
  public void invalidCity() {
    Country country = new Country("AAA", "TestCountry");
    City city = new City(1, "TestCity", "AAA", "DistrictTest", 99999);

    Vector<City> cities = new Vector<City>();
    cities.add(city);

    given(weatherService.getTempAndTime("TestCity"))
        .willReturn(new TempAndTime(310.37, 1593964800, -14400));

    given(cityRepository.findByName("TestCity")).willReturn(cities);

    given(countryRepository.findByCode("AAA")).willReturn(country);

    CityInfo cityResult = cityService.getCityInfo("diffTestCity");
    CityInfo expectedResult = null;

    assertThat(cityResult).isEqualTo(expectedResult);
  }

  // The city service should always return
  // the first result if multiple entries are found
  @Test
  public void multipleCities() {
    // Create new list of cities
    Vector<City> cities = new Vector<City>();

    // Fill in data for each cities country, city info
    Country countryA = new Country("AAA", "TestCountryA");
    City cityA = new City(1, "TestCity", "AAA", "DistrictTest", 11111);
    cities.add(cityA);
    given(countryRepository.findByCode("AAA")).willReturn(countryA);

    Country countryB = new Country("BBB", "TestCountryB");
    City cityB = new City(1, "TestCity", "BBB", "DistrictTest2", 22222);
    cities.add(cityB);
    given(countryRepository.findByCode("BBB")).willReturn(countryB);

    Country countryC = new Country("CCC", "TestCountryC");
    City cityC = new City(1, "TestCity", "CCC", "DistrictTest3", 33333);
    cities.add(cityC);
    given(countryRepository.findByCode("CCC")).willReturn(countryC);

    // temp and the same the same for all test cities A,B, and C given the same name "TestCity"
    given(weatherService.getTempAndTime("TestCity"))
        .willReturn(new TempAndTime(310.37, 1593964800, -14400));

    given(cityRepository.findByName("TestCity")).willReturn(cities);

    CityInfo cityResult = cityService.getCityInfo("TestCity");
    CityInfo expectedResult =
        new CityInfo(1, "TestCity", "AAA", "TestCountryA", "DistrictTest", 11111, 99.0, "12:00 PM");

    assertThat(cityResult).isEqualTo(expectedResult);
  }
}
