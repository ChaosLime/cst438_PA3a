package cst438hw2.service;

import java.util.Date;
import java.util.List;
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

		List<City> city = cityRepository.findByName(cityName);

		//If results found, always return the first result, otherwise return null
		if(city.size() >= 1) {
			City firstCity = city.get(0);
			Country country = countryRepository.findByCode(firstCity.getCountryCode());
			TempAndTime weather = weatherService.getTempAndTime(cityName);
			return new CityInfo(firstCity, country.getName(), weather.getFarTemp(), weather.getStringTime());
		}

		return null;
	}
	
}