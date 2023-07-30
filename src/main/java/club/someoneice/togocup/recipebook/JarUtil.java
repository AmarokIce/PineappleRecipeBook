package club.someoneice.togocup.recipebook;

import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("All")
public class JarUtil {
    public static final JarUtil INSTANCE = new JarUtil();
    public static JarUtil getInstance() {
        return INSTANCE;
    }


    /**
     * A Set hold all the url in jar's without `.class` and `.png`.<br />
     * Get it by {@link #getSet()} . <p />
     * 一个持有除.class与.png文件外全部Jar内文件的Url的集。 <br />
     * 使用 {@link #getSet()} 取得这个集。
     * */
    Set<UrlBuffered> url_list = Sets.newHashSet();

    /**
     * A Set hold all the url in jar's dir `data`. <br />
     * Gte it by {@link #getDataSet()}. <p />
     * 一个持有全部jar内data文件夹下Url的集。 <br />
     * 使用 {@link #getDataSet()} 取得这个集。
     * */
    Set<UrlBuffered> data_list = Sets.newHashSet();

    private static String LOCAL_URL = System.getProperty("user.dir");

    void read() {
        File modDir = new File(LOCAL_URL, "mods");
        if (modDir.exists() && modDir.isDirectory()) {
            for (File modJar : modDir.listFiles()) {
                if (modJar.isFile() && modJar.getName().contains(".jar")) {
                    try {
                        JarFile jar = new JarFile(modJar);
                        Enumeration<JarEntry> entrys = jar.entries();
                        while (entrys.hasMoreElements()) {
                            JarEntry entry = entrys.nextElement();
                            String name = entry.getName();
                            if (entry.isDirectory() || name.contains(".class")) continue;

                            Main.LOGGER.info("Read the file from Jar: " + name);
                            this.data_list.add(new UrlBuffered(name));
                            if (name.contains("data/")) this.data_list.add(new UrlBuffered(name));
                        }
                        jar.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        File dataDirLocal = new File(LOCAL_URL, "data");
        if (dataDirLocal.exists() && dataDirLocal.isDirectory()) {
            readFile(dataDirLocal);
        }
    }

    /**
     * TODO: A config that can mark which dir will scan, and scan all.
     */
    void readFile(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles())
                readFile(child);
        } else if (file.isFile()) {
            this.url_list.add(new UrlBuffered(file.getPath(), false));
            this.data_list.add(new UrlBuffered(file.getPath(), false));
        }
    }

    public Set<UrlBuffered> getSet() {
        return Sets.newHashSet(this.url_list);
    }

    public Set<UrlBuffered> getDataSet() {
        return Sets.newHashSet(this.data_list);
    }

    /**
     * Find a file by String in Jars. If it cannot find, it will return null and mark the url.<br />
     * 通过String获取一个Jar中的文件。如果无法找到，返回空值并标记路径。
     * @param url The file's url. Get it from JarUtil's scan.<br />
     *            文件的路径。从JarUtil的扫描中获取。
     * @return The file's InputStream.<br />
     *          文件的输入流InputStream。
     */
    @Nullable
    public InputStream getInputStreamFromUrl(UrlBuffered url) {
        try {
            return url.isJar ?
                this.getClass().getResourceAsStream("/" + url.filePath)
                    : new FileInputStream(LOCAL_URL + url.filePath);
        } catch (NullPointerException | FileNotFoundException pointer) {
            new ObjectNotFindException(url.filePath).printStackTrace();
            return null;
        }
    }

    /**
     * Read a file from String in Jar. If it cannot find, it will return null.<br />
     * 通过String获取一个Jar中的文件的内容。如果无法找到，返回空值并标记路径。
     * @param url The file's url. Get it from JarUtil's scan.<br />
     *            文件的路径。从JarUtil的扫描中获取。
     * @return The file's content.<br />
     *          文件的内容。
     * @see JarUtil#readFileFromUrl(UrlBuffered)
     */
    @Nullable
    public String readFileFromUrl(UrlBuffered url) {
        try {
            InputStream input = getInputStreamFromUrl(url);
            if (input != null) {
                byte[] buffer = new byte[input.available()];
                input.read(buffer);
                input.close();
                return new String(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * A data class hold the file path, and mark if it is in jar. <br />
     * The Jar will close so we should hold the path from it. And mark the local's data object. <p />
     * 用于标记文件路径以及是否为Jar文件的数据类。 <br />
     * 因为Jar文件需要被关闭，所以我们需要取得文件的路径。以及标记本地的文件对象。
     */
    public static class UrlBuffered {
        boolean isJar;
        String filePath;
        UrlBuffered(String fileUrl, boolean isJar) {
            this.isJar = isJar;
            this.filePath = fileUrl;
        }

        UrlBuffered(String fileUrl) {
            this(fileUrl, true);
        }

        public String getFileUrl() {
            return filePath;
        }
    }
}
