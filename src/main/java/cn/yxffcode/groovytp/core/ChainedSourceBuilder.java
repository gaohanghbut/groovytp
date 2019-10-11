package cn.yxffcode.groovytp.core;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * @author gaohang
 */
class ChainedSourceBuilder implements SourceBuilder {

  private static final Joiner JOINER = Joiner.on('\n');

  private static final VelocityEngine VE = new VelocityEngine();

  private final CodeTemplate codeTemplate;

  private final TemplateRenderChain templateRenderChain;

  ChainedSourceBuilder(CodeTemplate codeTemplate, TemplateRendInterceptor... renderInterceptors) {
    this.codeTemplate = codeTemplate;
    this.templateRenderChain = new TemplateRenderChain(renderInterceptors);
  }

  public ClassSource buildSource(Script script) {
    if (script == null) {
      throw new IllegalArgumentException("script param is null");
    }
    final TemplateRenderChain.RenderContext renderContext = templateRenderChain.proceed(script);
    final Map<String, List<String>> context = renderContext.getContext();
    final VelocityContext velocityContext = new VelocityContext(Maps.transformValues(context, strs -> JOINER.join(strs)));

    final StringWriter out = new StringWriter();
    final boolean success = VE.evaluate(velocityContext, out,
        codeTemplate.getName(), codeTemplate.getTemplateContent());
    if (!success) {
      throw new ScriptTemplateRendException("evaluate the velocity failed, current script is " +
          script.getContentFragment() + " and code template is " + codeTemplate);
    }
    return new ClassSource(out.toString());
  }
}

