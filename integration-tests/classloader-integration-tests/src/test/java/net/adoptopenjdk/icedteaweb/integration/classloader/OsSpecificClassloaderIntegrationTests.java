package net.adoptopenjdk.icedteaweb.integration.classloader;

import net.sourceforge.jnlp.JNLPFile;
import net.sourceforge.jnlp.runtime.classloader2.JnlpApplicationClassLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static net.adoptopenjdk.icedteaweb.integration.classloader.ClassloaderTestUtils.CLASS_A;
import static net.adoptopenjdk.icedteaweb.integration.classloader.ClassloaderTestUtils.JAR_1;
import static net.adoptopenjdk.icedteaweb.integration.classloader.ClassloaderTestUtils.createFile;

public class OsSpecificClassloaderIntegrationTests {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void testWindowsOnlyRessourceOnWindows() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-4.jnlp");

        //when
        new JnlpApplicationClassLoader(file, jarProvider);

        //than
        Assertions.assertEquals(1, jarProvider.getDownloaded().size());
        Assertions.assertTrue(jarProvider.hasTriedToDownload(JAR_1));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void testWindowsOnlyRessourceOnWindowsWithLoadClass() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-4.jnlp");
        final JnlpApplicationClassLoader classLoader = new JnlpApplicationClassLoader(file, jarProvider);

        //when
        final Class<?> loadedClass = classLoader.loadClass(CLASS_A);

        //than
        Assertions.assertNotNull(loadedClass);
        Assertions.assertEquals(classLoader, loadedClass.getClassLoader());
        Assertions.assertEquals(1, jarProvider.getDownloaded().size());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void testWindowsOnlyRessourceOnNotWindows() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-4.jnlp");

        //when
        new JnlpApplicationClassLoader(file, jarProvider);

        //than
        Assertions.assertEquals(0, jarProvider.getDownloaded().size());
        Assertions.assertFalse(jarProvider.hasTriedToDownload(JAR_1));
    }

    @Test
    @EnabledOnOs(OS.MAC)
    public void testMacOnlyRessourceOnMac() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-5.jnlp");

        //when
        new JnlpApplicationClassLoader(file, jarProvider);

        //than
        Assertions.assertEquals(1, jarProvider.getDownloaded().size());
        Assertions.assertTrue(jarProvider.hasTriedToDownload(JAR_1));
    }

    @Test
    @EnabledOnOs(OS.MAC)
    public void testMacOnlyRessourceOnMacWithLoadClass() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-5.jnlp");
        final JnlpApplicationClassLoader classLoader = new JnlpApplicationClassLoader(file, jarProvider);

        //when
        final Class<?> loadedClass = classLoader.loadClass(CLASS_A);

        //than
        Assertions.assertNotNull(loadedClass);
        Assertions.assertEquals(classLoader, loadedClass.getClassLoader());
        Assertions.assertEquals(1, jarProvider.getDownloaded().size());
    }

    @Test
    @DisabledOnOs(OS.MAC)
    public void testMacOnlyRessourceOnNotMac() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-5.jnlp");

        //when
        new JnlpApplicationClassLoader(file, jarProvider);

        //than
        Assertions.assertEquals(0, jarProvider.getDownloaded().size());
        Assertions.assertFalse(jarProvider.hasTriedToDownload(JAR_1));
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    public void testLinuxOnlyRessourceOnLinux() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-6.jnlp");

        //when
        new JnlpApplicationClassLoader(file, jarProvider);

        //than
        Assertions.assertEquals(1, jarProvider.getDownloaded().size());
        Assertions.assertTrue(jarProvider.hasTriedToDownload(JAR_1));
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    public void testLinuxOnlyRessourceOnLinuxWithLoadClass() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-6.jnlp");
        final JnlpApplicationClassLoader classLoader = new JnlpApplicationClassLoader(file, jarProvider);

        //when
        final Class<?> loadedClass = classLoader.loadClass(CLASS_A);

        //than
        Assertions.assertNotNull(loadedClass);
        Assertions.assertEquals(classLoader, loadedClass.getClassLoader());
        Assertions.assertEquals(1, jarProvider.getDownloaded().size());
    }

    @Test
    @DisabledOnOs(OS.LINUX)
    public void testLinuxOnlyRessourceOnNotLinux() throws Exception {
        //given
        final DummyJarProvider jarProvider = new DummyJarProvider();
        final JNLPFile file = createFile("integration-app-6.jnlp");

        //when
        new JnlpApplicationClassLoader(file, jarProvider);

        //than
        Assertions.assertEquals(0, jarProvider.getDownloaded().size());
        Assertions.assertFalse(jarProvider.hasTriedToDownload(JAR_1));
    }
}