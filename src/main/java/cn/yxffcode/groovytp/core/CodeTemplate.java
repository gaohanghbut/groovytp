package cn.yxffcode.groovytp.core;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * @author gaohang
 */
public class CodeTemplate {
  private final String name;
  private final String templatePath;

  private final String content;

  public CodeTemplate(String name, String templatePath) {
    this.name = name;
    this.templatePath = templatePath;

    try (InputStreamReader in = new InputStreamReader(CodeTemplate.class.getResourceAsStream(templatePath))) {
      this.content = CharStreams.toString(in);
    } catch (IOException e) {
      throw new TemplateReadException("cannot read the template:" + templatePath, e);
    }
  }

  public String getTemplateContent() {
    return content;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CodeTemplate that = (CodeTemplate) o;
    return Objects.equals(getName(), that.getName()) &&
        Objects.equals(templatePath, that.templatePath);
  }

  @Override
  public int hashCode() {

    return Objects.hash(getName(), templatePath);
  }

  @Override
  public String toString() {
    return "CodeTemplate{" +
        "name='" + name + '\'' +
        ", templatePath='" + templatePath + '\'' +
        '}';
  }
}
