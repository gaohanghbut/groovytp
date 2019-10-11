package cn.yxffcode.groovytp.core;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author gaohang
 */
public class TemplateRenderChain {

  private static final Splitter SPLITTER = Splitter.on('\n').trimResults();

  private final TemplateRendInterceptor[] renderInterceptors;

  public TemplateRenderChain(TemplateRendInterceptor[] renderInterceptors) {
    this.renderInterceptors = renderInterceptors;
  }

  public RenderContext proceed(Script script) {
    final RenderContext renderContext = new RenderContext(script);
    renderContext.doProceed();
    return renderContext;
  }

  public final class RenderContext {
    private final Map<String, List<String>> context = Maps.newHashMap();

    private final List<String> scriptLines;

    private int interceptorIndex;

    public RenderContext(Script script) {
      this.scriptLines = Collections.unmodifiableList(SPLITTER.splitToList(script.getContentFragment()));
    }

    Map<String, List<String>> getContext() {
      return context;
    }

    public RenderContext appendTo(String variable, String value) {
      List<String> values = context.get(variable);
      if (values == null) {
        values = new ArrayList<String>();
        context.put(variable, values);
      }
      values.add(value);
      return this;
    }

    public List<String> getScriptLines() {
      return scriptLines;
    }

    public void doProceed() {
      if (interceptorIndex >= renderInterceptors.length) {
        return;
      }
      //调用TemplateRenderInterceptor前先执行interceptorIndex++，调用结束后执行interceptorIndex--
      //因为这里是嵌套调用，RenderContext.doProceed()方法会在TemplateRenderInterceptor.interceptor()中被调用，而
      //RenderContext.doProceed()中又会调用TemplateRenderInterceptor.intercept()
      //当RenderContext.doProceed()被调用时，需要执行下一个TemplateRenderInterceptor
      final TemplateRendInterceptor currentInterceptor = renderInterceptors[interceptorIndex++];
      currentInterceptor.intercept(this);
      interceptorIndex--;
    }
  }
}
