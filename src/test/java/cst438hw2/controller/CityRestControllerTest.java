package cst438hw2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import cst438hw2.domain.City;
import cst438hw2.domain.CityInfo;
import cst438hw2.service.CityService;

@RunWith(SpringRunner.class)
@WebMvcTest(CityRestController.class)
public class CityRestControllerTest {

  @MockBean
  private CityService cityService;

  @Autowired
  private MockMvc mvc;

  // This object will be magically initialized by the initFields method below.
  private JacksonTester<CityInfo> json;

  @Before
  public void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
  }

  @Test
  public void contextLoads() {}

  @Test
  public void validCity() throws Exception {
    City city = new City(1, "TestCity", "AAA", "DistrictTest", 99999);

    given(cityService.getCityInfo("TestCity"))
        .willReturn(new CityInfo(city, "TestCountry", 99.0, "12:00 PM"));

    MockHttpServletResponse response =
        mvc.perform(get("/api/cities/TestCity")).andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    CityInfo cityResult = json.parseObject(response.getContentAsString());

    CityInfo expectedResult =
        new CityInfo(1, "TestCity", "AAA", "TestCountry", "DistrictTest", 99999, 99.0, "12:00 PM");

    assertThat(cityResult).isEqualTo(expectedResult);
  }

  @Test
  public void invalidCity() throws Exception {
    City city = new City(1, "TestCity", "AAA", "DistrictTest", 99999);

    given(cityService.getCityInfo("TestCity"))
        .willReturn(new CityInfo(city, "TestCountry", 99.0, "12:00 PM"));

    MockHttpServletResponse response =
        mvc.perform(get("/api/cities/diffTestCity")).andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

  }


}
