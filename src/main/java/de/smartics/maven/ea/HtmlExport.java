/*
 * Copyright 2014 smartics, Kronseder & Reiner GmbH
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

import org.codehaus.plexus.util.StringUtils;

/**
 *
 */
public class HtmlExport
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The GUID of the package within the project to export. Only used for HTML
   * site generation.
   *
   * @since 1.0
   */
  private String eaPackageGuid;

  /**
   * Export image format for HTML export. Only used for HTML site generation.
   *
   * @since 1.0
   */
  private String imageFormat = "PNG";

  /**
   * Export style applied to the export. Only used for HTML site generation.
   *
   * @since 1.0
   */
  private String style = "<default>";

  /**
   * File name extension for HTML pages. Only used for HTML site generation.
   *
   * @since 1.0
   */
  private String fileNameExtension = ".html";

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  public HtmlExport()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  public String getEaPackageGuid()
  {
    return eaPackageGuid;
  }

  public String getImageFormat()
  {
    return imageFormat;
  }

  public String getStyle()
  {
    return style;
  }

  public String getFileNameExtension()
  {
    return fileNameExtension;
  }

  public boolean hasEaPackageGuid()
  {
    return StringUtils.isNotBlank(eaPackageGuid);
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

  @Override
  public String toString()
  {
    return "eaPackageGuid=" + eaPackageGuid + ", imageFormat=" + imageFormat
           + ", style=" + style + ", fileNameExtension=" + fileNameExtension;
  }
}
