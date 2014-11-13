package de.smartics.maven.ea;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Utilities uses by the Mojos of this project.
 */
final class MojoUtils
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Utility class.
   */
  private MojoUtils()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Ensures that the given directory is present and if not, creates it.
   *
   * @param directory the directory to be created.
   * @throws MojoExecutionException if the directory is not present and cannot
   *           be created.
   */
  static void provideMojoDirectory(final File directory)
    throws MojoExecutionException
  {
    if (!directory.exists())
    {
      final boolean created = directory.mkdirs();
      if (!created && !directory.exists())
      {
        throw new MojoExecutionException("Cannot create directory: "
                                         + directory.getAbsolutePath());
      }
    }
  }

  // --- object basics --------------------------------------------------------

}
