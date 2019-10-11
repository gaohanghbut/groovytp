package cn.yxffcode.groovytp.core;

/**
 * @author gaohang
 */
public interface TemplateRendInterceptor {
  void intercept(TemplateRenderChain.RenderContext renderContext);
}
