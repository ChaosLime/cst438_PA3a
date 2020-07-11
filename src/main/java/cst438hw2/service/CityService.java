package cst438hw2.service;

import java.util.List;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cst438hw2.domain.City;
import cst438hw2.domain.CityInfo;
import cst438hw2.domain.CityRepository;
import cst438hw2.domain.Country;
import cst438hw2.domain.CountryRepository;
import cst438hw2.domain.TempAndTime;

@Service
public class CityService {

  @Autowired
  private CityRepository cityRepository;

  @Autowired
  private CountryRepository countryRepository;

  @Autowired
  private WeatherService weatherService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private FanoutExchange fanout;

  public CityInfo getCityInfo(String cityName) {

    List<City> city = cityRepository.findByName(cityName);

    // If results found, always return the first result, otherwise return null
    if (city.size() > 0) {
      City firstCity = city.get(0);
      Country country = countryRepository.findByCode(firstCity.getCountryCode());
      TempAndTime weather = weatherService.getTempAndTime(cityName);
      return new CityInfo(firstCity, country.getName(), weather.getFarTemp(),
          weather.getStringTime());
    }

    return null;
  }

  public void requestReservation(String cityName, String level, String email) {
    String msg = "{\"cityName\": \"" + cityName + "\" \"level\": \"" + level + "\" \"email\": \""
        + email + "\"}";
    System.out.println("Sending message:" + msg);
    rabbitTemplate.convertSendAndReceive(fanout.getName(), "", // routing key none.
        msg);
  }

}
