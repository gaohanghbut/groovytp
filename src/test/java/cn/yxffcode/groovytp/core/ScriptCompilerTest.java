package cn.yxffcode.groovytp.core;

import cn.yxffcode.groovytp.context.LogObjectPostProcessor;
import cn.yxffcode.groovytp.render.ImportExtractInterceptor;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author gaohang
 */
public class ScriptCompilerTest {

  @Test
  public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final CodeTemplate codeTemplate = new CodeTemplate("testTemplate", "/test_template.vm");

    final TemplateRendInterceptor[] interceptors = new TemplateRendInterceptor[]{
        new ImportExtractInterceptor(),
        //other interceptors
    };

    final ScriptCompiler scriptCompiler = ScriptCompiler.newBuilder()
        .supportCodeTemplate(codeTemplate, interceptors)
        //.supportCodeTemplate(otherTemplate, interceptors)
        .addObjectPostProcessors(new LogObjectPostProcessor()/*, other ObjectPostProcessor*/)
        .build();

    final Object obj = scriptCompiler.compileToObject("return 1 + 1", codeTemplate);

    System.out.println("obj class = " + obj.getClass().getCanonicalName());

    final Method method = obj.getClass().getMethod("test");
    final Object value = method.invoke(obj);
    System.out.println("value = " + value);
  }

}