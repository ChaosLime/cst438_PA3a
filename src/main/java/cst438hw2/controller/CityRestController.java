package cst438hw2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import cst438hw2.domain.CityInfo;
import cst438hw2.service.CityService;

@RestController
public class CityRestController {

  @Autowired
  private CityService cityService;

  @GetMapping("/api/cities/{city}")
  public ResponseEntity<CityInfo> getWeather(@PathVariable("city") String cityName) {

    CityInfo city = cityService.getCityInfo(cityName);
    if (city != null) {
      return new ResponseEntity<CityInfo>(city, HttpStatus.OK);
    } else {
      return new ResponseEntity<CityInfo>(HttpStatus.NOT_FOUND);
    }
  }

}
