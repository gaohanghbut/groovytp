package cn.yxffcode.groovytp.context;

import cn.yxffcode.groovytp.core.ObjectPostProcessor;

/**
 * @author gaohang
 */
public class LogObjectPostProcessor implements ObjectPostProcessor {
  @Override
  public Object postProcessObject(Object instance) {
    //to log the instance
    return instance;
  }
}
