package org.syaku.springboot.web.support.freemarker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.syaku.springboot.web.utils.ParameterUtils;

import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * FreeMarker Methods 용 ParameterUtils 클래스
 *
 * some.ftl
 * parameter = page=1&search=choi
 *
 * ${parameterUtils(Request, "merge", "mode=save")} return "page=1&search=choi&mode=save"
 * ${parameterUtils(Request, "pick", "page=&mode=save")} return "page=1&mode=save"
 *
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 2.
 * @see ParameterUtils
 */
public class ParameterUtilsTemplateModel implements TemplateMethodModelEx {
  private static final String MERGE = "merge";
  private static final String PICK = "pick";

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    if (arguments.size() != 3) {
      throw new TemplateModelException("Wrong arguments");
    }

    if (!(arguments.get(0) instanceof HttpRequestHashModel)) {
      throw new IllegalArgumentException("args[0] is not of type HttpRequestHashModel");
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
      throw new IllegalArgumentException("args[1] is not a string merge or pick");
    }
  }
}
