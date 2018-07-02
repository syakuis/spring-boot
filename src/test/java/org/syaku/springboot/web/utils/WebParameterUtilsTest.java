package org.syaku.springboot.web.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 2.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(WebParameterUtilsControllerTest.class)
public class WebParameterUtilsTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;

  class ParameterUtilsTemplateModel implements TemplateMethodModelEx {
    private static final String MERGE = "merge";
    private static final String PICK = "pick";

    @Override
    public Object exec(List arguments) throws TemplateModelException {
      if (arguments.size() != 3) {
        throw new TemplateModelException("Wrong arguments");
      }

      if (!(arguments.get(0) instanceof HttpRequestHashModel)) {
        throw new IllegalArgumentException("args[0] http");
      }

      HttpRequestHashModel requestHashModel = (HttpRequestHashModel) arguments.get(0);
      HttpServletRequest request = requestHashModel.getRequest();
      String mode = ((SimpleScalar) arguments.get(1)).getAsString();
      String params = ((SimpleScalar) arguments.get(2)).getAsString();

      if (MERGE.equals(mode)) {
        return ParameterUtils.merge(request.getParameterMap(), params);
      } else if (PICK.equals(mode)) {
        return ParameterUtils.pick(request.getParameterMap(), params);
      } else {
        throw new IllegalArgumentException("args[1] http");
      }
    }
  }

  @PostConstruct
  public void setup() {
    Configuration configuration = freeMarkerConfigurer.getConfiguration();
    configuration.setSharedVariable("parameterUtils", new ParameterUtilsTemplateModel());
  }

  @Test
  public void 템플릿_테스트() throws Exception {
    mockMvc.perform(get("/ftl?page=1&search=choi"))
      .andExpect(view().name("parameterUtils"))
      .andExpect(content()
        .string("page=1&search=choi&mode=save\npage=1&mode=save"))
      .andExpect(status().isOk()).andDo(print());
  }

  @Test
  public void 컨텐츠_테스트() throws Exception {
    mockMvc.perform(get("/merge?page=1&search=choi"))
      .andExpect(status().isOk())
      .andExpect(content().string("page=1&search=choi&mode=save"));

    mockMvc.perform(get("/pick?page=1&search=choi"))
      .andExpect(status().isOk())
      .andExpect(content().string("page=1&mode=save"));
  }
}

@Slf4j
@Controller
class WebParameterUtilsControllerTest {
  @GetMapping("ftl")
  public String parameterUtils() {
    return "parameterUtils";
  }

  @GetMapping(value = "merge")
  @ResponseBody
  public String merge(HttpServletRequest request) {
    return ParameterUtils.merge(request.getParameterMap(), "mode=save");
  }

  @GetMapping(value = "pick")
  @ResponseBody
  public String pick(HttpServletRequest request) {
    return ParameterUtils.pick(request.getParameterMap(), "page=&mode=save");
  }
}
