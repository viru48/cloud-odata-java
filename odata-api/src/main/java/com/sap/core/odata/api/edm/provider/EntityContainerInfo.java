/*******************************************************************************
 * Copyright 2013 SAP AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.sap.core.odata.api.edm.provider;

import java.util.List;

/**
 * Objects of this class represent an entity container
 * @author SAP AG
 */
public class EntityContainerInfo {

  private String name;
  private String extendz;
  private boolean isDefaultEntityContainer;
  private List<AnnotationAttribute> annotationAttributes;
  private List<AnnotationElement> annotationElements;

  /**
   * @return <b>String</b> name
   */
  public String getName() {
    return name;
  }

  /**
   * @return <b>String</b> name of the container which is extended by this container
   */
  public String getExtendz() {
    return extendz;
  }

  /**
   * @return <b>boolean</b> if this container is the default container
   */
  public boolean isDefaultEntityContainer() {
    return isDefaultEntityContainer;
  }

  /**
   * Sets the name of this {@link EntityContainerInfo}
   * @param name
   * @return {@link EntityContainerInfo} for method chaining
   */
  public EntityContainerInfo setName(final String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the entity container which is the parent of this {@link EntityContainerInfo}
   * @param extendz
   * @return {@link EntityContainerInfo} for method chaining
   */
  public EntityContainerInfo setExtendz(final String extendz) {
    this.extendz = extendz;
    return this;
  }

  /**
   * Sets if this is the default {@link EntityContainerInfo}
   * @param isDefaultEntityContainer
   * @return {@link EntityContainerInfo} for method chaining
   */
  public EntityContainerInfo setDefaultEntityContainer(final boolean isDefaultEntityContainer) {
    this.isDefaultEntityContainer = isDefaultEntityContainer;
    return this;
  }

  /**
   * @return collection of {@link AnnotationAttribute} annotation attributes
   */
  public List<AnnotationAttribute> getAnnotationAttributes() {
    return annotationAttributes;
  }

  /**
   * Sets the collection of {@link AnnotationAttribute} for this {@link EntityContainer}
   * @param annotationAttributes
   * @return {@link EntityContainer} for method chaining
   */
  public EntityContainerInfo setAnnotationAttributes(final List<AnnotationAttribute> annotationAttributes) {
    this.annotationAttributes = annotationAttributes;
    return this;
  }

  /**
   * @return collection of {@link AnnotationElement} annotation elements
   */
  public List<AnnotationElement> getAnnotationElements() {
    return annotationElements;
  }

  /**
   * Sets the collection of {@link AnnotationElement} for this {@link EntityContainer}
   * @param annotationElements
   * @return {@link EntityContainer} for method chaining
   */
  public EntityContainerInfo setAnnotationElements(final List<AnnotationElement> annotationElements) {
    this.annotationElements = annotationElements;
    return this;
  }

}
