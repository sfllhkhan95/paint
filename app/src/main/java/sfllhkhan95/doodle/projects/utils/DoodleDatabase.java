package sfllhkhan95.doodle.projects.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This is the project database. DoodleDatabase is responsible for managing all the project
 * files, including saving, opening and deleting them.
 *
 * @author saifkhichi96
 * @version 1.3.0
 * @since 1.0.0
 */
public class DoodleDatabase {

    private final static String EXT = ".jpg";

    private final static String PREFIX = "DOODLEv2_";
    private final static String DIR = "Android/data/sfllhkhan95.doodle/files/Projects/";
    private final static String rootDirPath;
    private final static File rootDir;

    private final static String PREFIX_LEGACY = "DOODLE_";
    private final static String DIR_LEGACY = "Pictures/Doodles/";
    private final static String legacyRootDirPath;
    private final static File legacyRootDir;

    static {
        legacyRootDirPath = Environment.getExternalStorageDirectory().toString() + "/" + DIR_LEGACY;
        legacyRootDir = new File(legacyRootDirPath);
        legacyRootDir.mkdirs();

        rootDirPath = Environment.getExternalStorageDirectory().toString() + "/" + DIR;
        rootDir = new File(rootDirPath);
        rootDir.mkdirs();
    }

    /**
     * Relocates a project to new root directory.
     *
     * @param projectName name of the project to be relocated
     * @since 3.4.3
     */
    private static void migrateDoodle(String projectName) {
        Bitmap doodle = DoodleFactory.loadFromPath(legacyRootDirPath + projectName);
        saveDoodle(doodle, projectName);
        removeDoodle(legacyRootDir, projectName);
    }

    /**
     * Relocates all projects to the updated root directory.
     *
     * @since 3.4.3
     */
    private static void migrateLegacyDoodles() {
        String[] list = legacyRootDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(PREFIX) || name.startsWith(PREFIX_LEGACY)) && name.endsWith(EXT);
            }
        });
        if (list != null) {
            for (String name : list) {
                migrateDoodle(name);
            }
        }
        legacyRootDir.delete();
    }

    /**
     * Renames projects to reflect updated naming syntax.
     *
     * @param oldName old name of the project
     * @return new name of the project
     * @since 2.1.0
     */
    private static String upgradeLegacyNames(String oldName) {
        String date = oldName.split("_")[1];
        String time = oldName.split("_")[2];

        String day = date.substring(0, 2);
        String month = date.substring(2, 4);
        String year = date.substring(4, 8);

        date = year + month + day;
        String newName = PREFIX + date + "_" + time;

        Bitmap doodle = DoodleFactory.loadFromPath(rootDirPath + oldName);
        saveDoodle(doodle, newName);
        removeDoodle(oldName);

        return newName;
    }

    /**
     * Opens a project as a Bitmap image of specified size.
     *
     * @param projectName name of the project to open
     * @param wd          width of the opened image
     * @param ht          height of the opened image
     * @return the Bitmap image of the project, or null
     */
    @Nullable
    public static Bitmap loadDoodle(String projectName, int wd, int ht) {
        File file = new File(rootDir, projectName);
        if (file.exists()) {
            return DoodleFactory.loadFromPath(rootDirPath + projectName, wd, ht);
        } else {
            return null;
        }
    }

    /**
     * Lists all the projects in the database.
     *
     * @return a list containing names of all the projects, or null
     */
    @Nullable
    public static String[] listDoodles() {
        migrateLegacyDoodles();
        String[] list = rootDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(PREFIX) || name.startsWith(PREFIX_LEGACY)) && name.endsWith(EXT);
            }
        });
        if (list == null) {
            return null;
        }

        List<String> newList = new ArrayList<>();

        for (String name : list) {
            // If file is from an older version, upgrade filename
            if (name.startsWith(PREFIX_LEGACY)) {
                name = upgradeLegacyNames(name);
            }

            newList.add(name);
        }
        list = newList.toArray(list);

        Arrays.sort(list, Collections.<String>reverseOrder());
        return list;
    }

    /**
     * Saves a new project to the database.
     *
     * @param projectData content of the new project
     */
    public static void saveDoodle(final Bitmap projectData) {
        // Create file for storage with CURRENT_TIMESTAMP as name
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        String timestamp = simpleDateFormat.format(new Date());
        String filename = PREFIX + timestamp + EXT;

        saveDoodle(projectData, filename);
    }

    /**
     * Saves a new project to the database.
     *
     * @param projectData content of the new project
     * @param projectName name of the new project
     */
    public static void saveDoodle(Bitmap projectData, String projectName) {
        File file = new File(rootDir, projectName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            projectData.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a project file exists in the database.
     *
     * @param projectName name of the project to check
     * @return true if the project is found, false otherwise
     */
    public static boolean contains(String projectName) {
        File file = new File(rootDir, projectName);
        return (file.exists());
    }

    /**
     * Deletes a project.
     *
     * @param projectName name of the project to be deleted
     * @return true if the project is successfully deleted, false otherwise
     */
    public static boolean removeDoodle(String projectName) {
        return removeDoodle(rootDir, projectName);
    }

    /**
     * Deletes a project.
     *
     * @param rootDir     root directory where this project is located
     * @param projectName name of the project to be deleted
     * @return true if the project is successfully deleted, false otherwise
     */
    private static boolean removeDoodle(File rootDir, String projectName) {
        File file = new File(rootDir, projectName);
        return file.exists() && file.delete();
    }

}