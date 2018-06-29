package org.syaku.springboot.freemarker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * 자바 공용 변수를 뷰(프리마커)에 전달하기 위한 테스트
 * resources/templates/hello.ftl
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 6. 28.
 */
@Slf4j
@RunWith(SpringRunner.class)
@WebMvcTest(FreeMarkerControllerTest.class)
public class FreeMarkerSharedVariablesTest {

  @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;

  @PostConstruct
  public void setup() throws Exception {
    Configuration configuration = freeMarkerConfigurer.getConfiguration();
    configuration.setSharedVariable("good", "good");
  }

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void test() throws Exception {
    this.mockMvc.perform(get("/"))
      .andExpect(status().isOk())
      .andExpect(view().name("hello"))
      .andDo(print());
  }
}

@Slf4j
@Controller
class FreeMarkerControllerTest {
  @GetMapping("/")
  public String hello() {
    return "hello";
  }

}