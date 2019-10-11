package cn.yxffcode.groovytp.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Throwables.throwIfUnchecked;

/**
 * @author gaohang
 */
public final class ScriptCompiler {

  public static Builder newBuilder() {
    return new Builder();
  }

  private final Map<String, SourceBuilder> sourceBuilders;

  private final List<ObjectPostProcessor> objectPostProcessors;

  private final GroovyClassLoader groovyClassLoader;

  private ScriptCompiler(Map<String, SourceBuilder> sourceBuilders, List<ObjectPostProcessor> objectPostProcessors) {
    this.sourceBuilders = sourceBuilders;
    this.objectPostProcessors = objectPostProcessors;
    this.groovyClassLoader = new GroovyClassLoader();
  }

  public <T> T compileToObject(String scriptCode, CodeTemplate codeTemplate) {
    if (scriptCode == null) {
      throw new IllegalArgumentException("script cannot be null");
    }
    if (codeTemplate == null) {
      throw new IllegalArgumentException("code template cannot be null");
    }
    final SourceBuilder sourceBuilder = sourceBuilders.get(codeTemplate.getName());
    if (sourceBuilder == null) {
      throw new IllegalArgumentException("cannot found SourceBuilder object to process the code template:"
          + codeTemplate.getName());
    }
    final ClassSource classSource = sourceBuilder.buildSource(new Script(scriptCode));

    final Class<?> groovyType = doCompile(classSource);
    //new instance
    final Object instance = newInstance(groovyType);
    //invoke post processors
    return (T) invokeObjectPostProcessors(instance);
  }

  private Object newInstance(Class<?> groovyType) {
    try {
      return groovyType.newInstance();
    } catch (Exception e) {
      throwIfUnchecked(e);
      throw new RuntimeException(e);
    }
  }

  private Object invokeObjectPostProcessors(Object instance) {
    if (objectPostProcessors == null || objectPostProcessors.isEmpty()) {
      return instance;
    }

    Object obj = instance;
    for (ObjectPostProcessor objectPostProcessor : objectPostProcessors) {
      obj = objectPostProcessor.postProcessObject(obj);
    }
    return obj;
  }

  private Class<?> doCompile(ClassSource classSource) {
    GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>() {
      public GroovyCodeSource run() {
        final String fileName = "script" + System.currentTimeMillis() +
            Math.abs(classSource.getClassContent().hashCode()) + ".groovy";
        return new GroovyCodeSource(classSource.getClassContent(), fileName, "/groovy/script");
      }
    });
    gcs.setCachable(true);
    return groovyClassLoader.parseClass(gcs);
  }

  public static final class Builder {

    private final Map<String, SourceBuilder> sourceBuilders = Maps.newHashMap();

    private final List<ObjectPostProcessor> objectPostProcessors = Lists.newArrayList();

    public Builder supportCodeTemplate(CodeTemplate codeTemplate, TemplateRendInterceptor... interceptors) {
      sourceBuilders.put(codeTemplate.getName(), new ChainedSourceBuilder(codeTemplate, interceptors));
      return this;
    }

    public Builder addObjectPostProcessors(ObjectPostProcessor... postProcessors) {
      this.objectPostProcessors.addAll(Arrays.asList(postProcessors));
      return this;
    }

    public ScriptCompiler build() {
      return new ScriptCompiler(sourceBuilders, objectPostProcessors);
    }
  }
}
