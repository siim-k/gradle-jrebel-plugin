/**
 *  Copyright (C) 2012 ZeroTurnaround <support@zeroturnaround.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.zeroturnaround.jrebel.gradle.model;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Web reource configuration.
 */
public class RebelWebResource implements RebelResource {

  private String directory;
  
  private List<String> excludes;
  
  private List<String> includes;
  
  private String target;

  public String getDirectory() {
    return directory;
  }

  public List<String> getExcludes() {
    return excludes;
  }

  public List<String> getIncludes() {
    return includes;
  }

  public String getTarget() {
    return target;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }

  public void setExcludes(List<String> excludes) {
    this.excludes = excludes;
  }

  public void setIncludes(List<String> includes) {
    this.includes = includes;
  }

  public void setTarget(String target) {
    this.target = target;
  }
  
  /**
   * Matches to the empty 'resource {..}' block that has a special meaning to mark the default element.
   */
  public boolean isDefaultElement() {
    return this.directory == null && this.target == null;
  }
  
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
    builder.append("directory", directory);
    builder.append("excludes", excludes);
    builder.append("includes", includes);
    builder.append("target", target);
    return builder.toString();
  }

}
