/*
 * Copyright 2014-2024 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.smartics.maven.ea;

import org.sparx.EnumXMIType;

/**
 * Stores the configuration the the XMI package export.
 * <p>
 * For details please refer to <a href=
 * "http://www.sparxsystems.com/enterprise_architect_user_guide/9.3/automation/project_2.html">
 * User Guide</a>.
 * </p>
 */
public class XmiPackageExport {
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  private String xmiTypeId;

  private EnumXMIType xmiType;

  private int diagramXml = 2;

  private int diagramImage = 3;

  private int formatXml = 0;

  private int useDtd = 0;

  private int xmiFlags = 1;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  public XmiPackageExport() {}

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  public void init() throws IllegalArgumentException {
    this.xmiType = asXmiType();
  }

  private EnumXMIType asXmiType() throws IllegalArgumentException {
    if (this.xmiTypeId == null) {
      return EnumXMIType.xmiEADefault;
    }
    final EnumXMIType xmiType = EnumXMIType.valueOf(this.xmiTypeId);
    return xmiType;
  }

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  public EnumXMIType getXmiType() {
    return xmiType;
  }

  public int getDiagramXml() {
    return diagramXml;
  }

  public int getDiagramImage() {
    return diagramImage;
  }

  public int getFormatXml() {
    return formatXml;
  }

  public int getUseDtd() {
    return useDtd;
  }

  public int getXmiFlags() {
    return xmiFlags;
  }

  // --- object basics --------------------------------------------------------

  @Override
  public String toString() {
    return "xmiType=" + xmiTypeId + ", diagramXml=" + diagramXml
        + ", diagramImage=" + diagramImage + ", formatXml=" + formatXml
        + ", useDtd=" + useDtd + ", xmiFlags=" + xmiFlags;
  }
}
