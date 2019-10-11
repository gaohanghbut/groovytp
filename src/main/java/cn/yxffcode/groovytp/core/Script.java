package cn.yxffcode.groovytp.core;

import java.util.Objects;

/**
 * @author gaohang
 */
public class Script {
  private String contentFragment;

  public Script(String contentFragment) {
    this.contentFragment = contentFragment;
  }

  public String getContentFragment() {
    return contentFragment;
  }

  public void setContentFragment(String contentFragment) {
    this.contentFragment = contentFragment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Script script = (Script) o;
    return Objects.equals(getContentFragment(), script.getContentFragment());
  }

  @Override
  public int hashCode() {

    return Objects.hash(getContentFragment());
  }

  @Override
  public String toString() {
    return "Script{" +
        "contentFragment='" + contentFragment + '\'' +
        '}';
  }
}
