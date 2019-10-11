package cn.yxffcode.groovytp.render;

import cn.yxffcode.groovytp.core.TemplateRendInterceptor;
import cn.yxffcode.groovytp.core.TemplateRenderChain;

/**
 * @author gaohang
 */
public class ImportExtractInterceptor implements TemplateRendInterceptor {
  @Override
  public void intercept(TemplateRenderChain.RenderContext renderContext) {
    renderContext.getScriptLines().forEach(line -> {
      if (line.startsWith("import")) {
        renderContext.appendTo("imports", line);
      } else {
        renderContext.appendTo("expression", line);
      }
    });
  }
}
